/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source   gmapHLR.c

@ClearCase-version: $Revision:/main/sw9/10 $ 

@date     $Date:16-Feb-2009 15:16:11 $

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
#ifdef LINUX
#include <poll.h>
#endif

/********************************
*      SIGNALWARE INCLUDES      *
********************************/
#include <tapsc.h>		/* contains struct. definitions for tap */
#include <tcap.h>		/* contains struct. definitions for tcap */

/********************************
*        GSM Map files          *
********************************/
#include <gmap.h>

#define MOM_FD     0

/*
 * Define:  	MYNAME
 *  Purpose:  	My process' logical name;  
 *     Note:    it does not exist before 'FtRegisterEx()
 */
#define MYNAME pSHM->cpt[OwnClientTableEntry].ClientLogicalName

int             ALIAS_NAME_INDEX = 0;	/* returned from SYSAttach() */
char            PROCESS_NAME[MAXcptNAME + 1] = "HLR";

U32             LOCAL_PC = 1000;

int             unknownIMSIsent = 0;
int             APPID = 0;	/* returned from gmapInitialize() */

MAP_Init        GMAPINIT;	/* gmap initialization structure */

char            SERVER = TRUE;	/* we launch it as a server by default */

time_t          START_TIME;

int             applicationContext = 2;
BOOL            Terminate = FALSE;	/* should we exit? */
BOOL            finished_initialization = FALSE;	/* Are we in the main loop yet? */
BOOL            authRequired = FALSE;	/* Is Authorization required ? */
int             restoreDataInvokeId[65000];
int             insertSubDataCounter[65000];
AsyncHeader_t   ash;	/* Our async (read) request          */
BOOL            async_req;	/* do we have an async read request? */
char            PRIPC[MAXipcBUFFERsize];	/* receive IPC buffer         */
struct pollfd   pfd[2];

/*Sample IMSI structure --S */

int encodeIMSI( char *imsi, int len, unsigned char *dest );
int convertToString( char *src, int len, char *dest );

typedef struct rand_s

{
    int             length;
    unsigned char   value[16];
}
rand_t;

typedef struct sres_s
{
    int             length;
    unsigned char   value[4];
}
sres_t;

typedef struct kc_s
{
    int             length;
    unsigned char   value[8];
}
kc_t;

typedef struct xres_s
{
    unsigned short  length;
    unsigned char   value[16];
}
xres_t;

typedef struct ck_s
{
    unsigned short  length;
    unsigned char   value[16];
}
ck_t;

typedef struct ik_s
{
    unsigned short  length;
    unsigned char   value[16];
}
ik_t;

typedef struct autn_s
{
    unsigned short  length;
    unsigned char   value[18];
}
autn_t;

#define MAX_NUMBER_AUTH_VECTORS 5

typedef struct sub_s
{
    int             length;
    int             count;
    unsigned char   IMSI[8];
    rand_t          r[MAX_NUMBER_AUTH_VECTORS];
    sres_t          s[MAX_NUMBER_AUTH_VECTORS];
    kc_t            k[MAX_NUMBER_AUTH_VECTORS];
    xres_t          xres[MAX_NUMBER_AUTH_VECTORS];
    ck_t            ck[MAX_NUMBER_AUTH_VECTORS];
    ik_t            ik[MAX_NUMBER_AUTH_VECTORS];
    autn_t          autn[MAX_NUMBER_AUTH_VECTORS];
    int             BScount;
    int             bs[50];
    int             TScount;
    int             ts[50];
    int             MSISDNlength;
    unsigned char   MSISDNvalue[8];
    int 	    SS;
}
sub_t;

#define MAX_SUBS 50
sub_t           sub[MAX_SUBS];

/* Prototypes */
int             getFromFile( const char *pFile );
int             isComment( const char *pFile );
void            sendInsertSubDataRequests( gblock_t * pgb, int i );
void            sendInsertSubDataReqBS( gblock_t * pgb, int i );
void            sendInsertSubDataReqTS( gblock_t * pgb, int i );
void            sendInsertSubDataReqSS( gblock_t * pgb, int ind );
/*Added a function to specify the odb flag's value in
 insertSubscriberData_Arg_v2 and v3*/
void            sendInsertSubDataReqODB( gblock_t * pgb, int ind );
void            sendRestoreDataResponse( gblock_t * pgb );
void            sendDelimiter( gblock_t * pgb );
void            printValue( int imsiLen, unsigned char *value );

/*************************************************************************

FUNCTION
    int getFromFile( const char *pFile )

DESCRIPTION
	Open and read the configuration file.

INPUTS
        Arg:   	 pFile - pointer to configuration file.

OUTPUTS
        Return:  1 if success
                -1 if error

FEND
*************************************************************************/

int
getFromFile( const char *pFile )
{
    FILE           *inp = NULL;
    char           *token[5];
    char           *tok[5];
    char           *bslist;
    char           *tslist;
    char            buff[80];
    int             k = 0;
    int             subCount = 0;
    
    if ( ( inp = fopen( pFile, "r" ) ) == NULL )
    {
	printf( "Cannot Open HLR file %s \n", pFile );
	return -1;
    }
    for ( ;; )
    {
	if ( fgets( buff, sizeof( buff ), inp ) == NULL )
	    break;
	if ( isComment( buff ) == 0 )
	{
	    int             i = 0, j = 0;
	    token[i++] = strtok( buff, "\t,\n, " );
	    while ( (token[i++] = strtok( NULL, "\t,\n, " ) ));
	    i--;
	    for ( j = 0; j < i; j++ )
	    {
             if(strcmp(token[j],"MSISDN") == 0)
                {
                  ++j;
                  sub[subCount].MSISDNlength =
                       encodeIMSI(token[j],
                                  strlen(token[j]),
                                  &sub[subCount].MSISDNvalue[0]);
                  printf("\n##### For MSISDN ");
                  printValue(sub[subCount].MSISDNlength, sub[subCount].MSISDNvalue);
                }
		else if ( strcmp( token[j], "IMSI" ) == 0 )
		{
		    ++j;
            if ((0 != subCount) && (2 == applicationContext) && (0 == k))
            {
                ( void ) fclose( inp );
                return -1;
            }
		    k = 0;	/* Start of a new set of vectors */
		    subCount++;
		    sub[subCount].length = encodeIMSI( token[j], strlen( token[j] ), &sub[subCount].IMSI[0] );
		    sub[subCount].SS = 0;
		    printf( "\n\n##### For IMSI " );
		    printValue( sub[subCount].length, sub[subCount].IMSI );
		}
		else if ( strcmp( token[j], "RAND" ) == 0 )
		{
		    ++j;
		    printf( "##### RAND %s \n", token[j] );
		    sub[subCount].r[k].length = convertToString( token[j], strlen( token[j] ), &sub[subCount].r[k].value[0] );
		}
		else if ( strcmp( token[j], "SRES" ) == 0 )
		{
		    ++j;
		    printf( "##### SRES %s \n", token[j] );
		    sub[subCount].s[k].length = convertToString( token[j], strlen( token[j] ), &sub[subCount].s[k].value[0] );

		}
		else if ( strcmp( token[j], "KC" ) == 0 )
		{
		    ++j;
		    printf( "##### KC %s \n", token[j] );
		    sub[subCount].k[k].length = convertToString( token[j], strlen( token[j] ), &sub[subCount].k[k].value[0] );
		    k++;
		}
		else if ( strcmp( token[j], "XRES" ) == 0 )
		{
		    ++j;
		    printf( "##### XRES %s \n", token[j] );
		    sub[subCount].xres[k].length = convertToString( token[j], strlen( token[j] ), &sub[subCount].xres[k].value[0] );
		}
		else if ( strcmp( token[j], "CK" ) == 0 )
		{
		    ++j;
		    printf( "##### CK %s \n", token[j] );
		    sub[subCount].ck[k].length = convertToString( token[j], strlen( token[j] ), &sub[subCount].ck[k].value[0] );
		}
		else if ( strcmp( token[j], "IK" ) == 0 )
		{
		    ++j;
		    printf( "##### IK %s \n", token[j] );
		    sub[subCount].ik[k].length = convertToString( token[j], strlen( token[j] ), &sub[subCount].ik[k].value[0] );
		}
		else if ( strcmp( token[j], "AUTN" ) == 0 )
		{
		    ++j;
		    printf( "##### AUTN %s \n", token[j] );
		    sub[subCount].autn[k].length = convertToString( token[j], strlen( token[j] ), &sub[subCount].autn[k].value[0] );
		    k++;
		}

		else if ( strcmp( token[j], "BS" ) == 0 )
		{
		    int             bsindex = 0;
		    ++j;
		    authRequired = TRUE;
		    bslist = token[j];
		    printf( "##### BS list is %s\n", bslist );
		    tok[bsindex] = strtok( bslist, ":,," );
/*
		    sub[subCount].bs[bsindex] = atoi( tok[bsindex] );
		    bsindex++;
*/
		    do{
			sub[subCount].bs[bsindex] = atoi( tok[bsindex] );
			bsindex++;
		    } while ( (tok[bsindex] = strtok( NULL, ":,," ) ));
		    sub[subCount].BScount = bsindex;
		    printf( "##### BScount is %d\n", sub[subCount].BScount );
		}
		else if ( strcmp( token[j], "TS" ) == 0 )
		{
		    int             tsindex = 0;
		    ++j;
		    authRequired = TRUE;
		    tslist = token[j];
		    printf( "##### TS list is %s\n", tslist );
		    tok[tsindex] = strtok( tslist, ":,," );
/*
		    sub[subCount].ts[tsindex] = atoi( tok[tsindex] );
		    tsindex++;
*/
		    do{
			sub[subCount].ts[tsindex] = atoi( tok[tsindex] );
			tsindex++;
		    } while ( (tok[tsindex] = strtok( NULL, ":,," ) ));
		    sub[subCount].TScount = tsindex;
		    printf( "##### TScount is %d\n", sub[subCount].TScount );
		}
		else if ( strcmp( token[j], "SS" ) == 0 )
		{
			++j;
			sub[subCount].SS = atoi( token[j] );
			printf( "##### Subscriber Status is: %s \n", (sub[subCount].SS == 0)?"Service Granted":"Service barred" );
				
		}


	    }			/* for */
            sub[subCount].count = k;
	}
	else			/* It is a comment */
	    continue;
    }

    sub[subCount].count = k;
    ( void ) fclose( inp );
    if ((1 == subCount) && (2 == applicationContext) && (0 == k))
    {
        return -1;
    }
   
    
/* TODO: find the bug (seg fault) in this commented section 
  {
    int i = 0;
    int j = 0;
    for (subCount = 1; subCount<3; subCount++)
    {
      if (sub[subCount].IMSI != 0)
      {
      for(i = 0; i < sub[subCount].count; i++)
      {
	printf("IMSI is %s length is %d count is %d \n",sub[subCount].IMSI,sub[subCount].length,sub[subCount].count);
	printf("RAND  length is %d \n",sub[subCount].r[i].length);
	printf("\nRand  ");
	for(j = 0; j < sub[subCount].r[i].length;j++)
	  printf("%x ",sub[subCount].r[i].value[j]);
	printf ("\n");
	printf("SRES length is %d \n",sub[subCount].s[i].length);
	printf("\nSres  ");
	for(j = 0; j < sub[subCount].s[i].length;j++)
	  printf("%x ",sub[subCount].s[i].value[j]);
	printf ("\n");

	printf("KC length is %d \n",sub[subCount].k[i].length);

	printf("\nKc  ");
	for(j = 0; j < sub[subCount].k[i].length;j++)
	  printf("%x ",sub[subCount].k[i].value[j]);
	printf ("\n");
      } 
      }
    } 
  }
*/
    return 1;


}


