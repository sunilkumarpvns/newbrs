package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DataSourceNotInitializedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;

public abstract class DBDetailProvider extends DetailProvider {

	private static final String DESCRIPTION = "List the details of DB datasource";
	private static final String DB = "-db";
	private static final String VIEW = "-view";
	private static final String HELP = "?";
	private static final String HELP_OPTION = "-help";
	private static final String DEAD = "DEAD";
	private static final String ALIVE = "ALIVE";
	private static final String NOT_INITIALIZED = "NOT_INITIALIZED";
	private static final String DB_CONFIGURATION_NOT_FOUND = "DB CONFIGURATION NOT FOUND";
	private static final String INVALID_OPTION = "Invalid Option";
	private static final String NO_SUCH_DB_DATASOURCE_FOUND = "NO SUCH DB DATASOURCE FOUND";

	private LinkedHashMap<String, DetailProvider> detailProviderMap;

	private static final String[] headers = { "DB DATASOURCES", "TYPE",
			"ACTIVE", "MIN_POOL_SIZE",
			"MAX_POOL_SIZE", "STATUS" };
	private static final int[] width = { 30, 15, 10, 15, 15, 10 };
	private static final int[] columnAlignment = { TableFormatter.CENTER,
			TableFormatter.CENTER, TableFormatter.CENTER,
			TableFormatter.CENTER, TableFormatter.CENTER,
			TableFormatter.CENTER};

	public DBDetailProvider() {
		detailProviderMap = new LinkedHashMap<String, DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {

		Map<String, DBDataSource> dataSourceMap = getDBDatasourceMap();
		if (dataSourceMap == null || dataSourceMap.isEmpty() == true) {
			return DB_CONFIGURATION_NOT_FOUND;
		}

		if (parameters != null && parameters.length > 0) {

			if (HELP_OPTION.equalsIgnoreCase(parameters[0]) || HELP.equals(parameters[0]) ) {
				return getHelpMsg();
			}
			if (parameters[0].equalsIgnoreCase(VIEW)) {
				if (parameters.length == 1) {
					return view(null);
				} else {
					String[] commmandParameters = new String[parameters.length - 1];
					System.arraycopy(parameters, 1, commmandParameters, 0, commmandParameters.length);
					return view(commmandParameters);
				}
			}
			if (detailProviderMap.containsKey(parameters[0].toLowerCase()) == false) {
				return INVALID_OPTION + getHelpMsg();
			} else {
				String[] commandParameter = new String[parameters.length - 1];
				System.arraycopy(parameters, 1, commandParameter, 0, commandParameter.length);
				return detailProviderMap.get(parameters[0].toLowerCase()).execute(commandParameter);
			}
		} else {
			return getHelpMsg();
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
		out.println("Possible options:\n");
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
		out.print("'" + getKey() + "':{'" + HELP_OPTION + "':{}, '" + VIEW + "':{'" + HELP_OPTION + "':{}");

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
		StringBuilder builder = new StringBuilder();
		String responseBuilder = "";
		TableFormatter formatter = new TableFormatter(headers, width, columnAlignment);
		String[] data = new String[width.length];
		for (DBDataSource dbDatasource : dbDatasourceMapParameter.values()) {

			String status = NOT_INITIALIZED;
			int active = -1;
			if (DBConnectionManager.isExist(dbDatasource.getDataSourceName())) {
				active = DBConnectionManager.getInstance(dbDatasource.getDataSourceName()).getNumberOfActiveConnections();
				status = checkDSStatus(dbDatasource.getDataSourceName());
			}
			data[0] = dbDatasource.getDataSourceName();
			data[1] = "DB DATASOURCE";
			data[2] = String.valueOf((active == -1 ? ("N.A.") : active));
			data[3] = String.valueOf(dbDatasource.getMinimumPoolSize());
			data[4] = String.valueOf(dbDatasource.getMaximumPoolSize());
			data[5] = status;
			formatter.addRecord(data);

		}
		builder.append(formatter.getFormattedValues());
		responseBuilder += builder.toString();
		return responseBuilder;
	}

	private String checkDSStatus(String dbDatasourseName) {
		
		Connection connection = null;
		try {
			connection = DBConnectionManager.getInstance(dbDatasourseName).getConnection();
			return ALIVE;
		} catch (DataSourceException e) {
			//TODO MALAV: HARSH: NARENDRA: check with devang that we can return FULL instead of ALIVE
			return DBConnectionManager.getInstance(dbDatasourseName).isDBDownSQLException(e) ? DEAD : ALIVE;
		} catch (DataSourceNotInitializedException e) {
			return NOT_INITIALIZED;
		} catch( Exception e){
			return DEAD;
		} finally {
			DBUtility.closeQuietly(connection);
		}
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

	protected abstract Map<String, DBDataSource> getDBDatasourceMap();
}
