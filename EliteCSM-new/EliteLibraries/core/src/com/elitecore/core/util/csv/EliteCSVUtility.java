package com.elitecore.core.util.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.elitecore.commons.logging.LogManager;

/**
 * 
 * @author Manjil Purohit
 *
 */
public class EliteCSVUtility {
	
	public static final String MODULE = "CSV-UTIL";
	
	/**
	 * This method parses {@link File} and returns a list of Map. Each map stores a one line record of CSV file in the form of
	 *  "Header Field --> Value"
	 * @param file
	 * @return List of Map
	 * @throws InvalidCSVFileException 
	 */
	public static List<Map<String, String>> parseCSV(File file) throws InvalidCSVFileException {
		try {
			if(isValidFile(file)) 
				return parseCSV(new FileInputStream(file), ",");
			else 
				throw new InvalidCSVFileException();
		} catch (IOException ioExe) {
			LogManager.getLogger().trace(MODULE, ioExe);
		}
		return null;
	}
	
	/**
	 * This method parses {@link File} with given delimiter and returns a list of Map. Each map stores a one line record of CSV
	 * file in the form of "Header Field --> Value"
	 * @param file
	 * @param delimiter
	 * @return List of Map
	 * @throws InvalidCSVFileException 
	 */
	public static List<Map<String, String>> parseCSV(File file, String delimiter) throws InvalidCSVFileException {
		try {
			if(isValidFile(file))
				return parseCSV(new FileInputStream(file), delimiter);
			else 
				throw new InvalidCSVFileException();
		} catch (IOException ioExe) {
			LogManager.getLogger().trace(MODULE, ioExe);
		}
		return null;
	}
	
	/**
	 * This method parses based on {@link InputStream} with given delimiter and returns a list of Map. Each map stores a one
	 * line record of CSV file in the form of "Header Field --> Value"
	 * @param inputStream
	 * @param delimiter
	 * @return List of Map
	 */
	public static List<Map<String, String>> parseCSV(InputStream inputStream, String delimiter) {
		int col = 0;
		String record = null;
		String[] header = null; 
		boolean isHeader = true;
		List<Map<String, String>> objects = new ArrayList<Map<String,String>>();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		
	    try {
			while((record = bufferedReader.readLine()) != null && record.trim().length() > 0){
				StringTokenizer st = new StringTokenizer(record, delimiter);
				
				if(isHeader){
					isHeader = false;
					header = new String[st.countTokens()];
					col = 0;
					while(st.hasMoreTokens()){
						String token = st.nextToken().trim();
						header[col] = token;
						col++;
					}
					if(col > 0){
						LogManager.getLogger().debug(MODULE, "Header: " + record);
						continue;
					}
				}
				LogManager.getLogger().debug(MODULE, "Record: " + record);
				Map<String, String> map = new LinkedHashMap<String, String>();
				col = 0;
				while(st.hasMoreTokens()) {
					String token = st.nextToken().trim();
					// Support of escape character
					while(token.endsWith("\\")) {
						token = token.replace("\\", ",") + st.nextToken();
					}
					map.put(header[col], token);
					col++;
				}
				objects.add(map);
			}
		} catch (IOException ioExe) {
			LogManager.getLogger().trace(MODULE, ioExe);
		} finally {
			
			if(inputStream != null) 
				try { inputStream.close();
				} catch (IOException e1) { LogManager.getLogger().trace(MODULE, "Error while closing InputStream !"); }
			if(bufferedReader != null)
				try { bufferedReader.close();
				} catch (IOException e) { LogManager.getLogger().trace(MODULE, "Error while closing BufferedReader !"); }
		}
		return objects;
	}
	public static CSVData parseCSVExt(InputStream inputStream, String delimiter) {
		String record = null;
		CSVData csvData= null; 
		String[] fieldNames = null; 
		boolean isHeader = true;
		BufferedReader bufferedReader = null;
		
	    try {
	    	bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    	csvData = new CSVData();
	    	int lineNumber = 0;
			while((record = bufferedReader.readLine()) != null){
				lineNumber++;
				if(isHeader){
					fieldNames  = record.split(delimiter);
					isHeader = false;
					LogManager.getLogger().debug(MODULE, "Header: " + record);
					continue;
				}
				
				LogManager.getLogger().debug(MODULE, "Record: " + record);
				String recordFields[] = record.split(delimiter);
				CSVRecordData csvRecordData = new CSVRecordData(lineNumber,recordFields);
				csvData.getRecords().add(csvRecordData);
			}
			csvData.setFieldNames(fieldNames);
		} catch (IOException ioExe) {
			LogManager.getLogger().trace(MODULE, ioExe);
		} finally {
			
			if(inputStream != null) 
				try { inputStream.close();
				} catch (IOException e1) { LogManager.getLogger().trace(MODULE, "Error while closing InputStream !"); }
			if(bufferedReader != null)
				try { bufferedReader.close();
				} catch (IOException e) { LogManager.getLogger().trace(MODULE, "Error while closing BufferedReader !"); }
		}
		return csvData;
	}
	public static boolean isValidFile(File file) {
		return file != null && file.isFile() && file.getName().toLowerCase().endsWith(".csv");
	}

}
