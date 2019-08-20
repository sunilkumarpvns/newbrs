/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source   UlticomTripletRequestor.c

@ClearCase-version: $Revision:/main/sw9/1 $ 

@date     $Date:5-Apr-2005 11:16:03 $

@product  Signalware

@subsystem  WLAN Gateway

DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE DEVELOPMENT OF A WORKING SOFTWARE PROGRAM.  THE SOURCE CODE PROVIDED IS NOT WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.
 
 ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER  INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and interest in and to the Software, and all intellectual property rights associated therewith, including without limitation, rights to copyrights, trade secrets or know-how. This disclaimer agreement shall be governed and construed in accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time without notice. Ulticom has no continuing obligation to provide additional source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples; these changes may be reported, for the sample code included herein, in new editions of the product.

******************************************************************************/




#include <jni.h>
#include <stdio.h>
#include <signal.h>
#include <unistd.h>

#ifdef SUN
#include <siginfo.h>
#endif

#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include "clientMod.h"
#include "com_elitecore_coreeap_util_sim_ulticom_data_AuthGateWayTripletRequestor.h"
#include "com_elitecore_coreeap_util_aka_ulticom_data_AuhenticationQuintupletRequester.h"
#include "com_elitecore_coreeap_util_sim_ulticom_UlticomCommunicator.h"

#define MAX_TRIPLETS 10
#define MSGSIZE 256
#define MAX_REQ 1024
#define ASYNC_POOLING_ITR 100
//TODO not the best way initially done for testing
// char msg1[MSGSIZE];
// char msg2[MSGSIZE];
// char msg3[MSGSIZE];
// char nativeRand[MAX_TRIPLETS][MSGSIZE];
// char nativeSres[MAX_TRIPLETS][MSGSIZE];
// char nativeKc[MAX_TRIPLETS][MSGSIZE];

// char nativeQRand[MSGSIZE];
// char nativeQAutn[MSGSIZE];
// char nativeQXres[MSGSIZE];
// char nativeQCk[MSGSIZE];
// char nativeQIk[MSGSIZE];

#define LOG_LEVEL_NONE 0
#define LOG_LEVEL_ERROR 1 
#define LOG_LEVEL_WARN 2
#define LOG_LEVEL_INFO 3
#define LOG_LEVEL_DEBUG 4
#define LOG_LEVEL_TRACE 5
#define LOG_LEVEL_ALL 6
#define DATETIMESIZE    64

