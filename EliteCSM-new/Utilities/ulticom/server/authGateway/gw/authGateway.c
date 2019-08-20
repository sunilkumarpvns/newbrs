/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005-2006 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source   authGateway.c

@ClearCase-version: $Revision:/main/sw9/22 $ 

@date     $Date:2-Feb-2009 11:34:37 $

@product  Signalware

@subsystem  WLAN Gateway

DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE DEVELOPMENT OF A WORKING SOFTWARE PROGRAM.  THE SOURCE CODE PROVIDED IS NOT WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.
 
 ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER  INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and interest in and to the Software, and all intellectual property rights associated therewith, including without limitation, rights to copyrights, trade secrets or know-how. This disclaimer agreement shall be governed and construed in accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time without notice. Ulticom has no continuing obligation to provide additional source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples; these changes may be reported, for the sample code included herein, in new editions of the product.

******************************************************************************/


/********************************
*	UNIX system files	*
********************************/
#include <apiinc.h>
#include <stdarg.h>
#include <time.h>
#include <sys/socket.h>
#include <netdb.h>
#include <poll.h>
#include <netinet/in.h>
#include <math.h>

/********************************
*      SCTP INCLUDES           *
#include <sctp.h>
********************************/

/********************************
*      SIGNALWARE INCLUDES      *
********************************/

#include <tapsc.h>		/* contains struct. definitions for tap */
#include <tcap.h>		/* contains struct. definitions for tcap */

/********************************
*   Map Gateway common files    *
********************************/
#include <ulcm_hash.h>
#include <authDataStruct.h>
#include <gwVersion.h>
#include <tcp_message.h>


/*
 * Constant:  	DEFAULT_NODENAME
 *  Purpose:  	default node name
 *     Note:    
 */
#define DEFAULT_NODENAME	"XX"

/*
 * Constant:  	DEFAULT_HOSTNAME
 *  Purpose:  	default host name
 *     Note:    
 */
#define DEFAULT_HOSTNAME	"myHostname"

/*
 * Constant:  	DEFAULT_PROTOCOL
 *  Purpose:  	default protocol
 *     Note:    
 */
#define DEFAULT_PROTOCOL	itu7

/*
 * Constant:  	DEFAULT_INVOKE_TO
 *  Purpose:  	default invoke timeout in seconds
 *     Note:    
 */
#define DEFAULT_INVOKE_TO	30

/*
 * Constant:  	DEFAULT_INVOKE_RETRY
 *  Purpose:  	default invoke retries
 *     Note:    
 */
#define DEFAULT_INVOKE_RETRY	1

/*
 * Constant:  	DEFAULT_MAX_REQUESTS
 *  Purpose:  	default maximum requests to track
 *     Note:    
 */
#define DEFAULT_MAX_REQUESTS	1000

/*
 * Constant:  	DEFAULT_LOCAL_NP
 *  Purpose:  	default local numbering plan
 *     Note:    
 */
#define DEFAULT_LOCAL_NP	e164

/*
 * Constant:  	DEFAULT_REMOTE_NP
 *  Purpose:  	default remote numbering plan
 *     Note:    
 */
#define DEFAULT_REMOTE_NP	e212

/*
 * Constant:  	DEFAULT_LOCAL_GTI
 *  Purpose:  	default local global title indicator
 *     Note:    
 */
#define DEFAULT_LOCAL_GTI	GT_0100

/*
 * Constant:  	DEFAULT_LOCAL_TT
 *  Purpose:  	default local translation type
 *     Note:    
 */
#define DEFAULT_LOCAL_TT	0

/*
 * Constant:  	DEFAULT_LOCAL_PC
 *  Purpose:  	default local point code
 *     Note:    
 */
#define DEFAULT_LOCAL_PC	0

/*
 * Constant:  	DEFAULT_LOCAL_SSN
 *  Purpose:  	default local subsystem number
 *     Note:    
 */
#define DEFAULT_LOCAL_SSN	SSN_VLR

/*
 * Constant:  	DEFAULT_LOCAL_MSISDN
 *  Purpose:  	default local subsystem number
 *     Note:    
 */
#define DEFAULT_LOCAL_MSISDN	"0"

/*
 * Constant:  	DEFAULT_APP_CONTEXT
 *  Purpose:  	default application context
 *     Note:    
 */
#define DEFAULT_APP_CONTEXT	2


/*
 * Define:  	MYNAME
 *  Purpose:  	My process' logical name;  
 *     Note:    it does not exist before 'FtRegisterEx()
 */
#define MYNAME pSHM->cpt[OwnClientTableEntry].ClientLogicalName

#define LOG_LEVEL_NONE 0
#define LOG_LEVEL_ERROR 1 
#define LOG_LEVEL_WARN 2
#define LOG_LEVEL_INFO 3
#define LOG_LEVEL_DEBUG 4
#define LOG_LEVEL_TRACE 5
#define LOG_LEVEL_ALL 6
#define DATETIMESIZE    64

#define ASYNC_POOLING_ITR 100

int logLevel = LOG_LEVEL_ALL;
#define LOG_MSG(msgLevel, format, logMsg...) if (logLevel >= msgLevel) {char outstr[DATETIMESIZE]; datetimestamp(outstr);\
 fprintf(stdout, "%s [%s:%d] [%u] ", outstr, __FILE__, __LINE__,  (unsigned int)pthread_self()); fprintf(stdout, format, ## logMsg ); }
  
  
/*
 * Global:  	world
 *  Purpose:  	buffer for WLAN global data
 */
/* declare the variables used to encode and decode ASN.1 */

WlanGlobal       world;

/*
 * Global:  	wlanBuf1
 *  Purpose:  	buffer for WLAN data
 */
WlanBuf          wlanBuf1;

/*
 * Global:  	wlanBuf2
 *  Purpose:  	buffer for WLAN data
 */
WlanBuf          wlanBuf2;

/*
 * Global:  	encodedBuffer
 *  Purpose:  	wlan encode buffer
 */
unsigned char   encodedBuffer[MAX_TCP_MESSAGE_SIZE];


/*
 * Define:  	NO_CLEAN
 *  Purpose:  	don't clean
 */
/* Boolean to trigger if sendResponse() needs to clean the hash table */
#define NO_CLEAN 	FALSE

/*
 * Define:  	CLEAN
 *  Purpose:  	do clean
 */
#define CLEAN 		TRUE



char supportedCamelPhases[4];
int msisdnlength = 14;


/*
 * typedef:  	mySharedMemory_t
 *  Purpose:  	shared memory structure
 */
typedef struct mySharedMemory_s
{
    FtSharedMem_t global;
    FtSharedMem_t   myTable;
    FtSharedMem_t   dialogId_buckets;
    FtSharedMem_t   dialogId_Hash;
    FtSharedMem_t   requestId_buckets;
    FtSharedMem_t   requestId_Hash;
} mySharedMemory_t;

/*
 * enum:  TerminateCause_t
 *  Purpose:  enumerate values for termination causes
 */
enum TerminateCause_t
{
    TERMINATE_CAUSE_SUCCESS=0,
    TERMINATE_CAUSE_SOCKET,
    TERMINATE_CAUSE_BIND,
    TERMINATE_CAUSE_LISTEN,
    TERMINATE_CAUSE_FTPUTSTATE,
    TERMINATE_CAUSE_PUTGBLOCK,
    TERMINATE_CAUSE_ASYNCBUFFER,
    TERMINATE_CAUSE_FTGETIPCASYNC,
    TERMINATE_CAUSE_FTCANCELASYNC,
    TERMINATE_CAUSE_FTGETDESCRIPTOR,
    TERMINATE_CAUSE_ACCEPT,
    TERMINATE_CAUSE_SHMCORRUPT
};

/*
 * Global:  	gCTBLOCK
 *  Purpose:  	global for sending a ctblock to TCAP
 */
ctblock_t gCTBLOCK;

/*
 * Global:  	sm
 *  Purpose:  	global for shared memory information
 */
mySharedMemory_t sm;		/* Track all of our shared memory segments */

/* Hash table variables */

/*
 * Global:  	PdialogId_buckets
 *  Purpose:  	pointer to hash table bucket
 */
bucket_t       *PdialogId_buckets;

/*
 * Global:  dialogId_hash
 *  Purpose:  	holds hash entry
 */
hash_info_t     dialogId_hash;

/*
 * Global:  	PdialogId_Hash
 *  Purpose:  	hash table of dialogs
 */
U16            *PdialogId_Hash;


/*
 * Global:  	PrequestId_buckets
 *  Purpose:  	requested bucket
 */
bucket_t       *PrequestId_buckets;

/*
 * Global:  	requestId_hash
 *  Purpose:  	requested id entry
 */
hash_info_t     requestId_hash;

/*
 * Global:  	PrequestId_Hash
 *  Purpose:  	hash table of requests
 */
U16            *PrequestId_Hash;


/*
 * Global:  	PmyTable
 *  Purpose:  	Table of all the pending requests
 */
pendingReq_t   *PmyTable;

/*
 * Global:  	Pglobal
 *  Purpose:  	Our primary shared memory segment; most of our
 *		global variables are stored here
 */
gmaplib_t      *Pglobal;


/*
 * Global:  	Terminate
 *  Purpose:  	should we exit?
 */
BOOL            Terminate = FALSE;

/*
 * Global:  	finished_initialization
 *  Purpose:  	Are we in the main loop yet?
 */
BOOL            finished_initialization = FALSE;


/* CR 27676: add globals and constants to support connection timeout */

/*
 * Constant:  	DEFAULT_CONNECT_TIMEOUT
 *  Purpose:  	default initialization retry count
 *     Note:    default is no connection timeout
 */
#define DEFAULT_CONNECT_TIMEOUT	0

/*
 * Global:  	_connection_timeout
 *  Purpose:  	How long an accepted connection is allowed to be inactive
 *		before closing and returning to listen port.
 */
int             _connection_timeout = DEFAULT_CONNECT_TIMEOUT;

/*
 * Constant:  	DEFAULT_CONNECT_RETRY
 *  Purpose:  	default initialization retry count
 *     Note:    
 */
#define DEFAULT_CONNECT_RETRY	10

/*
 * Global:  	_connection_retry
 *  Purpose:  	How many times to try and bind before terminating.
 */
int             _connection_retry = DEFAULT_CONNECT_RETRY;

/*
 * Constant:  	CONNECTION_RETRY_SLEEP_TIME
 *  Purpose:  	connection bind retry sleep time (seconds)
 *     Note:    because sleep will be interupted by signals, will be less
 */
#define CONNECTION_RETRY_SLEEP_TIME	1

/*
 * Global:  	_connection_last_activity
 *  Purpose:  	Keep track of time since last activity on socket connection.
 */
time_t          _connection_last_activity = (time_t) 0;

/*
 * Global:      segment_flag	
 *  Purpose:  	This flag indicates whether the segmentatioProhibited flag 
 *		inside SendAuthenticationInfoArg should be set or not. 
 */

int             segment_flag = TRUE;	/* HLR should not segment data    */

/*
 *  Global:      g_trace_enabled	
 *  Purpose:  	 Global tracing flag
 */

int		g_trace_enabled = FALSE;


/* Some function prototypes */
void            sendResponseOnSocket( pendingReq_t * prq, int clean );
void            processInsertSubscriberData( gblock_t *, pendingReq_t * );
void            insertSubscriberDataResp( gblock_t *, pendingReq_t * );
int             compareDigits( unsigned char * );
int             initializeSCCPaddresses( gblock_t *, pendingReq_t * );
void            setErrorCode( pendingReq_t * prq, int errorCode );
int             populateTripletsGblock( gblock_t * pgb, pendingReq_t * prq );
int             populateImsiGblock( gblock_t * pgb, pendingReq_t * prq );
int             populateUpdateGprsGblock( gblock_t * pgb, pendingReq_t * prq );
int             populateRestoreDataGblock( gblock_t * pgb, pendingReq_t * prq );
void            generateServiceString(gblock_t *pgb,pendingReq_t *prq);
void            gSendUReject(gblock_t *pgb,U8 problemType, U8 problemCode);
int             sendAnotherServiceMsg( gblock_t *pgb, pendingReq_t * prq );
void			endDialog(gblock_t* pgb, pendingReq_t *prq, int clean);
void			processInsertSubscriberDataGprs( gblock_t *, pendingReq_t * );


/*
 * Global:  	shm_name
 *  Purpose:  	shared memory segment name
 */
char            shm_name[MAXfileNAME];

/*
 * Define:  	CREATE_OR_ATTACH_SHM
 *  Purpose:  	A very large macro for creating or attaching to our (multiple) shared
 *  memory segments.  It's used in init_shared_memory() and main().
 */
#define CREATE_OR_ATTACH_SHM(PROCESS_NAME, NAME, TYPE, SIZE, PNAME) \
     snprintf(shm_name, sizeof(shm_name), "%s_%s.%s",\
	      PROCESS_NAME, #NAME, getenv("SHM"));\
\
     if(restart)\
	P##NAME = (TYPE *)FtAttachSharedMemory(PNAME, shm_name,\
					       FtShared_READ|FtShared_WRITE);\
     else \
	P##NAME = (TYPE *)FtCreateSharedMemory(PNAME, shm_name, SIZE);\
\
     if (!P##NAME)\
     {\
	DebugIndicator = TRUE;\
	FtErrorDisplay("Failed to create/attach shared memory segment");\
	fprintf(stderr, "Unable to %s to shared memory segment \"%s\"!\n",\
		restart ? "attach" : "create", shm_name);\
	exit(1);\
     }

/*
 * Define:  	MAX_TOKENS
 *  Purpose:  	specifies the number of name-value pairs in the conf file
 */
#define MAX_TOKENS ((2 *MAX_BS_CODES) + (2 * MAX_TS_CODES) + (2 * 9))



/************************************************************************

FUNCTION
        void    gSendUReject(Dialog *pdialog, S16 invokeId,
                                U8 problemType, U8 problemCode)

DESCRIPTION
        This function sets up the U_REJECT tblock and send to TCAP
	This function directly uses the TCAP instead of going thru
	the GSM library

INPUTS
        pdialog - pointer to dialog table entry
        invokeId        - invoke id
        problemType - problem type value
        problemCode - problem code value
        others:
OUTPUTS
        Return: None

FEND
*************************************************************************/
void    gSendUReject(gblock_t *gblk, U8 problemType, U8 problemCode)
{
        ctblock_t       *ptb;
	int		ret_code;

        ptb = &gCTBLOCK;
        (void)memset((char *)ptb, 0, sizeof(ctblock_t));
        ptb->dialog_id = gblk->dialogId;
        ptb->appl_id = gblk->applicationId;

        ptb->tcu.chp.invoke_id = gblk->invokeId;
        ptb->tcu.chp.problem_type = problemType;
        ptb->tcu.chp.problem_code = problemCode;

        ptb->prim_code = TC_U_REJECT;
	ret_code = cTCAPPutTblock(ptb);
        if(ret_code == RETURNerror)
        {
		DBG_DISP( DBG_MASK_ERR, 27, 
		"cTCAPutTblock(): errno: %d, discarding the message.\n", 
		ctcap_errno );
        }

}

/*************************************************************************

FUNCTION
    unsigned char compareObjectIDs(ObjectID* id1, ObjectID* id2)

DESCRIPTION
        Compare two objects Ids

INPUTS
        Arg:    id1, id2 : object IDs to compare

OUTPUTS
        Return: 0 if IDs are different, non 0 else

FEND
*************************************************************************/
unsigned char
compareObjectIDs( ObjectID * id1, ObjectID * id2 )
{
    if ( id1->count != id2->count )
	return 0;

    return ( memcmp( id1->value, id2->value, id1->count * sizeof( int ) ) == 0 );
}

/* FUNCTION compareDigitsIndex() */
/*************************************************************************

FUNCTION
    int compareDigitsIndex(unsigned char*, int)

DESCRIPTION
        This function  compares the digits passed as arguments with the
      odigit parameter of the line number 'index' of the authGateway
      configuration file.

INPUTS
        Arg:    dig - digitsto compare with the odigits from the file.
                index - identifier of the line in the configuration file

OUTPUTS
        Return:  1 if the digits are identical
                -1 if the digits are different

FEND
*************************************************************************/
int
compareDigitsIndex( unsigned char *dig, int index )
{
    int             i = 0, j = 0;
    unsigned char   digit_i;
    int             len, len1;

    if ( Pglobal->dbg.trace_enabled )
    {
        wlanPrint2(&world, "Line %d: Old Digits ... %s\n", index, Pglobal->sccpAddrArray[index].old_digits );
    }

    if ( strlen( Pglobal->sccpAddrArray[index].old_digits ) == 0 )
	return -1;		/* No old digits */

	if (strstr(Pglobal->sccpAddrArray[index].old_digits, "+") != NULL) {
		len1 = strcspn( Pglobal->sccpAddrArray[index].old_digits, "+" );
	}
	else 
		len1 = strcspn( Pglobal->sccpAddrArray[index].old_digits, "*" );

    if ( len1 == 0 )
	return 1;		/* old digits = *  , everything matches   */

    if ( len1 % 2 != 0 )
	len = len1 - 1;
    else
	len = len1;

    for ( i = 0, j = 0; i < len; i += 2, j++ )
    {
	digit_i = ( Pglobal->sccpAddrArray[index].old_digits[i] - '0' );

	if ( Pglobal->dbg.trace_enabled )
	{
	    wlanPrint(&world, "odigit_i is 0x%x\n", digit_i );
	}

	digit_i |= ( Pglobal->sccpAddrArray[index].old_digits[i + 1] - '0' ) << 4;

	if ( Pglobal->dbg.trace_enabled )
	{
	    wlanPrint(&world, "odigit_i is 0x%x\n", digit_i );
	    wlanPrint(&world, "IMSIdig[j] is %d\n", dig[j] );
	}

	if ( digit_i != dig[j] )
	    return -1;
    }

    if ( len1 % 2 != 0 )
    {
	digit_i = ( Pglobal->sccpAddrArray[index].old_digits[len1 - 1] ) - '0';

	if ( Pglobal->dbg.trace_enabled )
	{
	    wlanPrint(&world, "odigit_i is 0x%x\n", digit_i );
	    wlanPrint(&world, "Last IMSI digit 0x%x\n ", ( dig[( len + 1 ) / 2] & 0x0f ) );
	}

	if ( ( dig[( len + 1 ) / 2] & 0x0f ) != digit_i )
	    return -1;
    }

    return 1;
}

/* FUNCTION wlan_strlcpy() */
/*************************************************************************

FUNCTION
	size_t wlan_strlcpy(char *dst, const char *src0, size_t size)

DESCRIPTION
	The strlcpy() function copies  at most size-1  characters
     	(size being the  size of the  string buffer dst) from src
     	to dst,  truncating src if necessary.  The  result is always
     	null-terminated.  The  function  returns strlen(src). Buffer
     	overflow can be checked as  follows:

INPUTS
        Arg:    dst	: pointer to destination array
		src0	: pointer to source array
		size 	: size of destination array

OUTPUTS
        Return:  0 	- if dst = src0
		 <0	- if dst < src0
		 >0	- if dst > src0

FEND
*************************************************************************/
size_t
wlan_strlcpy(char *dst, const char *src0, size_t size)
{
	const char *src;

	for (src = src0; *src != '\0' && size > 1; size--)
		*dst++ = *src++;

	if (size >= 1)
		*dst = '\0';

	while (*src++ != '\0');

	return src - src0;
}

/* FUNCTION compareMsisdnIndex() 					*/
/*************************************************************************

FUNCTION
	int compareMsisdnIndex( unsigned char *dig, int index )

DESCRIPTION
	Compare MSISDN  index.
INPUTS
        Arg:    dig	: pointer to MSISDN value
		index	: index in global data

OUTPUTS
        Return:  -1 	: No msisdn
		  1	: everything matches

FEND
*************************************************************************/

int
compareMsisdnIndex( unsigned char *dig, int index )
{
    int             i = 0, j = 0;
    unsigned char   digit_i;
    int             len, len1;


    if ( Pglobal->dbg.trace_enabled )
    {
	wlanPrint2(&world, "Line %d: MSISDN ... %s\n", index, Pglobal->sccpAddrArray[index].msisdn );
    }

    if ( strlen( Pglobal->sccpAddrArray[index].msisdn ) == 0 )
	return -1;		/* No msisdn   */

    len1 = strcspn( Pglobal->sccpAddrArray[index].msisdn, "*" );

    if ( len1 == 0 )
	return 1;		/* msisdn is *   , everything matches  */

    if ( len1 % 2 != 0 )
	len = len1 - 1;
    else
	len = len1;

    for ( i = 0, j = 0; i < len; i += 2, j++ )
    {
	digit_i = ( Pglobal->sccpAddrArray[index].msisdn[i] - '0' );

	if ( Pglobal->dbg.trace_enabled )
	{
	    wlanPrint(&world, "msisdn_i is 0x%x\n", digit_i );
	}

	digit_i |= ( Pglobal->sccpAddrArray[index].msisdn[i + 1] - '0' ) << 4;

	if ( Pglobal->dbg.trace_enabled )
	{
	    wlanPrint(&world, "msisdn_i is 0x%x\n", digit_i );
	    wlanPrint(&world, "MSISDNdig[j] is %d\n", dig[j] );
	}

	if ( digit_i != dig[j] )
	    return -1;
    }

    if ( len1 % 2 != 0 )
    {
	digit_i = ( Pglobal->sccpAddrArray[index].msisdn[len1 - 1] ) - '0';

	if ( Pglobal->dbg.trace_enabled )
	{
	    wlanPrint(&world, "msisdn_i is 0x%x\n", digit_i );
	    wlanPrint(&world, "Last MSISDN digit 0x%x\n ", ( dig[( len + 1 ) / 2] & 0x0f ) );
	}

	if ( ( dig[( len + 1 ) / 2] & 0x0f ) != digit_i )
	    return -1;
    }

    return 1;
}

/* FUNCTION terminate() - Terminate application                        */
/*************************************************************************

FUNCTION
        void    terminate(int errorCode)

DESCRIPTION
        This function is called to terminate the application.  Note
        that the gMAPTerminate() function is called so that the
        shared memory allocated by the GMAP provider will be
        released.

INPUTS
        Arg:    errorCode - exit code

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
terminate( enum TerminateCause_t errorCode )
{
    DBG_DISP( DBG_MASK_L1, 3, "Closing the sockets and terminating...\n" );

    if ( Pglobal->psock != -1 )
    {
	shutdown( Pglobal->psock, 2 );
    }
    close( Pglobal->psock );

    if ( Pglobal->sock != -1 )
    {
	shutdown( Pglobal->sock, 2 );
    }
    close( Pglobal->sock );

    gMAPTerminate(  );

    DBG_DISP( DBG_MASK_L1, 4, "Terminated.\n" );

    FtTerminate( NOrestart, errorCode );
}

/* FUNCTION getTimeStamp() */
/*************************************************************************

FUNCTION
       int  getTimeStamp()

DESCRIPTION
        This function returns a timestamp

INPUTS
        Arg: none

OUTPUTS
        Return: number of seconds since Jan. 1, 1970

FEND
*************************************************************************/
int
getTimeStamp(  )
{
    struct timeval  tp;

    if ( 0 == gettimeofday( &tp, NULL ) )
	return tp.tv_sec;	/* seconds since Jan. 1, 1970 */
    else
    {
	DBG_DISP( DBG_MASK_ERR, 5, "Error while getting the timestamp.\n" );
	return -1;
    }
}

/* FUNCTION cancelRequest */
/*************************************************************************

FUNCTION
       void cancelRequest(int requestId)

DESCRIPTION
        This function mark an ongoing request as "cancelled"
	authRequest or imsiRequest are cancelled the same way

INPUTS
        Arg: requestId   the requestId of the request to cancel

OUTPUTS
        Return: none

FEND
*************************************************************************/
void
cancelRequest( int requestId )
{
    int             i;

    /* Find in the table the prq with the same requestId
       and set the status to cancelled  */
    for ( i = 1; i <= Pglobal->max_requests; i++ )
    {
	if ( PmyTable[i].requestId == requestId )
	{
	    PmyTable[i].status = canceled;
	    DBG_DISP( DBG_MASK_L2, 5, "Canceled request %d .\n", requestId );
	    return;
	}
    }

    DBG_DISP( DBG_MASK_ERR, 6, "Could not cancel the request. Request Id %d not found.\n", requestId );
}

/* FUNCTION sigintCatcher() - SIGINT signal handler            */
/*************************************************************************

FUNCTION
    void    sigintCatcher(void)

DESCRIPTION
        This function is the SIGINT handler.

INPUTS
        Arg:    None

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
sigintCatcher( void )
{
    if ( finished_initialization )
	/*  Do NOT send or receive IPC messages in signal handlers;
	 *  The Ft library IPC functions are NOT Async-Safe.  Instead,
	 *  we set a variable which will be checked in the main
	 *  process loop.
	 */
	Terminate = TRUE;
    else
	exit( 0 );		/* not in main loop yet */
}


/* FUNCTION processNState() - process N_STATE indication        */
/*************************************************************************

FUNCTION
        void    processNState(Header_t *ph)

DESCRIPTION
        This function is called to process the received N_STATE indication.
        The N_STATE indication is generated by the SCMG (SCCP management
    process) to indicate that if the remote SSN is in service or
    out of service.

INPUTS
        Arg:    ph - received N_STATE IPC message

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
processNState( Header_t * ph )
{
    scmg_nstate_t  *pnstate;
    char           *ptext=NULL;

    pnstate = &( ( iblock_t * ) ph )->primitives.nstate;

    if ( pnstate->NS_user_status == SCMG_UIS )
    {
	ptext = "UIS";
    }
    else if ( pnstate->NS_user_status == SCMG_UOS )
    {
	ptext = "UOS";
    }

    if ( Pglobal->GMAPINIT.protocol == ansi7 || Pglobal->GMAPINIT.protocol == chinese7 )
    {
	DBG_DISP( DBG_MASK_L2, 7,
		  "   N_STATE IND. PC=%d-%d-%d, SSN=%d, status=%s\n",
		  pnstate->NS_affect_pc >> 16, ( pnstate->NS_affect_pc & 0xff00 ) >> 8, 
		  pnstate->NS_affect_pc & 0xff, pnstate->NS_affect_ssn, ptext );

    }
    else
    {
	DBG_DISP( DBG_MASK_L2, 8, "   N_STATE IND. PC=%d, SSN=%d, status=%s\n", 
		pnstate->NS_affect_pc, pnstate->NS_affect_ssn, ptext );

    }
}


/* FUNCTION processPcState() - process PC_STATE indication        */
/*************************************************************************

FUNCTION
        void    processPcState(Header_t *ph)

DESCRIPTION
        This function is called to process the received PC_STATE indication.
        The PC_STATE indication is generated by the SCMG (SCCP management
    process) to indicate that if the remote point code is accessible
    or inaccessible.

INPUTS
        Arg:    ph - received PC_STATE IPC message.

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
processPcState( Header_t * ph )
{
    scmg_pcstate_t *ppcstate;
    char           *ptext=NULL;

    ppcstate = &( ( iblock_t * ) ph )->primitives.pcstate;

    if ( ppcstate->pc_status == SCMG_INACCESSABLE )
    {
	ptext = "INACCESSIBLE";
    }
    else if ( ppcstate->pc_status == SCMG_ACCESSABLE )
    {
	ptext = "ACCESSIBLE";
    }

    if ( Pglobal->GMAPINIT.protocol == ansi7 || Pglobal->GMAPINIT.protocol == chinese7 )
    {
	DBG_DISP( DBG_MASK_L2, 9,
		  "   PC_STATE IND. PC=%d-%d-%d, status=%s\n", ppcstate->pc_pc >> 16, 
		  ( ppcstate->pc_pc & 0xff00 ) >> 8, ppcstate->pc_pc & 0xff, ptext );

    }
    else
    {
	DBG_DISP( DBG_MASK_L2, 10, "   PC_STATE IND. PC=%d, status=%s\n", ppcstate->pc_pc, ptext );

    }
}

/* FUNCTION setup_socket() - Set up a listening socket */
/*************************************************************************

FUNCTION
    int setup_socket(int port)

DESCRIPTION
    Set up a listening socket. Do not wait for a connection.
    This function returns the file descriptor of the
    listen socket.  (all failure cases cause an exit).

INPUTS
    Arg: int port - port number

OUTPUTS
    Return:  the file descriptor of the accepted connection.

FEND
*************************************************************************/
int
setup_socket( int port, char *host )
{
    struct  sockaddr_in	sin;            /* sockaddr work area   */
    int counter;
    int optVal = 1;

    /****************************************
     * Set up the socket for the Pitcher    *
     ****************************************/
    sin.sin_port = htons((u_short) port);
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = INADDR_ANY;

    if (-1 == (Pglobal->psock = socket(PF_INET, SOCK_STREAM, 0)))
    {
	DBG_DISP( DBG_MASK_ERR, 11, "INIT_ERR: socket() failed\n" );
	terminate( TERMINATE_CAUSE_SOCKET );
    }

    if( -1 == setsockopt(Pglobal->psock, SOL_SOCKET, SO_REUSEADDR, &optVal, sizeof(optVal) ) )
    {
	DBG_DISP( DBG_MASK_ERR, 11, "INIT_ERR: setsockopt() failed, reason=(%s)\n", strerror(errno) );
	terminate( TERMINATE_CAUSE_SOCKET );
    }

    counter = 0;
    while (-1 == bind(Pglobal->psock, (struct sockaddr *)&sin, (int) sizeof(sin)))
    {
	DBG_DISP( DBG_MASK_ERR, 11, "INIT_ERR: bind() failed, errno=%d, %s\n", errno, strerror(errno) );
	counter++;
	if (counter > _connection_retry)
	{
            printf( "Failed to establish a connection to %s on port %d after %d attempts.\n", host, port, _connection_retry);
	    terminate( TERMINATE_CAUSE_BIND );
	}
	else
	{
            /* Because this process is now receiving signals, the sleep */
            /* time will be less than 1 second.  This guarentees that it */
            /* sleeps at least a little. */
            sleep( CONNECTION_RETRY_SLEEP_TIME );
	}
    }

    if (-1 == listen(Pglobal->psock, 1))
    {
	DBG_DISP( DBG_MASK_ERR, 14, "INIT_ERR: listen() failed, errno=%d\n", errno );
	terminate( TERMINATE_CAUSE_LISTEN );
    }

    /* CR 27676: set SO_KEEPALIVE for periodic heartbeat messages */
    /* to attempt to better track if connection gets hung. */
    int so_optval = 1;
    if (setsockopt(Pglobal->psock, SOL_SOCKET, SO_KEEPALIVE, (char *)&so_optval, sizeof(so_optval)) < 0)
    {
    	DBG_DISP( DBG_MASK_ERR, 595, 
    	    "Unable to set socket SO_KEEPALIVE, errno=%d\n", errno );
    }

    if ( FtPutStateEx( ( S16 ) 1, State_Green, "Socket ready", 10, FALSE ) == RETURNerror )
    {
	DBG_DISP( DBG_MASK_ERR, 14, "INIT_ERR: FtPutStateEx() failed, errno=%d\n", errno );
	terminate( TERMINATE_CAUSE_FTPUTSTATE );
    }

    return(Pglobal->psock);

}


/* FUNCTION convertPC */
/*************************************************************************

FUNCTION
       U32 convertPC(char* pcString)

DESCRIPTION
        This function converts a point code from a string to a U32

INPUTS
       Args: pcString	Point code in the form of a string

OUTPUTS
        Return: converted point code

FEND
*************************************************************************/
U32
convertPC( char *pcString )
{
    int             f1, f2, f3;

    if ( !pcString )
    {
	DBG_DISP( DBG_MASK_ERR, 15, "The pc options must be followed by a valid PC\n" );
	exit( 1 );
    }

    if ( strchr( pcString, '-' ) == NULL )
    {
	return atoi( pcString );
    }
    else
    {
	if ( sscanf( pcString, "%i-%i-%i", &f1, &f2, &f3 ) == 3 )
	{
	    return ( ( f1 << 16 ) + ( f2 << 8 ) + f3 );
	}
	else
	{
	    DBG_DISP( DBG_MASK_ERR, 16, "Invalid PC %s\n", pcString );
	    exit( 1 );
	}
    }
}


/* FUNCTION sendUIS() - send user in-service to SCMG        */
/*************************************************************************

FUNCTION
        void    sendUIS(void)

DESCRIPTION
        This function is called to send the user in serivce (UIS)  IPC
    message to SCMG (SCCP management process).  The UIS message
    must be sent to the SCMG so that incoming SCCP messages for
    the local bound SSN (see SYSbind) can be received by the process.

INPUTS
        Arg:    None

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
sendUIS( void )
{

    if ( Pglobal->GMAPINIT.protocol == ansi7 )
    {
	AscUIS( Pglobal->ALIAS_NAME_INDEX, Pglobal->GMAPINIT.ssn );
    }

    if ( Pglobal->GMAPINIT.protocol == chinese7 )
    {
	CHscUIS( Pglobal->ALIAS_NAME_INDEX, Pglobal->GMAPINIT.ssn );
    }
    else
    {
	CscUIS( Pglobal->ALIAS_NAME_INDEX, Pglobal->GMAPINIT.ssn );
    }
}

/*************************************************************************

FUNCTION
   	int  imsi2ascii( unsigned char *value, int length, char *asciiStr )

DESCRIPTION
        Convert IMSI to ASCII

INPUTS
        Arg:  	value	: pointer to IMSI string
		length	: length of IMSI string
		asciiStr: pointer to ASCII string	  

OUTPUTS
        Return: 0 - Success

FEND
*************************************************************************/

int
imsi2ascii( unsigned char *value, int length, char *asciiStr )
{
    int             i, j;

    if ( length == 0 )
    {
	asciiStr = NULL;
	return 0;
    }
 
    for ( i = 0, j = 0; i < length; i++, j++ )
    {
	if(( value[i] & 0x0f ) != 0x0f )
		asciiStr[j] = 0x30 + ( value[i] & 0x0f );
	else{
	  --j;
	}
	
	if(( value[i] & 0xf0 )  != 0xf0 )
		asciiStr[++j] = 0x30 + ( ( value[i] & 0xf0 ) >> 4 );
		
    }
    
    /* Add a NULL at the end of the string              */
    if ( asciiStr[j - 1] == 0x3f )	/* even number of digits */
	asciiStr[j - 1] = 0;	/* terminate the string  */
    else
	asciiStr[j] = 0;
    return 0;
}

/* FUNCTION initializeSCCPaddresses   */
/*************************************************************************

FUNCTION
       initializeSCCPaddresses(gblock_t*, pendingReq_t*)

DESCRIPTION
        This function is called to fill the called party address
    and calling party address parts of the MAP message.

INPUTS
        Arg:     pgb - a referenceq to an externally allocated gblock_t
                 prq - a reference to the structure defining the pending request


OUTPUTS
        Return: None

FEND
*************************************************************************/
int
initializeSCCPaddresses( gblock_t * pgb, pendingReq_t * prq )
{
    int             i, j;
    char            new_digits[MAX_MSISDN_LEN + 1];
    int             new_digits_len = 0;
    int             iRet = -1;
    char 			plus_new_digits[MAX_MSISDN_LEN + 1];

    for ( i = 0; i < Pglobal->sccpAddrCount; i++ )
    {
	if ( prq->request.choice == ulcm_imsiRequest_chosen )
	    iRet = compareMsisdnIndex( prq->request.u.imsiRequest.msisdn.value, i );
	else if ( prq->request.choice == ulcm_updateGprsRequest_chosen )
		iRet = compareDigitsIndex( prq->request.u.updateGprsRequest.updateGprsLocationArg.imsi.value, i );
	else if ( prq->request.choice == ulcm_authRequest_chosen )
	    iRet = compareDigitsIndex( prq->request.u.authRequest.imsi.value, i );

		if ( iRet == 1 )
		{
			prq->confIndex = i;
			break;
		}
    }

    if ( iRet == -1 )
    {
	DBG_DISP( DBG_MASK_ERR, 1715, 
		"No entry found in Configuration file for this IMSI or this MSISDN. Check the authGateway configuration file. \n" );
	setErrorCode( prq, ulcm_unknownImsi );
	sendResponseOnSocket( prq, CLEAN );
	return -1;
    }

    /* MAP spec 0902-751 section 6.1.3 SCCP addressing */
    pgb->parameter.openArg.destinationAddress.gt.gtIndicator = 
	Pglobal->sccpAddrArray[prq->confIndex].REMOTE_GTI;
    pgb->parameter.openArg.destinationAddress.routingIndicator = 
	Pglobal->sccpAddrArray[prq->confIndex].REMOTE_RI;

    if ( Pglobal->sccpAddrArray[prq->confIndex].REMOTE_PC != 0 )
    {
	pgb->parameter.openArg.destinationAddress.bit_mask |= 
	    MAP_SccpAddr_pointCode_present;
	pgb->parameter.openArg.destinationAddress.pointCode = 
	    Pglobal->sccpAddrArray[prq->confIndex].REMOTE_PC;
    }

    pgb->parameter.openArg.destinationAddress.ssn = 
	Pglobal->sccpAddrArray[prq->confIndex].REMOTE_SSN;

    if ( Pglobal->sccpAddrArray[prq->confIndex].REMOTE_GTI != GT_0000 )
    {
	if ( Pglobal->sccpAddrArray[prq->confIndex].REMOTE_GTI == GT_0100 &&
		(Pglobal->GMAPINIT.protocol == itu7 || Pglobal->GMAPINIT.protocol == chinese7) )
	{
	    pgb->parameter.openArg.destinationAddress.bit_mask |= 
		MAP_SccpAddr_gt_present;
	    pgb->parameter.openArg.destinationAddress.gt.bit_mask |= 
		MAP_SccpAddr_gt_numberingPlan_present;
	    pgb->parameter.openArg.destinationAddress.gt.translationType = 
		Pglobal->sccpAddrArray[prq->confIndex].REMOTE_TT;
	    pgb->parameter.openArg.destinationAddress.gt.natureOfAddress = 
		Pglobal->sccpAddrArray[prq->confIndex].REMOTE_NAI;
	    pgb->parameter.openArg.destinationAddress.gt.numberingPlan = 
		Pglobal->sccpAddrArray[prq->confIndex].REMOTE_NP;
	}
	else if ( Pglobal->sccpAddrArray[prq->confIndex].REMOTE_GTI == GT_0001 && 
		Pglobal->GMAPINIT.protocol == ansi7 )
	{
	    pgb->parameter.openArg.destinationAddress.bit_mask |= 
		MAP_SccpAddr_gt_present;
	    pgb->parameter.openArg.destinationAddress.gt.bit_mask |= 
		MAP_SccpAddr_gt_numberingPlan_present;
	    pgb->parameter.openArg.destinationAddress.gt.translationType = 
		Pglobal->sccpAddrArray[prq->confIndex].REMOTE_TT;
	    pgb->parameter.openArg.destinationAddress.gt.numberingPlan = 
		Pglobal->sccpAddrArray[prq->confIndex].REMOTE_NP;
	}
	else if ( Pglobal->sccpAddrArray[prq->confIndex].REMOTE_GTI == GT_0010 && 
		Pglobal->GMAPINIT.protocol == ansi7 )
	{
	    pgb->parameter.openArg.destinationAddress.bit_mask |= MAP_SccpAddr_gt_present;
	    pgb->parameter.openArg.destinationAddress.gt.translationType = 
		Pglobal->sccpAddrArray[prq->confIndex].REMOTE_TT;
	}
	else
	{
	    DBG_DISP( DBG_MASK_ERR, 273,
		      "initializeSCCPaddresses: Invalid GTI: %d at line %d of authGateway configuration file. If protocol is ITU-T, GTI should be 4; if protocol is ANSI, GTI should be 2.\n",
		      Pglobal->sccpAddrArray[prq->confIndex].REMOTE_GTI, Pglobal->sccpAddrCount );
	}

	if ( prq->request.choice == ulcm_imsiRequest_chosen )
	{
	    new_digits_len = prq->request.u.imsiRequest.msisdn.length;
	    memcpy( new_digits, prq->request.u.imsiRequest.msisdn.value, 
		    prq->request.u.imsiRequest.msisdn.length );
	}
	else if ( ( prq->request.choice == ulcm_updateGprsRequest_chosen ) && 
		( strstr( Pglobal->sccpAddrArray[prq->confIndex].old_digits, "*" ) != NULL ) )
	{
	    int  len = strlen( Pglobal->sccpAddrArray[prq->confIndex].new_digits );

	    new_digits_len = ( len + 1 ) / 2;
	    memset( new_digits, 0xff, MAX_MSISDN_LEN + 1 );
	}
	
	else if ( ( prq->request.choice == ulcm_authRequest_chosen ) && 
		( strstr( Pglobal->sccpAddrArray[prq->confIndex].old_digits, "+" ) != NULL ) )
	{	
	    memset (plus_new_digits, '\0', MAX_MSISDN_LEN + 1);
	    char* conf_new_digits = Pglobal->sccpAddrArray[prq->confIndex].new_digits;
	    printf("Pglobal->sccpAddrArray[prq->confIndex].new_digits: %s\n", Pglobal->sccpAddrArray[prq->confIndex].new_digits);
	    printf("Pglobal->sccpAddrArray[prq->confIndex].old_digits: %s\n", Pglobal->sccpAddrArray[prq->confIndex].old_digits);
	    char* old_digits = malloc(strlen(Pglobal->sccpAddrArray[prq->confIndex].old_digits));
	    memset(old_digits, '\0', (strlen(Pglobal->sccpAddrArray[prq->confIndex].old_digits)));
	    
	    char *IMSI = malloc(32);
	    memset(IMSI, '\0', 32);
	    
	    imsi2ascii(prq->request.u.authRequest.imsi.value, prq->request.u.authRequest.imsi.length, IMSI);
	    strcat(old_digits, IMSI+ (strlen(Pglobal->sccpAddrArray[prq->confIndex].old_digits) - 1));
	    
	    strcat(plus_new_digits, conf_new_digits);
	    strcat(plus_new_digits, old_digits);
	
		if (strlen(plus_new_digits) > strlen(IMSI))
		{
			plus_new_digits[strlen(IMSI) + 1] = '\0';
		}
		
		if (strlen(plus_new_digits) % 2 != 0)
			strcat(plus_new_digits,"0");
		
		new_digits_len = ( strlen(plus_new_digits) + 1 ) / 2;
		memset( new_digits, '\0', MAX_MSISDN_LEN + 1 );
	}

	else if ( ( prq->request.choice == ulcm_updateGprsRequest_chosen ) &&
                ( strstr( Pglobal->sccpAddrArray[prq->confIndex].old_digits, "+" ) != NULL ) )
        {
        
        	memset (plus_new_digits, '\0', MAX_MSISDN_LEN + 1);
            char* conf_new_digits = Pglobal->sccpAddrArray[prq->confIndex].new_digits;
            printf("Pglobal->sccpAddrArray[prq->confIndex].new_digits: %s\n", Pglobal->sccpAddrArray[prq->confIndex].new_digits);
            printf("Pglobal->sccpAddrArray[prq->confIndex].old_digits: %s\n", Pglobal->sccpAddrArray[prq->confIndex].old_digits);
            char* old_digits = malloc(strlen(Pglobal->sccpAddrArray[prq->confIndex].old_digits));
            memset(old_digits, '\0', (strlen(Pglobal->sccpAddrArray[prq->confIndex].old_digits)));

            char *IMSI = malloc(32);
            memset(IMSI, '\0', 32);

            imsi2ascii(prq->request.u.updateGprsRequest.updateGprsLocationArg.imsi.value, prq->request.u.updateGprsRequest.updateGprsLocationArg.imsi.length, IMSI);
            strcat(old_digits, IMSI+ (strlen(Pglobal->sccpAddrArray[prq->confIndex].old_digits) - 1));

            strcat(plus_new_digits, conf_new_digits);
            strcat(plus_new_digits, old_digits);

                if (strlen(plus_new_digits) > strlen(IMSI))
                {
                        plus_new_digits[strlen(IMSI) + 1] = '\0';
                }

                if (strlen(plus_new_digits) % 2 != 0)
                        strcat(plus_new_digits,"0");

                new_digits_len = ( strlen(plus_new_digits) + 1 ) / 2;
                memset( new_digits, '\0', MAX_MSISDN_LEN + 1 );
        
        }	
	else if ( ( prq->request.choice == ulcm_updateGprsRequest_chosen ) && 
		( strstr( Pglobal->sccpAddrArray[prq->confIndex].old_digits, "*" ) == NULL ) )
	{
	    new_digits_len = prq->request.u.updateGprsRequest.updateGprsLocationArg.imsi.length;
	    memcpy( new_digits, prq->request.u.updateGprsRequest.updateGprsLocationArg.imsi.value, 
		    prq->request.u.updateGprsRequest.updateGprsLocationArg.imsi.length );
	}

	else if ( ( prq->request.choice == ulcm_authRequest_chosen ) && 
		( strstr( Pglobal->sccpAddrArray[prq->confIndex].old_digits, "*" ) != NULL ) )
	{
	    int  len = strlen( Pglobal->sccpAddrArray[prq->confIndex].new_digits );
	    new_digits_len = ( len + 1 ) / 2;
	    memset( new_digits, 0xff, MAX_MSISDN_LEN + 1 );
	}
	else
	{
	    new_digits_len = prq->request.u.authRequest.imsi.length;
	    memcpy( new_digits, prq->request.u.authRequest.imsi.value, 
		    prq->request.u.authRequest.imsi.length );
	}

	if (  strstr( Pglobal->sccpAddrArray[prq->confIndex].old_digits, "+" ) == NULL ) 
	{
	    int             tempCounter = 0;
	    int             ind = 0;
	    int             len = strlen( Pglobal->sccpAddrArray[prq->confIndex].new_digits );
	    int             len1 = len;

	    if ( len % 2 != 0 )
		len1 = len - 1;

	    for ( tempCounter = 0; tempCounter < len1; tempCounter += 2 )
	    {
			new_digits[ind] = ( Pglobal->sccpAddrArray[prq->confIndex].new_digits[tempCounter] - '0' );
			new_digits[ind] |= ( Pglobal->sccpAddrArray[prq->confIndex].new_digits[tempCounter + 1] - '0' ) << 4;
			ind = ind + 1;
	    }

	    if ( len % 2 != 0 )
	    {
			new_digits[ind] &= 0xf0;
			new_digits[ind] |= Pglobal->sccpAddrArray[prq->confIndex].new_digits[len - 1] - '0';
	    }
	    
	    /*  Convert the IMSI from TBCD (Telephony Binary Coded Decimal) String
	   to  the format required by MAP_SccpAddr */
		for ( i = 0, j = 0; i < new_digits_len; i++ )
		{
			pgb->parameter.openArg.destinationAddress.gt.msisdn[j++] = new_digits[i] & 0x0f;
			pgb->parameter.openArg.destinationAddress.gt.msisdn[j] = ( new_digits[i] >> 4 ) & 0x0f;

			if ( pgb->parameter.openArg.destinationAddress.gt.msisdn[j] != 0xf )
			{
				j++;
			}
		}
	} else {
	
		int             tempCounter = 0;
	    int             ind = 0;
	    int             len = strlen( plus_new_digits );
	    int             len1 = len;
	
	    if ( len % 2 != 0 )
		len1 = len - 1;

	    for ( tempCounter = 0; tempCounter < len1; tempCounter += 2 )
	    {
			new_digits[ind] = ( plus_new_digits[tempCounter] - '0' );
			new_digits[ind] |= ( plus_new_digits[tempCounter + 1] - '0' ) << 4;
			ind = ind + 1;
	    }

	    if ( len % 2 != 0 )
	    {
			new_digits[ind] &= 0xf0;
			new_digits[ind] |= plus_new_digits[len - 1] - '0';
	    }
		
		/*  Convert the IMSI from TBCD (Telephony Binary Coded Decimal) String
	   to  the format required by MAP_SccpAddr */
		for ( i = 0, j = 0; i < new_digits_len; i++ )
		{
			pgb->parameter.openArg.destinationAddress.gt.msisdn[j++] = new_digits[i] & 0x0f;
			pgb->parameter.openArg.destinationAddress.gt.msisdn[j] = ( new_digits[i] >> 4 ) & 0x0f;

			if ( pgb->parameter.openArg.destinationAddress.gt.msisdn[j] != 0xf )
			{
				j++;
			}
		}
		
	}
	
	pgb->parameter.openArg.destinationAddress.gt.msisdnLength = j;
    }

   /**********************************/
    /* Encode the originating address */
   /**********************************/
    pgb->parameter.openArg.bit_mask |= MAP_OpenArg_originatingAddress_present;
    pgb->parameter.openArg.originatingAddress.gt.gtIndicator = Pglobal->LOCAL_GTI;
    pgb->parameter.openArg.originatingAddress.routingIndicator = Pglobal->LOCAL_RI;

    if ( Pglobal->LOCAL_PC != 0 )
    {

	pgb->parameter.openArg.originatingAddress.bit_mask |= MAP_SccpAddr_pointCode_present;
	pgb->parameter.openArg.originatingAddress.pointCode = Pglobal->LOCAL_PC;
	DBG_DISP( DBG_MASK_L2, 173,
		  "initializeSCCPaddresses: LPC (assigned) = %d"
		  ", LPC (global var) = %d\n", 
		  pgb->parameter.openArg.originatingAddress.pointCode, Pglobal->LOCAL_PC );
    }

    pgb->parameter.openArg.originatingAddress.ssn = Pglobal->LOCAL_SSN;
    pgb->parameter.openArg.originatingAddress.gt.natureOfAddress = Pglobal->LOCAL_NAI;
    pgb->parameter.openArg.originatingAddress.gt.numberingPlan = Pglobal->LOCAL_NP;

    if ( strcmp( Pglobal->LOCAL_MSISDN, "0" ) != 0 )
	pgb->parameter.openArg.originatingAddress.bit_mask |= MAP_SccpAddr_gt_present;

    /*   Convert the MSISDN string to  the format required by MAP_SccpAddr */
    pgb->parameter.openArg.originatingAddress.gt.msisdnLength = strlen( Pglobal->LOCAL_MSISDN );

    for ( i = 0; i < pgb->parameter.openArg.originatingAddress.gt.msisdnLength; i++ )
    {
	pgb->parameter.openArg.originatingAddress.gt.msisdn[i] = ( *( Pglobal->LOCAL_MSISDN + i ) & 0x0f );
    }

    pgb->parameter.openArg.originatingAddress.gt.translationType = Pglobal->LOCAL_TT;
    return 1;
}

/* FUNCTION initiateDialog() - Initiate a typical dialog and request       */
/*************************************************************************

FUNCTION
    int initiateDialog(gblock_t* pgb, pendingReq_t* prq)
    the local bound SSN (see SYSbind) can be received by the process.


DESCRIPTION
        This function is called by the client to send an open followed by
	a authentication info request

INPUTS
        Arg:    pgb - a reference to an externally allocated gblock_t
                prq - a reference to the structure defining the pending request

OUTPUTS
        Return: 1 if no error
                0 if an error occured 

FEND
*************************************************************************/
int
initiateDialog( gblock_t * pgb, pendingReq_t * prq )
{
    int             ret_val = 0;

    /* first: we send an open */
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = GMAP_OPEN;
    pgb->applicationId = 0;
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->parameter.openArg.bit_mask = 0;
    /* CR 27771: initialize other bitmasks set in "initializeSCCPaddresses"
       to zero */
    pgb->parameter.openArg.destinationAddress.bit_mask = 0;
    pgb->parameter.openArg.destinationAddress.gt.bit_mask = 0;
    pgb->parameter.openArg.originatingAddress.bit_mask = 0;


    memcpy( &pgb->parameter.openArg.applicationContext, &prq->applicationContext, sizeof( ObjectID ) );

    /*This function initializes the originating and the destination
       addresses in the gblock structure */

    if ( initializeSCCPaddresses( pgb, prq ) == -1 )
    {
	setErrorCode( prq, ulcm_gatewayProvisioningError );
	sendResponseOnSocket( prq, CLEAN );
	DBG_DISP( DBG_MASK_ERR, 17, "Cannot initialize SCCP address\n" );
	return ret_val;
    }

    if ( Pglobal->dbg.trace_enabled )
    {
	gMAPPrintGBlock( pgb );
    }

    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	if ( NULL != strstr( APIErr, "No TRANSLATION FOR SPECIFIC ADDRESS" ) )
	{
	    setErrorCode( prq, ulcm_unknownImsi );
	    /* at this point we didn't store the DialogId in the Table */
	    sendResponseOnSocket( prq, CLEAN );
	    DBG_DISP( DBG_MASK_ERR, 17, APIErr );
	}
	else
	{
	    DBG_DISP( DBG_MASK_ERR, 18, 
		    "initiateDialog: GMAP_OPEN gMAPPutGBlock failed for requestID %d: %s\n", 
		    prq->requestId, APIErr );
	    setErrorCode( prq, ulcm_gatewayError );
	    sendResponseOnSocket( prq, CLEAN );
	    return ret_val;
	}
    }

    /* we save the dialogId */
    prq->dialogId = pgb->dialogId;
    /* set the timestamp */
    prq->timestamp = getTimeStamp(  );

    /* populate the Gblock for the invoke request */
    if ( prq->request.choice == ulcm_authRequest_chosen )
    {
	if ( prq->status == waitingForOpenRspTriplets )
	{
	    ret_val = populateTripletsGblock( pgb, prq );
	}
	else if ( prq->status == waitingForOpenRspRestoreData )
	{
	    ret_val = populateRestoreDataGblock( pgb, prq );
	}
    }

    if ( prq->request.choice == ulcm_imsiRequest_chosen )
    {
	ret_val = populateImsiGblock( pgb, prq );
    }
    
	if ( prq->request.choice == ulcm_updateGprsRequest_chosen )
    {
        ret_val = populateUpdateGprsGblock( pgb, prq );
    }


    if ( ret_val != 1 )
	return ret_val;

    if ( Pglobal->dbg.trace_enabled )
    {
	gMAPPrintGBlock( pgb );
    }

    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	if ( NULL != strstr( APIErr, "No TRANSLATION FOR SPECIFIC ADDRESS" ) )
	{
	    setErrorCode( prq, ulcm_unknownImsi );
	    sendResponseOnSocket( prq, CLEAN );
	    DBG_DISP( DBG_MASK_ERR, 19, APIErr );
	}
	else
	{
	    DBG_DISP( DBG_MASK_ERR, 20, 
		    "initiateDialog: GMAP_OPEN gMAPPutGBlock failed: %s\n", 
		    APIErr );
	    setErrorCode( prq, ulcm_gatewayError );
	    sendResponseOnSocket( prq, CLEAN );
	    return ret_val;
	}
    }

    /* then we send a delimiter */
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = GMAP_DELIMITER;
    pgb->applicationId = 0;
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->parameter.delimiter.qualityOfService = CL_SVC_CLASS_1 | RETURN_ERROR;

    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	if ( NULL != strstr( APIErr, "No TRANSLATION FOR SPECIFIC ADDRESS" ) )
	{
	    prq->response.u.authResponse.errorCode = ulcm_unknownImsi;
	    /* the dialogId is in the table but no entry is in the hash yet */
	    sendResponseOnSocket( prq, CLEAN );
	    DBG_DISP( DBG_MASK_ERR, 21, APIErr );
	}
	else
	{
	    DBG_DISP( DBG_MASK_ERR, 22, 
		    "initiateDialog: GMAP_OPEN gMAPPutGBlock failed: %s\n", 
		    APIErr );
	    prq->response.u.authResponse.errorCode = ulcm_gatewayError;
	    sendResponseOnSocket( prq, CLEAN );
	}

	return ret_val;
    }
    else
    {
	/* The initiateDialog was succesfull */
	return 1;
    }
}
/* FUNCTION populateTripletsGblock() 					*/
/*************************************************************************

FUNCTION
	int populateTripletsGblock( gblock_t * pgb, pendingReq_t * prq )
DESCRIPTION
	Populate Triplet Gblock
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:   0 	: error
		  1	: success

FEND
*************************************************************************/

