package com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

@XmlType(propOrder = {"name","description","status","prePolicyLists","postPolicyLists"})
@XmlRootElement(name = "universal-plugin-detail")
public class UniversalPluginConfigurationImpl implements UniversalPluginConfiguration, Differentiable {
	
	public UniversalPluginConfigurationImpl() {
		description = RestUtitlity.getDefaultDescription();
	}

	@NotEmpty(message = "Universal Plugin name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	
	@NotEmpty(message = "Pre Policy must be specified")
	private List<AAAUniversalPluginPolicyDetail> prePolicyLists;
	
	@NotEmpty(message = "Post Policy must be specified")
	private List<AAAUniversalPluginPolicyDetail> postPolicyLists;
	private String description;
	
	@NotEmpty(message = "Plugin Status must be specified")
	@Pattern(regexp = "^$|CST01|CST02", message = "Invalid value of Status parameter. Value could be 'ACTIVE' or 'INACTIVE'.")
	private String status;
	
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
	@Valid
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
	@Valid
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
	
	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(StatusAdapter.class)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Name", name);
		jsonObject.put("Description", description);

		if (Collectionz.isNullOrEmpty(prePolicyLists) == false) {
			JSONArray array = new JSONArray();
			for (AAAUniversalPluginPolicyDetail aaaUniversalPluginPolicyDetail : prePolicyLists) {
				array.add(aaaUniversalPluginPolicyDetail.toJson());
			}
			jsonObject.put("Pre Policy List ", array);
		}

		if (Collectionz.isNullOrEmpty(postPolicyLists) == false) {
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

		if(Collectionz.isNullOrEmpty(getPrePolicyLists()) == false){
			for (int i=0; i< getPrePolicyLists().size(); i++){
				out.println("	--Pre Policy--");
				out.println(getPrePolicyLists().get(i) );
			}
		}else { 
			out.println("No Universal Pre policy configured");
		}

		out.println("----Universal Post Policy Details----");

		if(Collectionz.isNullOrEmpty(getPostPolicyLists()) == false){
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
