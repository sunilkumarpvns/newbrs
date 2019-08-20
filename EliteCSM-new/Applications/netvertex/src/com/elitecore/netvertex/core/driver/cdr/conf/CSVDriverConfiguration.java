package com.elitecore.netvertex.core.driver.cdr.conf;

import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public interface CSVDriverConfiguration extends DriverConfiguration {

	public long getRollingUnit();
	public int getRollingType();
	public int getPort();
	public int getFailOverTime();
	public boolean isHeader();
	public boolean isSequenceGlobalization();
	public String getDelimiter();
	public String getFileName();
	public String getFileLocation();
	public String getPrefixFileName();
	public String getDefaultFolderName();
	public String getSequenceRange();
	public String getDirectoryName();
	public String getSequencePosition();
	public String getAllocatingProtocol();
	public String getAddress();
	public String getRemoteLocation();
	public String getUserName();
	public String getPassword();
	public String getPostOperation();
	public String getArchiveLocation();
	public String getReportingType();
	public String getDriverType();
	public String getDriverInstanceId();
	
	public String getUsageKeyHeader();
	public String getInputOctetsHeader();
	public String getOutputOctetsHeader();
	public String getTotalOctetsHeader();
	public String getUsageTimeHeader();
	public String getRatingGroupHeader();
	public String getServiceIdentifierHeader();

	String getLevelHeader();

	public int getTimeBoundary();
	public long getTimeBasedRollingUnit();
	public long getSizeBasedRollingUnit();
	public long getRecordBasedRollingUnit();
	
	public List<CSVFieldMapping> getCDRFieldMappings();
	public Map<String, CSVStripMapping> getStripMappings();
	public SimpleDateFormat getCDRTimeStampFormat();
	public Map<RollingTypeConstant, Integer> getRollingTypeMap();

	public String getUnAccountedVolumeHeader();
	public String getUnAccountedUsageTimeHeader();
	public String getAccountedNonMonetaryVolumeHeader();
	public String getAccountedNonMonetaryUsageTimeHeader();
	public String getAccountedMonetaryHeader();
	public String getRateHeader();
	public String getDiscountHeader();
	public String getDiscountAmountHeader();
	public String getReportedVolumeHeader();
	public String getReportedUsageTimeHeader();
	public String getReportedEventHeader();

	public String getCoreSessionIdHeader();

    public String getVolumePulseHeader();
	public String getTimePulseHeader();
    public String getCalculatedVolumePulseHeader();
	public String getCalculatedTimePulseHeader();
	public String getCalledPartyHeader();
	public String getCallingPartyHeader();
	public String getCallStartHeader();
	public String getCallStopHeader();
	public String getRateCardHeader();

	String getCurrencyHeader();
	String getExponentHeader();

    String getPreviousUnAccountedVolumeHeader();

	String getPreviousUnAccountedTimeHeader();

	String getPackageNameHeader();

	String getProductOfferNameHeader();

	String getEventActionHeader();

	String getQuotaProfileNameHeader();

	String getRateCardNameHeader();

    String getChargingOperationHeader();

    String getRateCardGroupHeader();

    String getRateMinorUnitHeader();

    String getServiceHeader();

	String getCallTypeHeader();

	String getTariffTypeHeader();

    String getRevenueCodeHeader();
}
