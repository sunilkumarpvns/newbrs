package com.elitecore.elitesm.datamanager.servermgr.alert.data;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.TrapVersionAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.elitecore.elitesm.ws.rest.validator.esi.ValidateIPPort;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "trap-listener")
@XmlType(propOrder = {"trapServer", "trapVersion", "community", "advanceTrap", "repeatedMessageReduction"})
@ValidObject
public class AlertTrapListenerData extends BaseAlertListener implements Validator {

	private String trapListenerId;
	
	@Expose
	@SerializedName("Trap Server")
	@ValidateIPPort(message = "Invalid Trap Server. Enter valid HOST:PORT address.")
	private String trapServer;
	
	@Expose
	@SerializedName("Trap Version")
	@NotEmpty(message = "Trap Vesion must be specified. It can be V1, V2 or V2c.")
	private String trapVersion;
	
	@Expose
	@SerializedName("Community")
	@NotEmpty(message = "Community must be specified.")
	private String community;
	
	@Expose
	@SerializedName("Advance Trap")
	@NotNull(message = "Advance Trap must be specified. It can be true or false")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE, message = "Invalid Advance Trap. It can be true or false." )
	private String advanceTrap;
	
	@Expose
	@SerializedName("Repeated Message Reduction")
	@NotNull(message = "Repeated Message Reduction must be specified. It can be true or false")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE, message = "Invalid Repeated Message Reduction. It can be true or false." )
	private String repeatedMessageReduction;
	
	@XmlElement(name = "advance-trap")
	public String getAdvanceTrap() {
		return advanceTrap;
	}
	public void setAdvanceTrap(String advanceTrap) {
		this.advanceTrap = advanceTrap.toLowerCase();
	}
	
	@XmlTransient
	public String getTrapListenerId() {
		return trapListenerId;
	}
	public void setTrapListenerId(String trapListenerId) {
		this.trapListenerId = trapListenerId;
	}
	
	@XmlElement(name = "trap-server")
	public String getTrapServer() {
		return trapServer;
	}
	public void setTrapServer(String trapServer) {
		this.trapServer = trapServer;
	}
	
	@XmlElement(name = "trap-version")
	@XmlJavaTypeAdapter(TrapVersionAdapter.class)
	public String getTrapVersion() {
		return trapVersion;
	}
	public void setTrapVersion(String trapVersion) {
		this.trapVersion = trapVersion.toLowerCase();
	}
	
	@XmlElement(name = "community")
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Trap Server", trapServer);
		if(trapVersion != null){
			if(trapVersion.equalsIgnoreCase("0")){
				object.put("Trap version", "V1");
			}else if(trapVersion.equalsIgnoreCase("1")){
				object.put("Trap version", "V2c");
			}else{
				object.put("Trap version",trapVersion);
			}
		}
		object.put("Community", community);
		
		if( advanceTrap.equalsIgnoreCase("true")){
			object.put("Advanced Trap", "True");
		}else{
			object.put("Advanced Trap", "False");
		}
		
		if(repeatedMessageReduction.equalsIgnoreCase("true")){
			object.put("Repeated Message Reduction", "True");
		}else{
			object.put("Repeated Message Reduction", "False");
		}
		return object;
	}
	
	@XmlElement(name = "repeated-message-reduction")
	public String getRepeatedMessageReduction() {
		return repeatedMessageReduction;
	}
	public void setRepeatedMessageReduction(String repeatedMessageReduction) {
		this.repeatedMessageReduction = repeatedMessageReduction.toLowerCase();
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		if (Strings.isNullOrEmpty(trapVersion) == false){
			if (RestValidationMessages.INVALID_VALUE.equalsIgnoreCase(trapVersion)) {
				RestUtitlity.setValidationMessage(context, "Invalid Trap Version. It can be V1, V2 or V2c.");
				return false;
			}
		}
		return true;
	}
}
