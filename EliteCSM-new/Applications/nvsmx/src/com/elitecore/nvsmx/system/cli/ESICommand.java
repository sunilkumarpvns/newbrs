package com.elitecore.nvsmx.system.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.nvsmx.remotecommunications.EndPoint;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.NVSMXEndPoint;
import com.elitecore.nvsmx.remotecommunications.data.EndPointStatus;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;

public class ESICommand extends EliteBaseCommand {
	
	private static final String SCAN_TIMESTAMP_SYMBOL = "ST";
	private static final String MARK_DEAD_CALL_TIMESTAMP_SYMBOL = "MDT";
	private static final String DEAD_TIMESTAMP_SYMBOL = "DT";
	private static final String DEAD_COUNT_SYMBOL = "DC";
	private static final String ALIVE_SYMBOL = "A";
	private static final String DEAD_SYMBOL = "D";
	private static final String SHUTDOWN_SYMBOL = "S";
	private static final String MSG_NO_INSTANCE_FOUND_WITH_NAME = "NO INSTANCE FOUND WITH NAME: ";
	private static final String MSG_NO_ESI_INSTANCE_FOUND = "NO ESI INSTANCE FOUND";
	private static final String N_A = "N.A.";
	private static final String KEY = "esi";
	private static final String TYPE_NV = "NV";
	private static final String TYPE_PD = "PD";
	private static final String INDEX = "INDEX";
	private static final String STATUS = "STATUS";
	private static final String TYPE = "TYPE";
	private static final String NAME = "NAME";
	private static final String OPTION_VIEW = "-view";
	private static final String OPTION_SCAN = "-scan";
	private static final int WIDTH_STATUS = 6;
	private static final int WIDTH_TYPE = 4;
	private static final int WIDTH_NAME = 25;
	private static final int WIDTH_INDEX = 2;
	private static final String[] headers = {INDEX, NAME,TYPE,STATUS,DEAD_COUNT_SYMBOL, DEAD_TIMESTAMP_SYMBOL, MARK_DEAD_CALL_TIMESTAMP_SYMBOL, SCAN_TIMESTAMP_SYMBOL};
	private static final int[] columnWidths = {WIDTH_INDEX, WIDTH_NAME,WIDTH_TYPE, WIDTH_STATUS, 2, 21, 21, 21};
	private static final String[] footerHeader = { "ABBREVIATION", "MEANING" };
	private static final int[] footerWidth = { 15, 35 };
	private static final int[] footerColumnAlignment = { TableFormatter.LEFT, TableFormatter.LEFT };
	private final EndPointManager endPointManager;

	public ESICommand(EndPointManager endPointManager) {
		this.endPointManager = endPointManager;
	}

	@Override
	public String execute(String parameter) {
		
		if (Strings.isNullOrBlank(parameter)) {
			return getHelpMsg();
		}
				
		String[] parameters = parameter.split(" ");

		if (parameters[0].equalsIgnoreCase(OPTION_VIEW)) {
			
			if (parameters.length != 1) {
				return getHelpMsg();
			}
			return viewInstances();
		} else if (parameters[0].equalsIgnoreCase(OPTION_SCAN)) {
			
			if (parameters.length != 2) {
				return getHelpMsg();
			}
			return scanInstance(parameters[1]);
		}
		
		return getHelpMsg();
	}

	private String scanInstance(String instanceName) {
		
		EndPoint endPoint = getEndPoint(instanceName);
		
		if (endPoint == null) {
			return MSG_NO_INSTANCE_FOUND_WITH_NAME + instanceName;
		}
		
		endPoint.scan();
		
		String currentStatus = getStatus(endPoint);
		
		return "Instance Scanned Successfully. Current Status: " + currentStatus + "\n" +  getFooter();
	}

	private EndPoint getEndPoint(String instanceName) {
		List<EndPoint> allNetvertexEndPoint = endPointManager.getAllNetvertexEndPoint();
		if (Collectionz.isNullOrEmpty(allNetvertexEndPoint) == false) {
			for (int i = 0; i < allNetvertexEndPoint.size(); i++) {
				EndPoint endpoint = allNetvertexEndPoint.get(i);
				if (endpoint.getInstanceData().getName().equalsIgnoreCase(instanceName)) {
					return endpoint;
				}
			}
		}
		
		List<NVSMXEndPoint> allNvsmxEndPoints = endPointManager.getALLNvsmxEndPoints();
		if (Collectionz.isNullOrEmpty(allNvsmxEndPoints) == false) {
			for (int i = 0; i < allNvsmxEndPoints.size(); i++) {
				EndPoint endpoint = allNvsmxEndPoints.get(i);
				if (endpoint.getInstanceData().getName().equalsIgnoreCase(instanceName)) {
					return endpoint;
				}
			}
		}
		
		return null;
	}

