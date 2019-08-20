/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source   authGWdbg.c

@ClearCase-version: $Revision:/main/sw9/eecn_902_sp2/1 $ 

@date     $Date:23-Feb-2005 11:10:50 $

@product  Signalware

@subsystem  WLAN Gateway

DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE DEVELOPMENT OF A WORKING SOFTWARE PROGRAM.  THE SOURCE CODE PROVIDED IS NOT WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.
 
 ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER  INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and interest in and to the Software, and all intellectual property rights associated therewith, including without limitation, rights to copyrights, trade secrets or know-how. This disclaimer agreement shall be governed and construed in accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time without notice. Ulticom has no continuing obligation to provide additional source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples; these changes may be reported, for the sample code included herein, in new editions of the product.

******************************************************************************/


#include	<stdarg.h>
#include	<stdio.h>
#include	<stdlib.h>
#include	<fcntl.h>
#include	<string.h>
#include <poll.h>

#ifdef	_HPUX_SOURCE
#include	<sys/poll.h>
#endif

#include <Ft.h>
#include <msgtypes.h>
#include <QSource.h>
#include <apiinc.h>
#include <authGWdbg.h>

#include <MAPgateway.h>
#include <SMSgateway.h>
#include <wlan_custom.h>
#include <apiinc.h>


#define DATETIMESIZE    64
/*************** global variables ***************************************/

dbg_t          *p_DBG;		/* pointer to dbg_t structure   */
char            mg_DBG_NAME[DBG_MAX_NAME + 1] = { 0 };
//extern OssGlobal world;

/* FUNCTION mg_dbg_change_trace_file() - change trace file		*/
/*************************************************************************
								
FUNCTION
	int mg_dbg_change_trace_file( char *fileName, char *errorReport )

DESCRIPTION
	This is used to change the trace file name.

INPUTS
	Arg:	fileName : pointer to file name
		errorReport : pointer to error string

	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/

int
mg_dbg_change_trace_file( char *fileName, char *errorReport )
{
    char            buff[MAX_TRACE_FILENAME + 50];
    FILE           *oldFile=NULL;
    int             returnValue = TRUE;

    if ( p_DBG->trace_file )
    {
	oldFile = p_DBG->trace_file;
	p_DBG->trace_file = 0;
    }

    if ( !strncasecmp( fileName, "stdout", 7 ) )
    {
	p_DBG->trace_file = stdout;
    }
    else
    {
	p_DBG->trace_file = fopen( fileName, "a" );
    }
    if ( !p_DBG->trace_file )
    {
	sprintf( buff, "Unable to open trace file %s", fileName );
	perror( buff );
	if ( errorReport )
	{
	    sprintf( errorReport, "Unable to open file %s: %s\n", fileName, strerror( errno ) );
	}
	p_DBG->trace_file = oldFile;
	returnValue = FALSE;
    }
    else
    {
	if ( oldFile != stdout )
	{
	    fclose( oldFile );
	}
    }
    p_DBG->newTraceFile = 0;
    return returnValue;
}


/* FUNCTION mg_dbg_init() - initialize debug buffer and masks		*/
/*************************************************************************
								
FUNCTION
	void mg_dbg_init(dbg_t *pdbg, char *pname, int argc, char *argv[])

DESCRIPTION
	This is the first function to be called to enable debugging
	facilities.

INPUTS
	Arg:	pdbg - pointer to global or shared memory dbg_t structure
		pname - character string indicating process name
		argc - command line argument count
		argv - pointer to each command line argument

	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
void
mg_dbg_init( dbg_t * pdbg, char *pname, int argc, char *argv[], FILE * trace_file, int *error, char *errorReport )
{
    int             i;
    int             restart=FALSE;

    p_DBG = pdbg;
    if ( restart == FALSE )
    {
	( void ) memset( ( char * ) p_DBG, 0, sizeof( dbg_t ) );
    }
    p_DBG->dbgMask = 0x1;

    mg_dbg_set_dbg_mask( argc, argv );	/* set p_DBG->dbgMask from cmd line */
    ( void ) mg_dbg_copym( mg_DBG_NAME, pname, DBG_MAX_NAME + 1 );

    if ( p_DBG )
    {
	restart = FALSE;	/* default      */
	if ( trace_file )
	{
	    p_DBG->trace_file = trace_file;
	    /*printf("Output directed to %x\n",  p_DBG->trace_file) */
	}
	else
	    p_DBG->trace_file = stdout;

	for ( i = 1; i < argc; i++ )
	{
	    if ( mg_dbg_cmp_ignore_case( argv[i], "-DEBUGFILE", 10 ) == TRUE )
	    {
		if ( argv[i + 1] && ( argv[i + 1][0] != '-' ) )
		{
		    if ( !mg_dbg_change_trace_file( argv[i + 1], errorReport ) )
		    {
			*error = errno;
			return;
		    }
		}
		else
		{
		    sprintf( errorReport, "The -debugfile option must be " "followed by a valid file name\nAborting\n" );
		    *error = ERRORinvalidARGUMENT;
		    return;
		}
	    }
	}

	for ( i = 1; i < argc; i++ )
	{
	    if ( mg_dbg_cmp_ignore_case( argv[i], "-RESTART", 8 ) == TRUE )
	    {
		restart = TRUE;
		break;
	    }
	}

	p_DBG->trace_enabled = FALSE;	/* default      */
	for ( i = 1; i < argc; i++ )
	{
	    if ( mg_dbg_cmp_ignore_case( argv[i], "-NOTRACE", 8 ) == TRUE )
	    {
		p_DBG->trace_enabled = FALSE;
		break;
	    }
	    if ( mg_dbg_cmp_ignore_case( argv[i], "-TRACE", 8 ) == TRUE )
	    {
		p_DBG->trace_enabled = TRUE;
		break;
	    }
	}
    }
}


