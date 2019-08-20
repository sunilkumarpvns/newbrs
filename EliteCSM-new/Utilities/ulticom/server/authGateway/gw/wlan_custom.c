
/*****************************************************************************

			 Ulticom, Inc.
		Copyright 2005 All Rights Reserved.

	These computer program listings and specifications, herein,
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis
	for manufacture or sale of items without written permission. 

@source             wlan_custom.c

@ClearCase-version: 

@date               $Date:Tue Aug 26 16:19:23 2008

@product            Signalware

@subsystem          

@author             Pascal Linares

@description 

*****************************************************************************/
#include <string.h>
#include <MAPgateway.h>
#include <SMSgateway.h>
#include <wlan_custom.h>

static int wlanMAPgateway = WLANMAPGATEWAY;
static int wlanSMSgateway = WLANSMSGATEWAY;

void *MAPgateway = &wlanMAPgateway;
void *SMSgateway = &wlanSMSgateway;


void wlaninit(WlanGlobal *pWorld, int *pGateway )
{
    *pWorld = *pGateway;
}
void wlanDupWorld(WlanGlobal *pWorld1, WlanGlobal *pWorld2 )
{
    *pWorld2 = *pWorld1;
}


int wlanEncode(WlanGlobal *pWorld, int num, void *raw, WlanBuf *encoded)
{
    int return_code = 0;
    WlanPdu wlanPdu;
    wlanPdu.choice = *pWorld;
    
    switch (*pWorld)
    {
    case WLANMAPGATEWAY:
    {
        wlanPdu.pdu_type.wlan_map_pdu.choice = num;
        
        switch (num)
        {
        case ulcm_Request_PDU:
        {
            wlanPdu.pdu_type.wlan_map_pdu.req_or_resp.request = *(ulcm_Request *)raw;
        }
        break;
        case ulcm_Response_PDU:
        {
            wlanPdu.pdu_type.wlan_map_pdu.req_or_resp.response = *(ulcm_Response *)raw;
        }
        break;
        default:
            return_code = 1;
        }
    }
    break;
    case WLANSMSGATEWAY:
    {
        wlanPdu.pdu_type.wlan_sms_pdu.choice = num;
        
        switch (num)
        {
        case ulcm_smsRequest_PDU:
        {
            wlanPdu.pdu_type.wlan_sms_pdu.req_or_resp.request = *(ulcm_smsRequest *)raw;
        }
        break;
        case ulcm_smsResponse_PDU:
        {
            wlanPdu.pdu_type.wlan_sms_pdu.req_or_resp.response = *(ulcm_smsResponse *)raw;
        }
        break;
        default:
            return_code = 1;
        }
    }
    break;
    default:
        return_code = 1;
        break;
    }
    
    if (0 == return_code)
    {
        encoded->length = sizeof(WlanPdu);
        memcpy(encoded->value, (char *)&wlanPdu, sizeof(WlanPdu));
    }
    
    return return_code;
}

int wlanDecode(WlanGlobal *pWorld, int *num, WlanBuf *encoded, void **raw )
{
    int return_code = 0;
    WlanPdu *pWlanPdu;

    if (sizeof(WlanPdu) != encoded->length)
    {
        wlanPrint2(pWorld, "wlanDecode: received invalid PDU length: %d - awaited: %d\n",
                   encoded->length, (int)sizeof(WlanPdu));
//        return_code = 1;
//        goto end;
    }
    
    pWlanPdu = (WlanPdu *)encoded->value;
              
    switch (pWlanPdu->choice)
    {
    case WLANMAPGATEWAY:
    {
        *num = pWlanPdu->pdu_type.wlan_map_pdu.choice;
        
        switch (*num)
        {
        case ulcm_Request_PDU:
        {
            memcpy(*raw,
                   &(pWlanPdu->pdu_type.wlan_map_pdu.req_or_resp.request),
                   sizeof(ulcm_Request));
        }
        break;
        case ulcm_Response_PDU:
        {
            memcpy(*raw,         
                   &(pWlanPdu->pdu_type.wlan_map_pdu.req_or_resp.response),
                   sizeof(ulcm_Response));
        }
        break;
        default:
            wlanPrint(pWorld, "wlanDecode: received invalid MAP PDU choice: %d\n",
                       pWlanPdu->pdu_type.wlan_map_pdu.choice);
            return_code = 1;
        }
    }
    break;
    case WLANSMSGATEWAY:
    {
        *num = pWlanPdu->pdu_type.wlan_sms_pdu.choice;
        
        switch (*num)
        {
        case ulcm_smsRequest_PDU:
        {
           memcpy(*raw,
                  &(pWlanPdu->pdu_type.wlan_sms_pdu.req_or_resp.request),
                  sizeof(ulcm_smsRequest));
        }
        break;
        case ulcm_smsResponse_PDU:
        {
            memcpy(*raw,
                   &(pWlanPdu->pdu_type.wlan_sms_pdu.req_or_resp.response),
                   sizeof(ulcm_smsResponse));
        }
        break;
        default:
            wlanPrint(pWorld, "wlanDecode: received invalid SMS PDU choice: %d\n",
                       pWlanPdu->pdu_type.wlan_sms_pdu.choice);
            return_code = 1;
        }
    }
    break;
    default:
        wlanPrint(pWorld, "wlanDecode: received invalid PDU choice: %d\n",
                   pWlanPdu->choice);
        return_code = 1;
        break;
    }
    
    return return_code;
}
    
