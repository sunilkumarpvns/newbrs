package com.elitecore.core.imdg.impl;

import com.elitecore.core.imdg.autogen.EnumStatus;
import com.elitecore.core.imdg.autogen.MembersDetailEntry;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class MembersDetailEntryMBeanImpl extends MembersDetailEntry {

	private static SnmpMib myMib;

	public MembersDetailEntryMBeanImpl(long index, String host, String name, int port , int status) {
		super(myMib);
		this.MemberID = index;
		this.IpAddress = host;
		this.MemberName = name;
		this.Port = port;
		this.Status = new EnumStatus(status);
	}

	@Override
	public Integer getPort() throws SnmpStatusException {
		return this.Port;
	}

	public void setPort(Integer port) {
		this.Port= port; 
	}

	@Override
	public String getIpAddress() throws SnmpStatusException {
		return this.IpAddress;
	}

	@Override
	public String getMemberName() throws SnmpStatusException {
		return this.MemberName;
	}

	@Override
	public Long getMemberID() throws SnmpStatusException {
		return this.MemberID;
	}

	@Override
	public EnumStatus getStatus() throws SnmpStatusException {
		return this.Status;
	}

	public  void setStatus(int status) {
		this.Status = new EnumStatus(status);
	}
}