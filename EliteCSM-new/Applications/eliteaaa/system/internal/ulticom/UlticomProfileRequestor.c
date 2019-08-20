
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
#include "clientMod.h"
#include "com_elitecore_aaa_core_drivers_AuthGatewayProfileRequestor.h"
#include "com_elitecore_aaa_core_drivers_MAPGWRequestorConnectionPool.h"

#define MSISDNSIZE 16
#define MAX_REQ 1024
#define PDP_DATA_SIZE 1024
#define CHARGIN_CHAR_SIZE 128
#define ACCESS_REST_DATA_SIZE 128
#define MSGSIZE 256

#define LOG_LEVEL_NONE 0
#define LOG_LEVEL_ERROR 1 
#define LOG_LEVEL_WARN 2
#define LOG_LEVEL_INFO 3
#define LOG_LEVEL_DEBUG 4
#define LOG_LEVEL_TRACE 5
#define LOG_LEVEL_ALL 6
#define DATETIMESIZE    64

#define ASYNC_POOLING_ITR 100

#define DATA_LIST_PRESENT 1
#define DATA_LIST_NOT_PRESENT 0

int logLevel = LOG_LEVEL_ALL;
#define LOG_MSG(msgLevel, format, logMsg...) if (logLevel >= msgLevel) {char outstr[DATETIMESIZE]; datetimestamp(outstr);\
 fprintf(stdout, "%s [%s:%d] [%u] ", outstr, __FILE__, __LINE__,  (unsigned int)pthread_self()); fprintf(stdout, format, ## logMsg ); }

unsigned int modHandle = 0;
unsigned short  altSend = 0;
static unsigned int pAvailableCaps = 0;
//int iReponseReceived = 0 ;

typedef struct insertSubData {
	unsigned int nResult;
	int responseReceived;
	char customerStatus[1];
	char msisdnreceived[MSISDNSIZE];
	char vlrSubscriptionInfoPresent[1];
	char serviceProfile[128];
} insertSubData ;
insertSubData isdBufferArray[MAX_REQ];

typedef struct gprsLUData {
	unsigned int nResult;
	int responseReceived;
	char pdpData[PDP_DATA_SIZE];
	char chargingChar[CHARGIN_CHAR_SIZE];
	char accessRestrictionData[ACCESS_REST_DATA_SIZE];
	char msisdnreceived[MSISDNSIZE];
	ulcm_GPRSDataList *gprsDataList;
	int gprsDataListPresent;
}gprsLUData;
gprsLUData gprsLUDataBufferArray[MAX_REQ];


unsigned int driverModHandles[MAX_CONNECTIONS];

ulcm_mg_t_postSIMResults *pFnCallback = NULL;
ulcm_mg_t_postIMSIResults *pimsiCallback = NULL;
ulcm_mg_t_postAuthorizationResults *pAuthorizationCallback = NULL;
ulcm_mg_t_postAKAResults *pAKACallback = NULL;
ulcm_mg_t_postUpdateGprsResults *pUpdateGprsCallback = NULL;

void           *reqThread( void *temp );
void            decodeIMSI( const unsigned char *IMSI, int len );
void
 
 
 
 
 
 postTriplets( unsigned int hCallbackCookie,
	       unsigned int nResult, const ulcm_mg_triplet_t * pTripletBuffer, unsigned int tripletCount, const char *pServiceProfile );
void            postIMSI( unsigned int hCallbackCookie, unsigned int nResult, const char *pszIMSI );
void            postAuthorization( unsigned int hCallbackCookie, unsigned int nResult, const char *pServiceProfile , const char *pszmsisdn, int subscriberStatus, int vlrSubscriptionInfoPresent );
void            postQuintets( unsigned int hCallbackCookie, unsigned int nResult, const ulcm_mg_quintuplet_t * pQuintetBuffer, unsigned int quintetCount, const char *pServiceProfile );
void			postUpdateGprs( unsigned int hCallbackCookie, unsigned int nResult, const ulcm_mg_updategprsresponse_t * pupdateGprsResponse );

void logInJava(JNIEnv *env, jobject obj,jclass objectClass, jclass stringClass, jmethodID stringInit, char* nativeMessage);

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

    ulcm_mg_t_ModuleTerminate( modHandle,0 );
    exit( 0 );
    /* not in main loop yet */
}

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

unsigned int    iCallbackCookie = 1;
char            imsi[3][16];


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
// reqThread is original code from Ulticom test application.
void           *
reqThread( void *temp )
{
    int             iiRet = 0;
    unsigned int    hPIRequest = 0;

    printf("============================================\n");
    printf("Scenario : requestAuthorization.\n");
    printf("============================================\n");
    if (pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_AUTHORIZATION)) {
	iiRet = ulcm_mg_t_RequestAuthorizationInfo( modHandle,
			imsi[0], &hPIRequest,
			pAuthorizationCallback,
			iCallbackCookie++, 1);
    }
    if ( iiRet != 0 ) {
      printf("main() : Error in requestAuthorization, request returned 0x%x\n", iiRet);
    }
    else {
	  sleep( 1 );
    }
/*
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
	jobject createPDPContext(JNIEnv *env, ulcm_PDP_Context ulcm_pdp_Context)
 
DESCRIPTION
        Function to create PDPContext object to send to JAVA-AAA APp

INPUTS
    
OUTPUTS


FEND
 *************************************************************************/
