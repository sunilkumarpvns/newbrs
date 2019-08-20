package com.elitecore.netvertex.cli.db;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;

public class StatisticsDetailProvider extends DetailProvider {

	private static final String STATISTICS = "-statistics";
	private static final String DESCRIPTION = "Shows statistics of DB datasource";
	private static final String HELP = "?";
	private static final String HELP_OPTION = "-help";
	private static final String NO_SUCH_DB_DATASOURCE_FOUND = "NO SUCH DB DATASOURCE FOUND";
	private static final String NO_STATISTICS_AVAILABLE ="NO STATISTICS AVAILABLE";

	private static final String[] headers = { "N", "S", "DC", "DT", "MDT", "ST" };
	private static final int[] width = { 30, 1, 5, 15, 15, 15 };
	private static final int[] columnAlignment = { TableFormatter.CENTER,
			TableFormatter.CENTER, TableFormatter.CENTER,
			TableFormatter.CENTER, TableFormatter.CENTER, TableFormatter.CENTER };

	private static final String[] legendHeader = { "ABBREVIATION", "MEANING" };
	private static final int[] legendWidth = { 15, 35 };
	private static final int[] legendColumnAlignment = { TableFormatter.LEFT,
			TableFormatter.LEFT };
	

	private NetVertexServerContext serverContext;
	private LinkedHashMap<String, DetailProvider> detailProviderMap;

	public StatisticsDetailProvider(NetVertexServerContext serverContext) {
		detailProviderMap = new LinkedHashMap<String, DetailProvider>();
		this.serverContext = serverContext;
	}

	@Override
	public String execute(String[] parameters) {
		if (parameters == null || parameters.length == 0) {
			return "Invalid Argument. Datasource name not provided" + getHelpMsg();
		}
		if (HELP.equalsIgnoreCase(parameters[0]) || HELP_OPTION.equalsIgnoreCase(parameters[0])) {
			return getHelpMsg();
		}
		
		DBDataSource dbDataSource = getDBDatasourceMap().get(parameters[0]);
		if (dbDataSource == null) {
			return NO_SUCH_DB_DATASOURCE_FOUND;
		}
		return displayStatistics(dbDataSource);

	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getKey() + "[<options>]");
		out.println("Description : " + getDescription());
		out.println("Possible options:\n");
		out.println();
		out.println(EliteBaseCommand.fillChar("\t [<DB Datasource Name>]", 30) + "-Shows statistics of DB datasource");
		for (DetailProvider detailProvider : detailProviderMap.values()) {
			out.println(EliteBaseCommand.fillChar("\t" + detailProvider.getKey(), 30)+ "-" + detailProvider.getDescription());
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return STATISTICS;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
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

	protected Map<String, DBDataSource> getDBDatasourceMap() {
		Map<String, DBDataSource> dataSourceMap = new HashMap<String, DBDataSource>();
		DBDataSource dataSource = NetVertexDBConnectionManager.getInstance().getDataSource();
		if (dataSource.getConnectionURL().contains(CommonConstants.VOLTDB) == false) {
			dataSourceMap.put(dataSource.getDataSourceName(), dataSource);
		}
		if (serverContext.getServerConfiguration().getDatabaseDSConfiguration() != null) {
			Map<String, DBDataSourceImpl> tempDataSoucreMap = serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDatasourceNameMap();
			if (tempDataSoucreMap != null) {
				for (Map.Entry<String, DBDataSourceImpl> entry : tempDataSoucreMap.entrySet()) {
					if (entry.getValue().getConnectionURL().contains(CommonConstants.VOLTDB) == false) {
						dataSourceMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		return dataSourceMap;
	}

	private String displayStatistics(DBDataSource dbDataSource) {
	
		List<String> dbDatasourceList = DBConnectionManager.getDataSources();
		if(dbDatasourceList.contains(dbDataSource.getDataSourceName()) == false){
			return "Statistics not available for "+ dbDataSource.getDataSourceName() + ". Reason: Not initialized" ;
		}
		
		try {

			TransactionFactory transactionFactory = DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).getTransactionFactory();
			if(transactionFactory == null){
				return "Statistics not available for " + dbDataSource.getDataSourceName() + ". Reason: Not initialized"; 
			}

			ESIStatistics statistics = transactionFactory.getStatistics();

			if (statistics == null) {
				return NO_STATISTICS_AVAILABLE;
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss z");
			TableFormatter formatter = new TableFormatter(headers, width, TableFormatter.OUTER_BORDER);
			formatter.addRecord( new String[] {
								 	statistics.getName(),
									statistics.currentStatus().charAt(0) + "",
									statistics.getDeadCount() + "",
									(statistics.getLastDeadTimestamp() > 0 ? dateFormat.format(new Date(statistics.getLastDeadTimestamp()))	: "N.A.") + "",
									(statistics.getLastMarkDeadTimestamp() > 0 ? dateFormat.format(new Date(statistics.getLastMarkDeadTimestamp())) : "N.A.") + "",
									(statistics.getLastScanTimestamp() > 0 ? dateFormat.format(new Date(statistics.getLastScanTimestamp())) : "N.A.") + "", }, 
									columnAlignment);
			return formatter.getFormattedValues() + getLegend();
		} catch (Exception e) { 
			ignoreTrace(e);	
			return "Unable to fetch statistics for DS " +  dbDataSource.getDataSourceName() + ". Reason: " + e.getMessage();
		}
	}

	private String getLegend() {

		TableFormatter legendFormat = new TableFormatter(legendHeader, legendWidth, legendColumnAlignment, TableFormatter.NO_BORDERS);
		legendFormat.addRecord(new String[] { "N", "Name" });
		legendFormat.addRecord(new String[] { "S", "Status" });
		legendFormat.addRecord(new String[] { "A", "Alive" });
		legendFormat.addRecord(new String[] { "D", "Dead" });
		legendFormat.addRecord(new String[] { "DC", "Total Dead Count" });
		legendFormat.addRecord(new String[] { "DT", "Last Dead Timestamp" });
		legendFormat.addRecord(new String[] { "MDT","Last Mark Dead Called Timestamp" });
		legendFormat.addRecord(new String[] { "ST", "Last Scan Timestamp" });
		return legendFormat.getFormattedValues();
	}

}
