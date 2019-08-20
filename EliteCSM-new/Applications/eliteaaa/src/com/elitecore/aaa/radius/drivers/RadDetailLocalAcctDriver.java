package com.elitecore.aaa.radius.drivers;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.conf.DetailLocalAcctDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AttributeRelation;
import com.elitecore.aaa.core.drivers.DetailLocalAcctdriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;

public class RadDetailLocalAcctDriver extends DetailLocalAcctdriver {
	public static String MODULE = "RAD-DETAIL-LOCAL-ACCT-DRVR";
	private DetailLocalAcctDriverConfiguration radDetailLocalAcctConfiguration = null;
	private RadAcctDriver acctDriver;
	private final AAAServerContext serverContext;
	
	public RadDetailLocalAcctDriver(AAAServerContext serverContext, String driverInstanceId) {
		this(serverContext, (DetailLocalAcctDriverConfiguration)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
	}

	public RadDetailLocalAcctDriver(AAAServerContext serverContext, DetailLocalAcctDriverConfiguration driverConfiguration) {
		super(serverContext);
		this.serverContext = serverContext;
		this.radDetailLocalAcctConfiguration = driverConfiguration;
		currentLocation = driverConfiguration.getFileLocation();
		this.acctDriver = new RadAcctDriver();
	}

	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		super.initInternal();
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}
	
	@Override
	public String toString() {
		return radDetailLocalAcctConfiguration.toString();
	}
	
	@Override
	protected String getPattern() {
		return radDetailLocalAcctConfiguration.getPattern();
	}

	@Override
	protected boolean getGlobalization() {
		return radDetailLocalAcctConfiguration.getGlobalization();
	}

	@Override
	protected String[] getFileNameAttributes() {
		return radDetailLocalAcctConfiguration.getFileNameAttributes();
	}

	@Override
	protected String[] getFolderNameAttributes() {
		return radDetailLocalAcctConfiguration.getFolderNameAttributes();
	}

	@Override
	protected String getCounterFileName() {
		return getServerHome() + File.separator + "system" + File.separator + "cdr_sequence_detail_local_driver";
	}

	@Override
	protected String getAllocatingProtocol() {
		return radDetailLocalAcctConfiguration.getAllocatingProtocol();
	}

	@Override
	protected String getPassword() {
		//sending the plain text password
		return radDetailLocalAcctConfiguration.getPlainTextPassword();
	}

	@Override
	protected String getDestinationLocation() {
		return radDetailLocalAcctConfiguration.getDestinationLocation();
	}

	@Override
	protected String getUserName() {
		return radDetailLocalAcctConfiguration.getUsrName();
	}

	@Override
	protected int getPort() {
		return radDetailLocalAcctConfiguration.getPort();
	}

	@Override
	protected String getIpAddress() {
		return radDetailLocalAcctConfiguration.getIpAddress();
	}

	@Override
	protected int getFailOverTime() {
		return radDetailLocalAcctConfiguration.getFailOverTime();
	}

	@Override
	protected String getPostOperation() {
		return radDetailLocalAcctConfiguration.getPostOperation();
	}

	@Override
	protected String getArchiveLocations() {
		return radDetailLocalAcctConfiguration.getArchiveLocations();
	}

	@Override
	protected String getFileName() {
		return radDetailLocalAcctConfiguration.getFileName();
	}

	@Override
	protected String getWriteAttributes() {
		return radDetailLocalAcctConfiguration.getWriteAttributes();
	}

	@Override
	protected boolean getUseDictionaryValue() {
		return radDetailLocalAcctConfiguration.getUseDictionaryValue();
	}

	@Override
	protected String getAvPairSeparator() {
		return radDetailLocalAcctConfiguration.getAvPairSeparator();
	}

	@Override
	protected String getEventDateFormat() {
		return radDetailLocalAcctConfiguration.getEventDateFormat();
	}

	@Override
	protected boolean getCreateBlankFile() {
		return radDetailLocalAcctConfiguration.getCreateBlankFile();
	}

	@Override
	protected List<AttributeRelation> getAttributeRelationList() {
		return radDetailLocalAcctConfiguration.getAttributeRelationList();
	}

	@Override
	public String getName() {	
		return radDetailLocalAcctConfiguration.getDriverName();		
	}
	
	
	private List<IRadiusAttribute> getConfiguredAttributesList(ServiceRequest request) {
		List<AttributeRelation> attributesList = getAttributeRelationList();
		List<IRadiusAttribute> radAttributesList = new ArrayList<IRadiusAttribute>();
		 
		int size = attributesList.size();
		IRadiusAttribute radiusAttribute = null;
		for(int index=0;index < size;index++) {
			radiusAttribute = ((RadServiceRequest)request).getRadiusAttribute(attributesList.get(index).getAttributeId(),true);
			radAttributesList.add(radiusAttribute);
		}
	return radAttributesList;
	}

