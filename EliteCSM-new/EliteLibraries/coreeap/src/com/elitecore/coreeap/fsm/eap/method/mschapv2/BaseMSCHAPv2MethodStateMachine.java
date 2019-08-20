package com.elitecore.coreeap.fsm.eap.method.mschapv2;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.fsm.eap.method.BaseMethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers.MD4;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers.MSCHAP2Handler;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.types.mschapv2.MSCHAPv2EAPType;
import com.elitecore.coreeap.packet.types.mschapv2.packet.ChallengeMSCHAPv2Type;
import com.elitecore.coreeap.packet.types.mschapv2.packet.IMSCHAPv2Type;
import com.elitecore.coreeap.packet.types.mschapv2.packet.MSCHAPv2TypeDictionary;
import com.elitecore.coreeap.packet.types.mschapv2.packet.ResponseMSCHAPv2Type;
import com.elitecore.coreeap.packet.types.mschapv2.packet.SuccessMSCHAPv2Type;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.actions.MSCHAPv2Actions;
import com.elitecore.coreeap.util.constants.fsm.events.MSCHAPv2Events;
import com.elitecore.coreeap.util.constants.fsm.states.MSCHAPv2States;
import com.elitecore.coreeap.util.constants.mschapv2.OpCodeConstants;


public abstract class BaseMSCHAPv2MethodStateMachine  extends BaseMethodStateMachine{
	private static final String MODULE = "MSCHAPv2 STATE MACHINE";
	private static String MSCHAP_CHALLENGE_TEXT = null;
	private static String DEFAULT_NAME_FOR_MSCHAPv2_CHALLENGE_EAP_TYPE = "Eliteserver";
	
	private byte[] challengeBytes;
	private byte[] challengeRespBytes;
	
	private String failureReason;	
	private ICustomerAccountInfo customerAccountInfo = null;
	