int logLevel = LOG_LEVEL_ALL;
#define LOG_MSG(msgLevel, format, logMsg...) if (logLevel >= msgLevel) {char outstr[DATETIMESIZE]; datetimestamp(outstr);\
 fprintf(stdout, "%s [%s] ", outstr, __FILE__); fprintf(stdout, format, ## logMsg ); }
unsigned int modHandle = 0;

typedef struct tripletInfo {

	unsigned int nResult;
	int responseReceived;
	char SRES[MAX_TRIPLETS][MSGSIZE];
	char RAND[MAX_TRIPLETS][MSGSIZE];
	char KC[MAX_TRIPLETS][MSGSIZE];
	int tripletCount;
} tripletInfo ;

tripletInfo tripletInfoArray[MAX_REQ];

typedef struct quintetInfo {

	unsigned int nResult;
	int responseReceived;
	char AUTN[MSGSIZE];
	char RAND[MSGSIZE];
	char XRES[MSGSIZE];
	char IK[MSGSIZE];
	char CK[MSGSIZE];
	int quintetCount;
} quintetInfo;

quintetInfo quintetInfoArray[MAX_REQ];

unsigned int driverModHandles[MAX_CONNECTIONS];
// char *globalMsg=NULL;

//int numberOfTripletsReceived = 0;
//int iReponseReceived = 0 ;
unsigned int    testCase = 0;
unsigned short  altSend = 0;

static unsigned int pAvailableCaps = 0;

ulcm_mg_t_postSIMResults *pFnCallback = NULL;
ulcm_mg_t_postIMSIResults *pimsiCallback = NULL;
ulcm_mg_t_postAuthorizationResults *pAuthorizationCallback = NULL;
ulcm_mg_t_postAKAResults *pAKACallback = NULL;

void           *reqThread( void *temp );
//TODO not the best way initially done for testing
void            decodeIMSI_RAND( const unsigned char *IMSI, int len, int pos , unsigned long callbackCookie) ;
void            decodeIMSI_SRES( const unsigned char *IMSI, int len, int pos , unsigned long callbackCookie);
void            decodeIMSI_KC( const unsigned char *IMSI, int len, int pos , unsigned long callbackCookie);
void            decodeIMSI( const unsigned char *IMSI, int len );
void            postTriplets( unsigned long hCallbackCookie,
	       unsigned int nResult, /*const*/ ulcm_mg_triplet_t * pTripletBuffer, unsigned int tripletCount, ulcm_mg_quintuplet_t * pQuintetBuffer, unsigned int quintetCount, const char *pServiceProfile );
void            postIMSI( unsigned long hCallbackCookie, unsigned int nResult, const char *pszIMSI );
void            postAuthorization( unsigned long hCallbackCookie, unsigned int nResult, const char *pServiceProfile );
void           postQuintets( unsigned long hCallbackCookie,
	       unsigned int nResult, /*const*/ ulcm_mg_quintuplet_t * pQuintetBuffer, unsigned int quintetCount, ulcm_mg_triplet_t * pTripletBuffer, unsigned int nTripletCount, const char *pServiceProfile );
	       
void            decodeIMSI_QRAND( const unsigned char *IMSI, int len , unsigned long callbackCookie, quintetInfo *quintetBuffer);
void 		decodeIMSI_QXRES( const unsigned char *IMSI, int len, unsigned long callbackCookie, quintetInfo *quintetBuffer );
void 		decodeIMSI_QAUTN( const unsigned char *IMSI, int len , unsigned long callbackCookie, quintetInfo *quintetBuffer);
void 		decodeIMSI_QCK( const unsigned char *IMSI, int len , unsigned long callbackCookie, quintetInfo *quintetBuffer);
void 		decodeIMSI_QIK( const unsigned char *IMSI, int len , unsigned long callbackCookie, quintetInfo *quintetBuffer);
void 		logInJava(JNIEnv *env, jobject obj,jclass objectClass, jclass stringClass, jmethodID stringInit, char* nativeMessage);
void 		readQuintet( unsigned long hCallbackCookie,
			unsigned int nResult, /*const*/ ulcm_mg_quintuplet_t * pQuintupletBuffer, unsigned int nQuintupletCount, 
			ulcm_mg_triplet_t * pTripletBuffer, unsigned int nTripletCount, const char *pServiceProfile, int quintetCount );


/*************************************************************************

FUNCTION
	void sigintCatcher( int signum )

DESCRIPTION
        Handle Signal SIGINT.

INPUTS
        Arg: 	signum - Signal number 

OUTPUTS
        Return: None

FEND
*************************************************************************/
/*void
sigintCatcher( int signum )
{
#ifdef SUN
    siginfo_t       p;
    char            s[200];
#endif
    
    printf( "sigint catched:\n" );
    printf( "%d (%s)\n", signum, strsignal( signum ) );
#ifdef SUN
    psiginfo( &p, s );
    printf( "si_code : %d\n", p.si_code );
    printf( "si_pid : %u\n", p.si_pid );
    printf( "si_uid: %u\n", p.si_uid );
#endif

    ulcm_mg_t_ModuleTerminate( modHandle );
    exit( 0 );
  
}*/

unsigned int    iCallbackCookie = 1;
char            imsi[3][16];

int             countTriplets = 0;


/*************************************************************************

FUNCTION
	void *reqThread( void *temp )

DESCRIPTION
        It generates test cases.

INPUTS
        Arg:	temp : void pointer	

OUTPUTS
        Return: None

FEND
*************************************************************************/

void           *
reqThread( void *temp )
{
    int             iiRet = 0;
    unsigned int    hSIMRequest = 0;
    unsigned int    hPIRequest = 0;
    unsigned int    hAKARequest = 0;
    switch ( testCase )
    {
    case 0:
	printf( "============================================\n" );
	printf( "Scenario %d: requestTriplets.\n", testCase );
	printf( "============================================\n" );
	printf( "IMSI: %s , length: %d\n", imsi[0], (int) strlen( imsi[0] ) );
	if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_SIM ) )
	    iiRet = ulcm_mg_t_RequestSIMTriplets( modHandle, imsi[0], countTriplets, &hSIMRequest, pFnCallback, iCallbackCookie++, 0 );
	if ( iiRet != 0 )
	{
	    printf( "main() : Error in testCase %d, request returned 0x%x\n", testCase, iiRet );
	}


	//TODO shiv
	//testCase++;
	break;

    case 1:
	printf( "============================================\n" );
	printf( "Scenario %d: requestTriplets cancelTriplets.\n", testCase );
	printf( "============================================\n" );
	printf( "IMSI: %s , length: %d\n", imsi[0], (int) strlen( imsi[0] ) );
	if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_SIM ) )
	{
	    iiRet = ulcm_mg_t_RequestSIMTriplets( modHandle, imsi[0], countTriplets, &hSIMRequest, pFnCallback, iCallbackCookie++, 0 );
	    if ( iiRet != 0 )
	    {
	    	printf( "main() : Error in testCase %d, "
			"request returned 0x%x\n", testCase, iiRet );
	    }

	    iiRet = ulcm_mg_t_CancelSIMRequest(modHandle, 
			hSIMRequest, 0);
	    if (iiRet != 0 )
	    {
	    	printf( "main() : Error in testCase %d, "
			"request returned 0x%x\n", testCase, iiRet );
	    }
	}

	testCase++;
	break;

    case 2:
	
      	printf("============================================\n");
      	printf("Scenario %d: requestAuthorization.\n", testCase);
      	printf("============================================\n");
      	if (pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_AUTHORIZATION))
      	{
      		iiRet = ulcm_mg_t_RequestAuthorizationInfo( modHandle,
				imsi[0], &hPIRequest,
                                pAuthorizationCallback,
                                iCallbackCookie++,0);
      	}
      	testCase++;
      	break;

    case 3:
	printf( "============================================\n" );
	printf( "Scenario %d: requestAuthorization, cancelAuthorization.\n", testCase );
	printf( "============================================\n" );
	if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_AUTHORIZATION ) )
	{
	    iiRet = ulcm_mg_t_RequestAuthorizationInfo( modHandle, imsi[0], &hPIRequest, pAuthorizationCallback, iCallbackCookie++ ,0);
	    if ( iiRet != 0 )
	    {
		printf( "main() : Error in testCase %d, request returned 0x%x\n", testCase, iiRet );
	    }
	    iiRet = ulcm_mg_t_CancelAuthorizationRequest( modHandle, hPIRequest, 0);
	    if ( iiRet != 0 )
	    {
		printf( "main() : Error in testCase %d, request returned 0x%x\n", testCase, iiRet );
	    }

	}
	testCase++;
	break;
    case 4:
	printf( "============================================\n" );
	printf( "Scenario %d: requestAKAQuintets with no re-sync.\n", testCase );
	printf( "============================================\n" );
	if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_AKA ) )
	{
	    ulcm_mg_re_synchronisation_t re_synchronisationInfo;
	    re_synchronisationInfo.RAND[0] = '\0';
	    re_synchronisationInfo.AUTS[0] = '\0';
	    iiRet = ulcm_mg_t_RequestAKAQuintuplets( modHandle, 
		imsi[0], &re_synchronisationInfo,	/* re-sync */ 
		countTriplets,	/* quintet count */
		&hAKARequest, pAKACallback, iCallbackCookie++,0 );
	    if ( iiRet != 0 )
	    {
		printf( "main() : Error in testCase %d, request returned 0x%x\n", testCase, iiRet );
	    }

	}
	else
	{
	    printf( "AKA is not supported.\n" );
	}

	testCase++;
	break;
    case 5:
	printf( "============================================\n" );
	printf( "Scenario %d: requestAKAQuintets.\n", testCase );
	printf( "============================================\n" );
	if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_AKA ) )
	{
	    ulcm_mg_re_synchronisation_t re_synchronisationInfo;
	    snprintf( re_synchronisationInfo.RAND, sizeof( re_synchronisationInfo.RAND ), "12892233445566778899001122334455" );
	    snprintf( re_synchronisationInfo.AUTS, sizeof( re_synchronisationInfo.AUTS ), "60509682097439000000000000000011" );

	    printf( "re_synchronisationInfo.RAND: %s\n", re_synchronisationInfo.RAND );
	    printf( "re_synchronisationInfo.AUTS: %s\n", re_synchronisationInfo.AUTS );

	    iiRet = ulcm_mg_t_RequestAKAQuintuplets( modHandle, imsi[0], &re_synchronisationInfo,	/* re-sync */
			countTriplets,	/* quintet count */
		     &hAKARequest, pAKACallback, iCallbackCookie++, 0 );

	    if ( iiRet != 0 )
	    {
		printf( "main() : Error in testCase %d, request returned 0x%x\n", testCase, iiRet );
	    }

	}
	else
	{
	    printf( "AKA is not supported.\n" );
	}

	testCase++;
	
	break;
    case 6:
	printf( "============================================\n" );
	printf( "Scenario %d: requestAKAQuintets with no re-sync CancelAKARequest.\n", testCase );
	printf( "============================================\n" );
	if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_AKA ) )
	{
	    ulcm_mg_re_synchronisation_t re_synchronisationInfo;
	    re_synchronisationInfo.RAND[0] = '\0';
	    re_synchronisationInfo.AUTS[0] = '\0';
	    iiRet = ulcm_mg_t_RequestAKAQuintuplets( modHandle, 
		imsi[0], &re_synchronisationInfo,	/* re-sync */ 
		countTriplets,	/* quintet count */
		&hAKARequest, pAKACallback, iCallbackCookie++, 0 );
	    if ( iiRet != 0 )
	    {
		printf( "main() : Error in testCase %d, request returned 0x%x\n", testCase, iiRet );
	    }

	    iiRet = ulcm_mg_t_CancelAKARequest(modHandle,hAKARequest, 0);
	    if (iiRet != 0)
	    {
		printf( "main() : Error in testCase %d, request returned 0x%x\n", testCase, iiRet );

	    }

	}
	else
	{
	    printf( "AKA is not supported.\n" );
	}

	testCase++;
	break;
    case 7:
	printf( "============================================\n" );
	printf( "Scenario %d: requestAKAQuintets CancelAKARequest.\n", testCase );
	printf( "============================================\n" );
	if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_AKA ) )
	{
	    ulcm_mg_re_synchronisation_t re_synchronisationInfo;
	    snprintf( re_synchronisationInfo.RAND, sizeof( re_synchronisationInfo.RAND ), "12892233445566778899001122334455" );
	    snprintf( re_synchronisationInfo.AUTS, sizeof( re_synchronisationInfo.AUTS ), "60509682097439000000000000000011" );

	    printf( "re_synchronisationInfo.RAND: %s\n", re_synchronisationInfo.RAND );
	    printf( "re_synchronisationInfo.AUTS: %s\n", re_synchronisationInfo.AUTS );

	    iiRet = ulcm_mg_t_RequestAKAQuintuplets( modHandle, imsi[0], &re_synchronisationInfo,	/* re-sync */
			countTriplets,	/* quintet count */
		     &hAKARequest, pAKACallback, iCallbackCookie++, 0 );

	    if ( iiRet != 0 )
	    {
		printf( "main() : Error in testCase %d, request returned 0x%x\n", testCase, iiRet );
	    }

	    iiRet = ulcm_mg_t_CancelAKARequest(modHandle,hAKARequest, 0);
	    if (iiRet != 0)
	    {
		printf( "main() : Error in testCase %d, request returned 0x%x\n", testCase, iiRet );

	    }
	}
	else
	{
	    printf( "AKA is not supported.\n" );
	}
	testCase = 0;
	break;




/*
    case 1:  
      printf("============================================\n");
      printf("Scenario %d: requestIMSI, requestTriplets.\n", testCase);
      printf("============================================\n");
      if(altSend %2 != 0)
      {
         if (pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_IMSI))
            iiRet = ulcm_mg_t_RequestIMSI ( modHandle,
                                     msisdn,
                                     &hIMSIRequest,
                                     pimsiCallback,
                                     iCallbackCookie++ );
      altSend = 0;
      } else {
         if (pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_SIM))
            iiRet = ulcm_mg_t_RequestSIMTriplets( modHandle,imsi[0],
                                       5,
                                       &hSIMRequest,
                                       pFnCallback,
                                       iCallbackCookie++);
         altSend =1;
         testCase++;
      }
    break;
    case 2:
      printf("============================================\n");
      printf("Scenario %d: requestIMSI, cancelIMSI.\n", testCase);
      printf("============================================\n");
      if (pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_IMSI))
      {
      iiRet = ulcm_mg_t_RequestIMSI ( modHandle,
                                     msisdn,
                                     &hIMSIRequest,
                                     pimsiCallback,
                                     iCallbackCookie++ );
      iiRet = ulcm_mg_t_CancelIMSIRequest ( modHandle,
                                           hIMSIRequest);
      }
      testCase++;
    break;
    case 4:
      printf("============================================\n");
      printf("Scenario %d: requestIMSI, requestAuthorization.\n", testCase);
      printf("============================================\n");
      if(altSend %2 != 0)
      {
      if (pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_IMSI));

      iiRet = ulcm_mg_t_RequestIMSI ( modHandle,
                                     msisdn,
                                     &hIMSIRequest,
                                     pimsiCallback,
                                     iCallbackCookie++ );
        altSend = 0;
      } else {
      if (pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_AUTHORIZATION))
      iiRet = ulcm_mg_t_RequestAuthorizationInfo( modHandle,imsi[0],
                                                &hSIMRequest,
                                                pAuthorizationCallback,
                                                iCallbackCookie++);
      altSend = 1;
      testCase = 0;
      } 
    break;
*/

    default:
	printf( "Unknown Scenario...exiting...\n" );
	exit( 0 );
    }
    if ( iiRet != 0 )
    {
/*
      printf("main() : Error in testCase %d, request returned 0x%x\n",testCase, iiRet);
*/
    }
    else
    {
	sleep( 5 );
    }

    return NULL;
}

