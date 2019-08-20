/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source   clientMod.c

@ClearCase-version: $Revision:/main/sw9/7 $ 

@date     $Date:16-Dec-2008 15:45:42 $

@product  Signalware

@subsystem  WLAN Gateway

DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE DEVELOPMENT OF A WORKING SOFTWARE PROGRAM.  THE SOURCE CODE PROVIDED IS NOT WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.
 
 ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER  INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and interest in and to the Software, and all intellectual property rights associated therewith, including without limitation, rights to copyrights, trade secrets or know-how. This disclaimer agreement shall be governed and construed in accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time without notice. Ulticom has no continuing obligation to provide additional source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples; these changes may be reported, for the sample code included herein, in new editions of the product.

******************************************************************************/


/* SUBTITLE - Data Definitions and Includable Structure Definitions     */


/*Client Module --S*/
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <syslog.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <stdio.h>
#include <unistd.h>		/* close */
#include <netdb.h>
#include <signal.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <ulcm_hash.h>
/*
#include<sctp.h>
*/
#include <poll.h>
#include <stropts.h>
#include <sys/time.h>

#include "MAPgateway.h"
#include "SMSgateway.h"
#include <wlan_custom.h>
#include "clientMod.h"
#include "tcp_message.h"


/*
 * Constant:  ULCM_L_MAX_STR_LENGTH
 *  Purpose:  defines max string length
 */
#define ULCM_L_MAX_STR_LENGTH 200UL

/*
 * Constant:  ULCM_L_BUF_SIZE
 *  Purpose:  defines max buffer size
 */
#define ULCM_L_BUF_SIZE MAX_TCP_MESSAGE_SIZE


/*
 * Constant:  ULCM_L_MAX_REQ
 *  Purpose:  defines max requests
 */
#define ULCM_L_MAX_REQ 1000UL

/*
 * Constant:  ULCM_L_IN_USE
 *  Purpose:  defines in-use entry flag
 */
#define ULCM_L_IN_USE 1UL

/*
 * Constant:  ULCM_L_NOT_IN_USE
 *  Purpose:  defines not-in-use entry flag
 */
#define ULCM_L_NOT_IN_USE 0UL


/*
 * Constant:  ULCM_L_ERROR_LENGTH
 *  Purpose:  defines max error string length
 */
#define ULCM_L_ERROR_LENGTH 500UL

/*
 * Constant:  ULCM_L_SUCCESS
 *  Purpose:  defines success error code
 */
#define ULCM_L_SUCCESS 1UL

/*
 * Constant:  ULCM_L_MIN_API_VERSION
 *  Purpose:  defines min supported api version
 */
#define ULCM_L_MIN_API_VERSION 1UL


/*
 * Constant:  ULCM_L_MAX_SERVICE_LIST_STR
 *  Purpose:  defines max size of service list
 */
#define ULCM_L_MAX_SERVICE_LIST_STR 200UL

/*
 * Constant:  ULCM_L_MAX_MSISDN_STR
 *  Purpose:  defines max size of msisdn
 */
#define ULCM_L_MAX_MSISDN_STR 20UL

/*
 * Constant:  ULCM_L_MAX_MOD_HANDLES
 *  Purpose:  defines max module handles supported
 */
#define ULCM_L_MAX_MOD_HANDLES 100


/*
 * Constant:  BCD_IMSI_SIZE
 *  Purpose:  defines max octets used for IMSI
 */
#define BCD_IMSI_SIZE 8

/*
 * Constant:  BCD_MSISDN_SIZE
 *  Purpose:  defines max octets used for MSISDN
 */
#define BCD_MSISDN_SIZE 8


/*
 * Constant:  LIB_GW_VERSION
 *  Purpose:  defines lib<->gw interface version
 *     Note:  This value has to be changed every time the lib<->gw interface is broken
 */
#define LIB_GW_VERSION 3

/*
 * Global:  clientVersion
 *  Purpose:  holds value of client version
 */
 
 /*
  * 
  * Added by Dhaval Jobanputra @ Elitecore
  * 
  */
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

/*
  * 
  * Added by Dhaval Jobanputra @ Elitecore
  * 
  */
int             clientVersion = 0;


/*
 * Global:  arrModHandleAuth
 *  Purpose:  module handle array
 */
unsigned int    arrModHandleAuth[ULCM_L_MAX_MOD_HANDLES] = { 0 };

/*
 * Global:  numModHandlesAuth
 *  Purpose:  number of in-use module handles
 */
int             numModHandlesAuth = 0;

/*************************************************/

/*
 * Global:  worldAuth
 *  Purpose:  WLAN global structure
 */
WlanGlobal       worldAuth;



/****************************SOCKETS********************/

/*
 * Global:  authGw_fd
 *  Purpose:  auth gateway file descriptor
 */
int             authGw_fd[MAX_CONNECTIONS] = { -1 };

/*
 * Global:  localHostAuth
 *  Purpose:  local host name
 */
char            localHostAuth[50];

/*
 * Global:  localPortAuth
 *  Purpose:  local port
 */
unsigned int    localPortAuth;


/*
 * typedef:  sctpEndPointAuth_t
 *  Purpose:  defines structure to hold remote host info
 */
typedef struct sctpEndPointAuth_s
{
    char            remoteHost[50];
    char            ipAddr[10][20];
    unsigned int    port;
    int             ipAddrCount;
}
sctpEndPointAuth_t;


/*
 * Global:  sctpEndPointAuthListAuth
 *  Purpose:  list of remote host info
 */
sctpEndPointAuth_t sctpEndPointAuthListAuth[MAX_CONNECTIONS];

/*
 * Global:  sctpEndPointAuthCountAuth
 *  Purpose:   number of in-use remote hosts
 */
int             sctpEndPointAuthCountAuth = 0;

/*
 * Global:  activeSctpEndPointAuth
 *  Purpose:  
 */
int             activeSctpEndPointAuth = 0;


/*
 * Global:  connectSuccessAuth
 *  Purpose:  
 */
int             connectSuccessAuth = 0;

/*
 * Global:  confFileNameAuth
 *  Purpose:  
 */
char            confFileNameAuth[1024];

/*
 * Global:  initReqDoneAuth
 *  Purpose:  
 */
int             initReqDoneAuth[MAX_CONNECTIONS] = { 0 };


/* function prototypes */

void            setLocalPortAuth( unsigned int port );
void            setLocalHostAuth( char *locHost );
char           *getLocalHostAuth(  );
unsigned int    getLocalPortAuth(  );
int             retryConnectAuth( int sctpEndPointAuthIndx );
int             initConnectAuth( int sctpEndPointAuthIndx );
int             initConfParamsAuth( const char *pRemoteHost, const char *pLocalHost, int driverID  );
int             isCommentAuth( const char *pFile );
int             initSctpAuth( int actSctpEndPointAuth );
void            terminateSctpAuth( driverID );	
void 		shutdownSctpAuth( driverID );
int 		sendAuthRequest( int authRequestIndex, ulcm_Request gRequest, int driverID );
int 		cancelAuthRequest( unsigned int nModHandle, unsigned int hRequest, int driverID );
int 		checkModuleHandleAuth( unsigned int modHandle );
unsigned int	addModuleHandleAuth(  );
void            logErrorAuth( const char *msg );
void            logInfoAuth( const char *msg );
unsigned short  hashFunctionAuth( unsigned int hash_key );
int             getFreeHashtableSpotAuth(  );
void            initHashtableEntriesAuth(  );
void            clrEntryFromHashtableAuth( int index, unsigned int reqId );
int             sendRequestTriplets( int authRequestIndex, ulcm_Request gRequest, int driverID);
unsigned int    getRequestIdAuth(  );

/*********************THREAD********************/

/*
 * Global:  hashtable_entry_lockAuth
 *  Purpose:  
 */
static pthread_mutex_t hashtable_entry_lockAuth;

/*
 * Global:  retry_connect_lockAuth
 *  Purpose:  
 */
static pthread_mutex_t retry_connect_lockAuth;

/*
 * Global:  socket_send_lock
 *  Purpose:  
 */
static pthread_mutex_t socket_send_lock;

static void    *respHandlerAuth( void *temp );

/*
 * Global:  respHandlerThreadIdAuth
 *  Purpose:  
 */
pthread_t       respHandlerThreadIdAuth;

/***************************HASHTABLE******************/


/*
 * Global:  maxNumOfReqsAuth
 *  Purpose:  
 */
unsigned int    maxNumOfReqsAuth = 0;

/*
 * Global:  bucketsAuth
 *  Purpose:  
 */
bucket_t       *bucketsAuth = NULL;

/*
 * Global:  hash_infoAuth
 *  Purpose:  
 */
hash_info_t     hash_infoAuth;

/*
 * Global:  myHashAuth
 *  Purpose:  
 */
unsigned short *myHashAuth = NULL;


/*
 * typedef:  req_t
 *  Purpose:  
 */
typedef struct req_s
{
    unsigned long    callbackCookie;
    unsigned int    tripletCount;
    unsigned int    reqId;
    ulcm_mg_t_postSIMResults *pCallback;
    ulcm_mg_t_postSIMResultsEx *pExCallback;
    ulcm_mg_t_postIMSIResults *pIMSICallback;
    ulcm_mg_t_postAuthorizationResults *pAuthorizationCallback;
    ulcm_mg_t_postAuthorizationResultsEx *pAuthorizationExCallback;
    ulcm_mg_t_postAKAResults *pAKACallback;
    ulcm_mg_t_postUpdateGprsResults *pUpdateGprsCallback;

    unsigned int    use_status;
    unsigned short  requestType;
/*
 * defines:  ulcm_Request...
 *  Purpose:  enumerate requestType values
 */
# define ulcm_RequestSIMTriplets_chosen 1
# define ulcm_RequestAKAQuintuplets_chosen 2
# define ulcm_RequestIMSI_chosen 3
# define ulcm_RequestAuthorizationInfo_chosen 4
# define ulcm_RequestSIMTripletsEx_chosen 5
# define ulcm_RequestAuthorizationInfoEx_chosen 6
# define ulcm_RequestUpdateGprs_chosen 7
} req_t;


/*
 * Global:  pendReqAuth
 *  Purpose:  
 */
req_t          *pendReqAuth = NULL;


/*
 * Global:  info_msgAuth
 *  Purpose:  
 */
char            info_msgAuth[ULCM_L_ERROR_LENGTH];

/*
 * Constant:  NO_TRIPLETS
 *  Purpose:  defines number Of Requested vectors
 */
#define NO_TRIPLETS 0

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
   char            error_msg[ULCM_L_ERROR_LENGTH];
    if ( length == 0 )
    {
	asciiStr = NULL;
	return 0;
    }
 
    for ( i = 0, j = 0; i < length; i++, j++ )
    {
	if(( value[i] & 0xf0 )  != 0xf0 )
		asciiStr[j] = 0x30 + ( ( value[i] & 0xf0 ) >> 4 );
	else{
	  --j;
	}

	if(( value[i] & 0x0f ) != 0x0f )
		asciiStr[++j] = 0x30 + ( value[i] & 0x0f );
	
    }
    
    /* Add a NULL at the end of the string              */
    if ( asciiStr[j - 1] == 0x3f )	/* even number of digits */
	asciiStr[j - 1] = 0;	/* terminate the string  */
    else
	asciiStr[j] = 0;
    return 0;
}

/*************************************************************************

FUNCTION
   	int  char2BCD( const char *pIMSI, unsigned char *tIMSI )

DESCRIPTION
        Convert char to BCD

INPUTS
        Arg:  	pIMSI	: pointer to IMSI
		tIMSI	: pointer to BCD string	  

OUTPUTS
        Return: >0 - length of string

FEND
*************************************************************************/

static int
char2BCD( const char *pIMSI, unsigned char *tIMSI )
{
    /*Now the IMSI (ASCII) is converted to the BCD format... -S */
    int             iC;
    int             j1 = 0;
    int             bFlag = -1;
    int             lengthIMSI = strlen( pIMSI );
    if ( ( lengthIMSI % 2 ) != 0 )
    {
	lengthIMSI = lengthIMSI - 1;
	bFlag = 1;
    }
    for ( iC = 0, j1 = 0; iC < lengthIMSI; iC++ )
    {
	tIMSI[j1] = ( *( pIMSI + iC ) & 0x0f );
	tIMSI[j1] |= ( *( pIMSI + ( ++iC ) ) & 0x0f ) << 4;
	j1++;
    }

    if ( bFlag == 1 )
    {
	tIMSI[j1] = ( *( pIMSI + iC ) & 0x0f );
	tIMSI[j1] |= 0xf0;
	j1++;
    }
    return j1;			/* length */
}

/*************************************************************************

FUNCTION
   	int  checkModuleHandleAuth( unsigned int modHandle )

DESCRIPTION
        Search for module handler.

INPUTS
        Arg:  	modHandle : module handler

OUTPUTS
        Return: > -1  : index to module handler table
		-1    : if not found

FEND
*************************************************************************/

int
checkModuleHandleAuth( unsigned int modHandle )
{
    int             loop;
    for ( loop = 0; loop < ULCM_L_MAX_MOD_HANDLES; loop++ )
    {
	if ( arrModHandleAuth[loop] == modHandle )
	    return loop;
    }
    return -1;
}

/*************************************************************************

FUNCTION
   	int  addModuleHandleAuth( unsigned int modHandle )

DESCRIPTION
        Add module handler in table

INPUTS
        Arg:  	modHandle : module handler

OUTPUTS
        Return: > 0 	: Module handler
		0	: if not found

FEND
*************************************************************************/

unsigned int
addModuleHandleAuth(  )
{
    int             loop = 0;
    if ( numModHandlesAuth < ULCM_L_MAX_MOD_HANDLES - 1 )
    {
	for ( loop = 0; loop < ULCM_L_MAX_MOD_HANDLES - 1; loop++ )
	{
	    if ( arrModHandleAuth[loop] == 0 )
	    {
		arrModHandleAuth[loop] = rand(  );
		numModHandlesAuth++;
		return arrModHandleAuth[loop];
	    }
	}
    }
    return 0;
}

/*************************************************************************

FUNCTION
   	int  getSctpEndPointAuthCountAuth(  )

DESCRIPTION
        Return sctpEndPointAuthCountAuth value.

INPUTS
        Arg:	None

OUTPUTS
        Return: None

FEND
*************************************************************************/

int
getSctpEndPointAuthCountAuth(  )
{
    return sctpEndPointAuthCountAuth;
}

/*************************************************************************

FUNCTION
   	int incrementSctpEndPointAuthCountAuth(  )

DESCRIPTION
        Return sctpEndPointAuthCountAuth by one.

INPUTS
        Arg:	None

OUTPUTS
        Return: None

FEND
*************************************************************************/

void
incrementSctpEndPointAuthCountAuth(  )
{
    sctpEndPointAuthCountAuth++;
}

/*************************************************************************

FUNCTION
   	int getActiveSctpEndPointAuth(  )

DESCRIPTION
        Return activeSctpEndPointAuth value.

INPUTS
        Arg:	None

OUTPUTS
        Return: None

FEND
*************************************************************************/

int
getActiveSctpEndPointAuth(  )
{
    return activeSctpEndPointAuth;
}

