package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Collectionz;

/**
 * 
 * @author narendra.pathai
 */
@XmlRootElement(name = "cdr-generation")
public class DiameterCDRGenerationHandlerData extends DiameterApplicationHandlerDataSupport {
	
	@Valid
	private List<DiameterCDRHandlerEntryData> entries = new ArrayList<DiameterCDRHandlerEntryData>();
	
	@XmlElement(name = "cdr-handler-entry")
	@Size(min = 1, message = "Atleast one mapping is required in CDR Handler" )
	public List<DiameterCDRHandlerEntryData> getEntries() {
		return entries;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Diameter CDR Generation Handler | Enabled: %s", 10, ' '), getEnabled()));
		out.println(repeat("-", 70));
		for (DiameterCDRHandlerEntryData entry : getEntries()) {
			out.println(entry);
		}
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Enabled", getEnabled());
		if(Collectionz.isNullOrEmpty(entries) == false){
			JSONArray array = new JSONArray();
			for(DiameterCDRHandlerEntryData diameterCDRHandlerEntryData : entries){
				array.add(diameterCDRHandlerEntryData.toJson());
			}
			if(array.size() > 0){
				object.put("CDR Handler entry", array);
			}
		}
		return object;
	}
}
