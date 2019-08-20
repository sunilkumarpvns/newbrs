#ifndef _SBRGSMAPI_H_
#define _SBRGSMAPI_H_
/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source   clientMod.h

@ClearCase-version: $Revision:/main/sw9/2 $ 

@date     $Date:22-Aug-2005 08:46:02 $

@product  Signalware

@subsystem  WLAN Gateway

DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE DEVELOPMENT OF A WORKING SOFTWARE PROGRAM.  THE SOURCE CODE PROVIDED IS NOT WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.
 
 ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER  INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and interest in and to the Software, and all intellectual property rights associated therewith, including without limitation, rights to copyrights, trade secrets or know-how. This disclaimer agreement shall be governed and construed in accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time without notice. Ulticom has no continuing obligation to provide additional source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples; these changes may be reported, for the sample code included herein, in new editions of the product.

******************************************************************************/
#include "MAPgateway.h"

#ifdef __cplusplus
extern          "C"
{
#endif

#ifndef _SBR_INT_
#define _SBR_INT_

    typedef int     sbr_int32;
    typedef unsigned int sbr_uint32;

#endif

/*Client APIs Prototype */

/* All error belong to one of the 6 classes of error
  The Upper 16 bits of the error represent the class
*/
/********************************************/
/**** SUCCESS ***                           */
/********************************************/
#define ULCM_SUCCESS 			(0UL<<16)
#define ULCM_L_API_INIT_SUCCESS  	(ULCM_SUCCESS|0)
#define ULCM_L_CANCEL_IMSI_REQ_SUCCESS  (ULCM_SUCCESS|0)
#define ULCM_L_REQ_IMSI_SUCCESS         (ULCM_SUCCESS|0)
#define ULCM_L_CANCEL_SIM_REQ_SUCCESS 	(ULCM_SUCCESS|0)
#define ULCM_L_CANCEL_AKA_REQ_SUCCESS 	(ULCM_SUCCESS|0)
#define ULCM_L_REQ_SIM_TRIPLET_SUCCESS 	(ULCM_SUCCESS|0)
#define ULCM_L_REQ_AKA_QUINTUPLET_SUCCESS	(ULCM_SUCCESS|0)
#define ULCM_L_MOD_INIT_SUCCESS 	(ULCM_SUCCESS|0)
#define ULCM_L_CANCEL_AKA_REQ_SUCCESS 	(ULCM_SUCCESS|0)
#define ULCM_L_REQ_AUTH_INFO_SUCCESS	(ULCM_SUCCESS|0)
#define ULCM_L_CANCEL_AUTH_REQ_SUCCESS	(ULCM_SUCCESS|0)
#define ULCM_G_NO_ERROR 		(ULCM_SUCCESS|0)
#define ULCM_L_REQ_UPDATE_GPRS_SUCCESS	(ULCM_SUCCESS|0)
#define ULCM_L_CANCEL_UPDATE_GPRS_SUCCESS	(ULCM_SUCCESS|0)


/***************************************************/
/**** NOT_AUTHORIZED ***  1UL<<16 is 65536 decimal */
/**** The IMSI/MSISDN is valid and the user is     */
/**** authorized for the requested operation       */
/***************************************************/
#define ULCM_USER_UNAUTHORIZED 	(1UL<<16)

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
#define ULCM_L_TCP_INIT_FAILURE		ULCM_NOT_ACCESSIBLE|4
#define ULCM_L_INIT_REQ_FAILURE		ULCM_NOT_ACCESSIBLE|5
#define ULCM_L_MAX_REQ_EXCEDED		ULCM_NOT_ACCESSIBLE|6
#define ULCM_L_TCP_CONNECT_FAILURE 	ULCM_NOT_ACCESSIBLE|7
#define ULCM_L_TCP_SEND_FAILURE		ULCM_NOT_ACCESSIBLE|8
#define ULCM_L_MOD_HANDLE_SET_ALREADY	ULCM_NOT_ACCESSIBLE|9
#define ULCM_L_INVALID_MOD_HANDLE	ULCM_NOT_ACCESSIBLE|10
#define ULCM_L_REQ_NOT_FOUND		ULCM_NOT_ACCESSIBLE|11
#define ULCM_L_API_VERSION_INVALID	ULCM_NOT_ACCESSIBLE|12
#define ULCM_L_MOD_HANDLE_NOT_INIT	ULCM_NOT_ACCESSIBLE|13
#define ULCM_L_ASN_ENCODING_ERROR	ULCM_NOT_ACCESSIBLE|14
#define ULCM_L_ASN_DECODING_ERROR	ULCM_NOT_ACCESSIBLE|15
#define EC_TIMEOUT			ULCM_NOT_ACCESSIBLE|51

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

#define ULCM_L_API_VERSION 1

#define ULCM_L_MG_CAPS_SIM (1<<0)
#define ULCM_L_MG_CAPS_AKA (1<<1)
#define ULCM_L_MG_CAPS_SMS (1<<2)
#define ULCM_L_MG_CAPS_IMSI (1<<3)
#define ULCM_L_MG_CAPS_AUTHORIZATION (1<<4)
#define ULCM_L_MG_CAPS_UPDATEGPRS (1<<5)

/**********************************************************/
/**** ULCM_UNREQUESTED_VECTOR *** 7UL<<16 is 458752 decimal */
/**** The HLR returned triplets when quintets             */
/****  were expected, or reurned quintets when triplets   */
/****  were expected                                      */
/**********************************************************/
#define ULCM_UNREQUESTED_VECTOR      (7UL<<16)
#define ULCM_L_NO_TRIPLET_AVAILABLE    ULCM_UNREQUESTED_VECTOR|1
#define ULCM_L_NO_QUINTET_AVAILABLE    ULCM_UNREQUESTED_VECTOR|2


    typedef struct ulcm_mg_re_synchronisation_s
    {
	unsigned char   RAND[32];
	unsigned char   AUTS[28];
    }
    ulcm_mg_re_synchronisation_t;

    typedef struct ulcm_mg_triplet_s
    {
	unsigned char   RAND[16];
	unsigned char   SRES[4];
	unsigned char   Kc[8];
    }
    ulcm_mg_triplet_t;

    typedef struct ulcm_mg_quintuplet_s
    {
	unsigned char   RAND[16];
	unsigned char   AUTN[16];
	unsigned char   XRESLen;	/* length (in bytes) of XRES */
	unsigned char   XRES[16];	/* padded with 0 bytes beyond XRESLen */
	unsigned char   CK[16];
	unsigned char   IK[16];
    }
    ulcm_mg_quintuplet_t;
    
    typedef struct ulcm_mg_updategprsrequest_s
    {
        ulcm_UpdateGprsLocationArg_v3 updateGprsLocationArg; /* UpdateGprsLocationArguments */
    }
    ulcm_mg_updategprsrequest_t;

    typedef struct ulcm_mg_updategprsresponse_s
    {
        ulcm_UpdateGprsLocationRes_v3 updateGprsLocationRes; /* UpdateGprsLocationResponse */
        ulcm_InsertSubscriberDataArg_v3 insertSubscriberDataArg; /* GPRSSubscriptionData, AccessRestrictionData */
    }
    ulcm_mg_updategprsresponse_t;


#define SBRGSM_RET_SUCCESS      0	/* Success */
#define SBRGSM_RET_FAILURE      1	/* General (non-specific) error */
#define SBRGSM_RET_COMM_ERR     2	/* Unable to communicate with AuC to retrieve requested information */
#define SBRGSM_RET_BUSY_ERR     3	/* Able to communicate with AuC, but it too busy to service the request */
#define SBRGSM_RET_UNKNOWN_IMSI 4	/* Able to communicate with AuC, but no subscriber record found for the specified IMSI */
#define SBRGSM_RET_INVALID_HANDLE 5	/* Invalid handle specified */

#define SBRGSM_CAPS_SIM (1UL<<0)
#define SBRGSM_CAPS_AKA (1UL<<1)


/*
 * Global:  MAX_CONNECTIONS
 *  Purpose:  Max allowed sctp connections
 */
#define MAX_CONNECTIONS 10

    typedef struct sbrgsm_Triplet
    {
	unsigned char   RAND[16];
	unsigned char   SRES[4];
	unsigned char   Kc[8];
    }
    sbrgsm_Triplet;

    typedef struct sbrgsm_Quintet
    {
	unsigned char   RAND[16];
	unsigned char   AUTN[16];
	unsigned char   XRESLen;	/* length (in bytes) of XRES */
	unsigned char   XRES[16];	/* padded with 0 bytes beyond XRESLen */
	unsigned char   CK[16];
	unsigned char   IK[16];
    }
    sbrgsm_Quintet;


    sbr_int32       ulcm_mg_t_GetApiVersion( sbr_uint32 * pnApiVersion, sbr_uint32 * pnMinApiVersion );


    sbr_int32       ulcm_mg_t_ModuleInit( sbr_uint32 nApiVersion, const char *pRemoteHost, const char *pLocalHost, sbr_uint32 * pnAvailableCaps, sbr_uint32 * pnModHandle , int driverID );


    void            ulcm_mg_t_ModuleTerminate( sbr_uint32 nModHandle, int driverID );

    typedef void
	 
	 ulcm_mg_t_postSIMResults( sbr_uint32 hCallbackCookie,
				   sbr_int32 nResult, /*const*/ ulcm_mg_triplet_t * pTripletBuffer, sbr_uint32 nTripletCount, ulcm_mg_quintuplet_t * pQuintupletBuffer, sbr_uint32 nQuintupletCount, const char *pszServiceProfile );

         
	 
	 
	 
	 
	 
	sbr_int32
	ulcm_mg_t_RequestSIMTriplets( sbr_uint32 nModHandle,
				      const char *pszIMSI,
				      sbr_uint32 nTripletCount, sbr_uint32 * phSIMRequest, ulcm_mg_t_postSIMResults * pfnCallback, unsigned long hCallbackCookie, int driverID );

                    sbr_int32 ulcm_mg_t_CancelSIMRequest( sbr_uint32 nModHandle, sbr_uint32 hSIMRequest, int driverID );

    typedef void
	 
	 ulcm_mg_t_postAKAResults( unsigned long hCallbackCookie,
				   sbr_int32 nResult,
				   /*const*/ ulcm_mg_quintuplet_t * pQuintupletBuffer, sbr_uint32 nQuintupletCount, ulcm_mg_triplet_t * pTripletBuffer, sbr_uint32 nTripletCount, const char *pszServiceProfile );

         
	 
	 
	 
	 
	 
	sbr_int32
	ulcm_mg_t_RequestAKAQuintuplets( sbr_uint32 nModHandle,
					 const char *pszIMSI,
					 const ulcm_mg_re_synchronisation_t * pRe_synchronisation,
					 sbr_uint32 nQuintupletCount,
					 sbr_uint32 * phAKARequest, ulcm_mg_t_postAKAResults * pfnCallback, unsigned long hCallbackCookie, int driverID );


                    sbr_int32 ulcm_mg_t_CancelAKARequest( sbr_uint32 nModHandle, sbr_uint32 hAKARequest, int driverID );
    typedef void    ulcm_mg_t_postIMSIResults( unsigned long hCallbackCookie, sbr_int32 nResult, const char *pszIMSI );

         
	 
	 
	 
	 
	 
	sbr_int32
	ulcm_mg_t_RequestIMSI( sbr_uint32 nModHandle,
			       const char *pszMSISDN, sbr_uint32 * phIMSIRequest, ulcm_mg_t_postIMSIResults * pfnCallback, unsigned long hCallbackCookie, int driverID );

                    sbr_int32 ulcm_mg_t_CancelIMSIRequest( sbr_uint32 nModHandle, sbr_uint32 hIMSIRequest, int driverID );

    typedef void    ulcm_mg_t_postAuthorizationResults( unsigned long hCallbackCookie, sbr_int32 nResult, const char *pszServiceProfile , const char *pszmsisdn, int subscriberStatus, int vlrSubscriptionInfoPresent);

         
	 
	 
	 
	 
	 
	sbr_int32
	ulcm_mg_t_RequestAuthorizationInfo( sbr_uint32 nModHandle,
					    const char *pszIMSI,
					    sbr_uint32 * phAuthorizationRequest, ulcm_mg_t_postAuthorizationResults * pfnCallback, unsigned long hCallbackCookie, int driverID );

                    sbr_int32 ulcm_mg_t_CancelAuthorizationRequest( sbr_uint32 nModHandle, sbr_uint32 hAuthorizationRequest, int driverID );

	typedef void    ulcm_mg_t_postUpdateGprsResults( sbr_uint32 hCallbackCookie, sbr_int32 nResult, const ulcm_mg_updategprsresponse_t * pupdateGprsResponse);







	sbr_int32
	ulcm_mg_t_RequestUpdateGprs( sbr_uint32 nModHandle,
                                            const char *pszIMSI, const ulcm_mg_updategprsrequest_t * pupdateGprsParams,
                                            sbr_uint32 * phUpdateGprsRequest, ulcm_mg_t_postUpdateGprsResults * pfnCallback, sbr_uint32 hCallbackCookie, int driverID );

                    sbr_int32 ulcm_mg_t_CancelUpdateGprsRequest( sbr_uint32 nModHandle, sbr_uint32 hAuthorizationRequest, int driverID );
                    
                    
typedef void
ulcm_mg_t_postSIMResultsEx ( unsigned long hCallbackCookie,
                                      sbr_int32 nResult,
                                      const ulcm_mg_triplet_t * pTripletBuffer,
                                      sbr_uint32 nTripletCount,
                                      const char * pszServiceProfile,
                                      const char * pszMSISDN );

sbr_int32
ulcm_mg_t_RequestSIMTripletsEx ( sbr_uint32 nModHandle,
                                       const char * pszIMSI,
                                       sbr_uint32 nTripletCount,
                                       sbr_uint32 * phSIMRequest,
                                       ulcm_mg_t_postSIMResultsEx * pfnCallback,
                                       unsigned long hCallbackCookie );
typedef void
ulcm_mg_t_postAuthorizationResultsEx ( unsigned long hCallbackCookie,
                                       sbr_int32 nResult,
                                       const char * pszServiceProfile,
                                       const char * pszMSISDN );

sbr_int32
ulcm_mg_t_RequestAuthorizationInfoEx ( sbr_uint32 nModHandle,
                                       const char * pszIMSI,
                                       sbr_uint32 * phAuthorizationRequest,
                                       ulcm_mg_t_postAuthorizationResultsEx * pfnCallback,
                                       unsigned long hCallbackCookie );





#ifdef __cplusplus
}
#endif

#endif				/* _SBRGSMAPI_H_ */
