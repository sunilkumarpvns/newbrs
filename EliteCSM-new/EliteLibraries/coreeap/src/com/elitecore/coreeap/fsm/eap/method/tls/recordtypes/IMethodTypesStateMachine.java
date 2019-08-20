package com.elitecore.coreeap.fsm.eap.method.tls.recordtypes;

import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.types.tls.record.ITLSRecord;
import com.elitecore.coreeap.session.method.tls.TLSConnectionState;


public interface IMethodTypesStateMachine {
	public boolean check(ITLSRecord tlsRecord);
	public void process(ITLSRecord tlsRecord,ICustomerAccountInfoProvider provider)throws EAPException;	
	public void reset();	
	public void setTLSConnectionState(TLSConnectionState tlsConnectionState);
	public TLSConnectionState getTLSConnectionState();
	public boolean isSuccess();	
	public boolean isFailure();
	public boolean isDone();
	public byte[] getResponseTLSRecord();
	public int getType();
	public ICustomerAccountInfo getCustomerAccountInfo();
	public void clearCustomerAccountInfo();
	public String getFailureReason();
	public void setOUI(String oui);
}