/*************************************************************************

FUNCTION
    int encodeIMSI( char *imsi, int len, unsigned char *dest )

DESCRIPTION
    encode IMSI

INPUTS
        Arg:   	imsi - pointer to imsi.
		len - length
		dest - pointer to destination string

OUTPUTS
        Return:  index in destination string

FEND
*************************************************************************/

int
encodeIMSI( char *imsi, int len, unsigned char *dest )
{
    int             i = 0;
    int             bFlag = 0;
    int             k = 0;
    if ( ( len % 2 ) != 0 )
    {
	len = len - 1;
	bFlag = 1;
    }
    for ( i = 0; i < len; i++ )
    {
	dest[k] = ( *( imsi + i ) & 0x0f );
	dest[k] |= ( *( imsi + ( ++i ) ) & 0x0f ) << 4;
	k++;
    }
    if ( bFlag == 1 )
    {
	dest[k] = ( *( imsi + i ) & 0x0f );
	dest[k] |= 0xf0;
	k++;
    }
    return k;
}

/*************************************************************************

FUNCTION
    int convertToString( char *src, int len, char *dest )

DESCRIPTION
	Convert to string

INPUTS
        Arg:   	imsi - pointer to imsi.
		len - length
		dest - pointer to destination string

OUTPUTS
        Return:  count

FEND
*************************************************************************/

int
convertToString( char *src, int len, char *dest )
{
    char            arr[3];
    unsigned int    n;
    int             i = 0;
    int             cnt = 0;
    for ( i = 0; i < len; i += 2 )
    {
	arr[0] = src[i];
	arr[1] = src[i + 1];
	arr[2] = '\0';
	sscanf( arr, "%x", &n );
	/* printf("assigning ... %x\n",n);  */
	dest[cnt++] = n;
    }
    return cnt;
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
    return 1;
}

/* FUNCTION terminate() - Terminate application                        */
/*************************************************************************

FUNCTION
        void    terminate(void)

DESCRIPTION
        This function is called to terminate the application.  Note
        that the gMAPTerminate() function is called so that the
        shared memory allocated by the GMAP provider will be
        released.

INPUTS
        Arg:    None

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
terminate( int exitcode )
{
    time_t          duration = time( 0 ) - START_TIME;
    printf( "time elapsed: %u seconds\n", (int)duration );
    gMAPTerminate(  );
    printf( "terminated..\n" );
    FtTerminate( NOrestart, exitcode );
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
    if ( GMAPINIT.protocol == ansi7 || GMAPINIT.protocol == chinese7 )
    {
	printf( "   N_STATE IND. PC=%d-%d-%d, SSN=%d, status=%s\n",
		pnstate->NS_affect_pc >> 16, ( pnstate->NS_affect_pc & 0xff00 ) >> 8, pnstate->NS_affect_pc & 0xff, pnstate->NS_affect_ssn, ptext );
    }
    else
    {
	printf( "   N_STATE IND. PC=%d, SSN=%d, status=%s\n", pnstate->NS_affect_pc, pnstate->NS_affect_ssn, ptext );
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
    if ( GMAPINIT.protocol == ansi7 || GMAPINIT.protocol == chinese7 )
    {
	printf( "   PC_STATE IND. PC=%d-%d-%d, status=%s\n", ppcstate->pc_pc >> 16, ( ppcstate->pc_pc & 0xff00 ) >> 8, ppcstate->pc_pc & 0xff, ptext );
    }
    else
    {
	printf( "   PC_STATE IND. PC=%d, status=%s\n", ppcstate->pc_pc, ptext );
    }
}


/*************************************************************************

FUNCTION
	U32	convertPC( char *pcString )

DESCRIPTION
	Convert PC string to integer
INPUTS
        Arg:    pcString - pointer to PC string

OUTPUTS
        Return:	PC

FEND
*************************************************************************/

U32
convertPC( char *pcString )
{
    int             f1, f2, f3;

    if ( !pcString )
    {
	fprintf( stderr, "The pc options must be followed by a valid PC\n" );
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
	    fprintf( stderr, "Invalid PC %s\n", pcString );
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

    if ( GMAPINIT.protocol == ansi7 )
    {
	AscUIS( ALIAS_NAME_INDEX, GMAPINIT.ssn );
    }
    if ( GMAPINIT.protocol == chinese7 )
    {
	CHscUIS( ALIAS_NAME_INDEX, GMAPINIT.ssn );
    }
    else
    {
	CscUIS( ALIAS_NAME_INDEX, GMAPINIT.ssn );
    }
}

/* FUNCTION compareObjectIDs() - Initiate a typical dialog and request       */
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
    return ( memcmp( id1->value, id2->value, id1->count * sizeof( unsigned int ) ) == 0 );
}

//U16             dialogId;
S16             invokeId[65000];

/* FR 904 */
typedef struct vector_context_s
{
    int   dialogId;
    int   nbr_requested;
    int   saved_sub_index;
    int   saved_vector_index; 
    int   cont_vector_index; /* index into cont vector  */
}vector_context_t;

vector_context_t  context[65000]; /* index = dialogId */

#define MAX_QUINTET_PER_MSU	2
#define MAX_AUTH_VECT		5
#define MAX_SEGM_MSG    	MAX_AUTH_VECT 

int cont_vector[MAX_SEGM_MSG]={1,1,1,1,1};


/* FUNCTION abortDialog() - Aborts the dialog                           */
/*************************************************************************

FUNCTION
    void abortDialog(gblock_t* pgb)

DESCRIPTION
        This function aborts the dialog.

INPUTS
        Arg:    pgb - a reference to an externally allocated gblock_t

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
abortDialog( gblock_t * pgb )
{
    pgb->serviceType = GMAP_RSP;
    pgb->serviceMsg = GMAP_OPEN;
    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->parameter.openRes.bit_mask = MAP_OpenRes_applicationContext_present;
    if ( applicationContext == 3 )
    {
    if( TRUE == DebugIndicator )
	printf( "Application context should be 3\n" );
	if ( compareObjectIDs( &pgb->parameter.openArg.applicationContext, &infoRetrievalContext_v2 ) )
	{
	    pgb->parameter.openRes.applicationContext.count = infoRetrievalContext_v3.count;
	    memcpy( &pgb->parameter.openRes.applicationContext.value,
		    &infoRetrievalContext_v3.value, sizeof( pgb->parameter.openRes.applicationContext.value ) );
	}
	else if ( compareObjectIDs( &pgb->parameter.openArg.applicationContext, &networkLocUpContext_v2 ) )
	{
	    pgb->parameter.openRes.applicationContext.count = networkLocUpContext_v3.count;
	    memcpy( &pgb->parameter.openRes.applicationContext.value,
		    &networkLocUpContext_v3.value, sizeof( pgb->parameter.openRes.applicationContext.value ) );
	}
    }
    else if ( applicationContext == 2 )
    {
    if( TRUE == DebugIndicator )
	printf( "Application context should be 2\n" );
	if ( compareObjectIDs( &pgb->parameter.openArg.applicationContext, &infoRetrievalContext_v3 ) )
	{
	    pgb->parameter.openRes.applicationContext.count = infoRetrievalContext_v2.count;
	    memcpy( &pgb->parameter.openRes.applicationContext.value,
		    &infoRetrievalContext_v2.value, sizeof( pgb->parameter.openRes.applicationContext.value ) );
	}
	else if ( compareObjectIDs( &pgb->parameter.openArg.applicationContext, &networkLocUpContext_v3 ) )
	{
	    pgb->parameter.openRes.applicationContext.count = networkLocUpContext_v2.count;
	    memcpy( &pgb->parameter.openRes.applicationContext.value,
		    &networkLocUpContext_v2.value, sizeof( pgb->parameter.openRes.applicationContext.value ) );
	}
/*
	printf( "Size of struct %d\n", sizeof( pgb->parameter.openRes.applicationContext.value ) );
	printf( "value of 8 %d\n", pgb->parameter.openRes.applicationContext.value[7] );
*/
    }
    pgb->parameter.openRes.bit_mask |= MAP_OpenRes_refuseReason_present;
    pgb->parameter.openRes.result = dialogRefused;
    pgb->parameter.openRes.refuseReason = appContextNotSupported;
    if( TRUE == DebugIndicator )
	gMAPPrintGBlock( pgb );

    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 1 );
    }
}

/* FUNCTION sendOpenResponse() - send an open response                  */
/*************************************************************************

FUNCTION
    void sendOpenResponse(gblock_t* pgb, DialogResult result)

DESCRIPTION
        This function is called by the server to respond to a dialog: it
        issues a MAP-OPEN response.

INPUTS
        Arg:    pgb - a reference to an externally allocated gblock_t
                result - indicate whether the dialog is accepted or
                         refused

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
sendOpenResponse( gblock_t * pgb, DialogResult result )
{
    /* we send the open res */
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->serviceType = GMAP_RSP;
    pgb->serviceMsg = GMAP_OPEN;
    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->parameter.openRes.bit_mask = 0;
    pgb->parameter.openRes.result = result;
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 2 );
    }
}


