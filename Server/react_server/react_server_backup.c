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


#define PORT 8080
#define DEBUG 1
#define MAX 80

int sock_fd;


void sigintHandler(int sig_num) 
{ 
    /* Reset handler to catch SIGINT next time. 
       Refer http://en.cppreference.com/w/c/program/signal */
    //signal(SIGINT, sigintHandler); 
    printf("\n Closing sockets!\n");
    close(sock_fd);
    //raise(SIGINT);
    //fflush(stdout);
    exit(0); 
} 


void * func(void * connection_fd) 
{ 

	int sock_fd = *((int *)connection_fd);
	free(connection_fd); //felszabaditjuk mert nem kell tobbet
	
    char buff[MAX]; 
    int n; 
    // infinite loop for chat 
    for (;;) { 
        bzero(buff, MAX); 
  
        // read the message from client and copy it in buffer 
        read(sock_fd, buff, sizeof(buff)); 
        // print buffer which contains the client contents 
        printf("From client: %s\t To client : ", buff); 
        bzero(buff, MAX); 
        n = 0; 
        // copy server message in the buffer 
        while ((buff[n++] = getchar()) != '\n') 
            ; 
  
        // and send that buffer to client 
        write(sock_fd, buff, sizeof(buff)); 
  
        // if msg contains "Exit" then server exit and chat ended. 
        if (strncmp("exit", buff, 4) == 0) { 
            printf("Server Exit...\n"); 
            break; 
        } 
    } 
} 



int main()
{
	signal(SIGINT, sigintHandler);

	
	sock_fd = socket(AF_INET, SOCK_STREAM, 0);
	
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



	while(1)
	{
		int len = sizeof(client_addr);

		printf("Waiting for connection...\n");
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
	
		pthread_t t;
		int * p_connection_fd = malloc(sizeof(int));
		*p_connection_fd = connection_fd;
		pthread_create(&t, NULL, func, p_connection_fd);
		//func(connection_fd); 
	}

	
	close(sock_fd);

	return 0;
}