jobject createPDPContext(JNIEnv *env, ulcm_PDP_Context ulcm_pdp_Context,
		jclass Class_java_lang_String, jmethodID MID_String_init) {

	/* Get a reference to PDPContext class */
	jclass jClass_PDPContext = (*env)->FindClass(env,"com/elitecore/coreeap/data/sim/PDPContext");
	if(jClass_PDPContext == NULL ) {
		LOG_MSG(LOG_LEVEL_ERROR, "PDPContext Class not found \n");
		return NULL;
	}

	jmethodID MID_PDPcontext_init = (*env)->GetMethodID(env,jClass_PDPContext,"<init>","()V");
	if(MID_String_init == NULL) {
		LOG_MSG(LOG_LEVEL_ERROR, "com/elitecore/coreeap/data/sim/PDPContext<init>()V method not found \n");
		printf("Init Method not fonud for PDPContext Class \n");
		return NULL;
	}

	jobject pdpConext = (*env)->NewObject(env,jClass_PDPContext, MID_PDPcontext_init);
	if(pdpConext == NULL) {
		LOG_MSG(LOG_LEVEL_ERROR, "com/elitecore/coreeap/data/sim/PDPContext class creation failed\n");
		printf("com/elitecore/coreeap/data/sim/PDPContext class creation failed \n");
		return NULL;
	}

	jbyteArray bytes=0;
	jstring str;
	// Setting APN name
	int len = strlen(ulcm_pdp_Context.apn.value);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): Out of Memory in NATIVE while setting APN name\n");
		return NULL;
	}

	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)ulcm_pdp_Context.apn.value + 1);
		str = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
		(*env)->DeleteLocalRef(env,bytes);
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): byte array region could not be located while setting APN name\n");
		return NULL;
	}

	jmethodID setAPNMid = (*env)->GetMethodID(env, jClass_PDPContext, "setUlcm_APN", "(Ljava/lang/String;)V");
	if (setAPNMid == NULL) {
		LOG_MSG(LOG_LEVEL_ERROR, "setUlcm_APN(Ljava/lang/String;)V method not found while setting APN name\n");
		return NULL;
	}
	(*env)->CallObjectMethod(env, pdpConext, setAPNMid,str);

	// Setting PDP type
	len = ulcm_pdp_Context.pdp_Type.length;
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): Out of Memory in NATIVE while Setting PDP type\n");
		return NULL;
	}

	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)ulcm_pdp_Context.pdp_Type.value);
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): byte array region could not be located while Setting PDP type\n");
		return NULL;
	}

	jmethodID setPDPTypeMid = (*env)->GetMethodID(env, jClass_PDPContext, "setPdp_Type", "([B)V");
	if (setPDPTypeMid == NULL) {
		LOG_MSG(LOG_LEVEL_ERROR, "setPdp_Type([B)V method not found while Setting PDP type\n");
		return NULL;
	}
	(*env)->CallObjectMethod(env, pdpConext, setPDPTypeMid,bytes);
	(*env)->DeleteLocalRef(env,bytes);
	
	
	// Setting PDP context id
	/*len = sizeof(ulcm_pdp_Context.pdp_ContextId);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): Out of Memory in NATIVE while Setting PDP context id\n");
		return NULL;
	}

	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)ulcm_pdp_Context.pdp_ContextId);
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): byte array region could not be located Setting PDP context id\n");
		return NULL;
	}

	jmethodID setPdp_ContextId = (*env)->GetMethodID(env, jClass_PDPContext, "setPdp_ContextId", "([B)V");
	if (setPdp_ContextId == NULL) {
		LOG_MSG(LOG_LEVEL_ERROR, "setPdp_ContextId([B)V method not found Setting PDP context id\n");
		return NULL;
	}
	(*env)->CallObjectMethod(env, pdpConext, setPdp_ContextId,bytes);
	(*env)->DeleteLocalRef(env,bytes);
	*/
	// Setting pdp_Address
	if (ulcm_pdp_Context.bit_mask & PDP_Context_pdp_Address_present > 0) {
	len = ulcm_pdp_Context.pdp_Address.length;
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): Out of Memory in NATIVE while Setting pdp_Address\n");
		return NULL;
	}

	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)ulcm_pdp_Context.pdp_Address.value);
		str = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
		(*env)->DeleteLocalRef(env,bytes);
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): byte array region could not be located while Setting pdp_Address\n");
		return NULL;
	}

	jmethodID setPDPAddress = (*env)->GetMethodID(env, jClass_PDPContext, "setPdp_Address", "(Ljava/lang/String;)V");
	if (setPDPAddress == NULL) {
		LOG_MSG(LOG_LEVEL_ERROR, "setPdp_Address(Ljava/lang/String;)V method not found while Setting pdp_Address\n");
		return NULL;
	}
	(*env)->CallObjectMethod(env, pdpConext, setPDPAddress,str);
	}
	
	// Setting qos_Subscribed
	len = ulcm_pdp_Context.qos_Subscribed.length;
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): Out of Memory in NATIVE while Setting qos_Subscribed\n");
		return NULL;
	}

	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)ulcm_pdp_Context.qos_Subscribed.value);
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): byte array region could not be located while Setting qos_Subscribed\n");
		return NULL;
	}

	jmethodID setQoSSubscribed = (*env)->GetMethodID(env, jClass_PDPContext, "setQos_Subscribed", "([B)V");
	if (setQoSSubscribed == NULL) {
		LOG_MSG(LOG_LEVEL_ERROR, "setQos_Subscribed(Ljava/lang/String;)V method not found while Setting qos_Subscribed\n");
		return NULL;
	}
	(*env)->CallObjectMethod(env, pdpConext, setQoSSubscribed,bytes);
	(*env)->DeleteLocalRef(env,bytes);
	
	// Setting ext_QoS_Subscribed
	if ((ulcm_pdp_Context.bit_mask & PDP_Context_ext_QoS_Subscribed_present) == PDP_Context_ext_QoS_Subscribed_present) {
	len = ulcm_pdp_Context.ext_QoS_Subscribed.length;
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): Out of Memory in NATIVE while Setting ext_QoS_Subscribed\n");
		return NULL;
	}

	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)ulcm_pdp_Context.ext_QoS_Subscribed.value);
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): byte array region could not be located while Setting ext_QoS_Subscribed\n");
		return NULL;
	}

	jmethodID setExtQoSSubscribed = (*env)->GetMethodID(env, jClass_PDPContext, "setUlcm_Ext_QoS_Subscribed", "([B)V");
	if (setExtQoSSubscribed == NULL) {
		LOG_MSG(LOG_LEVEL_ERROR, "setUlcm_Ext_QoS_Subscribed(Ljava/lang/String;)V method not found while Setting ext_QoS_Subscribed\n");
		return NULL;
	}
	(*env)->CallObjectMethod(env, pdpConext, setExtQoSSubscribed,bytes);
	(*env)->DeleteLocalRef(env,bytes);
	}
	
	// Setting pdp_ChargingCharacteristics
	if ((ulcm_pdp_Context.bit_mask & PDP_Context_pdp_ChargingCharacteristics_present) == PDP_Context_pdp_ChargingCharacteristics_present) {
	len = ulcm_pdp_Context.pdp_ChargingCharacteristics.length;
	if((*env)->EnsureLocalCapacity(env,len) < 0){
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): Out of Memory in NATIVE while Setting pdp_ChargingCharacteristics\n");
		return NULL;
	}

	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)ulcm_pdp_Context.pdp_ChargingCharacteristics.value);
	}else{
		LOG_MSG(LOG_LEVEL_ERROR, "createPDPContext(): byte array region could not be located while Setting pdp_ChargingCharacteristics\n");
		return NULL;
	}

	jmethodID setChargingChar = (*env)->GetMethodID(env, jClass_PDPContext, "setUlcm_ChargingCharacteristics", "([B)V");
	if (setChargingChar == NULL) {
		LOG_MSG(LOG_LEVEL_ERROR, "setUlcm_ChargingCharacteristics([B)V method not found while Setting pdp_ChargingCharacteristics\n");
		return NULL;
	}
	(*env)->CallObjectMethod(env, pdpConext, setChargingChar,bytes);
	(*env)->DeleteLocalRef(env,bytes);
	}
	
	return pdpConext;
}
/************************************************************************
 * static unsigned int gsm_map_calc_bitrate(unsigned short value)
*************************************************************************/
static unsigned int gsm_map_calc_bitrate(unsigned short value){

	unsigned short granularity = 0;
	unsigned int returnvalue = 0;

	if (value == 0xff)
		return 0;

	granularity = value >> 6;
	returnvalue = value & 0x7f;
	switch (granularity){
	case 0:
		break;
	case 1:
		returnvalue = ((returnvalue - 0x40) << 3)+64;
		break;
	case 2:
		returnvalue = (returnvalue << 6)+576;
		break;
	case 3:
		returnvalue = (returnvalue << 6)+576;
		break;
	}
	return returnvalue;

}

