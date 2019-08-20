package com.elitecore.aaa.radius.plugins.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.conf.AAAPluginConfManager;
import com.elitecore.aaa.core.plugins.PluginDetailProvider;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.commons.plugins.BasePlugin;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;

/**
 * 
 * @author khushbu.chauhan
 * @author narendra.pathai
 *
 */
public class RadPluginManager implements ReInitializable {
	
	private static final String MODULE = "RAD-PLGN-MGR";

	private final ServerContext serverContext;
	private PluginContext pluginContext;
	private PluginConfigurable pluginConfigurable;
	private Map<String, RadPlugin<RadServiceRequest, RadServiceResponse>> nameToRadiusPlugin;
	
	public RadPluginManager(ServerContext serverContext, PluginConfigurable pluginConfigurable) {
		this.serverContext = serverContext;
		this.pluginContext = createPluginContext();
		this.pluginConfigurable = pluginConfigurable;
		this.nameToRadiusPlugin = new HashMap<String, RadPlugin<RadServiceRequest,RadServiceResponse>>();
	}

	public void init() {
		pluginConfigurable.getUniversalAuthPluginCofigurable().createPlugin(this.nameToRadiusPlugin);
		pluginConfigurable.getUniversalAcctPluginCofigurable().createPlugin(this.nameToRadiusPlugin);
		pluginConfigurable.getRadGroovyPluginConfigurable().createPlugin(this.nameToRadiusPlugin);
		pluginConfigurable.getRadiusTransactionLoggerConfigurable().createPlugin(this.nameToRadiusPlugin);
		pluginConfigurable.getQuotaManagementPluginConfigurable().createPlugin(this.nameToRadiusPlugin);
		pluginConfigurable.getUserStatisticAuthPluginConfigurable().createPlugin(this.nameToRadiusPlugin);
		
		try {
			PluginDetailProvider.getInstance().registerDetailProvider(new RadiusPluginsDetailProvider());
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().warn(MODULE, "Error in registering detail provider for radius plugins. Reason: " + e.getMessage());
		}
	}
	
	public final RadPluginRequestHandler createRadPluginReqHandler(List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList) {
		return new RadPluginRequestHandlerImpl(prePluginList, postPluginList);
	}
	
	private RadPlugin<RadServiceRequest, RadServiceResponse> getPlugin(String pluginName) {
		RadPlugin<RadServiceRequest, RadServiceResponse> radPlugin = nameToRadiusPlugin.get(pluginName);
		if (radPlugin == null) {
			radPlugin = createPluginFromSystemMapping(pluginName);
			if (radPlugin != null) {
				nameToRadiusPlugin.put(pluginName, radPlugin);
			}
		}
		return radPlugin;
	}
	

