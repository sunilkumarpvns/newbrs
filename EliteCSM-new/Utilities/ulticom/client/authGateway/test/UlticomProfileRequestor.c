
#include <stdio.h>
#include <signal.h>

#ifdef SUN
#include <siginfo.h>
#endif

#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include "clientMod.h"

#define MSGSIZE 256
char msg1[MSGSIZE];
char msg2[MSGSIZE];
char msg3[MSGSIZE];
char nativeRand[5][MSGSIZE];
char nativeSres[5][MSGSIZE];
char nativeKc[5][MSGSIZE];

char nativeQRand[MSGSIZE];
char nativeQAutn[MSGSIZE];
char nativeQXres[MSGSIZE];
char nativeQCk[MSGSIZE];
char nativeQIk[MSGSIZE];

char *globalMsg;

int numberOfTripletsReceived = 0;
int iReponseReceived = 0 ;

unsigned short  altSend = 0;
unsigned int    modHandle = 0;
static unsigned int pAvailableCaps = 0;

ulcm_mg_t_postSIMResults *pFnCallback = NULL;
ulcm_mg_t_postIMSIResults *pimsiCallback = NULL;
ulcm_mg_t_postAuthorizationResults *pAuthorizationCallback = NULL;
ulcm_mg_t_postAKAResults *pAKACallback = NULL;
ulcm_mg_t_postUpdateGprsResults *pUpdateGprsCallback = NULL;

void           *reqThread( void *temp );
void            decodeIMSI( const unsigned char *IMSI, int len );
void			postTriplets( unsigned int hCallbackCookie, unsigned int nResult, ulcm_mg_triplet_t * pTripletBuffer, unsigned int tripletCount,ulcm_mg_quintuplet_t * pQuintetBuffer, unsigned int quintetCount, const char *pServiceProfile );
void            postIMSI( unsigned int hCallbackCookie, unsigned int nResult, const char *pszIMSI );
void			postUpdateGprs( unsigned int hCallbackCookie, unsigned int nResult, const ulcm_mg_updategprsresponse_t * pupdateGprsResponse );
void            postAuthorization( unsigned int hCallbackCookie, unsigned int nResult, const char *pServiceProfile , const char *pszmsisdn, int subscriberStatus, int vlrSubscriptionInfoPresent);
void			postQuintets( unsigned int hCallbackCookie, unsigned int nResult, ulcm_mg_quintuplet_t * pQuintetBuffer, unsigned int quintetCount, ulcm_mg_triplet_t * pTripletBuffer, unsigned int tripletCount, const char *pServiceProfile );


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
void
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

    ulcm_mg_t_ModuleTerminate( modHandle ,1);
    exit( 0 );
    /* not in main loop yet */
}


/*************************************************************************

FUNCTION
	void usage(  )

DESCRIPTION
        It displays command line parameters.

INPUTS
        Arg:	None 

OUTPUTS
        Return: None

FEND
*************************************************************************/