/*************************************************************************

FUNCTION
	void postIMSI( unsigned int hCallbackCookie, 
			unsigned int nResult, const char *pIMSI )
DESCRIPTION
        It is used to display IMSI.

INPUTS
        Arg:	hCallbackCookie : Callback cookie
		nResult - result
		pIMSI - pointer to IMSI
OUTPUTS
        Return: None

FEND
*************************************************************************/

void
postIMSI( unsigned long hCallbackCookie, unsigned int nResult, const char *pIMSI )
{
//    int             i = 0;
    LOG_MSG( LOG_LEVEL_INFO, "\nResult received for requestIMSI(), result is 0x%x,  \n", nResult );
    LOG_MSG( LOG_LEVEL_INFO, "IMSI is : %s \n ", pIMSI );
    /* assign the IMSI to be used in requestSIMTriplets()  */
    sprintf( imsi[0], "%s", pIMSI );
}

/*************************************************************************

FUNCTION
	void postAuthorization( unsigned int hCallbackCookie, 
		unsigned int nResult, const char *pServiceProfile )

DESCRIPTION
        It is used to display profile info.

INPUTS
        Arg:	hCallbackCookie : Callback cookie
		nResult - # of result
		pServiceProfile - pointer to profile info
OUTPUTS
        Return: None

FEND
*************************************************************************/

void
postAuthorization( unsigned long hCallbackCookie, unsigned int nResult, const char *pServiceProfile )
{
    LOG_MSG( LOG_LEVEL_INFO, "\nResult received for requestAuthorization(), result is 0x%x,  \n", nResult );
    LOG_MSG( LOG_LEVEL_INFO, "Service Profile : %s \n ", pServiceProfile );

}

/*************************************************************************

FUNCTION
	void postTriplets( unsigned int hCallbackCookie,
	      	unsigned int nResult, 
		const ulcm_mg_triplet_t * pTripletBuffer, 
		unsigned int tripletCount, 
		const char *pServiceProfile )

DESCRIPTION
        It is used to display triplets.

INPUTS
        Arg:	hCallbackCookie : Callback cookie
		nResult - # of result
		pTripletBuffer - pointer to Triplet buffer
		tripletCount - Triplets count
		pServiceProfile - pointer to profile info
OUTPUTS
        Return: None

FEND
*************************************************************************/

void
postTriplets( unsigned long hCallbackCookie,
	      unsigned int nResult, /*const*/ ulcm_mg_triplet_t * pTripletBuffer, unsigned int tripletCount, ulcm_mg_quintuplet_t * pQuintetBuffer, unsigned int quintetCount, const char *pServiceProfile )
{
	int numberOfTripletsReceived = 0;
	
    int             i = 0;
    
    LOG_MSG( LOG_LEVEL_INFO, "Result received for requestTriplets(), result=0x%x, Triplets=%d  \n", nResult, tripletCount );

    if ( pTripletBuffer != NULL ) {
	
		for ( i = 0; i < tripletCount && i<MAX_TRIPLETS ; i++ ) {
	
			decodeIMSI_RAND( pTripletBuffer->RAND, 16, i , hCallbackCookie);
			decodeIMSI_SRES( pTripletBuffer->SRES, 4, i , hCallbackCookie);
			decodeIMSI_KC( pTripletBuffer->Kc, 8, i , hCallbackCookie);
			pTripletBuffer++;
			numberOfTripletsReceived++;
			LOG_MSG( LOG_LEVEL_INFO, "RAND=0x%s, SRES=0x%s, KC=0x%s\n", 
				tripletInfoArray[hCallbackCookie].RAND[i], 
				tripletInfoArray[hCallbackCookie].SRES[i], 
				tripletInfoArray[hCallbackCookie].KC[i]);
		}
		tripletInfoArray[hCallbackCookie].tripletCount = numberOfTripletsReceived;
	}
	else if ( pQuintetBuffer != NULL) {
		LOG_MSG( LOG_LEVEL_INFO, "\nQuintet Received when asked for triplets.\n");
		readQuintet( hCallbackCookie, nResult, pQuintetBuffer, quintetCount, pTripletBuffer, tripletCount, pServiceProfile, quintetCount );		
		LOG_MSG(LOG_LEVEL_INFO, "\readQuintet excution complete.\n");
		
	} 
    else {
		LOG_MSG( LOG_LEVEL_WARN, "Triplet Buffer does not contain anything....\n" );
    }

	
	tripletInfoArray[hCallbackCookie].responseReceived = 1;
	tripletInfoArray[hCallbackCookie].nResult = nResult;
}

void
decodeIMSI_RAND( const unsigned char *IMSI, int len , int pos, unsigned long callbackCookie)
{
    char            indx, indx2;
    int             i = 0;

	char tmpMsg[MSGSIZE];

    //LOG_MSG( LOG_LEVEL_INFO, "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx );
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx2 );
	//sprintf(tmpMsg, "%s%x", msg1,indx );
	//sprintf(msg1, "%s%x", tmpMsg,indx2 );
	sprintf(tmpMsg, "%s%x", tripletInfoArray[callbackCookie].RAND[pos],indx );
	sprintf(tripletInfoArray[callbackCookie].RAND[pos], "%s%x", tmpMsg,indx2 );	
    }
}

void
decodeIMSI_SRES( const unsigned char *IMSI, int len, int pos ,  unsigned long callbackCookie)
{
    char            indx, indx2;
    int             i = 0;
	char tmpMsg[MSGSIZE];
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx );
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx2 );
	//sprintf(tmpMsg, "%s%x", msg2,indx );
	//sprintf(msg2, "%s%x", tmpMsg,indx2 );
	sprintf(tmpMsg, "%s%x", tripletInfoArray[callbackCookie].SRES[pos],indx );
	sprintf(tripletInfoArray[callbackCookie].SRES[pos], "%s%x", tmpMsg,indx2 );
	
    }
}

void
decodeIMSI_KC( const unsigned char *IMSI, int len, int pos ,  unsigned long callbackCookie )
{
    char            indx, indx2;
    int             i = 0;
	char tmpMsg[MSGSIZE];
    //LOG_MSG( LOG_LEVEL_INFO, "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx );
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx2 );
	//sprintf(tmpMsg, "%s%x", msg3,indx );
	//sprintf(msg3, "%s%x", tmpMsg,indx2 );
	sprintf(tmpMsg, "%s%x", tripletInfoArray[callbackCookie].KC[pos],indx );
	sprintf( tripletInfoArray[callbackCookie].KC[pos], "%s%x", tmpMsg,indx2 );	
    }
}

void
decodeIMSI( const unsigned char *IMSI, int len )
{
    char            indx, indx2;
    int             i = 0;
    //LOG_MSG(LOG_LEVEL_INFO,  "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	LOG_MSG( LOG_LEVEL_INFO, "%x", indx );
	LOG_MSG( LOG_LEVEL_INFO, "%x", indx2 );
    }
}

/*************************************************************************

FUNCTION

	void readQuintet( unsigned int hCallbackCookie,
	      	unsigned int nResult, 
		const ulcm_mg_quintuplet_t * pQuintupletBuffer, 
		unsigned int nQuintupletCount, 
		const char *pServiceProfile )

DESCRIPTION
        Used to read quintet when asked for triplet

INPUTS
        Arg:	hCallbackCookie : Callback cookie
		nResult - # of result
		pQuintupletBuffer - pointer to Quintuplets buffer
		nQuintupletCount - Triplets count
		pServiceProfile - pointer to profile info
OUTPUTS
        Return: None

FEND
*************************************************************************/

