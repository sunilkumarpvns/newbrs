package com.elitecore.coreeap.fsm.eap.method.tls.recordtypes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.AccountInfoProviderException;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.data.tls.TLSSecurityKeys;
import com.elitecore.coreeap.data.tls.TLSSecurityParameters;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.dictionary.tls.TLSRecordTypeDictionary;
import com.elitecore.coreeap.fsm.EAPFailureReasonConstants;
import com.elitecore.coreeap.fsm.eap.EapStateMachine;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers.MD4;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers.MSCHAP2Handler;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers.MSCHAPv1Handler;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.EAPPacketFactory;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.packet.types.IdentityEAPType;
import com.elitecore.coreeap.packet.types.NAKEAPType;
import com.elitecore.coreeap.packet.types.tls.record.ITLSRecord;
import com.elitecore.coreeap.packet.types.tls.record.TLSPlaintext;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ContentType;
import com.elitecore.coreeap.packet.types.tls.record.types.AVP;
import com.elitecore.coreeap.packet.types.tls.record.types.ApplicationDataRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.ITLSRecordType;
import com.elitecore.coreeap.session.IEAPMethodSession;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.AttributeConstants;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.events.ApplicationEvents;
import com.elitecore.coreeap.util.constants.fsm.states.ApplicationStates;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;
import com.elitecore.coreeap.util.constants.tls.application.ApplicationMethodConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class TlsAppMethodStateMachine extends BaseMethodTypesStateMachine{
	
	public static final String MODULE = "APPLICATION STATE MACHINE";
	public static final String TUNNEL_EAP_STATE_MACHINE = "TUNNEL_EAP_STATE_MACHINE";
	public static final String COLON_SEPERATOR =":";
	private static final int MANDATORY_FLAG = 64;
	@SuppressWarnings("unused")
	private static final int VENDOR_FLAG = 128;
	private static final int VENDOR_MANDATORY_FLAG=192;
	
//	private static final int MSCHAP_CHALLENGE = 11;
//	private static final int MSCHAP2_RESPONSE = 25;
//	private static final int MSCHAP_RESPONSE = 1;
	
	private boolean isInnerIdentityRequested = false;
	private String failureReason;
	private ApplicationMethodConstants methodType = null;
	private int outerMethod; // It could be PEAP or TTLS
	private ICustomerAccountInfo customerAccountInfo = null;
	private boolean bSuccessIndication = false;
	private Queue<ITLSRecordType> responseRecordQueue = new LinkedBlockingQueue<ITLSRecordType>();
	HashMap<String, AVP>codeAVPmap = new HashMap<String, AVP>(4);
	
	public TlsAppMethodStateMachine(IEapConfigurationContext eapConfigurationContext, int method) {
		super(eapConfigurationContext);
		this.outerMethod = method;
		changeCurrentState(ApplicationStates.INITIALIZED);
	}

	public boolean check(ITLSRecord tlsRecordType) {
		// TODO Auto-generated method stub		
		return true;
	}

	public void parseApplicationRecord(ITLSRecordType tlsRecordType, ICustomerAccountInfoProvider provider){
		if(outerMethod == EapTypeConstants.TTLS.typeId || outerMethod == EapTypeConstants.TLS.typeId){
			
			Collection<AVP> avps = ((ApplicationDataRecordType)tlsRecordType).getAVPs();
			Iterator<AVP> avpsIterator = avps.iterator();
			
			AVP tempAVP=null;
			
			while(avpsIterator.hasNext()){
				tempAVP = (AVP)avpsIterator.next();			
				codeAVPmap.put(tempAVP.getVendorID()+COLON_SEPERATOR+tempAVP.getCode(),tempAVP);
			}
			tempAVP = null;
			
			/*
			 * Eliminated using identity attribute configuration to locate inner identity attribute, rather 0:1 (i.e. UserName) is 
			 * considered to be identity attribute.
			 */
			tempAVP = codeAVPmap.get(AttributeConstants.STANDARD_USER_NAME);
			if(tempAVP == null){
				tempAVP = codeAVPmap.get(AttributeConstants.STANDARD_EAP_MESSAGE);
				if(tempAVP == null){
					tempAVP = codeAVPmap.get(AttributeConstants.STANDARD_EAP_PAYLOAD);
				}
			}else{
				try {
					getTLSConnectionState().setParameterValue(IEAPMethodSession.TTLS_USER_IDENTITY, new String(tempAVP.getValue(),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					getTLSConnectionState().setParameterValue(IEAPMethodSession.TTLS_USER_IDENTITY, new String(tempAVP.getValue()));
				}
			}
			
			if(tempAVP == null){
				getTLSConnectionState().setParameterValue(IEAPMethodSession.TTLS_USER_IDENTITY, null);
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "User identity not found in TTLS data, building EAP-Failure");
				}
				setDone(true);
				setFailure(true);
				setSuccess(false);
				setFailureReason(EAPFailureReasonConstants.USER_NOT_FOUND);
				return;
			}
			
			if((tempAVP = (AVP)codeAVPmap.get(AttributeConstants.STANDARD_USER_PASSWORD))!= null ){
				setMethodType(ApplicationMethodConstants.PAP);
			}else if((tempAVP = (AVP)codeAVPmap.get(AttributeConstants.STANDARD_CHAP_PASSWORD))!= null ){					
				if((tempAVP = (AVP)codeAVPmap.get(AttributeConstants.STANDARD_CHAP_CHALLENGE)) != null ){
					//chap
					setMethodType(ApplicationMethodConstants.CHAP);
				}
			}else if((tempAVP = (AVP)codeAVPmap.get(AttributeConstants.STANDARD_EAP_PAYLOAD)) != null 
					|| (tempAVP = (AVP)codeAVPmap.get(AttributeConstants.STANDARD_EAP_MESSAGE)) != null ){
				//eap
				setMethodType(ApplicationMethodConstants.EAP);
			}else if((tempAVP = (AVP)codeAVPmap.get(AttributeConstants.MICROSOFT_MSCHAP_CHALLENGE))!= null ){
				if((tempAVP = (AVP)codeAVPmap.get(AttributeConstants.MICROSOFT_MSCHAP2_RESPONSE))!= null ){
					//mschapv2
					setMethodType(ApplicationMethodConstants.MSCHAPv2);
				}else if((tempAVP = (AVP)codeAVPmap.get(AttributeConstants.MICROSOFT_MSCHAP_CHALLENGE))!= null ){
					if((tempAVP = (AVP)codeAVPmap.get(AttributeConstants.MICROSOFT_MSCHAP_RESPONSE))!= null ){
						//mschapv1
						setMethodType(ApplicationMethodConstants.MSCHAP);
					}
				}
			}			
		}else if(outerMethod == EapTypeConstants.PEAP.typeId){
			setMethodType(ApplicationMethodConstants.EAP);
		}
	}
	

	public void process(ITLSRecord tlsRecord,ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "TLS Application packet being processed in state:" + getCurrentState());
		reset();
		TLSPlaintext tlsPlaintextRecord = (TLSPlaintext)tlsRecord;
		Collection<ITLSRecordType> applicationRecords = tlsPlaintextRecord.getContent();
		
		try{
			if(applicationRecords.size() > 0){
				Iterator<ITLSRecordType> applicationRecordsIterator = applicationRecords.iterator();
				while(applicationRecordsIterator.hasNext()){
					ITLSRecordType tlsRecordType = applicationRecordsIterator.next();		
					ApplicationEvents event = (ApplicationEvents)getEvent(tlsRecordType, provider);
					setCurrentEvent(event);			
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "TLS Application event raised : "+ event);
					handleEvent(event, tlsRecordType,provider);
					IEnum state = getNextState(event);
					changeCurrentState(state);
				}
			}else if(!isInnerIdentityRequested){ // Request Identity	
				LogManager.getLogger().trace(MODULE, "Inner request identity");
				ITLSRecordType tlsRecordType = new ApplicationDataRecordType();
				ApplicationEvents event = (ApplicationEvents)getEvent(tlsRecordType, provider);
				setCurrentEvent(event);			
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "TLS Application event raised : "+ event);
				handleEvent(event, tlsRecordType,provider);
				IEnum state = getNextState(event);
				changeCurrentState(state);
				isInnerIdentityRequested = true;
			}
			
		}catch(AccountInfoProviderException e){
			throw e;
		}catch(Exception e){
			//TODO - require proper handling
			setFailure(true);
			setSuccess(false);
			setDone(true);
		}
	}

	public void reset() {
		setSuccess(false);
		setFailure(false);
		setDone(false);
		clearCustomerAccountInfo();
	}

	public IEnum getNextState(IEnum event){
		switch((ApplicationEvents)event){
		case AppPAPRequestReceived:
			return ApplicationStates.PAP;
		case AppCHAPRequestReceived:
			return ApplicationStates.CHAP;
		case AppEAPRequestReceived:
			return ApplicationStates.EAP;
		case AppMSCHAPv2RequestReceived:
			return ApplicationStates.MSCHAPv2;
		case AppMSCHAPRequestReceived:
			return ApplicationStates.MSCHAP;
		default:
			return ApplicationStates.PAP;
		}
	}
	
	public IEnum getEvent(ITLSRecordType tlsRecordType, ICustomerAccountInfoProvider provider) {
		parseApplicationRecord(tlsRecordType, provider);
		if(getMethodType() == ApplicationMethodConstants.PAP){
			return ApplicationEvents.AppPAPRequestReceived;
		}else if(getMethodType() == ApplicationMethodConstants.CHAP){
			return ApplicationEvents.AppCHAPRequestReceived;			
		}else if(getMethodType() == ApplicationMethodConstants.EAP){
			return ApplicationEvents.AppEAPRequestReceived;
		}else if(getMethodType() == ApplicationMethodConstants.MSCHAPv2){
			return ApplicationEvents.AppMSCHAPv2RequestReceived;
		}else if(getMethodType() == ApplicationMethodConstants.MSCHAP){
			return ApplicationEvents.AppMSCHAPRequestReceived;
		}		
		return null;
	}
	
	public IEnum handleEvent(IEnum event, ITLSRecordType tlsRecordType,ICustomerAccountInfoProvider provider) throws EAPException {
		applyActions(event,tlsRecordType,provider);
		return null;
	}	