/* FUNCTION mg_dbg_check_trace_file_changed() - change trace file		*/
/*************************************************************************
								
FUNCTION
	void mg_dbg_check_trace_file_changed(  )

DESCRIPTION
	This is used to check if the debug output has been changed by the 
	gmapomd tool.

INPUTS
	Arg:	None

	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/

void
mg_dbg_check_trace_file_changed(  )
{
    if ( !p_DBG )
	return;

    if ( p_DBG->newTraceFile )
    {
	printf( "New trace file: %s\n", p_DBG->traceFilename );
	mg_dbg_change_trace_file( p_DBG->traceFilename, ( char * ) 0 );
    }
}
/* FUNCTION datetimestamp(char*) - writes current time to char buffer	*/
/*************************************************************************
	
DESCRIPTION
	writes current time to char buffer

OUTPUTS
	Return:	None

	Arg:	char buffer

FEND
*************************************************************************/
int datetimestamp(char* outstr)
{
	struct timeval curTime;
	gettimeofday(&curTime, NULL);
	int milli = curTime.tv_usec / 1000;

	char buffer [80];
	strftime(buffer, 80, "%d %b %Y %T", localtime(&curTime.tv_sec));

	sprintf(outstr, "%s:%d", buffer, milli);
	return strlen(outstr);
}

/* FUNCTION DBG_DISP() - display debug information if mask is enabled	*/
/*************************************************************************
								
FUNCTION
	void DBG_DISP(U32 mask, U32 err_number, char *format, ...)

DESCRIPTION
	Display debug message on console if the mask is enabled

INPUTS
	Arg:	mask - display category (mask)
		err_number - error number for freezing the trace
		format - format string
		variables ...

	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
void
DBG_DISP( U32 mask, U32 err_number, char *format, ... )
{
    va_list         list;
    char            dbg_buff[200], buff[256];
	
	char outstr[DATETIMESIZE];
	datetimestamp(outstr);
	
    mg_dbg_check_trace_file_changed(  );
    if ( p_DBG && ( mask & p_DBG->dbgMask ) )
    {
	va_start( list, format );
	( void ) vsnprintf( dbg_buff, sizeof( dbg_buff ), format, list );
	va_end( list );

	if ( mask & DBG_MASK_ER )
	{
	    snprintf( buff, sizeof( buff ), "%s %s[?ERR](%d): %s", outstr, mg_DBG_NAME, err_number, dbg_buff );	/* no CR/LF */
	}
	else
	{
	    snprintf( buff, sizeof( buff ), "%s %s[%04x] (%d): %s", outstr, mg_DBG_NAME, ( U16 ) mask, err_number, dbg_buff );
	}

	( void ) fprintf( p_DBG->trace_file, buff );
	if ( mask & DBG_MASK_ER )
	{
	    mg_dbg_error_log( buff );

	    /* Log errors to Event.diag file */
	    FtInternalDiagnosticEx( buff, 0,	/* no retry */
				    TRUE );	/* alertable */
	}
	else
	    mg_dbg_trace_execution( "%s %s",outstr, dbg_buff );
    }
    if ( p_DBG && err_number && ( err_number == p_DBG->trace_freeze ) )
    {
	p_DBG->trace_enabled = FALSE;
    }
}



