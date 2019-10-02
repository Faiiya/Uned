//este archivo es el fichero fuente que al compilarse produce el ejecutable PADRE
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h> /* for fork */
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <sys/wait.h>
#include <string.h>

typedef struct 
{
	long tipo;
	int pid;
	char status[3];
}Mensaje_struct;


// metodo que elimina un pid de la lista siempre que este en ella , respetando el semaforo
void remove_pid( int pid, int number, int id_semaforo, int lista[], struct sembuf Operacion) {
	int i;
	//espera al semaforo	
	Operacion.sem_op = -1;
	semop (id_semaforo, &Operacion, 1);
	for( i = 0; i < number; ++ i ) {	
		//printf("pid %d,lista[%d]:%d  | %d \n",pid,i,lista[i],lista[i] == pid);
	  	if(lista[i] == pid) {
			lista[i] = 0;
			break;
		}
	}
	//libera al semaforo
	Operacion.sem_op = 1;
	semop (id_semaforo, &Operacion, 1);
}

// metodo que cuenta el numero de pids que no son 0
int children_alive(int number, int id_semaforo, int lista[], struct sembuf Operacion) {
	int i;
	int count=0;
	//espera al semaforo	
	Operacion.sem_op = -1;
	semop (id_semaforo, &Operacion, 1);
	for( i = 0; i < number; ++ i ) {
	  	if(lista[i] != 0) {
			count++;
		}
	}
	//libera al semaforo
	Operacion.sem_op = 1;
	semop (id_semaforo, &Operacion, 1);
	return count;
}

/* 
metodo que reordena la lista para dejar los pids al principio  y los ceros al final
de modo que si sabemos que quedan 3 pids vivos un array de 10 sera [pid,pid,pid,0,0,0,0,0,0,0]
lo cual me creo que hace generar un numero aleatorio para atacar mucho mas sencillo ya que
simplemente es generar un numero del 0 al numero de pids vivos -1 y ese numero sera el identificador del pid	
*/
void reordena_lista(int lista[], int vivos, int max){
	int i,b;
	for( i = 0; i < vivos; ++ i ) {
		//si uno de los pids de la parte de los vivos es 0
	  	if(lista[i] == 0) {
			for( b = max; b >= vivos; -- b ) {
				//si uno de los pids de la parte de los 0s no es 0
				if(lista[b] != 0){
					//los cambia de posicion
					lista[i] = lista[b];
					lista[b] = 0;		
					break;		
				}
			}
		}
	}
}

//main del padre
int main(int argc, char ** argv)
{
	setbuf(stdout, NULL);	
	//crea variables
	key_t key;
	int id_cola_mensajes, id_semaforo,id_mem_compartida; 	//variables del ipc
	FILE* fifo_image;
	int number;
	int i, pid;
	char str_ko[3]= "KO";
	Mensaje_struct mensaje;
	int tamano_mensaje=sizeof(mensaje)-sizeof(mensaje.tipo);
	struct sembuf Operacion;
	Operacion.sem_num = 0;
	Operacion.sem_flg = 0;
	//crea la key	
	char *path = argv[1];
	int id = 'X';
	key = ftok(path, id);
	//coge el numero de hijos
	number = strtol (argv[2],NULL,10);	
	//creo la cola de mensajes con la key
	id_cola_mensajes = msgget (key, 0777 | IPC_CREAT);
	//creo el array, lo inicializa y creo la memoria compartida
	id_mem_compartida = shmget (key, number*sizeof(int),  0777 | IPC_CREAT);
	int *lista = (int *) shmat(id_mem_compartida, 0, 0);
	//creo el semaforo	
	id_semaforo = semget (key, 1, 0700 | IPC_CREAT);
	//inicializo el semaforo a 1
	semctl (id_semaforo, 0, SETVAL, 1);
	//crea la tuberia sin nombre barrera
	int descriptor_pipe;
	int barrera[2];  
    	pipe(barrera); 
	
	//pasamos los descriptores a string para poder mandarlos en el exec
	char num1[4];
	sprintf(num1, "%d", barrera[0]);
	char num2[4];
	sprintf(num2, "%d", barrera[1]);
	char total_alive[4];
	sprintf(total_alive, "%d", number);
	//crea N procesos hijos
	for(i = 0; i < number; i++) {
		pid = fork();
		if(pid < 0) {
			printf("Error");
			exit(1);
		} else if (pid == 0) {
			//el hijo ejecuta hijo.exe
			char* argv[] = { "hijo",
					 path,
					 num1,
					 num2,
					 total_alive, 
					 NULL};
			execvp("./hijo.exe",argv);
			exit(0); 
		}
	}
	//coge el numero de hijos vivos
	int alive = children_alive(number, id_semaforo, lista, Operacion);
	//espera a que todos los hijos esten creados y la lista este llena
	while(alive<number){
		usleep(100000);
		alive = children_alive(number, id_semaforo, lista, Operacion);
	}
	//cerramos el descriptor de leer en el padre
	close(barrera[0]);		
	while(alive >=2){
		//----------------------ronda de ataques---------------------------------
		//iniciando ronda de ataques
		printf("\033[1;31mataques\033[0m\033[0;36m=================\033[1;36miniciando ronda de ataques\033[0;36m=================\033[1;32mdefensa\033[0m\n\n");
		fflush( stdout );
		char b = 'a'; //1byte
		//llenamos la barrera con N bytes donde N es el numero de hijos vivos
		for(i = 0; i < alive; i++) {
			write(barrera[1], &b, sizeof(b));
		}
		//por cada hijo vivo espera su mensaje
		for(i=0;i<alive;i++){
			msgrcv(id_cola_mensajes,&mensaje,tamano_mensaje,1,0);
			//si el mensaje es == KO entonces	
			if(strcmp(str_ko,mensaje.status)==0){
				//mata al proceso
				kill(mensaje.pid,SIGTERM);
				//espera a que muera
				waitpid(mensaje.pid, NULL,0);
				printf("\033[0;31m\t\t\teliminando proceso: \033[1;31m%5d\033[0m\n",mensaje.pid);
				fflush(stdout);
				//elimina el proceso de la lista en memora compartida
				remove_pid(mensaje.pid, number, id_semaforo, lista,Operacion);
			}
		}
		alive = children_alive(alive, id_semaforo, lista, Operacion);
		printf("\033[0;33m\t\t\tprocesos vivos: \033[1;33m%5d\033[0m\n",alive);
		fflush(stdout);
		reordena_lista(lista, alive, number);
	}
	fifo_image = fopen("resultado", "w");
	if(alive == 1){
		//un ganaddor
		kill(lista[0],SIGTERM);
		fprintf(fifo_image,"\033[0;32m\t\t\tEl hijo \033[1;32m%5d\033[0;32m ha ganado\033[0m\n",lista[0]);
	}	
	else{
		//empate
		fprintf(fifo_image,"\t\t\t\033[1;33mEmpate\033[0m\n");
	}
	system("ipcrm -a");
	system("ipcs -qs");
	return 0;
}


