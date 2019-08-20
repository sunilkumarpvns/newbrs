package com.elitecore.coreeap.fsm.eap.method.akaprime;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
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
import com.elitecore.coreeap.packet.types.akaprime.AkaPrimeEapType;
import com.elitecore.coreeap.packet.types.sim.SIMAttributeDictionary;
import com.elitecore.coreeap.packet.types.sim.attributes.ISIMAttribute;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtAutn;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtCheckCode;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtFullAuthIdReq;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtIdentity;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtKdf;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtKdfInput;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtMac;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtRand;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtRes;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.aka.quintet.AkaQuintetDictionary;
import com.elitecore.coreeap.util.aka.quintet.IAkaQuintet;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.UserIdentityPrefixTypes;
import com.elitecore.coreeap.util.constants.aka.AkaQuintetAlgoConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.actions.AkaActions;
import com.elitecore.coreeap.util.constants.fsm.events.AkaEvents;
import com.elitecore.coreeap.util.constants.fsm.states.AkaStates;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.constants.sim.message.SIMMessageTypeConstants;
import com.elitecore.coreeap.util.sim.SIMUtility;
import com.elitecore.coreeap.util.tls.TLSUtility;

public abstract class BaseAKAPrimeMethodStateMachine extends BaseMethodStateMachine {
	
	private ICustomerAccountInfo customerAccountInfo = null;
	private byte[] identity = null;
	private byte[] mk = null;
	private byte[] tek,msk,emsk= null;
	private byte[] k_aut = null;
	private byte[] k_encr = null;
	private byte[] sqn = null;
	private byte[] amf = null;
	private byte[] rands = null;
	private byte[] ck_prime = null;
	private byte[] ik_prime = null;
	private byte[] k_re = null;
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
	
