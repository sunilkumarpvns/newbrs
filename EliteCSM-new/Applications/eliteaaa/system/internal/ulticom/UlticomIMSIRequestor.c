#include <jni.h>
#include <stdio.h>
#include <signal.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include "clientMod.h"
#include "smsClientMod.h"
#include "com_elitecore_aaa_core_drivers_SMSGatewayIMSIRequestor.h"

unsigned int modHandleSMS = 0;
ulcm_sms_t_postIMSIResults *pFnCallback_RI = NULL;
void *routingInfoThread( void *tmp );
void decodeIMSI( unsigned char *IMSI, int len );
void postIMSI( int hCallbackCookie, int nResult, const char *pszIMSI );
void logInJava(JNIEnv *env, jobject obj,jclass objectClass, jclass stringClass, jmethodID stringInit, char* nativeMessage);

#define MAX_IMSI_LENGTH 16
#define MAX_REQ 100

#define LOG_LEVEL_NONE 0
#define LOG_LEVEL_ERROR 1 
#define LOG_LEVEL_WARN 2
#define LOG_LEVEL_INFO 3
#define LOG_LEVEL_DEBUG 4
#define LOG_LEVEL_TRACE 5
#define LOG_LEVEL_ALL 6

int logLevel = LOG_LEVEL_ALL;

#define LOG_MSG(msgLevel, format, logMsg...) if (logLevel >= msgLevel) { fprintf(stdout, format, ## logMsg ); }

typedef struct ImsiInfo {

	char IMSI[MAX_IMSI_LENGTH];
	unsigned int responseReceived;

} ImsiInfo ;

ImsiInfo ImsiInfoBuffer[MAX_REQ];

void sigintCatcher( int signum ) {
    LOG_MSG( LOG_LEVEL_INFO, "came to sigint\n:" );
    ulcm_sms_t_ModuleTerminate( modHandleSMS );
    exit( 0 );

}

JNIEXPORT void JNICALL Java_com_elitecore_aaa_core_drivers_SMSGatewayIMSIRequestor_init
(JNIEnv *env, jobject obj, jstring javaRemoteHost, jstring javaLocalHost, jint javaLogLevel){
	logLevel = javaLogLevel;
	char error_msg[1024], log_msg[1024];
	const char *nativeLocalHostString = (*env)->GetStringUTFChars( env, javaLocalHost, 0 );	
	const char *nativeRemoteHostString = (*env)->GetStringUTFChars( env, javaRemoteHost, 0 );
	
	jclass cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
		LOG_MSG(LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_init: Object Class not found");
		sprintf(error_msg,"SMSGatewayIMSIRequestor_init: Object Class not found");
		goto failure;
    }
    
    jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
    if(Class_java_lang_String == NULL ){
		LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_init: String Class not found ");
		sprintf(error_msg,"SMSGatewayIMSIRequestor_init: java/lang/String Class not found");
		goto failure;
    }
	
    jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
    if(MID_String_init == NULL){
		LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_init: Method not fonud for String Class ");
		sprintf(error_msg,"SMSGatewayIMSIRequestor_init: java/lang/String<init>([B)V method not found");
		goto failure;
    }
	
	LOG_MSG( LOG_LEVEL_INFO, "Initializing SMS GW With Local Host: %s Remote Host: %s\n", nativeLocalHostString, nativeRemoteHostString );
	sprintf(log_msg,"Initializing SMS GW With Local Host: %s Remote Host: %s\n", nativeLocalHostString, nativeRemoteHostString );
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
	pFnCallback_RI = ( ulcm_sms_t_postIMSIResults * ) & postIMSI;
	
	unsigned int    pAvailableCaps = 0;
    int             iRet = 0;
		
    //signal( SIGINT, sigintCatcher );
	iRet = ulcm_sms_t_ModuleInit( 1, nativeRemoteHostString, nativeLocalHostString,  &pAvailableCaps, &modHandleSMS );

    if ( iRet != 0 ) {
		LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_init: Error in ulcm_sms_t_ModuleInit() iRet = %d\n", iRet );
		sprintf(error_msg,"SMSGatewayIMSIRequestor_init: Error in ulcm_sms_t_ModuleInit() iRet = %d\n", iRet );
		goto failure;
    } 
	
	goto destroy;
	failure:
		LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_init: Failure Reason : %s\n", error_msg);
		logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,error_msg);
		fflush(stdout);
		
		jstring javaErrorMsg;
		int len = strlen(error_msg);
		if((*env)->EnsureLocalCapacity(env,len) < 0){
			LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_init: Out of Memory in NATIVE");
			goto destroy;
		}
		jbyteArray bytes = (*env)->NewByteArray(env,len);
		if(bytes != NULL){
			(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
			javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
			(*env)->DeleteLocalRef(env,bytes);
        }else{
			LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_init: Could Not Call Failure Event from native String");
			goto destroy;
		}
		
		jmethodID onFailureMID =  (*env)->GetMethodID(env, cls, "onFailure", "(Ljava/lang/String;)V");
		if (onFailureMID == NULL) {
			LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_init: onFailure(Ljava/lang/String;)V method not found");
			goto destroy;
		}		
		(*env)->CallObjectMethod(env, obj, onFailureMID, javaErrorMsg);
	
	destroy:
		(*env)->ReleaseStringUTFChars(env, javaRemoteHost, nativeRemoteHostString);
		(*env)->ReleaseStringUTFChars(env, javaLocalHost, nativeLocalHostString);
}


