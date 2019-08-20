#ifndef dataStruct_h
#define dataStruct_h
/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source   authDataStruct.c

@ClearCase-version: $Revision:/main/sw9/2 $ 

@date     $Date:22-Sep-2008 17:50:41 $

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
*        GSM Map files          *
********************************/
#include <gmap.h>
#include <MAPgateway.h>
#include <SMSgateway.h>
#include <wlan_custom.h>
#include <authGWdbg.h>
#include <regex.h>


typedef enum
{
    waitingForOpenRspImsi,
    waitingForInvokeRspImsi,
    waitingForCloseImsi,
    waitingForOpenRspTriplets,
    waitingForInvokeRspTriplets,
    waitingForCloseTriplets,
    waitingForOpenRspRestoreData,
    waitingForInvokeRspRestoreData,
    waitingForCloseRestoreData,
	waitingForOpenRspUpdateGprs,
	waitingForInvokeRspUpdateGprs,
	waitingForCloseUpdateGprs,
    canceled
}
MyState;


    /** declare the variables used to form the SCCP address */
typedef struct sccpAddress_s
{
    NatureOfAddress REMOTE_NAI;
    NumberingPlan   REMOTE_NP;
    unsigned char   REMOTE_GTI;
    unsigned char   REMOTE_TT;
    RoutingIndicator REMOTE_RI;
    U32             REMOTE_PC;
    U8              REMOTE_SSN;

#define MAX_STRING_SIZE 128
#define MAX_BS_CODES 6
    U8              bsCodes[MAX_BS_CODES];
    char            bsString[MAX_BS_CODES][MAX_STRING_SIZE];
    U8              bs_ss_code[MAX_BS_CODES];
//    BOOL            bserviceProvisioned[MAX_BS_CODES];
    U8              bsCodesCount;
#define MAX_TS_CODES 6
    U8              tsCodes[MAX_TS_CODES];
    char            tsString[MAX_BS_CODES][MAX_STRING_SIZE];
    U8              ts_ss_code[MAX_TS_CODES];
//    BOOL            tserviceProvisioned[MAX_TS_CODES];
    U8              tsCodesCount;
#define MAX_SS_CODES 6
    U8              ssCodes[MAX_SS_CODES];
    char            ssString[MAX_SS_CODES][MAX_STRING_SIZE];
    U8              ssCodesCount;
#define MAX_ODB_CODES 6
    U32             odbCodes[MAX_ODB_CODES];
    char            odbString[MAX_ODB_CODES][MAX_STRING_SIZE];
    U8              odbCodesCount;

    char            old_digits[15];
    char            new_digits[15];
#define MAX_NUM_SCCP_ADDR 20
    char            msisdn[15];
#define MAX_APN_NAMES 7
	regex_t 		apnRegex[MAX_APN_NAMES];
	char			apnName[MAX_APN_NAMES][MAX_STRING_SIZE]; // Keeping this just to use for logging purpose
	U8				apnCount;
}
sccpAddress_t;

typedef struct pendingReq_s
{
    /* received from the socket */
    ulcm_Request    request;

    /* used internally */
    long            timestamp;
    int             dialogId;
    int             invokeId[2];
    int             invokeRetry[2];
    MyState         status;
    int             confIndex;
    BOOL            bserviceProvisioned[MAX_BS_CODES];
    BOOL            tserviceProvisioned[MAX_TS_CODES];
    ObjectID        applicationContext;

    /* sent on the socket */
    ulcm_Response   response;
    int             requestId;
}
pendingReq_t;
#define MAX_FD 3
#define LISTEN_FD  0
#define MOM_FD     1
#define SOCKET_FD  2

