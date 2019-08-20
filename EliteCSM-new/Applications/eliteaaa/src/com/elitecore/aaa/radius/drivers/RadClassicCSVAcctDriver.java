package com.elitecore.aaa.radius.drivers;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AttributesRelation;
import com.elitecore.aaa.core.data.StripAttributeRelation;
import com.elitecore.aaa.core.drivers.ClassicCSVAcctDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import static com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration.PREFIX;

public class RadClassicCSVAcctDriver extends ClassicCSVAcctDriver {
	private static final String MODULE = "RAD-CLASSIC-CSV-DRVR";
	private ClassicCSVAcctDriverConfiguration radClassicCSVAcctConfiguration = null;
	private RadAcctDriver acctDriver;
	private final AAAServerContext serverContext;
	private TimeSource timeSource = TimeSource.systemTimeSource();

	public RadClassicCSVAcctDriver(AAAServerContext serverContext, String driverInstanceId) {
		this(serverContext, (ClassicCSVAcctDriverConfiguration)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
	}
	
	public RadClassicCSVAcctDriver(AAAServerContext serverContext, ClassicCSVAcctDriverConfiguration driverConfiguration) {
		super(serverContext);
		this.serverContext = serverContext;
		currentLocation = driverConfiguration.getFileLocation();
		this.radClassicCSVAcctConfiguration = driverConfiguration;
		this.acctDriver = new RadAcctDriver();
	}
	
	@VisibleForTesting
	public RadClassicCSVAcctDriver(AAAServerContext serverContext, String driverInstanceId, TimeSource timeSource) {
		this(serverContext, driverInstanceId);
		this.timeSource = timeSource;
	}
	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		super.initInternal();
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}
		
	@Override
	public String toString() {
		return radClassicCSVAcctConfiguration.toString();
	}
	
	@Override
	protected String getAllocatingProtocol() {
		return radClassicCSVAcctConfiguration.getAllocatingProtocol();
	}

	@Override
	protected String getArchiveLocations() {
		return radClassicCSVAcctConfiguration.getArchiveLocations();
	}

	@Override
	protected List<AttributesRelation> getAttributesRelationList() {
		return radClassicCSVAcctConfiguration.getAttributesRelationList();
	}

	@Override
	protected String getCounterFileName() {
		return getServerHome() + File.separator + "system" + File.separator + "cdr_sequence_classic_csv_driver";
	}

	@Override
	protected int getFailOverTime() {
		return radClassicCSVAcctConfiguration.getFailOverTime();
	}

	@Override
	protected String getFileName() {
		return radClassicCSVAcctConfiguration.getFileName();
	}

	@Override
	protected String[] getFileNameAttributes() {
		return radClassicCSVAcctConfiguration.getFileNameAttributes();
	}

	@Override
	protected String[] getFolderNameAttributes() {
		return radClassicCSVAcctConfiguration.getFolderNameAttributes();
	}

	@Override
	protected boolean getGlobalization() {
		return radClassicCSVAcctConfiguration.getGlobalization();
	}

	@Override
	protected String getIpAddress() {
		return radClassicCSVAcctConfiguration.getIpAddress();
	}

	@Override
	protected String getPassword() {
		//returning the plain text password
		return radClassicCSVAcctConfiguration.getPlainTextPassword();
	}

	@Override
	protected String getPattern() {
		return radClassicCSVAcctConfiguration.getPattern();
	}

	@Override
	protected int getPort() {
		return radClassicCSVAcctConfiguration.getPort();
	}

	@Override
	protected String getPostOperation() {
		return radClassicCSVAcctConfiguration.getPostOperation();
	}

	@Override
	protected String getDestinationLocation() {
		return radClassicCSVAcctConfiguration.getDestinationLocation();
	}

	@Override
	protected String getUserName() {
		return radClassicCSVAcctConfiguration.getUsrName();
	}

	@Override
	protected String getDelimeter() {
		return radClassicCSVAcctConfiguration.getDelimeter();
	}

	@Override
	protected String getHeader() {
		return radClassicCSVAcctConfiguration.getHeader();
	}

	@Override
	protected List<StripAttributeRelation> getStripAttributeRelationList() {
		return radClassicCSVAcctConfiguration.getStripAttributeRelationList();
	}

	@Override
	protected String getDelimeterFirst() {
		return radClassicCSVAcctConfiguration.getDelimeterFirst();
	}

	@Override
	protected String getDelimeterLast() {
		return radClassicCSVAcctConfiguration.getDelimeterLast();
	}

	@Override
	protected boolean getCreateBlankFile() {
		return radClassicCSVAcctConfiguration.getCreateBlankFile();
	}

