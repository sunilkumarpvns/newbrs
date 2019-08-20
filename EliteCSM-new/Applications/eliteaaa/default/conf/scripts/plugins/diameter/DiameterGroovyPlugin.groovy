package plugins.diameter

import com.elitecore.aaa.core.plugins.conf.PluginDetail.PluginInfo;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.plugins.script.DiameterScriptPlugin;
/**
 * This is the plugin that will be added dynamically in the EliteAAA Framework.
 * 
 * Any new Diameter external GROOVY plugins to be added must be kept in <b>ELITEAAA_HOME/scripts/plugins/diameter</b>
 * directory or else the plugin will not be added.
 * 
 * All the Diameter External GROOVY Plugins should extend <b><code>DiameterScriptPlugin</code></b>
 * class otherwise the plugin will not be loaded.
 * 
 * 
 * @author narendra.pathai
 *
 */
class DiameterGroovyPlugin extends DiameterScriptPlugin{

	PluginInfo info;
	
	public DiameterGroovyPlugin(PluginContext context, PluginInfo info){
		super(context);
		this.info = info;
	}
	
	/**
	* This is the most important method of plugin. The name provided in this method will be used to
	* identify and call the plugin. This name should be provided in service configuration for the plugin
	* to be applied.
	* Avoid using blank spaces in name or else the output can be undesirable.
	* For example the following plugin has name <b>DIA_GROOVY_PLGN</b> and it is a
	* DIAMETER plugin. So to configure it as both in and out plugin:
	* Configure following in diameter-base.xml or any specific application which supports plugins
	* <plugin-list>
	*	<in-plugins>
	*		<in-plugin>DIA_GROOVY_PLGN</in-plugin>
	*	</in-plugins>
	*	<out-plugins>
	*		<out-plugin>DIA_GROOVY_PLGN</out-plugin>
	*	</out-plugins>
	</plugin-list>
	*/
	@Override
	public String getPluginName() {
		return "DIA_GROOVY_PLGN";
	}

	/**
	 * This method is used to get help regarding the description of the plugin.
	 */
	@Override
	public String getPluginDescription() {
		return "This is diameter groovy plugin";
	}

	/**
	 * This method will be called when the plugin is getting loaded in the framework at the time
	 * of server startup.
	 * Any tasks of reading the configuration and extra tasks should be done at this place
	 * This method will be called before call to handleInMessage and handleOutMessage.
	 */
	@Override
	public void init() {
//		initDataSource("Data Source Name");
	}

	/*
	 * This method will initialize the Data Source by passing data source name.
	 * 
	 * Method alive in ESIEventListner will execute when datasource is marked alive
	 * Method dead in ESIEventListner will execute when datasource is marked dead
	 * 
	 */
	private void initDataSource(String dataSourceName) throws DatabaseInitializationException, DatabaseTypeNotSupportedException {
		
		DBDataSource dataSource = ((AAAServerContext)getPluginContext().getServerContext()).getServerConfiguration().getDatabaseDSConfiguration().getDataSourceByName(dataSourceName);
		DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(dataSource.getDataSourceName());
		dbConnectionManager.init(dataSource, getPluginContext().getServerContext().getTaskScheduler());
	}
	
	/**
	 * This method will be called as soon as the request is received and before any further
	 * handling of request.
	 * The request will be served by diameter applications only after passing through this call.
	 */
	@Override
	public void handleInMessage(DiameterPacket request, DiameterPacket answer, String argument, PluginCallerIdentity callerID) {
	
	}

	/**
	* This method will be called when the radius processing is completed and the response is
	* about to be sent.
	* The response will be sent by diameter services only after passing through this call.
	*/
	@Override
	public void handleOutMessage(DiameterPacket request, DiameterPacket answer, String argument, PluginCallerIdentity callerID) {
		
	}

}