JNIEXPORT void JNICALL Java_com_elitecore_aaa_core_drivers_SMSGatewayIMSIRequestor_requestIMSI
( JNIEnv *env,  jobject obj, jstring javaMsisdn, jlong javaRequestTimeout, jint requestorID) {
	char error_msg[1024];
	char log_msg[1024];

	const char *nativeMsisdnString = (*env)->GetStringUTFChars( env, javaMsisdn, 0 );
	// To get microseconds from miliseonds instead of multiplying with 1000 here it is multiplied with 100
	// Because after every 1/10th Request timeout time check for response is done
    unsigned long requestTimeout = javaRequestTimeout * 100;
	int waitLoopCount = 0;
	
	memset (ImsiInfoBuffer[requestorID].IMSI, '\0', MAX_IMSI_LENGTH);
	ImsiInfoBuffer[requestorID].responseReceived = 0;
	
	 
	/* Get a reference to object class */
    jclass cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
		LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_requestIMSI: Object Class not found");
		sprintf(error_msg,"SMSGatewayIMSIRequestor_requestIMSI: Object Class not found");
		goto failure;
    }
    
    jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
    if(Class_java_lang_String == NULL ){
		LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_requestIMSI: String Class not found ");
		sprintf(error_msg,"SMSGatewayIMSIRequestor_requestIMSI: java/lang/String Class not found");
		goto failure;
    }
	
    jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
    if(MID_String_init == NULL){
		LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_requestIMSI: Method not fonud for String Class ");
		sprintf(error_msg,"SMSGatewayIMSIRequestor_requestIMSI: java/lang/String<init>([B)V method not found");
		goto failure;
    }
	
	/*Writing into JAVA Log file*/
	LOG_MSG( LOG_LEVEL_INFO, "SMSGatewayIMSIRequestor_requestIMSI: Requesting IMSI for MSISDN : %s",nativeMsisdnString);
    sprintf(log_msg,"SMSGatewayIMSIRequestor_requestIMSI: Requesting IMSI for MSISDN : %s",nativeMsisdnString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
	
	
   /*Request IMSI for the MSISDN */

    int             iiRet = 0;
    unsigned int    hRoutingInfoReq = 0;
	iiRet = ulcm_sms_t_RequestIMSI( modHandleSMS, nativeMsisdnString, &hRoutingInfoReq, pFnCallback_RI, requestorID );
	if ( iiRet != 0 ) {
		LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_requestIMSI: Error 0x%x(%d) for %s\n", iiRet, iiRet, nativeMsisdnString );
		sprintf(error_msg, "SMSGatewayIMSIRequestor_requestIMSI: Error 0x%x(%d) for %s\n", iiRet, iiRet, nativeMsisdnString );
		goto failure;      
    }else{
      LOG_MSG( LOG_LEVEL_INFO, "SMSGatewayIMSIRequestor_requestIMSI() success for %s\n", nativeMsisdnString);    
      sprintf(log_msg, "SMSGatewayIMSIRequestor_requestIMSI() success for %s\n", nativeMsisdnString);    
      logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
    }
	
	fflush(stdout);
	waitLoopCount = 0;
    while( (waitLoopCount < 10) && (ImsiInfoBuffer[requestorID].responseReceived == 0)){		 
      usleep(requestTimeout); // sleep before we check status of iReponseReceived flag  
	  waitLoopCount++;
    }
	
	LOG_MSG(LOG_LEVEL_TRACE, "SMSGatewayIMSIRequestor_requestIMSI(): Waited: %d miliseconds for IMSI %s\n",((waitLoopCount)* (requestTimeout/1000)), nativeMsisdnString);
    sprintf(log_msg, "SMSGatewayIMSIRequestor_requestIMSI(): Waited: %d miliseconds for IMSI %s\n",((waitLoopCount) * (requestTimeout/1000)), nativeMsisdnString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);  
    
    if(ImsiInfoBuffer[requestorID].responseReceived == 0){
	  ulcm_sms_t_CancelIMSIRequest(modHandleSMS, hRoutingInfoReq);
      LOG_MSG(LOG_LEVEL_WARN, "SMSGatewayIMSIRequestor_requestIMSI: Timeout !! No Response received from the SMS Gateway");
      sprintf(error_msg,"SMSGatewayIMSIRequestor_requestIMSI: Timeout !! No Response received from the SMS Gateway");
      goto failure;
    }
	
	fflush(stdout);
	int ImsiLen = strlen(ImsiInfoBuffer[requestorID].IMSI);
	if((*env)->EnsureLocalCapacity(env,ImsiLen) < 0){
      LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_requestIMSI: Out of Memory in NATIVE");
      sprintf(error_msg,"SMSGatewayIMSIRequestor_requestIMSI: Out of Memory in NATIVE");
      goto failure;
    }
	 /*Writing into JAVA Log file*/
    sprintf(log_msg, "IMSI received : %s , length : %d\n",ImsiInfoBuffer[requestorID].IMSI ,ImsiLen );        
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);
	
	jmethodID setImsiMID =  (*env)->GetMethodID(env, cls, "setIMSI", "(Ljava/lang/String;)V");
	if (setImsiMID == NULL) {
      sprintf(error_msg,"SMSGatewayIMSIRequestor_requestIMSI: setIMSI(Ljava/lang/String)V method not found");
      goto failure;
    }	
	jstring javaImsi;
	jbyteArray bytes = (*env)->NewByteArray(env,ImsiLen);
	
	if(bytes != NULL){
		(*env)->SetByteArrayRegion(env,bytes,0,ImsiLen,(jbyte *)ImsiInfoBuffer[requestorID].IMSI);
		javaImsi = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
		(*env)->DeleteLocalRef(env,bytes);
	}else {
      sprintf(error_msg, "SMSGatewayIMSIRequestor_requestIMSI: Could Not generate IMSI from native String");
      goto failure;
    }	

 	(*env)->CallObjectMethod(env, obj, setImsiMID, javaImsi);
	
	jmethodID onSuccessMID =  (*env)->GetMethodID(env, cls, "onSuccess", "()V");
	if (setImsiMID == NULL) {
		sprintf(error_msg,"SMSGatewayIMSIRequestor_requestIMSI: onSuccess()V method not found");
		goto destroy;
	}	
	(*env)->CallObjectMethod(env, obj, onSuccessMID);
	goto destroy;
	failure:
		LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_requestIMSI(): Failure Reason : %s\n", error_msg);
		logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,error_msg);
		fflush(stdout);
		
		jstring javaErrorMsg;
		int len = strlen(error_msg);
		if((*env)->EnsureLocalCapacity(env,len) < 0){
			LOG_MSG( LOG_LEVEL_ERROR, "Out of Memory in NATIVE\n");
			goto destroy;
		}
		bytes = (*env)->NewByteArray(env,len);
		if(bytes != NULL){
			(*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)error_msg);
			javaErrorMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
			(*env)->DeleteLocalRef(env,bytes);
        }else{
			LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_requestIMSI: Could Not Call Failure Event from native String\n");
			goto destroy;
		}
		
		jmethodID onFailureMID =  (*env)->GetMethodID(env, cls, "onFailure", "(Ljava/lang/String;)V");
		if (setImsiMID == NULL) {
			LOG_MSG( LOG_LEVEL_ERROR, "SMSGatewayIMSIRequestor_requestIMSI: onFailure(Ljava/lang/String;)V method not found\n");
			goto destroy;
		}		
		(*env)->CallObjectMethod(env, obj, onFailureMID, javaErrorMsg);
	destroy:
    
		(*env)->ReleaseStringUTFChars(env, javaMsisdn, nativeMsisdnString);
}