/*************************************************************************

FUNCTION
    int convertToString( char *src, int len, char *dest )

DESCRIPTION

        Convert to string

INPUTS
        Arg:    src - pointer to the input string
                len - length of the input string
                dest - pointer to destination string

OUTPUTS
        Return:  count (length of the destination string)

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

static void
usage(  )
{
    printf( "\nUsage: testApp conf_file imsi number_of_requested_vectors local_host_string remote_host_string request_type\n" );
    return;
	
}
unsigned int    iCallbackCookie = 1;
char            imsi[3][16];
char            msisdn[16];
char		msisdnreceived[16];
char customerStatus[5];
int             countTriplets = 0;
int reqType = 0;

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
    unsigned int    hIMSIRequest = 0;
    unsigned int    hUpdateGprsRequest = 0;
    globalMsg = malloc (5000 * sizeof(char));
	switch ( reqType )
	{
	case 1:
		printf( "============================================\n" );
		printf( "RequestTriplets.\n" );
		printf( "============================================\n" );
		printf( "IMSI: %s , length: %d\n", imsi[0], (int) strlen( imsi[0] ) );
		if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_SIM ) )
			iiRet = ulcm_mg_t_RequestSIMTriplets( modHandle, imsi[0], countTriplets, &hSIMRequest, pFnCallback, iCallbackCookie++ ,1);
		if ( iiRet != 0 )
		{
			printf( "main() : Error in request returned 0x%x\n", iiRet );
		}
		else 
		{
			sleep(1);
		}
		break;
	case 0:
		printf("============================================\n");
		printf("Scenario : requestAuthorization.\n");
		printf("============================================\n");
		if (pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_AUTHORIZATION))
		{
			iiRet = ulcm_mg_t_RequestAuthorizationInfo( modHandle,
			imsi[0], &hPIRequest,
			pAuthorizationCallback,
			iCallbackCookie++, 1);
		}
		if ( iiRet != 0 )
		{
			printf("main() : Error in requestAuthorization, request returned 0x%x\n", iiRet);
		}
		else
		{
			sleep( 1 );
		}
		break;
		break;
    case 2:
		printf( "============================================\n" );
		printf( "Scenario requestUpdateGprs.\n");
		printf( "============================================\n" );
		printf( "IMSI: %s , length: %d\n", imsi[0], (int) strlen( imsi[0] ) );
		if ( pAvailableCaps == ( pAvailableCaps | ULCM_L_MG_CAPS_UPDATEGPRS ) )
		{
			printf("Processing for UPDATE GPRS\n");
			ulcm_mg_updategprsrequest_t updateGprsParams;

			updateGprsParams.updateGprsLocationArg.bit_mask = UpdateGprsLocationArg_v3_sgsn_Capability_present;
			updateGprsParams.updateGprsLocationArg.sgsn_Number.length = 7;
			if (convertToString("91198922085565",14,updateGprsParams.updateGprsLocationArg.sgsn_Number.value) != 7)
                printf("error encoding sgsn-Number\n");
			updateGprsParams.updateGprsLocationArg.sgsn_Address.length = 5;
			if (convertToString("04706e224d",10,updateGprsParams.updateGprsLocationArg.sgsn_Address.value) != 5)
                printf("error encoding sgsn-Address\n");
			updateGprsParams.updateGprsLocationArg.sgsn_Capability.bit_mask = SGSN_Capability_gprsEnhancementsSupportIndicator_present | SGSN_Capability_supportedCamelPhases_present;
			updateGprsParams.updateGprsLocationArg.sgsn_Capability.gprsEnhancementsSupportIndicator = TRUE;
			updateGprsParams.updateGprsLocationArg.sgsn_Capability.supportedCamelPhases.length = 3; /* bit string, length is number of bits */
			updateGprsParams.updateGprsLocationArg.sgsn_Capability.supportedCamelPhases.value[0] = 0x20;
			updateGprsParams.updateGprsLocationArg.sgsn_Capability.supportedCamelPhases.value[1] = 0;
			printf("Requesting for UPDATE GPRS\n");
			iiRet = ulcm_mg_t_RequestUpdateGprs( modHandle, imsi[0], &updateGprsParams, &hUpdateGprsRequest, pUpdateGprsCallback, iCallbackCookie++, 1 );
			if ( iiRet != 0 )
			{
			printf( "main() : Error in testCase request returned 0x%d\n", iiRet );
			} else {
				printf("Requested for UPDATE GPRS\n");
			}

		}
		else
		{
			printf( "UPDATEGPRS is not supported.\n" );
		}
		
	default: 
		printf("Invalid Request Type Chosen.");
    } /*
    printf("MSISDN Received : %s\n",msisdnreceived);

    printf("============================================\n");
    printf("Scenario : requestMSISDN.\n");
    printf("============================================\n");
    printf("Condition Satifshfied for : %d == %d\n",pAvailableCaps,ULCM_L_MG_CAPS_IMSI);
    printf("Requesting msisdn for imsi : %s\n",imsi);
    if (pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_IMSI))
    {
	iiRet = ulcm_mg_t_RequestIMSI ( modHandle,
			    imsi[0],
			    &hIMSIRequest,
			    pimsiCallback,
			    iCallbackCookie++ );
    }

    if ( iiRet != 0 )
    {
      printf("main() : Error in requestMSISDN, request returned 0x%x\n", iiRet);
    }

    else
    {
	sleep( 1 );
    }
    */
    return NULL;
}


