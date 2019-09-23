#!/bin/bash
# lo primero es coger el uptime que es la primera linea de top
uptime="top - "$(uptime)
# creamos un array con todos los pid de los procesos
pid_array=($(ls -la /proc | awk '{print $9}' | grep "^[0-9]*$"))
# time that the machine booted
time_up=$(awk '{print $1}' < /proc/uptime)
# guardamos los herzios
hz=$(getconf CLK_TCK)
# guardamos el numero total de procesos e inicializamos el numero de los procesos de cada estado
total_process=${#pid_array[@]}
running=0
sleeping=0
stopped=0
zombie=0
# coger el uso de cpu de los diferentes campos necesarios (usuario , sistema, idle,...)
us=$(cat /proc/stat | head -n 1 | awk '{print $2}')
sy=$(cat /proc/stat | head -n 1 | awk '{print $4}')
ni=$(cat /proc/stat | head -n 1 | awk '{print $3}')
id=$(cat /proc/stat | head -n 1 | awk '{print $5}')
wa=$(cat /proc/stat | head -n 1 | awk '{print $6}')
hi=$(cat /proc/stat | head -n 1 | awk '{print $7}')
si=$(cat /proc/stat | head -n 1 | awk '{print $8}')
st=$(cat /proc/stat | head -n 1 | awk '{print $9}')
# coge el total de uso del cpu
total=$(($us+$sy+$ni+$id+$wa+$hi+$si+$st))
# guarda las variables en porcentaje sobre el total
us=$(echo "$us/$total*100" | bc -l)
sy=$(echo "$sy/$total*100" | bc -l)
ni=$(echo "$ni/$total*100" | bc -l)
id=$(echo "$id/$total*100" | bc -l)
wa=$(echo "$wa/$total*100" | bc -l)
hi=$(echo "$hi/$total*100" | bc -l)
si=$(echo "$si/$total*100" | bc -l)
st=$(echo "$st/$total*100" | bc -l)
# coger el array de los datos de la memoria
array_mem=($(cat /proc/meminfo | awk '{print $2}'))
# coger las variables necesarias del array para la cuarta linea
total_mem=${array_mem[0]}
free_mem=${array_mem[1]}
cache=${array_mem[3]}
buff=${array_mem[4]}
Slab=${array_mem[25]}
SReclaimable=${array_mem[26]}
Sunreclaimable=${array_mem[27]}
buff_cache=$((buff+cache+Slab))
mem_used=$((total_mem-free_mem-buff_cache))
# coger las variables necesarias del array para la quinta linea
total_swap=${array_mem[19]}
ts_free=${array_mem[20]}
ts_used=$((total_swap-ts_free))
av_mem=${array_mem[2]}
# contador del cpu usage total
total_cpu_usage=0
for pid in "${pid_array[@]}"
do	
	if [ -d /proc/$pid ]; then
		# coge el usuario del pid
		useruid=$(awk '/Uid/ {print $2}' 2> /dev/null < /proc/$pid/status)
		user=$(getent passwd $useruid | cut -d: -f1)
		# coge la prioridad del pid
		prio=$(awk '{print $18}' 2> /dev/null < /proc/$pid/stat)
		if [ $prio -eq "-100" ]; then
			prio="rt" 
		fi
		nice=$(awk '{print $19}' 2> /dev/null < /proc/$pid/stat)
		# memoria virtual usada por el proceso
		virt=$(awk '/VmSize/ {printf "%8d", $2}' 2> /dev/null < /proc/$pid/status)
		if [ -z "$virt" ]; then
		    virt=0
		fi
		# memoria residente usada por el proceso
		res=$(awk '/VmRSS/ {print $2}' 2> /dev/null < /proc/$pid/status)
		if [ -z "$res" ]; then
		    res=0
		fi
		# memoria compartida
		rss=$(awk '/RssFile/ {print $2}' 2> /dev/null < /proc/$pid/status)
		rsss=$(awk '/RssShmem/ {print $2}' 2> /dev/null < /proc/$pid/status)
		shr=$((rss+rsss))
		# el porcentaje de memoria es la memoria virtual entre el total
		mem_total=$(echo "scale = 2;" "$virt/$total_mem*100"  2> /dev/null | bc -l)
		# coge el tiempo
		total_time=$(awk '{print $14+$15+$16+$17}' 2> /dev/null < /proc/$pid/stat)
		middlevalue=$(echo "$total_time / $hz" 2> /dev/null | bc -l | awk '{printf "%f", $0}')	
		# parsea el tiempo y deja dos decimales en los segundos
		time=$(date -d@$middlevalue -u +%M:%S.%2N)
		# calcula el uso total del cpu
		start_time=$(awk '{print $22}' 2> /dev/null < /proc/$pid/stat)
		total_time_cpu=$(awk '{print $14+$15}' 2> /dev/null < /proc/$pid/stat)
		seconds=$(echo "scale = 10;" "$time_up - ($start_time / $hz)" | bc)
		cpu_usage=$(echo "scale = 10;" "100 * (($total_time_cpu / $hz) / $seconds)" | bc | awk '{printf "%f", $0}')
		total_cpu_usage=$(echo "$total_cpu_usage+$cpu_usage" 2> /dev/null | bc)
		if [ -z "$cpu_total" ]; then
		    cpu_total=0
		fi
		# coge el comando completo del proceso
		comand=$(awk '/Name/ {print $2}' 2> /dev/null < /proc/$pid/status)
		# quita los parentesis
		comand=${comand#"("}
		comand=${comand%")"}
		# coge el estado del proceso y lo añade a los contadores 
		state=$(awk '{print $3}' 2> /dev/null < /proc/$pid/stat)
		case $state in
		("R") 
		running=$((running + 1));;
		("S") 
		sleeping=$((sleeping + 1));;
		("D") 
		stopped=$((stopped + 1));;
		("Z") 
		zombie=$((zombie + 1));;
		esac
		LC_NUMERIC="en_US.UTF-8" printf "%5d %-12s %-4s %-3d %6d %6d %6d %1s %.10f %4.1f %8s %-15s\n" $pid $user $prio $nice $virt $res $shr $state $cpu_usage $mem_total ${time#0} $comand>> tmp.out
	fi
done
# calcula el porcentaje del total del cpu y ordena los datos por ese porcentaje cogiendo solo los 10 primeros
awk -v var="$total_cpu_usage" ' {$9 = ($9 / var) * 100; printf "%5d %-12s %-4s %-3d %6d %6d %6d %1s %.10f %4.1f %8s %-15s\n",$1, $2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12}' tmp.out >tmp2.out
LC_NUMERIC=en_US.UTF-8 sort -g -r -k9 tmp2.out |head -10 > salida.out
# colores para printear el output
bold=$(tput bold)
normal=$(tput sgr0)
rev=$(tput rev)
# primera linea
echo $uptime
# segunda linea
echo -e "Tasks: "${bold}$total_process${normal}" total,   "${bold}$running${normal}" running,   "${bold}$sleeping${normal}" sleeping,   "${bold}$stopped${normal}" stopped,   "${bold}$zombie${normal}" zombie"
# tercera linea
LC_NUMERIC="en_US.UTF-8" printf "%%Cpu(s):  "${bold}"%0.1f"${normal}" usuario,   "${bold}"%0.1f"${normal}" sist,   "${bold}"%0.1f"${normal}" adecuado,   "${bold}"%0.1f"${normal}" inact,   "${bold}"%0.1f"${normal}" en espera,   "${bold}"%0.1f"${normal}" hardw int,   "${bold}"%0.1f"${normal}" soft int,   "${bold}"%0.1f"${normal}" robar tiempo\n" $us $sy $ni $id $wa $hi $si $st
# cuarta linea
printf "%-8s:  %15s total, %18s free, %18s used, %18s buff/cache\n" "KiB Mem" ${bold}$total_mem${normal} ${bold}$free_mem${normal} ${bold}$mem_used${normal} ${bold}$buff_cache${normal}
# quinta linea
printf "%-8s:  %15s total, %18s free, %18s used, %18s avail Mem\n" "KiB Swap" ${bold}$total_swap${normal} ${bold}$ts_used${normal} ${bold}$ts_free${normal} ${bold}$av_mem${normal}
# cabecera de la tabla
printf "\n"
printf "${rev}%5s %-12s %-4s %-3s %6s %6s %6s %-1s %4s %4s %8s %-20s${normal}\n" "PID" "USUARIO" "PR" "NI" "VIRT" "RES" "SHR" "S" "%CPU" "%MEM" "HORA+" "ORDEN" 
# imprime los 10 procesos
while read p; do 
    LC_NUMERIC="en_US.UTF-8" printf "%5d %-12s %-4s %-3d %6d %6d %6d %1s %4.1f %4.1f %8s %-15s\n" $p
done < salida.out
# borra los archivos temporales
rm salida.out
rm tmp.out
rm tmp2.out











