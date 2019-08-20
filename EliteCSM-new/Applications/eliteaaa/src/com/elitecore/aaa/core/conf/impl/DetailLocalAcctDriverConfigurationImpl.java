package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.DetailLocalAcctDriverConfiguration;
import com.elitecore.aaa.core.data.AttributeRelation;
import com.elitecore.core.commons.fileio.RollingTypeConstant;


public abstract class DetailLocalAcctDriverConfigurationImpl implements DetailLocalAcctDriverConfiguration{

	private String driverInstanceId;

	private String driverName="";
	
	private DetailLocalDriverDetails detailLocalDriverDetails;
	private DetailLocalFileDetails detailLocalFileDetails;
	private FileRollingParamsDetail fileRollingParamsDetail;
	private DetailLocalFileTransferDetail detailLocalFileTransferDetail;
	private ArrayList<AttributeRelation> attributeRelationList;
	
	private String[] fileNameAttribute;
	private String[] folderNameAttribute;

	public final int DEFAULT_TIMEBOUNDRY = 1440;
	
	public DetailLocalAcctDriverConfigurationImpl() {
		this.detailLocalDriverDetails = new DetailLocalDriverDetails();
		this.detailLocalFileDetails = new DetailLocalFileDetails();
		this.fileRollingParamsDetail = new FileRollingParamsDetail();
		this.detailLocalFileTransferDetail = new DetailLocalFileTransferDetail();
		this.attributeRelationList = new ArrayList<AttributeRelation>();
	}

	@XmlElement(name ="id",type =String.class)
	public String getDriverInstanceId() {
		return this.driverInstanceId;
	}

	@XmlTransient
	public String getAllocatingProtocol() {
		return this.detailLocalFileTransferDetail.getAllocatingProtocol();
	}

	@XmlTransient
	public String getIpAddress() {
		return this.detailLocalFileTransferDetail.getIpAddress();
	}

	@XmlTransient
	public int getPort() {
		return this.detailLocalFileTransferDetail.getPort();
	}

	@XmlTransient
	public String getDestinationLocation() {
		return this.detailLocalFileTransferDetail.getDestinationLocation();
	}

	@XmlTransient
	public String getUsrName() {
		return this.detailLocalFileTransferDetail.getUsrName();
	}

	@XmlTransient
	public String getPassword() {
		return this.detailLocalFileTransferDetail.getPassword();
	}
	
	public String getPlainTextPassword(){
		return this.detailLocalFileTransferDetail.getPlainTextPassword();
	}

	@XmlTransient
	public String getPostOperation() {
		return this.detailLocalFileTransferDetail.getPostOperation();
	}

	@XmlTransient
	public String getArchiveLocations() {
		return this.detailLocalFileTransferDetail.getArchiveLocations();
	}

	@XmlTransient
	public int getFailOverTime() {
		return this.detailLocalFileTransferDetail.getFailOverTime();
	}

	@XmlTransient
	public String getFileName() {
		return this.detailLocalFileDetails.getFileName();
	}

	@XmlTransient
	public String getFileLocation() {
		return this.detailLocalFileDetails.getLocation();
	}

	@XmlTransient
	public String getDefauleDirName() {
		return this.detailLocalFileDetails.getDefaultDirName();
	}

	@XmlTransient
	public String getEventDateFormat() {
		return this.detailLocalDriverDetails.getEventDateFormat();
	}

	@XmlTransient
	public String getPrefixFileName() {
		return this.detailLocalFileDetails.getPrefixFileName();
	}

	@XmlTransient
	public String getFolderName() {
		return this.detailLocalFileDetails.getFolderName();
	}

	@XmlTransient
	public String getWriteAttributes() {
		return this.detailLocalDriverDetails.getWriteAttributes();
	}

	@XmlTransient
	public boolean getUseDictionaryValue() {
		return this.detailLocalDriverDetails.getIsUseDictonaryVal();
	}

	@XmlTransient
	public String getAvPairSeparator() {
		return this.detailLocalDriverDetails.getAvPairSeparator();
	}

	@XmlTransient
	public boolean getCreateBlankFile() {
		return this.detailLocalFileDetails.getIsCreateBlankFile();
	}

	@XmlTransient
	public String getSequenceRange() {
		return this.fileRollingParamsDetail.getSequenceRange();
	}
	
	@XmlTransient
	public String getPattern() {
		return this.fileRollingParamsDetail.getPattern();
	}

	@XmlTransient
	public boolean getGlobalization() {
		return this.fileRollingParamsDetail.getGlobalization();
	}