/* FUNCTION mg_dbg_trace_execution() - trace program execution (ascii)	*/
/*************************************************************************
								
FUNCTION
	void mg_dbg_trace_execution(char *format, ...)

DESCRIPTION
	This function is called to trace program execution. An entry
	is created in the trace buffer to indicate program execution
	sequence.

INPUTS
	Arg:	format - format string
		variables ...

	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
void
mg_dbg_trace_execution( char *format, ... )
{
    va_list         list;
    dbg_trace_ent_t *pent;	/* pointer to trace entry       */
    char            buff[200];


    if ( p_DBG == 0 )
    {
	return;
    }
    if ( p_DBG->trace_enabled == FALSE )
    {
	return;
    }
    pent = mg_dbg_trace_get_entry(  );
    if ( pent )
	pent->type = DBG_TP_ASCII;	/* ascii text           */

    va_start( list, format );
    ( void ) vsnprintf( buff, sizeof( buff ), format, list );
    va_end( list );
    ( void ) mg_dbg_copym( ( char * ) pent->data, buff, DBG_TRACE_DATA_SZ );
}


/* FUNCTION mg_dbg_trace_ipc() - trace received/transmitted  IPC msg	*/
/*************************************************************************
								
FUNCTION
	void mg_dbg_trace_ipc(dbg_ipc_t *pipc, U8 type, int msg_size)

DESCRIPTION
	This function is called to trace incoming or outgoing IPC msg

INPUTS
	Arg:	pipc - IPC buffer address
		type - DBG_TP_SEND_IPC or DBG_TP_RECV_IPC
		msg_size - IPC message size (including Header_t)
		
	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
void
mg_dbg_trace_ipc( dbg_ipc_t * pipc, U8 type, unsigned int msg_size )
{
    int             size;
    dbg_trace_ent_t *pent;	/* pointer to trace entry       */


    if ( p_DBG == 0 )
    {
	return;
    }
    if ( p_DBG->trace_enabled == FALSE )
    {
	return;
    }
    pent = mg_dbg_trace_get_entry(  );
    if ( pent )
	pent->type = type;

    pent->msg_type = pipc->hdr.messageType;
/*	if(msg_size < 0)
	{
		msg_size = DBG_TRACE_DATA_SZ;
	}
*/
    if ( msg_size < sizeof( Header_t ) )
    {
	pent->msg_size = msg_size;	/* error case */
    }
    else
    {
	pent->msg_size = msg_size - sizeof( Header_t );
    }
    if ( type == DBG_TP_SEND_IPC )
    {
	( void ) mg_dbg_copym( pent->proc_name, pipc->hdr.destination, MAXlogicalNAME );
    }
    else if ( type == DBG_TP_RECV_IPC )
    {
	( void ) mg_dbg_copym( pent->proc_name, pipc->hdr.originator, MAXlogicalNAME );
    }
    size = pent->msg_size;
    if ( size > DBG_TRACE_DATA_SZ )
    {
	size = DBG_TRACE_DATA_SZ;
    }
    if ( size )
    {
	( void ) memcpy( ( char * ) pent->data, ( char * ) pipc + sizeof( Header_t ), size );
    }
}




/* FUNCTION mg_dbg_trace_bin() - trace binary data				*/
/*************************************************************************
								
FUNCTION
	void mg_dbg_trace_bin(char *pdata, int primitive, int msg_size, char *phdr, ...)

DESCRIPTION
	This function is called to trace binary data

INPUTS
	Arg:	pdata - binary data pointer
		primitive - binary data identifier
		msg_size - data size
		phdr - display header string (up to MAXlogicalNAME)
		
	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
void
mg_dbg_trace_bin( char *pdata, int primitive, int msg_size, char *phdr, ... )
{
    va_list         list;
    int             size;
    dbg_trace_ent_t *pent;	/* pointer to trace entry       */
    char            buff[200];


    if ( p_DBG == 0 )
    {
	return;
    }
    if ( p_DBG->trace_enabled == FALSE )
    {
	return;
    }
    pent = mg_dbg_trace_get_entry(  );
    if ( pent )
	pent->type = DBG_TP_BIN;
    pent->primitive = primitive;
    pent->msg_type = 0;;
    pent->msg_size = msg_size;
    pent->proc_name[0] = 0;
    if ( phdr )
    {
	va_start( list, phdr );
	( void ) vsprintf( buff, phdr, list );
	va_end( list );
	( void ) mg_dbg_copym( pent->proc_name, buff, MAXlogicalNAME + 1 );
    }
    if ( msg_size < 0 )
    {
	msg_size = DBG_TRACE_DATA_SZ;
    }
    size = msg_size;
    if ( size > DBG_TRACE_DATA_SZ )
    {
	size = DBG_TRACE_DATA_SZ;
    }
    if ( size )
    {
	( void ) memcpy( ( char * ) pent->data, ( char * ) pdata, size );
    }
}