int
populateTripletsGblock( gblock_t * pgb, pendingReq_t * prq )
{
    /* then we send a SEND_AUTHENTICATION_INFO message */
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = SEND_AUTHENTICATION_INFO;
    pgb->applicationId = 0;
    pgb->invokeId = 1;
    /* we save the invokeId */
    prq->invokeId[0] = pgb->invokeId;
    pgb->linkedId = -1;
    pgb->invokeTimer = Pglobal->invokeTimeout;
    pgb->bit_mask = gblock_t_parameter_present;

    if ( compareObjectIDs( &prq->applicationContext, &infoRetrievalContext_v3 ) )
    {
        /* build a v3 authentication Info message */
        pgb->parameter.sendAuthenticationInfoArg_v3.bit_mask = 0;
        pgb->parameter.sendAuthenticationInfoArg_v3.imsi.length = 8;
        pgb->parameter.sendAuthenticationInfoArg_v3.numberOfRequestedVectors = 
        prq->request.u.authRequest.numberOfRequestedVectors;

        if ( prq->request.u.authRequest.re_synchronisationInfo.auts.length != 0 )
        {
            pgb->parameter.sendAuthenticationInfoArg_v3.bit_mask |= 
            SendAuthenticationInfoArg_v3_re_synchronisationInfo_present;

            pgb->parameter.sendAuthenticationInfoArg_v3.re_synchronisationInfo.rand.length = 
            prq->request.u.authRequest.re_synchronisationInfo.rand.length;
            memcpy( pgb->parameter.sendAuthenticationInfoArg_v3.re_synchronisationInfo.rand.value,
                    prq->request.u.authRequest.re_synchronisationInfo.rand.value,
                    sizeof( pgb->parameter.sendAuthenticationInfoArg_v3.re_synchronisationInfo.rand.value ) );
            pgb->parameter.sendAuthenticationInfoArg_v3.re_synchronisationInfo.auts.length = 
            prq->request.u.authRequest.re_synchronisationInfo.auts.length;
            memcpy( pgb->parameter.sendAuthenticationInfoArg_v3.re_synchronisationInfo.auts.value,
                    prq->request.u.authRequest.re_synchronisationInfo.auts.value,
                    sizeof( pgb->parameter.sendAuthenticationInfoArg_v3.re_synchronisationInfo.auts.value ) );
        }
        memcpy( pgb->parameter.sendAuthenticationInfoArg_v3.imsi.value,
                prq->request.u.authRequest.imsi.value, 
                sizeof( pgb->parameter.sendAuthenticationInfoArg_v3.imsi.value ) );

        /**
         * CR 32051
         * If segmentation is prohibited the HLR shall not send the result within
         * a TC-CONTINUE message.
         * NB: Note that the following modification affects the behavior of the gateway
         * in all cases. However, the change should be minor since the gateway is not
         * capable of processing segmented messages anyway.
         */
        if (!segment_flag) /* FR 904 -- make it a command line argument */
        {
        	pgb->parameter.sendAuthenticationInfoArg_v3.bit_mask |= 
        	SendAuthenticationInfoArg_v3_segmentationProhibited_present;
        	pgb->parameter.sendAuthenticationInfoArg_v3.segmentationProhibited = 1;
        	/** End of CR 32051 */
        }
    }
    else if ( compareObjectIDs( &prq->applicationContext, &infoRetrievalContext_v2 ) )
    {
        /* build a v2 authentication Info message */
        pgb->parameter.sendAuthenticationInfoArg_v2.length = 8;
        memcpy( pgb->parameter.sendAuthenticationInfoArg_v2.value, 
                prq->request.u.authRequest.imsi.value, pgb->parameter.sendAuthenticationInfoArg_v2.length );
    }
    else
    {
        prq->response.u.authResponse.errorCode = ulcm_gatewayError;
        sendResponseOnSocket( prq, CLEAN );
        DBG_DISP( DBG_MASK_ERR, 418, "Application Context version not supported \n" );
        return 0;
    }

    return 1;
}


