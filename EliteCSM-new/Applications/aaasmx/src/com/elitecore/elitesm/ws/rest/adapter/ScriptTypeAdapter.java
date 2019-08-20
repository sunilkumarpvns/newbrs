package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.constants.ScriptTypesConstants;

public class ScriptTypeAdapter extends XmlAdapter<String, String> {

	public static final String DRIVER_SCRIPT = "Driver Script";
	public static final String TRANSLATION_MAPPING_SCRIPT = "Translation Mapping Script";
	public static final String EXTERNAL_RADIUS_SCRIPT = "External Radius Script";
	public static final String DIAMETER_ROUTER_SCRIPT = "Diameter Router Script";

	@Override
	public String unmarshal(String scriptType) throws Exception {
		String scriptValue = null;

		if (DRIVER_SCRIPT.equalsIgnoreCase(scriptType)) {
			scriptValue = ScriptTypesConstants.DRIVER_SCRIPT;
		} else if (TRANSLATION_MAPPING_SCRIPT.equalsIgnoreCase(scriptType)) {
			scriptValue = ScriptTypesConstants.TRANSLATION_MAPPING_SCRIPT;
		} else if (EXTERNAL_RADIUS_SCRIPT.equalsIgnoreCase(scriptType)) {
			scriptValue = ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT;
		} else if (DIAMETER_ROUTER_SCRIPT.equalsIgnoreCase(scriptType)) {
			scriptValue = ScriptTypesConstants.DIAMETER_ROUTER_SCRIPT;
		} else {
			scriptValue = ScriptTypesConstants.INVALID_VALUE;
		}
		return scriptValue;
	}

	@Override
	public String marshal(String scriptType) throws Exception {
		String scriptValue = null;
		
		if (ScriptTypesConstants.DRIVER_SCRIPT.equals(scriptType)) {
			scriptValue = DRIVER_SCRIPT;
		} else if (ScriptTypesConstants.TRANSLATION_MAPPING_SCRIPT.equals(scriptType)) {
			scriptValue = TRANSLATION_MAPPING_SCRIPT;
		} else if (ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT.equals(scriptType)) {
			scriptValue = EXTERNAL_RADIUS_SCRIPT;
		} else if (ScriptTypesConstants.DIAMETER_ROUTER_SCRIPT.equals(scriptType)) {
			scriptValue = DIAMETER_ROUTER_SCRIPT;
		} else {
			scriptValue = null;
		}
		
		return scriptValue;
	}

}
