package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;
import com.elitecore.aaa.core.data.AttributesRelation;
import com.elitecore.aaa.core.data.StripAttributeRelation;
import com.elitecore.aaa.radius.conf.impl.FileTransferDetail;
import com.elitecore.core.commons.fileio.RollingTypeConstant;

public abstract class ClassicCSVAcctDriverConfigurationImpl implements ClassicCSVAcctDriverConfiguration{
	
	public static final String DRIVER = "driver";
	public static final String DRIVER_INSTANCEID = "instance-id";

	private String driverInstanceId;
	private String driverName ="";
	private String strDelimeterLast;
	private String strDelimeterFirst;
	private String[] fileNameAttribute;
	private String[] folderNameAttribute;
	
	private List<AttributesRelation> attributesRelationList;
	private ArrayList<StripAttributeRelation> stripAttributeRelationList;
	
	private CDRDetails cdrDetails;
	private FileDetails fileDetails;

	private FileRollingParamsDetail fileRollingParamsDetail;

	private FileTransferDetail fileTransferDetail;
	
	public final int DEFAULT_TIMEBOUNDRY = 1440;
	
	public ClassicCSVAcctDriverConfigurationImpl() {
		this.cdrDetails = new CDRDetails();
		this.fileDetails = new FileDetails();
		this.fileRollingParamsDetail = new FileRollingParamsDetail();
		this.fileTransferDetail = new FileTransferDetail();
		this.attributesRelationList=new ArrayList<AttributesRelation>();
		this.stripAttributeRelationList=new ArrayList<StripAttributeRelation>();
	}
	
	@XmlElement(name="cdr-details")
	public CDRDetails getCdrDetails() {
		return cdrDetails;
	}
	public void setCdrDetails(CDRDetails cdrDetails) {
		this.cdrDetails = cdrDetails;
	}
	
	@XmlElement(name="file-details")
	public FileDetails getFileDetails() {
		return fileDetails;
	}
	public void setFileDetails(FileDetails fileDetails) {
		this.fileDetails = fileDetails;
	}
	
	@XmlElement(name="file-transfer-details")
	public FileTransferDetail getFileTransferDetail() {
		return fileTransferDetail;
	}
	public void setFileTransferDetail(FileTransferDetail fileTransferDetail) {
		this.fileTransferDetail = fileTransferDetail;
	}
	
	@XmlElement(name="file-rolling-parameters")
	public FileRollingParamsDetail getFileRollingParamsDetail() {
		return fileRollingParamsDetail;
	}
	public void setFileRollingParamsDetail(
			FileRollingParamsDetail fileRollingParamsDetail) {
		this.fileRollingParamsDetail = fileRollingParamsDetail;
	}
	
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	
	
	public void setDelimeterLast(String strDelimeterLast) {
		this.strDelimeterLast = strDelimeterLast;
	}
	public void setDelimeterFirst(String strDelimeterFirst) {
		this.strDelimeterFirst = strDelimeterFirst;
	}
	
	public void setFileNameAttributes(String[] fileNameAttribute) {
		this.fileNameAttribute = fileNameAttribute;
	}
	public void setFolderNameAttributes(String[] folderNameAttribute) {
		this.folderNameAttribute = folderNameAttribute;
	}
	public void setAttributesRelationList(
			List<AttributesRelation> attributesRelationList) {
		this.attributesRelationList = attributesRelationList;
	}
	public void setStripAttributeRelationList(
			ArrayList<StripAttributeRelation> stripAttributeRelationList) {
		this.stripAttributeRelationList = stripAttributeRelationList;
	}

	@XmlElement(name ="id",type =String.class)
	public String getDriverInstanceId() {
		return this.driverInstanceId;
	}

	@XmlTransient
	public String getAllocatingProtocol() {
		return fileTransferDetail.getAllocatingProtocol();
	}

	@XmlTransient
	public String getIpAddress() {
		return fileTransferDetail.getIpAddress();
	}

	@XmlTransient
	public int getPort() {
		return fileTransferDetail.getPort();
	}

	@XmlTransient
	public String getDestinationLocation() {
		return fileTransferDetail.getDestinationLocation();
	}

	@XmlTransient
	public String getUsrName() {
		return fileTransferDetail.getUsrName();
	}

	@XmlTransient
	public String getPassword() {
		return fileTransferDetail.getPassword();
	}
	
	@Override
	public String getPlainTextPassword() {
		return fileTransferDetail.getPlainTextPassword();
	}

	@XmlTransient
	public String getPostOperation() {
		return fileTransferDetail.getPostOperation();
	}

	@XmlTransient
	public String getArchiveLocations() {
		return fileTransferDetail.getArchiveLocations();
	}

	@XmlTransient
	public int getFailOverTime() {
		return fileTransferDetail.getFailOverTime();
	}

	@XmlTransient
	public String getFileName() {
		return fileDetails.getFileName();
	}

