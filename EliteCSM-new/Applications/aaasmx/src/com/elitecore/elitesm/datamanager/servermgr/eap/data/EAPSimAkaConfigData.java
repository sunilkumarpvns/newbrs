package com.elitecore.elitesm.datamanager.servermgr.eap.data;

import java.io.Serializable;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.eapconfig.EAPEnableDisableAdapter;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder ={"pseudonymGenMethod","pseudonymHexenCoding","pseudonymPrefix","pseudonymRootNAI","pseudonymAAAIdentityInRootNAI",
		"fastReAuthGenMethod","fastReAuthHexenCoding","fastReAuthPrefix","fastReAuthRootNAI","fastReAuthAAAIdentityInRootNAI"})
@ValidObject
public class EAPSimAkaConfigData extends BaseData implements Serializable,Differentiable,Validator{

	private static final long serialVersionUID = 1L;
	
	private String configId;
	private Integer eapAuthType;
	private String eapId;
	
	@Pattern(regexp= RestValidationMessages.REGEX_EAP_SIMCONFIG_METHODTYPES, message = "Invalid value of Pseudonym Method , Possible values -NONE- ,BASE16, BASE32, BASE64, ELITECRYPT, BASIC_ALPHA_1")
	private String pseudonymGenMethod;
	
	@NotEmpty(message="Pseudonym Encoding must be specified ")
	@Pattern(regexp=RestValidationMessages.REGEX_ENABLE_DISABLE, message="Invalid value of Pseudonym Encoding(ENABLE or DISABLE)")
	private String pseudonymHexenCoding;
	
	private String pseudonymPrefix;
	
	@NotEmpty(message="Pseudonym RootNAI must be specified")
	@Pattern(regexp=RestValidationMessages.REGEX_ENABLE_DISABLE, message="Invalid value of Pseudonym RootNAI(ENABLE or DISABLE)")
	private String pseudonymRootNAI;
	
	private String pseudonymAAAIdentityInRootNAI;
	
	@Pattern(regexp= RestValidationMessages.REGEX_EAP_SIMCONFIG_METHODTYPES, message = "Invalid value of FastReAuth Method , Possible values -NONE- ,BASE16, BASE32, BASE64, ELITECRYPT, BASIC_ALPHA_1")
	private String fastReAuthGenMethod;
	
	@NotEmpty(message="FastReAuth Encoding must be specified")
	@Pattern(regexp=RestValidationMessages.REGEX_ENABLE_DISABLE, message="Invalid value of FastReAuth Encoding(ENABLE or DISABLE)")
	private String fastReAuthHexenCoding;
	
	private String fastReAuthPrefix;
	
	@NotEmpty(message="FastReAuth RootNAI must be specified")
	@Pattern(regexp=RestValidationMessages.REGEX_ENABLE_DISABLE, message="Invalid value of FastReAuth RootNAI(ENABLE or DISABLE)")
	private String fastReAuthRootNAI;
	
	private String fastReAuthAAAIdentityInRootNAI;
	
	@XmlElement(name="pseudonym-method")
	public String getPseudonymGenMethod() {
		return pseudonymGenMethod;
	}
	public void setPseudonymGenMethod(String pseudonymGenMethod) {
		this.pseudonymGenMethod = pseudonymGenMethod;
	}
	
	@XmlElement(name="pseudonym-encoding")
	@XmlJavaTypeAdapter(value=EAPEnableDisableAdapter.class)
	public String getPseudonymHexenCoding() {
		return pseudonymHexenCoding;
	}
	public void setPseudonymHexenCoding(String pseudonymHexenCoding) {
		this.pseudonymHexenCoding = pseudonymHexenCoding;
	}
	
	@XmlElement(name="pseudonym-prefix")
	public String getPseudonymPrefix() {
		return pseudonymPrefix;
	}
	public void setPseudonymPrefix(String pseudonymPrefix) {
		this.pseudonymPrefix = pseudonymPrefix;
	}
	
	@XmlElement(name="fast-reauth-method")
	public String getFastReAuthGenMethod() {
		return fastReAuthGenMethod;
	}
	public void setFastReAuthGenMethod(String fastReAuthGenMethod) {
		this.fastReAuthGenMethod = fastReAuthGenMethod;
	}
	
