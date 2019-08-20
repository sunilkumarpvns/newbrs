package com.elitecore.netvertex.service.offlinernc.prefix;

public class Region {

    private String name;
    private Country country;
    
	public Region(String name, Country country) {
		this.name = name;
		this.country = country;
	}

	public String getName() {
		return name;
	}

	public Country getCountry() {
		return country;
	}
}
