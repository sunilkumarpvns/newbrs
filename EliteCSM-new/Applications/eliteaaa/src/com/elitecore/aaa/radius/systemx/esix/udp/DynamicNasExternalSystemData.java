package com.elitecore.aaa.radius.systemx.esix.udp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.Nonnull;

import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.core.systemx.esix.udp.StatusCheckMethod;
import com.elitecore.core.util.generator.UUIDGenerator;
import com.elitecore.core.util.url.URLData;

/**
 * <p>This is the data class to store some of the details of external system. Other details
 * of external system is stored in its base class. This details of the external system 
 * will be required when we need to create dynamic NAS.</p>
 * 
 * <p>For dynamic NAS creation, this data class is used because this will contain COA and DM, 
 * both types of supported and unsupported attributes at a time unlike other external system.</p> 
 * 
 * @author Kuldeep Panchal
 *
 */
public class DynamicNasExternalSystemData extends RadiusExternalSystemData {

	public static final String ESI_NAME_PREFIX = "NAS-";
	private static final int STATUS_CHECK_DURATION = 120;
	private static final int RETRY_COUNT = 3;
	private static final int MIN_LOCAL_PORT = 1;
	private static final int EXPIRED_REQUEST_LIMIT_COUNT = -1;
	
	private String coaSupportedAttributesStr;
	private String coaUnSupportedAttributesStr;
	private String dmSupportedAttributesStr;
	private String dmUnSupportedAttributesStr;

	
	public DynamicNasExternalSystemData(){
		//required By Jaxb.
	}
	
	public DynamicNasExternalSystemData(String targetSystemIPAddress,
			int expCount, int communicationTimeout,
			int minLocalPort, String sharedSecret, int retryCount, int statusCheckDuration,
			String name, int esiType,
			String coaSupportedAttributesStr,String coaUnSupportedAttributesStr, 
			String dmSupportedAttributesStr,String dmUnSupportedAttributesStr,
			StatusCheckMethod scannerType, String scannerPacket) {
		
		super(UUIDGenerator.generate(), targetSystemIPAddress, expCount, communicationTimeout,
				minLocalPort, sharedSecret, retryCount, statusCheckDuration,name,esiType, scannerType, scannerPacket);
		this.coaSupportedAttributesStr = coaSupportedAttributesStr;
		this.coaUnSupportedAttributesStr = coaUnSupportedAttributesStr;
		this.dmSupportedAttributesStr = dmSupportedAttributesStr;
		this.dmUnSupportedAttributesStr = dmUnSupportedAttributesStr;

	}
	
	/**
	 * This will help to create Dynamic NAS external system data with the provided parameters.
	 * 
	 * <p>Note: Whenever this method is called IP Address and Port must be set externally on the returned 
	 * external system.</p>
	 * @throws UnknownHostException 
	 * 
	 */
	public static @Nonnull DynamicNasExternalSystemData create(
			URLData url, long timeout, String sharedSecret, 
			String supportedAttributesStrCOA,
			String unsupportedAttributesStrCOA,
			String supportedAttributesStrDM, String unsupportedAttributesStrDM) throws UnknownHostException {
		
		 DynamicNasExternalSystemData dynamicNasExternalSystemData = new DynamicNasExternalSystemData(url.getHost() + ":" + url.getPort(), 
				EXPIRED_REQUEST_LIMIT_COUNT, (int) timeout, 
				MIN_LOCAL_PORT, sharedSecret, RETRY_COUNT, STATUS_CHECK_DURATION, 
				ESI_NAME_PREFIX + url.getHost(), RadESTypeConstants.DYNAMIC_NAS.type, 
				supportedAttributesStrCOA, unsupportedAttributesStrCOA, 
				supportedAttributesStrDM, unsupportedAttributesStrDM,
				null, null);
		
		dynamicNasExternalSystemData.setIPAddress(InetAddress.getByName(url.getHost()));
		dynamicNasExternalSystemData.setPort(url.getPort());
		
		return dynamicNasExternalSystemData;
	}
	
	public String getCoaSupportedAttributesStr() {
		return coaSupportedAttributesStr;
	}
	
	public void setCoaSupportedAttributesStr(String coaSupportedAttributesStr) {
		this.coaSupportedAttributesStr = coaSupportedAttributesStr;
	}
	

	public String getCoaUnSupportedAttributesStr() {
		return coaUnSupportedAttributesStr;
	}
	
	public void setCoaUnSupportedAttributesStr(
			String coaUnSupportedAttributesStr) {
		this.coaUnSupportedAttributesStr = coaUnSupportedAttributesStr;
	}
	
	public String getDmSupportedAttributesStr() {
		return dmSupportedAttributesStr;
	}
	
	public void setDmSupportedAttributesStr(String dmSupportedAttributesStr) {
		this.dmSupportedAttributesStr = dmSupportedAttributesStr;
	}
	
	public String getDmUnSupportedAttributesStr() {
		return dmUnSupportedAttributesStr;
	}
	
	public void setDmUnSupportedAttributesStr(String dmUnSupportedAttributesStr) {
		this.dmUnSupportedAttributesStr = dmUnSupportedAttributesStr;
	}
	
	@Override
	public String toString() {
		String toString = super.toString();
		StringBuilder strBuilder = new StringBuilder(toString);
		strBuilder.append("coa supported attributes: ");
		strBuilder.append(getCoaSupportedAttributesStr());
		strBuilder.append("\n");
		strBuilder.append("coa un-supported attributes: ");
		strBuilder.append(getCoaUnSupportedAttributesStr());
		strBuilder.append("\n");
		strBuilder.append("dm supported attributes: ");
		strBuilder.append(getDmSupportedAttributesStr());
		strBuilder.append("\n");
		strBuilder.append("dm un-supported attributes: ");
		strBuilder.append(getDmUnSupportedAttributesStr());
		strBuilder.append("\n");
		strBuilder.append("Status-Check-Method: ");
		strBuilder.append(getStatusCheckMethod());
		return strBuilder.toString();
	}
}
