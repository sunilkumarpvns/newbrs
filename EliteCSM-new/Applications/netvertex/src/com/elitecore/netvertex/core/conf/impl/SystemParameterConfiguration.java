package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.sm.systemparameter.OfflineRnCFileSystemParameter;
import com.elitecore.corenetvertex.sm.systemparameter.OfflineRnCRatingSystemParameter;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.service.offlinernc.util.RoundingModeTypes;

public class SystemParameterConfiguration implements ToStringable {

    // offline
	private String failedFileExtension;
	private Integer reRatingProcessRecordBatchSize;
	private Integer errorProcessingRecordBatchSize;
	private Integer edrNotReceivedThreshold;
	private String zeroUsageCDR;
	private Integer backDatedUnratedEdrProcessingDays;
	private Integer ratingServiceCacheIntervalSec;
	private Integer numberOfDecimalPointsInTransaction;
	private String rateSelectionWhenDateChange;
	private String ratedEDRLocalArchiveDirectory;
	private RoundingModeTypes roundingCurrencyToSpecifiedDecimalPoint;
	private String edrDateTimeStampFormat;
	private String multiValueSeparator;
	private String systemTimeZone;

	// Global
    private String systemCurrency;
    private String country;
    private String operator;
    private DeploymentMode deploymentMode;
    private String tax;

    public SystemParameterConfiguration(){
		this.failedFileExtension = OfflineRnCFileSystemParameter.FAILED_FILE_EXTENSION.getValue();
		this.reRatingProcessRecordBatchSize = Integer.parseInt(OfflineRnCFileSystemParameter.RE_RATING_PROCESS_RECORD_BATCH_SIZE.getValue());
		this.errorProcessingRecordBatchSize = Integer.parseInt(OfflineRnCFileSystemParameter.ERROR_PROCESSING_RECORD_BATCH_SIZE.getValue());
		this.edrNotReceivedThreshold = Integer.parseInt(OfflineRnCFileSystemParameter.EDR_NOT_RECEIVED_THRESHOLD_DAYS.getValue());
		this.zeroUsageCDR = OfflineRnCRatingSystemParameter.INSERT_ZERO_USAGE_CDR.getValue();
		this.backDatedUnratedEdrProcessingDays = Integer.parseInt(OfflineRnCRatingSystemParameter.BACK_DATED_UNRATED_EDR_PROCESSING_DAYS.getValue());
		this.ratingServiceCacheIntervalSec = Integer.parseInt(OfflineRnCRatingSystemParameter.RATING_SERVICE_CACHE_INTERVAL_SEC.getValue());
		this.numberOfDecimalPointsInTransaction = Integer.parseInt(OfflineRnCRatingSystemParameter.NUMBER_OF_DECIMAL_POINTS_IN_TRANSACTION.getValue());
		this.rateSelectionWhenDateChange = OfflineRnCRatingSystemParameter.RATE_SELECTION_WHEN_DATE_CHANGE.getValue();
		this.ratedEDRLocalArchiveDirectory = OfflineRnCRatingSystemParameter.RATED_EDR_LOCAL_ARCHIVE_DIRECTORY_JSON.getValue();
		this.roundingCurrencyToSpecifiedDecimalPoint = RoundingModeTypes.TRUNCATE;
		this.edrDateTimeStampFormat = OfflineRnCRatingSystemParameter.EDR_DATE_TIMESTAMP_FORMAT.getValue();
		this.multiValueSeparator = OfflineRnCRatingSystemParameter.MULTIPLE_VALUE_SEPARATOR.getValue();
		this.systemTimeZone = OfflineRnCRatingSystemParameter.SYSTEM_TIMEZONE.getValue();
        this.systemCurrency = SystemParameter.CURRENCY.getValue();
        this.deploymentMode = DeploymentMode.PCC;
    }

	public String getFailedFileExtension() {
		return failedFileExtension;
	}

	public void setFailedFileExtension(String failedFileExtension) {
		this.failedFileExtension = failedFileExtension;
	}

	public Integer getReRatingProcessRecordBatchSize() {
		return reRatingProcessRecordBatchSize;
	}

	public void setReRatingProcessRecordBatchSize(Integer reRatingProcessRecordBatchSize) {
		this.reRatingProcessRecordBatchSize = reRatingProcessRecordBatchSize;
	}

