package com.elitecore.core.util.cli.cmd;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class ReInitDetailProvider extends DetailProvider {

	private static final String KEY = "-reinit";
	private static final String RE_INIT_SUCCESS = "RE-INITIALIZATION SUCCESS";
	private static final String DESCRIPTION = "ReInit DB datasource";
	private static final String DASH = "-";
	private LinkedHashMap<String, DetailProvider> detailProviderMap;
	private Map<String, DBDataSource> dbDataSources;
	private static final String NO_SUCH_DB_DATASOURCE_FOUND = "NO SUCH DB DATASOURCE FOUND";
	private static final String MODULE = "RELOAD-DETAIL-PRVDR";
	
	public ReInitDetailProvider(Map<String, DBDataSource> dbDataSources) {
		this.detailProviderMap = new LinkedHashMap<String, DetailProvider>();
		this.dbDataSources = dbDataSources;
	}
	
	@Override
	public String execute(String[] parameters) {

		if (parameters == null || parameters.length == 0) {
			return "Invalid Argument. Datasource name not provided."
					+ getHelpMsg();
		}
		
		if (isHelpSymbol(parameters[0])) {
			return getHelpMsg();
		}
		
		DBDataSource dbDataSource = dbDataSources.get(parameters[0]);

		if (dbDataSource == null) {
			return NO_SUCH_DB_DATASOURCE_FOUND;
		}
		
		return reInitDataSource(dbDataSource);
	}

	private String reInitDataSource(DBDataSource dbDataSource) {

		if (DBConnectionManager.isExist(dbDataSource.getDataSourceName()) == false) {
			return "Unable to re-init datasource: " + dbDataSource.getDataSourceName() + ". Reason: Datasource not initialized";
		}

		try {
			DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(dbDataSource.getDataSourceName());
			dbConnectionManager.reInit();
			
			return RE_INIT_SUCCESS;
		} catch (Exception e) { 
			getLogger().trace(MODULE, e);
			return "Unable to re-init datasource: " + dbDataSource.getDataSourceName() + ". Reason: " + e.getMessage();
		}
	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getKey() + " [<options>]");
		out.println("Description : " + getDescription());
		out.println("Possible options:");
		out.incrementIndentation();
		out.println(EliteBaseCommand.fillChar("[<DB Datasource Name>]", 30) + DASH + DESCRIPTION);
		out.incrementIndentation();
		for (DetailProvider detailProvider : detailProviderMap.values()) {
			out.println(EliteBaseCommand.fillChar(detailProvider.getKey(), 30) + DASH + detailProvider.getDescription());
		}

        for (Entry<String, DetailProvider> entry : detailProviderMap.entrySet()) {
            out.println(entry.getKey() + " : "
                    + entry.getValue().getDescription());
        }

		out.decrementIndentation();
		out.decrementIndentation();
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("'" + getKey() + "':{'" + HELP_OPTION + "':{}");

		if (Maps.isNullOrEmpty(dbDataSources) == false) {
			for (String datasourceName : dbDataSources.keySet()) {
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
	public String getDescription() {
		return DESCRIPTION;
	}
}
