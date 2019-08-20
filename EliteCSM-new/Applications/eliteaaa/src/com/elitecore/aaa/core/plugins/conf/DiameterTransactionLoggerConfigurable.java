package com.elitecore.aaa.core.plugins.conf;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.aaa.diameter.plugins.DiameterTransactionLogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.diameterapi.plugins.DiameterPlugin;

@XmlType(propOrder = {})
@XmlRootElement(name = "dia-transaction-logger")
@ConfigurationProperties(moduleName ="DIAMETER_TRANSACTION_LOGGER",synchronizeKey ="", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","plugin","diameter"},name = "diameter-transaction-logger")
public class DiameterTransactionLoggerConfigurable extends TransactionLoggerConfigurable<DiameterPlugin>{
	
	private static final String MODULE = "DIA_TRNSCT_LOGR_CNFGRBL";
	
	@Override
	public void createPlugin(Map<String, DiameterPlugin> nameToPlugin) {
		if (getPluginInfo().isPresent() == false) {
			return;
		}
		
		PluginInfo pluginInfo = new PluginInfo();
		pluginInfo.setPluginName(getPluginInfo().get().getName());
		
		DiameterTransactionLogger diameterTransactionLogger = new DiameterTransactionLogger(getPluginContext(), pluginInfo, this);
		try {
			diameterTransactionLogger.init();
			LogManager.getLogger().info(MODULE, "Successfully initailized Diameter Transaction Logger: " + getPluginInfo().get().getName());
			nameToPlugin.put(getPluginInfo().get().getName(), diameterTransactionLogger);
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to initialize plugin, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}

	@Override
	protected String getMODULE() {
		return MODULE;
	}

	@Override
	protected PluginType getPluginType() {
		return PluginType.DIAMETER_TRANSACTION_LOGGER;
	}

}