/* FUNCTION mg_dbg_process_dbg_cmd(): process debug command		*/
/*************************************************************************
								
FUNCTION
	int mg_dbg_process_dbg_cmd(char *pcmd)

DESCRIPTION
	This function checks for the debug command text and set the
	global p_DBG->dbgMask to the mask value if command text is "0xnnnn"
	where nnnn is the hexadecimal value of the debug mask.

INPUTS
	Arg:	pcmd - pointer to the debug command text

	Others:	None

OUTPUTS
	Return:	TRUE - if the text is a debug mask command
		FALSE - otherwise

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
int
mg_dbg_process_dbg_cmd( char *pcmd )
{
    int             mask;
    int             old_mask;

    if ( !p_DBG )
	return ( FALSE );
    while ( *pcmd == ' ' )
    {
	pcmd++;			/* skip leading blanks */
    }
    if ( *pcmd == '0' && toupper( *( pcmd + 1 ) ) == 'X' )
    {
	pcmd += 2;
	mask = ( int ) mg_dbg_gethex( pcmd );
	old_mask = p_DBG->dbgMask;	/* save old mask        */
	p_DBG->dbgMask = 0x2;	/* enable following display */
	DBG_DISP( 0x2, 1, "old mask=0x%x, new mask=0x%x\n", old_mask, mask );
	p_DBG->dbgMask = mask;	/* set new mask         */
	return ( TRUE );
    }
    else
    {
	return ( FALSE );
    }
}


/* FUNCTION mg_dbg_set_dbg_mask() - set p_DBG->dbgMask from command line argument*/
/*************************************************************************
								
FUNCTION
	void mg_dbg_set_dbg_mask(int argc, char *argv[])

DESCRIPTION
	This function sets the global p_DBG->dbgMask to the value defined
	in command line argument as "0xnnnn"
	where nnnn is the hexadecimal value of the debug mask.

INPUTS
	Arg:	argc - number of command line arguments
		argv - array of pointers to arguments

	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
void
mg_dbg_set_dbg_mask( int argc, char *argv[] )
{
    int             i;
    char           *parg;

    if ( !p_DBG )
	return;
    for ( i = 1; i < argc; i++ )
    {
	parg = argv[i];
	if ( *parg == '0' && toupper( *( parg + 1 ) ) == 'X' )
	{
	    parg += 2;
	    p_DBG->dbgMask = ( U32 ) mg_dbg_gethex( parg );
	    return;
	}
    }
}



/* FUNCTION mg_dbg_error_log() - error msg trace				*/
/*************************************************************************
								
FUNCTION
	void	mg_dbg_error_log(char *buff)

DESCRIPTION
	This function is called to put error msg into an error log entry

INPUTS
	Arg:	None
		
	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
void
mg_dbg_error_log( char *buff )
{
    U16             index;	/* trace entry index            */
    struct timeval  curr_time;	/* current time                 */
    dbg_errlog_ent_t *pent;

    if ( p_DBG == 0 )
    {
	return;
    }
    if ( p_DBG->trace_enabled == FALSE )
    {
	return;
    }
    index = p_DBG->errlog.index;
    pent = &p_DBG->errlog.ent[index];	/* error log entry address */
    mg_dbg_gettimeofday( &curr_time );	/* get current time of day */
    pent->tv_sec = curr_time.tv_sec;	/* timestamp error event */
    pent->tv_usec = curr_time.tv_usec;
    pent->err_data_type = DBG_ERR_DATA_ASC;	/* ascii text in data   */
    pent->seq_nbr = ( U16 ) p_DBG->trace_seq_nbr++;	/* set to 0 if wrapped      */
    ( void ) mg_dbg_copym( ( char * ) pent->data, buff, DBG_ERR_DATA_SZ );
    index++;
    if ( index >= DBG_ERRLOG_ENTRIES )
    {
	p_DBG->errlog.wrapped = TRUE;
	index = 0;
    }
    p_DBG->errlog.index = index;
}


/* FUNCTION mg_dbg_trace_get_entry() - debug get trace entry		*/
/*************************************************************************
								
FUNCTION
	mg_dbg_trace_ent_t	*dbg_trace_get_entry()

DESCRIPTION
	This function is called to get a trace entry

INPUTS
	Arg:	None
		
	Others:	None

OUTPUTS
	Return:	pointer to trace entry

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
dbg_trace_ent_t *
mg_dbg_trace_get_entry(  )
{
    dbg_trace_ent_t *pent;
    U16             index;	/* trace entry index            */
    struct timeval  curr_time;	/* current time                 */

    if ( !p_DBG )
	return ( NULL );
    index = p_DBG->trace.index;
    pent = &p_DBG->trace.ent[index];	/* this trace entry address */
    mg_dbg_gettimeofday( &curr_time );	/* get current time of day */
    pent->tv_sec = curr_time.tv_sec;	/* timestamp traced event */
    pent->tv_usec = curr_time.tv_usec;
    pent->seq_nbr = ( U16 ) p_DBG->trace_seq_nbr++;	/* set to 0 if wrapped */
    index++;
    if ( index >= DBG_TRACE_ENTRIES )
    {
	p_DBG->trace.wrapped = TRUE;
	index = 0;
    }
    p_DBG->trace.index = index;	/* index to next entry  */
    return ( pent );		/* return this entry    */
}




