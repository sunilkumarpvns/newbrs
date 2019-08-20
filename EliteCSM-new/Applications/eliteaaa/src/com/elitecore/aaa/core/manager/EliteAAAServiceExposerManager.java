package com.elitecore.aaa.core.manager;


import static java.lang.String.format;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.ServiceUnavailableException;

import org.apache.axis.AxisFault;

import com.elitecore.aaa.core.drivers.MapGWImsiRequestorConnectionPool;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.server.axixserver.IDiameterWSRequestHandler;
import com.elitecore.aaa.core.server.axixserver.InMemoryRequestHandler;
import com.elitecore.aaa.diameter.conf.impl.ImsiBasedRoutingTableConfigurable;
import com.elitecore.aaa.diameter.conf.impl.MsisdnBasedRoutingTableConfigurable;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.SMSGWResponseListener;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.collections.Trie;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.core.system.comm.ILocalResponseListener;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.routerx.imsi.ImsiBasedRouteEntryData;
import com.elitecore.diameterapi.diameter.common.routerx.imsi.ImsiBasedRoutingTableData;
import com.elitecore.diameterapi.diameter.common.routerx.msisdn.MsisdnBasedRouteEntryData;
import com.elitecore.diameterapi.diameter.common.routerx.msisdn.MsisdnBasedRoutingTableData;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.EndToEndPool;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;


public class EliteAAAServiceExposerManager {

	
	private static final String RESULT_CODE = "RESULTCODE";
	private static final String MESSAGE = "MESSAGE"; 
	
	private static EliteAAAServiceExposerManager eliteAAAServiceExposerManager;

	private static final String MODULE = "ELITE-AAA-SERVICE-EXPOSER-MANAGER";
	private InMemoryRequestHandler dynAuthServiceInMemoryRequestHandler;
	private InMemoryRequestHandler radAuthServiceInMemoryRequestHandler;
	private InMemoryRequestHandler radAcctServiceInMemoryRequestHandler;
	private IDiameterWSRequestHandler diameterWSRequestHandler;
	private AAAServerContext serverContext;
	private boolean isImsiRequestorEnabled = false;
	private long requestTimeoutInSec = 3;
	
	private EliteAAAServiceExposerManager() {
		
	}

	static {
		eliteAAAServiceExposerManager = new EliteAAAServiceExposerManager();
	}
	
	static public EliteAAAServiceExposerManager getInstance() {
		return eliteAAAServiceExposerManager;
	}

