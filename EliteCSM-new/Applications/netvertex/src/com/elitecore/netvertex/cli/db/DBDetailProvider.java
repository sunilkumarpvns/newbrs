package com.elitecore.netvertex.cli.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;

public class DBDetailProvider extends DetailProvider {

	private static final String DESCRIPTION = "List the details of DB datasource";
	private static final String DB = "-db";
	private static final String VIEW = "-view";
	private static final String HELP = "?";
	private static final String HELP_OPTION = "-help";
	private static final String DEAD = "DEAD";
	private static final String ALIVE = "ALIVE";
	private static final String NOT_INITIALIZED = "NOT_INITIALIZED";
	private static final String INVALID_OPTION = "Invalid Option";
	private static final String NO_SUCH_DB_DATASOURCE_FOUND = "NO SUCH DB DATASOURCE FOUND";
	private NetVertexServerContext serverContext;
	
	private LinkedHashMap<String, DetailProvider> detailProviderMap;

	private static final String[] headers = { "DB DATASOURCES",
			"ACTIVE", "MIN_SIZE",
			"MAX_SIZE", "STATUS" };
	private static final int[] width = { 26, 6, 8, 8, 15 };
	private static final int[] columnAlignment = { TableFormatter.CENTER,
			TableFormatter.CENTER,
			TableFormatter.CENTER, TableFormatter.CENTER,
			TableFormatter.CENTER};

	public DBDetailProvider( NetVertexServerContext serverContext) {
		this.serverContext = serverContext;
		detailProviderMap = new LinkedHashMap<String, DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {

		if (parameters == null || parameters.length == 0) {
			return getHelpMsg();
		}	
		if (HELP_OPTION.equalsIgnoreCase(parameters[0]) || HELP.equals(parameters[0]) ) {
			return getHelpMsg();
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
			return detailProviderMap.get(parameters[0].toLowerCase()).execute(Arrays.copyOfRange(parameters, 1,parameters.length));
		}

	}

	private String getViewHelpMessage(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : view [<options>]");
		out.println("Description : Shows details of DB datasource " );
		out.println("Possible options:\n");
		out.println();
		out.println(EliteBaseCommand.fillChar("\t [<DB Datasource Name>]", 30) + "-Shows details of DB datasource");
		out.close();
		return stringWriter.toString();
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getKey() + "[<options>]");
		out.println("Description : " + getDescription());
		out.println("Possible options:");
		out.println();
		out.println(EliteBaseCommand.fillChar("\t" + VIEW, 25) + "-Shows details of DB datasource");
		if (detailProviderMap.isEmpty() == false) {
			for (DetailProvider detailProvider : detailProviderMap.values()) {
				out.println(EliteBaseCommand.fillChar("\t" + detailProvider.getKey(), 25) + "-" + detailProvider.getDescription());
			}
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return DB;
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("'" + getKey() + "':{'" + HELP_OPTION + "':{}, '" + VIEW + "':{ '" + HELP_OPTION + "':{}");

		Map<String, DBDataSource> dataSourceMap = getDBDatasourceMap();
		if (dataSourceMap != null && dataSourceMap.isEmpty() == false) {
			for (String datasourceName : getDBDatasourceMap().keySet()) {
				out.print(", '" + datasourceName + "':{} ");
			}
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

	private String view(String[] parameters) {
		Map<String, DBDataSource> dataSourceMap = getDBDatasourceMap();
		Map<String, DBDataSource> datasourceParameterMap;
		if (parameters == null || parameters.length == 0) {
			return dbFormat(dataSourceMap);
		} else {
			if(HELP.equals(parameters[0]) || HELP_OPTION.equalsIgnoreCase(parameters[0]) ){
				return getViewHelpMessage();
			}
			datasourceParameterMap = new HashMap<String, DBDataSource>();
			for (String commandParameter : parameters) {
				if (dataSourceMap.containsKey(commandParameter)) {
					datasourceParameterMap.put(commandParameter, dataSourceMap.get(commandParameter));
				}
			}
			if(datasourceParameterMap.isEmpty() == true){
				return NO_SUCH_DB_DATASOURCE_FOUND;
			}
			return dbFormat(datasourceParameterMap);
		}

	}
	
	protected String dbFormat(Map<String, DBDataSource> dbDatasourceMapParameter) {
		TableFormatter formatter = new TableFormatter(headers, width, columnAlignment);
		String[] data = new String[width.length];
		for (DBDataSource dbDatasource : dbDatasourceMapParameter.values()) {
			
			String status = NOT_INITIALIZED;
			int active = -1;
			List<String> dataSources = DBConnectionManager.getDataSources();
			if (dataSources.contains(dbDatasource.getDataSourceName())) {
				if (isInitialized(dbDatasource.getDataSourceName()) == true) {
					if (isAlive(dbDatasource.getDataSourceName())) {
						active = DBConnectionManager.getInstance(dbDatasource.getDataSourceName()).getNumberOfActiveConnections();
					}
					status = checkDSStatus(dbDatasource.getDataSourceName());
				}
			}
			data[0] = dbDatasource.getDataSourceName();
			data[1] = String.valueOf((active == -1 ? ("N.A.") : active));
			data[2] = String.valueOf(dbDatasource.getMinimumPoolSize());
			data[3] = String.valueOf(dbDatasource.getMaximumPoolSize());
			data[4] = status;
			formatter.addRecord(data);
		}

		return formatter.getFormattedValues();
	}

	private boolean isAlive(String dataSourceName) {
		return DBConnectionManager.getInstance(dataSourceName).getTransactionFactory().isAlive();
	}

	private boolean isInitialized(String dbDatasourceName) {
		return Objects.nonNull(DBConnectionManager.getInstance(dbDatasourceName).getTransactionFactory());
	}
	
	private String checkDSStatus(String dbDatasourceName) {
		
		if(isInitialized(dbDatasourceName) == false){
			return NOT_INITIALIZED;
		}

		
		return isAlive(dbDatasourceName) ? ALIVE : DEAD;
			
	}

	@Override
	public void registerDetailProvider(DetailProvider detailprovider)
			throws RegistrationFailedException {

		if (detailprovider.getKey() == null) {
			throw new RegistrationFailedException(
					"Failed to register detail provider. Reason: key is not specified.");
		}

		if (getDetailProviderMap().containsKey(detailprovider.getKey().toLowerCase())) {
			throw new RegistrationFailedException("Failed to register detail provider. Reason: Detail Provider already contains detail provider with Key : "
							+ detailprovider.getKey());
		}

		getDetailProviderMap().put(detailprovider.getKey().toLowerCase(),detailprovider);
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	protected Map<String, DBDataSource> getDBDatasourceMap(){
		Map<String, DBDataSource> dataSourceMap = new HashMap<String, DBDataSource>();
		DBDataSource dataSource = NetVertexDBConnectionManager.getInstance().getDataSource();
		if (dataSource.getConnectionURL().contains(CommonConstants.VOLTDB) == false) {
			dataSourceMap.put(dataSource.getDataSourceName(), dataSource);
		}
		if (serverContext.getServerConfiguration().getDatabaseDSConfiguration() != null) {
			Map<String, DBDataSourceImpl> tempDataSourceMap = serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDatasourceNameMap();
			if (tempDataSourceMap != null) {
				for (Map.Entry<String, DBDataSourceImpl> entry : tempDataSourceMap.entrySet()) {
					if (entry.getValue().getConnectionURL().contains(CommonConstants.VOLTDB) == false) {
						dataSourceMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		return dataSourceMap;
	}
}