/* FUNCTION sendAnotherServiceMsg()                                    */
/*************************************************************************

FUNCTION
        int sendAnotherServiceMsg( gblock_t * pgb, pendingReq_t * prq )
DESCRIPTION
       Send a SEND_AUTHENTICATION_INFO request within a TC-CONTINUE 
INPUTS
        Arg:    pgb     : pointer to gblock
                prq     : pointer to pending request

OUTPUTS
        Return:   0     : error
                  1     : success

FEND
*************************************************************************/
int
sendAnotherServiceMsg(gblock_t *pgb, pendingReq_t * prq )
{
    pgb->dialogId = prq->dialogId;
    /* Send an empty SEND_AUTHENTICATION_INFO message */
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = SEND_AUTHENTICATION_INFO;
    pgb->applicationId = 0; 
    pgb->invokeId = ++prq->invokeId[0];
    /* we save the invokeId */
    prq->invokeId[0] = pgb->invokeId;
    pgb->bit_mask = 0;
    pgb->parameter.openArg.destinationAddress.bit_mask = 0;
    pgb->parameter.openArg.destinationAddress.gt.bit_mask = 0;
    pgb->parameter.openArg.originatingAddress.bit_mask = 0;


    if ( compareObjectIDs( &prq->applicationContext, &infoRetrievalContext_v3 ))
    {
        pgb->parameter.sendAuthenticationInfoArg_v3.bit_mask = 0;
	pgb->parameter.sendAuthenticationInfoArg_v3.imsi.length = 0;
    }
    else /* Something is wrong */
    {
	    DBG_DISP( DBG_MASK_ERR, 20, 
		    "sendAnotherServiceMsg: TC-CONTINUE for v2???\n");
	    setErrorCode( prq, ulcm_gatewayError );
	    sendResponseOnSocket( prq, CLEAN );
	    return 0;
    }

    if ( Pglobal->dbg.trace_enabled )
    {
	gMAPPrintGBlock( pgb );
    }

    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	if ( NULL != strstr( APIErr, "No TRANSLATION FOR SPECIFIC ADDRESS" ) )
	{
	    setErrorCode( prq, ulcm_unknownImsi );
	    sendResponseOnSocket( prq, CLEAN );
	    DBG_DISP( DBG_MASK_ERR, 19, APIErr );
	}
	else
	{
	    DBG_DISP( DBG_MASK_ERR, 20, 
		    "sendAnotherServiceMsg: gMAPPutGBlock failed: %s\n", 
		    APIErr );
	    setErrorCode( prq, ulcm_gatewayError );
	    sendResponseOnSocket( prq, CLEAN );
	    return 0;
	}
    }

    /* then we send a delimiter */
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = GMAP_DELIMITER;
    pgb->applicationId = 0;
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->parameter.delimiter.qualityOfService = CL_SVC_CLASS_1 | RETURN_ERROR;

    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	if ( NULL != strstr( APIErr, "No TRANSLATION FOR SPECIFIC ADDRESS" ) )
	{
	    prq->response.u.authResponse.errorCode = ulcm_unknownImsi;
	    /* the dialogId is in the table but no entry is in the hash yet */
	    sendResponseOnSocket( prq, CLEAN );
	    DBG_DISP( DBG_MASK_ERR, 21, APIErr );
	}
	else
	{
	    DBG_DISP( DBG_MASK_ERR, 22, 
		    "sendAnotherServiceMsg: gMAPPutGBlock failed: %s\n", 
		    APIErr );
	    prq->response.u.authResponse.errorCode = ulcm_gatewayError;
	    sendResponseOnSocket( prq, CLEAN );
	}

	return 0;
    }
    else
    {
	/* TC-CONTINUE was sent succesfully */
	return 1;
    }
}

/* FUNCTION populateRestoreDataGblock() 					*/
/*************************************************************************

FUNCTION
	int populateRestoreDataGblock( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	Populate Restore Data  Gblock
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:   0 	: error
		  1	: success

FEND
*************************************************************************/

int
populateRestoreDataGblock( gblock_t * pgb, pendingReq_t * prq )
{
    int    k=0;
//    printf("populateRestoreDataGblock: requestId=%d\n", prq->requestId);
    /* then we send a RESTORE_DATA message */
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = RESTORE_DATA;
    pgb->applicationId = 0;
    pgb->invokeId = 50;
    /* we save the invokeId */
    prq->invokeId[0] = pgb->invokeId;
    pgb->linkedId = -1;
    pgb->invokeTimer = Pglobal->invokeTimeout;
    pgb->bit_mask = gblock_t_parameter_present;

    if ( compareObjectIDs( &prq->applicationContext, &networkLocUpContext_v3 ) )
    {
	pgb->parameter.restoreDataArg_v3.bit_mask = 0;
	pgb->parameter.restoreDataArg_v3.imsi.length = 8;
	memcpy( pgb->parameter.restoreDataArg_v3.imsi.value, 
		prq->request.u.authRequest.imsi.value, 
		pgb->parameter.restoreDataArg_v3.imsi.length );
	if ( supportedCamelPhases[0] != '\0' )
	{	
		pgb->parameter.restoreDataArg_v3.bit_mask = pgb->parameter.restoreDataArg_v3.bit_mask | RestoreDataArg_v3_vlr_Capability_present;
		pgb->parameter.restoreDataArg_v3.vlr_Capability.bit_mask = VLR_Capability_supportedCamelPhases_present;
		pgb->parameter.restoreDataArg_v3.vlr_Capability.supportedCamelPhases.length = 16;
	}
	
	int phase = 0;
	if ( supportedCamelPhases[0] == '1' )
	{
	        phase = SupportedCamelPhases_phase1;
	}
	if ( supportedCamelPhases[1] == '1' )
	{
	        phase = phase | SupportedCamelPhases_phase2;
	}
	if ( supportedCamelPhases[2] == '1' )
	{
	        phase = phase | SupportedCamelPhases_phase3;
	}
	if ( supportedCamelPhases[3] == '1' )
	{
	        phase = phase | SupportedCamelPhases_phase4;
	}
	
	char ch[2] = {phase,0};
	memcpy( pgb->parameter.restoreDataArg_v3.vlr_Capability.supportedCamelPhases.value, ch , sizeof(ch));
	
	
    }
    else if ( compareObjectIDs( &prq->applicationContext, &networkLocUpContext_v2 ) )
    {
	/* build a v2 authentication Info message */
	pgb->parameter.restoreDataArg_v2.imsi.length = 8;
	memcpy( pgb->parameter.restoreDataArg_v2.imsi.value, 
		prq->request.u.authRequest.imsi.value, 
		pgb->parameter.restoreDataArg_v2.imsi.length );

    }
    else
    {
	prq->response.u.authResponse.errorCode = ulcm_gatewayError;
	sendResponseOnSocket( prq, CLEAN );
	DBG_DISP( DBG_MASK_ERR, 420, "Application Context version not supported \n" );
	return 0;
    }
//    printf("populateRestoreDataGblock: Reset the serviceProvisioned and ss_code fields\n");
//    printf("populateRestoreDataGblock:  bsCodesCount=%d, tsCodesCount=%d\n", 
//	    Pglobal->sccpAddrArray[prq->confIndex].bsCodesCount, 
//	    Pglobal->sccpAddrArray[prq->confIndex].tsCodesCount);

    /* Reset the serviceProvisioned and ss_code fields for each BS and TS */
    for (k = 0;k < Pglobal->sccpAddrArray[prq->confIndex].tsCodesCount; k++)
    {
        prq->tserviceProvisioned[k] = FALSE;
        Pglobal->sccpAddrArray[prq->confIndex].ts_ss_code[k] = 0;
    }

    for (k = 0;k < Pglobal->sccpAddrArray[prq->confIndex].bsCodesCount; k++)
    {
        prq->bserviceProvisioned[k] = FALSE;
        Pglobal->sccpAddrArray[prq->confIndex].bs_ss_code[k] = 0;
    }

    return 1;
}

/* FUNCTION populateImsiGblock() 					*/
/*************************************************************************

FUNCTION
	int populateImsiGblock( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	Populate IMSI  Gblock
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:   0 	: error
		  1	: success

FEND
*************************************************************************/
int
populateImsiGblock( gblock_t * pgb, pendingReq_t * prq )
{
    /* then we send a SEND_IMSI message */
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = SEND_IMSI;
    pgb->applicationId = 0;
    pgb->invokeId = 1;
    /* we save the invokeId */
    prq->invokeId[0] = pgb->invokeId;
    pgb->linkedId = -1;
    pgb->invokeTimer = Pglobal->invokeTimeout;
    pgb->bit_mask = gblock_t_parameter_present;
    memcpy( &pgb->parameter.roamingNumber_v1, 
	    &prq->request.u.imsiRequest.msisdn, 
	    sizeof( ulcm_ISDN_AddressString ) );
    return 1;
}

/* FUNCTION populateUpdateGprsGblock() 					*/
/*************************************************************************

FUNCTION
	int populateUpdateGprsGblock( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	Populate UpdateGprsLogation  Gblock
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:   0 	: error
		  1	: success

FEND
*************************************************************************/
int
populateUpdateGprsGblock( gblock_t * pgb, pendingReq_t * prq )
{
    /* then we send an UPDATE_GPRS_LOCATION message */
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = UPDATE_GPRS_LOCATION;
    pgb->applicationId = 0;
    pgb->invokeId = 1;
    /* we save the invokeId */
    prq->invokeId[0] = pgb->invokeId;
    pgb->linkedId = -1;
    pgb->invokeTimer = Pglobal->invokeTimeout;
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->parameter.updateGprsLocationArg_v3.bit_mask = 0;
    pgb->parameter.updateGprsLocationArg_v3.imsi.length = 8;
    memcpy( &pgb->parameter.updateGprsLocationArg_v3.imsi.value, 
	    &prq->request.u.updateGprsRequest.updateGprsLocationArg.imsi.value, 
	    prq->request.u.updateGprsRequest.updateGprsLocationArg.imsi.length );
    pgb->parameter.updateGprsLocationArg_v3.sgsn_Number.length = prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Number.length;
    memcpy( &pgb->parameter.updateGprsLocationArg_v3.sgsn_Number.value, 
	    &prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Number.value, 
	    prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Number.length );
    pgb->parameter.updateGprsLocationArg_v3.sgsn_Address.length = prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Address.length;
    memcpy( &pgb->parameter.updateGprsLocationArg_v3.sgsn_Address.value, 
	    &prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Address.value, 
	    prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Address.length );
    if ( ( prq->request.u.updateGprsRequest.updateGprsLocationArg.bit_mask & UpdateGprsLocationArg_v3_sgsn_Capability_present ) != 0 )
    {
	pgb->parameter.updateGprsLocationArg_v3.sgsn_Capability.bit_mask = 0;
	if ( ( prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Capability.bit_mask & SGSN_Capability_gprsEnhancementsSupportIndicator_present ) != 0 )
	{
	    pgb->parameter.updateGprsLocationArg_v3.sgsn_Capability.bit_mask |= SGSN_Capability_gprsEnhancementsSupportIndicator_present;
	    pgb->parameter.updateGprsLocationArg_v3.sgsn_Capability.gprsEnhancementsSupportIndicator = TRUE;
	}
	if ( ( prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Capability.bit_mask & SGSN_Capability_supportedCamelPhases_present ) != 0 )
	{
	    pgb->parameter.updateGprsLocationArg_v3.sgsn_Capability.bit_mask |= SGSN_Capability_supportedCamelPhases_present; /* bit string, length is number of bits */
	    pgb->parameter.updateGprsLocationArg_v3.sgsn_Capability.supportedCamelPhases.length = prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Capability.supportedCamelPhases.length;
	    pgb->parameter.updateGprsLocationArg_v3.sgsn_Capability.supportedCamelPhases.value[0] = prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Capability.supportedCamelPhases.value[0];
	    pgb->parameter.updateGprsLocationArg_v3.sgsn_Capability.supportedCamelPhases.value[1] = prq->request.u.updateGprsRequest.updateGprsLocationArg.sgsn_Capability.supportedCamelPhases.value[1];
	}
	if ( pgb->parameter.updateGprsLocationArg_v3.sgsn_Capability.bit_mask != 0 )
	{
	    pgb->parameter.updateGprsLocationArg_v3.bit_mask |= UpdateGprsLocationArg_v3_sgsn_Capability_present;
	}
    }
    return 1;
}


/* FUNCTION abortDialog() - Aborts the dialog                           */
/*************************************************************************

FUNCTION
    void abortDialog(gblock_t* pgb, pendingReq_t *prq, int clean)

DESCRIPTION
        This function aborts the dialog.

INPUTS
        Arg:    pgb - a referenceq to an externally allocated gblock_t
                clean - specify if we have to remove entry from table

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
abortDialog( gblock_t * pgb, pendingReq_t * prq, int clean )
{
    clean = CLEAN;		/* check if in some cases we need no CLEAN */
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = GMAP_U_ABORT;
    pgb->applicationId = 0;
    pgb->dialogId = prq->dialogId;
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->parameter.uAbortArg.userReason.choice = MAP_UserAbortChoice_userSpecificReason_chosen;
	pgb->parameter.uAbortArg.qualityOfService=CL_SVC_CLASS_1;
	
    if ( Pglobal->dbg.trace_enabled )
    {
	gMAPPrintGBlock( pgb );
    }

    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	if ( NULL != strstr( APIErr, "No TRANSLATION FOR SPECIFIC ADDRESS" ) )
	{
	    setErrorCode( prq, ulcm_unknownImsi );
	    sendResponseOnSocket( prq, clean );
	    DBG_DISP( DBG_MASK_ERR, 23, APIErr );
	}
	else
	{
	    DBG_DISP( DBG_MASK_ERR, 24, 
		    "abortDialog: GMAP_OPEN gMAPPutGBlock failed: %s\n", 
		    APIErr );
	    prq->response.u.authResponse.errorCode = ulcm_gatewayError;
	    prq->response.u.imsiResponse.errorCode = ulcm_gatewayError;
	    sendResponseOnSocket( prq, clean );
	}
    }
}


/* FUNCTION endDialog() - Closes the dialog                           */
/*************************************************************************

FUNCTION
    void endDialog(gblock_t* pgb, pendingReq_t *prq, int clean)

DESCRIPTION
        This function aborts the dialog.

INPUTS
        Arg:    pgb - a referenceq to an externally allocated gblock_t
                clean - specify if we have to remove entry from table

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
endDialog( gblock_t * pgb, pendingReq_t * prq, int clean )
{
    int             tempIndex = 0, currentDialogId = 0;

    clean = CLEAN;		/* check if in some cases we need no CLEAN */
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = GMAP_CLOSE;
    pgb->applicationId = 0;
    pgb->dialogId = prq->dialogId;
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->parameter.closeArg.releaseMethod = normalRelease;
    pgb->parameter.closeArg.qualityOfService = CL_SVC_CLASS_1;


    if ( Pglobal->dbg.trace_enabled )
    {
	gMAPPrintGBlock( pgb );
    }

    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	DBG_DISP( DBG_MASK_ERR, 24, 
	    "endDialog: GMAP_CLOSE gMAPPutGBlock failed, error = %d, \
		error string: %s\n", 
		pgb->error, APIErr );
	    prq->response.u.authResponse.errorCode = ulcm_gatewayError;
	    prq->response.u.imsiResponse.errorCode = ulcm_gatewayError;
	    sendResponseOnSocket( prq, clean );
	    return;
    }
    /* Now do the RESTORE-DATA stuff */
    /* If there was an error during the authentication or
       if no authorization is required (no bs or ts configured) we send the
       response on to the client library and don't do the restoreData
     */

        if ( prq->response.u.authResponse.errorCode != ulcm_noError )
        {
            sendResponseOnSocket( prq, CLEAN );
        }
	else 
	{
	    prq->response.u.authResponse.serviceFound = 1;
	    sendResponseOnSocket( prq, CLEAN );
	}
}


/* FUNCTION setErrorCode */
/*************************************************************************

FUNCTION
    void setErrorCode (pendingReq_t *prq, int errorCode)

DESCRIPTION
        This function set the given error code into the appropriate structure
        in order to return the error code to the client. 

INPUTS
        Arg:    prq - 
                errorCode

OUTPUTS
        None

FEND
*************************************************************************/
void
setErrorCode( pendingReq_t * prq, int errorCode )
{
    if ( prq->request.choice == ulcm_authRequest_chosen )
    {
	prq->response.u.authResponse.errorCode = errorCode;
    }

    if ( prq->request.choice == ulcm_imsiRequest_chosen )
    {
	prq->response.u.imsiResponse.errorCode = errorCode;
    }
}

/* FUNCTION cleanHashTables */
/*************************************************************************

FUNCTION
    int  cleanHashTables(pendingReq_t *prq)

DESCRIPTION
        This function remove an entry from the dialogId and from the
       requestId hash tables

INPUTS
        Arg:    prq - element to be removed from the hash tables

OUTPUTS
        Return: 1 on success, 0 on failure

FEND
*************************************************************************/

int
cleanHashTables( pendingReq_t * prq )
{
    int             returnVal = 1;

    if ( !hash_remove_entry( &dialogId_hash, prq->dialogId ) )
    {
	/* Cannot find entry for this dialogId */
	DBG_DISP( DBG_MASK_ERR, 251, 
		"Cannot remove entry for the dialogId %d \n", 
		prq->dialogId );
	returnVal -= 1;
    }

    if ( !hash_remove_entry( &requestId_hash, prq->requestId ) )
    {
	/* Cannot find entry for this dialogId */
	DBG_DISP( DBG_MASK_ERR, 252, 
		"Cannot remove entry for the requestId %d\n", 
		prq->requestId );
	returnVal -= 1;
    }

    return returnVal;
}

/* FUNCTION findFreeSpot */
/*************************************************************************

FUNCTION
    int  findFreeSpot()

DESCRIPTION
        This function find an empty entry in the table

INPUTS
        Arg:  none

OUTPUTS
        Return: index of the entry

FEND
*************************************************************************/
short
findFreeSpot(  )
{
    int             i;

    /* find a free spot in the table */
    for ( i = 1; i <= Pglobal->max_requests; i++ )
    {
	/* If there is a  valid entry, the dialogId cannot be null */
	if ( PmyTable[i].dialogId == 0 )
	{
	    /* initialize the entry before it can be used */
	    memset( &PmyTable[i], 0, sizeof( pendingReq_t ) );
	    return i;
	}
    }

    return -1;
}

/* FUNCTION sendErrorOnSocket() */
/*************************************************************************

FUNCTION
    void sendErrorOnSocket(ulcm_ErrorCode err)

DESCRIPTION
        This function creates an error message and send it on the socket

INPUTS
        Arg:  err - the error code

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
sendErrorOnSocket( ulcm_ErrorCode err )
{
    pendingReq_t    prq;

    prq.response.choice = ulcm_errorResponse_chosen;
    prq.response.u.errorResponse.errorCode = err;
    sendResponseOnSocket( &prq, NO_CLEAN );
}

/* FUNCTION sendResponseOnSocket() */
/*************************************************************************

FUNCTION
        void sendResponseOnSocket(pendingReq_t* prq, int clean)

DESCRIPTION
        This function sends a response message on the socket

INPUTS
        Arg:  prq - structure containing the response
              clean - true if the tables have to be cleaned

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
sendResponseOnSocket( pendingReq_t * prq, BOOL clean )
{
    int             pdunum = ulcm_Response_PDU;
    int             return_code;
/*
**  commented out as this is used only in dumpResponse call
**  part of fix for 32769
    char	    resp[1024];
*/


    wlanBuf1.length = MAX_TCP_MESSAGE_SIZE;
    wlanBuf1.value = encodedBuffer;

    if ( Pglobal->dbg.trace_enabled )
    {
        printf("Dumping Response before sending on socket\n");
        printf("=========================================\n");
        /* 
        ** commenting out dumpResponse so that there is no dependency
        ** on authDataStruct.h which is not delivered -- Vijaya
        ** fix for CR 32769
        */
        /* dumpResponse(&prq->response, resp, sizeof( resp ) ); */
    }


    return_code = wlanEncode(&world, pdunum, &prq->response, &wlanBuf1 );

    if ( 0 != return_code )
    {
	DBG_DISP( DBG_MASK_ERR, 26, 
		"Error %d while encoding the response message for the client.\n", 
		return_code );
	sendErrorOnSocket( ulcm_gatewayError );
    }

    if ( -1 == tcp_send( Pglobal->sock, wlanBuf1.value, wlanBuf1.length ) )
    {
	perror("sendResponseOnSocket: send");
	/*DBG_DISP( DBG_MASK_ERR, 261, "sctp_send failed: %d\n", errno ); */
	DBG_DISP( DBG_MASK_ERR, 261, 
		"TCP send failed: %d, waiting for another connection\n", 
		errno );
    } else {
		LOG_MSG(LOG_LEVEL_INFO, "Response sent succesfully to client. Request ID: %d\n", prq->requestId);
    }
    

    if ( Pglobal->dbg.trace_enabled )
    {
	wlanPrintHex(&world, ( char * ) wlanBuf1.value, wlanBuf1.length );
	wlanPrintPDU(&world, pdunum, &prq->response );
    }

    if ( clean )
    {
	cleanHashTables( prq );
	/* re-initializes this entry in the table  */
	memset( prq, 0, sizeof( *prq ) );
    }
}

/* FUNCTION retryInitiateDialog() */
/*************************************************************************

FUNCTION
    void retryInitiateDialog(gblock_t *pgb, pendingReq_t *prq, int status)

DESCRIPTION
	re-send the same initiateDialog request with
        a different dialogId
INPUTS
        Arg:    pgb - a reference to the received gblock_t
                prq
                status - state machine state (waiting for open response ...)

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
retryInitiateDialog( gblock_t * pgb, pendingReq_t * prq, int status )
{
    /* Here we call initiateDialog()
     * and clear the old dialogID
     */
    int             oldDialogId = prq->dialogId;

    /* we will have a different DialogId so we need to     */
    /* update the hash (remove old entry, insert new entry) */
    int             i = hash_remove_entry( &dialogId_hash, oldDialogId );

    if ( i == 0 )
    {				/* should not happen, we did a hash_get earlier */
	DBG_DISP( DBG_MASK_ERR, 632, 
		"Provider error: hash_remove_entry(): Cannot find entry for the dialogId %d\n", 
		oldDialogId );
    }

    /* re-sending the same invoke */
    prq->status = status;

    if ( 0 != initiateDialog( pgb, prq ) )
    {
	/* initiateDialog was successful */
	if ( 0 == hash_add_entry( &dialogId_hash, pgb->dialogId, i ) )
	{			/* should not happen as we just freed a bucket */
	    DBG_DISP( DBG_MASK_ERR, 633, 
		    "No more bucket available, cannot insert entry in the hash.\n" );
	}
    }
    else
    {
	/* initiateDialog failed, the table should have been cleaned, clean the requestId_hash */
	int  i = hash_remove_entry( &requestId_hash, prq->requestId );

	if ( i == 0 )
	{			/* should not happen, we did a hash_get earlier */
	    DBG_DISP( DBG_MASK_ERR, 6321, 
		    "Provider error: hash_remove_entry(): Cannot find entry for the requestId %d\n", 
		    prq->requestId );
	}
    }
}

