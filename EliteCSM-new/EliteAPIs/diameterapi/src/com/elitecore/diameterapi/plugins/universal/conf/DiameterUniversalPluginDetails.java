package com.elitecore.diameterapi.plugins.universal.conf;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.diameterapi.plugins.DiameterUniversalPluginPolicyDetail;

@XmlRootElement(name = "diameter-universal-plugin-detail")
public class DiameterUniversalPluginDetails implements UniversalDiameterPluginConf, Differentiable {

	private String name;
	private List<DiameterUniversalPluginPolicyDetail> inPluginList;
	private List<DiameterUniversalPluginPolicyDetail> outPluginList;
	private String description;
	
	@XmlTransient
	private PluginInfo pluginInfo;
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElementWrapper(name ="universal-in-diameter-policies")
	@XmlElement(name ="policy-data")
	@Override
	public List<DiameterUniversalPluginPolicyDetail> getInPluginList() {
		return inPluginList;
	}
	
	public void setInPluginList(List<DiameterUniversalPluginPolicyDetail> inPluginList) {
		this.inPluginList = inPluginList;
	}
	
	@XmlElementWrapper(name ="universal-out-diameter-policies")
	@XmlElement(name ="policy-data")
	@Override
	public List<DiameterUniversalPluginPolicyDetail> getOutPluginList() {
		return outPluginList;
	}
	
	public void setOutPluginList(List<DiameterUniversalPluginPolicyDetail> outPluginList) {
		this.outPluginList = outPluginList;
	}
	
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Name", name);
		jsonObject.put("Description", description);

		if (inPluginList != null) {
			JSONArray array = new JSONArray();
			for (DiameterUniversalPluginPolicyDetail aaaUniversalPluginPolicyDetail : inPluginList) {
				array.add(aaaUniversalPluginPolicyDetail.toJson());
			}
			jsonObject.put("In Plugin Lists ", array);
		}

		if (outPluginList != null) {
			JSONArray array = new JSONArray();
			for (DiameterUniversalPluginPolicyDetail aaaUniversalPluginPolicyDetail : outPluginList) {
				array.add(aaaUniversalPluginPolicyDetail.toJson());
			}
			jsonObject.put("Out Plugin Lists ", array);
		}
		
		return jsonObject;
	}
	
	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}
	
	@PostRead
	public void postRead() {
		pluginInfo = new com.elitecore.core.commons.plugins.PluginInfo();
		pluginInfo.setPluginName(name);
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println("\n ----Universal Diameter Plugin Configuration---- ");
		out.println();
        out.println("----Universal Diameter In-Policy(ies) Details----");
        
        if(getInPluginList() != null && !getInPluginList().isEmpty()){
        	for (int i=0; i< getInPluginList().size(); i++){
        		out.println("In-Policy");
                out.println(getInPluginList().get(i) );
        	}
        }else { 
        	out.println("No Universal Diameter In-Policy(ies) configured");
        }

        out.println("----Universal Diameter Out-Policy(ies) Details----");
       
        if(getOutPluginList() != null && !getOutPluginList().isEmpty()){
        	for (int i=0; i< getOutPluginList().size(); i++){
        		out.println("Out-Policy");
                out.println(getOutPluginList().get(i));
        	}
        }else { 
        	out.println("No Universal Diameter Out-Policy(ies) configured");
        }
        out.close();

        return stringBuffer.toString();
	}
}