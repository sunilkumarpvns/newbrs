package com.elitecore.aaa.diameter.service.application.drivers;


import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AttributesRelation;
import com.elitecore.aaa.core.data.StripAttributeRelation;
import com.elitecore.aaa.core.drivers.ClassicCSVAcctDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import static com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration.PREFIX;

public class NasClassicCSVAcctDriver extends ClassicCSVAcctDriver {
	public static String MODULE = "NAS-CLASSIC-CSV-ACCT-Drvr";
	private ClassicCSVAcctDriverConfiguration classicCSVAcctConfiguration = null;
	private NasAcctDriver acctDriver = new NasAcctDriver();
	private TimeSource timeSource = TimeSource.systemTimeSource();
	public NasClassicCSVAcctDriver(ServerContext serverContext,String driverInstanceId) {
		this(serverContext, (ClassicCSVAcctDriverConfiguration)((AAAServerContext)serverContext).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(driverInstanceId), driverInstanceId);
	}
	
	public NasClassicCSVAcctDriver(ServerContext serverContext, ClassicCSVAcctDriverConfiguration driverConfiguration, String driverInstanceId) {
		super(serverContext);		
		this.classicCSVAcctConfiguration = driverConfiguration;
		currentLocation = driverConfiguration.getFileLocation();
	}

	@VisibleForTesting
	public NasClassicCSVAcctDriver(ServerContext serverContext,String driverInstanceId, TimeSource timeSource) {
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
		return classicCSVAcctConfiguration.toString();
	}
	
	@Override
	protected String getAllocatingProtocol() {
		return classicCSVAcctConfiguration.getAllocatingProtocol();
	}

	@Override
	protected String getArchiveLocations() {
		return classicCSVAcctConfiguration.getArchiveLocations();
	}

	@Override
	protected List<AttributesRelation> getAttributesRelationList() {
		return classicCSVAcctConfiguration.getAttributesRelationList();
	}

	@Override
	protected String getCounterFileName() {
		return getServerContext().getServerHome()+ File.separator + "system"+ File.separator + "cdr_sequence_classic_csv_driver";
	}

	@Override
	protected int getFailOverTime() {
		return classicCSVAcctConfiguration.getFailOverTime();
	}

	@Override
	protected String getFileName() {
		return classicCSVAcctConfiguration.getFileName();
	}

	@Override
	protected String[] getFileNameAttributes() {
		return classicCSVAcctConfiguration.getFileNameAttributes();
	}

	@Override
	protected String[] getFolderNameAttributes() {
		return classicCSVAcctConfiguration.getFolderNameAttributes();
	}

	@Override
	protected boolean getGlobalization() {
		return classicCSVAcctConfiguration.getGlobalization();
	}

	@Override
	protected String getIpAddress() {
		return classicCSVAcctConfiguration.getIpAddress();
	}

	@Override
	protected String getPassword() {
		//returning the plain text password
		return classicCSVAcctConfiguration.getPlainTextPassword();
	}

	@Override
	protected String getPattern() {
		return classicCSVAcctConfiguration.getPattern();
	}

	@Override
	protected int getPort() {
		return classicCSVAcctConfiguration.getPort();
	}

	@Override
	protected String getPostOperation() {
		return classicCSVAcctConfiguration.getPostOperation();
	}

	@Override
	protected String getDestinationLocation() {
		return classicCSVAcctConfiguration.getDestinationLocation();
	}

	@Override
	protected String getUserName() {
		return classicCSVAcctConfiguration.getUsrName();
	}

	@Override
	protected String getDelimeter() {
		return classicCSVAcctConfiguration.getDelimeter();
	}

	@Override
	protected String getHeader() {
		return classicCSVAcctConfiguration.getHeader();
	}

	@Override
	protected List<StripAttributeRelation> getStripAttributeRelationList() {
		return classicCSVAcctConfiguration.getStripAttributeRelationList();
	}

	@Override
	protected String getDelimeterFirst() {
		return classicCSVAcctConfiguration.getDelimeterFirst();
	}

	@Override
	protected String getDelimeterLast() {
		return classicCSVAcctConfiguration.getDelimeterLast();
	}

