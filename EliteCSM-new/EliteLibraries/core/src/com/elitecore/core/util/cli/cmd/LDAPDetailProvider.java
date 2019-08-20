package com.elitecore.core.util.cli.cmd;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.commons.utilx.ldap.LDAPConnectionManager;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.core.util.cli.TableFormatter;

public abstract class LDAPDetailProvider extends DetailProvider {

	protected static final String DESCRIPTION = "List details of LDAP DataSources";
	protected static final String VIEW = "-view";
	private static final String HELP = "?";
	private static final String HELP_OPTION = "-help";
	private static final String NA = "N.A.";
	private static final String NOT_INITIALIZED = "NOT_INITIALIZED";
	private static final String DEAD = "DEAD";
	private static final String ALIVE = "ALIVE";
	private static final String LDAP_CONFIGURATION_NOT_FOUND = "LDAP CONFIGURATION NOT FOUND";
	private static final String NO_SUCH_LDAP_DATASOURCE_FOUND = "NO SUCH LDAP DATASOURCE FOUND";
	private static final String INVALID_OPTION = "Invalid Option";

	private LinkedHashMap<String, DetailProvider> detailProviderMap;
	private final LDAPConnectionManagerProvider connectionProvider;

	private static final String[] headers = { "LDAP DATASOURCES",
			"ACTIVE", "MIN_SIZE",
			"MAX_SIZE", "STATUS" };
	private static final int[] width = { 26, 6, 8, 8, 15 };
	private static final int[] columnAlignment = { TableFormatter.CENTER, TableFormatter.CENTER,
			TableFormatter.CENTER, TableFormatter.CENTER,
			TableFormatter.CENTER};

	
	public LDAPDetailProvider() {
		this(new DefaultLDAPConnectionProvider());
	}

	public LDAPDetailProvider(LDAPConnectionManagerProvider connectionProvider) {
		detailProviderMap = new LinkedHashMap<String, DetailProvider>();
		this.connectionProvider = connectionProvider;
	}