	private PluginContext createPluginContext(){
		return new PluginContext() {
			
			@Override
			public ServerContext getServerContext() {
				return serverContext;
			}
			
			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {				
				AAAPluginConfManager pluginConfigurationManager = ((AAAServerContext)getServerContext()).getServerConfiguration().getPluginManagerConfiguration();
				
				return pluginConfigurationManager != null
						? pluginConfigurationManager.getRadPluginConfiguration(pluginName) 
						: null;
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	private RadPlugin<RadServiceRequest, RadServiceResponse> createPluginObject(String pluginClassName,PluginInfo pluginInfo) 
	throws ClassNotFoundException, 
	SecurityException, 
	NoSuchMethodException, 
	IllegalArgumentException, 
	InstantiationException, 
	IllegalAccessException, 
	InvocationTargetException{
	
	
		Class<?> classParameters[];
		Constructor<?> constructor = null;
		RadPlugin<RadServiceRequest, RadServiceResponse> pluginObj = null;
		
		classParameters = new Class[2];
		classParameters[0] = PluginContext.class;
		classParameters[1] = PluginInfo.class;
		
		Class<?> c;
		
		c = Class.forName(pluginClassName);
	
		constructor = c.getConstructor(classParameters);
		Object obj[] = new Object[2];
		obj[0] = this.pluginContext;
		obj[1] = pluginInfo;
		
		pluginObj = (RadPlugin<RadServiceRequest, RadServiceResponse>) constructor.newInstance(obj);
		return pluginObj;
	}
	
	protected RadPlugin<RadServiceRequest, RadServiceResponse> createPluginFromSystemMapping(String pluginName) {
		if(pluginName ==null ||!(pluginName.trim().length() >0)){
			return null;
		}
		PluginInfo pluginInfo = getPluginInfo(pluginName);
		if(pluginInfo!=null){

			try{
				RadPlugin<RadServiceRequest, RadServiceResponse> plugin = createPluginObject(pluginInfo.getPluginClass(), pluginInfo);
				plugin.init();
				serverContext.registerCacheable(((BasePlugin)plugin));
				return plugin;
			} catch (ClassNotFoundException e) {
				LogManager.getLogger().error(MODULE,"Error while creating Plugin:" + pluginInfo.toString() + " reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
			} catch (SecurityException e) {
				LogManager.getLogger().error(MODULE,"Error while creating Plugin:" + pluginInfo.toString() + " reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
			} catch (NoSuchMethodException e) {
				LogManager.getLogger().error(MODULE,"Error while creating Plugin:" + pluginInfo.toString() + " reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
			} catch (IllegalArgumentException e) {
				LogManager.getLogger().error(MODULE,"Error while creating Plugin:" + pluginInfo.toString() + " reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
			} catch (InstantiationException e) {
				LogManager.getLogger().error(MODULE,"Error while creating Plugin:" + pluginInfo.toString() + " reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
			} catch (IllegalAccessException e) {
				LogManager.getLogger().error(MODULE,"Error while creating Plugin:" + pluginInfo.toString() + " reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
			} catch (InvocationTargetException e) {
				LogManager.getLogger().error(MODULE,"Error while creating Plugin:" + pluginInfo.toString() + " reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
			} catch (InitializationFailedException e) {
				LogManager.getLogger().error(MODULE," Error while initializing Plugin: " + pluginInfo.toString()+" reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
			}

		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE," Plugin information not found for plugin: "+pluginName);
			}
		}
		return null;

	}
	private PluginInfo getPluginInfo(String pluginName) {
		//Guard statement
		AAAPluginConfManager aaaPluginManagerConfiguration = ((AAAServerContext)serverContext).getServerConfiguration().getPluginManagerConfiguration();
		if(aaaPluginManagerConfiguration == null)
			return null;
		
		PluginInfo plugInfo = null;
		List<PluginInfo> tempList = aaaPluginManagerConfiguration.getRadPluginInfoList();
		if(tempList != null && tempList.size()>0){
			PluginInfo temPluginInfo;
			for(int i=0;i<tempList.size();i++){
				temPluginInfo = tempList.get(i);
				if(pluginName.equals(temPluginInfo.getPluginName())){
					plugInfo = temPluginInfo;
					break;
				}
			}
		}		
		return plugInfo;
	}

	
	private class RadPluginRequestHandlerImpl implements RadPluginRequestHandler {
		private List<PluginEntryDetail> postPlugins;
		private List<PluginEntryDetail> prePlugins;
		
		public RadPluginRequestHandlerImpl(
				List<PluginEntryDetail> prePluginList,
				List<PluginEntryDetail> postPluginList) {
			prePlugins = prePluginList;
			postPlugins = postPluginList;
		}

		@Override
		public void handlePostRequest(RadServiceRequest request, RadServiceResponse response, ISession session) {
			for (PluginEntryDetail pluginData : this.postPlugins) {
				RadPlugin<RadServiceRequest, RadServiceResponse> radPlugin = getPlugin(pluginData.getPluginName());
				if (radPlugin != null) {
					try{
						radPlugin.handlePostRequest(request, response, pluginData.getPluginArgument(), pluginData.getCallerId(), session);
					}catch(Exception e){
						LogManager.getLogger().trace(MODULE,e);
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
							LogManager.getLogger().error(MODULE, e.getMessage());
						}
					}
				}
			}
		}

		@Override
		public void handlePreRequest(RadServiceRequest request, RadServiceResponse response, ISession session) {
			for (PluginEntryDetail pluginData : this.prePlugins) {
				RadPlugin<RadServiceRequest, RadServiceResponse> radPlugin = getPlugin(pluginData.getPluginName());
				if (radPlugin != null) {
					try{
						radPlugin.handlePreRequest(request, response, pluginData.getPluginArgument(), pluginData.getCallerId(), session);
					}catch(Exception e){
						LogManager.getLogger().trace(MODULE,e);
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
							LogManager.getLogger().error(MODULE, e.getMessage());
						}
					}
				}
			}
		}
	}


	@Override
	public void reInit() throws InitializationFailedException {
		
	}
	
	private class RadiusPluginsDetailProvider extends DetailProvider {

		@Override
		public String execute(String[] parameters) {
			TableFormatter tableFormatter = new TableFormatter(new String[] {"NAME"}, new int[] {50});
			for (RadPlugin<RadServiceRequest, RadServiceResponse> plugin: nameToRadiusPlugin.values()) {
				tableFormatter.addRecord(new String[] {plugin.getPluginName()});
			}
			return tableFormatter.getFormattedValues();
		}

		@Override
		public String getHelpMsg() {
			return "Shows created radius plugins";
		}
		
		@Override
		public String getDescription() {
			return "Shows created radius plugins";
		}

		@Override
		public String getKey() {
			return "radius";
		}

		@Override
		public HashMap<String, DetailProvider> getDetailProviderMap() {
			return new HashMap<String, DetailProvider>(0);
		}
	}
}