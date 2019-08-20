package com.elitecore.netvertex.service.offlinernc.prefix;

public class City {

	private String name;
	private Region region;
	private String countryId;

	public City(String countryId, String name, Region region) {
		this.countryId = countryId;
		this.name = name;
		this.region = region;
	}

	public String getName() {
		return name;
	}

	public Region getRegion() {
		return region;
	}

	public String getCountryId() {
		return countryId;
	}
}