/*************************************************************************

FUNCTION
   	int incrementActiveSctpEndPointAuth(  )

DESCRIPTION
        Return activeSctpEndPointAuth value.

INPUTS
        Arg:	None

OUTPUTS
        Return: None

FEND
*************************************************************************/

void
incrementActiveSctpEndPointAuth(  )
{
    activeSctpEndPointAuth++;
    activeSctpEndPointAuth %= getSctpEndPointAuthCountAuth(  );
}

/*************************************************************************

FUNCTION
   	void setLocalPortAuth( unsigned int port )

DESCRIPTION
        Set local port.

INPUTS
        Arg:	port : port number

OUTPUTS
        Return: None

FEND
*************************************************************************/

void
setLocalPortAuth( unsigned int port )
{
    localPortAuth = port;
}

/*************************************************************************

FUNCTION
   	void setLocalHostAuth( char *locHost )

DESCRIPTION
        Set local Host name.

INPUTS
        Arg:	locHost : pointer to local host

OUTPUTS
        Return: None

FEND
*************************************************************************/

void
setLocalHostAuth( char *locHost )
{
    int             len = strlen( locHost );
    strcpy( localHostAuth, locHost );
    localHostAuth[len] = '\0';
}

/*************************************************************************

FUNCTION
	char *getLocalHostAuth(  )

DESCRIPTION
        Return local Host name.

INPUTS
        Arg:	None

OUTPUTS
        Return: pointer to local host name string

FEND
*************************************************************************/
char           *
getLocalHostAuth(  )
{
    return localHostAuth;
}

/*************************************************************************

FUNCTION
	unsigned int getLocalPortAuth(  )

DESCRIPTION
        Return local Host #.

INPUTS
        Arg:	None

OUTPUTS
        Return: local port #

FEND
*************************************************************************/

unsigned int
getLocalPortAuth(  )
{
    return localPortAuth;
}

/*************************************************************************

FUNCTION
	int InitRequestAuth( int temp_version )

DESCRIPTION
	Get version number and maximum # of request from gateway.
INPUTS
        Arg:	temp_version : temporary version #.

OUTPUTS
        Return:	ULCM_L_TCP_INIT_FAILURE
		ULCM_L_VERSION_NOT_SUPPORTED
		ULCM_L_SUCCESS	

FEND
*************************************************************************/

int
InitRequestAuth( int temp_version, int driverID )
{

    ulcm_Request    gRequest;
    ulcm_Response   gResponse;
    ulcm_Response  *pGResponse;
    WlanBuf          encodeBuf, decodeBuf;
    int             pdunum = ulcm_Request_PDU;
    char            error_msg[ULCM_L_ERROR_LENGTH];
    unsigned char   msg[ULCM_L_BUF_SIZE];
    int             rc;
    int             n;
    /*
       Pack the data into the request Structure
       and call the encode/decode API
     */
    memset( &gRequest, 0, sizeof( gRequest ) );
    gRequest.choice = ulcm_initRequest_chosen;

    clientVersion = temp_version;
    gRequest.u.initRequest.version = LIB_GW_VERSION;
    encodeBuf.length = ULCM_L_BUF_SIZE;
    encodeBuf.value = msg;
    rc = wlanEncode( &worldAuth, pdunum, &gRequest, &encodeBuf );
    if ( rc != 0 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "InitRequestAuth()failed: wlanEncode returned %d ", rc );;
	logErrorAuth( error_msg );
	return ULCM_L_TCP_INIT_FAILURE;
    }
    pthread_mutex_lock( &socket_send_lock );
      rc = tcp_send( authGw_fd[driverID], encodeBuf.value, encodeBuf.length );
    pthread_mutex_unlock( &socket_send_lock );

    if ( rc < 0 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "InitRequestAuth()failed: TCP send() returned %d", errno );
	logErrorAuth( error_msg );
	return ULCM_L_TCP_INIT_FAILURE;

    }
    else
    {
	/*    wlanPrintHex(&worldAuth,(char*)encodeBuf.value,encodeBuf.length);
	   wlanPrintPDU(&worldAuth,pdunum,&gRequest);
	 */
    }
    /*Response for InitRequest */
    pdunum = ulcm_Response_PDU;
    /* receive segments */
    memset( msg, 0x0, ULCM_L_BUF_SIZE );
    n = tcp_recv( authGw_fd[driverID], msg, ULCM_L_BUF_SIZE );
    if ( n <= 0 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "InitRequestAuth(): TCP recv() Failed" );
	logErrorAuth( error_msg );
	return ULCM_L_TCP_INIT_FAILURE;

    }


    /*Unpack the data here. --S */
    decodeBuf.length = n; //ULCM_L_BUF_SIZE;
    decodeBuf.value = ( unsigned char * ) msg;
    pGResponse = &gResponse;
    wlanSetDecodingLength( &worldAuth, sizeof( ulcm_Response ) );
    n = wlanDecode( &worldAuth, &pdunum, &decodeBuf, ( void ** ) &pGResponse );
    if ( n != 0 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "InitRequestAuth() failed: wlanDecode returned %d ", rc );;
	logErrorAuth( error_msg );
	return ULCM_L_TCP_INIT_FAILURE;
    }

    n = 0;
     /*wlanPrintPDU(&worldAuth,pdunum,&gResponse);  
     */

    /*Since this is a prototype and the code for incompatible version 
       was added in the end, we just creat the element and 
       destroy it. --S */
    if ( gResponse.choice == ulcm_initResponse_chosen )
    {
	if ( bucketsAuth != NULL )
	    free( bucketsAuth );
	if ( myHashAuth != NULL )
	    free( myHashAuth );
	if ( pendReqAuth != NULL )
	    free( pendReqAuth );

	maxNumOfReqsAuth = gResponse.u.initResponse.maxRequest + 1;
	bucketsAuth = ( bucket_t * ) malloc( sizeof( bucket_t ) * maxNumOfReqsAuth );
	myHashAuth = ( unsigned short * ) malloc( sizeof( unsigned short ) * maxNumOfReqsAuth );
	pendReqAuth = ( req_t * ) malloc( sizeof( req_t ) * maxNumOfReqsAuth );


	/*Initialize the hash table. --S */

	hash_infoAuth.hash_function = &hashFunctionAuth;	/* hash function */
	hash_infoAuth.table = myHashAuth;	/* hash table  */
	hash_infoAuth.bucket = bucketsAuth;
	/* max number of simultaneous requests */
	hash_infoAuth.table_size = maxNumOfReqsAuth;
	hash_infoAuth.bucket_size = maxNumOfReqsAuth;

	/*Also Initialise the elements in the callbackcookie 
	   and requestId list with -1. --S */

	initHashtableEntriesAuth(  );


	/*Call the Initialization function that 
	   initialises the hash table. --S */
	hash_init( &hash_infoAuth );
    }
    snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "InitRequestAuth() Version Supported is %d\n", gResponse.u.initResponse.versionSupported );
    logInfoAuth( info_msgAuth );

    /* In a real application this should be replaced by a check of the lib<->Gw interface version
       and a check of the app<->lib version. The Gateway should return its version of the lib<->Gw version
       the lib should compare it to its internal lib<->Gw version (a type of ULCM_NOT_FUNCTIONAL
       should be returned if they don't match). Then the lib should compare its
       internal app<->lib version with the version received from the app (VERSION_NOT_SUPPORTED
       is returned if they don't match). 
     */
    if ( gResponse.u.initResponse.versionSupported == FALSE )
	return ULCM_L_VERSION_NOT_SUPPORTED;
    return ULCM_L_SUCCESS;
}

/*************************************************************************

FUNCTION
	int initConnectAuth( int sctpEndPointAuthIndx )

DESCRIPTION
	Initilaize TCP connection between library and gateway.
INPUTS
        Arg:	sctpEndPointAuthIndx	: index in connection table

OUTPUTS
        Return:	ULCM_L_TCP_INIT_FAILURE
		ULCM_L_SUCCESS	

FEND
*************************************************************************/

int
initConnectAuth( int sctpEndPointAuthIndx )
{
    char            error_msg[ULCM_L_ERROR_LENGTH];
    //int             indx = 0;
    struct sockaddr_in sin;            /* sockaddr work area   */
    struct hostent *phostent;
    int		connected=FALSE;

	LOG_MSG(LOG_LEVEL_INFO, "Calling shutdown for driverID: %d from initConnectAuth()\n");
    shutdownSctpAuth( sctpEndPointAuthIndx );

    {
	printf("initConnectAuth: host=%s, port=%d\n", 
		sctpEndPointAuthListAuth[sctpEndPointAuthIndx].remoteHost, 
		sctpEndPointAuthListAuth[sctpEndPointAuthIndx].port);
	phostent = gethostbyname(sctpEndPointAuthListAuth[sctpEndPointAuthIndx].remoteHost);

	if ((char *)phostent == (char *)0)
	{
	    snprintf( error_msg, ULCM_L_ERROR_LENGTH, "initConnectAuth() gethostbyname() error for %s\n", 
		    sctpEndPointAuthListAuth[sctpEndPointAuthIndx].remoteHost );
	    logErrorAuth( error_msg );
	    goto end;
	}
	memcpy(&sin.sin_addr, phostent->h_addr, phostent->h_length);

	sin.sin_port = htons((u_short)sctpEndPointAuthListAuth[sctpEndPointAuthIndx].port);
	sin.sin_family = AF_INET;

	if (-1 == (authGw_fd[sctpEndPointAuthIndx] = socket(PF_INET, SOCK_STREAM, 0)))
	if ( authGw_fd[sctpEndPointAuthIndx] == -1 )
	{

	    snprintf( error_msg, ULCM_L_ERROR_LENGTH, "initConnectAuth(): TCP socket() Error" );
	    logErrorAuth( error_msg );
	    goto end;
	}

	// set_socket_rw_timeout method is defined in tcp_message.c. It sets send and receive timeout to given fd.
	// Provide miliseconds as timeout
	if ( 0 != set_socket_rw_timeout(authGw_fd[sctpEndPointAuthIndx], 1000))
	{
		printf("initConnectAuth: Failed to set socket timeout for fd: %d\n", authGw_fd[sctpEndPointAuthIndx]);
	
	} else {
	
		printf("initConnectAuth: Set socket timeout success for fd: %d\n", authGw_fd[sctpEndPointAuthIndx]);
		
	}

	if (-1 == connect(authGw_fd[sctpEndPointAuthIndx], (struct sockaddr *)&sin,
				 (int)sizeof(struct sockaddr_in)))
	{
	    perror("initConnectAuth: connect");
	    snprintf( error_msg, ULCM_L_ERROR_LENGTH, "initConnectAuth(): connect() error\n");
	    logErrorAuth( error_msg );
	    goto end;
	}
	else
	{
	    connected = TRUE;
	 
	}
    }

    // for( indx=0; indx < getSctpEndPointAuthCountAuth(); indx++ )
    // {
	// printf("initConnectAuth: host=%s, port=%d\n", 
		// sctpEndPointAuthListAuth[indx].remoteHost, 
		// sctpEndPointAuthListAuth[indx].port);
	// phostent = gethostbyname(sctpEndPointAuthListAuth[indx].remoteHost);

	// if ((char *)phostent == (char *)0)
	// {
	    // snprintf( error_msg, ULCM_L_ERROR_LENGTH, "initConnectAuth() gethostbyname() error for %s\n", 
		    // sctpEndPointAuthListAuth[indx].remoteHost );
	    // logErrorAuth( error_msg );
	    // continue;
	// }
	// memcpy(&sin.sin_addr, phostent->h_addr, phostent->h_length);

	// sin.sin_port = htons((u_short)sctpEndPointAuthListAuth[indx].port);
	// sin.sin_family = AF_INET;

	// if (-1 == (authGw_fd = socket(PF_INET, SOCK_STREAM, 0)))
	// if ( authGw_fd == -1 )
	// {

	    // snprintf( error_msg, ULCM_L_ERROR_LENGTH, "initConnectAuth(): TCP socket() Error" );
	    // logErrorAuth( error_msg );
	    // continue;
	// }

	// if (-1 == connect(authGw_fd, (struct sockaddr *)&sin,
				 // (int)sizeof(struct sockaddr_in)))
	// {
	    // perror("initConnectAuth: connect");
	    // snprintf( error_msg, ULCM_L_ERROR_LENGTH, "initConnectAuth(): connect() error\n");
	    // logErrorAuth( error_msg );
	    // continue;
	// }
	// else
	// {
	    // connected = TRUE;
	    // break;
	// }
    // }
	end:
    if( TRUE == connected )
    {
	printf("initConnectAuth: connected - leave\n");
	return ULCM_L_SUCCESS;
    }
    else
    {
	printf("initConnectAuth: NOT connected - leave\n");
	return ULCM_L_TCP_INIT_FAILURE;
    }
}

/*************************************************************************

FUNCTION
	int retryConnectAuth( int sctpEndPointAuthIndx )

DESCRIPTION
	Retry TCP connection between library and gateway.
INPUTS
        Arg:	sctpEndPointAuthIndx	: index in connection table

OUTPUTS
        Return:	ULCM_L_TCP_CONNECT_FAILURE
		ULCM_L_SUCCESS	

FEND
*************************************************************************/
int
retryConnectAuth( int sctpEndPointAuthIndx )
{
    if ( initConnectAuth( sctpEndPointAuthIndx ) != ULCM_L_SUCCESS )
    {
	return ULCM_L_TCP_CONNECT_FAILURE;
    }
	return ULCM_L_SUCCESS;
}

/*************************************************************************

FUNCTION
	void clrEntryFromHashtableAuth( int index, unsigned int reqId )

DESCRIPTION
	Clear Hash table entry.
INPUTS
        Arg:	index	: Index in hash table
		reqId	: request id

OUTPUTS
        Return:	None

FEND
*************************************************************************/

void
clrEntryFromHashtableAuth( int index, unsigned int reqId )
{

    if ( !index )
	return;

    pthread_mutex_lock( &hashtable_entry_lockAuth );

    if ( pendReqAuth[index].reqId == reqId )
    {
	pendReqAuth[index].use_status = ULCM_L_NOT_IN_USE;
	pendReqAuth[index].callbackCookie = 0;
	pendReqAuth[index].reqId = 0;

    }
    pthread_mutex_unlock( &hashtable_entry_lockAuth );
}

/*************************************************************************

FUNCTION
	unsigned short hashFunctionAuth( unsigned int hash_key )

DESCRIPTION
	Calculate function key.	
INPUTS
        Arg:	hash_key: hash key

OUTPUTS
        Return:	hash index

FEND
*************************************************************************/

unsigned short
hashFunctionAuth( unsigned int hash_key )
{
    return ( hash_key % maxNumOfReqsAuth );
}

int             seed = 10;

/*************************************************************************

FUNCTION
	unsigned int getRequestIdAuth(  )
DESCRIPTION
	Get request id.	
INPUTS
        Arg:	None

OUTPUTS
        Return:	id	

FEND
*************************************************************************/

unsigned int
getRequestIdAuth(  )
{
    struct timeval  val;
    gettimeofday( ( &val ), NULL );
    return val.tv_usec;
}

