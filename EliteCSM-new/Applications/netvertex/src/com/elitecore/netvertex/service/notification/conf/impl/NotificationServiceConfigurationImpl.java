package com.elitecore.netvertex.service.notification.conf.impl;

import com.elitecore.core.servicex.EliteScheduledServiceConf;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.service.notification.conf.EmailAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.SMSAgentConfiguration;

import java.util.Objects;

public class NotificationServiceConfigurationImpl implements ToStringable{

	private long serviceExecutionPeriod;
	private int maxParallelExecution;
	private int batchSize;
	private EmailAgentConfiguration emailAgentConfiguration;
	private SMSAgentConfiguration smsAgentConfiguration;

	public NotificationServiceConfigurationImpl(long serviceExecutionPeriod,
												int maxParallelExecution,
												int batchSize,
												EmailAgentConfiguration emailAgentConfiguration,
												SMSAgentConfiguration smsAgentConfiguration) {
		this.serviceExecutionPeriod = serviceExecutionPeriod;
		this.maxParallelExecution = maxParallelExecution;
		this.batchSize = batchSize;
		this.emailAgentConfiguration = emailAgentConfiguration;
		this.smsAgentConfiguration = smsAgentConfiguration;
	}

	public long getServiceExecutionPeriod() {
		return serviceExecutionPeriod;
	}

	public int getMaxParallelExecution() {
		return maxParallelExecution;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public EmailAgentConfiguration getEmailAgentConfiguration() {
		return emailAgentConfiguration;
	}

	public SMSAgentConfiguration getSmsAgentConfiguration() {
		return smsAgentConfiguration;
	}

	public boolean isEmailNotificationEnabled() {
		return Objects.nonNull(emailAgentConfiguration);
	}

	public boolean isSMSNotificationEnabled() {
		return Objects.nonNull(smsAgentConfiguration);
	}
	public EliteScheduledServiceConf getEliteSchuduledServiceConf() {
		return new EliteScheduledServiceConf(maxParallelExecution, 60, serviceExecutionPeriod);
	}


	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading("-- Notification Service --");
		builder.incrementIndentation();
		toString(builder);
		builder.decrementIndentation();
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("Service Execution Period", serviceExecutionPeriod);
		builder.append("Max Parallel Execution", maxParallelExecution);
		builder.append("Batch Size", batchSize);
		builder.appendChildObject("Email Agent", emailAgentConfiguration);
		builder.appendChildObject("SMS Agent", smsAgentConfiguration);
	}



}