/*************************************************************************

FUNCTION
	int main( int argc, const char *argv[] )

DESCRIPTION
        It is main function.

INPUTS
        Arg:	argc    - count of command line arguments
                argv[]  - list of command line arguement pointers
OUTPUTS
        Return: 0 - Success

FEND
*************************************************************************/

int
main( int argc, const char *argv[] )
{
//    unsigned int    hSIMRequest1, hSIMRequest2;

    int             iRet = 0;
//    char            error_msg[200];
    pthread_t       reqThreadId[1];
	char localHost[50];
	char remoteHost[50];
	
    if ( argc < 7 )
    {
	usage(  );
	return 0;
    }

	sprintf( imsi[0], "%s", argv[2] );
    printf( "IMSI is %s\n", argv[2] );
    sprintf( msisdn, "%s", argv[2] );
    printf( "MSISDN is %s\n", argv[2] );
    imsi[1][0] = '\0';
    imsi[2][0] = '\0';
    customerStatus[0] = '\0';

    countTriplets = atoi(argv[3]);
    printf( "Requesting %d triplets \n", countTriplets );
	sprintf( localHost, "%s" ,argv[4] );
	printf( "Local Host Value: %s\n", localHost );
	sprintf( remoteHost, "%s" , argv[5] );
	printf( "Remote Host Value: %s\n", remoteHost );
	reqType = atoi (argv[6]);

	     
/*  signal(SIGINT,sigintCatcher);*/
    pFnCallback = ( ulcm_mg_t_postSIMResults * ) & postTriplets;
    pimsiCallback = ( ulcm_mg_t_postIMSIResults * ) & postIMSI;
    pAuthorizationCallback = ( ulcm_mg_t_postAuthorizationResults * ) & postAuthorization;
    pAKACallback = ( ulcm_mg_t_postAKAResults * ) & postQuintets;
    pUpdateGprsCallback = (ulcm_mg_t_postUpdateGprsResults *) & postUpdateGprs;
    printf("Initializing with host:%s\n", remoteHost);
    iRet = ulcm_mg_t_ModuleInit( 1,remoteHost , localHost, &pAvailableCaps, &modHandle , 1);
	//iRet = ulcm_mg_t_ModuleInit( 1, "REMOTE_HOST aaa-ulticom:2906 [10.193.250.142]"," LOCAL_HOST aaa-ulticom:0",&pAvailableCaps, &modHandle );
    printf( " Library capabilities bitmask: %x\n", pAvailableCaps );
    if ( iRet != 0 )
    {
	printf( "main(): Error in moduleInit() iRet = %d\n", iRet );
	return 0;
    }
    sleep( 2 );
    /*Start the thread --S */
    {
	long             i = 0;
	for ( ;; )
	{
	    msisdnreceived[0] = '\0';
	    pthread_create( &reqThreadId[1], NULL, reqThread, ( void * ) i );
	    pthread_join( reqThreadId[1], NULL );
	sleep(5);
	    {
		int             i;
		for ( i = 0; i < 1000000; i++ );
	    }
	}
    }
    for ( ;; );
    return 0;
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
postIMSI( unsigned int hCallbackCookie, unsigned int nResult, const char *pIMSI )
{
//    int             i = 0;
    printf( "\n" );
    printf( "Result received for requestIMSI(), result is 0x%x,  ", nResult );
    printf( "IMSI is : %s \n ", pIMSI );
    /* assign the IMSI to be used in requestSIMTriplets()  */
    sprintf( imsi[0], "%s", pIMSI );
}

/*************************************************************************

FUNCTION
	void postUpdateGprs( unsigned int hCallbackCookie, 
			unsigned int nResult,
			const ulcm_mg_updategprsresponse_t * pupdateGprsResponse )
DESCRIPTION
        It is used to the UpdateGprs response.

INPUTS
        Arg:	hCallbackCookie : Callback cookie
		nResult - result
		pupdateGprsResponse - pointer to UpdateGprs response
OUTPUTS
        Return: None

FEND
*************************************************************************/

void
postUpdateGprs( unsigned int hCallbackCookie, unsigned int nResult, const ulcm_mg_updategprsresponse_t * pupdateGprsResponse )
{
    printf( "\n" );
    printf( "Result received for requestUpdateGprs(), result is 0x%x,  ", nResult );
    printf( "\nhlr-Number is : ");
    decodeIMSI( pupdateGprsResponse->updateGprsLocationRes.hlr_Number.value, pupdateGprsResponse->updateGprsLocationRes.hlr_Number.length );
    printf( "\n" );
    if ( ( pupdateGprsResponse->updateGprsLocationRes.bit_mask & UpdateGprsLocationRes_v3_add_Capability_present ) != 0)
    {
	printf ( "add-Capability is present \n" );
	}
	if ( ( pupdateGprsResponse->insertSubscriberDataArg.bit_mask & InsertSubscriberDataArg_v3_accessRestrictionData_present ) != 0)
	{
	printf ( "accessRestrictionData is " );
	decodeIMSI( pupdateGprsResponse->insertSubscriberDataArg.accessRestrictionData.value, 1 );
	printf( "\n" );
	}
	
	if ( ( pupdateGprsResponse->insertSubscriberDataArg.bit_mask & InsertSubscriberDataArg_v3_gprsSubscriptionData_present ) != 0) 
	{
	printf ( "gprsSubscriptionData_present \n" );
	if (pupdateGprsResponse->insertSubscriberDataArg.gprsSubscriptionData.completeDataListIncluded)
	{
		printf("completeDataListIncluded\n");
		printf("PDP context count: %d\n", pupdateGprsResponse->insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.count);
	}
	
	decodeIMSI( pupdateGprsResponse->insertSubscriberDataArg.accessRestrictionData.value, 1 );
	printf( "\n" );
	}

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
postAuthorization( unsigned int hCallbackCookie, unsigned int nResult, const char *pServiceProfile, const char *pszmsisdn, int subscriberStatus, int vlrSubscriptionInfoPresent )
{
    printf( "\n" );
    printf( "Result received for requestAuthorization(), result is 0x%x,  ", nResult );
    printf( "Service Profile : %s \n ", pServiceProfile );    
    printf( "msisdn received : %s , length : %d\n ", pszmsisdn ,strlen(pszmsisdn));
    char prepaid[8] = "Prepaid";
	prepaid[8] = '\0';
	char postpaid[9] = "Postpaid";
	postpaid[9] = '\0';
    char            indx, indx2;
    int len = strlen(pszmsisdn);
    
    int i=0;
    for( i = 0; i< len ; ){
      indx =  *( pszmsisdn + i++ ) ;
      indx2 = *( pszmsisdn + i++) ;
      printf("%x%x",indx2,indx);
      sprintf(msisdnreceived,"%s%c",msisdnreceived,indx2);
      sprintf(msisdnreceived,"%s%c",msisdnreceived,indx);
    }
    sprintf (customerStatus, "%d",  subscriberStatus);
    printf( "converted msisdn received : %s , length : %d\n ", msisdnreceived, strlen(msisdnreceived) ); 
    printf( "Subsciber Status received: %d\n",  subscriberStatus );   
    printf( "Customer Status received: %s \n",  customerStatus);	
	printf("Customer Type: %s\n", (vlrSubscriptionInfoPresent==1?prepaid:postpaid));
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
postTriplets( unsigned int hCallbackCookie,
	      unsigned int nResult, /*const*/ ulcm_mg_triplet_t * pTripletBuffer, unsigned int tripletCount, ulcm_mg_quintuplet_t * pQuintetBuffer, unsigned int quintetCount, const char *pServiceProfile )
{
    int             i = 0, j=0, k1=0, k2=0,k3=0,l=0;
	char xres1[8], xres2[8], xres3[8], xres4[8];
	char ck1[16], ck2[16], ik1[16], ik2[16],ch;
    printf( "\n" );
    printf( "\nResult received for requestTriplets(), result is 0x%x  ", nResult );
    printf( "\nNumber of triplets is %d  ", tripletCount );
    printf( "\nService Profile (authorization info) : %s ", pServiceProfile );
    if ( pTripletBuffer != NULL )
    {
	for ( i = 0; i < tripletCount; i++ )
	{
	    printf( "\nRand is   " );
	    decodeIMSI_RAND( pTripletBuffer->RAND, 16, i );
	    printf( "\nSres is  " );
	    decodeIMSI_SRES( pTripletBuffer->SRES, 4, i );
	    printf( "\nKc is   " );
	    decodeIMSI_KC( pTripletBuffer->Kc, 8, i );
	    pTripletBuffer++;
	    numberOfTripletsReceived++;
	}
	iReponseReceived = 1;
    }
	 else if ( pQuintetBuffer != NULL)
	{
		printf("\nQuintet Received.\n");
		
		globalMsg = strcat (globalMsg , "Quintet Received." );		
		printf("\nCalling PostQuintets.\n");
		postQuintets( hCallbackCookie, nResult, pQuintetBuffer, quintetCount, pTripletBuffer, tripletCount, pServiceProfile );		
		printf("\nPostQuintets excution complete.\n");
		printf("Xres : %s\n" , nativeQXres);
		printf("ck : %s\n" , nativeQCk);
		printf("ik : %s\n" , nativeQIk);
		printf("quintetCount  : %d\n" , quintetCount );
		for ( i=0 ; i<quintetCount ; i++ ) {
			//memcpy (nativeRand[i], nativeQRand, strlen(nativeQRand));
			for ( j=0 ; j<32 ; j++){
				nativeRand[i][j] = nativeQRand[l++];
			}

			printf ( "RAND received is: %s \n ", nativeRand[i] );
			
			globalMsg = strcat (globalMsg , "\nRAND received is: " );
			globalMsg = strcat (globalMsg , nativeRand[i] );
			for ( j=strlen(nativeQXres) ; j<32 ; j++){
				nativeQXres[j] = '0';
			}
			for ( j=0; j<8 ; j++ ) {
				ch = nativeQXres[k3++];
				if(ch >= 65 && ch <= 70){
					xres1[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					xres1[j] = ch - 87;
				}else
					xres1[j] = ch - 48;
			}
			for ( j=0 ; j<8 ; j++ ) {
				ch = nativeQXres[k3++];
				if(ch >= 65 && ch <= 70){
					xres2[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					xres2[j] = ch - 87;
				}else
					xres2[j] = ch - 48;				
			}
			for ( j=0 ; j<8 ; j++ ) {
				ch = nativeQXres[k3++];
				if(ch >= 65 && ch <= 70){
					xres3[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					xres3[j] = ch - 87;
				}else
					xres3[j] = ch - 48;
			}
			for ( j=0 ; j<8 ; j++ ) {
				ch = nativeQXres[k3++];
				if(ch >= 65 && ch <= 70){
					xres4[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					xres4[j] = ch - 87;
				}else
					xres4[j] = ch - 48;
			}
			memset( nativeSres[i], 0, 256 );
			for ( j=0 ; j<8 ; j++ ) {						
				printf("Debug :[%x][%x][%x][%x] = %d\n",xres1[j],xres2[j],xres3[j],xres4[j],(((xres1[j] ^ xres2[j])^ xres3[j]) ^ xres4[j]));
				if((((xres1[j] ^ xres2[j])^ xres3[j]) ^ xres4[j]) <= 9)	
					nativeSres[i][j] = (((xres1[j] ^ xres2[j])^ xres3[j]) ^ xres4[j]) + '0';
				else
					nativeSres[i][j] = (((xres1[j] ^ xres2[j])^ xres3[j]) ^ xres4[j]) + 55;
			}
			printf ( "SRES received is: %s \n ", nativeSres[i] );
			globalMsg = strcat (globalMsg , "\nSRES received is: " );
			globalMsg = strcat (globalMsg , nativeSres[i] );
			for ( j=0; j<16 ; j++ ) {				
				ch = nativeQCk[k1++];
				if(ch >= 65 && ch <= 70){
					ck1[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					ck1[j] = ch - 87;
				}else
					ck1[j] = ch - 48;
				
			}
			for ( j=0 ; j<16 ; j++ ) {				
				ch = nativeQCk[k1++];
				if(ch >= 65 && ch <= 70){
					ck2[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					ck2[j] = ch - 87;
				}else
					ck2[j] = ch - 48;				
			}
			
			for ( j=0; j<16 ; j++ ) {				
				ch = nativeQIk[k2++];
				if(ch >= 65 && ch <= 70){
					ik1[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					ik1[j] = ch - 87;
				}else
					ik1[j] = ch - 48;								
			}
			for ( j=0 ; j<16 ; j++ ) {
				ch = nativeQIk[k2++];
				if(ch >= 65 && ch <= 70){
					ik2[j] = ch - 55;
				}else if(ch >= 97 && ch <= 102){
					ik2[j] = ch - 87;
				}else
					ik2[j] = ch - 48;								
			}
			memset( nativeKc[i], 0, 256 );
			for ( j=0 ; j<16 ; j++ ) {
				if((((ck1[j] ^ ck2[j]) ^ ik1[j]) ^ ik2[j]) <= 9)
					nativeKc[i][j] = (((ck1[j] ^ ck2[j]) ^ ik1[j]) ^ ik2[j]) + '0';
				else
					nativeKc[i][j] = (((ck1[j] ^ ck2[j]) ^ ik1[j]) ^ ik2[j]) + 55;
			}			
			printf ( "KC received is: %s \n ", nativeKc[i] );
			globalMsg = strcat (globalMsg , "\nKC received is: " );
			globalMsg = strcat (globalMsg , nativeKc[i] );
			numberOfTripletsReceived++;
		}
		iReponseReceived = 1;
	} 
    else
    {
	printf( "Triplet Buffer does not contain anything....\n" );
    }
    printf( "\n" );

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
postQuintets( unsigned int hCallbackCookie,
	      unsigned int nResult, /*const*/ ulcm_mg_quintuplet_t * pQuintupletBuffer, unsigned int nQuintupletCount, ulcm_mg_triplet_t * pTripletBuffer, unsigned int nTripletCount, const char *pServiceProfile )
{
    int             i = 0;
    printf( "\n" );
    printf( "Result received for requestAKAQuintets(), result is 0x%x  ", nResult );
    printf( "Number of quintets is %d  ", nQuintupletCount );
    printf( "Service Profile (authorization info) : %s \n ", pServiceProfile );
    if ( pQuintupletBuffer != NULL )
    {
	for ( i = 0; i < nQuintupletCount; i++ )
	{
		globalMsg = strcat (globalMsg , "Processing Q rand" );	
	    printf( "\nRAND is   " );
	    decodeIMSI_QRAND( pQuintupletBuffer->RAND, 16 );
		globalMsg = strcat (globalMsg , "Processing Q XRES" );	
	
		printf("\nstrlen of xres %d\n", strlen(pQuintupletBuffer->XRES));
		printf( "\nXRES is  " );
		decodeIMSI_QXRES( pQuintupletBuffer->XRES,  strlen(pQuintupletBuffer->XRES));
		globalMsg = strcat (globalMsg , "Processing Q CK" );	
	    printf( "\nCK is   " );
	    decodeIMSI_QCK( pQuintupletBuffer->CK, 16 );
		globalMsg = strcat (globalMsg , "Processing Q IK" );	
	    printf( "\nIK is   " );
	    decodeIMSI_QIK( pQuintupletBuffer->IK, 16 );
		globalMsg = strcat (globalMsg , "Processing Q AUTN" );	
	    printf( "\nAUTN is   " );
	    decodeIMSI_QAUTN( pQuintupletBuffer->AUTN, 18 );
	    pQuintupletBuffer++;

	}
	iReponseReceived = 1;
    }
    else
    {
	printf( "quintet Buffer does not contain anything....\n" );
    }
    printf( "\n" );
}


/*************************************************************************

FUNCTION

	void	decodeIMSI( const unsigned char *IMSI, int len )

DESCRIPTION
        It  displays IMSI.

INPUTS
        Arg:	IMSI - pointer to IMSI	
		len - length
OUTPUTS
        Return: None

FEND
*************************************************************************/

void
decodeIMSI( const unsigned char *IMSI, int len )
{
    char            indx, indx2;
    int             i = 0;
    printf( "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	printf( "%x", indx );
	printf( "%x", indx2 );
    }

}
void decodeIMSI_QRAND( const unsigned char *IMSI, int len)
{
    char            indx, indx2;
    int             i = 0;
    char tmpMsg[MSGSIZE];

    printf( "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	printf("%x", indx );
	printf( "%x", indx2 );
	sprintf(tmpMsg, "%s%x", nativeQRand,indx );
	sprintf(nativeQRand, "%s%x", tmpMsg,indx2 );	
    }
}

void decodeIMSI_QAUTN( const unsigned char *IMSI, int len)
{
    char            indx, indx2;
    int             i = 0;
    char tmpMsg[MSGSIZE];

    printf( "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	printf("%x", indx );
	printf( "%x", indx2 );
	sprintf(tmpMsg, "%s%x", nativeQAutn,indx );
	sprintf(nativeQAutn, "%s%x", tmpMsg,indx2 );	
    }
}

void decodeIMSI_QXRES( const unsigned char *IMSI, int len)
{
    char            indx, indx2;
    int             i = 0;
    char tmpMsg[MSGSIZE];

    printf( "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	printf("%x", indx );
	printf( "%x", indx2 );
	sprintf(tmpMsg, "%s%x", nativeQXres,indx );
	sprintf(nativeQXres, "%s%x", tmpMsg,indx2 );	
    }
}

void decodeIMSI_QCK( const unsigned char *IMSI, int len)
{
    char            indx, indx2;
    int             i = 0;
    char tmpMsg[MSGSIZE];

    printf( "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	printf("%x", indx );
	printf( "%x", indx2 );
	sprintf(tmpMsg, "%s%x", nativeQCk,indx );
	sprintf(nativeQCk, "%s%x", tmpMsg,indx2 );	
    }
}

void decodeIMSI_QIK( const unsigned char *IMSI, int len)
{
    char            indx, indx2;
    int             i = 0;
    char tmpMsg[MSGSIZE];

    printf( "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	printf("%x", indx );
	printf( "%x", indx2 );
	sprintf(tmpMsg, "%s%x", nativeQIk,indx );
	sprintf(nativeQIk, "%s%x", tmpMsg,indx2 );	
    }
}

void
decodeIMSI_RAND( const unsigned char *IMSI, int len , int pos)
{
    char            indx, indx2;
    int             i = 0;

	char tmpMsg[MSGSIZE];

    printf( "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	
	printf("%x", indx );
	printf( "%x", indx2 );
	//sprintf(tmpMsg, "%s%x", msg1,indx );
	//sprintf(msg1, "%s%x", tmpMsg,indx2 );
	sprintf(tmpMsg, "%s%x", nativeRand[pos],indx );
	sprintf(nativeRand[pos], "%s%x", tmpMsg,indx2 );	
    }
	
}

void
decodeIMSI_SRES( const unsigned char *IMSI, int len, int pos )
{
    char            indx, indx2;
    int             i = 0;
	char tmpMsg[MSGSIZE];
    printf( "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	printf(" %x", indx );
	printf( "%x", indx2 );
	//sprintf(tmpMsg, "%s%x", msg2,indx );
	//sprintf(msg2, "%s%x", tmpMsg,indx2 );
	sprintf(tmpMsg, "%s%x", nativeSres[pos],indx );
	sprintf(nativeSres[pos], "%s%x", tmpMsg,indx2 );
	
    }
}

void
decodeIMSI_KC( const unsigned char *IMSI, int len, int pos )
{
    char            indx, indx2;
    int             i = 0;
	char tmpMsg[MSGSIZE];
    printf( "0x" );
    for ( i = 0; i < len; i++ )
    {
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	printf(" %x", indx );
	printf( "%x", indx2 );
	//sprintf(tmpMsg, "%s%x", msg3,indx );
	//sprintf(msg3, "%s%x", tmpMsg,indx2 );
	sprintf(tmpMsg, "%s%x", nativeKc[pos],indx );
	sprintf(nativeKc[pos], "%s%x", tmpMsg,indx2 );	
    }
}