typedef struct gmaplib_s
{
#define FENCE 0xc4c4c4c4
    U32             bfence;

    int             ALIAS_NAME_INDEX;	/* returned from SYSAttach()   */
    char            PROCESS_NAME[MAXcptNAME + 1];

    char            PRIPC[MAXipcBUFFERsize];	/* receive IPC buffer         */

    int             debug_flag;	/* foundation tool debug print */
    BOOL            async_req;	/* do we have an async read request? */
    AsyncHeader_t   ash;	/* Our async (read) request          */

    struct pollfd   pfd[MAX_FD];
    int             psock;	/* parent socket id     */
    int             sock;	/* accepted socket id   */
#define MAX_HOST_NAME 128
    char            hostname[MAX_HOST_NAME];	/* local hostname */
    int             port;	/* port number */
    int             APPID;	/* returned from gmapInitialize() */
    int             invokeTimeout;	/* in seconds */
    int             nbInvokeRetry;

    MAP_Init        GMAPINIT;	/* gmap initialization structure */

    dbg_t           dbg;	/* debug library work area      */

    /* TODO: we should include the XTCAP_MAX_DIALOG_CNT from atcap.h or ctcap.h */
#define CTCAP_MAX_DIALOG_CNT    32000
#define MAX_MAX_REQUESTS CTCAP_MAX_DIALOG_CNT
    int             max_requests;

    NatureOfAddress LOCAL_NAI;
    NumberingPlan   LOCAL_NP;
    unsigned char   LOCAL_GTI;
    unsigned char   LOCAL_TT;
    char            LOCAL_MSISDN[MAX_MSISDN_LEN];
    RoutingIndicator LOCAL_RI;
    U32             LOCAL_PC;
    U8              LOCAL_SSN;


    sccpAddress_t   sccpAddrArray[MAX_NUM_SCCP_ADDR];
    U8              sccpAddrCount;
    U8              ApplicationContext;	/* only versions 2 and 3 are supported */

    U32             efence;

#define MAX_LENGTH 256

}
gmaplib_t;


/* FUNCTION dump_pendingReq() - dumps a pendingReq_t */
/*************************************************************************

FUNCTION
        char* dump_pendingReq()

DESCRIPTION
        Creates a string displaying the content of a pendingReq_t

INPUTS
        Arg:    The message to log

OUTPUTS
        Return: None

FEND
*************************************************************************/
char           *
dumpRequest( ulcm_Request * rq, char *dump, size_t size )
{

    switch ( rq->choice )
    {
    case ulcm_initRequest_chosen:
	snprintf( dump, size, "REQ:initRequest - version:%d\n", rq->u.initRequest.version );
	break;
    case ulcm_authRequest_chosen:
	snprintf( dump, size,
		  "REQ:authRequest - requestId:%d, imsi:%s, #:%d\n",
		  rq->u.authRequest.requestId, rq->u.authRequest.imsi.value, rq->u.authRequest.numberOfRequestedVectors );
	break;

    case ulcm_cancelAuthRequest_chosen:
	snprintf( dump, size, "REQ:cancelAuthRequest - requestId:%d\n", rq->u.cancelAuthRequest );
	break;
    }
    return dump;
}

/*************************************************************************

FUNCTION
        char*	 dumpObjectID( ObjectID * p, char *dump, size_t size )

DESCRIPTION
        Dump object ID.

INPUTS
        Arg:   p - pointer to oject ID.
	       dump - pointer to dump string
	       size - size

OUTPUTS
        Return: pointer to dump string

FEND
*************************************************************************/

char           *
dumpObjectID( ObjectID * p, char *dump, size_t size )
{

    if ( p->count > 10 )
	snprintf( dump, size, " invalid ObjectID" );
    else
    {
	int             i;

	for ( i = 0; i < p->count; ++i )
	    snprintf( dump, size, " %d", p->value[i] );
    }
    return dump;
}

/*************************************************************************

FUNCTION
        char* dumpResponse( ulcm_Response * rsp, char *dump, size_t size )

DESCRIPTION
        Dump response

INPUTS
        Arg:   	rsp - pointer to response
		dump - pointer to dump buffer
	       	size - size

OUTPUTS
        Return: pointer to dump string

FEND
*************************************************************************/