	@XmlTransient
	public String getFileLocation() {
		return fileDetails.getFileLocation();
	}

	@XmlTransient
	public String getDefaultDirName() {
		return fileDetails.getDefaultDirName();
	}

	@XmlTransient
	public boolean getCreateBlankFile() {
		return fileDetails.getIsCreateBlankFile();
	}

	@XmlTransient
	@Override
	public Map<RollingTypeConstant, Integer> getRollingTypeMap() {
		return fileRollingParamsDetail.getRollingTypeMap();
	}
	
	@XmlTransient
	public String getPrefixFileName() {
		return fileDetails.getPrefixFileName();
	}
	@XmlTransient
	public String getFolderName() {
		return fileDetails.getFolderName();
	}
	
	@XmlTransient
	public String getHeader() {
		return cdrDetails.getHeader();
	}

	@XmlTransient
	public String getDelimeter() {
		return cdrDetails.getDelimeter();
	}

	@XmlTransient
	public String getDelimeterFirst() {
		return this.strDelimeterFirst;
	}

	@XmlTransient
	public String getDelimeterLast() {
		return this.strDelimeterLast;
	}
	
	@XmlTransient
	public String getmultiValueDelimeter() {
		return cdrDetails.getmultiValueDelimeter();
	}

	@XmlTransient
	public String getPattern() {
		return fileRollingParamsDetail.getPattern();
	}

	@XmlTransient
	public boolean getGlobalization() {
		return fileRollingParamsDetail.getGlobalization();
	}

	@XmlTransient
	public String getSequenceRange() {
		return fileRollingParamsDetail.getSequenceRange();
	}
	
	@XmlTransient
	public @Nonnull String getCDRTimeStampHeader() {
		return cdrDetails.getCdrTimeStampHeader();
	}

	@XmlTransient
	public String getCDRTimeStampFormat() {
		return cdrDetails.getCDRTimeStampFormat();
	}
	
	@XmlTransient
	public @Nonnull String getCDRTimeStampPosition() {
		return cdrDetails.getCdrTimeStampPosition();
	}

	@XmlElementWrapper(name ="classic-csv-field-mapping")
	@XmlElement(name="mapping")
	public List<AttributesRelation> getAttributesRelationList() {
		return this.attributesRelationList;
	}

	@XmlElementWrapper(name ="classic-csv-strip-pattern-relation-mapping")
	@XmlElement(name ="mapping")
	public List<StripAttributeRelation> getStripAttributeRelationList() {
		return this.stripAttributeRelationList;
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

	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- Acct Classic Csv Driver Configuration -- ");
		out.println();
		out.println("    File Allocator --> ");
		out.println();
		out.println("    ----------------------------------        ");
		out.println("    Port                    = " + getPort());
		out.println("    Allocating Protocol     = " + getAllocatingProtocol());
		out.println("    Archive Locations       = " + getArchiveLocations());
		out.println("    FailOver Time           = " + getFailOverTime());
		out.println("    Destination Location    = " + getDestinationLocation());
		out.println("    Post Operation          = " + getPostOperation());
		out.println("    password                = ****");
		out.println("    UserName                = " + getUsrName());
		out.println("    ----------------------------------        ");

		out.println();
		out.println("    CDR Sequencing Detail --> " );
		out.println("        * CDR_Sequencing_Range    = " + getSequenceRange());
		out.println("        * Pattern                 = " + getPattern());
		out.println("        * GlobaliZation           = " + getGlobalization());
		out.println();
		out.println("    Classic Csv File Location     = " + getFileLocation());
		out.println("    File_Name                     = " + getFileName());
		out.println("    Default_Directory_Name        = " + getDefaultDirName());
		out.println("    Create_Blank_File             = " + getCreateBlankFile());
		out.println("    TimeBased_Rolling_Unit        = " + getFileRollingParamsDetail().getTimeBaseRollingUnit());
		out.println("    SizeBased_Rolling_Unit        = " + getFileRollingParamsDetail().getSizeBaseRollingUnit());
		out.println("    RecordBased_Rolling_Unit      = " + getFileRollingParamsDetail().getRecordBaseRollingUnit());
		out.println("    TimeBoundry_Rolling_Unit      = " + getFileRollingParamsDetail().getTimeBoundryRollingUnit());
		out.println("    Prefix_File_Name              = " + getPrefixFileName());
		out.println("    Prefix_Folder_Name            = " + getFolderName());
		out.println("    Header                        = " + getHeader());
		out.println("    Delimeter                     = " + getDelimeter());
		out.println("    Multi_Val_Delimeter           = " + getmultiValueDelimeter());

		out.println("    ----------------------------------        ");

		out.close();
		return stringBuffer.toString();
	}
	
	@XmlTransient
	@Override
	public String getEnclosingChar() {
		return cdrDetails.getEnclosingChar();
}
	
}