	@XmlElementWrapper(name ="driver-attribute-relation-data")
	@XmlElement(name="mapping")
	public List<AttributeRelation> getAttributeRelationList() {
		return this.attributeRelationList;
	}

	@XmlTransient
	public String[] getFileNameAttributes() {
		return this.fileNameAttribute;
	}

	@XmlTransient
	public String[] getFolderNameAttributes() {
		return this.folderNameAttribute;
	}	

	
	@Override
	@XmlElement(name="driver-name",type=String.class)
	public String getDriverName() {
		return driverName;
	}
	
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	@XmlElement(name="detail-local-driver-details")
	public DetailLocalDriverDetails getDetailLocalDriverDetails() {
		return detailLocalDriverDetails;
	}

	public void setDetailLocalDriverDetails(
			DetailLocalDriverDetails detailLocalDriverDetails) {
		this.detailLocalDriverDetails = detailLocalDriverDetails;
	}
	
	@XmlElement(name="file-details")
	public DetailLocalFileDetails getDetailLocalFileDetails() {
		return detailLocalFileDetails;
	}

	public void setDetailLocalFileDetails(
			DetailLocalFileDetails detailLocalFileDetails) {
		this.detailLocalFileDetails = detailLocalFileDetails;
	}
	
	@XmlElement(name="file-rolling-parameters")
	public FileRollingParamsDetail getFileRollingParamsDetail() {
		return fileRollingParamsDetail;
	}

	public void setFileRollingParamsDetail(
			FileRollingParamsDetail fileRollingParamsDetail) {
		this.fileRollingParamsDetail = fileRollingParamsDetail;
	}
	
	@XmlElement(name="file-allocator-details")
	public DetailLocalFileTransferDetail getDetailLocalFileTransferDetail() {
		return detailLocalFileTransferDetail;
	}

	public void setDetailLocalFileTransferDetail(
			DetailLocalFileTransferDetail detailLocalFileTransferDetail) {
		this.detailLocalFileTransferDetail = detailLocalFileTransferDetail;
	}
	
	public void setAttributeRelationList(
			ArrayList<AttributeRelation> attributeRelationList) {
		this.attributeRelationList = attributeRelationList;
	}

	
	public void setFileNameAttribute(String[] fileNameAttribute) {
		this.fileNameAttribute = fileNameAttribute;
	}
	public void setFolderNameAttribute(String[] folderNameAttribute) {
		this.folderNameAttribute = folderNameAttribute;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- Acct Detail Local Driver Configuration -- ");
		out.println();
		out.println("    File Allocator -> ");
		out.println();
		out.println("--------------------------------");
		out.println("    Port = " + getPort());
		out.println("    Allocating Protocol = " + getAllocatingProtocol());
		out.println("    Archive Locations = " + getArchiveLocations());
		out.println("    FailOver Time = " + getFailOverTime());
		out.println("    Destination Location = " + getDestinationLocation());
		out.println("    Post Operation = " + getPostOperation());
		out.println("    password = ****");
		out.println("    User = " + getUsrName());
		out.println("--------------------------------");

		out.println();
		out.println("    Detail Local File Location = " + getFileLocation());
		out.println("    CDR Sequencing Detail -> " );
		out.println("      CDR_Sequencing_Range = " + getSequenceRange());
		out.println("      Pattern = " + getPattern());
		out.println("      GlobaliZation = " + getGlobalization());
		out.println();
		out.println("    Prefix_File_Name = " + getPrefixFileName());
		out.println("    Prefix_Folder_Name = " + getFolderName());
		out.println("    Use_Dictionary_Value = " + getUseDictionaryValue());
		out.println("    AV_Pair_Seperator = " + getAvPairSeparator());
		out.println("    File_Name = " + getFileName());
		out.println("    Default_Directory_Name = " + getDefauleDirName());
		out.println("    Write_Attributes = " + getWriteAttributes());
		out.println("    Pattern = " + getPattern());
		out.println("    TimeBased_Rolling_Unit = " + getFileRollingParamsDetail().getTimeBaseRollingUnit());
		out.println("    SizeBased_Rolling_Unit = " + getFileRollingParamsDetail().getSizeBaseRollingUnit());
		out.println("    RecordBased_Rolling_Unit = " + getFileRollingParamsDetail().getRecordBaseRollingUnit());
		out.println("    TimeBoundry_Rolling_Unit = " + getFileRollingParamsDetail().getTimeBoundryRollingUnit());

		out.println("    ");

		out.close();
		return stringBuffer.toString();
	}

	@XmlTransient
	@Override
	public Map<RollingTypeConstant, Integer> getRollingTypeMap() {
		return fileRollingParamsDetail.getRollingTypeMap();
	}
}