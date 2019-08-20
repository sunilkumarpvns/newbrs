package com.elitecore.netvertex.core.locationmanagement.data;


import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class WiFiSSIDInfoConfiguration implements ToStringable{

	private String wifiSSID;
	private LocationInformationConfiguration locationInformationConfiguration;
  
	public WiFiSSIDInfoConfiguration(String wifiSSID, LocationInformationConfiguration locationInformationConfiguration) {
		this.wifiSSID = wifiSSID;
		this.locationInformationConfiguration = locationInformationConfiguration;
	}
	
	
	public String getWifiSSID() {
		return wifiSSID;
	}


	public LocationInformationConfiguration getLocationInformationConfiguration() {
		return locationInformationConfiguration;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- WiFiSSID Information -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("WiFi SSID", wifiSSID)
				.appendChildObject("Location Information", locationInformationConfiguration);
	}
}