/* FUNCTION mg_dbg_gettimeofday() - get current time of the day		*/
/*************************************************************************
								
FUNCTION
	void	mg_dbg_gettimeofday(struct timeval *pcurr_time)

DESCRIPTION
	This function is called to get the current time of the day

INPUTS
	Arg:	pcurr_time - pointer to current time structure
		
	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
void
mg_dbg_gettimeofday( struct timeval *pcurr_time )
{

#if !defined(__nonstopux) && !defined (RM600) && !defined (M88K)

    ( void ) gettimeofday( pcurr_time, 0 );	/* get current time of day */
#else

    ( void ) gettimeofday( pcurr_time );	/* get current time of day */

#endif
}


/* FUNCTION mg_dbg_get_token()- get token from a string			*/
/*************************************************************************

FUNCTION
	int mg_dbg_get_token(dbg_field_t *pfld, char *pstr, char *ds)

DESCRIPTION
	This function is called to get the current time of the day

INPUTS
	Arg:	pfld - pointer to field_t structure
 		pstr - pointer to string
                ds - null terminated string which contains
                     all the field delimiting characters
                     (NULL is a default terminating character)
		
	Others:	None

OUTPUTS
	Return:
		field length + 1

FEND
*************************************************************************/
/************************************************************************/
int
mg_dbg_get_token( dbg_field_t * pfld, char *pstr, char *ds )
{
    int             i;
    char            ch;
    char           *wds;	/* working ds pointer   */


    pfld->delimiter = 0;
    pfld->len = 0;		/* initialize   */
    for ( i = 0; i < DBG_MAX_FLD_SZ; i++ )
    {
	ch = *pstr++;		/* command character */
/*
 *		Search the delimiter string to match the command character.
 *		The null character at the end of the "ds" string is also
 *		examined.
 */
	wds = ds;		/* wds -> delimiter string */
	for ( ;; )
	{
	    if ( ch == *wds )
	    {			/* match one delimiter    */
		pfld->delimiter = ch;	/* save delimiter */
		pfld->data[i] = 0;	/* null terminate */
		pfld->len = ( U16 ) i;	/* field length   */
		return ( i + 1 );
	    }
	    else if ( *wds == 0 )
	    {
/*
 *				the character is not one of the delimiters
 *				(including this NULL).
 */
		break;
	    }
	    wds++;		/* check next delimiter */
	}
	pfld->data[i] = ch;	/* save non-delimiter character */
    }
    pfld->delimiter = 0;	/* save delimiter */
    pfld->data[DBG_MAX_FLD_SZ - 1] = 0;	/* null terminate */
    pfld->len = DBG_MAX_FLD_SZ - 1;	/* field length   */
    return ( DBG_MAX_FLD_SZ );
}


/* FUNCTION mg_dbg_dump_trace_data() - dump trace data in multiple lines 	*/
/*************************************************************************
FUNCTION
	void mg_dbg_dump_trace_data(U8 *pU8, int size, int spaces)

DESCRIPTION

INPUTS
	Arg:	pU8 - pointer to data string
		size- number of bytes to dump
		spaces - number of leading spaces in each line
		
	Others:	None

OUTPUTS
	Return: None
	
FEND
*************************************************************************/
/************************************************************************/
void
mg_dbg_dump_trace_data( U8 * pU8, int size, int spaces )
{
    int             i;

    if ( size == 0 || !p_DBG )
    {
	return;
    }
    mg_dbg_check_trace_file_changed(  );
    if ( size > DBG_TRACE_DATA_SZ )
    {
	size = DBG_TRACE_DATA_SZ;
    }
    for ( ;; )
    {
	for ( i = 0; i < spaces; i++ )
	{
	    ( void ) fprintf( p_DBG->trace_file, " " );
	}
	if ( size > 16 )
	{
	    mg_dbg_hex_dump_a_line( pU8, 16 );
	    mg_dbg_hex_to_asc( pU8, 16 );
	    size -= 16;
	    pU8 += 16;
	    ( void ) fprintf( p_DBG->trace_file, "\n" );
	}
	else
	{
	    mg_dbg_hex_dump_a_line( pU8, size );
	    mg_dbg_hex_to_asc( pU8, size );
	    ( void ) fprintf( p_DBG->trace_file, "\n" );
	    break;
	}
    }
}





