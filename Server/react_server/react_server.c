
//gcc react_server.c myqueue.c -lmysqlclient -lpthread -o server -g

#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h> 
#include <netinet/in.h>
#include <netdb.h> 
#include <string.h> 
#include <sys/types.h>
#include <fcntl.h> // for open
#include <unistd.h> // for close
#include <pthread.h>
#include <signal.h>
#include <mysql/mysql.h>
#include <string.h>
#include "myqueue.h"


#define PORT 8080
#define DEBUG 1
//size of buffer
#define MAX 80
#define THREAD_POOL_SIZE 3

pthread_t thread_pool[THREAD_POOL_SIZE];
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cond_var = PTHREAD_COND_INITIALIZER;

int sock_fd;
MYSQL *con;


//handle ^C
void sigintHandler(int sig_num) 
{ 
	//bezarjuk a socketet kilepes elott
	close(sock_fd);
	
    printf("\n Socket closed!\n");
    exit(0); 
} 


//error message, when query fail
void finish_with_error(MYSQL *con)
{
  fprintf(stderr, "%s\n", mysql_error(con));
  mysql_close(con);
  exit(1);        
}


void * thread_function(void *arg)
{
	int id = (int) arg;
	
	//fprintf(stderr, "Thread started with #%i\n", id);

	while(1)
	{
		
		pthread_mutex_lock(&mutex);
		pthread_cond_wait(&cond_var, &mutex);
		int* p_client = dequeue();
		pthread_mutex_unlock(&mutex);
	
		
		
		if(p_client != NULL)
		{
			fprintf(stderr,"\n\nTHREAD #%i: CONNECTION #%i\n\n",id, *p_client);
			
			//get string from client, exit when "exit" message
/*			char buff[MAX]; */
/*			int n; */
/*			*/
/*			*/
/*			// infinite loop for chat */
/*			for (;;) */
/*			{ */
/*				bzero(buff, MAX); */
/*		  */
/*				// read the message from client and copy it in buffer */
/*				read(*p_client, buff, sizeof(buff)); */
/*				fprintf(stderr,"hello\n");*/
/*				// print buffer which contains the client contents */
/*				//printf("From client: %s\t To client : ", buff); */
/*				//bzero(buff, MAX); */
/*				//n = 0; */
/*				*/
/*				// copy server message in the buffer */
/*				//while ((buff[n++] = getchar()) != '\n') ; */
/*		  */
/*				// and send that buffer to client */
/*				//write(sock_fd, buff, sizeof(buff)); */
/*		  */
/*				// if msg contains "Exit" then server exit and chat ended. */
/*				if (strncmp("exit", buff, 4) == 0) */
/*				{ */
/*				    printf("Server Exit...\n"); */
/*				    break; */
/*				} */
/*			} */
/*			fprintf(stderr,"THREAD #%i kilepett\n",id);*/
/*			*/
			
			//if client connected, insert new row into the table
			
			if (mysql_query(con, "use react")) 
			{
			  fprintf(stderr, "%s\n", mysql_error(con));
			  mysql_close(con);
			  exit(1);
			}

			char first[1000] = "insert into sequences (owner, name, description) values ('tunde', 'test', 'szal";
			
			char second[10];
		  	sprintf(second, "%d", id);
			
			strcat(first, second);
			strcat(first,"');");
			fprintf(stderr,"%s\n\n\n",first);



			if (mysql_query(con, first)) 
			{
			  fprintf(stderr, "%s\n", mysql_error(con));
			  mysql_close(con);
			  exit(1);
			}
			
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			
			if (mysql_query(con, "SELECT * FROM sequences")) 
			{
			  finish_with_error(con);
			}

			MYSQL_RES *result = mysql_store_result(con);

			if (result == NULL) 
			{
			  finish_with_error(con);
			}

			int num_fields = mysql_num_fields(result);

			MYSQL_ROW row;

			while ((row = mysql_fetch_row(result))) 
			{ 
			  for(int i = 0; i < num_fields; i++) 
			  { 
				  printf("%s ", row[i] ? row[i] : "NULL"); 
			  } 
				  printf("\n"); 
			}

			mysql_free_result(result);
		}
		else
		{
			//fprintf(stderr,"threa #%i is going to sleep \n", id);
			sleep(1);
			fprintf(stderr,"THREAD #%i SLEEPING\n",id);
		}
	}
	
	fprintf(stderr,"THREAD #%i ENDING\n",id);
}



int main()
{
	setbuf(stderr, NULL);
	setbuf(stdout, NULL);


	//elinditjuk a szalakat, amik blokkolva lesznek, amig uj kliens kapcsolodik
	for(int i = 0; i < THREAD_POOL_SIZE; ++i)
	{
		pthread_create(&thread_pool[i], NULL, thread_function, (void *)i);
	}


	//ctrl-c lekezelese
	signal(SIGINT, sigintHandler);

	//socket letrehozasa
	sock_fd = socket(AF_INET, SOCK_STREAM, 0);
	
    
    //jelezzuk a kernelnek, hogy ezt a socketet ujra akarjuk hasznalni
    int yes = 1;
    if (setsockopt(sock_fd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(yes)) == -1) 
	{
		perror("setsockopt");
		exit(1);
	}
	
	
	if(sock_fd == -1)
	{
		printf("socket creation failed!");
		return -1;
	}
	else
	{
		if(DEBUG)
		{
			printf("socket file descriptor : %i \n",sock_fd);
		}
	}

	
	struct sockaddr_in server_addr, client_addr;

	//inicializalas
	bzero(&server_addr, sizeof(server_addr));
	bzero(&client_addr, sizeof(client_addr)); 
	
	
	server_addr.sin_family = AF_INET;
	server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	server_addr.sin_port = htons(PORT);
	
	
	if((bind(sock_fd, (const struct sockaddr *) &server_addr, sizeof(server_addr))) != 0)
	{
		printf("socket bind failed");
		return -2;
	}
	else
	{
		if(DEBUG)
		{
			printf("Socket succesfully binded!\n");
		}
	}
	
	
	if((listen(sock_fd,999)) != 0)
	{
		printf("Listen failed!");
		return -1;
	}
	else
	{
		if(DEBUG)
		{
			printf("Listen succesfully performed!\n");
		}
	}




	con = mysql_init(NULL);

	//csatlakozunk az adatbazishoz
	if (con == NULL) 
	{
	  fprintf(stderr, "%s\n", mysql_error(con));
	  exit(1);
	}

	//bejelentkezunk
	if (mysql_real_connect(con, "localhost", "tunde", "incorrect", 
		  NULL, 0, NULL, 0) == NULL) 
	{
	  fprintf(stderr, "%s\n", mysql_error(con));
	  mysql_close(con);
	  exit(1);
	}  
	

	while(1)
	{
		int len = sizeof(client_addr);	

		printf("Waiting for connection...\n");
		
		//varunk egy beerkezo kacsolatra
		int connection_fd = accept(sock_fd, (struct sockaddr *) &client_addr, &len);
			
		if(connection_fd < 0)
		{
			printf("Server accept failed!");
			return -1;
		}
		else
		{
			if(DEBUG)
			{
				printf("Server accept succesfull!\n");
			}
		}
		
		int* p_client = malloc(sizeof(int));
		*p_client = connection_fd;
		
		pthread_mutex_lock(&mutex);
		
		enqueue(p_client);
		pthread_cond_signal(&cond_var);
		
		pthread_mutex_unlock(&mutex);
		
	}

	
	close(sock_fd);

	return 0;
}
