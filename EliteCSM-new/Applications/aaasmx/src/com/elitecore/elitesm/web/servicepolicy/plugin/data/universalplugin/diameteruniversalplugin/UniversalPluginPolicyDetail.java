package com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

public abstract class UniversalPluginPolicyDetail<T>{
	
	/*protected List<T> parameterDetailsForPlugin;*/
	@NotEmpty(message = "Plugin Action must be specified")
	private String action;
	
	@NotEmpty(message = "Diameter Universal Plugin Policy name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	
	@NotEmpty(message = "Enabled Field must be specified")
	@Pattern(regexp = "^$|true|false", message = "Invalid value of enabled field. Value could be 'true' or 'false'.")
	private String enabled;

	public UniversalPluginPolicyDetail() {
		
	}

	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name ="action")
	@Pattern(regexp = "^$|none|Stop", message = "Invalid Plugin Action parameter. It can be none or Stop.")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	@XmlElement(name = "enabled")
	public String getEnabled() {
		return enabled;
	}
	
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	
	/*@XmlTransient
	public abstract List<T> getParameterDetailsForPlugin();
	

	public abstract void setParameterDetailsForPlugin(List<T> parameterDetailsForPlugin);*/
	
	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);        
        out.println(" 		Policy Name = "+name);
        out.println(" 		Action = "+action);
        
        out.println("Policy Parameters:");
       /* if(parameterDetailsForPlugin != null && parameterDetailsForPlugin.size() >0){
        	for (int i=0; i< parameterDetailsForPlugin.size(); i++){
                out.println(parameterDetailsForPlugin.get(i));
}
        }else { 
        	out.println("No plugin-info configured");
        }*/
        out.close();
        return stringBuffer.toString();
	}
}
