/*
 *	EAP Project
 *	Elitecore Technologies Ltd.
 *	904, Silicon Tower, Law Garden
 *	Ahmedabad, India - 380009
 *
 *	Created on Jan 8, 2007
 *	Created By Hrishikesh K. Trivedi
 */
package com.elitecore.aaa.core.eap.session;

import java.util.List;

import javax.annotation.Nullable;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;




 /** 
 * 
 * @author Elitecore Technologies Ltd.
 * 
 * Represents an EAPSessionID consists of the values of Radius Attributes in the RadiusPacket.
 * 
 * 		RFC 3579 : 2.6.1 Identifier Space 
 * 
 * 			NAS-Identifier
 * 			NAS-IP-Address
 * 			User-Name
 * 			NAS-Port
 * 			NAS-Port-Type
 * 			NAS-Port-Id
 * 			Called-Station-Id		
 * 			Calling-Station-Id
 * 			Originating-Line-Info -- (Type:94)
 * 
 * */
 
public class EAPSessionId {
 
	public static final String MODULE = "EAPSESSIONID";
	private String nasIdendtifier;
	private String nasIPAddress;
	private String userName;
	private String nasPort;
	private String nasPortType;
	private String nasPortID;
	private String calledStationID;
	private String callingStationID;
	private String originatingLineInfo;
	
	public EAPSessionId(){
		nasIPAddress       	="0";
		userName     		="0";
		nasPort      		="0";
		nasPortType  		="0";
		nasPortID    		="0";
		calledStationID		="0";
		callingStationID	="0";
		originatingLineInfo ="0";
	}
	
