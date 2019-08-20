package com.elitecore.netvertex.gateway.diameter.gy;

import java.io.StringWriter;
import java.util.List;
import java.util.Objects;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResultCode;

/**
 * ReportedMSCC will represent MSCC block received from client. 
 * It will contain reported usage of specified rating group and service identifier 
 * 
 * @author Chetan.Sankhala
 */
public class MSCC {

	private List<Long> serviceIdentifiers;
	// Intentionally taken Long instead long. Reason: null will represent absence of rating group in MSCC
	private long ratingGroup;
	private GyServiceUnits usedServiceUnits;
	private GyServiceUnits grantedServiceUnits;
	private GyServiceUnits requestedServiceUnits;
	private ResultCode resultCode;
	private ReportingReason reportingReason;
	private long validityTime;
	private FinalUnitIndication finalUnitIndiacation;
	private long timeQuotaThreshold;
	private long volumeQuotaThreshold;

    private MSCC(List<Long> serviceIdentifiers,
                 long ratingGroup,
                 GyServiceUnits usedServiceUnits,
                 ResultCode resultCode,
                 GyServiceUnits grantedServiceUnits,
                 long validityTime,
                 ReportingReason reportingReason,
                 long timeQuotaThreshold,
                 FinalUnitIndication finalUnitIndiacation,
                 long volumeQuotaThreshold,
				 GyServiceUnits requestedServiceUnits) {
        this.serviceIdentifiers = serviceIdentifiers;
        this.ratingGroup = ratingGroup;
        this.usedServiceUnits = usedServiceUnits;
        this.grantedServiceUnits = grantedServiceUnits;
        this.resultCode = resultCode;
        this.reportingReason = reportingReason;
        this.validityTime = validityTime;
        this.finalUnitIndiacation = finalUnitIndiacation;
        this.timeQuotaThreshold = timeQuotaThreshold;
        this.volumeQuotaThreshold = volumeQuotaThreshold;
		this.requestedServiceUnits = requestedServiceUnits;
	}

    public MSCC() {
		this.reportingReason = ReportingReason.THRESHOLD;
		this.resultCode = ResultCode.SUCCESS;
	}


    public GyServiceUnits getGrantedServiceUnits() {
		return grantedServiceUnits;
	}

	public void setGrantedServiceUnits(GyServiceUnits grantedServiceUnits) {
		this.grantedServiceUnits = grantedServiceUnits;
	}

	public long getValidityTime() {
		return validityTime;
	}

	public void setValidityTime(long validityTime) {
		this.validityTime = validityTime;
	}

	public FinalUnitIndication getFinalUnitIndiacation() {
		return finalUnitIndiacation;
	}

	public void setFinalUnitIndiacation(FinalUnitIndication finalUnitIndiacation) {
		this.finalUnitIndiacation = finalUnitIndiacation;
	}

	public long getTimeQuotaThreshold() {
		return timeQuotaThreshold;
	}

	public void setTimeQuotaThreshold(long timeQuotaThreshold) {
		this.timeQuotaThreshold = timeQuotaThreshold;
	}

	public long getVolumeQuotaThreshold() {
		return volumeQuotaThreshold;
	}

	public void setVolumeQuotaThreshold(long volumeQuotaThreshold) {
		this.volumeQuotaThreshold = volumeQuotaThreshold;
	}

	public ResultCode getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}

	public void setServiceIdentifiers(List<Long> serviceIdentifiers) {
		this.serviceIdentifiers = serviceIdentifiers;
	}

	public void setRatingGroup(long ratingGroup) {
		this.ratingGroup = ratingGroup;
	}

	public List<Long> getServiceIdentifiers() {
		return serviceIdentifiers;
	}

	public long getRatingGroup() {
		return ratingGroup;
	}

	public GyServiceUnits getUsedServiceUnits() {
		return usedServiceUnits;
	}

	public void setUsedServiceUnits(GyServiceUnits usedServiceUnits) {
		this.usedServiceUnits = usedServiceUnits;
	}
	
	public void setReportingReason(ReportingReason reportingReason) {
		this.reportingReason = reportingReason;
	}
	
	public ReportingReason getReportingReason() {
		return reportingReason;
	}

	public void setRequestedServiceUnits(GyServiceUnits requestedServiceUnits) {
		this.requestedServiceUnits = requestedServiceUnits;
	}

	public GyServiceUnits getRequestedServiceUnits() {
		return requestedServiceUnits;
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
        IndentingWriter ipWriter = new IndentingPrintWriter(stringWriter);
		toString(ipWriter);
		ipWriter.close();
		return stringWriter.toString();
	}

	public void toString(IndentingWriter ipWriter) {

		ipWriter.println("Rating Group: " + ratingGroup);
		if (Collectionz.isNullOrEmpty(serviceIdentifiers) == false) {
			StringBuilder siBuilder = new StringBuilder(serviceIdentifiers.get(0)+"");
			
			for (int i = 1; i < serviceIdentifiers.size(); i++) {
				siBuilder.append(CommonConstants.COMMA).append(serviceIdentifiers.get(i));
			}
			
			ipWriter.println("Service Identifiers: " + siBuilder.toString());
		}

		if (requestedServiceUnits != null) {
			ipWriter.println("Requested Service Units: ");
			ipWriter.incrementIndentation();
			requestedServiceUnits.toString(ipWriter);
			ipWriter.decrementIndentation();
		}

		
		if (usedServiceUnits != null) {
			ipWriter.println("Used Service Units: ");
			ipWriter.incrementIndentation();
			usedServiceUnits.toString(ipWriter);
			ipWriter.decrementIndentation();
		}

        if (grantedServiceUnits != null) {
            ipWriter.println("Granted Service Units: ");
            ipWriter.incrementIndentation();
            grantedServiceUnits.toString(ipWriter);
            ipWriter.decrementIndentation();
        }
	}

    public MSCC copy() {

        GyServiceUnits usedServiceUnits = null;
        if (Objects.nonNull(this.usedServiceUnits)) {
            usedServiceUnits = this.usedServiceUnits.copy();
        }

        GyServiceUnits grantedServiceUnits = null;
        if (Objects.nonNull(this.grantedServiceUnits)) {
            grantedServiceUnits = this.grantedServiceUnits.copy();
        }

        return new MSCC(serviceIdentifiers, ratingGroup, usedServiceUnits, resultCode, grantedServiceUnits, validityTime, reportingReason, timeQuotaThreshold, finalUnitIndiacation, volumeQuotaThreshold, requestedServiceUnits);
    }
}
