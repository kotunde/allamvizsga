//#include <my_global.h>

//gcc mysql_test.c -lmysqlclient -o sql

#include <mysql/mysql.h>
#include <stdio.h>
#include <stdlib.h>


void finish_with_error(MYSQL *con)
{
  fprintf(stderr, "%s\n", mysql_error(con));
  mysql_close(con);
  exit(1);        
}


int main(int argc, char **argv)
{  
	
	MYSQL *con = mysql_init(NULL);

	if (con == NULL) 
	{
	  fprintf(stderr, "%s\n", mysql_error(con));
	  exit(1);
	}

	if (mysql_real_connect(con, "localhost", "attila", "incorrect", 
		  NULL, 0, NULL, 0) == NULL) 
	{
	  fprintf(stderr, "%s\n", mysql_error(con));
	  mysql_close(con);
	  exit(1);
	}  

/*	if (mysql_query(con, "CREATE DATABASE react")) */
/*	{*/
/*	  fprintf(stderr, "%s\n", mysql_error(con));*/
/*	  mysql_close(con);*/
/*	  exit(1);*/
/*	}*/

	if (mysql_query(con, "use react")) 
	{
	  fprintf(stderr, "%s\n", mysql_error(con));
	  mysql_close(con);
	  exit(1);
	}


	if (mysql_query(con, "insert into sequences (owner, name, description) values ('attila', 'test2', 'hellohellohellohellohellohellohello')")) 
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


	mysql_close(con);
	exit(0);
}
