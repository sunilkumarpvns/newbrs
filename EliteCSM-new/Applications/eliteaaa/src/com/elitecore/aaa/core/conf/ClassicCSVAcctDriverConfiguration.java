package com.elitecore.aaa.core.conf;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.data.AttributesRelation;
import com.elitecore.aaa.core.data.StripAttributeRelation;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.core.commons.fileio.RollingTypeConstant;

public interface ClassicCSVAcctDriverConfiguration extends DriverConfiguration{

		public static final String SUFFIX = "SUFFIX";
		public static final String PREFIX = "PREFIX";
		
		public static final String CDRTIMESTAMP_HEADER_DEFAULT_VALUE = "CDRTimeStamp";
		public static final String CDRTIMESTAMP_POSITION_DEFAULT_VALUE = SUFFIX;
	
		public String getAllocatingProtocol();

		public String getIpAddress();

		public int getPort();

		public String getDestinationLocation();

		public String getUsrName();

		public String getPassword();
		
		public String getPlainTextPassword();

		public String getPostOperation();

		public String getArchiveLocations();

		public int getFailOverTime();

		public String getFileName();

		public String getFileLocation();

		public String getDefaultDirName();

		public String getPrefixFileName();

		public String getFolderName();

		public String getPattern();

		public boolean getGlobalization();
		
		public String getHeader();
		
		public String getDelimeter();
		
		public String getDelimeterFirst();
		
		public String getDelimeterLast();
		
		public String getmultiValueDelimeter();

		public boolean getCreateBlankFile();

		public String[] getFileNameAttributes();

		public String[] getFolderNameAttributes();
			
		public @Nonnull String getCDRTimeStampHeader();
		
		public String getCDRTimeStampFormat();
		
		public @Nonnull String getCDRTimeStampPosition();
		
		public List<AttributesRelation> getAttributesRelationList();
		
		public List<StripAttributeRelation> getStripAttributeRelationList();

		public String getSequenceRange();

		public String getEnclosingChar();
		
		public Map<RollingTypeConstant, Integer> getRollingTypeMap();
}