	@Override
	public String execute(String[] parameters) {

		
		if (parameters == null || parameters.length == 0) {
			return getHelpMsg();
		}
		if (HELP.equals(parameters[0])  || HELP_OPTION.equalsIgnoreCase(parameters[0]) ) {
			return getHelpMsg();
		}
		
		Map<String, LDAPDataSource> ldapDataSourceMap = getLDAPDatasourceMap();
		if (ldapDataSourceMap == null || ldapDataSourceMap.isEmpty()) {
			return LDAP_CONFIGURATION_NOT_FOUND;
		}
		
		if (VIEW.equalsIgnoreCase(parameters[0])) {
			if (parameters.length == 1) {
				return view(null);
			} else {
				return view(Arrays.copyOfRange(parameters, 1, parameters.length));
			}
		}
		if (detailProviderMap.containsKey(parameters[0].toLowerCase()) == false) {
			return INVALID_OPTION + getHelpMsg();
		} else {
			return detailProviderMap.get(parameters[0].toLowerCase()).execute(Arrays.copyOfRange(parameters, 1, parameters.length));
		}
		
	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getKey() + "[<options>]");
		out.println("Description : " + getDescription());
		out.println("Possible Options:");
		out.println();
		out.println(EliteBaseCommand.fillChar("\t" + VIEW, 25) + "-Shows details of LDAP datasource");
		if (detailProviderMap.isEmpty() == false) {
			for (DetailProvider detailProvider : detailProviderMap.values()) {
				out.println(EliteBaseCommand.fillChar( "\t" + detailProvider.getKey(), 25 )	+ "-" + detailProvider.getDescription());
			}
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return "-l";
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("'" + getKey() + "':{'" + HELP_OPTION + "':{}, '" + VIEW + "':{ '" + HELP_OPTION + "':{}");
		for (String datasourceName : getLDAPDatasourceMap().keySet()) {
			out.print(", '" + datasourceName + "':{} ");
		}
		out.print("}");
		
		for (DetailProvider provider : detailProviderMap.values()) {
			out.print(",");
			out.print(provider.getHotkeyHelp());
		}
		out.print("}");
		
		out.close();
		return writer.toString();

	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	private boolean isAlive(LDAPConnectionManager ldapConnectionManager) {
		
		try {
			LDAPConnection connection = ldapConnectionManager.getConnection();
			
			if (connection == null) {
				return false;
			}
			
			try {
				connection.disconnect();
			} finally {
				ldapConnectionManager.closeConnection(connection);
			}
			return true;
		} catch (LDAPException e) {
			LogManager.ignoreTrace(e);
			return false;
		}
	}

	private String view(String[] parameters) {
		Map<String, LDAPDataSource> dataSourceMap = getLDAPDatasourceMap();
		Map<String, LDAPDataSource> datasourceParameterMap;
		if (parameters == null || parameters.length == 0) {
			return ldapFormat(dataSourceMap);
		} else {
			if(HELP.equals(parameters[0]) || HELP_OPTION.equalsIgnoreCase(parameters[0])){
				return getViewHelpMessage();
			}
			datasourceParameterMap = new HashMap<String, LDAPDataSource>();
			for (String commandParameter : parameters) {
				if (dataSourceMap.containsKey(commandParameter)) {
					datasourceParameterMap.put(commandParameter,
							dataSourceMap.get(commandParameter));
				}
			}
			if(datasourceParameterMap.isEmpty() == true){
				return NO_SUCH_LDAP_DATASOURCE_FOUND;
			}
			return ldapFormat(datasourceParameterMap);
		}

	}

	private String getViewHelpMessage(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : view [<options>]");
		out.println("Description : Shows details of LDAP datasource " );
		out.println("Possible options:\n");
		out.println();
		out.println(EliteBaseCommand.fillChar("\t [<LDAP Datasource Name>]", 30) + "-Shows details of LDAP datasource");
		out.close();
		return stringWriter.toString();
	}
	
	public String ldapFormat(Map<String, LDAPDataSource> datasourceParametersMap) {
		TableFormatter formatter = new TableFormatter(headers, width, columnAlignment);
		for (LDAPDataSource ldapDatasource : datasourceParametersMap.values()) {
			int active = -1;
			String status = getDSStatus(ldapDatasource.getDataSourceName());
			
			String [] data = new String[width.length];
			data[0] = ldapDatasource.getDataSourceName();
			data[1] = String.valueOf(active == -1 ? NA : active);
			data[2] = String.valueOf(ldapDatasource.getMinPoolSize());
			data[3] = String.valueOf(ldapDatasource.getMaxPoolSize());
			data[4] = status;
			formatter.addRecord(data);
		}
		return formatter.getFormattedValues();
	}

	private String getDSStatus(String dataSourceName) {
		LDAPConnectionManager ldapConnectioManager = connectionProvider.getConnectionManager(dataSourceName);
	    
	    if (ldapConnectioManager.isInitialize() == false) {
		return NOT_INITIALIZED;
	    }
	    
	    if (isAlive(ldapConnectioManager)) {
		return ALIVE;
	    }
	    
	    return DEAD;
	}

	@Override
	public void registerDetailProvider(DetailProvider detailprovider) throws RegistrationFailedException {

		if (detailprovider.getKey() == null) {
			throw new RegistrationFailedException("Failed to register detail provider. Reason: key is not specified.");
		}

		if (getDetailProviderMap().containsKey(detailprovider.getKey().toLowerCase())) {
			throw new RegistrationFailedException( "Failed to register detail provider. Reason: Detail Provider already contains detail provider with Key : "
							+ detailprovider.getKey());
		}

		getDetailProviderMap().put(detailprovider.getKey().toLowerCase(),
				detailprovider);
	}

	protected abstract Map<String, LDAPDataSource> getLDAPDatasourceMap();

}
