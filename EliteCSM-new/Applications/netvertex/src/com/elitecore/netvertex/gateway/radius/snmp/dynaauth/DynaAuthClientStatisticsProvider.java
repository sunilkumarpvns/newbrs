package com.elitecore.netvertex.gateway.radius.snmp.dynaauth;

import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.autogen.RadiusDynAuthClientScalars;

public class DynaAuthClientStatisticsProvider extends RadiusDynAuthClientScalars{

	transient private DynaAuthClientCounters dynaAuthClientCounters;
	
	public DynaAuthClientStatisticsProvider(DynaAuthClientCounters dynaAuthClientCounters){
		this.dynaAuthClientCounters = dynaAuthClientCounters;
	}

	@Override
	public Long getRadiusDynAuthClientCoAInvalidServerAddresses(){
		return dynaAuthClientCounters.getCoAInvalidServerAddrCntr();
	}

	@Override
	public Long getRadiusDynAuthClientDisconInvalidServerAddresses(){
		return dynaAuthClientCounters.getDisInvalidAddrCntr();
	}


	public long getCoAReqCntr() {
		return dynaAuthClientCounters.getCoAReqCntr();
	}

	public long getCoAAuthOnlyReqCntr() {
		return dynaAuthClientCounters.getCoAAuthOnlyReqCntr();
	}

	public long getCoARetraCntr() {
		return dynaAuthClientCounters.getCoARetraCntr();
	}

	public long getCoAAckCntr() {
		return dynaAuthClientCounters.getCoAAckCntr();
	}

	public long getCoANakCntr() {
		return dynaAuthClientCounters.getCoANakCntr();
	}

	public long getCoANakAuthOnlyReqCntr() {
		return dynaAuthClientCounters.getCoANakAuthOnlyReqCntr();
	}

	public long getCoANakSessNoCtxCntr() {
		return dynaAuthClientCounters.getCoANakSessNoCtxCntr();
	}

	public long getMalformedCoAResCntr() {
		return dynaAuthClientCounters.getCoAMalformedResCntr();
	}

	public long getCoABadAuthenticatorCntr() {
		return dynaAuthClientCounters.getCoABadAuthenticatorCntr();
	}

	public long getCoAPenReqCntr() {
		return dynaAuthClientCounters.getCoAPenReqCntr();
	}

	public long getCoATimeoutCntr() {
		return dynaAuthClientCounters.getCoATimeoutCntr();
	}

	public long getCoAPackDropCntr() {
		return dynaAuthClientCounters.getCoAPackDropCntr();
	}

	public long getCoAUnknownTypeCntr() {
		return dynaAuthClientCounters.getCoAUnknownTypeCntr();
	}

	public long getDisReqCntr() {
		return dynaAuthClientCounters.getDisReqCntr();
	}

	public long getDisAuthOnlyReqCntr() {
		return dynaAuthClientCounters.getDisAuthOnlyReqCntr();
	}

	public long getDisRetraCntr() {
		return dynaAuthClientCounters.getDisRetraCntr();
	}

	public long getDisAckCntr() {
		return dynaAuthClientCounters.getDisAckCntr();
	}

	public long getDisNackCntr() {
		return dynaAuthClientCounters.getDisNackCntr();
	}

	public long getDisNackAuthOnlyReqCntr() {
		return dynaAuthClientCounters.getDisNackAuthOnlyReqCntr();
	}

	public long getDisNackSessNoCtxCntr() {
		return dynaAuthClientCounters.getDisNackSessNoCtxCntr();
	}

	public long getMalformedDisResCntr() {
		return dynaAuthClientCounters.getDisMalformedResCntr();
	}

	public long getDisBadAuthenticatorCntr() {
		return dynaAuthClientCounters.getDisBadAuthenticatorCntr();
	}

	public long getDisPenReqCntr() {
		return dynaAuthClientCounters.getDisPenReqCntr();
	}

	public long getDisTimeoutCntr() {
		return dynaAuthClientCounters.getDisTimeoutCntr();
	}

	public long getDisPackDropCntr() {
		return dynaAuthClientCounters.getDisPackDropCntr();
	}
}