/* FUNCTION retryInitiateDialogTriplets() 				*/
/*************************************************************************

FUNCTION
	void retryInitiateDialogTriplets( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	call 	retryInitiateDialog function with waitingForOpenRspTriplets
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/

void
retryInitiateDialogTriplets( gblock_t * pgb, pendingReq_t * prq )
{
    retryInitiateDialog( pgb, prq, waitingForOpenRspTriplets );
}

/* FUNCTION retryInitiateDialogRestoreData() 				*/
/*************************************************************************

FUNCTION
	void retryInitiateDialogRestoreData( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	call 	retryInitiateDialog function with waitingForOpenRspRestoreData
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/
void
retryInitiateDialogRestoreData( gblock_t * pgb, pendingReq_t * prq )
{
    retryInitiateDialog( pgb, prq, waitingForOpenRspRestoreData );
}

/* FUNCTION retryInitiateDialogImsi() 				*/
/*************************************************************************

FUNCTION
	void retryInitiateDialogImsi( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	call 	retryInitiateDialog function with waitingForOpenRspImsi
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/
void
retryInitiateDialogImsi( gblock_t * pgb, pendingReq_t * prq )
{
    retryInitiateDialog( pgb, prq, waitingForOpenRspImsi );
}


/* FUNCTION retryInitiateDialogUpdateGprs() 				*/
/*************************************************************************

FUNCTION
	void retryInitiateDialogUpdateGprs( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	call 	retryInitiateDialog function with waitingForOpenRspUpdateGprs
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/
void
retryInitiateDialogUpdateGprs( gblock_t * pgb, pendingReq_t * prq )
{
    retryInitiateDialog( pgb, prq, waitingForOpenRspUpdateGprs );
}


/* FUNCTION gblockReceived() - event processing for incoming GSM messages */
/*************************************************************************

FUNCTION
    void gblockReceived(gblock_t *pgb)

DESCRIPTION
	It expects to receive an open response
	followed by a sendAuthenticationInfo response and a close.

INPUTS
        Arg:    pgb - a reference to the received gblock_t

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
gblockReceived( gblock_t * pgb )
{
    pendingReq_t   *prq = 0;
    int             i, index = 0;
    int             validInvokeId = -1;
    int             tempIndex = 0, currentDialogId = 0;
    int		    count, vectorsRecd=0;
    int             AC;

    if ( Pglobal->dbg.trace_enabled )
    {
	gMAPPrintGBlock( pgb );
    }

    if ( !( index = hash_get_entry( &dialogId_hash, pgb->dialogId ) ) )
    {

	/*
	 *	Modifications for FR 596, CR 28045
	 *	Send TC_U_REJECT for MAP_CANCEL_LOCATION message with
	 *	new incoming dialog id.
	 */

	if ( pgb->serviceType == GMAP_REQ &&
		pgb->serviceMsg == CANCEL_LOCATION )
	{
		int j,gIndex=-1;

		/*
		 * Find Entry in the Pglobal structure for Remote PC
		 */
		for (j=0; j<Pglobal->sccpAddrCount;j++)
		{
			if (Pglobal->sccpAddrArray[j].REMOTE_PC == pgb->parameter.openArg.originatingAddress.pointCode)
			{
				gIndex=j;
			}
		}
		
 		pgb->serviceType = GMAP_RSP;
    		pgb->serviceMsg = GMAP_OPEN;
    		pgb->bit_mask = gblock_t_parameter_present;
		
		pgb->parameter.openRes.bit_mask |=
			MAP_OpenRes_responderAddress_present;
        	pgb->parameter.openRes.responderAddress.pointCode =
            		pgb->parameter.openArg.destinationAddress.pointCode;
		pgb->parameter.openRes.responderAddress.ssn =
				pgb->parameter.openArg.destinationAddress.ssn;
		pgb->parameter.openRes.result = dialogAccepted;
		if (gIndex == -1)
		{
			pgb->parameter.openRes.responderAddress.routingIndicator = routeOnDpc;
		}
		else
		{
			pgb->parameter.openRes.responderAddress.routingIndicator = Pglobal->sccpAddrArray[gIndex].REMOTE_RI;
		}

		if ( gMAPPutGBlock( pgb ) != 0 )
 		{
 			DBG_DISP( DBG_MASK_ERR, 27, 
 			"gblockReceived: Cannot send GMAP_RSP for dialog %d.\n", 
 			pgb->dialogId );
 		}

		/*
		 *	Send TC_U_REJECT with problem code unrecognized
		 *	operation.
		 */
		gSendUReject(pgb, CP_PT_IP, CP_IP_UR_OP);

 		pgb->serviceType = GMAP_REQ;
    		pgb->serviceMsg = GMAP_DELIMITER;
    		pgb->bit_mask = gblock_t_parameter_present;
    		pgb->parameter.delimiter.qualityOfService = CL_SVC_CLASS_1 | RETURN_ERROR;
		if ( gMAPPutGBlock( pgb ) != 0 )
 		{
 			DBG_DISP( DBG_MASK_ERR, 27, 
 			"gblockReceived: Cannot send reject for dialog %d.\n", 
 			pgb->dialogId );
 		}

 		pgb->serviceType = GMAP_REQ;
    		pgb->serviceMsg = GMAP_U_ABORT;
    		pgb->bit_mask = gblock_t_parameter_present;
    		pgb->parameter.uAbortArg.userReason.choice = 
			MAP_UserAbortChoice_userSpecificReason_chosen;
		if ( gMAPPutGBlock( pgb ) != 0 )
 		{
 			DBG_DISP( DBG_MASK_ERR, 27, 
 			"gblockReceived: Cannot send abort for dialog %d.\n", 
 			pgb->dialogId );
 		}

	}
	else
	{
		DBG_DISP( DBG_MASK_ERR, 27, 
		"gblockReceived: Unknown dialogId received: %d, discarding the message.\n", 
		pgb->dialogId );
	}
	return;
    }
    else
    {
	prq = &PmyTable[index];
    }

    switch ( prq->status )
    {
    case waitingForOpenRspImsi:
	if ( ( pgb->serviceType != GMAP_RSP ) || ( pgb->serviceMsg != GMAP_OPEN ) )
	{
	    DBG_DISP( DBG_MASK_ERR, 928, 
		    "gblockReceived: GMAP_RSP expected, received %d. Discarding the message.\n", 
		    pgb->serviceType );
	    return;
	}

	if ( pgb->parameter.openRes.result == dialogAccepted )
	{
	    prq->status = waitingForInvokeRspImsi;
	}
	else if ( pgb->parameter.openRes.result == dialogRefused && 
		pgb->parameter.openRes.refuseReason == appContextNotSupported )
	{
	    if ( !compareObjectIDs( &pgb->parameter.openRes.applicationContext, &imsiRetrievalContext_v2 ) )
	    {
		DBG_DISP( DBG_MASK_ERR, 929, "Dialog refused, Application Context not supported\n" );
		return;
	    }
	}

	break;

    case waitingForInvokeRspImsi:
	/* check if the invokeId is in the list of open invokes  */
	for ( i = 0; i < 2; i++ )
	{
	    if ( pgb->invokeId == prq->invokeId[i] )
	    {
		validInvokeId = i;
		break;
	    }
	}


	if ( validInvokeId < 0 )
	{
	    DBG_DISP( DBG_MASK_ERR, 930, 
		    "Unexpected invokeId %d received for dialogId %d: , only %d and %d are valid. Discarding the message.\n",
		      pgb->invokeId, pgb->dialogId, prq->invokeId[0], prq->invokeId[1] );
	    return;
	}

	switch ( pgb->serviceType )
	{
	case GMAP_PROVIDER_ERROR:
	    if ( pgb->serviceMsg == NO_RESPONSE )
	    {
		DBG_DISP( DBG_MASK_ERR, 931, 
			"Provider error: No response from peer for dialog %d, requestId %d.\n", 
			prq->dialogId, prq->requestId );

		abortDialog( pgb, prq, NO_CLEAN );

		if ( Pglobal->nbInvokeRetry > prq->invokeRetry[validInvokeId] )
		{
		    DBG_DISP( DBG_MASK_L2, 931, 
			    "No response from the SS7 for dialogID %d, " "retrying\n", 
			    pgb->dialogId );
		    prq->invokeRetry[validInvokeId]++;
		    retryInitiateDialogImsi( pgb, prq );

		}
		else
		{
		    /* we already retried too many times, now
		       we return an error to the client */
		    prq->response.u.imsiResponse.errorCode = ulcm_commError;
		    DBG_DISP( DBG_MASK_L2, 932, 
			    "No response for dialogID %d," " sending commError to the client\n", 
			    pgb->dialogId );
		    sendResponseOnSocket( prq, CLEAN );
		}
	    }
	    else
	    {
		DBG_DISP( DBG_MASK_ERR, 934, 
			"Provide Error: pgb->serviceMsg = %d\n", 
			pgb->serviceMsg );
		abortDialog( pgb, prq, CLEAN );
	    }
	    break;

	case GMAP_RSP:
	    switch ( pgb->serviceMsg )
	    {
	    case SEND_IMSI:
		if ( compareObjectIDs( &prq->applicationContext, &imsiRetrievalContext_v2 ) )
		{
		    memcpy( &prq->response.u.imsiResponse.imsi, 
			    &pgb->parameter.imsi, 
			    sizeof( pgb->parameter.imsi ) );
		}
		else
		{
		    prq->response.u.imsiResponse.errorCode = ulcm_gatewayError;
		    sendResponseOnSocket( prq, CLEAN );
		    DBG_DISP( DBG_MASK_ERR, 421, "Application Context version  not supported \n" );
		    return;
		}

		prq->response.u.imsiResponse.errorCode = ulcm_noError;
		prq->invokeId[validInvokeId] = 0;
		prq->status = waitingForCloseImsi;
		break;

	    default:
		DBG_DISP( DBG_MASK_L2, 935, 
			"Unexpected service message %d. Discarding the message...\n", 
			pgb->serviceMsg );
		return;
		break;
	    }

	    break;

	case GMAP_ERROR:
	    switch ( pgb->serviceMsg )
	    {
		/* sendIMSI ERRORS */
	    case DATA_MISSING:
		DBG_DISP( DBG_MASK_L2, 36, "Error received from the SS7: DATA_MISSING\n" );
		prq->response.u.imsiResponse.errorCode = ulcm_malformedMessage;
		prq->status = waitingForCloseImsi;
		break;
	    case UNEXPECTED_DATA_VALUE:
		DBG_DISP( DBG_MASK_L2, 38, "Error received from the SS7: UNEXPECTED_DATA_VALUE\n" );
		prq->response.u.imsiResponse.errorCode = ulcm_malformedMessage;
		prq->status = waitingForCloseImsi;
		break;
	    case UNKNOWN_SUBSCRIBER:
		DBG_DISP( DBG_MASK_L2, 38, "Error received from the SS7: UNKNOWN_SUBSCRIBER\n" );
		prq->response.u.imsiResponse.errorCode = ulcm_unknownImsi;
		prq->status = waitingForCloseImsi;
		break;
	    default:
		DBG_DISP( DBG_MASK_L2, 40, 
			"Error received from the HLR: Unknown Error %d...\n", 
			pgb->serviceMsg );
		return;
		break;
	    }

	    prq->status = waitingForCloseImsi;
	    break;

	default:
	    DBG_DISP( DBG_MASK_L2, 941, 
		    "Unexpected serviceType %d. Discarding message...\n", 
		    pgb->serviceType );
	    break;
	}
	break;


    case waitingForCloseImsi:
	if ( pgb->serviceMsg != GMAP_CLOSE )
	{
	    DBG_DISP( DBG_MASK_L2, 942, 
		    "Unexpected serviceMsg, GMAP_CLOSE REQ message expected.\n" );
	    return;
	}

	sendResponseOnSocket( prq, CLEAN );
	break;

    case waitingForOpenRspUpdateGprs:
	if ( ( pgb->serviceType != GMAP_RSP ) || ( pgb->serviceMsg != GMAP_OPEN ) )
	{
	    DBG_DISP( DBG_MASK_ERR, 928, 
		    "gblockReceived: GMAP_RSP expected, received %d. Discarding the message.\n", 
		    pgb->serviceType );
	    return;
	}

	if ( pgb->parameter.openRes.result == dialogAccepted )
	{
	    prq->status = waitingForInvokeRspUpdateGprs;
	}
	else if ( pgb->parameter.openRes.result == dialogRefused && 
		pgb->parameter.openRes.refuseReason == appContextNotSupported )
	{
	    if ( !compareObjectIDs( &pgb->parameter.openRes.applicationContext, &gprsLocationUpdateContext_v3 ) )
	    {
		DBG_DISP( DBG_MASK_ERR, 929, "Dialog refused, Application Context not supported\n" );
		return;
	    }
	}

	break;

    case waitingForInvokeRspUpdateGprs:
	/*Delimeters are discarded ... */
	if ( pgb->serviceType == GMAP_REQ && 
		pgb->serviceMsg == GMAP_DELIMITER )
	    break;

	/* check if the invokeId is in the
	   list of open invokes  */
	for ( i = 0; i < 2; i++ )
	{
	    if ( pgb->invokeId == prq->invokeId[i] )
	    {
		validInvokeId = i;
		break;
	    }
	}

	/* InsertSubscriberData is a request. Therefore, it uses its own new invokeId  */
	if ( !( pgb->serviceType == GMAP_REQ && 
		pgb->serviceMsg == INSERT_SUBSCRIBER_DATA ) && 
		( validInvokeId < 0 ) )
	{
	    DBG_DISP( DBG_MASK_ERR, 330,
		      "Unexpected invokeId %d received for dialogId %d: , only %d and %d are valid. Discarding the message.\n",
		      pgb->invokeId, pgb->dialogId, prq->invokeId[0], prq->invokeId[1] );
	    return;
	}

	switch ( pgb->serviceType )
	{

	case GMAP_PROVIDER_ERROR:
	    if ( pgb->serviceMsg == NO_RESPONSE )
	    {
		DBG_DISP( DBG_MASK_ERR, 331, 
			"Provider error: No response from peer for dialog %d, requestId %d.\n", 
			prq->dialogId, prq->requestId );

		abortDialog( pgb, prq, NO_CLEAN );

		if ( Pglobal->nbInvokeRetry > prq->invokeRetry[validInvokeId] )
		{
		    DBG_DISP( DBG_MASK_L2, 331, 
			    "No response from the HLR for dialogID %d, " "retrying\n", 
			    pgb->dialogId );
		    prq->invokeRetry[validInvokeId]++;
		    retryInitiateDialogUpdateGprs( pgb, prq );
		}
		else
		{
		    /* we already retried too many times, now */
		    /*   we return an error to the client     */
		    prq->response.u.updateGprsResponse.errorCode = ulcm_commError;
		    DBG_DISP( DBG_MASK_L2, 332, 
			    "No response from the HLR for dialogID %d," " sending commError to the client\n", 
			    pgb->dialogId );
		    sendResponseOnSocket( prq, CLEAN );
		}
	    }
	    else
	    {
		DBG_DISP( DBG_MASK_ERR, 334, "Provide Error: pgb->serviceMsg = %d\n", pgb->serviceMsg );
		abortDialog( pgb, prq, CLEAN );
	    }
	    break;

	case GMAP_REQ:
	    switch ( pgb->serviceMsg )
	    {
	    case INSERT_SUBSCRIBER_DATA:
		processInsertSubscriberDataGprs( pgb, prq );
		insertSubscriberDataResp( pgb, prq );
		break;
	    case GMAP_DELIMITER:
		DBG_DISP( DBG_MASK_L2, 33455, 
			"Delimiter Received  %d. Discarding the messge...\n", 
			pgb->serviceMsg );
		break;
	    default:
		DBG_DISP( DBG_MASK_L2, 3345, 
			"Unexpected service message %d. Discarding the messge...\n", 
			pgb->serviceMsg );
		return;
	    }

	case GMAP_RSP:
	    switch ( pgb->serviceMsg )
	    {
	    case UPDATE_GPRS_LOCATION:
		prq->response.u.updateGprsResponse.updateGprsLocationRes.bit_mask = 0;
		prq->response.u.updateGprsResponse.updateGprsLocationRes.hlr_Number.length = pgb->parameter.updateGprsLocationRes_v3.hlr_Number.length;
		memcpy( &prq->response.u.updateGprsResponse.updateGprsLocationRes.hlr_Number.value, 
			&pgb->parameter.updateGprsLocationRes_v3.hlr_Number.value,
			pgb->parameter.updateGprsLocationRes_v3.hlr_Number.length );
		if ( ( pgb->parameter.updateGprsLocationRes_v3.bit_mask & UpdateGprsLocationRes_v3_add_Capability_present ) != 0 )
		{
		    prq->response.u.updateGprsResponse.updateGprsLocationRes.bit_mask |= UpdateGprsLocationRes_v3_add_Capability_present;
		    prq->response.u.updateGprsResponse.updateGprsLocationRes.add_Capability = TRUE;
		}
		prq->response.u.updateGprsResponse.errorCode = ulcm_noError;
		prq->invokeId[validInvokeId] = 0;
		prq->status = waitingForCloseUpdateGprs;
		break;
	    case GMAP_DELIMITER:
		DBG_DISP( DBG_MASK_L2, 33455, 
			"Delimiter Received  %d. Discarding the messge...\n", 
			pgb->serviceMsg );
		break;
	    default:
		DBG_DISP( DBG_MASK_L2, 335, 
			"Unexpected service message %d. Discarding the messge...\n", 
			pgb->serviceMsg );
		return;
		break;
	    }
	    break;

	case GMAP_ERROR:
	    switch ( pgb->serviceMsg )
	    {
		/* UpdateGprsLocation ERRORS */
	    case DATA_MISSING:
		DBG_DISP( DBG_MASK_L2, 336, "Error received from the HLR: DATA_MISSING\n" );
	    case SYSTEM_FAILURE:
		DBG_DISP( DBG_MASK_L2, 337, "Error received from the HLR: SYSTEM_FAILURE\n" );
	    case UNEXPECTED_DATA_VALUE:
		DBG_DISP( DBG_MASK_L2, 338, "Error received from the HLR: UNEXPECTED_DATA_VALUE\n" );
		prq->response.u.updateGprsResponse.errorCode = ulcm_malformedMessage;
		prq->status = waitingForCloseUpdateGprs;
		break;
	    case UNKNOWN_SUBSCRIBER:
		DBG_DISP( DBG_MASK_L2, 339, "Error received from the HLR: UNKNOWN_SUBSCRIBER\n" );
		prq->response.u.updateGprsResponse.errorCode = ulcm_unknownImsi;
		prq->status = waitingForCloseUpdateGprs;
		break;
	    default:
		DBG_DISP( DBG_MASK_L2, 340, 
			"Error received from the HLR: Unknown Error %d...\n", 
			pgb->serviceMsg );
		return;
		break;
	    }

	    prq->status = waitingForCloseUpdateGprs;
	    break;

	default:
	    DBG_DISP( DBG_MASK_L2, 341, 
		    "Unexpected serviceType %d. Discarding message...\n", 
		    pgb->serviceType );
	    break;
	}
	break;

    case waitingForCloseUpdateGprs:
	if ( pgb->serviceMsg != GMAP_CLOSE )
	{
	    DBG_DISP( DBG_MASK_L2, 942, 
		    "Unexpected serviceMsg, GMAP_CLOSE REQ message expected.\n" );
	    return;
	}

	sendResponseOnSocket( prq, CLEAN );
	break;

    case waitingForOpenRspTriplets:
	if ( ( pgb->serviceType != GMAP_RSP ) || ( pgb->serviceMsg != GMAP_OPEN ) )
	{
	    DBG_DISP( DBG_MASK_ERR, 28, 
		    "gblockReceived: GMAP_RSP expected, received %d. Discarding the message.\n", 
		    pgb->serviceType );
	    return;
	}

	if ( pgb->parameter.openRes.result == dialogAccepted )
	{
	    prq->status = waitingForInvokeRspTriplets;
	}
	else if ( pgb->parameter.openRes.result == dialogRefused && 
		pgb->parameter.openRes.refuseReason == appContextNotSupported )
	{
	    /* Added code to negotiate application context. */

	    /* Set our application context to the one supported by the other party (HLR) */
	    if ( compareObjectIDs( &pgb->parameter.openRes.applicationContext, &infoRetrievalContext_v3 ) )
	    {
		memcpy( &prq->applicationContext, &infoRetrievalContext_v3, sizeof( ObjectID ) );
	    }
	    else if ( compareObjectIDs( &pgb->parameter.openRes.applicationContext, &infoRetrievalContext_v2 ) )
	    {
		memcpy( &prq->applicationContext, &infoRetrievalContext_v2, sizeof( ObjectID ) );
	    }

	    if ( !( compareObjectIDs( &pgb->parameter.openRes.applicationContext,
				      &infoRetrievalContext_v3 ) ) &&
		 !( compareObjectIDs( &pgb->parameter.openRes.applicationContext, &infoRetrievalContext_v2 ) ) )
	    {
		DBG_DISP( DBG_MASK_ERR, 29, "Dialog refused\n" );
		return;
	    }

	    retryInitiateDialogTriplets( pgb, prq );
	}
	break;

    case waitingForOpenRspRestoreData:
	if ( ( pgb->serviceType != GMAP_RSP ) || ( pgb->serviceMsg != GMAP_OPEN ) )
	{
	    DBG_DISP( DBG_MASK_ERR, 328, 
		    "gblockReceived: GMAP_RSP expected, received %d. Discarding the message.\n", 
		    pgb->serviceType );
	    return;
	}

	if ( pgb->parameter.openRes.result == dialogAccepted )
	{
	    prq->status = waitingForInvokeRspRestoreData;
	}
	else if ( pgb->parameter.openRes.result == dialogRefused && 
		pgb->parameter.openRes.refuseReason == appContextNotSupported )
	{
	    memcpy( &prq->applicationContext, 
		    &pgb->parameter.openRes.applicationContext, 
		    sizeof( pgb->parameter.openRes.applicationContext ) );

	    if ( !( compareObjectIDs( &pgb->parameter.openRes.applicationContext,
				      &networkLocUpContext_v3 ) ) &&
		 !( compareObjectIDs( &pgb->parameter.openRes.applicationContext, &networkLocUpContext_v2 ) ) )
	    {
		DBG_DISP( DBG_MASK_ERR, 129, "Dialog refused\n" );
		return;
	    }

	    retryInitiateDialogRestoreData( pgb, prq );
	}
	break;

    case waitingForInvokeRspTriplets:
	/* check if the invokeId is in the list of open invokes  */
	for ( i = 0; i < 2; i++ )
	{
	    if ( pgb->invokeId == prq->invokeId[i] )
	    {
		validInvokeId = i;
		break;
	    }
	}

	if ( validInvokeId < 0 )
	{
	    DBG_DISP( DBG_MASK_ERR, 30, 
		    "Unexpected invokeId %d received for dialogId %d: , only %d and %d are valid. Discarding the message.\n",
		      pgb->invokeId, pgb->dialogId, prq->invokeId[0], prq->invokeId[1] );
	    return;
	}

	switch ( pgb->serviceType )
	{
	case GMAP_PROVIDER_ERROR:
	    if ( pgb->serviceMsg == NO_RESPONSE )
	    {
		DBG_DISP( DBG_MASK_ERR, 31, 
			"Provider error: No response from peer for dialog %d, requestId %d.\n", 
			prq->dialogId, prq->requestId );

		abortDialog( pgb, prq, NO_CLEAN );

		if ( Pglobal->nbInvokeRetry > prq->invokeRetry[validInvokeId] )
		{
		    DBG_DISP( DBG_MASK_L2, 331, 
			    "No response from the HLR for dialogID %d, " "retrying\n", 
			    pgb->dialogId );
		    prq->invokeRetry[validInvokeId]++;
		    retryInitiateDialogTriplets( pgb, prq );

		}
		else
		{
		    /* we already retried too many times, now
		       we return an error to the client */
		    prq->response.u.authResponse.errorCode = ulcm_commError;
		    DBG_DISP( DBG_MASK_L2, 332, 
			    "No response from the HLR for dialogID %d," " sending commError to the client\n", 
			    pgb->dialogId );
		    sendResponseOnSocket( prq, CLEAN );
		}
	    }
	    else
	    {
		DBG_DISP( DBG_MASK_ERR, 34, "Provide Error: pgb->serviceMsg = %d\n", pgb->serviceMsg );
		abortDialog( pgb, prq, CLEAN );
	    }
	    break;

	case GMAP_RSP:
	    switch ( pgb->serviceMsg )
	    {
	    case SEND_AUTHENTICATION_INFO:
		if ( compareObjectIDs( &prq->applicationContext, &infoRetrievalContext_v3 ) )
		{
		    AC=3;
		}
		else if ( compareObjectIDs( &prq->applicationContext, &infoRetrievalContext_v2 ) )
		{
		    AC=2;
		}
		else
		{
		    prq->response.u.authResponse.errorCode = ulcm_gatewayError;
		    sendResponseOnSocket( prq, CLEAN );
		    DBG_DISP( DBG_MASK_ERR, 421, "Application Context version  not supported \n" );
		    return;
		}
		if (2 == AC)
		{
		    prq->response.u.authResponse.bit_mask =
			ulcm_authenticationSetList_present;
		    prq->response.u.authResponse.authenticationSetList.choice = 
			AuthenticationSetList_t2_tripletList_chosen;
		    memcpy( &prq->response.u.authResponse.authenticationSetList.u.tripletList,
			    &pgb->parameter.sendAuthenticationInfoRes_v2, 
			    sizeof( prq->response.u.authResponse.authenticationSetList.u.tripletList ) );
                    /* If the HLR sends more triplets than what was requested by the radius server, we truncate the response.
                       (Note: If the HLR sends less triplets...the client needs to issue a new request.)
                     */
                    if ( prq->response.u.authResponse.authenticationSetList.u.tripletList.count >
                        prq->request.u.authRequest.numberOfRequestedVectors )
                    {
                        prq->response.u.authResponse.authenticationSetList.u.tripletList.count =
                        prq->request.u.authRequest.numberOfRequestedVectors;
                    }

                    prq->response.u.authResponse.errorCode = ulcm_noError;
                    prq->invokeId[validInvokeId] = 0;
		    prq->status = waitingForCloseTriplets;
		    break;
		}

		/* AC == 3 */
		if (0 == pgb->parameter.sendAuthenticationInfoRes_v3.bit_mask)
		{
		    prq->status = waitingForCloseTriplets;
		    break; /* Nothing to do */
		}
		count =  pgb->parameter.sendAuthenticationInfoRes_v3.\
				authenticationSetList.u.tripletList.count; 
		prq->response.u.authResponse.bit_mask = 
				ulcm_authenticationSetList_present;

		/* How many do we have so far ? */
		vectorsRecd = prq->response.u.authResponse.\
			authenticationSetList.u.quintupletList.count;
                if ((vectorsRecd + count) > 5) /* don't want overflow */ 
		{
		    count = 5 - vectorsRecd;
		}
                prq->response.u.authResponse.authenticationSetList.choice =
			pgb->parameter.sendAuthenticationInfoRes_v3.\
						authenticationSetList.choice;
                if (ulcm_quintupletList_chosen == 
		    prq->response.u.authResponse.authenticationSetList.choice)
		{
	  	    memcpy(&prq->response.u.authResponse.\
		     authenticationSetList.u.quintupletList.value[vectorsRecd],
                     &pgb->parameter.sendAuthenticationInfoRes_v3.\
		     authenticationSetList.u.quintupletList.value[0], 
		     pgb->parameter.sendAuthenticationInfoRes_v3.\
		     authenticationSetList.u.quintupletList.\
		     count*sizeof(ulcm_AuthenticationQuintuplet));

                   prq->response.u.authResponse.authenticationSetList.u.\
					quintupletList.count += count;
		}
		else
                {
                        memcpy(&prq->response.u.authResponse.\
			 authenticationSetList.u.tripletList.value[vectorsRecd],
			 &pgb->parameter.sendAuthenticationInfoRes_v3.\
			 authenticationSetList.u.tripletList.value[0], 
			 pgb->parameter.sendAuthenticationInfoRes_v3.\
			 authenticationSetList.u.tripletList.
			 count*sizeof(ulcm_AuthenticationTriplet));

                    prq->response.u.authResponse.authenticationSetList.u.\
					tripletList.count += count;
                }


		/* If the HLR sends more triplets than what was requested by the radius server,
		   we truncate the response.
		   (Note: If the HLR sends less triplets...the client needs to issue a new request.)
		 */
		if ( prq->response.u.authResponse.authenticationSetList.u.tripletList.count > 
			prq->request.u.authRequest.numberOfRequestedVectors )
		{
		    prq->response.u.authResponse.authenticationSetList.u.tripletList.count = 
			prq->request.u.authRequest.numberOfRequestedVectors;
		}
		if ( prq->response.u.authResponse.authenticationSetList.u.tripletList.count >= 
			prq->request.u.authRequest.numberOfRequestedVectors )
	       {
		    /* We're done. If the HLR does not send us a CLOSE
		     * in the waitingForCloseTriplets state, then we'll
		     * send the CLOSE.
		     */
		    prq->response.u.authResponse.errorCode = ulcm_noError;
		    prq->invokeId[validInvokeId] = 0;
		}
		prq->status = waitingForCloseTriplets;
		break;


	    default:
		DBG_DISP( DBG_MASK_L2, 35, 
			"Unexpected service message %d. Discarding the messge...\n", 
			pgb->serviceMsg );
		return;
		break;
	    }
	    break;

	case GMAP_ERROR:
	    switch ( pgb->serviceMsg )
	    {
		/* SendAuthenticationInfo ERRORS */
	    case DATA_MISSING:
		DBG_DISP( DBG_MASK_L2, 36, "Error received from the HLR: DATA_MISSING\n" );
	    case SYSTEM_FAILURE:
		DBG_DISP( DBG_MASK_L2, 37, "Error received from the HLR: SYSTEM_FAILURE\n" );
	    case UNEXPECTED_DATA_VALUE:
		DBG_DISP( DBG_MASK_L2, 38, "Error received from the HLR: UNEXPECTED_DATA_VALUE\n" );
		prq->response.u.authResponse.errorCode = ulcm_malformedMessage;
		prq->status = waitingForCloseTriplets;
		break;
	    case UNKNOWN_SUBSCRIBER:
		DBG_DISP( DBG_MASK_L2, 39, "Error received from the HLR: UNKNOWN_SUBSCRIBER\n" );
		prq->response.u.authResponse.errorCode = ulcm_unknownImsi;
		prq->status = waitingForCloseTriplets;
		break;
	    default:
		DBG_DISP( DBG_MASK_L2, 40, 
			"Error received from the HLR: Unknown Error %d...\n", 
			pgb->serviceMsg );
		return;
		break;
	    }

	    /* needed only if the error doesn't come in a CLOSE
	       abortDialog(pgb, prq, CLEAN); */

	    prq->status = waitingForCloseTriplets;
	    break;
	default:
	    DBG_DISP( DBG_MASK_L2, 41, 
		    "Unexpected serviceType %d. Discarding message...\n", 
		    pgb->serviceType );
	    break;
	}
	break;

    case waitingForInvokeRspRestoreData:
	/*Delimeters are discarded ... */
	if ( pgb->serviceType == GMAP_REQ && 
		pgb->serviceMsg == GMAP_DELIMITER )
	    break;

	/* check if the invokeId is in the
	   list of open invokes  */
	for ( i = 0; i < 2; i++ )
	{
	    if ( pgb->invokeId == prq->invokeId[i] )
	    {
		validInvokeId = i;
		break;
	    }
	}

	/* InsertSubscriberData is a request. Therefore, it uses its own new invokeId  */
	if ( !( pgb->serviceType == GMAP_REQ && 
		pgb->serviceMsg == INSERT_SUBSCRIBER_DATA ) && 
		( validInvokeId < 0 ) )
	{
	    DBG_DISP( DBG_MASK_ERR, 330,
		      "Unexpected invokeId %d received for dialogId %d: , only %d and %d are valid. Discarding the message.\n",
		      pgb->invokeId, pgb->dialogId, prq->invokeId[0], prq->invokeId[1] );
	    return;
	}

	switch ( pgb->serviceType )
	{

	case GMAP_PROVIDER_ERROR:
	    if ( pgb->serviceMsg == NO_RESPONSE )
	    {
		DBG_DISP( DBG_MASK_ERR, 331, 
			"Provider error: No response from peer for dialog %d, requestId %d.\n", 
			prq->dialogId, prq->requestId );

		abortDialog( pgb, prq, NO_CLEAN );

		if ( Pglobal->nbInvokeRetry > prq->invokeRetry[validInvokeId] )
		{
		    DBG_DISP( DBG_MASK_L2, 331, 
			    "No response from the HLR for dialogID %d, " "retrying\n", 
			    pgb->dialogId );
		    prq->invokeRetry[validInvokeId]++;
		    retryInitiateDialogRestoreData( pgb, prq );
		}
		else
		{
		    /* we already retried too many times, now */
		    /*   we return an error to the client     */
		    prq->response.u.authResponse.errorCode = ulcm_commError;
		    DBG_DISP( DBG_MASK_L2, 332, 
			    "No response from the HLR for dialogID %d," " sending commError to the client\n", 
			    pgb->dialogId );
		    sendResponseOnSocket( prq, CLEAN );
		}
	    }
	    else
	    {
		DBG_DISP( DBG_MASK_ERR, 334, "Provide Error: pgb->serviceMsg = %d\n", pgb->serviceMsg );
		abortDialog( pgb, prq, CLEAN );
	    }
	    break;

	case GMAP_REQ:
	    switch ( pgb->serviceMsg )
	    {
	    case INSERT_SUBSCRIBER_DATA:
		processInsertSubscriberData( pgb, prq );
		insertSubscriberDataResp( pgb, prq );
		break;
	    case GMAP_DELIMITER:
		DBG_DISP( DBG_MASK_L2, 33455, 
			"Delimiter Received  %d. Discarding the messge...\n", 
			pgb->serviceMsg );
		break;
	    default:
		DBG_DISP( DBG_MASK_L2, 3345, 
			"Unexpected service message %d. Discarding the messge...\n", 
			pgb->serviceMsg );
		return;
	    }

	case GMAP_RSP:
	    switch ( pgb->serviceMsg )
	    {
	    case RESTORE_DATA:
                generateServiceString(pgb,prq);
		prq->response.u.authResponse.errorCode = ulcm_noError;
		prq->invokeId[validInvokeId] = 0;
		prq->status = waitingForCloseRestoreData;
		break;
	    case GMAP_DELIMITER:
		DBG_DISP( DBG_MASK_L2, 33455, 
			"Delimiter Received  %d. Discarding the messge...\n", 
			pgb->serviceMsg );
		break;
	    default:
		DBG_DISP( DBG_MASK_L2, 335, 
			"Unexpected service message %d. Discarding the messge...\n", 
			pgb->serviceMsg );
		return;
		break;
	    }
	    break;

	case GMAP_ERROR:
	    switch ( pgb->serviceMsg )
	    {
		/* RestoreData ERRORS */
	    case DATA_MISSING:
		DBG_DISP( DBG_MASK_L2, 336, "Error received from the HLR: DATA_MISSING\n" );
	    case SYSTEM_FAILURE:
		DBG_DISP( DBG_MASK_L2, 337, "Error received from the HLR: SYSTEM_FAILURE\n" );
	    case UNEXPECTED_DATA_VALUE:
		DBG_DISP( DBG_MASK_L2, 338, "Error received from the HLR: UNEXPECTED_DATA_VALUE\n" );
		prq->response.u.authResponse.errorCode = ulcm_malformedMessage;
		prq->status = waitingForCloseRestoreData;
		break;
	    case UNKNOWN_SUBSCRIBER:
		DBG_DISP( DBG_MASK_L2, 339, "Error received from the HLR: UNKNOWN_SUBSCRIBER\n" );
		prq->response.u.authResponse.errorCode = ulcm_unknownImsi;
		prq->status = waitingForCloseRestoreData;
		break;
	    default:
		DBG_DISP( DBG_MASK_L2, 340, 
			"Error received from the HLR: Unknown Error %d...\n", 
			pgb->serviceMsg );
		return;
		break;
	    }

	    prq->status = waitingForCloseRestoreData;
	    break;

	default:
	    DBG_DISP( DBG_MASK_L2, 341, 
		    "Unexpected serviceType %d. Discarding message...\n", 
		    pgb->serviceType );
	    break;
	}
	break;

    case waitingForCloseTriplets:
	if ( pgb->serviceMsg == GMAP_DELIMITER )
	{
            /* If we do not have the requested # of vectors, then send another 
	     * request in a TC-CONTINUE 
	     */
	    if ( prq->response.u.authResponse.authenticationSetList.u.tripletList.count < 
			prq->request.u.authRequest.numberOfRequestedVectors )
	       {
		    sendAnotherServiceMsg(pgb, prq); /* Send a TC-CONTINUE */
	            /* Go back to the old state */
	            prq->status = waitingForInvokeRspTriplets;
		}
		else /* close the dialog */
		{
		    endDialog(pgb, prq, CLEAN);
		}
	     break;
	}
        
	else if ( pgb->serviceMsg != GMAP_CLOSE )
	{
	    DBG_DISP( DBG_MASK_L2, 42, 
		    "Unexpected serviceMsg, GMAP_CLOSE REQ message expected.\n" );

	    return;
	}

	/* If there was an error during the authentication or
	   if no authorization is required (no bs or ts configured) we send the
	   response on to the client library and don't do the restoreData
	 */

	if ( prq->response.u.authResponse.errorCode != ulcm_noError )
	{
	    sendResponseOnSocket( prq, CLEAN );
	}
	else 
	{
	    prq->response.u.authResponse.serviceFound = 1;
/*                strcpy(&prq->response.u.authResponse.serviceList[0],
                                "NoAuthorizationCheck");

*/
	    sendResponseOnSocket( prq, CLEAN );

	}

	break;

    case waitingForCloseRestoreData:
	if ( pgb->serviceMsg != GMAP_CLOSE )
	{
	    DBG_DISP( DBG_MASK_L2, 42, 
		    "Unexpected serviceMsg, GMAP_CLOSE REQ message expected.\n" );

	    return;
	}

	if ( prq->response.u.authResponse.errorCode != ulcm_noError )
	{
	    sendResponseOnSocket( prq, CLEAN );
	}
	/* if no service was found, set the error code to UserNotAuthorized */
	else if ( prq->response.u.authResponse.serviceFound == 0 )
	{
	    prq->response.u.authResponse.errorCode = ulcm_userNotAuthorized;
	    sendResponseOnSocket( prq, CLEAN );
	}
	else
	{
	    sendResponseOnSocket( prq, CLEAN );
	}
	break;

    case canceled:
	if ( ( pgb->serviceMsg == GMAP_CLOSE ) || ( pgb->serviceType == GMAP_PROVIDER_ERROR ) )
	{
		if ( ( pgb->serviceType == GMAP_PROVIDER_ERROR ) && ( pgb->serviceMsg == NO_RESPONSE ) )
		{
		abortDialog( pgb, prq, NO_CLEAN );
		}
	    cleanHashTables( prq );
	    /* re-initializes this entry in the table  */
	    memset( prq, 0, sizeof( *prq ) );
	}
	else if ( pgb->serviceMsg == INSERT_SUBSCRIBER_DATA )
	{
	    /* If the message is an InsertSubscriberDataArg, the application
	       should send a InsertSubscriberDataRes so the other party
	       can eventually close the dialog */

	    insertSubscriberDataResp( pgb, prq );
	}
	else
	{
	    DBG_DISP( DBG_MASK_L2, 43, 
		    "Message discarded, the request has been canceled by the client.\n" );
	}
	break;

    default:
		DBG_DISP( DBG_MASK_ERR, 
			61, 
			"Status value out of range, value=%d\n", 
			prq->status);
	break;
    }				/* switch status */
}
/* FUNCTION processInsertSubscriberDataODB_v2() 			*/
/*************************************************************************

FUNCTION
	void processInsertSubscriberDataODB_v2( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data Operator Determined Barring v2.
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/
void
processInsertSubscriberDataODB_v2( gblock_t * pgb, pendingReq_t * prq )
{
    int             k = 0;
	
	for ( k = 0; k < Pglobal->sccpAddrArray[prq->confIndex].odbCodesCount; k++ )
	{
    if ( Pglobal->dbg.trace_enabled )
    {
	wlanPrint(&world, "Operator Determined Barring (ODB) Data %d \n", 
		pgb->parameter.insertSubscriberDataArg_v2.odb_Data.odb_GeneralData.value[ODB_GeneralData_allOG_CallsBarred_byte] );
    }

    if ( Pglobal->dbg.trace_enabled )
    {
	wlanPrint2(&world, " HLR ODB : %u ... configured ODB : %u\n",
		  pgb->parameter.insertSubscriberDataArg_v2.odb_Data.odb_GeneralData.value[ODB_GeneralData_allOG_CallsBarred_byte], 
		  Pglobal->sccpAddrArray[prq->confIndex].odbCodes[k] );
    }

    /*  If the ODB data received is equal to the ODB data configured */
    if ( pgb->parameter.insertSubscriberDataArg_v2.odb_Data.odb_GeneralData.value[ODB_GeneralData_allOG_CallsBarred_byte] == 
	    Pglobal->sccpAddrArray[prq->confIndex].odbCodes[k] )
    {
	/* if it is the first svc of the list we don't write the separator */
	if ( prq->response.u.authResponse.serviceFound != 0 )
	{
	    strcat( &prq->response.u.authResponse.serviceList[0], ":" );
	    strcat( &prq->response.u.authResponse.serviceList[0], 
		    Pglobal->sccpAddrArray[prq->confIndex].odbString[k] );
	}
	else
	{
	    strcpy( &prq->response.u.authResponse.serviceList[0], 
		    Pglobal->sccpAddrArray[prq->confIndex].odbString[k] );
	}

	prq->response.u.authResponse.serviceFound = 1;
	break;
    }
	}
}

