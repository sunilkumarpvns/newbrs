package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.ws.rest.serverconfig.alertconfiguration.AlertConfigurationConstant;

public class RollingUnitAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String rollingUnit) {

		String rollingUnitvalue = null;
		long value = 0;
		if (AlertConfigurationConstant.DAILY.equalsIgnoreCase(rollingUnit)) {
			rollingUnitvalue = AlertConfigurationConstant.DAILY_VALUE;
		} else if (AlertConfigurationConstant.HOUR.equals(rollingUnit)) {
			rollingUnitvalue = AlertConfigurationConstant.HOUR_VALUE;
		} else if (AlertConfigurationConstant.MINUTE.equals(rollingUnit)){
			rollingUnitvalue = AlertConfigurationConstant.MINUTE_VALUE;
		} else {
			try {
				value = Long.parseLong(rollingUnit);
				rollingUnitvalue = String.valueOf(value);
			} catch (NumberFormatException nfe) {
				rollingUnitvalue = "-1";
			}
		}

		return rollingUnitvalue;
	}

	@Override
	public String marshal(String rollingUnit) {

		return rollingUnit;

	}
}
