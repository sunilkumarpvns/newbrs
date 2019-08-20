package com.elitecore.coreeap.fsm.eap.method.aka;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

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
import com.elitecore.coreeap.data.aka.AKAQuintet;
import com.elitecore.coreeap.fsm.EAPFailureReasonConstants;
import com.elitecore.coreeap.fsm.eap.method.BaseMethodStateMachine;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.types.IdentityEAPType;
import com.elitecore.coreeap.packet.types.aka.AkaEapType;
import com.elitecore.coreeap.packet.types.sim.SIMAttributeDictionary;
import com.elitecore.coreeap.packet.types.sim.attributes.ISIMAttribute;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtAutn;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtAuts;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtCheckCode;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtFullAuthIdReq;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtIdentity;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtMac;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtRand;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtRes;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.aka.quintet.AkaQuintetDictionary;
import com.elitecore.coreeap.util.aka.quintet.IAkaQuintet;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.aka.AkaQuintetAlgoConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.actions.AkaActions;
import com.elitecore.coreeap.util.constants.fsm.events.AkaEvents;
import com.elitecore.coreeap.util.constants.fsm.states.AkaStates;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.constants.sim.message.SIMMessageTypeConstants;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.sim.SIMUtility;
import com.elitecore.coreeap.util.tls.TLSUtility;

public abstract class BaseAKAMethodStateMachine extends BaseMethodStateMachine {
	
	private ICustomerAccountInfo customerAccountInfo = null;
	private byte[] identity = null;
	private byte[] mk = null;
	private byte[] tek,msk,emsk= null;
	private byte[] k_aut = null;
	private byte[] k_encr = null;
	private byte[] sqn = null;
	private byte[] amf = null;
	private byte[] rands = null;
	private AKAQuintet quintet = null;
	private byte[] totalAKARoundTripBytes;
	public static final int PROFILE_AS_TRIPLETDS=0;
	public static final int ULTICOM_AS_TRIPLETDS=1;
	/***
	 * isIdentityReqRequired : is identity request required in start message
	 *  0 = identity request is not required 
	 *  1 = Any identity request is required
	 *  2 = Permanent Identity request is required
	 *  3 = Full auth Identity request is required
	 */
	private int isIdentityReqRequired = 0;
	private String failureReason ;
//	private static final int ANY_IDENTITY_REQUEST = 1;
//	private static final int PERMANENT_IDENTITY_REQUEST = 2;
//	private static final int FULL_AUTH_IDENTITY_REQUEST = 3;
	private String authenticatedUserIdentity = null; 
	
	public BaseAKAMethodStateMachine(
			IEapConfigurationContext eapConfigurationContext) {
		super(eapConfigurationContext);
		changeCurrentState(AkaStates.INITIALIZED);
	}

	@Override
	public void reset() {
		super.reset();
		identity = null;
		mk = null;
		tek =null;
		msk = null;
		emsk= null;
		k_aut = null;
		k_encr = null;
		isIdentityReqRequired = 0;
		failureReason = null;		
		amf = new byte[2];
		sqn = new byte[]{0,0,0,0,1,0};
		totalAKARoundTripBytes = null;
	}
	
	public byte[] getTotalAKARoundTripBytes() {
		return totalAKARoundTripBytes;
	}

	public void setTotalAKARoundTripBytes(byte[] totalAKARoundTripBytes) {
		this.totalAKARoundTripBytes = totalAKARoundTripBytes;
	}

	abstract public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException;
	