/* FUNCTION processInsertSubscriberDataODB_v3() 			*/
/*************************************************************************

FUNCTION
	void processInsertSubscriberDataODB_v3( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data Operator Determined Barring v3.
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/

void
processInsertSubscriberDataODB_v3( gblock_t * pgb, pendingReq_t * prq )
{
    int             k = 0;
    
	for ( k = 0; k < Pglobal->sccpAddrArray[prq->confIndex].odbCodesCount; k++ )
	{
    if ( Pglobal->dbg.trace_enabled )
    {
	wlanPrint(&world, "Operator Determined Barring (ODB) Data %d \n", 
		pgb->parameter.insertSubscriberDataArg_v3.odb_Data.odb_GeneralData.value[ODB_GeneralData_t2_allOG_CallsBarred_byte] );
    }

    if ( Pglobal->dbg.trace_enabled )
	wlanPrint2(&world, " HLR ODB %d : configured ODB %d\n",
		  pgb->parameter.insertSubscriberDataArg_v3.odb_Data.odb_GeneralData.value[ODB_GeneralData_t2_allOG_CallsBarred_byte], 
		  Pglobal->sccpAddrArray[prq->confIndex].odbCodes[k] );

    /*  If the ODB data received is equal to the ODB data configured */
    if ( pgb->parameter.insertSubscriberDataArg_v3.odb_Data.odb_GeneralData.value[ODB_GeneralData_t2_allOG_CallsBarred_byte] == 
	    Pglobal->sccpAddrArray[prq->confIndex].odbCodes[k] )
    {
	/* if it is the first svc of the list we don't write the separator */
	if ( prq->response.u.authResponse.serviceFound != 0 )
	{
	    strcat( &prq->response.u.authResponse.serviceList[0], ":" );
	    strcat( &prq->response.u.authResponse.serviceList[0], 
		    Pglobal->sccpAddrArray[prq->confIndex].odbString[k] );
	}
	else
	{
	    strcpy( &prq->response.u.authResponse.serviceList[0], 
		    Pglobal->sccpAddrArray[prq->confIndex].odbString[k] );
	}

	prq->response.u.authResponse.serviceFound = 1;
	break;
    }
	}
}

/* FUNCTION processInsertSubscriberDataBS_v2() 				*/
/*************************************************************************

FUNCTION
	void processInsertSubscriberDataBS_v2( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data Bearer Service v2.
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/
void
processInsertSubscriberDataBS_v2( gblock_t * pgb, pendingReq_t * prq )
{
    int             i = 0;
    int             count = 0;
    int             k = 0;

    if ( Pglobal->dbg.trace_enabled )
    {
	wlanPrint(&world, "Bearer Service List Count %d \n", 
		pgb->parameter.insertSubscriberDataArg_v2.bearerServiceList.count );
    }

//    printf("processInsertSubscriberDataBS_v2: ******   enter   **** requestId=%d\n", prq->requestId);
    count = pgb->parameter.insertSubscriberDataArg_v2.bearerServiceList.count;

    if ( count > 5 )
	count = 5;

    for ( i = 0; i < count; i++ )
    {



	for ( k = 0; k < Pglobal->sccpAddrArray[prq->confIndex].bsCodesCount; k++ )
	{
	    if ( Pglobal->dbg.trace_enabled )
		wlanPrint4(&world, " HLR BS %d : %d ... configured BS %d : %d\n", i,
			  pgb->parameter.insertSubscriberDataArg_v2.bearerServiceList.value[i].value[0], 
			  k, Pglobal->sccpAddrArray[prq->confIndex].bsCodes[k] );

	    /*  If the BS received is equal to the BS configured or
	       equals to the "All XXX services" represented by the first 5 bits of the BS or
	       if it is equal to 0 "All BS services " (see definition of BS values in MAP spec)
	     */
	    if ( ( pgb->parameter.insertSubscriberDataArg_v2.bearerServiceList.value[i].value[0] ==
		   Pglobal->sccpAddrArray[prq->confIndex].bsCodes[k] ) ||
		 ( pgb->parameter.insertSubscriberDataArg_v2.bearerServiceList.value[i].value[0] ==
		   ( Pglobal->sccpAddrArray[prq->confIndex].bsCodes[k] & 0xf8 ) ) ||
		 ( pgb->parameter.insertSubscriberDataArg_v2.bearerServiceList.value[i].value[0] == 0 ) )

	    {
//		printf("processInsertSubscriberDataBS_v2: requestId=%d\n", prq->requestId);
//		printf("processInsertSubscriberDataBS_v2: prq->confIndex=%d, k=%d\n", prq->confIndex, k);
              /* We found that the service is provisionned */
              prq->bserviceProvisioned[k] = TRUE;
	
	    }
	}

    }
}

/* FUNCTION processInsertSubscriberDataBS_v3() 				*/
/*************************************************************************

FUNCTION
	void processInsertSubscriberDataBS_v3( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data Bearer Service v3.
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/

void
processInsertSubscriberDataBS_v3( gblock_t * pgb, pendingReq_t * prq )
{
    int             i = 0, count = 0;
    int             k = 0;

    count = pgb->parameter.insertSubscriberDataArg_v3.bearerServiceList.count;

    for ( i = 0; i < count; i++ )
    {

	for ( k = 0; k < Pglobal->sccpAddrArray[prq->confIndex].bsCodesCount; k++ )
	{
	    if ( Pglobal->dbg.trace_enabled )
		wlanPrint4(&world, " HLR BS %d : %d ... configured BS %d : %d\n", i,
			  pgb->parameter.insertSubscriberDataArg_v3.bearerServiceList.value[i].value[0], 
			  k, Pglobal->sccpAddrArray[prq->confIndex].bsCodes[k] );

	    /*  If the BS received is equal to the BS configured or
	       equals to the "All XXX services" represented by the first 5 bits of the BS or
	       if it is equal to 0 "All BS services " (see definition of BS values in MAP spec)
	     */
	    if ( ( pgb->parameter.insertSubscriberDataArg_v3.bearerServiceList.value[i].value[0] ==
		   Pglobal->sccpAddrArray[prq->confIndex].bsCodes[k] ) ||
		 ( pgb->parameter.insertSubscriberDataArg_v3.bearerServiceList.value[i].value[0] ==
		   ( Pglobal->sccpAddrArray[prq->confIndex].bsCodes[k] & 0xf8 ) ) ||
		 ( pgb->parameter.insertSubscriberDataArg_v3.bearerServiceList.value[i].value[0] == 0 ) )

	    {
		//printf("processInsertSubscriberDataBS_v3: requestId=%d\n", prq->requestId);
                prq->bserviceProvisioned[k] = TRUE;	
	    }
	}

    }
}

/* FUNCTION processInsertSubscriberDataTS_v3() 				*/
/*************************************************************************

FUNCTION
	void processInsertSubscriberDataTS_v3( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data Tele Service v3.
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/

void
processInsertSubscriberDataTS_v3( gblock_t * pgb, pendingReq_t * prq )
{
    int             i = 0, count = 0;
    int             k = 0;

    count = pgb->parameter.insertSubscriberDataArg_v3.teleserviceList.count;

    for ( i = 0; i < count; i++ )
    {

	for ( k = 0; k < Pglobal->sccpAddrArray[prq->confIndex].tsCodesCount; k++ )
	{
	    if ( Pglobal->dbg.trace_enabled )
		wlanPrint4(&world, "HLR TS %d : %d ... configured TS %d : %d\n", i,
			  pgb->parameter.insertSubscriberDataArg_v3.teleserviceList.value[i].value[0], 
			  k, Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] );

	    /*
	       The teleservice received from the HLR is equal to the one configured OR
	       it is equals to the "All XXX services" represented by the first 4 bits of the TS OR
	       it is equal to "allTeleservices-ExeptSMS" (0x80) AND the TS configured is not SMS (0x21 or 0x22) OR
	       it is equal to "allDataTeleservices" (0x70) AND the TS configured is either 0x2* OR  0x6*
	     */
	    if ( ( pgb->parameter.insertSubscriberDataArg_v3.teleserviceList.value[i].value[0] ==
		   Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] )
		 || ( pgb->parameter.insertSubscriberDataArg_v3.teleserviceList.value[i].value[0] ==
		      ( Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] & 0xf0 ) )
		 || ( pgb->parameter.insertSubscriberDataArg_v3.teleserviceList.value[i].value[0] == 0 )
		 || ( ( pgb->parameter.insertSubscriberDataArg_v3.teleserviceList.value[i].value[0] ==
			0x80 )
		      && ( ( Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] & 0xf0 ) != 0x20 ) )
		 || ( ( pgb->parameter.insertSubscriberDataArg_v3.teleserviceList.value[i].value[0] ==
			0x70 )
		      && ( ( ( Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] & 0xf0 ) != 0x20 )
			   || ( ( Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] & 0xf0 ) != 0x60 ) ) ) )
	    {
		prq->tserviceProvisioned[k] = TRUE;
	    }
	}

    }
}

/* FUNCTION processInsertSubscriberDataTS_v2() 				*/
/*************************************************************************

FUNCTION
	void processInsertSubscriberDataTS_v2( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data Tele Service v2.
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/

void
processInsertSubscriberDataTS_v2( gblock_t * pgb, pendingReq_t * prq )
{
    int             i = 0, count = 0;
    int             k = 0;

    count = pgb->parameter.insertSubscriberDataArg_v2.teleserviceList.count;

    for ( i = 0; i < count; i++ )
    {

	for ( k = 0; k < Pglobal->sccpAddrArray[prq->confIndex].tsCodesCount; k++ )
	{
	    if ( Pglobal->dbg.trace_enabled )
		wlanPrint4(&world, "HLR TS %d : %d ... configured TS %d : %d\n", i,
			  pgb->parameter.insertSubscriberDataArg_v2.teleserviceList.value[i].value[0], 
			  k, Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] );

	    /*
	       The teleservice received from the HLR is equal to the one configured OR
	       it is equals to the "All XXX services" represented by the first 4 bits of the TS OR
	       it is equal to "allTeleservices-ExeptSMS" (0x80) AND the TS configured is not SMS (0x21 or 0x22) OR
	       it is equal to "allDataTeleservices" (0x70) AND the TS configured is either 0x2* OR  0x6*
	     */
	    if ( ( pgb->parameter.insertSubscriberDataArg_v2.teleserviceList.value[i].value[0] ==
		   Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] )
		 || ( pgb->parameter.insertSubscriberDataArg_v2.teleserviceList.value[i].value[0] ==
		      ( Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] & 0xf0 ) )
		 || ( pgb->parameter.insertSubscriberDataArg_v2.teleserviceList.value[i].value[0] == 0 )
		 || ( ( pgb->parameter.insertSubscriberDataArg_v2.teleserviceList.value[i].value[0] ==
			0x80 )
		      && ( ( Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] & 0xf0 ) != 0x20 ) )
		 || ( ( pgb->parameter.insertSubscriberDataArg_v2.teleserviceList.value[i].value[0] ==
			0x70 )
		      && ( ( ( Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] & 0xf0 ) != 0x20 )
			   || ( ( Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k] & 0xf0 ) != 0x60 ) ) ) )
	    {
		prq->tserviceProvisioned[k] = TRUE;
	    }
	}

    }
}

/* FUNCTION processInsertSubscriberData_provisionedSS_v2()		*/
/*************************************************************************

FUNCTION
	void processInsertSubscriberData_provisionedSS_v2( gblock_t * pgb, 
				pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data  provisioned SS v2.
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/

void
processInsertSubscriberData_provisionedSS_v2(gblock_t *pgb,pendingReq_t *prq)
{
  int i, j, k;
  int baoc = 0x92;

  for (i=0; i<pgb->parameter.insertSubscriberDataArg_v2.provisionedSS.count; i++)
  {
    if (pgb->parameter.insertSubscriberDataArg_v2.
                  provisionedSS.value[i].u.callBarringInfo.ss_Code.value[0] !=  baoc)
    {
        /* We need to check only the services that have the baoc flag set */
        continue;
    }

    if (pgb->parameter.insertSubscriberDataArg_v2.provisionedSS.value[i].choice ==
                                                    SS_Info_callBarringInfo_chosen)
    {
        for (j=0; j<pgb->parameter.insertSubscriberDataArg_v2.provisionedSS.
             value[i].u.callBarringInfo.callBarringFeatureList.count;j++)
        {
            if ((pgb->parameter.insertSubscriberDataArg_v2.provisionedSS.value[i].
                 u.callBarringInfo.callBarringFeatureList.value[j].bit_mask &
                 CallBarringFeature_t2_basicService_present) ==
                 CallBarringFeature_t2_basicService_present)
            {
                if (pgb->parameter.insertSubscriberDataArg_v2.provisionedSS.
                    value[i].u.callBarringInfo.callBarringFeatureList.
                    value[j].basicService.choice ==
                    BasicServiceCode_bearerService_chosen)
                {

                    for (k=0; k<Pglobal->sccpAddrArray[prq->confIndex].bsCodesCount;
                         k++)
                    {
                        if (pgb->parameter.insertSubscriberDataArg_v2.provisionedSS.
                            value[i].u.callBarringInfo.callBarringFeatureList.
                            value[j].basicService.u.bearerService.value[0] ==
                            Pglobal->sccpAddrArray[prq->confIndex].bsCodes[k])
                        {
                            if ((pgb->parameter.insertSubscriberDataArg_v2.
                                 provisionedSS.value[i].u.callBarringInfo.bit_mask &
                                 CallBarringInfo_t2_ss_Code_present) ==
                                 CallBarringInfo_t2_ss_Code_present)
                            Pglobal->sccpAddrArray[prq->confIndex].bs_ss_code[k] =
                            pgb->parameter.insertSubscriberDataArg_v2.
                            provisionedSS.value[i].u.callBarringInfo.ss_Code.value[0];
                        }
                    } /* for k loop */
                } else if (pgb->parameter.insertSubscriberDataArg_v2.provisionedSS.
                           value[i].u.callBarringInfo.callBarringFeatureList.
                           value[j].basicService.choice ==
                           BasicServiceCode_teleservice_chosen)
                {
                    for (k=0; k<Pglobal->sccpAddrArray[prq->confIndex].tsCodesCount;
                         k++)
                    {
                        if (pgb->parameter.insertSubscriberDataArg_v2.provisionedSS.
                            value[i].u.callBarringInfo.callBarringFeatureList.
                            value[j].basicService.u.teleservice.value[0] ==
                            Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k])
                        {
                            if ((pgb->parameter.insertSubscriberDataArg_v2.
                                 provisionedSS.value[i].u.callBarringInfo.bit_mask &
                                 CallBarringInfo_t2_ss_Code_present) ==
                                 CallBarringInfo_t2_ss_Code_present)
                            Pglobal->sccpAddrArray[prq->confIndex].ts_ss_code[k] =
                            pgb->parameter.insertSubscriberDataArg_v2.
                            provisionedSS.value[i].u.callBarringInfo.ss_Code.value[0];
                        }
                    } /* for k loop */
                }
            }
       } /* for j loop */
    }
  } /* for i loop */
}

/* FUNCTION processInsertSubscriberData_provisionedSS_v3()		*/
/*************************************************************************


FUNCTION
	void processInsertSubscriberData_provisionedSS_v3( gblock_t * pgb, 
				pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data  provisioned SS v3.
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/

void
processInsertSubscriberData_provisionedSS_v3(gblock_t *pgb,pendingReq_t *prq)
{
  int i, j, k;
  int baoc = 0x92;

  for (i=0; i<pgb->parameter.insertSubscriberDataArg_v3.provisionedSS.count; i++)
  {
    if (pgb->parameter.insertSubscriberDataArg_v3.
                  provisionedSS.value[i].u.callBarringInfo.ss_Code.value[0] != baoc)
    {
        /* We need to check only the services that have the baoc flag set */
        continue;
    }

    if (pgb->parameter.insertSubscriberDataArg_v3.provisionedSS.value[i].choice ==
                                                Ext_SS_Info_callBarringInfo_chosen)
    {
        for (j=0; j<pgb->parameter.insertSubscriberDataArg_v3.
          provisionedSS.value[i].u.callBarringInfo.callBarringFeatureList.count;j++)
        {
            if ((pgb->parameter.insertSubscriberDataArg_v3.provisionedSS.value[i].
                 u.callBarringInfo.callBarringFeatureList.value[j].bit_mask &
                 Ext_CallBarringFeature_basicService_present) ==
                 Ext_CallBarringFeature_basicService_present)
            {
                if (pgb->parameter.insertSubscriberDataArg_v3.provisionedSS.
                    value[i].u.callBarringInfo.callBarringFeatureList.value[j].
                    basicService.choice ==
                    Ext_BasicServiceCode_ext_BearerService_chosen)
                {
                    for (k=0; k<Pglobal->sccpAddrArray[prq->confIndex].bsCodesCount; k++)
                    {
                        if (pgb->parameter.insertSubscriberDataArg_v3.provisionedSS.
                            value[i].u.callBarringInfo.callBarringFeatureList.
                            value[j].basicService.u.ext_BearerService.value[0] ==
                            Pglobal->sccpAddrArray[prq->confIndex].bsCodes[k])
                        {
                            Pglobal->sccpAddrArray[prq->confIndex].bs_ss_code[k] =
                            pgb->parameter.insertSubscriberDataArg_v3.provisionedSS.
                            value[i].u.callBarringInfo.ss_Code.value[0];
                        }
                    } /* for k loop */
                } else if (pgb->parameter.insertSubscriberDataArg_v3.provisionedSS.
                           value[i].u.callBarringInfo.callBarringFeatureList.
                           value[j].basicService.choice ==
                           Ext_BasicServiceCode_ext_Teleservice_chosen)
                {
                    for (k=0; k<Pglobal->sccpAddrArray[prq->confIndex].tsCodesCount; k++)
                    {
                        if (pgb->parameter.insertSubscriberDataArg_v3.provisionedSS.
                            value[i].u.callBarringInfo.callBarringFeatureList.
                            value[j].basicService.u.ext_Teleservice.value[0] ==
                            Pglobal->sccpAddrArray[prq->confIndex].tsCodes[k])
                        {
                            Pglobal->sccpAddrArray[prq->confIndex].ts_ss_code[k] =
                            pgb->parameter.insertSubscriberDataArg_v3.provisionedSS.
                            value[i].u.callBarringInfo.ss_Code.value[0];
                        }
                    } /* for k loop */
                }
            }
        } /* for j loop */
    }
  } /* for i loop */
}

/* FUNCTION processInsertSubscriberData()				*/
/*************************************************************************

FUNCTION
	void processInsertSubscriberData( gblock_t * pgb, 
				pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data 
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/

void
processInsertSubscriberData( gblock_t * pgb, pendingReq_t * prq )
{
    if ( compareObjectIDs( &prq->applicationContext, &networkLocUpContext_v3 ) )
    {
	if ( ( pgb->parameter.insertSubscriberDataArg_v3.bit_mask &
	       InsertSubscriberDataArg_v3_bearerServiceList_present ) == 
		InsertSubscriberDataArg_v3_bearerServiceList_present )
	{

	    processInsertSubscriberDataBS_v3( pgb, prq );
	}

	if ( ( pgb->parameter.insertSubscriberDataArg_v3.bit_mask &
	       InsertSubscriberDataArg_v3_teleserviceList_present ) == 
		InsertSubscriberDataArg_v3_teleserviceList_present )
	{
	    processInsertSubscriberDataTS_v3( pgb, prq );
	}

	if ( ( pgb->parameter.insertSubscriberDataArg_v3.bit_mask &
	       InsertSubscriberDataArg_v3_odb_Data_present ) == 
		InsertSubscriberDataArg_v3_odb_Data_present )
	{
	    processInsertSubscriberDataODB_v3( pgb, prq );
	}

	if ( ( pgb->parameter.insertSubscriberDataArg_v3.bit_mask &
	       InsertSubscriberDataArg_v3_provisionedSS_present ) == 
		InsertSubscriberDataArg_v3_provisionedSS_present )
	{
          processInsertSubscriberData_provisionedSS_v3(pgb,prq);
    } 
	if ((pgb->parameter.insertSubscriberDataArg_v3.bit_mask & InsertSubscriberDataArg_v3_vlrCamelSubscriptionInfo_present) ==
		InsertSubscriberDataArg_v3_vlrCamelSubscriptionInfo_present ){
		prq->response.u.authResponse.vlrCamelSubscriptionInfo.vlrCamelSubscriptionInfoPresent = 1;
		printf("<<<<<<<<<<<<<<VLR camel subscription info present>>>>>>>>>>>>>>>>");
	} else {
		prq->response.u.authResponse.vlrCamelSubscriptionInfo.vlrCamelSubscriptionInfoPresent = 0;
		printf("<<<<<<<<<<<<<<VLR camel subscription info not present>>>>>>>>>>>>>>>>");
	}

        if((pgb->parameter.insertSubscriberDataArg_v3.bit_mask &
               InsertSubscriberDataArg_v3_msisdn_present)
               == InsertSubscriberDataArg_v3_msisdn_present)
        {
		printf("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<msisdn condition satifishfied for V3>>>>>>>>>>>>>>>>>>>\n"); 
		gMAPPrintGBlock( pgb );
		printf("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<msisdn condition satifishfied for V3>>>>>>>>>>>>>>>>>>>\n");
		printf("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<length : %d>>>>>>>>>>>>>>>>>>>\n",pgb->parameter.insertSubscriberDataArg_v3.msisdn.length);
		if(msisdnlength> 0){
		    int offset = pgb->parameter.insertSubscriberDataArg_v3.msisdn.length - msisdnlength;
		    if(msisdnlength && offset > 0){
			prq->response.u.authResponse.msisdn.length = msisdnlength;
			char* pointer = &pgb->parameter.insertSubscriberDataArg_v3.msisdn.value;
			pointer += offset;
			memcpy(&prq->response.u.authResponse.msisdn.value ,
			    pointer,
			    msisdnlength);
		    }else{
			prq->response.u.authResponse.msisdn.length =
			    pgb->parameter.insertSubscriberDataArg_v3.msisdn.length;

			memcpy(&prq->response.u.authResponse.msisdn.value,
			    &pgb->parameter.insertSubscriberDataArg_v3.msisdn.value,
			    prq->response.u.authResponse.msisdn.length);
		    }
		}else{
		    prq->response.u.authResponse.msisdn.length = pgb->parameter.insertSubscriberDataArg_v3.msisdn.length - 1;
		    char* pointer = &pgb->parameter.insertSubscriberDataArg_v3.msisdn.value;
		    pointer += 1;
		    memcpy(&prq->response.u.authResponse.msisdn.value ,pointer,prq->response.u.authResponse.msisdn.length);		  
		}
	}
	
	if ((pgb->parameter.insertSubscriberDataArg_v3.bit_mask &
		InsertSubscriberDataArg_v3_subscriberStatus_present)
		== InsertSubscriberDataArg_v3_subscriberStatus_present)
	{
		prq->response.u.authResponse.subscriberStatus.subscriber_status_present = 1;
		prq->response.u.authResponse.subscriberStatus.subscriberStatus = pgb->parameter.insertSubscriberDataArg_v3.subscriberStatus;
		printf("<<<<<<<<<<<<<<<<Subscriber status received: %d>>>>>>>>>>>>>>>>", pgb->parameter.insertSubscriberDataArg_v3.subscriberStatus);	
	}

    }

    else if ( compareObjectIDs( &prq->applicationContext, &networkLocUpContext_v2 ) )
    {
	if ( ( pgb->parameter.insertSubscriberDataArg_v2.bit_mask &
	       InsertSubscriberDataArg_v2_bearerServiceList_present ) == 
		InsertSubscriberDataArg_v2_bearerServiceList_present )
	{
	    processInsertSubscriberDataBS_v2( pgb, prq );
	}

	if ( ( pgb->parameter.insertSubscriberDataArg_v2.bit_mask &
	       InsertSubscriberDataArg_v2_teleserviceList_present ) == 
		InsertSubscriberDataArg_v2_teleserviceList_present )
	{
	    processInsertSubscriberDataTS_v2( pgb, prq );
	}

	if ( ( pgb->parameter.insertSubscriberDataArg_v2.bit_mask &
	       InsertSubscriberDataArg_v2_odb_Data_present ) == 
		InsertSubscriberDataArg_v2_odb_Data_present )
	{
	    processInsertSubscriberDataODB_v2( pgb, prq );
	}

	if ( ( pgb->parameter.insertSubscriberDataArg_v2.bit_mask &
	       InsertSubscriberDataArg_v2_provisionedSS_present ) == 
		InsertSubscriberDataArg_v2_provisionedSS_present )
	{
            printf("Insert Subscriber data provisioned SS processing ...\n");
            processInsertSubscriberData_provisionedSS_v2(pgb,prq);
        }

        if((pgb->parameter.insertSubscriberDataArg_v2.bit_mask &
               InsertSubscriberDataArg_v2_msisdn_present)
               == InsertSubscriberDataArg_v2_msisdn_present)
        {
		printf("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<msisdn condition satifishfied for V2:%d>>>>>>>>>>>>>>>>>>>",pgb->parameter.insertSubscriberDataArg_v2.imsi.length);
		gMAPPrintGBlock( pgb );
		gMAPPutGBlock( pgb );
		printf("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<msisdn condition satifishfied for V2:%d>>>>>>>>>>>>>>>>>>>",pgb->parameter.insertSubscriberDataArg_v2.imsi.length);
		if(msisdnlength > 0){
		    int offset = pgb->parameter.insertSubscriberDataArg_v2.msisdn.length - msisdnlength;
		    if(msisdnlength && offset > 0){			
			prq->response.u.authResponse.msisdn.length = msisdnlength;
			char* pointer = &pgb->parameter.insertSubscriberDataArg_v2.msisdn.value;
			pointer += offset;
			memcpy(&prq->response.u.authResponse.msisdn.value,
			    &pgb->parameter.insertSubscriberDataArg_v2.msisdn.value,
			    msisdnlength);
		    }else{
			prq->response.u.authResponse.msisdn.length =
			    pgb->parameter.insertSubscriberDataArg_v2.msisdn.length;
			memcpy(&prq->response.u.authResponse.msisdn.value,
			    &pgb->parameter.insertSubscriberDataArg_v2.msisdn.value,
			    prq->response.u.authResponse.msisdn.length);
		    }
		}else{
		    prq->response.u.authResponse.msisdn.length = pgb->parameter.insertSubscriberDataArg_v2.msisdn.length;
		    char* pointer = &pgb->parameter.insertSubscriberDataArg_v2.msisdn.value;
		    pointer += 1;
		    memcpy(&prq->response.u.authResponse.msisdn.value,&pgb->parameter.insertSubscriberDataArg_v2.msisdn.value,prq->response.u.authResponse.msisdn.length);		  
		}
        }
        
        if ((pgb->parameter.insertSubscriberDataArg_v2.bit_mask &
		InsertSubscriberDataArg_v2_subscriberStatus_present)
		== InsertSubscriberDataArg_v2_subscriberStatus_present)
	{
		prq->response.u.authResponse.subscriberStatus.subscriber_status_present = 1;
		prq->response.u.authResponse.subscriberStatus.subscriberStatus = pgb->parameter.insertSubscriberDataArg_v2.subscriberStatus;
		printf("<<<<<<<<<<<<<<<<Subscriber status received: %d>>>>>>>>>>>>>>>>", pgb->parameter.insertSubscriberDataArg_v2.subscriberStatus);	
	}

    }
    else
    {
	if ( Pglobal->dbg.trace_enabled )
	{
	    wlanPrint0(&world, "Application Context Not Supported...  \n" );
	    gMAPPrintGBlock( pgb );
	}
    }

}

/* FUNCTION processInsertSubscriberDataGprs()				*/
/*************************************************************************

FUNCTION
	void processInsertSubscriberDataGprs( gblock_t * pgb, 
				pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data for Gprs
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/

void
processInsertSubscriberDataGprs( gblock_t * pgb, pendingReq_t * prq )
{
    int i, count;


	if ( ( pgb->parameter.insertSubscriberDataArg_v3.bit_mask & InsertSubscriberDataArg_v3_msisdn_present ) != 0 )
    {
		 prq->response.u.updateGprsResponse.insertSubscriberDataArg.bit_mask |= InsertSubscriberDataArg_v3_msisdn_present;
//		 prq->response.u.updateGprsResponse.insertSubscriberDataArg.msisdn.length = pgb->parameter.insertSubscriberDataArg_v3.msisdn.length;
//		    memcpy( &prq->response.u.updateGprsResponse.insertSubscriberDataArg.msisdn.value, 
//			    &pgb->parameter.insertSubscriberDataArg_v3.msisdn.value,
//			    pgb->parameter.insertSubscriberDataArg_v3.msisdn.length);
		if(msisdnlength> 0){
			int offset = pgb->parameter.insertSubscriberDataArg_v3.msisdn.length - msisdnlength;
			if(msisdnlength && offset > 0){
				prq->response.u.updateGprsResponse.insertSubscriberDataArg.msisdn.length = msisdnlength;
				char* pointer = &pgb->parameter.insertSubscriberDataArg_v3.msisdn.value;
				pointer += offset;
				memcpy(&prq->response.u.updateGprsResponse.insertSubscriberDataArg.msisdn.value , pointer, msisdnlength);
		    }else{
				prq->response.u.updateGprsResponse.insertSubscriberDataArg.msisdn.length = pgb->parameter.insertSubscriberDataArg_v3.msisdn.length;
				memcpy(&prq->response.u.updateGprsResponse.insertSubscriberDataArg.msisdn.value, &pgb->parameter.insertSubscriberDataArg_v3.msisdn.value,
			    prq->response.u.updateGprsResponse.insertSubscriberDataArg.msisdn.length);
		    }
		}else{
		    prq->response.u.updateGprsResponse.insertSubscriberDataArg.msisdn.length = pgb->parameter.insertSubscriberDataArg_v3.msisdn.length - 1;
		    char* pointer = &pgb->parameter.insertSubscriberDataArg_v3.msisdn.value;
		    pointer += 1;
		    memcpy(&prq->response.u.updateGprsResponse.insertSubscriberDataArg.msisdn.value ,pointer,prq->response.u.updateGprsResponse.insertSubscriberDataArg.msisdn.length);		  
		}
	}

	if ( ( pgb->parameter.insertSubscriberDataArg_v3.bit_mask & InsertSubscriberDataArg_v3_chargingCharacteristics_present ) != 0 )
    {
		 prq->response.u.updateGprsResponse.insertSubscriberDataArg.bit_mask |= InsertSubscriberDataArg_v3_chargingCharacteristics_present;
		 prq->response.u.updateGprsResponse.insertSubscriberDataArg.chargingCharacteristicsOuter.length = pgb->parameter.insertSubscriberDataArg_v3.chargingCharacteristics.length;
		    memcpy( &prq->response.u.updateGprsResponse.insertSubscriberDataArg.chargingCharacteristicsOuter.value, 
			    &pgb->parameter.insertSubscriberDataArg_v3.chargingCharacteristics.value,
			    pgb->parameter.insertSubscriberDataArg_v3.chargingCharacteristics.length);
	}

    if ( ( pgb->parameter.insertSubscriberDataArg_v3.bit_mask & InsertSubscriberDataArg_v3_gprsSubscriptionData_present ) != 0 )
    {

	prq->response.u.updateGprsResponse.insertSubscriberDataArg.bit_mask |= InsertSubscriberDataArg_v3_gprsSubscriptionData_present;

	if ( ( pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.bit_mask & GPRSSubscriptionData_completeDataListIncluded_present ) != 0 )
	{
	    prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.bit_mask |= GPRSSubscriptionData_completeDataListIncluded_present;
	    prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.completeDataListIncluded = TRUE;
	}
	
	if (( pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.bit_mask & GPRSSubscriptionData_chargingCharacteristics_present ) != 0)
	{
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.bit_mask |= GPRSSubscriptionData_chargingCharacteristics_present;
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.chargingCharacteristics.length = pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.chargingCharacteristics.length;
		memcpy( &prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.chargingCharacteristics.value, 
			&pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.chargingCharacteristics.value,
			pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.chargingCharacteristics.length );
		
	}

	for ( i = 0; i < pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.count; i++ )
	{
	    if ( prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.count < 7 )
	    {
		int valid = isValidAPN(pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].apn.value + 1,
								prq->confIndex);
		if (valid == 0) {
			printf("Ignoring APN %s, as it is not configured in configuration\n", pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].apn.value + 1);
			continue;
		}
		count = prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.count;
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].bit_mask = 0;
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].pdp_ContextId = pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].pdp_ContextId;
		
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].pdp_Type.length = pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].pdp_Type.length;
		memcpy( &prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].pdp_Type.value, 
			&pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].pdp_Type.value,
			pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].pdp_Type.length );
		
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].qos_Subscribed.length = pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].qos_Subscribed.length;
		memcpy( &prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].qos_Subscribed.value, 
			&pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].qos_Subscribed.value,
			pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].qos_Subscribed.length );
		
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].apn.length = pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].apn.length;
		memcpy( &prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].apn.value, 
			&pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].apn.value,
			pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].apn.length );
		
		if ( ( pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].bit_mask & PDP_Context_ext_QoS_Subscribed_present ) != 0 )
		{
		    prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].bit_mask |= PDP_Context_ext_QoS_Subscribed_present;
		    prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].ext_QoS_Subscribed.length = pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].ext_QoS_Subscribed.length;
		    memcpy( &prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].ext_QoS_Subscribed.value, 
			    &pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].ext_QoS_Subscribed.value,
			    pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].ext_QoS_Subscribed.length );
		}
		if ( ( pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].bit_mask & PDP_Context_pdp_ChargingCharacteristics_present ) != 0 )
		{
		    prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].bit_mask |= PDP_Context_pdp_ChargingCharacteristics_present;
		    prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].pdp_ChargingCharacteristics.length = pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].pdp_ChargingCharacteristics.length;
		    memcpy( &prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].pdp_ChargingCharacteristics.value, 
			    &pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].pdp_ChargingCharacteristics.value,
			    pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].pdp_ChargingCharacteristics.length );
		} else {
			printf("CHargin char not present for %s", pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].apn.value);
		}
		if ( ( pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].bit_mask & PDP_Context_ext2_QoS_Subscribed_present ) != 0 )
		{
		    prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].bit_mask |= PDP_Context_ext2_QoS_Subscribed_present;
		    prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].ext2_QoS_Subscribed.length = pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].ext2_QoS_Subscribed.length;
		    memcpy( &prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[count].ext2_QoS_Subscribed.value, 
			    &pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].ext2_QoS_Subscribed.value,
			    pgb->parameter.insertSubscriberDataArg_v3.gprsSubscriptionData.gprsDataList.value[i].ext2_QoS_Subscribed.length );
		}
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.count++;
	    }
	}

    }

    if ( ( pgb->parameter.insertSubscriberDataArg_v3.bit_mask & InsertSubscriberDataArg_v3_accessRestrictionData_present ) != 0 )
    {
	prq->response.u.updateGprsResponse.insertSubscriberDataArg.bit_mask |= InsertSubscriberDataArg_v3_accessRestrictionData_present; /* bit string, length is number of bits */
	prq->response.u.updateGprsResponse.insertSubscriberDataArg.accessRestrictionData.length = pgb->parameter.insertSubscriberDataArg_v3.accessRestrictionData.length;
	prq->response.u.updateGprsResponse.insertSubscriberDataArg.accessRestrictionData.value[0] = pgb->parameter.insertSubscriberDataArg_v3.accessRestrictionData.value[0];
    }

}

