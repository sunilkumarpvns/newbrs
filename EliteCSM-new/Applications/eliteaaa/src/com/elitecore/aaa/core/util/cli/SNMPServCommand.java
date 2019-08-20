package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.serverx.snmp.mib.mib2.extended.SnmpImpl;
import com.elitecore.core.util.cli.TableFormatter;

public class SNMPServCommand extends EliteBaseCommand {
	
	private final static String SUMMARY  = "-s";
	private final static String NOT_APPLICABLE     = "-";
	private final int[] width= {20,10,10};
	private final String[] header={"TYPE","SNMP-IN","SNMP-OUT"};
	private SnmpImpl snmpimpl;
	
	public SNMPServCommand(SnmpImpl snmpimpl){
		this.snmpimpl = snmpimpl;
	}
	
	@Override
	public String execute(String parameter) {

		if(parameter == null || parameter.trim().length() == 0){
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println("Required parameter missing.");
			out.println(getHelp());
			out.close();
			return stringWriter.toString();	
		}
		String[] parameters = parseArgumentString(parameter);
		
		if(parameters == null || parameters.length == 0){
			return getErrorMsg();
		}
		
		if (SUMMARY.equalsIgnoreCase(parameters[0])) {
			
			if(parameters.length > 1 && CSV.equalsIgnoreCase(parameters[1])){
				TableFormatter formatter = new TableFormatter(header, width, 
						TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
				return getSNMPSummary(formatter);
			}
			
			int[] alignment  = new int[]{TableFormatter.LEFT,TableFormatter.RIGHT,TableFormatter.RIGHT};
			TableFormatter formatter = new TableFormatter(header, width, alignment, TableFormatter.ONLY_HEADER_LINE);
			return getSNMPSummary(formatter);
		}
		return getHelp();
	}

	@Override
	public String getCommandName() {
		return "snmpserv";
	}

	@Override
	public String getDescription() {
		return "Displays SNMP service detail as per option.";
	}

	@Override
	public String getHotkeyHelp() {
		return "{'"+getCommandName()+"':{'"+SUMMARY+"':{'"+CSV+"':{}}}}";
	}
	
	private String getHelp(){
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		String paramName =SUMMARY ;					 
		String paramDesc ="Displays SNMP Service Request summary.";
		out.println("Usage : snmpserv <options>");
		out.println("Possible options"  );
		out.print("    " + fillChar(paramName,52)  +  paramDesc );
		out.close();		
		return stringWriter.toString();	
	}
	
	private String getErrorMsg(){
		StringWriter sb = new StringWriter();
		PrintWriter out = new PrintWriter(sb);
		out.println("Invalid Parameter or Parameter format is not correct.");
		out.println("Try 'snmpserv ?'.");
		out.close();
		return sb.toString();
		
	}
	
	private String getSNMPSummary(TableFormatter stringFormatter){
		
		if(snmpimpl == null){
			stringFormatter.add("SNMP Statistics is not available", TableFormatter.LEFT);
			return stringFormatter.getFormattedValues();
		}
		
		String[] rowData = new String[3];
		stringFormatter.addRecord(getStatisticRow(rowData,"Packets", String.valueOf(snmpimpl.getSnmpInPkts()), String.valueOf(snmpimpl.getSnmpOutPkts())));
		stringFormatter.addRecord(getStatisticRow(rowData,"GetRequests", String.valueOf(snmpimpl.getSnmpInGetRequests()), String.valueOf(snmpimpl.getSnmpOutGetRequests())));
		stringFormatter.addRecord(getStatisticRow(rowData,"Get Nexts", String.valueOf(snmpimpl.getSnmpInGetNexts()), String.valueOf(snmpimpl.getSnmpOutGetNexts())));
		stringFormatter.addRecord(getStatisticRow(rowData,"Set Requests", String.valueOf(snmpimpl.getSnmpInSetRequests()), String.valueOf(snmpimpl.getSnmpOutSetRequests())));
		stringFormatter.addRecord(getStatisticRow(rowData,"Get Responses", String.valueOf(snmpimpl.getSnmpInGetResponses()), String.valueOf(snmpimpl.getSnmpOutGetResponses())));
		stringFormatter.addRecord(getStatisticRow(rowData,"Bad Versions", String.valueOf(snmpimpl.getSnmpInBadVersions()),NOT_APPLICABLE));
		stringFormatter.addRecord(getStatisticRow(rowData,"Bad Community Names", String.valueOf(snmpimpl.getSnmpInBadCommunityNames()),NOT_APPLICABLE));
		stringFormatter.addRecord(getStatisticRow(rowData,"Bad Community Uses", String.valueOf(snmpimpl.getSnmpInBadCommunityUses()), NOT_APPLICABLE));
		stringFormatter.addRecord(getStatisticRow(rowData,"ASN Parse Errs", String.valueOf(snmpimpl.getSnmpInASNParseErrs()),NOT_APPLICABLE));
		stringFormatter.addRecord(getStatisticRow(rowData,"Too Bigs", String.valueOf(snmpimpl.getSnmpInTooBigs()), String.valueOf(snmpimpl.getSnmpOutTooBigs())));
		stringFormatter.addRecord(getStatisticRow(rowData,"No Such Names", String.valueOf(snmpimpl.getSnmpInNoSuchNames()), String.valueOf(snmpimpl.getSnmpOutNoSuchNames())));
		stringFormatter.addRecord(getStatisticRow(rowData,"Bad Values", String.valueOf(snmpimpl.getSnmpInBadValues()), String.valueOf(snmpimpl.getSnmpOutBadValues())));
		stringFormatter.addRecord(getStatisticRow(rowData,"Gen Errs", String.valueOf(snmpimpl.getSnmpInGenErrs()), String.valueOf(snmpimpl.getSnmpOutGenErrs())));
		stringFormatter.addRecord(getStatisticRow(rowData,"Read Onlys", String.valueOf(snmpimpl.getSnmpInReadOnlys()), NOT_APPLICABLE));
		stringFormatter.addRecord(getStatisticRow(rowData,"Total Req Vars", String.valueOf(snmpimpl.getSnmpInTotalReqVars()), NOT_APPLICABLE));
		stringFormatter.addRecord(getStatisticRow(rowData,"Total Set Vars", String.valueOf(snmpimpl.getSnmpInTotalSetVars()),NOT_APPLICABLE));
		stringFormatter.addRecord(getStatisticRow(rowData,"Traps", String.valueOf(snmpimpl.getSnmpInTraps()), String.valueOf(snmpimpl.getSnmpOutTraps())));
	
		stringFormatter.addNewLine();
		stringFormatter.add("Enable Authentication Traps : "+ snmpimpl.getSnmpEnableAuthenTraps().toString());
		
		return stringFormatter.getFormattedValues();
	}

	private String[] getStatisticRow(String[] array,String type,String in,String out){
		array[0] = type;
		array[1] = in;
		array[2] = out;
		return array;
	}
}