	@Override
	public IEnum[] getActionList(IEnum event) {
		AkaActions[] actionList = null;
		switch((AkaEvents)event){
		case AkaInvalidRequest:
			actionList = new AkaActions[2];
			actionList[0] = AkaActions.DiscardRequest;
			actionList[1] = AkaActions.BuildFailure;
			break;
		case AkaNAKReceived:
			actionList = new AkaActions[2];
			actionList[0] = AkaActions.CheckForIsIdReqRequired;
			actionList[1] = AkaActions.GenerateStart;
			break;
		case AkaRequestReceived:
			actionList = new AkaActions[3];
			actionList[0] = AkaActions.CheckForIsIdReqRequired;
			actionList[1] = AkaActions.Validate_Response;
			actionList[2] = AkaActions.GenerateResponse;
			break;
		case AkaSynchronizationFailureReceived:
			actionList = new AkaActions[1];
			actionList[0] = AkaActions.Resynchronize;
			break;
		case AkaResponseIdentityReceived:
			actionList = new AkaActions[3];
			actionList[0] = AkaActions.ProcessResponseIdentity;
			actionList[1] = AkaActions.CheckForIsIdReqRequired;
			actionList[2] = AkaActions.GenerateStart;
			break;			
		case AkaUnconditionalEvent:
			actionList = new AkaActions[2];
			actionList[0] = AkaActions.DiscardRequest;
			actionList[1] = AkaActions.BuildFailure;
			break;		
		case AkaFailure:
			actionList = new AkaActions[1];
			actionList[0] = AkaActions.BuildFailure;
			break;
		case AkaSuccess:
			actionList = new AkaActions[1];
			actionList[0] = AkaActions.BuildSuccess;
			break;
		}
		return actionList;
	}

	@Override
	public IEnum getEvent(AAAEapRespData aaaEapRespData) {
		EAPPacket respPacket = aaaEapRespData.getEapRespPacket();
		
		if(respPacket.getEAPType().getType() == EapTypeConstants.IDENTITY.typeId){
			return AkaEvents.AkaResponseIdentityReceived;
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.NAK.typeId){
			return AkaEvents.AkaNAKReceived;
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.AKA.typeId){
			AkaEapType simEapType = (AkaEapType)respPacket.getEAPType();
			if (simEapType.getAkaMessageType()== SIMMessageTypeConstants.AKA_SYNCHRONIZATION_FAILURE.Id) {
				return AkaEvents.AkaSynchronizationFailureReceived;
			} else if (simEapType.getAkaMessageType()== SIMMessageTypeConstants.CLIENT_ERROR.Id
						|| simEapType.getAkaMessageType()== SIMMessageTypeConstants.AKA_AUTHENTICATION_REJECT.Id) {
				return AkaEvents.AkaFailure;
			} else {
				return AkaEvents.AkaRequestReceived;			
			}
		}else if(getCurrentState() == AkaStates.INITIALIZED 
					|| getCurrentState() == AkaStates.SUCCESS_BUILDED 
					|| getCurrentState() == AkaStates.FAILURE_BUILDED
					|| getCurrentState() == AkaStates.CHALLENGE_GENERATE){
				return AkaEvents.AkaRequestReceived;			
		}else if(!isValidRequest() && !isSuccess()){
			return AkaEvents.AkaInvalidRequest;
		}else if(isValidRequest() && isSuccess() && getCurrentState() == AkaStates.CHALLENGE_GENERATE){
			return AkaEvents.AkaSuccess;
		}else if(isValidRequest() && isFailure() && getCurrentState() == AkaStates.CHALLENGE_GENERATE){
			return AkaEvents.AkaFailure;	
		}else{
			return AkaEvents.AkaUnconditionalEvent;
		}														
	}
	public IEnum getNextState(IEnum event){
		switch((AkaEvents)event){
		case AkaFailure:
			return AkaStates.FAILURE_BUILDED;
		case AkaInvalidRequest:
			return AkaStates.FAILURE_BUILDED;
		case AkaNAKReceived:
			return AkaStates.START_GENERATED;
		case AkaRequestReceived:
			return AkaStates.CHALLENGE_GENERATE;
		case AkaResponseIdentityReceived:
			return AkaStates.START_GENERATED;
		case AkaSuccess:
			return AkaStates.SUCCESS_BUILDED;
		case AkaUnconditionalEvent:
			return AkaStates.DISCARDED;
		default:
			break;
		}
		return null;
	}
	