	public Integer getErrorProcessingRecordBatchSize() {
		return errorProcessingRecordBatchSize;
	}

	public void setErrorProcessingRecordBatchSize(Integer errorProcessingRecordBatchSize) {
		this.errorProcessingRecordBatchSize = errorProcessingRecordBatchSize;
	}

	public Integer getEdrNotReceivedThreshold() {
		return edrNotReceivedThreshold;
	}

	public void setEdrNotReceivedThreshold(Integer edrNotReceivedThreshold) {
		this.edrNotReceivedThreshold = edrNotReceivedThreshold;
	}

	public String getZeroUsageCDR() {
		return zeroUsageCDR;
	}

	public void setZeroUsageCDR(String zeroUsageCDR) {
		this.zeroUsageCDR = zeroUsageCDR;
	}

	public Integer getBackDatedUnratedEdrProcessingDays() {
		return backDatedUnratedEdrProcessingDays;
	}

	public void setBackDatedUnratedEdrProcessingDays(Integer backDatedUnratedEdrProcessingDays) {
		this.backDatedUnratedEdrProcessingDays = backDatedUnratedEdrProcessingDays;
	}

	public Integer getRatingServiceCacheIntervalSec() {
		return ratingServiceCacheIntervalSec;
	}

	public void setRatingServiceCacheIntervalSec(Integer ratingServiceCacheIntervalSec) {
		this.ratingServiceCacheIntervalSec = ratingServiceCacheIntervalSec;
	}

	public Integer getNumberOfDecimalPointsInTransaction() {
		return numberOfDecimalPointsInTransaction;
	}

	public void setNumberOfDecimalPointsInTransaction(Integer numberOfDecimalPointsInTransaction) {
		this.numberOfDecimalPointsInTransaction = numberOfDecimalPointsInTransaction;
	}

	public String getRateSelectionWhenDateChange() {
		return rateSelectionWhenDateChange;
	}

	public void setRateSelectionWhenDateChange(String rateSelectionWhenDateChange) {
		this.rateSelectionWhenDateChange = rateSelectionWhenDateChange;
	}

	public String getRatedEDRLocalArchiveDirectory() {
		return ratedEDRLocalArchiveDirectory;
	}

	public void setRatedEDRLocalArchiveDirectory(String ratedEDRLocalArchiveDirectory) {
		this.ratedEDRLocalArchiveDirectory = ratedEDRLocalArchiveDirectory;
	}

	public RoundingModeTypes getRoundingCurrencyToSpecifiedDecimalPoint() {
		return roundingCurrencyToSpecifiedDecimalPoint;
	}

	public void setRoundingCurrencyToSpecifiedDecimalPoint(RoundingModeTypes roundingCurrencyToSpecifiedDecimalPoint) {
		this.roundingCurrencyToSpecifiedDecimalPoint = roundingCurrencyToSpecifiedDecimalPoint;
	}

	public String getEdrDateTimeStampFormat() {
		return edrDateTimeStampFormat;
	}

	public void setEdrDateTimeStampFormat(String edrDateTimeStampFormat) {
		this.edrDateTimeStampFormat = edrDateTimeStampFormat;
	}

	public String getMultiValueSeparator() {
		return multiValueSeparator;
	}

	public void setMultiValueSeparator(String multiValueSeparator) {
		this.multiValueSeparator = multiValueSeparator;
	}

	public String getSystemCurrency() {
		return systemCurrency;
	}

	public void setSystemCurrency(String systemCurrency) {
		this.systemCurrency = systemCurrency;
	}

	public void setTax(String tax) {
    	this.tax = tax;
	}

	public String getSystemTimeZone() {
		return systemTimeZone;
	}

	public void setSystemTimeZone(String systemTimeZone) {
		this.systemTimeZone = systemTimeZone;
	}

	public String getTax() {
		return tax;
	}

	public DeploymentMode getDeploymentMode() {
		return deploymentMode;
	}

	public void setDeploymentMode(DeploymentMode deploymentMode) {
		this.deploymentMode = deploymentMode;
	}

	@Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- System Parameter Configuration -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.newline()
                .append("System Currency", systemCurrency)
                .append("Country", country)
                .append("Operator", operator)
				.append("Deployment Mode", deploymentMode)
				.append("Tax", tax);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