/*************************************************************************

FUNCTION
	void logInJava(JNIEnv *env, jobject obj, char *nativeMessage)

DESCRIPTION
        This function log into JAVA logging file

INPUTS
        Arg:	argc    - count of command line arguments
                argv[]  - list of command line arguement pointers
OUTPUTS
        Return: 0 - Success

FEND
*************************************************************************/
void logInJava(JNIEnv *env, jobject obj,jclass cls, jclass Class_java_lang_String, jmethodID MID_String_init, char* nativeMessage){

    int len;
    jbyteArray bytes=0;
    jstring javaLogMsg;	
    
    if((*env)->EnsureLocalCapacity(env,2) < 0){
      LOG_MSG(LOG_LEVEL_ERROR, "logInJava(): Out of Memory in NATIVE\n");
      return;
    }
    
    len = strlen(nativeMessage);
    bytes = (*env)->NewByteArray(env,len);
    if(bytes != NULL){
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)nativeMessage);
      javaLogMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "logInJava(): Could Not Call Failure Event from native String\n");
      return;
    }
    
    jmethodID newmid = (*env)->GetMethodID(env, cls, "log", "(Ljava/lang/String;)V");
    if (newmid == NULL) {
      LOG_MSG(LOG_LEVEL_ERROR, "log(Ljava/lang/String;)V method not found\n");
      return;
    }
    
    (*env)->CallObjectMethod(env, obj, newmid,javaLogMsg);

}
/*************************************************************************

FUNCTION
	jboolean initSCTPConnection (jint driverID, jstring remoteHost, jstring localHost);

DESCRIPTION
        It is main function.

INPUTS
        Arg:	argc    - count of command line arguments
                argv[]  - list of command line arguement pointers
OUTPUTS
        Return: 0 - Success

FEND
*************************************************************************/
JNIEXPORT jint JNICALL Java_com_elitecore_aaa_core_drivers_MAPGWRequestorConnectionPool_initAuthGWConnection
(JNIEnv *env, jobject obj, jint driverID, jint javaLogLevel, jstring javaRemoteHost, jstring javaLocalHost) {
	logLevel = javaLogLevel;
	const char *nativeRemoteHostString = (*env)->GetStringUTFChars(env, javaRemoteHost, 0);
    const char *nativeLocalHostString = (*env)->GetStringUTFChars(env, javaLocalHost, 0);
	char log_msg[1024], error_msg[1024];
	int iRet = 0;
	int javaRetVal = 1;
	
	/* Get a reference to object class */
    jclass cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
      LOG_MSG(LOG_LEVEL_ERROR, "Object Class not found\n");
      sprintf(error_msg,"Object Class not found");
	  javaRetVal = 0;
      goto failure1;
    }

    jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
    if(Class_java_lang_String == NULL ) {
	  LOG_MSG(LOG_LEVEL_ERROR, "String Class not found \n");
	  sprintf(error_msg,"java/lang/String Class not found");
	  javaRetVal = 0;
	  goto failure1;
    }
    jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
    if(MID_String_init == NULL) {
      printf("Method not fonud for String Class ");
      sprintf(error_msg,"java/lang/String<init>([B)V method not found");
	  javaRetVal = 0;
      goto failure1;
    }
	
	/*Writing into JAVA Log file*/
    sprintf(log_msg,"Initializing with following configuration\n%s\n%s",nativeLocalHostString,nativeRemoteHostString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
    iRet = ulcm_mg_t_ModuleInit( 1, nativeRemoteHostString, nativeLocalHostString, &pAvailableCaps, &modHandle , driverID);
	driverModHandles[driverID] = modHandle;
	
	 /*Writing into JAVA Log file*/
    sprintf(log_msg, " Library capabilities bitmask: %x\n", pAvailableCaps );        
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
	
	  /*  signal(SIGINT,sigintCatcher);*/
    pFnCallback = ( ulcm_mg_t_postSIMResults * ) & postTriplets;
    pimsiCallback = ( ulcm_mg_t_postIMSIResults * ) & postIMSI;
    pAuthorizationCallback = ( ulcm_mg_t_postAuthorizationResults * ) & postAuthorization;
    pAKACallback = ( ulcm_mg_t_postAKAResults * ) & postQuintets;
    pUpdateGprsCallback = (ulcm_mg_t_postUpdateGprsResults *) & postUpdateGprs;
	
	if ( iRet != 0 ) {
		LOG_MSG(LOG_LEVEL_ERROR,  "main(): Error in moduleInit() iRet = %d\n", iRet );
		sprintf(error_msg, "requestProfile(): Error in moduleInit() iRet = %d\n", iRet );
		javaRetVal = 0;
		goto failure1;
    }
	
	goto destroy;
    failure1:
    LOG_MSG(LOG_LEVEL_ERROR, "Failure happen !!, Reason : %s\n", error_msg);
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
	
	fflush(stdout);
	
	return javaRetVal;
}

