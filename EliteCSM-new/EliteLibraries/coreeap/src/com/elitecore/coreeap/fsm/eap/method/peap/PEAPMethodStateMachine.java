package com.elitecore.coreeap.fsm.eap.method.peap;

import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.fsm.eap.method.tls.BaseTLSMethodStateMachine;
import com.elitecore.coreeap.packet.types.tls.TLSEAPType;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.states.TlsStates;
import com.elitecore.coreeap.util.constants.tls.TLSFlagConstants;

public class PEAPMethodStateMachine extends BaseTLSMethodStateMachine {
	private static final String MODULE = "PEAP STATE MACHINE";
	public PEAPMethodStateMachine(
			IEapConfigurationContext eapConfigurationContext) {
		super(eapConfigurationContext);
		changeCurrentState(TlsStates.INITIALIZE);
	}
	@Override
	public String getModuleName() {
		return MODULE;
	}
	@Override
	public String getMethodName() {
		return EapTypeConstants.PEAP.name();
	}

	public TLSEAPType createStartType() {		
		TLSEAPType tlsStartType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.PEAP.typeId);		
		tlsStartType.setFlagValue(TLSFlagConstants.S_FLAG.value);				
		return(tlsStartType);
	}
	public int getMethodCode() {
		return EapTypeConstants.PEAP.typeId;
	}
	public String getMSKLabel(){
		return "client EAP encryption";
	}
	
	@Override
	public boolean isSendCertificateRequest(){
		return getEapConfigurationContext().isPEAPSendCertificateRequest();
	}
	@Override
	public boolean isRequestEAPIdentityInTunnelOnACK() {
		return true;
	}
	
}
