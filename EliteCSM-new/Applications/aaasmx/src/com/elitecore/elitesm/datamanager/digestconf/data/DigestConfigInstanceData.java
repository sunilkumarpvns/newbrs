package com.elitecore.elitesm.datamanager.digestconf.data;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlType(propOrder = {"name","description","realm","defaultQoP","defaultAlgo","opaque","defaultNonceLength","defaultNonce",
		"draftAAASipEnable"})
@XmlRootElement(name="digest-configuration")
public class DigestConfigInstanceData extends BaseData implements Serializable,Differentiable{

	private static final long serialVersionUID = 1L;
	private String digestConfId;
	
	@Expose
	@SerializedName("Name")
	@NotEmpty(message = "Digest Configuration Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX ,message=RestValidationMessages.NAME_INVALID)
	private String name;
	
	@Expose
	@SerializedName("Description")
	private String description;
	
	@Expose
	@SerializedName("Digest Realm")
	@NotEmpty(message="Digest Realm must be specified")
	private String  realm;       
	
	@Expose
	@SerializedName("Default Digest QoP")
	@NotEmpty(message = "Default Digest QoP must be specified")
	@Pattern(regexp = "^$|auth|auth-int", message = "Invalid value of Default Digest QoP parameter. It can be auth or auth-int.")
	private String defaultQoP;          
	
	@Expose
	@SerializedName("Default Digest Algorithm")
	@NotEmpty(message="Default Digest Algorithm  must be specified")
	private String defaultAlgo;              
	
	@Expose
	@SerializedName("Digest Opaque")
	@NotEmpty(message="Digest Opaque value  must be Specified")
	private String opaque;        
	
	@Expose
	@SerializedName("Default Digest Nonce")
	@NotEmpty(message="Default Digest Nonce value must be specified")
	private String defaultNonce;               
	
	@Expose
	@SerializedName("Default Digest Nonce Length")
	@NotNull(message = "Default Digest Nonce Length value must be specified")
	@Min(value=0,message="Default Digest Nonce Length  value  must be Numeric")
	private Integer defaultNonceLength;   
	
	@Expose
	@SerializedName("Draft Sterman AAA SIP")
	@NotEmpty(message = "Draft Sterman AAA SIP must be specified")
	@Pattern(regexp = "^$|true|false", message = "Invalid value of Draft Sterman AAA SIP. Value could be 'true' or 'false'.")
	private String draftAAASipEnable;
	
	private String lastModifiedbyStaffid;
	private String createdbyStaffid;
	private Timestamp lastModifiedDate;
	private Timestamp createDate;
	private String auditUId;
	
	public DigestConfigInstanceData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
	@XmlTransient
	public String getLastModifiedbyStaffid() {
		return lastModifiedbyStaffid;
	}
	public void setLastModifiedbyStaffid(String lastModifiedbyStaffid) {
		this.lastModifiedbyStaffid = lastModifiedbyStaffid;
	}
	
	@XmlTransient
	public String getCreatedbyStaffid() {
		return createdbyStaffid;
	}
	public void setCreatedbyStaffid(String createdbyStaffid) {
		this.createdbyStaffid = createdbyStaffid;
	}
	
	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	@XmlTransient
	public String getDigestConfId() {
		return digestConfId;
	}
	public void setDigestConfId(String digestConfId) {
		this.digestConfId = digestConfId;
	}
	
	@XmlElement(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name="digest-realm")
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	
	@XmlElement(name="default-digest-qop")
	public String getDefaultQoP() {
		return defaultQoP;
	}
	public void setDefaultQoP(String defaultQoP) {
		this.defaultQoP = defaultQoP;
	}
	
	@XmlElement(name="default-digest-algorithm")
	public String getDefaultAlgo() {
		return defaultAlgo;
	}
	public void setDefaultAlgo(String defaultAlgo) {
		this.defaultAlgo = defaultAlgo;
	}
	
	@XmlElement(name="digest-opaque")
	public String getOpaque() {
		return opaque;
	}
	public void setOpaque(String opaque) {
		this.opaque = opaque;
	}
	
	@XmlElement(name="default-digest-nonce")
	public String getDefaultNonce() {
		return defaultNonce;
	}
	public void setDefaultNonce(String defaultNonce) {
		this.defaultNonce = defaultNonce;
	}
	
	@XmlElement(name="default-digest-nonce-length")
	public Integer getDefaultNonceLength() {
		return defaultNonceLength;
	}
	public void setDefaultNonceLength(Integer defaultNonceLength) {
		this.defaultNonceLength = defaultNonceLength;
	}
	
	@XmlElement(name="draft-sterman-aaa-sip")
	public String getDraftAAASipEnable() {
		return draftAAASipEnable;
	}
	public void setDraftAAASipEnable(String draftAAASipEnable) {
		this.draftAAASipEnable = draftAAASipEnable;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("Digest Realm", realm);
		object.put("Default Digest QoP", (defaultQoP.equals("auth") ? "Auth" : "Auth-Int"));
		object.put("Default Digest Algorithm", defaultAlgo);
		object.put("Digest Opaque", opaque);
		object.put("Default Digest Nonce Length", defaultNonceLength);
		object.put("Default Digest Nonce", defaultNonce);
		object.put("Draft Sterman AAA SIP", draftAAASipEnable);
		return object;
	}
	
	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}  
}