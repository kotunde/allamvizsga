#include <stdio.h>
#include <stdlib.h>


struct node 
{
	int value;
	struct node* next;
};

typedef struct node node_t;


node_t* create_new_node(int value)
{
	node_t* node = (node_t*) malloc(sizeof(node_t));
	node->value = value;
	node->next = NULL;
	
	return node; 
}


node_t* insert_at_head(node_t * head, node_t* node_to_insert)
{
	node_to_insert->next = head;
	return node_to_insert;
}


void print_list(node_t* head)
{
	node_t* temporary = head;
	
	while(temporary != NULL)
	{
		printf("%i - ", temporary->value);
		fflush(stdout);   
		temporary = temporary->next;
	}
	
	printf("\n");
}


node_t* find_node(node_t* head, int value)
{
	node_t* tmp = head;
	
	while(tmp != NULL)
	{
		if(tmp->value == value)
		{
			return tmp;
		}
		else
		{
			tmp = tmp->next;
		}
	}
	
	return NULL;
}


void insert_after_node(node_t* node_to_insert_after, node_t * new_node)
{
	new_node->next = node_to_insert_after->next;
	node_to_insert_after->next = new_node;
}


int main()
{
	node_t* head = NULL;//(node_t*) malloc(sizeof(node_t)); 
	node_t* tmp ;//= (node_t*) malloc(sizeof(node_t));
	
	for(int i = 0; i < 25; ++i)
	{
		tmp = create_new_node(i);
		head = insert_at_head(head, tmp);
	}

	print_list(head);
	tmp = find_node(head, 13);

	printf("found node with value %i", tmp->value);

	insert_after_node(tmp, create_new_node(75));
	print_list(head);
	return 0;
}
