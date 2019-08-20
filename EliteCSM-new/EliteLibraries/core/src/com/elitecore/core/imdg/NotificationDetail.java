package com.elitecore.core.imdg;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.imdg.autogen.EnumInstanceStatus;
import com.elitecore.core.imdg.impl.TableMembersDetailTableImpl;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.Alerts;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.MembershipEvent;
import com.sun.management.snmp.SnmpStatusException;

public class NotificationDetail {

	private static final String MODULE = "IMDG-ALERT-GENERATOR";

	private ServerContext context;
	private TableMembersDetailTableImpl memberDetailTable;

	public NotificationDetail(TableMembersDetailTableImpl memberDetailTable) {
		this.memberDetailTable =  memberDetailTable;
	}

	protected void setMemberAsActive(MembershipEvent membershipEvent) {
		try {
			memberDetailTable.setMemberAsActive(membershipEvent.getMember());
		} catch (SnmpStatusException e) {
			LogManager.getLogger().warn(MODULE, "Problem with changing member status Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}


	protected void setMemberAsInctive(MembershipEvent membershipEvent) {
		try {
			memberDetailTable.setMemberAsInactive(membershipEvent.getMember());
		} catch (SnmpStatusException e) {
			LogManager.getLogger().warn(MODULE, "Problem with changing member status Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}

	public void generateInstanceShutdownAlert(LifecycleEvent lifecycleEvent) {
		Map<Alerts,Object> alertData = new HashMap<Alerts, Object>();
		alertData.put(Alerts.INSTANCE_STATUS,  new EnumInstanceStatus(1));

		this.context.generateSystemAlert(AlertSeverity.WARN, Alerts.ALERT_INSTANCE_STATUS, MODULE,  lifecycleEvent.toString(), alertData );
	}

}