/*************************************************************************

FUNCTION
	int getFreeHashtableSpotAuth(  )
DESCRIPTION
	Get free hash table spot.	
INPUTS
        Arg:	None

OUTPUTS
        Return:	-1	: Hash tabel full.
		>0	: Hash table index 	

FEND
*************************************************************************/

int
getFreeHashtableSpotAuth(  )
{
    int             i = 0;
    U8              isFree = 0;

    for ( i = 1; i < maxNumOfReqsAuth; i++ )
    {
	pthread_mutex_lock( &hashtable_entry_lockAuth );
	if ( pendReqAuth[i].use_status == ULCM_L_NOT_IN_USE )
	{
	    pendReqAuth[i].use_status = ULCM_L_IN_USE;
	    isFree = 1;
	}
	pthread_mutex_unlock( &hashtable_entry_lockAuth );
	if ( isFree == 1 )
	    return i;
    }
    return -1;
}

/*************************************************************************

FUNCTION
	void initHashtableEntriesAuth(  )
DESCRIPTION
	Initialize hash table entry.	
INPUTS
        Arg:	None

OUTPUTS
        Return:	None

FEND
*************************************************************************/

void
initHashtableEntriesAuth(  )
{
    int             i = 0;
    for ( i = 1; i < maxNumOfReqsAuth; i++ )
    {
	pendReqAuth[i].callbackCookie = 0;
	pendReqAuth[i].reqId = 0;
	pendReqAuth[i].use_status = ULCM_L_NOT_IN_USE;
    }
}

/*************************************************************************

FUNCTION
	void logErrorAuth( const char *msg )

DESCRIPTION
	Log error message.
INPUTS
        Arg:	msg	: log error message

OUTPUTS
        Return:	None

FEND
*************************************************************************/

void
logErrorAuth( const char *msg )
{
    printf( "%s \n", msg );

    syslog( LOG_ERR, "libulcmmg.so ERR: %s:%d:%s\n",__FUNCTION__,__LINE__,msg );
    return;
}
/*************************************************************************

FUNCTION
	void logInfoAuth( const char *msg )

DESCRIPTION
	Log info message.
INPUTS
        Arg:	msg	: log error message

OUTPUTS
        Return:	None

FEND
*************************************************************************/

void
logInfoAuth( const char *msg )
{
    syslog( LOG_ERR, "libulcmmg.so INFO: %s\n", msg );

    return;
}

/*************************************************************************

FUNCTION
	int ulcm_mg_t_GetApiVersion( unsigned int *pApiVersion, 
					unsigned int *pMinApiVersion )
DESCRIPTION
	Return version #.
INPUTS
        Arg:	pApiVersion	: pointer to API version.
		pMinApiVersion	: Pointer to minimum API version.

OUTPUTS
        Return:	ULCM_L_INVALID_ARG	: error
		0			: success

FEND
*************************************************************************/

int
ulcm_mg_t_GetApiVersion( unsigned int *pApiVersion, unsigned int *pMinApiVersion )
{
    snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "libulcmmg: GetApiVersion() called \n" );
    logInfoAuth( info_msgAuth );
    if ( pApiVersion == NULL )
	return ULCM_L_INVALID_ARG;
    if ( pMinApiVersion == NULL )
	return ULCM_L_INVALID_ARG;
    *pApiVersion = ULCM_L_API_VERSION;
    *pMinApiVersion = ULCM_L_MIN_API_VERSION;
    return ULCM_L_API_INIT_SUCCESS;
}


/*************************************************************************

FUNCTION
	int isCommentAuth( const char *buff )

DESCRIPTION
	Check for wild card character.	
INPUTS
        Arg:	buff	: pointer to buffer string.	

OUTPUTS
        Return:	buff	: pointer to buffer string. 

FEND
*************************************************************************/
int
isCommentAuth( const char *buff )
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
    return 0;
}


/*************************************************************************

FUNCTION
	int initConfParamsAuth(  const char *pRemoteHost, const char *pLocalHost, int driverID  )

DESCRIPTION
	Initilize configuration parameters.

INPUTS
        Arg:	pFile	: ptr to configuration file.

OUTPUTS
        Return: ULCM_L_CONF_INIT_FAILURE
		ULCM_L_CONF_PARAM_NOT_VALID
		ULCM_L_SUCCESS
		


FEND
*************************************************************************/

int
initConfParamsAuth( const char *pRemoteHost, const char *pLocalHost, int driverID )
{
    char            error_msg[ULCM_L_ERROR_LENGTH];
    char           *token[5];
    char            buff[ULCM_L_MAX_STR_LENGTH];
    int             indx = getSctpEndPointAuthCountAuth();
    int             remote_host_proper = 0;
    int             local_host_proper = 0;
	
	
	if ( driverID < getSctpEndPointAuthCountAuth()){
		snprintf( error_msg, ULCM_L_ERROR_LENGTH, "initConfParamsAuth() : Driver Id is less than already created sctp connection");
		logErrorAuth( error_msg );
		return ULCM_L_SUCCESS;
	}
	
        if ( strcpy( buff,  pRemoteHost) == NULL ){
	   snprintf( error_msg, ULCM_L_ERROR_LENGTH, "initConfParamsAuth() : remote host is not configured");
	   logErrorAuth( error_msg );
    	   return ULCM_L_CONF_INIT_FAILURE;
	}
	
        if ( isCommentAuth( buff ) == 0 )
        {
            /*Use the strtok function, give the separators 
              ' ' , '\n' and '\t' --S */
            {
                int             i = 0, j = 0, k = 0;
                token[i++] = strtok( buff, ":,\t,\n,[,], " );

                while ( (token[i++] = strtok( NULL, ":,\t,\n,[,], " )) )
                {
                    ;
                }

                i--;

                for ( j = 0; j < i; j++ )
                {
                    if ( strcmp( token[j], "REMOTE_HOST" ) == 0 )
                    {
                        strcpy( sctpEndPointAuthListAuth[driverID].remoteHost, token[++j] );
                        printf( "Remote host is %s \n", sctpEndPointAuthListAuth[driverID].remoteHost );
                        sctpEndPointAuthListAuth[driverID].port = atoi( token[++j] );
                        printf( "Remote port is %d\n", sctpEndPointAuthListAuth[driverID].port );
                        incrementSctpEndPointAuthCountAuth(  );
                        while ( ++j < i )
                        {
                            strcpy( sctpEndPointAuthListAuth[driverID].ipAddr[k++], token[j] );
                            printf( "Remote ipaddr is %s\n", token[j] );
                            remote_host_proper = 1;
                        }
                        sctpEndPointAuthListAuth[driverID].ipAddrCount = k;
                        indx++;
                    }
                    else if ( strcmp( token[j], "LOCAL_HOST" ) == 0 )
                    {
                        setLocalHostAuth( token[++j] );
                        printf( "Local Host is %s\n", getLocalHostAuth(  ) );
                        setLocalPortAuth( atoi( token[++j] ) );
                        printf( "Local Port is %d\n", getLocalPortAuth(  ) );
                        local_host_proper = 1;
                    }
                }
            }
        }

    { /*printing */
        int             i = 0;
        for ( i = 0; i < getSctpEndPointAuthCountAuth(  ); i++ )
        {
            printf( " addr[%d] is %s:%d\n", i, sctpEndPointAuthListAuth[i].remoteHost, sctpEndPointAuthListAuth[i].port );
        }
    }

    if ( remote_host_proper == 0 )
    {
        snprintf( error_msg, ULCM_L_ERROR_LENGTH, "Conf parameters for Remote Host not found " );
        logErrorAuth( error_msg );
        return ULCM_L_CONF_PARAM_NOT_VALID;
    }


	if ( strcpy( buff,  pLocalHost) == NULL ){
	   snprintf( error_msg, ULCM_L_ERROR_LENGTH, "initConfParamsAuth() : local host is not configured");
	   logErrorAuth( error_msg );
    	   return ULCM_L_CONF_INIT_FAILURE;
	}

        if ( isCommentAuth( buff ) == 0 )
        {
            /*Use the strtok function, give the separators 
              ' ' , '\n' and '\t' --S */
            {
                int             i = 0, j = 0, k = 0;
                token[i++] = strtok( buff, ":,\t,\n,[,], " );

                while ( (token[i++] = strtok( NULL, ":,\t,\n,[,], " )) )
                {
                    ;
                }

                i--;

                for ( j = 0; j < i; j++ )
                {
                    if ( strcmp( token[j], "REMOTE_HOST" ) == 0 )
                    {
                        strcpy( sctpEndPointAuthListAuth[driverID].remoteHost, token[++j] );
                        printf( "Remote host is %s \n", sctpEndPointAuthListAuth[driverID].remoteHost );
                        sctpEndPointAuthListAuth[driverID].port = atoi( token[++j] );
                        printf( "Remote port is %d\n", sctpEndPointAuthListAuth[driverID].port );
                        incrementSctpEndPointAuthCountAuth(  );
                        while ( ++j < i )
                        {
                            strcpy( sctpEndPointAuthListAuth[driverID].ipAddr[k++], token[j] );
                            printf( "Remote ipaddr is %s\n", token[j] );
                            remote_host_proper = 1;
                        }
                        sctpEndPointAuthListAuth[driverID].ipAddrCount = k;
                        indx++;
                    }
                    else if ( strcmp( token[j], "LOCAL_HOST" ) == 0 )
                    {
                        setLocalHostAuth( token[++j] );
                        printf( "Local Host is %s\n", getLocalHostAuth(  ) );
                        setLocalPortAuth( atoi( token[++j] ) );
                        printf( "Local Port is %d\n", getLocalPortAuth(  ) );
                        local_host_proper = 1;
                    }
                }
            }
        }

	
    {/*printing*/
        int             i = 0;
        for ( i = 0; i < getSctpEndPointAuthCountAuth(  ); i++ )
        {
            printf( " addr[%d] is %s:%d\n", i, sctpEndPointAuthListAuth[i].remoteHost, sctpEndPointAuthListAuth[i].port );
        }
    }

    if ( local_host_proper == 0 )
    {
        snprintf( error_msg, ULCM_L_ERROR_LENGTH, "Conf parameters for Local Host not found " );
        logErrorAuth( error_msg );
        return ULCM_L_CONF_PARAM_NOT_VALID;
    }
    return ULCM_L_SUCCESS;

}

/* FUNCTION - ulcm_mg_t_ModuleInit() Module Initialization Function */
/**************************************************************************

FUNCTION
        int 
	ulcm_mg_t_ModuleInit(unsigned int nAPIVersion,
	                  const char* pInit, 
			  unsigned int * pAvailableCaps,
			  unsigned int * pModHandle)
DESCRIPTION
	Module Initialization Function

INPUTS
	Arg:	nAPIVersion	- API version of the Library..
	        pInit           - Configuration File containing the IP Addr
                                  of the MAP-SS7 gateway.
		pAvailableCaps  -
		pModHandle      - Module Handle (For Future Use)
		
	Others:	

OUTPUTS
	Return:	
	        ULCM_L_MOD_INIT_SUCCESS     - Success
	        ULCM_L_CONF_INIT_FAILURE    - Failed to read the conf file.
		ULCM_L_CONF_PARAM_NOT_VALID - entry for local or remote host not found
		                              in conf file
		ULCM_L_MOD_HANDLE_NOT_INIT  - Module Handle pointer is NULL.
		ULCM_L_API_VERSION_INVALID  - api version not supported
		ULCM_L_INVALID_ARG          - one or more pointer parameters is NULL

	Arg:	pModHandle                  - return a module handle.
	Others:	

LOGIC
	1.  Read the conf file and set the host IP Addr.
	
	2.  Initialise the encode/decode tool.
	
	3.  Initialise the Hastable (Requests).

	4.  Connect to the MAP-SS7 Gateway using the TCP protocol.

	5.  Send a InitRequest message to the MAP-SS7 Gateway.
	
	6.  Start a response Handler thread that accepts response from the 
	    MAP-SS7 gateway.
FEND
**************************************************************************/

int
ulcm_mg_t_ModuleInit( unsigned int nApiVersion, const char *pRemoteHost, const char *pLocalHost,
		      unsigned int *pAvailableCaps, unsigned int *pModHandle, int driverID )
{

    char            error_msg[ULCM_L_ERROR_LENGTH];
    unsigned long   ret_val = ULCM_L_SUCCESS;

    snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "libulcmmg : ModuleInit() called \n" );
    logInfoAuth( info_msgAuth );

    if ( pRemoteHost    == NULL ||
	 pLocalHost     == NULL ||
    	 pAvailableCaps == NULL ||
    	 pModHandle     == NULL )
    {
	return ULCM_L_INVALID_ARG;
    }
    
    if ( nApiVersion != ULCM_L_API_VERSION )
	return ULCM_L_API_VERSION_INVALID;
    
    /*Initialize the mutexes */
    pthread_mutex_init( &retry_connect_lockAuth, NULL );
    pthread_mutex_init( &hashtable_entry_lockAuth, NULL );
    pthread_mutex_init( &socket_send_lock, NULL );

    pthread_mutex_lock( &hashtable_entry_lockAuth );

    if ( ret_val == ULCM_L_SUCCESS )
    {
	*pModHandle = addModuleHandleAuth(  );
	if ( *pModHandle == 0 )
	{
	    snprintf( error_msg, ULCM_L_ERROR_LENGTH, "libsmsulcmmg : ModuleInit()" "ULCM_L_MOD_HANDLE_NOT_INIT " );
	    logErrorAuth( error_msg );
	    return ULCM_L_MOD_HANDLE_NOT_INIT;
	}
	else
	{
	    printf( "ModuleInit() module handle added.\n" );
	}
    }

    *pAvailableCaps |= ULCM_L_MG_CAPS_SIM;
    *pAvailableCaps |= ULCM_L_MG_CAPS_AUTHORIZATION;
    *pAvailableCaps |= ULCM_L_MG_CAPS_AKA;
    *pAvailableCaps |= ULCM_L_MG_CAPS_IMSI;
    *pAvailableCaps |= ULCM_L_MG_CAPS_UPDATEGPRS;

    /*Read the configuration file and get all the values. --S */
    if ( ret_val == ULCM_L_SUCCESS )
    {
	ret_val = initConfParamsAuth( pRemoteHost, pLocalHost , driverID);
    }

    /*Initialization for encode/decode tool. --S */
    if ( ret_val == ULCM_L_SUCCESS )
	wlaninit( &worldAuth, MAPgateway );

    if ( ret_val != ULCM_L_SUCCESS )
    {
	int             indx = checkModuleHandleAuth( *pModHandle );
	if ( indx != -1 )
	{
	    arrModHandleAuth[indx] = 0;
	    numModHandlesAuth--;
	}
    }


    pthread_mutex_unlock( &hashtable_entry_lockAuth );

    /*Initialisation for encode/decode tool ends here. --S */

    if ( ret_val == ULCM_L_SUCCESS )
    {
	printf( " Auth module initialized\n" );
	return ULCM_L_MOD_INIT_SUCCESS;
    }
    else
	return ret_val;

}

/*************************************************************************

FUNCTION
	int initSctpAuth( int actSctpEndPointAuth )

DESCRIPTION
	Initiate TCP connection

INPUTS
        Arg:	actSctpEndPointAuth : index in connection table

OUTPUTS
        Return: ULCM_L_TCP_INIT_FAILURE
		ULCM_L_SUCCESS
		


FEND
*************************************************************************/

