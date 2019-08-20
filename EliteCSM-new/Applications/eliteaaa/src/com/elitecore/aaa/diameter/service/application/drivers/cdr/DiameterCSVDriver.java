package com.elitecore.aaa.diameter.service.application.drivers.cdr;

import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AttributesRelation;
import com.elitecore.aaa.core.data.StripAttributeRelation;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.commons.fileio.base.BaseFileStream;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DiameterCSVDriver extends BaseCSVDriver<DiameterPacket> {

	private static final String MODULE = "DIA-CSV-DRIVER";
	private static final String CDRTIMESTAMP = "CDRTIMESTAMP";

	private ClassicCSVAcctDriverConfigurationImpl driverConfiguration;
	private SimpleDateFormat cdrTimestampFormat;
	private long rollingUnit;
	private int rollingType;

	public DiameterCSVDriver(String serverHome,
			ClassicCSVAcctDriverConfigurationImpl driverConfiguration,
			TaskScheduler taskSchedular) {
		super(serverHome, taskSchedular, null);
		this.driverConfiguration = driverConfiguration;
	}

	@Override
	public void init() throws DriverInitializationFailedException {
		
		Map<RollingTypeConstant, Integer> rollingTypeMap = driverConfiguration.getRollingTypeMap();
		
		if(rollingTypeMap == null || rollingTypeMap.isEmpty() ) {
			throw new DriverInitializationFailedException("Rolling Unit not available");
		}
		for(Entry<RollingTypeConstant,Integer> entry : rollingTypeMap.entrySet()) {
			switch (entry.getKey()) {
		
			case SIZE_BASED_ROLLING:
				rollingType = BaseFileStream.SIZE_BASED_ROLLING;
				break;
			case TIME_BASED_ROLLING:
			case TIME_BOUNDRY_ROLLING:
				rollingType = BaseFileStream.TIME_BASED_ROLLING;
				break;
			case RECORD_BASED_ROLLING:
				rollingType = BaseFileStream.RECORD_BASED_ROLLING;
				break;
			}
			rollingUnit = entry.getValue();
			break;
		}
		super.init();

		if (Collectionz.isNullOrEmpty(driverConfiguration.getAttributesRelationList())) {
			throw new DriverInitializationFailedException("Missing Field Mappings");
		}
		try {
			if(Strings.isNullOrBlank(driverConfiguration.getCDRTimeStampFormat())){
				cdrTimestampFormat = new SimpleDateFormat();
			}else{
				cdrTimestampFormat = new SimpleDateFormat(driverConfiguration.getCDRTimeStampFormat());
			}
		} catch (IllegalArgumentException e) {
			LogManager.ignoreTrace(e);
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE,	"Invalid CDRTimeStampFormat: "
								+ driverConfiguration.getCDRTimeStampFormat()
								+ " for driver :" + getName() +". So Applying Default Time-Stamp format");
			}
			cdrTimestampFormat = new SimpleDateFormat();
		}
		registerCSVLineBuilder(new DiameterCSVLineBuilder());
	}

	@Override
	public String getDriverInstanceUUID() {
		return driverConfiguration.getDriverInstanceId();
	}

	@Override
	public int getDriverType() {
		return driverConfiguration.getDriverType().value;
	}

	@Override
	public String getDriverName() {
		return driverConfiguration.getDriverName();
	}

	@Override
	public String getName() {
		return getDriverName();
	}

	@Override
	public String getTypeName() {
		return DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER.name();
	}

	@Override
	public String getCSVHeaderLine() {
		StringBuilder csvHeaderLine = new StringBuilder();
		String enclosingChar = getEnclosingCharacter();

		for (AttributesRelation mapping : getAttributesRelations()) {

			if (enclosingChar != null) {
				csvHeaderLine.append(enclosingChar);
				csvHeaderLine.append(mapping.getHeader());
				csvHeaderLine.append(enclosingChar).append(getDelimiter());
			} else {
				csvHeaderLine.append(mapping.getHeader()).append(
						getDelimiter());
			}
		}
		if (getCDRTimeStampFormat() != null) {
			if (enclosingChar != null) {
				csvHeaderLine.append(enclosingChar);
				csvHeaderLine.append(CDRTIMESTAMP);
				csvHeaderLine.append(enclosingChar);
			} else {
				csvHeaderLine.append(CDRTIMESTAMP);
			}
		}
		return  csvHeaderLine.toString();
	}

	@Override
	public String getCounterFileName() {
		return serverHome + File.separator + "system" + File.separator
				+ "cdr_sequence_classic_csv_driver";
	}

	@Override
	public SimpleDateFormat getCDRTimeStampFormat() {
		return cdrTimestampFormat;
	}

	@Override
	public int getPort() {
		return driverConfiguration.getPort();
	}

	@Override
	public int getFailOverTime() {
		return driverConfiguration.getFailOverTime();
	}

	@Override
	public boolean isHeader() {
		return Boolean.valueOf(driverConfiguration.getHeader());
	}

	@Override
	public boolean isSequenceGlobalization() {
		return driverConfiguration.getGlobalization();
	}

	@Override
	public String getDelimiter() {
		return driverConfiguration.getDelimeter();
	}

	@Override
	public String getMultipleDelimiter() {
		return driverConfiguration.getmultiValueDelimeter();
	}

	@Override
	public String getFileName() {
		return driverConfiguration.getFileName();
	}

	@Override
	public String getFileLocation() {
		return driverConfiguration.getFileLocation();
	}

	@Override
	public String getPrefixFileName() {
		return driverConfiguration.getPrefixFileName();
	}

	@Override
	public String getDefaultDirectoryName() {
		return driverConfiguration.getDefaultDirName();
	}

	@Override
	public String getDirectoryName() {
		return driverConfiguration.getFolderName();
	}

	@Override
	public String getSequenceRange() {
		return driverConfiguration.getSequenceRange();
	}

	@Override
	public String getSequencePosition() {
		return driverConfiguration.getPattern();
	}

	@Override
	public String getAllocatingProtocol() {
		return driverConfiguration.getAllocatingProtocol();
	}

	@Override
	public String getIPAddress() {
		return driverConfiguration.getIpAddress();
	}

	@Override
	public String getRemoteLocation() {
		return driverConfiguration.getDestinationLocation();
	}

	@Override
	public String getUserName() {
		return driverConfiguration.getUsrName();
	}

	@Override
	public String getPassword() {
		return driverConfiguration.getPassword();
	}

	@Override
	public String decrypt(String encriptedPassword, int encriptionType) {
		try {
			return PasswordEncryption.getInstance().decrypt(encriptedPassword, encriptionType);
		} catch (NoSuchEncryptionException | DecryptionNotSupportedException | DecryptionFailedException e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		return null;
	}

	@Override
	public String getPostOperation() {
		return driverConfiguration.getPostOperation();
	}

	@Override
	public String getArchiveLocation() {
		return driverConfiguration.getArchiveLocations();
	}

	/**
	 * refer {@link #getStripAttributeRelation()} instead
	 */
	@Override
	public CSVStripMapping getStripMapping(String key) {
		throw new UnsupportedOperationException("CSV Strip Mapping is not supported with CSV Driver");
	}

	/**
	 * refer {@link #getAttributesRelations()} instead
	 */
	@Override
	public List<CSVFieldMapping> getCSVFieldMappings() {
		throw new UnsupportedOperationException("CSV Field Mappings are not supported with the driver");
	}

	@Override
	public Map<RollingTypeConstant, Integer> getRollingTypeMap() {
		return driverConfiguration.getRollingTypeMap();
	}

	public List<AttributesRelation> getAttributesRelations() {
		return driverConfiguration.getAttributesRelationList();
	}

	@Override
	public String getParameterValue(DiameterPacket request, String key) {

		List<IDiameterAVP> avpList = request.getAVPList(key, true);
		if (CollectionUtils.isEmpty(avpList) == false) {

			StringBuilder strBuilder = new StringBuilder();
			
			formatMultiDeliminatorValue(strBuilder,
					strip(key, avpList.get(0).getStringValue()));
			for (int i = 1; i < avpList.size(); i++) {

				strBuilder.append(getMultipleDelimiter());
				formatMultiDeliminatorValue(strBuilder,
						strip(key, avpList.get(i).getStringValue()));
			}
			return strBuilder.toString();
		}
		return null;
	}

	private String strip(String strAttributeId, String strValueToBeStripped){
		String strippedValue = strValueToBeStripped;

		if(strAttributeId == null || strValueToBeStripped == null || getStripAttributeRelation() == null ) {
			return strippedValue;
		}
		for(int i=0 ; i < getStripAttributeRelation().size() ; i++) {
			StripAttributeRelation stripAttributeRelation = getStripAttributeRelation().get(i);
			String pattern = null;
			String seperator;
			if (stripAttributeRelation == null) {
				continue;
			}
			if (strAttributeId.equalsIgnoreCase(stripAttributeRelation.getAttributeId()) == false) {
				continue;
			}
			pattern = stripAttributeRelation.getPattern();
			seperator = stripAttributeRelation.getSeparator();

			int indexOfSeprator = strValueToBeStripped.indexOf(seperator);
			if(indexOfSeprator != -1) {
				if(pattern.equalsIgnoreCase(SUFFIX)) {
					strippedValue =  strValueToBeStripped.substring(0, indexOfSeprator).trim();
				} else if(pattern.equalsIgnoreCase(PREFIX)) {
					strippedValue = strValueToBeStripped.substring(indexOfSeprator+1, strValueToBeStripped.length()).trim();
				}
			}
			return strippedValue;
		}
		return strippedValue;
	}
	
	private List<StripAttributeRelation> getStripAttributeRelation(){
		return driverConfiguration.getStripAttributeRelationList();
	}
	
	private void formatMultiDeliminatorValue(StringBuilder stringBuilder,
			String value) {

		if(value == null) {
			return;
		}

		if (value.contains(getMultipleDelimiter()))
			value = value.replaceAll(getMultipleDelimiter(), "\\\\"
					+ getMultipleDelimiter());
		stringBuilder.append(value);
	}

	private class DiameterCSVLineBuilder implements CSVLineBuilder<DiameterPacket> {

		@Override
		public List<String> getCSVRecords(DiameterPacket request) {
			List<String> records = new ArrayList<String>();
			StringBuilder record = new StringBuilder();

			List<AttributesRelation> fieldMappings = driverConfiguration
					.getAttributesRelationList();
			String enclosingChar = getEnclosingCharacter();

			for (int i = 0; i < fieldMappings.size(); i++) {
				AttributesRelation mapping = fieldMappings.get(i);

				String value = getParameterValue(request,
						mapping.getAttributeId());
				if (Strings.isNullOrBlank(value)) {
					value = mapping.getDefaultValue();
				}
				if (Strings.isNullOrBlank(value) == false
						&& Strings.isNullOrBlank(enclosingChar) == false) {
					if (value.contains(enclosingChar))
						value = value.replaceAll(enclosingChar, "\\\\"
								+ enclosingChar);
					value = enclosingChar + value + enclosingChar;
				}
				appendValue(record, value);
			}
			// Char for Timestamp
			if (Strings.isNullOrBlank(enclosingChar) == false) {
				record.append(enclosingChar);
				appendTimestamp(record);
				record.append(enclosingChar);
			} else {
				appendTimestamp(record);
			}
			records.add(record.toString());
			return records;
		}

	}

	
	public boolean getCreateBlankFile() {
		return driverConfiguration.getCreateBlankFile();
	}

	
	public String getEnclosingCharacter() {
		return driverConfiguration.getEnclosingChar();
	}

	@Override
	protected int getStatusCheckDuration() {
		return ESCommunicator.NO_SCANNER_THREAD;
	}

	@Override
	public long getRollingUnit() {
		return rollingUnit;
	}

	@Override
	public int getRollingType() {
		return rollingType;
	}

}
