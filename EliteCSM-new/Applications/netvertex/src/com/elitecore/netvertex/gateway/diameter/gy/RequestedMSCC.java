package com.elitecore.netvertex.gateway.diameter.gy;

import java.io.StringWriter;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.GyServiceUnits;

/**
 * ReportedMSCC will represent MSCC block received from client. 
 * It will contain requested units of specified rating group and service identifier.
 * 
 * @author narendra.pathai
 */
public class RequestedMSCC {
	
	private List<Long> serviceIdentifiers;
	// Intentionally taken Long instead long. Reason: null will represent absense of rating group in MSCC
	private Long ratingGroup;
	private GyServiceUnits requestedServiceUnits;
	
	public void setServiceIdentifiers(List<Long> serviceIdentifiers) {
		this.serviceIdentifiers = serviceIdentifiers;
	}

	public void setRatingGroup(Long ratingGroup) {
		this.ratingGroup = ratingGroup;
	}

	public List<Long> getServiceIdentifiers() {
		return serviceIdentifiers;
	}

	public Long getRatingGroup() {
		return ratingGroup;
	}
	
	public GyServiceUnits getRequestedServiceUnits() {
		return requestedServiceUnits;
	}

	public void setRequestedServiceUnits(GyServiceUnits requestedServiceUnits) {
		this.requestedServiceUnits = requestedServiceUnits;
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		IndentingPrintWriter ipWriter = new IndentingPrintWriter(stringWriter);
		toString(ipWriter);
		ipWriter.close();
		return stringWriter.toString();
	}

	public void toString(IndentingPrintWriter ipWriter) {

		if (ratingGroup != null) {
			ipWriter.println("Rating Group: " + ratingGroup);
		}

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
	}
}