#define XRES_SIZE 8
#define CK_IK_SIZE 16
void
readQuintet( unsigned long hCallbackCookie,
	      unsigned int nResult, /*const*/ ulcm_mg_quintuplet_t * pQuintupletBuffer, unsigned int nQuintupletCount, ulcm_mg_triplet_t * pTripletBuffer, unsigned int nTripletCount, const char *pServiceProfile, int quintetCount )
{
	
    int             i = 0, j=0, k1=0, k2=0,k3=0,l=0;
	char xres1[XRES_SIZE], xres2[XRES_SIZE], xres3[XRES_SIZE], xres4[XRES_SIZE];
	char ck1[CK_IK_SIZE], ck2[CK_IK_SIZE], ik1[CK_IK_SIZE], ik2[CK_IK_SIZE],ch;

    quintetInfo quintetBuffer;
	
    LOG_MSG( LOG_LEVEL_INFO, "Result received for requestAKAQuintets(), result=0x%x, Quintets=%d\n", nResult, nQuintupletCount );
    // TODO Generate one triplet from one each quintet
	int numberOfTripletsReceived = 0;
	
	if (nQuintupletCount > MAX_TRIPLETS) {
		nQuintupletCount = MAX_TRIPLETS;
	}
	
    if ( pQuintupletBuffer != NULL ) {	
		for ( i = 0; i < nQuintupletCount; i++ ) {
			
			k1=0;
			k2=0;
			k3=0;
			l=0;
	memset(&quintetBuffer, '\0', sizeof(quintetInfo));
	memset(xres1, '\0', XRES_SIZE);
	memset(xres2, '\0', XRES_SIZE);
	memset(xres3, '\0', XRES_SIZE);
	memset(xres4, '\0', XRES_SIZE);
	memset(ck1, '\0', CK_IK_SIZE);
	memset(ck2, '\0', CK_IK_SIZE);
	memset(ik1, '\0', CK_IK_SIZE);
	memset(ik2, '\0', CK_IK_SIZE);
	
			decodeIMSI_QRAND( pQuintupletBuffer->RAND, 16 , hCallbackCookie, &quintetBuffer);
			decodeIMSI_QXRES( pQuintupletBuffer->XRES, strlen(pQuintupletBuffer->XRES) , hCallbackCookie, &quintetBuffer);
			decodeIMSI_QCK( pQuintupletBuffer->CK, 16 , hCallbackCookie, &quintetBuffer);
			decodeIMSI_QIK( pQuintupletBuffer->IK, 16 , hCallbackCookie, &quintetBuffer);
			decodeIMSI_QAUTN( pQuintupletBuffer->AUTN, 16 , hCallbackCookie, &quintetBuffer);
			LOG_MSG(LOG_LEVEL_INFO, "XRES=%s, CK=%s, IK=%s\n" , quintetBuffer.XRES, quintetBuffer.CK, quintetBuffer.IK);

			for ( j=0 ; j<32 ; j++){
				tripletInfoArray[hCallbackCookie].RAND[i][j] = quintetBuffer.RAND[l++];
			}

			LOG_MSG ( LOG_LEVEL_INFO, "RAND received is: %s \n ", tripletInfoArray[hCallbackCookie].RAND[i] );
			
			for ( j=strlen(quintetBuffer.XRES) ; j<32 ; j++){
				quintetBuffer.XRES[j] = '0';
			}
			for ( j=0; j<8 ; j++ ) {
				ch = quintetBuffer.XRES[k3++];
				if(ch >= 65 && ch <= 70){
					xres1[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					xres1[j] = ch - 87;
				}else
					xres1[j] = ch - 48;
			}
			for ( j=0 ; j<8 ; j++ ) {
				ch = quintetBuffer.XRES[k3++];
				if(ch >= 65 && ch <= 70){
					xres2[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					xres2[j] = ch - 87;
				}else
					xres2[j] = ch - 48;				
			}
			for ( j=0 ; j<8 ; j++ ) {
				ch =quintetBuffer.XRES[k3++];
				if(ch >= 65 && ch <= 70){
					xres3[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					xres3[j] = ch - 87;
				}else
					xres3[j] = ch - 48;
			}
			for ( j=0 ; j<8 ; j++ ) {
				ch = quintetBuffer.XRES[k3++];
				if(ch >= 65 && ch <= 70){
					xres4[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					xres4[j] = ch - 87;
				}else
					xres4[j] = ch - 48;
			}
			memset( tripletInfoArray[hCallbackCookie].SRES[i], 0, 256 );
			for ( j=0 ; j<8 ; j++ ) {						
				LOG_MSG( LOG_LEVEL_DEBUG, "Debug :[%x][%x][%x][%x] = %d\n",xres1[j],xres2[j],xres3[j],xres4[j],(((xres1[j] ^ xres2[j])^ xres3[j]) ^ xres4[j]));
				if((((xres1[j] ^ xres2[j])^ xres3[j]) ^ xres4[j]) <= 9)	
					tripletInfoArray[hCallbackCookie].SRES[i][j] = (((xres1[j] ^ xres2[j])^ xres3[j]) ^ xres4[j]) + '0';
				else
					tripletInfoArray[hCallbackCookie].SRES[i][j] = (((xres1[j] ^ xres2[j])^ xres3[j]) ^ xres4[j]) + 55;
			}
			LOG_MSG ( LOG_LEVEL_INFO, "SRES received is: %s \n ", tripletInfoArray[hCallbackCookie].SRES[i] );
			
			for ( j=0; j<16 ; j++ ) {				
				ch = quintetBuffer.CK[k1++];
				if(ch >= 65 && ch <= 70){
					ck1[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					ck1[j] = ch - 87;
				}else
					ck1[j] = ch - 48;
				
			}
			for ( j=0 ; j<16 ; j++ ) {				
				ch = quintetBuffer.CK[k1++];
				if(ch >= 65 && ch <= 70){
					ck2[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					ck2[j] = ch - 87;
				}else
					ck2[j] = ch - 48;				
			}
			
			for ( j=0; j<16 ; j++ ) {				
				ch = quintetBuffer.IK[k2++];
				if(ch >= 65 && ch <= 70){
					ik1[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					ik1[j] = ch - 87;
				}else
					ik1[j] = ch - 48;								
			}
			for ( j=0 ; j<16 ; j++ ) {
				ch =quintetBuffer.IK[k2++];
				if(ch >= 65 && ch <= 70){
					ik2[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					ik2[j] = ch - 87;
				}else
					ik2[j] = ch - 48;								
			}
			memset( tripletInfoArray[hCallbackCookie].KC[i], 0, 256 );
			for ( j=0 ; j<16 ; j++ ) {
				if((((ck1[j] ^ ck2[j]) ^ ik1[j]) ^ ik2[j]) <= 9)
					tripletInfoArray[hCallbackCookie].KC[i][j] = (((ck1[j] ^ ck2[j]) ^ ik1[j]) ^ ik2[j]) + '0';
				else
					tripletInfoArray[hCallbackCookie].KC[i][j] = (((ck1[j] ^ ck2[j]) ^ ik1[j]) ^ ik2[j]) + 55;
			}			
			LOG_MSG ( LOG_LEVEL_INFO, "KC received is: %s \n ", tripletInfoArray[hCallbackCookie].KC[i] );
			
			numberOfTripletsReceived++;
		
			pQuintupletBuffer++;

		}
			
			
		tripletInfoArray[hCallbackCookie].tripletCount = numberOfTripletsReceived;
				
    }
    else {
		LOG_MSG( LOG_LEVEL_WARN, "quintet Buffer does not contain anything....\n" );
    }
}

/*************************************************************************

FUNCTION

	void postQuintets( unsigned int hCallbackCookie,
	      	unsigned int nResult, 
		const ulcm_mg_quintuplet_t * pQuintupletBuffer, 
		unsigned int nQuintupletCount, 
		const char *pServiceProfile )

DESCRIPTION
        It is used to display quintets.

INPUTS
        Arg:	hCallbackCookie : Callback cookie
		nResult - # of result
		pQuintupletBuffer - pointer to Quintuplets buffer
		nQuintupletCount - Triplets count
		pServiceProfile - pointer to profile info
OUTPUTS
        Return: None

FEND
*************************************************************************/
void
postQuintets( unsigned long hCallbackCookie,
	      unsigned int nResult, /*const*/ ulcm_mg_quintuplet_t * pQuintupletBuffer, unsigned int nQuintupletCount, ulcm_mg_triplet_t * pTripletBuffer, unsigned int nTripletCount, const char *pServiceProfile )
{
    int             i = 0;
    quintetInfo *quintetBuffer;
	quintetBuffer = &quintetInfoArray[hCallbackCookie];
    LOG_MSG( LOG_LEVEL_INFO, "Result received for requestAKAQuintets(), result=0x%x, Quintets=%d  \n", nResult, nQuintupletCount );
    if ( pQuintupletBuffer != NULL ) {	
		for ( i = 0; i < nQuintupletCount; i++ ) {
			decodeIMSI_QRAND( pQuintupletBuffer->RAND, 16 , hCallbackCookie, quintetBuffer);
			decodeIMSI_QXRES( pQuintupletBuffer->XRES, strlen(pQuintupletBuffer->XRES) , hCallbackCookie, quintetBuffer);
			decodeIMSI_QCK( pQuintupletBuffer->CK, 16 , hCallbackCookie, quintetBuffer);
			decodeIMSI_QIK( pQuintupletBuffer->IK, 16 , hCallbackCookie, quintetBuffer);
			decodeIMSI_QAUTN( pQuintupletBuffer->AUTN, 16 , hCallbackCookie, quintetBuffer);

			LOG_MSG( LOG_LEVEL_INFO, "RAND=0x%s, XRES=0x%s, CK=0x%s, IK=0x%s, AUTN=0x%s,\n", 
			quintetBuffer->RAND, quintetBuffer->XRES, quintetBuffer->CK, quintetBuffer->IK, quintetBuffer->AUTN);
			
			pQuintupletBuffer++;			
		}
    }
    else {
		LOG_MSG( LOG_LEVEL_WARN, "quintet Buffer does not contain anything....\n" );
    }
    
	quintetInfoArray[hCallbackCookie].responseReceived = 1;
	quintetInfoArray[hCallbackCookie].nResult = nResult;
}


void decodeIMSI_QRAND( const unsigned char *IMSI, int len, unsigned long callbackCookie, quintetInfo *quintetBuffer)
{
    char            indx, indx2;
    int             i = 0;
    char tmpMsg[MSGSIZE];

    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx );
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx2 );
	sprintf(tmpMsg, "%s%x", quintetBuffer->RAND,indx );
	sprintf(quintetBuffer->RAND, "%s%x", tmpMsg,indx2 );	
    }
}

void decodeIMSI_QAUTN( const unsigned char *IMSI, int len, unsigned long callbackCookie, quintetInfo *quintetBuffer)
{
    char            indx, indx2;
    int             i = 0;
    char tmpMsg[MSGSIZE];

    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx );
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx2 );
	sprintf(tmpMsg, "%s%x", quintetBuffer->AUTN,indx );
	sprintf(quintetBuffer->AUTN, "%s%x", tmpMsg,indx2 );	
    }
}

void decodeIMSI_QXRES( const unsigned char *IMSI, int len, unsigned long callbackCookie, quintetInfo *quintetBuffer)
{
    char            indx, indx2;
    int             i = 0;
    char tmpMsg[MSGSIZE];

    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx );
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx2 );
	sprintf(tmpMsg, "%s%x", quintetBuffer->XRES,indx );
	sprintf(quintetBuffer->XRES, "%s%x", tmpMsg,indx2 );	
    }
	
}

