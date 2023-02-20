/*
 ** EPITECH PROJECT, 2019
 ** Task
 ** File description:
 ** Function my_print_com
 */

#include "mychap.h"

void init_sock(int fd, char *inter)
{
    struct sockaddr_ll addr = {0};
    struct ifreq ifr;

    strncpy(ifr.ifr_name, inter, IFNAMSIZ);
    if (ioctl(fd, SIOCGIFINDEX, &ifr) == -1){
        perror("ioctl");
        close(fd);
        exit(84);
    }
    addr.sll_ifindex = ifr.ifr_ifindex;
    addr.sll_halen = ETH_ALEN;
    addr.sll_family = AF_PACKET;
    if (bind(fd, (struct sockaddr *)&addr, sizeof(addr)) == -1)
        perror("bind");
}
req_t *receive_package(int fd)
{
    char *buff = malloc(4096);
    struct tcphdr *tcp_hdr;
    void *another;
    fd_set readfs;
    int ok = 0;
    req_t *ret = malloc(sizeof(req_t));

    FD_ZERO(&readfs);
    FD_SET(fd, &readfs);
    select(fd + 1, &readfs, NULL, NULL, NULL);
    if (FD_ISSET(fd, &readfs))
        ok =read(fd, buff, 4096);
    buff[ok] = 0;
    tcp_hdr = (void *) buff + sizeof(struct iphdr) + sizeof(struct ethhdr);
    another = tcp_hdr;
    another += 2;
    if (ntohs(*(unsigned short *)tcp_hdr) == (unsigned short)2416 || ntohs(*(unsigned short *)another) == (unsigned short)2416) {
        //dirty tcp port checking
        another = tcp_hdr + 12;
        ret->data = (void *) buff + sizeof(struct iphdr) + sizeof(struct ethhdr) 
            + (tcp_hdr->th_off * 4);
        ret->len = ok - (sizeof(struct iphdr) + sizeof(struct ethhdr) 
            + (tcp_hdr->th_off * 4));
        if (ntohs(*(unsigned short *)tcp_hdr) == (unsigned short)2416)
            ret->direction = 0;
        else    
            ret->direction = 1;
        //determine if the request goes to or came from server with the port witch match
        if (ret->len)
            return (ret);
    }
    free(ret);
    return (0);
}

int main (int argc, char **argv)
{
    int fd = socket(AF_PACKET,SOCK_RAW, htons(ETH_P_ALL));
    int ok = 1;
    req_t *dassault = 0;
    if (argc > 1) {
        init_sock(fd, argv[1]);
        while (1){
            dassault = receive_package(fd);
            if (dassault){
                if (dassault->direction)
                    printf("\n>>>>>>>>upload<<<<<<\n");
                else
                    printf("\n>>>>>>>>download<<<<<<\n");
                for (int i = 0; dassault->len > i; i++) {
                    if (isprint(dassault->data[i]))
                        printf("%c", dassault->data[i]);
                    else    
                        printf("*");
                }
                printf("\n-----------\n");
                for (int i = 0; dassault->len > i; i++)
                    printf("%02x ", dassault->data[i]);
                printf("\n\n\n");
                free(dassault);
            }
        }   
    }
    return (0);
}
