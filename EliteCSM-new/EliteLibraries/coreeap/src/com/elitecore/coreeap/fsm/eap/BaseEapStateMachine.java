package com.elitecore.coreeap.fsm.eap;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.commons.policy.EapPolicy;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.commons.util.constants.policy.EapPolicyValueConstants;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.AccountInfoProviderException;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.fsm.EAPFailureReasonConstants;
import com.elitecore.coreeap.fsm.EliteBaseStateMachine;
import com.elitecore.coreeap.fsm.eap.method.IMethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.aka.AKAMethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.akaprime.AKAPrimeMethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.gtc.GTCMethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.md5.Md5MethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.mschapv2.MSCHAPv2MethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.peap.PEAPMethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.sim.SIMMethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.tls.TLSMethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.tls.TTLSMethodStateMachine;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.EAPPacketFactory;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.packet.types.IdentityEAPType;
import com.elitecore.coreeap.packet.types.NAKEAPType;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.events.EapEvents;
import com.elitecore.coreeap.util.constants.fsm.states.EapStates;
import com.elitecore.coreeap.util.constants.fsm.states.MethodStates;

public abstract class BaseEapStateMachine extends EliteBaseStateMachine implements IEapStateMachine{
	private static final String MODULE = "EAP STATE MACHINE";	
	private int currentMethod;		
	private boolean rxResp= false;
	private int respId;
	private int currentId;
	private int respMethod;
	private EapPolicy policy = new EapPolicy();
	private IMethodStateMachine m = null;
	
	private boolean aaaEapResp = false;
	private boolean aaaEapNoReq = false;
	private boolean aaaEapReq = false;		
	private int methodState=0;
	protected boolean ignore = false;
	private String ignoreReason;
	private byte[] aaaEapKeyData=null;
	private boolean aaaeapKeyAvailable = false;	
	private boolean aaaEapFail = false;
	private boolean aaaEapSuccess = false;
	private boolean isDuplicateRequest = false;

	private long startTimeStamp = 0;
	private String identity = new String();
	private String failureReason;
	private ICustomerAccountInfo customerAccountInfo = null;
	private ICustomerAccountInfoProvider provider = null;

	private IEapConfigurationContext eapConfigurationContext;

	private AAAEapRespData lastAAAEapRespData = new AAAEapRespData();
	public BaseEapStateMachine(IEapConfigurationContext eapConfigurationContext) {
		changeCurrentState(EapStates.INITIALIZE);
		this.eapConfigurationContext = eapConfigurationContext;
	}

	public IEnum getNextState(IEnum state) {
		switch((EapStates)state){
		case FAILURE:
		case SUCCESS:
		case INITIALIZE:
		case IDLE:
		case DISCARD:
			switch((EapEvents)getCurrentEvent()){
				case EapMethodNotDefined:
				case EapPacketIgnored:
				case EapPacketIsNotResponse:
					return EapStates.DISCARD;					
				case EapMethodRespReceived:
				case EapNakReceived:
				case EapRespIdentityReceived:
					return EapStates.IDLE;
				case EapSuccessEvent:		
					return EapStates.SUCCESS;
				default:
					return EapStates.FAILURE;
					
			}
		default:
			return EapStates.FAILURE;
		}
	}

	@Override
	public IEnum[] getActionList(IEnum event) {
		
		// TODO Remove this method from EliteBaseStateMachine when all the method state machine classes implements applyActions method of EliteBaseStateMachine. 
		return null;
			}