	@Override
	protected String getmultiValueDelimeter() {
		return radClassicCSVAcctConfiguration.getmultiValueDelimeter();
	}

	@Override
	protected String getDefaultDirName() {
		return radClassicCSVAcctConfiguration.getDefaultDirName();
	}

	@Override
	protected String getCDRTimeStampHeader() {
		return radClassicCSVAcctConfiguration.getCDRTimeStampHeader();
	}
	
	@Override
	protected String getCDRTimeStampFormat() {
		return radClassicCSVAcctConfiguration.getCDRTimeStampFormat();
	}
	
	@Override
	protected String getCDRTimeStampPosition() {
		return radClassicCSVAcctConfiguration.getCDRTimeStampPosition();
	}
	
	@Override
	protected String getSequenceRange() {
		return radClassicCSVAcctConfiguration.getSequenceRange();
	}
	
	@Override
	public String getName() {
		return  radClassicCSVAcctConfiguration.getDriverName();
	}
	
	@Override
	protected String getNameFromArray(String[] fileAttributes,ServiceRequest request) {
		return this.acctDriver.getNameFromArray(fileAttributes, (RadServiceRequest)request);		
	}
	
	@Override
	protected String getConfiguredCSVLine(ServiceRequest request) throws DriverProcessFailedException{
		StringBuilder strBuilder = new StringBuilder();
		
		// For first iteration this separator will contain start delimiter.
		String strSeperator = (getDelimeterFirst()!=null)?getDelimeterFirst():"";
		RadServiceRequest radServiceRequest = (RadServiceRequest)request;
		
		//This separator contains configured delimiter
		String delimiter = getDelimeter();
		
		List<AttributesRelation> attributeRelationList = getAttributesRelationList();
		int attrRelListSize = attributeRelationList.size();
		AttributesRelation attributesRelation=null;
		String defualtValue = null;
		List<String> attributeList = null;
		String multiValDel = getmultiValueDelimeter();
		for(int i=0;i<attrRelListSize;i++){
		String enclosingChar = getEnclosingChar();
			attributesRelation = attributeRelationList.get(i);
			defualtValue = attributesRelation.getDefaultValue();
			attributeList = attributesRelation.getAttributeList();
			int attrListSize = attributeList.size();
			String attributeId=null;
			
			int iFoundOccurence =0; 
			for(int j=0;j<attrListSize;j++){
				attributeId = attributeList.get(j);
				ArrayList<IRadiusAttribute> radAttributeList = (ArrayList<IRadiusAttribute>)radServiceRequest.getRadiusAttributes(attributeId,true);
				
				if(radAttributeList!=null && radAttributeList.size()>0){
					StringBuilder valueBuilder = new StringBuilder();
					int radAttributeListsize = radAttributeList.size();
					IRadiusAttribute radAttribute = null;
					for(int k = 0;k<radAttributeListsize;k++){
						radAttribute = radAttributeList.get(k);
						if(radAttribute!=null){
							String radAttributeValue = radAttribute.getStringValue(attributesRelation.getUserDictionaryValue());
							if(radAttributeValue!=null){
								radAttributeValue = getStrippedValue(attributeId, radAttributeValue);
								
								if(delimiter.length()>0 && radAttributeValue.contains(delimiter)){
									radAttributeValue = radAttributeValue.replaceAll(delimiter, "\\\\"+delimiter);
								}
								
								if(multiValDel.length()>0 && radAttributeValue.contains(multiValDel)){
									radAttributeValue = radAttributeValue.replaceAll(multiValDel, "\\\\"+multiValDel);
								}
								
								if(valueBuilder.length() >0)
									valueBuilder.append(multiValDel);
								valueBuilder.append(radAttributeValue);
								
								iFoundOccurence++;
							}	
						}
					}
					
					String strValueBuilder = valueBuilder.toString();
					if(iFoundOccurence > 1){
						if(enclosingChar==null){
							enclosingChar = "\"";
						}
						if(strValueBuilder.contains(enclosingChar)){
							strValueBuilder = strValueBuilder.replaceAll(enclosingChar, "\\\\"+enclosingChar);
						}
						strBuilder.append(enclosingChar);
						strBuilder.append(strValueBuilder);
						strBuilder.append(enclosingChar);
					}else{
						if(enclosingChar!=null){
							if(strValueBuilder.contains(enclosingChar)){
								strValueBuilder = strValueBuilder.replaceAll(enclosingChar, "\\\\"+enclosingChar);
							}	
							strBuilder.append(enclosingChar);
							strBuilder.append(strValueBuilder);
							strBuilder.append(enclosingChar);
						}else {
						strBuilder.append(valueBuilder.toString());
					}
					}
					
					if(iFoundOccurence > 0)
						break;
				}
			}
			
			if(iFoundOccurence == 0){
				if(enclosingChar != null){
					
					if(defualtValue.contains(enclosingChar)){
						defualtValue = defualtValue.replaceAll(enclosingChar, "\\\\"+enclosingChar);
					}
				strBuilder.append(enclosingChar);
					strBuilder.append(defualtValue);
				strBuilder.append(enclosingChar);
				}else{
					strBuilder.append(defualtValue);
			}
			}
			
			strSeperator = (getDelimeter() != null)?getDelimeter():"";
			
			if(i != attributeRelationList.size() - 1)
				strBuilder.append(strSeperator);		
			
		}

		if(getSimpleDateFormat() != null) {
			String strDateFormat = getSimpleDateFormat().format(new Timestamp(timeSource.currentTimeInMillis()));
			if (PREFIX.equalsIgnoreCase(getCDRTimeStampPosition())) {
				strBuilder.insert(0, strDateFormat + strSeperator);
			} else {
				strBuilder.append(strSeperator);
				strBuilder.append(strDateFormat);	
			}
		}

		strBuilder.append(getDelimeterLast()!=null?getDelimeterLast():"");
		return strBuilder.toString();
	}
	

