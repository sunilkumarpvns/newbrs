package com.elitecore.aaa.rm.drivers;


import static com.elitecore.aaa.rm.policies.RMChargingPolicy.RM_ASYNC_REQUEST_EXECUTOR;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.rm.drivers.conf.RMDiameterChargingDriverConf;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.driverx.BaseDriver;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.VirtualInputStream;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class RMDiameterChargingDriver extends BaseDriver implements RMChargingDriver {
	
	private static final String MODULE = "RM-DIA-CHR-DRVR";
	
	private RMDiameterChargingDriverConf driverConf;
	private Map<String,ServiceUnitsSession> serviceUnitsMap;
	
	private VirtualInputStream virtualInputStream;
	private Map<Integer,ServiceRequest>radiusRequestMap;
	private Map<Integer,ServiceResponse>radiusResponseMap;
	private RMDiameterChargingDriverStatisctic driverStatistics;
	
	public RMDiameterChargingDriver(AAAServerContext serverContext, RMDiameterChargingDriverConf driverConf) {
		
		super(serverContext);
		this.driverConf = driverConf;
		this.serviceUnitsMap = new ConcurrentHashMap<String, ServiceUnitsSession>();
		this.radiusRequestMap = new ConcurrentHashMap<Integer, ServiceRequest>();
		this.radiusResponseMap = new ConcurrentHashMap<Integer, ServiceResponse>();
	}

	@Override
	protected void initInternal() throws DriverInitializationFailedException {		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Initializing Driver: " + driverConf.getDriverName()); 
		}
		
		if(TranslationAgent.getInstance().isExists(driverConf.getTranslationMappingName())){
			throw new DriverInitializationFailedException("Failed to initialize Driver: " + getName() + 
						", Reason: Translation Policy: " + driverConf.getTranslationMappingName() + " not found");
		}

		try {
			initVirtualPeer();
		} catch (ElementRegistrationFailedException e) {
			
			throw new DriverInitializationFailedException("Virtual Peer initialization failed for Driver: "+ 
						driverConf.getDriverName()+ ", Reason: " + e.getMessage());
		}

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Driver: "+ driverConf.getDriverName()+ " initialized successfully."); 
		}		
	}
	
	private void setMendatoryAvpsFromClassAttribute(RadServiceRequest request,DiameterPacket diameterPacket){
		if(request.getPacketType() != RadiusConstants.ACCESS_REQUEST_MESSAGE)
			return;
		
		Collection<IRadiusAttribute> classAttributes = request.getRadiusAttributes(true,RadiusAttributeConstants.CLASS);
		if(classAttributes == null)
			return;
		
		for(IRadiusAttribute classAttribute : classAttributes){
			boolean bSetSessionId =  false;
			boolean bSetCCReqNumber = false;
			String classAttributeValue = classAttribute.getStringValue();
			String avps[] = classAttributeValue.split(",");
			for(String avp : avps ){
				String avpVlaues[] = avp.split("=");
				if(avpVlaues.length == 2){
					if(DiameterAVPConstants.SESSION_ID_STR.equalsIgnoreCase(avpVlaues[0])){
						bSetSessionId = true;
						IDiameterAVP sessionIdAvp = diameterPacket.getAVP(DiameterAVPConstants.SESSION_ID);
						if(sessionIdAvp != null){
							sessionIdAvp.setStringValue(avpVlaues[1]);
						}else{
							sessionIdAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_ID);
							sessionIdAvp.setStringValue(avpVlaues[1]);						
							diameterPacket.addAvp(sessionIdAvp);
						}
						LogManager.getLogger().debug(MODULE, "Session-Id Avp set with value:" + sessionIdAvp.getStringValue());
					}else if(DiameterAVPConstants.CC_REQUEST_NUMBER_STR.equalsIgnoreCase(avpVlaues[0])){
						bSetCCReqNumber = true;
						IDiameterAVP ccReqNumAvp = diameterPacket.getAVP(DiameterAVPConstants.CC_REQUEST_NUMBER);
						if(ccReqNumAvp != null){
							ccReqNumAvp.setStringValue(avpVlaues[1]);
						}else{
							ccReqNumAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_REQUEST_NUMBER);
							ccReqNumAvp.setStringValue(avpVlaues[1]);
							diameterPacket.addAvp(ccReqNumAvp);
						}							
						LogManager.getLogger().debug(MODULE, "CC-Request-Number Avp set with value:" + ccReqNumAvp.getStringValue());
					}
				}			
			}	
			if(bSetCCReqNumber && bSetSessionId)
				return;
		}
	}
	private String getSessionId(DiameterPacket packet){
		IDiameterAVP sessionAvp =  packet.getAVP(DiameterAVPConstants.SESSION_ID);
		if(sessionAvp != null)
			return sessionAvp.getStringValue();
		return null;
	}
	private void removeSession(DiameterAnswer diameterAnswer){
		IDiameterAVP ccReqAvp = diameterAnswer.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		if(ccReqAvp != null ){
			IDiameterAVP resultCodeAvp = diameterAnswer.getAVP(DiameterAVPConstants.RESULT_CODE);
			if(ccReqAvp.getInteger() == DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST && resultCodeAvp != null &&resultCodeAvp.getInteger() == ResultCode.DIAMETER_SUCCESS.code){ // TERMINATION
				String sessionId = getSessionId(diameterAnswer);
				if(sessionId!= null && serviceUnitsMap.remove(sessionId)!= null){
					LogManager.getLogger().debug(MODULE, "UnitsSession removed for session id: " + sessionId);
				}
			}else{//INITIAL, UPDATE
				
			}
		}
	}
	private String getSessionIdFrom(RadServiceRequest request){
		Collection<IRadiusAttribute> classAttributs = request.getRadiusAttributes(RadiusAttributeConstants.CLASS,true);
		if(classAttributs == null)
			return null;
		for(IRadiusAttribute classAttribute: classAttributs){
			if(classAttribute != null){
				String classAttributeValue = classAttribute.getStringValue();
				String avps[] = classAttributeValue.split(",");
				for(String avp : avps ){
					String avpVlaues[] = avp.split("=");
					if(avpVlaues.length == 2){
						if(DiameterAVPConstants.SESSION_ID_STR.equalsIgnoreCase(avpVlaues[0]))						
							return avpVlaues[1];
					}
				}
			}
		}
		return null;				
	}
	private void setSessionValues(RadServiceRequest radServiceRequest){
		String sessionId = getSessionIdFrom(radServiceRequest);
		if(sessionId == null){
			return;
		}
		ServiceUnitsSession session = this.serviceUnitsMap.get(sessionId);
		
			long ccOutputOctats = 0;
			long ccInputOctats = 0;
		long ccTime = 0;
			if(session == null){
				session = new ServiceUnitsSession();
				serviceUnitsMap.put(sessionId, session);
			}
		IRadiusAttribute msisdnAvp = radServiceRequest.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID +":"+ RadiusAttributeConstants.ELITE_PROFILE_MSISDN);
			if(msisdnAvp != null){
				LogManager.getLogger().info(MODULE, "MSISDN found in Request setting value to session MSISDN = " + session.msisdn);
				session.msisdn = msisdnAvp.getStringValue();
			}else{
				try {
					LogManager.getLogger().info(MODULE, "MSISDN not found in Request so adding it from session, MSISDN = " + session.msisdn);
					msisdnAvp = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID +":"+ RadiusAttributeConstants.ELITE_PROFILE_MSISDN);
					msisdnAvp.setStringValue(session.msisdn);
				} catch (InvalidAttributeIdException e) {
				LogManager.getLogger().warn(MODULE, "Unable to add " + RadiusAttributeConstants.ELITE_PROFILE_MSISDN_STR + " Attribute not found in Radius Dictionary");
				}			
			}
		radServiceRequest.addInfoAttribute(msisdnAvp);
			IRadiusAttribute inputOctatsAttribute = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_OCTETS,true);
		if(inputOctatsAttribute != null){
			long inputOctats= inputOctatsAttribute.getLongValue();
			ccInputOctats = inputOctats - session.previousInputOctates;
			if(ccInputOctats < 0){
				ccInputOctats = 0;
			}
			session.previousInputOctates = inputOctats;
		}
			IRadiusAttribute outputOctatsAttribute = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_OCTETS,true);
		if(outputOctatsAttribute != null){
			long outputOctats= outputOctatsAttribute.getLongValue();
			ccOutputOctats = outputOctats- session.previousOutputOctates;
			if(ccOutputOctats < 0){
				ccOutputOctats = 0;
			}
			session.previousOutputOctates = outputOctats;
		}
		IRadiusAttribute acctSessionTimeAttribute = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_TIME,true);
		if(acctSessionTimeAttribute != null){
			long acctSessionTime = acctSessionTimeAttribute.getLongValue();
			ccTime = acctSessionTime - session.previousTime;
			if(ccTime < 0){
				ccTime = 0;
			}
			session.previousTime = acctSessionTime;
		}
		long ccTotalOctets  = ccInputOctats + ccOutputOctats;	
		GroupedAttribute ecDtaMsccAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.EC_DTA_MSCC);
		if(ecDtaMsccAttr == null){
			LogManager.getLogger().warn(MODULE, "Unable to add " + RadiusAttributeConstants.EC_DTA_MSCC_STR + " Attribute not found in Radius Dictionary");
			return;
		}
			
		IRadiusAttribute ecAcctTotalOctetsAttr = null;
			// ELITE_ACCT_TOTAL_USED_UNITS 
			try {
			ecAcctTotalOctetsAttr = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID +":"+ RadiusAttributeConstants.EC_DTA_MSCC + ":" + RadiusAttributeConstants.EC_DTA_MSCC_TOTAL_OCTETS);
			ecAcctTotalOctetsAttr.setStringValue(String.valueOf(ccTotalOctets));
			ecDtaMsccAttr.addTLVAttribute(ecAcctTotalOctetsAttr);
			} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().warn(MODULE,  "Unable to add " + RadiusAttributeConstants.EC_DTA_MSCC_TOTAL_OCTETS_STR + " Attribute, Reason: " + e.getMessage());
			}
			
		IRadiusAttribute ecAcctInOctetsAttr = null;
			try {
			ecAcctInOctetsAttr = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID +":"+  RadiusAttributeConstants.EC_DTA_MSCC + ":" + RadiusAttributeConstants.EC_DTA_MSCC_IN_OCTETS);
			ecAcctInOctetsAttr.setStringValue(String.valueOf(ccInputOctats));
			ecDtaMsccAttr.addTLVAttribute(ecAcctInOctetsAttr);
			} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().warn(MODULE,  "Unable to add " + RadiusAttributeConstants.EC_DTA_MSCC_IN_OCTETS_STR + " Attribute, Reason: " + e.getMessage());
			}
			
		IRadiusAttribute ecAcctOutOctetsAttr = null;
			try {
			ecAcctOutOctetsAttr = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID +":"+  RadiusAttributeConstants.EC_DTA_MSCC + ":" + RadiusAttributeConstants.EC_DTA_MSCC_OUT_OCTETS);
			ecAcctOutOctetsAttr.setStringValue(String.valueOf(ccOutputOctats));
			ecDtaMsccAttr.addTLVAttribute(ecAcctOutOctetsAttr);
			} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().warn(MODULE, "Unable to add " + RadiusAttributeConstants.EC_DTA_MSCC_OUT_OCTETS_STR + " Attribute, Reason: " + e.getMessage());
			}

		IRadiusAttribute ecAcctTime = null;
		try {
			ecAcctTime = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID +":"+  RadiusAttributeConstants.EC_DTA_MSCC + ":" + RadiusAttributeConstants.EC_DTA_MSCC_TIME);
			ecAcctTime.setStringValue(String.valueOf(ccTime));
			ecDtaMsccAttr.addTLVAttribute(ecAcctTime);
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().warn(MODULE, RadiusAttributeConstants.EC_DTA_MSCC_TIME_STR + " Attribute, Reason: " + e.getMessage());
		}		
		radServiceRequest.addInfoAttribute(ecDtaMsccAttr);
	}
	@Override
	public void handleRequest(ServiceRequest request,ServiceResponse response)
	throws DriverProcessFailedException {
			incrementTotalRequests();
			DiameterRequest diameterRequest = new DiameterRequest();
			TranslatorParamsImpl params = new TranslatorParamsImpl(request, diameterRequest);
			
			try {
				setSessionValues((RadServiceRequest)request);
				TranslationAgent.getInstance().translate(driverConf.getTranslationMappingName(),  params, TranslatorConstants.REQUEST_TRANSLATION);
				request.setParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING, params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));

				if(!(Boolean.parseBoolean(String.valueOf(params.getParam(TranslatorConstants.DUMMY_MAPPING))))){
					setMendatoryAvpsFromClassAttribute((RadServiceRequest)request, diameterRequest);
					((RadServiceRequest)request).stopFurtherExecution();
					response.setProcessingCompleted(false);
					request.setParameter(AAATranslatorConstants.DESTINATION_REQUEST, diameterRequest);

					radiusRequestMap.put(diameterRequest.getHop_by_hopIdentifier(), request);
					radiusResponseMap.put(diameterRequest.getHop_by_hopIdentifier(), response);
					this.virtualInputStream.received(diameterRequest);
					incrementTotalSuccess();
				}else{
					TranslatorParamsImpl tempParam = new TranslatorParamsImpl(null, response, request, null);
					tempParam.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, request.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
					tempParam.setParam(AAATranslatorConstants.DUMMY_MAPPING, true);
					try {
						TranslationAgent.getInstance().translate(driverConf.getTranslationMappingName(),  tempParam, TranslatorConstants.RESPONSE_TRANSLATION);
						incrementTotalSuccess();
					} catch (TranslationFailedException e) {
						response.markForDropRequest();
						response.setProcessingCompleted(true);
						LogManager.getLogger().trace(MODULE,e);
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error while translating response reason: " + e.getMessage());
						driverStatistics.incrTranslationFailed();
					}			
				}
			} catch (TranslationFailedException e) {
				LogManager.getLogger().trace(MODULE,e);
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error while translating response reason: " + e.getMessage());
				
				response.setFurtherProcessingRequired(false);
				response.isMarkedForDropRequest();
				
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Translation failed, dropping request");
				driverStatistics.incrTranslationFailed();
			} catch (Exception e) {
				response.setProcessingCompleted(true);
				response.markForDropRequest();
				
				incrementTotalErrorResponses();
				LogManager.getLogger().trace(MODULE,e);
				
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Error while submitting request to stack, dropping request");
				
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error while submitting request to stack reason: " + e.getMessage());
			}
		}

	@Override
	public int getType() {		
		return driverConf.getDriverType().value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RM_DIAMETER_DRIVER.name();
	}
	
	@Override
	public String getName() {		
		return driverConf.getDriverName();
	}
	@Override
	protected int getStatusCheckDuration() {		
		return 0;
	}
	
	@Override
	public void scan() {
	}
	private boolean containsClassAttribute(RadServiceResponse response,String sessionId){
		Collection<IRadiusAttribute> classAttributes =  response.getRadiusAttributes(true,RadiusAttributeConstants.CLASS);
		if(classAttributes != null){
			String compareVal = DiameterAVPConstants.SESSION_ID_STR + "="+ sessionId;
			for(IRadiusAttribute radiusAttribute : classAttributes){
				String val  = radiusAttribute.getStringValue();
				if(val.contains(compareVal))
					return true;
			}
		}
		return false;
	}
	private void callDisconnectUrl(RadServiceRequest request){
		String disconnectUrl = this.driverConf.getDisconnectUrl();
		StringBuffer buffer = new StringBuffer(disconnectUrl);
		int fromIndex = buffer.indexOf("{");
		int endIndex  = buffer.indexOf("}");
		if(fromIndex != -1 && endIndex != -1){
			String attributeId = buffer.substring(fromIndex+1,endIndex);
			IRadiusAttribute attribute = request.getRadiusAttribute(attributeId,true);
			String val = null;			
			if(attribute != null){
				val = attribute.getStringValue();				
				disconnectUrl = disconnectUrl.replace(attributeId, val);
				buffer = new StringBuffer(disconnectUrl);
				fromIndex = buffer.indexOf("{");
				if(fromIndex > 0)
					buffer = buffer.deleteCharAt(fromIndex);
				endIndex  = buffer.indexOf("}");
				if(endIndex != -1)
					buffer = buffer.deleteCharAt(endIndex);
				disconnectUrl = buffer.toString();
				disconnectUrl = disconnectUrl.replaceAll("}", "");
			}else{
				LogManager.getLogger().info(MODULE, "Attribute id: " + attributeId + "Not found in request packet");
			}
			
		}
		try {
			URL url = new URL(disconnectUrl);
			HttpURLConnection  connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(2000);
			LogManager.getLogger().info(MODULE, "Trying to disconnect using URL: " + disconnectUrl);			
			connection.connect();
			int responseCode = connection.getResponseCode();
			LogManager.getLogger().info(MODULE, "Response code received from URL : " + responseCode);
		} catch (MalformedURLException e) {
			LogManager.getLogger().error(MODULE, "error while calling disconnect url reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}catch(SocketTimeoutException e){
			LogManager.getLogger().error(MODULE, "error while calling disconnect url reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}catch (IOException e) {
			LogManager.getLogger().error(MODULE, "error while calling disconnect url reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
	}

	
	private class ServiceUnitsSession{
		
		private long previousInputOctates = 0;
		private long previousOutputOctates = 0;
		private long previousTime = 0;
		private String msisdn = "";
		private ServiceUnitsSession(){				
		}
	}

	private void initVirtualPeer() throws ElementRegistrationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Initializing Diameter Virtual Peers configuration."); 
		}
		PeerDataImpl peerData = new PeerDataImpl();
		peerData.setPeerName(getName());
		peerData.setHostIdentity(getName());
		peerData.setSecurityStandard(SecurityStandard.NONE);
		peerData.setRemoteIPAddress("localhost");
		peerData.setInitiateConnectionDuration(0);
		peerData.setWatchdogInterval(0);

		this.virtualInputStream = ((AAAServerContext)getServerContext()).registerVirtualPeer(peerData, 

				new VirtualOutputStream() {

			@Override
			public void send(Packet diaPacket) {
				DiameterPacket packet = (DiameterPacket) diaPacket;
				handleReceivedDiameterResponse((DiameterPacket)packet);
			}
		});
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Diameter Virtual Peers configuration initialized successfully."); 
		}
	}
	
	@SuppressWarnings("unchecked")
	private void handleReceivedDiameterResponse(DiameterPacket packet){
		
		ServiceRequest request = radiusRequestMap.remove(packet.getHop_by_hopIdentifier());
		ServiceResponse response = radiusResponseMap.remove(packet.getHop_by_hopIdentifier());
		
		if(request==null || response==null){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Invalid Diameter Response Received for Hop-By-Hop identifier: "+packet.getHop_by_hopIdentifier());
			return;
		}
		
		
		TranslatorParamsImpl params = new TranslatorParamsImpl(packet, response, request, request.getParameter(AAATranslatorConstants.DESTINATION_REQUEST));
		params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING,request.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
		try {
			TranslationAgent.getInstance().translate(RMDiameterChargingDriver.this.driverConf.getTranslationMappingName(), params, TranslatorConstants.RESPONSE_TRANSLATION);
			removeSession((DiameterAnswer)packet);
			IDiameterAVP resultCodeAvp = packet.getAVP(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL+"."+DiameterAVPConstants.RESULT_CODE);
			long resultCode = ResultCode.DIAMETER_SUCCESS.code;
			if(resultCodeAvp != null){
				resultCode = resultCodeAvp.getInteger();
			}
			if(resultCode != ResultCode.DIAMETER_SUCCESS.code){
				RadServiceResponse radResponse = (RadServiceResponse)response;
				RadiusPacket disconnectPacket = new RadiusPacket();
				disconnectPacket.setBytes(radResponse.getResponseBytes());
				disconnectPacket.setPacketType(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE);
				/* Sending disconnect request from DynaAuthService */
				//EliteAAAServiceExposerManager.getInstance().requestDisconnect(disconnectPacket);
				callDisconnectUrl((RadServiceRequest)request);
			}else if(response != null && ((RadServiceResponse)response).getPacketType() == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
				IRadiusAttribute classAttribute  = ((RadServiceResponse)response).getRadiusAttribute(RadiusAttributeConstants.CLASS);
				String sessionId = packet.getAVPValue(DiameterAVPConstants.SESSION_ID);
				if(classAttribute == null || !containsClassAttribute((RadServiceResponse)response,sessionId)){
					classAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CLASS);
					String classAttributeValue = DiameterAVPConstants.SESSION_ID_STR+"="+sessionId+ ","+ DiameterAVPConstants.CC_REQUEST_NUMBER_STR+ "=" + packet.getAVPValue(DiameterAVPConstants.CC_REQUEST_NUMBER);
					classAttribute.setStringValue(classAttributeValue);
					((RadServiceResponse)response).addAttribute(classAttribute);
				}
			}
			
			((AsyncRequestExecutor<ServiceRequest, ServiceResponse>) request.getParameter(
					RM_ASYNC_REQUEST_EXECUTOR)).handleServiceRequest(
							request, response);
		} catch (TranslationFailedException e) {
			LogManager.getLogger().trace(MODULE,e);
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while translating response reason: " + e.getMessage());
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Translation failed, dropping request");
			response.markForDropRequest();
			response.setProcessingCompleted(true);				
		}
		
	
		
	}
	
	@Override
	protected ESIStatisticsImpl createESIStatistics() {
		driverStatistics = new RMDiameterChargingDriverStatisctic();
		return driverStatistics;
	}
	
	/**
	 * This class will be used to maintain the 
	 * custom counter for Diameter Charging Driver.
	 * 
	 * @author elitecore
	 */
	private class RMDiameterChargingDriverStatisctic extends ESIStatisticsImpl{
		
		private AtomicLong translationFailed = new AtomicLong(0);
		
		public long getTranslationFailed() {
			return translationFailed.get();
		}
		
		public void incrTranslationFailed() {
			this.translationFailed.incrementAndGet();
		}
		
		@Override
		public String toString() {
			int[] width= {35,30};
			String[] header={};
			TableFormatter esiStatsTableFormatter = new TableFormatter(header, width,TableFormatter.NO_BORDERS);
			esiStatsTableFormatter.addRecord(new String[]{"Total Translation Failed",":"+String.valueOf(getTranslationFailed())});

			return super.toString() + esiStatsTableFormatter.getFormattedValues();
		}
	}
}