/* FUNCTION SendAuthenticationInfoResponse() - Issue a SEND_AUTHENTICATION_INFO Resp */
/*************************************************************************
FUNCTION					
        void sendAuthenticationInfoResponse(gblock_t* pgb)
									  
DESCRIPTION
        This function is used by the server to respond to a SendAuthenticationInfo Request
        request.

INPUTS
        Arg:    pgb - a reference to an externally allocated gblock_t
                subs   - the index of the selected subscriber

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
sendAuthenticationInfoResponse( gblock_t * pgb, int subs )
{
    int             iCount = 0, countOfVec = 0;
/* FR 904 */
    int             vectors_sent_so_far, nbr_requested;
    int             vectors_this_msu=0;
    int		    startIndex, endIndex;
    U16		    dialogId = pgb->dialogId;

    vectors_sent_so_far = context[dialogId].saved_vector_index+1;
    nbr_requested = context[dialogId].nbr_requested;



    if( TRUE == DebugIndicator )
	printf( "sendAuthenticationResponse() : Processing the request\n" );

    if ( applicationContext == 2 )
    {
	for ( iCount = 0; iCount < 5; iCount++ )
	{
	    if ( sub[subs].r[iCount].length != 0 )
	    {
		countOfVec++;
	    }
	    else
	    {
		break;
	    }
	}

	if( TRUE == DebugIndicator )
	    printf( "Count of Vectors = %d\n", countOfVec );
    }
    else
    {
	countOfVec = nbr_requested; /* set it for now */
    }

    /*gMAPPrintGBlock(pgb); */
    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;

    pgb->serviceType = GMAP_RSP;
    pgb->serviceMsg = SEND_AUTHENTICATION_INFO;
    pgb->invokeId = invokeId[dialogId];

    /* The NumberOfRequestedVectors is of type short and this argument
       (from the request) specifies the number of triplets required. --S */

    if ( countOfVec >= 5 )
	countOfVec = 5;

    if ( applicationContext == 3 )
    {
	/* Use segmentation */
        vectors_this_msu = cont_vector[context[dialogId].cont_vector_index++];
	if (vectors_sent_so_far+vectors_this_msu > sub[subs].count)
	{
	    /* Readjust */
	    vectors_this_msu = sub[subs].count-vectors_sent_so_far;
	}
	if ( sub[subs].xres[0].length != 0 )
	{
	    /* If xres is present we have quintuplets */
	    pgb->parameter.sendAuthenticationInfoRes_v3.bit_mask = SendAuthenticationInfoRes_v3_authenticationSetList_present;
	    pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.choice = AuthenticationSetList_t2_quintupletList_chosen;
	    pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.u.tripletList.count = vectors_this_msu;
    	    if( TRUE == DebugIndicator )
	    {
    	        printf("Inside sendAuthenticationInfoResponse. vectors requested = %d, vectors sent so far = %d, vectors this msu = %d, dialog ID = %d subs=%d\n",  nbr_requested, vectors_sent_so_far,vectors_this_msu, dialogId,subs);
	    }
	}
	else
	{
	    /* we have triplets */
	    pgb->parameter.sendAuthenticationInfoRes_v3.bit_mask = SendAuthenticationInfoRes_v3_authenticationSetList_present;
	    pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.choice = AuthenticationSetList_t2_tripletList_chosen;
	    pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.u.quintupletList.count = vectors_this_msu;
    	    if( TRUE == DebugIndicator )
	    {
    	        printf("Inside sendAuthenticationInfoResponse for triplets. vectors requested = %d, vectors sent so far = %d, vectors this msu = %d, dialog ID = %d subs=%d\n",  nbr_requested, vectors_sent_so_far,vectors_this_msu, dialogId,subs);
	    }
	}
    }
    else if ( applicationContext == 2 )
    {
	pgb->parameter.sendAuthenticationInfoRes_v2.count = countOfVec;
    }
    else
    {
	printf( "Application Context Version %d Not Supported \n", applicationContext );
	exit( 0 );
    }
    if ( applicationContext == 3 )
    {
	/* If xres is present we have quintuplets */
        if (0 != sub[subs].xres[0].length)
        {
            startIndex = vectors_sent_so_far;
            context[dialogId].saved_vector_index+= vectors_this_msu;
            endIndex  = startIndex+vectors_this_msu; 
	    if ( TRUE == DebugIndicator )
	    {
		printf("startIndex = %d, endIndex = %d\n", startIndex, endIndex);
	    }
	    iCount = 0;
            while (startIndex < endIndex)
            {
	        if( TRUE == DebugIndicator )
	        {
	            printf( "Looping for subscriber #%d...." "AuthenticationVector number %d\n", subs, iCount );
	            printf( "length of rand is %d\n", sub[subs].r[startIndex].length );
	            printf( "length of xres is %d\n", sub[subs].xres[startIndex].length );
	            printf( "length of ck is %d\n", sub[subs].ck[startIndex].length );
	            printf( "length of ik is %d\n", sub[subs].ik[startIndex].length );
	            printf( "length of autn is %d\n", sub[subs].autn[startIndex].length );
	        }

	        pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.u.quintupletList.value[iCount].rand.length = sub[subs].r[startIndex].length;
	        memcpy( pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.
		u.quintupletList.value[iCount].rand.value, sub[subs].r[startIndex].value, sub[subs].r[startIndex].length );
	        pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.u.quintupletList.value[iCount].xres.length = sub[subs].xres[startIndex].length;
	        memcpy( pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.
		u.quintupletList.value[iCount].xres.value, sub[subs].xres[startIndex].value, sub[subs].xres[startIndex].length );
	        pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.u.quintupletList.value[iCount].ck.length = sub[subs].ck[startIndex].length;
	        memcpy( pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.
		u.quintupletList.value[iCount].ck.value, sub[subs].ck[startIndex].value, sub[subs].ck[startIndex].length );
	        pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.u.quintupletList.value[iCount].ik.length = sub[subs].ik[startIndex].length;
	        memcpy( pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.
		u.quintupletList.value[iCount].ik.value, sub[subs].ik[startIndex].value, sub[subs].ik[startIndex].length );
	        pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.u.quintupletList.value[iCount].autn.length = sub[subs].autn[startIndex].length;
	        memcpy( pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.
		u.quintupletList.value[iCount].autn.value, sub[subs].autn[startIndex].value, sub[subs].autn[startIndex].length );
                startIndex++;
		iCount++;
	    }
	}
	else
	{
            startIndex = vectors_sent_so_far;
            context[dialogId].saved_vector_index+= vectors_this_msu;
            endIndex  = startIndex+vectors_this_msu; 
	    if ( TRUE == DebugIndicator )
	    {
		printf("startIndex = %d, endIndex = %d\n", startIndex, endIndex);
	    }
	    iCount = 0;
            while (startIndex < endIndex)
	    {
		pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.u.tripletList.value[iCount].rand.length = sub[subs].r[startIndex].length;
		memcpy( pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.
			u.tripletList.value[iCount].rand.value, 
			sub[subs].r[startIndex].value, 
			sub[subs].r[startIndex].length );
		pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.u.tripletList.value[iCount].sres.length = sub[subs].s[startIndex].length;
		memcpy( pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.
			u.tripletList.value[iCount].sres.value, 
			sub[subs].s[startIndex].value, 
			sub[subs].s[startIndex].length );
		pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.u.tripletList.value[iCount].kc.length = sub[subs].k[startIndex].length;
		memcpy( pgb->parameter.sendAuthenticationInfoRes_v3.authenticationSetList.
			u.tripletList.value[iCount].kc.value, 
			sub[subs].k[startIndex].value, 
			sub[subs].k[startIndex].length );
                startIndex++;
		iCount++;
            }

	}
	if (TRUE == DebugIndicator)
	{
            printf( "Processed the request for DID %d, sending the response\n", 
			dialogId );
	}

    }
    else if ( applicationContext == 2 )
    {
	for (iCount = 0; iCount <5; iCount++)
	{
            pgb->parameter.sendAuthenticationInfoRes_v2.value[iCount].rand.length = sub[subs].r[iCount].length;
            memcpy( pgb->parameter.sendAuthenticationInfoRes_v2.value[iCount].rand.value, 
	    sub[subs].r[iCount].value, 
	    sub[subs].r[iCount].length );
            pgb->parameter.sendAuthenticationInfoRes_v2.value[iCount].sres.length = sub[subs].s[iCount].length;
            memcpy( pgb->parameter.sendAuthenticationInfoRes_v2.value[iCount].sres.value, 
	    sub[subs].s[iCount].value, 
	    sub[subs].s[iCount].length );
            pgb->parameter.sendAuthenticationInfoRes_v2.value[iCount].kc.length = sub[subs].k[iCount].length;
            memcpy( pgb->parameter.sendAuthenticationInfoRes_v2.value[iCount].kc.value, 
	    sub[subs].k[iCount].value, 
	    sub[subs].k[iCount].length );
        if( TRUE == DebugIndicator )
        {
	/* CR35768 */
	    int i;
	    printf( "Processed the request, sending the response\n" );
	    printf( "Size of RAND is %d\n", pgb->parameter.sendAuthenticationInfoRes_v2.value[iCount].rand.length );
	    printf( "Value of RAND is : ");
	    for (i=0; i<pgb->parameter.sendAuthenticationInfoRes_v2.value[iCount].rand.length; i++)
	    {
	    printf( "%02x", pgb->parameter.sendAuthenticationInfoRes_v2.value[iCount].rand.value[i] );
	    }
	    printf( "\n");
	/* CR35768 */    
        }
	}
    }
    else
    {
        printf( "Application Context Version %d Not Supported \n", applicationContext );
        exit( 0 );
    }

    if( TRUE == DebugIndicator )
	gMAPPrintGBlock( pgb );
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 3 );
    }
}

/* FUNCTION unknownSubscriber() - Issue a UNKNOWN_SUBSCRIBER Resp */
/*************************************************************************

FUNCTION
    void unknownSubscriber(gblock_t* pgb)

DESCRIPTION
        This function is used by the server to respond to a 
	SendAuthenticationInfo
        request.

INPUTS
        Arg:    pgb - a reference to an externally allocated
	gblock_t

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
unknownSubscriber( gblock_t * pgb )
{
    if( TRUE == DebugIndicator )
    printf( "unknownSubscriber() : Processing the request\n" );
    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;

    pgb->serviceType = GMAP_ERROR;
    pgb->serviceMsg = UNKNOWN_SUBSCRIBER;
    pgb->invokeId = invokeId[pgb->dialogId];

    pgb->parameter.unknownSubscriberParam_t3.bit_mask = UnknownSubscriberParam_t3_unknownSubscriberDiagnostic_present;
    pgb->parameter.unknownSubscriberParam_t3.unknownSubscriberDiagnostic = UnknownSubscriberDiagnostic_t2_imsiUnknown;

    if( TRUE == DebugIndicator )
	gMAPPrintGBlock( pgb );
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 4 );
    }
    unknownIMSIsent = 1;
}



/*************************************************************************

FUNCTION
	char *dumpObjectID( ObjectID * p, char *dump, size_t size )

DESCRIPTION
	Dump object id.
INPUTS
        Arg: p - pointer to object id 
	     dump - pointer to string to dump object id
	     size - size

OUTPUTS
        Return:	pointer to string to dump 

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
	void printValue( int imsiLen, unsigned char *value )

DESCRIPTION
	Print Value
INPUTS
        Arg: imsiLen - print of IMSI
	     value - pointer to value
OUTPUTS
        Return:None

FEND
*************************************************************************/

void
printValue( int imsiLen, unsigned char *value )
{
    int             iCount = 0;
    for ( iCount = 0; iCount < imsiLen; iCount++ )
    {
	printf( "%x", ( value[iCount] & 0x0f ) );
	printf( "%x", ( value[iCount] & 0xf0 ) >> 4 );
    }
    printf( "\n" );
}

/*************************************************************************

FUNCTION
	int verify( int imsiLen, unsigned char *imsiValue )
DESCRIPTION
	Verify IMSI.
INPUTS
        Arg:	imsiLen - Length of IMSI 
		imsiValue - pointer to imsi value

OUTPUTS
        Return:	-1 - error
		>0 - subCount
FEND
*************************************************************************/

int
verify( int imsiLen, unsigned char *imsiValue )
{
    int             iCount = 0;
    int             subCount = 1;
    BOOL            foundIMSI = TRUE;

    if( TRUE == DebugIndicator )
    {
    printf( "IMSI length = %d\n", imsiLen );
    printf( "IMSI value = " );
    for ( iCount = 0; iCount < imsiLen; iCount++ )
    {
	printf( "%x", ( imsiValue[iCount] & 0x0f ) );
	printf( "%x", ( imsiValue[iCount] & 0xf0 ) >> 4 );
    }
    printf( "\n" );
    }

    for ( subCount = 1; subCount < MAX_SUBS; subCount++ )
    {
    if( TRUE == DebugIndicator )
	printf( "Configured IMSI #%d is ", subCount );
	foundIMSI = TRUE;
	for ( iCount = 0; iCount < imsiLen; iCount++ )
	{
    if( TRUE == DebugIndicator )
    {
	    printf( "%x", sub[subCount].IMSI[iCount] & 0x0f );
	    printf( "%x", ( sub[subCount].IMSI[iCount] & 0xf0 ) >> 4 );
    }
	    if ( imsiValue[iCount] != sub[subCount].IMSI[iCount] )
	    {
		foundIMSI = FALSE;
	    }

	}
    if( TRUE == DebugIndicator )
	printf( "\n" );
	if ( foundIMSI )
	{
	    return subCount;
	}
    }
    return -1;

}


/*************************************************************************

FUNCTION
	void verifyAndSend( gblock_t * pgb )
	
DESCRIPTION
	Verify and send gblock.
INPUTS
        Arg:	pgb - pointer to gblock

OUTPUTS
        Return: None
FEND
*************************************************************************/

