package com.elitecore.coreeap.fsm.eap.method.gtc;

import java.io.UnsupportedEncodingException;

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
import com.elitecore.coreeap.packet.types.gtc.GtcEAPType;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.actions.GtcActions;
import com.elitecore.coreeap.util.constants.fsm.events.GtcEvents;
import com.elitecore.coreeap.util.constants.fsm.states.GtcStates;

public abstract class BaseGTCMethodStateMachine extends BaseMethodStateMachine{
	
	private static final String MODULE = "GTC STATE MACHINE";

	private static final String GTC_QUESTION = "Enter your password:";
	private String failureReason;
	private ICustomerAccountInfo customerAccountInfo = null;
	
	public BaseGTCMethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
		super(eapConfigurationContext);
		changeCurrentState(GtcStates.INITIALIZED);		
		
	}

	abstract public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException;
	
	@Override
	public IEnum[] getActionList(IEnum event) {
		GtcActions[] actionList = new GtcActions[2];
		switch((GtcEvents)event){
		case GtcNAKReceived:
		case GtcResponseIdentityReceived:
			actionList[0] = GtcActions.Initialize;
			actionList[1] = GtcActions.Generate_challenge;
			break;
		case GtcRequestReceived:
			actionList[0] = GtcActions.Initialize;
			actionList[1] = GtcActions.ValidateGtcResponse;
			break;
		default:
			actionList[0] = GtcActions.Initialize;
			actionList[1] = GtcActions.DiscardRequest;
		}
		return actionList;
	}

	@Override
	public IEnum getEvent(AAAEapRespData aaaEapRespData) {
		if(!isValidRequest()){
			return GtcEvents.GtcInvalidRequest;
		}
		EAPPacket respPacket = aaaEapRespData.getEapRespPacket();

		if(respPacket.getEAPType().getType() == EapTypeConstants.IDENTITY.typeId){
			return GtcEvents.GtcResponseIdentityReceived;
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.NAK.typeId){
			return GtcEvents.GtcNAKReceived;
		}else if(respPacket.getEAPType().getType() == EapTypeConstants.GTC.typeId){
			return GtcEvents.GtcRequestReceived;
		}
		return null;						
	}
	public IEnum getNextState(IEnum event){
		switch((GtcEvents)event){
		case GtcResponseIdentityReceived:
			return GtcStates.CHALLENGE_GENERATE;
		case GtcNAKReceived:
			return GtcStates.CHALLENGE_GENERATE;
		case GtcRequestReceived:
			return GtcStates.VALIDATE;
		default:
			return GtcStates.DISCARDED;
		}
	}
	
	public void parseGtcResp(AAAEapRespData aaaEapRespData){
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		if(eapPacket != null){
			if(eapPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
				setIdentifier(eapPacket.getIdentifier());
				if(eapPacket.getEAPType().getType() == EapTypeConstants.GTC.typeId){
					GtcEAPType gtcEAPType = (GtcEAPType)eapPacket.getEAPType();
					//TODO- If Required any validation regarding EAP-GTC, Place here
				}
				setValidRequest(true);
				return;
			}
		}	
		setValidRequest(false);
	}

	protected void actionOnInitialize(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "GTC Action for Initialize, State : " + getCurrentState());
		setSuccess(false);
		setDone(false);
	}
	protected void actionOnDiscardRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "GTC Action for DiscardRequest, State : " + getCurrentState());
		setSuccess(false);
		setDone(true);
		setFailure(true);
	}

	protected void actionOnGenerate_challenge(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "GTC Action for Generate_Challenge, State : " + getCurrentState());
		GtcEAPType gtcEAPType = (GtcEAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.GTC.typeId);
		//TODO - "Enter your password:" is hardcoded String, We can give configurable option for it. It will prompt to End User.
		try {
			gtcEAPType.setData(GTC_QUESTION.getBytes(CommonConstants.UTF8));
		} catch (UnsupportedEncodingException e) {
			gtcEAPType.setData(GTC_QUESTION.getBytes());
		}
		setReqEapType(gtcEAPType);			
	}
	
	protected void actionOnValidateGtcResponse(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException{
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "GTC Action for ValidateGTCResponse, State : " + getCurrentState());
		ICustomerAccountInfo customerAccountInfo = provider.getCustomerAccountInfo();
		if(customerAccountInfo != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "User Profile: " + customerAccountInfo);
			String strPasswordCheck = customerAccountInfo.getPasswordCheck();
			GtcEAPType gtcEAPType = (GtcEAPType) aaaEapRespData.getEapRespPacket().getEAPType();
			String token = null;
			try{
				token = new String(gtcEAPType.getData(),CommonConstants.UTF8);
			}catch(UnsupportedEncodingException e){
				token = new String(gtcEAPType.getData());
			}
			if(strPasswordCheck == null || !strPasswordCheck.equalsIgnoreCase("no")){
				String strPassword = customerAccountInfo.getPassword();
				if(strPassword != null && strPassword.equals(token)){
					setSuccess(true);
					setFailure(false);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "GTC validation failed, considering EAP-GTC failure.");
					setSuccess(false);
					setFailure(true);
					setFailureReason(EAPFailureReasonConstants.INVALID_EAPGTC_PASSWORD);
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Password check flag is set to: " + customerAccountInfo.getPasswordCheck() + ", skipping validation.");
				setSuccess(true);
				setFailure(false);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().debug(MODULE, "Customer profile cannot be located, considering EAP-GTC failure.");
			setSuccess(false);
			setFailure(true);
			setFailureReason(EAPFailureReasonConstants.USER_NOT_FOUND);
		}
		setDone(true);
	}
	
	protected String getModuleName(){
		return MODULE;
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