int
initSctpAuth( int actSctpEndPointAuth )
{
    unsigned long   initVar = 0;
    unsigned long   ret_val = 0;
    pthread_attr_t  attr;
    /*Connecting to the MAP Gateway Application starts here. --S */
    pthread_mutex_lock( &retry_connect_lockAuth );

    if (( initReqDoneAuth[actSctpEndPointAuth] == initVar) || (authGw_fd[actSctpEndPointAuth] == -1))
    {

	ret_val = retryConnectAuth( actSctpEndPointAuth );
	/*Init Request Sent to get the parameters from the Gateway. --S */
	if ( ret_val == ULCM_L_SUCCESS )
	{
	    ret_val = InitRequestAuth( ULCM_L_API_VERSION, actSctpEndPointAuth );
	}
	if ( ret_val == ULCM_L_SUCCESS )
	{
	    pthread_attr_init( &attr );
	    if ( pthread_create( &respHandlerThreadIdAuth, &attr, respHandlerAuth, (void *)actSctpEndPointAuth ) < 0 )
	    {
		snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "initSctpAuth " "Thread is not Created\n" );
		logErrorAuth( info_msgAuth );
		terminateSctpAuth( actSctpEndPointAuth );
		pthread_cancel( respHandlerThreadIdAuth );

	    }
	    else
	    {
		snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "initSctpAuth() Thread created\n" );
		logInfoAuth( info_msgAuth );
		ret_val = ULCM_L_SUCCESS;
		initReqDoneAuth[actSctpEndPointAuth] = 1;

	    }
	}
    }
    initVar = initReqDoneAuth[actSctpEndPointAuth];
    pthread_mutex_unlock( &retry_connect_lockAuth );
    if ( initVar == 0 )
	return ULCM_L_TCP_INIT_FAILURE;
    else
	return ULCM_L_SUCCESS;
}


/*************************************************************************

FUNCTION
	void terminateSctpAuth(  )

DESCRIPTION
	Terminate TCP connection

INPUTS
        Arg:	None	

OUTPUTS
        Return:	None
		


FEND
*************************************************************************/

void
terminateSctpAuth( int driverID )
{
    if ( bucketsAuth != NULL )
	free( bucketsAuth );
    if ( myHashAuth != NULL )
	free( myHashAuth );
    if ( pendReqAuth != NULL )
	free( pendReqAuth );

    maxNumOfReqsAuth = 0;
	printf("terminateSctpAuth( driver ID: %d )\n", driverID);
    shutdownSctpAuth( driverID );
    initReqDoneAuth[driverID] = 0;
}

/*************************************************************************

FUNCTION
	void shutdownSctpAuth(  )
DESCRIPTION
	Shutdown TCP connection

INPUTS
        Arg:	None

OUTPUTS
        Return: None
		
FEND
*************************************************************************/

void
shutdownSctpAuth( int driverID )
{
    char            error_msg[ULCM_L_ERROR_LENGTH];

    if ( authGw_fd[driverID] == -1 )
	return;			/* Nothing to do! */
    
    if ( shutdown( authGw_fd[driverID], 2 ) == -1 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "shutdownSctpAuth(): shutdown() " );
	logInfoAuth( error_msg );
    }


    if ( close( authGw_fd[driverID] ) == -1 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "shutdownSctpAuth(): close()" );
	logInfoAuth( error_msg );
    }
    authGw_fd[driverID] = -1;
}

/*Client Provided APIs*/

/*	Author		:	Sriram Chandrasekaran
 *	Date		:	March 14 2003
 *	Description	:	This function performs the following operations:
 1. close the gateway socket
 2. close the listener socket
*/

/* FUNCTION - ulcm_mg_t_ModuleTerminate() Module Initialization Function */
/**************************************************************************
                                                                           
FUNCTION
        void
        ulcm_mg_t_ModuleTerminate()
                                                                           
DESCRIPTION
	Module Termination Function

INPUTS
	Arg:	nModHandle   - module Handle
	Others:	

OUTPUTS
	Return:	
      	Arg:	
	Others:	

LOGIC
	1.  Close the TCP connection.
	
	2.  Initialise the Hastable (Requests).

FEND
**************************************************************************/

void
ulcm_mg_t_ModuleTerminate( unsigned int nModHandle, int driverID )
{



    char            error_msg[ULCM_L_ERROR_LENGTH];
    unsigned long   ret_val = 0;
    int             index = -1;
    snprintf( error_msg, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_ModuleTerminate() called\n" );
    logInfoAuth( error_msg );

    index = checkModuleHandleAuth( nModHandle );
    if ( index == -1 )
	return;
    pthread_mutex_lock( &hashtable_entry_lockAuth );
    if ( ret_val == 0 )
    {
	arrModHandleAuth[index] = 0;
	numModHandlesAuth--;
    }
    if ( numModHandlesAuth <= 0 )
    {
	if ( bucketsAuth != NULL )
	    free( bucketsAuth );
	if ( myHashAuth != NULL )
	    free( myHashAuth );
	if ( pendReqAuth != NULL )
	    free( pendReqAuth );

	maxNumOfReqsAuth = 0;
	/*kill the response handler thread */

	if ( (0 != (int) respHandlerThreadIdAuth) && 
	     (pthread_cancel( respHandlerThreadIdAuth ) != 0) )
	{
	    snprintf( error_msg, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_ModuleTerminate(): pThreadCancel() " );
	    logInfoAuth( error_msg );
	}

	/*
	 * Perform graceful shutdown.
	 */

	if ( authGw_fd[driverID] != -1 && shutdown( authGw_fd[driverID], 2 ) == -1 )

	{
	    snprintf( error_msg, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_ModuleTerminate(): shutdown() " );
	    logInfoAuth( error_msg );
	}


	if ( authGw_fd[driverID] != -1 && close( authGw_fd[driverID] ) == -1 )
	{
	    snprintf( error_msg, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_ModuleTerminate(): close()" );
	    logInfoAuth( error_msg );
	}
    }

    pthread_mutex_unlock( &hashtable_entry_lockAuth );
    snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_ModuleTerminate: DONE! " );
    logInfoAuth( info_msgAuth );
    return;
}


/*Client Provided APIs*/

/*	Author		:	Sriram Chandrasekaran
 *	Date		:	March 14 2003
 *	Description	:	This function performs the 
                                following operations:
                                1. packs the input into a string to be sent across
                                2. sends the string to the gateway
*/
/* FUNCTION - ulcm_mg_t_RequestSIMTriplets() Request SIM Triplets Function */
/**************************************************************************

FUNCTION
        unsigned int
	ulcm_mg_t_RequestSIMTriplets( unsigned int nModHandle,
	                           const char * pIMSI,
	                           unsigned int iTripletCount,
				   unsigned int* pSIMRequest,
				   ulcm_mg_t_postSIMResults *pFnCallback,
				   unsigned int iCallbackCookie)

DESCRIPTION
	Request SIM triplets for the given IMSI.

INPUTS
	Arg:	
	    nModHandle      - Module Handle
	    pIMSI           - IMSI (in ASCII)
	    iTripletCount   - Number of triplets requested (1..3)
	    pSIMRequest     - Handle that identifies the request 
	                      (Used while canceling the request)
	    pFnCallback     - Callback function that is invoked when 
	                      the response arrives from the gateway.
            iCallbackCookie - Passed as a parameter to the callback 
	                      function (for the client application to
			      correlate the request with the response)

	Others:	

OUTPUTS
	Return: 
	      REQUEST_SUCESS     -   Request Succeeded
	      MAX_ULCM_REQ_ERROR      -   Maximum Number of 
                                     Outstanding requests.
	      SEND_FAILURE    -      Not able to send the 
		                     message to the gateway.
      	Arg:	
	   pSIMRequest        -  
	Others:	

LOGIC
	1.  Get an entry from the hash table.
	
	2.  Build the request into an encode/decode ulcm_mg_Request structure.

	3.  Send the request to the gateway.

FEND
**************************************************************************/

int
ulcm_mg_t_RequestSIMTriplets( unsigned int nModHandle,
			      const char *pIMSI,
			      unsigned int iTripletCount, unsigned int *pSIMRequest, ulcm_mg_t_postSIMResults * pFnCallback, unsigned long iCallbackCookie, int driverID )
{
    ulcm_Request    gRequest;
    int             freeSpot = 0;
    char            error_msg[ULCM_L_ERROR_LENGTH];
    unsigned long   ret_val = 0;
    int             indx = 0;

    /* test if all arguments look valid */
    if ( checkModuleHandleAuth( nModHandle ) == -1 )
	return ULCM_L_INVALID_MOD_HANDLE;
    if ( ( pIMSI == NULL ) || ( pFnCallback == NULL ) || ( pSIMRequest == NULL ) )
	return ULCM_L_INVALID_ARG;

    *pSIMRequest = getRequestIdAuth(  );

    /* Check EndPointAuths until we find one for which TCP and thread are working. */
    // for ( indx = 0; indx < getSctpEndPointAuthCountAuth(  ); indx++ )
    // {
	// if ( ( ret_val = initSctpAuth( getActiveSctpEndPointAuth(  ) ) ) != ULCM_L_SUCCESS )
	    // incrementActiveSctpEndPointAuth(  );
	// else
	    // break;		/* we have an active connection, we don't need to continue  */
    // }
	
	ret_val = initSctpAuth( driverID );
	
    if ( ret_val != ULCM_L_SUCCESS )
	return ULCM_L_TCP_INIT_FAILURE;

    /*create a requestID and associate this with the iCallbackCookie */
    freeSpot = getFreeHashtableSpotAuth(  );
    if ( freeSpot == -1 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_RequestSIMTriplets() " "Maximum Requests Already Reached " );
	logErrorAuth( error_msg );
	return ULCM_L_MAX_REQ_EXCEDED;
    }
    pendReqAuth[freeSpot].callbackCookie = iCallbackCookie;
    pendReqAuth[freeSpot].reqId = *pSIMRequest;
    pendReqAuth[freeSpot].pCallback = pFnCallback;
    pendReqAuth[freeSpot].requestType = ulcm_RequestSIMTriplets_chosen;
    hash_add_entry( &hash_infoAuth, *pSIMRequest, freeSpot );

    /*Pack the data into the encode/decode request Structure and call the encode/decode API */
    memset( &gRequest, 0, sizeof( gRequest ) );
    gRequest.choice = ulcm_authRequest_chosen;
    gRequest.u.authRequest.requestId = *pSIMRequest;
    gRequest.u.authRequest.imsi.length = char2BCD( pIMSI, &gRequest.u.authRequest.imsi.value[0] );
    gRequest.u.authRequest.numberOfRequestedVectors = iTripletCount;

    return sendAuthRequest( freeSpot, gRequest, driverID );

}


/*************************************************************************

FUNCTION
	int sendAuthRequest( int authRequestIndex, ulcm_Request gRequest )

DESCRIPTION
	Send authentication request.	

INPUTS
        Arg:	authRequestIndex : 	Auth request index
		gRequest	 : 	request structure

OUTPUTS
        Return: ULCM_L_ASN_ENCODING_ERROR
		ULCM_L_TCP_SEND_FAILURE
		ULCM_L_REQ_SIM_TRIPLET_SUCCESS
		
FEND
*************************************************************************/

int
sendAuthRequest( int authRequestIndex, ulcm_Request gRequest, int driverID )
{
    WlanBuf          encodeBuf;
    int             rc;
    unsigned char   msg[ULCM_L_BUF_SIZE];
    int             pdunum = ulcm_Request_PDU;
    WlanGlobal       worldAuth2;

    /*Duplicate this and then use it. --S */
    wlanDupWorld( &worldAuth, &worldAuth2 );

    encodeBuf.length = ULCM_L_BUF_SIZE;
    encodeBuf.value = msg;
    rc = wlanEncode( &worldAuth2, pdunum, &gRequest, &encodeBuf );
    /*
       wlanPrintPDU(&worldAuth2,pdunum,&gRequest);
     */
    if ( rc != 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "sendAuthRequest failed: wlanEncode returned %d", rc );
	logErrorAuth( info_msgAuth );
	clrEntryFromHashtableAuth( authRequestIndex, gRequest.u.authRequest.requestId );
	wlanterm( &worldAuth2 );
	return ULCM_L_ASN_ENCODING_ERROR;
    }

    pthread_mutex_lock( &socket_send_lock );
    rc = tcp_send( authGw_fd[driverID], encodeBuf.value, encodeBuf.length );
    pthread_mutex_unlock( &socket_send_lock );

    wlanterm( &worldAuth2 );

    if ( rc < 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, 
		"sendAuthRequest() failed " "send() returned %d. \n", errno );
	logErrorAuth( info_msgAuth );
	clrEntryFromHashtableAuth( authRequestIndex, gRequest.u.authRequest.requestId );
	return ULCM_L_TCP_SEND_FAILURE;
    }

    return ULCM_L_REQ_SIM_TRIPLET_SUCCESS;
}

/* FUNCTION - ulcm_mg_t_cancelSIMRequest() Cancel SIM Request Function */
/**************************************************************************

FUNCTION
        unsigned int
	ulcm_mg_t_cancelSIMRequest(unsigned int nModHandle,unsigned int hSIMRequest)

DESCRIPTION
	Cancel the request (using the hSIMRequest handle)

INPUTS
	Arg:	
	    nModHandle      - Module Handle
	    hSIMRequest     - Handle that identifies the request 

	Others:	

OUTPUTS
	Return: 
	      MOD_HANDLE_NOT_INIT   -   Module Uninitialised
	      ULCM_INVALID_MOD_HANDLE    -   Module Handle Invalid
	      REQUEST_NOT_FOUND     -   Request Succeeded
	      SEND_FAILURE          -   Not able to send the 
		                        message to the gateway.
      	Arg:	
	   
	Others:	

LOGIC
	1.  Get the entry from the hash table.
	
	2.  Build the request into an ulcm_mg_Request structure.

	3.  Send the cancel request to the gateway.

FEND
**************************************************************************/

int
ulcm_mg_t_CancelSIMRequest( unsigned int nModHandle, unsigned int hSIMRequest, int driverID )
{
    return cancelAuthRequest( nModHandle, hSIMRequest, driverID );
}

/**************************************************************************

FUNCTION
        int cancelAuthRequest(unsigned int nModHandle,unsigned int hSIMRequest)

DESCRIPTION
	Cancel the authentication request (using the hSIMRequest handle)

INPUTS
	Arg:	
	    nModHandle      - Module Handle
	    hSIMRequest     - Handle that identifies the request 

	Others:	

OUTPUTS
	Return: 
	      MOD_HANDLE_NOT_INIT   -   Module Uninitialised
	      ULCM_INVALID_MOD_HANDLE    -   Module Handle Invalid
	      REQUEST_NOT_FOUND     -   Request Succeeded
	      SEND_FAILURE          -   Not able to send the 
		                        message to the gateway.
      	Arg:	
	   
	Others:	

FEND
**************************************************************************/

int
cancelAuthRequest( unsigned int nModHandle, unsigned int hRequest, int driverID )
{
    unsigned char   msg[ULCM_L_BUF_SIZE];
    int             rc;
    WlanGlobal       worldAuth3;
    int             pdunum = ulcm_Request_PDU;
    ulcm_Request    gRequest;
    int             index = 0;
    WlanBuf          encodeBuf;

    if ( checkModuleHandleAuth( nModHandle ) == -1 )
	return ULCM_L_INVALID_MOD_HANDLE;
    if ( initReqDoneAuth[driverID] == 0 )
	return ULCM_L_REQ_NOT_FOUND;
    index = hash_get_entry( &hash_infoAuth, hRequest );
    if ( !index )
	return ULCM_L_REQ_NOT_FOUND;
    wlanDupWorld( &worldAuth, &worldAuth3 );
    clrEntryFromHashtableAuth( index, hRequest );
    hash_remove_entry( &hash_infoAuth, hRequest );

    /*Pack the data into the request Structure and call the encode/decode API */
    memset( &gRequest, 0, sizeof( gRequest ) );
    gRequest.choice = ulcm_cancelAuthRequest_chosen;
    gRequest.u.cancelAuthRequest = hRequest;
    encodeBuf.length = ULCM_L_BUF_SIZE;
    encodeBuf.value = msg;
    rc = wlanEncode( &worldAuth3, pdunum, &gRequest, &encodeBuf );
    /* wlanPrintPDU(&worldAuth3,pdunum,&gRequest); */
    if ( rc != 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "cancelAuthRequest failed: wlanEncode returned %d", rc );
	logErrorAuth( info_msgAuth );
	wlanterm( &worldAuth3 );
	return ULCM_L_ASN_ENCODING_ERROR;
    }


    pthread_mutex_lock( &socket_send_lock );
    rc = tcp_send( authGw_fd[driverID], encodeBuf.value, encodeBuf.length );
    pthread_mutex_unlock( &socket_send_lock );
    wlanterm( &worldAuth3 );

    if ( rc < 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "cancelAuthRequest() failed: send() returned %d \n", errno );
	logInfoAuth( info_msgAuth );
	return ULCM_L_TCP_SEND_FAILURE;
    }

    return ULCM_L_CANCEL_SIM_REQ_SUCCESS;
}