	public void parseAKAResp(AAAEapRespData aaaEapRespData){
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		if(eapPacket != null){
			if(eapPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
				setIdentifier(eapPacket.getIdentifier());
				if(eapPacket.getEAPType().getType() == EapTypeConstants.AKA.typeId){
					AkaEapType akaEapType = (AkaEapType)eapPacket.getEAPType();				
					if(akaEapType.getAkaMessageType()== SIMMessageTypeConstants.AKA_CHALLENGE.Id
							|| akaEapType.getAkaMessageType()== SIMMessageTypeConstants.AKA_AUTHENTICATION_REJECT.Id
							|| akaEapType.getAkaMessageType()== SIMMessageTypeConstants.AKA_SYNCHRONIZATION_FAILURE.Id
							|| akaEapType.getAkaMessageType()== SIMMessageTypeConstants.AKA_IDENTITY.Id
							|| akaEapType.getAkaMessageType()== SIMMessageTypeConstants.NOTIFICATION.Id
							|| akaEapType.getAkaMessageType()== SIMMessageTypeConstants.RE_AUTHENTICATION.Id
							|| akaEapType.getAkaMessageType()== SIMMessageTypeConstants.CLIENT_ERROR.Id){
						setValidRequest(true);
						return;
					}
				}								
			}
		}
		setValidRequest(false);
	}

