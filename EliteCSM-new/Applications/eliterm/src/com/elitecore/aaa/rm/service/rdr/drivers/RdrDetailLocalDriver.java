package com.elitecore.aaa.rm.service.rdr.drivers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.aaa.core.data.AttributeRelation;
import com.elitecore.aaa.core.drivers.DetailLocalAcctdriver;
import com.elitecore.aaa.rm.conf.RdrDetailLocalConfiguration;
import com.elitecore.aaa.rm.service.rdr.service.RDRServiceContext;
import com.elitecore.aaa.rm.service.rdr.service.RDRServiceRequest;
import com.elitecore.aaa.rm.service.rdr.tlv.BaseRDRTLV;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.servicex.ServiceRequest;

public class RdrDetailLocalDriver extends DetailLocalAcctdriver {
	
	public static String MODULE = "RDR-DETAIL-LOCAL-ACCT-Drvr";
	private RdrDetailLocalConfiguration rdrDetailLocalConfiguration = null;
	//private RadAcctDriver acctDriver;
	
	private int iDriverInstanceId;
	public RdrDetailLocalDriver(RDRServiceContext serviceContext,int driverInstanceId) {
		this(serviceContext, (RdrDetailLocalConfiguration)serviceContext.getRdrDetailLocalConfiguration(), driverInstanceId);
	}

	public RdrDetailLocalDriver(RDRServiceContext  serviceContext, RdrDetailLocalConfiguration driverConfiguration, int driverInstanceId) {
		super(serviceContext.getServerContext());
		this.iDriverInstanceId = driverInstanceId;
		this.rdrDetailLocalConfiguration = driverConfiguration;
		currentLocation = driverConfiguration.getFileLocation();
	}
	
	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		super.initInternal();
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}	
	
	@Override
	public String getName() {
		return rdrDetailLocalConfiguration.getDriverName();
	}

	@Override
	protected String getPattern() {
		return rdrDetailLocalConfiguration.getPattern();
	}

	@Override
	protected boolean getGlobalization() {
		return rdrDetailLocalConfiguration.getGlobalization();
	}

	@Override
	protected String[] getFileNameAttributes() {
		// TODO Auto-generated method stub
		String[] array={"A","B"};
		return array;
	}

	@Override
	protected String[] getFolderNameAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getCounterFileName() {
		// TODO Auto-generated method stub
		return "counterFile";
	}

	@Override
	protected String getAllocatingProtocol() {
		// TODO Auto-generated method stub
		return rdrDetailLocalConfiguration.getAllocatingProtocol();
	}

	@Override
	protected String getUserName() {
		// TODO Auto-generated method stub
		return rdrDetailLocalConfiguration.getUsrName();
	}

	@Override
	protected String getPassword() {
		// TODO Auto-generated method stub
		return rdrDetailLocalConfiguration.getPassword();
	}

	@Override
	protected int getPort() {
		// TODO Auto-generated method stub
		return rdrDetailLocalConfiguration.getPort();
	}

	@Override
	protected String getDestinationLocation() {
		// TODO Auto-generated method stub
		return rdrDetailLocalConfiguration.getRemoteLocation();
	}

	@Override
	protected String getIpAddress() {
		// TODO Auto-generated method stub
		return rdrDetailLocalConfiguration.getIpAddress();
	}

	@Override
	protected int getFailOverTime() {
		// TODO Auto-generated method stub
		return rdrDetailLocalConfiguration.getFailOverTime();
	}

	@Override
	protected String getPostOperation() {
		// TODO Auto-generated method stub
		return rdrDetailLocalConfiguration.getPostOperation();
	}

	@Override
	protected String getArchiveLocations() {
		// TODO Auto-generated method stub
		return rdrDetailLocalConfiguration.getArchieveLocation();
	}

	@Override
	protected String getFileName() {
		// TODO Auto-generated method stub
		return rdrDetailLocalConfiguration.getFileName();
	}

	@Override
	protected String getWriteAttributes() {
		return rdrDetailLocalConfiguration.getWriteAttributes();
	}

	@Override
	protected boolean getUseDictionaryValue() {
		return rdrDetailLocalConfiguration.getUseDictionaryValue();
	}

	@Override
	protected String getAvPairSeparator() {
		return rdrDetailLocalConfiguration.getAvPairSeparator();
	}

	@Override
	protected String getEventDateFormat() {
		return rdrDetailLocalConfiguration.getEventDateFormat();
	}

	@Override
	protected String getDefaultDirName() {
		return rdrDetailLocalConfiguration.getDefauleDirName();
	}

	@Override
	protected boolean getCreateBlankFile() {
		return true;
	}

	@Override
	protected List<AttributeRelation> getAttributeRelationList() {
		return null;
	}

	@Override
	protected String getNameFromArray(String[] fileAttributes,
			ServiceRequest request) {
		return "A";
	}

	@Override
	protected void writeAttributes(ServiceRequest request,
			EliteFileWriter fileWriter, String destinationPath) {
		if(fileWriter!=null){			
				
				fileWriter.appendRecord(getAttributesToWrite(((RDRServiceRequest)request).getFields()));
				fileWriter.flush();
				
				LogManager.getLogger().trace(MODULE, "All Atttributes dumped using DetailLocalAcct Driver");
				LogManager.getLogger().trace(MODULE, "Location For Dumped File:" +destinationPath);
			
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Attributes not dumped using DetailLocalAcct Driver ,Reason : File Writer not initialized");
		}
	}

	private String getAttributesToWrite(HashMap<Integer,BaseRDRTLV> rdrTLVs) {
		// TODO Auto-generated method stub
		StringWriter stringBuffer=new StringWriter();
		PrintWriter out=new PrintWriter(stringBuffer);
		Set set = rdrTLVs.entrySet();
		  java.util.Iterator i = set.iterator();
		  out.println();
		  out.println("\t\t\t---RDR Fields---");
		  
		  while (i.hasNext()) {
			  Map.Entry me = (Map.Entry) i.next();
			  out.print("\t"+me.getValue()+" ");
		  }
		  return stringBuffer.toString();
	}


	@Override
	public int getType() {
		return 0;
	}
	
	@Override
	public String getTypeName() {
		//returning the dummy value that is used in the configuration
		return "RDR";
	}

	@Override
	protected String getSequenceRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isAttributeID(String str) {
		// TODO This method needs to be implemented if support of attribute ID and Timestamp is required in RDR
		return false;
	}

	@Override
	protected String getAttributeValue(String attrID, ServiceRequest request) {
		// TODO This method needs to be implemented if support of attribute ID and Timestamp is required in RDR
		return null;
	}

	@Override
	protected Map<RollingTypeConstant, Integer> getRollingTypeMap() {
		// TODO Auto-generated method stub
		return null;
	}
}