/* FUNCTION - ulcm_mg_t_RequestIMSI() Request IMSI Function */
/**************************************************************************

FUNCTION
        unsigned int
        ulcm_mg_t_RequestIMSI( unsigned int nModHandle,
                               const char * pMSISDN,
                               unsigned int* pIMSIRequest,
                               ulcm_mg_t_postIMSIResults *pFnCallback,
                               unsigned int iCallbackCookie)

DESCRIPTION
        Request the IMSI for the given MSISDN.

INPUTS
        Arg:
            nModHandle      - Module Handle
            pMSISDN         - MSISDN (in ASCII)
            pIMSIRequest    - Handle that identifies the request
                              (Used while canceling the request)
            pFnCallback     - Callback function that is invoked when
                              the response arrives from the gateway.
            iCallbackCookie - Passed as a parameter to the callback
                              function (for the client application to
                              correlate the request with the response)

        Others:

OUTPUTS
        Return:
              REQUEST_SUCESS     -   Request Succeeded
              MAX_ULCM_REQ_ERROR      -   Maximum Number of
                                     Outstanding requests.
              SEND_FAILURE    -      Not able to send the
                                     message to the gateway.
        Arg:
           pIMSIRequest        -
        Others:

LOGIC
        1.  Get an entry from the hash table.

        2.  Build the request into an ulcm_mg_Request structure.

        3.  Send the request to the gateway.

FEND
**************************************************************************/

int
ulcm_mg_t_RequestIMSI( unsigned int 			nModHandle,
		       const char 			*pMSISDN, 
		       unsigned int 			*pIMSIRequest, 
		       ulcm_mg_t_postIMSIResults	*pFnCallback, 
		       unsigned long 			iCallbackCookie, int driverID )
{

    ulcm_Request    gRequest;
    WlanBuf          encodeBuf;
    int             rc;
    int             freeSpot = 0;
    unsigned char   msg[ULCM_L_BUF_SIZE];
    char            error_msg[ULCM_L_ERROR_LENGTH];
    int             pdunum = ulcm_Request_PDU;
    WlanGlobal       worldAuth2;
    unsigned long   ret_val = 0;
    int             indx = 0;

    /* test if all arguments look valid */
    if ( checkModuleHandleAuth( nModHandle ) == -1 )
	return ULCM_L_INVALID_MOD_HANDLE;

    if ( ( pIMSIRequest == NULL ) || ( pMSISDN == NULL ) || ( pFnCallback == NULL ) )
	return ULCM_L_INVALID_ARG;

    /* Check EndPointAuths until we find  one for which TCP and thread are working. */
    for ( indx = 0; indx < getSctpEndPointAuthCountAuth(  ); indx++ )
    {
	if ( ( ret_val = initSctpAuth( getActiveSctpEndPointAuth(  ) ) ) != ULCM_L_SUCCESS )
	    incrementActiveSctpEndPointAuth(  );
	else
	{
	    break;		/* we have an active connection, we don't need to continue testing other EndPointAuths */
	}
    }
    if ( ret_val != ULCM_L_SUCCESS )
    {
	return ULCM_L_TCP_INIT_FAILURE;
    }

    /*create a requestID and associate this with the iCallbackCookie */
    *pIMSIRequest = getRequestIdAuth(  );
    freeSpot = getFreeHashtableSpotAuth(  );
    if ( freeSpot == -1 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_RequestIMSI(): " "Maximum number of concurrent requests exceeded already" );
	logErrorAuth( error_msg );
	return ULCM_L_MAX_REQ_EXCEDED;
    }
    pendReqAuth[freeSpot].callbackCookie = iCallbackCookie;
    pendReqAuth[freeSpot].reqId = *pIMSIRequest;
    pendReqAuth[freeSpot].pIMSICallback = pFnCallback;
    pendReqAuth[freeSpot].requestType = ulcm_RequestIMSI_chosen;
    hash_add_entry( &hash_infoAuth, *pIMSIRequest, freeSpot );

    /*Duplicate this and then use it. --S */
    wlanDupWorld( &worldAuth, &worldAuth2 );

    /*Pack the data into the request Structure and call the encode/decode API */
    memset( &gRequest, 0, sizeof( gRequest ) );
    gRequest.choice = ulcm_imsiRequest_chosen;
    gRequest.u.imsiRequest.requestId = *pIMSIRequest;
    gRequest.u.imsiRequest.msisdn.length = char2BCD( pMSISDN, &gRequest.u.imsiRequest.msisdn.value[0] );
    encodeBuf.length = ULCM_L_BUF_SIZE;
    encodeBuf.value = msg;
    rc = wlanEncode( &worldAuth2, pdunum, &gRequest, &encodeBuf );
    /* wlanPrintPDU(&worldAuth2,pdunum,&gRequest);
     */
    if ( rc != 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_RequestIMSI failed: wlanEncode returned %d", rc );
	logErrorAuth( info_msgAuth );
	clrEntryFromHashtableAuth( freeSpot, gRequest.u.imsiRequest.requestId );
	wlanterm( &worldAuth2 );
	return ULCM_L_ASN_ENCODING_ERROR;
    }

    pthread_mutex_lock( &socket_send_lock );
    rc = tcp_send( authGw_fd[driverID], encodeBuf.value, encodeBuf.length);
    pthread_mutex_unlock( &socket_send_lock );
    wlanterm( &worldAuth2 );

    if ( rc < 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_RequestIMSI() failed: send() returned %d\n", errno );
	logInfoAuth( info_msgAuth );
	clrEntryFromHashtableAuth( freeSpot, gRequest.u.imsiRequest.requestId );
	return ULCM_L_TCP_SEND_FAILURE;
    }
    return ULCM_L_REQ_IMSI_SUCCESS;

}

/* FUNCTION - ulcm_mg_t_cancelIMSIRequest() Cancel IMSI Request Function */
/**************************************************************************

FUNCTION
        unsigned int
        ulcm_mg_t_cancelIMSIRequest(unsigned int nModHandle,unsigned int hIMSIRequest)

DESCRIPTION
        Cancel the request (using the hIMSIRequest handle)

INPUTS
        Arg:
            nModHandle      - Module Handle
            hIMSIRequest    - Handle that identifies the request

        Others:

OUTPUTS
        Return:
              MOD_HANDLE_NOT_INIT   -   Module Uninitialised
              ULCM_INVALID_MOD_HANDLE    -   Module Handle Invalid
              REQUEST_NOT_FOUND     -   Request Succeeded
              SEND_FAILURE          -   Not able to send the
                                        message to the gateway.
        Arg:

        Others:

LOGIC
        1.  Get the entry from the hash table.

        2.  Build the request into an  ulcm_mg_Request structure.

        3.  Send the cancel request to the gateway.

FEND
**************************************************************************/
int
ulcm_mg_t_CancelIMSIRequest( unsigned int nModHandle, unsigned int hIMSIRequest, int driverID )
{
    unsigned char   msg[ULCM_L_BUF_SIZE];
    int             rc;
    WlanGlobal       worldAuth3;
    int             pdunum = ulcm_Request_PDU;
    ulcm_Request    gRequest;
    int             index = 0;
    WlanBuf          encodeBuf;

    if ( checkModuleHandleAuth( nModHandle ) == -1 )
	return ULCM_L_INVALID_MOD_HANDLE;
    if ( initReqDoneAuth[driverID] == 0 )
	return ULCM_L_REQ_NOT_FOUND;
    index = hash_get_entry( &hash_infoAuth, hIMSIRequest );
    if ( !index )
	return ULCM_L_REQ_NOT_FOUND;
    clrEntryFromHashtableAuth( index, hIMSIRequest );
    hash_remove_entry( &hash_infoAuth, hIMSIRequest );

    wlanDupWorld( &worldAuth, &worldAuth3 );
    /*Pack the data into the request Structure and call the encode/decode API */
    memset( &gRequest, 0, sizeof( gRequest ) );
    gRequest.choice = ulcm_cancelImsiRequest_chosen;
    gRequest.u.cancelImsiRequest = hIMSIRequest;
    encodeBuf.length = ULCM_L_BUF_SIZE;
    encodeBuf.value = msg;
    rc = wlanEncode( &worldAuth3, pdunum, &gRequest, &encodeBuf );
    if ( rc != 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_CancelIMSIRequest failed: wlanEncode returned %d", rc );
	logErrorAuth( info_msgAuth );
	wlanterm( &worldAuth3 );
	return ULCM_L_ASN_ENCODING_ERROR;
    }

    /* wlanPrintPDU(&worldAuth3,pdunum,&gRequest);
     */
    /*
       wlanPrintHex(&worldAuth,(char*)encodeBuf.value,encodeBuf.length);

       wlanPrintPDU(&worldAuth,pdunum,&gRequest);
       snprintf(error_msg, ULCM_L_ERROR_LENGTH,
       "ulcm_mg_t_cancelIMSIRequest() Request Id %d ",
       gRequest.u.cancelImsiRequest);
       logInfoAuth(error_msg);
     */


    pthread_mutex_lock( &socket_send_lock );
    rc = tcp_send( authGw_fd[driverID], encodeBuf.value, encodeBuf.length);
    pthread_mutex_unlock( &socket_send_lock );
    wlanterm( &worldAuth3 );

    if ( rc < 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_CancelIMSIRequest() failed: send() returned %d \n", errno );
	logInfoAuth( info_msgAuth );
	return ULCM_L_TCP_SEND_FAILURE;
    }
    return ULCM_L_CANCEL_IMSI_REQ_SUCCESS;
}

/* FUNCTION - ulcm_mg_t_RequestUpdateGprs() Request UpdateGprs Function */
/**************************************************************************

FUNCTION
        unsigned int
        ulcm_mg_t_RequestUpdateGprs( unsigned int nModHandle,
                               const char *pIMSI,
                               const ulcm_mg_updategprsrequest_t *pUpdateGprsParams,
                               unsigned int *pUpdateGprsRequest,
                               ulcm_mg_t_postGrpsUpdateResults *pFnCallback,
                               unsigned int iCallbackCookie)

DESCRIPTION
        Request UpdateGprs for the given IMSI.

INPUTS
        Arg:
            nModHandle      - Module Handle
            pIMSI           - pointer to IMSI
            pUpdateGprsParams     - UpdateGprsRequest parameters
            pUpdateGprsRequest    - Handle that identifies the request
                              (Used while canceling the request)
            pFnCallback     - Callback function that is invoked when
                              the response arrives from the gateway.
            iCallbackCookie - Passed as a parameter to the callback
                              function (for the client application to
                              correlate the request with the response)

        Others:

OUTPUTS
        Return:
              REQUEST_SUCESS     -   Request Succeeded
              MAX_ULCM_REQ_ERROR      -   Maximum Number of
                                     Outstanding requests.
              SEND_FAILURE    -      Not able to send the
                                     message to the gateway.
        Arg:
           pUpdateGprsRequest        -
        Others:

LOGIC
        1.  Get an entry from the hash table.

        2.  Build the request into an ulcm_mg_Request structure.

        3.  Send the request to the gateway.

FEND
**************************************************************************/

