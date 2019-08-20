/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source   SMSgateway.h

@ClearCase-version: $Revision:/main/sw9/2 $ 

@date     $Date:16-Dec-2008 15:50:49 $

@product  Signalware

@subsystem  WLAN Gateway

DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE DEVELOPMENT OF A WORKING SOFTWARE PROGRAM.  THE SOURCE CODE PROVIDED IS NOT WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.
 
 ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER  INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and interest in and to the Software, and all intellectual property rights associated therewith, including without limitation, rights to copyrights, trade secrets or know-how. This disclaimer agreement shall be governed and construed in accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time without notice. Ulticom has no continuing obligation to provide additional source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples; these changes may be reported, for the sample code included herein, in new editions of the product.

******************************************************************************/

#ifndef _SMSgateway_h_
#define _SMSgateway_h_




#ifdef __cplusplus
extern "C" {
#endif

#ifndef FALSE
#define FALSE   0
#endif
#ifndef TRUE
#define TRUE    1
#endif

#define          ulcm_smsRequest_PDU 1
#define          ulcm_smsResponse_PDU 2


typedef int ulcm_smsVersion; /* INTEGER */


typedef int ulcm_smsRequestId; /* INTEGER */


typedef enum
    {
        ulcm_smsnoError = 0,
        ulcm_smsfacilityNotSupported = 65536,
        ulcm_smscallBarred = 65537,
        ulcm_smsteleserviceNotSupported = 65538,
        ulcm_smsunknownImsi = 131072,
        ulcm_smscommError = 196608,
        ulcm_smsmalformedMessage = 196609,
        ulcm_smsmaxRequestExceeded = 196610,
        ulcm_smsgatewayError = 196611,
        ulcm_smsgeneralError = 327680,
        ulcm_smsgatewayProvisioningError = 327681,
        ulcm_smsmemorycapacityExceeded = 458753,
        ulcm_smsequipmentNotSMEquipped = 458754,
        ulcm_smsillegalEquipment = 458755,
        ulcm_smsequipmentProtocolError = 458756,
        ulcm_smssubscriberBusy = 458757,
        ulcm_smsunknownServiceCenter = 458758,
        ulcm_smsserviceCenterCongestion = 458759,
        ulcm_smsinvalidSMEAddress = 458760,
        ulcm_smssubscriberNotServiceCenterSubscriber = 458761,
        ulcm_smsabsentSubscriber = 458762,
        ulcm_smssystemFailure = 458763
    } ulcm_smsErrorCode; /* ENUMERATED { ulcm_smsnoError (0), ulcm_smsfacilityNotSupported (65536), ulcm_smscallBarred (65537), ulcm_smsteleserviceNotSupported (65538), ulcm_smsunknownImsi (131072), ulcm_smscommError (196608), ulcm_smsmalformedMessage (196609), ulcm_smsmaxRequestExceeded (196610), ulcm_smsgatewayError (196611), ulcm_smsgeneralError (327680), ulcm_smsgatewayProvisioningError (327681), ulcm_smsmemorycapacityExceeded (458753), ulcm_smsequipmentNotSMEquipped (458754), ulcm_smsillegalEquipment (458755), ulcm_smsequipmentProtocolError (458756), ulcm_smssubscriberBusy (458757), ulcm_smsunknownServiceCenter (458758), ulcm_smsserviceCenterCongestion (458759), ulcm_smsinvalidSMEAddress (458760), ulcm_smssubscriberNotServiceCenterSubscriber (458761), ulcm_smsabsentSubscriber (458762), ulcm_smssystemFailure (458763) }  */


typedef struct ulcm_smsSignalInfo
{
    unsigned short length;
    unsigned char value[200];

} ulcm_smsSignalInfo; /* OCTET STRING SIZE 1..200 */
    

typedef struct ulcm_smsLMSI
{
    unsigned short  length;
    unsigned char   value[4];

} ulcm_smsLMSI; /* OCTET STRING SIZE 4 */


typedef struct ulcm_smsAddressString
{
    unsigned short  length;
    unsigned char   value[20];

} ulcm_smsAddressString; /* OCTET STRING SIZE 1..20 */


typedef struct ulcm_smsInitRequest /* SEQUENCE */
{
	ulcm_smsVersion version; /* Version */
} ulcm_smsInitRequest;


typedef struct ulcm_smsInitResponse /* SEQUENCE */
{
	int maxRequest; /* INTEGER */
	int versionSupported; /* BOOLEAN */
	ulcm_smsErrorCode errorCode; /* ErrorCode */
} ulcm_smsInitResponse;


typedef struct ulcm_smsMOSMSResponse /* SEQUENCE */
{
	ulcm_smsRequestId requestId; /* RequestId */
	ulcm_smsSignalInfo signalInfo; /* SignalInfo */
	ulcm_smsErrorCode errorCode; /* ErrorCode */
} ulcm_smsMOSMSResponse;


typedef struct ulcm_smsMTSMSResponse /* SEQUENCE */
{
	ulcm_smsRequestId requestId; /* RequestId */
	ulcm_smsSignalInfo signalInfo; /* SignalInfo */
	ulcm_smsErrorCode errorCode; /* ErrorCode */
} ulcm_smsMTSMSResponse;


typedef struct ulcm_smsErrorResponse /* SEQUENCE */
{
	ulcm_smsErrorCode errorCode; /* ErrorCode */
} ulcm_smsErrorResponse;


typedef ulcm_smsRequestId ulcm_smsCancelSMS; /* RequestId */


typedef struct ulcm_smsIMSI
{
    unsigned short length;
    unsigned char value[8];

} ulcm_smsIMSI; /* TBCD-STRING SIZE 3..8 */


typedef struct 
{
    unsigned short  length;
    unsigned char   value[9];
}  ulcm_smsISDN_AddressString; /* AddressString SIZE 1..maxISDN-AddressLength */


typedef struct ulcm_smsMTSMSRequest /* SEQUENCE */
{
    unsigned char   bit_mask;
#       define      ulcm_smsmoreMessagesToSend_present 0x80
	ulcm_smsRequestId requestId; /* RequestId */
	ulcm_smsIMSI imsi; /* IMSI */
	ulcm_smsSignalInfo shortMessage; /* SignalInfo */
	char moreMessagesToSend; /* NULL OPTIONAL */
} ulcm_smsMTSMSRequest;


typedef struct ulcm_smsRoutingInfoReq /* SEQUENCE */
{
	ulcm_smsRequestId requestId; /* RequestId */
	ulcm_smsISDN_AddressString msisdn; /* ISDN-AddressString */
} ulcm_smsRoutingInfoReq;


typedef struct ulcm_smsSM_RP_DA /* CHOICE */
{
    unsigned short  choice;
#       define      ulcm_smsimsi_chosen 1
#       define      ulcm_smslmsi_chosen 2
#       define      ulcm_smsserviceCentreAddressDA_chosen 3
#       define      ulcm_smsnoSM_RP_DA_chosen 4
    union {
        ulcm_smsIMSI imsi; /* [0] IMSI */
        ulcm_smsLMSI lmsi; /* [1] LMSI */
        ulcm_smsAddressString serviceCentreAddressDA; /* [4] AddressString */
        char noSM_RP_DA; /* [5] NULL */
    } u;
} ulcm_smsSM_RP_DA;


typedef struct ulcm_smsSM_RP_OA /* CHOICE */
{
    unsigned short  choice;
#       define      ulcm_smsmsisdn_chosen 1
#       define      ulcm_smsserviceCentreAddressOA_chosen 2
#       define      ulcm_smsnoSM_RP_OA_chosen 3
    union  {
        ulcm_smsISDN_AddressString msisdn; /* [2] ISDN-AddressString */
        ulcm_smsAddressString serviceCentreAddressOA; /* [4] AddressString */
        char noSM_RP_OA; /* [5] NULL */
    } u;
} ulcm_smsSM_RP_OA;


typedef struct ulcm_smsMOSMSRequest /* SEQUENCE */
{
	ulcm_smsRequestId requestId; /* RequestId */
	ulcm_smsSM_RP_DA sm_RP_DA; /* SM-RP-DA */
	ulcm_smsSM_RP_OA sm_RP_OA; /* SM-RP-OA */
	ulcm_smsIMSI imsi; /* IMSI */
	ulcm_smsSignalInfo shortMessage; /* SignalInfo */
} ulcm_smsMOSMSRequest;

typedef struct ulcm_smsAdditional_Number /* CHOICE */
{
    unsigned short  choice;
#       define      ulcm_smsmsc_Number_chosen 1
#       define      ulcm_smssgsn_Number_chosen 2
    union {
        ulcm_smsISDN_AddressString msc_Number; /* [0] ISDN-AddressString */
        ulcm_smsISDN_AddressString sgsn_Number; /* [1] ISDN-AddressString */
    } u;
} ulcm_smsAdditional_Number;

typedef struct ulcm_smsLocationInfoWithLMSI /* SEQUENCE */
{
    unsigned char   bit_mask;
#       define      ulcm_smslmsi_present 0x80
#       define      ulcm_smsextensionContainer_present 0x40
#       define      ulcm_smsgprsNodeIndicator_present 0x20
#       define      ulcm_smsadditional_Number_present 0x10
	ulcm_smsISDN_AddressString networkNode_Number; /* [1] ISDN-AddressString */
	ulcm_smsLMSI lmsi; /* LMSI OPTIONAL */
	char gprsNodeIndicator; /* [5] NULL OPTIONAL */
	ulcm_smsAdditional_Number additional_Number; /* [6] Additional-Number OPTIONAL */
} ulcm_smsLocationInfoWithLMSI;

typedef struct ulcm_smsRoutingInfoResponse /* SEQUENCE */
{
	ulcm_smsRequestId requestId; /* RequestId */
	ulcm_smsIMSI imsi; /* IMSI */
	ulcm_smsLocationInfoWithLMSI locationInfoWithLMSI; /* LocationInfoWithLMSI */
	ulcm_smsErrorCode errorCode; /* ErrorCode */
} ulcm_smsRoutingInfoResponse;


typedef struct ulcm_smsRequest /* CHOICE */
{
    unsigned short  choice;
#       define      ulcm_smsinitRequest_chosen 1
#       define      ulcm_smsmoSmsRequest_chosen 2
#       define      ulcm_smscancelSMS_chosen 3
#       define      ulcm_smsroutingInfoReq_chosen 4
#       define      ulcm_smsmtSmsRequest_chosen 5
    union {
        ulcm_smsInitRequest initRequest; /* [0] InitRequest */
        ulcm_smsMOSMSRequest moSmsRequest; /* [1] MOSMSRequest */
        ulcm_smsCancelSMS cancelSMS; /* [2] CancelSMS */
        ulcm_smsRoutingInfoReq routingInfoReq; /* [3] RoutingInfoReq */
        ulcm_smsMTSMSRequest mtSmsRequest; /* [4] MTSMSRequest */
    } u;
} ulcm_smsRequest;


typedef struct ulcm_smsResponse /* CHOICE */
{
    unsigned short  choice;
#       define      ulcm_smsinitResponse_chosen 1
#       define      ulcm_smsmoSmsResponse_chosen 2
#       define      ulcm_smserrorResponse_chosen 3
#       define      ulcm_smsroutingInfoResponse_chosen 4
#       define      ulcm_smsmtSmsResponse_chosen 5
    union {
        ulcm_smsInitResponse initResponse; /* [0] InitResponse */
        ulcm_smsMOSMSResponse moSmsResponse; /* [1] MOSMSResponse */
        ulcm_smsErrorResponse errorResponse; /* [2] ErrorResponse */
        ulcm_smsRoutingInfoResponse routingInfoResponse; /* [3] RoutingInfoResponse */
        ulcm_smsMTSMSResponse mtSmsResponse; /* [4] MTSMSResponse */
    } u;
} ulcm_smsResponse;




/* ========== Object Declarations ========== */

extern void *SMSgateway;    /* encoder-decoder table */

/* ========== Object Set Declarations ========== */
#ifdef __cplusplus
extern "C" {
#endif

#endif /* conditional include of SMSgateway.h */