	protected void discardRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider){
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA Action for DiscardRequest, State : " + getCurrentState());
		setSuccess(false);
		setDone(false);
	}

	protected void buildSuccess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA Action for BuildSuccess, State : " + getCurrentState());
		setSuccess(true);
		setFailure(false);
		setDone(true);
		LogManager.getLogger().trace(getModuleName(), "Done : " + isDone());
	}
	
	protected void buildFailure(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA Action for BuildFailure, State : " + getCurrentState());
		setFailure(true);
		setSuccess(false);
		setDone(true);
		setFailureReason(EAPFailureReasonConstants.INVALID_AKA_PASSWORD);
	}	
	
	
	protected void validateAKAResponse(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA Action for ValidateAKAResponse, State : " + getCurrentState());
		byte[] receivedMAC = null;		
		AkaEapType receivedAkaEapType = (AkaEapType)aaaEapRespData.getEapRespPacket().getEAPType();
		if(receivedAkaEapType.getAkaMessageType() == SIMMessageTypeConstants.AKA_CHALLENGE.Id){
			List<ISIMAttribute> akaAttrList = receivedAkaEapType.getAkaAttrList();
			for(ISIMAttribute akaAttribute : akaAttrList){
				if(akaAttribute.getType() == SIMAttributeTypeConstants.AT_MAC.Id){					
					receivedMAC = ((AtMac)akaAttribute).getMAC();
				}
			}
			
			//validate mac
			EAPPacket eapPacket = null;
			try {
				eapPacket = (EAPPacket) aaaEapRespData.getEapRespPacket().clone();
			} catch (CloneNotSupportedException e) {
				LogManager.getLogger().trace(getModuleName(), "EAP Packet clonning failed, Reason : " + e.getMessage());
				LogManager.getLogger().trace(getModuleName(), e);
			}
			AkaEapType simEapType = (AkaEapType)eapPacket.getEAPType();
			List<ISIMAttribute> akaAttributeList = simEapType.getAkaAttrList();
			for(ISIMAttribute akaAttribute : akaAttributeList){
				if(akaAttribute.getType() == SIMAttributeTypeConstants.AT_MAC.Id){					
					((AtMac)akaAttribute).setValueBytes(new byte[16]);
				}
			}
			simEapType.setAkaAttrList(akaAttributeList);			
			byte [] macBytes = eapPacket.getBytes();
			LogManager.getLogger().trace(getModuleName(), "MAC Input: " +TLSUtility.bytesToHex(macBytes));
			macBytes = TLSUtility.HMAC("SHA-1", macBytes, getK_aut());
			

			byte []finalMacBytes=new byte[16];
			System.arraycopy(macBytes, 0, finalMacBytes,0, 16);
			LogManager.getLogger().trace(getModuleName(), "Received MAC : " +TLSUtility.bytesToHex(receivedMAC));
			LogManager.getLogger().trace(getModuleName(), "Calculated MAC : " +TLSUtility.bytesToHex(finalMacBytes));
			LogManager.getLogger().trace(getModuleName(), "MAC is " + (Arrays.equals(receivedMAC, finalMacBytes) ?"match": "not match"));
			if(Arrays.equals(receivedMAC, finalMacBytes)){
				setSuccess(true);
				setFailure(false);
				setDone(true);
				
			}else{
				setFailure(true);
				setSuccess(false);
				setDone(true);
			}						
			
			//checking for AT_RES
			byte[] receivedResBytes = null;
			akaAttrList = receivedAkaEapType.getAkaAttrList();
			for(ISIMAttribute akaAttribute : akaAttrList){
				if(akaAttribute.getType() == SIMAttributeTypeConstants.AT_RES.Id){					
					receivedResBytes = ((AtRes)akaAttribute).getRes();
				}
			}

			LogManager.getLogger().trace(getModuleName(), "Received "+ SIMAttributeTypeConstants.getName(SIMAttributeTypeConstants.AT_RES.Id)+" : " +TLSUtility.bytesToHex(receivedResBytes));
			LogManager.getLogger().trace(getModuleName(), "Generated "+ SIMAttributeTypeConstants.getName(SIMAttributeTypeConstants.AT_RES.Id)+" : " +TLSUtility.bytesToHex(quintet.getXres()));
			LogManager.getLogger().trace(getModuleName(), SIMAttributeTypeConstants.getName(SIMAttributeTypeConstants.AT_RES.Id)+" is " + (Arrays.equals(receivedResBytes, quintet.getXres()) ?"match": "not match"));
			if(Arrays.equals(receivedResBytes, quintet.getXres())){
				setSuccess(true);
				setFailure(false);
				setDone(true);
				
			}else{
				setFailure(true);
				setSuccess(false);
				setDone(true);
			}						

			//checking for AT_CHECKCODE
			byte[] receivedCheckcodeBytes = null;
			akaAttrList = receivedAkaEapType.getAkaAttrList();
			for(ISIMAttribute akaAttribute : akaAttrList){
				if(akaAttribute.getType() == SIMAttributeTypeConstants.AT_CHECKCODE.Id){					
					receivedCheckcodeBytes = ((AtCheckCode)akaAttribute).getCheckCode();
				}
			}
			if(receivedCheckcodeBytes != null && receivedCheckcodeBytes.length == 20){
				byte[] generateCheckcodeBytes = null;			
				try {
					MessageDigest digest = MessageDigest.getInstance("SHA-1");
					digest.update(getTotalAKARoundTripBytes());
					generateCheckcodeBytes = digest.digest();
					if(Arrays.equals(receivedCheckcodeBytes, generateCheckcodeBytes)){
						setSuccess(true);
						setFailure(false);
						setDone(true);
					}else{
						LogManager.getLogger().info(getModuleName(), "Received "+ SIMAttributeTypeConstants.getName(SIMAttributeTypeConstants.AT_CHECKCODE.Id)+" : " +TLSUtility.bytesToHex(receivedCheckcodeBytes));
						LogManager.getLogger().info(getModuleName(), "Generated "+ SIMAttributeTypeConstants.getName(SIMAttributeTypeConstants.AT_CHECKCODE.Id)+" : " +TLSUtility.bytesToHex(generateCheckcodeBytes));
						LogManager.getLogger().warn(getModuleName(), SIMAttributeTypeConstants.getName(SIMAttributeTypeConstants.AT_CHECKCODE.Id)+" is " + (Arrays.equals(receivedCheckcodeBytes, generateCheckcodeBytes) ?"match": "not match"));
						setFailure(true);
						setSuccess(false);
						setDone(true);
					}
				} catch (NoSuchAlgorithmException e) { 
					setSuccess(true);
					setFailure(false);
					setDone(true);
					ignoreTrace(e);
				}
			}
		}
		
	}

	protected void generateResponse(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA Action for GenerateResponse, State : " + getCurrentState());
		AkaEapType receivedAkaEapType = (AkaEapType)aaaEapRespData.getEapRespPacket().getEAPType();
		if(receivedAkaEapType.getAkaMessageType() == SIMMessageTypeConstants.AKA_IDENTITY.Id){
			generateChallengeMessage(aaaEapRespData, provider);
		}else if(receivedAkaEapType.getAkaMessageType() == SIMMessageTypeConstants.AKA_CHALLENGE.Id){		
			setDone(true);			
		}else if(receivedAkaEapType.getAkaMessageType() == SIMMessageTypeConstants.NOTIFICATION.Id){
		}else if(receivedAkaEapType.getAkaMessageType() == SIMMessageTypeConstants.RE_AUTHENTICATION.Id){
		}
	}
	protected void generateIdentityOrChallengeMessage(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		// In future if we include any other attribute then AT_PERMANENT_ID_REQ, AT_FULLAUTH_ID_REQ, or AT_ANY_ID_REQ 
		// then we MUST include AT_CHECKCODE in AKA-Response for RFC compliance
		
		EAPPacket respPacket = aaaEapRespData.getEapRespPacket();
		byte[] eapResponseIdentity = ((IdentityEAPType)respPacket.getEAPType()).getIdentity();
		String identity ;
		try{
			identity = new String(eapResponseIdentity, CommonConstants.UTF8); 
		}catch(UnsupportedEncodingException e){ 
			identity = new String(eapResponseIdentity);
			ignoreTrace(e);
		} 
		if(identity.startsWith("0")) {
			getQuintetAndGenerateAKAChallenge(provider, eapResponseIdentity);
			setIdentity(eapResponseIdentity);
		} else {
			AkaEapType akaEapType = new AkaEapType();
			akaEapType.setAkaMessageType(SIMMessageTypeConstants.AKA_IDENTITY.Id);
			AtFullAuthIdReq atFullAuthIdReq  = new AtFullAuthIdReq();		
			akaEapType.setAkaAttributes(atFullAuthIdReq.getBytes());
			setReqEapType(akaEapType);
		}
	}

	protected void generateChallengeMessage(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		
		AkaEapType receivedAkaEapType = (AkaEapType)aaaEapRespData.getEapRespPacket().getEAPType();
		
		Collection<byte[]> akaAttrs = receivedAkaEapType.getAkaAttributes();
		Iterator<byte[]> akaAttrsItr = akaAttrs.iterator();
		HashMap<String, ISIMAttribute> receivedAttributes = new HashMap<String, ISIMAttribute>();
		LogManager.getLogger().trace(getModuleName(), "received AKA Attributes length " + akaAttrs.size());
		while(akaAttrsItr.hasNext()){
			byte[] akaAttribute = akaAttrsItr.next();
			ISIMAttribute akaAttribute2 = SIMAttributeDictionary.getInstance().getAttribute((akaAttribute[0] & 0xFF));
			akaAttribute2.setBytes(akaAttribute);
			LogManager.getLogger().trace(getModuleName(), "received AKA Attribute : " + Utility.bytesToHex(akaAttribute));
			LogManager.getLogger().trace(getModuleName(), "AKA Attribute : " + akaAttribute2);
			receivedAttributes.put(SIMAttributeTypeConstants.getName(akaAttribute[0]), akaAttribute2);
		}
		
		if(!receivedAttributes.containsKey(SIMAttributeTypeConstants.AT_IDENTITY.name)) {
			LogManager.getLogger().trace(getModuleName(), SIMAttributeTypeConstants.AT_IDENTITY.name + " not found");
		}
				
		byte[] identityBytes = ((AtIdentity)receivedAttributes.get(SIMAttributeTypeConstants.AT_IDENTITY.name)).getIdentity();
		setIdentity(identityBytes);	
		getQuintetAndGenerateAKAChallenge(provider, identityBytes);
	}

	private void getQuintetAndGenerateAKAChallenge(ICustomerAccountInfoProvider provider, byte[] identityBytes) {
		String identity;
		try {
			identity = new String(identityBytes,CommonConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			identity = new String(identityBytes);
		}
		
		try{
			
			getQuintetFromProfileDS(identity, provider);
			if (customerAccountInfo != null)
				setUserIdentity(customerAccountInfo.getUserIdentity());
		}catch(IllegalArgumentException iae){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(getModuleName(), "Problem in provisioning the Quintet in the DS. Reason : " + iae.getMessage());
			setSuccess(false);
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.AKA_FAILURE);
		}

		if(isFailure())
			return;
		
		
	   /*
		 * need to include an AT_MAC attribute so that it will get
		 * calculated. The NONCE_MT and the MAC are both 16 bytes, so
		 * we store the NONCE_MT in the MAC for the encoder, which
		 * will pull it out before it does the operation.
		 */
		buildAkaChallenge(identityBytes);
	}
	
	protected void resynchronizeRequest(AAAEapRespData aaaEapRespData, ICustomerAccountInfoProvider provider) {
		
		HashMap<String, ISIMAttribute> receivedAkaAttributes = fetchAkaAttributesReceivedFrom(aaaEapRespData);
		byte[] autsBytes = ((AtAuts)receivedAkaAttributes.get(SIMAttributeTypeConstants.AT_AUTS.name)).getAuts();
		
		String userIdentity = getUserIdentityForResynchronization();
		
		regenerateQuintet(autsBytes, userIdentity, provider);
		
		if (isFailure()) {
			return;
		}
		
		byte[] identityBytes = getIdentity();
		if (identityBytes == null) {
			identityBytes = userIdentity.getBytes();
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(getModuleName(), "Identity bytes not found from the previous handshake. "
						+ "So, using identity: " + TLSUtility.bytesToHex(identityBytes) + "to calculate keys which is used to fetch profile");
			}
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(getModuleName(), "Using identity: " + TLSUtility.bytesToHex(identityBytes) 
				+ " to calculate keys which is found from the previous handshake");
			}
		}
		
		buildAkaChallenge(identityBytes);
	}

	private String getUserIdentityForResynchronization() {
		String userIdentity = null;
		
		if (getIdentity() != null) {
			try {
				userIdentity = new String(getIdentity(),CommonConstants.UTF8);
			} catch (UnsupportedEncodingException e) { 
				userIdentity = new String(getIdentity());
				ignoreTrace(e);
			}
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(getModuleName(), "Successfully fetched user identity: " + userIdentity + " from previous handshake for resynchronization");
			}
			
		} 
		
		if (userIdentity == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(getModuleName(), "Unable to fetch identity from previous handshake. So, fetching from user identity");
			}
			userIdentity = getUserIdentity();
		}
		
		if (userIdentity == null) {
			if (customerAccountInfo != null) {
				userIdentity = customerAccountInfo.getUserIdentity();
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(getModuleName(), "Not found in user identity directly so fetched user identity from customer acct info, id: " + userIdentity);
				}
				
				if (userIdentity == null) {
					userIdentity = customerAccountInfo.getUserName();
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(getModuleName(), "Not found user identity from customer acct info so fetched username from the same, id: " + userIdentity);
					}
				}
			} 
		}
		
		if (userIdentity == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(getModuleName(), "Unable to fetch username from customer acct info. So, remained blank");
			}
			userIdentity = "";
		} 
		
		return userIdentity;
	}

	private HashMap<String, ISIMAttribute> fetchAkaAttributesReceivedFrom(AAAEapRespData aaaEapRespData) {
		HashMap<String, ISIMAttribute> receivedAttributes = new HashMap<String, ISIMAttribute>();
		
		AkaEapType receivedAkaEapType = (AkaEapType)aaaEapRespData.getEapRespPacket().getEAPType();
		Collection<byte[]> akaAttrs = receivedAkaEapType.getAkaAttributes();
		Iterator<byte[]> akaAttrsItr = akaAttrs.iterator();
		LogManager.getLogger().trace(getModuleName(), "received AKA Attributes length " + akaAttrs.size());
		while(akaAttrsItr.hasNext()){
			byte[] akaAttribute = akaAttrsItr.next();
			ISIMAttribute akaAttribute2 = SIMAttributeDictionary.getInstance().getAttribute((akaAttribute[0] & 0xFF));
			akaAttribute2.setBytes(akaAttribute);
			LogManager.getLogger().trace(getModuleName(), "received AKA Attribute : " + Utility.bytesToHex(akaAttribute));
			LogManager.getLogger().trace(getModuleName(), "AKA Attribute : " + akaAttribute2);
			receivedAttributes.put(SIMAttributeTypeConstants.getName(akaAttribute[0]), akaAttribute2);
		}
		return receivedAttributes;
	}
	
	private void regenerateQuintet(byte[] autsBytes, String userIdentity, ICustomerAccountInfoProvider provider) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(getModuleName(), "Fetching profile for resynchronization using user identity: " + userIdentity);
		}
		
		byte[] sipData = new byte[30];
		System.arraycopy(getRands(), 0, sipData, 0, getRands().length);
		System.arraycopy(autsBytes, 0, sipData, getRands().length, autsBytes.length);
	
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getModuleName(), "SIP data generated: " + TLSUtility.bytesToHex(sipData));
		}
		
		String identityStr = userIdentity + ";" + TLSUtility.bytesToHex(sipData);
		
		try{
			getQuintetFromProfileDS(identityStr, provider);
			if (customerAccountInfo != null) {
				setUserIdentity(customerAccountInfo.getUserIdentity());
			}
		}catch(IllegalArgumentException iae){
			LogManager.getLogger().error(getModuleName(), "Problem in provisioning the Quintet in the DS. Reason : " + iae.getMessage());
			LogManager.getLogger().trace(getModuleName(), iae);
			setSuccess(false);
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.AKA_FAILURE);
		}
	}

	private void buildAkaChallenge(byte[] identityBytes) {
			AtRand atRand = new AtRand();				
			atRand.setValueBytes(getRands());
			
			calculateKeys(identityBytes, quintet.getIk(), quintet.getCk());		

			AtAutn atAutn = new AtAutn();
			atAutn.setValueBytes(quintet.getAutn());
			
			AtMac atMac = new AtMac();
			atMac.setValueBytes(new byte[16]);

			AkaEapType akaEapType = new AkaEapType();
			akaEapType.setAkaMessageType(SIMMessageTypeConstants.AKA_CHALLENGE.Id);
			akaEapType.setAkaAttributes(atRand.getBytes());
			akaEapType.setAkaAttributes(atAutn.getBytes());
			akaEapType.setAkaAttributes(atMac.getBytes());
			
			setReqEapType(akaEapType);		
			LogManager.getLogger().trace(getModuleName(), "Generated AKA Challenge Message : " + akaEapType.toString());
	}

	private void getQuintetFromProfileDS(String identity, ICustomerAccountInfoProvider provider) {
		ICustomerAccountInfo customerAccountInfo = provider.getCustomerAccountInfo(identity, EapTypeConstants.AKA);
		if(customerAccountInfo != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(getModuleName(), "User Profile: " + customerAccountInfo);
			setCustomerAccountInfo(customerAccountInfo);	
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().debug(getModuleName(), "Customer profile cannot be located, considering EAP-MD5 failure.");
			setSuccess(false);
			setFailure(true);
			setFailureReason(EAPFailureReasonConstants.USER_NOT_FOUND);
			setDone(true);
			return;
		}

		byte[] key   = null;
		byte[] operatorVariant = null; 
		int akaQuintetAlgoCode = 0;
		String password = customerAccountInfo.getPassword();
		if(password != null && password.length() > 0){
			StringTokenizer stkTokenizer = new StringTokenizer(password,":");			
			if(stkTokenizer.countTokens() > 1){
				String gsmA3A8Algo = stkTokenizer.nextToken();				
				try{
					akaQuintetAlgoCode = Integer.parseInt(gsmA3A8Algo);					
				}catch(Exception e){
					// Do nothing.
				}
				if(!AkaQuintetAlgoConstants.isValid(akaQuintetAlgoCode)){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(getModuleName(), "Invalid AKAQuintet Algo into profile");
					setSuccess(false);
					setFailure(true);
					setDone(true);
					setFailureReason(EAPFailureReasonConstants.AKA_FAILURE);	
					return;							
				}
				
				if(akaQuintetAlgoCode==0){

					String simTriplet;
					if(stkTokenizer.hasMoreTokens()){
						simTriplet = stkTokenizer.nextToken();
					} else {
						throw new IllegalArgumentException("Quintets not configured for identity: " + identity);
					}
					StringTokenizer st = new StringTokenizer(simTriplet,",");
					byte []rands = null;					
					byte[] xres = null;
					byte[] ck = null;
					byte[] ik = null;
					byte[] autn = null;
					if (st.hasMoreTokens()) {
					rands=TLSUtility.HexToBytes(st.nextToken());
					} else {
						throw new IllegalArgumentException("RAND not configured for: " + identity);
					}
					if (st.hasMoreTokens()) {
					xres=TLSUtility.HexToBytes(st.nextToken());
					} else {
						throw new IllegalArgumentException("XRES not configured for: " + identity);
					}
					if (st.hasMoreTokens()) {
					ck=TLSUtility.HexToBytes(st.nextToken());
					} else {
						throw new IllegalArgumentException("CK not configured for: " + identity);
					}
					if (st.hasMoreTokens()) {
					ik=TLSUtility.HexToBytes(st.nextToken());
					} else {
						throw new IllegalArgumentException("IK not configured for: " + identity);
					}
					if (st.hasMoreTokens()) {
					autn=TLSUtility.HexToBytes(st.nextToken());
					} else {
						throw new IllegalArgumentException("AUTN not configured for: " + identity);
					}
					quintet = new AKAQuintet(rands,autn,xres,ck,ik);
					LogManager.getLogger().debug(getModuleName(), "Configured quintuplets into proflie : " + quintet);
					setRands(rands);
				}else{
					String akaKey;
					if(stkTokenizer.hasMoreTokens()){
						akaKey = stkTokenizer.nextToken();
					} else {
						throw new IllegalArgumentException("AKA key not configured for identity: " + identity);
					}
					LogManager.getLogger().info(getModuleName(), "Aka Key : " + akaKey);
					
					key   = TLSUtility.HexToBytes(akaKey);
					if(stkTokenizer.hasMoreTokens()){
						String simOperatorVariant = stkTokenizer.nextToken();
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(getModuleName(), "Operator Variant : " + simOperatorVariant);
						operatorVariant = TLSUtility.HexToBytes(simOperatorVariant);
					}else{
						LogManager.getLogger().debug(getModuleName(), "Operator Variant is not present into profile");
					}
					IAkaQuintet akaQuintet = AkaQuintetDictionary.getInstance().getAkaQuintet(akaQuintetAlgoCode);
					setRands(SIMUtility.generateRandom());
					try {
						akaQuintet.configure(TLSUtility.bytesToHex(operatorVariant));
						quintet = new AKAQuintet(key , getRands(),this.sqn,this.amf,akaQuintet);			
						LogManager.getLogger().debug(getModuleName(), "Quintet : " + quintet); 
					} catch (GeneralSecurityException e) {
						LogManager.getLogger().trace(getModuleName(), "Problem in generating AUTN, Reason : " + e.getMessage());
						LogManager.getLogger().trace(getModuleName(), e);
					}
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModuleName(), "AKAQuintet Algo or AKA Key is not present into profile");
				setSuccess(false);
				setFailure(true);
				setDone(true);
				setFailureReason(EAPFailureReasonConstants.AKA_FAILURE);		
				return;				
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(getModuleName(), "Password is blank, EAP-AKA can not be proceed");
			setSuccess(false);
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.AKA_FAILURE);		
			return;
		}
		}
	
	protected void checkForIsIdReqRequired(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA Action for CheckForIsIdReqRequired, State : " + getCurrentState());		
	}

	protected void processResponseIdentity(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA Action for processResponseIdentity, State : " + getCurrentState());
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
		if (identity != null) {
			this.identity = new byte[identity.length];
			System.arraycopy(identity, 0, this.identity, 0, identity.length);
		} else {
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
	
	public void calculateKeys(byte[] identityBytes, byte[] ik, byte[] ck){
		
				
		
		//Generation of MK = SHA1(Identity | ik | ck)

		MessageDigest md5MessageDigest = Utility.getMessageDigest(HashAlgorithm.SHA1.getIdentifier());

		md5MessageDigest.update(identityBytes);
		LogManager.getLogger().trace(getModuleName(), "User Identity : " + TLSUtility.bytesToHex(identityBytes));
//		md5MessageDigest.update(TLSUtility.HexToBytes("0x70616e6b6974"));			
		
		md5MessageDigest.update(ik);
		md5MessageDigest.update(ck);
		
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

	public byte[] getKey() {
		return getMsk();
	}

	public byte[] getRands() {
		return rands;
	}

	public void setRands(byte[] rands) {
		this.rands = rands;
	}
	@Override
	public String getUserIdentity(){
		return authenticatedUserIdentity;
	}
	private void setUserIdentity(String userIdentity){
		this.authenticatedUserIdentity = userIdentity;
	}

}