/*************************************************************************

FUNCTION
	void requestProfile( int argc, const char *argv[] )

DESCRIPTION
        It is main function.

INPUTS
        Arg:	argc    - count of command line arguments
                argv[]  - list of command line arguement pointers
OUTPUTS
        Return: 0 - Success

FEND
*************************************************************************/
#if GPRS_LU == 1
JNIEXPORT int JNICALL Java_com_elitecore_aaa_core_drivers_AuthGatewayProfileRequestor_requestProfile
  (JNIEnv *env, jobject obj, jstring javaImsi, jlong javaRequestTimeout, jint requestorID, jint driverID){
	  
    const char *nativeImsiString = (*env)->GetStringUTFChars(env, javaImsi, 0);
	
    // Conversion from miliseconds to microseconds
	unsigned long requestTimeout = javaRequestTimeout * 1000; 
    
	// requestTimeout is divided by ASYNC_POOLING_ITR because AAA will wait for response in ASYNC_POOLING_ITR equal slot of configured timeout. 
	// At the end of each slot it will check weather response is received or not
	unsigned long timeoutSlot = requestTimeout / ASYNC_POOLING_ITR;
    
	char            log_msg[1024], error_msg[1024];
    //int             iRet = 0;    
    int             iiRet = 0;
    unsigned int    hPIRequest = 0;
    int waitLoopCount = 0;
	
	if ( timeoutSlot < 1000) timeoutSlot  = 1000;
	memset (gprsLUDataBufferArray[requestorID].pdpData, '\0', PDP_DATA_SIZE);
	memset (gprsLUDataBufferArray[requestorID].chargingChar, '\0', CHARGIN_CHAR_SIZE);
	memset (gprsLUDataBufferArray[requestorID].accessRestrictionData, '\0', ACCESS_REST_DATA_SIZE);
	memset (gprsLUDataBufferArray[requestorID].msisdnreceived, '\0', MSISDNSIZE);
	gprsLUDataBufferArray[requestorID].gprsDataList = malloc(sizeof(struct ulcm_GPRSDataList));
	memset (gprsLUDataBufferArray[requestorID].gprsDataList, 0, sizeof(struct ulcm_GPRSDataList));
	gprsLUDataBufferArray[requestorID].gprsDataListPresent = DATA_LIST_NOT_PRESENT;
	gprsLUDataBufferArray[requestorID].responseReceived = 0;
	gprsLUDataBufferArray[requestorID].nResult = ULCM_G_GATEWAY_ERROR;
	
    /* Get a reference to object class */
    jclass cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): Object Class not found\n");
      sprintf(error_msg,"AuthGatewayProfileRequestor_requestProfile(): Object Class not found");
      goto failure1;
    }
    
    jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
    if(Class_java_lang_String == NULL ){
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): String Class not found\n");
	  sprintf(error_msg,"AuthGatewayProfileRequestor_requestProfile(): java/lang/String Class not found");
      goto destroy;
    }
    jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
    if(MID_String_init == NULL){
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): String<init> method not found\n");
      sprintf(error_msg,"java/lang/String<init>([B)V method not found");
      goto destroy;
    }
    
    /*Writing into JAVA Log file*/
    sprintf(log_msg,"Profile lookup started for IMSI : %s",nativeImsiString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);

	LOG_MSG( LOG_LEVEL_INFO, "AuthGatewayProfileRequestor_requestProfile(): CallbackCookie: %u IMSI: %s\n", requestorID, nativeImsiString );
   
  
    fflush(stdout);
    if (!(pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_UPDATEGPRS)))
    {
			printf("Capabilities insufficient for making a call to GPRS UPDATE LOCATION!!!\n");
			return gprsLUDataBufferArray[requestorID].nResult ;
	}
	    /*Writing into JAVA Log file*/
	sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): Calling ulcm_mg_t_RequestUpdateGprs for IMSI: %s Using Handle: %d\n", nativeImsiString, driverModHandles[driverID]);
	logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
	
	{
	
	ulcm_mg_updategprsrequest_t updateGprsParams;
	// Assigning GT of MAP Gateway to sgsn-number, phase-3 to supported camel phases and MAP Gateway server's IP to SGSN address
	// Either this is to be parameterized or to be changed in every deployment. (Compile after changing values)
	
	updateGprsParams.updateGprsLocationArg.bit_mask = UpdateGprsLocationArg_v3_sgsn_Capability_present;
	updateGprsParams.updateGprsLocationArg.sgsn_Number.length = 7;
	if (convertToString("91198902045593",14,updateGprsParams.updateGprsLocationArg.sgsn_Number.value) != 7)
		printf("error encoding sgsn-Number\n");
	updateGprsParams.updateGprsLocationArg.sgsn_Address.length = 5;
	if (convertToString("040a57c062",10,updateGprsParams.updateGprsLocationArg.sgsn_Address.value) != 5)
		printf("error encoding sgsn-Address\n");
	updateGprsParams.updateGprsLocationArg.sgsn_Capability.bit_mask = SGSN_Capability_gprsEnhancementsSupportIndicator_present | SGSN_Capability_supportedCamelPhases_present;
	updateGprsParams.updateGprsLocationArg.sgsn_Capability.gprsEnhancementsSupportIndicator = TRUE;
	updateGprsParams.updateGprsLocationArg.sgsn_Capability.supportedCamelPhases.length = 3; /* bit string, length is number of bits */
	updateGprsParams.updateGprsLocationArg.sgsn_Capability.supportedCamelPhases.value[0] = 0x20;
	updateGprsParams.updateGprsLocationArg.sgsn_Capability.supportedCamelPhases.value[1] = 0;
	
	iiRet = ulcm_mg_t_RequestUpdateGprs( driverModHandles[driverID], nativeImsiString, &updateGprsParams, &hPIRequest, pUpdateGprsCallback, requestorID, driverID );
		
	}
    
    if ( iiRet != 0 )
    {
	  LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile() error 0x%x(%d) for %s\n", iiRet,iiRet, nativeImsiString);
      sprintf(error_msg,"AuthGatewayProfileRequestor_requestProfile() error 0x%x(%d) for %s\n", iiRet,iiRet, nativeImsiString);
	  gprsLUDataBufferArray[requestorID].nResult = iiRet;
      goto failure1;      
    }else{
      LOG_MSG(LOG_LEVEL_INFO, "AuthGatewayProfileRequestor_requestProfile() success for %s\n", nativeImsiString);    
      sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile() success for %s\n", nativeImsiString);    
      logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
    }
	fflush(stdout);

	waitLoopCount = 0;
    while( (waitLoopCount < ASYNC_POOLING_ITR) && (gprsLUDataBufferArray[requestorID].responseReceived == 0)){		 
	LOG_MSG(LOG_LEVEL_TRACE, "AuthGatewayProfileRequestor_requestProfile(): Iteration: %d for IMSI %s\n", waitLoopCount, nativeImsiString);
	usleep(timeoutSlot); // sleep before we check status of responseReceived flag  
	  waitLoopCount++;
    }

    sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): Waited: %lu miliseconds for IMSI %s\n",((waitLoopCount) * (timeoutSlot / 1000)), nativeImsiString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);       
    
	if(gprsLUDataBufferArray[requestorID].responseReceived == 0){
	ulcm_mg_t_CancelUpdateGprsRequest( driverModHandles[driverID] , hPIRequest, driverID );
      LOG_MSG(LOG_LEVEL_WARN, "AuthGatewayProfileRequestor_requestProfile(): Timeout!!! No Response received from the MAP Gateway for IMSI: %s\n", nativeImsiString);
      sprintf(error_msg,"AuthGatewayProfileRequestor_requestProfile(): Timeout!!! No Response received from the MAP Gateway for IMSI: %s\n", nativeImsiString);
      gprsLUDataBufferArray[requestorID].nResult = EC_TIMEOUT;
      goto timeout;
    }
	
	if (gprsLUDataBufferArray[requestorID].nResult != 0 && gprsLUDataBufferArray[requestorID].nResult != 65536 && gprsLUDataBufferArray[requestorID].nResult != 131072){
		LOG_MSG(LOG_LEVEL_WARN, "AuthGatewayProfileRequestor_requestProfile(): User %s Not Found\n", nativeImsiString);
		sprintf(error_msg,"AuthGatewayProfileRequestor_requestProfile(): User %s Not Found\n", nativeImsiString);
		goto failure1;
	}
    fflush(stdout);

	// Now all Java Native conversions starts below
    int len;
    jbyteArray bytes=0;
    /*converting MSISDN string from msisdnreceived*/
    
	jstring javaPdpData;
	jstring javaChargingChar;
	jstring javaAccessRestrictionData;
	jstring javaMSISDN;
	
	len = strlen(gprsLUDataBufferArray[requestorID].msisdnreceived);
	if((*env)->EnsureLocalCapacity(env,len) < 0){
      LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE");
      sprintf(error_msg,"Out of Memory in NATIVE");
      goto failure1;
    }
     /*Writing into JAVA Log file*/
    sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): MSISDN: %s , length: %d\n",gprsLUDataBufferArray[requestorID].msisdnreceived,len );        
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
    bytes = (*env)->NewByteArray(env,len);
    if(bytes != NULL){
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)gprsLUDataBufferArray[requestorID].msisdnreceived);
      javaMSISDN = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);		
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): Could Not generate MSISDN from native String\n");
      goto failure1;
    }			
    
    jmethodID mid = (*env)->GetMethodID(env, cls, "setMsisdn", "(Ljava/lang/String;)V");
    if (mid == NULL) {
      sprintf(error_msg,"setMsisdn(Ljava/lang/String)V method not found");
      goto failure1;
    }	
    (*env)->CallObjectMethod(env, obj, mid, javaMSISDN);
    
    
	len = strlen(gprsLUDataBufferArray[requestorID].pdpData);
    if((*env)->EnsureLocalCapacity(env,len) < 0){
      LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE");
      sprintf(error_msg,"Out of Memory in NATIVE");
      goto failure1;
    }

        /*Writing into JAVA Log file*/
    sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): PDP Data: %s, length: %d\n",gprsLUDataBufferArray[requestorID].pdpData ,len );        
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);

    bytes = (*env)->NewByteArray(env,len);
    if(bytes != NULL){
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)gprsLUDataBufferArray[requestorID].pdpData);
      javaPdpData = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);		
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): Could Not generate PDP Data from native String\n");
      goto failure1;
    }			
    
    
    mid = (*env)->GetMethodID(env, cls, "setIMSI", "(Ljava/lang/String;)V");
    if (mid == NULL) {
      sprintf(error_msg,"setIMSI(Ljava/lang/String)V method not found");
      goto failure1;
    }	
    (*env)->CallObjectMethod(env, obj, mid, javaImsi);
    
    
    mid = (*env)->GetMethodID(env, cls, "setParam1", "(Ljava/lang/String;)V");
    if (mid == NULL) {
      sprintf(error_msg,"setParam1(Ljava/lang/String)V method not found");
      goto failure1;
    }	
    (*env)->CallObjectMethod(env, obj, mid, javaPdpData);
    
    sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): Charging Characteristics: %s \n",gprsLUDataBufferArray[requestorID].chargingChar);        
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
    
    len = strlen(gprsLUDataBufferArray[requestorID].chargingChar);
    bytes = (*env)->NewByteArray(env,len);
    if(bytes != NULL){
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)gprsLUDataBufferArray[requestorID].chargingChar);
      javaChargingChar = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);		
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): Failed to generate Charging Characteristics from native string\n");
      goto failure1;
    }
    
    mid = (*env)->GetMethodID(env, cls, "setParam2", "(Ljava/lang/String;)V");
    if (mid == NULL) {
      sprintf(error_msg,"setParam2(Ljava/lang/String)V method not found");
      goto failure1;
    }	
    (*env)->CallObjectMethod(env, obj, mid, javaChargingChar);
    
	sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): accessRestrictionData: %s \n", gprsLUDataBufferArray[requestorID].accessRestrictionData);        
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
    
    len = strlen(gprsLUDataBufferArray[requestorID].accessRestrictionData);
    bytes = (*env)->NewByteArray(env,len);
    if(bytes != NULL){
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)gprsLUDataBufferArray[requestorID].accessRestrictionData);
      javaAccessRestrictionData = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);		
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): Failed to generate accessRestrictionData from native string\n");
      goto failure1;
    }
    
    mid = (*env)->GetMethodID(env, cls, "setParam3", "(Ljava/lang/String;)V");
    if (mid == NULL) {
      sprintf(error_msg,"setParam3(Ljava/lang/String)V method not found");
      goto failure1;
    }	
    (*env)->CallObjectMethod(env, obj, mid, javaAccessRestrictionData);

	if (gprsLUDataBufferArray[requestorID].gprsDataListPresent == DATA_LIST_PRESENT) {
    	
    	jmethodID pdpMid = (*env)->GetMethodID(env, cls, "addPDPContext", "(Lcom/elitecore/coreeap/data/sim/PDPContext;)V");
    	if (pdpMid == NULL) {
			LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): addPDPContext(Lcom/elitecore/coreeap/data/sim/PDPContext;)V method not found\n");
			goto destroy;
		}
		int i = 0;
    	for ( i = 0 ; i<gprsLUDataBufferArray[requestorID].gprsDataList->count ; i++) {
    		jobject pdpContext = createPDPContext(env, gprsLUDataBufferArray[requestorID].gprsDataList->value[i],
    				Class_java_lang_String, MID_String_init);
    		if (pdpContext != NULL) {
				(*env)->CallObjectMethod(env, obj, pdpMid, pdpContext);
			}
    	}

    	
		
    }
	
    jmethodID successmid = (*env)->GetMethodID(env, cls, "onSuccess", "()V");
    
    if (successmid == NULL) {
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): onSuccess()V method not found\n");
      goto destroy;
    }
    
    (*env)->CallObjectMethod(env, obj, successmid);
    
    goto destroy;
    
    failure1:
    LOG_MSG(LOG_LEVEL_WARN, "AuthGatewayProfileRequestor_requestProfile(): Failure Reason : %s\n", error_msg);
	fflush(stdout);
    jstring javaErrorMsg;
    
    if((*env)->EnsureLocalCapacity(env,2) < 0){
      LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE");
      goto destroy;
    }
    
    len = strlen(error_msg);
    
    bytes = (*env)->NewByteArray(env,len);
    
    if(bytes != NULL){
      
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
      
      javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      
      (*env)->DeleteLocalRef(env,bytes);
      
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "Could Not Call Failure Event from native String");
      goto destroy;
    }
    /* Get a reference to objÆs class */ /* Already checked into begining */
    /*cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
      LOG_MSG("Object Class not found");	
    }*/
    
    
    jmethodID newmid = (*env)->GetMethodID(env, cls, "onFailure", "(Ljava/lang/String;)V");
    
    
    if (newmid == NULL) {
      LOG_MSG(LOG_LEVEL_ERROR, "onFailure(Ljava/lang/String;)V method not found");
      goto destroy;
    }
    
    (*env)->CallObjectMethod(env, obj, newmid,javaErrorMsg);
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
    (*env)->ReleaseStringUTFChars(env, javaImsi, nativeImsiString);

	fflush(stdout);
	return gprsLUDataBufferArray[requestorID].nResult;
}

