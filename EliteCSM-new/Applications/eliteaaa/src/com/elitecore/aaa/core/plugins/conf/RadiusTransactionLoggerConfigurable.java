package com.elitecore.aaa.core.plugins.conf;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.aaa.diameter.plugins.RadiusTransactionLogger;
import com.elitecore.aaa.radius.plugins.core.RadPlugin;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.plugins.PluginInfo;

@XmlType(propOrder = {})
@XmlRootElement(name = "rad-transaction-logger")
@ConfigurationProperties(moduleName ="RADIUS_TRANSACTION_LOGGER",synchronizeKey ="", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","plugin","radius"},name = "radius-transaction-logger")
public class RadiusTransactionLoggerConfigurable extends TransactionLoggerConfigurable<RadPlugin<RadServiceRequest, RadServiceResponse>> {

private static final String MODULE = "RAD-TRNSCT-LOGR-CNFGRBL";
	
	@Override
	public void createPlugin(Map<String, RadPlugin<RadServiceRequest, RadServiceResponse>> nameToPlugin) {
		if (getPluginInfo().isPresent() == false) {
			return;
		}
	
		PluginInfo pluginInfo = new PluginInfo();
		pluginInfo.setPluginName(getPluginInfo().get().getName());

		RadiusTransactionLogger radiusTransactionLogger = new RadiusTransactionLogger(getPluginContext(), pluginInfo, this);
		try {
			radiusTransactionLogger.init();
			LogManager.getLogger().info(MODULE, "Successfully initailized Radius Transaction Logger: " + getPluginInfo().get().getName());
			nameToPlugin.put(getPluginInfo().get().getName(), radiusTransactionLogger);
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
		return PluginType.RADIUS_TRANSACTION_LOGGER;
	}
	
}