//	public void actionOnRequestReceived(ITLSRecordType tlsRecordType){
//		Logger.logTrace(MODULE, "CAll - Application Action for RequestReceived, State : " + getCurrentState());		
//	}
//	public void actionOnInvalidRequest(ITLSRecordType tlsRecordType){
//		Logger.logTrace(MODULE, "CAll - Application Action for InvalidRequest, State : " + getCurrentState());
//	}
	
	private void applyActions(IEnum event, ITLSRecordType tlsRecordType, ICustomerAccountInfoProvider provider) throws EAPException {
		switch((ApplicationEvents)event){
		case AppPAPRequestReceived:
			actionOnHandlePAPAuth(tlsRecordType,provider);
			break;
		case AppCHAPRequestReceived:
			actionOnHandleCHAPAuth(tlsRecordType,provider);
			break;
		case AppEAPRequestReceived:
			actionOnHandleEAPAuth(tlsRecordType,provider);
			break;
		case AppMSCHAPv2RequestReceived:
			actionOnHandleMSCHAPv2Auth(tlsRecordType,provider);
			break;
		case AppMSCHAPRequestReceived:
			actionOnHandleMSCHAPv1Auth(tlsRecordType,provider);
			break;
		default:
			actionOnHandlePAPAuth(tlsRecordType,provider);
		}
	}

	public void actionOnHandleEAPAuth(ITLSRecordType tlsRecordType,ICustomerAccountInfoProvider provider) throws EAPException{
		
		if(outerMethod == EapTypeConstants.TTLS.typeId || outerMethod == EapTypeConstants.TLS.typeId){

			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Application action for HandleEAPAuth, State : " + getCurrentState());
			
			AVP eapAVP = codeAVPmap.get(AttributeConstants.STANDARD_EAP_PAYLOAD);
			if(eapAVP == null)
				eapAVP = codeAVPmap.get(AttributeConstants.STANDARD_EAP_MESSAGE);
			byte[] eapPacketBytes = eapAVP.getValue();
			
			EapStateMachine eapStateMachine = null;				
			
			if(getTLSConnectionState().getParameterValue(TUNNEL_EAP_STATE_MACHINE) == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "New EAP State Machine is created ");
				int tunnelMethod = getEapConfigurationContext().getTTLSDefaultNegotiationMethod();
				eapStateMachine = new EapStateMachine(getEapConfigurationContext(),true,tunnelMethod); //this shows that this state machine is now inside the tunnel negotiation			
				getTLSConnectionState().setParameterValue(TUNNEL_EAP_STATE_MACHINE, eapStateMachine);
			}else{			
				eapStateMachine = (EapStateMachine)getTLSConnectionState().getParameterValue(TUNNEL_EAP_STATE_MACHINE);
			}
			
			AAAEapRespData aaaEapRespData = null;
			EAPPacket eapRequestPacket=null;
			try {
				if(eapPacketBytes != null){
					EAPPacket eapPacket = new EAPPacket(eapPacketBytes);
					if(eapPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
						aaaEapRespData = new AAAEapRespData(eapPacket);
						eapStateMachine.processRequest(aaaEapRespData,provider);					
						eapRequestPacket = aaaEapRespData.getEapReqPacket();
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, "EAP Request Packet : " + eapRequestPacket);
					}									
				}else{
						EAPPacket tunnelEAPPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.RESPONSE.packetId);
						
						IdentityEAPType identityEAPType = (IdentityEAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.IDENTITY.typeId);
						try{
							identityEAPType.setIdentity("eliteaaa".getBytes(CommonConstants.UTF8));
						}catch(UnsupportedEncodingException e){
							identityEAPType.setIdentity("eliteaaa".getBytes());
						}
						tunnelEAPPacket.setIdentifier(1);
						tunnelEAPPacket.setEAPType(identityEAPType);
						tunnelEAPPacket.resetLength();									
						
						if(tunnelEAPPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
							aaaEapRespData = new AAAEapRespData(tunnelEAPPacket);
							eapStateMachine.processRequest(aaaEapRespData,provider);					
							EAPPacket requestEAPPacket = aaaEapRespData.getEapReqPacket();
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "EAP Request Packet : " + requestEAPPacket);
						}									

						eapRequestPacket = aaaEapRespData.getEapReqPacket();
						if(eapRequestPacket.getEAPType().getType() != EapTypeConstants.MD5_CHALLENGE.typeId){
							tunnelEAPPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.RESPONSE.packetId);
							
							NAKEAPType nakEAPType = (NAKEAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.NAK.typeId);
							byte[] alternateMethod = {(byte)4};
							nakEAPType.setAlternateMethods(alternateMethod);
							
							tunnelEAPPacket.setIdentifier(eapRequestPacket.getIdentifier());
							tunnelEAPPacket.setEAPType(nakEAPType);
							tunnelEAPPacket.resetLength();						
							
							if(tunnelEAPPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
								aaaEapRespData = new AAAEapRespData(tunnelEAPPacket);
								eapStateMachine.processRequest(aaaEapRespData,provider);					
								EAPPacket requestEAPPacket = aaaEapRespData.getEapReqPacket();
								if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
									LogManager.getLogger().trace(MODULE, "EAP Request Packet : " + requestEAPPacket);
							}								
						}
				}
			} catch (InvalidEAPPacketException e) {
//				if(Logger.isLogLevel(LogLevel.ERROR)
//					Logger.logError(MODULE,"Error during eap authentication : " + e.getMessage());
//				Logger.logTrace(MODULE,e);
				throw new EAPException("Error during eap authentication. Reason: " + e.getMessage(), e);
			}		
			
			if(eapRequestPacket != null){
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "EAP response while handling application data:" + Utility.bytesToHex(eapRequestPacket.getBytes()));
				if(eapRequestPacket.getCode() == EapPacketConstants.SUCCESS.packetId){
					setDone(true);
					setSuccess(true);
					getTLSConnectionState().setParameterValue(TUNNEL_EAP_STATE_MACHINE, new EapStateMachine(getEapConfigurationContext()));
					return;
				}else if(eapRequestPacket.getCode() == EapPacketConstants.FAILURE.packetId){
					setDone(true);
					setFailure(true);				
					if(eapStateMachine != null)
						setFailureReason(eapStateMachine.getFailureReason());
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, "Received application record bytes: " + Utility.bytesToHex(tlsRecordType.getBytes()));
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "TTLS Failed for tunneled EAP authentication.");
					getTLSConnectionState().setParameterValue(TUNNEL_EAP_STATE_MACHINE, new EapStateMachine(getEapConfigurationContext()));
					return;
				}else if(eapRequestPacket.getCode() == EapPacketConstants.REQUEST.packetId){
					AVP responseAVP = new AVP();
					responseAVP.setId(getEapConfigurationContext().getEAPAttributeID());
					
					responseAVP.setFlag(MANDATORY_FLAG);
					responseAVP.setValue(eapRequestPacket.getBytes());
					responseAVP.refreshHeader();
				
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, "AVP Response - " +  Utility.bytesToHex(responseAVP.getBytes()));
					
					TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
					ITLSRecordType recordType = (ITLSRecordType)TLSRecordTypeDictionary.getInstance().createTLSRecord(TLSRecordConstants.ApplicationData.value, tlsSecurityParameters.getProtocolVersion());
					((ApplicationDataRecordType)recordType).setAVP(responseAVP);
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, "Aplication record bytes : " + Utility.bytesToHex(recordType.getBytes()));
					addResponseRecord(recordType);
					return;
				}
			}	
			else{
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "EAP response after handling application data: null");
					LogManager.getLogger().trace(MODULE, "Received application record bytes: " + Utility.bytesToHex(tlsRecordType.getBytes()));
				}
				setDone(true);
				setFailure(true);
			}
		}else if(outerMethod == EapTypeConstants.PEAP.typeId){
			
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Application action for HandleEAPAuth, State : " + getCurrentState());
			
			if(bSuccessIndication){
				bSuccessIndication = false;
				setSuccess(true);
				setDone(true);
				return;
			}

			if(tlsRecordType == null || tlsRecordType.getBytes().length == 0) {
				EAPPacket tunnelEAPPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.REQUEST.packetId);
				
				IdentityEAPType identityEAPType = (IdentityEAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.IDENTITY.typeId);
				identityEAPType.setIdentity(new byte[1]);
				try {
					tunnelEAPPacket.setIdentifier(getTLSConnectionState().getIdentifier());
					tunnelEAPPacket.setEAPType(identityEAPType);
				} catch (InvalidEAPPacketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				tunnelEAPPacket.resetLength();									

				
				ITLSRecordType recordType = new ApplicationDataRecordType();
				recordType.setBytes(tunnelEAPPacket.getBytes());
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Aplication record bytes : " + Utility.bytesToHex(recordType.getBytes()));
				addResponseRecord(recordType);
				return;	
			}
			byte[] eapPacketBytes = ((ApplicationDataRecordType)tlsRecordType).getBytes();
			
			EapStateMachine eapStateMachine = null;				
			
			if(getTLSConnectionState().getParameterValue(TUNNEL_EAP_STATE_MACHINE) == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "New EAP State Machine is created ");
			
				int tunnelMethod = getEapConfigurationContext().getPEAPDefaultNegotiationMethod();
				eapStateMachine = new EapStateMachine(getEapConfigurationContext(),true,tunnelMethod);			
				getTLSConnectionState().setParameterValue(TUNNEL_EAP_STATE_MACHINE, eapStateMachine);
			}else{			
				eapStateMachine = (EapStateMachine)getTLSConnectionState().getParameterValue(TUNNEL_EAP_STATE_MACHINE);
			}
			
			AAAEapRespData aaaEapRespData = null;
			EAPPacket eapRequestPacket=null;
			try {
				if(eapPacketBytes != null){
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, "TLS App Bytes : " + TLSUtility.bytesToHex(eapPacketBytes));
					EAPPacket eapPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.RESPONSE.packetId);
					EAPType eapType = EAPTypeDictionary.getInstance().createEAPType(eapPacketBytes[0]);
					byte[] tempData = new byte[eapPacketBytes.length-1];
					System.arraycopy(eapPacketBytes, 1, tempData, 0, tempData.length);
					
					eapPacket.setIdentifier(getTLSConnectionState().getIdentifier());
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
						LogManager.getLogger().trace(MODULE, "MD5 Challenge Bytes : " + TLSUtility.bytesToHex(tempData));
						LogManager.getLogger().trace(MODULE, "MD5 Challenge Length : " + tempData.length);
					}
					eapType.setData(tempData);
					eapPacket.setEAPType(eapType);
					eapPacket.resetLength();
					
					if(eapPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
						aaaEapRespData = new AAAEapRespData(eapPacket);
						eapStateMachine.processRequest(aaaEapRespData,provider);					
						eapRequestPacket = aaaEapRespData.getEapReqPacket();
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, "EAP Request Packet : " + eapRequestPacket);
					}									
				}else{
						EAPPacket tunnelEAPPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.RESPONSE.packetId);
						
						IdentityEAPType identityEAPType = (IdentityEAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.IDENTITY.typeId);
						identityEAPType.setIdentity("eliteaaa".getBytes());											
						tunnelEAPPacket.setIdentifier(getTLSConnectionState().getIdentifier());
						tunnelEAPPacket.setEAPType(identityEAPType);
						tunnelEAPPacket.resetLength();									
						
						if(tunnelEAPPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
							aaaEapRespData = new AAAEapRespData(tunnelEAPPacket);
							eapStateMachine.processRequest(aaaEapRespData,provider);					
							EAPPacket requestEAPPacket = aaaEapRespData.getEapReqPacket();
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "EAP Request Packet : " + requestEAPPacket);
						}									

						eapRequestPacket = aaaEapRespData.getEapReqPacket();
						if(eapRequestPacket.getEAPType().getType() != EapTypeConstants.MD5_CHALLENGE.typeId){
							tunnelEAPPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.RESPONSE.packetId);
							
							NAKEAPType nakEAPType = (NAKEAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.NAK.typeId);
							byte[] alternateMethod = {(byte)4};
							nakEAPType.setAlternateMethods(alternateMethod);
							tunnelEAPPacket.setIdentifier(eapRequestPacket.getIdentifier());
							tunnelEAPPacket.setEAPType(nakEAPType);
							tunnelEAPPacket.resetLength();						
							
							if(tunnelEAPPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
								aaaEapRespData = new AAAEapRespData(tunnelEAPPacket);
								eapStateMachine.processRequest(aaaEapRespData,provider);					
								EAPPacket requestEAPPacket = aaaEapRespData.getEapReqPacket();
								if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
									LogManager.getLogger().trace(MODULE, "EAP Request Packet : " + requestEAPPacket);
							}								
						}
				}
			} catch (InvalidEAPPacketException e) {
//				if(Logger.isLogLevel(LogLevel.ERROR)
//					Logger.logError(MODULE,"Error during eap authentication : " + e.getMessage());
//				Logger.logTrace(MODULE,e);
				throw new EAPException("Error during eap authentication. Reason: " + e.getMessage(), e);
			}		
			
			if(eapRequestPacket != null){
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "EAP response while handling application data:" + Utility.bytesToHex(eapRequestPacket.getBytes()));
				if(eapRequestPacket.getCode() == EapPacketConstants.SUCCESS.packetId){
					ITLSRecordType recordType = new ApplicationDataRecordType();
					byte[] successBytes = {1,(byte)(getTLSConnectionState().getIdentifier()+1),0,11,33,(byte)128,3,0,2,0,1};
					recordType.setBytes(successBytes);
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, "Aplication record bytes : " + Utility.bytesToHex(recordType.getBytes()));
					addResponseRecord(recordType);				
					bSuccessIndication = true;
					return;

