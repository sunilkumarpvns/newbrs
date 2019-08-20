package com.elitecore.aaa.diameter.service.application.drivers;

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
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;

public class NasDetailLocalAcctDriver extends DetailLocalAcctdriver {

	public static String MODULE = "NAS-DETAIL-LOCAL-ACCT-Drvr";
	private DetailLocalAcctDriverConfiguration detailLocalAcctConfiguration = null;
	
	private NasAcctDriver acctDriver = new NasAcctDriver();
	
	public NasDetailLocalAcctDriver(ServerContext serverContext,String driverInstanceId) {
		this(serverContext, (DetailLocalAcctDriverConfiguration)((AAAServerContext)serverContext).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(driverInstanceId), driverInstanceId);
	}

	public NasDetailLocalAcctDriver(ServerContext serverContext, DetailLocalAcctDriverConfiguration driverConfiguration, String driverInstanceId) {
		super(serverContext);
		this.detailLocalAcctConfiguration = driverConfiguration;
	}

	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		super.initInternal();
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}
	
	@Override
	public String toString() {
		return detailLocalAcctConfiguration.toString();
	}


	@Override
	protected String getPattern() {
		return detailLocalAcctConfiguration.getPattern();
	}

	@Override
	protected boolean getGlobalization() {
		return detailLocalAcctConfiguration.getGlobalization();
	}

	@Override
	protected String[] getFileNameAttributes() {
		return detailLocalAcctConfiguration.getFileNameAttributes();
	}

	@Override
	protected String[] getFolderNameAttributes() {
		return detailLocalAcctConfiguration.getFolderNameAttributes();
	}

	@Override
	protected String getAllocatingProtocol() {
		return detailLocalAcctConfiguration.getAllocatingProtocol();
	}

	@Override
	protected String getPassword() {
		//sending the plain text password
		return detailLocalAcctConfiguration.getPlainTextPassword();
	}

	@Override
	protected String getDestinationLocation() {
		return detailLocalAcctConfiguration.getDestinationLocation();
	}

	@Override
	protected String getUserName() {
		return detailLocalAcctConfiguration.getUsrName();
	}

	@Override
	protected int getPort() {
		return detailLocalAcctConfiguration.getPort();
	}

	@Override
	protected String getIpAddress() {
		return detailLocalAcctConfiguration.getIpAddress();
	}

	@Override
	protected int getFailOverTime() {
		return detailLocalAcctConfiguration.getFailOverTime();
	}

	@Override
	protected String getPostOperation() {
		return detailLocalAcctConfiguration.getPostOperation();
	}

	@Override
	protected String getArchiveLocations() {
		return detailLocalAcctConfiguration.getArchiveLocations();
	}
	
	@Override
	protected String getFileName() {
		return detailLocalAcctConfiguration.getFileName();
	}

	@Override
	protected String getWriteAttributes() {
		return detailLocalAcctConfiguration.getWriteAttributes();
	}

	@Override
	protected boolean getUseDictionaryValue() {
		return detailLocalAcctConfiguration.getUseDictionaryValue();
	}

	@Override
	protected String getAvPairSeparator() {
		return detailLocalAcctConfiguration.getAvPairSeparator();
	}

	@Override
	protected String getEventDateFormat() {
		return detailLocalAcctConfiguration.getEventDateFormat();
	}

	@Override
	protected boolean getCreateBlankFile() {
		return detailLocalAcctConfiguration.getCreateBlankFile();
	}

	@Override
	protected List<AttributeRelation> getAttributeRelationList() {
		return detailLocalAcctConfiguration.getAttributeRelationList();
	}

	@Override
	public String getName() {	
		return detailLocalAcctConfiguration.getDriverName();		
	}
	
	private List<IDiameterAVP> getConfiguredAttributesList(ApplicationRequest request) {
		List<AttributeRelation> attributesList = getAttributeRelationList();
		List<IDiameterAVP> avpList = new ArrayList<IDiameterAVP>();
		 
		int size = attributesList.size();
		IDiameterAVP avp = null;
		for(int index=0;index < size;index++) {
			avp = request.getAVP(attributesList.get(index).getAttributeId());
			avpList.add(avp);
		}
	return avpList;
	}

	@Override
	protected void writeAttributes(ServiceRequest serviceRequest,
			EliteFileWriter fileWriter,String destinationPath) {
		ApplicationRequest request = (ApplicationRequest)serviceRequest;
		if(fileWriter!=null){
			if(getWriteAttributes().equalsIgnoreCase("ALL")) {
				fileWriter.appendRecord(getAttributesToWrite(request.getAvps()));
				LogManager.getLogger().trace(MODULE, "All Atttributes dumped using DetailLocalAcct Driver");
				LogManager.getLogger().trace(MODULE, "Location For Dumped File:" +destinationPath);
			}else if(getWriteAttributes().equalsIgnoreCase("CONFIGURED")) {
				fileWriter.appendRecord(getAttributesToWrite(getConfiguredAttributesList(request)));
				LogManager.getLogger().trace(MODULE, "Configured Attributes dumped using DetailLocalAcct Driver");
				LogManager.getLogger().trace(MODULE, "Location For Dumped File:" +destinationPath);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Attributes not dumped using DetailLocalAcct Driver ,Reason : File Writer not initialized");
		}
	}
	
	public String getAttributesToWrite(List<IDiameterAVP> avpList) {
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
		int size = avpList.size();
		IDiameterAVP avp = null;
		AttributeRelation attributeRelation = null;
		
		boolean bUseDictionaryValue = getUseDictionaryValue();

		for(int index=0;index<size;index++) {
			avp = avpList.get(index);
			String avpValue = "";
			boolean found = false;
		

			if(avp == null) {
				attributeRelation = getAttributeRelationList().get(index);
				avpValue = attributeRelation.getDefaultVale();
			}else {
				avpValue = avp.getStringValue(bUseDictionaryValue);
			}
			
			if(avpValue!=null && avpValue.contains(avPairSeparator)){
				avpValue = avpValue.replaceAll(avPairSeparator, "\\\\"+avPairSeparator);
			}
				

			if(avp != null && avp.getVendorId()==0){
				StringBuilder strBuilder = new StringBuilder("\t");
				strBuilder.append(DiameterDictionary.getInstance().getAttributeName(avp.getAVPCode()));
				strBuilder.append(" ");
				strBuilder.append(avPairSeparator );
				strBuilder.append(" ");
				strBuilder.append(avpValue);
				printWriter.println(strBuilder.toString());
				found = true;								
			} else if(avp != null){
				StringBuilder strBuilder = new StringBuilder("\t");
				strBuilder.append(avp.getVendorId());
				strBuilder.append(":");
				strBuilder.append(DiameterDictionary.getInstance().getAttributeName(avp.getVendorId(),avp.getAVPCode()));
				strBuilder.append(" ");
				strBuilder.append(avPairSeparator );
				strBuilder.append(" ");
				strBuilder.append(avpValue);
				printWriter.println(strBuilder.toString());
				found = true;
			}

			if(!found){
				if(attributeRelation!=null){
					IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(attributeRelation.getAttributeId());
					if(diameterAVP!=null){
						StringBuilder strBuilder = new StringBuilder("\t");
						if(diameterAVP.getVendorId() != 0 ){
							strBuilder.append(diameterAVP.getVendorId());
							strBuilder.append(":");
						}
						strBuilder.append(DiameterDictionary.getInstance().getAttributeName(diameterAVP.getVendorId(), diameterAVP.getAVPCode()));
						strBuilder.append(" ");
						strBuilder.append(avPairSeparator);
						strBuilder.append(" ");
						strBuilder.append(avpValue);					
						printWriter.println(strBuilder.toString());
					}
				}	
			}		
		}
		printWriter.println();
		return strWriter.toString(); 
	}

	@Override
	protected String getNameFromArray(String[] fileAttributes,
			ServiceRequest request) {
		return this.acctDriver.getNameFromArray(fileAttributes, request);
	}

	@Override
	public  int getType() {
		return DriverTypes.NAS_DETAIL_LOCAL_ACCT_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.NAS_DETAIL_LOCAL_ACCT_DRIVER.name();
	}

	@Override
	protected String getDefaultDirName() {
		return detailLocalAcctConfiguration.getDefauleDirName();
	}

	@Override
	protected String getSequenceRange() {
		return detailLocalAcctConfiguration.getSequenceRange();
	}

	@Override
	protected String getCounterFileName() {
		return getServerHome() + File.separator + "system" + File.separator + "cdr_sequence_detail_local_driver";
	}
	@Override
	public void reInit() {
		this.detailLocalAcctConfiguration = (DetailLocalAcctDriverConfiguration)((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(this.detailLocalAcctConfiguration.getDriverInstanceId());
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
		return detailLocalAcctConfiguration.getRollingTypeMap();
	}

}
