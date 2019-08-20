package com.elitecore.aaa.diameter.plugins;

import com.elitecore.aaa.core.plugins.conf.DiameterTransactionLoggerConfigurable;
import com.elitecore.aaa.core.plugins.transactionlogger.KeywordValueProvider;
import com.elitecore.aaa.core.plugins.transactionlogger.TransactionLogger;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.plugins.DiameterPlugin;

public class DiameterTransactionLogger extends TransactionLogger<DiameterPlugin> implements DiameterPlugin {
	
	private static final String MODULE = "DIA-TRNSCTN-PLGN";
	
	private DiameterTransactionLoggerConfigurable configurable;

	public DiameterTransactionLogger(PluginContext pluginContext,
			PluginInfo pluginInfo, DiameterTransactionLoggerConfigurable configurable) {
		this(pluginContext, pluginInfo, configurable, TimeSource.systemTimeSource());
	}
	
	@VisibleForTesting
	DiameterTransactionLogger(PluginContext pluginContext, PluginInfo pluginInfo, 
			DiameterTransactionLoggerConfigurable configurable, TimeSource timesource) {
		super(pluginContext, pluginInfo, timesource);
		this.configurable = configurable;
	}

	@Override
	public void handleOutMessage(DiameterPacket diameterRequest,
			DiameterPacket diameterAnswer, ISession session,
			String argument, PluginCallerIdentity callerID) {
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "handling out message.");
		}
		
		KeywordValueProvider formatValueProvider = new KeywordValueProvider(
				new DiameterAVPValueProvider(diameterRequest),
				new DiameterAVPValueProvider(diameterAnswer));
		
		logTransaction(formatValueProvider, argument);
	}


	@Override
	public void handleInMessage(DiameterPacket diameterRequest,
			DiameterPacket diameterAnswer, ISession session,
			String argument, PluginCallerIdentity callerID) {
		throw new UnsupportedOperationException("Diameter transaction logger is not supported as In plugin");
	}

	@Override
	protected String getModule() {
		return MODULE;
	}

	@Override
	protected DiameterTransactionLoggerConfigurable getConfigurable() {
		return this.configurable;
	}

}