/*FUNCTION mg_dbg_hex_dump_a_line() - dump a line (up to 16 bytes) in hex 	*/
/*************************************************************************

FUNCTION 
	mg_dbg_hex_dump_a_line() - dump one line in hex format

DESCRIPTION
	dump one line (up to 16 bytes) of data in hex format

INPUTS
	Arg:	pdata - pointer to data
		size  - size of data

        Others: None

OUTPUTS
	Return:  

	Arg:    None

	Others: None

************************************************************************/
/************************************************************************/
void
mg_dbg_hex_dump_a_line( U8 * pdata, int size )
{
    int             i;

    if ( !p_DBG )
	return;

    mg_dbg_check_trace_file_changed(  );
    if ( size > 16 )
    {
	size = 16;
    }
    for ( i = 0; i < size; i++ )
    {
	( void ) fprintf( p_DBG->trace_file, "%02x ", *pdata++ );
    }
}


/* FUNCTION mg_dbg_hex_dump() - dump data in hex format			*/
/*************************************************************************

FUNCTION 
	mg_dbg_hex_dump() - dump data in hex format

DESCRIPTION
	dump data in hex format utility

INPUTS
	Arg:	phdr  - pointer to header text
		pdata - pointer to data
		size  - size of data

        Others: None

OUTPUTS
	Return:  

	Arg:    None

	Others: None

************************************************************************/
/************************************************************************/
void
mg_dbg_hex_dump( char *phdr, U8 * pdata, int size )
{
    int             offset;
    int             skipped;


    offset = 0;
    skipped = FALSE;
    if ( !p_DBG )
	return;

    mg_dbg_check_trace_file_changed(  );
    ( void ) fprintf( p_DBG->trace_file, "  %d(0x%x) bytes (%s)\n", size, size, phdr );
    for ( ;; )
    {
	if ( size > 16 )
	{
	    if ( mg_dbg_hex_all_zeros( pdata, 16 ) == TRUE )
	    {
		skipped = TRUE;
	    }
	    else
	    {
		( void ) fprintf( p_DBG->trace_file, "    (%04x)", offset );
		if ( skipped == TRUE )
		{
		    ( void ) fprintf( p_DBG->trace_file, "*" );
		}
		else
		{
		    ( void ) fprintf( p_DBG->trace_file, " " );
		}
		skipped = FALSE;
		mg_dbg_hex_dump_a_line( pdata, 16 );
		mg_dbg_hex_to_asc( pdata, 16 );
		( void ) fprintf( p_DBG->trace_file, "\n" );
	    }
	    size -= 16;
	    pdata += 16;
	    offset += 16;
	}
	else
	{
/*
 *		    always output last line
 */
	    ( void ) fprintf( p_DBG->trace_file, "    (%04x)", offset );
	    if ( skipped == TRUE )
	    {
		( void ) fprintf( p_DBG->trace_file, "*" );
	    }
	    else
	    {
		( void ) fprintf( p_DBG->trace_file, " " );
	    }
	    mg_dbg_hex_dump_a_line( pdata, size );
	    mg_dbg_hex_to_asc( pdata, size );
	    ( void ) fprintf( p_DBG->trace_file, "\n" );
	    break;
	}
    }
}



/*FUNCTION mg_dbg_hex_to_asc() - convert hexadecimal to ascii char.	*/
/*************************************************************************

FUNCTION 
	mg_dbg_hex_to_asc() - convert hexadecimal to ascii character

DESCRIPTION
	convert hexadecimal to ascii character for one dump line

INPUTS
	Arg:	pdata - pointer to data
		size  - size of data ( <= 16 bytes )

        Others: None

OUTPUTS
	Return:  

	Arg:    None

	Others: None

************************************************************************/
/************************************************************************/
void
mg_dbg_hex_to_asc( U8 * pdata, int size )
{
    int             i;
    int             remain;


    if ( !p_DBG )
	return;
    mg_dbg_check_trace_file_changed(  );

    if ( size > 16 )
    {
	size = 16;
    }
    ( void ) fprintf( p_DBG->trace_file, " " );
    remain = 16 - size;
    for ( i = 0; i < remain; i++ )
    {
	( void ) fprintf( p_DBG->trace_file, "   " );
    }
    for ( i = 0; i < size; i++ )
    {
	if ( *pdata < 0x20 || *pdata >= 0x7f )
	{
	    ( void ) fprintf( p_DBG->trace_file, "." );
	}
	else
	{
	    ( void ) fprintf( p_DBG->trace_file, "%c", *pdata );
	}
	pdata++;
    }
}