int
ulcm_mg_t_RequestUpdateGprs( unsigned int 		nModHandle,
		       const char *pIMSI,
		       const ulcm_mg_updategprsrequest_t *pUpdateGprsParams,
		       unsigned int 			*pUpdateGprsRequest, 
		       ulcm_mg_t_postUpdateGprsResults	*pFnCallback, 
		       unsigned int 			iCallbackCookie, int driverID )
{

    ulcm_Request    gRequest;
    WlanBuf          encodeBuf;
    int             rc;
    int             freeSpot = 0;
    unsigned char   msg[ULCM_L_BUF_SIZE];
    char            error_msg[ULCM_L_ERROR_LENGTH];
    int             pdunum = ulcm_Request_PDU;
    WlanGlobal       worldAuth2;
    unsigned long   ret_val = 0;
    int             indx = 0;

    /* test if all arguments look valid */
    if ( checkModuleHandleAuth( nModHandle ) == -1 )
	return ULCM_L_INVALID_MOD_HANDLE;

    if ( ( pUpdateGprsRequest == NULL ) || ( pUpdateGprsParams == NULL ) || ( pFnCallback == NULL ) )
	return ULCM_L_INVALID_ARG;

	ret_val = initSctpAuth( driverID );
    /* Check EndPointAuths until we find  one for which TCP and thread are working. */
    //~ for ( indx = 0; indx < getSctpEndPointAuthCountAuth(  ); indx++ )
    //~ {
	//~ if ( ( ret_val = initSctpAuth( getActiveSctpEndPointAuth(  ) ) ) != ULCM_L_SUCCESS )
	    //~ incrementActiveSctpEndPointAuth(  );
	//~ else
	//~ {
	    //~ break;		/* we have an active connection, we don't need to continue testing other EndPointAuths */
	//~ }
    //~ }
    if ( ret_val != ULCM_L_SUCCESS )
    {
	return ULCM_L_TCP_INIT_FAILURE;
    }

    /*create a requestID and associate this with the iCallbackCookie */
    *pUpdateGprsRequest = getRequestIdAuth(  );
    freeSpot = getFreeHashtableSpotAuth(  );
    if ( freeSpot == -1 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_RequestUpdateGprs(): " "Maximum number of concurrent requests exceeded already" );
	logErrorAuth( error_msg );
	return ULCM_L_MAX_REQ_EXCEDED;
    }
    pendReqAuth[freeSpot].callbackCookie = iCallbackCookie;
    pendReqAuth[freeSpot].reqId = *pUpdateGprsRequest;
    pendReqAuth[freeSpot].pUpdateGprsCallback = pFnCallback;
    pendReqAuth[freeSpot].requestType = ulcm_RequestUpdateGprs_chosen;
    hash_add_entry( &hash_infoAuth, *pUpdateGprsRequest, freeSpot );

    /*Duplicate this and then use it. --S */
    wlanDupWorld( &worldAuth, &worldAuth2 );

    /*Pack the data into the request Structure and call the encode/decode API */
    memset( &gRequest, 0, sizeof( gRequest ) );
    gRequest.choice = ulcm_updateGprsRequest_chosen;
    gRequest.u.updateGprsRequest.requestId = *pUpdateGprsRequest;
    gRequest.u.updateGprsRequest.updateGprsLocationArg = pUpdateGprsParams->updateGprsLocationArg;
    gRequest.u.updateGprsRequest.updateGprsLocationArg.imsi.length = char2BCD( pIMSI, &gRequest.u.updateGprsRequest.updateGprsLocationArg.imsi.value[0] );
    encodeBuf.length = ULCM_L_BUF_SIZE;
    encodeBuf.value = msg;
    rc = wlanEncode( &worldAuth2, pdunum, &gRequest, &encodeBuf );
    /* wlanPrintPDU(&worldAuth2,pdunum,&gRequest);
     */
    if ( rc != 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_RequestUpdateGprs failed: wlanEncode returned %d", rc );
	logErrorAuth( info_msgAuth );
	clrEntryFromHashtableAuth( freeSpot, gRequest.u.updateGprsRequest.requestId );
	wlanterm( &worldAuth2 );
	return ULCM_L_ASN_ENCODING_ERROR;
    }

    pthread_mutex_lock( &socket_send_lock );
    rc = tcp_send( authGw_fd[driverID], encodeBuf.value, encodeBuf.length);
    pthread_mutex_unlock( &socket_send_lock );
    wlanterm( &worldAuth2 );

    if ( rc < 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_RequestUpdateGprs() failed: send() returned %d\n", errno );
	logInfoAuth( info_msgAuth );
	clrEntryFromHashtableAuth( freeSpot, gRequest.u.updateGprsRequest.requestId );
	return ULCM_L_TCP_SEND_FAILURE;
    }
    return ULCM_L_REQ_UPDATE_GPRS_SUCCESS;

}

/* FUNCTION - ulcm_mg_t_cancelUpdateGprsRequest() Cancel UpdateGprs Request Function */
/**************************************************************************

FUNCTION
        unsigned int
        ulcm_mg_t_cancelUpdateGprsRequest(unsigned int nModHandle,unsigned int hUpdateGprsRequest)

DESCRIPTION
        Cancel the request (using the hUpdateGprsRequest handle)

INPUTS
        Arg:
            nModHandle      - Module Handle
            hUpdateGprsRequest    - Handle that identifies the request

        Others:

OUTPUTS
        Return:
              MOD_HANDLE_NOT_INIT   -   Module Uninitialised
              ULCM_INVALID_MOD_HANDLE    -   Module Handle Invalid
              REQUEST_NOT_FOUND     -   Request Succeeded
              SEND_FAILURE          -   Not able to send the
                                        message to the gateway.
        Arg:

        Others:

LOGIC
        1.  Get the entry from the hash table.

        2.  Build the request into an  ulcm_mg_Request structure.

        3.  Send the cancel request to the gateway.

FEND
**************************************************************************/
int
ulcm_mg_t_CancelUpdateGprsRequest( unsigned int nModHandle, unsigned int hUpdateGprsRequest, int driverID )
{
    unsigned char   msg[ULCM_L_BUF_SIZE];
    int             rc;
    WlanGlobal       worldAuth3;
    int             pdunum = ulcm_Request_PDU;
    ulcm_Request    gRequest;
    int             index = 0;
    WlanBuf          encodeBuf;

    if ( checkModuleHandleAuth( nModHandle ) == -1 )
	return ULCM_L_INVALID_MOD_HANDLE;
    if ( initReqDoneAuth[driverID] == 0 )
	return ULCM_L_REQ_NOT_FOUND;
    index = hash_get_entry( &hash_infoAuth, hUpdateGprsRequest );
    if ( !index )
	return ULCM_L_REQ_NOT_FOUND;
    clrEntryFromHashtableAuth( index, hUpdateGprsRequest );
    hash_remove_entry( &hash_infoAuth, hUpdateGprsRequest );

    wlanDupWorld( &worldAuth, &worldAuth3 );
    /*Pack the data into the request Structure and call the encode/decode API */
    memset( &gRequest, 0, sizeof( gRequest ) );
    gRequest.choice = ulcm_cancelUpdateGprsRequest_chosen;
    gRequest.u.cancelUpdateGprsRequest = hUpdateGprsRequest;
    encodeBuf.length = ULCM_L_BUF_SIZE;
    encodeBuf.value = msg;
    rc = wlanEncode( &worldAuth3, pdunum, &gRequest, &encodeBuf );
    if ( rc != 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_CancelUpdateGprsRequest failed: wlanEncode returned %d", rc );
	logErrorAuth( info_msgAuth );
	wlanterm( &worldAuth3 );
	return ULCM_L_ASN_ENCODING_ERROR;
    }

    /* wlanPrintPDU(&worldAuth3,pdunum,&gRequest);
     */
    /*
       wlanPrintHex(&worldAuth,(char*)encodeBuf.value,encodeBuf.length);

       wlanPrintPDU(&worldAuth,pdunum,&gRequest);
       snprintf(error_msg, ULCM_L_ERROR_LENGTH,
       "ulcm_mg_t_cancelUpdateGprsRequest() Request Id %d ",
       gRequest.u.cancelUpdateGprsRequest);
       logInfoAuth(error_msg);
     */


    pthread_mutex_lock( &socket_send_lock );
    rc = tcp_send( authGw_fd[driverID], encodeBuf.value, encodeBuf.length);
    pthread_mutex_unlock( &socket_send_lock );
    wlanterm( &worldAuth3 );

    if ( rc < 0 )
    {
	snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_CancelUpdateGprsRequest() failed: send() returned %d \n", errno );
	logInfoAuth( info_msgAuth );
	return ULCM_L_TCP_SEND_FAILURE;
    }
    return ULCM_L_CANCEL_UPDATE_GPRS_SUCCESS;
}


/**************************************************************************

FUNCTION
	int ulcm_mg_t_RequestAuthorizationInfo( unsigned int nModHandle,
			const char *pIMSI,
			unsigned int *pSIMRequest, 
			ulcm_mg_t_postAuthorizationResults * pFnCallback, 
			unsigned int iCallbackCookie )

DESCRIPTION
	Send profile information  request to auth gateway.
INPUTS
        Arg:
            nModHandle      - Module Handle
	    pIMSI	    - pointer to IMSI
	    pSIMRequest	    - Handle that identifies the request
                              (Used while canceling the request)
	    pFnCallback	    - Callback function that is invoked when
                              the response arrives from the gateway.
	    iCallbackCookie - Passed as a parameter to the callback
                              function (for the client application to
                              correlate the request with the response)
OUTPUTS
        Return:
	      ULCM_L_TCP_INIT_FAILURE - TCP Initilization fail
              MOD_HANDLE_NOT_INIT   -   Module Uninitialised
	      ULCM_L_INVALID_ARG    -   Invalid argument
	      ULCM_L_MAX_REQ_EXCEDED - Maximum request exceeded

FEND
*******************************************************************************/

int
ulcm_mg_t_RequestAuthorizationInfo( unsigned int nModHandle,
				    const char *pIMSI,
				    unsigned int *pSIMRequest, 
				    ulcm_mg_t_postAuthorizationResults * pFnCallback, 
				    unsigned long iCallbackCookie, int driverID )
{
    ulcm_Request    gRequest;
    int             freeSpot = 0;
    char            error_msg[ULCM_L_ERROR_LENGTH];
    unsigned long   ret_val = 0;
    int             indx = 0;
    int             ret = 0;

    /* Check EndPointAuths until we find one for which TCP and thread are working. */
//    for ( indx = 0; indx < getSctpEndPointAuthCountAuth(  ); indx++ )
//    {
//	if ( ( ret_val = initSctpAuth( getActiveSctpEndPointAuth(  ) ) ) != ULCM_L_SUCCESS )
//	    incrementActiveSctpEndPointAuth(  );
//	else
//	    break;		/* we have an active connection, we don't need to continue  */
//   }

	ret_val = initSctpAuth( driverID );

    if ( ret_val != ULCM_L_SUCCESS )
	return ULCM_L_TCP_INIT_FAILURE;

    snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_RequestAuthorizationInfo called. \n" );
    logInfoAuth( info_msgAuth );

    /* test if all arguments look valid */
    if ( checkModuleHandleAuth( nModHandle ) == -1 )
	return ULCM_L_INVALID_MOD_HANDLE;
    if ( ( pIMSI == NULL ) || ( pFnCallback == NULL ) || ( pSIMRequest == NULL ) )
	return ULCM_L_INVALID_ARG;

    /*create a requestID and associate this with the iCallbackCookie */
    *pSIMRequest = getRequestIdAuth(  );
//    printf("ulcm_mg_t_RequestAuthorizationInfo: iCallbackCookie=%d, requestId=%d\n", iCallbackCookie, *pSIMRequest);

    freeSpot = getFreeHashtableSpotAuth(  );
    if ( freeSpot == -1 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_RequestAuthorization(): Maximum Requests Reached Already" );
	logErrorAuth( error_msg );
	return ULCM_L_MAX_REQ_EXCEDED;
    }
    pendReqAuth[freeSpot].callbackCookie = iCallbackCookie;
    pendReqAuth[freeSpot].reqId = *pSIMRequest;
    pendReqAuth[freeSpot].pAuthorizationCallback = pFnCallback;
    pendReqAuth[freeSpot].requestType = ulcm_RequestAuthorizationInfo_chosen;
    hash_add_entry( &hash_infoAuth, *pSIMRequest, freeSpot );

    /*Pack the data into the  request Structure and call the encode/decode API */
    memset( &gRequest, 0, sizeof( gRequest ) );
    gRequest.choice = ulcm_authRequest_chosen;
    gRequest.u.authRequest.requestId = *pSIMRequest;
    gRequest.u.authRequest.imsi.length = char2BCD( pIMSI, &gRequest.u.authRequest.imsi.value[0] );
    gRequest.u.authRequest.numberOfRequestedVectors = NO_TRIPLETS;

    ret = sendAuthRequest( freeSpot, gRequest, driverID );

    if( ret == ULCM_L_REQ_SIM_TRIPLET_SUCCESS )
    {
	return ULCM_L_REQ_AUTH_INFO_SUCCESS;
    }
    else
    {
	return ret;
    }
}


/**************************************************************************

FUNCTION
	int ulcm_mg_t_RequestAuthorizationInfoEx(unsigned int nModHandle,
                                     const char * pIMSI,
                                     unsigned int * pSIMRequest,
                                     ulcm_mg_t_postAuthorizationResultsEx * pFnCallback,
                                     unsigned int iCallbackCookie )

DESCRIPTION
	Send profile information  request to auth gateway.

INPUTS
        Arg:
            nModHandle      - Module Handle
	    pIMSI	    - pointer to IMSI
	    pSIMRequest	    - Handle that identifies the request
                              (Used while canceling the request)
	    pFnCallback	    - Callback function that is invoked when
                              the response arrives from the gateway.
	    iCallbackCookie - Passed as a parameter to the callback
                              function (for the client application to
                              correlate the request with the response)
OUTPUTS
        Return:
	      ULCM_L_TCP_INIT_FAILURE - TCP Initilization fail
	      ULCM_L_INVALID_MOD_HANDLE - Invalid Module handler
	      ULCM_L_INVALID_ARG    -   Invalid argument
	      ULCM_L_MAX_REQ_EXCEDED - Maximum request exceeded

FEND
*******************************************************************************/

int
ulcm_mg_t_RequestAuthorizationInfoEx(unsigned int nModHandle,
                                     const char * pIMSI,
                                     unsigned int * pSIMRequest,
                                     ulcm_mg_t_postAuthorizationResultsEx * pFnCallback,
                                     unsigned long iCallbackCookie )
{
  ulcm_Request gRequest;
  int freeSpot = 0;
  char error_msg[ULCM_L_ERROR_LENGTH];
  unsigned long ret_val = 0;
  int indx = 0;

  /* Check EndPointAuths until we find one for which TCP and thread are working.
*/
  for(indx = 0; indx < getSctpEndPointAuthCountAuth(); indx++)
  {
      if ((ret_val = initSctpAuth(getActiveSctpEndPointAuth())) != ULCM_L_SUCCESS)
          incrementActiveSctpEndPointAuth();
      else
          break;  /* we have an active connection, we don't need to continue  */
  }
  if ( ret_val != ULCM_L_SUCCESS)
      return ULCM_L_TCP_INIT_FAILURE;

  snprintf(info_msgAuth, ULCM_L_ERROR_LENGTH,
           "ulcm_mg_t_RequestAuthorizationInfo called. \n");
  logInfoAuth(info_msgAuth);
  /* test if all arguments look valid */
  if(checkModuleHandleAuth(nModHandle) == -1)
    return ULCM_L_INVALID_MOD_HANDLE;
  if((pIMSI == NULL) || (pFnCallback == NULL)||(pSIMRequest == NULL))
    return ULCM_L_INVALID_ARG;

  /*create a requestID and associate this with the iCallbackCookie*/
  *pSIMRequest = getRequestIdAuth();

  freeSpot = getFreeHashtableSpotAuth();
  if(freeSpot == -1){
    snprintf(error_msg, ULCM_L_ERROR_LENGTH,
            "ulcm_mg_t_RequestAuthorizationEx(): Maximum Requests Reached Already"
);
    logErrorAuth(error_msg);
    return ULCM_L_MAX_REQ_EXCEDED;
  }
  pendReqAuth[freeSpot].callbackCookie = iCallbackCookie;
  pendReqAuth[freeSpot].reqId = *pSIMRequest;
  pendReqAuth[freeSpot].pAuthorizationExCallback = pFnCallback;
  pendReqAuth[freeSpot].requestType = ulcm_RequestAuthorizationInfoEx_chosen;
  hash_add_entry(&hash_infoAuth,*pSIMRequest,freeSpot);

  /*Pack the data into the request Structure and call the encode/decode API*/
  gRequest.choice = ulcm_authRequest_chosen;
  gRequest.u.authRequest.requestId = *pSIMRequest;
  gRequest.u.authRequest.imsi.length =
            char2BCD(pIMSI, (unsigned char *)&gRequest.u.authRequest.imsi.value);
  gRequest.u.authRequest.numberOfRequestedVectors = NO_TRIPLETS;

 return sendRequestTriplets(freeSpot, gRequest, 0);
}

