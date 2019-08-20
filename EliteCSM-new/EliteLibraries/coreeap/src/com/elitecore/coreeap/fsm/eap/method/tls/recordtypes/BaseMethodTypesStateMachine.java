package com.elitecore.coreeap.fsm.eap.method.tls.recordtypes;

import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.fsm.BaseStateMachine;
import com.elitecore.coreeap.session.method.tls.TLSConnectionState;

public abstract class BaseMethodTypesStateMachine extends BaseStateMachine implements  IMethodTypesStateMachine{
	private IEapConfigurationContext eapConfigurationContext;
	private TLSConnectionState tlsConnectionState;
	private boolean bSuccess = false;
	private boolean bFailure = false;
	private boolean bDone = false;

	public BaseMethodTypesStateMachine(IEapConfigurationContext eapConfigurationContext){
		this.eapConfigurationContext = eapConfigurationContext;
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
	
	public final boolean isSuccess() {	
		return bSuccess;
	}
	
	protected final void setSuccess(boolean value){
		bSuccess = value;
	}	

	public final void setTLSConnectionState(TLSConnectionState tlsConnectionState) {	
		this.tlsConnectionState = tlsConnectionState;
	}

	public final TLSConnectionState getTLSConnectionState(){
		return this.tlsConnectionState;
	}		
	
	abstract public byte[] getResponseTLSRecord();
	abstract public void setOUI(String oui);

}
