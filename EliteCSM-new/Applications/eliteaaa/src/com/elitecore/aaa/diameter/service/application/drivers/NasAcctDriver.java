package com.elitecore.aaa.diameter.service.application.drivers;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class NasAcctDriver {
	
	public int getAcctReqStatusType(ApplicationRequest acctRequest) {
		return (int)acctRequest.getAVP(DiameterAVPConstants.ACCOUNTING_RECORD_TYPE).getInteger();
	}

	protected String getNameFromArray(String[] fileAttributes,ServiceRequest request) {
		ApplicationRequest appRequest  = (ApplicationRequest)request;
		String strPrefixFileName = null;
		IDiameterAVP diameterAvp = null;
		if(fileAttributes != null && fileAttributes.length > 0) {
			int noOfFileAttr = fileAttributes.length;
			for(int i=0;i<noOfFileAttr;i++) {
				diameterAvp = appRequest.getAVP(fileAttributes[i],true);
				if(diameterAvp!=null){
					String strAttributeValue = diameterAvp.getStringValue();
					if(diameterAvp.getVendorId()==0){
						if(diameterAvp.getAVPCode()==DiameterAVPConstants.USER_NAME_INT){
							if(strAttributeValue.contains("@")) {
								strAttributeValue = strAttributeValue.substring(strAttributeValue.indexOf('@') + 1);
							}
						}
						strPrefixFileName = (strPrefixFileName != null) ? (strPrefixFileName+"_" + strAttributeValue): strAttributeValue;
					}else {
						strPrefixFileName = (strPrefixFileName != null) ? (strPrefixFileName+"_"+ strAttributeValue): strAttributeValue;
					}
				}
			}
		}
		return strPrefixFileName;	
	}
}
