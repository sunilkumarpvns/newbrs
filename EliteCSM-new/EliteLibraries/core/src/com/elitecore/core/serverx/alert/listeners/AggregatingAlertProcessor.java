package com.elitecore.core.serverx.alert.listeners;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.TaskScheduler;

public class AggregatingAlertProcessor implements SystemAlertProcessor {

	private static final String MODULE = "AGGREGATING_ALERT_PROCESSOR";

	@Nonnull private SystemAlertProcessor alertProcessor;
	/*
	 * This map contains all the alerts that are passed in the constructor,
	 * and they will never be removed during its life time.
	 * So, do not remove any alert entry in the map.
	 * we can perform add/remove operation in list. 
	 * And most important of all always take lock first before doing any thing on the list  
	 */
	private ConcurrentHashMap<String, List<SystemAlert>> alertIdToAlerts;
	/*
	 * Represents the list of alerts that are eligible for aggregation 
	 */
	@Nonnull private List<IAlertEnum> aggregationEnabledAlerts;

	@Nonnull private final TaskScheduler taskScheduler;

	public AggregatingAlertProcessor(SystemAlertProcessor alertProcessor,
			@Nonnull TaskScheduler taskScheduler,
			List<IAlertEnum> aggregationEnabledAlerts) {
		this.alertProcessor = checkNotNull(alertProcessor, "alertProcessor is null");
		this.aggregationEnabledAlerts = checkNotNull(aggregationEnabledAlerts, "aggregationEnabledAlerts is null");
		this.taskScheduler = checkNotNull(taskScheduler, "taskScheduler is null");
		this.alertIdToAlerts = new ConcurrentHashMap<String, List<SystemAlert>>();
	}
	
	@Override
	public void init() throws InitializationFailedException {
		alertProcessor.init();
		fillAlertMapWithEmptyAlertList();
		taskScheduler.scheduleIntervalBasedTask(new AggregatingAlertTask());
	}
	
	private void fillAlertMapWithEmptyAlertList() {
		for (IAlertEnum alert : aggregationEnabledAlerts) {
			if(alert == null) {
				continue;
			}
			alertIdToAlerts.put(alert.id(), new LinkedList<SystemAlert>());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.elitecore.core.serverx.alert.listeners.SystemAlertProcessor#handleSystemAlert(com.elitecore.core.serverx.alert.event.SystemAlert)
	 * we are using synchronized block for list even though it is synchronized, but it is not superfluous 
	 * because we are not performing atomic operation here(single operation on list), 
	 * instead of atomic, we are performing compound operation on the list.
	 * Here, we first check the list for emptiness and then we add alert to the list  
	 */
	@Override
	public void handleSystemAlert(SystemAlert systemAlert) {
		checkNotNull(systemAlert, "systemAlert is null");

		IAlertEnum alert = systemAlert.getAlert();
		if(!alertIdToAlerts.containsKey(alert.id())) {
			alertProcessor.handleSystemAlert(systemAlert);
			return;
		}
		
		//eligible for aggregation
		List<SystemAlert> alertList = alertIdToAlerts.get(alert.id());
		synchronized (alertList) {
			if(alertList.isEmpty()) {
				alertProcessor.handleSystemAlert(systemAlert);
			}
			alertList.add(systemAlert);
		}
	}
	
	/**
	 * <i>alerts</i> list aggregates list of the alerts that are alert specific lists.<br>
	 * For example, if 5 Unknown User alert and 5 High Response Time alert is generated, 
	 * then alerts list will contain two list, one list for Unknown User and another for High Response Time
	 */
	public void aggregateAndSendAlerts() {
		List<List<SystemAlert>> alerts = new LinkedList<List<SystemAlert>>();
		
		for(List<SystemAlert> systemAlertList : alertIdToAlerts.values()) {
			synchronized (systemAlertList) {
				if(systemAlertList.size() > 1) {
					alerts.add(new LinkedList<SystemAlert>(systemAlertList));
				}
				systemAlertList.clear();
			}
		}

		for (List<SystemAlert> systemAlerts : alerts) {
			filterFirstAlert(systemAlerts);
			handleRepeatedAlert(systemAlerts);
		}
	}

	private void filterFirstAlert(List<SystemAlert> systemAlerts) {
		systemAlerts.remove(0);
	}

	private void handleRepeatedAlert(List<SystemAlert> receivedAlerts) {
		SystemAlert systemAlert = receivedAlerts.get(0);
		String repeatedAlertMessage = systemAlert.getAlert().aggregateAlertMessages(receivedAlerts);
		alertProcessor.handleSystemAlert(new SystemAlert(systemAlert.getAlert(), MODULE, systemAlert.getSeverity(), repeatedAlertMessage));
	}
	
	@Override
	public String getAlertProcessorType() {
		return alertProcessor.getAlertProcessorType();
	}
	@Override
	public String getAlertProcessorId() {
		return alertProcessor.getAlertProcessorId();
	}

	class AggregatingAlertTask extends BaseIntervalBasedTask {

		private static final String REPEATED_MESSAGE_REDUCTION_INTERVAL_STR = "alert.repeatedmessagereduction.interval.sec";
		private static final int REPEATED_MESSAGE_REDUCTION_INTERVAL_DEFAULT_VAL = 10;
		private long interval;
		
		public AggregatingAlertTask() {
			this.interval = getIntervalFromSystemEnvironment();
		}

		private long getIntervalFromSystemEnvironment() {
			long intervalInLong = REPEATED_MESSAGE_REDUCTION_INTERVAL_DEFAULT_VAL;
			String intervalInString = System.getProperty(REPEATED_MESSAGE_REDUCTION_INTERVAL_STR);
			
			if(intervalInString != null) {
				try {
					intervalInLong = Long.parseLong(intervalInString.trim());
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
						LogManager.getLogger().info(MODULE, "set " + intervalInLong + " minutes for repeated message reduction interval");
					}
				} catch(Exception e) {
					LogManager.ignoreTrace(e);
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Invalid value: " + intervalInString + " configured for " + REPEATED_MESSAGE_REDUCTION_INTERVAL_STR + " So, setting default value: " + REPEATED_MESSAGE_REDUCTION_INTERVAL_DEFAULT_VAL);
					}
				}
			}
			return intervalInLong;
		}
		
		@Override
		public long getInterval() {
			return interval;
		}
		
		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.SECONDS;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			aggregateAndSendAlerts();
		}
	}
}