/*FUNCTION mg_dbg_hex_all_zeros() - check if next n bytes are zeros	*/
/*************************************************************************

FUNCTION 
	mg_dbg_hex_all_zeros() - check if next n bytes are all zeros

DESCRIPTION
	check if next dump line are all zeros

INPUTS
	Arg:	pdata - pointer to data
		size - number of bytes to check
	
        Others: None

OUTPUTS
	Return:  

	Arg:    TRUE - if all n bytes are zero
		FALSE - oterhwise

	Others: None

************************************************************************/
/************************************************************************/
int
mg_dbg_hex_all_zeros( U8 * pdata, int size )
{
    int             i;


    for ( i = 0; i < size; i++ )
    {
	if ( *pdata++ != 0 )
	{
	    return ( FALSE );
	}
    }
    return ( TRUE );
}





/* FUNCTION mg_dbg_copym() - copy string until null or maximum reached	*/
/*************************************************************************
								
FUNCTION
	int mg_dbg_copym(char *s1, char *s2, U16 size)

DESCRIPTION
	copy a string until NULL (included) or size is reached. A NULL
	is always padded at the end of the destination buffer. If the
	source string is longer than the size, only (size-1) characters are
	copied.

INPUTS
	Arg:	s1 - destination buffer
		s2 - source string
		size - maximum size to copy

	Others:	None

OUTPUTS
	Return:	actual size copied

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
int
mg_dbg_copym( char *s1, char *s2, U16 size )
{
    int             i;


    for ( i = 0; i < ( int ) size; i++ )
    {
	*s1 = *s2;		/* copy a single character */
	if ( *s2 == 0 )
	{
	    return ( i );
	}
	s1++;
	s2++;
    }
    s1--;
    *s1 = 0;
    return ( i - 1 );
}




/* FUNCTION mg_dbg_cmp() - compare two strings (case ignored)		*/
/*************************************************************************
								
FUNCTION
	int mg_dbg_cmp(char *s1, char *s2, U16 size)

DESCRIPTION

INPUTS
	Arg:	s1 : first string
	        s2 : second string
	        size: max. size to compare

	Others:	None

OUTPUTS
	Return:
		TRUE - if two strings are the same (including size)
		FALSE - if otherwise

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
int
mg_dbg_cmp( char *s1, char *s2, U16 size )
{
    U16             i;



    for ( i = 0; i < size; i++ )
    {
	if ( *s1 == 0 && *s2 == 0 )
	{
	    return ( TRUE );
	}
	if ( toupper( *s1 ) != toupper( *s2 ) )
	{
	    return ( FALSE );
	}
	s1++;
	s2++;
    }
    if ( *s1 != 0 || *s2 != 0 )
    {
	return ( FALSE );	/* not properly terminated      */
    }
    return ( TRUE );
}


/* FUNCTION mg_dbg_cmp_ignore_case() - compare two strings (case ignored)	*/
/*************************************************************************
								
FUNCTION
	int mg_dbg_cmp_ignore_case(char *s1, char *s2, U16 size)

DESCRIPTION

INPUTS
	Arg:	s1 - first string
		s2 - second string
		size - max. size to compare

	Others:	None

OUTPUTS
	Return:	TRUE - if two strings are the same (including size)
		FALSE - if otherwise

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
int
mg_dbg_cmp_ignore_case( char *s1, char *s2, U16 size )
{
    U16             i;



    for ( i = 0; i < size; i++ )
    {
	if ( *s1 == 0 && *s2 == 0 )
	{
	    return ( TRUE );
	}
	if ( toupper( *s1 ) != toupper( *s2 ) )
	{
	    return ( FALSE );
	}
	s1++;
	s2++;
    }
    if ( *s1 != 0 || *s2 != 0 )
    {
	return ( FALSE );	/* not properly terminated      */
    }
    return ( TRUE );
}



