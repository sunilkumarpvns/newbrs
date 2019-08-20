package com.elitecore.netvertex.core.locationmanagement.data;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class SAIConfiguration implements ToStringable{

	private long sacCode;
	private LocationInformationConfiguration locationInformationConfiguration;
	public SAIConfiguration(long sacCode, LocationInformationConfiguration locatioInformation) {
		super();
		this.sacCode = sacCode;
		this.locationInformationConfiguration = locatioInformation;
	}
	public long getSacCode() {
		return sacCode;
	}
	
	public LocationInformationConfiguration getLocationInformationConfiguration() {
		return locationInformationConfiguration;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- SAI Information -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("SAC Code", sacCode)
				.append("Location Id", locationInformationConfiguration.getLocationId());
	}
}