char           *
dumpResponse( ulcm_Response * rsp, char *dump, size_t size )
{
    char            tr[80];
    int             i, count;

    switch ( rsp->choice )
    {
    case ulcm_initResponse_chosen:
	snprintf( dump, size,
		  "RES:initResponse - maxReq:%d, versOK: %derr:%d\n",
		  rsp->u.initResponse.maxRequest, rsp->u.initResponse.versionSupported, rsp->u.initResponse.errorCode );
	break;
    case ulcm_authResponse_chosen:
	if ( rsp->u.authResponse.bit_mask == ulcm_authenticationSetList_present )
	{
	    snprintf( dump, size, "RES:authResponse - requestId:%d, err:%d \n", rsp->u.authResponse.requestId, rsp->u.authResponse.errorCode );
	    count = rsp->u.authResponse.authenticationSetList.u.tripletList.count;
	    for ( i = 0; i < count; i++ )
	    {
		snprintf( tr, sizeof( tr ),
			  "  Triplet[%d]: {%s},{%s},{%s}\n",
			  count,
			  rsp->u.authResponse.authenticationSetList.u.tripletList.value[i].rand.value,
			  rsp->u.authResponse.authenticationSetList.u.tripletList.value[i].sres.value,
			  rsp->u.authResponse.authenticationSetList.u.tripletList.value[i].kc.value );
	    }
	    strncat( dump, tr, strlen( tr ) );
	}
	else
	{
	    snprintf( dump, size, "RES:authResponse - requestId:%d, err:%d \n", rsp->u.authResponse.requestId, rsp->u.authResponse.errorCode );
	}
	snprintf( dump, size, "RES:authResponse - serviceFound:%d \n", rsp->u.authResponse.serviceFound );
	snprintf( dump, size, "RES:authResponse - serviceList:%s \n", rsp->u.authResponse.serviceList );

	break;


    case ulcm_errorResponse_chosen:
	snprintf( dump, size, "RES:errorResponse - requestId:%d\n", rsp->u.errorResponse.errorCode );
	break;
    }
    return dump;
}

/*************************************************************************

FUNCTION
	void dump_pendingReq( pendingReq_t * prq, FILE * dbgout )
	
DESCRIPTION
        Dump  pending request.

INPUTS
        Arg:  	prq - pointer to pending request 
		dbgout - FILE pointer to dump output

OUTPUTS
        Return: None

FEND
*************************************************************************/

void
dump_pendingReq( pendingReq_t * prq, FILE * dbgout )
{
    char            dump[160];
    char            result[8000] = "\n====================================== \n";
    char            req[80];
    char            resp[480];
    char            AC[80];
    char           *timestamp;

    dumpRequest( &prq->request, req, sizeof( req ) );
    dumpResponse( &prq->response, resp, sizeof( resp ) );
    dumpObjectID( &prq->applicationContext, AC, sizeof( AC ) );

    if ( prq->dialogId != 0 )
    {				/* we dump only valid (non-empty) entries */
	timestamp = ctime( &prq->timestamp );
	snprintf( dump, sizeof( dump ),
		  "%sdlgId:%d, invkId:[%d][%d], invokeRetry:[%d][%d], status: %d, index in Conf file: %d, requestId:%d\n",
		  timestamp,
		  prq->dialogId, prq->invokeId[0], prq->invokeId[1], prq->invokeRetry[0], prq->invokeRetry[1], prq->status, prq->confIndex, prq->requestId );

	strncat( result, req, strlen( req ) );
	strncat( result, dump, strlen( dump ) );
	strncat( result, AC, strlen( AC ) );
	strncat( result, resp, strlen( resp ) );

	fprintf( dbgout, result );
    }
}


/*************************************************************************

FUNCTION
	void dump_sccp_addresses( gmaplib_t * Pglobal, FILE * dbgout )

	
DESCRIPTION
        Dump sccp address. 

INPUTS
        Arg:  	 Pglobal - pointer to global data
		dbgout - FILE pointer to dump output

OUTPUTS
        Return: None

FEND
*************************************************************************/

