package com.elitecore.aaa.radius.drivers;


import com.elitecore.aaa.core.drivers.DBAcctDriver;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;


public class RadAcctDriver implements IRadAcctDriver {
	
	private static String MODULE = "RAD-ACCT-DRVR";

	@Override
	public int getAcctReqStatusType(RadServiceRequest request) {
		
		int iStatusType = DBAcctDriver.ACCT_STATUS_TYPE_NOT_FOUND;
		IRadiusAttribute statusTypeAttr = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE); 
		if(statusTypeAttr!=null){
			iStatusType = statusTypeAttr.getIntValue();
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Acct-Status-Type attribute not found in request");
		}
		return iStatusType;
		
		
	}
	protected String getNameFromArray(String[] fileAttributes,RadServiceRequest request) {
		String strPrefixFileName = null;
		IRadiusAttribute radiusAttribute = null;
		
		if(fileAttributes != null && fileAttributes.length > 0) {
			int noOfFileAttr = fileAttributes.length;
			for(int i=0;i<noOfFileAttr;i++) {
				radiusAttribute = request.getRadiusAttribute(fileAttributes[i],true);
				if(radiusAttribute!=null){
					String atttibuteValue = radiusAttribute.getStringValue();
					if(radiusAttribute.getVendorID()== RadiusConstants.STANDARD_VENDOR_ID){
						if(radiusAttribute.getID() ==RadiusAttributeConstants.USER_NAME){
							if(atttibuteValue.contains("@"))
								atttibuteValue = atttibuteValue.substring(atttibuteValue.indexOf('@') + 1);
						}
						strPrefixFileName = (strPrefixFileName != null) ? (strPrefixFileName+"_" + atttibuteValue): atttibuteValue;
					}else {
						strPrefixFileName = (strPrefixFileName != null) ? (strPrefixFileName+"_"+ atttibuteValue): atttibuteValue;
					}
				}
				
			}
		}
		return strPrefixFileName;
	}

}
