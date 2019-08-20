package com.elitecore.aaa.diameter.plugins;

import com.elitecore.aaa.core.plugins.conf.RadiusTransactionLoggerConfigurable;
import com.elitecore.aaa.core.plugins.transactionlogger.KeywordValueProvider;
import com.elitecore.aaa.core.plugins.transactionlogger.TransactionLogger;
import com.elitecore.aaa.radius.plugins.core.RadPlugin;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.translators.RadServiceRequestValueProvider;
import com.elitecore.aaa.radius.translators.RadServiceResponseValueProvider;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public class RadiusTransactionLogger extends TransactionLogger<RadPlugin<RadServiceRequest, RadServiceResponse>> implements RadPlugin<RadServiceRequest, RadServiceResponse> {
	
	private static final String MODULE = "RAD-TRNSCTN-PLGN";

	private final RadiusTransactionLoggerConfigurable configurable;

	public RadiusTransactionLogger(PluginContext pluginContext, PluginInfo pluginInfo, 
			RadiusTransactionLoggerConfigurable configurable) {
		this(pluginContext, pluginInfo, configurable, TimeSource.systemTimeSource());
	}
	
	@VisibleForTesting
	RadiusTransactionLogger(PluginContext pluginContext, PluginInfo pluginInfo, 
			RadiusTransactionLoggerConfigurable configurable, TimeSource timesource) {
		super(pluginContext, pluginInfo, timesource);
		this.configurable = configurable;
	}

	@Override
	public void handlePreRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument,
			PluginCallerIdentity callerID, ISession session) {
		throw new UnsupportedOperationException("Radius transaction logger is not supported as Pre plugin");
	}

	@Override
	public void handlePostRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument,
			PluginCallerIdentity callerID, ISession session) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "handling post message.");
		}
		
		KeywordValueProvider formatValueProvider = new KeywordValueProvider(
				new RadServiceRequestValueProvider(serviceRequest),
				new RadServiceResponseValueProvider(serviceResponse));
		
		logTransaction(formatValueProvider, argument);
		
	}

	@Override
	protected String getModule() {
		return MODULE;
	}

	@Override
	protected RadiusTransactionLoggerConfigurable getConfigurable() {
		return this.configurable;
	}

}
