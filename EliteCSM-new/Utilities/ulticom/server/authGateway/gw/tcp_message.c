/******************************************************************************

                        Copyright (c) 2005 Ulticom, Inc.


File:     smsClientMod.h
Author:   Gary Birnhak
Created:  02/2/05
Revision: $Revision$ $Date$




DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE DEVELOPMENT OF A WORKING SOFTWARE PROGRAM.  THE SOURCE CODE PROVIDED IS NOT WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.
 
 ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER  INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and interest in and to the Software, and all intellectual property rights associated therewith, including without limitation, rights to copyrights, trade secrets or know-how. This disclaimer agreement shall be governed and construed in accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time without notice. Ulticom has no continuing obligation to provide additional source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples; these changes may be reported, for the sample code included herein, in new editions of the product.
******************************************************************************/


#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include <errno.h>

static int 
tcp_recv_buffer( int fd, unsigned char *buffer, size_t size, unsigned short size_required );
static int
tcp_write_buffer( int fd, unsigned char *buffer, size_t size );


/*************************************************************************

FUNCTION
        int tcp_send( int fd, unsigned char *buffer, size_t size )

DESCRIPTION
        Sends a message over the TCP socket

INPUTS
        Arg:    fd - TCP socket file descriptor
        Arg:    buffer - message to be sent
        Arg:    size - size of the message to send
        Others: None

OUTPUTS
        Return: number of bytes sent, or error code returned by tcp_write_buffer 
        Arg:    None
        Others: None
*************************************************************************/
int
tcp_send( int fd, unsigned char *buffer, size_t size )
{
    int iRet;
    unsigned short usSize = (unsigned short) size;
    unsigned short msg_len = htons( usSize );

#ifdef DEBUG    
    printf("tcp_send: attempting to send msg of size=%d\n", usSize );
#endif

    /* first send the message size */
    iRet = tcp_write_buffer( fd, (unsigned char *) &msg_len, sizeof(unsigned short) );

    if( iRet < 0 )
    {
	return iRet;
    }

    /* then send the actual message */
    iRet = tcp_write_buffer( fd, buffer, size );

    if( iRet < 0 )
    {
	return iRet;
    }
    else
    {
	/* return number of bytes sent */
#ifdef DEBUG    
	printf("tcp_send: msg sent!\n");
#endif
	return size;
    }
}


/*************************************************************************

FUNCTION
        int tcp_write_buffer( int fd, unsigned char *buffer, size_t size )

DESCRIPTION
        Sends "size" bytes over the TCP socket

INPUTS
        Arg:    fd - TCP socket file descriptor
        Arg:    buffer - data to be sent
        Arg:    size - how many bytes to send
        Others: None

OUTPUTS
        Return: number of bytes sent, or error returned by write
        Arg:    None
        Others: None
*************************************************************************/
static int
tcp_write_buffer( int fd, unsigned char *buffer, size_t size )
{
    unsigned short sent_count = 0;
    unsigned short send_count = size;
    unsigned char *pBuffer = &buffer[0];

    while( send_count > 0 )
    {
	sent_count = write( fd, pBuffer, send_count );

	if( sent_count > 0 )
	{
	    /* okay, some data was sent */

	    /* send_count represents the number of bytes
	       left to be sent in the buffer */
	    send_count -= sent_count;
	    
	    if( 0 == send_count )
	    {
		/* done; return number of bytes sent */
		return size;
	    }
	    else
	    {
		/* advance the pointer and try again */
		pBuffer += sent_count;
	    }
	}
	else
	{
	    if( EINTR == errno )
	    {
		perror("tcp_write_buffer - continuing");
		continue;
	    }

	    /* error; return whatever received from write
	       leaving errno intact */
	    perror("tcp_write_buffer");
	    return sent_count;
	}
    }

    return 0;  /* make compiler happy */
}




/*************************************************************************

FUNCTION
        int tcp_recv( int fd, unsigned char *buffer, size_t size )

DESCRIPTION
        Read a message from the TCP socket

INPUTS
        Arg:    fd - TCP socket file descriptor
        Arg:    buffer - where to put the received data
        Arg:    size - how many bytes the buffer may hold
        Others: None

OUTPUTS
        Return: returns whatever tcp_recv_buffer returned 
        Arg:    None
        Others: None
*************************************************************************/
int 
tcp_recv( int fd, unsigned char *buffer, size_t size )
{
    int iRet;
    unsigned short len_buffer;
    unsigned short msg_len;

    /* first get the msg size */
    iRet = tcp_recv_buffer( fd, (unsigned char *) &len_buffer, sizeof( unsigned short ), sizeof( unsigned short ) );

    if( iRet < 0 )
    {
	/* error */
	return iRet;
    }

    msg_len = ntohs( len_buffer );

#ifdef DEBUG    
    printf("tcp_recv: now reading msg size=%d\n", msg_len );
#endif

    /* now get the message */
    iRet = tcp_recv_buffer( fd, buffer, msg_len, msg_len );

#ifdef DEBUG    
    printf("tcp_recv: msg received, number of bytes=%d\n", iRet );
#endif
    return iRet;
}


/*************************************************************************

FUNCTION
        int tcp_recv_buffer( int fd, unsigned char *buffer, size_t size, unsigned short size_required )

DESCRIPTION
        Read "size_required" bytes from the TCP socket

INPUTS
        Arg:    fd - TCP socket file descriptor
        Arg:    buffer - where to put the received data
        Arg:    size - how many bytes the buffer may hold
        Arg:    size_required - only return after receiving this amount of bytes
        Others: None

OUTPUTS
        Return: returns number of bytes received, or error returned by read 
        Arg:    None
        Others: None
*************************************************************************/
static int 
tcp_recv_buffer( int fd, unsigned char *buffer, size_t size, unsigned short size_required )
{
    int recv_count = 0;
    int total_recv_count = 0;
    unsigned char *pBuffer = &buffer[0];

#ifdef DEBUG    
    printf("tcp_recv_buffer: enter, size_required=%d, size=%ld\n", size_required, size);
#endif

    if( size_required > size )
    {
	/* parameter error */
	return 0;
    }

    while( total_recv_count < size_required )
    {
	recv_count = read( fd, pBuffer, size_required - total_recv_count );
#ifdef DEBUG    
    printf("tcp_recv_buffer: read returned, recv_count=%d\n", recv_count);
#endif

	if( recv_count < 0 )
	{
	    if( EINTR == errno )
	    {
#ifdef DEBUG    
		perror("tcp_recv_buffer - continuing");
#endif
		continue;
	    }

	    /* error; return whatever read returned, leaving errno intact */
	    perror("tcp_recv_buffer");
	    return recv_count;
	}
	else if( 0 ==  recv_count)
	{
	    return 0;
	}
	else
	{
	    total_recv_count += recv_count;

	    if( total_recv_count == size_required )
	    {
		/* done; return number of bytes received */
		return size_required;
	    }
	    else
	    {
		/* advance buffer pointer and try again */
		pBuffer += recv_count;
	    }
	}
    }

    return 0; /* make compilers happy */
}