//					setDone(true);
//					setSuccess(true);
//					return;
				}else if(eapRequestPacket.getCode() == EapPacketConstants.FAILURE.packetId){
					setDone(true);
					setFailure(true);
					setSuccess(false);
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, "Received application record bytes: " + Utility.bytesToHex(tlsRecordType.getBytes()));
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "TTLS Failed for tunneled EAP authentication.");
					return;
				}else if(eapRequestPacket.getCode() == EapPacketConstants.REQUEST.packetId){
					
					ITLSRecordType recordType = new ApplicationDataRecordType();
					recordType.setBytes(eapRequestPacket.getEAPType().toBytes());
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, "Aplication record bytes : " + Utility.bytesToHex(recordType.getBytes()));
					addResponseRecord(recordType);
					return;
				}
			}	
			else{
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "EAP response after handling application data: null");
					LogManager.getLogger().trace(MODULE, "Received application record bytes: " + Utility.bytesToHex(tlsRecordType.getBytes()));
				}
				setDone(true);
				setFailure(true);
				setSuccess(false);
			}

		}		

	}
	
	public void actionOnHandleMSCHAPv2Auth(ITLSRecordType tlsRecordType,ICustomerAccountInfoProvider provider)throws EAPException{
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Application action for HandleMSCHAPv2Auth, State : " + getCurrentState());
		final int MICROSOFT_VENDOR_ID = 311;
		
		String strUserIdentity = (String)getTLSConnectionState().getParameterValue(IEAPMethodSession.TTLS_USER_IDENTITY);
		ICustomerAccountInfo customerAccountInfo = provider.getCustomerAccountInfo(strUserIdentity);
		if(customerAccountInfo == null){
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Customer profile can not be located for " + strUserIdentity + ", considering TTLS-MSCHAPv2 failure.");
			}
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.USER_NOT_FOUND);
			return;
		}			

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "User Profile: " + customerAccountInfo);
		
		setCustomerAccountInfo(customerAccountInfo);
		
		String strPasswordCheck = customerAccountInfo.getPasswordCheck();
		if(strPasswordCheck != null && strPasswordCheck.equalsIgnoreCase("NO")){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Password check is disabled for this user, but cannot be skipped for TTLS-MSCHAPv2.");
		}
		
		String userPassword = customerAccountInfo.getPassword();
		 
		AVP mschapChallengeAVP = (AVP) codeAVPmap.get(AttributeConstants.MICROSOFT_MSCHAP_CHALLENGE);
		AVP mschapResponseAVP = (AVP) codeAVPmap.get(AttributeConstants.MICROSOFT_MSCHAP2_RESPONSE);
		AVP usernameAVP = (AVP)codeAVPmap.get(AttributeConstants.STANDARD_USER_NAME);
		
		String username = null;
		try{
			username = new String(usernameAVP.getValue(),CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			username = new String(usernameAVP.getValue());
		}
		byte[] mschapResponse = mschapResponseAVP.getValue();
		byte[] challenge = mschapChallengeAVP.getValue();
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Authenticator Challenge : " + Utility.bytesToHex(challenge));
		
		int chapId = mschapResponse[0] & 0xFF; 
		byte[] peerChallenge = new byte[16];
		System.arraycopy(mschapResponse, 2, peerChallenge, 0, 16);
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Peer challenge : " + Utility.bytesToHex(peerChallenge));
		
		byte[] calculatedMSCHAP2Response = MSCHAP2Handler.getMsChap2Response(chapId,peerChallenge,challenge,username,userPassword);
		
		for(int i=0; i<calculatedMSCHAP2Response.length;i++){
			if(calculatedMSCHAP2Response[i] != mschapResponse[i]){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "MSCHAPv2 response is invalid.");
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "MSCHAPv2 Response received : " + Utility.bytesToHex(mschapResponse) + 		
					"MSCHAPv2 Response calculated : " + Utility.bytesToHex(calculatedMSCHAP2Response));
				}
				setFailure(true);
				setDone(true);
				setFailureReason(EAPFailureReasonConstants.INVALID_MSCHAPv2_PASSWORD);
				return;
			}
		}

		byte pwHashHash[] = new byte[16];
    	byte challengeHash[] = new byte[8];

			MessageDigest sha1MessageDigest = Utility.getMessageDigest(HashAlgorithm.SHA1.getIdentifier());


