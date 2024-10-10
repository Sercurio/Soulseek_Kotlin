/*
** EPITECH PROJECT, 2019
** Task
** File description:
** Function my_print_com
*/

#ifndef MYCHAP
#define MYCHAP

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/udp.h>
#include <netinet/ip.h>
#include <net/ethernet.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <time.h>
#include <stdlib.h>
#include <sys/select.h>
#include <netdb.h>
#include <netinet/if_ether.h>
#include <arpa/inet.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include <openssl/sha.h>
#include <net/ethernet.h>
#include <linux/if_packet.h>

struct tcphdr {
    u_short th_sport;  /* source port */
    u_short th_dport;  /* destination port */
    uint32_t th_seq;   /* sequence number */
    uint32_t th_ack;   /* acknowledgement number */
#if BYTE_ORDER == LITTLE_ENDIAN
    u_int th_x2: 4,  /* (unused) */
    th_off: 4;  /* data offset */
#endif
#if BYTE_ORDER == BIG_ENDIAN
    u_int th_off: 4,  /* data offset */
    th_x2: 4;  /* (unused) */
#endif
    u_char th_flags;
#define TH_FIN 0x01
#define TH_SYN 0x02
#define TH_RST 0x04
#define TH_PUSH 0x08
#define TH_ACK 0x10
#define TH_URG 0x20
#define TH_ECE 0x40
#define TH_CWR 0x80
#define TH_FLAGS (TH_FIN|TH_SYN|TH_RST|TH_ACK|TH_URG|TH_ECE|TH_CWR)

    u_short th_win;   /* window */
    u_short th_sum;   /* checksum */
    u_short th_urp;   /* urgent pointer */
};
//__attribute__((packed));

typedef struct req {
    char direction;
    unsigned char *data;
    int len;
} req_t;

#endif
