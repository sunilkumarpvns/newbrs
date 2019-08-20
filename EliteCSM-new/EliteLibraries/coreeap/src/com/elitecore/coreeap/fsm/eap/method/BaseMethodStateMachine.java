package com.elitecore.coreeap.fsm.eap.method;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.fsm.EliteBaseStateMachine;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.EAPPacketFactory;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;


public abstract class BaseMethodStateMachine extends EliteBaseStateMachine implements IMethodStateMachine {
	private static final String MODULE = "BASE METHOD STATE MACHINE";
//	protected HashMap<String, Object> session = new HashMap<String, Object>();
	private boolean bSuccess = false;
	private boolean bFailure = false;
	private boolean bDone = false;
	private boolean ignore = false;
	private int identifier = 0;	
	private List<EAPType> reqEapType = new ArrayList<EAPType>();
	private IEapConfigurationContext eapConfigurationContext;
	private ICustomerAccountInfo customerAccountInfo = null;
	private String eapResponseIdentity;
	public BaseMethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
		this.eapConfigurationContext = eapConfigurationContext;
	}
	
	public void initPickUp() {
		resetFlags();
	}

	public EAPPacket buildReq(int currentId) throws EAPException {
		// TODO Auto-generated method stub
		EAPPacket eapPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.REQUEST.packetId);
		try {
			eapPacket.setIdentifier(currentId);
			if(getReqEapTypes().size() > 1)				
				eapPacket.setEAPType(getReqEapTypes());
			else
				eapPacket.setEAPType(getReqEapType());
		} catch (InvalidEAPPacketException e) {
//			Logger.logTrace(MODULE,e.getMessage());
//			Logger.logTrace(MODULE,e);
			throw new EAPException("Error during building Eap Request Packet. Reason: " + e.getMessage(), e);
		}		
		eapPacket.resetLength();		
		return eapPacket;
	}

	public final boolean isSuccess() {	
		return bSuccess;
	}
	
	protected final void setSuccess(boolean value){
		bSuccess = value;
	}	

	public void reset() {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Reset Method State Machine");
		resetSession();
		resetFlags();
	}
	
	public void resetFlags(){
		setSuccess(false);
		setDone(false);
		setFailure(false);
	}
	protected final void setValidRequest(boolean ignore){
		this.ignore = ignore;
	}
	
	protected final boolean isValidRequest(){
		return this.ignore;
	}

	public final EAPType getReqEapType() {
		return reqEapType.get(0);
	}

	public final List<EAPType> getReqEapTypes() {		
		LogManager.getLogger().trace(MODULE, "Multiple EAP Request");
		for(int i =0 ; i < reqEapType.size() ; i ++ ){
			LogManager.getLogger().trace(MODULE, "EAP Type : " + reqEapType.get(i));
		}
		return reqEapType;
	}

	public final void setReqEapType(EAPType respEapType) {
		if(this.reqEapType == null)
			this.reqEapType = new ArrayList<EAPType>();
		else
			this.reqEapType.clear();
		this.reqEapType.add(respEapType);
	}

	public final void setReqEapType(List<EAPType> respEapType) {
		this.reqEapType = respEapType;
	}

	public final void addReqEapType(EAPType respEapType){
		if(reqEapType == null)
			reqEapType = new ArrayList<EAPType>();
		reqEapType.add(respEapType);
	}
	
	public IEapConfigurationContext getEapConfigurationContext() {
		return eapConfigurationContext;
	}

	public final boolean isDone() {
		return bDone;
	}

	public final void setDone(boolean done) {
		bDone = done;
	}

	public final boolean isFailure() {
		return bFailure;
	}

	public final void setFailure(boolean failure) {
		bFailure = failure;
	}
	public final int getIdentifier() {
		return identifier;
	}

	public final void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public void resetSession(){
//		session = new HashMap<String, Object>();
	}
/*	public final Object getSessionData(String key) {
		return session.get(key);
	}

	public final void setSessionData(String key, Object value) {
		this.session.put(key, value);
	}
*/	
	public ICustomerAccountInfo getCustomerAccountInfo() {
		return customerAccountInfo;
	}
	
	public void setCustomerAccountInfo(ICustomerAccountInfo customerAccountInfo) {
		this.customerAccountInfo = customerAccountInfo;
	}
	
	public String getUserIdentity(){
		return null;
	}
	
	public String getIgnoreReason(){
		return null;
	}

	public void postProcess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException{
		//	WHATEVER METHOD NEEDS TO DO POST PROCESS IMPLEMENTATION MUST OVERRIDE THIS METHOD..
	}
	
	public void preProcess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException{
		//	WHATEVER METHOD NEEDS TO DO PRE PROCESS IMPLEMENTATION MUST OVERRIDE THIS METHOD..
	}
	
	public boolean validateMAC(String macValue){
		return true;
	}
	
	@Override
	public  void applyActions(IEnum event,AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		// TODO Remove this implementation when all method state machine classes override this method.
	}
	
	@Override
	public String[] getSessionIdentities() {
		return null;
	}
	
	@Override
	public void setEAPResponseIdentity(String identity) {
		eapResponseIdentity = identity;
}
	
	@Override
	public String getEAPResponseIdentity() {
		return eapResponseIdentity;
	}
}
