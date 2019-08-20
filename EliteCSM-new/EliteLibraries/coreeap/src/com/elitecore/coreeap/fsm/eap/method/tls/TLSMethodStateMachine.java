package com.elitecore.coreeap.fsm.eap.method.tls;

import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.packet.types.tls.TLSEAPType;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.states.TlsStates;
import com.elitecore.coreeap.util.constants.tls.TLSFlagConstants;

public class TLSMethodStateMachine extends BaseTLSMethodStateMachine {
	private static final String MODULE = "TLS STATE MACHINE";
	public TLSMethodStateMachine(
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
		// TODO Auto-generated method stub
		return "TLS";
	}

	public TLSEAPType createStartType() {		
		TLSEAPType tlsStartType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.TLS.typeId);		
		tlsStartType.setFlagValue(TLSFlagConstants.S_FLAG.value);				
		return(tlsStartType);
	}

	public int getMethodCode() {
		return EapTypeConstants.TLS.typeId;		
	}
	
	public String getMSKLabel(){
		return "client EAP encryption";
	}
	
	@Override
	public boolean isSendCertificateRequest(){
		return getEapConfigurationContext().isTLSSendCertificateRequest();
	}

	@Override
	public boolean isRequestEAPIdentityInTunnelOnACK() {
		return false;
	}
}
