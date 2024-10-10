##
## EPITECH PROJECT, 2018
## Task
## File description:
## Function my_print_com
##

SRC	=	src/main.c\


INCLUDE	=	include/

CPPFLAGS	=	-I$(INCLUDE) -I$(LIBINCLUDE) -g3 -lcrypto

OUTPUT	=	client

OBJ	=	$(SRC:.c=.o)

CFLAGS =	-Wall -Wextra -g3

all:	$(OUTPUT)

$(OUTPUT): $(OBJ)
	gcc  -o $(OUTPUT) $(OBJ) -I$(INCLUDE) -lcrypto

re:	fclean all

clean:
	rm -rf src/*.o

fclean:	clean
	rm -rf $(OUTPUT)