	@XmlElement(name="fast-reauth-encoding")
	@XmlJavaTypeAdapter(value=EAPEnableDisableAdapter.class)
	public String getFastReAuthHexenCoding() {
		return fastReAuthHexenCoding;
	}
	public void setFastReAuthHexenCoding(String fastReAuthHexenCoding) {
		this.fastReAuthHexenCoding = fastReAuthHexenCoding;
	}
	@XmlElement(name="fast-reauth-prefix")
	public String getFastReAuthPrefix() {
		return fastReAuthPrefix;
	}
	public void setFastReAuthPrefix(String fastReAuthPrefix) {
		this.fastReAuthPrefix = fastReAuthPrefix;
	}
	
	@XmlTransient
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	
	@XmlTransient
	public Integer getEapAuthType() {
		return eapAuthType;
	}
	public void setEapAuthType(Integer eapAuthType) {
		this.eapAuthType = eapAuthType;
	}
	
	@XmlTransient
	public String getEapId() {
		return eapId;
	}
	public void setEapId(String eapId) {
		this.eapId = eapId;
	}
	
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		JSONObject pseudonymConfiguration = new JSONObject();
		pseudonymConfiguration.put("Pseudonym Method", pseudonymGenMethod);
		pseudonymConfiguration.put("Pseudonym Encoding", pseudonymHexenCoding);
		pseudonymConfiguration.put("Pseudonym Prefix", pseudonymPrefix);
		pseudonymConfiguration.put("Pseudonym Root NAI", pseudonymRootNAI);
		pseudonymConfiguration.put("Pseudonym AAA Identity In Root NAI", pseudonymAAAIdentityInRootNAI);
		object.put("Pseudonym Configuration", pseudonymConfiguration);
		
		JSONObject fastReAuthConfiguration = new JSONObject();
		fastReAuthConfiguration.put("FastReAuth Method", fastReAuthGenMethod);
		fastReAuthConfiguration.put("FastReAuth Encoding", fastReAuthHexenCoding);
		fastReAuthConfiguration.put("FastReAuth Prefix", fastReAuthPrefix);
		fastReAuthConfiguration.put("FastReAuth Root NAI", fastReAuthRootNAI);
		fastReAuthConfiguration.put("FastReAuth AAA Identity In Root NAI", fastReAuthAAAIdentityInRootNAI);
		object.put("FastReAuth Configuration", fastReAuthConfiguration);
		
		return object;
	}
	
	@XmlElement(name="pseudonym-root-nai")
	@XmlJavaTypeAdapter(value=EAPEnableDisableAdapter.class)
	public String getPseudonymRootNAI() {
		return pseudonymRootNAI;
	}
	public void setPseudonymRootNAI(String pseudonymRootNAI) {
		this.pseudonymRootNAI = pseudonymRootNAI;
	}
	
	@XmlElement(name="pseudonym-aaa-identity-in-root-nai")
	public String getPseudonymAAAIdentityInRootNAI() {
		return pseudonymAAAIdentityInRootNAI;
	}
	public void setPseudonymAAAIdentityInRootNAI(
			String pseudonymAAAIdentityInRootNAI) {
		this.pseudonymAAAIdentityInRootNAI = pseudonymAAAIdentityInRootNAI;
	}
	
	@XmlElement(name="fast-reauth-root-nai")
	@XmlJavaTypeAdapter(value=EAPEnableDisableAdapter.class)
	public String getFastReAuthRootNAI() {
		return fastReAuthRootNAI;
	}
	public void setFastReAuthRootNAI(String fastReAuthRootNAI) {
		this.fastReAuthRootNAI = fastReAuthRootNAI;
	}
	
	@XmlElement(name="fast-reauth-aaa-identity-in-root-nai")
	public String getFastReAuthAAAIdentityInRootNAI() {
		return fastReAuthAAAIdentityInRootNAI;
	}
	public void setFastReAuthAAAIdentityInRootNAI(
			String fastReAuthAAAIdentityInRootNAI) {
		this.fastReAuthAAAIdentityInRootNAI = fastReAuthAAAIdentityInRootNAI;
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(this.pseudonymGenMethod)){
			setPseudonymGenMethod(null);
		}
		
		if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(this.fastReAuthGenMethod)){
			setFastReAuthGenMethod(null);
		}
		
		return isValid;
	}

}
