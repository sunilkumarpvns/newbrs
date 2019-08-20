package com.elitecore.diameterapi.diameter.common.explicitrouting;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.explicitrouting.exception.ExplicitRoutingFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * This handles Explicit Diameter Routing. 
 * Explicit Routing guarantees that certain chain of diameter nodes 
 * will participate in routing of all Messages of that Session. 
 * For more details Refer RFC 6159
 * 
 * @author monica.lulla
 *
 */
public class ExplicitRoutingHandler {
	private static final String MODULE = "EXP-RTNG-HNDLR";
	private static final int OWN_PATH_RECORD_NOT_FOUND = -1;
	/**
	 * This handles explicit routing of incoming Request Messages. 
	 * Thus, it is  associated with ER-Proxy and ER-Destination Roles.
	 * 
	 * @param diameterPacket on which to perform explicit routing
	 * @throws ExplicitRoutingFailedException on Invalid Proxy Stack and Diameter ER not Available.
	 */
	public void handle(DiameterPacket diameterPacket) throws ExplicitRoutingFailedException {

		if(diameterPacket.isResponse())
			return;
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Handling Explicit Routing for Session-ID=" + 
			diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
		}
		try{
			AvpGrouped explicitPathAVP = (AvpGrouped) diameterPacket.getAVP(DiameterAVPConstants.HW_ELIPLICIT_PATH);
			if(explicitPathAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Not performing Explicit Routing, Reason: " + 
					DiameterAVPConstants.HW_ELIPLICIT_PATH_STR + " AVP not found");
				}
				return;
			}
			List<IDiameterAVP> explicitPathRecordAVPs = explicitPathAVP.getSubAttributeList(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
			if(explicitPathRecordAVPs.size() == 0){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Sending " + ResultCode.DIAMETER_ER_NOT_AVAILABLE +", Reason: " +	
					DiameterAVPConstants.HW_ELIPLICIT_PATH_STR + " Grouped AVP does not contain "+ 
					DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR + " AVP.");
				}
				throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, 
						DiameterAVPConstants.HW_ELIPLICIT_PATH_STR + " Grouped AVP does not contain "+ 
						DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR + " AVP.");
			}

			int ownPathRecordIndex = getOwnPathRecordIndex(explicitPathRecordAVPs);
			if(ownPathRecordIndex == OWN_PATH_RECORD_NOT_FOUND){

				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Own Explicit-Path-Record not found for Session-ID=" +
					diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
				
				IDiameterAVP destHost = diameterPacket.getAVP(DiameterAVPConstants.DESTINATION_HOST);
				if(isOngoingPathDiscovery(destHost, explicitPathRecordAVPs)){

					appendOwnIdentity(explicitPathAVP);
					return;
				}
				/*
				 * Path has been discovered and we are not in path. 
				 * Possibly, we might not have received initial req of this session or we might not have participated.
				 */
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Not participating in Explicit Routing, Reason: Explicit Path already discovered for Session-ID=" + 
					diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
				}
				return;
			}

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Own Explicit-Path-Record found for Session-ID=" +
				diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
			/*
			 * Own Identity if present it MUST be the First Record --> Index=1
			 */
			if(ownPathRecordIndex != 1){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Sending " + ResultCode.DIAMETER_INVALID_PROXY_PATH_STACK +
					", Reason: Own idenity is present but not in first Explicit Path Record");
				}
				throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_INVALID_PROXY_PATH_STACK, 
						"Own idenity is present but not in first path record");
			}

			if(explicitPathRecordAVPs.size() == 1){
				//Single Explicit-Path-Record found --> Act as Destination Node
				handleERDestination();
				return;
			}
			
			handleERProxy(diameterPacket, explicitPathAVP, explicitPathRecordAVPs);

		}catch(ClassCastException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Sending " + ResultCode.DIAMETER_ER_NOT_AVAILABLE +
				", Reason: Unable to parse " + DiameterAVPConstants.HW_ELIPLICIT_PATH_STR + 
				" AVP, Reason: " + e.getMessage());
			}
			throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, e);
		}
	}

	private void appendOwnIdentity(AvpGrouped explicitPathAVP) throws ExplicitRoutingFailedException {
		
		AvpGrouped ownExplicitPath = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		if(ownExplicitPath == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Unable to append Own Path-Record, Sending " + 
				ResultCode.DIAMETER_ER_NOT_AVAILABLE+ ", Reason: " + 
				DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR + " not found in Dictionary");
			}
			throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE,  
					DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR + " not found in Dictionary");
		}
		IDiameterAVP subAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		if(subAvp == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Unable to append Own Proxy Host, Sending " + 
				ResultCode.DIAMETER_ER_NOT_AVAILABLE+ ", Reason: " + 
				DiameterAVPConstants.HW_PROXY_HOST_STR + " not found in Dictionary");
			}
			throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, 
					DiameterAVPConstants.HW_PROXY_HOST_STR + " not found in Dictionary");
		}
		subAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		ownExplicitPath.addSubAvp(subAvp);
		subAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		if(subAvp == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Not Appending Own Proxy-Realm, Reason: " + 
				DiameterAVPConstants.HW_PROXY_REALM_STR + " not found in Dictionary");
			}
		}else{
			subAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
			ownExplicitPath.addSubAvp(subAvp);
		}
		explicitPathAVP.addSubAvp(ownExplicitPath);
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Appended Own " + DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR);
		}
	}

	/** 
	 * The ER-Proxy MUST verify that Explicit-Path discovery is in progress by
	 * verifying that the Proxy-Host AVP of first Explicit-Path-Record AVP 
	 * in the Explicit-Path AVP does NOT match the Destination-Host AVP (if present).
	 * If this verification succeeds or the Destination-Host AVP is absent, 
	 * we mark it as Ongoing Path Discovery.
	 * @param destHostAVP 
	 */
	private boolean isOngoingPathDiscovery(IDiameterAVP destHostAVP, List<IDiameterAVP> explicitPathRecordAVPs) {
		
		if(destHostAVP == null || destHostAVP.getStringValue() == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Explicit Path Discovery is in progress. Reason: Destination Host not found");
			}
			return true;
		}
		
		AvpGrouped explicitPathRecord = (AvpGrouped) explicitPathRecordAVPs.get(0);
		IDiameterAVP proxyHostAvp = explicitPathRecord.getSubAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		if(proxyHostAvp != null){
			if(destHostAVP.getStringValue().equals(proxyHostAvp.getStringValue()) == false){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Explicit Path Discovery is in progress. " +
					"Reason: Proxy-Host of first Explicit-Path-Record does not match the Destination Host");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * the ER-Proxy MUST remove its Own Path Record from the 
	 * Explicit-Path AVP and repopulate the Destination-Host and 
	 * possibly the Destination-Realm AVP from the next Explicit-Path-Record 
	 * present in the Explicit-Path AVP.
	 *     
	 * @param diameterPacket
	 * @throws ExplicitRoutingFailedException 
	 */
	private void handleERProxy(DiameterPacket diameterPacket, 
			AvpGrouped explicitPathAVP, List<IDiameterAVP> explicitPathRecordAVPs) throws ExplicitRoutingFailedException {

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Handling ER Proxy in Explicit Routing.");
		}
		
		ArrayList<IDiameterAVP> subAvpList = explicitPathAVP.getGroupedAvp();
		for(int index = 0;index < subAvpList.size(); index++){
			if(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD.equalsIgnoreCase(subAvpList.get(index).getAVPId())){
				subAvpList.remove(index);
				break;
			}
		}
		explicitPathAVP.setGroupedAvp(subAvpList);
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Popping Own " + DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR +
			" AVP from Explicit Path Stack.");
		}
		//Getting next record of all Path Records for Populating Dest Host and Realm
		AvpGrouped nextExplicitPathRecord = (AvpGrouped) explicitPathRecordAVPs.get(1);
		if(nextExplicitPathRecord == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){ 
				LogManager.getLogger().warn(MODULE, "Not populating Destination AVPs, Sending: " + 
				ResultCode.DIAMETER_ER_NOT_AVAILABLE + 
				", Reason: Next " + DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR + 
				" AVP not found in Explicit Path.");
			}
			throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, 
					"Next " + DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR + 
					" AVP not found in Explicit Path.");
		}
		IDiameterAVP pathAvp = nextExplicitPathRecord.getSubAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		if(pathAvp == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Not populating Destination AVPs, Reason: " + 
				DiameterAVPConstants.HW_PROXY_HOST_STR + " AVP not found in Next "+
				DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR);
			}
			throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, 
					DiameterAVPConstants.HW_PROXY_HOST_STR + 
					" AVP not found in Next " + DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR);
		}
		IDiameterAVP destAvp = diameterPacket.getAVP(DiameterAVPConstants.DESTINATION_HOST);
		String value = pathAvp.getStringValue();
		if(value != null){
			if(destAvp == null){
				destAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.DESTINATION_HOST);
				diameterPacket.addAvp(destAvp);
			}
			destAvp.setStringValue(value);
		}
		
		
		destAvp = diameterPacket.getAVP(DiameterAVPConstants.DESTINATION_REALM);
		if (destAvp != null) {
			
			pathAvp = nextExplicitPathRecord.getSubAttribute(DiameterAVPConstants.HW_PROXY_REALM);
			if(pathAvp == null){
				return;
			}
			value = pathAvp.getStringValue();
			if(value != null){
				destAvp.setStringValue(value);
			}
		}
	}

	private void handleERDestination() {
		// TODO Scheduled in stage II
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Handling ER Destination in Explicit Routing.");
		}
	}

	/**
	 * 
	 * @param explicitPathRecordAVPs
	 * @return the index of Own Path-Record where first record has index=1.
	 * @throws ExplicitRoutingFailedException 
	 */
	private int getOwnPathRecordIndex(List<IDiameterAVP> explicitPathRecordAVPs) 
			throws ExplicitRoutingFailedException {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Checking for Own Explicit-Path-Record availability in Explicit-Path.");
		}
		for(int index = 0 ; index < explicitPathRecordAVPs.size() ; index++){
			if(isOwnProxyHost((AvpGrouped) explicitPathRecordAVPs.get(index))){
				return index+1;
			}
		}
		return OWN_PATH_RECORD_NOT_FOUND;
	}
	
	private boolean isOwnProxyHost(AvpGrouped explicitPathRecord) throws ExplicitRoutingFailedException {
		IDiameterAVP hwProxyHost = explicitPathRecord.getSubAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		if(hwProxyHost == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Sending " +ResultCode.DIAMETER_ER_NOT_AVAILABLE+ 
				", Reason: " + DiameterAVPConstants.HW_PROXY_HOST_STR + 
				" AVP not found in " + DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR);
			}
			throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, 
					DiameterAVPConstants.HW_PROXY_HOST_STR + " AVP not found in " + 
					DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD_STR);
		}
		String ownIdentity = Parameter.getInstance().getOwnDiameterIdentity();
		if(ownIdentity.equals(hwProxyHost.getStringValue()))
			return true;
		return false;
	}
}