JNIEXPORT void JNICALL Java_com_elitecore_aaa_core_drivers_SMSGatewayIMSIRequestor_terminate
  (JNIEnv *env, jobject obj, jstring javaRemoteHost, jstring javaLocalHost) {
  
	const char *nativeRemoteHostString = (*env)->GetStringUTFChars(env, javaRemoteHost, 0);
    const char *nativeLocalHostString = (*env)->GetStringUTFChars(env, javaLocalHost, 0);
    char            error_msg[1024], log_msg[1024];

    /* Get a reference to object class */
    jclass cls = (*env)->GetObjectClass(env, obj);
    if(cls == NULL ) {
      LOG_MSG( LOG_LEVEL_ERROR, "Object Class not found");
      sprintf(error_msg,"Object Class not found");
      goto failure;
    }

    jclass Class_java_lang_String = (*env)->FindClass(env,"java/lang/String");
    if(Class_java_lang_String == NULL ) {
      LOG_MSG( LOG_LEVEL_ERROR, "String Class not found ");
      sprintf(error_msg,"java/lang/String Class not found");
      goto failure;
    }
    jmethodID MID_String_init = (*env)->GetMethodID(env,Class_java_lang_String,"<init>","([B)V");
    if(MID_String_init == NULL) {
      LOG_MSG( LOG_LEVEL_ERROR, "Method not fonud for String Class ");
      sprintf(error_msg,"java/lang/String<init>([B)V method not found");
      goto failure;
    }
  
	sprintf(log_msg,"Terminating SMSG/W communication with following configuration\n%s\n%s",nativeLocalHostString,nativeRemoteHostString);
    logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,log_msg);    
	
	ulcm_sms_t_ModuleTerminate(modHandleSMS);
	goto destroy;
	
	failure:
		logInJava(env,obj, cls,Class_java_lang_String,MID_String_init,error_msg);
	destroy:    
		(*env)->ReleaseStringUTFChars(env, javaRemoteHost, nativeRemoteHostString);
		(*env)->ReleaseStringUTFChars(env, javaLocalHost, nativeLocalHostString);
}
void
decodeIMSI( unsigned char *IMSI, int len )
{
    char            indx, indx2;
    int             i = 0;
    for ( i = 0; i < len; i++ ) {
		indx = ( ( *( IMSI + i ) & 0xf0 ) >> 4 ) + atoi( "0" );
		indx2 = ( ( *( IMSI + i ) & 0x0f ) ) + atoi( "0" );
		LOG_MSG( LOG_LEVEL_INFO, "%x", indx );
		LOG_MSG( LOG_LEVEL_INFO, "%x", indx2 );
    }

}

