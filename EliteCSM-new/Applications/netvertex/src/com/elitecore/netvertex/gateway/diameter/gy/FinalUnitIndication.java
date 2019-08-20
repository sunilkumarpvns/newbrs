package com.elitecore.netvertex.gateway.diameter.gy;

import java.io.StringWriter;
import java.util.List;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.CommonConstants;

public class FinalUnitIndication {

	private FinalUnitAction action;
	private RedirectAddressType redirectAddressType;
	private String redirectServerAddress;
	private List<String> restrictionFilterRules;
	private List<String> filterIds;
	
	public FinalUnitIndication() {	}
	
	public FinalUnitAction getAction() {
		return action;
	}
	public void setAction(FinalUnitAction action) {
		this.action = action;
	}
	public RedirectAddressType getRedirectAddressType() {
		return redirectAddressType;
	}
	public void setRedirectAddressType(RedirectAddressType redirectAddressType) {
		this.redirectAddressType = redirectAddressType;
	}
	public String getRedirectServerAddress() {
		return redirectServerAddress;
	}
	public void setRedirectServerAddress(String redirectServerAddress) {
		this.redirectServerAddress = redirectServerAddress;
	}
	public List<String> getRestrictionFilterRules() {
		return restrictionFilterRules;
	}
	public void setRestrictionFilterRules(List<String> restrictionFilterRules) {
		this.restrictionFilterRules = restrictionFilterRules;
	}
	public List<String> getFilterIds() {
		return filterIds;
	}
	public void setFilterIds(List<String> filterIds) {
		this.filterIds = filterIds;
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

		if (action != null) {
			ipWriter.println("Final Action: " + action.name());
		}

		if (redirectAddressType != null) {
			ipWriter.println("Redirect Address Type: " + redirectAddressType);
		}

		if (redirectServerAddress != null) {
			ipWriter.println("Redirect Server Address: " + redirectServerAddress);
		}

		if (restrictionFilterRules != null) {
			StringBuilder siBuilder = new StringBuilder(restrictionFilterRules.get(0) + "");

			for (int i = 1; i < restrictionFilterRules.size(); i++) {
				siBuilder.append(CommonConstants.COMMA).append(restrictionFilterRules.get(i));
			}

			ipWriter.println("Restriction Filter Rules: " + siBuilder.toString());
		}

		if (filterIds != null) {
			ipWriter.println("Filter Ids: ");
			for (int i = 0; i < filterIds.size(); i++) {
				ipWriter.println(filterIds.get(i));
			}
		}
	}
}
