package com.elitecore.netvertex.core.locationmanagement.data;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
public class LocationInformationConfiguration implements ToStringable{

	private String locationId;
	private String area;
	
	private String city;
	private String region;
	private Country country;
	private int congestionStatus;
	private String param1;
	private String param2;
	private String param3;


	public LocationInformationConfiguration(String locationId, String area, String city, String region, Country country,
											int congestionStatus, String param1, String param2, String param3) {
		this.locationId = locationId;
		this.area = area;
		this.city = city;
		this.region = region;
		this.country=country;
		this.param1=param1;
		this.param2=param2;
		this.param3=param3;
		this.congestionStatus = congestionStatus;
	}


	public LocationInformationConfiguration(Country country) {
		this.country=country;
	}



	/**
	 * @return the locationId
	 */
	public String getLocationId() {
		return locationId;
	}
	
	
	
	
	public String getCity() {
		return city;
	}
	
	public String getRegion() {
		return region;
	}
	
	public String getCountry() {
		return country.getName();
	}

	public String getGeography(){
		return country.getGeography();
	}
	
	public String getArea() {
		return area;
	}
	
	public void setArea(String area) {
		this.area = area;
	}

	public void setCity (String city ) {
		this.city = city ;
	}



	public void setRegion (String region ) {
		this.region = region ;
	}

	public String getParam1() {
		return param1;
	}

	public String getParam2() {
		return param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + locationId.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this.getClass() == obj.getClass())
			return true;
		try{
			LocationInformationConfiguration other = (LocationInformationConfiguration) obj;
			return locationId != other.locationId;
		}catch(ClassCastException e){
			LogManager.ignoreTrace(e);
			return false;
		}
	
	}

	public int getCongestionStatus() {
		return congestionStatus;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- Location Information -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("Location ID", locationId)
				.append("Area", area)
				.append("City", city)
				.append("Region", region)
				.append("Country", country.getName())
				.append("Geography",country.getGeography())
				.append("Param1", param1)
				.append("Param2", param2)
				.append("Param3", param3)
				.append("Congestion Status", congestionStatus);
	}
}
