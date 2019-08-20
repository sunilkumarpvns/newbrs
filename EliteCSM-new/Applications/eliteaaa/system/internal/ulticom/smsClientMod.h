/*******************************************************************************

                        Copyright (c) 2005 Ulticom, Inc.


File:     smsClientMod.h
Author:   Gary Birnhak
Created:  02/2/05
Revision: $Revision$ $Date$

DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE
DEVELOPMENT OF A WORKING SOFTWARE PROGRAM. THE SOURCE CODE PROVIDED IS NOT
WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS
INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.

ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE
OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND
PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE
GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE
, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING,
REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY
KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY
OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER
INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE
SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and
interest in and to the Software, and all intellectual property rights associated
therewith, including without limitation, rights to copyrights, trade or
know-how. This disclaimer agreement shall be governed and construed in
accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time
without notice. Ulticom has no continuing obligation to provide additional
source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples;
these changes may be reported, for the sample code included herein, in new
editions of the product.

*******************************************************************************/

#ifndef _SBRSMSAPI_H_
#define _SBRSMSAPI_H_

#ifdef __cplusplus
extern          "C"
{
#endif

#ifndef _SBR_INT_
#define _SBR_INT_

    typedef int     sbr_int32;
    typedef unsigned int sbr_uint32;

#endif /*_SBR_INT_*/

/*Client APIs Prototype */

/* All error belong to one of the 6 classes of error
  The Upper 16 bits of the error represent the class
*/
/********************************************/
/**** SUCCESS ***                           */
/********************************************/
#define ULCM_SUCCESS 		(0UL<<16)
#ifndef ULCM_L_MOD_INIT_SUCCESS
#define ULCM_L_MOD_INIT_SUCCESS 	(ULCM_SUCCESS|0)
#endif
#ifndef ULCM_L_API_INIT_SUCCESS
#define ULCM_L_API_INIT_SUCCESS  	(ULCM_SUCCESS|0)
#endif
#define ULCM_L_CANCEL_SMS_SUCCESS   	(ULCM_SUCCESS|0)
#ifndef ULCM_G_NO_ERROR
#define ULCM_G_NO_ERROR 		(ULCM_SUCCESS|0)
#endif

/***************************************************/
/**** NOT_AUTHORIZED ***  1UL<<16 is 65536 decimal */
/**** The IMSI/MSISDN is valid and the user is     */
/**** authorized for the requested operation       */
/***************************************************/
#define ULCM_USER_UNAUTHORIZED 	(1UL<<16)
#define ULCM_G_FACILITY_NOT_SUPPORTED		ULCM_USER_UNAUTHORIZED|0
#define ULCM_G_CALL_BARRED					ULCM_USER_UNAUTHORIZED|1
#define ULCM_G_TELESERVICE_NOT_SUPPORTED 	ULCM_USER_UNAUTHORIZED|2

/***************************************************/
/*** INVALID_USER *** 2UL<<16 is 131072 decimal    */
/*** The IMSI/MSISDN is not valid on any HLR served*/
/***  by this module or are missing in internal    */
/***  provisionning of the module                  */
/***************************************************/
#define ULCM_INVALID_USER	(2UL<<16)
#define ULCM_G_UNKNOWN_IMSI		ULCM_INVALID_USER|0

/*****************************************************/
/**** NOT_ACCESSIBLE*** 3UL<<16 is 196608 decimal    */
/**** The HLR and/or MAP Gateway is inaccessible but */
/**** may be accessible later                        */
/*****************************************************/
#define ULCM_NOT_ACCESSIBLE (3UL<<16)
#define ULCM_G_COMM_ERROR      		ULCM_NOT_ACCESSIBLE|0
#define ULCM_G_MALFORMED_MESSAGE	ULCM_NOT_ACCESSIBLE|1
#define ULCM_G_MAX_REQ_EXCEEDED		ULCM_NOT_ACCESSIBLE|2
#define ULCM_G_GATEWAY_ERROR		ULCM_NOT_ACCESSIBLE|3
#define ULCM_L_TCP_INIT_FAILURE	ULCM_NOT_ACCESSIBLE|4
#define ULCM_L_INIT_REQ_FAILURE		ULCM_NOT_ACCESSIBLE|5
#define ULCM_L_MAX_REQ_EXCEDED		ULCM_NOT_ACCESSIBLE|6
#define ULCM_L_TCP_CONNECT_FAILURE 	ULCM_NOT_ACCESSIBLE|7
#define ULCM_L_TCP_SEND_FAILURE	ULCM_NOT_ACCESSIBLE|8
#define ULCM_L_MOD_HANDLE_SET_ALREADY	ULCM_NOT_ACCESSIBLE|9
#define ULCM_L_INVALID_MOD_HANDLE	ULCM_NOT_ACCESSIBLE|10
#define ULCM_L_REQ_NOT_FOUND		ULCM_NOT_ACCESSIBLE|11
#define ULCM_L_API_VERSION_INVALID	ULCM_NOT_ACCESSIBLE|12
#define ULCM_L_MOD_HANDLE_NOT_INIT	ULCM_NOT_ACCESSIBLE|13

/****************************************************/
/****INVALID_PARAM***  4UL<<16 is 262144 decimal    */
/**** An incorrect or stale parameter was specified */
/**** to the call                                   */
/****************************************************/
#define ULCM_INVALID_PARAM	(4UL<<16)
#define ULCM_L_INVALID_ARG		ULCM_INVALID_PARAM|0

/***************************************************/
/****NOT_FUNCTIONAL*** 5UL<<16 is 327680 decimal   */
/**** The HLR and/or MAP Gateway is not functional.*/
/**** Further calls will never succeed.            */
/***************************************************/
#define ULCM_NOT_FUNCTIONAL (5UL<<16)
#define ULCM_G_GENERAL_ERROR		ULCM_NOT_FUNCTIONAL|0
#define ULCM_G_GATEWAY_PROV_ERROR	ULCM_NOT_FUNCTIONAL|1
#define ULCM_L_CONF_INIT_FAILURE	ULCM_NOT_FUNCTIONAL|2
#define ULCM_L_CONF_PARAM_NOT_VALID 	ULCM_NOT_FUNCTIONAL|3
#define ULCM_L_INTERNAL_ERROR		ULCM_NOT_FUNCTIONAL|4
#define ULCM_L_FUNCTION_NOT_SUPPORTED	ULCM_NOT_FUNCTIONAL|5

/**********************************************************/
/**** VERSION_NOT_SUPPORTED *** 6UL<<16 is 393216 decimal */
/**** Version incompatibility between the application     */
/****  and the library or between the library and the     */
/****  Gateway                                            */
/**********************************************************/
#define ULCM_VERSION_NOT_SUPPORTED	(6UL<<16)
#define ULCM_L_VERSION_NOT_SUPPORTED	ULCM_VERSION_NOT_SUPPORTED|0

/******************************************************/
/**** SMS_NOT_DELIVERED*** 7UL<<16 is 458752 decimal  */
/****  The Short Message could not be delivered to    */
/****  the subscriber                                 */
/******************************************************/
#define ULCM_SMS_NOT_DELIVERED		(7UL<<16)
#define ULCM_G_SMS_MEMORY_FULL		ULCM_SMS_NOT_DELIVERED|1
#define ULCM_G_SMS_EQ_NOT_SM_EQUIPTED	ULCM_SMS_NOT_DELIVERED|2
#define ULCM_G_SMS_ILLEGAL_EQUIPMENT	ULCM_SMS_NOT_DELIVERED|3
#define ULCM_G_SMS_EQUIPMENT_PROTOCOL_ERROR	ULCM_SMS_NOT_DELIVERED|4
#define ULCM_G_SMS_SUBSCRIBER_BUSY		ULCM_SMS_NOT_DELIVERED|5
#define ULCM_G_SMS_UNKNOWN_SERVICE_CENTER	ULCM_SMS_NOT_DELIVERED|6
#define ULCM_G_SMS_SERVICE_CENTER_CONGESTION	ULCM_SMS_NOT_DELIVERED|7
#define ULCM_G_SMS_INVALID_ENTITY_ADDRESS	ULCM_SMS_NOT_DELIVERED|8
#define ULCM_G_SMS_NOT_SERVICE_CENTER_SUBSCRIBER	ULCM_SMS_NOT_DELIVERED|9
#define ULCM_G_SMS_ABSENT_SUBSCRIBER		ULCM_SMS_NOT_DELIVERED|10
#define ULCM_G_SMS_SYSTEM_FAILURE               ULCM_SMS_NOT_DELIVERED|11


#define ULCM_L_API_VERSION 1

#define ULCM_L_SMS_CAPS_SIM (1<<0)
#define ULCM_L_SMS_CAPS_AKA (1<<1)
#define ULCM_L_SMS_CAPS_SM (1<<2)
#define ULCM_L_SMS_CAPS_IMSI (1<<3)

#define SBRSMS_RET_SUCCESS      0	/* Success */
#define SBRSMS_RET_COMM_ERR     2	/* Unable to communicate with AuC to retrieve requested information */
#define SBRSMS_RET_BUSY_ERR     3	/* Able to communicate with AuC, but it too busy to service the request */
#define SBRSMS_RET_UNKNOWN_IMSI 4	/* Able to communicate with AuC, but no subscriber record found for the specified IMSI */
#define SBRSMS_RET_INVALID_HANDLE 5	/* Invalid handle specified */

    sbr_int32       ulcm_sms_t_GetApiVersion( sbr_uint32 * pnApiVersion, sbr_uint32 * pnMinApiVersion );


    sbr_int32       ulcm_sms_t_ModuleInit( sbr_uint32 nApiVersion, const char *pRemoteHost, const char *pLocalHost, sbr_uint32 * pnAvailableCaps, sbr_uint32 * pnModHandle );


    void            ulcm_sms_t_ModuleTerminate( sbr_uint32 nModHandle );

    typedef void    ulcm_sms_t_postSMResults( sbr_uint32 hCallbackCookie, sbr_uint32 nResult, sbr_uint32 nStatusCode );

    typedef void    ulcm_sms_t_postIMSIResults( sbr_uint32 hCallbackCookie, sbr_int32 nResult, const char *pszIMSI );

         
	 
	 
	 
	 
	 
	sbr_int32
	ulcm_sms_t_RequestIMSI( sbr_uint32 nModHandle,
				const char *pszMSISDN, sbr_uint32 * phIMSIRequest, ulcm_sms_t_postIMSIResults * pfnCallback, sbr_uint32 hCallbackCookie );

                    sbr_int32 ulcm_sms_t_CancelIMSIRequest( unsigned int nModHandle, unsigned int hIMSIRequest );

    /*SMS Feature..... -S */
         
	 
	 
	 
	 
	 
	sbr_int32
	ulcm_sms_t_SendSMSMessage( sbr_uint32 nModHandle,
				   const char *pszIMSI,
				   const char *pszSMStr, sbr_uint32 * phSMRequest, ulcm_sms_t_postSMResults * pfnCallback, sbr_uint32 hSMCallbackCookie );

                    sbr_int32 ulcm_sms_t_CancelSMSMessage( sbr_uint32 nModHandle, sbr_uint32 phSMRequest );

#ifdef __cplusplus
}
#endif

#endif				/* _SBRSMSAPI_H_ */