#else

JNIEXPORT int JNICALL Java_com_elitecore_aaa_core_drivers_AuthGatewayProfileRequestor_requestProfile
  (JNIEnv *env, jobject obj, jstring javaImsi, jlong javaRequestTimeout, jint requestorID, jint driverID){
    const char *nativeImsiString = (*env)->GetStringUTFChars(env, javaImsi, 0);
	
    // Conversion from miliseconds to microseconds
	unsigned long requestTimeout = javaRequestTimeout * 1000; 
    
	// requestTimeout is divided by ASYNC_POOLING_ITR because AAA will wait for response in ASYNC_POOLING_ITR equal slot of configured timeout. 
	// At the end of each slot it will check weather response is received or not
	unsigned long timeoutSlot = requestTimeout / ASYNC_POOLING_ITR;
    
	char            log_msg[1024], error_msg[1024];
    //int             iRet = 0;    
    int             iiRet = 0;
    unsigned int    hPIRequest = 0;
    int waitLoopCount = 0;

	if ( timeoutSlot < 1000) timeoutSlot  = 1000;
	memset (isdBufferArray[requestorID].msisdnreceived, '\0', MSISDNSIZE);
	isdBufferArray[requestorID].responseReceived = 0;
	isdBufferArray[requestorID].customerStatus[0] = '\0';
	isdBufferArray[requestorID].vlrSubscriptionInfoPresent[0] = '\0';
	isdBufferArray[requestorID].serviceProfile[0] = '\0';
	isdBufferArray[requestorID].nResult = ULCM_G_GATEWAY_ERROR;
	
    /* Get a reference to object class */
    jclass cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): Object Class not found\n");
      sprintf(error_msg,"AuthGatewayProfileRequestor_requestProfile(): Object Class not found");
      goto failure1;
    }
    
    jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
    if(Class_java_lang_String == NULL ){
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): String Class not found\n");
	  sprintf(error_msg,"AuthGatewayProfileRequestor_requestProfile(): java/lang/String Class not found");
      goto destroy;
    }
    jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
    if(MID_String_init == NULL){
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): String<init> method not found\n");
      sprintf(error_msg,"java/lang/String<init>([B)V method not found");
      goto destroy;
    }
    
    /*Writing into JAVA Log file*/
    sprintf(log_msg,"Profile lookup started for IMSI : %s",nativeImsiString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);

	LOG_MSG( LOG_LEVEL_INFO, "AuthGatewayProfileRequestor_requestProfile(): CallbackCookie: %u IMSI: %s\n", requestorID, nativeImsiString );
   
  
    fflush(stdout);
    if (!(pAvailableCaps == (pAvailableCaps | ULCM_L_MG_CAPS_AUTHORIZATION)))
    {
		LOG_MSG(LOG_LEVEL_ERROR, "FATAL error!! Available capabilities did not match. This should never Happen!!!\n");
		return isdBufferArray[requestorID].nResult;
	}
	    /*Writing into JAVA Log file*/
	  sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): Calling ulcm_mg_t_RequestAuthorizationInfo for IMSI: %s Using Handle: %d\n", nativeImsiString, driverModHandles[driverID]);
      logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);

      iiRet = ulcm_mg_t_RequestAuthorizationInfo( driverModHandles[driverID],
						  nativeImsiString, &hPIRequest,
						  pAuthorizationCallback,
						  requestorID, driverID);
    
    if ( iiRet != 0 )
    {
	  LOG_MSG(LOG_LEVEL_ERROR, "requestProfile() error 0x%x(%d) for %s\n", iiRet,iiRet, nativeImsiString);
      sprintf(error_msg,"AuthGatewayProfileRequestor_requestProfile() error 0x%x(%d) for %s\n", iiRet,iiRet, nativeImsiString);
	  isdBufferArray[requestorID].nResult = iiRet;
      goto failure1;      
    }else{
      LOG_MSG(LOG_LEVEL_INFO, "requestProfile() success for %s, Requestor ID: %d, \n", nativeImsiString, requestorID);    
      sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile() success for %s\n", nativeImsiString);    
      logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
    }
	fflush(stdout);

	waitLoopCount = 0;
    while( (waitLoopCount < ASYNC_POOLING_ITR) && (isdBufferArray[requestorID].responseReceived == 0)){		 
	LOG_MSG(LOG_LEVEL_TRACE, "requestProfile(): Requestor ID: %d, Iteration: %d for IMSI %s\n", requestorID, waitLoopCount, nativeImsiString);
	usleep(timeoutSlot); // sleep before we check status of responseReceived flag  
	  waitLoopCount++;
    }

    sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): Waited: %lu miliseconds for IMSI %s\n",((waitLoopCount) * (timeoutSlot / 1000)), nativeImsiString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);       
    
	if(isdBufferArray[requestorID].responseReceived == 0){
	  ulcm_mg_t_CancelAuthorizationRequest (driverModHandles[driverID], hPIRequest, driverID);
      LOG_MSG(LOG_LEVEL_WARN, "requestProfile: Requestor ID: %d, Timeout!!! No Response received from the MAP Gateway for IMSI: %s\n",requestorID, nativeImsiString);
      sprintf(error_msg,"AuthGatewayProfileRequestor_requestProfile(): Timeout!!! No Response received from the MAP Gateway for IMSI: %s\n", nativeImsiString);
      isdBufferArray[requestorID].nResult = EC_TIMEOUT;
      goto timeout;
    } else {
		LOG_MSG(LOG_LEVEL_TRACE, "requestProfile: Requestor ID: %d, Response received from the MAP Gateway for Req ID: %d\n",requestorID);
	}
	
	if (isdBufferArray[requestorID].nResult != 0 && isdBufferArray[requestorID].nResult != 65536 && isdBufferArray[requestorID].nResult != 131072){
		LOG_MSG(LOG_LEVEL_WARN, "AuthGatewayProfileRequestor_requestProfile(): User %s Not Found\n", nativeImsiString);
		sprintf(error_msg,"AuthGatewayProfileRequestor_requestProfile(): User %s Not Found\n", nativeImsiString);
		goto failure1;
	}
    fflush(stdout);

	// Now all Java Native conversions starts below
    int len;
    jbyteArray bytes=0;
    /*converting MSISDN string from msisdnreceived*/
    jstring javaMSISDN;
    jstring javaCustomerStatus;
	jstring javaVlrSubscriptionInfoPresent;
	jstring javaServiceProfile;
     len = strlen(isdBufferArray[requestorID].msisdnreceived);
    if((*env)->EnsureLocalCapacity(env,len) < 0){
      LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE");
      sprintf(error_msg,"Out of Memory in NATIVE");
      goto failure1;
    }
    
    
        /*Writing into JAVA Log file*/
    sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): MSISDN: %s , length: %d\n",isdBufferArray[requestorID].msisdnreceived ,len );        
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);

    bytes = (*env)->NewByteArray(env,len);
    if(bytes != NULL){
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)isdBufferArray[requestorID].msisdnreceived);
      javaMSISDN = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);		
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): Could Not generate MSISDN from native String\n");
      goto failure1;
    }			
    
    jmethodID mid = (*env)->GetMethodID(env, cls, "setMsisdn", "(Ljava/lang/String;)V");
    if (mid == NULL) {
      sprintf(error_msg,"setMsisdn(Ljava/lang/String)V method not found");
      goto failure1;
    }	
    (*env)->CallObjectMethod(env, obj, mid, javaMSISDN);
    
    mid = (*env)->GetMethodID(env, cls, "setIMSI", "(Ljava/lang/String;)V");
    if (mid == NULL) {
      sprintf(error_msg,"setIMSI(Ljava/lang/String)V method not found");
      goto failure1;
    }	
    (*env)->CallObjectMethod(env, obj, mid, javaImsi);
    
    
    sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): Subscriber Status: %c \n",isdBufferArray[requestorID].customerStatus[0]);        
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
    
    len = 1;
    bytes = (*env)->NewByteArray(env,len);
    if(bytes != NULL){
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)isdBufferArray[requestorID].customerStatus);
      javaCustomerStatus = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);		
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): Failed to generate Subscriber Status from native string\n");
      goto failure1;
    }
    
    mid = (*env)->GetMethodID(env, cls, "setCustomerStatus", "(Ljava/lang/String;)V");
    if (mid == NULL) {
      sprintf(error_msg,"setCustomerStatus(Ljava/lang/String)V method not found");
      goto failure1;
    }	
    (*env)->CallObjectMethod(env, obj, mid, javaCustomerStatus);
    
	sprintf(log_msg, "AuthGatewayProfileRequestor_requestProfile(): Subscriber Type: %s \n", isdBufferArray[requestorID].vlrSubscriptionInfoPresent);        
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
    
    len = 1;
    bytes = (*env)->NewByteArray(env,len);
    if(bytes != NULL){
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)isdBufferArray[requestorID].vlrSubscriptionInfoPresent);
      javaVlrSubscriptionInfoPresent = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);		
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): Failed to generate vlrSubscriptionInfoPresent from native string\n");
      goto failure1;
    }
    
    mid = (*env)->GetMethodID(env, cls, "setVlrSubscriptionInfoPresent", "(Ljava/lang/String;)V");
    if (mid == NULL) {
      sprintf(error_msg,"setVlrSubscriptionInfoPresent(Ljava/lang/String)V method not found");
      goto failure1;
    }	
    (*env)->CallObjectMethod(env, obj, mid, javaVlrSubscriptionInfoPresent);
	
	len = strlen(isdBufferArray[requestorID].serviceProfile);
	 if((*env)->EnsureLocalCapacity(env,len) < 0){
      LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE");
      sprintf(error_msg,"Out of Memory in NATIVE");
      goto failure1;
    }
	
	bytes = (*env)->NewByteArray(env,len);
	if(bytes != NULL){
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)isdBufferArray[requestorID].serviceProfile);
      javaServiceProfile = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);		
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): Failed to generate serviceProfile from native string\n");
      goto failure1;
    }
    
    mid = (*env)->GetMethodID(env, cls, "setServiceProfile", "(Ljava/lang/String;)V");
    if (mid == NULL) {
      sprintf(error_msg,"setServiceProfile(Ljava/lang/String)V method not found");
      goto failure1;
    }	
    (*env)->CallObjectMethod(env, obj, mid, javaServiceProfile);
	
    jmethodID successmid = (*env)->GetMethodID(env, cls, "onSuccess", "()V");
    
    if (successmid == NULL) {
      LOG_MSG(LOG_LEVEL_ERROR, "AuthGatewayProfileRequestor_requestProfile(): onSuccess()V method not found\n");
      goto destroy;
    }
    
    (*env)->CallObjectMethod(env, obj, successmid);
    
    goto destroy;
    
    failure1:
    LOG_MSG(LOG_LEVEL_WARN, "AuthGatewayProfileRequestor_requestProfile(): Failure Reason : %s\n", error_msg);
	fflush(stdout);
    jstring javaErrorMsg;
    
    if((*env)->EnsureLocalCapacity(env,2) < 0){
      LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE");
      goto destroy;
    }
    
    len = strlen(error_msg);
    
    bytes = (*env)->NewByteArray(env,len);
    
    if(bytes != NULL){
      
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
      
      javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      
      (*env)->DeleteLocalRef(env,bytes);
      
    }else{
      LOG_MSG(LOG_LEVEL_ERROR, "Could Not Call Failure Event from native String");
      goto destroy;
    }
    /* Get a reference to objÆs class */ /* Already checked into begining */
    /*cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
      LOG_MSG("Object Class not found");	
    }*/
    
    
    jmethodID newmid = (*env)->GetMethodID(env, cls, "onFailure", "(Ljava/lang/String;)V");
    
    
    if (newmid == NULL) {
      LOG_MSG(LOG_LEVEL_ERROR, "onFailure(Ljava/lang/String;)V method not found");
      goto destroy;
    }
    
    (*env)->CallObjectMethod(env, obj, newmid,javaErrorMsg);
    goto destroy;

	timeout:
	
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
    (*env)->ReleaseStringUTFChars(env, javaImsi, nativeImsiString);

	fflush(stdout);
	return isdBufferArray[requestorID].nResult;
}
#endif

