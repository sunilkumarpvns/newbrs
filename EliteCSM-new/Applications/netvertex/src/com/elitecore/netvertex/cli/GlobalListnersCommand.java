package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.diameterapi.core.stack.constant.Status;

public  abstract class GlobalListnersCommand extends EliteBaseCommand{
	
	private SimpleDateFormat sdf = null;
	
	private final static int LISTENER_NAME=15;
	private final static int LISTENER_ADDRESS=20;
	private final static int START_DATE=30;
	private final static int STATUS=25;
	private final static int REMARKS=15;
	
	private int[] width= {LISTENER_NAME,
			LISTENER_ADDRESS,
			START_DATE,
			STATUS,
			REMARKS};
	
	private String[] header={
			"Listener Name",
			"Listener Address",
			"Start Date",
			"Status",
			"Remarks"
		};
	
	public GlobalListnersCommand() {
		this.sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	}
	
	@Override
	public String execute(String parameter) {
		
		TableFormatter formatter = null;
		if (parameter == null || parameter.trim().length() == 0) {
			 formatter=new TableFormatter(header, width);
			return getGlobalListnerSummary(formatter);
		}
		
		parameter = parameter.trim();
		if(CSV.equalsIgnoreCase(parameter)){
			formatter=new TableFormatter(header, width, 
					TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
			return getGlobalListnerSummary(formatter);
		}
		return getHelp();
	}
	
	private String getGlobalListnerSummary(TableFormatter formatter){
		
		String data[]=new String[width.length];
		data[0]="RADIUS";
		data[1]=getRadiusAddress() + " : "+getRadiusPort();
		data[2]=getRadiusStartDate() == null  ? " -- " : sdf.format(getRadiusStartDate());
		data[3]=convertToCamelCase(getRadiusStatus());
		data[4]=getRadiusRemarks()==null ? " -- " : getRadiusRemarks();
		formatter.addRecord(data);
		
		data[0]="DIAMETER";
		data[1]=getDiameterStackAddress() + " : "+getDiameterStackPort();
		data[2]=getDiameterStackStartDate() == null  ? " -- " : sdf.format(getDiameterStackStartDate());
		data[3]=convertToCamelCase(getDiameterStackStatus().name());
		data[4]=getDiameterStackRemarks()==null ? " -- " : getDiameterStackRemarks();
		formatter.addRecord(data);
		
		return formatter.getFormattedValues();
	}
	
	@Override
	public String getCommandName() {
		return "globallisteners";
	}

	@Override
	public String getDescription() {
		return "Global Listner Details";
	}
	
	@Override
	public String getHotkeyHelp() {
		return "{'globallisteners':{'"+CSV+"':{}}}";
	}
	
	private String getHelp() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() + " [<option>]");
		out.println("Description : " + getDescription());
		out.println();
		out.println("----------------------------:Possible Options:----------------------------");
		out.println(CSV + "		: " + getDescription() + " in CSV format");
		out.println();
		return stringWriter.toString();
	}
	
	// Diameter Information
	public abstract String getDiameterStackAddress();
	public abstract int getDiameterStackPort();
	public abstract Status getDiameterStackStatus();
	public abstract Date getDiameterStackStartDate();
	public abstract String getDiameterStackRemarks();
	// Radius Information
	public abstract String getRadiusAddress() ;
	public abstract int getRadiusPort() ;
	public abstract Date getRadiusStartDate() ;
	public abstract String getRadiusStatus() ;
	public abstract String getRadiusRemarks();
	
    public String convertToCamelCase(String word){
    	if(word!=null && word.trim().length()>0){
    		String firstChar=""+word.charAt(0);
    		firstChar=firstChar.toUpperCase();
    		word=(firstChar)+(word.substring(1).toLowerCase());
    	}    	    
    	return word;    	
    }
}
