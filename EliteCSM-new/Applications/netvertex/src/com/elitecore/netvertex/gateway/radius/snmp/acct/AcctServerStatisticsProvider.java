package com.elitecore.netvertex.gateway.radius.snmp.acct;

import com.elitecore.netvertex.gateway.radius.snmp.acct.autogen.EnumRadiusAccServConfigReset;
import com.elitecore.netvertex.gateway.radius.snmp.acct.autogen.RadiusAccServ;
import com.elitecore.netvertex.gateway.radius.snmp.acct.autogen.TableRadiusAccClientTable;

public class AcctServerStatisticsProvider extends RadiusAccServ {

	private transient AcctServerCounters acctServerMibCounters;
	
	public AcctServerStatisticsProvider(AcctServerCounters acctServerMibCounters){
		this.acctServerMibCounters = acctServerMibCounters;
	}

	@Override
	public Long getRadiusAccServTotalMalformedRequests()
	{
		return acctServerMibCounters.getMalformedReqCntr();
	}

	@Override
	public Long getRadiusAccServTotalResponses(){
		return acctServerMibCounters.getResCntr();
	}

	@Override
	public Long getRadiusAccServTotalDupRequests(){
		return acctServerMibCounters.getDupReqCntr();
	}

	@Override
	public Long getRadiusAccServTotalInvalidRequests(){
		return acctServerMibCounters.getTotalInvalidReqCntr();
	}

	@Override
	public Long getRadiusAccServTotalRequests(){
		return acctServerMibCounters.getReqCntr();
	}

	@Override
	public TableRadiusAccClientTable accessRadiusAccClientTable(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumRadiusAccServConfigReset getRadiusAccServConfigReset(){
		int configReset = acctServerMibCounters.getConfigReset();
		return new EnumRadiusAccServConfigReset(configReset);
	}

	@Override
	public void setRadiusAccServConfigReset(EnumRadiusAccServConfigReset configReset){
		if(configReset != null){
			acctServerMibCounters.setConfigReset(configReset.intValue());
		}
	}

	@Override
	public void checkRadiusAccServConfigReset(EnumRadiusAccServConfigReset x){
		// TODO Auto-generated method stub
	}

	@Override
	public Long getRadiusAccServTotalUnknownTypes(){
		return acctServerMibCounters.getUnknownTypeCntr();
	}

	@Override
	public Long getRadiusAccServTotalNoRecords(){
		return acctServerMibCounters.getNoRecordCntr();
	}

	@Override
	public Long getRadiusAccServResetTime(){
		return acctServerMibCounters.getResetTime();
	}

	@Override
	public Long getRadiusAccServTotalPacketsDropped(){
		return acctServerMibCounters.getPackDropCntr();
	}

	@Override
	public Long getRadiusAccServUpTime(){
		return acctServerMibCounters.getServUpTime();
	}

	@Override
	public Long getRadiusAccServTotalBadAuthenticators(){
		return acctServerMibCounters.getBadAuthenticatorCntr();
	}

	@Override
	public String getRadiusAccServIdent(){
		return acctServerMibCounters.getServIdent();
	}

	public long getAccStartReqCntr() {
		return acctServerMibCounters.getStartReqCntr();
	}

	public long getAccStopReqCntr() {
		return acctServerMibCounters.getStopReqCntr();
	}

	public long getAccIntrUpdateReqCntr() {
		return acctServerMibCounters.getIntrUpdateReqCntr();
	}
}
