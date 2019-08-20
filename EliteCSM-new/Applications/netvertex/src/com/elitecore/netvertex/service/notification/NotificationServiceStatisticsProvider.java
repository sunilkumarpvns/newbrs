package com.elitecore.netvertex.service.notification;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.EnumNotificationStatisticsReset;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.NotificationStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class NotificationServiceStatisticsProvider extends NotificationStatistics{

	private static final String MODULE = "NOTIFICATION-SRV-STATS-PROVIDER";
	transient private NotificationServiceCounters notificationServiceCounters;
	transient private NotificationServiceContext notificationServiceContext;
	public NotificationServiceStatisticsProvider(NotificationServiceCounters notificationServiceCounters, 
			NotificationServiceContext notificationServiceContext) {
		this.notificationServiceContext = notificationServiceContext;
		this.notificationServiceCounters = notificationServiceCounters;
	}
	@Override
	public Long getTotalNotificationProcessed(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalNotificationProcessed());
	}
	@Override
	public Long getTotalNotificationProcessedToday(){
		return  SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalNotificationProcessedToday());
	}
	@Override
	public Long getTotalNotificationProcessedYesterday(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalNotificationProcessedYesterday());
	}
	@Override
	public Long getTotalEmailProcessed() {
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalEmailProcessed());
	}
	@Override
	public Long getTotalEmailSuccess(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalEmailSent());
	}
	@Override
	public Long getTotalEmailFailures(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalEmailFailures());
	}
	
	@Override
	public Long getTotalSMSSuccess(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalSMSSent());
	}
	@Override
	public Long getTotalSMSFailures(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalSMSFailures());
	}
	@Override
	public Long getNotificationServiceUpTime(){
		return SnmpCounterUtil.convertToCounter32(((System.currentTimeMillis() - notificationServiceCounters.getServiceUpTime()) / 10));
	}
	@Override
	public Long getTotalEmailProcessedToday() {
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalEmailProcessedToday());
	}
	@Override
	public Long getTotalEmailFailuresToday() {
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalEmailFailuresToday());
	}
	@Override
	public Long getTotalSMSFailuresToday() {
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalSMSFailuresToday());
	}
	@Override
	public Long getTotalEmailProcessedYesterday() {
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalEmailProcessedYesterday());
	}
	@Override
	public Long getTotalEmailFailuresYesterday() {
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalEmailFailuresYesterday());
	}
	@Override
	public Long getTotalSMSFailuresYesterday() {
		return  SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalSMSFailuresYesterday());
	}

	@Override
	public Long getTotalSMSSuccessYesterday(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalSMSSentYesterday());
	}

	@Override
	public Long getTotalSMSProcessedYesterday(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalSMSSProcessedYesterday());
	}

	@Override
	public Long getTotalEmailSuccessYesterday(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalEmailSentYesterday());
	}

	@Override
	public Long getTotalSMSProcessed(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalSMSProcessed());
	}

	@Override
	public Long getTotalSMSSuccessToday(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalSMSSentToday());
	}

	@Override
	public Long getTotalSMSProcessedToday(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalSMSProcessedToday());
	}

	@Override
	public EnumNotificationStatisticsReset getNotificationStatisticsReset()
			throws SnmpStatusException {
		int statsReset = notificationServiceCounters.getStatisticsReset();
		try{
			return new EnumNotificationStatisticsReset(statsReset);
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error while creating Notification Statistics Reset Enum. Reason: " 
						+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return new EnumNotificationStatisticsReset(NotificationServiceCounters.STATS_OTHER);
		}
	}

	@Override
	public void setNotificationStatisticsReset(EnumNotificationStatisticsReset notificationStatisticsReset)
			throws SnmpStatusException {
		if(notificationStatisticsReset == null){
			LogManager.getLogger().error(MODULE, "Unable to reset Notification service statistics. Reason: statistics reset value received is null");
			throw new IllegalArgumentException();
		}

		if(notificationStatisticsReset.intValue() == NotificationServiceCounters.STATS_RESET){
			notificationServiceCounters.resetStatistics();
		}else{
			LogManager.getLogger().error(MODULE, "Unable to reset Notification service statistics. Reason: Invalid statistics reset value received: " 
					+ notificationStatisticsReset.intValue());
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void checkNotificationStatisticsReset(
			EnumNotificationStatisticsReset x){
	}

	@Override
	public Long getTotalEmailSuccessToday(){
		return SnmpCounterUtil.convertToCounter32(notificationServiceCounters.getTotalEmailSentToday());
	}

	@Override
	public Long getNotificationStatisticsResetTime(){
		return SnmpCounterUtil.convertToCounter32((System.currentTimeMillis() - notificationServiceCounters.getStatisicsResetTime()) / 10);
	}
}