void decodeIMSI_QCK( const unsigned char *IMSI, int len, unsigned long callbackCookie, quintetInfo *quintetBuffer)
{
    char            indx, indx2;
    int             i = 0;
    char tmpMsg[MSGSIZE];

    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx );
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx2 );
	sprintf(tmpMsg, "%s%x", quintetBuffer->CK,indx );
	sprintf(quintetBuffer->CK, "%s%x", tmpMsg,indx2 );	
    }
}

void decodeIMSI_QIK( const unsigned char *IMSI, int len, unsigned long callbackCookie, quintetInfo *quintetBuffer)
{
    char            indx, indx2;
    int             i = 0;
    char tmpMsg[MSGSIZE];

    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx );
	//LOG_MSG( LOG_LEVEL_INFO, "%x", indx2 );
	sprintf(tmpMsg, "%s%x", quintetBuffer->IK,indx );
	sprintf(quintetBuffer->IK, "%s%x", tmpMsg,indx2 );	
    }
}

JNIEXPORT jint JNICALL Java_com_elitecore_coreeap_util_sim_ulticom_UlticomCommunicator_initAuthGWConnection
(JNIEnv *env, jobject obj, jint javaLogLevel, jstring javaRemoteHost, jstring javaLocalHost, jint driverID) {
	logLevel = javaLogLevel;
	const char *nativeRemoteHostString = (*env)->GetStringUTFChars(env, javaRemoteHost, 0);
    const char *nativeLocalHostString = (*env)->GetStringUTFChars(env, javaLocalHost, 0);
	char error_msg[1024], log_msg[1024];
	int iRet = 0, len;
	int javaRetVal = 1;
	
	/* Get a reference to object class */
	jclass cls = (*env)->GetObjectClass(env, obj);
	if(cls == NULL ) {
      LOG_MSG(LOG_LEVEL_ERROR, "Object Class not found");
		sprintf(error_msg,"Object Class not found");
	  javaRetVal = 0;
		goto failure1;
	}

	jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
	if(Class_java_lang_String == NULL ){
	  LOG_MSG(LOG_LEVEL_ERROR, "String Class not found ");
		sprintf(error_msg,"java/lang/String Class not found");
	  javaRetVal = 0;
	  goto failure1;
	}
    	jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
	if(MID_String_init == NULL){
      printf("Method not fonud for String Class ");
		sprintf(error_msg,"java/lang/String<init>([B)V method not found");
	  javaRetVal = 0;
		goto failure1;
    }
	
  /*  signal(SIGINT,sigintCatcher);*/
    pFnCallback = ( ulcm_mg_t_postSIMResults * ) & postTriplets;
    pimsiCallback = ( ulcm_mg_t_postIMSIResults * ) & postIMSI;
    pAuthorizationCallback = ( ulcm_mg_t_postAuthorizationResults * ) & postAuthorization;
    pAKACallback = ( ulcm_mg_t_postAKAResults * ) & postQuintets;
  
	sprintf(log_msg,"Initializing with following configuration\n%s\n%s",nativeLocalHostString,nativeRemoteHostString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
	iRet = ulcm_mg_t_ModuleInit( 1, nativeRemoteHostString, nativeLocalHostString, &pAvailableCaps, &modHandle, driverID );
	driverModHandles[driverID] = modHandle;
		
    LOG_MSG( LOG_LEVEL_TRACE, " Library capabilities bitmask: %x\n", pAvailableCaps );
    if ( iRet != 0 ) {
		LOG_MSG( LOG_LEVEL_ERROR, "main(): Error in moduleInit() iRet = %d\n", iRet );
		sprintf(error_msg, "requestTriplets(): Error in moduleInit() iRet = %d\n", iRet );
		goto failure1;
    }
	fflush(stdout);
	goto destroy;
	
	failure1:	
	LOG_MSG(LOG_LEVEL_ERROR, "Failure happen !!, Reason : %s\n", error_msg);	
	jstring javaErrorMsg;	
	
	len = strlen(error_msg);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
		goto destroy;
	}
	
	jbyteArray bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
		javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);	
		(*env)->DeleteLocalRef(env,bytes);
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "Could Not Call Failure Event from native String\n");
		goto destroy;
	}
	
    jmethodID newmid = (*env)->GetMethodID(env, cls, "onFailure", "(Ljava/lang/String;)V");
    if (newmid == NULL) {
    	LOG_MSG( LOG_LEVEL_ERROR, "onFailure(Ljava/lang/String;)V method not found\n");
        goto destroy;
    }
	(*env)->CallObjectMethod(env, obj, newmid,javaErrorMsg);
	fflush(stdout);
    destroy:
	(*env)->ReleaseStringUTFChars(env, javaRemoteHost, nativeRemoteHostString);
	(*env)->ReleaseStringUTFChars(env, javaLocalHost, nativeLocalHostString);
	fflush(stdout);
	return javaRetVal;
}

