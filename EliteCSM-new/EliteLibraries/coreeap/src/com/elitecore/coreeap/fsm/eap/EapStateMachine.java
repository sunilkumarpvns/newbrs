package com.elitecore.coreeap.fsm.eap;

import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;

public class EapStateMachine extends DefaultEapStateMachine {

	private boolean bTunnelMode = false;
	private int iDefaultTunnelMethod = 0;
	public EapStateMachine(IEapConfigurationContext eapConfigurationContext){
		super(eapConfigurationContext);
	}
	public EapStateMachine(IEapConfigurationContext eapConfigurationContext,boolean tunnelMode, int defaultTunnelMethod){
		super(eapConfigurationContext);		
		bTunnelMode = tunnelMode;
		iDefaultTunnelMethod = defaultTunnelMethod;
	}
	
	public boolean isTunnelMode(){
		 return bTunnelMode;
	}
	
	public int getDefaultTunnelMethod(){
		return iDefaultTunnelMethod;
	}
}
