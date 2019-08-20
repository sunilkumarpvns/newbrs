package com.elitecore.core.imdg.impl;

import java.util.Map;

import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.imdg.autogen.MembersDetailEntry;
import com.elitecore.core.imdg.autogen.MembersDetailEntryMBean;
import com.elitecore.core.imdg.autogen.TableMembersDetailTable;
import com.hazelcast.core.Member;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableMembersDetailTableImpl extends TableMembersDetailTable {

	public TableMembersDetailTableImpl(SnmpMib myMib) {
		super(myMib);
	}

	public void addMemebersToTableEntries(Map<String, String> memberData, String localmember) throws SnmpStatusException {
		long index=1;
		int port = 0;
		int status = 1;
		String name="";
		String ip = null;

		
		
		for(Map.Entry<String, String> member :memberData.entrySet()) {
			if(member.getKey().equalsIgnoreCase(localmember)){ 
				continue;
			}
			name = member.getKey();
			ip = member.getValue();
			MembersDetailEntry entry = new MembersDetailEntryMBeanImpl(index++, ip, name,port,status);
			super.addEntry(entry);
		}
	}

	public void setMemberAsActive(Member member) throws SnmpStatusException {
		for (MembersDetailEntryMBean memberEntry : super.getEntries()) {
			if (memberEntry.getMemberName().equalsIgnoreCase(member.getStringAttribute(HazelcastImdgInstance.MEMBER_NAME)) && member.getAddress().getPort()!=0) {
				((MembersDetailEntryMBeanImpl) memberEntry).setStatus(0);
				((MembersDetailEntryMBeanImpl) memberEntry).setPort(member.getAddress().getPort());
			}
		}
	}

	public void setMemberAsInactive(Member member) throws SnmpStatusException {
		for (MembersDetailEntryMBean memberEntry : super.getEntries()) {
			if (memberEntry.getMemberName().equalsIgnoreCase(member.getStringAttribute(HazelcastImdgInstance.MEMBER_NAME))) {
				((MembersDetailEntryMBeanImpl) memberEntry).setStatus(1);
				((MembersDetailEntryMBeanImpl) memberEntry).setPort(0);
			}
		}
	}

}