/*************************************************************************
FUNCTION
	void startTestCase(JNIEnv * env, jobject obj, jstring imsi, jint noOfTriplets)

DESCRIPTION
        Start the Test Case

INPUTS
        Arg: 	jstring - IMSI
                jint    - number of tripletes 

OUTPUTS
        Return: None

**************************************************************************/
JNIEXPORT jint JNICALL Java_com_elitecore_coreeap_util_sim_ulticom_data_AuthGateWayTripletRequestor_requestTriplets  
(JNIEnv * env, jobject obj,jstring javaImsi, jint noOfTriplets, jlong javaRequestTimeout, jint requestorID, int driverID){

	
    const char *nativeImsiString = (*env)->GetStringUTFChars(env, javaImsi, 0);
    char error_msg[1024], log_msg[1024];
	
	 // Conversion from miliseconds to microseconds
	unsigned long requestTimeout = javaRequestTimeout * 1000; 
	
	// requestTimeout is divided by ASYNC_POOLING_ITR because AAA will wait for response in ASYNC_POOLING_ITR equal slot of configured timeout. 
	// At the end of each slot it will check weather response is received or not
	unsigned long timeoutSlot = requestTimeout / ASYNC_POOLING_ITR;
	
    countTriplets = noOfTriplets;
    int waitLoopCount = 0;
	if ( timeoutSlot < 1000) timeoutSlot  = 1000;
	/* Get a reference to objÆs class */
    jclass cls = (*env)->GetObjectClass(env, obj);
	if(cls == NULL ) {
		LOG_MSG( LOG_LEVEL_ERROR, "Object Class not found\n");
		sprintf(error_msg,"Object Class not found");
		goto failure1;
	}
	jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
	if(Class_java_lang_String == NULL ){
		LOG_MSG( LOG_LEVEL_ERROR, "String Class not found \n");
		sprintf(error_msg,"java/lang/String Class not found");
		goto destroy;
	}
	
    jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
	if(MID_String_init == NULL){
		LOG_MSG( LOG_LEVEL_ERROR, "Method not fonud for String Class \n");
		sprintf(error_msg,"java/lang/String<init>([B)V method not found");
		goto destroy;
	}
    
	if (requestorID >= MAX_REQ ){
		sprintf(error_msg,"Requestor ID exceeded Maximum permissible value: %d Requestor ID: %d", MAX_REQ, requestorID);
		goto failure1;
	}
       
    LOG_MSG( LOG_LEVEL_INFO, "Request Triplets for  IMSI: %s   Length: %d No Of Triplets: %d\n", nativeImsiString,(int) strlen( nativeImsiString ),countTriplets);
    
    
    
    
	int             iiRet = 0;
    unsigned int    hSIMRequest = 0;
	 int ctr = 0;
	
	for ( ctr=0 ; ctr<MAX_TRIPLETS ; ctr++) { 
		memset (tripletInfoArray[requestorID].SRES[ctr], '\0', MSGSIZE);
		memset (tripletInfoArray[requestorID].RAND[ctr], '\0', MSGSIZE);
		memset (tripletInfoArray[requestorID].KC[ctr], '\0', MSGSIZE);
	}
	tripletInfoArray[requestorID].tripletCount = 0;	
	tripletInfoArray[requestorID].responseReceived = 0;
	tripletInfoArray[requestorID].nResult = ULCM_G_GATEWAY_ERROR;
	
	fflush(stdout);
	if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_SIM ) )
		iiRet = ulcm_mg_t_RequestSIMTriplets( driverModHandles[driverID], nativeImsiString, countTriplets, &hSIMRequest, pFnCallback, requestorID, driverID );
	
	if ( iiRet != 0 ) {
		LOG_MSG( LOG_LEVEL_WARN, "AuthGateWayTripletRequestor_requestTriplets(): Error 0x%x(%d) for IMSI: %s\n", iiRet, iiRet, nativeImsiString );
		sprintf(error_msg,"AuthGateWayTripletRequestor_requestTriplets(): Error 0x%x(%d) for IMSI: %s\n", iiRet, iiRet, nativeImsiString);
		tripletInfoArray[requestorID].nResult = iiRet;
		goto failure1;
	} else {
		LOG_MSG( LOG_LEVEL_INFO, "AuthGateWayTripletRequestor_requestTriplets() success for %s\n", nativeImsiString);    
		sprintf(log_msg, "AuthGateWayTripletRequestor_requestTriplets() success for %s\n", nativeImsiString);    
		logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
	}
	fflush(stdout);
	
	waitLoopCount = 0;
    while( (waitLoopCount < ASYNC_POOLING_ITR) && (tripletInfoArray[requestorID].responseReceived == 0)){
	  LOG_MSG(LOG_LEVEL_TRACE, "AuthGateWayTripletRequestor_requestTriplets(): Iteration: %d for IMSI %s\n", waitLoopCount, nativeImsiString);
      usleep(timeoutSlot); // sleep before we check status of iReponseReceived flag 
	  waitLoopCount++;
    }
		
    sprintf(log_msg, "AuthGateWayTripletRequestor_requestTriplets(): Waited: %ld miliseconds for IMSI %s\n",((waitLoopCount) * (timeoutSlot/1000)), nativeImsiString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg); 
	fflush(stdout);
	if(tripletInfoArray[requestorID].responseReceived == 0){
		ulcm_mg_t_CancelSIMRequest( driverModHandles[driverID], hSIMRequest, driverID );
		LOG_MSG(LOG_LEVEL_WARN, "AuthGateWayTripletRequestor_requestTriplets() Timeout!!! No Response received from the MAP Gateway for IMSI: %s\n", nativeImsiString);
		sprintf(error_msg,"AuthGateWayTripletRequestor_requestTriplets() Timeout!!! No Response received from the MAP Gateway for IMSI %s", nativeImsiString);
		tripletInfoArray[requestorID].nResult = EC_TIMEOUT;
		goto timeout;
	}
	
	if(tripletInfoArray[requestorID].responseReceived > 0){
		if(tripletInfoArray[requestorID].tripletCount < countTriplets){	
			ulcm_mg_t_CancelSIMRequest( driverModHandles[driverID], hSIMRequest, driverID );		
			//LOG_MSG(LOG_LEVEL_WARN, "Could not locate %d Triplets from the Auth Gateway. Received Triplet: %d\n",countTriplets, tripletInfoArray[requestorID].tripletCount);
			//sprintf(error_msg,"Could not locate %d Triplets from the Auth Gateway. Received Triplet: %d",countTriplets, tripletInfoArray[requestorID].tripletCount);
			//goto failure1;			
		}
	}
  	int len;
	jbyteArray bytes=0;
	
	int tripletPos = 0;	

 /*Writing into JAVA Log file*/
    //logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,globalMsg);
	while(tripletPos < tripletInfoArray[requestorID].tripletCount){

	/*converting RAND string from msg1*/
		jstring javaRAND;
	
		if((*env)->EnsureLocalCapacity(env,2) < 0){
			LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
			sprintf(error_msg,"Out of Memory in NATIVE");
			goto failure1;
		}
		len = strlen(tripletInfoArray[requestorID].RAND[tripletPos]);
		bytes = (*env)->NewByteArray(env,len);
		if(bytes != NULL){
			(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)tripletInfoArray[requestorID].RAND[tripletPos]);
			javaRAND = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
			(*env)->DeleteLocalRef(env,bytes);		
		}else{
			LOG_MSG(LOG_LEVEL_ERROR, "Could Not generate RAND from native String\n");
			goto failure1;
		}			
	
	/*converting SRES string from msg2*/
		jstring javaSRES;
		
		if((*env)->EnsureLocalCapacity(env,2) < 0){
			LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
			sprintf(error_msg,"Out of Memory in NATIVE");
			goto failure1;
		}
		len = strlen(tripletInfoArray[requestorID].SRES[tripletPos]);
		bytes = (*env)->NewByteArray(env,len);
		if(bytes != NULL){
			(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)tripletInfoArray[requestorID].SRES[tripletPos]);
			javaSRES = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
			(*env)->DeleteLocalRef(env,bytes);		
		}else{
			LOG_MSG(LOG_LEVEL_ERROR, "Could Not generate SRES from native String\n");
			goto failure1;
		}
	
	/*converting KC string from msg3*/
		jstring javaKC;	
		if((*env)->EnsureLocalCapacity(env,2) < 0){
			LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
			sprintf(error_msg,"Out of Memory in NATIVE");
			goto failure1;
		}
		len = strlen(tripletInfoArray[requestorID].KC[tripletPos]);
		bytes = (*env)->NewByteArray(env,len);
		if(bytes != NULL){
			(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)tripletInfoArray[requestorID].KC[tripletPos]);
			javaKC = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
			(*env)->DeleteLocalRef(env,bytes);		
		}else{
			LOG_MSG(LOG_LEVEL_ERROR, "Could Not generate KC from native String\n");
			sprintf(error_msg,"Could Not generate KC from native String");
			goto failure1;
		}
    
	    jmethodID mid = (*env)->GetMethodID(env, cls, "addTriplets", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	    if (mid == NULL) {
	    	sprintf(error_msg,"addTriplets(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V method not found");
	        goto failure1;
	    }	
	    (*env)->CallObjectMethod(env, obj, mid, javaRAND, javaSRES, javaKC);
	    
	    /********LOOP END*************/
	    tripletPos++;
	}
	    
    /*Calling Success Method*/
    jmethodID successmid = (*env)->GetMethodID(env, cls, "onSuccess", "()V");
    if (successmid == NULL) {
    	LOG_MSG(LOG_LEVEL_ERROR, "onSuccess()V method not found\n");
        goto destroy;
    }
    (*env)->CallObjectMethod(env, obj, successmid);
    fflush(stdout);
    goto destroy;
   
	failure1:	
	LOG_MSG(LOG_LEVEL_WARN, "Failure happen !!, Reason : %s\n", error_msg);	
	jstring javaErrorMsg;	
	
	len = strlen(error_msg);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
		goto destroy;
	}
	bytes = (*env)->NewByteArray(env,len);
	
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
		javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
		(*env)->DeleteLocalRef(env,bytes);
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "Could Not Call Failure Event from native String\n");
		goto destroy;
	}
	
    jmethodID newmid = (*env)->GetMethodID(env, cls, "onFailure", "(Ljava/lang/String;)V");
    if (newmid == NULL) {
    	LOG_MSG(LOG_LEVEL_ERROR, "onFailure(Ljava/lang/String;)V method not found\n");
        goto destroy;
    }
	(*env)->CallObjectMethod(env, obj, newmid,javaErrorMsg);

	fflush(stdout);
	goto destroy;

	timeout:
		javaErrorMsg;	
	
		len = strlen(error_msg);
		if((*env)->EnsureLocalCapacity(env,len) < 0){
			LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
			goto destroy;
		}
		bytes = (*env)->NewByteArray(env,len);
	
		if(bytes != NULL){
			(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
			javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
			(*env)->DeleteLocalRef(env,bytes);
		}else{
			LOG_MSG(LOG_LEVEL_ERROR, "Could Not Call Timeout Event from native\n");
			goto destroy;
		}
	
		newmid = (*env)->GetMethodID(env, cls, "onTimeout", "()V");
		if (newmid == NULL) {
			LOG_MSG(LOG_LEVEL_ERROR, "onTimeout()V method not found\n");
			goto destroy;
		}
		(*env)->CallObjectMethod(env, obj, newmid,javaErrorMsg);

		fflush(stdout);
    destroy:
	// printf("freeing up globalmsg %p \n", globalMsg);
	// free (	globalMsg );
	// // globalMsg = NULL;
	(*env)->ReleaseStringUTFChars(env, javaImsi, nativeImsiString);  
	fflush(stdout);	
	//jint javaResultCode = tripletInfoArray[requestorID].nResult;
	//return javaResultCode;
	return tripletInfoArray[requestorID].nResult;
}

