package com.elitecore.aaa.core.util;

import java.io.File;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.ServiceRequest;

public class FileLocationParser {
	
	private static final int END_OF_STRING = -1;
	private static final String MODULE = "FILE-LOCATN-PRSR";
	
	private DirectoryLocationInfo[] directoryLocationInfos;
	private SimpleDateFormat dateFormat;
	private AttributeValueProvider valueProvider;
	private String moduleName;
	
	public FileLocationParser(String location, String moduleName, AttributeValueProvider valueProvider) {
		dateFormat = new SimpleDateFormat();
		this.valueProvider = valueProvider;
		this.moduleName = moduleName;
		parse(location);
	}

	private void parse(String location) {
		
		if (location == null || location.trim().length() < 1 ) {
			LogManager.getLogger().warn(MODULE, "File location not provided for:" + moduleName);
			return;
		}
		
		// If file location does not contain "{" and "}" that means it is a location without timestamp and attribute ID
		if ((location.contains("{") == false) || (location.contains("}") == false)) {
			DirectoryLocationInfo directoryLocationInfo = new DirectoryLocationInfo(location, DirectoryLocationInfo.STATIC_NAME);
			directoryLocationInfos = new DirectoryLocationInfo[1];
			directoryLocationInfos[0] = directoryLocationInfo;
			return;
		}
		
		List<DirectoryLocationInfo> directoryLocationInfoList = new ArrayList<DirectoryLocationInfo>();

		StringReader stringReader=new StringReader(location);
		int currentChar;
		try {
			StringBuilder currentToken = new StringBuilder();
			while((currentChar=stringReader.read())!= END_OF_STRING){
				char ch = (char) currentChar;

				if (ch == '\\') {
					if ((currentChar = stringReader.read()) != END_OF_STRING) { 
						ch = (char)currentChar; 
						currentToken.append(ch);
					}
					continue;
				}

				if (ch == File.separatorChar) {

					String str = currentToken.toString();
					currentToken = new StringBuilder();
					if (str.length() > 0) {
						DirectoryLocationInfo LocationInfo = new DirectoryLocationInfo(str, DirectoryLocationInfo.STATIC_NAME);
						directoryLocationInfoList.add(LocationInfo);
					}
					DirectoryLocationInfo LocationInfo = new DirectoryLocationInfo(File.separator, DirectoryLocationInfo.STATIC_NAME);
					directoryLocationInfoList.add(LocationInfo);
					continue;
				}

				if (ch == '{') {
					while (((currentChar = stringReader.read()) != END_OF_STRING)) {
						ch = (char) currentChar;
						if (ch == '\\') {
							if ((currentChar = stringReader.read()) != END_OF_STRING) { 
								ch = (char)currentChar; 
								currentToken.append(ch);
							}
						} else if (ch == '}') {
							break;
						} else {
							currentToken.append(ch);
						}
					};
					String str = currentToken.toString();
					DirectoryLocationInfo LocationInfo = null;
					if (valueProvider.isValidAttributeID(str))
						LocationInfo = new DirectoryLocationInfo(str, DirectoryLocationInfo.ATTR_ID);
					else
						LocationInfo = new DirectoryLocationInfo(str, DirectoryLocationInfo.TIME_STAMP);
					directoryLocationInfoList.add(LocationInfo);
					currentToken = new StringBuilder();
					continue;
				}
				currentToken.append(ch);

			};
			if (currentToken != null) {
				DirectoryLocationInfo LocationInfo = new DirectoryLocationInfo(currentToken.toString(), DirectoryLocationInfo.STATIC_NAME);
				directoryLocationInfoList.add(LocationInfo);
			}
		} catch (Exception exception ) {
			LogManager.getLogger().warn(MODULE, "Invalid File location: " + location + " configured for: " 
					+ moduleName + " Reason: " + exception.getMessage());
			DirectoryLocationInfo directoryLocationInfo = new DirectoryLocationInfo(location, DirectoryLocationInfo.STATIC_NAME);
			directoryLocationInfos = new DirectoryLocationInfo[1];
			directoryLocationInfos[0] = directoryLocationInfo;
			return;
		}
		
		directoryLocationInfos = new DirectoryLocationInfo[directoryLocationInfoList.size()];
		directoryLocationInfos = directoryLocationInfoList.toArray(directoryLocationInfos);
	}
	
	public String getLocation(ServiceRequest request) {
		String fileLocation = "";
		for ( int i=0 ; i<directoryLocationInfos.length ; i++) {

			switch (directoryLocationInfos[i].getType()) {
			case DirectoryLocationInfo.ATTR_ID:
				// If this attribute is not received in request then that token will be skipped in directory hierarchy
				// i.e. if configuration is data/{0:1}/csv/ then the location returned would be data/csv
				String avpValue = valueProvider.getAttributeValue(request, directoryLocationInfos[i].getName());
				if (avpValue != null && avpValue.trim().length() > 0) {
					fileLocation += avpValue;
				} else {
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) 
						LogManager.getLogger().info(MODULE, "Attribute with ID: " + directoryLocationInfos[i].getName() + " not received in request. Skipping its value " +
								"in directory hierarchy.");
				}
				break;
			case DirectoryLocationInfo.TIME_STAMP:
				try {
					dateFormat.applyPattern(directoryLocationInfos[i].getName());
					String dateVal = dateFormat.format(new Date());
					fileLocation += dateVal;
				} catch (IllegalArgumentException e) {
					LogManager.getLogger().warn(MODULE, "Invalid Timestamp format: " + directoryLocationInfos[i].getName() + " configured for file location in driver: " + moduleName);
					fileLocation += directoryLocationInfos[i].getName();
				}
				
				break;
			default:
				fileLocation += directoryLocationInfos[i].getName();		
			}
		}
		return fileLocation;
		
	}
	
	// This class contains information of directory name and its type. Type includes static name, 
	// time stamp and attribute ID. For these types constants are defined
	private class DirectoryLocationInfo {
		private final static short STATIC_NAME = 1;
		private final static short ATTR_ID = 2;
		private final static short TIME_STAMP = 3;
		
		String name;
		short type;
		public DirectoryLocationInfo(String name, short type) {
			this.name = name;
			this.type = type;
		}
		
		public String getName() {
			return name;
		}
		
		public short getType() {
			return type;
		}
	}
	
	// Implementation of this interface is needed to be supplied by user of FileLocationParser
	// This is used to get value of an attribute ID and checking whether a string is a valid attribute ID
	public interface AttributeValueProvider {
		public String getAttributeValue(ServiceRequest serviceRequest, String attrID);
		public boolean isValidAttributeID(String attrID);
	}
}