/* FUNCTION mg_dbg_gethex() - get hexadecimal value from character string	*/
/*************************************************************************
								
FUNCTION
	long mg_dbg_gethex(char *pc)

DESCRIPTION
	converts hexadecimal character string to binary value.

INPUTS
	Arg:	pc - pointer to hexadecimal character string

	Others:	None

OUTPUTS
	Return:	binary value of the hexadecimal character string

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
long
mg_dbg_gethex( char *pc )
{
    long            result;
    char            digit;


    result = 0;
    for ( ;; )
    {
	digit = *pc++;
	if ( digit >= '0' && digit <= '9' )
	{
	    result = ( result << 4 ) + ( digit - '0' );
	}
	else if ( digit >= 'A' && digit <= 'F' )
	{
	    result = ( result << 4 ) + ( digit - 'A' + 10 );
	}
	else if ( digit >= 'a' && digit <= 'f' )
	{
	    result = ( result << 4 ) + ( digit - 'a' + 10 );
	}
	else
	{
	    break;
	}
    }
    return ( result );
}



/* FUNCTION mg_dbg_getdec() - get decimal value from character string	*/
/*************************************************************************
								
FUNCTION
	U32 mg_dbg_getdec(char *pc)

DESCRIPTION
	converts decimal character string to binary value.

INPUTS
	Arg:	pc - pointer to decimal character string

	Others:	None

OUTPUTS
	Return:	binary value of the decimal character string

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
U32
mg_dbg_getdec( char *pc )
{
    U32             result;	/* to hold decimal value */
    char            digit;


    result = 0;
    for ( ;; )
    {
	if ( *pc == 0 )
	{
	    break;
	}
	digit = *pc++;
	if ( digit >= '0' && digit <= '9' )
	{
	    result = result * 10 + ( digit - '0' );
	}
	else
	{
	    break;
	}
    }
    return ( result );
}


/* FUNCTION mg_dbg_all_digit()- check if string characters are all digit	*/
/*************************************************************************
								
FUNCTION
	int mg_dbg_all_digit(char *pc, U16 size)

DESCRIPTION
	Check if a character string characters are all digits

INPUTS
	Arg:	pc - pointer to character string
		size - size of string

	Others:	None

OUTPUTS
	Return:	TRUE - if all characters in string are digits
		FALSE - otherwise

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
int
mg_dbg_all_digit( char *pc, U16 size )
{
    U16             i;

    if ( size == 0 )
    {
	return ( FALSE );
    }
    for ( i = 0; i < size; i++ )
    {
	if ( pc[i] == 0 )
	{
	    return ( TRUE );
	}
	if ( pc[i] < '0' || pc[i] > '9' )
	{
	    return ( FALSE );
	}
    }
    return ( TRUE );
}


/*SUBTITLE mg_dbg_get_dpc()- get point code (ANSI) from command string	*/
/*************************************************************************
**                                                                      **
** Function:	mg_dbg_get_dpc(dbg_field_t *fld, char *pcmd, 		**
**					      char *pdelimiter)		**
**                                                                      **
** Input:	pcmd - command text pointer (at "nn-cc-mm")		**
**		pdelimiter - pointer to delimiter char. string		**
**									**
** Output:      pc (-1 if invalid PC)					**
**		dbg_field_t: len = point code string length including	**
**					the delimiting character	**
**		e.g., "10-100-200,", len = 11				**
**                                                                      **
** Description:								**
**                                                                      **
*************************************************************************/
int
mg_dbg_get_dpc( dbg_field_t * fld, char *pcmd, char *pdelimiter )
{
    int             dpc;
    U8              network;
    U8              cluster;
    U8              member;
    char           *pc;


    pc = pcmd;
    pc += mg_dbg_get_token( fld, pc, "-" );
    if ( fld->delimiter != '-' )
    {
	return ( -1 );
    }
    network = ( U8 ) mg_dbg_getdec( fld->data );
    pc += mg_dbg_get_token( fld, pc, "-" );
    if ( fld->delimiter != '-' )
    {
	return ( -1 );
    }
    cluster = ( U8 ) mg_dbg_getdec( fld->data );
    pc += mg_dbg_get_token( fld, pc, pdelimiter );
    member = ( U8 ) mg_dbg_getdec( fld->data );

    dpc = ( network << 16 ) + ( cluster << 8 ) + member;
    fld->len = pc - pcmd;
    return ( dpc );
}



/* FUNCTION mg_dbg_disp_pc() - display point code (ANSI)			*/
/*************************************************************************
								
FUNCTION
	void mg_dbg_disp_pc(U32 pc)

DESCRIPTION
	Display ANSI point code

INPUTS
	Arg:	pc - point code

	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
void
mg_dbg_disp_pc( U32 pc )
{


    ( void ) fprintf( stderr, "%d-%d-%d", ( pc & 0xff0000 ) >> 16, ( pc & 0xff00 ) >> 8, pc & 0xff );

}


/* FUNCTION mg_DbgLevelEqual() - 			                */
/*************************************************************************
								
FUNCTION
	void mg_DbgLevelEqual(U32 mask)

DESCRIPTION
	Test if the current debug level is greater that the given level

INPUTS
	Arg:	debug mask

	Others:	None

OUTPUTS
	Return:	None

	Arg:	None

FEND
*************************************************************************/
/************************************************************************/
int
mg_DbgLevelEqual( U32 mask )
{
    return ( p_DBG && ( mask & p_DBG->dbgMask ) );
}