void
verifyAndSend( gblock_t * pgb )
{
    int             iCount = 0;
    int             le = 0;
    int             subCount = 1;
    unsigned char   tempIMSI = 0;
    BOOL            foundIMSI = TRUE;

    if ( applicationContext == 2 )
    {
	le = pgb->parameter.insertSubscriberDataArg_v2.imsi.length;
    if( TRUE == DebugIndicator )
	printf( "IMSI length is %d\n", le );

	for ( subCount = 1; subCount < MAX_SUBS; subCount++ )
	{
	    for ( iCount = 0; iCount < le; iCount++ )
	    {
		tempIMSI = ( ( sub[subCount].IMSI[iCount] & 0x0f ) << 4 ) | ( ( sub[subCount].IMSI[iCount] & 0xf0 ) >> 4 );

		if ( pgb->parameter.insertSubscriberDataArg_v2.imsi.value[iCount] != tempIMSI )
		{
		    foundIMSI = FALSE;
		}
	    }
	}
	if ( foundIMSI )
	{
	    sendAuthenticationInfoResponse( pgb, subCount );
	    return;
	}
    }
    if ( applicationContext == 3 )
    {
	le = pgb->parameter.insertSubscriberDataArg_v3.imsi.length;
    if( TRUE == DebugIndicator )
	printf( "IMSI length is %d\n", le );

	for ( subCount = 1; subCount < MAX_SUBS; subCount++ )
	{
	    for ( iCount = 0; iCount < le; iCount++ )
	    {
		tempIMSI = ( ( sub[subCount].IMSI[iCount] & 0x0f ) << 4 ) | ( ( sub[subCount].IMSI[iCount] & 0xf0 ) >> 4 );

		if ( pgb->parameter.insertSubscriberDataArg_v3.imsi.value[iCount] != tempIMSI )
		{
		    foundIMSI = FALSE;
		}
	    }
	}
	if ( foundIMSI )
	{
	    sendAuthenticationInfoResponse( pgb, subCount );
	    return;
	}
    }
    else if ( compareObjectIDs( &pgb->parameter.openArg.applicationContext, &networkLocUpContext_v2 ) && applicationContext == 2 )
    {
	le = pgb->parameter.restoreDataArg_v2.imsi.length;
    if( TRUE == DebugIndicator )
	printf( "IMSI length is %d\n", le );

	for ( subCount = 1; subCount < MAX_SUBS; subCount++ )
	{
	    for ( iCount = 0; iCount < le; iCount++ )
	    {
		tempIMSI = ( ( sub[subCount].IMSI[iCount] & 0x0f ) << 4 ) | ( ( sub[subCount].IMSI[iCount] & 0xf0 ) >> 4 );

		if ( pgb->parameter.restoreDataArg_v2.imsi.value[iCount] != tempIMSI )
		{
		    foundIMSI = FALSE;
		}
	    }
	}
	if ( foundIMSI )
	{
	    if ( pgb->serviceMsg == RESTORE_DATA )
		sendInsertSubDataRequests( pgb, subCount );
	    return;
	}
    }
    else if ( compareObjectIDs( &pgb->parameter.openArg.applicationContext, &networkLocUpContext_v3 ) && applicationContext == 3 )
    {
	le = pgb->parameter.restoreDataArg_v3.imsi.length;
    if( TRUE == DebugIndicator )
	printf( "IMSI length is %d\n", le );

	for ( subCount = 1; subCount < MAX_SUBS; subCount++ )
	{
	    for ( iCount = 0; iCount < le; iCount++ )
	    {
		tempIMSI = ( ( sub[subCount].IMSI[iCount] & 0x0f ) << 4 ) | ( ( sub[subCount].IMSI[iCount] & 0xf0 ) >> 4 );
		if ( pgb->parameter.restoreDataArg_v3.imsi.value[iCount] != tempIMSI )
		{
		    foundIMSI = FALSE;
		}
	    }
	}
	if ( foundIMSI )
	{
	    if ( pgb->serviceMsg == RESTORE_DATA )
		sendInsertSubDataRequests( pgb, subCount );
	    return;
	}

    }
    else
    {
	char           *dump=NULL;
	int             size = 100;
	printf( "Application Context Version %d Not Supported \n", applicationContext );
	dumpObjectID( &pgb->parameter.openArg.applicationContext, dump, size );
	printf( "%s\n", dump );
/*
      if (pgb->parameter.openArg.applicationContext.count > 10)
        printf(" invalid ObjectID, count is %d \n", &pgb->parameter.openArg.applicationContext.count);
      else
      {
        printf("Application Context is ");
        for (i=0; i<pgb->parameter.openArg.applicationContext.count; ++i)
            printf(" %d", pgb->parameter.openArg.applicationContext.value[i]);
        printf("\n");
      }
*/
	exit( 0 );
    }

    printf( "Unknown Subscriber......\n" );
    unknownSubscriber( pgb );
    return;

}


int             alternateSend = 2;
/*************************************************************************

FUNCTION
	void sendInsertSubDataRequests( gblock_t * pgb, int i )
	
DESCRIPTION
	Send insert Sub data request.
INPUTS
        Arg:	pgb - pointer to gblock
		i - indication

OUTPUTS
        Return: None
FEND
*************************************************************************/

void
sendInsertSubDataRequests( gblock_t * pgb, int i )
{
/*  
  if(alternateSend++ % 2 == 0)
*/
    {
    	if( sub[i].BScount != 0 )
	{
	    if( TRUE == DebugIndicator )
		printf( "First, send a delimiter after each InsertSubscriberData\n" );
	    sendInsertSubDataReqBS( pgb, i );
	    sendDelimiter( pgb );
	    insertSubDataCounter[pgb->dialogId]++;
	    if( TRUE == DebugIndicator )
		printf( "Message sent: insertSubDataCounter[%d] = %d \n", 
			pgb->dialogId, insertSubDataCounter [pgb->dialogId]);
	}

    	if( sub[i].TScount != 0 )
	{
	    sendInsertSubDataReqTS( pgb, i );
	    sendDelimiter( pgb );
	    insertSubDataCounter[pgb->dialogId]++;
	    if( TRUE == DebugIndicator )
		printf( "Message sent: insertSubDataCounter[%d] = %d \n", 
			pgb->dialogId, insertSubDataCounter [pgb->dialogId]);
	}

	/* sending the ODB data */
	/*This may not be required in the configuration file. for the HLR */
	sendInsertSubDataReqSS( pgb, i );
	sendDelimiter( pgb );
	sendInsertSubDataReqODB( pgb, i );
	sendDelimiter( pgb );
	insertSubDataCounter[pgb->dialogId]++;
    if( TRUE == DebugIndicator )
	printf( "Message sent: insertSubDataCounter[%d] = %d \n", pgb->dialogId, insertSubDataCounter [pgb->dialogId]);


    }
/*  else
    {
      printf("Second, send all the InsertSubscriberData in the same message\n");
      sendInsertSubDataReqBS(pgb, i);
      printf("second send after BS\n");
      sendInsertSubDataReqTS(pgb, i);
      printf("second send after TS\n");
      sendInsertSubDataReqTS(pgb, i);
      sendDelimiter(pgb);
      insertSubDataCounter++;
      printf("Message sent: insertSubDataCounter = %d \n", insertSubDataCounter);

    }
*/
}


/*************************************************************************

FUNCTION
	void sendInsertSubDataReqBS( gblock_t * pgb, int ind )
	
DESCRIPTION
	Send insert Sub data request for bearer service.
INPUTS
        Arg:	pgb - pointer to gblock
		ind - indication

OUTPUTS
        Return: None
FEND
*************************************************************************/

void
sendInsertSubDataReqBS( gblock_t * pgb, int ind )
{

    /*gMAPPrintGBlock(pgb); */
    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;

    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = INSERT_SUBSCRIBER_DATA;
    pgb->invokeId = ++invokeId[pgb->dialogId];
    if ( applicationContext == 3 )
    {
	memcpy( &pgb->parameter.openArg.applicationContext, &subscriberDataMngtContext_v3, sizeof( ObjectID ) );
    }
    else if ( applicationContext == 2 )
    {
	memcpy( &pgb->parameter.openArg.applicationContext, &subscriberDataMngtContext_v2, sizeof( ObjectID ) );
    }
    if ( applicationContext == 3 )
    {
	int             i = 0;
        pgb->parameter.insertSubscriberDataArg_v3.bit_mask |=
             InsertSubscriberDataArg_v3_msisdn_present;
        pgb->parameter.insertSubscriberDataArg_v3.msisdn.length =
             sub[ind].MSISDNlength;
        memcpy(&pgb->parameter.insertSubscriberDataArg_v3.msisdn.value,
                sub[ind].MSISDNvalue,
                pgb->parameter.insertSubscriberDataArg_v3.msisdn.length);

	pgb->parameter.insertSubscriberDataArg_v3.bit_mask |= InsertSubscriberDataArg_v3_bearerServiceList_present;
	/* 
	   printf(" ####################################################\n"
	   "insertSubscriberDataArg_v3 bitmask is %x\n",
	   pgb->parameter.insertSubscriberDataArg_v3.bit_mask);
	   printf(" insertSubscriberDataArg_v2 bitmask is %x\n",
	   pgb->parameter.insertSubscriberDataArg_v2.bit_mask);
	 */

	pgb->parameter.insertSubscriberDataArg_v3.bearerServiceList.count = sub[ind].BScount;
	for ( i = 0; i < sub[ind].BScount; i++ )
	{
	    pgb->parameter.insertSubscriberDataArg_v3.bearerServiceList.value[i].length = 1;
	    pgb->parameter.insertSubscriberDataArg_v3.bearerServiceList.value[i].value[0] = ( unsigned char ) sub[ind].bs[i];
	}
    }
    else if ( applicationContext == 2 )
    {
	int             i = 0;
        pgb->parameter.insertSubscriberDataArg_v2.bit_mask |=
             InsertSubscriberDataArg_v2_msisdn_present;
        pgb->parameter.insertSubscriberDataArg_v2.msisdn.length =
              sub[ind].MSISDNlength;
        memcpy(&pgb->parameter.insertSubscriberDataArg_v2.msisdn.value,
                sub[ind].MSISDNvalue,
                pgb->parameter.insertSubscriberDataArg_v2.msisdn.length);

	pgb->parameter.insertSubscriberDataArg_v2.bit_mask |= InsertSubscriberDataArg_v2_bearerServiceList_present;

	pgb->parameter.insertSubscriberDataArg_v2.bearerServiceList.count = sub[ind].BScount;
	for ( i = 0; i < sub[ind].BScount; i++ )
	{
	    pgb->parameter.insertSubscriberDataArg_v2.bearerServiceList.value[i].length = 1;
	    pgb->parameter.insertSubscriberDataArg_v2.bearerServiceList.value[i].value[0] = ( unsigned char ) sub[ind].bs[i];
	}
    if( TRUE == DebugIndicator )
	printf( "Bearer Service List Count is %d \n", pgb->parameter.insertSubscriberDataArg_v2.bearerServiceList.count );
    }
    else
    {

	printf( "Application Context Version %d Not Supported \n", applicationContext );
	exit( 0 );
    }
    if( TRUE == DebugIndicator )
	gMAPPrintGBlock( pgb );
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 5 );
    }
}



/*************************************************************************

FUNCTION
	void sendInsertSubDataReqODB( gblock_t * pgb, int ind )
	
DESCRIPTION
	Send insert Sub data request for ODB.
INPUTS
        Arg:	pgb - pointer to gblock
		ind - indication

OUTPUTS
        Return: None
FEND
*************************************************************************/

