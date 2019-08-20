package com.elitecore.netvertex.gateway.radius.snmp.auth;

import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.netvertex.gateway.radius.snmp.auth.autogen.EnumRadiusAuthServConfigReset;
import com.elitecore.netvertex.gateway.radius.snmp.auth.autogen.RadiusAuthServ;
import com.elitecore.netvertex.gateway.radius.snmp.auth.autogen.TableRadiusAuthClientTable;

public class AuthServerStatisticsProvider extends RadiusAuthServ{

	private transient AuthServerCounters authServerCounters;
	public AuthServerStatisticsProvider(AuthServerCounters authServerCounters){
		this.authServerCounters = authServerCounters;
	}
	@Override
	public Long getRadiusAuthServTotalAccessRejects(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getAccessRejectCntr());
	}
	@Override
	public Long getRadiusAuthServTotalAccessAccepts(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getAccessAcceptCntr());
	}
	@Override
	public Long getRadiusAuthServTotalDupAccessRequests(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getDupReqCntr());
	}
	@Override
	public Long getRadiusAuthServTotalInvalidRequests()	{
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getInvalidReqCntr());
	}
	@Override
	public TableRadiusAuthClientTable accessRadiusAuthClientTable(){
		return null;
	}
	@Override
	public Long getRadiusAuthServTotalUnknownTypes(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getUnknownTypeCntr());
	}
	@Override
	public Long getRadiusAuthServTotalAccessRequests(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getReqCntr());
	}
	@Override
	public EnumRadiusAuthServConfigReset getRadiusAuthServConfigReset(){
		int configReset = authServerCounters.getConfigReset();
		return new EnumRadiusAuthServConfigReset(configReset);
	}
	@Override
	public void setRadiusAuthServConfigReset(EnumRadiusAuthServConfigReset configReset){
		if(configReset != null){
			authServerCounters.setConfigReset(configReset.intValue());
		}
	}
	@Override
	public void checkRadiusAuthServConfigReset(EnumRadiusAuthServConfigReset x){

	}

	@Override
	public Long getRadiusAuthServTotalPacketsDropped(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getPackDropCntr());
	}
	@Override
	public Long getRadiusAuthServResetTime(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getResetTime());
	}
	@Override
	public Long getRadiusAuthServTotalBadAuthenticators(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getBadAuthenticatorCntr());
	}
	@Override
	public Long getRadiusAuthServTotalMalformedAccessRequests(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getMalformedReqCntr());
	}
	@Override
	public Long getRadiusAuthServUpTime(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getServUpTime());
	}
	@Override
	public String getRadiusAuthServIdent(){
		return authServerCounters.getServerIdent();
	}
	@Override
	public Long getRadiusAuthServTotalAccessChallenges(){
		return SnmpCounterUtil.convertToCounter32(authServerCounters.getAccessChallenges());
	}
}
