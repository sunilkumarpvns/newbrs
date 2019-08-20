package com.elitecore.aaa.diameter.plugins.core;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.core.plugins.PluginDetailProvider;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.diameterapi.plugins.DiameterPlugin;

/**
 * 
 * @author khushbu.chauhan
 * @author narendra.pathai
 *
 */
public class DiameterPluginManager {
	private static final String MODULE = "DIA-PLGN-MGR";
	
	private final PluginConfigurable pluginConfigurable;
	private final Map<String, DiameterPlugin> nameToPlugin;
	
	public DiameterPluginManager(PluginConfigurable pluginConfigurable) {
		this.pluginConfigurable = pluginConfigurable;
		this.nameToPlugin = new HashMap<String, DiameterPlugin>();
	}

	public void init() {
		pluginConfigurable.getUniversalDiameterPluginConfigurable().createPlugin(this.nameToPlugin);
		pluginConfigurable.getDiaGroovyPluginConfigurable().createPlugin(this.nameToPlugin);
		pluginConfigurable.getDiameterTransactionLoggerConfigurable().createPlugin(this.nameToPlugin);
		
		try {
			PluginDetailProvider.getInstance().registerDetailProvider(new DiameterPluginsDetailProvider());
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().warn(MODULE, "Error in registering detail provider for diameter plugins. Reason: " + e.getMessage());
		}
	}

	public Map<String, DiameterPlugin> getNameToPluginMap() {
		return nameToPlugin;
	}
	
	private class DiameterPluginsDetailProvider extends DetailProvider {

		@Override
		public String execute(String[] parameters) {
			TableFormatter tableFormatter = new TableFormatter(new String[] {"NAME"}, new int[] {50});
			for (DiameterPlugin plugin: nameToPlugin.values()) {
				tableFormatter.addRecord(new String[] {plugin.getName()});
			}
			return tableFormatter.getFormattedValues();
		}

		@Override
		public String getHelpMsg() {
			return "Shows created diameter plugins";
		}
		
		@Override
		public String getDescription() {
			return "Shows created diameter plugins";
		}

		@Override
		public String getKey() {
			return "diameter";
		}

		@Override
		public HashMap<String, DetailProvider> getDetailProviderMap() {
			return new HashMap<String, DetailProvider>(0);
		}
	}
}
