package com.elitecore.netvertex.core.locationmanagement.data;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class RAIConfiguration implements ToStringable{

	private long racCode;
	private LocationInformationConfiguration locationInformationConfiguration;
	public RAIConfiguration(long racCode, LocationInformationConfiguration locationInformationConfiguration) {
		super();
		this.racCode = racCode;
		this.locationInformationConfiguration = locationInformationConfiguration;
	}
	public long getRacCode() {
		return racCode;
	}
	public LocationInformationConfiguration getLocationInformationConfiguration() {
		return locationInformationConfiguration;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- RAI Information -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("RAC Code", racCode)
				.append("Location Id", locationInformationConfiguration.getLocationId());
	}
}
