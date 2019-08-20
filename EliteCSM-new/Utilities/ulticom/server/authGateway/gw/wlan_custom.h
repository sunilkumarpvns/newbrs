#ifndef _WLAN__COMMON__WLAN_CUSTOM_H
#define _WLAN__COMMON__WLAN_CUSTOM_H

/*****************************************************************************

                         Ulticom, Inc.
                Copyright 2005 All Rights Reserved.

        These computer program listings and specifications, herein,
        are the property of Ulticom, Inc. and shall not be 
        reproduced or copied or used in whole or in part as the basis
        for manufacture or sale of items without written permission. 

@source wlan_custom.h

@version $Revision: /main/sw9/1 $

@date               $Date:Tue Aug 19 15:07:45 2008

@product            Signalware

@subsystem          

@author             Pascal Linares

@description 

*****************************************************************************/

#include <stdio.h>
typedef int WlanGlobal;

typedef struct {
    unsigned short choice;
    union {
        ulcm_Request request;
        ulcm_Response response;
    } req_or_resp;
} wlan_map_pdu_t;

typedef struct {
    unsigned short choice;
    union {
        ulcm_smsRequest request;
        ulcm_smsResponse response;
    } req_or_resp;
} wlan_sms_pdu_t;

typedef struct {
    unsigned short choice;
#define WLANMAPGATEWAY 1
#define WLANSMSGATEWAY 2
    union {
        wlan_map_pdu_t   wlan_map_pdu;
        wlan_sms_pdu_t   wlan_sms_pdu;
    } pdu_type;
} WlanPdu;

typedef struct {
    int length;
    unsigned char *value;
} WlanBuf;


#define wlanPrint0(pWorld, format) printf(format)
#define wlanPrint(pWorld, format, data) printf(format, data)
#define wlanPrint2(pWorld, format, data, data2) printf(format, data, data2)
#define wlanPrint3(pWorld, format, data, data2, data3) printf(format, data, data2, data3)
#define wlanPrint4(pWorld, format, data, data2, data3, data4) printf(format, data, data2, data3, data4)
#define wlanPrintHex(pWorld, buffer, length)
#define wlanPrintPDU(pWorld, num, pdu ) 

#define DEBUGPDU 0
#define DEBUG_ERRORS 1

#define wlanSetEncodingFlags(pWorld, flags) 
#define wlanSetDecodingFlags(pWorld, flags) 
#define wlanterm(pWorld)
#define wlanCheckConstraints(pWorld, pdnum, pRequest ) 0


void wlaninit(WlanGlobal *pWorld, int *pGateway );
void wlanDupWorld(WlanGlobal *pWorld1, WlanGlobal *pWorld2 );
int wlanEncode(WlanGlobal *pWorld, int num, void *raw, WlanBuf *encoded);
int wlanDecode(WlanGlobal *pWorld, int *num, WlanBuf *encoded, void **raw );
#define wlanSetDecodingLength(pWorld, size) 


#endif /* _WLAN__COMMON__WLAN_CUSTOM_H*/
