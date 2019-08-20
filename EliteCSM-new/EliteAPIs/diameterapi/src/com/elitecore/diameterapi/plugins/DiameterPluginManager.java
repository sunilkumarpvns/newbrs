package com.elitecore.diameterapi.plugins;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.BasePlugin;
import com.elitecore.core.commons.plugins.Plugin;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.plugins.script.ScriptPlugin;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.plugins.script.DiameterGroovyPlugin;
import com.elitecore.diameterapi.plugins.script.DiameterScriptPlugin;

public class DiameterPluginManager {
	
	private static final String MODULE = "DIA-PLUGIN-MNGR";
	private static Map<String, DiameterPlugin> plugins = new HashMap<String, DiameterPlugin>();
	public static final DiameterPluginManager NO_PLUGIN_MANAGER = new DiameterPluginManager();
	
	private List<PluginEntryDetail> inPlugins;
	private List<PluginEntryDetail> outPlugins;
	
	public DiameterPluginManager() {
		this.inPlugins = new ArrayList<PluginEntryDetail>();
		this.outPlugins = new ArrayList<PluginEntryDetail>();
	}
	
	public DiameterPluginManager(Map<String, DiameterPlugin>  plugins) {
		this();
		DiameterPluginManager.plugins.putAll(plugins);
	}
	
	public void registerInPlugins(List<PluginEntryDetail> inPlugInList) {
		this.inPlugins = inPlugInList;	
	}

	public void registerOutPlugins(List<PluginEntryDetail> outPluginList) {
		this.outPlugins = outPluginList;	
	}
	
	public void applyInPlugins(DiameterRequest diameterRequest,
			DiameterAnswer diameterAnswer, ISession session) {
		for (PluginEntryDetail pluginData : inPlugins) {
			String name = pluginData.getPluginName();
			DiameterPlugin plugin = plugins.get(name);
			if (plugin != null) {
				plugin.handleInMessage(diameterRequest, diameterAnswer, session, pluginData.getPluginArgument(), pluginData.getCallerId());
			}
		}
	}
	
	public void applyOutPlugins(DiameterRequest diameterRequest,
			DiameterAnswer diameterAnswer, ISession session) {
		for (PluginEntryDetail pluginData : this.outPlugins) {
			String name = pluginData.getPluginName();
			DiameterPlugin plugin = plugins.get(name);
			if (plugin != null) {
				plugin.handleOutMessage(diameterRequest, diameterAnswer, session, pluginData.getPluginArgument(), pluginData.getCallerId());
			}
		}
	}
	
	/**
	 * Creates the instance of plugin using the plugin info and passes the configuration object to it
	 */
	public static void createAndRegisterPlugin(PluginInfo pluginInfo, PluginConfiguration pluginConfiguration, final ServerContext serverContext) throws Exception{
		Plugin plugin = createPluginObject(pluginInfo.getPluginClass(),pluginInfo,pluginConfiguration,serverContext);
		if(plugin instanceof BaseDiameterPlugin){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Initializing Diameter Plugin: "+plugin.toString());
			plugin.init();
			plugins.put(pluginInfo.getPluginName(), (DiameterPlugin) plugin);
		}else{
			LogManager.getLogger().debug(MODULE, plugin.getPluginName() + ": the plugin does not extend BaseDiameterPlugin.");
		}
	}
	
	/**
	 *	Scans the external plugins path and loads the plugins with external scripts manager 
	 */
	public static void createAndRegisterExternalPlugins(final ServerContext serverContext){
		//loading external plugins
		if(serverContext.getExternalScriptsManager() != null){
			String externalDiameterPluginsPath = serverContext.getServerHome() + File.separator + "scripts" + File.separator + "plugins" + File.separator + "diameter";
			PluginContext externalPluginContext = (PluginContext) createPluginContex(serverContext, null);
			List<ScriptPlugin> externalPlugins = serverContext.getExternalScriptsManager().loadExternalPlugins(externalDiameterPluginsPath,externalPluginContext);
			if(externalPlugins != null){
				for(ScriptPlugin plugin : externalPlugins){
					try{
						if(plugin instanceof DiameterScriptPlugin){
							DiameterGroovyPlugin groovyPlugin = new DiameterGroovyPlugin((DiameterScriptPlugin) plugin, externalPluginContext, createPluginInfo(plugin));
							groovyPlugin.init();
							plugins.put(groovyPlugin.getName(), groovyPlugin);
						}else{
							LogManager.getLogger().debug(MODULE, plugin.getPluginName() + ": Either plugin does not extend DiameterScriptPlugin or plugin location is incorrect.");
						}
					}catch(InitializationFailedException ex){
						LogManager.getLogger().debug(MODULE, plugin.getPluginName() + ": Error in initializing plugin." + ex.getMessage());
					}
				}
			}
		}
	}
	
	private static BasePlugin createPluginObject(String pluginClassName,PluginInfo pluginInfo,PluginConfiguration pluginConfiguration,ServerContext serverContext) 
	throws ClassNotFoundException, 
	SecurityException, 
	NoSuchMethodException, 
	IllegalArgumentException, 
	InstantiationException, 
	IllegalAccessException, 
	InvocationTargetException{
	
	
		Class<?> classParameters[];
		Constructor<?> constructor = null;
		BasePlugin pluginObj = null;
		
		classParameters = new Class[2];
		classParameters[0] = PluginContext.class;
		classParameters[1] = PluginInfo.class;
		
		Class<?> c;
		
		c = Class.forName(pluginClassName);
		
		constructor = c.getConstructor(classParameters);
		Object obj[] = new Object[2];
		obj[0] = createPluginContex(serverContext,pluginConfiguration);
		obj[1] = pluginInfo;
		
		pluginObj = (BasePlugin) constructor.newInstance(obj);
		return pluginObj;
	}
	
	private static Object createPluginContex(final ServerContext serverContext,
			final PluginConfiguration pluginConfiguration) {
		return new PluginContext(){

			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return pluginConfiguration;
			}

			@Override
			public ServerContext getServerContext() {
				return serverContext;
			}
			
		};
	}

	private static PluginInfo createPluginInfo(ScriptPlugin scriptPlugin){
		PluginInfo pluginInfo = new PluginInfo();
		pluginInfo.setPluginName(scriptPlugin.getPluginName());
		pluginInfo.setDescription(scriptPlugin.getPluginDescription());
		return pluginInfo;
	}

}