/**************************************************************************

FUNCTION
	int ulcm_mg_t_CancelAuthorizationRequest( unsigned int nModHandle, 
				unsigned int hAuthorizationRequest )

DESCRIPTION
	Cancel authorization request.

INPUTS
        Arg:
            nModHandle      - Module Handle
	    hAuthorizationRequest - Handle that identifies the request

OUTPUTS
        Return:
	      MOD_HANDLE_NOT_INIT   -   Module Uninitialised
	      ULCM_INVALID_MOD_HANDLE    -   Module Handle Invalid
	      REQUEST_NOT_FOUND     -   Request Succeeded
	      SEND_FAILURE          -   Not able to send the 
		                        message to the gateway.

FEND
*******************************************************************************/

int
ulcm_mg_t_CancelAuthorizationRequest( unsigned int nModHandle, unsigned int hAuthorizationRequest, int driverID )
{
    int ret = ulcm_mg_t_CancelSIMRequest( nModHandle, hAuthorizationRequest, driverID );

    if( ret == ULCM_L_CANCEL_SIM_REQ_SUCCESS )
    {
	return ULCM_L_CANCEL_AUTH_REQ_SUCCESS;
    }
    else
    {
	return ret;
    }
}


/**************************************************************************

FUNCTION
        unsigned int
	ulcm_mg_t_RequestSIMTripletsEx( unsigned int nModHandle,
	                           const char * pIMSI,
	                           unsigned int iTripletCount,
				   unsigned int* pSIMRequest,
				   ulcm_mg_t_postSIMResults *pFnCallback,
				   unsigned int iCallbackCookie)

DESCRIPTION
	Request SIM triplets for the given IMSI.

INPUTS
	Arg:	
	    nModHandle      - Module Handle
	    pIMSI           - IMSI (in ASCII)
	    iTripletCount   - Number of triplets requested (1..3)
	    pSIMRequest     - Handle that identifies the request 
	                      (Used while canceling the request)
	    pFnCallback     - Callback function that is invoked when 
	                      the response arrives from the gateway.
            iCallbackCookie - Passed as a parameter to the callback 
	                      function (for the client application to
			      correlate the request with the response)


OUTPUTS
	Return: 
	      REQUEST_SUCESS     -   Request Succeeded
	      MAX_ULCM_REQ_ERROR      -   Maximum Number of 
                                     Outstanding requests.
	      SEND_FAILURE    -      Not able to send the 
		                     message to the gateway.
      	Arg:	
	   pSIMRequest        -  


FEND
**************************************************************************/

int
ulcm_mg_t_RequestSIMTripletsEx( unsigned int nModHandle,
                                const char * pIMSI,
                                unsigned int iTripletCount,
                                unsigned int* pSIMRequest,
                                ulcm_mg_t_postSIMResultsEx *pFnCallback,
                                unsigned long iCallbackCookie)
{
  ulcm_Request gRequest;
  int freeSpot = 0;
  char error_msg[ULCM_L_ERROR_LENGTH];

  /* test if all arguments look valid */
  if(checkModuleHandleAuth(nModHandle) == -1)
    return ULCM_L_INVALID_MOD_HANDLE;
  if((pIMSI == NULL) || (pFnCallback == NULL)||(pSIMRequest == NULL))
    return ULCM_L_INVALID_ARG;

  *pSIMRequest = getRequestIdAuth();

  /*create a requestID and associate this with the iCallbackCookie*/
  freeSpot = getFreeHashtableSpotAuth();
  if(freeSpot == -1){
    snprintf(error_msg, ULCM_L_ERROR_LENGTH,
            "ulcm_mg_t_RequestSIMTripletsEx() "
             "Maximum Requests Already Reached ");
    logErrorAuth(error_msg);
    return ULCM_L_MAX_REQ_EXCEDED;
  }
  pendReqAuth[freeSpot].callbackCookie = iCallbackCookie;
  pendReqAuth[freeSpot].reqId = *pSIMRequest;
  pendReqAuth[freeSpot].pExCallback = pFnCallback;
  pendReqAuth[freeSpot].requestType = ulcm_RequestSIMTripletsEx_chosen;
  hash_add_entry(&hash_infoAuth,*pSIMRequest,freeSpot);

  /*Pack the data into the request Structure and call the encode/decode API*/
  gRequest.choice = ulcm_authRequest_chosen;
  gRequest.u.authRequest.requestId = *pSIMRequest;
  gRequest.u.authRequest.imsi.length =
               char2BCD(pIMSI, (unsigned char *)&gRequest.u.authRequest.imsi.value);
  gRequest.u.authRequest.numberOfRequestedVectors = iTripletCount;

  return sendRequestTriplets(freeSpot, gRequest, 0);

}

/**************************************************************************

FUNCTION
	int sendRequestTriplets( int authRequestIndex,
                     ulcm_Request gRequest)

DESCRIPTION
	Send request for triplets .


INPUTS
	Arg:	
	    authRequestIndex - authentication request index
	    gRequest - request structure

OUTPUTS
	Return: 
	      ULCM_L_TCP_SEND_FAILURE
	      ULCM_L_REQ_SIM_TRIPLET_SUCCESS 

FEND
**************************************************************************/

int
sendRequestTriplets( int authRequestIndex,
                     ulcm_Request gRequest, int driverID)
{
  WlanBuf encodeBuf;
  int rc;
  unsigned char msg[ULCM_L_BUF_SIZE];
  int pdunum = ulcm_Request_PDU;
  WlanGlobal worldAuth2;

  /*Duplicate this and then use it. --S*/
  wlanDupWorld(&worldAuth,&worldAuth2);

  encodeBuf.length = ULCM_L_BUF_SIZE;
  encodeBuf.value = msg;
  wlanEncode(&worldAuth2,pdunum,&gRequest,&encodeBuf);
//  wlanPrintPDU(&worldAuth2,pdunum,&gRequest);

  pthread_mutex_lock( &socket_send_lock );
  rc = tcp_send( authGw_fd[driverID], encodeBuf.value, encodeBuf.length);
  pthread_mutex_unlock( &socket_send_lock );

  wlanterm( &worldAuth2 );

  if(rc<0) {

      snprintf(info_msgAuth, ULCM_L_ERROR_LENGTH,
               "ulcm_mg_t_RequestSIMTriplets() "
               "sctp_send () failed\n");
      clrEntryFromHashtableAuth(authRequestIndex, gRequest.u.authRequest.requestId);
      return ULCM_L_TCP_SEND_FAILURE;
  }
  return ULCM_L_REQ_SIM_TRIPLET_SUCCESS;

}

long timevaldiff(struct timeval *starttime, struct timeval *finishtime)
{
  long msec;
  msec=(finishtime->tv_sec-starttime->tv_sec)*1000;
  msec+=(finishtime->tv_usec-starttime->tv_usec)/1000;
  return msec;
}


/* This function listens on a client socket and calls a fuSIMnction
   that matches the request with the response. --S*/
/* FUNCTION - respHandler() Module Initialization Function */
/**************************************************************************

FUNCTION
        void
	respHandler()
	
DESCRIPTION
	Response Handler Function.

INPUTS
	Arg:	temp
	Others:	

OUTPUTS
	Return:	
      	Arg:	
	Others:	

LOGIC
	1.  Receive Response from the Gateway on a TCP connection
	
	2.  Call the Callback function after decoding the result.
	
	3.  Remove the Hashtable EntriesAuth.

FEND
**************************************************************************/

