package com.elitecore.aaa.rm.service.rdr.drivers.confg.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AttributeRelation;
import com.elitecore.aaa.rm.conf.RdrDetailLocalConfiguration;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.serverx.ServerContext;

@ReadOrder(order = { "rdrDetailLocalConfigurable" })
public class RdrDetailLocalConfigurationImpl extends CompositeConfigurable implements	RdrDetailLocalConfiguration {
	
	private static final String MODULE = "RDR-DETAIL-LOCAL-CONF-IMPL";	
	
	@Configuration private RdrDetailLocalConfigurable rdrDetailLocalConfigurable;
	
	public static final String DRIVER = "driver";
	public static final String DRIVER_INSTANCEID = "instance-id";
	
	public static final String ROOT_NODE_NAME = "detail-local-drivers";
	
	public static final String DRIVER_XMLFILENAME = "rdr-acct-driver-conf.xml";
	public static final String DRIVER_XMLFILENAME_ROOT_NODE = "driver-configs";
	
	private String driverInstanceId;
	private String driverName="";
	
	private ArrayList<AttributeRelation> attributeRelationList;
	
	
	@XmlElementWrapper(name ="driver-attribute-relation-data")
	@XmlElement(name="mapping")
	public ArrayList<AttributeRelation> getAttributeRelationList() {
		return attributeRelationList;
	}
	public void setAttributeRelationList(ArrayList<AttributeRelation> attributeRelationList) {
		this.attributeRelationList = attributeRelationList;
	}

	//Detail Local Driver Details
	private String eventDateFormat="EEE dd MMM,yyyy,hh:mm:ss aaa";
	private String writeAttributes="All";
	private boolean useDictonaryVal=true;
	private String avPairSeparator="=";

	//File Details
	private String fileName="detail.local";
	private String location="data/detail-local-files";
	private String defaultDirName="no_nas_ip_address";
	private String prefixFileName="0:4";
	private String folderName="0:4";
	
	//File Rolling Parameters
	private int fileRollingType=EliteFileWriter.TIME_BASED_ROLLING;
	private int rollingUnit = EliteFileWriter.TIME_BASED_ROLLING_EVERY_DAY;
	private String range="Default";
	private String pattern;
	private boolean globalization;
	
	//File Allocator Details
	private String allocatingProtocol="Local";
	private String ipAddress;
	private int port;
	private String remoteLocation;
	private String usrName;
	private String password;
	private String postOperation="Delete";
	private String archieveLoc="data/detail-local-files/archive";
	private int failOverTime=3;

	
	public RdrDetailLocalConfigurationImpl() {
		this.attributeRelationList = new ArrayList<AttributeRelation>();
	}
	public RdrDetailLocalConfigurationImpl(ServerContext serverContext) {
		this.attributeRelationList = new ArrayList<AttributeRelation>();
	}	

