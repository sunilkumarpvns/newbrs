package com.elitecore.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TableFormatter {
	
	public static final char ROW_SEPARATOR_HYPHEN = '-';
	public static final String COLUMN_SEPARATOR_PIPE = "|";
	public static final String COLUMN_SEPARATOR_COMMA = ",";
	private char rowSeparator = ROW_SEPARATOR_HYPHEN;
	private int[] width;
	private String[] header;
	private boolean isWidthExceed;
	private int patternLength=0;
	private int format;
	private int[] column_alignment;
	public static final int LEFT=0;
	public static final int RIGHT=2;
	public static final int CENTER=3;
	private String columnSeparator = COLUMN_SEPARATOR_PIPE;
	
	private static final int RIGHT_MARGIN=1;
	private static final int LEFT_MARGIN=1;
	
	public static final int ALL_BORDER=0;
	public static final int OUTER_BORDER=1;
	public static final int ONLY_HEADER_LINE=2;
	public static final int NO_BORDERS=3;
	public static final int BORDER_ABOVE_ONLY=4;
	public static final int ONLY_HEADER_LINE_WITH_COL_SEPARATOR=5;
	/**
	 * o/p: "datavalue1","datvalue2","datavalue3" 
	 */
	public static final int CSV=6;
	
	private StringWriter stringWriter = new StringWriter();
	private PrintWriter out = new PrintWriter(stringWriter);
	
	public TableFormatter(String[] header,int[] width){
		this(header,width, null,ALL_BORDER);
	}
	
	public TableFormatter(String[] header,int[] width,int[] column_alignment){
		this(header,width,column_alignment,ALL_BORDER);
	}
	public TableFormatter(String[] header,int[] width,int format){
		this(header,width,null,format);
	}

	public TableFormatter(String[] header,int[] width,int[] column_alignment,int format){
		this(header, width, column_alignment, format, ROW_SEPARATOR_HYPHEN, COLUMN_SEPARATOR_PIPE);
	}
	
	public TableFormatter(String[] header,int[] width, int format, String columnSeparator) {
		this(header, width, null, format, ROW_SEPARATOR_HYPHEN, columnSeparator);
	}
	public TableFormatter(String[] header,int[] width, int[] column_alignment, int format, String columnSeparator) {
		this(header, width, column_alignment, format, ROW_SEPARATOR_HYPHEN, columnSeparator);
	}
	
	public TableFormatter(String[] header,int[] width,int[] column_alignment,int format, char rowSeparator, String columnSeparator){
		
		this.columnSeparator = columnSeparator;
		this.rowSeparator = rowSeparator;
		this.width=width;
		this.header=header;
		
		if(column_alignment != null)
			this.column_alignment = column_alignment;
		else
			this.column_alignment = new int[width.length];
		
		// Formatting Header
		this.format = format;
		
		// for ONLY_HEADER_LINE : Skip Patter before Header and set line separator to single space
		int[] headerAlignment = new int[header.length];
		if(column_alignment != null){
			headerAlignment = column_alignment;
		}else{
			for(int i=0;i<headerAlignment.length;i++)
				headerAlignment[i] = CENTER;
		}
		
		if(format == ONLY_HEADER_LINE || format == NO_BORDERS){
			this.columnSeparator = "";
		}
		
		for(int i=0;i<(width.length-1);i++){
			patternLength+=width[i] + LEFT_MARGIN + this.columnSeparator.length() + RIGHT_MARGIN;
		}
		// separator Left Margin Data right margin separator
		patternLength+=width[width.length-1] + LEFT_MARGIN + 2*this.columnSeparator.length()+ RIGHT_MARGIN ;
		
		
		if(format == ALL_BORDER || format== OUTER_BORDER){
			out.println(fillChar("", patternLength, '-'));
		}
		
		addRecord(this.header,headerAlignment);
		// Add Patter after Header
		if(format == ONLY_HEADER_LINE || format == OUTER_BORDER || format == ONLY_HEADER_LINE_WITH_COL_SEPARATOR){
			out.println(fillChar("", patternLength, this.rowSeparator));
		}
	}
	
	public void addRecord(String[] dataValues){
		addRecord(dataValues, column_alignment);
	}
	public void addRecord(String[] dataValues,int[] column_alignment){
		
		if(dataValues==null || dataValues.length!=width.length){
			return;
		}
		
		String[] data=new String[dataValues.length];
		System.arraycopy(dataValues, 0, data, 0, data.length);
		
		if(format == CSV){
			for(int i=0; i<data.length;i++){
				out.print("\"" + data[i] + "\"");
				if(i != data.length-1)
					out.print(columnSeparator);
			}
			out.println();
			return;
		}
		isWidthExceed=true;
		while(isWidthExceed){
			for(int i=0;i<data.length;i++){
				if(data[i].length()>width[i]){
					isWidthExceed=true;
					break;
				}else{
					isWidthExceed=false;
				}
			}
			// Form prefix String up to column width
			String[] tempData=new String[data.length];
			for(int i=0;i<data.length;i++){
				tempData[i]=formPrefixString(data[i],width[i]);
			}
			
			// Fill prefix String 
			out.print(columnSeparator);							// | Filling separator for first column 
			for(int i=0;i<(tempData.length);i++){					 
				out.print(" ");										// | 
				out.print(getAlignedColumnValue(tempData[i], column_alignment[i], width[i]));  // | Value    as Per Specified Alignment
				out.print(" ");										// | Value 
				out.print(columnSeparator);						// | Value |
			}
			
			// New line for Next Record
			out.println();
			
			// Form remaining String to be added at new line 
			for(int i=0;i<data.length;i++){
				data[i]=data[i].substring(tempData[i].length(),data[i].length());
			}
		}
		
		// For ALL_BORDER : Add Patter after Header and each Record inserted
		if(format == ALL_BORDER){
			out.println(fillChar("", patternLength, rowSeparator));
		}
	}
	
	public void addNewLine(){
		out.println();
	}
	
	public void add(String string){
		out.print(string);
	}
	
	public void add(String string,int alignment){
		
		if(format == CSV){
			string = "\"" + string + "\"";
		}
		out.println(getAlignedValue(string, alignment));
		
		// For ALL_BORDER : Add Patter after Header and each Record inserted
		if(format == ALL_BORDER){
			out.println(fillChar("", patternLength, rowSeparator));
		}
	}
	
	public void add(int columnIndex,String value,int alignment){
		if(!(columnIndex<0 && columnIndex>width.length)){
			String[] data=new String[width.length];
			for(int i=0;i<width.length;i++){
				data[i]="";
			}
			data[columnIndex-1]=getAlignedColumnValue(value, alignment, width[columnIndex-1]);
			addRecord(data);
		}
	}
	
	private String getAlignedColumnValue(String value, int alignment, int width) {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		switch(alignment){
		case TableFormatter.RIGHT : out.print(fillChar(width - value.length() , "" ,' ') + value); break;
		case TableFormatter.CENTER : out.print(fillChar((width - value.length())/2 , "" , ' ') + value + fillChar(width - (width - value.length())/2 - value.length() ,"" ,' ')); break;
		case TableFormatter.LEFT : out.print(value + fillChar("", width - value.length() , ' ')); break;
		}
		return writer.toString();
	}

	public void add(int columnIndex,String value){
		if(columnIndex>0 && columnIndex<=width.length){
			String[] data=new String[width.length];
			for(int i=0;i<width.length;i++){
				data[i]="";
			}
			data[columnIndex-1]=value;
			addRecord(data);
		}
	}
	
	private String getAlignedValue(String value,int alignment){

		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		if(format != CSV)
			out.print(columnSeparator);
		out.print(" ");
		int width = patternLength - (columnSeparator.length() + LEFT_MARGIN)*2;
		switch(alignment){
		case TableFormatter.RIGHT  : out.print(fillChar("", width - value.length()  ,' ') + value); break;
		case TableFormatter.CENTER : out.print(fillChar("", (width - value.length())/2, ' ') + value + fillChar(width - (width - value.length())/2 - value.length() ,"" ,' ')); break;
		case TableFormatter.LEFT   : out.print(value + fillChar("", width - value.length() , ' ') ); break;
		}
		out.print(" ");
		if(format != CSV)
			out.print(columnSeparator);
		return writer.toString();
	}
	
	public String getFormattedValues(){
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print(stringWriter.toString());
		
		// For OUTER_BORDER : Add Patter after last Row inserted.
		if(format == OUTER_BORDER){
			out.println(fillChar("", patternLength, rowSeparator));
		}
		return writer.toString();
	}
	
	private String formPrefixString(String str,int length){
		if(str.length()>length){
			return str.substring(0,length);
		}
		return str;
	}
	
	private String fillChar(String input, int length, char chr){
		if (input == null)
			input = "";
		StringBuilder stringBuffer = new StringBuilder();
		stringBuffer.append(input);
		for(int i = input.length(); i<length; i++){
			stringBuffer.append(chr);
		}
		return stringBuffer.toString();
	}
	
	private String fillChar(int length, String input, char chr){
		if (input == null)
			input = "";
		StringBuilder stringBuffer = new StringBuilder();
		for(int i = input.length(); i<(length); i++){
			stringBuffer.append(chr);
		}
		stringBuffer.append(input);
		return stringBuffer.toString();
	}
}