void
sendInsertSubDataReqODB( gblock_t * pgb, int ind )
{

    /*gMAPPrintGBlock(pgb); */
    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;

    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = INSERT_SUBSCRIBER_DATA;
    pgb->invokeId = ++invokeId[pgb->dialogId];
    if ( applicationContext == 3 )
    {
	memcpy( &pgb->parameter.openArg.applicationContext, &subscriberDataMngtContext_v3, sizeof( ObjectID ) );
    }
    else if ( applicationContext == 2 )
    {
	memcpy( &pgb->parameter.openArg.applicationContext, &subscriberDataMngtContext_v2, sizeof( ObjectID ) );
    }
    if ( applicationContext == 3 )
    {
	pgb->parameter.insertSubscriberDataArg_v3.bit_mask |= InsertSubscriberDataArg_v3_odb_Data_present;

/*	printf( " ####################################################\n"
		"insertSubscriberDataArg_v3 bitmask is %x\n", pgb->parameter.insertSubscriberDataArg_v3.bit_mask );
	    printf( " insertSubscriberDataArg_v2 bitmask is %x\n", pgb->parameter.insertSubscriberDataArg_v2.bit_mask );
	}
*/	/* The odb_GeneralData = 0 means all OG Calls are barred */

	pgb->parameter.insertSubscriberDataArg_v3.odb_Data.odb_GeneralData.length = 15;
	pgb->parameter.insertSubscriberDataArg_v3.odb_Data.odb_GeneralData.value[ODB_GeneralData_t2_allOG_CallsBarred_byte] = ODB_GeneralData_t2_allOG_CallsBarred;
    if( TRUE == DebugIndicator )
	printf( "Value of allOG_CallsBarred is 0x%x\n", ODB_GeneralData_t2_allOG_CallsBarred );
    }
    else if ( applicationContext == 2 )
    {
	pgb->parameter.insertSubscriberDataArg_v2.bit_mask |= InsertSubscriberDataArg_v2_odb_Data_present;
	pgb->parameter.insertSubscriberDataArg_v2.odb_Data.odb_GeneralData.length = 6;
	pgb->parameter.insertSubscriberDataArg_v2.odb_Data.odb_GeneralData.value[ODB_GeneralData_allOG_CallsBarred_byte] = ODB_GeneralData_allOG_CallsBarred;
    if( TRUE == DebugIndicator )
	printf( "Value of allOG_CallsBarred is %u\n", ODB_GeneralData_allOG_CallsBarred );
    }
    else
    {

	printf( "Application Context Version %d Not Supported \n", applicationContext );
	exit( 0 );
    }

    if( TRUE == DebugIndicator )
	gMAPPrintGBlock( pgb );
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 6 );
    }
}


/*************************************************************************

FUNCTION
	void  sendRestoreDataResponse( gblock_t * pgb )
	
DESCRIPTION
	Send restrore data response.
INPUTS
        Arg:	pgb - pointer to gblock

OUTPUTS
        Return: None
FEND
*************************************************************************/

void
sendRestoreDataResponse( gblock_t * pgb )
{


    if( TRUE == DebugIndicator )
    printf( "sendRestoreDataResponse() : Processing the request\n" );
    /*gMAPPrintGBlock(pgb); */
    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;

    pgb->serviceType = GMAP_RSP;
    pgb->serviceMsg = RESTORE_DATA;
    pgb->invokeId = restoreDataInvokeId[pgb->dialogId];
    if ( applicationContext == 2 )
    {
	pgb->parameter.restoreDataRes_v2.bit_mask = 0;
	pgb->parameter.restoreDataRes_v2.hlr_Number.length = 1;
	pgb->parameter.restoreDataRes_v2.hlr_Number.value[0] = '5';
    }
    else if ( applicationContext == 3 )
    {
	pgb->parameter.restoreDataRes_v3.bit_mask = 0;
	pgb->parameter.restoreDataRes_v3.hlr_Number.length = 1;
	pgb->parameter.restoreDataRes_v3.hlr_Number.value[0] = '5';
    }
    else
    {
	printf( "Application Context Version %d Not Supported \n", applicationContext );
	exit( 0 );
    }
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 7 );
    }

}


/*************************************************************************

FUNCTION
	void  sendIMSIResponse( gblock_t * pgb )
	
DESCRIPTION
	Send IMSI response.
INPUTS
        Arg:	pgb - pointer to gblock

OUTPUTS
        Return: None
FEND
*************************************************************************/

void
sendIMSIResponse( gblock_t * pgb )
{
    printf( "sendIMSIResponse() : Processing the request\n" );
    if( TRUE == DebugIndicator )
	gMAPPrintGBlock( pgb );
    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;

    pgb->serviceType = GMAP_RSP;
    pgb->serviceMsg = SEND_IMSI;
    /* TODO: Do not always return the first IMSI of the list
       We should have a mapping MSISDN <-> IMSI   
     */
    memcpy( &pgb->parameter.imsi.value, &sub[1].IMSI, sizeof( pgb->parameter.imsi.value ) );
    pgb->parameter.imsi.length = sub[1].length;
    printf( "imsi length is %d\n", pgb->parameter.imsi.length );
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 8 );
    }
}


/*************************************************************************

FUNCTION
        void  sendUpdateGprsLocationResponse( gblock_t * pgb )

DESCRIPTION
	Send IMSI response.
INPUTS
        Arg:	pgb - pointer to gblock

OUTPUTS
        Return: None
FEND
*************************************************************************/

void
sendUpdateGprsLocationResponse( gblock_t * pgb )
{
    printf( "sendUpdateGprsLocationResponse() : Processing the request\n" );
    if( TRUE == DebugIndicator )
	gMAPPrintGBlock( pgb );
    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;

    pgb->serviceType = GMAP_RSP;
    pgb->serviceMsg = UPDATE_GPRS_LOCATION;
    /* TODO: The hlr-Number shouldn't be hardcoded
     */
    pgb->parameter.updateGprsLocationRes_v3.bit_mask = UpdateGprsLocationRes_v3_add_Capability_present;
    pgb->parameter.updateGprsLocationRes_v3.hlr_Number.length = 7;
    if ( convertToString( "91198902044524", 14, pgb->parameter.updateGprsLocationRes_v3.hlr_Number.value ) != 7 )
	printf( "error encoding hlr-Number\n" );
    pgb->parameter.updateGprsLocationRes_v3.add_Capability = TRUE;
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 17 );
    }
}



/*************************************************************************

FUNCTION
	void  sendInsertSubDataReqTS( gblock_t * pgb, int ind )
	
DESCRIPTION
	Send Insert Sub data request for teleservice.
INPUTS
        Arg:	pgb - pointer to gblock

OUTPUTS
        Return: None
FEND
*************************************************************************/

void
sendInsertSubDataReqTS( gblock_t * pgb, int ind )
{
    int             i = 0;
    /*gMAPPrintGBlock(pgb); */

    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;

    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = INSERT_SUBSCRIBER_DATA;
    pgb->invokeId = ++invokeId[pgb->dialogId];

    if ( applicationContext == 3 )
    {
	pgb->parameter.insertSubscriberDataArg_v3.bit_mask |= InsertSubscriberDataArg_v3_teleserviceList_present;
	pgb->parameter.insertSubscriberDataArg_v3.teleserviceList.count = sub[ind].TScount;
	for ( i = 0; i < sub[ind].TScount; i++ )
	{
	    pgb->parameter.insertSubscriberDataArg_v3.teleserviceList.value[i].length = 1;
	    pgb->parameter.insertSubscriberDataArg_v3.teleserviceList.value[i].value[0] = sub[ind].ts[i];
	}
    }
    else if ( applicationContext == 2 )
    {
	pgb->parameter.insertSubscriberDataArg_v2.bit_mask |= InsertSubscriberDataArg_v2_teleserviceList_present;
	pgb->parameter.insertSubscriberDataArg_v2.teleserviceList.count = sub[ind].TScount;
	for ( i = 0; i < sub[ind].TScount; i++ )
	{
	    pgb->parameter.insertSubscriberDataArg_v2.teleserviceList.value[i].length = 1;
	    pgb->parameter.insertSubscriberDataArg_v2.teleserviceList.value[i].value[0] = sub[ind].ts[i];
	}
    }
    else
    {
	printf( "Application Context Version %d Not Supported\n", applicationContext );
	exit( 0 );
    }
    if( TRUE == DebugIndicator )
	gMAPPrintGBlock( pgb );
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 9 );
    }

}

/*************************************************************************

FUNCTION
	void  sendInsertSubDataReqSS( gblock_t * pgb, int ind )
	
DESCRIPTION
	Send Insert Sub data - inserts subsrciber status
INPUTS
        Arg:	pgb - pointer to gblock

OUTPUTS
        Return: None
FEND
*************************************************************************/

void
sendInsertSubDataReqSS( gblock_t * pgb, int ind )
{
    int             i = 0;
    /*gMAPPrintGBlock(pgb); */

    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;

    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = INSERT_SUBSCRIBER_DATA;
    pgb->invokeId = ++invokeId[pgb->dialogId];

    if ( applicationContext == 3 )
    {
	pgb->parameter.insertSubscriberDataArg_v3.bit_mask |= InsertSubscriberDataArg_v3_subscriberStatus_present;
	pgb->parameter.insertSubscriberDataArg_v3.subscriberStatus = sub[ind].SS;
	
    }
    else if ( applicationContext == 2 )
    {
	pgb->parameter.insertSubscriberDataArg_v2.bit_mask |= InsertSubscriberDataArg_v2_subscriberStatus_present;
	pgb->parameter.insertSubscriberDataArg_v2.subscriberStatus = sub[ind].SS;
	
    }
    else
    {
	printf( "Application Context Version %d Not Supported\n", applicationContext );
	exit( 0 );
    }
    if( TRUE == DebugIndicator )
	gMAPPrintGBlock( pgb );
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 9 );
    }

}


/*************************************************************************

FUNCTION
	void  sendDelimiter( gblock_t * pgb )
	
DESCRIPTION
	Send delimiter.
INPUTS
        Arg:	pgb - pointer to gblock

OUTPUTS
        Return: None
FEND
*************************************************************************/

void
sendDelimiter( gblock_t * pgb )
{
    /* then we send a delimiter */
//    pgb->dialogId = dialogId;
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = GMAP_DELIMITER;
    pgb->applicationId = 0;
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->parameter.delimiter.qualityOfService = CL_SVC_CLASS_1;
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 10 );
    }
}


