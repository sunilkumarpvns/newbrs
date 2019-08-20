package com.elitecore.coreeap.fsm.eap.method.sim;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.data.sim.GsmTriplet;
import com.elitecore.coreeap.data.sim.GsmTriplets;
import com.elitecore.coreeap.fsm.EAPFailureReasonConstants;
import com.elitecore.coreeap.fsm.eap.method.BaseMethodStateMachine;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.types.sim.SIMAttributeDictionary;
import com.elitecore.coreeap.packet.types.sim.SimEapType;
import com.elitecore.coreeap.packet.types.sim.attributes.ISIMAttribute;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtAnyIdReq;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtCounter;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtEncrData;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtFullAuthIdReq;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtIV;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtIdentity;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtMac;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtNextPseudonym;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtNextReAuthId;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtNonceS;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtPermanentIdReq;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtRand;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtVersionList;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.SimAkaIdentityType;
import com.elitecore.coreeap.util.constants.fsm.events.SimEvents;
import com.elitecore.coreeap.util.constants.fsm.states.SimStates;
import com.elitecore.coreeap.util.constants.sim.GsmA3A8AlgoConstants;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.constants.sim.message.SIMMessageTypeConstants;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.sim.SIMUtility;
import com.elitecore.coreeap.util.tls.TLSUtility;

public abstract class BaseSIMMethodStateMachine extends BaseMethodStateMachine {
	
	private static final String SHA = "SHA";
	private static final String MODULE= "Base SIM Method State Machine";
	private ICustomerAccountInfo customerAccountInfo = null;
	private byte[] identity = null;
	private byte[] mk = null;
	private byte[] tek,msk,emsk= null;
	private byte[] atVersionListBytes = null;
	private byte[] k_aut = null;
	private byte[] k_encr = null;
	private byte[] nonce_s = null;
	private int iAt_ctr = 0;
	private AtCounter at_ctr =  null;
	private GsmTriplets gsmTriplets;
	public static final int NUMBER_OF_TRIPLETS = 3;
	public static final int PROFILE_AS_TRIPLETDS=0;
	public static final int ULTICOM_AS_TRIPLETDS=1;
	private String pseudonymID = null;
	private String permanentID = null;
	private String fastReauthID = null;
	private String authenticatedUserIdentity = null;
	
	/***
	 * isIdentityReqRequired : is identity request required in start message
	 *  0 = identity request is not required 
	 *  1 = Any identity request is required
	 *  2 = Permanent Identity request is required
	 *  3 = Full auth Identity request is required
	 */
	private int isIdentityReqRequired = 0;
	private String failureReason ;
	
	protected SimAkaIdentityType requestType = SimAkaIdentityType.UNKNOWN_IDENTITY;
	protected int prevStartMsgType = 0;
	private boolean isStartGenerated;
	
	private int pseudonymIdMethod = 0;
	private String pseudonymPrefix = null;
	private boolean isPseudoHexEncoding;
	
	private int fastReauthIdMethod = 0;
	private String fastReauthPrefix = null;
	private boolean isFastReauthHexEncoding;
	
	public BaseSIMMethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
		super(eapConfigurationContext);
		changeCurrentState(SimStates.INITIALIZED);
		pseudonymIdMethod = eapConfigurationContext.getPseudoIdentityMethod(EapTypeConstants.SIM.typeId);
		pseudonymPrefix = eapConfigurationContext.getPseudoIdentityPrefix(EapTypeConstants.SIM.typeId);
		isPseudoHexEncoding = eapConfigurationContext.isPseudoHexEncodingSupported(EapTypeConstants.SIM.typeId);
		if (pseudonymPrefix == null)
			pseudonymIdMethod = 0;
		fastReauthIdMethod = eapConfigurationContext.getFastReauthIdentityMethod(EapTypeConstants.SIM.typeId);
		fastReauthPrefix = eapConfigurationContext.getFastReauthIdentityPrefix(EapTypeConstants.SIM.typeId);
		isFastReauthHexEncoding = eapConfigurationContext.isFastReauthHexEncodingSupported(EapTypeConstants.SIM.typeId);
		if (fastReauthPrefix == null)
			fastReauthIdMethod = 0;
		
	}