	@Override
	protected boolean getCreateBlankFile() {
		return classicCSVAcctConfiguration.getCreateBlankFile();
	}

	@Override
	protected String getmultiValueDelimeter() {
		return classicCSVAcctConfiguration.getmultiValueDelimeter();
	}

	@Override
	protected String getDefaultDirName() {
		return classicCSVAcctConfiguration.getDefaultDirName();
	}

	@Override
	protected String getCDRTimeStampFormat() {
		return classicCSVAcctConfiguration.getCDRTimeStampFormat();
	}
	
	@Override
	@Nonnull protected String getCDRTimeStampHeader() {
		return classicCSVAcctConfiguration.getCDRTimeStampHeader();
	}

	@Override
	@Nonnull protected String getCDRTimeStampPosition() {
		return classicCSVAcctConfiguration.getCDRTimeStampPosition();
	}
	
	@Override
	public String getName() {
		return  classicCSVAcctConfiguration.getDriverName();
	}
	
	@Override
	protected String getConfiguredCSVLine(ServiceRequest request) throws DriverProcessFailedException{
		StringBuilder strBuilder = new StringBuilder();
		ApplicationRequest nasApplicationRequest = (ApplicationRequest)request;
		
		// For first iteration this separator will contain start delimiter.
		String strSeperator = (getDelimeterFirst()!=null)?getDelimeterFirst():"";
		
		List<AttributesRelation> attributeRelationList = getAttributesRelationList();
		int attrRelListSize = attributeRelationList.size();
		
		//This separator contains configured delimiter
		String delimiter = getDelimeter();
		
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
				
				List<IDiameterAVP> diameterAvpList = nasApplicationRequest.getAVPList(attributeId,true);
				if(diameterAvpList!=null && diameterAvpList.size()>0){
					StringBuilder valueBuilder = new StringBuilder();
					int radAttributeListsize = diameterAvpList.size();
					IDiameterAVP diameterAVP = null;
					
					for(int k = 0;k<radAttributeListsize;k++){
						diameterAVP = diameterAvpList.get(k);
						if(diameterAVP!=null){
							String diaAvpValue = diameterAVP.getStringValue(attributesRelation.getUserDictionaryValue());
							if(diaAvpValue!=null){
								diaAvpValue = getStrippedValue(attributeId, diaAvpValue);
								
								if(delimiter.length()>0 && diaAvpValue.contains(delimiter)){
									diaAvpValue = diaAvpValue.replaceAll(delimiter, "\\\\"+delimiter);
								}
								
								if(multiValDel.length()>0 && diaAvpValue.contains(multiValDel)){
									diaAvpValue = diaAvpValue.replaceAll(multiValDel, "\\\\"+multiValDel);
								}
								
								if(valueBuilder.length() >0)
									valueBuilder.append(multiValDel);
								valueBuilder.append(diaAvpValue);
								
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
			// do i need to check for multivalue delimeter ?
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
	protected String getConfiguredCSVHeaderLine()
			throws DriverInitializationFailedException {
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
	protected String getNameFromArray(String[] fileAttributes,
			ServiceRequest request) {
		return this.acctDriver.getNameFromArray(fileAttributes, request);
	}

	@Override
	public int getType() {
		return DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER.name();
	}

	@Override
	protected String getSequenceRange() {
		return classicCSVAcctConfiguration.getSequenceRange();
	}

	@Override
	protected String getEnclosingChar() {
		return classicCSVAcctConfiguration.getEnclosingChar();
}
	@Override
	public void reInit() {
		this.classicCSVAcctConfiguration =  (ClassicCSVAcctDriverConfiguration)((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(classicCSVAcctConfiguration.getDriverInstanceId());
	}

	@Override
	protected boolean isAttributeID(String str) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(str);
		return avp != null;
}

	@Override
	protected String getAttributeValue(String attrID, ServiceRequest request) {
		ApplicationRequest nasApplicationRequest = (ApplicationRequest)request;
		IDiameterAVP avp = nasApplicationRequest.getAVP(attrID,true);
		if (avp != null)
			return avp.getStringValue();
		return null;
	}

	@Override
	protected Map<RollingTypeConstant, Integer> getRollingTypeMap() {
		return classicCSVAcctConfiguration.getRollingTypeMap();
	}
}