/***********************************************************
This function matches receivedAPN with all configure APNs.
Returns 1 if success else 0
************************************************************/
int isValidAPN(char *receivedApnName, int confIndex) {
	
	// If no APN is configured consider received APN as valid APN
	if (Pglobal->sccpAddrArray[confIndex].apnCount == 0 ){
		return 1;
	}
	
	// else check with all configured APNs
	for (int i=0 ; i<Pglobal->sccpAddrArray[confIndex].apnCount ; i++) {
		// Uncomment below line for debugging
		//printf("Comparing Configured APN regex \"%s\" with Received APN name \"%s\"\n", Pglobal->sccpAddrArray[confIndex].apnName[i], receivedApnName);
		
		int ret = regexec(&Pglobal->sccpAddrArray[confIndex].apnRegex[i], receivedApnName, 0, NULL, 0);
        if( !ret ){
				// Uncomment below line for debugging
               //printf("Configured APN regex \"%s\" matched with Received APN name \"%s\"\n", Pglobal->sccpAddrArray[confIndex].apnName[i], receivedApnName);
			return 1;
        } else {
				char msgbuf[100];
                regerror(ret, &Pglobal->sccpAddrArray[confIndex].apnRegex[i], msgbuf, sizeof(msgbuf));
				fprintf(stderr, "Configured APN regex \"%s\" did not match with Received APN name \"%s\", Reason: %s\n", 
					Pglobal->sccpAddrArray[confIndex].apnName[i], receivedApnName, msgbuf);
                return 0;
		}
	}
	return 0;
	
}

/* FUNCTION insertSubscriberDataResp()				*/
/*************************************************************************

FUNCTION
	void insertSubscriberDataResp( gblock_t * pgb, pendingReq_t * prq )

DESCRIPTION
	Process Insert Subscriber data response
INPUTS
        Arg:    pgb	: pointer to gblock
		prq	: pointer to pending request

OUTPUTS
        Return:		None 

FEND
*************************************************************************/

void
insertSubscriberDataResp( gblock_t * pgb, pendingReq_t * prq )
{
    pgb->applicationId = 0;
    pgb->dialogId = prq->dialogId;
    pgb->bit_mask = 0;	/* CR25652 */
    pgb->serviceType = GMAP_RSP;
    pgb->serviceMsg = INSERT_SUBSCRIBER_DATA;
    pgb->parameter.insertSubscriberDataRes_v3.bit_mask = 0;
	pgb->bit_mask = gblock_t_parameter_present;
    if ( Pglobal->dbg.trace_enabled )
    {
	gMAPPrintGBlock( pgb );
    }

	if ( supportedCamelPhases[0] != '\0' )
	{	
		pgb->parameter.insertSubscriberDataRes_v3.bit_mask = pgb->parameter.insertSubscriberDataRes_v3.bit_mask | InsertSubscriberDataRes_v3_supportedCamelPhases_present;
		int phase = 0;
		if ( supportedCamelPhases[0] == '1' )
		{
			phase = SupportedCamelPhases_phase1;
		}
		if ( supportedCamelPhases[1] == '1' )
		{
			phase = phase | SupportedCamelPhases_phase2;
		}
		if ( supportedCamelPhases[2] == '1' )
		{
			phase = phase | SupportedCamelPhases_phase3;
		}
		if ( supportedCamelPhases[3] == '1' )
		{
			phase = phase | SupportedCamelPhases_phase4;
		}
		pgb->parameter.insertSubscriberDataRes_v3.supportedCamelPhases.length = 16;
		char ch[2] = {phase,0};
		memcpy( pgb->parameter.insertSubscriberDataRes_v3.supportedCamelPhases.value, ch , sizeof(ch));
		
	}
	
    if ( gMAPPutGBlock( pgb ) != 0 )
		terminate( TERMINATE_CAUSE_PUTGBLOCK );
	
    /*Sending a Delimeter . */
    pgb->serviceType = GMAP_REQ;	/* CR25652 */
    pgb->serviceMsg = GMAP_DELIMITER;
    pgb->applicationId = 0;
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->parameter.delimiter.qualityOfService = CL_SVC_CLASS_1 | RETURN_ERROR;

    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	if ( NULL != strstr( APIErr, "No TRANSLATION FOR SPECIFIC ADDRESS" ) )
	{
	    prq->response.u.authResponse.errorCode = ulcm_unknownImsi;
	    sendResponseOnSocket( prq, CLEAN );
	    DBG_DISP( DBG_MASK_ERR, 3211, APIErr );
	}
	else
	{
	    DBG_DISP( DBG_MASK_ERR, 
		    3222, 
		    "insertSubscriberDataResp: GMAP_OPEN gMAPPutGBlock failed: %s\n", 
		    APIErr );
	    prq->response.u.authResponse.errorCode = ulcm_gatewayError;
	    sendResponseOnSocket( prq, CLEAN );
	}
    }

}

/* FUNCTION generateServiceString() - generate the string to return to the client */
/*************************************************************************

FUNCTION
    void generateServiceString(gblock_t *pgb,pendingReq_t *prq)

DESCRIPTION

        For each service configured in the gateway conf file,
        verify is the service is provisionned and not barred (baoc)
        before adding the corresponding string to the authorization
        string returned to the client.

INPUTS
        Arg:    pgb - a reference to the received gblock_t
                prq - a reference to the pendingReq_t

OUTPUTS
        Return: None

FEND
*************************************************************************/

void
generateServiceString(gblock_t *pgb,pendingReq_t *prq)
{
    int k;
//    int baoc = 0x92;

//    printf("generateServiceString: requestId=%d\n", prq->requestId);
//    printf("generateServiceString: bsCodesCount=%d\n", Pglobal->sccpAddrArray[prq->confIndex].bsCodesCount);
//    printf("generateServiceString: prq->confIndex=%d\n", prq->confIndex);

    for (k = 0;k < Pglobal->sccpAddrArray[prq->confIndex].bsCodesCount; k++)
    {
//	printf("generateServiceString: bserviceProvisioned[k]=%d, bs_ss_code[k]=%d\n",
//		Pglobal->sccpAddrArray[prq->confIndex].bserviceProvisioned[k],
//		Pglobal->sccpAddrArray[prq->confIndex].bs_ss_code[k]);

         /* Add the string only if the service is provisionned and not barred */
        if ((prq->bserviceProvisioned[k]))
        {
              /* if it is the first svc of the list we don't write the separator */
              if (prq->response.u.authResponse.serviceFound != 0)
              {
                  strcat(&prq->response.u.authResponse.serviceList[0],":");

                  strcat(&prq->response.u.authResponse.serviceList[0],
                         Pglobal->sccpAddrArray[prq->confIndex].bsString[k]);
              }else {
                  strcpy(&prq->response.u.authResponse.serviceList[0],
                         Pglobal->sccpAddrArray[prq->confIndex].bsString[k]);
              }
              prq->response.u.authResponse.serviceFound = 1;
        }
    }

    for (k = 0;k < Pglobal->sccpAddrArray[prq->confIndex].tsCodesCount; k++)
    {
        if ((prq->tserviceProvisioned[k]))
        {
              /* if it is the first svc of the list we don't write the separator */
              if (prq->response.u.authResponse.serviceFound != 0)
              {
                  strcat(&prq->response.u.authResponse.serviceList[0],":");

                  strcat(&prq->response.u.authResponse.serviceList[0],
                         Pglobal->sccpAddrArray[prq->confIndex].tsString[k]);

              }else {
                  strcpy(&prq->response.u.authResponse.serviceList[0],

                         Pglobal->sccpAddrArray[prq->confIndex].tsString[k]);

              }

              prq->response.u.authResponse.serviceFound = 1;
        }
    }
}

/*FUNCTION process_socket_msg() - process the message received on the socket*/
/*************************************************************************

FUNCTION
    void process_socket_msg()

DESCRIPTION
    This function processes the message received on the socket.
    It parses the message, fills the structure, initiate a TCAP dialog
    and send the appropriate MAP requests.

INPUTS
    Arg: buffer
OUTPUTS
    Return: None
FEND
*************************************************************************/
void
process_socket_msg( char *buff, int len, gblock_t * pgb )
{
    int             i, j, ret_code=0;
    int             pdunum = ulcm_Request_PDU;
    ulcm_Request   *rq1;
    ulcm_Request    rq;
    pendingReq_t   *prq;
    int             return_code;

    if ( Pglobal->dbg.trace_enabled )
    {
		wlanPrint0(&world, "#################\nWLAN PRINT Hex: Received message\n" );
		wlanPrintHex(&world, buff, len );
    }

    i = findFreeSpot(  );

    if ( i == -1 )
    {
		DBG_DISP( DBG_MASK_ERR, 44, 
				  "process_socket_msg: Could not find a free entry in the table.\n" );
		sendErrorOnSocket( ulcm_maxRequestExceeded );
		return;
    }

    prq = &PmyTable[i];

    wlanBuf2.length = len;
    wlanBuf2.value = ( unsigned char * ) buff;
    rq1 = &rq;

    wlanSetDecodingLength(&world, sizeof( ulcm_Request ) );
    return_code = wlanDecode(&world, &pdunum, &wlanBuf2, ( void ** ) &rq1 );

    if ( 0 != return_code )
    {
        DBG_DISP( DBG_MASK_L2, 45, 
                  "process_socket_msg: Error %d while decoding the received message. Discarding.\n", 
                  return_code );
        sendErrorOnSocket( ulcm_malformedMessage );
        return;
    }

    switch ( rq.choice )
    {
    case ulcm_initRequest_chosen:
		memcpy( &prq->request, &rq, sizeof( ulcm_Request ) );
		prq->response.choice = ulcm_initResponse_chosen;
		prq->response.u.initResponse.maxRequest = Pglobal->max_requests;

		if ( VERSION <= prq->request.u.initRequest.version )
		{
			prq->response.u.initResponse.versionSupported = TRUE;
			sendResponseOnSocket( prq, NO_CLEAN );
		}
		else
		{
			prq->response.u.initResponse.versionSupported = FALSE;
			sendResponseOnSocket( prq, NO_CLEAN );

			if ( Pglobal->sock != -1 )
				shutdown( Pglobal->sock, 2 );
	    
			close( Pglobal->sock );
	    
			/* listen to the parent socket for new connections */
			Pglobal->pfd[SOCKET_FD].fd = Pglobal->psock;
		}
	
		memset( prq, 0, sizeof( *prq ) );
		break;

    case ulcm_authRequest_chosen:
		memcpy( &prq->request, &rq, sizeof( ulcm_Request ) );
		/* initialize the structure */
		prq->invokeId[0] = 0;
		prq->response.choice = ulcm_authResponse_chosen;
		prq->response.u.authResponse.errorCode = ulcm_noError;
		prq->response.u.authResponse.bit_mask = 0;
		prq->response.u.authResponse.requestId = prq->request.u.authRequest.requestId;
		prq->requestId = prq->request.u.authRequest.requestId;
		prq->response.u.authResponse.serviceFound = 0;
        prq->response.u.authResponse.msisdn.length = 0;

		for ( j = 0; j < Pglobal->sccpAddrCount; j++ )
    	{
	    	ret_code = 
				compareDigitsIndex( prq->request.u.authRequest.imsi.value, j );
		
			if ( ret_code == 1 )
			{
//		    printf("process_socket_msg: prq->confIndex=%d\n", prq->confIndex);

	    		prq->confIndex = j;
	    		break;
			}
		}



		if ( prq->request.u.authRequest.numberOfRequestedVectors != 0 )
		{

			prq->status = waitingForOpenRspTriplets;

			if ( Pglobal->ApplicationContext == 3 )
			{
				memcpy( &prq->applicationContext, &infoRetrievalContext_v3, sizeof( ObjectID ) );
			}
			else if ( Pglobal->ApplicationContext == 2 )
			{
				memcpy( &prq->applicationContext, &infoRetrievalContext_v2, sizeof( ObjectID ) );
			}
			else
			{
				prq->response.u.authResponse.errorCode = ulcm_gatewayError;
				sendResponseOnSocket( prq, CLEAN );
				DBG_DISP( DBG_MASK_ERR, 417, 
						  "Application Context version %d not supported \n", 
						  Pglobal->ApplicationContext );
				return;
			}

			/* initiates a TCAP dialog  */
			if ( 0 != initiateDialog( pgb, prq ) )
			{
				/* initiateDialog succeeded, insert the prq in the table */
				if ( ( 0 == hash_add_entry( &dialogId_hash, pgb->dialogId, i ) ) || 
					 ( 0 == hash_add_entry( &requestId_hash, PmyTable[i].requestId, i ) ) )
				{
					DBG_DISP( DBG_MASK_ERR, 46, 
							  "No more bucket available, cannot insert entry in the hash.\n" );
					memset( &PmyTable[i], 0, sizeof( PmyTable[i] ) );
					sendErrorOnSocket( ulcm_maxRequestExceeded );
				}
			}
			else
			{
				/* initiateDialog Failed. */
			}
		}
        else if ((Pglobal->sccpAddrArray[prq->confIndex].bsCodesCount == 0) &&
				 (Pglobal->sccpAddrArray[prq->confIndex].tsCodesCount == 0))
		{
			/* If no bs or bs present, send an empty response */
			prq->response.u.authResponse.serviceFound = 1;
			sendResponseOnSocket(prq,CLEAN);

		} else /* No triplets requested but authorization requested */

		{
			prq->status = waitingForOpenRspRestoreData;

			/* Before sending the restoreData we change the application context
			   to networkLocUpContext, but we keep the same version number.
			*/
			if ( Pglobal->ApplicationContext == 3 )
			{
				memcpy( &prq->applicationContext, &networkLocUpContext_v3, sizeof( ObjectID ) );
			}
			else if ( Pglobal->ApplicationContext == 2 )
			{
				memcpy( &prq->applicationContext, &networkLocUpContext_v2, sizeof( ObjectID ) );
			}
			else
			{
				prq->response.u.authResponse.errorCode = ulcm_gatewayError;
				sendResponseOnSocket( prq, CLEAN );
				DBG_DISP( DBG_MASK_ERR, 717, "Application Context version not supported \n" );
				return;
			}

			/* initiates a TCAP dialog  */
			if ( 0 != initiateDialog( pgb, prq ) )
			{
				/* initiateDialog succeeded, insert prq in the table */
				if ( ( 0 == hash_add_entry( &dialogId_hash, pgb->dialogId, i ) ) || 
					 ( 0 == hash_add_entry( &requestId_hash, PmyTable[i].requestId, i ) ) )
				{
					DBG_DISP( DBG_MASK_ERR, 3335, 
							  "No more bucket available, cannot insert entry in the hash.\n" );
					memset( &PmyTable[i], 0, sizeof( PmyTable[i] ) );
					sendErrorOnSocket( ulcm_maxRequestExceeded );
				}
			}
			else
			{
				/* initiateDialog Failed. */
			}
		}
		break;

    case ulcm_cancelAuthRequest_chosen:
    case ulcm_cancelImsiRequest_chosen:
		cancelRequest( rq.u.cancelAuthRequest );
		return;
		break;

    case ulcm_imsiRequest_chosen:
		memcpy( &prq->request, &rq, sizeof( ulcm_Request ) );
	
		/* initialize the structure */
		prq->invokeId[0] = 0;
		prq->status = waitingForOpenRspImsi;
		prq->response.choice = ulcm_imsiResponse_chosen;
		prq->response.u.imsiResponse.errorCode = ulcm_noError;
		prq->requestId = prq->request.u.imsiRequest.requestId;
		prq->response.u.imsiResponse.requestId = prq->requestId;
	
		/* At this time, v2 is the only imsiRetrievalContext existing in the spec */
		memcpy( &prq->applicationContext, &imsiRetrievalContext_v2, sizeof( ObjectID ) );

		/* initiates a TCAP dialog  */
		if ( 0 != initiateDialog( pgb, prq ) )
		{
			/* initiateDialog succeeded, insert the prq in the table */
			if ( ( 0 == hash_add_entry( &dialogId_hash, pgb->dialogId, i ) ) || 
				 ( 0 == hash_add_entry( &requestId_hash, PmyTable[i].requestId, i ) ) )
			{
				DBG_DISP( DBG_MASK_ERR, 946, 
						  "No more bucket available, cannot insert entry in the hash.\n" );
				memset( &PmyTable[i], 0, sizeof( PmyTable[i] ) );
				sendErrorOnSocket( ulcm_maxRequestExceeded );
			}
		}

		break;
	case ulcm_updateGprsRequest_chosen:
		memcpy( &prq->request, &rq, sizeof( ulcm_Request ) );
	
		/* initialize the structure */
		prq->invokeId[0] = 0;
		prq->status = waitingForOpenRspUpdateGprs;
		prq->response.choice = ulcm_updateGprsResponse_chosen;
		prq->response.u.updateGprsResponse.errorCode = ulcm_noError;
		prq->requestId = prq->request.u.updateGprsRequest.requestId;
		prq->response.u.updateGprsResponse.requestId = prq->requestId;
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.bit_mask = 0;
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.bit_mask = 0;
		prq->response.u.updateGprsResponse.insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.count = 0;
	
		/* At this time, v3 is the only gprsLocationUpdateContext existing in the spec */
		memcpy( &prq->applicationContext, &gprsLocationUpdateContext_v3, sizeof( ObjectID ) );

		/* initiates a TCAP dialog  */
		if ( 0 != initiateDialog( pgb, prq ) )
		{
			/* initiateDialog succeeded, insert the prq in the table */
			if ( ( 0 == hash_add_entry( &dialogId_hash, pgb->dialogId, i ) ) || 
				 ( 0 == hash_add_entry( &requestId_hash, PmyTable[i].requestId, i ) ) )
			{
				DBG_DISP( DBG_MASK_ERR, 946, 
						  "No more bucket available, cannot insert entry in the hash.\n" );
				memset( &PmyTable[i], 0, sizeof( PmyTable[i] ) );
				sendErrorOnSocket( ulcm_maxRequestExceeded );
			}
		}

		break;



    default:
		DBG_DISP( DBG_MASK_ERR, 47, "Invalid request.choice: %d\n", rq.choice );
		sendErrorOnSocket( ulcm_malformedMessage );
    }

}

/* FUNCTION process_mom_q() - Read messages from the MOM queue (if any) */
/*************************************************************************

FUNCTION
    void process_mom_q ()

DESCRIPTION
    This function reads the messages from the MOM queue.

INPUTS
    Arg: pgb a gblock
    Global: ash
OUTPUTS
    Return: None
FEND
*************************************************************************/
void
process_mom_q( gblock_t * pgb )
{
    int             ret_code;
    char           *ipc;

    /* If we are here, then we are completing the asynch request */
    Pglobal->async_req = FALSE;

    if ( ( ret_code = AsyncError( &Pglobal->ash ) ) )
    {
	/*  There was some kind of error:  probably the request was cancelled
	 */
	DBG_DISP( DBG_MASK_ERR, 48, "%s: process_mom_q(): I/O error=%d\n", MYNAME, ret_code );
	return;
    }

    ipc = ( char * ) AsyncGetMsgHeader( &Pglobal->ash );

    switch ( ( ( Header_t * ) ipc )->messageType )

    {
    case N_NOTICE_IND:
    case N_UNITDATA_IND:	/* received a TCAP message */
	gMAPTakeMsg( ( cblock_t * ) ipc );
	
	while ( gMAPGetGBlock( pgb ) == 0 )
	{
	    gblockReceived( pgb );
	}
	break;
    case N_STATE_IND:
	processNState( ( Header_t * ) & ipc );
	break;
    case N_PCSTATE_IND:
	processPcState( ( Header_t * ) & ipc );
	break;
    case TAP_STATE_CHANGE:	/* only if process is designatable */
	DBG_DISP( DBG_MASK_L2, 49, "   Received TAP_STATE_CHANGE\n" );
	break;
    default:
	DBG_DISP( DBG_MASK_ERR, 50, 
		"   Received unknown msg type[0x%x]\n", 
		( ( Header_t * ) ipc )->messageType );
	break;
    }
}

/* FUNCTION setup_async_req() */
/*************************************************************************

FUNCTION
    void setup_async_req()

DESCRIPTION
    This function sets up  the async request

INPUTS
    Arg:  none
OUTPUTS
    Return: None
FEND
*************************************************************************/
void
setup_async_req(  )
{

    /* If there is already a request out there, don't create another one */
    if ( Pglobal->async_req )
	return;

    if ( Pglobal->ash.bufferStatus && !AsyncComplete( &Pglobal->ash ) )
    {
	/*  Safety measure: don't attempt to create a new async request if
	 *  the prior one didn't complete yet.
	 *  This should never happen.
	 */
	DBG_DISP( DBG_MASK_ERR, 51, 
		"%s: setup_async_req(): ", 
		"!AsyncComplete(ash): 0x%x\n", 
		MYNAME, Pglobal->ash.bufferStatus );
	terminate( TERMINATE_CAUSE_ASYNCBUFFER );
    }

    if ( FtGetIpcAsync( &Pglobal->ash,	/* set up async. read request */
			0,	/* get any messages */
			MAXipcBUFFERsize,	/* length of our buffer    */
			TRUE,	/* truncate messages */
			0 )	/* callback function */
	 == RETURNerror )
    {
	DBG_DISP( DBG_MASK_ERR, 52, "(%s): FtGetIpcAsync() failed: (%d)%s\n", 
		MYNAME, errno, strerror( errno ) );
	terminate( TERMINATE_CAUSE_FTGETIPCASYNC );
    }

    /* we now have an async request out there to be aware of */
    Pglobal->async_req = TRUE;
}

/* FUNCTION cancel_async_req() */
/*************************************************************************

FUNCTION
    void cancel_async_req()

DESCRIPTION
    This function cancels the async request

INPUTS
    Arg:  none
OUTPUTS
    Return: None
FEND
*************************************************************************/
void
cancel_async_req( gblock_t * pgb )
{

    if ( Pglobal->async_req )
    {
	Pglobal->async_req = FALSE;

	if ( FtCancelAsyncRequest( &Pglobal->ash ) == RETURNerror )
	{
	    DBG_DISP( DBG_MASK_ERR, 53, 
		    "(%s): FtCancelAsyncRequest() failed: (%d)%s\n", 
		    MYNAME, errno, strerror( errno ) );
	    terminate( TERMINATE_CAUSE_FTCANCELASYNC );
	}

	/* Complete the I/O NOW;  we REALLY don't want the read out there! */
	FtProcessIO(  );

	/*  Call process_mom_q() to complete the I/O.  It should be noted that
	 *  it is possible to receive an IPC message here--if we sent down
	 *  the cancellation request but MOM had already completed the I/O.
	 */
	process_mom_q( pgb );
    }
}