	public IEnum getEvent(AAAEapRespData aaaEapRespData) {
		if(ignore)
			return EapEvents.EapPacketIgnored;
		
		if(!rxResp)
			return EapEvents.EapPacketIsNotResponse;		
		else if(rxResp && (respMethod == EapTypeConstants.NAK.typeId || respMethod == EapTypeConstants.EXPANDED.typeId))
			return EapEvents.EapNakReceived;
		else if(rxResp && (respMethod == EapTypeConstants.IDENTITY.typeId)) 
			return EapEvents.EapRespIdentityReceived;					


		int decision  = policy.getDecision();
		if(decision == EapPolicyValueConstants.FAILURE.value)
			return EapEvents.EapFailureEvent;
		else if(decision == EapPolicyValueConstants.SUCCESS.value)
			return EapEvents.EapSuccessEvent;		

		if(methodState== MethodStates.END.id)
			return EapEvents.EapSuccessEvent;
		else if(methodState== MethodStates.ERROR.id)
			return EapEvents.EapMethodRespReceived;

		if(currentMethod == EapPolicyValueConstants.NONE.value)
			return EapEvents.EapMethodRespReceived;
		else if(rxResp && (respMethod == currentMethod)) //&& (respId == currentId)
			return EapEvents.EapMethodRespReceived;
		else 
			return EapEvents.EapPacketIgnored;
 	}
	abstract public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException;	
	public void parseEapResp(AAAEapRespData aaaEapRespData){
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		
		if(eapPacket.getCode() == EapPacketConstants.RESPONSE.packetId){
			rxResp = true;
			if(eapPacket.getEAPType().getType() == EapTypeConstants.IDENTITY.typeId){
				ignore = false;
				setIgnoreReason("");
				IdentityEAPType eapType = (IdentityEAPType) eapPacket.getEAPType();
				try{
					setIdentity(new String(eapType.getIdentity(),CommonConstants.UTF8));
				}catch(UnsupportedEncodingException e){
					setIdentity(new String(eapType.getIdentity()));
				}
				setStartTimeStamp(System.currentTimeMillis());
				currentId = eapPacket.getIdentifier();
				methodState = MethodStates.PROPOSED.id;
				policy.setDecision(EapPolicyValueConstants.CONTINUE.value);
			}			
		}else {
			ignore = true;	
			setIgnoreReason("Malform Packet !!!, EAP " + EapPacketConstants.getName(eapPacket.getCode()) + " is received");
		}
		respId =eapPacket.getIdentifier();
		if(eapPacket.getEAPType() != null)
			respMethod = eapPacket.getEAPType().getType();
	}
	