	@Override
	protected void writeAttributes(ServiceRequest request,
			EliteFileWriter fileWriter,String destinationPath) {
		if(fileWriter!=null){
			if(getWriteAttributes().equalsIgnoreCase("ALL")) {
				fileWriter.appendRecord(getAttributesToWrite((ArrayList<IRadiusAttribute>)((RadServiceRequest)request).getRadiusAttributes()));
				LogManager.getLogger().trace(MODULE, "All Atttributes dumped using DetailLocalAcct Driver");
				LogManager.getLogger().trace(MODULE, "Location For Dumped File:" +destinationPath);
			}else if(getWriteAttributes().equalsIgnoreCase("CONFIGURED")) {
				fileWriter.appendRecord(getAttributesToWrite(getConfiguredAttributesList(request)));
				LogManager.getLogger().trace(MODULE, "Configured Attributes dumped using DetailLocalAcct Driver");
				LogManager.getLogger().trace(MODULE, "Location For Dumped File:" +destinationPath);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Attributes not dumped using DetailLocalAcct Driver :"+getName()+ " Reason : File Writer not initialized");
		}
	}
	private String getAttributesToWrite(List<IRadiusAttribute> radiusAttributesList) {
		StringWriter strWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(strWriter);
		String avPairSeparator = getAvPairSeparator();

		String strDateFormat = getSimpleDateFormat().format(new Date());
		if(strDateFormat.contains(avPairSeparator)){
			strDateFormat = strDateFormat.replaceAll(avPairSeparator, "\\\\"+avPairSeparator);
		}
		printWriter.println(strDateFormat);
		printWriter.println();

		// Iterate over the list of attributes in the configuration file
		int size = radiusAttributesList.size();
		IRadiusAttribute radiusAttribute = null;
		AttributeRelation attributeRelation = null;
		 
		boolean bUseDictionaryValue = getUseDictionaryValue();
		
		for(int index=0;index<size;index++) {
			radiusAttribute = radiusAttributesList.get(index);
			String attributeValue = "";
			
			
			boolean found = false;

			if(radiusAttribute == null) {
				attributeRelation = getAttributeRelationList().get(index);
				attributeValue = attributeRelation.getDefaultVale();
			
			}else {
				attributeValue = radiusAttribute.getStringValue(bUseDictionaryValue);
				
			}
			
			if(attributeValue!=null && attributeValue.contains(avPairSeparator)){
				attributeValue = attributeValue.replaceAll(avPairSeparator, "\\\\"+avPairSeparator);
			}

			if(radiusAttribute != null){
				if(!radiusAttribute.isVendorSpecific()){
					StringBuilder strBuilder = new StringBuilder("\t");
					strBuilder.append(Dictionary.getInstance().getAttributeName(radiusAttribute.getID()));
					strBuilder.append(" ");
					strBuilder.append(avPairSeparator);
					strBuilder.append(" ");
					strBuilder.append(attributeValue);
					printWriter.println(strBuilder.toString());
					found = true;								
				} else{
					StringBuilder strBuilder = new StringBuilder("\t");
					VendorSpecificAttribute vendorSpecificAttr = (VendorSpecificAttribute)radiusAttribute;
					IVendorSpecificAttribute vendorAttrType = vendorSpecificAttr.getVendorTypeAttribute();
					List<IRadiusAttribute> radiusAttrList = (List<IRadiusAttribute>) vendorAttrType.getAttributes();
					for(IRadiusAttribute attribute : radiusAttrList){
						strBuilder.append(Dictionary.getInstance().getVendorName(radiusAttribute.getVendorID()));
						strBuilder.append(":");
						strBuilder.append(Dictionary.getInstance().getAttributeName(radiusAttribute.getVendorID(),attribute.getType()));
						strBuilder.append(" ");
						strBuilder.append(avPairSeparator);
						strBuilder.append(" ");
						strBuilder.append(attribute.getStringValue());
						printWriter.println(strBuilder.toString());
					}
					found = true;
				}
			}

			if(!found){
				try{
					if(attributeRelation!=null){
						AttributeId attributeId = Dictionary.getInstance().getAttributeId(attributeRelation.getAttributeId());
						if(attributeId!=null){
							StringBuilder strBuilder = new StringBuilder("\t");
							if(attributeId.getVendorId() != 0 ){
								strBuilder.append(attributeId.getVendorId());
								strBuilder.append(":");
							}
							strBuilder.append(attributeId.getAttributeName());
							strBuilder.append(" ");
							strBuilder.append(getAvPairSeparator());
							strBuilder.append(" ");
							strBuilder.append(attributeValue);					
							printWriter.println(strBuilder.toString());
						}
					}	
				}catch (InvalidAttributeIdException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Invalid Attribute id : "+attributeRelation.getAttributeId()+" found in driver : "+getName());
				}	
			}		
		}
		printWriter.println();
		return strWriter.toString(); 
	}

	@Override
	protected String getNameFromArray(String[] fileAttributes,
			ServiceRequest request) {
		
		return this.acctDriver.getNameFromArray(fileAttributes, (RadServiceRequest)request);
	}

	@Override
	public int getType() {
		return DriverTypes.RAD_DETAIL_LOCAL_ACCT_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RAD_DETAIL_LOCAL_ACCT_DRIVER.name();
	}

	@Override
	protected String getDefaultDirName() {
		return radDetailLocalAcctConfiguration.getDefauleDirName();
	}

	@Override
	protected String getSequenceRange() {
		return radDetailLocalAcctConfiguration.getSequenceRange();
	}
	
	@Override
	public void reInit() {
		this.radDetailLocalAcctConfiguration = (DetailLocalAcctDriverConfiguration)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(this.radDetailLocalAcctConfiguration.getDriverInstanceId());
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
		return radDetailLocalAcctConfiguration.getRollingTypeMap();
	}
}