/*	public void calculateGsmTriplets(){
		boolean bUseGsmA3A8Algo = true;
		byte[] key   = TLSUtility.HexToBytes("0x11111111111111111111111111111111");
		byte[] rand1 = TLSUtility.HexToBytes("0xabcd1234abcd1234abcd1234abcd1234");
		byte[] rand2 = TLSUtility.HexToBytes("0xbcd1234abcd1234abcd1234abcd1234a");
		byte[] rand3 = TLSUtility.HexToBytes("0xcd1234abcd1234abcd1234abcd1234ab");
		
		gsmTriplets = new GsmTriplets();
		
		if(!bUseGsmA3A8Algo){			
			byte[] sRes1 = TLSUtility.HexToBytes("0x00000000");
			byte[] sRes2 = TLSUtility.HexToBytes("0x00000000");
			byte[] sRes3 = TLSUtility.HexToBytes("0x00000000");
			byte[] kcs1 = TLSUtility.HexToBytes("0x0000000000000000");
			byte[] kcs2 = TLSUtility.HexToBytes("0x0000000000000000");
			byte[] kcs3 = TLSUtility.HexToBytes("0x0000000000000000");
			GsmTriplet gsmTriplet1 = new GsmTriplet(rand1,sRes1,kcs1);
			GsmTriplet gsmTriplet2 = new GsmTriplet(rand2,sRes2,kcs2);
			GsmTriplet gsmTriplet3 = new GsmTriplet(rand3,sRes3,kcs3);
			gsmTriplets.addGsmTriplet(gsmTriplet1);
			gsmTriplets.addGsmTriplet(gsmTriplet2);
			gsmTriplets.addGsmTriplet(gsmTriplet3);
		}else{
			gsmTriplets.addGsmTriplet(rand1, key, TLSUtility.HexToBytes("0x44444444444444444444444444444444"));
			gsmTriplets.addGsmTriplet(rand2, key, TLSUtility.HexToBytes("0x44444444444444444444444444444444"));
			gsmTriplets.addGsmTriplet(rand3, key, TLSUtility.HexToBytes("0x44444444444444444444444444444444"));
		}
	}*/

	protected void calculateGsmTriplets(byte[] key, byte[] operatorVariant, byte[] rand,int gsmA3A8Algo, int numberOfTriplets) throws GeneralSecurityException{
		gsmTriplets = new GsmTriplets();
		gsmTriplets.addGsmTriplets(rand, key,operatorVariant, gsmA3A8Algo, numberOfTriplets);
	}

	@Override
	public void reset() {
		super.reset();
		if (fastReauthIdMethod == 0) {
		identity = null;
		mk = null;
		tek =null;
			k_aut = null;
			k_encr = null;
		}
		msk = null;
		emsk= null;
		atVersionListBytes = null;
		isIdentityReqRequired = 0;
		failureReason = null;		
	}
	
	abstract public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException;
	
	@Override
	public IEnum[] getActionList(IEnum event) {
		return null;
	}

	@Override
	public IEnum getEvent(AAAEapRespData aaaEapRespData) {
		EAPPacket respPacket = aaaEapRespData.getEapRespPacket();
		
		if(respPacket.getEAPType().getType() == EapTypeConstants.IDENTITY.typeId){
			return SimEvents.SimResponseIdentityReceived;
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.NAK.typeId){
			return SimEvents.SimNAKReceived;
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.SIM.typeId){

			switch (SIMMessageTypeConstants.get(((SimEapType)respPacket.getEAPType()).getSimMessageType())){
			case CLIENT_ERROR:
				return SimEvents.SimFailure;
			case SIM_START: 
				switch (requestType) {
				case PSEUDONYM_IDENTITY_REQUEST: 
					return SimEvents.SimStartReceivedWithPseudonymId;
				case PERMANENT_IDENTITY_REQUEST: 
					return SimEvents.SimStartReceivedWithPermanentId;
				case FASTREAUTH_IDENTITY_REQUEST:
					return SimEvents.SimStartReceivedWithFastReauthId;
				case UNKNOWN_IDENTITY: 
					return SimEvents.SimStartReceivedWithUnknownId;
				}
			case RE_AUTHENTICATION:
				return SimEvents.SimReauthentication;
			case SIM_CHALLENGE:
				return SimEvents.SimChallenge;
			default: 
				return SimEvents.SimRequestReceived;			
			}					

		}else if(getCurrentState() == SimStates.INITIALIZED 
					|| getCurrentState() == SimStates.SUCCESS_BUILDED 
					|| getCurrentState() == SimStates.FAILURE_BUILDED
					|| getCurrentState() == SimStates.CHALLENGE_GENERATE){
				return SimEvents.SimRequestReceived;			
		}else if(!isValidRequest() && !isSuccess()){
			return SimEvents.SimInvalidRequest;
		}else if(isValidRequest() && isSuccess() && getCurrentState() == SimStates.CHALLENGE_GENERATE){
			return SimEvents.SimSuccess;
		}else if(isValidRequest() && isFailure() && getCurrentState() == SimStates.CHALLENGE_GENERATE){
			return SimEvents.SimFailure;	
		}else{
			return SimEvents.SimUnconditionalEvent;
		}														
	}
	public IEnum getNextState(IEnum event){
		switch((SimEvents)event){
		case SimFailure:
			return SimStates.FAILURE_BUILDED;
		case SimInvalidRequest:
			return SimStates.FAILURE_BUILDED;
		case SimNAKReceived:
			return SimStates.START_GENERATED;
		case SimRequestReceived:
			return SimStates.CHALLENGE_GENERATE;
		case SimResponseIdentityReceived:
			return SimStates.START_GENERATED;
		case SimSuccess:
			return SimStates.SUCCESS_BUILDED;
		case SimUnconditionalEvent:
			return SimStates.DISCARDED;
		}
		return null;
	}
	
	public void parseSIMResp(AAAEapRespData aaaEapRespData){
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		if(eapPacket != null){
			if(eapPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
				setIdentifier(eapPacket.getIdentifier());
				if(eapPacket.getEAPType().getType() == EapTypeConstants.SIM.typeId){
					SimEapType simEapType = (SimEapType)eapPacket.getEAPType();				
					if(simEapType.getSimMessageType()== SIMMessageTypeConstants.SIM_START.Id
							|| simEapType.getSimMessageType()== SIMMessageTypeConstants.SIM_CHALLENGE.Id
							|| simEapType.getSimMessageType()== SIMMessageTypeConstants.NOTIFICATION.Id
							|| simEapType.getSimMessageType()== SIMMessageTypeConstants.RE_AUTHENTICATION.Id
							|| simEapType.getSimMessageType()== SIMMessageTypeConstants.CLIENT_ERROR.Id){
						identifyRequestType(aaaEapRespData);
						setValidRequest(true);
						return;
					}
				}								
			}
		}
		setValidRequest(false);
	}

	protected void discardRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider){
		LogManager.getLogger().trace(getModuleName(), "CAll - SIM Action for DiscardRequest, State : " + getCurrentState());
		setSuccess(false);
		setDone(false);
	}

	protected void buildSuccess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		LogManager.getLogger().trace(getModuleName(), "CAll - SIM Action for BuildSuccess, State : " + getCurrentState());
		setSuccess(true);
		setDone(true);
		LogManager.getLogger().trace(getModuleName(), "Done : " + isDone());
	}
	
	protected void buildFailure(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		LogManager.getLogger().trace(getModuleName(), "CAll - SIM Action for BuildFailure, State : " + getCurrentState());
		setFailure(true);
		setSuccess(false);
		setDone(true);
		setFailureReason(EAPFailureReasonConstants.INVALID_SIM_PASSWORD);
	}	
	
	
	protected void validateSIMResponse(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - SIM Action for ValidateSIMResponse, State : " + getCurrentState());
		if (gsmTriplets == null ){
			setFailure(true);
			setDone(true);
			return;
		}
		byte[] receivedMAC = null;		
		SimEapType receivedSimEapType = (SimEapType)aaaEapRespData.getEapRespPacket().getEAPType();
		if(receivedSimEapType.getSimMessageType() == SIMMessageTypeConstants.SIM_CHALLENGE.Id){
			List<ISIMAttribute> simAttrList = receivedSimEapType.getSimAttrList();
			for(ISIMAttribute simAttribute : simAttrList){
				if(simAttribute.getType() == SIMAttributeTypeConstants.AT_MAC.Id){					
					receivedMAC = ((AtMac)simAttribute).getMAC();
				}
			}
			
			//validate mac
			EAPPacket eapPacket = null;
			try {
				eapPacket = (EAPPacket) aaaEapRespData.getEapRespPacket().clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			SimEapType simEapType = (SimEapType)eapPacket.getEAPType();
			List<ISIMAttribute> simAttributeList = simEapType.getSimAttrList();
			for(ISIMAttribute simAttribute : simAttributeList){
				if(simAttribute.getType() == SIMAttributeTypeConstants.AT_MAC.Id){					
					((AtMac)simAttribute).setValueBytes(new byte[16]);
				}
			}
			simEapType.setSimAttrList(simAttributeList);			
			byte [] macBytes = eapPacket.getBytes();
			macBytes = TLSUtility.appendBytes(macBytes, gsmTriplets.getSres());
			LogManager.getLogger().info(getModuleName(), "MAC Input: " +TLSUtility.bytesToHex(macBytes));
			macBytes = TLSUtility.HMAC("SHA-1", macBytes, getK_aut());
			

			byte []finalMacBytes=new byte[16];
			System.arraycopy(macBytes, 0, finalMacBytes,0, 16);
			LogManager.getLogger().trace(getModuleName(), "Received MAC : " +TLSUtility.bytesToHex(receivedMAC));
			LogManager.getLogger().trace(getModuleName(), "Calculated MAC : " +TLSUtility.bytesToHex(finalMacBytes));
			LogManager.getLogger().trace(getModuleName(), "MAC is " + (Arrays.equals(receivedMAC, finalMacBytes) ?"match": "not match"));
			if(Arrays.equals(receivedMAC, finalMacBytes)){
				setSuccess(true);
				setDone(true);
				
			}else{
				setFailure(true);
				setDone(true);
			}						
		} else if(receivedSimEapType.getSimMessageType() == SIMMessageTypeConstants.RE_AUTHENTICATION.Id){
			List<ISIMAttribute> simAttrList = receivedSimEapType.getSimAttrList();
			for(ISIMAttribute simAttribute : simAttrList){
				if(simAttribute.getType() == SIMAttributeTypeConstants.AT_MAC.Id){					
					receivedMAC = ((AtMac)simAttribute).getMAC();
				}
			}

			//validate mac
			EAPPacket eapPacket = null;
			try {
				eapPacket = (EAPPacket) aaaEapRespData.getEapRespPacket().clone();
			} catch (CloneNotSupportedException e) {
				LogManager.getLogger().trace(MODULE, e);
			}
			SimEapType simEapType = (SimEapType)eapPacket.getEAPType();
			List<ISIMAttribute> simAttributeList = simEapType.getSimAttrList();
			for(ISIMAttribute simAttribute : simAttributeList){
				if(simAttribute.getType() == SIMAttributeTypeConstants.AT_MAC.Id){					
					((AtMac)simAttribute).setValueBytes(new byte[16]);
				}
			}
			simEapType.setSimAttrList(simAttributeList);			
			byte [] macBytes = eapPacket.getBytes();
			macBytes = TLSUtility.appendBytes(macBytes, nonce_s);
			LogManager.getLogger().info(getModuleName(), "MAC Input: " +TLSUtility.bytesToHex(macBytes));
			macBytes = TLSUtility.HMAC("SHA-1", macBytes, getK_aut());


			byte []finalMacBytes=new byte[16];
			System.arraycopy(macBytes, 0, finalMacBytes,0, 16);
			LogManager.getLogger().trace(getModuleName(), "Received MAC : " +TLSUtility.bytesToHex(receivedMAC));
			LogManager.getLogger().trace(getModuleName(), "Calculated MAC : " +TLSUtility.bytesToHex(finalMacBytes));
			LogManager.getLogger().trace(getModuleName(), "MAC is " + (Arrays.equals(receivedMAC, finalMacBytes) ?"match": "not match"));
			if(Arrays.equals(receivedMAC, finalMacBytes)){
				setSuccess(true);
				setDone(true);

			}else{
				setFailure(true);
				setDone(true);
			}						
		}
	}

	protected void identifyRequestType(AAAEapRespData aaaEapRespData){
		SimEapType simEapType = (SimEapType) aaaEapRespData.getEapRespPacket().getEAPType();	
		ISIMAttribute attr = simEapType.getSimAttribute(SIMAttributeTypeConstants.AT_IDENTITY.Id);
		if(attr == null){
			return;
		}
		LogManager.getLogger().trace(MODULE, "Identyfying Identity of SIM Start: " + attr);
		requestType = SimAkaIdentityType.UNKNOWN_IDENTITY;
		byte[] attrValue = attr.getValueBytes();
		String strAttrValue = null;
		try {
			strAttrValue = new String (attrValue, CommonConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			strAttrValue = new String (attrValue);
		}
		if (SIMUtility.isPermanentID(strAttrValue, pseudonymPrefix, fastReauthPrefix)){
			requestType = SimAkaIdentityType.PERMANENT_IDENTITY_REQUEST;
			LogManager.getLogger().debug(MODULE, "Identity Type: PERMANENT_IDENTITY_REQUEST");
		}
		else if (SIMUtility.isPseudonymID(strAttrValue, pseudonymID)){
			requestType = SimAkaIdentityType.PSEUDONYM_IDENTITY_REQUEST;
			LogManager.getLogger().debug(MODULE, "Identity Type: PSEUDONYM_IDENTITY_REQUEST");
		} else if (SIMUtility.isFastReauthID(strAttrValue, fastReauthID)){
			requestType = SimAkaIdentityType.FASTREAUTH_IDENTITY_REQUEST;
			LogManager.getLogger().debug(MODULE, "Identity Type: FASTREAUTH_IDENTITY_REQUEST");
		} else {
			isIdentityReqRequired = 1;
			LogManager.getLogger().debug(MODULE, "Unknown Identity Type.");
		}
	}
	
	protected void generateResponse(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - SIM Action for GenerateResponse, State : " + getCurrentState());
		SimEapType receivedSimEapType = (SimEapType)aaaEapRespData.getEapRespPacket().getEAPType();
		if(receivedSimEapType.getSimMessageType() == SIMMessageTypeConstants.SIM_START.Id){
			generateChallengeMessage(aaaEapRespData, provider);
		}else if(receivedSimEapType.getSimMessageType() == SIMMessageTypeConstants.SIM_CHALLENGE.Id){		
			setDone(true);			
		}else if(receivedSimEapType.getSimMessageType() == SIMMessageTypeConstants.NOTIFICATION.Id){
		}else if(receivedSimEapType.getSimMessageType() == SIMMessageTypeConstants.RE_AUTHENTICATION.Id){
		}
	}
	protected void generateStartMessage(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		SimEapType simEapType = new SimEapType();
		simEapType.setSimMessageType(SIMMessageTypeConstants.SIM_START.Id);
		AtVersionList atVersionListAttr= new AtVersionList();
		byte[] supportedVersionList= {0,1};
		atVersionListAttr.setSupportedVersionList(supportedVersionList);
		simEapType.setSimAttributes(atVersionListAttr.getBytes());
		this.atVersionListBytes = atVersionListAttr.getValueBytes();
		
		ISIMAttribute idReqAttr = null;
		if (iAt_ctr == getEapConfigurationContext().getMaxSIMReauthCount()) {
			idReqAttr = new AtFullAuthIdReq();
			simEapType.setSimAttributes(idReqAttr.getBytes());
			setReqEapType(simEapType);
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Generating Start message for: Full Auth ID Required because Fast Reauthentication limit: " 
										+ getEapConfigurationContext().getMaxSIMReauthCount() + " is reached");
			}
			iAt_ctr = 0;
		} else if (fastReauthID != null){

			idReqAttr = new AtAnyIdReq();
			simEapType.setSimAttributes(idReqAttr.getBytes());
			setReqEapType(simEapType);
			LogManager.getLogger().debug(MODULE, "Generating Start message for: Any ID Required");
		} else if (pseudonymID != null){
			idReqAttr = new AtFullAuthIdReq();
			simEapType.setSimAttributes(idReqAttr.getBytes());
			setReqEapType(simEapType);
			LogManager.getLogger().debug(MODULE, "Generating Start message for: Full Auth ID Required");
		} else if (!isStartGenerated){
			idReqAttr = new AtPermanentIdReq();
			simEapType.setSimAttributes(idReqAttr.getBytes());
			setReqEapType(simEapType);
			LogManager.getLogger().debug(MODULE, "Generating Start message for: Permanent ID required");
			isStartGenerated = true;
		} else {
			LogManager.getLogger().debug(MODULE, "Building failure message as SIM identity type was unknown");
			buildFailure(aaaEapRespData, provider);
		}
		
		
	}
		
	protected void actionOnRequestReceived(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		checkForIsIdReqRequired(aaaEapRespData, provider);
		validateSIMResponse(aaaEapRespData, provider);
	}

	protected void actionOnSimStartReceived(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		checkForIsIdReqRequired(aaaEapRespData, provider);
		validateSIMResponse(aaaEapRespData, provider);
	}

	protected void actionOnNAKReceived(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		//checkForIsIdReqRequired(aaaEapRespData, provider);
	}

	protected void actionOnResponseIdentityReceived(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		processResponseIdentity(aaaEapRespData, provider);
		//checkForIsIdReqRequired(aaaEapRespData, provider);
	}

	protected void generateChallengeMessage(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(MODULE, "Invoked generate challange message");

		SimEapType receivedSimEapType = (SimEapType)aaaEapRespData.getEapRespPacket().getEAPType();
		Collection<byte[]> simAttrs = receivedSimEapType.getSimAttributes();
		Iterator<byte[]> simAttrsItr = simAttrs.iterator();
		HashMap<String, ISIMAttribute> receivedAttributes = new HashMap<String, ISIMAttribute>();
		LogManager.getLogger().trace(getModuleName(), "received SIM Attributes length " + simAttrs.size());
		while(simAttrsItr.hasNext()){
			byte[] simAttribute = simAttrsItr.next();
			ISIMAttribute simAttribute2 = SIMAttributeDictionary.getInstance().getAttribute((simAttribute[0] & 0xFF));
			simAttribute2.setBytes(simAttribute);
			LogManager.getLogger().trace(getModuleName(), "received SIM Attribute : " + Utility.bytesToHex(simAttribute));
			LogManager.getLogger().trace(getModuleName(), "SIM Attribute : " + simAttribute2);
			receivedAttributes.put(SIMAttributeTypeConstants.getName(simAttribute[0] & 0xFF), simAttribute2);
		}
		String identity = null;
		String identityInHex = null;
		if ( receivedAttributes.get(SIMAttributeTypeConstants.AT_IDENTITY.name) != null ){
		try {
			identityInHex = Utility.bytesToHex(((AtIdentity)receivedAttributes.get(SIMAttributeTypeConstants.AT_IDENTITY.name)).getIdentity()).substring(2);
			identity = new String(((AtIdentity)receivedAttributes.get(SIMAttributeTypeConstants.AT_IDENTITY.name)).getIdentity(),CommonConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			identityInHex = Utility.bytesToHex(((AtIdentity)receivedAttributes.get(SIMAttributeTypeConstants.AT_IDENTITY.name)).getIdentity()).substring(2);
			identity = new String(((AtIdentity)receivedAttributes.get(SIMAttributeTypeConstants.AT_IDENTITY.name)).getIdentity());
		}
		}else{
			identity = getEAPResponseIdentity();
		}
		if (requestType == SimAkaIdentityType.PERMANENT_IDENTITY_REQUEST){
			permanentID = identity;
		} else if (requestType == SimAkaIdentityType.PSEUDONYM_IDENTITY_REQUEST){
			identity = permanentID;
			LogManager.getLogger().trace(MODULE, "Pseudonym Identity request received for: " + permanentID);
		}
		
		SimEapType simEapType = new SimEapType();

		switch (requestType){
		case PERMANENT_IDENTITY_REQUEST:
		case PSEUDONYM_IDENTITY_REQUEST:
		try{
			getTripletFromProfileDS(identity, provider);
			if (customerAccountInfo != null)
				setUserIdentity(customerAccountInfo.getUserIdentity());
		}catch(IllegalArgumentException iae){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Problem in provisioning the triplet in the DS. Reason : " + iae.getMessage());
			setSuccess(false);
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.SIM_FAILURE);
		}

		if(isFailure())
			return;

		
		calculateKeys(receivedAttributes);		
		simEapType.setSimMessageType(SIMMessageTypeConstants.SIM_CHALLENGE.Id);
		
		AtRand atRand = new AtRand();				
		atRand.setValueBytes(gsmTriplets.getRands());
			simEapType.setSimAttributes(atRand.getBytes());
			byte[] encrBytes = null;
			if (pseudonymIdMethod != 0 && pseudonymID == null){

				AtNextPseudonym pseudonym = new AtNextPseudonym();
				pseudonymID = getEapConfigurationContext().getTemporaryIdentityGenerator().encodeTemporaryIdentity(permanentID, pseudonymIdMethod, pseudonymPrefix, isPseudoHexEncoding);
				pseudonym.setNextPseudonym(pseudonymID);
				encrBytes = pseudonym.getBytes();
				LogManager.getLogger().trace(MODULE, "Sending pseudonym ID: "+ pseudonymID + " to: " + permanentID);
			}

			if (fastReauthIdMethod != 0){
				AtNextReAuthId reAuthId  = new AtNextReAuthId();
				fastReauthID = getEapConfigurationContext().getTemporaryIdentityGenerator().encodeTemporaryIdentity(permanentID, fastReauthIdMethod, fastReauthPrefix, isFastReauthHexEncoding);
				reAuthId.setNextReauthID(fastReauthID);
				encrBytes = TLSUtility.appendBytes(encrBytes, reAuthId.getBytes());
				LogManager.getLogger().trace(MODULE, "Sending Fast reauth ID: " + fastReauthID + " to: " + permanentID);
			}
		
			if (encrBytes != null) {
			AtIV atIV = new AtIV();
			atIV.setValueBytes(SIMUtility.generateRandom());
				simEapType.setSimAttributes(atIV.getBytes());
				AtEncrData atEncrData = new AtEncrData();
				atEncrData.encryptAndSetBytesForEncrData(k_encr, atIV, encrBytes);
			
				simEapType.setSimAttributes(atEncrData.getBytes());
			}
			break;
		case FASTREAUTH_IDENTITY_REQUEST:
			calculateKeysForFastReauth(receivedAttributes);
			simEapType.setSimMessageType(SIMMessageTypeConstants.RE_AUTHENTICATION.Id);
			
			AtNextReAuthId reAuthId  = new AtNextReAuthId();
			fastReauthID = getEapConfigurationContext().getTemporaryIdentityGenerator().encodeTemporaryIdentity(permanentID, fastReauthIdMethod, fastReauthPrefix, isFastReauthHexEncoding);
			reAuthId.setNextReauthID(fastReauthID);
			LogManager.getLogger().trace(MODULE, "Sending Fast reauth ID: " + fastReauthID + " to: " + permanentID);
			
			AtIV atIV = new AtIV();
			atIV.setValueBytes(SIMUtility.generateRandom());
			simEapType.setSimAttributes(atIV.getBytes());
			
			encrBytes =  at_ctr.getBytes();

			AtNonceS atNonse = new AtNonceS();
			atNonse.setValueBytes(nonce_s);
			encrBytes = TLSUtility.appendBytes(encrBytes, atNonse.getBytes());
			
			encrBytes = TLSUtility.appendBytes(encrBytes, reAuthId.getBytes());
			
			AtEncrData atEncrData = new AtEncrData();
			atEncrData.encryptAndSetBytesForEncrData(getK_encr(), atIV, encrBytes);
			simEapType.setSimAttributes(atEncrData.getBytes());
		}

		AtMac atMac = new AtMac();
		atMac.setValueBytes(new byte[16]);
		
		simEapType.setSimAttributes(atMac.getBytes());
		setReqEapType(simEapType);		
		LogManager.getLogger().trace(getModuleName(), "Generated SIM Challenge Message : " + simEapType.toString());
		prevStartMsgType = 0;
		isStartGenerated = false;
	}
	
	protected boolean checkForIsIdReqRequired(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - SIM Action for CheckForIsIdReqRequired, State : " + getCurrentState());		
		if (requestType == SimAkaIdentityType.UNKNOWN_IDENTITY)
			return true;
		return false;
	}

	protected void processResponseIdentity(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - SIM Action for processResponseIdentity, State : " + getCurrentState());
	}

	public ICustomerAccountInfo getCustomerAccountInfo() {
		return customerAccountInfo ;
	}

	public void setCustomerAccountInfo(ICustomerAccountInfo customerAccountInfo) {
		this.customerAccountInfo = customerAccountInfo;
	}

	public byte[] getIdentity() {
		return identity;
	}

	public void setIdentity(byte[] identity) {
		if(this.identity != null){
			this.identity = new byte[identity.length];
			System.arraycopy(identity, 0, this.identity, 0, identity.length);
		}else{
			this.identity = null;
		}
	}

	public int getIsIdentityReqRequired() {
		return isIdentityReqRequired;
	}

	public void setIdentityReqRequiredTo(int isIdentityReqRequired) {
		this.isIdentityReqRequired = isIdentityReqRequired;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	
	public void calculateKeys(HashMap<String, ISIMAttribute> receivedAttributes){
		
		//Generation of MK = SHA1(Identity|n*Kc| NONCE_MT| Version List| Selected Version)

		MessageDigest md5MessageDigest = Utility.getMessageDigest(HashAlgorithm.SHA1.getIdentifier());

		if(receivedAttributes.containsKey(SIMAttributeTypeConstants.AT_IDENTITY.name)){			
			md5MessageDigest.update(((AtIdentity)receivedAttributes.get(SIMAttributeTypeConstants.AT_IDENTITY.name)).getIdentity());
		} else
			LogManager.getLogger().trace(getModuleName(), SIMAttributeTypeConstants.AT_IDENTITY.name + " not found");
		LogManager.getLogger().trace(getModuleName(), "User Identity : " + TLSUtility.bytesToHex(((AtIdentity)receivedAttributes.get(SIMAttributeTypeConstants.AT_IDENTITY.name)).getIdentity()));
//		md5MessageDigest.update(TLSUtility.HexToBytes("0x70616e6b6974"));			
		
		LogManager.getLogger().trace(getModuleName(), "Kcs Used : " + TLSUtility.bytesToHex(gsmTriplets.getKcs()));
		md5MessageDigest.update(gsmTriplets.getKcs());
		
		if(receivedAttributes.containsKey(SIMAttributeTypeConstants.AT_NONCE_MT.name))
			md5MessageDigest.update( receivedAttributes.get(SIMAttributeTypeConstants.AT_NONCE_MT.name).getValueBytes());
		else
			LogManager.getLogger().trace(getModuleName(), SIMAttributeTypeConstants.AT_NONCE_MT.name + " not found");
//		md5MessageDigest.update(TLSUtility.HexToBytes("0x4545ef027b8d04004645ef027b8d0400"));
		
		md5MessageDigest.update(this.atVersionListBytes);
		
		if(receivedAttributes.containsKey(SIMAttributeTypeConstants.AT_SELECTED_VERSION.name))
			md5MessageDigest.update(receivedAttributes.get(SIMAttributeTypeConstants.AT_SELECTED_VERSION.name).getReservedBytes());
		else
			LogManager.getLogger().trace(getModuleName(), SIMAttributeTypeConstants.AT_SELECTED_VERSION.name + " not found");
		
		byte [] mk = md5MessageDigest.digest();
		LogManager.getLogger().trace(getModuleName(), "Master Key : " + TLSUtility.bytesToHex(mk));
		byte [] k_aut= new byte[16];
		byte [] k_encr= new byte[16];
		byte [] msk= new byte[64];
		byte [] emsk = new byte[64];
		byte[] keysTotalBytes = SIMUtility.SimPRF(mk);
		System.arraycopy(keysTotalBytes, 0,k_encr,0, 16);
		System.arraycopy(keysTotalBytes,16, k_aut,0, 16);
		System.arraycopy(keysTotalBytes,32,  msk ,0, 64);
		System.arraycopy(keysTotalBytes,96,  emsk,0, 64);
		
		setMk(mk);
		setK_aut(k_aut);
		setK_encr(k_encr);
		setMsk(msk);
		setEmsk(emsk);		
		LogManager.getLogger().trace(getModuleName(), "Kauth  : = " + TLSUtility.bytesToHex(k_aut));
		LogManager.getLogger().trace(getModuleName(), "K_Encr : = " + TLSUtility.bytesToHex(k_encr));
		LogManager.getLogger().trace(getModuleName(), "msk    : = " + TLSUtility.bytesToHex(msk));
		LogManager.getLogger().trace(getModuleName(), "emsk   : = " + TLSUtility.bytesToHex(emsk));
		
	}
	
	public void calculateKeysForFastReauth(HashMap<String, ISIMAttribute> receivedAttributes){
	
		LogManager.getLogger().trace(getModuleName(), "Master Key : " + TLSUtility.bytesToHex(getMk()));
		nonce_s = SIMUtility.generateRandom();
		LogManager.getLogger().trace(getModuleName(), "Generated Nonce_S : " + TLSUtility.bytesToHex(nonce_s));
		iAt_ctr++;
		if (at_ctr == null){
			at_ctr = new AtCounter();
		}
		at_ctr.setCounter(iAt_ctr);
		byte [] msk= new byte[64];
		byte [] emsk = new byte[64];
		
		if (receivedAttributes.containsKey(SIMAttributeTypeConstants.AT_IDENTITY.name)) {
			identity = ((AtIdentity)receivedAttributes.get(SIMAttributeTypeConstants.AT_IDENTITY.name)).getIdentity();
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Fast Re-auth Identity: " + TLSUtility.bytesToHex(identity));
			}
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Fast Re-auth Identity not received");
			}
		}

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "AT Counter: " + TLSUtility.bytesToHex(at_ctr.getReservedBytes()));
		}

		byte[] xkey = identity;
		xkey = TLSUtility.appendBytes(xkey, at_ctr.getReservedBytes());
		xkey = TLSUtility.appendBytes(xkey, nonce_s);
		xkey = TLSUtility.appendBytes(xkey, mk);
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "XKEY: " + TLSUtility.bytesToHex(xkey));
		}
		try {
			MessageDigest msgDigest = MessageDigest.getInstance(SHA);
			msgDigest.update(xkey);
			xkey = msgDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			// will never occur as the specified security algorithm is SHA and that will
			// always be provided by security provider
		}
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "XKEY': " + TLSUtility.bytesToHex(xkey));
		}

		byte[] keysTotalBytes = SIMUtility.SimPRF(xkey);
		
		System.arraycopy(keysTotalBytes,0,  msk ,0, 64);
		System.arraycopy(keysTotalBytes,64,  emsk,0, 64);
		
		setMsk(msk);
		setEmsk(emsk);		
		LogManager.getLogger().trace(getModuleName(), "Kauth  : = " + TLSUtility.bytesToHex(getK_aut()));
		LogManager.getLogger().trace(getModuleName(), "K_Encr : = " + TLSUtility.bytesToHex(getK_encr()));
		LogManager.getLogger().trace(getModuleName(), "msk    : = " + TLSUtility.bytesToHex(msk));
		LogManager.getLogger().trace(getModuleName(), "emsk   : = " + TLSUtility.bytesToHex(emsk));
		
	}
	
	abstract public String getModuleName();
	
	public byte[] getMk() {
		return mk;
	}

	public void setMk(byte[] mk) {
		this.mk = mk;
	}

	public byte[] getTek() {
		return tek;
	}

	public void setTek(byte[] tek) {
		this.tek = tek;
	}

	public byte[] getMsk() {
		return msk;
	}

	public void setMsk(byte[] msk) {
		this.msk = msk;
	}

	public byte[] getEmsk() {
		return emsk;
	}

	public void setEmsk(byte[] emsk) {
		this.emsk = emsk;
	}

	public byte[] getK_aut() {
		return k_aut;
	}

	public void setK_aut(byte[] k_aut) {
		this.k_aut = k_aut;
	}

	public byte[] getK_encr() {
		return k_encr;
	}

	public void setK_encr(byte[] k_encr) {
		this.k_encr = k_encr;
	}

	public GsmTriplets getGsmTriplets() {
		return gsmTriplets;
	}

	public void setGsmTriplets(GsmTriplets gsmTriplets) {
		this.gsmTriplets = gsmTriplets;
	}

	public byte[] getKey() {
		return getMsk();
	}

	private void getTripletFromProfileDS(String identity, ICustomerAccountInfoProvider provider){

		ICustomerAccountInfo customerAccountInfo = provider.getCustomerAccountInfo(identity);
		if(customerAccountInfo != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(getModuleName(), "User Profile: " + customerAccountInfo);
			setCustomerAccountInfo(customerAccountInfo);	
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().debug(getModuleName(), "Customer profile cannot be located, considering EAP-SIM failure.");
			setSuccess(false);
			setFailure(true);
			setFailureReason(EAPFailureReasonConstants.USER_NOT_FOUND);
			setDone(true);
			return;
		}


		byte[] key   = null;

		int gsmA3A8AlgoCode = 0;
		String password = customerAccountInfo.getPassword().trim();
		if(password != null && password.length() > 0){
			StringTokenizer stkTokenizer = new StringTokenizer(password,":");			
			if(stkTokenizer.countTokens() > 1){
				String gsmA3A8Algo = stkTokenizer.nextToken();				
				try{
					gsmA3A8AlgoCode = Integer.parseInt(gsmA3A8Algo);					
				}catch(Exception e){
					// Do nothing.
				}

				//Check to see that if A3A8 Algorithm should not be zero. As we have consider 0 (zero) as 
				//the just not calculating triplets and just putting the value from the password filed.

					if(!GsmA3A8AlgoConstants.isValid(gsmA3A8AlgoCode)){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(getModuleName(), "Invalid GSMA3A8 Algo into profile");
						setSuccess(false);
						setFailure(true);
						setDone(true);
						setFailureReason(EAPFailureReasonConstants.SIM_FAILURE);	
						return;							
					}

				int cntr=0;
				if(gsmA3A8AlgoCode==0){
					
					gsmTriplets = new GsmTriplets();
					
					while(stkTokenizer.hasMoreTokens()){
						String simTriplet = stkTokenizer.nextToken();
						StringTokenizer st = new StringTokenizer(simTriplet,",");
						byte []rands = null; 
						byte [] kc   = null;
						byte[] sres = null;
						if (st.hasMoreTokens()) {
						rands=TLSUtility.HexToBytes(st.nextToken());
						} else {
							throw new IllegalArgumentException("RAND not configured for: " + identity);
						}
						if (st.hasMoreTokens()) {
						sres=TLSUtility.HexToBytes(st.nextToken());
						} else { 
							throw new IllegalArgumentException("SRES not configured for: " + identity);
						}
						if (st.hasMoreTokens()) {
						kc=TLSUtility.HexToBytes(st.nextToken());
						} else {
							throw new IllegalArgumentException("KC not configured for: " + identity);
						}
						GsmTriplet triplet = new GsmTriplet(rands,sres,kc);
						gsmTriplets.addGsmTriplet(triplet);
						cntr++;
					}
					
					if(cntr < 2)
						throw new IllegalArgumentException("Insufficient Number of Triplets Received from DS. Reason: Minimum 2 Required. Provisioned: " + cntr);
				}else{
					int noOfTriplet = 0;
					if (stkTokenizer.hasMoreTokens()){
						try {
							noOfTriplet = Integer.parseInt(stkTokenizer.nextToken());
						} catch (NumberFormatException e){
							throw new IllegalArgumentException("Number of triplets is not defined");
						}
					} else {
						LogManager.getLogger().warn(MODULE, "Number of triplets is not defined");
					}
					if (noOfTriplet < 1 || noOfTriplet > 10){
						throw new IllegalArgumentException("Number of triplets can not be less than 1 and greater than 10");
					}
					String simKey;
					if(stkTokenizer.hasMoreTokens()){
						simKey = stkTokenizer.nextToken();
					LogManager.getLogger().info(getModuleName(), "Sim Key : " + simKey);
					} else {
						throw new IllegalArgumentException("Sim key not configured for identity: " + identity);
					}

					key = TLSUtility.HexToBytes(simKey);
					byte[] rands = SIMUtility.generateRandom(noOfTriplet); 
					byte[] operatorVariant = null;
					if(stkTokenizer.hasMoreTokens()){
						String simOperatorVariant = stkTokenizer.nextToken();
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(getModuleName(), "Operator Variant : " + simOperatorVariant);
						operatorVariant = TLSUtility.HexToBytes(simOperatorVariant);
					}else{
						LogManager.getLogger().debug(getModuleName(), "Operator Variant is not present into profile");
					}

					//Calculating the GSM Triplets.
					try {
						calculateGsmTriplets(key,operatorVariant,rands,gsmA3A8AlgoCode, noOfTriplet);
					} catch (GeneralSecurityException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "Problem in generating Triplets, Reason : " + e.getMessage());
						setSuccess(false);
						setFailure(true);
						setDone(true);
						setFailureReason(EAPFailureReasonConstants.SIM_FAILURE);
						return;
					}
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModuleName(), "GSMA3A8 Algo or SIM Key is not present into profile");
				setSuccess(false);
				setFailure(true);
				setDone(true);
				setFailureReason(EAPFailureReasonConstants.SIM_FAILURE);		
				return;				
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(getModuleName(), "Password is blank, EAP-SIM can not be proceed");
			setSuccess(false);
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.SIM_FAILURE);		
			return;
		}
	}
	
	@Override
	public String getUserIdentity(){
		return authenticatedUserIdentity;
	}
	private void setUserIdentity(String userIdentity){
		this.authenticatedUserIdentity = userIdentity;
	}
	
	@Override
	public String[] getSessionIdentities() {
		return new String[]{pseudonymID, fastReauthID};
	}
	
/*	public static void main(String[] args) {
		calculateKeys(null);
	}
*/

}

	

