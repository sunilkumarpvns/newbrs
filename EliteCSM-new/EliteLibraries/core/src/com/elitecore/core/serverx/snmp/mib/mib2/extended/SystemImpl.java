package com.elitecore.core.serverx.snmp.mib.mib2.extended;

import com.elitecore.core.serverx.snmp.mib.mib2.autogen.SystemMBean;

public class SystemImpl implements SystemMBean {
	
	private String sysDescr;
	private String sysObjId;
	private long sysUpTime;
	private String sysContact;
	private String sysName;
	private String sysLocation;
	private int sysServices;
	
	public SystemImpl(String sysDescr, String sysObjId, long sysUpTime,
			String sysContact, String sysName, String sysLocation) {
		this.sysDescr = sysDescr;
		this.sysObjId = sysObjId;
		this.sysUpTime = sysUpTime;
		this.sysContact = sysContact;
		this.sysName = sysName;
		this.sysLocation = sysLocation;
		// sysServices = 2^(L -1) where L , where L is the OSI Layer No.
		// As our product performs at Application Layer (7). It is 2^(7 -1)
		this.sysServices = 64;
	}

	@Override
	public String getSysLocation(){
		return sysLocation;
	}

	@Override
	public void setSysLocation(String sysLocation){
		this.sysLocation = sysLocation;
	}

	@Override
	public void checkSysLocation(String x){
	}

	@Override
	public String getSysName(){
		return sysName;
	}

	@Override
	public void setSysName(String sysName){
		this.sysName = sysName;
	}

	@Override
	public void checkSysName(String x){
	}

	@Override
	public String getSysContact(){
		return sysContact;
	}

	@Override
	public void setSysContact(String x){
	}

	@Override
	public void checkSysContact(String x){
	}

	@Override
	public Long getSysUpTime(){
		return (System.currentTimeMillis() - sysUpTime) / 10;
	}

	@Override
	public String getSysObjectID(){
		return sysObjId;
	}

	@Override
	public String getSysDescr(){
		return sysDescr;
	}

	@Override
	public Integer getSysServices(){
		return sysServices;
	}
}