/* FUNCTION mainLoop */
/*************************************************************************

FUNCTION
    void mainLoop()

DESCRIPTION
    This is the main loop of this application

INPUTS
    Arg:
OUTPUTS
    Return: None
FEND
*************************************************************************/
void
mainLoop(  )
{
    int             size;
    char            buff[1024] = { 0 };
    int             ret_code;
    MAP_AppTimeout  appTimeout;	/* MAP_AppTimeout is defined in gmaptim.h */

    gblock_t        gblock;
    gblock_t       *pgb = &gblock;

/*
 *    Enter main loop of the application
 */

    /* Set Async Header message pointer */
    AsyncSetMsgHeader( &Pglobal->ash, ( void * ) &Pglobal->PRIPC );

    /* Get ready to poll() */
    memset( Pglobal->pfd, 0, sizeof( Pglobal->pfd ) );
	int i = 0;
	for (i = 0 ; i < MAX_FD ; i++) {
		Pglobal->pfd[i].fd = -1;
	}
    Pglobal->pfd[LISTEN_FD].fd = setup_socket( Pglobal->port, Pglobal->hostname );

    /* Get MOM file descriptor */
    if ( ( Pglobal->pfd[MOM_FD].fd = FtGetDescriptor(  ) ) < 1 )
    {
        DebugIndicator = TRUE;
        FtErrorDisplay( "FtGetDescriptor() failed" );
        terminate( TERMINATE_CAUSE_FTGETDESCRIPTOR );
    }

    /* poll for data messages */

	for (i = 0 ; i < MAX_FD ; i++) {
		Pglobal->pfd[i].events = POLLIN;
	}
    
    /* Receive messages */
    finished_initialization = TRUE;	/* SIGINT will set terminate */


    DBG_DISP( DBG_MASK_L2, 54, "Entering Main Loop\n" );

    for ( ;; )
    {
        gMAPAppTimerHandler(  );

        while ( gMAPAppGetTimeout( &appTimeout ) == 0 )
        {
            if ( appTimeout.id1 == GUARD_TIMER_ID )
            {
                gMAPTimeout( &appTimeout );
            }
        }


        if ( Terminate )	/* if SIGINT caught, break out of loop */
            break;

        /*  Set up the async read.
         *  This will not do anything if the read is already outstanding
         *  (e.g., in the EINTR or ETIME case).
         */
        setup_async_req(  );

        ret_code = poll( Pglobal->pfd, MAX_FD, INFTIM );

        if ( ret_code <= 0 )
        {
            while ( gMAPGetGBlock( pgb ) == 0 )
            {
                gblockReceived( pgb );
            }
	    
            /* CR 27676: check timeout for accepted socket */
            if (( _connection_timeout > 0)
                && ( Pglobal->pfd[SOCKET_FD].fd != Pglobal->psock ))
            {
                if (difftime(time((time_t)NULL), _connection_last_activity)
                    > (_connection_timeout * 60))
                {
                    DBG_DISP( DBG_MASK_ERR, 594, 
                              "Connection timeout, closing socket\n" );

                    /* close the accepted socket */
                    close( Pglobal->pfd[SOCKET_FD].fd );
		
                    /* now go back to checking the listen socket */
                    //Pglobal->pfd[SOCKET_FD].fd = Pglobal->psock;
					Pglobal->pfd[SOCKET_FD].fd = -1;
                }
            }
            continue;
        }


        if ( Pglobal->pfd[LISTEN_FD].revents & POLLERR || Pglobal->pfd[LISTEN_FD].revents & POLLNVAL || Pglobal->pfd[LISTEN_FD].revents & POLLHUP)
        {
            DBG_DISP( DBG_MASK_L2, 55, "(%s): POLLERR or POLLNVAL\n", MYNAME );
            /* CR 27676: an error has occurred, so force a close */
            close(Pglobal->sock);
            Pglobal->sock = -1;
			Pglobal->pfd[SOCKET_FD].fd = -1;
            close(Pglobal->psock);
            Pglobal->psock = -1;
            Pglobal->pfd[LISTEN_FD].fd = setup_socket( Pglobal->port, Pglobal->hostname );
            DBG_DISP( DBG_MASK_L2, 56, "(%s): Error on the listen socket,  connections reset\n", MYNAME );
            continue;
        }

		
		if ( Pglobal->pfd[SOCKET_FD].revents & POLLERR || Pglobal->pfd[SOCKET_FD].revents & POLLNVAL || Pglobal->pfd[SOCKET_FD].revents & POLLHUP)
        {
            DBG_DISP( DBG_MASK_L2, 55, "(%s): POLLERR or POLLNVAL\n", MYNAME );
            /* CR 27676: an error has occurred, so force a close */
            close(Pglobal->sock);
            Pglobal->sock = -1;
			Pglobal->pfd[SOCKET_FD].fd = -1;
            //Pglobal->pfd[LISTEN_FD].fd = setup_socket( Pglobal->port, Pglobal->hostname );
            DBG_DISP( DBG_MASK_L2, 56, "(%s): Error on the connection socket,  connections reset\n", MYNAME );
            continue;
        }

        /* First check for messages from MOM */
        if ( Pglobal->pfd[MOM_FD].revents & POLLIN )
        {
            /* There are IPC messages for us to read */
            FtProcessIO(  );
            process_mom_q( pgb );
        }

		if ( Pglobal->pfd[LISTEN_FD].revents & POLLIN )
        {
			/*  If we were poll()ing on the parent (listen) socket, then we
             *  haven't accept()ed a connection yet.  Since there was activity
             *  on the listen socket, we can now accept() a connection.
             */
            printf("POLLIN event on LISTEN_FD\n");
            {
			
				close(Pglobal->sock);
				Pglobal->sock = -1;
				Pglobal->pfd[SOCKET_FD].fd = -1;
			
                struct  sockaddr_in sina;	/* accepted sockaddr    */
                socklen_t AddrLen = (socklen_t)sizeof(sina);

                if (-1 == (Pglobal->sock = accept(Pglobal->psock, (struct sockaddr *)&sina, &AddrLen)))

                {
                    DBG_DISP( DBG_MASK_ERR, 58, "accept() failed, errno=%d\n",errno );
		   	
                    /* Close and reinitialise the socket */	   
                    close( Pglobal->sock );
                    Pglobal->sock = -1;
                    Pglobal->pfd[SOCKET_FD].fd = -1; 
                    DBG_DISP( DBG_MASK_L2, 581, "(%s): Resetting connection\n", MYNAME);
                    continue;
                }
				
                /* CR 27676: set SO_KEEPALIVE for periodic heartbeat messages */
                /* to attempt to better track if connection gets hung. */
                /*int so_optval = 1;
                if (setsockopt(Pglobal->sock, SOL_SOCKET, SO_KEEPALIVE, (char *)&so_optval, sizeof(so_optval)) < 0)
                {
                    DBG_DISP( DBG_MASK_ERR, 593, 
                              "Unable to set socket SO_KEEPALIVE, errno=%d\n", errno );
                }*/

                /* CR 27676: set last activity time to now */
                _connection_last_activity = time((time_t)NULL);

                /* From now on, we poll on the connection socket */
                Pglobal->pfd[SOCKET_FD].fd = Pglobal->sock;

                ( void ) FtPutStateEx( ( S16 ) 2, State_Green, "Connection Established", 10, FALSE );

                DBG_DISP( DBG_MASK_L2, 59, "%s: connection established\n", MYNAME );

                continue;
            }
		
		}
		
        /* Then check for messages from TCP */
        if ( Pglobal->pfd[SOCKET_FD].revents & POLLIN )
        {
            /*  If we were poll()ing on the parent (listen) socket, then we
             *  haven't accept()ed a connection yet.  Since there was activity
             *  on the listen socket, we can now accept() a connection.
             */
            if ( Pglobal->pfd[SOCKET_FD].fd == Pglobal->psock )
            {
                struct  sockaddr_in sina;	/* accepted sockaddr    */
                socklen_t AddrLen = (socklen_t)sizeof(sina);
				printf("Error!! LISTEN_FD is called inside CONNECTION_FD");
                if (-1 == (Pglobal->sock = accept(Pglobal->psock, (struct sockaddr *)&sina, &AddrLen)))

                {
                    DBG_DISP( DBG_MASK_ERR, 58, "accept() failed, errno=%d\n",errno );
		   	
                    /* Close and reinitialise the socket */	   
                    close( Pglobal->sock );
                    Pglobal->sock = -1;
					Pglobal->pfd[SOCKET_FD].fd = -1; 
                    close( Pglobal->psock );
                    Pglobal->psock = -1;
                    Pglobal->pfd[LISTEN_FD].fd = setup_socket( Pglobal->port, Pglobal->hostname); 
                    DBG_DISP( DBG_MASK_L2, 581, "(%s): Resetting connection\n", MYNAME);
                    continue;
                }

                /* CR 27676: set SO_KEEPALIVE for periodic heartbeat messages */
                /* to attempt to better track if connection gets hung. */
               /* int so_optval = 1;
                if (setsockopt(Pglobal->sock, SOL_SOCKET, SO_KEEPALIVE, (char *)&so_optval, sizeof(so_optval)) < 0)
                {
                    DBG_DISP( DBG_MASK_ERR, 593, 
                              "Unable to set socket SO_KEEPALIVE, errno=%d\n", errno );
                }*/

                /* CR 27676: set last activity time to now */
                _connection_last_activity = time((time_t)NULL);

                /* From now on, we poll on the connection socket */
                Pglobal->pfd[SOCKET_FD].fd = Pglobal->sock;

                ( void ) FtPutStateEx( ( S16 ) 2, State_Green, "Connection Established", 10, FALSE );

                DBG_DISP( DBG_MASK_L2, 59, "%s: connection established\n", MYNAME );

                continue;
            }

            /*  Else we were polling on the connection and there is now
             *  some data there to read.
             */
            size = tcp_recv(Pglobal->pfd[SOCKET_FD].fd, buff, sizeof(buff));

            /* Check for errors on the read() */
            if ( size < 1 )
            {
                if ( size )
                {
                    DBG_DISP( DBG_MASK_ERR, 591, 
                              "TCP recv() failed, errno=%d, wait for another connection\n", errno );
                }
                else
                {
                    DBG_DISP( DBG_MASK_ERR, 592, 
                              "Socket closed, errno=%d, wait for another connection\n", errno );
                }
		
                /* close the accepted socket */
				close(Pglobal->sock);
                Pglobal->pfd[SOCKET_FD].fd = -1;
				Pglobal->sock = -1;
                /* now go back to checking the listen socket */
                //Pglobal->pfd[SOCKET_FD].fd = Pglobal->psock;
                continue;
            }

            /*  Else we got some data from the socket.
             *
             *  The data coming
             *  from the socket might cause the application to go off and
             *  do something that requires 2-way IPC communication (e.g.,
             *  SYSbind(3q) or putting a message to the TCAP librarie.)
             *  In this case, it would be necessary to cancel the async
             *  read request so that it does not interfere with these other
             *  API calls.
             *
             *  It should be noted that cancelling the async read request
             *  is not expensive: it's the same thing that happens when the
             *  synchronous functions (e.g., FtGetIpcEx(3f)) are interrupted
             *  by a signal or time out.
             *
             *  When we go back around to the top of the loop, the async read
             *  request will be set up again.
             *
             *   cancel_async_req(pgb);
             */

            /* Here we "consume" the message we got on the socket
               we parse the message and fill the structure
               we build MAP requests and send them to the HLR */

            process_socket_msg( buff, size, pgb );

            /* CR 27676: update activity time after processing message */
            _connection_last_activity = time((time_t)NULL);
        }
    }				/* for() */
}



/* FUNCTION hashFunction */
/*************************************************************************

FUNCTION
        U16 hashFunction(U32 hash_key)

DESCRIPTION
        hash function used by the two hash tables


INPUTS
        Arg: hash_key

OUTPUTS
        Return: hash value

FEND
*************************************************************************/
U16
hashFunction( U32 hash_key )
{
    return ( hash_key % Pglobal->max_requests );
}

/* FUNCTION cleanOldEntriesFromTable */
/*************************************************************************

FUNCTION
        cleanOldEntriesFromTable()

DESCRIPTION
        Check the timestamp of all entries int he table and remove the entries
        that have a timestamp too old.


INPUTS
        Arg: None

        Global: None
OUTPUTS
        Return: None

FEND
*************************************************************************/
void
cleanOldEntriesFromTable(  )
{
    int             i, index;

    for ( i = 1; i <= Pglobal->max_requests; i++ )
    {
	index = requestId_hash.bucket[i].lnk_index;
	
	/*  The GSM (and TCAP) libraries do not currently keep dialogs nor invokes
	 *  across process restarts.  Thus, we should clear the whole table upon
	 *  restart.
	 *
	 *  If this changes (e.g., we start using OOS) then this line (or some
	 *  other protective measure) should be put back in.
	 *if(PmyTable[index].timestamp + Pglobal->invokeTimeout +10> getTimeStamp())
	 */
	{
	    hash_remove_entry( &dialogId_hash, PmyTable[index].dialogId );
	    hash_remove_entry( &requestId_hash, requestId_hash.bucket[i].hash_key );
	    memset( &PmyTable[index], 0, sizeof( PmyTable[index] ) );
	}
    }
}


/* FUNCTION init_shared_memory()                                         */
/*************************************************************************

FUNCTION
        int init_shared_memory (restart)

DESCRIPTION
        Create or attach to (depending on 'restart') all process shared
	memory segments.

INPUTS
        Arg:    restart - if TRUE then attach to, instead of create, the
			  shared memory

        Global: None
OUTPUTS
        Return: None

	NOTE:  All failure cases cause the process to exit.

FEND
*************************************************************************/
void
init_shared_memory( BOOL restart )
{
    CREATE_OR_ATTACH_SHM( Pglobal->PROCESS_NAME, myTable, pendingReq_t, sizeof( pendingReq_t ) * ( Pglobal->max_requests + 1 ), &sm.myTable );
    
    CREATE_OR_ATTACH_SHM( Pglobal->PROCESS_NAME, dialogId_buckets, bucket_t, sizeof( bucket_t ) * ( Pglobal->max_requests + 1 ), &sm.dialogId_buckets );
    
    CREATE_OR_ATTACH_SHM( Pglobal->PROCESS_NAME, dialogId_Hash, U16, sizeof( U16 ) * ( Pglobal->max_requests + 1 ), &sm.dialogId_Hash );
    
    CREATE_OR_ATTACH_SHM( Pglobal->PROCESS_NAME, requestId_buckets, bucket_t, sizeof( bucket_t ) * ( Pglobal->max_requests + 1 ), &sm.requestId_buckets );
    
    CREATE_OR_ATTACH_SHM( Pglobal->PROCESS_NAME, requestId_Hash, U16, sizeof( U16 ) * ( Pglobal->max_requests + 1 ), &sm.requestId_Hash );
}


/* FUNCTION isComment()				*/
/*************************************************************************

FUNCTION
	int isComment( const char *buff )

DESCRIPTION
	check if any wild character in the string.	
INPUTS
        Arg:    buff	: pointer to constant string

OUTPUTS
        Return:	0	: if No
		1	: if Yes

FEND
*************************************************************************/

int
isComment( const char *buff )
{
    int             len = strlen( buff );
    int             i = 0;

    for ( i = 0; i < len; i++ )
    {
	if ( buff[i] == ' ' )
	    continue;
	else if ( buff[i] == '\t' )
	    continue;
	else if ( buff[i] == '#' )
	    return 1;
	else if ( buff[i] == '\n' )
	    return 1;
	else
	    return 0;
    }

    /* should not get here */
    return 0;
}


/* FUNCTION begins_with_digit()						*/
/*************************************************************************

FUNCTION
	BOOL begins_with_digit( const char *value )

DESCRIPTION
	check if the input string begins with a digit
INPUTS
        Arg:  	 pFile	: pointer to configuration file name

OUTPUTS
        Return:	TRUE	: begins with a digit
		FALSE	: doesn't begins with a digit

FEND
*************************************************************************/

static BOOL
begins_with_digit( const char *value )
{
    char *endptr;
    char buf[16];

    if( NULL == value )
    {
	printf("begins_with_digit: value=NULL, return FALSE\n");
	return FALSE;
    }

    sprintf( buf, "%c", value[0] );
    strtol( buf, &endptr, 0 );

    if( 0 == strlen(endptr) )
    {
	return TRUE;
    }
    else
    {
	return FALSE;
    }
}


/* FUNCTION initConfParams()						*/
/*************************************************************************

FUNCTION
	int initConfParams( const char *pFile )

DESCRIPTION
	Read configuration file and put the value in global variable.
INPUTS
        Arg:  	 pFile	: pointer to configuration file name

OUTPUTS
        Return:	0	: Success
		1	: error

FEND
*************************************************************************/

int
initConfParams( const char *pFile )
{
    /*
     * In the code below, the number MAX_TOKENS specifies the number of name-value pairs.
     *
     * There are atmost 9 + 2 (bs and ts) members of the sccpAddress_t structure in one line.
     *
     * For example :
     * The conf file may contain the following line.
     *
     * odigits 123 ndigits 321 rnai 0 rnp 0 rgti 0 rtt 0 rri 0 rpc 0 rssn 0 bs 12:gold bs 13:silver  ts 1:bronze
     *
     */

    FILE           *inp = NULL;
    char           *token[1024];
    char            buff[1024];
    char           *tmpBS=NULL, *tmpTS=NULL, *tmpSS=NULL, *tmpODB=NULL, *tmpAPN=NULL;

    if ( ( inp = fopen( pFile, "r" ) ) == NULL )
    {
	printf( "Cannot open authGateway configuration file %s \n", pFile );
	return -1;
    }

    for ( ;; )
    {
	if ( fgets( buff, sizeof( buff ), inp ) == NULL )
	    break;

	if ( isComment( buff ) == 0 )
	{
	    int             i = 0, numTokens = 0;
	    
	    /*first split the buff into tokens */
	    token[numTokens++] = strtok( buff, ",,=, ,\t\n" );

	    while ( ( token[numTokens++] = strtok( NULL, ",,=, ,\t\n" ) ) );

	    numTokens--;

	    if ( numTokens > 0 )
	    {
		if ( Pglobal->sccpAddrCount >= MAX_NUM_SCCP_ADDR )
		{
		    fprintf( stderr, 
			    "Maximum Number of lines (%d) in the MAP-GW conf file reached\n", 
			    MAX_NUM_SCCP_ADDR );
		    (void)fclose(inp);
		    return 0;
		}
	    }

	    for ( i = 0; i < numTokens; i++ )
	    {
		if ( strcmp( token[i], "rri" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			int temp = atoi( token[i] );

			if( temp < 0 || temp > 1 )
			{
			    fprintf( stderr, "rri %d out of range\n", temp );
			    ( void ) fclose( inp );
			    return -1;
			}

			Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_RI = temp;
			fprintf( stderr, "Pglobal->sccpAddrArray[%d].REMOTE_RI = %d\n",
				 Pglobal->sccpAddrCount, 
				 Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_RI );
		    }
		    else
		    {
			fprintf( stderr, 
				"Error at line number %d: Option rri must be followd by a value, taking default value.\n", 
				Pglobal->sccpAddrCount );
			( void ) fclose( inp );
			return -1;
		    }
		}


		if ( strcmp( token[i], "rpc" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
//			Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_PC = convertPC( token[++i] );
			Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_PC = convertPC( token[i] );
			fprintf( stderr, "Pglobal->sccpAddrArray[%d].REMOTE_PC = %d\n",
				 Pglobal->sccpAddrCount, Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_PC );

		    }
		    else
		    {
			fprintf( stderr, 
				"Error at line number %d: Option rpc must be followed by a value.\n", 
				Pglobal->sccpAddrCount );
			( void ) fclose( inp );
			return -1;
		    }
		}



		if ( strcmp( token[i], "rssn" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			int temp = atoi( token[i] );

			if( temp < 0 || temp > 255 )
			{
			    fprintf( stderr, "rssn %d out of range\n", temp );
			    ( void ) fclose( inp );
			    return -1;
			}
		
			Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_SSN = ( U8 ) temp;
			fprintf( stderr, "Pglobal->sccpAddrArray[%d].REMOTE_SSN = %d\n",
				 Pglobal->sccpAddrCount, 
				 Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_SSN );

		    }
		    else
		    {
			fprintf( stderr, 
				"Error at line number %d: Option rssn must be followd by a value, taking default value.\n", 
				Pglobal->sccpAddrCount );
			( void ) fclose( inp );
			return -1;
		    }
		}


		if ( strcmp( token[i], "rnai" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			switch ( ( U8 ) atoi( token[i] ) )
			{
			case natureOfAddressNotPresent:
			case msisdnSubscriberNumber:
			case msisdnNationalSignificantNumber:
			case msisdnInternationalNumber:
			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_NAI = ( U8 ) atoi( token[i] );
			    break;
			default:
			    fprintf( stderr, 
				    "Error at line number %d: Invalid nai, taking default value \n", 
				    Pglobal->sccpAddrCount );
			}
		    }
		    else
		    {
			fprintf( stderr, 
				"Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "rnp" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			switch ( ( U8 ) atoi( token[i] ) )
			{
			case e164:
			case e212:
			case e214:
			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_NP = ( U8 ) atoi( token[i] );
			    break;
			default:
			    fprintf( stderr, 
				    "Error at line number %d: Invalid value specified for option rnp,"
				     " using default value\n", 
				     Pglobal->sccpAddrCount );
			}
		    }
		    else
		    {
			fprintf( stderr, 
				"Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}
		
		if ( strcmp( token[i], "rgti" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			if ( ( U8 ) atoi( token[i] ) == GT_0100 ||
			     ( U8 ) atoi( token[i] ) == GT_0000 || 
			     ( U8 ) atoi( token[i] ) == GT_0010 || 
			     ( U8 ) atoi( token[i] ) == GT_0001 )
			{
//			    i++;
			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_GTI = 
				( U8 ) atoi( token[i] );
			}
			else
			{
			    fprintf( stderr, "Error at line number %d: Invalid value specified for option rgti "
				     "Only 0 and 4 are supported for ITU and 1 and 2 for ANSI. \n", 
				     Pglobal->sccpAddrCount );
			    ( void ) fclose( inp );

			    return -1;
			}
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "rtt" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			int temp = atoi( token[i] );

			if( temp < 0 || temp > 255 )
			{
			    fprintf( stderr, "rtt %d out of range\n", temp );
			    ( void ) fclose( inp );
			    return -1;
			}
		
			Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_TT = ( U8 ) temp;
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "odigits" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			strcpy( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].old_digits, token[i] );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			printf( "Closed the file\n" );
			return -1;
		    }
		}

		if ( strcmp( token[i], "ndigits" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			strcpy( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].new_digits, token[i] );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "msisdn" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens )
		    {
			strcpy( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].msisdn, token[i] );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}



		if ( strcmp( token[i], "apn" ) == 0 )
		{
		    if ( ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].apnCount ) < MAX_APN_NAMES )
		    {
			if ( ( i + 1 ) < numTokens )
			{
			    int             tempSubscript;
			    tempSubscript = Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].apnCount;
			    tmpAPN = token[++i];
			    fprintf( stderr, "APN is %s , index is %d\n", tmpAPN, tempSubscript );
			    
				int ret = regcomp(&Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].apnRegex[tempSubscript], tmpAPN, REG_ICASE);
				if (ret){
					fprintf( stderr, "APN Regex: %s compilation failed.\n", tmpAPN);
					( void ) fclose( inp );
					return -1;
				}
				
				strcpy(Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].apnName[tempSubscript], tmpAPN);
				
			    if ( tempSubscript != 0 ) 
				fprintf( stderr, "At line number %d: APN string [%d] is %s \n",
					 Pglobal->sccpAddrCount,
					 tempSubscript - 1, 
					 Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].apnName[tempSubscript - 1] );
				
			    fprintf( stderr, "At line number %d: APN string [%d] is %s \n",
				     Pglobal->sccpAddrCount, 
				     tempSubscript, 
				     Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].apnName[tempSubscript] );
				     
			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].apnCount++;
			}
			else
			{
			    fprintf( stderr, "Error at line number %d: premature EOL found\n", 
				    Pglobal->sccpAddrCount );
			    ( void ) fclose( inp );
			    return -1;
			}
		    }
		    else
		    {
			tmpAPN = token[++i];
			fprintf( stderr, "APN is %s , only %d apn names allowed, ignored\n", tmpAPN, MAX_APN_NAMES );
		    }
		}



		if ( strcmp( token[i], "bs" ) == 0 )
		{
		    if ( ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].bsCodesCount ) < MAX_BS_CODES )
		    {
			if ( ( i + 1 ) < numTokens )
			{
			    int             tempSubscript;
			    tempSubscript = Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].bsCodesCount;
			    tmpBS = token[++i];
			    fprintf( stderr, "BS is %s , index is %d\n", tmpBS, tempSubscript );
			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].bsCodes[tempSubscript] = 
				strtoul( strtok( tmpBS, ":" ), NULL, 10 );
			    wlan_strlcpy( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].bsString[tempSubscript], 
				    strtok( NULL, ":" ), 
				    MAX_STRING_SIZE );

			    if ( tempSubscript != 0 )
				fprintf( stderr, "At line number %d: BS string [%d] is %s \n",
					 Pglobal->sccpAddrCount,
					 tempSubscript - 1, 
					 Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].bsString[tempSubscript - 1] );

			    fprintf( stderr, "At line number %d: BS string [%d] is %s \n",
				     Pglobal->sccpAddrCount, 
				     tempSubscript, 
				     Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].bsString[tempSubscript] );

			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].bsCodesCount++;
			}
			else
			{
			    fprintf( stderr, "Error at line number %d: premature EOL found\n", 
				    Pglobal->sccpAddrCount );
			    ( void ) fclose( inp );
			    return -1;
			}
		    }
		    else
		    {
			tmpBS = token[++i];
			fprintf( stderr, "BS is %s , only %d bs codes allowed, ignored\n", tmpBS, MAX_BS_CODES );
		    }
		}

		if ( strcmp( token[i], "ts" ) == 0 )
		{

		    if ( ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].tsCodesCount ) < MAX_TS_CODES )
		    {
			if ( ( i + 1 ) < numTokens )
			{
			    int tempSubscript = Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].tsCodesCount;
			    
			    tmpTS = token[++i];

			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].tsCodes[tempSubscript] = 
				strtoul( strtok( tmpTS, ":" ), NULL, 10 );
			    wlan_strlcpy( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].tsString[tempSubscript], 
				    strtok( NULL, ":" ), MAX_STRING_SIZE );



			    fprintf( stderr, "At line %d, TS string [%d] is %s \n",
				     Pglobal->sccpAddrCount, 
				     tempSubscript, 
				     Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].tsString[tempSubscript] );

			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].tsCodesCount++;
			}
			else
			{
			    fprintf( stderr, "Error at line number %d: premature EOL found\n", 
				    Pglobal->sccpAddrCount );
			    ( void ) fclose( inp );
			    return -1;
			}
		    }
		    else
		    {
			tmpBS = token[++i];
			fprintf( stderr, "ts is %s , only %d ts codes allowed, ignored\n", tmpTS, MAX_TS_CODES );
		    }
		}

		if ( strcmp( token[i], "odb" ) == 0 )
		{
		    if ( ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].odbCodesCount ) < MAX_ODB_CODES )
		    {
			if ( ( i + 1 ) < numTokens )
			{
			    int tempSubscript = Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].odbCodesCount;
			    
			    tmpODB = token[++i];
			    /* 
			       Used a separate function to convert the HEX String value into a Unsigned Int
			     */

			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].odbCodes[tempSubscript] = 
				strtoul( strtok( tmpODB, ":" ), NULL, 10 );



			    printf( "value of ODB at tempSubscript is %u\n", 
				    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].odbCodes[tempSubscript] );

			    wlan_strlcpy( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].odbString[tempSubscript], 
				    strtok( NULL, ":" ), MAX_STRING_SIZE );
			    fprintf( stderr, "At line %d, ODB string [%d] is %s \n",
				     Pglobal->sccpAddrCount, 
				     tempSubscript, 
				     Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].odbString[tempSubscript] );

			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].odbCodesCount++;
			}
			else
			{
			    fprintf( stderr, "Error at line number %d: premature EOL found\n", 
				    Pglobal->sccpAddrCount );
			    ( void ) fclose( inp );
			    return -1;
			}
		    }
		    else
		    {
			tmpBS = token[++i];
			fprintf( stderr, "odb is %s , only %d odb codes allowed, ignored\n", tmpODB, MAX_ODB_CODES );
		    }
		}

		if ( strcmp( token[i], "ss" ) == 0 )
		{
		    if ( ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].ssCodesCount ) < MAX_SS_CODES )
		    {
			if ( ( i + 1 ) < numTokens )
			{
			    int  tempSubscript = Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].ssCodesCount;
			    tmpSS = token[++i];

			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].ssCodes[tempSubscript] = 
				strtoul( strtok( tmpSS, ":" ), NULL, 10 );
			    wlan_strlcpy( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].ssString[tempSubscript], 
				    strtok( NULL, ":" ), MAX_STRING_SIZE );
			    fprintf( stderr, "At line %d, SS string [%d] is %s \n",
				     Pglobal->sccpAddrCount, 
				     tempSubscript, 
				     Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].ssString[tempSubscript] );

			    Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].ssCodesCount++;
			}
			else
			{
			    fprintf( stderr, "Error at line number %d: premature EOL found\n", 
				    Pglobal->sccpAddrCount );
			    ( void ) fclose( inp );
			    return -1;
			}
		    }
		    else
		    {
			tmpBS = token[++i];
			fprintf( stderr, "ss is %s , only %d ss codes allowed, ignored\n", tmpSS, MAX_SS_CODES );
		    }
		}
		/* CR 27676: command-line too long, allow optional arguments */
		/* in config file too */

		if ( strcmp( token[i], "lri" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			int temp = atoi( token[i] );

			if( temp < 0 || temp > 1 )
			{
			    fprintf( stderr, "lri %d out of range\n", temp );
			    ( void ) fclose( inp );
			    return -1;
			}

			Pglobal->LOCAL_RI = temp;
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "lpc" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			Pglobal->LOCAL_PC = convertPC( token[i] );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "lssn" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			int temp = atoi( token[i] );

			if( temp < 0 || temp > 255 )
			{
			    fprintf( stderr, "lssn %d out of range\n", temp );
			    ( void ) fclose( inp );
			    return -1;
			}
		
			Pglobal->GMAPINIT.ssn = ( U8 ) temp;
			Pglobal->LOCAL_SSN = ( U8 ) temp;
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "lnai" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			switch ( ( U8 ) atoi( token[i] ) )
			{
			case natureOfAddressNotPresent:
			case msisdnSubscriberNumber:
			case msisdnNationalSignificantNumber:
			case msisdnInternationalNumber:
			    Pglobal->LOCAL_NAI = ( U8 ) atoi( token[++i] );
			    break;
			default:
			    fprintf( stderr,
				"Invalid value specified for option -lnai\n" );
			    ( void ) fclose( inp );
			    return -1;
                	}

		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "lnp" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			switch ( ( U8 ) atoi( token[i] ) )
			{
			case e164:
			case e212:
			case e214:
			    Pglobal->LOCAL_NP = ( U8 ) atoi( token[i] );
			    break;
			default:
			    fprintf( stderr,
				"Invalid value specified for option lnp\n" );
			    ( void ) fclose( inp );
			    return -1;
                	}

		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "lgti" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			if ( ( U8 ) atoi( token[i] ) <= 4 )
				Pglobal->LOCAL_GTI = ( U8 ) atoi( token[i] );
			else
			    fprintf( stderr,
				"Invalid value specified for option -lgti," "using default value %d",
				Pglobal->LOCAL_GTI );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "ltt" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			int temp = atoi( token[i] );

			if( temp < 0 || temp > 255 )
			{
			    fprintf( stderr, "ltt %d out of range\n", temp );
			    ( void ) fclose( inp );
			    return -1;
			}
		
			Pglobal->LOCAL_TT = ( U8 ) temp;
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if (( strcmp( token[i], "lmsisdn" ) == 0 ) || ( strcmp( token[i], "ldigits" ) == 0 ))
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			( void ) strcpy( Pglobal->LOCAL_MSISDN, token[i] );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "invktimeout" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			Pglobal->invokeTimeout = atoi( token[i] );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "invkretry" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			Pglobal->nbInvokeRetry = atoi( token[i] );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "conntimeout" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			_connection_timeout = atoi( token[i] );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "connretry" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			_connection_retry = atoi( token[i] );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "max_requests" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			Pglobal->max_requests = atoi( token[i] );
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

		if ( strcmp( token[i], "appctx" ) == 0 )
		{
		    if ( ( i + 1 ) < numTokens && TRUE == begins_with_digit( token[++i] ) )
		    {
			if ( ( atoi( token[i] ) == 2 ) || ( atoi( token[i] ) == 3) )
			{
			    Pglobal->ApplicationContext = atoi( token[i] );
			    fprintf( stderr, "Pglobal->ApplicationContext = %d\n", Pglobal->ApplicationContext );
			}
			else
			{
			    fprintf( stderr,
				"This application supports only application contexts version 2 or 3, taking default value=%d\n",
				  Pglobal->ApplicationContext );
			}
		    }
		    else
		    {
			fprintf( stderr, "Error at line number %d: Option %s must be followed by a value\n", 
				Pglobal->sccpAddrCount, token[i - 1] );
			( void ) fclose( inp );
			return -1;
		    }
		}

	    }

	    if ( ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].old_digits ) != 0 )
		 && ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].msisdn ) != 0 ) )
	    {
		fprintf( stderr, "Error at line number %d: Cannot have msisdn and odigits on the same line.\n", 
			Pglobal->sccpAddrCount + 1 );
		( void ) fclose( inp );
		return -1;

	    }

	    if ( ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].old_digits ) == 0 )
		 && ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].msisdn ) == 0 ) )
	    {
		fprintf( stderr, "Error at line number %d: No old digits present and no msisdn present.\n", 
			Pglobal->sccpAddrCount + 1 );
		( void ) fclose( inp );
		return -1;
	    }

	    if ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_GTI != 0 )
	    {
		if ( ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].new_digits ) == 0 )
		     && ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].msisdn ) == 0 ) )
		{
		    fprintf( stderr, 
			    "Error at line number %d: New Digits or MSISDN are required to route on GT.\n", 
			    Pglobal->sccpAddrCount + 1 );
		    ( void ) fclose( inp );
		    return -1;
		}

		/*if ( ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].new_digits ) !=
		       strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].old_digits ) ) )
		    if ( strstr( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].old_digits, "*" ) == NULL )
		    {
			fprintf( stderr, 
				"Error at line number %d: Length of old digits and " "new digits should be the same.\n", 
				Pglobal->sccpAddrCount + 1 );
			( void ) fclose( inp );
			return -1;
		    }*/
	    }

	    if ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_RI == routeOnGt )
	    {
		if ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_GTI == GT_0000 )
		{
		    fprintf( stderr, 
			    "Error at line number %d: Remote GTI cannot be 0 because remote RI is routeOnGt.\n", 
			    Pglobal->sccpAddrCount + 1 );
		    ( void ) fclose( inp );
		    return -1;
		}

		if ( ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].msisdn ) == 0 )
		     && ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].new_digits ) == 0 ) )
		{
		    fprintf( stderr, 
			    "Error at line number %d: New digits required as RI = routeOnGt.\n", 
			    Pglobal->sccpAddrCount + 1 );
		    ( void ) fclose( inp );
		    return -1;
		}

		/*if ( ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].msisdn ) == 0 )
		     && ( ( strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].new_digits ) !=
			    strlen( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].old_digits ) ) ) )
		    if ( strstr( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].old_digits, "*" ) == NULL )

		    {
			fprintf( stderr, 
				"Error at line number %d: Length of old digits and " "new digits should be the same.\n", 
				Pglobal->sccpAddrCount + 1 );
			( void ) fclose( inp );
			return -1;
		    }*/
	    }
	    else if ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_RI == routeOnDpc )
	    {
		if ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_PC == 0 )
		{
		    fprintf( stderr, 
			    "Error at line number %d: rpc must be specified if rri=routeOnDpc.\n", 
			    Pglobal->sccpAddrCount + 1 );
		    ( void ) fclose( inp );
		    return -1;
		}
		
		if ( Pglobal->sccpAddrArray[Pglobal->sccpAddrCount].REMOTE_SSN == 0 )
		{
		    fprintf( stderr, 
			    "Error at line number %d: rssn must be specified if rri=routeOnDpc.\n", 
			    Pglobal->sccpAddrCount + 1 );
		    ( void ) fclose( inp );
		    return -1;
		}
	    }
	    Pglobal->sccpAddrCount++;
	}
    }

    ( void ) fclose( inp );

    if( 0 == Pglobal->sccpAddrCount )
    {
	printf("No valid non-commented lines in configuration file.\n");
	return -1;
    }
    else
    {
	return 1;
    }
}