JNIEXPORT void Java_com_elitecore_aaa_core_drivers_MAPGWRequestorConnectionPool_terminateAuthGWConnection
  (JNIEnv *env, jobject obj, jstring javaRemoteHost, jstring javaLocalHost, jint driverID) {
    const char *nativeRemoteHostString = (*env)->GetStringUTFChars(env, javaRemoteHost, 0);
    const char *nativeLocalHostString = (*env)->GetStringUTFChars(env, javaLocalHost, 0);
    char            error_msg[1024], log_msg[1024];

    /* Get a reference to object class */
    jclass cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
      LOG_MSG(LOG_LEVEL_ERROR, "Object Class not found");
      sprintf(error_msg,"Object Class not found");
      goto failure1;
    }

    jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
    if(Class_java_lang_String == NULL ) {
      LOG_MSG(LOG_LEVEL_ERROR, "String Class not found ");
      sprintf(error_msg,"java/lang/String Class not found");
      goto destroy;
    }
    jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
    if(MID_String_init == NULL) {
      LOG_MSG(LOG_LEVEL_ERROR, "Method not fonud for String Class ");
      sprintf(error_msg,"java/lang/String<init>([B)V method not found");
      goto destroy;
    }
    
    /*Writing into JAVA Log file*/
    sprintf(log_msg,"Terminate AuthG/W communication with following configuration\n%s\n%s",nativeLocalHostString,nativeRemoteHostString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);    

	ulcm_mg_t_ModuleTerminate( driverModHandles[driverID], driverID );

	goto destroy;

    failure1:	
    LOG_MSG(LOG_LEVEL_ERROR, "Failure happen !!, Reason : %s\n", error_msg);	
    jstring javaErrorMsg;

    if((*env)->EnsureLocalCapacity(env,2) < 0) {
      LOG_MSG(LOG_LEVEL_ERROR, "Out of Memory in NATIVE");
      goto destroy;
    }

	int len = strlen(error_msg);

    jbyteArray bytes = (*env)->NewByteArray(env,len);
    if(bytes != NULL) {
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
      javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);
    } else {
      LOG_MSG(LOG_LEVEL_ERROR, "Could Not Call Failure Event from native String");
      goto destroy;
    }
    
    jmethodID newmid = (*env)->GetMethodID(env, cls, "onFailure", "(Ljava/lang/String;)V");
    if (newmid == NULL) {
      LOG_MSG(LOG_LEVEL_ERROR, "onFailure(Ljava/lang/String;)V method not found");
      goto destroy;
    }
    (*env)->CallObjectMethod(env, obj, newmid,javaErrorMsg);
    
    destroy:    
    (*env)->ReleaseStringUTFChars(env, javaRemoteHost, nativeRemoteHostString);
    (*env)->ReleaseStringUTFChars(env, javaLocalHost, nativeLocalHostString);
}





