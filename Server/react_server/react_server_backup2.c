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


#define PORT 8080
#define DEBUG 1
#define MAX 80

int sock_fd;


struct thread_param
{
	int connection_fd;
	MYSQL* con;	
};

void sigintHandler(int sig_num) 
{ 
	//bezarjuk a socketet
	close(sock_fd);
	
    printf("\n Socket closed!\n");
    exit(0); 
} 


void finish_with_error(MYSQL *con)
{
  fprintf(stderr, "%s\n", mysql_error(con));
  mysql_close(con);
  exit(1);        
}


void * func(void * thread_param) 
{ 
	//fprintf(stderr, "thread started!\n");
	//fprintf(stderr, "connection file descriptor : %i\n", ((struct thread_param *)thread_param)-> connection_fd);
	
	int connection_fd = ((struct thread_param *)thread_param)-> connection_fd;
	MYSQL* con = ((struct thread_param *)thread_param)->con;
	
	fprintf(stderr, "connection file descriptor : %i\n",connection_fd);
	
	//fprintf(stderr, "hello\n");
	free(thread_param); //felszabaditjuk mert nem kell tobbet
	
/*    char buff[MAX]; */
/*    int n; */
/*    // infinite loop for chat */
/*    for (;;) */
/*    { */
/*        bzero(buff, MAX); */
/*  */
/*        // read the message from client and copy it in buffer */
/*        read(sock_fd, buff, sizeof(buff)); */
/*        // print buffer which contains the client contents */
/*        printf("From client: %s\t To client : ", buff); */
/*        bzero(buff, MAX); */
/*        n = 0; */
/*        // copy server message in the buffer */
/*        while ((buff[n++] = getchar()) != '\n') */
/*            ; */
/*  */
/*        // and send that buffer to client */
/*        write(sock_fd, buff, sizeof(buff)); */
/*  */
/*        // if msg contains "Exit" then server exit and chat ended. */
/*        if (strncmp("exit", buff, 4) == 0) { */
/*            printf("Server Exit...\n"); */
/*            break; */
/*        } */
/*    } */
    
    
    
    if (mysql_query(con, "use react")) 
	{
	  fprintf(stderr, "%s\n", mysql_error(con));
	  mysql_close(con);
	  exit(1);
	}

	char first[1000] = "insert into sequences (owner, name, description) values ('attila', 'test2', 'szal";
	
	char second[10];
  	sprintf(second, "%d", connection_fd);
	
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



int main()
{
	//ctrl-c lekezelese
	signal(SIGINT, sigintHandler);

	//
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




	MYSQL *con = mysql_init(NULL);

	//csatlakozunk az adatbazishoz
	if (con == NULL) 
	{
	  fprintf(stderr, "%s\n", mysql_error(con));
	  exit(1);
	}

	//bejelentkezunk
	if (mysql_real_connect(con, "localhost", "attila", "incorrect", 
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
		
		
		struct thread_param * thread_param = (struct thread_param*) malloc(sizeof(thread_param));
		
		thread_param->connection_fd = connection_fd;
		thread_param->con = con;
	
	
		pthread_t t;
		
/*		int * p_connection_fd = malloc(sizeof(int));*/
/*		*p_connection_fd = connection_fd;*/
		
		pthread_create(&t, NULL, func, (void *)thread_param);
		
		//pthread_join(t, NULL);	
		//func(connection_fd); 
	}

	
	close(sock_fd);
	
	printf("\nDONE!\n");

	return 0;
}
