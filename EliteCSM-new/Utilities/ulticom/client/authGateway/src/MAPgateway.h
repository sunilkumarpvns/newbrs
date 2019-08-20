/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source   MAPgateway.h

@ClearCase-version: $Revision:/main/sw9/2 $ 

@date     $Date:16-Dec-2008 15:50:41 $

@product  Signalware

@subsystem  WLAN Gateway

DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE DEVELOPMENT OF A WORKING SOFTWARE PROGRAM.  THE SOURCE CODE PROVIDED IS NOT WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.
 
 ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER  INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and interest in and to the Software, and all intellectual property rights associated therewith, including without limitation, rights to copyrights, trade secrets or know-how. This disclaimer agreement shall be governed and construed in accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time without notice. Ulticom has no continuing obligation to provide additional source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples; these changes may be reported, for the sample code included herein, in new editions of the product.

******************************************************************************/

#ifndef _MAPgateway_h_
#define _MAPgateway_h_




#ifdef __cplusplus
extern "C" {
#endif

#ifndef FALSE
#define FALSE   0
#endif
#ifndef TRUE
#define TRUE    1
#endif

#define          ulcm_Request_PDU 1
#define          ulcm_Response_PDU 2
#define          ulcm_ClientHandle_PDU 3
#define          ulcm_RequestType_PDU 4

typedef int ulcm_ClientHandle; /* INTEGER */


typedef int ulcm_Version; /* INTEGER */


typedef struct ulcm_AddressString
{
        unsigned short length;
        unsigned char value[20];
} ulcm_AddressString; /* OCTET STRING SIZE 1..maxAddressLength */


typedef int ulcm_RequestId; /* INTEGER */
typedef ulcm_RequestId ulcm_CancelUpdateGprsRequest; /* RequestId */


typedef int ulcm_ServiceFound; /* INTEGER */


typedef char ulcm_ServiceList[101]; /* VisibleString SIZE 0..100 */

typedef enum
    {
    ulcm_noError = 0,
    ulcm_userNotAuthorized = 65536,
    ulcm_unknownImsi = 131072,
    ulcm_commError = 196608,
    ulcm_malformedMessage = 196609,
    ulcm_maxRequestExceeded = 196610,
    ulcm_gatewayError = 196611,
    ulcm_generalError = 327680,
    ulcm_gatewayProvisioningError = 327681
    } ulcm_ErrorCode; /* ENUMERATED { ulcm_noError (0), ulcm_userNotAuthorized (65536), ulcm_unknownImsi (131072), ulcm_commError (196608), ulcm_malformedMessage (196609), ulcm_maxRequestExceeded (196610), ulcm_gatewayError (196611), ulcm_generalError (327680), ulcm_gatewayProvisioningError (327681) }  */


typedef enum
    {
        ulcm_requestSIMTriplets = 0,
        ulcm_requestAKAQuintets = 1,
        ulcm_cancelSIMRequest = 2,
        ulcm_cancelAKARequest = 3
    } ulcm_RequestType; /* ENUMERATED { ulcm_requestSIMTriplets (0), ulcm_requestAKAQuintets (1), ulcm_cancelSIMRequest (2), ulcm_cancelAKARequest (3) }  */


typedef struct ulcm_IMSI
{
    unsigned short length;
    unsigned char value[8];
} ulcm_IMSI; /* TBCD-STRING SIZE 3..8 */

typedef struct ulcm_APN {
    unsigned short  length;
    unsigned char   value[63];
} ulcm_APN;

typedef unsigned short  ulcm_ContextId;

typedef struct ulcm_ChargingCharacteristics {
    unsigned short  length;
    unsigned char   value[2];
} ulcm_ChargingCharacteristics;

typedef struct ulcm_Ext_QoS_Subscribed {
    unsigned short  length;
    unsigned char   value[9];
} ulcm_Ext_QoS_Subscribed;

typedef struct ulcm_Ext2_QoS_Subscribed {
    unsigned short  length;
    unsigned char   value[3];
} ulcm_Ext2_QoS_Subscribed;

typedef struct ulcm_PDP_Address {
    unsigned short  length;
    unsigned char   value[16];
} ulcm_PDP_Address;

typedef struct ulcm_PDP_Type {
    unsigned short  length;
    unsigned char   value[2];
} ulcm_PDP_Type;

typedef struct ulcm_QoS_Subscribed {
    unsigned short  length;
    unsigned char   value[3];
} ulcm_QoS_Subscribed;

typedef struct ulcm_GSN_Address {
    unsigned short  length;
    unsigned char   value[17];
} ulcm_GSN_Address;

typedef unsigned short ulcm_NumberOfRequestedVectors; /* INTEGER 1..5 */

typedef struct ulcm_RAND
{
    unsigned short length;
    unsigned char value[16];

} ulcm_RAND; /* OCTET STRING SIZE 16 */

typedef struct ulcm_SRES
{
    unsigned short  length;
    unsigned char   value[4];

} ulcm_SRES; /* OCTET STRING SIZE 4 */


typedef struct ulcm_Kc
{
    unsigned short length;
    unsigned char value[8];

} ulcm_Kc; /* OCTET STRING SIZE 8 */


typedef struct ulcm_XRES
{
    unsigned short length;
    unsigned char value[16];

} ulcm_XRES; /* OCTET STRING SIZE 4..16 */


typedef struct ulcm_CK
{
    unsigned short length;
    unsigned char value[16];

} ulcm_CK; /* OCTET STRING SIZE 16 */


typedef struct ulcm_IK
{
    unsigned short length;
    unsigned char value[16];

} ulcm_IK; /* OCTET STRING SIZE 16 */


typedef struct ulcm_AUTN
{
    unsigned short  length;
    unsigned char   value[18];

} ulcm_AUTN; /* OCTET STRING SIZE 14..18 */


typedef struct ulcm_AUTS
{
    unsigned short length;
    unsigned char value[14];

} ulcm_AUTS; /* OCTET STRING SIZE 14 */

typedef struct ulcm_SupportedCamelPhases {
    unsigned short  length;  /* number of significant bits */
    unsigned char   value[2];
} ulcm_SupportedCamelPhases;

typedef struct ulcm_SupportedLCS_CapabilitySets {
    unsigned short  length;  /* number of significant bits */
    unsigned char   value[2];
} ulcm_SupportedLCS_CapabilitySets;

typedef struct ulcm_SubscriberStatus
{
	int subscriber_status_present;
	int subscriberStatus;	
} ulcm_SubscriberStatus;

typedef struct ulcm_vlrCamelSubscriptionInfo {
	
	int vlrCamelSubscriptionInfoPresent;
	
} ulcm_vlrCamelSubscriptionInfo;
typedef struct ulcm_InitRequest /* SEQUENCE */
{
	ulcm_Version version; /* Version */
} ulcm_InitRequest;

typedef struct ulcm_InitResponse /* SEQUENCE */
{
	int maxRequest; /* INTEGER */
	int versionSupported; /* BOOLEAN */
	ulcm_ErrorCode errorCode; /* ErrorCode */
} ulcm_InitResponse;

typedef struct ulcm_ErrorResponse /* SEQUENCE */
{
	ulcm_ErrorCode errorCode; /* ErrorCode */
} ulcm_ErrorResponse;

typedef struct ulcm_AgeIndicator {
    unsigned short  length;
    unsigned char   value[6];
} ulcm_AgeIndicator;

typedef struct ulcm_IMEI {
    unsigned short  length;
    unsigned char   value[8];
} ulcm_IMEI;

typedef struct ulcm_OfferedCamel4CSIs {
    unsigned short  length;  /* number of significant bits */
    unsigned char   value[2];
} ulcm_OfferedCamel4CSIs;

typedef struct ulcm_ADD_Info {
    unsigned char   bit_mask;
#       define      ADD_Info_skipSubscriberDataUpdate_present 0x80
    ulcm_IMEI       imeisv;
    char            skipSubscriberDataUpdate;  /* optional; set in bit_mask
                                 * ADD_Info_skipSubscriberDataUpdate_present if
                                 * present */
} ulcm_ADD_Info;

typedef struct ulcm_SuperChargerInfo {
    unsigned short  choice;
#       define      SuperChargerInfo_sendSubscriberData_chosen 1
#       define      SuperChargerInfo_subscriberDataStored_chosen 2
    union _union {
        char            sendSubscriberData;  /* to choose, set choice to
                                * SuperChargerInfo_sendSubscriberData_chosen */
        ulcm_AgeIndicator subscriberDataStored;  /* to choose, set choice to
                              * SuperChargerInfo_subscriberDataStored_chosen */
    } u;
} ulcm_SuperChargerInfo;

typedef ulcm_RequestId ulcm_CancelAuthRequest; /* RequestId */


typedef ulcm_RequestId ulcm_CancelImsiRequest; /* RequestId */


typedef ulcm_AddressString ulcm_ISDN_AddressString; /* AddressString SIZE 1..maxISDN-AddressLength */

typedef struct ulcm_AuthenticationTriplet /* SEQUENCE */
{
	ulcm_RAND rand; /* RAND */
	ulcm_SRES sres; /* SRES */
	ulcm_Kc kc; /* Kc */
} ulcm_AuthenticationTriplet;


typedef struct ulcm_AuthenticationQuintuplet /* SEQUENCE */
{
	ulcm_RAND rand; /* RAND */
	ulcm_XRES xres; /* XRES */
	ulcm_CK ck; /* CK */
	ulcm_IK ik; /* IK */
	ulcm_AUTN autn; /* AUTN */
} ulcm_AuthenticationQuintuplet;


typedef struct ulcm_Re_synchronisationInfo /* SEQUENCE */
{
	ulcm_RAND rand; /* RAND */
	ulcm_AUTS auts; /* AUTS */
} ulcm_Re_synchronisationInfo;


typedef struct ulcm_AuthRequest /* SEQUENCE */
{
	ulcm_RequestId requestId; /* RequestId */
	ulcm_IMSI imsi; /* IMSI */
	ulcm_NumberOfRequestedVectors numberOfRequestedVectors; /* NumberOfRequestedVectors */
	ulcm_Re_synchronisationInfo re_synchronisationInfo; /* Re-synchronisationInfo */
} ulcm_AuthRequest;


typedef struct ulcm_ImsiRequest /* SEQUENCE */
{
	ulcm_RequestId requestId; /* RequestId */
	ulcm_ISDN_AddressString msisdn; /* ISDN-AddressString */
} ulcm_ImsiRequest;


typedef struct ulcm_ImsiResponse /* SEQUENCE */
{
	ulcm_RequestId requestId; /* RequestId */
	ulcm_IMSI imsi; /* IMSI */
	ulcm_ErrorCode errorCode; /* ErrorCode */
} ulcm_ImsiResponse;


typedef struct ulcm_TripletList
{
    unsigned short  count;
    ulcm_AuthenticationTriplet value[5];
}  ulcm_TripletList; /* SEQUENCE SIZE 1..5 OF AuthenticationTriplet */

typedef struct ulcm_QuintupletList
{
    unsigned short  count;
    ulcm_AuthenticationQuintuplet value[5];
} ulcm_QuintupletList; /* SEQUENCE SIZE 1..5 OF AuthenticationQuintuplet */

typedef struct ulcm_AuthenticationSetList /* CHOICE */
{
    unsigned short  choice;
#       define      ulcm_tripletList_chosen 1
#       define      ulcm_quintupletList_chosen 2

    union {
        ulcm_TripletList tripletList; /* [0] TripletList */
        ulcm_QuintupletList quintupletList; /* [1] QuintupletList */
    } u;
} ulcm_AuthenticationSetList;


typedef struct ulcm_AuthResponse /* SEQUENCE */
{
    unsigned char   bit_mask;
#       define      ulcm_authenticationSetList_present 0x80
	ulcm_RequestId requestId; /* RequestId */
	ulcm_AuthenticationSetList authenticationSetList; /* AuthenticationSetList OPTIONAL */
	ulcm_ErrorCode errorCode; /* ErrorCode */
	ulcm_ServiceFound serviceFound; /* ServiceFound */
	ulcm_ServiceList serviceList; /* ServiceList */
	ulcm_ISDN_AddressString msisdn; /* ISDN-AddressString */
	ulcm_SubscriberStatus subscriberStatus; /* Subscriber status*/
	ulcm_vlrCamelSubscriptionInfo vlrCamelSubscriptionInfo;
} ulcm_AuthResponse;

typedef struct ulcm_SGSN_Capability {
    unsigned char   bit_mask;
#       define      SGSN_Capability_solsaSupportIndicator_present 0x80
#       define      SGSN_Capability_extensionContainer_present 0x40
#       define      SGSN_Capability_superChargerSupportedInServingNetworkEntity_present 0x20
#       define      SGSN_Capability_gprsEnhancementsSupportIndicator_present 0x10
#       define      SGSN_Capability_supportedCamelPhases_present 0x08
#       define      SGSN_Capability_supportedLCS_CapabilitySets_present 0x04
#       define      SGSN_Capability_offeredCamel4CSIs_present 0x02
#       define      SGSN_Capability_smsCallBarringSupportIndicator_present 0x01
    char            solsaSupportIndicator;  /* optional; set in bit_mask
                             * SGSN_Capability_solsaSupportIndicator_present if
                             * present */
    ulcm_SuperChargerInfo superChargerSupportedInServingNetworkEntity;  /* extension
                                   * #1; optional; set in bit_mask
       * SGSN_Capability_superChargerSupportedInServingNetworkEntity_present if
       * present */
    char            gprsEnhancementsSupportIndicator;  /* extension #2;
                                   * optional; set in bit_mask
                  * SGSN_Capability_gprsEnhancementsSupportIndicator_present if
                  * present */
    ulcm_SupportedCamelPhases supportedCamelPhases;  /* extension #3; optional; set
                                   * in bit_mask
                              * SGSN_Capability_supportedCamelPhases_present if
                              * present */
    ulcm_SupportedLCS_CapabilitySets supportedLCS_CapabilitySets;  /* extension #4;
                                   * optional; set in bit_mask
                       * SGSN_Capability_supportedLCS_CapabilitySets_present if
                       * present */
    ulcm_OfferedCamel4CSIs offeredCamel4CSIs;  /* extension #5; optional; set in
                                   * bit_mask
                                   * SGSN_Capability_offeredCamel4CSIs_present
                                   * if present */
    char            smsCallBarringSupportIndicator;  /* extension #6; optional;
                                   * set in bit_mask
                    * SGSN_Capability_smsCallBarringSupportIndicator_present if
                    * present */
} ulcm_SGSN_Capability;

typedef struct ulcm_UpdateGprsLocationArg_v3 {
    unsigned char   bit_mask;
#       define      UpdateGprsLocationArg_v3_extensionContainer_present 0x80
#       define      UpdateGprsLocationArg_v3_sgsn_Capability_present 0x40
#       define      UpdateGprsLocationArg_v3_informPreviousNetworkEntity_present 0x20
#       define      UpdateGprsLocationArg_v3_ps_LCS_NotSupportedByUE_present 0x10
#       define      UpdateGprsLocationArg_v3_v_gmlc_Address_present 0x08
#       define      UpdateGprsLocationArg_v3_add_info_present 0x04
    ulcm_IMSI       imsi;
    ulcm_ISDN_AddressString sgsn_Number;
    ulcm_GSN_Address sgsn_Address;
    ulcm_SGSN_Capability sgsn_Capability;  /* extension #1; optional; set in bit_mask
                          * UpdateGprsLocationArg_v3_sgsn_Capability_present if
                          * present */
    char            informPreviousNetworkEntity;  /* extension #2; optional; set
                                   * in bit_mask
              * UpdateGprsLocationArg_v3_informPreviousNetworkEntity_present if
              * present */
    char            ps_LCS_NotSupportedByUE;  /* extension #3; optional; set in
                                   * bit_mask
                  * UpdateGprsLocationArg_v3_ps_LCS_NotSupportedByUE_present if
                  * present */
    ulcm_GSN_Address v_gmlc_Address;  /* extension #4; optional; set in bit_mask
                           * UpdateGprsLocationArg_v3_v_gmlc_Address_present if
                           * present */
    ulcm_ADD_Info   add_info;  /* extension #5; optional; set in bit_mask
                                * UpdateGprsLocationArg_v3_add_info_present if
                                * present */
} ulcm_UpdateGprsLocationArg_v3;

typedef struct ulcm_UpdateGprsRequest /* SEQUENCE */
{
	ulcm_RequestId requestId; /* RequestId */
	ulcm_UpdateGprsLocationArg_v3 updateGprsLocationArg; /* UpdateGprsLocationArguments */
} ulcm_UpdateGprsRequest;

typedef struct ulcm_UpdateGprsLocationRes_v3 {
    unsigned char   bit_mask;
#       define      UpdateGprsLocationRes_v3_extensionContainer_present 0x80
#       define      UpdateGprsLocationRes_v3_add_Capability_present 0x40
    ulcm_ISDN_AddressString hlr_Number;
    char            add_Capability;  /* extension #1; optional; set in bit_mask
                           * UpdateGprsLocationRes_v3_add_Capability_present if
                           * present */
} ulcm_UpdateGprsLocationRes_v3;

typedef struct ulcm_PDP_Context {
    unsigned char   bit_mask;
#       define      PDP_Context_pdp_Address_present 0x80
#       define      PDP_Context_vplmnAddressAllowed_present 0x40
#       define      PDP_Context_extensionContainer_present 0x20
#       define      PDP_Context_ext_QoS_Subscribed_present 0x10
#       define      PDP_Context_pdp_ChargingCharacteristics_present 0x08
#       define      PDP_Context_ext2_QoS_Subscribed_present 0x04
    ulcm_ContextId  pdp_ContextId;
    ulcm_PDP_Type   pdp_Type;
    ulcm_PDP_Address pdp_Address;  /* optional; set in bit_mask
                                   * PDP_Context_pdp_Address_present if
                                   * present */
    ulcm_QoS_Subscribed qos_Subscribed;
    char            vplmnAddressAllowed;  /* optional; set in bit_mask
                                   * PDP_Context_vplmnAddressAllowed_present if
                                   * present */
    ulcm_APN        apn;
    ulcm_Ext_QoS_Subscribed ext_QoS_Subscribed;  /* extension #1; optional; set in
                                   * bit_mask
                                   * PDP_Context_ext_QoS_Subscribed_present if
                                   * present */
    ulcm_ChargingCharacteristics pdp_ChargingCharacteristics;  /* extension #2;
                                   * optional; set in bit_mask
                           * PDP_Context_pdp_ChargingCharacteristics_present if
                           * present */
    ulcm_Ext2_QoS_Subscribed ext2_QoS_Subscribed;  /* extension #3; optional; set in
                                   * bit_mask
                                   * PDP_Context_ext2_QoS_Subscribed_present if
                                   * present */
} ulcm_PDP_Context;

typedef struct ulcm_GPRSDataList {
    unsigned short  count;
    ulcm_PDP_Context value[7];
} ulcm_GPRSDataList;

/* Note: sgsn_CAMEL_SubscriptionInfo has been deleted from GPRSSubscriptionData */

typedef struct ulcm_GPRSSubscriptionData {
    unsigned char   bit_mask;
#       define      GPRSSubscriptionData_completeDataListIncluded_present 0x80
#       define      GPRSSubscriptionData_extensionContainer_present 0x40
#       define      GPRSSubscriptionData_sgsn_CAMEL_SubscriptionInfo_present 0x20
#       define      GPRSSubscriptionData_chargingCharacteristics_present 0x10
    char            completeDataListIncluded;  /* optional; set in bit_mask
                     * GPRSSubscriptionData_completeDataListIncluded_present if
                     * present */
    ulcm_GPRSDataList gprsDataList;
    ulcm_ChargingCharacteristics chargingCharacteristics;  /* extension #2; optional;
                                   * set in bit_mask
                      * GPRSSubscriptionData_chargingCharacteristics_present if
                      * present */
} ulcm_GPRSSubscriptionData;

typedef struct ulcm_AccessRestrictionData {
    unsigned short  length;  /* number of significant bits */
    unsigned char   value[1];
} ulcm_AccessRestrictionData;

typedef struct ulcm_InsertSubscriberDataArg_v3 {
    unsigned int    bit_mask;
#       define      InsertSubscriberDataArg_v3_imsi_present 0x80000000
#       define      InsertSubscriberDataArg_v3_msisdn_present 0x40000000
#       define      InsertSubscriberDataArg_v3_category_present 0x20000000
#       define      InsertSubscriberDataArg_v3_subscriberStatus_present 0x10000000
#       define      InsertSubscriberDataArg_v3_bearerServiceList_present 0x08000000
#       define      InsertSubscriberDataArg_v3_teleserviceList_present 0x04000000
#       define      InsertSubscriberDataArg_v3_provisionedSS_present 0x02000000
#       define      InsertSubscriberDataArg_v3_odb_Data_present 0x01000000
#       define      InsertSubscriberDataArg_v3_roamingRestrictionDueToUnsupportedFeature_present 0x00800000
#       define      InsertSubscriberDataArg_v3_regionalSubscriptionData_present 0x00400000
#       define      InsertSubscriberDataArg_v3_vbsSubscriptionData_present 0x00200000
#       define      InsertSubscriberDataArg_v3_vgcsSubscriptionData_present 0x00100000
#       define      InsertSubscriberDataArg_v3_vlrCamelSubscriptionInfo_present 0x00080000
#       define      InsertSubscriberDataArg_v3_extensionContainer_present 0x00040000
#       define      InsertSubscriberDataArg_v3_naea_PreferredCI_present 0x00020000
#       define      InsertSubscriberDataArg_v3_gprsSubscriptionData_present 0x00010000
#       define      InsertSubscriberDataArg_v3_roamingRestrictedInSgsnDueToUnsupportedFeature_present 0x00008000
#       define      InsertSubscriberDataArg_v3_networkAccessMode_present 0x00004000
#       define      InsertSubscriberDataArg_v3_lsaInformation_present 0x00002000
#       define      InsertSubscriberDataArg_v3_lmu_Indicator_present 0x00001000
#       define      InsertSubscriberDataArg_v3_lcsInformation_present 0x00000800
#       define      InsertSubscriberDataArg_v3_istAlertTimer_present 0x00000400
#       define      InsertSubscriberDataArg_v3_superChargerSupportedInHLR_present 0x00000200
#       define      InsertSubscriberDataArg_v3_mc_SS_Info_present 0x00000100
#       define      InsertSubscriberDataArg_v3_cs_AllocationRetentionPriority_present 0x00000080
#       define      InsertSubscriberDataArg_v3_sgsn_CAMEL_SubscriptionInfo_present 0x00000040
#       define      InsertSubscriberDataArg_v3_chargingCharacteristics_present 0x00000020
#       define      InsertSubscriberDataArg_v3_accessRestrictionData_present 0x00000010
    ulcm_GPRSSubscriptionData gprsSubscriptionData;  /* extension #2; optional; set
                                   * in bit_mask
                   * InsertSubscriberDataArg_v3_gprsSubscriptionData_present if
                   * present */
    ulcm_AccessRestrictionData accessRestrictionData;  /* extension #14; optional;
                                   * set in bit_mask
                  * InsertSubscriberDataArg_v3_accessRestrictionData_present if
                  * present */
	ulcm_ChargingCharacteristics chargingCharacteristicsOuter;  /* extension #2;
                                   * optional; set in bit_mask
                           * InsertSubscriberDataArg_v3_chargingCharacteristics_present if
                           * present */
    ulcm_ISDN_AddressString msisdn;  /* optional; set in bit_mask
                                 * InsertSubscriberDataArg_v3_msisdn_present if
                                 * present */

} ulcm_InsertSubscriberDataArg_v3;

typedef struct ulcm_UpdateGprsResponse /* SEQUENCE */
{
	ulcm_RequestId requestId; /* RequestId */
	ulcm_UpdateGprsLocationRes_v3 updateGprsLocationRes; /* UpdateGprsLocationResponse */
	ulcm_InsertSubscriberDataArg_v3 insertSubscriberDataArg; /* GPRSSubscriptionData, AccessRestrictionData */
	ulcm_ErrorCode errorCode; /* ErrorCode */
} ulcm_UpdateGprsResponse;




typedef struct ulcm_Request /* CHOICE */
{
    unsigned short choice;
    #define ulcm_initRequest_chosen 1
    #define ulcm_authRequest_chosen 2
    #define ulcm_cancelAuthRequest_chosen 3
    #define ulcm_imsiRequest_chosen 4
    #define ulcm_cancelImsiRequest_chosen 5
    #define ulcm_updateGprsRequest_chosen 6
    #define ulcm_cancelUpdateGprsRequest_chosen 7

    union {
	ulcm_InitRequest initRequest; /* [0] InitRequest */
	ulcm_AuthRequest authRequest; /* [1] AuthRequest */
	ulcm_CancelAuthRequest cancelAuthRequest; /* [2] CancelAuthRequest */
	ulcm_ImsiRequest imsiRequest; /* [3] ImsiRequest */
	ulcm_CancelImsiRequest cancelImsiRequest; /* [4] CancelImsiRequest */
	ulcm_UpdateGprsRequest updateGprsRequest; /* [5] UpdateGprsRequest */
 	ulcm_CancelUpdateGprsRequest cancelUpdateGprsRequest; /* [6] CancelUpdateGprsRequest */
    } u;
} ulcm_Request;


typedef struct ulcm_Response /* CHOICE */
{
    unsigned short  choice;
#       define      ulcm_initResponse_chosen 1
#       define      ulcm_authResponse_chosen 2
#       define      ulcm_errorResponse_chosen 3
#       define      ulcm_imsiResponse_chosen 4
#       define      ulcm_updateGprsResponse_chosen 5
    union  {
        ulcm_InitResponse initResponse; /* [0] InitResponse */
        ulcm_AuthResponse authResponse; /* [1] AuthResponse */
        ulcm_ErrorResponse errorResponse; /* [2] ErrorResponse */
        ulcm_ImsiResponse imsiResponse; /* [3] ImsiResponse */
        ulcm_UpdateGprsResponse updateGprsResponse; /* [4] UpdateGprsResponse */
    } u;
} ulcm_Response;


/* ========== Object Declarations ========== */

extern void *MAPgateway;    /* encoder-decoder table */

/* ========== Object Set Declarations ========== */
#ifdef __cplusplus
extern "C" {
#endif

#endif /* conditional include of MAPgateway.h */

/* Start of data structures copied from $OMNI_HOME/include/gsmpdus.h and modified slightly */

/* Note: The extension containers have been removed to help keep the TCP message size down */

/* Note: The size of the GPRSDataList has been reduced from 50 to 7 */

//typedef struct ulcm_UpdateGprsLocationArg_v3 {
    //unsigned char   bit_mask;
//#       define      UpdateGprsLocationArg_v3_extensionContainer_present 0x80
//#       define      UpdateGprsLocationArg_v3_sgsn_Capability_present 0x40
//#       define      UpdateGprsLocationArg_v3_informPreviousNetworkEntity_present 0x20
//#       define      UpdateGprsLocationArg_v3_ps_LCS_NotSupportedByUE_present 0x10
//#       define      UpdateGprsLocationArg_v3_v_gmlc_Address_present 0x08
//#       define      UpdateGprsLocationArg_v3_add_info_present 0x04
    //ulcm_IMSI       imsi;
    //ulcm_ISDN_AddressString sgsn_Number;
    //ulcm_GSN_Address sgsn_Address;
    //ulcm_SGSN_Capability sgsn_Capability;  /* extension #1; optional; set in bit_mask
                          //* UpdateGprsLocationArg_v3_sgsn_Capability_present if
                          //* present */
    //char            informPreviousNetworkEntity;  /* extension #2; optional; set
                                   //* in bit_mask
              //* UpdateGprsLocationArg_v3_informPreviousNetworkEntity_present if
              //* present */
    //char            ps_LCS_NotSupportedByUE;  /* extension #3; optional; set in
                                   //* bit_mask
                  //* UpdateGprsLocationArg_v3_ps_LCS_NotSupportedByUE_present if
                  //* present */
    //ulcm_GSN_Address v_gmlc_Address;  /* extension #4; optional; set in bit_mask
                           //* UpdateGprsLocationArg_v3_v_gmlc_Address_present if
                           //* present */
    //ulcm_ADD_Info   add_info;  /* extension #5; optional; set in bit_mask
                                //* UpdateGprsLocationArg_v3_add_info_present if
                                //* present */
//} ulcm_UpdateGprsLocationArg_v3;
/* End of data structures copied from $OMNI_HOME/include/gsmpdus.h and modified slightly */
