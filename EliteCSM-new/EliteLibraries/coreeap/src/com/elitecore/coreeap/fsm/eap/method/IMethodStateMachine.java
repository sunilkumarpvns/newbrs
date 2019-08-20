package com.elitecore.coreeap.fsm.eap.method;

import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;

public interface IMethodStateMachine {
	
	public boolean check(AAAEapRespData aaaEapRespData);
	public void process(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException;
	public void postProcess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException;
	public void preProcess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException;
	public void initPickUp();
	public void reset();
	public boolean isSuccess();	
	public boolean isFailure();
	public boolean isDone();
	public int getIdentifier();
	public byte[] getKey();
	public String getUserIdentity();
	public String[] getSessionIdentities();
	public EAPPacket buildReq(int currentId) throws EAPException;
	public int getMethodCode();
	public ICustomerAccountInfo getCustomerAccountInfo();
	public String getFailureReason();
	public String getIgnoreReason();
	public boolean validateMAC(String macValue);
	public void setEAPResponseIdentity(String identity);
	public String getEAPResponseIdentity();
}