	public int nextId(int currentId){
		if(currentId >= 255){
			return 1;
		}
		return currentId + 1;
	}
	public EAPPacket buildFailure(int currentId) throws EAPException{
		EAPPacket failurePacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.FAILURE.packetId);
		try {
			failurePacket.setIdentifier(currentId);
		} catch (InvalidEAPPacketException e) {
//			Logger.logTrace(MODULE,e.getMessage());
//			Logger.logTrace(MODULE,e);
			throw  new EAPException("Error during Building Failure. Reason: " + e.getMessage(), e);
		}
		failurePacket.resetLength();
		return failurePacket;
	}
	public EAPPacket buildSuccess(int currentId) throws EAPException{
		EAPPacket successPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.SUCCESS.packetId);
		try {
			successPacket.setIdentifier(currentId);
		} catch (InvalidEAPPacketException e) {
			// TODO Auto-generated catch block
//			Logger.logTrace(MODULE,e.getMessage());
//			Logger.logTrace(MODULE,e);
			throw  new EAPException("Error during Building Success. Reason: " + e.getMessage(), e);
		}
		successPacket.resetLength();
		return successPacket;
	}
	protected void actionOnInitialize(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for Initialize, State : " + getCurrentState());
		this.currentMethod = EapPolicyValueConstants.NONE.value;
	//	parseEapResp(aaaEapRespData);
		if(rxResp)
			currentId = respId;			
		else
			currentId = 0;		
	}
	
	protected void actionOnPickUpMethod(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{	
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for PickUpMethod, State : " + getCurrentState());
		
		
		if(policy.doPickUp(respMethod)){ 
			currentMethod = respMethod;
			if(currentMethod != EapPolicyValueConstants.NONE.value && 
					currentMethod != EapPolicyValueConstants.IDENTITY_TYPE.value &&
					currentMethod != EapPolicyValueConstants.NOTIFICATION_TYPE.value &&
					currentMethod != EapPolicyValueConstants.NAK_TYPE.value
					){
				LogManager.getLogger().trace(MODULE, "Current Method : " + currentMethod);
//				m = FsmFactory.getInstance().getMethodStateMachine(currentMethod);
				m = getMethodStateMachine(currentMethod);
			}else {
//				m = FsmFactory.getInstance().getMethodStateMachine(policy.getNextMethod());
				if(isTunnelMode()){
					m = getMethodStateMachine(getDefaultTunnelMethod());
				}else{
					m = getMethodStateMachine(policy.getNextMethod(getEapConfigurationContext().getDefaultNegotiationMethod()));
				}
			}
			m.initPickUp();
		}
	}
	protected void actionOnProcessRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for ProcessRequest, State : " + getCurrentState());
		parseEapResp(aaaEapRespData);
	}
	protected void actionOnBuildEapStart(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for BuildEapStart, State : " + getCurrentState());
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		if(eapPacket != null && eapPacket.getEAPType().getType() == EapTypeConstants.NAK.typeId){
			NAKEAPType nakEAPType = (NAKEAPType)eapPacket.getEAPType();
			byte[] altMethods = nakEAPType.getAlternateMethods();
			int i = 0;
			List<Integer> enabledMethodsList = getEapConfigurationContext().getEnabledAuthMethods();
			boolean methodFound = false;
			int altMethod;
			while( i < altMethods.length ){
				altMethod = (int)(altMethods[i] & 0xFF);
				if(altMethod == EapTypeConstants.NO_ALTERNATIVE.typeId){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "NAK with NO-ALTERNATIVES received.");
					ignore = true;
					break;
				}else{
					if(enabledMethodsList != null && !enabledMethodsList.isEmpty()){
						if(!enabledMethodsList.contains(altMethods[i] & 0xFF)){
							i++;
							continue;
						}
					}
					if(EapTypeConstants.isValid(altMethod)){						
						policy.setMethod(altMethod);
						methodFound = true;
						break;
					}
				}
				i++;
			}
			if(!methodFound){
				methodState = MethodStates.END.id;
				policy.setDecision(EapPolicyValueConstants.FAILURE.value);
				respMethod = EapTypeConstants.NO_ALTERNATIVE.typeId;
				setFailureReason(EAPFailureReasonConstants.EAP_METHOD_NOT_SUPPORTED);
			}
		}				

		if(m == null)
			LogManager.getLogger().trace(MODULE, "method state machine is null");
		
		m.reset();
		
	}
	protected void actionOnSelectAction(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{	
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for SelectAction, State : " + getCurrentState());		
	}
	protected void actionOnValidateRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for ValidateRequest, State : " + getCurrentState());
		if(!ignore){
		ignore = m.check(aaaEapRespData) ^ true;
			ignoreReason = m.getIgnoreReason();
	}
	}
	protected void actionOnBuildMethodResponse(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for BuildMethodResponse, State : " + getCurrentState());
		try{
			m.preProcess(aaaEapRespData, provider);
			m.process(aaaEapRespData,provider);
		}catch(AccountInfoProviderException e){
			methodState = MethodStates.ERROR.id;
			throw e;
		}
		if(m.isDone()){
			//TODO - policy.update
			
//			currentMethod = aaaEapRespData.getEapReqPacket().getEAPType().getType();
//			Logger.logDebug(MODULE, "Current Method : " + currentMethod);			
			policy.setMethod(currentMethod);
			if(m.isSuccess()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Method Success, Updating Policy. Decision: " +
					EapPolicyValueConstants.SUCCESS.name);
				
				policy.setDecision(new Integer(EapPolicyValueConstants.SUCCESS.value));
			}else if(m.isFailure()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Method Failure, Updating Policy. Decision: " +
					EapPolicyValueConstants.FAILURE.name);
				
				policy.setDecision(new Integer(EapPolicyValueConstants.FAILURE.value));
			}
			aaaEapKeyData = m.getKey();
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "AAAEAPKeyData (Key Material): " + Utility.bytesToHex(aaaEapKeyData));
			methodState = MethodStates.END.id;
		}else{
//			currentMethod = aaaEapRespData.getEapReqPacket().getEAPType().getType();
//			Logger.logDebug(MODULE, "Current Method : " + currentMethod);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Eap Method response generated, Updating Policy. Decision: " +
				EapPolicyValueConstants.CONTINUE.name);
			
			policy.setDecision(new Integer(EapPolicyValueConstants.CONTINUE.value));
			methodState = MethodStates.CONTINUE.id;
		}
	}
	protected void actionOnHandleProposeMethod(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for HandleProposeMethod, State : " + getCurrentState());
		if(policy.getDecision() != EapPolicyValueConstants.FAILURE.value){
			policy.setDecision(new Integer(EapPolicyValueConstants.CONTINUE.value));
			methodState = MethodStates.CONTINUE.id;
			if(isTunnelMode()){
				currentMethod = policy.getNextMethod(getDefaultTunnelMethod());
			}else{
				currentMethod = policy.getNextMethod(getEapConfigurationContext().getDefaultNegotiationMethod());
			}
			m = getMethodStateMachine(currentMethod);		
			if(m != null){
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Method State Machine found for method " + EapTypeConstants.getName(currentMethod));
				m.reset();
				m.process(aaaEapRespData,provider);
				if(currentMethod == EapTypeConstants.IDENTITY.typeId || currentMethod == EapTypeConstants.NOTIFICATION.typeId){
					methodState = MethodStates.CONTINUE.id;
				}else{
					methodState = MethodStates.PROPOSED.id;
				}
			}
		}
	}
	protected void actionOnMethodRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{	
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for MethodRequest, State : " + getCurrentState());
		if(policy.getDecision() == EapPolicyValueConstants.CONTINUE.value){
			currentId = nextId(currentId);
			EAPPacket aaaEapReqPacket = m.buildReq(currentId);
			currentMethod = aaaEapReqPacket.getEAPType().getType();
			aaaEapRespData.setEapReqPacket(aaaEapReqPacket);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
				if(aaaEapKeyData == null){
					LogManager.getLogger().trace(MODULE, "Setting Eap Key Data = null");
				}
			}
			aaaEapRespData.setAAAEapKeyData(aaaEapKeyData);
			getLastAAAEapRespData().setEapReqPacket(aaaEapReqPacket.getBytes());
			getLastAAAEapRespData().setAAAEapKeyData(aaaEapKeyData);
			try{
				m.postProcess(aaaEapRespData,provider);
			}catch(EAPException e){
				methodState = MethodStates.ERROR.id;
				throw e;
			}
 		}
	}
	protected void actionOnDiscardRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for DiscardRequest, State : " + getCurrentState());
		aaaEapResp = false;
		aaaEapNoReq = true;
	}
	protected void sendEapRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for SendEapRequest, State : " + getCurrentState());
		int decision = policy.getDecision();
		if(decision == EapPolicyValueConstants.SUCCESS.value){
			actionOnBuildSuccess(aaaEapRespData, provider);
			EapEvents event = (EapEvents)getEvent(aaaEapRespData);		
			setCurrentEvent(event);
			IEnum state = getNextState(getCurrentState());
			changeCurrentState(state);
		}else if(decision == EapPolicyValueConstants.FAILURE.value){
			actionOnBuildFailure(aaaEapRespData, provider);
			EapEvents event = (EapEvents)getEvent(aaaEapRespData);		
			setCurrentEvent(event);
			IEnum state = getNextState(getCurrentState());
			changeCurrentState(state);
		}		
		aaaEapResp = false;
		aaaEapReq = true;
	}
	protected void actionOnBuildFailure(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for BuildFailure, State : " + getCurrentState());
		EAPPacket aaaEapReqPacket = buildFailure(currentId);
		aaaEapFail = true;		
		aaaEapRespData.setEapReqPacket(aaaEapReqPacket);
	}
	protected void actionOnBuildSuccess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Action for BuildSuccess, State : " + getCurrentState());
		EAPPacket aaaEapReqPacket = buildSuccess(currentId);
		aaaEapRespData.setEapReqPacket(aaaEapReqPacket);
		if(aaaEapKeyData != null){
			aaaeapKeyAvailable = true;
			aaaEapRespData.setAAAEapKeyData(aaaEapKeyData);
		}
		aaaEapSuccess = true;
		this.customerAccountInfo =  m.getCustomerAccountInfo();
	}
	
	public IMethodStateMachine getMethodStateMachine(int method){		
		if(this.m != null &&  this.m.getMethodCode() == method){
			return this.m;
		}
		IMethodStateMachine m=null;
		if(EapTypeConstants.isValid(method)){
			if(EapTypeConstants.MD5_CHALLENGE.typeId == method){
				m = new Md5MethodStateMachine(getEapConfigurationContext());
			}else if(EapTypeConstants.GTC.typeId == method){
				m = new GTCMethodStateMachine(getEapConfigurationContext());					
			}else if(EapTypeConstants.MSCHAPv2.typeId == method){
				m = new MSCHAPv2MethodStateMachine(getEapConfigurationContext());			
			}else if(EapTypeConstants.TLS.typeId == method){
				m = new TLSMethodStateMachine(getEapConfigurationContext());
			}else if(EapTypeConstants.TTLS.typeId == method){
				m = new TTLSMethodStateMachine(getEapConfigurationContext());				
			}else if(EapTypeConstants.SIM.typeId == method){
				m = new SIMMethodStateMachine(getEapConfigurationContext());				
			}else if(EapTypeConstants.PEAP.typeId == method){
				m = new PEAPMethodStateMachine(getEapConfigurationContext());
			}else if(EapTypeConstants.AKA.typeId == method){
				m = new AKAMethodStateMachine(getEapConfigurationContext());			
			}else if(EapTypeConstants.AKA_PRIME.typeId == method){
				m = new AKAPrimeMethodStateMachine(getEapConfigurationContext());
			}
		}
		if(m != null){
			m.setEAPResponseIdentity(getUserIdentity());
		}
		return m;
	}

	protected String getModuleName(){
		return MODULE;
	}

	public boolean isAaaEapResp() {
		return aaaEapResp;
	}

	public void setAaaEapResp(boolean aaaEapResp) {
		this.aaaEapResp = aaaEapResp;
	}

	public IEapConfigurationContext getEapConfigurationContext() {
		return eapConfigurationContext;
	}	

	public long getStartTimeStamp() {
		return startTimeStamp;
	}
		
	public void setStartTimeStamp(long startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}
		
	public String getIdentity() {
		return identity;
	}
		
	public void setIdentity(String identity) {
		this.identity = identity;
	}
		
	public int getCurrentMethod() {
		return currentMethod;
	}

	public void setCurrentMethod(int currentMethod) {
		this.currentMethod = currentMethod;
	}

	public ICustomerAccountInfo getCustomerAccountInfo() {
		if(this.customerAccountInfo == null){
			this.customerAccountInfo = getCustomerAccountInfoProvider().getCustomerAccountInfo();
		}
	return customerAccountInfo;
	}

	public ICustomerAccountInfo getCustomerAccountInfo(String userIdentity) {
		if(this.customerAccountInfo == null){
			this.customerAccountInfo = getCustomerAccountInfoProvider().getCustomerAccountInfo(userIdentity);
		}
	return customerAccountInfo;
	}
	
	public void setCustomerAccountInfo(ICustomerAccountInfo customerAccountInfo) {
	this.customerAccountInfo = customerAccountInfo;
	}

	public ICustomerAccountInfoProvider getCustomerAccountInfoProvider() {
		return provider;
	}

	public void setCustomerAccountInfoProvider(ICustomerAccountInfoProvider provider) {
		this.provider = provider;
	}

	public void clearCustomerAccountInfo(){
		this.customerAccountInfo = null;
	}
	public void clearAccountInfoProvider(){
		this.provider = null;
	}
	public int getCurrentIdentifier() {
		return currentId;
	}

	public void setCurrentIdentifier(int currentId) {
		this.currentId = currentId;
	}

	public byte[] getAAAEapKeyData() {
		return aaaEapKeyData;
	}

	public void setAAAEapKeyData(byte[] aaaEapKeyData) {
		this.aaaEapKeyData = aaaEapKeyData;
	}

	public AAAEapRespData getLastAAAEapRespData() {
		return lastAAAEapRespData;
	}

	public void setLastAAAEapRespData(AAAEapRespData lastAAAEapRespData) {
		this.lastAAAEapRespData = lastAAAEapRespData;
	}

	public String getUserIdentity() {
		String strUserIdentity = null;
		if(m != null){
			strUserIdentity = m.getUserIdentity();
			if(strUserIdentity == null && this.customerAccountInfo != null){
				strUserIdentity = this.customerAccountInfo.getUserIdentity();
			}
		}else
			return null;
		return strUserIdentity;
	}

	public boolean isDuplicateRequest() {
		return isDuplicateRequest;
	}

	public void setDuplicateRequest(boolean isDuplicateRequest) {
		this.isDuplicateRequest = isDuplicateRequest;
	}

	public String getFailureReason() {
		if(m != null){
			String reason = m.getFailureReason();
			if(reason != null && reason.length() > 0)
				return reason;
		}
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	
	public String getIgnoreReason() {
		return ignoreReason;
	}

	public void setIgnoreReason(String ignoreReason) {
		this.ignoreReason = ignoreReason;
	}

	public boolean validateMAC(String macValue){
		return m.validateMAC(macValue);
	}

	public byte[] getLastEAPRequestBytes(){
		return this.lastAAAEapRespData.getEapReqPacketBytes();
}
	public  void applyActions(IEnum event,AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		switch((EapStates)getCurrentState()){
		case DISCARD:
		case INITIALIZE:
			switch((EapEvents)event){
				case EapNakReceived:
					actionOnBuildEapStart(aaaEapRespData,provider);
					actionOnSelectAction(aaaEapRespData,provider);
					actionOnHandleProposeMethod(aaaEapRespData,provider);
					actionOnMethodRequest(aaaEapRespData,provider);
					break;
				case EapRespIdentityReceived:
					actionOnPickUpMethod(aaaEapRespData,provider);
					actionOnSelectAction(aaaEapRespData,provider);
					actionOnHandleProposeMethod(aaaEapRespData,provider);
					actionOnMethodRequest(aaaEapRespData,provider);
					break;
				case EapMethodRespReceived:
					actionOnPickUpMethod(aaaEapRespData,provider);
					actionOnBuildMethodResponse(aaaEapRespData,provider);
					actionOnMethodRequest(aaaEapRespData,provider);
					break;
				case EapFailureEvent:
					actionOnBuildFailure(aaaEapRespData,provider);
					break;
				case EapMethodNotDefined:
				case EapPacketIgnored:
				case EapPacketIsNotResponse:
					actionOnDiscardRequest(aaaEapRespData,provider);
					break;			 		
				case EapSuccessEvent:
					actionOnBuildSuccess(aaaEapRespData,provider);
					break;
				default:
					actionOnBuildFailure(aaaEapRespData,provider);
			}
			break;
		case IDLE:
			switch((EapEvents)event){
			case EapNakReceived:
				actionOnBuildEapStart(aaaEapRespData,provider);
				actionOnSelectAction(aaaEapRespData,provider);
				actionOnHandleProposeMethod(aaaEapRespData,provider);
				actionOnMethodRequest(aaaEapRespData,provider);
				break;
			case EapRespIdentityReceived:
				actionOnBuildEapStart(aaaEapRespData,provider);
				actionOnSelectAction(aaaEapRespData,provider);
				actionOnHandleProposeMethod(aaaEapRespData,provider);
				actionOnMethodRequest(aaaEapRespData,provider);
				break;
			case EapMethodRespReceived:
				actionOnValidateRequest(aaaEapRespData,provider);
				actionOnBuildMethodResponse(aaaEapRespData,provider);
				actionOnMethodRequest(aaaEapRespData,provider);
				break;
			case EapFailureEvent:
				actionOnBuildFailure(aaaEapRespData,provider);
				break;
			case EapMethodNotDefined:
			case EapPacketIgnored:
			case EapPacketIsNotResponse:
				actionOnDiscardRequest(aaaEapRespData,provider);
				break;			 		
			case EapSuccessEvent:
				actionOnBuildSuccess(aaaEapRespData,provider);
				break;
			default:
				actionOnBuildFailure(aaaEapRespData,provider);
			}
			break;
		default:
			switch((EapEvents)event){
			case EapNakReceived:
				actionOnInitialize(aaaEapRespData,provider);
				actionOnBuildEapStart(aaaEapRespData,provider);
				actionOnSelectAction(aaaEapRespData,provider);
				actionOnHandleProposeMethod(aaaEapRespData,provider);
				actionOnMethodRequest(aaaEapRespData,provider);
				break;
			case EapRespIdentityReceived:
				actionOnInitialize(aaaEapRespData,provider);
				actionOnPickUpMethod(aaaEapRespData,provider);
				actionOnSelectAction(aaaEapRespData,provider);
				actionOnHandleProposeMethod(aaaEapRespData,provider);
				actionOnMethodRequest(aaaEapRespData,provider);
				break;
			case EapMethodRespReceived:
				actionOnInitialize(aaaEapRespData,provider);
				actionOnPickUpMethod(aaaEapRespData,provider);
				actionOnBuildMethodResponse(aaaEapRespData,provider);
				actionOnMethodRequest(aaaEapRespData,provider);
				break;
			case EapFailureEvent:
				actionOnBuildFailure(aaaEapRespData,provider);
				break;
			case EapMethodNotDefined:
			case EapPacketIgnored:
			case EapPacketIsNotResponse:
				actionOnDiscardRequest(aaaEapRespData,provider);
				break;			 		
			case EapSuccessEvent:
				actionOnBuildSuccess(aaaEapRespData,provider);
				break;
			default:
				actionOnInitialize(aaaEapRespData,provider);
				actionOnBuildFailure(aaaEapRespData,provider);
			}
			break;
		}
	}
	@Override
	public String[] getSessionIdentities() {
		if (m != null){
			return m.getSessionIdentities();
		}
		return null;
	}

	public abstract boolean isTunnelMode();
	public abstract int getDefaultTunnelMethod();
}
