package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import com.elitecore.aaa.core.data.ParamsDetail;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;

/**
 * Detail provider for show command to display Miscellaneous configuration
 * 
 * @author Kuldeep Panchal
 *
 */
public class MiscellaneousConfigDetailProvider extends DetailProvider {

	private static final int[] WIDTH = new int[]{ 46, 28 };
	private static final String MISC = "misc";
	public static final String HELP = "-help";
	private final String[] header = { "Parameter Name", "Parameter Value"};
	private final int[] alignment = new int[] { TableFormatter.LEFT, TableFormatter.RIGHT};
	
	private static volatile MiscellaneousConfigDetailProvider miscellaneousConfigDetailProvider;
	private HashMap<String, DetailProvider> subDetailProviderMap;
	private AAAServerContext serverContext;
	
	private MiscellaneousConfigDetailProvider(AAAServerContext serverContext) {
		this.serverContext = serverContext;
		this.subDetailProviderMap = new HashMap<String, DetailProvider>();
	}
	
	public static MiscellaneousConfigDetailProvider getInstance(AAAServerContext serverContext) {
		if (miscellaneousConfigDetailProvider == null) {
			synchronized (MiscellaneousConfigDetailProvider.class) {
				if (miscellaneousConfigDetailProvider == null) {
					miscellaneousConfigDetailProvider = new MiscellaneousConfigDetailProvider(serverContext);
				}
			}
		}
		return miscellaneousConfigDetailProvider;
	}
	
	@Override
	public String execute(String[] parameters) {
		
		if (parameters.length == 0) {
			
			if (serverContext.getServerConfiguration().getMiscellaneousConfigurable() == null) {
				return "Miscellaneous configuration not available";
			}
			
			List<ParamsDetail> paramsList = serverContext.getServerConfiguration().getMiscellaneousConfigurable().getParamsList();

			TableFormatter formatter = new TableFormatter(header, WIDTH, alignment, TableFormatter.ONLY_HEADER_LINE_WITH_COL_SEPARATOR);
			String[] rowData = new String[2];
			for (ParamsDetail paramsDetail : paramsList) {
				rowData[0] = paramsDetail.getName();
				rowData[1] = paramsDetail.getValue();
				formatter.addRecord(rowData);
			}

			return "Miscellaneous Configuration:\n" 
					+ formatter.getFormattedValues() 
					+ "\nNote: Depending on the module, the changes made in some of the parameters will"
					+ "\ncome into effect and some will not. To get all in effect need to restart the server.\n";
		}
		
		if ("?".equals(parameters[0]) || HELP.equals(parameters[0])) {
			return getHelpMsg();
		}

		return "Invalid Option\n"+getHelpMsg();
		
	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + MISC);
		out.println("Description : " + getDescription());
		return stringWriter.toString();
	}

	@Override
	public String getHotkeyHelp() {
		return "'" + MISC + "':{'-help':{}}";
	}
	
	@Override
	public String getKey() {
		return MISC;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return this.subDetailProviderMap;
	}
	
	@Override
	public String getDescription() {
		return "Displays the current miscellaneous configurations";
	}
}