static void    *
respHandlerAuth( void *temp )
{
	int driverID = (int) temp;
    char            msg[ULCM_L_BUF_SIZE];
    int             n;
    int             i = 0;
    int             index, tempTripletCount, tempQuintupletCount;
    char            error_msg[ULCM_L_ERROR_LENGTH];
    WlanGlobal       worldAuth1;
    int             pdunum = ulcm_Response_PDU;
    ulcm_mg_triplet_t pTripletBuffer[5], *pTripletStart;
    ulcm_mg_quintuplet_t pQuintupletBuffer[5], *pQuintupletStart;
    ulcm_mg_updategprsresponse_t updategprsresponse;
    static int      error = -1;
    char            servStr[ULCM_L_MAX_SERVICE_LIST_STR];
    char            imsiStr[ULCM_L_MAX_STR_LENGTH];
    char            msisdnStr[ULCM_L_MAX_STR_LENGTH];
    char 	    msisdnreceived[ULCM_L_MAX_MSISDN_STR];
    int		    subscriberStatus;
	int 	vlrSubscriptionInfoPresent;

    /*duplicate the WlanworldAuth and then use it. --S */

    wlanDupWorld( &worldAuth, &worldAuth1 );
    snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "Thread gets created \n" );
    logInfoAuth( info_msgAuth );

    while ( 1 )
    {
	ulcm_Response   gResponse;
	ulcm_Response  *pGResponse;
	WlanBuf          decodeBuf;

	/* receive segments */

	memset( &msg, 0x0, sizeof(WlanPdu));
    n = tcp_recv( authGw_fd[driverID], msg,sizeof(WlanPdu));
	if ( n <= 0 )
	{
	    snprintf( info_msgAuth, ULCM_L_ERROR_LENGTH, "respHandler() : recv() failed\n" );
	    logInfoAuth( info_msgAuth );
	    initReqDoneAuth[driverID] = 0;
	    return NULL;
	}
	/* wlanPrintHex(&worldAuth1,(char*)msg,n); 
	   wlanPrintPDU(&worldAuth1,pdunum,(char*)msg);
	 */

	/*Unpack the data here. --S */
	decodeBuf.length = n; //ULCM_L_BUF_SIZE;
	decodeBuf.value = ( unsigned char * ) msg;
	pGResponse = &gResponse;
	wlanSetDecodingLength( &worldAuth1, sizeof( ulcm_Response ) );
	if ( 0 != wlanDecode( &worldAuth1, &pdunum, &decodeBuf, ( void ** ) &pGResponse ) )
	{
	    snprintf( error_msg, ULCM_L_ERROR_LENGTH, 
		    "respHandler() Error %d while decoding the received message. Restarting socket\n", n );
	    logErrorAuth( error_msg );
	    printf(error_msg);
	    initReqDoneAuth[driverID] = 0;
	    return NULL;
	}
	/*Get the triplet count and triplet buffer. --S */
	pTripletStart = NULL;
	pQuintupletStart = NULL;
	tempTripletCount = 0;
	tempQuintupletCount = 0;
	switch ( gResponse.choice )
	{
	case ulcm_imsiResponse_chosen:
	    {
		imsi2ascii( gResponse.u.imsiResponse.imsi.value, gResponse.u.imsiResponse.imsi.length, imsiStr );

		index = hash_get_entry( &hash_infoAuth, gResponse.u.imsiResponse.requestId );
		if ( !index )
		{
		    /* Cannot find entry for this dialogId */
		    snprintf( error_msg, ULCM_L_ERROR_LENGTH,
			      "respHandler() : Cannot find requestId %u in the Hash table", gResponse.u.authResponse.requestId );
		    logErrorAuth( error_msg );
		    printf(error_msg);
		    continue;
		}
		else
		{
		    ( pendReqAuth[index].pIMSICallback ) ( pendReqAuth[index].callbackCookie, gResponse.u.imsiResponse.errorCode, &imsiStr[0] );
		    clrEntryFromHashtableAuth( index, gResponse.u.imsiResponse.requestId );
			if ( !hash_remove_entry( &hash_infoAuth, gResponse.u.imsiResponse.requestId ) )
		    {
			/* Cannot find entry for this RequestId */
			snprintf( error_msg, ULCM_L_ERROR_LENGTH, "respHandler() : "
				  " Cannot remove request #%d from the Hash table", gResponse.u.imsiResponse.requestId );
			logErrorAuth( error_msg );
		    printf(error_msg);
			continue;	/*Cannot clear the hash table entry index is 0 !!! */
		    }
		}		/* else index */

		break;
	    }			/* end case ulcm_imsiResponse_chosen */
	case ulcm_updateGprsResponse_chosen:
	    {
		index = hash_get_entry( &hash_infoAuth, gResponse.u.updateGprsResponse.requestId );
		if ( !index )
		{
		    /* Cannot find entry for this dialogId */
		    snprintf( error_msg, ULCM_L_ERROR_LENGTH,
			      "respHandler() : Cannot find requestId %d in the Hash table", gResponse.u.updateGprsResponse.requestId );
		    logErrorAuth( error_msg );
		    printf(error_msg);
		    continue;
		}
		else
		{
		    updategprsresponse.updateGprsLocationRes = gResponse.u.updateGprsResponse.updateGprsLocationRes;
		    updategprsresponse.insertSubscriberDataArg = gResponse.u.updateGprsResponse.insertSubscriberDataArg;
		    ( pendReqAuth[index].pUpdateGprsCallback ) ( pendReqAuth[index].callbackCookie, gResponse.u.updateGprsResponse.errorCode, &updategprsresponse );
		    clrEntryFromHashtableAuth( index, gResponse.u.updateGprsResponse.requestId );

		    if ( !hash_remove_entry( &hash_infoAuth, gResponse.u.updateGprsResponse.requestId ) )
		    {
			/* Cannot find entry for this RequestId */
			snprintf( error_msg, ULCM_L_ERROR_LENGTH, "respHandler() : "
				  " Cannot remove request #%d from the Hash table", gResponse.u.updateGprsResponse.requestId );
			logErrorAuth( error_msg );
		    printf(error_msg);
			continue;	/*Cannot clear the hash table entry index is 0 !!! */
		    }
		}		/* else index */

		break;
	    }			/* end case ulcm_updateGprsResponse_chosen */

	case ulcm_authResponse_chosen:
	    {
		if ( gResponse.u.authResponse.bit_mask == ulcm_authenticationSetList_present )
		{
		    if ( gResponse.u.authResponse.authenticationSetList.choice == ulcm_tripletList_chosen )
		    {
			tempTripletCount = gResponse.u.authResponse.authenticationSetList.u.tripletList.count;
			pTripletStart = pTripletBuffer;
			for ( i = 0; i < tempTripletCount; i++ )
			{
			    memcpy( pTripletBuffer[i].RAND,
				    gResponse.u.authResponse.authenticationSetList.u.
				    tripletList.value[i].rand.value, gResponse.u.authResponse.authenticationSetList.u.tripletList.value[i].rand.length );

			    memcpy( pTripletBuffer[i].SRES,
				    gResponse.u.authResponse.authenticationSetList.u.
				    tripletList.value[i].sres.value, gResponse.u.authResponse.authenticationSetList.u.tripletList.value[i].sres.length );

			    memcpy( pTripletBuffer[i].Kc,
				    gResponse.u.authResponse.authenticationSetList.u.
				    tripletList.value[i].kc.value, gResponse.u.authResponse.authenticationSetList.u.tripletList.value[i].kc.length );
			}	/* for */
		    }		/* if ulcm_tripletList_chosen */
		    else if ( gResponse.u.authResponse.authenticationSetList.choice == ulcm_quintupletList_chosen )
		    {
			tempQuintupletCount = gResponse.u.authResponse.authenticationSetList.u.quintupletList.count;
			pQuintupletStart = pQuintupletBuffer;
			for ( i = 0; i < tempQuintupletCount; i++ )
			{
			    memcpy( pQuintupletBuffer[i].RAND,
				    gResponse.u.authResponse.authenticationSetList.u.
				    quintupletList.value[i].rand.value, gResponse.u.authResponse.authenticationSetList.u.quintupletList.value[i].rand.length );

			    memcpy( pQuintupletBuffer[i].XRES,
				    gResponse.u.authResponse.authenticationSetList.u.
				    quintupletList.value[i].xres.value, gResponse.u.authResponse.authenticationSetList.u.quintupletList.value[i].xres.length );

			    memcpy( pQuintupletBuffer[i].CK,
				    gResponse.u.authResponse.authenticationSetList.u.
				    quintupletList.value[i].ck.value, gResponse.u.authResponse.authenticationSetList.u.quintupletList.value[i].ck.length );

			    memcpy( pQuintupletBuffer[i].IK,
				    gResponse.u.authResponse.authenticationSetList.u.
				    quintupletList.value[i].ik.value, gResponse.u.authResponse.authenticationSetList.u.quintupletList.value[i].ik.length );

			    memcpy( pQuintupletBuffer[i].AUTN,
				    gResponse.u.authResponse.authenticationSetList.u.
				    quintupletList.value[i].autn.value, gResponse.u.authResponse.authenticationSetList.u.quintupletList.value[i].autn.length );
			}	/* for */
		    }		/* if ulcm_quintupletList_chosen */
		}		/* if ulcm_authenticationSetList_present */

		/*Now get the callbackCookie  to pass back to the client */
		index = hash_get_entry( &hash_infoAuth, gResponse.u.authResponse.requestId );
		if ( !index )
		{
		    /* Cannot find entry for this dialogId */
		    snprintf( error_msg, ULCM_L_ERROR_LENGTH,
			      "respHandler() : Cannot find requestId %d in the Hash table", gResponse.u.authResponse.requestId );
		    logErrorAuth( error_msg );
		    printf(error_msg);
		    continue;
		}
		else
		{
		    /*Got the callback function and callback cookie. 
		       Call the callback function now. --S */

		    if ( gResponse.u.authResponse.serviceFound == 0 )
			snprintf( servStr, ULCM_L_MAX_SERVICE_LIST_STR, "%s", "NoService" );
		    if ( strlen( gResponse.u.authResponse.serviceList ) == 0 )
			snprintf( servStr, ULCM_L_MAX_SERVICE_LIST_STR, "%s", "NoAuthorization" );
		    else if ( gResponse.u.authResponse.serviceFound == 1 )
			snprintf( servStr, ULCM_L_MAX_SERVICE_LIST_STR, "%s", gResponse.u.authResponse.serviceList );
		    
		    if (gResponse.u.authResponse.msisdn.length == 0)
  			snprintf( msisdnreceived, ULCM_L_MAX_MSISDN_STR, "%s", "0000000000" );
		    else if (gResponse.u.authResponse.msisdn.length != 0 ){
/*			printf("\nCondition satifished\n");
			int tempLength = gResponse.u.authResponse.msisdn.length * 2;
			char temp[tempLength];
			int i = 0, pos = 0;
			for(i = 0 ; i < gResponse.u.authResponse.msisdn.length ; i++)
			{
				unsigned char ch  = gResponse.u.authResponse.msisdn.value[i];
				printf("%d - > [%d] [%d] ",ch ,(ch >> 4) & 0xF,(ch & 0xF));
				temp[pos + 1] = (ch & 0xF) + 48;
				ch = ch >> 4;
				temp[pos] =  (ch & 0xF) + 48;
				pos= pos + 2;
			}*/
			imsi2ascii( gResponse.u.authResponse.msisdn.value, gResponse.u.authResponse.msisdn.length, msisdnreceived );
		    }
		    if (gResponse.u.authResponse.subscriberStatus.subscriber_status_present == 1 )
		    {
		    		subscriberStatus = gResponse.u.authResponse.subscriberStatus.subscriberStatus;
		    }
		vlrSubscriptionInfoPresent = gResponse.u.authResponse.vlrCamelSubscriptionInfo.vlrCamelSubscriptionInfoPresent;
//                    printf("\nmsisdn received : %s , length : %d\n",gResponse.u.authResponse.msisdn.value, gResponse.u.authResponse.msisdn.length);

		    snprintf( error_msg, ULCM_L_ERROR_LENGTH,
			      "Callback response: "
			      "  callbackCookie: %lu"
			      "  error code = 0x%x "
			      "  triplets count = %d "
			      "  quintuplet count = %d "
			      "  service String = %s" 
			      "  msisdn received = %s \n",
			      pendReqAuth[index].callbackCookie, 
			      gResponse.u.authResponse.errorCode, 
			      tempTripletCount, 
			      tempQuintupletCount, 
			      servStr ,
			      msisdnreceived);

		    logInfoAuth( error_msg );

		    /* invoke the callback */
		    switch ( pendReqAuth[index].requestType )
		    {
			case ulcm_RequestAuthorizationInfo_chosen:
			    {
				( pendReqAuth[index].pAuthorizationCallback ) ( pendReqAuth[index].callbackCookie, gResponse.u.authResponse.errorCode, servStr,msisdnreceived,subscriberStatus,vlrSubscriptionInfoPresent );
				break;
			    }
			case ulcm_RequestSIMTriplets_chosen:
			    {
				unsigned int    errorCode = gResponse.u.authResponse.errorCode;
				if ( tempTripletCount == 0 )
				{
				    if ( errorCode == 0 )
					errorCode = ULCM_L_NO_TRIPLET_AVAILABLE;
				}
				( pendReqAuth[index].pCallback ) ( pendReqAuth[index].callbackCookie, errorCode, pTripletStart, tempTripletCount, pQuintupletStart, tempQuintupletCount, servStr );
				break;
			    }
			case ulcm_RequestAKAQuintuplets_chosen:
			    {
				unsigned int    errorCode = gResponse.u.authResponse.errorCode;
				if ( tempQuintupletCount == 0 )
				{
				    if ( errorCode == 0 )
				    {
					errorCode = ULCM_L_NO_QUINTET_AVAILABLE;
					strcpy(servStr, "");
				    }
				}

				( pendReqAuth[index].pAKACallback ) ( pendReqAuth[index].callbackCookie, errorCode, pQuintupletStart, tempQuintupletCount, pTripletStart, tempTripletCount, servStr );
				break;
			    }
			 case ulcm_RequestSIMTripletsEx_chosen:
			 {
			    printf ("msisdn.length is %d\n", gResponse.u.authResponse.msisdn.length);
			    imsi2ascii(gResponse.u.authResponse.msisdn.value,
				       gResponse.u.authResponse.msisdn.length,
				       msisdnStr);

			    (pendReqAuth[index].pExCallback)(pendReqAuth[index].callbackCookie,
					       gResponse.u.authResponse.errorCode,
					       pTripletStart,
					       tempTripletCount,
					       servStr,
					       &msisdnStr[0]);
			 break;
			 }
			 case ulcm_RequestAuthorizationInfoEx_chosen:
			 {
			    printf ("msisdn.length is %d\n", gResponse.u.authResponse.msisdn.length);
			    imsi2ascii(gResponse.u.authResponse.msisdn.value,
				       gResponse.u.authResponse.msisdn.length,
				       msisdnStr);

			    (pendReqAuth[index].pAuthorizationExCallback)(pendReqAuth[index].callbackCookie,
					       gResponse.u.authResponse.errorCode,
					       servStr,
					       &msisdnStr[0]);
			 break;
			 }
			 default:
			    snprintf( error_msg, ULCM_L_ERROR_LENGTH, " Invalid response choice received from the Gateway. Response Choice: %d", pendReqAuth[index].requestType ) ;
			    logErrorAuth( error_msg );
		    printf(error_msg);
			 break;
		    }		/* end switch (.requestType) */

		    clrEntryFromHashtableAuth( index, gResponse.u.authResponse.requestId );
		    if ( !hash_remove_entry( &hash_infoAuth, gResponse.u.authResponse.requestId ) )
		    {
			/* Cannot find entry for this RequestId */
			snprintf( error_msg, ULCM_L_ERROR_LENGTH, "respHandler() : "
				  " Cannot remove request #%d from the Hash table", gResponse.u.authResponse.requestId );
			logErrorAuth( error_msg );
		    printf(error_msg);
			continue;	/*Cannot clear the hash table entry index is 0 !!! */
		    }
		}		/* else */
		break;
	    }			/* end case ulcm_authResponse_chosen */
	default:
	    snprintf( error_msg, ULCM_L_ERROR_LENGTH, " Invalid response choice received from the Gateway. Response Choice: %d", gResponse.choice  );
	    logErrorAuth( error_msg );
		    printf(error_msg);
	    continue;
	    break;
	}			/* end switch (gResponse.choice) */
    }				/* while (1) */

    wlanterm( &worldAuth1 );
    return ( void * ) &error;
}


/**************************************************************************

FUNCTION
	int ulcm_mg_t_RequestAKAQuintuplets( unsigned int nModHandle,
		 const char *pIMSI,
		 const ulcm_mg_re_synchronisation_t * pRe_synchronisation,
		 unsigned int iQuintupletCount,
		 unsigned int *pAKARequest, 
		 ulcm_mg_t_postAKAResults * pFnCallback, 
		 unsigned int iCallbackCookie )

DESCRIPTION
	Request quintuplets for the given IMSI.

INPUTS
	Arg:	
	    nModHandle      - Module Handle
	    pIMSI           - pointer to IMSI (in ASCII)
	    pRe_synchronisation - pointer to resynchronisation.
	    iQuintupletCount   - Number of quintuplet requested 
	    pAKARequest     - Handle that identifies the request 
	                      (Used while canceling the request)
	    pFnCallback     - Callback function that is invoked when 
	                      the response arrives from the gateway.
            iCallbackCookie - Passed as a parameter to the callback 
	                      function (for the client application to
			      correlate the request with the response)


OUTPUTS
	Return: 
	      ULCM_L_INVALID_MOD_HANDLE
	      ULCM_L_INVALID_ARG
	      ULCM_L_TCP_INIT_FAILURE
	      ULCM_L_MAX_REQ_EXCEDED
	      ULCM_L_ASN_ENCODING_ERROR
	      ULCM_L_TCP_SEND_FAILURE
	      ULCM_L_REQ_SIM_TRIPLET_SUCCESS

      	Arg:	
	   pSIMRequest        -  


FEND
**************************************************************************/


int
ulcm_mg_t_RequestAKAQuintuplets( unsigned int nModHandle,
				 const char *pIMSI,
				 const ulcm_mg_re_synchronisation_t * pRe_synchronisation,
				 unsigned int iQuintupletCount,
				 unsigned int *pAKARequest, ulcm_mg_t_postAKAResults * pFnCallback, 
				 unsigned long iCallbackCookie, int driverID )
{
    ulcm_Request    gRequest;
    int             freeSpot = 0;
    char            error_msg[ULCM_L_ERROR_LENGTH];
    unsigned long   ret_val = 0;
    int             indx = 0;
    int             ret = 0;

    /* test if all arguments look valid */
    if ( checkModuleHandleAuth( nModHandle ) == -1 )
	return ULCM_L_INVALID_MOD_HANDLE;
    if ( ( pIMSI == NULL ) || ( pFnCallback == NULL ) || ( pAKARequest == NULL ) || ( pRe_synchronisation == NULL ) )
	return ULCM_L_INVALID_ARG;

    *pAKARequest = getRequestIdAuth(  );

    /* Check EndPointAuths until we find one for which TCP and thread are working. */
    // for ( indx = 0; indx < getSctpEndPointAuthCountAuth(  ); indx++ )
    // {
	// if ( ( ret_val = initSctpAuth( getActiveSctpEndPointAuth(  ) ) ) != ULCM_L_SUCCESS )
	    // incrementActiveSctpEndPointAuth(  );
	// else
	    // break;		/* we have an active connection, we don't need to continue  */
    // }
	
	ret_val = initSctpAuth( driverID );
	
    if ( ret_val != ULCM_L_SUCCESS )
	return ULCM_L_TCP_INIT_FAILURE;

    /*create a requestID and associate this with the iCallbackCookie */
    freeSpot = getFreeHashtableSpotAuth(  );
    if ( freeSpot == -1 )
    {
	snprintf( error_msg, ULCM_L_ERROR_LENGTH, "ulcm_mg_t_RequestAKAQuintuplets() " "Maximum number of concurrent Requests Already Reached " );
	logErrorAuth( error_msg );
	return ULCM_L_MAX_REQ_EXCEDED;
    }
    pendReqAuth[freeSpot].callbackCookie = iCallbackCookie;
    pendReqAuth[freeSpot].reqId = *pAKARequest;
    pendReqAuth[freeSpot].pAKACallback = pFnCallback;
    pendReqAuth[freeSpot].requestType = ulcm_RequestAKAQuintuplets_chosen;
    hash_add_entry( &hash_infoAuth, *pAKARequest, freeSpot );

    /*Pack the data into the request Structure and call the encode/decode API */
    memset( &gRequest, 0, sizeof( gRequest ) );
    gRequest.choice = ulcm_authRequest_chosen;
    gRequest.u.authRequest.requestId = *pAKARequest;
    gRequest.u.authRequest.imsi.length = char2BCD( pIMSI, (unsigned char *) &gRequest.u.authRequest.imsi.value[0] );
    gRequest.u.authRequest.numberOfRequestedVectors = iQuintupletCount;
    gRequest.u.authRequest.re_synchronisationInfo.rand.length =
	char2BCD( pRe_synchronisation->RAND, &gRequest.u.authRequest.re_synchronisationInfo.rand.value[0] );
    gRequest.u.authRequest.re_synchronisationInfo.auts.length =
	char2BCD( pRe_synchronisation->AUTS, &gRequest.u.authRequest.re_synchronisationInfo.auts.value[0] );

    ret = sendAuthRequest( freeSpot, gRequest, driverID);

    if( ret == ULCM_L_REQ_SIM_TRIPLET_SUCCESS )
    {
	return ULCM_L_REQ_AKA_QUINTUPLET_SUCCESS;
    }
    else
    {
	return ret;
    }

}


/**************************************************************************

FUNCTION

	int
	ulcm_mg_t_CancelAKARequest( unsigned int nModHandle, unsigned int hAKARequest )

DESCRIPTION
	Cancel the request (using the hSIMRequest handle)

INPUTS
	Arg:	
	    nModHandle      - Module Handle
	    hAKARequest     - Handler that identifies the request 

OUTPUTS
	Return: 
	      MOD_HANDLE_NOT_INIT   -   Module Uninitialised
	      ULCM_INVALID_MOD_HANDLE    -   Module Handle Invalid
	      REQUEST_NOT_FOUND     -   Request Succeeded
	      SEND_FAILURE          -   Not able to send the 
		                        message to the gateway.
      	Arg:	
	   
FEND
**************************************************************************/

int
ulcm_mg_t_CancelAKARequest( unsigned int nModHandle, unsigned int hAKARequest, int driverID )
{
    int iRet = cancelAuthRequest( nModHandle, hAKARequest, driverID);

    if( iRet == ULCM_L_CANCEL_SIM_REQ_SUCCESS )
    {
	return ULCM_L_CANCEL_AKA_REQ_SUCCESS;
    }
    else
    {
	return iRet;
    }
}
