package com.elitecore.core.serverx.snmp.mib.mib2.extended;

import com.elitecore.core.serverx.snmp.mib.mib2.autogen.EnumSnmpEnableAuthenTraps;
import com.elitecore.core.serverx.snmp.mib.mib2.autogen.SnmpMBean;
import com.sun.management.comm.SnmpAdaptorServerMBean;

public class SnmpImpl implements SnmpMBean {

	private SnmpAdaptorServerMBean adaptorServerMBean;
	private final int AUTHEN_TRAPS_ENABLED = 1;
	private final int AUTHEN_TRAPS_DISABLED = 2;
	
	public SnmpImpl(SnmpAdaptorServerMBean adaptorServerMBean){
		this.adaptorServerMBean = adaptorServerMBean;
	}

	@Override
	public EnumSnmpEnableAuthenTraps getSnmpEnableAuthenTraps(){
		if(adaptorServerMBean.getAuthTrapEnabled()){
			return new EnumSnmpEnableAuthenTraps(AUTHEN_TRAPS_ENABLED);
		}
		return new EnumSnmpEnableAuthenTraps(AUTHEN_TRAPS_DISABLED);
	}

	@Override
	public void setSnmpEnableAuthenTraps(EnumSnmpEnableAuthenTraps enableAuthenTraps){
		if(enableAuthenTraps != null){
			if(enableAuthenTraps.intValue() == AUTHEN_TRAPS_ENABLED){
				adaptorServerMBean.setAuthTrapEnabled(true);
			}else if(enableAuthenTraps.intValue() == AUTHEN_TRAPS_DISABLED){
				adaptorServerMBean.setAuthTrapEnabled(false);
			}
		}
	}

	@Override
	public void checkSnmpEnableAuthenTraps(EnumSnmpEnableAuthenTraps x){
	}

	@Override
	public Long getSnmpInSetRequests(){
		return adaptorServerMBean.getSnmpInSetRequests();
	}

	@Override
	public Long getSnmpInGetNexts(){
		return adaptorServerMBean.getSnmpInGetNexts();
	}

	@Override
	public Long getSnmpInGetRequests(){
		return adaptorServerMBean.getSnmpInGetRequests();
	}

	@Override
	public Long getSnmpInTotalSetVars(){
		return adaptorServerMBean.getSnmpInTotalSetVars();
	}

	@Override
	public Long getSnmpInTotalReqVars(){
		return adaptorServerMBean.getSnmpInTotalReqVars();
	}

	@Override
	public Long getSnmpOutTraps(){
		return adaptorServerMBean.getSnmpOutTraps();
	}

	@Override
	public Long getSnmpOutGetResponses(){
		return adaptorServerMBean.getSnmpOutGetResponses();
	}

	@Override
	public Long getSnmpInASNParseErrs(){
		return adaptorServerMBean.getSnmpInASNParseErrs();
	}

	@Override
	public Long getSnmpInBadCommunityUses(){
		return adaptorServerMBean.getSnmpInBadCommunityUses();
	}

	@Override
	public Long getSnmpInBadCommunityNames(){
		return adaptorServerMBean.getSnmpInBadCommunityNames();
	}

	@Override
	public Long getSnmpInBadVersions(){
		return adaptorServerMBean.getSnmpInBadVersions();
	}

	@Override
	public Long getSnmpOutGenErrs(){
		return adaptorServerMBean.getSnmpOutGenErrs();
	}

	@Override
	public Long getSnmpOutPkts(){
		return adaptorServerMBean.getSnmpOutPkts();
	}

	@Override
	public Long getSnmpInPkts(){
		return adaptorServerMBean.getSnmpInPkts();
	}

	@Override
	public Long getSnmpOutBadValues(){
		return adaptorServerMBean.getSnmpOutBadValues();
	}

	@Override
	public Long getSnmpOutNoSuchNames(){
		return adaptorServerMBean.getSnmpOutNoSuchNames();
	}

	@Override
	public Long getSnmpOutTooBigs(){
		return adaptorServerMBean.getSnmpOutTooBigs();
	}
	@Override
	public Long getSnmpOutGetRequests(){
		return 0L;
	}
	@Override
	public Long getSnmpInTraps(){
		return 0L;
	}

	@Override
	public Long getSnmpInGetResponses(){
		return 0L;
	}
	@Override
	public Long getSnmpInGenErrs(){
		return 0L;
	}

	@Override
	public Long getSnmpInReadOnlys(){
		return 0L;
	}

	@Override
	public Long getSnmpInBadValues(){
		return 0L;
	}

	@Override
	public Long getSnmpInNoSuchNames(){
		return 0L;
	}
	@Override
	public Long getSnmpInTooBigs(){
		return 0L;
	}
	@Override
	public Long getSnmpOutSetRequests(){
		return 0L;
	}
	@Override
	public Long getSnmpOutGetNexts(){
		return 0L;
	}
}