JNIEXPORT jint JNICALL Java_com_elitecore_coreeap_util_aka_ulticom_data_AuhenticationQuintupletRequester_requestQuintuplet
(JNIEnv * env, jobject obj,jstring javaImsi, jlong javaRequestTimeout, jint requestorID, int driverID){        
   	const char *nativeImsiString = (*env)->GetStringUTFChars(env, javaImsi, 0);
  	char  error_msg[1024];
  	char  log_msg[1024];
    int waitLoopCount = 0, countTriplets = 1;
   		
	memset (quintetInfoArray[requestorID].RAND ,'\0', MSGSIZE);
	memset (quintetInfoArray[requestorID].AUTN ,'\0', MSGSIZE);
	memset (quintetInfoArray[requestorID].XRES ,'\0', MSGSIZE);
	memset (quintetInfoArray[requestorID].CK ,'\0', MSGSIZE);
	memset (quintetInfoArray[requestorID].IK ,'\0', MSGSIZE);
	quintetInfoArray[requestorID].responseReceived = 0;
	quintetInfoArray[requestorID].quintetCount = 0;
	quintetInfoArray[requestorID].nResult = ULCM_G_GATEWAY_ERROR;
   	
	// Conversion from miliseconds to microseconds
	unsigned long requestTimeout = javaRequestTimeout * 1000; 
	
	// requestTimeout is divided by ASYNC_POOLING_ITR because AAA will wait for response in ASYNC_POOLING_ITR equal slot of configured timeout. 
	// At the end of each slot it will check weather response is received or not
	unsigned long timeoutSlot = requestTimeout / ASYNC_POOLING_ITR;
	if ( timeoutSlot < 1000) timeoutSlot  = 1000;   			
    /* Get a reference to object class */
    jclass cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
      LOG_MSG(LOG_LEVEL_ERROR, "Object Class not found\n");
      sprintf(error_msg,"Object Class not found");
      goto failure1;
    }

   	jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
	if(Class_java_lang_String == NULL ){
		LOG_MSG(LOG_LEVEL_ERROR, "String Class not found \n");
		sprintf(error_msg,"java/lang/String Class not found");
		goto destroy;
	}
	
    jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
	if(MID_String_init == NULL){
		LOG_MSG(LOG_LEVEL_ERROR, "Method not fonud for String Class \n");
		sprintf(error_msg,"java/lang/String<init>([B)V method not found");
		goto destroy;
	}
	
	if (requestorID >= MAX_REQ ){
		sprintf(error_msg,"Requestor ID exceeded Maximum permissible value: %d RequestorID: %d", MAX_REQ, requestorID);
		goto failure1;
	}
    
        /*Writing into JAVA Log file*/
    sprintf(log_msg,"Request QuinTuplet for IMSI : %s",nativeImsiString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);

	
	LOG_MSG( LOG_LEVEL_INFO, "Request QuinTuplet for IMSI: %s\n", nativeImsiString );
	

	
 	int             iiRet = 0;
  	unsigned int    hAKARequest = 0;
	
	if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_AKA ) )
	{
		ulcm_mg_re_synchronisation_t re_synchronisationInfo;
	    re_synchronisationInfo.RAND[0] = '\0';
	    re_synchronisationInfo.AUTS[0] = '\0';
	    iiRet = ulcm_mg_t_RequestAKAQuintuplets( driverModHandles[driverID], 
			nativeImsiString, &re_synchronisationInfo,	/* re-sync */ 
			countTriplets,	/* quintet count */
			&hAKARequest, pAKACallback, requestorID, driverID );
	    if ( iiRet != 0 ) {
			LOG_MSG( LOG_LEVEL_ERROR, "AuhenticationQuintupletRequester_requestQuintuplet(): Error 0x%x(%d) for IMSI: %s\n", iiRet, iiRet, nativeImsiString );
			sprintf(error_msg,"AuhenticationQuintupletRequester_requestQuintuplet(): Error 0x%x(%d) for IMSI: %s\n", iiRet, iiRet, nativeImsiString);
			quintetInfoArray[requestorID].nResult = iiRet;
			goto failure1;
		} else {
			LOG_MSG(LOG_LEVEL_INFO, "AuhenticationQuintupletRequester_requestQuintuplet() success for %s\n", nativeImsiString);    
			sprintf(log_msg, "AuhenticationQuintupletRequester_requestQuintuplet() success for %s\n", nativeImsiString);    
			logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
		}
	}
	else
	{
	    LOG_MSG( LOG_LEVEL_ERROR, "AKA is not supported.\n" );
	}
       
	fflush ( stdout );
	waitLoopCount = 0;
    while( (waitLoopCount < ASYNC_POOLING_ITR) && (quintetInfoArray[requestorID].responseReceived == 0)){		 
      usleep(timeoutSlot); // sleep before we check status of ReponseReceived flag  
	  waitLoopCount++;
    }
	
	LOG_MSG(LOG_LEVEL_TRACE, "AuhenticationQuintupletRequester_requestQuintuplet(): Waited: %ld miliseconds for IMSI %s\n",((waitLoopCount)* (timeoutSlot/1000)), nativeImsiString);
    sprintf(log_msg, "AuhenticationQuintupletRequester_requestQuintuplet(): Waited: %ld miliseconds for IMSI %s\n",((waitLoopCount) * (timeoutSlot/1000)), nativeImsiString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);

	if(quintetInfoArray[requestorID].responseReceived == 0){
		ulcm_mg_t_CancelSIMRequest(driverModHandles[driverID], hAKARequest, driverID);
		LOG_MSG(LOG_LEVEL_WARN, "AuhenticationQuintupletRequester_requestQuintuplet() Timeout!!! No Response received from the MAP Gateway for IMSI %s\n", nativeImsiString );
		sprintf(error_msg,"AuhenticationQuintupletRequester_requestQuintuplet() Timeout!!! No Response received from the MAP Gateway for IMSI %s\n", nativeImsiString);
		quintetInfoArray[requestorID].nResult = EC_TIMEOUT;
		goto timeout;
	}
	fflush ( stdout );
	int len;
	jbyteArray bytes=0;
		/*converting RAND string from nativeQRand*/
	jstring javaQRAND;
	len = strlen(quintetInfoArray[requestorID].RAND);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
		sprintf(error_msg,"Out of Memory in NATIVE");
		goto failure1;
	}
	
	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)quintetInfoArray[requestorID].RAND);
		javaQRAND = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
		(*env)->DeleteLocalRef(env,bytes);		
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "Could Not generate RAND from native String\n");
		goto failure1;
	}			
	
	/*converting javaQAutn string from nativeQAutn*/
	jstring javaQAutn;
	len = strlen(quintetInfoArray[requestorID].AUTN);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
		sprintf(error_msg,"Out of Memory in NATIVE");
		goto failure1;
	}
	
	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)quintetInfoArray[requestorID].AUTN);
		javaQAutn = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
		(*env)->DeleteLocalRef(env,bytes);		
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "Could Not generate javaQAutn from native String\n");
		goto failure1;
	}			

	/*converting javaQXres string from nativeQXres*/
	jstring javaQXres;
	len = strlen(quintetInfoArray[requestorID].XRES);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
		sprintf(error_msg,"Out of Memory in NATIVE");
		goto failure1;
	}
	
	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)quintetInfoArray[requestorID].XRES);
		javaQXres = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
		(*env)->DeleteLocalRef(env,bytes);		
	}else{
		LOG_MSG( LOG_LEVEL_ERROR, "Could Not generate javaQXres from native String\n");
		goto failure1;
	}	

	/*converting javaQCk string from nativeQCk*/
	jstring javaQCk;
	len = strlen(quintetInfoArray[requestorID].CK);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG( LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
		sprintf(error_msg,"Out of Memory in NATIVE");
		goto failure1;
	}
	
	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)quintetInfoArray[requestorID].CK);
		javaQCk = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
		(*env)->DeleteLocalRef(env,bytes);		
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "Could Not generate javaQCk from native String\n");
		goto failure1;
	}	
	
	/*converting javaQIk string from nativeQIk*/
	jstring javaQIk;
	len = strlen(quintetInfoArray[requestorID].IK);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
		sprintf(error_msg,"Out of Memory in NATIVE");
		goto failure1;
	}
	
	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)quintetInfoArray[requestorID].IK);
		javaQIk = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
		(*env)->DeleteLocalRef(env,bytes);		
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "Could Not generate javaQIk from native String\n");
		goto failure1;
	}	

   
    jmethodID mid = (*env)->GetMethodID(env, cls, "addQuintuplet", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	if (mid == NULL) {
	     sprintf(error_msg,"addQuintuplet(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V method not found");
	     goto failure1;
	}	
	(*env)->CallObjectMethod(env, obj, mid, javaQRAND, javaQAutn, javaQXres, javaQCk, javaQIk);

   	/*Calling Success Method*/
   	jmethodID successmid = (*env)->GetMethodID(env, cls, "onSuccess", "()V");
   	if (successmid == NULL) {
   		LOG_MSG( LOG_LEVEL_ERROR, "onSuccess()V method not found\n");
        goto destroy;
   	}
   	(*env)->CallObjectMethod(env, obj, successmid);
	fflush ( stdout );
    goto destroy;
    
   	failure1:	
	LOG_MSG(LOG_LEVEL_WARN, "Failure happen !!, Reason : %s\n", error_msg);	
	jstring javaErrorMsg;	
	len = strlen(error_msg);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
		goto destroy;
	}
	
	
	bytes = (*env)->NewByteArray(env,len);
	
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
		javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
		(*env)->DeleteLocalRef(env,bytes);
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "Could Not Call Failure Event from native String\n");
		goto destroy;
	}

	jmethodID newmid = (*env)->GetMethodID(env, cls, "onFailure", "(Ljava/lang/String;)V");
   	if (newmid == NULL) {
   		LOG_MSG( LOG_LEVEL_ERROR, "onFailure(Ljava/lang/String;)V method not found\n");
       	goto destroy;
   	}
   	(*env)->CallObjectMethod(env, obj, newmid,javaErrorMsg);
	fflush ( stdout );
	goto destroy;
	
	timeout:
		javaErrorMsg;	
	
		len = strlen(error_msg);
		if((*env)->EnsureLocalCapacity(env,len) < 0){
			LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
			goto destroy;
		}
		bytes = (*env)->NewByteArray(env,len);
	
		if(bytes != NULL){
			(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
			javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
			(*env)->DeleteLocalRef(env,bytes);
		}else{
			LOG_MSG(LOG_LEVEL_ERROR, "Could Not Call Timeout Event from native\n");
			goto destroy;
		}
	
		newmid = (*env)->GetMethodID(env, cls, "onTimeout", "()V");
		if (newmid == NULL) {
			LOG_MSG(LOG_LEVEL_ERROR, "onFailure(Ljava/lang/String;)V method not found\n");
			goto destroy;
		}
		(*env)->CallObjectMethod(env, obj, newmid,javaErrorMsg);

		fflush(stdout);	

   	destroy:
	// printf("freeing up globalmsg %p\n", globalMsg);
	// free (	globalMsg );
	// globalMsg = NULL;
	(*env)->ReleaseStringUTFChars(env, javaImsi, nativeImsiString);	
	fflush ( stdout );
	return quintetInfoArray[requestorID].nResult;
}