/* FUNCTION sendClose() - Closes the dialog                             */
/*************************************************************************

FUNCTION
    void sendClose(gblock_t* pgb)

DESCRIPTION
        This function closes the dialog.

INPUTS
        Arg:    pgb - a reference to an externally allocated gblock_t

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
sendClose( gblock_t * pgb )
{
    pgb->applicationId = 0;
//    pgb->dialogId = dialogId;
    pgb->bit_mask = gblock_t_parameter_present;
    pgb->serviceType = GMAP_REQ;
    pgb->serviceMsg = GMAP_CLOSE;
    pgb->parameter.closeArg.releaseMethod = normalRelease;
    pgb->parameter.closeArg.qualityOfService = CL_SVC_CLASS_1;
    if( TRUE == DebugIndicator )
    printf( "Sending GMAP_CLOSE \n" );
    if ( gMAPPutGBlock( pgb ) != 0 )
    {
	S8 buf[256];
	sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
	       pgb->errorSource, pgb->error, pgb->errorReport);
	FtErrorDisplay( buf );
	terminate( 11 );
    }
}

/* FUNCTION SendEmptyAuthenticationInfoResponse() - Issue a SEND_AUTHENTICATION_INFO Resp */
/*************************************************************************
FUNCTION
        void sendEmptyAuthenticationInfoResponse(gblock_t* pgb, int subs)

DESCRIPTION
        This function is used by the server to respond to a SendAuthenticationIn
fo Request
        request when the IMSI is known but no vectors are present.

INPUTS
        Arg:    pgb - a reference to an externally allocated gblock_t

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
sendEmptyAuthenticationInfoResponse( gblock_t * pgb)
{
    if( TRUE == DebugIndicator )
        printf("In sendEmptyAuthenticationInfoResponse\n");

    pgb->applicationId = 0;
    pgb->bit_mask = 0;

    pgb->serviceType = GMAP_RSP;
    pgb->serviceMsg = SEND_AUTHENTICATION_INFO;
    pgb->invokeId = invokeId[pgb->dialogId];

    if ( applicationContext == 3 )
    {
            pgb->parameter.sendAuthenticationInfoRes_v3.bit_mask = 0;
    }
    else
    {
      printf("Invalid sendEmptyAuthenticationInfoResponse for Application Context 2\n");
    }
    if( TRUE == DebugIndicator )
        gMAPPrintGBlock( pgb );

    if ( gMAPPutGBlock( pgb ) != 0 )
    {
        S8 buf[256];
        sprintf(buf, "pgb->errorSource=%d, pgb->error=%d, pgb->errorReport=%s",
               pgb->errorSource, pgb->error, pgb->errorReport);
        FtErrorDisplay( buf );
        terminate( 3 );
    }

}



/* FUNCTION gblockReceived() - event processing for incoming GSM messages*/
/*************************************************************************

FUNCTION
    void gblockReceived(gblock_t *pgb)

DESCRIPTION
        This function implements both the client and the server behaviour:
	When invoked as a server, it expects to receive an open followed
	by a cancel location request and a delimiter then issue a response.
	When invoked as a client, it expects to receive an open response
	followed by an cancel location response and a close.

INPUTS
        Arg:    pgb - a reference to the received gblock_t

OUTPUTS
        Return: None

FEND
*************************************************************************/

