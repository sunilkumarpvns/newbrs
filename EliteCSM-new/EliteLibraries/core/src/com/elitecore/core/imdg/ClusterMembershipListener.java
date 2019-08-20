package com.elitecore.core.imdg;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.imdg.autogen.EnumMemberStatus;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.Alerts;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

public class ClusterMembershipListener  implements MembershipListener{

	private static final String MODULE = "CLUSTER-MEMBERSHIP-ALERT-GENERATOR";
	private NotificationDetail notificationDetail;
	private ServerContext context;

	public ClusterMembershipListener(ServerContext context) {
		this.context = context;

	}

	@Override
	public void memberAdded(MembershipEvent membershipEvent) {
		generateMemberAddedAlert(membershipEvent);
	}

	@Override
	public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
		// No-op
	}

	@Override
	public void memberRemoved(MembershipEvent membershipEvent) {
		generateMemberRemovedAlert(membershipEvent);
	}

	public void setNotificationDetail(NotificationDetail notificationDetail) {
		this.notificationDetail = notificationDetail;
	}

	private void generateMemberAddedAlert(MembershipEvent membershipEvent)  {
		EnumMap<Alerts,Object> alertData = new EnumMap<Alerts, Object> (Alerts.class);
		alertData.put(Alerts.CLUSTER_MEMBER, membershipEvent.toString());
		alertData.put(Alerts.MEMBER_STATUS, new EnumMemberStatus(0));

		this.context.generateSystemAlert(AlertSeverity.INFO, Alerts.ALERT_MEMBER_STATUS, MODULE, "memeberAdded " + membershipEvent.toString(), alertData );
		
		if (notificationDetail == null) {
			return;
		}
		notificationDetail.setMemberAsActive(membershipEvent);

	}

	private void generateMemberRemovedAlert(MembershipEvent membershipEvent) {
		EnumMap<Alerts,Object> alertData = new EnumMap<Alerts, Object> (Alerts.class);
		alertData.put(Alerts.CLUSTER_MEMBER, membershipEvent.getMember().getAddress().toString());
		alertData.put(Alerts.MEMBER_STATUS, new EnumMemberStatus(1));

		this.context.generateSystemAlert(AlertSeverity.WARN, Alerts.ALERT_MEMBER_STATUS, MODULE, "memeberAdded " + membershipEvent.getMember().getAddress().toString(), alertData );
		
		if(notificationDetail == null) {
			return;
		}
		notificationDetail.setMemberAsInctive(membershipEvent);
	}

}
