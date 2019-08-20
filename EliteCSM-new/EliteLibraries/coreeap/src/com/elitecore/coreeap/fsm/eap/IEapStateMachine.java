package com.elitecore.coreeap.fsm.eap;

import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.util.constants.fsm.IEnum;

public interface IEapStateMachine {
	public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException;
	public long getStartTimeStamp();
	public void setStartTimeStamp(long startTimeStamp);
	public void setIdentity(String identity);
	public String getIdentity();
	public String getUserIdentity();
	public int getCurrentMethod();
	public String[] getSessionIdentities();
	public IEnum getCurrentEvent();
	public int getCurrentIdentifier();
	public ICustomerAccountInfo getCustomerAccountInfo();
	public ICustomerAccountInfo getCustomerAccountInfo(String userIdentity);
	public EAPPacket buildFailure(int currentId) throws EAPException;
	public EAPPacket buildSuccess(int currentId) throws EAPException;
	public boolean isDuplicateRequest();
	public void clearCustomerAccountInfo();
	public void clearAccountInfoProvider();
	public String getFailureReason();
	public String getIgnoreReason();
	public boolean validateMAC(String macValue);
	public byte[] getLastEAPRequestBytes();
}