void
gblockReceived( gblock_t * pgb )
{
    int             index = -1;

    if ( ( pgb->serviceType == GMAP_REQ ) && ( pgb->serviceMsg == GMAP_OPEN ) )
    {
	/*Check for Application Context .... */
	/* This server expects to be able to completely process this dialog
	   before handling a new one (because the request will be in the
	   same MSU as the open).
	 */

//	dialogId = pgb->dialogId;
	if ( ( compareObjectIDs( &pgb->parameter.openArg.applicationContext, &infoRetrievalContext_v2 )
	       && applicationContext == 2 ) ||
	     ( compareObjectIDs( &pgb->parameter.openArg.applicationContext, &infoRetrievalContext_v3 )
	       && applicationContext == 3 ) ||
	     ( compareObjectIDs( &pgb->parameter.openArg.applicationContext, &networkLocUpContext_v2 )
	       && applicationContext == 2 ) ||
	     ( compareObjectIDs( &pgb->parameter.openArg.applicationContext, &networkLocUpContext_v3 )
	       && applicationContext == 3 ) || compareObjectIDs( &pgb->parameter.openArg.applicationContext, &imsiRetrievalContext_v2 ) ||
 	       compareObjectIDs( &pgb->parameter.openArg.applicationContext, &gprsLocationUpdateContext_v3 ) )

	{
	    sendOpenResponse( pgb, dialogAccepted );
    if( TRUE == DebugIndicator )
	    printf( " Sent OPEN RESPONSE\n" );
	    return;
	}
	else
	{
	    abortDialog( pgb );
    if( TRUE == DebugIndicator )
	    printf( " Sent ABORT DIALOG\n" );
	    return;
	}
    }
    switch ( pgb->serviceMsg )
    {
    case SEND_AUTHENTICATION_INFO:
    if( TRUE == DebugIndicator )
	printf( "Message received: %d (SEND_AUTHENTICATION_INFO)\n", SEND_AUTHENTICATION_INFO );
	
	invokeId[pgb->dialogId] = pgb->invokeId;
	/* Initial Request */
	 if(context[pgb->dialogId].dialogId != pgb->dialogId)
	{
	    context[pgb->dialogId].dialogId = pgb->dialogId;
	    if ( applicationContext == 2 )
            {
	        index = verify( pgb->parameter.sendAuthenticationInfoArg_v2.length, pgb->parameter.sendAuthenticationInfoArg_v2.value );
	    }
	    else /* ac=3 */
	    {
	        index = verify( pgb->parameter.sendAuthenticationInfoArg_v3.imsi.length, pgb->parameter.sendAuthenticationInfoArg_v3.imsi.value );
                context[pgb->dialogId].nbr_requested = 
			pgb->parameter.sendAuthenticationInfoArg_v3.\
				numberOfRequestedVectors;
		if (TRUE == DebugIndicator)
		{
            	    printf( "Number of requested vectors = %d\n", 
				context[pgb->dialogId].nbr_requested );
		}
                context[pgb->dialogId].saved_vector_index = -1;
	    }
	    if (( index != -1 )&& (sub[index].count != 0))
            {
		if (sub[index].count != 
			context[pgb->dialogId].nbr_requested)
		{
		    context[pgb->dialogId].nbr_requested =
					sub[index].count;
		}

                context[pgb->dialogId].saved_sub_index = index;
                context[pgb->dialogId].cont_vector_index = 0;
	        sendAuthenticationInfoResponse( pgb, index);
	    }
	    else if (( index != -1 ) && (sub[index].count == 0))
	    {
		/* No auth vectors, but known user */
		sendEmptyAuthenticationInfoResponse( pgb);
	        sendClose( pgb );
	        context[pgb->dialogId].dialogId=0;
	        break;
	    }
	    else
	    {
	        unknownSubscriber( pgb );
	        sendClose( pgb );
	        context[pgb->dialogId].dialogId=0;
	        break;
	    }
        }
	else /* Recd a TC-CONT.  Both these conditions must be true:
	      * AC = 3, and requested vectors = quintets.
	      */
	{
	    sendAuthenticationInfoResponse( pgb, 
		context[pgb->dialogId].saved_sub_index );
	}

        /* FR 904 */
	/* Decide whether to send a DELIMITER or a CLOSE */
	if(( applicationContext == 2 )||
           (context[pgb->dialogId].saved_vector_index + 1 >= 
		context[pgb->dialogId].nbr_requested))
        {
	    if (TRUE == DebugIndicator)
	    {
	        printf("Sending a Close\n");
	    }
	    sendClose( pgb );
	    invokeId[pgb->dialogId]=0;
	    context[pgb->dialogId].dialogId=0;
        }
        else
	{
	    if (TRUE == DebugIndicator)
	    {
	        printf("Sending a Delimiter\n");
	    }
	    sendDelimiter(pgb);
	}
	break;

    case RESTORE_DATA:
    if( TRUE == DebugIndicator )
	printf( "Message received: %d (RESTORE_DATA)\n", RESTORE_DATA );
	restoreDataInvokeId[pgb->dialogId] = pgb->invokeId;
	if ( applicationContext == 2 )
	    index = verify( pgb->parameter.restoreDataArg_v2.imsi.length, pgb->parameter.restoreDataArg_v2.imsi.value );
	else
	    index = verify( pgb->parameter.restoreDataArg_v3.imsi.length, pgb->parameter.restoreDataArg_v3.imsi.value );
	if ( index != -1 )
	    sendInsertSubDataRequests( pgb, index );
	else
	    unknownSubscriber( pgb );
	break;
    case GMAP_DELIMITER:
	/*Dont worry abt the Delimiters. It is done in the verifyAndSend portion */
	break;

    case INSERT_SUBSCRIBER_DATA:
    if( TRUE == DebugIndicator )
	printf( "Message received: %d (INSERT_SUBSCRIBER_DATA)\n", INSERT_SUBSCRIBER_DATA );
	invokeId[pgb->dialogId] = pgb->invokeId;
    if( TRUE == DebugIndicator )
	printf( "Message received: insertSubDataCounter[%d] = %d \n", pgb->dialogId, insertSubDataCounter [pgb->dialogId]);
	if ( --insertSubDataCounter[pgb->dialogId] == 0 )
	{
	    /*insertSubDataCounter is incremented 
	       everytime you send a InsertSubscriberData message.
	       --S */

	    sendRestoreDataResponse( pgb );
	    sendClose( pgb );
	}
	break;
    case SEND_IMSI:
    if( TRUE == DebugIndicator )
	printf( "Message received: %d (SEND_IMSI)\n", SEND_IMSI );
	sendIMSIResponse( pgb );
	sendClose( pgb );
	break;

	case UPDATE_GPRS_LOCATION:
    if( TRUE == DebugIndicator )
    printf( "Message received: %d (UPDATE_GPRS_LOCATION)\n", UPDATE_GPRS_LOCATION );
	sendUpdateGprsLocationResponse( pgb );
	sendClose( pgb );
	break;

    case GMAP_U_ABORT:
    if( TRUE == DebugIndicator )
	printf( "Message received: %d (GMAP_U_ABORT)\n", GMAP_U_ABORT );
	break;

    case GMAP_CLOSE:
    if( TRUE == DebugIndicator )
        printf( "Message received: %d (GMAP_CLOSE)\n", GMAP_CLOSE );
	/* cleanup */

	invokeId[pgb->dialogId]=0;
	context[pgb->dialogId].dialogId=0;
	break;

    default:
     printf( "Message received: %d (GMAP_CLOSE)\n", pgb->serviceMsg );
	fprintf( stderr, "Received unexpected message. Aborting...\n" );
	if( TRUE == DebugIndicator )
	    gMAPPrintGBlock( pgb );
	/*terminate(  ); */
	break;
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
    async_req = FALSE;

    if ( ( ret_code = AsyncError( &ash ) ) )
    {
	/*  There was some kind of error:  probably the request was cancelled
	 */
//	DBG_DISP( DBG_MASK_ERR, 48, "%s: process_mom_q(): I/O error=%d\n", MYNAME, ret_code );
	printf( "%s: process_mom_q(): I/O error=%d\n", MYNAME, ret_code );
	return;
    }

    ipc = ( char * ) AsyncGetMsgHeader( &ash );

    switch ( ( ( Header_t * ) ipc )->messageType )
    {
    case N_NOTICE_IND:
    case N_UNITDATA_IND:	/* received a TCAP message */
	gMAPTakeMsg( ( cblock_t * ) ipc );
//	memset( pgb, 0, sizeof( *pgb ) );
	while ( gMAPGetGBlock( pgb ) == 0 )
	{
	    gblockReceived( pgb );
//	    memset( pgb, 0, sizeof( *pgb ) );
	}
	break;
    case N_STATE_IND:
	processNState( ( Header_t * ) & ipc );
	break;
    case N_PCSTATE_IND:
	processPcState( ( Header_t * ) & ipc );
	break;
    case TAP_STATE_CHANGE:	/* only if process is designatable */
//	DBG_DISP( DBG_MASK_L2, 49, "   Received TAP_STATE_CHANGE\n" );
	break;
    default:
//	DBG_DISP( DBG_MASK_ERR, 50, "   Received unknown msg type[0x%x]\n", ( ( Header_t * ) ipc )->messageType );
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
    if ( async_req )
	return;

    if ( ash.bufferStatus && !AsyncComplete( &ash ) )
    {
	/*  Safety measure: don't attempt to create a new async request if
	 *  the prior one didn't complete yet.
	 *  This should never happen.
	 */
//	DBG_DISP( DBG_MASK_ERR, 51, "%s: setup_async_req(): ", "!AsyncComplete(ash): 0x%x\n", MYNAME, ash.bufferStatus );
	terminate( 12 );
    }

    if ( FtGetIpcAsync( &ash,	/* set up async. read request */
			0,	/* get any messages */
			MAXipcBUFFERsize,	/* length of our buffer    */
			TRUE,	/* truncate messages */
			0 )	/* callback function */
	 == RETURNerror )
    {
//	DBG_DISP( DBG_MASK_ERR, 52, "(%s): FtGetIpcAsync() failed: (%d)%s\n", MYNAME, errno, strerror( errno ) );
	terminate( 13 );
    }

    /* we now have an async request out there to be aware of */
    async_req = TRUE;
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

    if ( async_req )
    {
	async_req = FALSE;
	if ( FtCancelAsyncRequest( &ash ) == RETURNerror )
	{
//	    DBG_DISP( DBG_MASK_ERR, 53, "(%s): FtCancelAsyncRequest() failed: (%d)%s\n", MYNAME, errno, strerror( errno ) );
	    terminate( 14 );
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


/* FUNCTION asyncMainLoop */
/*************************************************************************

FUNCTION
    void asyncMainLoop()

DESCRIPTION
    This is the main loop of this application

INPUTS
    Arg:
OUTPUTS
    Return: None
FEND
*************************************************************************/
void
asyncMainLoop(  )
{
//    int             size;
//    char            buff[1024] = { 0 };
    int             ret_code;
    MAP_AppTimeout  appTimeout;	/* MAP_AppTimeout is defined in gmaptim.h */

    gblock_t        gblock;
    gblock_t       *pgb = &gblock;

/*
 *    Enter main loop of the application
 */

    /* Set Async Header message pointer */
    AsyncSetMsgHeader( &ash, ( void * ) &PRIPC );

    /* Get ready to poll() */
    memset( pfd, 0, sizeof( pfd ) );
//    Pglobal->pfd[SOCKET_FD].fd = setup_socket( Pglobal->port, Pglobal->hostname );

    /* Get MOM file descriptor */
    if ( ( pfd[MOM_FD].fd = FtGetDescriptor(  ) ) < 1 )
    {
	DebugIndicator = TRUE;
	FtErrorDisplay( "FtGetDescriptor() failed" );
	terminate( 15 );
    }

    /* poll for data messages */
//    Pglobal->pfd[SOCKET_FD].events = POLLIN;
    pfd[MOM_FD].events = POLLIN;

    /* Receive messages */
    finished_initialization = TRUE;	/* SIGINT will set terminate */


//    DBG_DISP( DBG_MASK_L2, 54, "Entering Main Loop\n" );
    printf( "Entering Main Loop\n" );

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
//printf("asyncMainLoop: before poll\n");
	ret_code = poll( pfd, 1, INFTIM );
//printf("asyncMainLoop: after poll\n");

	if ( ret_code <= 0 )
	{
//	    memset( pgb, 0, sizeof( *pgb ) );
	    while ( gMAPGetGBlock( pgb ) == 0 )
	    {
		gblockReceived( pgb );
//		memset( pgb, 0, sizeof( *pgb ) );
	    }
	    continue;
	}


//	if ( Pglobal->pfd[SOCKET_FD].revents & POLLERR || Pglobal->pfd[SOCKET_FD].revents & POLLNVAL )
//	{
//	    DBG_DISP( DBG_MASK_L2, 55, "(%s): POLLERR or POLLNVAL\n", MYNAME );
//	    Pglobal->sock = -1;
//	    Pglobal->psock = -1;
//	    Pglobal->pfd[SOCKET_FD].fd = setup_socket( Pglobal->port, Pglobal->hostname );
//	    DBG_DISP( DBG_MASK_L2, 56, "(%s): Error on the socket,  resetting connections\n", MYNAME );
//	    continue;
//	}
//	if ( Pglobal->pfd[SOCKET_FD].revents & POLLHUP )
//	{
//	    DBG_DISP( DBG_MASK_L2, 57, "(%s): Socket closed,  listening for new connections\n", MYNAME );
//	    /* listen to the parent socket for connections */
//	    Pglobal->pfd[SOCKET_FD].fd = Pglobal->psock;
//	    continue;
//	}

	/* First check for messages from MOM */
	if ( pfd[MOM_FD].revents & POLLIN )
	{
	    /* There are IPC messages for us to read */
	    FtProcessIO(  );
	    process_mom_q( pgb );
	}

//	/* Then check for messages from TCP */
//	if ( Pglobal->pfd[SOCKET_FD].revents & POLLIN )
//	{
//	    /*  If we were poll()ing on the parent (listen) socket, then we
//	     *  haven't accept()ed a connection yet.  Since there was activity
//	     *  on the listen socket, we can now accept() a connection.
//	     */
//	    if ( Pglobal->pfd[SOCKET_FD].fd == Pglobal->psock )
//	    {
///*
//		Pglobal->sock = sctp_accept( Pglobal->psock, NULL );
//		if ( -1 == Pglobal->sock )
//		{
//		    DBG_DISP( DBG_MASK_ERR, 58, "sctp_accept() failed\n" );
//		    terminate(  );
//		}
//*/
//		struct  sockaddr_in sina;	/* accepted sockaddr    */
//		size_t AddrLen;
//
//		AddrLen = (size_t)sizeof(sina);
//
////		memset(&sina, 0, sizeof(sina));
//
//		if (-1 == (Pglobal->sock = accept(Pglobal->psock, (struct sockaddr *)&sina, &AddrLen)))
//
//		{
//		    DBG_DISP( DBG_MASK_ERR, 58, "accept() failed\n" );
//		    terminate(  );
//		}
//
//
//		/* From now on, we poll on the connection socket */
//		Pglobal->pfd[SOCKET_FD].fd = Pglobal->sock;
//
//		( void ) FtPutStateEx( ( S16 ) 2, State_Green, "Connection Established", 10, FALSE );
//
//		DBG_DISP( DBG_MASK_L2, 59, "%s: connection established\n", MYNAME );
//
//		continue;
//	    }
//
//	    /*  Else we were polling on the connection and there is now
//	     *  some data there to read.
//	     */
///*	    size = sctp_recv( Pglobal->pfd[SOCKET_FD].fd, buff, sizeof( buff ), NULL ); */
//	    size = tcp_recv(Pglobal->pfd[SOCKET_FD].fd, buff, sizeof(buff));
//
//	    /* Check for errors on the read() */
//	    if ( size < 1 )
//	    {
//		if ( size )
//		{
///*			DBG_DISP( DBG_MASK_ERR, 591, "sctp_recv() failed, errno=%d\n", errno );
//*/
//		    DBG_DISP( DBG_MASK_ERR, 591, "TCP recv() failed, errno=%d\n", errno );
//		}
//		else
//		{
//		    DBG_DISP( DBG_MASK_ERR, 592, "Socket closed, errno=%d, wait for another connection\n", errno );
//		    /* close the accepted socket */
//		    close( Pglobal->pfd[SOCKET_FD].fd );
//		    /* now go back to checking the listen socket */
//		    Pglobal->pfd[SOCKET_FD].fd = Pglobal->psock;
//		    continue;
//		}
//		terminate(  );
//	    }
//
//	    /*  Else we got some data from the socket.
//	     *
//	     *  The data coming
//	     *  from the socket might cause the application to go off and
//	     *  do something that requires 2-way IPC communication (e.g.,
//	     *  SYSbind(3q) or putting a message to the TCAP librarie.)
//	     *  In this case, it would be necessary to cancel the async
//	     *  read request so that it does not interfere with these other
//	     *  API calls.
//	     *
//	     *  It should be noted that cancelling the async read request
//	     *  is not expensive: it's the same thing that happens when the
//	     *  synchronous functions (e.g., FtGetIpcEx(3f)) are interrupted
//	     *  by a signal or time out.
//	     *
//	     *  When we go back around to the top of the loop, the async read
//	     *  request will be set up again.
//	     *
//	     *   cancel_async_req(pgb);
//	     */
//
//	    /* Here we "consume" the message we got on the socket
//	       we parse the message and fill the structure
//	       we build MAP requests and send them to the HLR */
//
//	    process_socket_msg( buff, size, pgb );
//
//	}
//
    }				/* for() */
}



/* FUNCTION mainLoop() - Main Processing Routine            */
/*************************************************************************

FUNCTION
    void mainLoop(gblock_t* pgb)

DESCRIPTION
        This function is the main processing loop for the process.

INPUTS
        Arg:    pgb - a reference to an externally allocated gblock_t

OUTPUTS
        Return: None

FEND
*************************************************************************/
void
mainLoop( gblock_t * pgb )
{
    MAP_AppTimeout  appTimeout;	/* MAP_AppTimeout is defined in gmaptim.h */

#define GT_MAX_IPC (4096)
    char            ipc[GT_MAX_IPC];

    finished_initialization = TRUE;	/* SIGINT will set terminate */
    for ( ;; )
    {
	int             retStatus;
	BOOL            savedDebugIndicator;

	if ( Terminate )
	    terminate( 16 );

	gMAPAppTimerHandler(  );

	while ( gMAPAppGetTimeout( &appTimeout ) == 0 )
	{
	    if ( appTimeout.id1 == GUARD_TIMER_ID )
		gMAPTimeout( &appTimeout );
	}

	while ( gMAPGetGBlock( pgb ) == 0 )
	    gblockReceived( pgb );

	savedDebugIndicator = DebugIndicator;
	DebugIndicator = FALSE;
	retStatus = FtGetIpcEx( ( Header_t * ) & ipc, 0,	/* any message type  */
				GT_MAX_IPC,	/* max. size to rcv  */
				TRUE,	/* truncate if large */
				TRUE,	/* blocking read     */
				TRUE );	/* interruptible     */
	DebugIndicator = savedDebugIndicator;

	if ( retStatus == RETURNerror )	/* error occured in a blocking read */
	{
	    if ( errno != EINTR )	/* not interrupt */
	    {
		fprintf( stderr, "%s: Err in FtGetIpcEx(), blocking read [errno=%d]\n", PROCESS_NAME, errno );
	    }
	}
	else
	{
	    switch ( ( ( Header_t * ) ipc )->messageType )
	    {
	    case N_NOTICE_IND:
	    case N_UNITDATA_IND:	/* received a TCAP message */
		gMAPTakeMsg( ( cblock_t * ) & ipc );
		while ( gMAPGetGBlock( pgb ) == 0 )
		    gblockReceived( pgb );
		break;
	    case N_STATE_IND:
		processNState( ( Header_t * ) & ipc );
		break;
	    case N_PCSTATE_IND:
		processPcState( ( Header_t * ) & ipc );
		break;
	    case TAP_STATE_CHANGE:	/* only if process is designatable */
		fprintf( stderr, "   Received TAP_STATE_CHANGE\n" );
		break;
	    default:
		fprintf( stderr, "   Received unknown msg type[0x%x]\n", ( ( Header_t * ) ipc )->messageType );
		break;
	    }
	}
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
    int             LND_state;
    char           *pchr = "C7";	/* default protocol type    */
    int             debug_flag = FALSE;	/* foundation tool debug print    */
    int             monitor_flag = FALSE;	/* process monitor        */
    int             restart_flag = AUTOrestart;	/* allow restart of process    */
    char            stand_alone = TRUE;	/* pgm is started from start file, */
    /* not from cmd line.        */
    char            logical_name[MAXlogicalNAME + 1];

    int             iFile = 0;	/*Index of the hlr file parameter */
    char            contConfig[10]={0};
    char            *pTmpContConfig = NULL;
    int             contRequestedAuthVectors=0;
    int             fSegment=FALSE;


    /* default values */
    GMAPINIT.ssn = 8;
    GMAPINIT.nDialogs = 100;
    GMAPINIT.nInvokes = 100;
    GMAPINIT.protocol = itu7;
    strcpy( GMAPINIT.nodeName, "C7" );
    GMAPINIT.debugFile = stdout;

    printf( "SHM=%s, OMNI_HOME=%s argv[0]=%s\n", getenv( "SHM" ), getenv( "OMNI_HOME" ), argv[0] );

    if ( argc < 2 )
    {
	printf( "%s [-alone] [-node <node name>] [-prot <A7, C7, or CH7>] [-no_rst]\n", argv[0] );
	printf( "    [-lpc <local PC>] [-lssn <local SSN>]\n" );
	printf( "    [-appctx <application context version>]\n" );
        printf( "    [-cont v,w,x,y,z]\n" );
	printf( "    [-debug] [-monitor] [0x<gsmdebugmask>]\n" );
	printf( "    default <node name> = C7\n" );
	printf( "    default <process name> = HLR\n" );
	printf( "    default prot = C7\n" );
	printf( "    default <local PC>     = 1000\n" );
	printf( "    default <local SSN>    = 6\n" );
	printf( "    default debug = FALSE, no_rst = FALSE(AUTOrestart)\n" );
	printf( "    default alone = TRUE, <gsmdebugmask> = 01\n" );
	exit( 0 );
    }
/*
 *    parse command line arguments, and setup proper global variables
 */
    for ( i = 1; i < argc; i++ )
    {
	/* printf("argv[%d] = %s\n", i, argv[i]);  */
	if ( strcmp( argv[i], "-prot" ) == 0 )
	{
	    if ( strcmp( argv[i + 1], "A7" ) == 0 )
	    {
		GMAPINIT.protocol = ansi7;
		pchr = "A7";
	    }
	    else if ( strcmp( argv[i + 1], "CH7" ) == 0 )
	    {
		GMAPINIT.protocol = chinese7;
		pchr = "CH7";
	    }
	    else
	    {
		GMAPINIT.protocol = itu7;
		pchr = "C7";
	    }
	}
	if ( strcmp( argv[i], "-node" ) == 0 )
	    ( void ) strcpy( GMAPINIT.nodeName, argv[i + 1] );
	if ( strcmp( argv[i], "-name" ) == 0 )
	    ( void ) strcpy( PROCESS_NAME, argv[i + 1] );
	if ( strcmp( argv[i], "-alone" ) == 0 )
	    stand_alone = TRUE;
	if ( strcmp( argv[i], "-no_rst" ) == 0 )
	    restart_flag = NOrestart;
	if ( strcmp( argv[i], "-lpc" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
		LOCAL_PC = convertPC( argv[i + 1] );
	    else
	    {
		fprintf( stderr, "Option -lpc must be followd by a PC\n" );
		exit( 1 );
	    }
	}
	if ( strcmp( argv[i], "-appctx" ) == 0 )
	    applicationContext = ( U8 ) atoi( argv[i + 1] );
	if ( strcmp( argv[i], "-lssn" ) == 0 )
	    GMAPINIT.ssn = ( U8 ) atoi( argv[i + 1] );
	if ( strcmp( argv[i], "-ndialog" ) == 0 )
	    GMAPINIT.nDialogs = ( U16 ) atoi( argv[i + 1] );
	if ( strcmp( argv[i], "-ninvoke" ) == 0 )
	    GMAPINIT.nInvokes = ( U16 ) atoi( argv[i + 1] );
	if ( strcmp( argv[i], "-debug" ) == 0 )
	    debug_flag = TRUE;	/* Ft debug flag */
	if ( strcmp( argv[i], "-monitor" ) == 0 )
	    monitor_flag = TRUE;

	if ( strcmp( argv[i], "-tracefile" ) == 0 )
	{
	    if ( ( i + 1 ) < argc )
	    {
		GMAPINIT.debugFile = fopen( argv[i + 1], "w" );
		if ( !GMAPINIT.debugFile )
		{
		    fprintf( stderr, "Unable to open trace file %s:%s.\n" "Using stdout instead\n", argv[i + 1], strerror( errno ) );
		    GMAPINIT.debugFile = stdout;
		}
	    }
	    else
	    {
		fprintf( stderr, "Option -tracefile must be followed by the name of a file\n" );
		exit( 1 );
	    }
	}
	if ( strcmp( argv[i], "-hlr" ) == 0 )
	    iFile = i + 1;
	if ( strcmp( argv[i], "-conf" ) == 0 )
	    iFile = i + 1;

        if (strcmp( argv[i], "-cont" ) == 0 )
        {
           if ( ++i < argc )
                sprintf( contConfig, "%s", argv[i] );

           if( TRUE == DebugIndicator )
                printf("contConfig  is = %s\n", contConfig);

            pTmpContConfig = strtok( contConfig, ",");
            for ( j = 0; j < MAX_SEGM_MSG; j++ )
            {
                if(pTmpContConfig == NULL)
                {
		    break;
		}
		else
		{
                    cont_vector[j]=atoi(pTmpContConfig);
		    if (0 == cont_vector[j])
		    {
			/* Do not allow 0 */
			printf("Invalid value 0 for -cont\n");
			exit(0);
		    }
                    contRequestedAuthVectors+=cont_vector[j];

                    pTmpContConfig = NULL;
                    pTmpContConfig= strtok(NULL, "," );
                }
            }

           if (contRequestedAuthVectors > MAX_AUTH_VECT)
           {
              printf ("Only %d Authentication Vectors can be requested, %d are configured\n",MAX_AUTH_VECT,contRequestedAuthVectors);
              exit(0);
           }

           for( j = 0; j < MAX_SEGM_MSG; j++ )
               printf("cont_vector[%d] = %d\n",j,cont_vector[j]);

	   fSegment=TRUE;
        }

    }
    if (!fSegment)
    {
	    /* Segmentation not requested. Set the value to 5 in the 1st slot */
	    printf("Segmentation not requested\n");
	    cont_vector[0]=MAX_AUTH_VECT;
    }
   


    if ( GMAPINIT.protocol == chinese7 || GMAPINIT.protocol == ansi7 )
    {
	if ( LOCAL_PC == 1000 )
	    LOCAL_PC = 0x0a0a0a;	/* point code = 10-10-10 */
    }
    printf( "PROTOCOL=%d(%s), NODE=%s, ALONE=%d, PROCESS_NAME=%s\n", GMAPINIT.protocol, pchr, GMAPINIT.nodeName, stand_alone, PROCESS_NAME );

    printf( "DEBUG=%d, MONITOR=%d, NDIALOGS=%d, NINVOKES=%d\n", debug_flag, monitor_flag, GMAPINIT.nDialogs, GMAPINIT.nInvokes );

    if ( GMAPINIT.protocol != itu7 )
    {
	printf( "LPC=%d-%d-%d, LSSN=%d\n", LOCAL_PC >> 16, ( LOCAL_PC & 0xff00 ) >> 8, LOCAL_PC & 0xff, GMAPINIT.ssn );
    }
    else
    {
	printf( "LPC=%d, LSSN=%d\n", LOCAL_PC, GMAPINIT.ssn );
    }
/*
 *    register the application
 */
    if ( stand_alone == TRUE )
    {
	( void ) sprintf( logical_name, "%s", PROCESS_NAME );
	ret_code = FtAttach( logical_name,	/* process logical name     */
			     argv[0],	/* process executable name  */
			     " ",	/* execution parameters     */
			     0,	/* execution priority       */
			     0,	/* RT time quantum          */
			     0,	/* RT time quantum          */
			     0,	/* process class identifier */
			     10 );	/* max. wait for CPT entry  */
	if ( ret_code == -1 )
	{
	    fprintf( stderr, "   FtAttach(), errno=%d(%s)\n", errno, LastErrorReport );
	    exit( 10 );
	}
    }

    if ( iFile <= 0 )
    {
	printf( "HLR database file not supplied\n" );
	printf( "usage : -conf <file-name> \n" );
	exit( 10 );
    }
    else if ( getFromFile( argv[iFile] ) < 0 )
    {
	printf( "HLR database file Improper\n" );
	exit( 10 );
    }

    ret_code = FtRegister(	/* Register this process            */
			      argc	/* Command Line Argument count      */
			      , argv	/* Command Line Arguments           */
			      , debug_flag	/* Debug Printouts Required ?       */
			      , monitor_flag	/* Msg Activity Monitor Required ?  */
			      , TRUE	/* Ipc Queue Required ?             */
			      , TRUE	/* Flush Ipc Queue Before Start ?   */
			      , FALSE	/* Allow Ipc Msg Queueing Always    */
			      , TRUE	/* Process Has SIGINT Handler       */
			      , ( U16 ) restart_flag	/* Automatic Restart allowed ?      */
			      , 0	/* Process Class Designation        */
			      , 0	/* Initial Process State Declaration */
			      , 0	/* Event Distribution Filter Value  */
			      , 10 );	/* retry                            */

    if ( ret_code == -1 )
    {
	fprintf( stderr, "   FtRegister(): errno=%d(%s)\n", errno, LastErrorReport );
	exit( 20 );
    }

    ret_code = FtAssignHandler( SIGINT, sigintCatcher );
    if ( ret_code == RETURNerror )
    {
	printf( "cannot assign SIGINT handler, errno=%d\n", errno );
    }
/*
 *    wait for the logical node to be ready
 */
    i = 0;
    for ( ;; )
    {
	LND_state = FtGetState( GMAPINIT.nodeName );
	if ( LND_state == LNDready )
	{
	    break;
	}
	if ( stand_alone == TRUE )
	{
	    fprintf( stderr, "   Node %s is not ready, state=0x%x\n", GMAPINIT.nodeName, LND_state );
	    FtTerminate( NOrestart, 12 );
	}
	i++;
	if ( ( i % 60 ) == 0 )
	{
	    fprintf( stderr, "   Node %s is not ready, state=0x%x\n", GMAPINIT.nodeName, LND_state );
	}
	( void ) FtPend( 1000 );	/* cannot call app_sleep() yet, */
	/* the application should call  */
	/* app_sleep() library function */
	/* after gMAPInitialize() is called */
    }
/*
 *    logical node is ready, bind for the SSN service
 */
    ALIAS_NAME_INDEX = SYSattach( GMAPINIT.nodeName, FALSE );
    if ( ALIAS_NAME_INDEX == -1 )
    {
	fprintf( stderr, "   SYSattach(): errno=%d(%s)\n", errno, LastErrorReport );
	FtTerminate( NOrestart, 5 );
    }

    ret_code = SYSbind( ALIAS_NAME_INDEX, FALSE,	/* non-designatable */
			MTP_SCCP_TCAP_USER, GMAPINIT.ssn, SCCP_TCAP_CLASS );	/* | SCCP_CONTROL_PRIMITIVE); */
    if ( ret_code == -1 )
    {
	fprintf( stderr, "   SYSbind(): errno=%d(%s)\n", errno, LastErrorReport );
	FtTerminate( NOrestart, 7 );
    }
/*
 *    initialize GMAP provider
 */
    strncpy( GMAPINIT.nodeName, GMAPINIT.nodeName, MAXcptNAME );

    sendUIS(  );		/* send user in-service to SCMG */
    printf( "Initializing GMAP...\n" );

    APPID = gMAPInitialize( &GMAPINIT, argc, argv );
    printf( "End initialization\n" );

    if ( APPID == -1 )
    {
	printf( "   gMAPInitialize(): errno=%d(%s)\n", GMAPINIT.error, GMAPINIT.errorReport );
	FtTerminate( NOrestart, 9 );
    }

/*
 *    Enter main loop of the application
 */
    START_TIME = time( 0 );

    printf( "\n   HLR test application ready ...\n\n\n" );
    asyncMainLoop();
    FtTerminate( NOrestart, 9 );

    return ( 0 );
}