	public BaseMSCHAPv2MethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
		// TODO Auto-generated constructor stub
		super(eapConfigurationContext);
		changeCurrentState(MSCHAPv2States.INITIALIZED);		
		
	}

	abstract public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException;
	
	@Override
	public IEnum[] getActionList(IEnum event) {
		// TODO Auto-generated method stub
		MSCHAPv2Actions[] actionList = null;
		switch((MSCHAPv2Events)event){
		case MSCHAPv2NAKReceived:
		case MSCHAPv2ResponseIdentityReceived:
			actionList = new MSCHAPv2Actions[1];
			actionList[0] = MSCHAPv2Actions.Generate_challenge;
			break;
		case MSCHAPv2RequestReceived:
			actionList = new MSCHAPv2Actions[1];
			actionList[0] = MSCHAPv2Actions.ProcessRequest;
			break;
		case MSCHAPv2ValidRequest:
			actionList = new MSCHAPv2Actions[1];
			actionList[0] = MSCHAPv2Actions.ValidateMSCHAPv2Response;
			break;
		case MSCHAPv2Success:
			actionList = new MSCHAPv2Actions[1];
			actionList[0] = MSCHAPv2Actions.BuildSuccess;
			break;
		case MSCHAPv2Failure:
			actionList = new MSCHAPv2Actions[1];
			actionList[0] = MSCHAPv2Actions.BuildFailure;
			break;
		case MSCHAPv2ResponseGenerated:
			actionList = new MSCHAPv2Actions[1];
			actionList[0] = MSCHAPv2Actions.SendResponse;
			break;
		default:
			actionList = new MSCHAPv2Actions[1];
			actionList[0] = MSCHAPv2Actions.DiscardRequest;

		}
		return actionList;
	}

	@Override
	public IEnum getEvent(AAAEapRespData aaaEapRespData) {
		// TODO Auto-generated method stub
		EAPPacket respPacket = aaaEapRespData.getEapRespPacket();
		
		if(getCurrentState() == MSCHAPv2States.INITIALIZED || getCurrentState() == MSCHAPv2States.SUCCESS_BUILDED || getCurrentState() == MSCHAPv2States.FAILURE_BUILDED){
			return MSCHAPv2Events.MSCHAPv2RequestReceived;
		}else if(getCurrentState() == MSCHAPv2States.SEND){
			return MSCHAPv2Events.MSCHAPv2RequestReceived;			
		}else if(getCurrentState() == MSCHAPv2States.CHALLENGE_GENERATE){
			return MSCHAPv2Events.MSCHAPv2ResponseGenerated;
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.IDENTITY.typeId
					&& getCurrentState() == MSCHAPv2States.RECEIVED){
			return MSCHAPv2Events.MSCHAPv2ResponseIdentityReceived;
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.NAK.typeId){
			return MSCHAPv2Events.MSCHAPv2NAKReceived;
		}else if(isValidRequest() && !isSuccess() && getCurrentState() == MSCHAPv2States.RECEIVED){
			return MSCHAPv2Events.MSCHAPv2ValidRequest;
		}else if(!isValidRequest() && !isSuccess() && getCurrentState() == MSCHAPv2States.RECEIVED){
			return MSCHAPv2Events.MSCHAPv2InvalidRequest;
		}else if(isValidRequest() && isSuccess() && getCurrentState() == MSCHAPv2States.VALIDATE){
			return MSCHAPv2Events.MSCHAPv2Success;
		}else if(isValidRequest() && isFailure() && getCurrentState() == MSCHAPv2States.VALIDATE){
			return MSCHAPv2Events.MSCHAPv2Failure;	
		}else if(getCurrentState() == MSCHAPv2States.VALIDATE)
			return MSCHAPv2Events.MSCHAPv2ValidRequest;
		return null;						
	}
	public IEnum getNextState(IEnum event){
		switch((MSCHAPv2Events)event){
		case MSCHAPv2NAKReceived:
		case MSCHAPv2ResponseIdentityReceived:
			return MSCHAPv2States.CHALLENGE_GENERATE;			
		case MSCHAPv2RequestReceived:
			return MSCHAPv2States.RECEIVED;
		case MSCHAPv2ValidRequest:
			return MSCHAPv2States.VALIDATE;
		case MSCHAPv2Success:
			return MSCHAPv2States.SUCCESS_BUILDED;
		case MSCHAPv2Failure:
			return MSCHAPv2States.FAILURE_BUILDED;
		case MSCHAPv2ResponseGenerated:
			return MSCHAPv2States.SEND;
		default:
			return MSCHAPv2States.DISCARDED;
		}
	}
	
	public void parseMSCHAPv2Resp(AAAEapRespData aaaEapRespData){
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		if(eapPacket != null){
			if(eapPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
				setIdentifier(eapPacket.getIdentifier());
				if(eapPacket.getEAPType().getType() == EapTypeConstants.MSCHAPv2.typeId){
					MSCHAPv2EAPType mschapv2ChallengeEAPType = (MSCHAPv2EAPType)eapPacket.getEAPType();
					LogManager.getLogger().trace(MODULE, OpCodeConstants.getName(mschapv2ChallengeEAPType.getOpCode()) + " received : " + mschapv2ChallengeEAPType);
					if(mschapv2ChallengeEAPType.getOpCode() == OpCodeConstants.RESPONSE.opCode){
						setChallengeRespBytes(((ResponseMSCHAPv2Type)mschapv2ChallengeEAPType.getMsCHAPv2Type()).getResponse());
					}
				}
				setValidRequest(true);
				return;
			}
		}	
		setValidRequest(false);
	}

	protected void actionOnInitialize(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MSCHAPv2 Action for Initialize, State : " + getCurrentState());
		setSuccess(false);
		setDone(false);
	}
	protected void actionOnProcessRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MSCHAPv2 Action for ProcessRequest, State : " + getCurrentState());		
		parseMSCHAPv2Resp(aaaEapRespData);