JNIEXPORT void JNICALL Java_com_elitecore_aaa_core_drivers_MAPGWRequestorConnectionPool_shutdownSctpAuthConnection
  (JNIEnv *env, jobject jobj, jint driverID) {
  
  shutdownSctpAuth(driverID);
   
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
    LOG_MSG(LOG_LEVEL_INFO,  "\nResult received for requestIMSI(), result is 0x%x,  \n", nResult );
    LOG_MSG(LOG_LEVEL_INFO,  "IMSI is : %s \n ", pIMSI );
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
postAuthorization( unsigned int hCallbackCookie, unsigned int nResult, const char *pServiceProfile, const char *pszmsisdn, int subscriberStatus , int vlrSubscriptionInfoPresent )
{
	LOG_MSG( LOG_LEVEL_INFO, "postAuthorization(): CallbackCookie: %u, Result: 0x%x, \n", hCallbackCookie, nResult );
    LOG_MSG( LOG_LEVEL_INFO, "MSISDN=%s, Service Profile=%s\n", pszmsisdn, pServiceProfile );
	fflush(stdout);
	
    char            indx, indx2;
    int len = strlen(pszmsisdn);
    int i=0;
    for( i = 0; i< len ; ){
      indx =  *( pszmsisdn + i++ ) ;
      indx2 = *( pszmsisdn + i++) ;
      sprintf(isdBufferArray[hCallbackCookie].msisdnreceived,"%s%c",isdBufferArray[hCallbackCookie].msisdnreceived,indx2);
      sprintf(isdBufferArray[hCallbackCookie].msisdnreceived,"%s%c",isdBufferArray[hCallbackCookie].msisdnreceived,indx);
    }
	sprintf(isdBufferArray[hCallbackCookie].serviceProfile,"%s", pServiceProfile);
    if (subscriberStatus == 1){
    	isdBufferArray[hCallbackCookie].customerStatus[0] = 'I';
    } else {
    	isdBufferArray[hCallbackCookie].customerStatus[0] = 'A';
    }
	if (vlrSubscriptionInfoPresent == 1) {
		isdBufferArray[hCallbackCookie].vlrSubscriptionInfoPresent[0] = '1';
	} else {
		isdBufferArray[hCallbackCookie].vlrSubscriptionInfoPresent[0] = '0';
	}
	isdBufferArray[hCallbackCookie].nResult = nResult;
	
    LOG_MSG( LOG_LEVEL_INFO, "postAuthorization(): Converted MSISDN: %s Subsciber Status: %c vlrSubscriptionInfoPresent: %c\n", isdBufferArray[hCallbackCookie].msisdnreceived, isdBufferArray[hCallbackCookie].customerStatus[0], isdBufferArray[hCallbackCookie].vlrSubscriptionInfoPresent[0]);
	isdBufferArray[hCallbackCookie].responseReceived = 1;
    fflush(stdout);
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
	      unsigned int nResult, const ulcm_mg_triplet_t * pTripletBuffer, unsigned int tripletCount, const char *pServiceProfile )
{
    int             i = 0;

    LOG_MSG( LOG_LEVEL_INFO, "postTriplets() result: 0x%x  \n", nResult );
    LOG_MSG( LOG_LEVEL_INFO, "Number of triplets: %d \n", tripletCount );
    LOG_MSG( LOG_LEVEL_INFO, "Service Profile (authorization info): %s\n", pServiceProfile );
	fflush(stdout);
	
    if ( pTripletBuffer != NULL )
    {
	  for ( i = 0; i < tripletCount; i++ )
	  {
	    LOG_MSG( LOG_LEVEL_INFO, "\nRand: " );
	    decodeIMSI( pTripletBuffer->RAND, 16 );
	    LOG_MSG( LOG_LEVEL_INFO, "\nSres: " );
	    decodeIMSI( pTripletBuffer->SRES, 4 );
	    LOG_MSG( LOG_LEVEL_INFO, "\nKc: " );
	    decodeIMSI( pTripletBuffer->Kc, 8 );
	    pTripletBuffer++;
	  }
    }
    else
    {
	  LOG_MSG( LOG_LEVEL_WARN, "postTriplets(): Triplet Buffer does not contain anything....\n" );
    }
    fflush(stdout);
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
	      unsigned int nResult, const ulcm_mg_quintuplet_t * pQuintupletBuffer, unsigned int nQuintupletCount, const char *pServiceProfile )
{
    int             i = 0;

    fflush(stdout);
    LOG_MSG( LOG_LEVEL_INFO, "postQuintets(): Result: 0x%x ", nResult );
    LOG_MSG( LOG_LEVEL_INFO, "Number of quintets: %d  ", nQuintupletCount );
    LOG_MSG( LOG_LEVEL_INFO, "Service Profile (authorization info): %s \n", pServiceProfile );
    fflush(stdout);
    if ( pQuintupletBuffer != NULL )
    {
	  for ( i = 0; i < nQuintupletCount; i++ ) {
	    LOG_MSG( LOG_LEVEL_INFO, "\nRAND: " );
	    decodeIMSI( pQuintupletBuffer->RAND, 16 );
	    LOG_MSG( LOG_LEVEL_INFO, "\nXRES: " );
	    decodeIMSI( pQuintupletBuffer->XRES, 16 );
	    LOG_MSG( LOG_LEVEL_INFO, "\nCK: " );
	    decodeIMSI( pQuintupletBuffer->CK, 16 );
	    LOG_MSG( LOG_LEVEL_INFO, "\nIK: " );
	    decodeIMSI( pQuintupletBuffer->IK, 16 );
	    LOG_MSG( LOG_LEVEL_INFO, "\nAUTN: " );
	    decodeIMSI( pQuintupletBuffer->AUTN, 18 );
	    pQuintupletBuffer++;

	  }
    }
    else {
	  LOG_MSG( LOG_LEVEL_WARN, "quintet Buffer does not contain anything....\n" );
    }
    fflush(stdout);
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
	LOG_MSG( LOG_LEVEL_INFO, "\postUpdateGprs(): CallbackCookie: %u, Result: 0x%x, \n", hCallbackCookie, nResult );
	fflush(stdout);
	int i = 0;
	char tempChargingchar[CHARGIN_CHAR_SIZE];
	char tempPDPData[PDP_DATA_SIZE];
	int iChargingChar = 0, iAccessRestrData = 0;
	char *delim = "###";
	char dig[2] = "\0";
	char digAccessRestrData[2] = "\0";
    
    // Below parameters, hlr-number and add-capability, are not required right now. In future, if required, Below code can be uncommented and the values 
    // can be assigned to some member of gprsLUDataBufferArray
    
    //printf( "\nhlr-Number is : ");
    //decodeIMSI( pupdateGprsResponse->updateGprsLocationRes.hlr_Number.value, pupdateGprsResponse->updateGprsLocationRes.hlr_Number.length );
    //printf( "\n" );
    
    //if ( ( pupdateGprsResponse->updateGprsLocationRes.bit_mask & UpdateGprsLocationRes_v3_add_Capability_present ) != 0)
    //{
	//	printf ( "add-Capability is present \n" );
	//}
	
	if ( ( pupdateGprsResponse->insertSubscriberDataArg.bit_mask & InsertSubscriberDataArg_v3_accessRestrictionData_present ) != 0)
	{
		iAccessRestrData = decodeAccessRestrictionData( pupdateGprsResponse->insertSubscriberDataArg.accessRestrictionData.value,
			gprsLUDataBufferArray[hCallbackCookie].accessRestrictionData);
	}
	
	if ( ( pupdateGprsResponse->insertSubscriberDataArg.bit_mask & InsertSubscriberDataArg_v3_msisdn_present ) != 0)
	{
		char pmsisdn[MSISDNSIZE];
		snprintf( pmsisdn, MSISDNSIZE, "%s", "0000000000" );
		imsi2ascii( pupdateGprsResponse->insertSubscriberDataArg.msisdn.value, pupdateGprsResponse->insertSubscriberDataArg.msisdn.length, pmsisdn);
		char indx, indx2;
		int len = strlen(pmsisdn);
		for( i = 0; i< len ; ){
			indx =  *( pmsisdn + i++) ;
			indx2 = *( pmsisdn + i++) ;
			sprintf(gprsLUDataBufferArray[hCallbackCookie].msisdnreceived,"%s%c",gprsLUDataBufferArray[hCallbackCookie].msisdnreceived,indx2);
			sprintf(gprsLUDataBufferArray[hCallbackCookie].msisdnreceived,"%s%c",gprsLUDataBufferArray[hCallbackCookie].msisdnreceived,indx);
	}

	}
	
	if (( pupdateGprsResponse->insertSubscriberDataArg.bit_mask & InsertSubscriberDataArg_v3_chargingCharacteristics_present ) != 0)
		{	
			memset(tempChargingchar, '\0', CHARGIN_CHAR_SIZE);
			iChargingChar = 0;
			iChargingChar = decodeCharginChar(pupdateGprsResponse->insertSubscriberDataArg.chargingCharacteristicsOuter.value);
			dig[0] = (char)(((int)'0')+iChargingChar);
			dig[1] = '\0';
			strcat(gprsLUDataBufferArray[hCallbackCookie].chargingChar, "OUTER");
			strcat(gprsLUDataBufferArray[hCallbackCookie].chargingChar, dig);
			strcat(gprsLUDataBufferArray[hCallbackCookie].chargingChar, delim);
			
		}
	
	if ( ( pupdateGprsResponse->insertSubscriberDataArg.bit_mask & InsertSubscriberDataArg_v3_gprsSubscriptionData_present ) != 0) 
	{
		if ((pupdateGprsResponse->insertSubscriberDataArg.gprsSubscriptionData.bit_mask & GPRSSubscriptionData_completeDataListIncluded_present) != 0 )
		{					
			gprsLUDataBufferArray[hCallbackCookie].gprsDataListPresent = DATA_LIST_PRESENT;
			gprsLUDataBufferArray[hCallbackCookie].gprsDataList = &pupdateGprsResponse->insertSubscriberDataArg.gprsSubscriptionData.gprsDataList;
			for ( i = 0 ; i < pupdateGprsResponse->insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.count ; i++) {
				
					strcat(gprsLUDataBufferArray[hCallbackCookie].pdpData, (pupdateGprsResponse->insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[i].apn.value + 1));
					strcat(gprsLUDataBufferArray[hCallbackCookie].pdpData, "###");
					
				if ( ( pupdateGprsResponse->insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[i].bit_mask & PDP_Context_pdp_ChargingCharacteristics_present ) != 0 )
				{
					memset(tempChargingchar, '\0', CHARGIN_CHAR_SIZE);
					iChargingChar = 0;
					
					iChargingChar = decodeCharginChar(pupdateGprsResponse->insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[i].pdp_ChargingCharacteristics.value);
					dig[0] = (char)(((int)'0')+iChargingChar);
					dig[1] = '\0';
					strcat(gprsLUDataBufferArray[hCallbackCookie].chargingChar, (pupdateGprsResponse->insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[i].apn.value + 1));
					strcat(gprsLUDataBufferArray[hCallbackCookie].chargingChar, dig);
					strcat(gprsLUDataBufferArray[hCallbackCookie].chargingChar, delim);
						
				} else {
					strcat(gprsLUDataBufferArray[hCallbackCookie].chargingChar, (pupdateGprsResponse->insertSubscriberDataArg.gprsSubscriptionData.gprsDataList.value[i].apn.value + 1));
					strcat(gprsLUDataBufferArray[hCallbackCookie].chargingChar, delim);
				}
			}
		}
	}
	
	gprsLUDataBufferArray[hCallbackCookie].nResult = nResult;
	gprsLUDataBufferArray[hCallbackCookie].responseReceived = 1;
    fflush(stdout);
}


void
decodeData( const unsigned char *IMSI, int len , char *toIMSI)
{
	char            indx, indx2;
	int i = 0;
	char tmpMsg[MSGSIZE];
    for ( i = 0; i < len; i++ )
    {
	indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	
	sprintf(tmpMsg, "%s%x", toIMSI, indx2 );
	sprintf(toIMSI, "%s%x", tmpMsg,indx);
    }
    //printf("toIMSI(%d) values \n", strlen(toIMSI));
}


int
decodeCharginChar( const unsigned char *IMSI)
{
	int             i = 0, iChargingChar = 0;
	char ch, ch1;
	int _10000000 = 128, _01000000 = 64, _00100000 = 32, _00010000 = 16, _00001000 = 8, _00000100 = 4, _00000010 = 2, _00000001 = 1;
	
	if (strlen(IMSI) > 0)
		ch =IMSI[0];
	else 	
		return 0;
		
	if(ch >= 65 && ch <= 70){
		ch1 = ch - 55;
	}else if(ch >= 97 && ch <= 102){
		ch1 = ch - 87;
	}else
		ch1 = ch - 48;

	if ((ch1 & _00001000) > 0) {
		iChargingChar += 8;
	}
	if ((ch1 & _00000100) > 0) {
		iChargingChar += 4;
	}
	if ((ch1 & _00000010) > 0) {
		iChargingChar += 2;
	}
	if ((ch1 & _00000001) > 0) {
		iChargingChar += 1;
	}

	return iChargingChar;
	
}


int
decodeAccessRestrictionData( const unsigned char *data, char *toData)
{
	int             i = 0, iAccessRestrData = 0;
	char ch, ch1;
	char *chAccessRestrData = malloc(sizeof(char)*32);
	
	int _10000000 = 128, _01000000 = 64, _00100000 = 32, _00010000 = 16, _00001000 = 8, _00000100 = 4, _00000010 = 2, _00000001 = 1;
	
	ch1 =data[0];
	
	if ((ch1 & _10000000) > 0) {
		strcat(toData, "UTY");
		iAccessRestrData += 128;
	} else {
		strcat(toData, "UTN");
	}
	strcat(toData, "#");
	
	if ((ch1 & _01000000) > 0) {
		strcat(toData, "GEY");
		iAccessRestrData += 64;
	} else {
		strcat(toData, "GEN");
	}
	strcat(toData, "#");
	
	if ((ch1 & _00100000) > 0) {
		strcat(toData, "GAY");
		iAccessRestrData += 32;
	} else {
		strcat(toData, "GAN");
	}
	strcat(toData, "#");
	
	if ((ch1 & _00010000) > 0) {
		strcat(toData, "IY");
		iAccessRestrData += 16;
	} else {
		strcat(toData, "IN");
	}
	strcat(toData, "#");
	
	if ((ch1 & _00001000) > 0) {
		strcat(toData, "EUY");
		iAccessRestrData += 8;
	} else {
		strcat(toData, "EUN");
	}
	strcat(toData, "#");
	
	if ((ch1 & _00000100) > 0) {
		strcat(toData, "HOY");
		iAccessRestrData += 4;
	} else {
		strcat(toData, "HON");
	}
	strcat(toData, "#");
	
	if ((ch1 & _00000010) > 0) {
		strcat(toData, "X");
		iAccessRestrData += 2;
	}
	if ((ch1 & _00000001) > 0) {
		strcat(toData, "X\0");
		iAccessRestrData += 1;
	}
	
	toData = chAccessRestrData;
	return iAccessRestrData;
	
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
    LOG_MSG( LOG_LEVEL_INFO, "\n0x" );
    for ( i = 0; i < len; i++ ) {
	  indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
	  indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
	  printf("%x", indx );
	  printf("%x", indx2 );
    }
    fflush(stdout);

}