JNIEXPORT void JNICALL Java_com_elitecore_coreeap_util_sim_ulticom_UlticomCommunicator_terminateAuthGWConnection
(JNIEnv *env, jobject obj, jstring javaRemoteHost, jstring javaLocalHost, jint driverID) {
	const char *nativeRemoteHostString = (*env)->GetStringUTFChars(env, javaRemoteHost, 0);
    const char *nativeLocalHostString = (*env)->GetStringUTFChars(env, javaLocalHost, 0);
    char  error_msg[1024], log_msg[1024];

    /* Get a reference to object class */
    jclass cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
      LOG_MSG( LOG_LEVEL_ERROR, "Object Class not found\n");
      sprintf(error_msg,"Object Class not found");
      goto failure1;
    }

    jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
    if(Class_java_lang_String == NULL ) {
      LOG_MSG( LOG_LEVEL_ERROR, "String Class not found \n");
      sprintf(error_msg,"java/lang/String Class not found");
      goto destroy;
    }
    jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
    if(MID_String_init == NULL) {
      LOG_MSG(LOG_LEVEL_ERROR, "Method not fonud for String Class \n");
      sprintf(error_msg,"java/lang/String<init>([B)V method not found");
      goto destroy;
    }
    
    /*Writing into JAVA Log file*/
    sprintf(log_msg,"Terminate AuthG/W communication with following configuration\n%s\n%s",nativeLocalHostString,nativeRemoteHostString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);    

	ulcm_mg_t_ModuleTerminate( driverModHandles[driverID], driverID );

	goto destroy;

    failure1:	
    LOG_MSG(LOG_LEVEL_WARN, "Failure happen !!, Reason : %s\n", error_msg);	
    jstring javaErrorMsg;

    if((*env)->EnsureLocalCapacity(env,2) < 0) {
      LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
      goto destroy;
    }

	int len = strlen(error_msg);

    jbyteArray bytes = (*env)->NewByteArray(env,len);
    if(bytes != NULL) {
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
      javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);
    } else {
      LOG_MSG(LOG_LEVEL_ERROR, "Could Not Call Failure Event from native String\n");
      goto destroy;
    }
    
    jmethodID newmid = (*env)->GetMethodID(env, cls, "onFailure", "(Ljava/lang/String;)V");
    if (newmid == NULL) {
      LOG_MSG(LOG_LEVEL_ERROR, "onFailure(Ljava/lang/String;)V method not found\n");
      goto destroy;
    }
    (*env)->CallObjectMethod(env, obj, newmid,javaErrorMsg);
    
    destroy:    
    (*env)->ReleaseStringUTFChars(env, javaRemoteHost, nativeRemoteHostString);
    (*env)->ReleaseStringUTFChars(env, javaLocalHost, nativeLocalHostString);
}



JNIEXPORT void JNICALL Java_com_elitecore_coreeap_util_sim_ulticom_UlticomCommunicator_shutdownSctpAuthConnection
  (JNIEnv *env, jobject jobj, jint driverID) {
  
  shutdownSctpAuth(driverID);
  
  }
  
/*************************************************************************

FUNCTION
	void logInJava(JNIEnv *env, jobject obj, char *nativeMessage)

DESCRIPTION
        This function log into JAVA logging file

INPUTS
        Arg:	
OUTPUTS
        Return: 

FEND
*************************************************************************/
void logInJava(JNIEnv *env, jobject obj,jclass cls, jclass Class_java_lang_String, jmethodID MID_String_init, char* nativeMessage){

    int len;
    jbyteArray bytes=0;
    jstring javaLogMsg;	
    
    if((*env)->EnsureLocalCapacity(env,2) < 0){
      LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
      return;
    }
    
    len = strlen(nativeMessage);
    
    bytes = (*env)->NewByteArray(env,len);
    
    if(bytes != NULL){
      
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)nativeMessage);
      
      javaLogMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      
      (*env)->DeleteLocalRef(env,bytes);
      
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "Could Not Call Failure Event from native String\n");
      return;
    }
    
    jmethodID newmid = (*env)->GetMethodID(env, cls, "log", "(Ljava/lang/String;)V");
    
    
    if (newmid == NULL) {
      LOG_MSG(LOG_LEVEL_ERROR, "log(Ljava/lang/String;)V method not found\n");
      return;
    }
    
    (*env)->CallObjectMethod(env, obj, newmid,javaLogMsg);
    

}
