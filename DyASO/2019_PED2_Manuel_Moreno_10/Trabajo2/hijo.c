//este archivo es el fichero fuente que al compilarse produce el ejecutable HIJO
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

char status[3] ="";
typedef struct 
{
	long tipo;
	int pid;
	char status[3];
}Mensaje_struct;

void defensa(){
	printf("\033[0;32m\t\t\t\t\t\t\tEl hijo \033[1;32m%5d\033[0;32m ha repelido un ataque\033[0m\n", getpid());
	fflush(stdout);	
	strcpy(status,"OK");
}

void indefenso(){
	fflush(stdout);
	printf("\033[0;31mEl hijo \033[1;31m%5d\033[0;31m ha sido emboscado mientras realizaba un ataque\033[0m\n", getpid());
	strcpy(status,"KO");
}

// metodo que añade un pid a la lista siempre que exista un hueco , respetando el semaforo
void add_pid( int pid, int number, int id_semaforo, int lista[], struct sembuf Operacion) {
	int i;
	//espera al semaforo	
	Operacion.sem_op = -1;
	semop (id_semaforo, &Operacion, 1);
	for( i = 0; i < number; ++ i ) {
	  	if(lista[i] == 0) {
			lista[i] = pid;
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

int coge_pid(int pids_vivos, int id_semaforo, int lista[], struct sembuf Operacion){
	int pid=0;
	while(pid==0){
		int id_ataque = rand()%pids_vivos; 
		//espera al semaforo	
		Operacion.sem_op = -1;
		semop (id_semaforo, &Operacion, 1);
		//coge el pid
		if(lista[id_ataque]==getpid()){
			//si el pid es igual al del proceso lo ignora
			//libera al semaforo
			Operacion.sem_op = 1;
			semop (id_semaforo, &Operacion, 1);
			continue;
		}
		pid = lista[id_ataque];
		//libera al semaforo
		Operacion.sem_op = 1;
		semop (id_semaforo, &Operacion, 1);
	}
	return pid;
}

int main(int argc, char ** argv)
{	
	setbuf(stdout, NULL);	
	/* Intializes random number generator */
   	srand(getpid());
	//crea variables
	key_t key;
	int id_cola_mensajes, id_semaforo,id_mem_compartida; 		//variables del ipc
	FILE* fifo_image;
	int number = atoi(argv[4]);
	//cogemos los identificadores de la tuberia sin nombre
	int num1 = atoi(argv[2]);
	int num2 = atoi(argv[3]);
	//recreamos la tuberia en un array
	int barrera[] = {num1, num2};
	int pids_vivos;
	Mensaje_struct mensaje;
	int tamano_mensaje=sizeof(mensaje)-sizeof(mensaje.tipo);
	struct sembuf Operacion;
	Operacion.sem_num = 0;
	Operacion.sem_flg = 0;
	//crea la key	
	char *path = argv[1];
	int id = 'X';
	key = ftok(path, id);
	//reabre los ipc
	//creo la cola de mensajes con la key
	id_cola_mensajes = msgget (key, 0777 | IPC_CREAT);
	//creo el array y la memoria compartida
	id_mem_compartida = shmget (key, number*sizeof(int),  0777 | IPC_CREAT);
	int *lista = (int *) shmat(id_mem_compartida, 0, 0);
	//creo el semaforo	
	id_semaforo = semget (key, 1, 0700 | IPC_CREAT);
	//añade el pid a la lista de pids
	int pid = getpid();
	add_pid ( pid, number, id_semaforo, lista, Operacion);
	// cierra el descriptor de escribir
        close(barrera[1]);
	char val;
	//-----------------------------------------ronda de ataques---------------------------------
	while(1)
	{
		//espera la barrera
		read(barrera[0],&val,1);
		//coge los pid vivos
		pids_vivos = children_alive(number, id_semaforo, lista, Operacion);
		//defiende
		if(rand()%2==0){
			signal(SIGUSR1,defensa);
			usleep(200000);

		}
		//ataca
		else{	
			signal(SIGUSR1,indefenso);
			usleep(100000);
			int pid_ataque = coge_pid( pids_vivos, id_semaforo, lista, Operacion);
			printf("\t\t\t\033[1;33m%5d \033[0;33mAtacando al proceso \033[1;33m%5d\033[0m\n", getpid(), pid_ataque);
			kill(pid_ataque,SIGUSR1);
			usleep(100000);	
		}
		//se envia el mensaje con el resultado del ataque al padre
		mensaje.tipo= 1;
		mensaje.pid = pid;
		strcpy(mensaje.status,status);	
		msgsnd(id_cola_mensajes, (struct msgbuf *) &mensaje,tamano_mensaje,0);	
	}    
}