void
postIMSI( int hCallbackCookie, int nResult, const char *pszIMSI )
{
    LOG_MSG( LOG_LEVEL_INFO, "Result received from request IMSI is %d hCallbackcookie is %d \n", nResult, hCallbackCookie );
    LOG_MSG( LOG_LEVEL_INFO, "IMSI received is %s \n", pszIMSI );

    sprintf( ImsiInfoBuffer[hCallbackCookie].IMSI, "%s", pszIMSI );
	ImsiInfoBuffer[hCallbackCookie].responseReceived = 1; 
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
      LOG_MSG( LOG_LEVEL_ERROR, "Out of Memory in NATIVE");
      return;
    }
    
    len = strlen(nativeMessage);
    
    bytes = (*env)->NewByteArray(env,len);
    
    if(bytes != NULL){
      
      (*env)->SetByteArrayRegion(env,bytes,0,len,(jbyte *)nativeMessage);
      javaLogMsg = (*env)->NewObject(env,Class_java_lang_String, MID_String_init, bytes);
      (*env)->DeleteLocalRef(env,bytes);
      
    }else{
      LOG_MSG( LOG_LEVEL_ERROR, "Could Not Call Failure Event from native String");
      return;
    }
    jmethodID logMID = (*env)->GetMethodID(env, cls, "log", "(Ljava/lang/String;)V");
    
    if (logMID == NULL) {
      LOG_MSG( LOG_LEVEL_ERROR, "log(Ljava/lang/String;)V method not found");
      return;
    }
    
    (*env)->CallObjectMethod(env, obj, logMID,javaLogMsg);

}