	@Override
	@XmlElement(name="id",type = String.class)
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}

	@Override
	@XmlTransient
	public DriverTypes getDriverType() {		
		return null;
	}
	

	@Override
	@XmlElement(name ="name",type = String.class)
	public String getDriverName() {
		return driverName;
	}
	
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	@Override
	public void readConfiguration() throws LoadConfigurationException {}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println("Driver Name "+getDriverName());
		out.println(" -- RDR Acct Detail Local Driver Configuration -- ");
		out.println();
		out.println("    File Allocator -> ");
		out.println();
		out.println("--------------------------------");
		out.println("    Port = " + getPort());
		out.println("    Allocating Protocol = " + getAllocatingProtocol());
		out.println("    Archieve Location = " + getArchieveLocation());
		out.println("    FailOver Time = " + getFailOverTime());
		out.println("    Address = " + getRemoteLocation());
		out.println("    Post Operation = " + getPostOperation());
		out.println("    password = ****");
		out.println("    User = " + getUsrName());
		out.println("--------------------------------");

		out.println();
		out.println("    Detail Local File Location = " + getFileLocation());
		out.println("    CDR Sequencing Detail -> " );
		out.println("      CDR_Sequencing_Range = " + getRange());
		out.println("      Pattern = " + getPattern());
		out.println("      GlobaliZation = " + getGlobalization());
		out.println();
		out.println("    Prefix_File_Name = " + getPrefixFileName());
		out.println("    Prefix_Folder_Name = " + getFolderName());
		out.println("    Use_Dictionary_Value = " + getUseDictionaryValue());
		out.println("    AV_Pair_Seperator = " + getAvPairSeparator());
		out.println("    File_Name = " + getFileName());
		out.println("    Rolling_Unit = " + getRollingUnit());
		out.println("    Default_Directory_Name = " + getDefauleDirName());
		out.println("    Rollling_Type = " + getFileRollingType());
		out.println("    Write_Attributes = " + getWriteAttributes());
		out.println("    Pattern = " + getPattern());
		out.println("    ");
		out.close();
		return stringBuffer.toString();
	}

	@Override
	@XmlElement(name="event-dataformate",type = String.class)
	public String getEventDateFormat() {
		return this.eventDateFormat;
	}
	public void setEventDateFormat(String eventDateFormat){
		this.eventDateFormat = eventDateFormat;
	}
	
	@Override
	@XmlElement(name ="write-attributes",type = String.class)
	public String getWriteAttributes() {
		return this.writeAttributes;
	}
	public void setWriteAttributes(String writeAttributes) {
		this.writeAttributes = writeAttributes;
	}
	
	@Override
	@XmlElement(name="use-dictionary-value",type = boolean.class)
	public boolean getUseDictionaryValue() {
		return this.useDictonaryVal;
	}
	public void setUseDictionaryValue(boolean useDictonaryVal) {
		this.useDictonaryVal = useDictonaryVal;
	}
	
	@Override
	@XmlElement(name="avppair-separator",type = String.class)
	public String getAvPairSeparator() {
		return this.avPairSeparator;
	}
	public void setAvPairSeparator(String avPairSeparator) {
		this.avPairSeparator = avPairSeparator;
	}
	
	@Override
	@XmlElement(name="file-name",type =String.class)
	public String getFileName() {
		return this.fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	@XmlElement(name="file-location",type = String.class)
	public String getFileLocation() {
		return this.location;
	}
	public void setFileLocation(String location) {
		this.location = location;
	}
	@Override
	@XmlElement(name ="default-dir-name",type = String.class)
	public String getDefauleDirName() {
		return this.defaultDirName;
	}
	public void setDefauleDirName(String defaultDirName) {
		this.defaultDirName = defaultDirName;
	}
	
	@Override
	@XmlElement(name = "prefix-file-name",type = String.class)
	public String getPrefixFileName() {
		return this.prefixFileName;
	}
	public void setPrefixFileName(String prefixFileName) {
		this.prefixFileName = prefixFileName;
	}
	
	@Override
	@XmlElement(name="folder-name",type=String.class)
	public String getFolderName() {
		return this.folderName;
	}
	public void setFolderName(String folderName) {
	this.folderName = folderName;
	}
	
	@Override
	@XmlElement(name="rolling-type",type=int.class)
	public int getFileRollingType() {
		return this.fileRollingType;
	}
	public void setFileRollingType(int fileRollingType) {
		this.fileRollingType = fileRollingType;
	}
	
	@Override
	@XmlElement(name="rolling-unit",type=int.class)
	public int getRollingUnit() {
		return this.rollingUnit;
	}
	public void setRollingUnit(int rollingUnit) {
		this.rollingUnit = rollingUnit;
	}
	
	@Override
	@XmlElement(name="range",type=String.class)
	public String getRange() {
		return this.range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	
	@Override
	@XmlElement(name="pattern",type=String.class)
	public String getPattern() {
		return this.pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	@Override
	@XmlElement(name="globalization",type=boolean.class)
	public boolean getGlobalization() {
		return this.globalization;
	}
	public void setGlobalization(boolean globalization) {
		this.globalization = globalization;
	}
	
	@Override
	@XmlElement(name="allocating-protocol",type=String.class)
	public String getAllocatingProtocol() {
		return this.allocatingProtocol;
	}
	
	public void setAllocatingProtocol(String allocatingProtocol) {
		this.allocatingProtocol = allocatingProtocol;
	}
	
	@Override
	@XmlElement(name="ip-address",type = String.class)
	public String getIpAddress() {
		return this.ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	@Override
	@XmlElement(name="port",type=int.class)
	public int getPort() {
		return this.port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@Override
	@XmlElement(name="remote-location",type=String.class)
	public String getRemoteLocation() {
		return this.remoteLocation;
	}
	public void setRemoteLocation(String remoteLocation) {
		this.remoteLocation = remoteLocation;
	}
	
	@Override
	@XmlElement(name="user-name",type=String.class)
	public String getUsrName() {
		return this.usrName;
	}
	public void setUsrName(String usrName) {
		this.usrName = usrName;
	}
	@Override
	@XmlElement(name="password",type=String.class)
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	@XmlElement(name="post-operation",type=String.class)
	public String getPostOperation() {
		return this.postOperation;
	}
	public void setPostOperation(String postOperation) {
		this.postOperation = postOperation;
	}
	@Override
	@XmlElement(name="archive-location",type=String.class)
	public String getArchieveLocation() {
		return this.archieveLoc;
	}
	public void setArchieveLocation(String archieveLoc) {
		this.archieveLoc = archieveLoc;
	}
	@Override
	@XmlElement(name="failvover-time",type=int.class)
	public int getFailOverTime() {
		return this.failOverTime;
	}
	public void setFailOverTime(int failOverTime) {
		this.failOverTime = failOverTime;
	}
	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@PostRead
	public void postReadProcessing(){
		
	}	
	@PostWrite
	public void postWriteProcessing() {

	}
	
	@PostReload
	public void postReloadProcessing() {

	}
	@DBReload
	public void reloadConfiguration() throws LoadConfigurationException {
	}
		
}
