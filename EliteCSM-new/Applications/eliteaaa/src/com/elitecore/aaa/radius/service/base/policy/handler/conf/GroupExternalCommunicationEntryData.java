package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

public class GroupExternalCommunicationEntryData extends ExternalCommunicationEntryData {

	private String radiusEsiGroupName;
	private RadiusEsiGroupData radiusESIGroupData;

	@XmlElement(name ="esi-group-name")
	public String getRadiusEsiGroupName() {
		return radiusEsiGroupName;
	}

	public void setRadiusEsiGroupName(String radiusEsiGroupName) {
		this.radiusEsiGroupName = radiusEsiGroupName;
	}

	@XmlTransient
	public RadiusEsiGroupData getRadiusESIGroupData() {
		return radiusESIGroupData;
	}

	public void setRadESIGroupData(RadiusEsiGroupData esiGroupByName) {
		this.radiusESIGroupData = esiGroupByName;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("ESI-Group-Name", radiusEsiGroupName);
		object.put("Rule Set", getRuleset().trim());
		object.put("Translation Mapping", getTranslationMapping());
		object.put("Script", getScript().trim());
		object.put("Accept On Timeout", isAcceptOnTimeout());
		return object;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(format("%-30s: %s", "Ruleset", getRuleset() != null ? getRuleset() : ""));
		out.println(format("%-30s: %s", "ESI-Group-Name", getRadiusEsiGroupName()));
		out.println(format("%-30s: %s", "Translation Mapping",
				getTranslationMapping() != null ? getTranslationMapping() : ""));
		out.println(format("%-30s: %s", "Script", getScript() != null ? getScript() : ""));
		out.println(format("%-30s: %s", "Accept on Timeout", isAcceptOnTimeout()));
		out.close();
		return writer.toString();
	}
}