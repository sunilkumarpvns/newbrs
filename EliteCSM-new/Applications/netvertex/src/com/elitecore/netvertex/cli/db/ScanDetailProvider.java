package com.elitecore.netvertex.cli.db;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;

public class ScanDetailProvider extends DetailProvider {

	private static final String SCAN = "-scan";
	private static final String DESCRIPTION = "Scan DB datasource for aliveness";
	private static final String HELP = "?";
	private static final String HELP_OPTION = "-help";
	private static final String NO_SUCH_DB_DATASOURCE_FOUND = "NO SUCH DB DATASOURCE FOUND";
	

	private LinkedHashMap<String, DetailProvider> detailProviderMap;
	private NetVertexServerContext serverContext;

	public ScanDetailProvider(NetVertexServerContext serverContext) {
		detailProviderMap = new LinkedHashMap<String, DetailProvider>();
		this.serverContext = serverContext;
	}

	@Override
	public String execute(String[] parameters) {

		if (parameters == null || parameters.length == 0) {
			return "Invalid Argument. Datasource name not provided"
					+ getHelpMsg();
		}
		if (HELP.equals(parameters[0]) || HELP_OPTION.equalsIgnoreCase(parameters[0])) {
			return getHelpMsg();
		}

		DBDataSource dbDataSource = getDBDatasourceMap().get(parameters[0]);

		if (dbDataSource == null) {
			return NO_SUCH_DB_DATASOURCE_FOUND;
		}
		return scanDataSource(dbDataSource);
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
		out.println(EliteBaseCommand.fillChar("\t [<DB Datasource Name>]", 30) + "-Scan DB datasource for aliveness");
		for (DetailProvider detailProvider : detailProviderMap.values()) {
			out.println(EliteBaseCommand.fillChar("\t" + detailProvider.getKey(), 30) + "-" + detailProvider.getDescription());
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("'" + getKey() + "':{'" + HELP_OPTION + "':{}");
		Map<String, DBDataSource> dataSourceMap = getDBDatasourceMap();

		if (dataSourceMap != null && dataSourceMap.isEmpty() == false) {
			for (String datasourceName : getDBDatasourceMap().keySet()) {
				out.print(", '" + datasourceName + "':{} ");
			}
		}

		for (DetailProvider provider : detailProviderMap.values()) {
			out.print(",");
			out.print(provider.getHotkeyHelp());
		}
		out.print("}");
		out.close();
		return writer.toString();
	}

	@Override
	public String getKey() {
		return SCAN;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	private String scanDataSource(DBDataSource dbDataSource) {
		
		List<String> dbDataSourceList = DBConnectionManager.getDataSources();
		if(dbDataSourceList.contains(dbDataSource.getDataSourceName()) == false){
			return "Unable to scan " + dbDataSource.getDataSourceName() + ". Reason: Datasource not initialized";  
		}
		
		try {
			TransactionFactory transactionFactory = DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).getTransactionFactory();
			if( transactionFactory == null){
				return "Unable to scan " + dbDataSource.getDataSourceName() + ". Reason: Datasource not initialized";
			}
			
			transactionFactory.scan();
			
			return "SCANNED SUCCESSFULLY \nCurrent Satus: " + (transactionFactory.isAlive() ? "ALIVE" : "DEAD");
			
		} catch (Exception e) { 
			ignoreTrace(e);
			return "Unable to scan " + dbDataSource.getDataSourceName() + ". Reason: " + e.getMessage();
		}
	}

	protected Map<String, DBDataSource> getDBDatasourceMap() {
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

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
}