	public int requestCOA(String strUserName, Map<String, String> attrMap) throws AxisFault{
		if(!isValidClient()){
			new AxisFault(AAAServerConstants.UNKNOWN_CLIENT_127_0_0_1);
		}

		RadiusPacket radiusRequestPacket = createRadiusPacket(strUserName , attrMap,RadiusConstants.COA_REQUEST_MESSAGE);

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Request packet to be processed  "+radiusRequestPacket);

		RadiusPacket responsePacket = null;
		try {
			responsePacket = sendBlockingLocalRequest(radiusRequestPacket);
		} catch (ServiceUnavailableException e) {
			throw new AxisFault("Could not dispatch local CoA request", e);
		} catch (Exception e) {

			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Could not dispatch local CoA request. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);

		}
		return responsePacket != null ? responsePacket.getPacketType() : 0;

		}
	public Map<String, String[]> requestCOAExt(String strUserName,Map<String, String[]> attrMap)throws AxisFault {

		if(!isValidClient()){
			new AxisFault(AAAServerConstants.UNKNOWN_CLIENT_127_0_0_1);
	}
		
		RadiusPacket radiusRequestPacket = createRadiusPacketExt(strUserName , attrMap,RadiusConstants.COA_REQUEST_MESSAGE);
	
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Request packet to be processed  "+radiusRequestPacket);
	
		RadiusPacket responsePacket = null;
		try {
			responsePacket = sendBlockingLocalRequest(radiusRequestPacket);
		} catch (ServiceUnavailableException e) {
			throw new AxisFault("Could not dispatch local CoA request", e);
		} catch (Exception e) {
	
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Could not dispatch local request. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
	
		}
	
		Map<String, String[]> responseMapNew = generateRadiusResponseMap(responsePacket);
		
		return responseMapNew;
		}

	private Map<String, String[]> generateRadiusResponseMap(RadiusPacket responsePacket) {
		
		Map<String, String[]> responseMapNew = new HashMap<String,String[]>();
		
		if (responsePacket == null) {
			responseMapNew.put("21067:127", new String[]{"0"});
			return responseMapNew;
		}
		
		Map<String,ArrayList<String>> responseMap = new HashMap<String,ArrayList<String>>();
		for(IRadiusAttribute radAttr : responsePacket.getRadiusAttributes() ){
			if(!radAttr.isVendorSpecific()){
				if(!responseMap.containsKey(radAttr.getParentId())){
					responseMap.put(radAttr.getParentId(), new ArrayList<String>());
					responseMapNew.put(radAttr.getParentId(), null);
				}				
				responseMap.get(radAttr.getParentId()).add(radAttr.getStringValue());
			}else{
				for(IRadiusAttribute subAttr : (List<IRadiusAttribute>)((VendorSpecificAttribute)radAttr).getAttributes() ){
					if(!responseMap.containsKey(subAttr.getParentId())){
						responseMap.put(subAttr.getParentId(), new ArrayList<String>());
					}
					
					responseMap.get(subAttr.getParentId()).add(subAttr.getStringValue());					
				}
			}
		}
		for(Map.Entry<String,ArrayList<String>> attribute :responseMap.entrySet()){
			String key = attribute.getKey();
			String [] attribValues = new String[attribute.getValue().size()];
			responseMapNew.put(key, attribute.getValue().toArray(attribValues));
		}
		
		responseMapNew.put("21067:127", new String[]{String.valueOf(responsePacket.getPacketType())});
		
		return responseMapNew;
	}

	public int requestDiameterReAuth(Map<String, String> attrMap) {

		try {

			DiameterRequest diameterRequest = new DiameterRequest();
			TranslatorParams params = new TranslatorParamsImpl(attrMap,diameterRequest);

			TranslationAgent.getInstance().translate(diameterWSRequestHandler.getMappingConfigId(TranslatorConstants.DIAMETER_RE_AUTH), params, TranslatorConstants.REQUEST_TRANSLATION);
			diameterRequest.setParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING, params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Request packet to be processed  "+diameterRequest);

			DiameterAnswer diameterAnswer = diameterWSRequestHandler.submitToStack(diameterRequest);

			IDiameterAVP resultCodeAvp = diameterAnswer.getAVP(DiameterAVPConstants.RESULT_CODE);
			if(resultCodeAvp!=null)
				return Integer.parseInt(resultCodeAvp.getStringValue());
			else
				return -1;
		} catch (TranslationFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Problem During Handling Diameter ReAuth Request , Reason :"+e.getMessage());
			return -1;
		}

	}


	public int requestDiameterAbortSession(Map<String, String> attrMap) {

		try {

			DiameterRequest diameterRequest = new DiameterRequest();
			TranslatorParams params = new TranslatorParamsImpl(attrMap,diameterRequest);

			TranslationAgent.getInstance().translate(diameterWSRequestHandler.getMappingConfigId(TranslatorConstants.DIAMETER_ABORT_SESSION), params, TranslatorConstants.REQUEST_TRANSLATION);
			diameterRequest.setParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING, params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Request packet to be processed  "+diameterRequest);

			DiameterAnswer diameterAnswer = diameterWSRequestHandler.submitToStack(diameterRequest);

			IDiameterAVP resultCodeAvp = diameterAnswer.getAVP(DiameterAVPConstants.RESULT_CODE);
			if(resultCodeAvp!=null)
				return Integer.parseInt(resultCodeAvp.getStringValue());
			else
				return -1;
		} catch (TranslationFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Problem During Handling Diameter Abort Session Request , Reason :"+e.getMessage());
			return -1;
		}

	}
	public int requestDisconnect(String strUserName, Map<String, String> attrMap) throws AxisFault{
		if(!isValidClient()){
			new AxisFault(AAAServerConstants.UNKNOWN_CLIENT_127_0_0_1);
		}
		RadiusPacket radiusRequestPacket = createRadiusPacket(strUserName , attrMap,RadiusConstants.DISCONNECTION_REQUEST_MESSAGE);
		return requestDisconnect(radiusRequestPacket);
	}
	
	private Map<String, String[]> requestDisconnectExt(RadiusPacket radiusRequestPacket)throws AxisFault {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Request packet to be processed  "+radiusRequestPacket);

		RadiusPacket responsePacket = null;
		try {
			responsePacket = sendBlockingLocalRequest(radiusRequestPacket);
		}catch (ServiceUnavailableException e) {
			throw new AxisFault("Could not dispatch local DM request", e);
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Could not dispatch local request. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		Map<String, String[]> responseMapNew = new HashMap<String,String[]>();
		
		if (responsePacket == null) {
			responseMapNew.put("21067:127", new String[]{"0"});
			return responseMapNew;
		}

		Map<String,ArrayList<String>> responseMap = new HashMap<String,ArrayList<String>>();
		for(IRadiusAttribute radAttr : responsePacket.getRadiusAttributes() ){
			if(!radAttr.isVendorSpecific()){
				if(!responseMap.containsKey(radAttr.getParentId())){
					responseMap.put(radAttr.getParentId(), new ArrayList<String>());
					responseMapNew.put(radAttr.getParentId(), null);
				}				
				responseMap.get(radAttr.getParentId()).add(radAttr.getStringValue());
			}else{
				for(IRadiusAttribute subAttr : (List<IRadiusAttribute>)((VendorSpecificAttribute)radAttr).getAttributes() ){
					if(!responseMap.containsKey(subAttr.getParentId())){
						responseMap.put(subAttr.getParentId(), new ArrayList<String>());
					}

					responseMap.get(subAttr.getParentId()).add(subAttr.getStringValue());					
				}
			}
		}
		for(Map.Entry<String, ArrayList<String>> attribute :responseMap.entrySet()){
			String key = attribute.getKey();
			String [] attribValues = new String[attribute.getValue().size()];
			responseMapNew.put(key, attribute.getValue().toArray(attribValues));
		}

		responseMapNew.put("21067:127", new String[]{String.valueOf(responsePacket.getPacketType())});

		return responseMapNew;
	}

	public Map<String, String[]> requestDisconnectExt(String strUserName, Map<String, String[]> attrMap) throws AxisFault{
		if(!isValidClient()){
			new AxisFault(AAAServerConstants.UNKNOWN_CLIENT_127_0_0_1);
		}
		RadiusPacket radiusRequestPacket = createRadiusPacketExt(strUserName , attrMap,RadiusConstants.DISCONNECTION_REQUEST_MESSAGE);
		return requestDisconnectExt(radiusRequestPacket);
	}
	public int requestDisconnect(RadiusPacket radiusRequestPacket) throws AxisFault {
		
		if(!isValidClient()){
			new AxisFault(AAAServerConstants.UNKNOWN_CLIENT_127_0_0_1);
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Request packet to be processed  "+radiusRequestPacket);

		RadiusPacket responsePacket = null;
		try {
			responsePacket = sendBlockingLocalRequest(radiusRequestPacket);
		}catch (ServiceUnavailableException e) {
			throw new AxisFault("Could not dispatch local DM request", e);
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Could not dispatch local request. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		return responsePacket != null ? responsePacket.getPacketType() : 0;
	}

	private RadiusPacket createRadiusPacket(String strUserName , Map<String, String> attrMap , int requestType) {

		RadiusPacket radiusRequestPacket = new RadiusPacket();

		radiusRequestPacket.setPacketType(requestType);
		radiusRequestPacket.setIdentifier(RadiusUtility.generateIdentifier());

		if(strUserName != null) {
			IRadiusAttribute userNameAttr = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.USER_NAME);
			if(userNameAttr!=null){
				userNameAttr.setStringValue(strUserName);
				radiusRequestPacket.addAttribute(userNameAttr);
			}	
		}

		String attributeId;
		String attributeValue;
		IRadiusAttribute radiusAttribute ;

		for(Map.Entry<String, String> attribute :attrMap.entrySet()){
			attributeId  = attribute.getKey();
			attributeValue = attribute.getValue();
			if(attributeId!=null && attributeId.trim().length()>0 && attributeValue!=null){
				radiusAttribute =  Dictionary.getInstance().getKnownAttribute(attributeId);
				if(radiusAttribute!=null){
					radiusAttribute.setStringValue(attributeValue);
					radiusRequestPacket.addAttribute(radiusAttribute);
				}
			}
		}
		
		//	re-encrypting value of any encryptable attribute		
		try {
			radiusRequestPacket.refreshPacketHeader();
			radiusRequestPacket.refreshInfoPacketHeader();
			String sharedSecret = serverContext.getServerConfiguration().getRadClientConfiguration().getClientSharedSecret(InetAddress.getLocalHost().getHostAddress(), requestType);
			byte[] authenticator = getRequestAuthenticator(requestType, radiusRequestPacket, sharedSecret);
			radiusRequestPacket.setAuthenticator(authenticator);
			((RadiusPacket)radiusRequestPacket).reencryptAttributes(null, null, RadiusUtility.generateRFC2865RequestAuthenticator(), sharedSecret);
		} catch (UnknownHostException e) {
		}
		
		return radiusRequestPacket;
	}

	private RadiusPacket createRadiusPacketExt(String strUserName , Map<String, String[]> attrMap , int requestType) {

		if(strUserName!=null){
			if(attrMap==null){
				attrMap = new HashMap<String, String[]>();
			}
			attrMap.put(RadiusAttributeConstants.USER_NAME_STR, new String[]{strUserName});
		}
		
		return createRadiusRequestPacket(RadiusConstants.COA_REQUEST_MESSAGE, RadiusUtility.generateIdentifier(), attrMap);
	}
	
	private RadiusPacket createRadiusRequestPacket(int requestType,int identifier,Map<String, String[]> attrMap) {
		
		RadiusPacket radiusRequestPacket = new RadiusPacket();

		radiusRequestPacket.setPacketType(requestType);
		radiusRequestPacket.setIdentifier(identifier);

		if(attrMap!=null){

		String attributeId;
		Object[] attributeValue;
		IRadiusAttribute radiusAttribute ;
		
			for(Map.Entry<String, String[]> attribute :attrMap.entrySet()){
			attributeId  = (String)attribute.getKey();
			
			attributeValue = (Object[])attribute.getValue();
			
			if(attributeId!=null && attributeId.trim().length()>0 && attributeValue!=null){
				for(Object strValue  : attributeValue){
						try {
							radiusAttribute =  Dictionary.getInstance().getAttribute(attributeId);
					if(radiusAttribute!=null){
						radiusAttribute.setStringValue(String.valueOf(strValue));
						radiusRequestPacket.addAttribute(radiusAttribute);
					}
						} catch (InvalidAttributeIdException e) {
				}
			}
		}
			}

			
		}
		
		radiusRequestPacket.refreshPacketHeader();
		radiusRequestPacket.refreshInfoPacketHeader();
		try {
			
		//	re-encrypting value of any encryptable attribute		
			 
			String secret = serverContext.getServerConfiguration().getRadClientConfiguration().getClientSharedSecret(InetAddress.getLocalHost().getHostAddress(), requestType);
			byte[] authenticator = getRequestAuthenticator(requestType, radiusRequestPacket, secret);
			radiusRequestPacket.setAuthenticator(authenticator);
			((RadiusPacket)radiusRequestPacket).reencryptAttributes(null, null,authenticator ,secret);
			
		} catch (UnknownHostException e) {
		}

		return radiusRequestPacket;
	}

	/**
	 * Forwards the radius request to local service instance based on request packet type
	 * in a non-blocking manner.
	 * 
	 * @param requestPacket a non-null packet
	 * @return future reference which can be used to fetch the response. Response may be null.
	 * @throws UnknownHostException if localhost is unknown
	 * @throws ServiceUnavailableException if the service that can honor the requested
	 * packet type is not started
	 * @throws IllegalArgumentException if no service can honor the requested packet type
	 */
	public SettableFuture<RadiusPacket> sendLocalRequest(final RadiusPacket requestPacket) throws UnknownHostException, ServiceUnavailableException {
		final SettableFuture<RadiusPacket> future = SettableFuture.create();
		
		InMemoryRequestHandler serviceResponseListner = selectInMemoryRequestHandler(requestPacket.getPacketType());

		serviceResponseListner.handleRequest(requestPacket.getBytes(), new ILocalResponseListener() {
			
			public void responseReceived(byte[] response) {
				RadiusPacket responsePacket = new RadiusPacket();
				responsePacket.setBytes(response);
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Response received: " + response);
				}
				future.set(responsePacket);
			}

			public void requestDropped(byte[] request) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Request dropped for radius packet: " + requestPacket);
				}
				future.set(null);
			}

			public void requestTimedout(byte[] request) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Request timed out for radius packet: " + requestPacket);
				}
				future.set(null);
			}
		});
		return future;
	}
	
	/**
	 * Forwards the radius request to local service instance based on request packet type
	 * in a blocking manner with a timeout of 3 seconds.
	 * 
	 * @param requestPacket a non-null packet
	 * @return a nullable response packet. Response packet may be null when request is dropped
	 * or if the local service is unable to honor the request  
	 * @throws UnknownHostException if localhost is unknown
	 * @throws ServiceUnavailableException if the service that can honor the requested
	 * packet type is not started
	 * @throws TimeoutException if request times out
	 * @throws IllegalArgumentException if no service can honor the requested packet type
	 * @throws ExecutionException if there is any error while submitting local request
	 */
	public RadiusPacket sendBlockingLocalRequest(
			RadiusPacket radiusRequestPacket) throws InterruptedException,
			TimeoutException, ExecutionException, UnknownHostException,
			ServiceUnavailableException {
		return sendLocalRequest(radiusRequestPacket).get(requestTimeoutInSec  , TimeUnit.SECONDS);
	}
	
	private InMemoryRequestHandler selectInMemoryRequestHandler(int packetType) throws ServiceUnavailableException {
		if (packetType == RadiusConstants.COA_REQUEST_MESSAGE 
				|| packetType == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE) {
			if (dynAuthServiceInMemoryRequestHandler == null) {
				throw new ServiceUnavailableException(AAAServerConstants.DYNAUTH_SERVICE_IS_NOT_RUNNING);
			}
			return dynAuthServiceInMemoryRequestHandler;
		} else if (packetType == RadiusConstants.ACCESS_REQUEST_MESSAGE) {
			if (radAuthServiceInMemoryRequestHandler == null) {
				throw new ServiceUnavailableException(AAAServerConstants.AUTH_SERVICE_IS_NOT_RUNNING);
			}
			return radAuthServiceInMemoryRequestHandler;
		} else if (packetType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE 
				|| packetType == RadiusConstants.NAS_REBOOT_REQUEST) {
			if (radAcctServiceInMemoryRequestHandler == null) {
				throw new ServiceUnavailableException(AAAServerConstants.ACCT_SERVICE_IS_NOT_RUNNING);
			}
			return radAcctServiceInMemoryRequestHandler;
		} else {
			throw new IllegalArgumentException(AAAServerConstants.INVALID_PACKET_TYPE_RECEIVED);
		}
	}

	public int requestHotline(String strUserName, Map<String, String> attrMap) {
		return 0;
	}
	
	public int wimaxDynAuthRequest(int packetType,String strUserName, Map<String, String> attrMap) {
		return 0;
	}

	public void registerDynAuthServiceInMemoryRequestHandler(InMemoryRequestHandler dynAuthServiceInMemoryRequestHandler) {
		this.dynAuthServiceInMemoryRequestHandler = dynAuthServiceInMemoryRequestHandler;
	}
	
	public void registerRadAuthServiceInMemoryRequestHandler(InMemoryRequestHandler radAuthServiceInMemoryRequestHandler) {
		this.radAuthServiceInMemoryRequestHandler = radAuthServiceInMemoryRequestHandler;
	}
	
	public void registerRadAcctServiceInMemoryRequestHandler(InMemoryRequestHandler radAcctServiceInMemoryRequestHandler) {
		this.radAcctServiceInMemoryRequestHandler = radAcctServiceInMemoryRequestHandler;
	}
	
	public void registerDiameterServiceInMemoryRequestHandler(IDiameterWSRequestHandler diameterWSListener) {
		this.diameterWSRequestHandler = diameterWSListener;
	}
	
	public void setServerContext(AAAServerContext context){
		this.serverContext = context;
	}
	
	public Map<String, String> requestGenericDiameterRequest(int commandCode,
			int applicationId, int hopByHopId, int endToEndId, byte flagByte,
			Map<String, String> attrMap) {

		Map<String, String> respMap = new HashMap<String, String>();
		try {
			
			if (hopByHopId <= 0) {
				hopByHopId = HopByHopPool.get();
			}
			if (endToEndId <= 0) {
				endToEndId = EndToEndPool.get();
			}

			DiameterRequest diameterRequest = new DiameterRequest();
			TranslatorParams params = new TranslatorParamsImpl(attrMap,diameterRequest);
			diameterRequest.setCommandCode(commandCode);
			diameterRequest.setApplicationID(applicationId);
			diameterRequest.setHop_by_hopIdentifier(hopByHopId);
			diameterRequest.setEnd_to_endIdentifier(endToEndId);

			setFlag(diameterRequest,flagByte);

			TranslationAgent.getInstance().translate(diameterWSRequestHandler.getMappingConfigId(TranslatorConstants.DIAMETER_GENERIC_REQUEST), params, TranslatorConstants.REQUEST_TRANSLATION);
			diameterRequest.setParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING, params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Request packet to be processed  "+diameterRequest);

			DiameterAnswer diameterAnswer = diameterWSRequestHandler.submitToStack(diameterRequest);

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Response Received "+diameterAnswer);

			params = new TranslatorParamsImpl(diameterAnswer, respMap, attrMap, diameterRequest);
			params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, diameterRequest.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
			TranslationAgent.getInstance().translate(diameterWSRequestHandler.getMappingConfigId(TranslatorConstants.DIAMETER_GENERIC_REQUEST), params, TranslatorConstants.RESPONSE_TRANSLATION);

		} catch (TranslationFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Problem During Handling Generic Diameter Request , Reason :"+e.getMessage());
			new AxisFault(e.getMessage());
		}
		return respMap;
	}
	
	public String requestIMSIForMSISDN(String msisdn) throws org.apache.axis.AxisFault{
		SMSGWResponseListener responseListener = new SMSGWResponseListener();
		try{
			if ((LogManager.getLogger().isLogLevel(LogLevel.INFO)))
				LogManager.getLogger().info(MODULE, "Requesting IMSI for MSISDN: " + msisdn );
			MapGWImsiRequestorConnectionPool.getInstance().requestIMSIForMSISDN(msisdn, responseListener);
		}catch(Exception e){
			if ((LogManager.getLogger().isLogLevel(LogLevel.WARN)))
				LogManager.getLogger().warn(MODULE, "Could not locate IMSI for MSISDN: " + msisdn + "Reason: " + e.getMessage() + "Raising AxisFault with Error code -1");
			throw new AxisFault("-1",e.getMessage(),"",null);
		}
		String imsi = responseListener.getImsi();
		if (imsi != null && imsi.trim().length() > 0){
			return imsi;
		} else {
			if ((LogManager.getLogger().isLogLevel(LogLevel.WARN)))
				LogManager.getLogger().warn(MODULE, "IMSI not found for MSISDN: " + msisdn + "Raising AxisFault with Error code 1");
			throw new AxisFault("1","IMSI not found","",null);
		}
		
	}

	private void setFlag(DiameterPacket diameterRequest, byte flagByte) {
		if((flagByte & 0x40) != 0)
			diameterRequest.setProxiableBit();
		if((flagByte & 0x20) != 0)
			diameterRequest.setErrorBit();
		if((flagByte & 0x10) != 0)
			diameterRequest.setReTransmittedBit();
	}

	public void init() {
		initailizeMapGWConnectionPool();
		getWs2LocalRequestTimeout();
	}

	private void initailizeMapGWConnectionPool() {
		String localHost = System.getProperty("local-host-for-imsi-requestor");
		String remoteHost = System.getProperty("remote-host-for-imsi-requestor");
		String requestTimeout = System.getProperty("request-timeout-for-imsi-requestor");
		if (localHost!= null && remoteHost!=null && localHost.trim().length() > 0 && remoteHost.trim().length() > 0){
			isImsiRequestorEnabled = true;
			MapGWImsiRequestorConnectionPool.getInstance().init(localHost, remoteHost, requestTimeout);
		}
	}
	
	private void getWs2LocalRequestTimeout() {
		try {
			String ws2LocalRequestTimeoutInSec = System.getProperty("ws-to-local-request-timeout");

			if (Strings.isNullOrBlank(ws2LocalRequestTimeoutInSec) == false) {
				requestTimeoutInSec = Integer.parseInt(ws2LocalRequestTimeoutInSec);
			} else {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().debug(MODULE, "System property ws-to-local-request-timeout is not configured," +
							" so taking 3 second as default request timeout.");
				}
			}
		}catch (NumberFormatException nfe) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Invalid value is configured in misc for local request timeout, so taking " +
						"3 second as default request timeout.");
			}
		}
	}

	public void stop(){
		if (isImsiRequestorEnabled){
			MapGWImsiRequestorConnectionPool.getInstance().terminate();
		}
	}

	public Map<String, String[]> requestGenericRadiusWS(int packetType,int identifier ,Map<String, String[]> attrMap) throws AxisFault {
	
		RadiusPacket radiusRequestPacket = createRadiusRequestPacket(packetType, identifier, attrMap);
	
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Request packet to be processed  "+radiusRequestPacket);
	
		RadiusPacket responsePacket = null;
		try {
			responsePacket = sendBlockingLocalRequest(radiusRequestPacket);
		} catch (ServiceUnavailableException e) {
			throw new AxisFault("Could not dispatch local request", e);
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Could not dispatch local request. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
}
		
		Map<String, String[]> responseMapNew = generateRadiusResponseMap(responsePacket);
		
		return responseMapNew;
	}

	private byte[] getRequestAuthenticator(int requestType,RadiusPacket requestPacket,String strSharedSecret) {
		if(requestType==RadiusConstants.ACCOUNTING_REQUEST_MESSAGE || requestType == RadiusConstants.COA_REQUEST_MESSAGE || requestType == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE){
			return RadiusUtility.generateRFC2866RequestAuthenticator(requestPacket, strSharedSecret);
		}
		return RadiusUtility.generateRFC2865RequestAuthenticator();
	}
	
	private boolean isValidClient() {
		RadClientConfiguration radClientConfiguration = serverContext.getServerConfiguration().getRadClientConfiguration();
		if(radClientConfiguration!=null){
			return true;	
		}else {
			return false;
		}
		
	}

	public Map<String, String> getPeerByIMSI(String imsi, String imsiTableName) {

		Map<String, String> response = new HashMap<String, String>();

		ImsiBasedRoutingTableData imsiBasedRoutingTableData = serverContext.getServerConfiguration()
				.getImsiBasedRoutingTableConfiguration().getImsiBasedRoutingTableDataByName(imsiTableName);
		
		if (imsiBasedRoutingTableData == null) {
			response.put(RESULT_CODE, WSResponseCodes.IMSI_TABLE_NOT_FOUND.code);
			response.put(MESSAGE, format(WSResponseCodes.IMSI_TABLE_NOT_FOUND.message, imsiTableName));
			return response;
		}
		Trie<ImsiBasedRouteEntryData> imsiTrie = imsiBasedRoutingTableData.trie();
		imsi = Utility.getIMSIFromIdentity(imsi);
		if (imsi.length() < 15 ) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Invalid IMSI: " + imsi + 
						" received in Web Service request," +
						" Reason: IMSI shoud be atleast 15 digit decimal value" );
			}
			response.put(RESULT_CODE, WSResponseCodes.INVALID_IMSI.code);
			response.put(MESSAGE, format(WSResponseCodes.INVALID_IMSI.message, imsi));
			return response;
		}
		ImsiBasedRouteEntryData entryData = imsiTrie.longestPrefixKeyMatch(imsi);
		if (entryData == null) {
			response.put(RESULT_CODE, WSResponseCodes.IMSI_NOT_FOUND.code);
			response.put(MESSAGE, format(WSResponseCodes.IMSI_NOT_FOUND.message, imsi));
			return response;
		}
		response.put(RESULT_CODE, WSResponseCodes.SUCCESS.code);
		response.put(MESSAGE, WSResponseCodes.SUCCESS.message);
		response.put(ImsiBasedRoutingTableConfigurable.PRIMARY_PEERNAME, entryData.getPrimaryPeer());
		response.put(ImsiBasedRoutingTableConfigurable.SECONDARY_PEERNAME, entryData.getSecondaryPeer());
		response.put(ImsiBasedRoutingTableConfigurable.TAG, entryData.getTag());
		return response;
	}


	private enum WSResponseCodes {
		SUCCESS("200", "Success"),
		IMSI_TABLE_NOT_FOUND("461", "IMSI routing table: '%s' not found"),
		IMSI_NOT_FOUND("462", "IMSI: '%s' not found"), 
		INVALID_IMSI("463", "Invalid IMSI: '%s'"), 

		MSISDN_TABLE_NOT_FOUND("464", "MSISDN routing table: '%s' not found"),
		MSISDN_NOT_FOUND("465", "MSISDN: '%s' not found"), 
		INVALID_MSISDN("466", "Invalid MSISDN: '%s'")
		;
		String code;
		String message;

		private WSResponseCodes(String code, String message) {
			this.code = code;
			this.message = message;
		}

	}

	public Map<String, String> getPeerByMSISDN(String msisdn,
			String msisdnTableName) {
		Map<String, String> response = new HashMap<String, String>();
		
		MsisdnBasedRoutingTableData msisdnBasedRoutingTableData = serverContext.getServerConfiguration()
				.getMsisdnBasedRoutingTableConfiguration().getMsisdnBasedRoutingTableDataByName(msisdnTableName);
		
		if (msisdnBasedRoutingTableData == null) {
			response.put(RESULT_CODE, WSResponseCodes.MSISDN_TABLE_NOT_FOUND.code);
			response.put(MESSAGE, format(WSResponseCodes.MSISDN_TABLE_NOT_FOUND.message, msisdnTableName));
			return response;
		}
		String formatedMsisdn = Utility.formatMsisdn(msisdn, 
				msisdnBasedRoutingTableData.getMsisdnLength(), 
				msisdnBasedRoutingTableData.getMcc());
		
		if (Strings.isNullOrBlank(formatedMsisdn)) {
			if (LogManager.getLogger().isDebugLogLevel() ) {
				LogManager.getLogger().debug(MODULE, "Invalid MSISDN: " + msisdn + 
						" received in Web Service request");
			}
			response.put(RESULT_CODE, WSResponseCodes.INVALID_MSISDN.code);
			response.put(MESSAGE, format(WSResponseCodes.INVALID_MSISDN.message, msisdn));
		}
		
		Trie<MsisdnBasedRouteEntryData> msisdnTrie = msisdnBasedRoutingTableData.trie();
		MsisdnBasedRouteEntryData entryData = msisdnTrie.longestPrefixKeyMatch(formatedMsisdn);
		if (entryData == null) {
			response.put(RESULT_CODE, WSResponseCodes.MSISDN_NOT_FOUND.code);
			response.put(MESSAGE, format(WSResponseCodes.MSISDN_NOT_FOUND.message, msisdn));
			return response;
		}
		response.put(RESULT_CODE, WSResponseCodes.SUCCESS.code);
		response.put(MESSAGE, WSResponseCodes.SUCCESS.message);
		response.put(MsisdnBasedRoutingTableConfigurable.PRIMARY_PEERNAME, entryData.getPrimaryPeer());
		response.put(MsisdnBasedRoutingTableConfigurable.SECONDARY_PEERNAME, entryData.getSecondaryPeer());
		response.put(MsisdnBasedRoutingTableConfigurable.TAG, entryData.getTag());
		return response;
	}

	public @Nonnull SettableFuture<DiameterAnswer> sendSessionDisconnect (@Nonnull ProtocolType sessionTypeConstants, 
			@Nullable final String translationName, final DiameterRequest diameterRequest)  throws CommunicationException {
		
		final SettableFuture<DiameterAnswer> diaFuture;

		if (sessionTypeConstants == ProtocolType.RADIUS) {
			LogManager.getLogger().debug(MODULE, "Session was generated by Radius protocol, so translating ASR to DM");

			if (translationName == null) {
				throw new CommunicationException("Unable to generate disconnect message, Reason: no translation mapping found");
			}
			
			final RadiusPacket requestRadiusPacket = new RadiusPacket();
			requestRadiusPacket.setIdentifier(RadiusUtility.generateIdentifier());
			TranslatorParams params = new TranslatorParamsImpl(diameterRequest, requestRadiusPacket);
			
			try {
				TranslationAgent.getInstance().translate(translationName, params, true);
			} catch (TranslationFailedException e) {
				throw new CommunicationException("Unable to generate disconnect message, Reason: " + e.getMessage(), e);
			}
			
			requestRadiusPacket.refreshPacketHeader();
			requestRadiusPacket.refreshInfoPacketHeader();

			RadClientConfiguration radClientConfiguration = serverContext.getServerConfiguration().getRadClientConfiguration();
			
			RadClientData clientData;
			try {
				clientData = radClientConfiguration.getClientData(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				throw new CommunicationException("Unable to generate disconnect message, Reason: " + e.getMessage(), e);
			}
			
			String strSharedSecret = clientData.getSharedSecret(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE);

			if (strSharedSecret == null) {
				throw new CommunicationException("Unable to generate disconnect message, Reason: shared secret not found for localhost");
			}

			byte[] authenticator = RadiusUtility.generateRFC2866RequestAuthenticator(requestRadiusPacket, strSharedSecret);
			if (authenticator == null) {
				throw new CommunicationException("Unable to generate disconnect message, Reason: radius packet authenticator generation failure");
			}
			requestRadiusPacket.setAuthenticator(authenticator);

			try {
				diaFuture = SettableFuture.create();
				final SettableFuture<RadiusPacket> radFuture = sendLocalRequest(requestRadiusPacket);
				radFuture.addListener(new Runnable() {
					
					@Override
					public void run() {
						DiameterAnswer answer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
						try {
							RadiusPacket radiusPacket = radFuture.get();
							if (radiusPacket != null) {
								answer = new DiameterAnswer(diameterRequest);
								TranslatorParams params = new TranslatorParamsImpl(radiusPacket, answer, diameterRequest, requestRadiusPacket);
								TranslationAgent.getInstance().translate(translationName, params, false);
							}
						} catch (InterruptedException e) {
							LogManager.getLogger().trace(MODULE, e);
						} catch (ExecutionException e) {
							LogManager.getLogger().trace(MODULE, e);
						} catch (TranslationFailedException e) {
							LogManager.getLogger().trace(MODULE, e);
						}
						diaFuture.set(answer);
					}
				}, serverContext.getTaskScheduler());
			} catch (UnknownHostException e) {
				throw new CommunicationException("Unable to generate disconnect message, Reason: " + e.getMessage(), e);
			} catch (ServiceUnavailableException e) {
				throw new CommunicationException("Unable to generate disconnect message, Reason: " + e.getMessage(), e);
			}
		} else {
			diaFuture = diameterWSRequestHandler.submitNonBlockingToStack(diameterRequest);
		}
		return diaFuture;
	}
	
	/**
	 * This exception is thrown if there is any error while communicating with other service, component or entity.
	 */
	public class CommunicationException extends Exception{

		private static final long serialVersionUID = 4151201157176954485L;

		public CommunicationException() {
			super();
		}
		
		public CommunicationException(String message) {
			super(message);
		}
		
		public CommunicationException(Throwable cause) {
			super(cause);
		}
		
		public CommunicationException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