	private String viewInstances() {
		List<EndPoint> allNetvertexEndPoints = endPointManager.getAllNetvertexEndPoint();
		List<NVSMXEndPoint> allNvsmxEndPoints = endPointManager.getALLNvsmxEndPoints();

		if (Collectionz.isNullOrEmpty(allNetvertexEndPoints) && Collectionz.isNullOrEmpty(allNvsmxEndPoints)) {
			return MSG_NO_ESI_INSTANCE_FOUND;
		} 
		
		TableFormatter formatter = new TableFormatter(headers, columnWidths, TableFormatter.ALL_BORDER);
		
		int index=1;
		for (int i = 0; i < allNetvertexEndPoints.size(); i++) {
			formatter.addRecord(format(allNetvertexEndPoints.get(i), index++, TYPE_NV));
		}

		for (int i = 0; i < allNvsmxEndPoints.size(); i++) {
			formatter.addRecord(format(allNvsmxEndPoints.get(i), index++, TYPE_PD));
		}
		
		return formatter.getFormattedValues() + getFooter();
	}
	
	private String getFooter() {

		TableFormatter legendFormat = new TableFormatter(footerHeader, footerWidth, footerColumnAlignment, TableFormatter.NO_BORDERS);
		legendFormat.addRecord(new String[] { ALIVE_SYMBOL, "Alive" });
		legendFormat.addRecord(new String[] { DEAD_SYMBOL, "Dead" });
		legendFormat.addRecord(new String[] { SHUTDOWN_SYMBOL, "Shutdown" });
		legendFormat.addRecord(new String[] { DEAD_COUNT_SYMBOL, "Total Dead Count" });
		legendFormat.addRecord(new String[] { DEAD_TIMESTAMP_SYMBOL, "Last Dead Timestamp" });
		legendFormat.addRecord(new String[] { MARK_DEAD_CALL_TIMESTAMP_SYMBOL,"Last Mark Dead Called Timestamp" });
		legendFormat.addRecord(new String[] { SCAN_TIMESTAMP_SYMBOL, "Last Scan Timestamp" });
		return legendFormat.getFormattedValues();
	}

	private String[] format(EndPoint ep, int index, String type) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss z");
		
		ServerInformation si = ep.getInstanceData();
		String[] data = new String[columnWidths.length];
		data[0] = index+"";
		data[1] = si.getName();
		data[2] = type;
		data[3] = getStatus(ep, type);
		data[4] = ep.getStatistics().getDeadCount()+"";
		data[5] = ep.getStatistics().getLastDeadTimestamp() > 0 ? dateFormat.format(new Date(ep.getStatistics().getLastDeadTimestamp())) : N_A;
		data[6] = ep.getStatistics().getLastMarkDeadTimestamp() > 0 ? dateFormat.format(new Date(ep.getStatistics().getLastMarkDeadTimestamp())) : N_A;
		data[7] = ep.getStatistics().getLastScanTimestamp() > 0 ? dateFormat.format(new Date(ep.getStatistics().getLastScanTimestamp())) : N_A;
		
		return data;
	}
	
	private String getStatus(EndPoint ep, String type) {
		
		if (TYPE_PD.equals(type)) {
			return ((NVSMXEndPoint)ep).getStatus().equals(EndPointStatus.STARTED.getVal()) ? getStatus(ep) : SHUTDOWN_SYMBOL;	
		} else {
			return getStatus(ep);
		}
	}

	private String getStatus(EndPoint ep) {
		return ep.isAlive() ? ALIVE_SYMBOL : DEAD_SYMBOL;
	}

	@Override
	public String getCommandName() {
		return KEY;
	}

	@Override
	public String getDescription() {
		return "Manages ESI Instances";
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(stringWriter);
		out.println();
		out.println("   " + KEY + " <option>");
		out.println(" Description: " + getDescription());

		out.println(" Possible Options: ");
		out.println();
		out.incrementIndentation();
		out.print(StringUtility.fillChar(OPTION_VIEW, 8));
		out.println(": Shows all NV and PD Instances");
		out.print(StringUtility.fillChar(OPTION_SCAN, 8));
		out.println("<Instance Name> : Check for aliveness for provided instance");
		
		out.decrementIndentation();
		out.println();
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getHotkeyHelp() {

		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("{'" + KEY + "':{'" + HELP_OPTION + "':{},'" + HELP + "':{},");
		
		out.print("'" + OPTION_VIEW + "':{},'" + OPTION_SCAN + "':{");
	
		List<EndPoint> allNetvertexEndPoints = endPointManager.getAllNetvertexEndPoint();
		List<NVSMXEndPoint> allNvsmxEndPoints = endPointManager.getALLNvsmxEndPoints();
	
		if (Collectionz.isNullOrEmpty(allNetvertexEndPoints) == false) {
			out.print("'" + allNetvertexEndPoints.get(0).getInstanceData().getName() + "':{}");
			
			for (int i = 1; i < allNetvertexEndPoints.size(); i++) {
				out.print(",'" + allNetvertexEndPoints.get(i).getInstanceData().getName() + "':{}");
			}
		}
		
		if (Collectionz.isNullOrEmpty(allNvsmxEndPoints) == false) {
			out.print(",'" + allNvsmxEndPoints.get(0).getInstanceData().getName() + "':{}");
			for (int i = 1; i < allNvsmxEndPoints.size(); i++) {
				out.print(",'" + allNvsmxEndPoints.get(i).getInstanceData().getName() + "':{}");
			}
		}
		
		out.print("}}}");
		
		return writer.toString();
	} 

}
