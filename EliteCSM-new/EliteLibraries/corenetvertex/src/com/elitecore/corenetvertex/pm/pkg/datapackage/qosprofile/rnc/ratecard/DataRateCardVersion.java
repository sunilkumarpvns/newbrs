package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard;


import java.io.Serializable;
import java.util.List;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;

public class DataRateCardVersion implements RateCardVersion, Serializable {
	private String rateCardId;
	private String rateCardName;
	private String name;
	private List<VersionDetail> versionDetails;
	
	public DataRateCardVersion(String rateCardId, String rateCardName, String name, List<VersionDetail> versionDetails) {
		this.rateCardId = rateCardId;
		this.rateCardName = rateCardName;
		this.name = name;
		this.versionDetails = versionDetails;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
    public List<VersionDetail> getVersionDetails() {
		return versionDetails;
	}

	@Override
	public String toString() {

		IndentingToStringBuilder toStringBuilder = new IndentingToStringBuilder();

		toString(toStringBuilder);

		return toStringBuilder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("Name", name)
				.appendChildObject("Version Details", versionDetails);

	}

	@Override
	public String getRateCardId() {
		return rateCardId;
	}

	public String getRateCardName() {
		return rateCardName;
	}
}