//		if(isValidRequest()){
//			
//		}
		
		processRequest(aaaEapRespData,provider);
	}
	protected void actionOnDiscardRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MSCHAPv2 Action for DiscardRequest, State : " + getCurrentState());
		setSuccess(false);
		setDone(false);
	}
	protected void actionOnBuildSuccess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MSCHAPv2 Action for BuildSuccess, State : " + getCurrentState());
		setSuccess(true);
		setDone(true);
	}
	protected void actionOnBuildFailure(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MSCHAPv2 Action for BuildFailure, State : " + getCurrentState());
		setSuccess(false);
		setDone(true);
	}
	protected void actionOnSendResponse(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MSCHAPv2 Action for SendResponse, State : " + getCurrentState());		
		setSuccess(false);
		setDone(false);
	}
	
	protected void actionOnGenerate_challenge(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MSCHAPv2 Action for Generate_Challenge, State : " + getCurrentState());

		
		MSCHAPv2EAPType mschapv2EAPType = (MSCHAPv2EAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.MSCHAPv2.typeId);
		mschapv2EAPType.setOpCode(OpCodeConstants.CHALLENGE.opCode);
		
		IMSCHAPv2Type mschapv2type = MSCHAPv2TypeDictionary.getInstance().createMSCHAPv2Type(OpCodeConstants.CHALLENGE.opCode);
		
		MSCHAP_CHALLENGE_TEXT = generateRandomString();
		setChallengeBytes(getChallengeBytes(MSCHAP_CHALLENGE_TEXT));		
		((ChallengeMSCHAPv2Type)mschapv2type).setIdentifier(getIdentifier()+1);
		((ChallengeMSCHAPv2Type)mschapv2type).setValueSize(16);
		((ChallengeMSCHAPv2Type)mschapv2type).setChallenge(getChallengeBytes());
		((ChallengeMSCHAPv2Type)mschapv2type).setName(DEFAULT_NAME_FOR_MSCHAPv2_CHALLENGE_EAP_TYPE.getBytes());
		mschapv2EAPType.setMsCHAPv2Type(mschapv2type);
		setReqEapType(mschapv2EAPType);			
		processRequest(aaaEapRespData,provider);
	}
	
	protected void actionOnValidateMSCHAPv2Response(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException{
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MSCHAPv2 Action for ValidateMSCHAPv2Response, State : " + getCurrentState());
		ICustomerAccountInfo customerAccountInfo = provider.getCustomerAccountInfo();
		if(customerAccountInfo != null){
					
			MSCHAPv2EAPType mschapv2EAPType = (MSCHAPv2EAPType) aaaEapRespData.getEapRespPacket().getEAPType();
			if(mschapv2EAPType.getOpCode() == OpCodeConstants.RESPONSE.opCode){
				ResponseMSCHAPv2Type responseMSCHAPv2Type = (ResponseMSCHAPv2Type) mschapv2EAPType.getMsCHAPv2Type();
				byte[] mschapResponse = responseMSCHAPv2Type.getResponse();
				byte[] challenge = getChallengeBytes();
				byte[] peerChallenge = responseMSCHAPv2Type.getPeerChallenge();
				String username;
				try {
					username = new String(responseMSCHAPv2Type.getName(),CommonConstants.UTF8);
				} catch (UnsupportedEncodingException e2) {
					username = new String(responseMSCHAPv2Type.getName());
				}
				
//				username = customerAccountInfo.getUserIdentity();
//				username = DEFAULT_NAME_FOR_MSCHAPv2_CHALLENGE_EAP_TYPE;				
				String password = customerAccountInfo.getPassword();
				int chapId = responseMSCHAPv2Type.getIdentifier();				
				byte[] calculatedMSCHAP2Response = MSCHAP2Handler.getMsChap2Response(chapId,peerChallenge,challenge,username,password);
				byte[] ntresponse = responseMSCHAPv2Type.getNTResponse();
				for(int i=26; i<calculatedMSCHAP2Response.length;i++){
					if(calculatedMSCHAP2Response[i] != ntresponse[i-26]){
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "MSCHAPv2 response is invalid.");
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							LogManager.getLogger().trace(MODULE, "MSCHAPv2 Response received : " + Utility.bytesToHex(ntresponse) + 		
							"MSCHAPv2 Response calculated : " + Utility.bytesToHex(calculatedMSCHAP2Response));
						}
						setFailure(true);	
						setDone(true);
						processRequest(aaaEapRespData,provider);
						return;
					}
				}
				byte pwHashHash[] = new byte[16];
		    	byte challengeHash[] = new byte[8];
		 		try {

					MessageDigest sha1MessageDigest = (MessageDigest) MessageDigest.getInstance("SHA-1").clone();


//		    			Modified for Aricent
					
//		    			sha1MessageDigest.update(mschapResponse, 2, 16);
//		    			sha1MessageDigest.update(mschapResponse);
//		    			sha1MessageDigest.update(MSCHAP2Handler.stringToLatin1Bytes(username));
					
					sha1MessageDigest.update(peerChallenge);
					sha1MessageDigest.update(challenge);
					sha1MessageDigest.update(username.getBytes());
					
					byte[] digest  = sha1MessageDigest.digest();    			
		           
		            System.arraycopy(digest, 0, challengeHash, 0, 8);
		            byte peerResponse[] = new byte[24];
		            System.arraycopy(mschapResponse, 25, peerResponse, 0, 24);
					byte[] pwHash = new byte[16];
					byte[] passBytes = null;
					try{
						passBytes = password.getBytes("UTF-16LE");					
					}catch(UnsupportedEncodingException e){
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "User password can not be converted into UTF-16LE");	            
			        }				
					MD4.getDigest(passBytes, 0, passBytes.length, pwHash, 0);
		            MD4.getDigest(pwHash, 0, 16, pwHashHash, 0);
					
				} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
					noSuchAlgorithmException.printStackTrace();
				} catch (CloneNotSupportedException cns) {
					cns.printStackTrace();
				}

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
//				byte[] authRespBytes = new byte[1+authRespStrBytes.length];
//				authRespBytes[0] = calculatedMSCHAP2Response[0];
//				System.arraycopy(authRespStrBytes, 0, authRespBytes, 1, authRespStrBytes.length);

				MSCHAPv2EAPType responsemschapv2EAPType = (MSCHAPv2EAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.MSCHAPv2.typeId);
				responsemschapv2EAPType.setOpCode(OpCodeConstants.SUCCESS.opCode);
				
				IMSCHAPv2Type mschapv2type = MSCHAPv2TypeDictionary.getInstance().createMSCHAPv2Type(OpCodeConstants.SUCCESS.opCode);
				
				MSCHAP_CHALLENGE_TEXT = generateRandomString();
				setChallengeBytes(getChallengeBytes(MSCHAP_CHALLENGE_TEXT));
				((SuccessMSCHAPv2Type)mschapv2type).setIdentifier(chapId);
				((SuccessMSCHAPv2Type)mschapv2type).setMessage(authRespStrBytes);
				responsemschapv2EAPType.setMsCHAPv2Type(mschapv2type);
				setReqEapType(responsemschapv2EAPType);
				return;
			}else if(mschapv2EAPType.getOpCode() == OpCodeConstants.SUCCESS.opCode){
				setSuccess(true);
				setFailure(false);
				setDone(true);				
			}else if(mschapv2EAPType.getOpCode() == OpCodeConstants.FAILURE.opCode){
				setSuccess(false);
				setFailure(true);
				setDone(true);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().debug(MODULE, "Customer profile cannot be located, considering EAP-MSCHAPv2 failure.");
			setSuccess(false);
			setFailure(true);
			setDone(true);
		}
		processRequest(aaaEapRespData,provider);
	}
	
	// Will generate the Random String
	private String generateRandomString(){
		if(getEapConfigurationContext().isTestMode())
			return "30f9d3dc-6028-48b3-9421-de2c010f7845";
		String randomString = new String();
		UUID uuid = UUID.randomUUID();
		randomString = uuid.toString();
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Random String Generated for Challenge: " + randomString);
		//printBytes("Random String Bytes", randomString.getBytes());
		return randomString;
	}

	// Will apply MSCHAPv2 on the string and returns byte[] with length 16. 
	private byte[] getChallengeBytes(String challenge){
		byte[] returnBytes = null;
		byte[] challengeBytes = challenge.getBytes();
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(challengeBytes);
			returnBytes = md.digest();
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while hashing MD5 challenge bytes with MD5. Reason: "+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return returnBytes;
	}

	protected String getModuleName(){
		return MODULE;
	}
	private boolean isMSCHAPv2ChallengeValid(int identifier, String userPassword, byte[] challengeBytes, byte[] challengeResponseBytes){
		byte[] password = null;
		if(userPassword != null){
			password = userPassword.getBytes();
		}
		
		boolean returnValue = false;
		try{
			MessageDigest md5MessageDigest = MessageDigest.getInstance("MD5");
			md5MessageDigest.update((byte)identifier);
			md5MessageDigest.update(password);
			md5MessageDigest.update(challengeBytes);
			byte[] tempBytes = md5MessageDigest.digest();
			
			for(int i = 0; i < challengeResponseBytes.length; i++){
				if(challengeResponseBytes[i] != tempBytes[i]){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "MD5 response is invalid.");
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, "Expected: " +Utility.bytesToHex(tempBytes)+ ", Received: " +Utility.bytesToHex(challengeResponseBytes));
					return false;
				}
			}
			returnValue = true;
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error :"+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return returnValue;
	}

	public byte[] getChallengeBytes() {
		return challengeBytes;
	}

	public void setChallengeBytes(byte[] challengeBytes) {		
		if(challengeBytes != null){
			this.challengeBytes = new byte[challengeBytes.length];
			System.arraycopy(challengeBytes, 0, this.challengeBytes, 0, challengeBytes.length);
		}
	}

	public byte[] getChallengeRespBytes() {
		return challengeRespBytes;
	}

	public void setChallengeRespBytes(byte[] challengeRespBytes) {
		if(challengeRespBytes != null){
			this.challengeRespBytes = new byte[challengeRespBytes.length];
			System.arraycopy(challengeRespBytes, 0, this.challengeRespBytes, 0, challengeRespBytes.length);
		}		
	}

	public ICustomerAccountInfo getCustomerAccountInfo() {
		return customerAccountInfo;
	}
		
	public void setCustomerAccountInfo(ICustomerAccountInfo customerAccountInfo) {
		this.customerAccountInfo = customerAccountInfo;
	}


	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

}