	@Override
	protected String getConfiguredCSVHeaderLine() throws DriverInitializationFailedException {
		StringBuilder strBuilder = new StringBuilder();
		String strSeperator = (getDelimeterFirst()!=null)?getDelimeterFirst():"";
				
		List<AttributesRelation> attributeRelationList = getAttributesRelationList();
		int attrRelationListSize = attributeRelationList.size();
		
		for(int i=0;i<attrRelationListSize;i++){
			
			AttributesRelation attributesRelation = attributeRelationList.get(i); 
			List<String> attributeList = attributesRelation.getAttributeList();
			String header =attributesRelation.getHeader();
			
			int attributeListSize = attributeList.size();

			String enclosingChar = getEnclosingChar();
			if(enclosingChar == null) {
				enclosingChar="";
			}
			if(header == null || header.trim().length() == 0) {
				strBuilder.append(strSeperator);
			for(int j=0;j<attributeListSize;j++){
				String strAttributeId = attributeList.get(j);
				AttributeId attributeId=null; 
				try {
					attributeId = Dictionary.getInstance().getAttributeId(strAttributeId);
						if(attributeId.getAttributeName() != null) {
					if(attributeId.getVendorId()!=-1){
								strBuilder.append(enclosingChar);
						if(attributeId.getVendorId()!= 0){
							strBuilder.append(attributeId.getVendorId());
							strBuilder.append(":");
						}
								strBuilder.append(attributeId.getAttributeName()+enclosingChar);
					}
							break; 
						}
					} catch (InvalidAttributeIdException e) {
						LogManager.getLogger().warn(MODULE, "Header can not be built in Classic CSV Acct Driver:" + getName()+ ": Reason:" + e.getMessage());
					}
				}
				}else {
					strBuilder.append(strSeperator);
				strBuilder.append(enclosingChar+header+enclosingChar);
				}
				strSeperator = (getDelimeter()!=null)?getDelimeter():"";
			}
				
		if(getSimpleDateFormat() != null) {
			if (PREFIX.equalsIgnoreCase(getCDRTimeStampPosition())) {
				strBuilder.insert(0, getCDRTimeStampHeader() + strSeperator);
			} else {
				strBuilder.append(strSeperator);
				strBuilder.append(getCDRTimeStampHeader());	
			}
		}
		
		strBuilder.append(getDelimeterLast()!=null?getDelimeterLast():"");
		
		return strBuilder.toString();
	}

	@Override
	public int getType() {
		return DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER.value;
	}

	@Override
	public String getTypeName() {
		return DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER.name();
	}
	@Override
	protected String getEnclosingChar() {
		return radClassicCSVAcctConfiguration.getEnclosingChar();
	}
	
	@Override
	public void reInit() {
		this.radClassicCSVAcctConfiguration = (ClassicCSVAcctDriverConfiguration)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(this.radClassicCSVAcctConfiguration.getDriverInstanceId());
	}

	@Override
	protected boolean isAttributeID(String str) {
		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(str);
		return attribute != null;
}

	@Override
	protected String getAttributeValue(String attrID, ServiceRequest request) {
		RadServiceRequest radServiceRequest = (RadServiceRequest)request;
		IRadiusAttribute attribute = radServiceRequest.getRadiusAttribute(attrID, true);
		if (attribute != null)
			return attribute.getStringValue();
		return null;
	}

	@Override
	protected Map<RollingTypeConstant, Integer> getRollingTypeMap() {
		return radClassicCSVAcctConfiguration.getRollingTypeMap();
	}
}
