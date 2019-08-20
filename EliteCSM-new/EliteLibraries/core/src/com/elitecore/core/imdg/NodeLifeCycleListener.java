package com.elitecore.core.imdg;

import java.util.EnumMap;

import com.elitecore.core.imdg.autogen.EnumInstanceStatus;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.Alerts;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;

public class NodeLifeCycleListener  implements LifecycleListener{

	private static final String MODULE = "NODE-LIFECYCLE-ALERT-GENERATOR";
	private ServerContext context;

	public NodeLifeCycleListener(ServerContext context) {
		this.context = context;
	}

	@Override
	public void stateChanged(LifecycleEvent lifecycleEvent) {
		if(lifecycleEvent.getState() == LifecycleState.STARTED) {
			EnumMap<Alerts,Object> alertData = new EnumMap<Alerts, Object> (Alerts.class);
			alertData.put(Alerts.INSTANCE_STATUS, new EnumInstanceStatus(0));

			context.generateSystemAlert(AlertSeverity.INFO, Alerts.ALERT_INSTANCE_STATUS, MODULE, lifecycleEvent.toString(), alertData);
		} else if(lifecycleEvent.getState() == LifecycleState.SHUTDOWN) {
			EnumMap<Alerts,Object> alertData = new EnumMap<Alerts, Object> (Alerts.class);
			alertData.put(Alerts.INSTANCE_STATUS,  new EnumInstanceStatus(1));

			this.context.generateSystemAlert(AlertSeverity.WARN, Alerts.ALERT_INSTANCE_STATUS, MODULE,  lifecycleEvent.toString(), alertData );
		}
	}

}
