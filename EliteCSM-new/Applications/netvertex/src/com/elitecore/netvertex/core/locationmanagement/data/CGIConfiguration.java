package com.elitecore.netvertex.core.locationmanagement.data;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class CGIConfiguration implements ToStringable{

	private long ci;
	private LocationInformationConfiguration locationInformationConfiguration;
	
	public CGIConfiguration(long ci, LocationInformationConfiguration locationInform) {
		super();
		this.ci = ci;
		this.locationInformationConfiguration = locationInform;
	}
	
	
	public long getCi() {
		return ci;
	}


	public LocationInformationConfiguration getLocationInformationConfiguration(){
		
		return locationInformationConfiguration;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- CGI Configuration -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("CI Code", ci)
				.append("Location Id", locationInformationConfiguration.getLocationId());
	}
}