/* FUNCTION main()                            */
/*************************************************************************

FUNCTION
        int main (int argc, char *argv[])

DESCRIPTION
        This is the process entry point.

INPUTS
        Arg:    argc    - count of command line arguments
                argv[]  - list of command line arguement pointers

        Global: None
OUTPUTS
        Return: None

FEND
*************************************************************************/

int
main( int argc, char *argv[] )
{
    int             i, j;
    int             ret_code;
    int             confFound = 0;
    char           *pchr = "C7";	/* default protocol type          */
    char            processName[MAXcptNAME + 1] = "GMT";
    int             monitor_flag = FALSE;	/* process monitor                */
    int             restart_flag = AUTOrestart;	/* allow restart of process       */
    char            stand_alone = TRUE;	/* pgm is started from start file, */
                                        /* not from cmd line.        */
    char            logical_name[MAXlogicalNAME + 1];
    BOOL            restart = FALSE;

    /*  All our global variables are stored in shared memory--So we need to
     *  create or attach this shared memory segment before processing all
     *  of our command line arguments.
     *
     *  So, we first go through and look for -start/-restart (to determine
     *  if we create or attach (respectively) the shared memory segment.
     *
     *  This is a bit of a waste of time, but not too much...  :-(
     */

    for ( i = 1; i < argc; i++ )
    {
	if ( strcmp( argv[i], "-start" ) == 0 )
	    stand_alone = FALSE;

	if ( strcmp( argv[i], "-restart" ) == 0 )
	{
	    stand_alone = FALSE;
	    /*  We don't want do do anything specific in case of restart.
	     *  We treat it as a fresh start.
	     */
	}

	if ( ( ( i + 1 ) < argc ) && ( strcmp( argv[i], "-name" ) == 0 ) )
	    ( void ) strcpy( processName, argv[i + 1] );
    }

    if ( argc < 2 )
    {
	printf( "%s  -port <port number> -host <hostname> -node <node name> -prot <A7, C7, or CH7> \n", argv[0] );
	printf( "-conf <configuration file>\n" );
	printf( "-lpc <local PC> -lssn <local SSN> -ldigits <local digits>\n" );
	printf( "[-lri  <local Routing Indicator>]" );
	printf( "[-lnp  <local Numbering Plan>]\n" );
	printf( "[-lnai <local Nature of Address Indicator>]" );
	printf( "[-lgti <local Global Title Indicator>]" );
	printf( "[-ltt  <local Translation Type>]\n" );
	printf( "[-invktimeout <duration of invoke timeout in seconds>]" );
	printf( "[-invkretry   <number of invoke retry>\n" );
	printf( "[-conntimeout <duration of connection timeout in minutes>\n" );
	printf( "[-connretry   <number of connection bind attempts>\n" );
	printf( "[-debug] [-monitor] [0x<gsmdebugmask>] [-no_rst]\n" );
	printf( "[-max_requests <maximum number of pending requests>]\n " );
	printf( "[-appctx <application context version initially tried>]\n" );
	printf( "[-name <process name>]\n" );
/*
** CR 32895 -- remove -nosegment option so that its not customer visible
  	printf( "[-nosegment]<Disable MAP level segmentation>]\n" );
*/
        

	printf( "\n" );
	printf( "    default Values:\n" );
	printf( "    ---------------\n" );
	printf( "    -name = GMT\n" );
	printf( "    -prot = C7\n" );
	printf( "    -appctx =%d\n", DEFAULT_APP_CONTEXT );
	printf( "    -lri = %d\n", routeOnGt );
	printf( "    -lnp = %d\n", DEFAULT_LOCAL_NP );
	printf( "    -lnai =  %d\n", msisdnInternationalNumber );
	printf( "    -lgti =  %d\n", DEFAULT_LOCAL_GTI );
	printf( "    -ltt =  %d\n", DEFAULT_LOCAL_TT );
	printf( "    -max_requests = %d\n", DEFAULT_MAX_REQUESTS );
	printf( "    -invktimeout  = %d (seconds)\n", DEFAULT_INVOKE_TO );
	printf( "    -invkretry = %d \n", DEFAULT_INVOKE_RETRY );
	printf( "    -conntimeout  = %d (minutes, 0 for no timeout)\n", DEFAULT_CONNECT_TIMEOUT );
	printf( "    -connretry = %d \n", DEFAULT_CONNECT_RETRY );
	printf( "    debug = FALSE, no_rst = FALSE(AUTOrestart)\n" );
	printf( "    default alone = FALSE, <gsmdebugmask> = 01\n" );
	
	printf( "    sizeof(WlanPdu) = %d\n", (int) sizeof(WlanPdu));
	printf( "    sizeof(ulcm_UpdateGprsRequest) = %d\n", (int) sizeof(ulcm_UpdateGprsRequest));
	printf( "    sizeof(ulcm_UpdateGprsResponse) = %d\n", (int) sizeof(ulcm_UpdateGprsResponse));
/*
** CR 32895 -- remove -nosegment option so that its not customer visible
	printf( "    nosegment = FALSE\n" );
*/

	exit(0);
    }

    if (getenv( "SHM" ) == NULL)
    {
        printf( "Environment variable SHM is not set and is required.\n");
        exit(1);
    }
    else
    {
        printf( "SHM=%s\n", getenv( "SHM" ));
    }
    if (getenv( "OMNI_HOME" ) == NULL)
    {
        printf( "Environment variable OMNI_HOME is not set and is required.\n");
        exit(1);
    }
    else
    {
        printf( "OMNI_HOME=%s\n", getenv( "OMNI_HOME" ));
    }
    printf( "argv[0]=%s\n", argv[0] );
    printf( "processName=%s\n", processName );

/*
 *    register the application
 */
    if ( stand_alone == TRUE )
    {
	( void ) snprintf( logical_name, sizeof( logical_name ), "%s", processName );
	ret_code = FtAttach( logical_name,	/* process logical name     */
			     argv[0],	/* process executable name  */
			     " ",	/* execution parameters     */
			     0,	/* execution priority       */
			     0,	/* RT time quantum          */
			     0,	/* RT time quantum          */
			     0,	/* process class identifier */
			     10 );	/* max. wait for CPT entry  */

	if ( ret_code == RETURNerror )
	{
	    FtAnalyzeErrno( __FILE__, __LINE__ );
	    fprintf( stderr, "   FtAttach(), errno=%d(%s)\n", errno, LastErrorReport );
	    exit( 0 );
	}
    }


    CREATE_OR_ATTACH_SHM( processName, global, gmaplib_t, sizeof( gmaplib_t ), &sm.global )

	/* Only fill in default values on fresh start */
    if ( !restart )
    {
	/* So we can safety check our data */
	Pglobal->bfence = FENCE;
	Pglobal->efence = FENCE;

	/* default values */
	Pglobal->GMAPINIT.ssn = DEFAULT_LOCAL_SSN;
	Pglobal->GMAPINIT.protocol = DEFAULT_PROTOCOL;
	strncpy( Pglobal->GMAPINIT.nodeName, DEFAULT_NODENAME, sizeof( Pglobal->GMAPINIT.nodeName ) );
	Pglobal->GMAPINIT.debugFile = stdout;

	strncpy( Pglobal->PROCESS_NAME, processName, sizeof( Pglobal->PROCESS_NAME ) );
	Pglobal->psock = -1;
	Pglobal->sock = -1;
	strncpy( Pglobal->hostname, DEFAULT_HOSTNAME, sizeof( Pglobal->hostname ) );
	Pglobal->invokeTimeout = DEFAULT_INVOKE_TO;	/* in seconds */
	Pglobal->nbInvokeRetry = DEFAULT_INVOKE_RETRY;
	Pglobal->max_requests = DEFAULT_MAX_REQUESTS;

	/** declare the variables used to form the calling SCCP address */
	Pglobal->LOCAL_RI = routeOnGt;
	Pglobal->LOCAL_NAI = msisdnInternationalNumber;
	Pglobal->LOCAL_NP = DEFAULT_LOCAL_NP;
	Pglobal->LOCAL_GTI = DEFAULT_LOCAL_GTI;
	Pglobal->LOCAL_TT = DEFAULT_LOCAL_TT;
	Pglobal->LOCAL_PC = DEFAULT_LOCAL_PC;
	Pglobal->LOCAL_SSN = DEFAULT_LOCAL_SSN;
	strncpy( Pglobal->LOCAL_MSISDN, DEFAULT_LOCAL_MSISDN, sizeof( Pglobal->LOCAL_MSISDN ) );

	Pglobal->ApplicationContext = DEFAULT_APP_CONTEXT;

	/*Initialise the SCCP Address structure */
	memset( &Pglobal->sccpAddrArray, 0, sizeof( Pglobal->sccpAddrArray ) );
	
	for ( i = 0; i < MAX_NUM_SCCP_ADDR; i++ )
	{
	    Pglobal->sccpAddrArray[i].REMOTE_NP = DEFAULT_REMOTE_NP;
	}
    }
    else if ( Pglobal->bfence != FENCE || Pglobal->efence != FENCE )
    {

	/* The shared memory got corrupted */
	fprintf( stderr, 
		"Shared memory invalid on restart!\n\tbfence=0x%x, efence=0x%x\n", 
		Pglobal->bfence, Pglobal->efence );
	terminate( TERMINATE_CAUSE_SHMCORRUPT );

    }
    else
    {
	/* restart is not supported in this version.
	 * This portion of code is never executed  */

	/* Some things in shared memory must be reset if we restart */
	Pglobal->GMAPINIT.debugFile = stdout;

	Pglobal->psock = -1;
	Pglobal->sock = -1;
    }


/*
 *    parse command line arguments, and setup proper global variables
 */
    for ( i = 1; i < argc; i++ )
    {
    	if ( strcmp( argv[i], "-phase" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
		( void ) strcpy( supportedCamelPhases, argv[i + 1] );
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
		
	}
		
	if ( strcmp( argv[i], "-port" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
	    {
		if ( !( Pglobal->port = ( int ) strtol( argv[i + 1], 0, 0 ) ) )
		{
		    fprintf( stderr, "(%s): Invalid port number '%s' %d\n", 
			    Pglobal->PROCESS_NAME, argv[i + 1], i );
		    exit( EXIT_FAILURE );
		}
	    }
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}
	
	if ( strcmp( argv[i], "-prot" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
	    {
		if ( strcmp( argv[i + 1], "a7" ) == 0 || strcmp( argv[i + 1], "A7" ) == 0 )
		{
		    Pglobal->GMAPINIT.protocol = ansi7;
		    pchr = "A7";
		}
		else if ( strcmp( argv[i + 1], "ch7" ) == 0 || strcmp( argv[i + 1], "CH7" ) == 0 )
		{
		    Pglobal->GMAPINIT.protocol = chinese7;
		    pchr = "CH7";
		}
		else if ( strcmp( argv[i + 1], "c7" ) == 0 || strcmp( argv[i + 1], "C7" ) == 0 )
		{
		    Pglobal->GMAPINIT.protocol = itu7;
		    pchr = "C7";
		}
		else
		{
		    pchr = "Invalid Protocol";
		    fprintf( stderr, "Invalid value %s for option -prot\n", argv[i + 1] );
		    exit( 1 );

		}
	    }
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-node" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
		( void ) strcpy( Pglobal->GMAPINIT.nodeName, argv[i + 1] );
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-name" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
		( void ) strcpy( Pglobal->PROCESS_NAME, argv[i + 1] );
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-invktimeout" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
		Pglobal->invokeTimeout = atoi( argv[i + 1] );
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-invkretry" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
		Pglobal->nbInvokeRetry = atoi( argv[i + 1] );
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-appctx" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
	    {
		if ( ( atoi( argv[i + 1] ) == 2 ) || ( atoi( argv[i + 1] ) == 3 ) )
		{
		    Pglobal->ApplicationContext = atoi( argv[i + 1] );
		    fprintf( stderr, "Pglobal->ApplicationContext = %d\n", Pglobal->ApplicationContext );
		}
		else
		{
		    fprintf( stderr, 
			    "This application supports only application contexts version 2 or 3\n" );
		    exit( EXIT_FAILURE );
		}
	    }
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}


	if ( strcmp( argv[i], "-max_requests" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
		Pglobal->max_requests = atoi( argv[i + 1] );
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-msisdnlength" ) == 0 )
	{
	    if ( ( i + 1 ) < argc ){
		msisdnlength = atoi( argv[i + 1] );
		if(msisdnlength % 2 != 0 ){
			fprintf( stderr, "Option %s must be a even value\n", argv[i] );
			exit( 1 );
		}
		msisdnlength = msisdnlength / 2;
	    }else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-host" ) == 0 )
	{
	    if ( i == argc )
	    {
		fprintf( stderr, " -h requires local hostname argument\n" );
		exit( EXIT_FAILURE );
	    }
	    
	    ( void ) strcpy( Pglobal->hostname, argv[++i] );
	}

	if ( strcmp( argv[i], "-lri" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
	    {
		int temp = atoi( argv[i + 1] );

		if( temp < 0 || temp > 1 )
		{
		    fprintf( stderr, "lri %d out of range\n", temp );
		    exit( 1 );
		}

		Pglobal->LOCAL_RI = temp;
		fprintf( stderr, "Pglobal->LOCAL_RI = %d\n", Pglobal->LOCAL_RI );
	    }
	    else
	    {
		fprintf( stderr, "Option -lri must be followd by a value.\n" );
		exit( EXIT_FAILURE );
	    }
	}

	if ( strcmp( argv[i], "-lpc" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
		Pglobal->LOCAL_PC = convertPC( argv[i + 1] );
	    else
	    {
		fprintf( stderr, "Option -lpc must be followd by a PC\n" );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-lssn" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
	    {
		int temp = atoi( argv[i + 1] );

		if( temp < 0 || temp > 255 )
		{
		    fprintf( stderr, "SSN %d out of range\n", temp );
		    exit( 1 );
		}
		
		Pglobal->GMAPINIT.ssn = ( U8 ) temp;
		Pglobal->LOCAL_SSN    = ( U8 ) temp;
	    }
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-lnai" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
	    {
		switch ( ( U8 ) atoi( argv[i + 1] ) )
		{
		case natureOfAddressNotPresent:
		case msisdnSubscriberNumber:
		case msisdnNationalSignificantNumber:
		case msisdnInternationalNumber:
		    Pglobal->LOCAL_NAI = ( U8 ) atoi( argv[i + 1] );
		    break;
		default:
		    fprintf( stderr, 
			    "Invalid value specified for option -lnai\n" );
		    exit( EXIT_FAILURE );
		}
	    }
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-lnp" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
	    {
		switch ( ( U8 ) atoi( argv[i + 1] ) )
		{
		case e164:
		case e212:
		case e214:
		    Pglobal->LOCAL_NP = ( U8 ) atoi( argv[i + 1] );
		    break;
		default:
		    fprintf( stderr, 
			    "Invalid value specified for option -lnp\n" );
		exit( EXIT_FAILURE );
		}
	    }
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-lgti" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
	    {
		if ( ( U8 ) atoi( argv[i + 1] ) <= 4 )
		{
		    Pglobal->LOCAL_GTI = ( U8 ) atoi( argv[i + 1] );
		    printf("Using lgti=%d, input from the command line\n", Pglobal->LOCAL_GTI);
		}
		else
		{
		    fprintf( stderr, 
			    "Invalid value specified for option -lgti" );
		    exit( EXIT_FAILURE );
		}
	    }
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( strcmp( argv[i], "-ltt" ) == 0 )
	{
	    if ( ( i + 1 ) < argc && TRUE == begins_with_digit( argv[i + 1] ) )
	    {
		int temp = atoi( argv[i + 1] );

		if( temp < 0 || temp > 255 )
		{
		    fprintf( stderr, "ltt %d out of range\n", temp );
		    exit( 1 );
		}
		
		Pglobal->LOCAL_TT = ( U8 ) temp;
	    }
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	if ( ( strcmp( argv[i], "-lmsisdn" ) == 0 ) || ( strcmp( argv[i], "-ldigits" ) == 0 ) )
	{
	    if ( ( i + 1 ) < argc )
	    {
		( void ) strcpy( Pglobal->LOCAL_MSISDN, argv[++i] );
	    }
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}


	if ( strcmp( argv[i], "-conf" ) == 0 )
	{
	    int             iRet = 0;

	    confFound = 1;
	    printf( "Entering conf file...\n" );
	    iRet = initConfParams( argv[i + 1] );

	    if ( iRet <= -1 )
	    {
		fprintf( stderr, " \nError in Configuration File %s at line number %d\n\n", 
			argv[i + 1], Pglobal->sccpAddrCount );
		exit( EXIT_FAILURE );

	    }
	}

	if ( strcmp( argv[i], "-tracefile" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
	    {
		Pglobal->GMAPINIT.debugFile = fopen( argv[i + 1], "w" );
		Pglobal->dbg.trace_file = Pglobal->GMAPINIT.debugFile;
		Pglobal->dbg.newTraceFile = 1;
		Pglobal->dbg.trace_enabled = TRUE;

		if ( !Pglobal->GMAPINIT.debugFile )
		{
		    fprintf( stderr, "Unable to open trace file %s:%s.\n" "Using stdout instead\n", 
			    argv[i + 1], strerror( errno ) );
		    Pglobal->GMAPINIT.debugFile = stdout;
		}
	    }
	    else
	    {
		fprintf( stderr, "Option -tracefile must be followed by the name of a file\n" );
		exit( 1 );
	    }
	}
	if ( strcmp( argv[i], "-conntimeout" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
		_connection_timeout = atoi( argv[i + 1] );
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}
	if ( strcmp( argv[i], "-connretry" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
		_connection_retry = atoi( argv[i + 1] );
	    else
	    {
		fprintf( stderr, "Option %s must be followed by a value\n", argv[i] );
		exit( 1 );
	    }
	}

	/*
	 *  Flags
	 */

	if ( strcmp( argv[i], "-no_rst" ) == 0 )
	    restart_flag = NOrestart;

	if ( strcmp( argv[i], "-debug" ) == 0 )
        {
	    g_trace_enabled = Pglobal->dbg.trace_enabled = TRUE;
        }

	if ( strcmp( argv[i], "-monitor" ) == 0 )
	    monitor_flag = TRUE;

	if ( strcmp( argv[i], "-nosegment" ) == 0 )
	    segment_flag = FALSE;
    }

    /*
     * Check mandatory parameters
     */

    if ( confFound == 0 )
    {
	fprintf( stderr, " Missing required argument: -conf <Gateway conf file> \n" );
	exit( EXIT_FAILURE );
    }

    if ( Pglobal->port == 0 )
    {
	fprintf( stderr, "Missing required argument: -port <port number>\n" );
	exit( EXIT_FAILURE );
    }

    if ( strcmp( Pglobal->hostname, "myHostname" ) == 0 )
    {
	fprintf( stderr, " Missing required argument: -host <local hostname>\n" );
	exit( EXIT_FAILURE );
    }

    if ( strcmp( Pglobal->GMAPINIT.nodeName, "XX" ) == 0 )
    {
	fprintf( stderr, " Missing required argument: -node <node name>\n" );
	exit( EXIT_FAILURE );
    }

    if ( Pglobal->max_requests >= MAX_MAX_REQUESTS - 10 )
    {
	fprintf( stderr, "max_requests (%d) exceeds max (%d), using %d!\n", 
		Pglobal->max_requests, MAX_MAX_REQUESTS, MAX_MAX_REQUESTS );

	Pglobal->max_requests = MAX_MAX_REQUESTS;
    }

    /* If we send more than one invoke per dialog, change the nDialogs */
    Pglobal->GMAPINIT.nDialogs = Pglobal->max_requests;
    Pglobal->GMAPINIT.nInvokes = Pglobal->max_requests;

    printf( "%s: Using port=%d\n", Pglobal->PROCESS_NAME, Pglobal->port );
    printf( "%s: Using hostname=%s\n", Pglobal->PROCESS_NAME, Pglobal->hostname );
    printf( "PROTOCOL=%d(%s), NODE=%s, ALONE=%d, PROCESS_NAME=%s\n",
	    Pglobal->GMAPINIT.protocol, pchr, Pglobal->GMAPINIT.nodeName, 
	    stand_alone, Pglobal->PROCESS_NAME );

    printf( "DEBUG=%d, MONITOR=%d, NDIALOGS=%d, NINVOKES=%d\n",
	    Pglobal->dbg.trace_enabled, monitor_flag, Pglobal->GMAPINIT.nDialogs, 
	    Pglobal->GMAPINIT.nInvokes );

    /* Debug Printouts Required ?       */
    ret_code = FtRegister(	/* Register this process            */
			      argc	/* Command Line Argument count      */
			      , argv	/* Command Line Arguments           */
			      , Pglobal->dbg.trace_enabled, monitor_flag	/* Msg Activity Monitor Required ?  */
			      , TRUE	/* Ipc Queue Required ?             */
			      , TRUE	/* Flush Ipc Queue Before Start ?   */
			      , FALSE	/* Allow Ipc Msg Queueing Always    */
			      , TRUE	/* Process Has SIGINT Handler       */
			      , ( U16 ) restart_flag	/* Automatic Restart allowed ?      */
			      , 0	/* Process Class Designation        */
			      , 0	/* Initial Process State Declaration */
			      , 0	/* Event Distribution Filter Value  */
			      , 10 );	/* retry                            */


    if ( ret_code == RETURNerror )
    {
	int             serrno = errno;
	char           *shm = getenv( "SHM" ) ? getenv( "SHM" ) : "";

	DebugIndicator = TRUE;
	FtAnalyzeErrno( __FILE__, __LINE__ );

	switch ( serrno )
	{
	case ERRORpopNOTalive:
	    /* Signalware's not running (at least in this SHM) */
	    printf( "%s: Signalware does not appear to be running under SHM=%s on this CE\n", 
		    argv[0], shm );
	    break;

	case ERRORnotALLOWED:
	    /* Someone tried to start us from command line */
	    printf( "%s: must be started by POP (use the Start(1f) command or CREATE-PROCESS(8q) MML command.)\n", 
		    argv[0] );
	    break;

	case ERRORinvalidARGUMENT:
	    /* $OMNI_HOME not set or ??? */
	    printf( "%s: Is $OMNI_HOME set?\n", argv[0] );
	    break;

	case ENOENT:
	    printf( "%s: Signalware is not running under SHM=%s\n", argv[0], shm );
	    break;

	case ERRORinvalidSHMID:
	    /* $SHM is invalid or unset? */
	    printf( "%s: $SHM=%s is invalid\n", argv[0], shm );
	    break;
	default:
	    /* no specific action needed here */
	    break;
	}

	exit( EXIT_FAILURE );
    }

    if( 0 != Pglobal->LOCAL_PC )
    {
	SYSlogicalNodeAttributes_t *pattr;

	pattr = SYSgetAttributes (Pglobal->GMAPINIT.nodeName);

	if (pattr == NULL)
	{
	    printf ("SYSgetAttributes() failed, errno=%d\n", errno);
	    exit( EXIT_FAILURE );
	}

	if( Pglobal->LOCAL_PC != pattr->pointCode )
	{
	    printf ("lpc specified on command line does not match pointcode of attached node\n");
	    exit( EXIT_FAILURE );
	}
    }



    FtAssignHandler( SIGINT, sigintCatcher );


/*
 *     bind for the SSN service
 */
    Pglobal->ALIAS_NAME_INDEX = SYSattach( Pglobal->GMAPINIT.nodeName, FALSE );

    if ( Pglobal->ALIAS_NAME_INDEX == -1 )
    {
	fprintf( stderr, "   SYSattach(): errno=%d(%s)\n", errno, LastErrorReport );
	FtTerminate( NOrestart, 5 );
    }

    ret_code = SYSbind( Pglobal->ALIAS_NAME_INDEX, FALSE,	/* non-designatable */
			MTP_SCCP_TCAP_USER, Pglobal->GMAPINIT.ssn, SCCP_TCAP_CLASS );	/* | SCCP_CONTROL_PRIMITIVE); */

    if ( ret_code == -1 )
    {
	fprintf( stderr, "   SYSbind(): errno=%d(%s)\n", errno, LastErrorReport );
	FtTerminate( NOrestart, 7 );
    }
/*
 *    initialize GMAP provider
 */
    strncpy( Pglobal->GMAPINIT.nodeName, Pglobal->GMAPINIT.nodeName, MAXcptNAME );

    mg_dbg_init( &Pglobal->dbg, 
	    Pglobal->PROCESS_NAME, 
	    argc, 
	    argv, 
	    Pglobal->GMAPINIT.debugFile, 
	    &Pglobal->GMAPINIT.error, 
	    Pglobal->GMAPINIT.errorReport );

    if ( Pglobal->GMAPINIT.error != 0 )
	exit( 33 );

    /* FR 904 - mg_dbg_init() function clobbers the Pglobal->dbg.trace_enabled
     * value. Reinstate it.
     */
    Pglobal->dbg.trace_enabled = g_trace_enabled;;
    sendUIS(  );		/* send user in-service to SCMG */
    printf( "Initializing GMAP...\n" );
    Pglobal->APPID = gMAPInitialize( &Pglobal->GMAPINIT,
				     /*
				      *  If we are restarting, we must convince
				      *  the GSM library that we are in a -start
				      *  mode so that it clears the dialog and
				      *  invoke tables.
				      */
				     restart ? argc - 1 : argc, argv );
    printf( "End initialization\n" );

    /* waiting for the UIS message */
    for ( j = 0; j < 5; j++ )
    {
	sleep( 1 );
    }

    if ( Pglobal->APPID == -1 )
    {
	printf( "   gMAPInitialize(): errno=%d(%s)\n", 
		Pglobal->GMAPINIT.error, Pglobal->GMAPINIT.errorReport );
	FtTerminate( NOrestart, 9 );
    }

/*
 *  initalize the shared memory
 */
    init_shared_memory( restart );

    if ( restart )
    {
	cleanOldEntriesFromTable(  );
    }


/*
 *    initialize the hashTable of requests in progress
 */
    dialogId_hash.hash_function = &hashFunction;	/* hash function */
    dialogId_hash.table = PdialogId_Hash;	/* hash table  */
    dialogId_hash.bucket = PdialogId_buckets;
    dialogId_hash.table_size = Pglobal->max_requests + 1;
    dialogId_hash.bucket_size = Pglobal->max_requests + 1;
    hash_init( &dialogId_hash );

    requestId_hash.hash_function = &hashFunction;	/* hash function */
    requestId_hash.table = PrequestId_Hash;	/* hash table  */
    requestId_hash.bucket = PrequestId_buckets;
    requestId_hash.table_size = Pglobal->max_requests + 1;
    requestId_hash.bucket_size = Pglobal->max_requests + 1;
    hash_init( &requestId_hash );



/*
 *    initialize the ASN.1 encoder/decoder
 */
    wlaninit( &world, MAPgateway );

    if ( Pglobal->dbg.trace_enabled )
    {

        wlanSetEncodingFlags( &world, DEBUGPDU );
        wlanSetDecodingFlags( &world, DEBUGPDU | DEBUG_ERRORS );
    }
    
/*
 *    Enter main loop of the application
 */

    mainLoop(  );

    /* Exit gracefully */
    terminate( TERMINATE_CAUSE_SUCCESS );

    /* Make compilers happy */
    return ( 0 );

}