	public BaseAKAPrimeMethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
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
		ck_prime = null;
		ik_prime = null;
		k_re = null;
		isIdentityReqRequired = 0;
		failureReason = null;		
		amf = new byte[]{(byte)128,0};
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
		default:
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
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.AKA_PRIME.typeId){
			AkaPrimeEapType simEapType = (AkaPrimeEapType)respPacket.getEAPType();
			if(simEapType.getAkaMessageType()== SIMMessageTypeConstants.CLIENT_ERROR.Id
					|| simEapType.getAkaMessageType()== SIMMessageTypeConstants.AKA_AUTHENTICATION_REJECT.Id
					|| simEapType.getAkaMessageType()== SIMMessageTypeConstants.AKA_SYNCHRONIZATION_FAILURE.Id)
				return AkaEvents.AkaFailure;
			else
				return AkaEvents.AkaRequestReceived;			
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
	
	public void parseAKAPRIMEResp(AAAEapRespData aaaEapRespData){
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		if(eapPacket != null){
			if(eapPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
				setIdentifier(eapPacket.getIdentifier());
				if(eapPacket.getEAPType().getType() == EapTypeConstants.AKA_PRIME.typeId){
					AkaPrimeEapType akaEapType = (AkaPrimeEapType)eapPacket.getEAPType();				
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
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA - PRIME Action for DiscardRequest, State : " + getCurrentState());
		setSuccess(false);
		setDone(false);
	}

	protected void buildSuccess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA - PRIME Action for BuildSuccess, State : " + getCurrentState());
		setSuccess(true);
		setFailure(false);
		setDone(true);
		LogManager.getLogger().trace(getModuleName(), "Done : " + isDone());
	}
	
	protected void buildFailure(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA - PRIME Action for BuildFailure, State : " + getCurrentState());
		setFailure(true);
		setSuccess(false);
		setDone(true);
		setFailureReason(EAPFailureReasonConstants.INVALID_AKA_PRIME_PASSWORD);
	}	
	
	
	protected void validateAKAPRIMEResponse(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA - PRIME Action for ValidateAKAPRIMEResponse, State : " + getCurrentState());
		byte[] receivedMAC = null;		
		AkaPrimeEapType receivedAkaEapType = (AkaPrimeEapType)aaaEapRespData.getEapRespPacket().getEAPType();
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
			AkaPrimeEapType simEapType = (AkaPrimeEapType)eapPacket.getEAPType();
			List<ISIMAttribute> akaAttributeList = simEapType.getAkaAttrList();
			for(ISIMAttribute akaAttribute : akaAttributeList){
				if(akaAttribute.getType() == SIMAttributeTypeConstants.AT_MAC.Id){					
					((AtMac)akaAttribute).setValueBytes(new byte[16]);
				}
			}
			simEapType.setAkaAttrList(akaAttributeList);			
			byte [] macBytes = eapPacket.getBytes();
			LogManager.getLogger().trace(getModuleName(), "MAC Input: " +TLSUtility.bytesToHex(macBytes));
			try {
				macBytes = SIMUtility.HMAC_SHA256(getK_aut(), macBytes);
			} catch (InvalidKeyException e) {
				LogManager.getLogger().error(getModuleName(), "AKA-Prime validate response failed, Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (NoSuchAlgorithmException e) {
				LogManager.getLogger().error(getModuleName(), "AKA-Prime validate response failed, Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
			

			byte []finalMacBytes=new byte[16];
			System.arraycopy(macBytes, 0, finalMacBytes, 0, 16);
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
			if(receivedCheckcodeBytes != null && receivedCheckcodeBytes.length == 32){
				byte[] generateCheckcodeBytes = null;			
				try {
					MessageDigest digest = MessageDigest.getInstance("SHA-256");
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
				}
			}
		}
		
	}

	protected void generateResponse(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA - PRIME Action for GenerateResponse, State : " + getCurrentState());
		AkaPrimeEapType receivedAkaEapType = (AkaPrimeEapType)aaaEapRespData.getEapRespPacket().getEAPType();
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
		} 
		if(identity.startsWith(UserIdentityPrefixTypes.AKA_PRIME_PERMANENT.identifier)) {
			getQuintetAndGenerateAKAPRIMEChallenge(provider, eapResponseIdentity);
		} else {
			AkaPrimeEapType akaEapType = new AkaPrimeEapType();
			akaEapType.setAkaMessageType(SIMMessageTypeConstants.AKA_IDENTITY.Id);
			AtFullAuthIdReq atFullAuthIdReq  = new AtFullAuthIdReq();		
			akaEapType.setAkaAttributes(atFullAuthIdReq.getBytes());
			setReqEapType(akaEapType);
		}
	}

	protected void generateChallengeMessage(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		
		AkaPrimeEapType receivedAkaEapType = (AkaPrimeEapType)aaaEapRespData.getEapRespPacket().getEAPType();
		
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
			
		getQuintetAndGenerateAKAPRIMEChallenge(provider, identityBytes);
	}

	private void getQuintetAndGenerateAKAPRIMEChallenge(ICustomerAccountInfoProvider provider, byte[] identityBytes) {
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
			setFailureReason(EAPFailureReasonConstants.AKA_PRIME_FAILURE);
		}

		if(isFailure())
			return;

		
		
	   /*
		 * need to include an AT_MAC attribute so that it will get
		 * calculated. The NONCE_MT and the MAC are both 16 bytes, so
		 * we store the NONCE_MT in the MAC for the encoder, which
		 * will pull it out before it does the operation.
		 */
		
		AtKdfInput atKdfInput = new AtKdfInput();
		String networkName = identity.substring(identity.indexOf("@") + 1 , identity.indexOf(".mnc"));
		atKdfInput.setValueBytes(networkName.getBytes());
		atKdfInput.setActualNetworkNameLength(networkName.length());
		
		AtKdf atKdf = new AtKdf();
		atKdf.setKdf(1);
		
		AtRand atRand = new AtRand();				
		atRand.setValueBytes(getRands());

		calculateAKAPrimeKeys(identityBytes,quintet.getIk(),quintet.getCk(), networkName, quintet.getAutn());		

		AtAutn atAutn = new AtAutn();
		atAutn.setValueBytes(quintet.getAutn());
		
		AtMac atMac = new AtMac();
		atMac.setValueBytes(new byte[16]);

		AkaPrimeEapType akaEapType = new AkaPrimeEapType();
		akaEapType.setAkaMessageType(SIMMessageTypeConstants.AKA_CHALLENGE.Id);
		akaEapType.setAkaAttributes(atRand.getBytes());
		akaEapType.setAkaAttributes(atAutn.getBytes());
		akaEapType.setAkaAttributes(atKdf.getBytes());
		akaEapType.setAkaAttributes(atKdfInput.getBytes());
		akaEapType.setAkaAttributes(atMac.getBytes());
		
		setReqEapType(akaEapType);		
		LogManager.getLogger().trace(getModuleName(), "received AKA PRIME Challenge Message : " + akaEapType.toString());
	}
	
	private void getQuintetFromProfileDS(String identity, ICustomerAccountInfoProvider provider) {
		ICustomerAccountInfo customerAccountInfo = provider.getCustomerAccountInfo(identity, EapTypeConstants.AKA_PRIME);
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
					setFailureReason(EAPFailureReasonConstants.AKA_PRIME_FAILURE);	
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
						throw new IllegalArgumentException("AKA PRIME key not configured for identity: " + identity);
					}
					LogManager.getLogger().info(getModuleName(), "Aka Prime Key : " + akaKey);
					
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
					LogManager.getLogger().debug(getModuleName(), "AKAQuintet Algo or AKA PRIME Key is not present into profile");
				setSuccess(false);
				setFailure(true);
				setDone(true);
				setFailureReason(EAPFailureReasonConstants.AKA_PRIME_FAILURE);		
				return;				
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(getModuleName(), "Password is blank, EAP-AKA-PRIME can not be proceed");
			setSuccess(false);
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.AKA_PRIME_FAILURE);		
			return;
		}
		}
	
	protected void checkForIsIdReqRequired(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA - PRIME Action for CheckForIsIdReqRequired, State : " + getCurrentState());		
	}

	protected void processResponseIdentity(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		LogManager.getLogger().trace(getModuleName(), "CAll - AKA - PRIME Action for processResponseIdentity, State : " + getCurrentState());
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
	
	public void calculateAKAPrimeKeys(byte[] identityBytes, byte[] ik, byte[] ck, String networkName, byte[] autn) {
		
		byte[] key = TLSUtility.appendBytes(ck, ik);

		byte[] sBytes = generateSBytes(networkName, autn);

		byte[] result;
		try {
			result = SIMUtility.HMAC_SHA256(key, sBytes);
			ck_prime = Arrays.copyOfRange(result, 0, 16);
			ik_prime = Arrays.copyOfRange(result, 16, 32); 

			byte[] ckik = TLSUtility.appendBytes(ik_prime , ck_prime);
			
			String identity ;
			try{
				identity = new String(identityBytes, CommonConstants.UTF8); 
			}catch(UnsupportedEncodingException e){
				identity = new String(identityBytes);
			}
			
			String identityStr = "EAP-AKA'" + identity;

			byte[] masterKey = SIMUtility.prfPrime(ckik, identityStr.getBytes(), 208);

			LogManager.getLogger().trace(getModuleName(), "Master Key : " + TLSUtility.bytesToHex(masterKey));
			
			byte[] k_encr = Arrays.copyOfRange(masterKey, 0, 16);
			byte[] k_aut = Arrays.copyOfRange(masterKey, 16, 48);
			byte[] k_re = Arrays.copyOfRange(masterKey, 48, 80);
			byte[] msk = Arrays.copyOfRange(masterKey, 80, 144);
			byte[] emsk = Arrays.copyOfRange(masterKey, 144, 208);
			
			setMk(masterKey);
			setK_aut(k_aut);
			setK_encr(k_encr);
			setK_re(k_re);
			setMsk(msk);
			setEmsk(emsk);		
			LogManager.getLogger().trace(getModuleName(), "K_Encr : = " + TLSUtility.bytesToHex(k_encr));
			LogManager.getLogger().trace(getModuleName(), "Kauth  : = " + TLSUtility.bytesToHex(k_aut));
			LogManager.getLogger().trace(getModuleName(), "K_re  : = " + TLSUtility.bytesToHex(k_re));
			LogManager.getLogger().trace(getModuleName(), "msk    : = " + TLSUtility.bytesToHex(msk));
			LogManager.getLogger().trace(getModuleName(), "emsk   : = " + TLSUtility.bytesToHex(emsk));
		} catch (InvalidKeyException e) {
			LogManager.getLogger().error(getModuleName(), "AKA-Prime Key calculation Failed, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		} catch (NoSuchAlgorithmException e) {
			LogManager.getLogger().error(getModuleName(), "AKA-Prime Key calculation Failed, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		} catch (IOException e) {
			//Will never occur
		}
	}

	private byte[] generateSBytes(String networkName, byte[] autn) {
		byte FC = (byte) 32;
		byte[] p0 = networkName.getBytes();
		byte[] l0 = new byte[] {(byte)0, (byte) p0.length };
		byte[] p1 = new byte[6];
		System.arraycopy(autn, 0, p1, 0, 6);
		byte[] l1 = new byte[] {(byte)0,(byte)6};

		byte[] sBytes = new byte[1 + p0.length + l0.length + p1.length + l1.length];
		sBytes[0] = FC;
		System.arraycopy(p0, 0, sBytes, 1, p0.length);
		System.arraycopy(l0, 0, sBytes, p0.length + 1, l0.length);
		System.arraycopy(p1, 0, sBytes, 1 + p0.length + l0.length , p1.length);
		System.arraycopy(l1, 0, sBytes, 1 + p0.length + l0.length + p1.length, l1.length);

		return sBytes;
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
	
	public byte[] getCk_prime() {
		return ck_prime;
	}
	
	public void setCk_prime(byte[] ck_prime) {
		this.ck_prime = ck_prime;
	}

	public byte[] getIk_prime() {
		return ik_prime;
	}
	
	public void setIk_prime(byte[] ik_prime) {
		this.ik_prime = ik_prime;
	}
	
	public byte[] getK_re() {
		return k_re;
	}
	
	public void setK_re(byte[] k_re) {
		this.k_re = k_re;
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



