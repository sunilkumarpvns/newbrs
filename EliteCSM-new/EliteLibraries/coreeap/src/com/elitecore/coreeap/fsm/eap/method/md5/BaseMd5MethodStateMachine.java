package com.elitecore.coreeap.fsm.eap.method.md5;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.UUID;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.fsm.EAPFailureReasonConstants;
import com.elitecore.coreeap.fsm.eap.method.BaseMethodStateMachine;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.types.md5.MD5ChallengeEAPType;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.actions.MD5Actions;
import com.elitecore.coreeap.util.constants.fsm.events.MD5Events;
import com.elitecore.coreeap.util.constants.fsm.states.Md5States;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;


public abstract class BaseMd5MethodStateMachine  extends BaseMethodStateMachine{
	private static final String MODULE = "MD5 STATE MACHINE";
	private static String MD5_CHALLENGE_TEXT = null;
	private static String DEFAULT_NAME_FOR_MD5_CHALLENGE_EAP_TYPE = "Eliteserver";
	
	private byte[] challengeBytes;
	private byte[] challengeRespBytes;
	
	private String failureReason;
	private ICustomerAccountInfo customerAccountInfo = null;
	
	public BaseMd5MethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
		// TODO Auto-generated constructor stub
		super(eapConfigurationContext);
		changeCurrentState(Md5States.INITIALIZED);		
		
	}

	abstract public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException;
	
	@Override
	public IEnum[] getActionList(IEnum event) {
		// TODO Auto-generated method stub
		MD5Actions[] actionList = new MD5Actions[2];
		switch((MD5Events)event){
		case MD5NAKReceived:
		case MD5ResponseIdentityReceived:
			actionList[0] = MD5Actions.Initialize;
			actionList[1] = MD5Actions.Generate_challenge;
			break;
		case MD5RequestReceived:
			actionList[0] = MD5Actions.Initialize;
			actionList[1] = MD5Actions.ValidateMD5Response;
			break;
		default:
			actionList[0] = MD5Actions.Initialize;
			actionList[1] = MD5Actions.DiscardRequest;
		}
		return actionList;
	}

	@Override
	public IEnum getEvent(AAAEapRespData aaaEapRespData) {
		// TODO Auto-generated method stub
		if(!isValidRequest()){
			return MD5Events.MD5InvalidRequest;
		}
		EAPPacket respPacket = aaaEapRespData.getEapRespPacket();

		if(respPacket.getEAPType().getType() == EapTypeConstants.IDENTITY.typeId){
			return MD5Events.MD5ResponseIdentityReceived;
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.NAK.typeId){
			return MD5Events.MD5NAKReceived;
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.MD5_CHALLENGE.typeId){
			return MD5Events.MD5RequestReceived;
		}
		return null;						
	}
	public IEnum getNextState(IEnum event){
		switch((MD5Events)event){
		case MD5ResponseIdentityReceived:
			return Md5States.CHALLENGE_GENERATE;
		case MD5NAKReceived:
			return Md5States.CHALLENGE_GENERATE;
		case MD5RequestReceived:
			return Md5States.VALIDATE;
		default:
			return Md5States.DISCARDED;
		}
	}
	
	public void parseMD5Resp(AAAEapRespData aaaEapRespData){
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		if(eapPacket != null){
			if(eapPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
				setIdentifier(eapPacket.getIdentifier());
				if(eapPacket.getEAPType().getType() == EapTypeConstants.MD5_CHALLENGE.typeId){
					MD5ChallengeEAPType md5ChallengeEAPType = (MD5ChallengeEAPType)eapPacket.getEAPType();
					setChallengeRespBytes(md5ChallengeEAPType.getValue());
				}
				setValidRequest(true);
				return;
			}
		}	
		setValidRequest(false);
	}

	protected void actionOnInitialize(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MD5 Action for Initialize, State : " + getCurrentState());
		setSuccess(false);
		setDone(false);
	}
	protected void actionOnDiscardRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MD5 Action for DiscardRequest, State : " + getCurrentState());
		setSuccess(false);
		setDone(true);
		setFailure(true);
	}

	protected void actionOnGenerate_challenge(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MD5 Action for Generate_Challenge, State : " + getCurrentState());
		MD5ChallengeEAPType md5EAPType = (MD5ChallengeEAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.MD5_CHALLENGE.typeId);
		MD5_CHALLENGE_TEXT = generateRandomString();
		setChallengeBytes(getChallengeBytes(MD5_CHALLENGE_TEXT));
		md5EAPType.setValue(getChallengeBytes());
		try{
			md5EAPType.setName(DEFAULT_NAME_FOR_MD5_CHALLENGE_EAP_TYPE.getBytes(CommonConstants.UTF8));
		}catch(UnsupportedEncodingException e){
			md5EAPType.setName(DEFAULT_NAME_FOR_MD5_CHALLENGE_EAP_TYPE.getBytes());
		}
		setReqEapType(md5EAPType);			
	}
	
	protected void actionOnValidateMD5Response(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException{
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "MD5 Action for ValidateMD5Response, State : " + getCurrentState());
		ICustomerAccountInfo customerAccountInfo = provider.getCustomerAccountInfo();
		if(customerAccountInfo != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "User Profile: " + customerAccountInfo);
			String strPasswordCheck = customerAccountInfo.getPasswordCheck();
			if(strPasswordCheck == null || !strPasswordCheck.equalsIgnoreCase("no")){
				if(isMD5ChallengeValid(getIdentifier(),customerAccountInfo.getPassword(), getChallengeBytes(), getChallengeRespBytes())){
					setCustomerAccountInfo(customerAccountInfo);	
					setSuccess(true);
					setFailure(false);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "MD5 validation failed, considering EAP-MD5 failure.");
					setSuccess(false);
					setFailure(true);
					setFailureReason(EAPFailureReasonConstants.INVALID_EAPMD5_PASSWORD);
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Password check flag is set to: " + customerAccountInfo.getPasswordCheck() + ", skipping validation.");
				setSuccess(true);
				setFailure(false);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().debug(MODULE, "Customer profile cannot be located, considering EAP-MD5 failure.");
			setSuccess(false);
			setFailure(true);
			setFailureReason(EAPFailureReasonConstants.USER_NOT_FOUND);
		}
		setDone(true);
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

	// Will apply MD5 on the string and returns byte[] with length 16. 
	private byte[] getChallengeBytes(String challenge){
		byte[] returnBytes = null;
		byte[] challengeBytes = null;
		try{
			challengeBytes = challenge.getBytes(CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			challengeBytes = challenge.getBytes();
		}
		try{
			MessageDigest md = Utility.getMessageDigest(HashAlgorithm.MD5.getIdentifier());
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
	private boolean isMD5ChallengeValid(int identifier, String userPassword, byte[] challengeBytes, byte[] challengeResponseBytes){
		byte[] password = null;
		if(userPassword != null){
			try{
				password = userPassword.getBytes(CommonConstants.UTF8);
			}catch(UnsupportedEncodingException e){
				password = userPassword.getBytes();
			}
		}
		
		boolean returnValue = false;
		try{
			MessageDigest md5MessageDigest = Utility.getMessageDigest(HashAlgorithm.MD5.getIdentifier());
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

	public void clearCustomerAccountInfo(){
		this.customerAccountInfo = null;
	}
	
	public void reset() {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Reset Method State Machine");
		resetSession();
		resetFlags();
		setFailureReason(null);
		clearCustomerAccountInfo();
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

}
