package com.elitecore.aaa.radius.sessionx.snmp;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.sm.client.RemoteSessionManagerClientMIB;
import com.elitecore.aaa.radius.sessionx.snmp.remotesm.autogen.RemoteSessionManagerEntry;
import static com.elitecore.core.util.mbean.SnmpCounterUtil.convertToCounter32;

public class RemoteSessionManagerEntryMBeanImpl extends RemoteSessionManagerEntry{

	private String remoteSessionManagerName;
	private String remoteSessionManagerIPAddress;
	private int remoteSMIndex;

	public RemoteSessionManagerEntryMBeanImpl(int remoteSMIndex,String remoteSMName,String remoteSMIPAddress) {
		this.remoteSMIndex = remoteSMIndex; 
		this.remoteSessionManagerName = remoteSMName;
		this.remoteSessionManagerIPAddress = remoteSMIPAddress;
	}
	
	@Override
	public Long getRemoteSMAcctStopRequestRx(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmAcctStopRequestRx(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMAcctUpdateRequestRx(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmAcctUpdateRequestRx(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMAcctStartRequestRx(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmAcctStartRequestRx(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMAcctResponseTx(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmAcctResponseTx(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMAcctRequestRx(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmAcctRequestRx(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMAccessRejectTx(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmAccessRejectTx(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMAccessAcceptTx(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmAccessAcceptTx(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMAccessRequestRx(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmAccessRequestRx(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMUnknownRequestType(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmUnknownRequestType(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMRequestDropped(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmRequestDropped(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMResponsesTx(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmResponsesTx(remoteSessionManagerName));
	}

	@Override
	public Long getRemoteSMRequestRx(){
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmRequestRx(remoteSessionManagerName));
	}

	@Override
	public String getRemoteSMName(){
		return remoteSessionManagerName;
	}

	@Override
	public Integer getClientIndex(){
		return remoteSMIndex;
	}

	@Override
	public String getRemoteSMIPAddress(){
		return remoteSessionManagerIPAddress;
	}

	@Override
	public Long getRemoteSMTimeoutRequest() {
		return convertToCounter32(RemoteSessionManagerClientMIB.getSmRequestTimeout(remoteSessionManagerName));
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.REMOTE_SM_TABLE + getRemoteSMName();
	}
}