void
dump_sccp_addresses( gmaplib_t * Pglobal, FILE * dbgout )
{

    int             i = 0, j = 0;
    for ( i = 0; i < MAX_NUM_SCCP_ADDR; i++ )
    {
	fprintf( dbgout, "Array [%d] remote NAI=0x%x, remote NP=0x%x, remote GTI=0x%x,"
		 "remote TT=0x%x, remote PC=%d, remote SSN=%d remote RI=%d\n",
		 i, Pglobal->sccpAddrArray[i].REMOTE_NAI, Pglobal->sccpAddrArray[i].REMOTE_NP,
		 Pglobal->sccpAddrArray[i].REMOTE_GTI, Pglobal->sccpAddrArray[i].REMOTE_TT,
		 Pglobal->sccpAddrArray[i].REMOTE_PC, Pglobal->sccpAddrArray[i].REMOTE_SSN, Pglobal->sccpAddrArray[i].REMOTE_RI );

	fprintf( dbgout, "bsCodeCount=%d\n", Pglobal->sccpAddrArray[i].bsCodesCount );
	for ( j = 0; j < MAX_BS_CODES; j++ )
	{
	    fprintf( dbgout, "bsCode[%d]=%d \t", j, Pglobal->sccpAddrArray[i].bsCodes[j] );
	    fprintf( dbgout, "bsString[%d]=%s \n", j, Pglobal->sccpAddrArray[i].bsString[j] );
	}
	fprintf( dbgout, "tsCodeCount=%d\n", Pglobal->sccpAddrArray[i].tsCodesCount );
	for ( j = 0; j < MAX_TS_CODES; j++ )
	{
	    fprintf( dbgout, "tsCode[%d]=%d \t", j, Pglobal->sccpAddrArray[i].tsCodes[j] );
	    fprintf( dbgout, "tsString[%d]=%s \n", j, Pglobal->sccpAddrArray[i].tsString[j] );
	}
	fprintf( dbgout, "old_digits = %s\n", Pglobal->sccpAddrArray[i].old_digits );
	fprintf( dbgout, "new_digits = %s\n", Pglobal->sccpAddrArray[i].new_digits );
    }

}

/*************************************************************************

FUNCTION
	void dump_GB( gmaplib_t * Pglobal, FILE * dbgout )

DESCRIPTION
        Dump global data.

INPUTS
        Arg:	Pglobal - pointer to global data	 
		dbgout - File pointer to dump global data.

OUTPUTS
        Return: None

FEND
*************************************************************************/

void
dump_GB( gmaplib_t * Pglobal, FILE * dbgout )
{
    fprintf( dbgout, "bfence=0x%x, efence=0x%x\n", Pglobal->bfence, Pglobal->efence );
    fprintf( dbgout, "debug_flag = %d\n", Pglobal->debug_flag );
    fprintf( dbgout, "hostname %s, port:%d, "
	     "pollfd[0] = %d, pollfd[1] = %d, psock=%d, sock=%d\n",
	     Pglobal->hostname, Pglobal->port, Pglobal->pfd[0].fd, Pglobal->pfd[1].fd, Pglobal->psock, Pglobal->sock );
    fprintf( dbgout, "ALIAS_NAME_INDEX=%d ", Pglobal->ALIAS_NAME_INDEX );
    fprintf( dbgout, "invokeTimeout=%d, nbInvokeRetry=%d, max_requests=%d\n", Pglobal->invokeTimeout, Pglobal->nbInvokeRetry, Pglobal->max_requests );
    fprintf( dbgout, "application context version=%d\n", Pglobal->ApplicationContext );
    fprintf( dbgout, "trace_enabled=%d, trace_freeze=%d, trace_seq_nbr=%d\n",
	     Pglobal->dbg.trace_enabled, Pglobal->dbg.trace_freeze, Pglobal->dbg.trace_seq_nbr );
    fprintf( dbgout, "traceFilename=%s, newTraceFile=%d, dbgMask=0x%x\n", Pglobal->dbg.traceFilename, Pglobal->dbg.newTraceFile, Pglobal->dbg.dbgMask );
    //fprintf( dbgout, "asn1out=%p, trace_file=0x%p\n", asn1out, Pglobal->dbg.trace_file );

    fprintf( dbgout, "local NAI=0x%x, local NP=0x%x, local GTI=0x%x,"
	     "local TT=0x%x, local PC=%d, local SSN=%d, local MSISDN=%s, local RI=%d\n",
	     Pglobal->LOCAL_NAI, Pglobal->LOCAL_NP,
	     Pglobal->LOCAL_GTI, Pglobal->LOCAL_TT, Pglobal->LOCAL_PC, Pglobal->LOCAL_SSN, Pglobal->LOCAL_MSISDN, Pglobal->LOCAL_RI );
    dump_sccp_addresses( Pglobal, dbgout );
    fprintf( dbgout, "dbg_buff=%s\n", Pglobal->dbg.dbg_buff );

}

#endif