//    			Modified for Aricent
			
//    			sha1MessageDigest.update(mschapResponse, 2, 16);
//    			sha1MessageDigest.update(mschapResponse);
//    			sha1MessageDigest.update(MSCHAP2Handler.stringToLatin1Bytes(username));
			
			sha1MessageDigest.update(peerChallenge);
			sha1MessageDigest.update(challenge);
			try{
				sha1MessageDigest.update(username.getBytes(CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				sha1MessageDigest.update(username.getBytes());
			}
			
			byte[] digest  = sha1MessageDigest.digest();    			
           
            System.arraycopy(digest, 0, challengeHash, 0, 8);
            byte peerResponse[] = new byte[24];
            System.arraycopy(mschapResponse, 26, peerResponse, 0, 24);
			byte[] pwHash = new byte[16];
			byte[] passBytes = null;
			try{
				passBytes = userPassword.getBytes("UTF-16LE");					
			}catch(UnsupportedEncodingException e){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "User password can not be converted into UTF-16LE");	            
	        }				
			MD4.getDigest(passBytes, 0, passBytes.length, pwHash, 0);
            MD4.getDigest(pwHash, 0, 16, pwHashHash, 0);
			

		String authResponse = MSCHAP2Handler.getMsChap2SuccessValue(calculatedMSCHAP2Response,challengeHash,pwHashHash);
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Auth response : " + authResponse);
		byte[] authRespStrBytes = null;
		try {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Auth response bytes in UTF-8 format: " + Utility.bytesToHex(authResponse.getBytes(CommonConstants.UTF8)));
			authRespStrBytes = authResponse.getBytes(CommonConstants.UTF8);
		} catch (UnsupportedEncodingException e1) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during MSCHAPv2 authentication : " + e1.getMessage());
			LogManager.getLogger().trace(MODULE, e1);
		}
		byte[] authRespBytes = new byte[1+authRespStrBytes.length];
		authRespBytes[0] = calculatedMSCHAP2Response[0];
		System.arraycopy(authRespStrBytes, 0, authRespBytes, 1, authRespStrBytes.length);
		AVP responseAVP = new AVP();
		responseAVP.setId(26);
		responseAVP.setFlag(VENDOR_MANDATORY_FLAG);
		responseAVP.setVendorID(MICROSOFT_VENDOR_ID);
		responseAVP.setValue(authRespBytes);	
		responseAVP.refreshHeader();
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "AVP response: " +  Utility.bytesToHex(responseAVP.getBytes()));
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		ITLSRecordType recordType = (ITLSRecordType)TLSRecordTypeDictionary.getInstance().createTLSRecord(TLSRecordConstants.ApplicationData.value, tlsSecurityParameters.getProtocolVersion());
		((ApplicationDataRecordType)recordType).setAVP(responseAVP);			
				
		addResponseRecord(recordType);
		setDone(false);
		setSuccess(true);				
	}

	public void actionOnHandlePAPAuth(ITLSRecordType tlsRecordType,ICustomerAccountInfoProvider provider)throws EAPException{
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Application action for HandlePAPAuth, State : " + getCurrentState());
		
		String strUserIdentity = (String)getTLSConnectionState().getParameterValue(IEAPMethodSession.TTLS_USER_IDENTITY);
		ICustomerAccountInfo customerAccountInfo = provider.getCustomerAccountInfo(strUserIdentity);
		if(customerAccountInfo == null){
			LogManager.getLogger().warn(MODULE, "Customer profile can not be located for " + strUserIdentity + ", considering TTLS-PAP failure.");
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.USER_NOT_FOUND);
			return;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "User Profile: " + customerAccountInfo);
		
		setCustomerAccountInfo(customerAccountInfo);
		
		String strPasswordCheck = customerAccountInfo.getPasswordCheck();
		if(strPasswordCheck == null || !strPasswordCheck.equalsIgnoreCase("NO")){
			AVP userPasswordAVP = codeAVPmap.get(AttributeConstants.STANDARD_USER_PASSWORD);
			String userPassword = null;
			try {
				userPassword = new String(userPasswordAVP.getValue(),CommonConstants.UTF8).trim();
			} catch (UnsupportedEncodingException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error during pap authentication : " + e.getMessage());
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Received application record bytes: " + Utility.bytesToHex(tlsRecordType.getBytes()));
				LogManager.getLogger().trace(MODULE, e);
				processFailure(EAPFailureReasonConstants.INVALID_PASSWORD);
				return;
			}
			
			boolean passwordVerifiedSuccessfully = false;
			if(provider.checkLDAPBindAuthenticationRequired(customerAccountInfo)){
				passwordVerifiedSuccessfully = provider.doLdapBindAuthentication(userPassword, customerAccountInfo);
			}else{
				passwordVerifiedSuccessfully = userPassword.equals(customerAccountInfo.getPassword());
			}
			
			if(passwordVerifiedSuccessfully){
				processSuccess();
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Password is incorrect.");
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Received application record bytes: " + Utility.bytesToHex(tlsRecordType.getBytes()));
				
				processFailure(EAPFailureReasonConstants.INVALID_PASSWORD);
			}
		} else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Password check is disabled for this user, skipping TTLS-PAP validation.");
			processSuccess();
		}
	}

	private void processFailure(String failureReason){
		setDone(true);
		setFailure(true);
		setSuccess(false);
		setFailureReason(failureReason);
	}
	
	private void processSuccess(){
		setDone(true);
		setSuccess(true);
		setFailure(false);
	}
	
	
	public void actionOnHandleCHAPAuth(ITLSRecordType tlsRecordType,ICustomerAccountInfoProvider provider)throws EAPException{
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Application action for HandleCHAPAuth, State : " + getCurrentState());
		
		String strUserIdentity = (String)getTLSConnectionState().getParameterValue(IEAPMethodSession.TTLS_USER_IDENTITY);
		ICustomerAccountInfo customerAccountInfo = provider.getCustomerAccountInfo(strUserIdentity);
		if(customerAccountInfo == null){
			LogManager.getLogger().warn(MODULE, "Customer profile can not be located for " + strUserIdentity + ", considering TTLS-CHAP failure.");
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.USER_NOT_FOUND);
			return;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "User Profile: " + customerAccountInfo);
		
		setCustomerAccountInfo(customerAccountInfo);
		String strPasswordCheck = customerAccountInfo.getPasswordCheck();
		if(strPasswordCheck == null || !strPasswordCheck.equalsIgnoreCase("no")){
			TLSSecurityKeys tlsSecurityKeys = (TLSSecurityKeys)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_KEY);

			AVP chapChallengeAVP = (AVP)codeAVPmap.get(AttributeConstants.STANDARD_CHAP_CHALLENGE);
			AVP chapPasswordAVP = (AVP)codeAVPmap.get(AttributeConstants.STANDARD_CHAP_PASSWORD);
			
			byte[] receivedChapChallenge = chapChallengeAVP.getValue();

			byte[] chapChallenge = TLSUtility.generateTTLSChapChallange(tlsSecurityKeys.getMasterSecret(),tlsSecurityKeys.getClientRandom(),tlsSecurityKeys.getServerRandom(),(TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER));
			byte[] calculatedChapChallange = new byte[chapChallenge.length - 1];
			System.arraycopy(chapChallenge,0,calculatedChapChallange,0,calculatedChapChallange.length);
					
			byte[] receivedChapPassword = chapPasswordAVP.getValue();