	public static String createEapSessionId(RadServiceRequest radiusServiceRequest, List<String> tempIdPrefixes){
		
		if (tempIdPrefixes != null){
			IRadiusAttribute userNameAttr = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
			if (userNameAttr != null){
				String userName = userNameAttr.getStringValue();
				for (int i=0 ; i<tempIdPrefixes.size() ; i++){
					if (userName.startsWith(tempIdPrefixes.get(i))){
						if (userName.indexOf('@') != -1){
							return userName.substring(0, userName.indexOf('@'));
						}
						return userName;
					}
				}
			}
		}
		
		IRadiusAttribute stateAttribute = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.STATE);
		EAPSessionId eapSessionId=null; 
		String strEapSessionId="";
		if(stateAttribute!=null){
			String stateValue = stateAttribute.getStringValue();
			if(stateValue!=null && stateValue.length()>0){
					strEapSessionId = stateValue;
			}else{
				eapSessionId = new EAPSessionId(radiusServiceRequest);
				strEapSessionId=eapSessionId.toString();
			}
		}else{
			eapSessionId = new EAPSessionId(radiusServiceRequest);
			strEapSessionId=eapSessionId.toString();
		}  
		return strEapSessionId;
	}
	// If u change order of all Attribute at that time also change order of valueOf method's attribute order.
	/**
	 * @return new instance of EAP Session Id if eapSessionId is valid. 
	 * Returns {@code null} if eapSessionId is {@code null} or invalid.
	 */
	public static @Nullable EAPSessionId valueOf(String eapSessionId){
		if(eapSessionId == null){
			return null;
		}

		String [] eapSessionsStr = eapSessionId.split(" ");
		if (eapSessionsStr.length != 9) {
			return null;
		}
		
		EAPSessionId eapsession = new EAPSessionId();
		eapsession.callingStationID = eapSessionsStr[0];
		eapsession.userName = eapSessionsStr[1];
		eapsession.nasIPAddress = eapSessionsStr[2];
		eapsession.nasIdendtifier = eapSessionsStr[3];
		eapsession.nasPort = eapSessionsStr[4];
		eapsession.nasPortID = eapSessionsStr[5];
		eapsession.nasPortType = eapSessionsStr[6];
		eapsession.calledStationID = eapSessionsStr[7];
		eapsession.originatingLineInfo = eapSessionsStr[8];

		return eapsession;

	}
	
	public EAPSessionId(RadServiceRequest radiusServiceRequest){
		IRadiusAttribute radiusAttr=null;
		
		radiusAttr= radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER);
		this.nasIdendtifier= (radiusAttr!=null)?radiusAttr.getStringValue():"0";
		
		radiusAttr = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
		this.nasIPAddress= (radiusAttr!=null)?radiusAttr.getStringValue():"0";
		
		radiusAttr = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
		this.userName= (radiusAttr!=null)?radiusAttr.getStringValue():"0";
		
		radiusAttr = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT);
		this.nasPort= (radiusAttr!=null)?radiusAttr.getStringValue():"0";
		
		radiusAttr = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT_TYPE);
		this.nasPortType= (radiusAttr!=null)?radiusAttr.getStringValue():"0";
		
		radiusAttr = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT_ID);
		this.nasPortID= (radiusAttr!=null)?radiusAttr.getStringValue():"0";

		radiusAttr = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.CALLED_STATION_ID);
		this.calledStationID= (radiusAttr!=null)?radiusAttr.getStringValue():"0";
		
		radiusAttr = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
		this.callingStationID= (radiusAttr!=null)?radiusAttr.getStringValue():"0";

		radiusAttr = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.ORIGINATING_LINE_INFO);
		this.originatingLineInfo= (radiusAttr!=null)?radiusAttr.getStringValue():"0";
	}
	public String getCalledStationID() {
		return this.calledStationID;
	}
	public void setCalledStationID(String calledStationID) {
		this.calledStationID = calledStationID;
	}
	public String getCallingStationID() {
		return this.callingStationID ;
	}
	public void setCallingStationID(String callingStationID) {
		this.callingStationID = callingStationID;
	}
	public String getNasIdendtifier() {
		return this.nasIdendtifier;
	}
	public void setNasIdendtifier(String nasIdendtifier) {
		this.nasIdendtifier = nasIdendtifier;
	}
	public String getNasIPAddress() {
		return this.nasIPAddress;
	}
	public void setNasIPAddress(String nasIPAddress) {
		this.nasIPAddress = nasIPAddress;
	}
	public String getNasPort() {
		return this.nasPort;
	}
	public void setNasPort(String nasPort) {
		this.nasPort = nasPort;
	}
	public String getNasPortID() {
		return this.nasPortID;
	}
	public void setNasPortID(String nasPortID) {
		this.nasPortID = nasPortID;
	}
	public String getNasPortType() {
		return this.nasPortType;
	}
	public void setNasPortType(String nasPortType) {
		this.nasPortType = nasPortType;
	}
	public String getOriginatingLineInfo() {
		return this.originatingLineInfo;
	}
	public void setOriginatingLineInfo(String originatingLineInfo) {
		this.originatingLineInfo = originatingLineInfo;
	}
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Do not change the sequence of appending as it has the significane.
	 */
	// If u change order of all Attribute at that time also change order of valueOf method's attribute order.
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append(this.getCallingStationID()+ " ");
		strBuilder.append(this.getUserName()+ " ");
		strBuilder.append(this.getNasIPAddress()+ " ");
		strBuilder.append(this.getNasIdendtifier()+ " ");
		strBuilder.append(this.getNasPort()+ " ");
		strBuilder.append(this.getNasPortID()+ " ");
		strBuilder.append(this.getNasPortType()+ " ");
		strBuilder.append(this.getCalledStationID()+ " ");
		strBuilder.append(this.getOriginatingLineInfo());
		
		if(strBuilder.length()>253){
			strBuilder.setLength(253);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "The EAPSessionId is trimmed for Username:" + this.getUserName());
		}
		return strBuilder.toString();
	}
	
	public boolean equals(Object obj){		
		if (obj instanceof EAPSessionId) {
			EAPSessionId sessionID = (EAPSessionId) obj;
//			Logger.logInfo("this -> SESSION", this + " obj->" + sessionID);
		    //TODO - complete the follwing comparision for all the memeber variables.	
			if (this.callingStationID.equals(sessionID.callingStationID) &&
					this.userName.equals(sessionID.userName) &&
					this.nasIPAddress.equals(sessionID.nasIPAddress) &&
					this.nasIdendtifier.equals(sessionID.nasIdendtifier) && 
					this.nasPort.equals(sessionID.nasPort) &&
					this.nasPortID.equals(sessionID.nasPortID) &&
					this.nasPortType.equals(sessionID.nasPortType) &&
					this.calledStationID.equals(sessionID.calledStationID) &&
					this.originatingLineInfo.equals(sessionID.originatingLineInfo)){
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		return 1;
	}
}
 
