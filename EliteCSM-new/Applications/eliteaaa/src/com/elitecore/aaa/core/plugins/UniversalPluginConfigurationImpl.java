package com.elitecore.aaa.core.plugins;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.core.plugins.conf.UniversalPluginConfiguration;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.plugins.PluginInfo;

@XmlRootElement(name = "universal-plugin-detail")
public class UniversalPluginConfigurationImpl implements UniversalPluginConfiguration, Differentiable {

	private String name;
	private List<AAAUniversalPluginPolicyDetail> prePolicyLists;
	private List<AAAUniversalPluginPolicyDetail> postPolicyLists;
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
	
	@Override
	@XmlElementWrapper(name = "universal-pre-policies")
	@XmlElement(name = "pre-policy-detail")
	public List<AAAUniversalPluginPolicyDetail> getPrePolicyLists() {
		return prePolicyLists;
	}

	public void setPrePolicyLists(
			List<AAAUniversalPluginPolicyDetail> prePolicyLists) {
		this.prePolicyLists = prePolicyLists;
	}
	
	@Override
	@XmlElementWrapper(name = "universal-post-policies")
	@XmlElement(name = "post-policy-detail")
	public List<AAAUniversalPluginPolicyDetail> getPostPolicyLists() {
		return postPolicyLists;
	}
	
	public void setPostPolicyLists(
			List<AAAUniversalPluginPolicyDetail> postPolicyLists) {
		this.postPolicyLists = postPolicyLists;
	}
	
	@PostRead
	public void postRead() {
		pluginInfo = new com.elitecore.core.commons.plugins.PluginInfo();
		pluginInfo.setPluginName(name);
	}
	
	@XmlTransient
	public PluginInfo getPluginInfo() {
		return pluginInfo;
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

		if (prePolicyLists != null) {
			JSONArray array = new JSONArray();
			for (AAAUniversalPluginPolicyDetail aaaUniversalPluginPolicyDetail : prePolicyLists) {
				array.add(aaaUniversalPluginPolicyDetail.toJson());
			}
			jsonObject.put("Pre Policy List ", array);
		}

		if (postPolicyLists != null) {
			JSONArray array = new JSONArray();
			for (AAAUniversalPluginPolicyDetail aaaUniversalPluginPolicyDetail : postPolicyLists) {
				array.add(aaaUniversalPluginPolicyDetail.toJson());
			}
			jsonObject.put("Post Policy List ", array);
		}
		
		return jsonObject;
	}
	
	@Override
	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println("\n ----Universal Plugin Configuration---- ");
		out.println();
		out.println("----Universal Pre Policy Details----");

		if(getPrePolicyLists() != null && !getPrePolicyLists().isEmpty()){
			for (int i=0; i< getPrePolicyLists().size(); i++){
				out.println("	--Pre Policy--");
				out.println(getPrePolicyLists().get(i) );
			}
		}else { 
			out.println("No Universal Pre policy configured");
		}

		out.println("----Universal Post Policy Details----");

		if(getPostPolicyLists() != null && !getPostPolicyLists().isEmpty()){
			for (int i=0; i< getPostPolicyLists().size(); i++){
				out.println("	--Post Policy--");
				out.println(getPostPolicyLists().get(i));
			}
		}else { 
			out.println("No Universal Post policy configured");
		}
		out.close();

		return stringBuffer.toString();
	}
}