//			byte[] chapResponse = new byte[16];
//			System.arraycopy(receivedChapPassword,1,chapResponse,0,16);
//			byte chapID = receivedChapPassword[0];		

			String userPassword = customerAccountInfo.getPassword();
			//spirent
	//		boolean isPasswordValid = TLSUtility.validateCHAPPassword(chapResponse, chapChallenge[16], userPassword, receivedChapChallenge);
			boolean isPasswordValid = TLSUtility.validateCHAPPassword(receivedChapPassword, chapChallenge[16], userPassword, receivedChapChallenge);
	//		//8950AAA
	//		if(!isPasswordValid){
	//			if(Logger.isLogLevel(LogLevel.DEBUG)
	//				Logger.logDebug(MODULE, "Going for another method of validation.");
	//			isPasswordValid = TLSUtility.validateCHAPPassword(chapResponse, chapID, userPassword, receivedChapChallenge);
	//		}
			
			if(isPasswordValid){
				setSuccess(true);
				setFailure(false);
				setDone(true);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Chap Authentication Failed");
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Received application record bytes: " + Utility.bytesToHex(tlsRecordType.getBytes()));
				setFailure(true);
				setSuccess(false);
				setDone(true);
				setFailureReason(EAPFailureReasonConstants.INVALID_CHAP_PASSWORD);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Password check is disabled for this user, skipping TTLS-CHAP validation.");
			setSuccess(true);
			setFailure(false);
			setDone(true);
		}
	}

	public void actionOnHandleMSCHAPv1Auth(ITLSRecordType tlsRecordType,ICustomerAccountInfoProvider provider)throws EAPException{
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Application action for HandleMSCHAPv1Auth, State : " + getCurrentState());
		
		String strUserIdentity = (String)getTLSConnectionState().getParameterValue(IEAPMethodSession.TTLS_USER_IDENTITY);
		ICustomerAccountInfo customerAccountInfo = provider.getCustomerAccountInfo(strUserIdentity);
		if(customerAccountInfo == null){
			LogManager.getLogger().warn(MODULE, "Customer profile can not be located for " + strUserIdentity + ", considering TTLS-MSCHAPv1 failure.");
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.USER_NOT_FOUND);
			return;
		}			

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "User Profile: " + customerAccountInfo);
		
		setCustomerAccountInfo(customerAccountInfo);
		
		String strPasswordCheck = customerAccountInfo.getPasswordCheck();
		
		if(strPasswordCheck == null || !strPasswordCheck.equalsIgnoreCase("no")){

		String userPassword = customerAccountInfo.getPassword();
		AVP mschapChallengeAVP = (AVP) codeAVPmap.get(AttributeConstants.MICROSOFT_MSCHAP_CHALLENGE);
		AVP mschapResponseAVP = (AVP) codeAVPmap.get(AttributeConstants.MICROSOFT_MSCHAP_RESPONSE);

		byte[] msChapResponse = mschapResponseAVP.getValue();
		byte[] challenge = mschapChallengeAVP.getValue();
		byte[] calculatedMSCHAPResponse = MSCHAPv1Handler.getMsChapResponse(userPassword.getBytes(), challenge, msChapResponse[0]); //ChapId
		boolean isValid = Arrays.equals(calculatedMSCHAPResponse, msChapResponse);
		
		if(isValid){
			setSuccess(true);
			setFailure(false);
			setDone(true);
		}else{

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "MSCHAPv1 Response received : "   + Utility.bytesToHex(msChapResponse) + 		
				"MSCHAP Response calculated : " + Utility.bytesToHex(calculatedMSCHAPResponse));
			}
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.INVALID_MSCHAP_PASSWORD);
		}
	}else{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Password check is disabled for this user, skipping TTLS-MSCHAPv1 validation.");
		setSuccess(true);
		setFailure(false);
		setDone(true);			
		}
	}

	public void addResponseRecord(ITLSRecordType tlsRecordType){
		this.responseRecordQueue.add(tlsRecordType);
	}

	public ITLSRecordType getResponseRecordType(){
		return this.responseRecordQueue.poll();
	}
	
	public boolean hasMoreResponseRecordType(){
		return !this.responseRecordQueue.isEmpty();
	}
	
	public void resetResponseRecordTypeQueue(){
		this.responseRecordQueue = new LinkedBlockingQueue<ITLSRecordType>();
	}

	@Override
	public byte[] getResponseTLSRecord() {
		TLSPlaintext tlsResponseRecord = new TLSPlaintext();
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		tlsResponseRecord.setContentType(new ContentType(TLSRecordConstants.ApplicationData.value));
		tlsResponseRecord.setProtocolVersion(tlsSecurityParameters.getProtocolVersion());
		ITLSRecordType tlsRecordType =null;
		while(hasMoreResponseRecordType()){
			tlsRecordType = getResponseRecordType();			
			tlsResponseRecord.setContent(tlsRecordType);
		}
		tlsResponseRecord.refreshHeader();
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "TLS Application response generated " + tlsResponseRecord);
		return tlsResponseRecord.getBytes();		
	}

	public int getType() {
		// TODO Auto-generated method stub
		return TLSRecordConstants.ApplicationData.value;
	}

	public ApplicationMethodConstants getMethodType() {
		return methodType;
	}

	public void setMethodType(ApplicationMethodConstants methodType) {
		this.methodType = methodType;
	}
	
	public ICustomerAccountInfo getCustomerAccountInfo() {
		return customerAccountInfo;
	}
		
	public void setCustomerAccountInfo(ICustomerAccountInfo customerAccountInfo) {
		this.customerAccountInfo = customerAccountInfo;
	}

	public void clearCustomerAccountInfo(){
		this.customerAccountInfo = null;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	@Override
	public void setOUI(String oui) {
		// TODO Auto-generated method stub
		
	}
}
