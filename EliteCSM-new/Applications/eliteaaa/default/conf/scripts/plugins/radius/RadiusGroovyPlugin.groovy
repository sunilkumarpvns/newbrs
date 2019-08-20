import com.elitecore.aaa.core.plugins.conf.PluginDetail.PluginInfo
import com.elitecore.aaa.core.server.AAAServerContext
import com.elitecore.aaa.radius.plugins.script.RadiusScriptPlugin
import com.elitecore.aaa.radius.service.RadServiceRequest
import com.elitecore.aaa.radius.service.RadServiceResponse
import com.elitecore.commons.logging.LogManager
import com.elitecore.core.commons.plugins.PluginContext
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity
import com.elitecore.core.commons.util.db.DBConnectionManager
import com.elitecore.core.commons.util.db.DatabaseInitializationException
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException
import com.elitecore.core.commons.util.db.datasource.DBDataSource
import com.elitecore.core.serverx.manager.cache.CacheConstants
import com.elitecore.core.serverx.manager.cache.CacheDetail
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider
import com.elitecore.core.serverx.manager.cache.Cacheable
import com.elitecore.coreradius.commons.attributes.AddressAttribute
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute
import com.elitecore.coreradius.commons.packet.RadiusPacket
import com.elitecore.coreradius.commons.util.Dictionary
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants

/**
 * This is the plugin that will be added dynamically in the EliteAAA Framework.
 * 
 * Any new external GROOVY plugin to be added must be kept in <b>ELITEAAA_HOME/scripts/plugins</b>
 * directory or else the plugin will not be added.
 * 
 * All the Radius External GROOVY Plugins should extend <b><code>RadiusScriptPlugin</code></b>
 * class otherwise the plugin will not be loaded.
 *
 * @author narendra.pathai
 *
 */
class GroovyPlugin extends RadiusScriptPlugin{

	CacheConf cache = new CacheConf();
	PluginInfo info;
	
	public GroovyPlugin(PluginContext context, PluginInfo info){
		super(context);
		this.info = info;
	}

	/**
	 * This is the most important method of plugin. The name provided in this method will be used to
	 * identify and call the plugin. This name should be provided in service configuration for the plugin
	 * to be applied.
	 * Avoid using blank spaces in name or else the output can be undesirable.
	 * For example the following plugin has name <b>GROOVY_PLUGIN</b> and it is a
	 * radius plugin. So to configure it as both pre and post plugin:
	 * In <b>rad-auth.xml</b>
	 * <pre-plugins>
	 * 	<pre-plugin>GROOVY_PLUGIN</pre-plugin>
	 * </pre-plugins>
	 * <post-plugins>
	 * 	<post-plugin>GROOVY_PLUGIN</post-plugin>
	 * </post-plugins>
	 */
	@Override
	public String getPluginName() {
		return "RADIUS_GROOVY_PLUGIN";
	}

	/**
	 * This method is used to get help regarding the description of the plugin.
	 */
	@Override
	public String getPluginDescription() {
		return "This is a Radius Groovy Plugin";
	}

	/**
	 * This method will be called when the plugin is getting loaded in the framework at the time
	 * of server startup.
	 * Any tasks of reading the configuration and extra tasks should be done at this place
	 * This method will be called before call to handlePreRequest and handlePostRequest.
	 */
	@Override
	public void init() {
		getPluginContext().getServerContext().registerCacheable(cache);
		//		initDataSource("Data Source Name");
	}

	/*
	 * This method will initialize the Data Source by passing data source name.
	 *
	 * Method alive in ESIEventListner will execute when datasource is marked alive
	 * Method dead in ESIEventListner will execute when datasource is marked dead
	 *
	 */
	private void initDataSource(String dataSourceName) throws DatabaseInitializationException, DatabaseTypeNotSupportedException  {

		DBDataSource dataSource = ((AAAServerContext)context.getServerContext()).getServerConfiguration().getDatabaseDSConfiguration().getDataSourceByName(dataSourceName);
		DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(dataSource.getDataSourceName());
		dbConnectionManager.init(dataSource, context.getServerContext().getTaskScheduler());
	}

	/**
	 * This method will be called as soon as the request is received and before any further
	 * handling of request.
	 * The request will be served by radius services only after passing through this call.
	 */
	@Override
	public void handlePreRequest(RadServiceRequest request,RadServiceResponse response, String argument, PluginCallerIdentity callerID) {

		//do not change this code
		RadiusPacket requestPacket = new RadiusPacket();
		requestPacket.setBytes(request.getRequestBytes());
		requestPacket.addInfoAttributes(request.getInfoAttributes());


		IRadiusAttribute ipAddressAttribute = request.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS);

		//add as many similar conditions as required
		if(checkSubnet(ipAddressAttribute,"10.106.1.0/24")){
			addAttribute(requestPacket, RadiusAttributeConstants.USER_NAME, "1");
		}


		//do not change this code
		requestPacket.refreshPacketHeader();
		request.setRequestBytes(requestPacket.getBytes());

	}

	//do not change this method
	private boolean checkSubnet(IRadiusAttribute attribute, String subnetRepresentation){
		if(attribute == null){
			LogManager.getLogger().info("RADIUS_GROOVY_PLUGIN", "Attribute not found.");
			return false;
		}

		IRadiusAttribute newAddressAttribute = new AddressAttribute();
		newAddressAttribute.setStringValue(subnetRepresentation);
		return attribute.equals(newAddressAttribute);
	}

	private void addAttribute(RadiusPacket requestPacket, int attributeID, String value){
		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(attributeID);
		if(attribute != null){
			attribute.setStringValue(value);
			requestPacket.addAttribute(attribute);
		}
	}
	/**
	 * This method will be called when the radius processing is completed and the response is
	 * about to be sent.
	 * The response will be sent by radius services only after passing through this call.
	 */
	@Override
	public void handlePostRequest(RadServiceRequest request,RadServiceResponse response, String argument, PluginCallerIdentity callerID) {
	}


	//this is the class that maintains the logic for reading and reloading the configuration like XML
	class CacheConf implements Cacheable{

		//you get access to this method when reload cache method is fired from CLI
		//you need to apply the business logic for reloading the configuration here
		@Override
		public CacheDetail reloadCache() {
			CacheDetailProvider cacheDetail = new CacheDetailProvider();
			cacheDetail.setName(getPluginName());
			cacheDetail.setResultCode(CacheConstants.SUCCESS);
			cacheDetail.setDescription("Groovy plugin cache reloaded successfully");
			return cacheDetail;
		}

		@Override
		public String getName() {
			return getPluginName();
		}
	}
}
