package com.elitecore.core.imdg;

import java.util.EnumMap;

import com.elitecore.core.imdg.autogen.EnumMigrationStatus;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.Alerts;
import com.hazelcast.core.MigrationEvent;
import com.hazelcast.core.MigrationListener;

public class ClusterMigrationListener implements MigrationListener {

	private static final String MODULE = "MIGRATION-ALERT-GENERATOR";
	private ServerContext context;

	public ClusterMigrationListener(ServerContext context) {
		this.context = context;
	}

	@Override
	public void migrationCompleted(MigrationEvent event) {
		generateMigrationCompletedAlert(event);
	}

	@Override
	public void migrationFailed(MigrationEvent event) {
		generateMigrationFailedAlert(event);
	}

	@Override
	public void migrationStarted(MigrationEvent event) {
		generatePartialMigrationAlert(event);
	}

	
	private void generateMigrationCompletedAlert(MigrationEvent event) {
		EnumMap<Alerts,Object> alertData = new EnumMap<Alerts, Object> (Alerts.class);
		alertData.put(Alerts.MIGRATION_STATUS, new EnumMigrationStatus(0));

		this.context.generateSystemAlert(AlertSeverity.INFO, Alerts.ALERT_MIGRATION_HEALTH, MODULE,  event.toString(), alertData );
	}

	private void generateMigrationFailedAlert(MigrationEvent event) {
		EnumMap<Alerts,Object> alertData = new EnumMap<Alerts, Object> (Alerts.class);
		alertData.put(Alerts.MIGRATION_STATUS,  new EnumMigrationStatus(3));

		this.context.generateSystemAlert(AlertSeverity.WARN, Alerts.ALERT_MIGRATION_HEALTH, MODULE,  event.toString(), alertData );
	}

	private void generatePartialMigrationAlert(MigrationEvent event) {
		EnumMap<Alerts,Object> alertData = new EnumMap<Alerts, Object> (Alerts.class);
		alertData.put(Alerts.MIGRATION_STATUS,  new EnumMigrationStatus(1));

		this.context.generateSystemAlert(AlertSeverity.INFO, Alerts.ALERT_MIGRATION_HEALTH, MODULE,  event.toString(), alertData);
	}
}