package com.elitecore.elitesm.datamanager.externalsystem.data;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.systemx.esix.udp.StatusCheckMethod;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.ExternalSystemTypeAdapter;
import com.elitecore.elitesm.ws.rest.adapter.StatusCheckMethodAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.IsNumeric;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.elitecore.elitesm.ws.rest.validator.esi.ValidateIPPort;
import com.elitecore.elitesm.ws.rest.validator.esi.ValidateRealmName;


@XmlRootElement(name="external-system")
@ValidateRealmName(esiTypeField="esiTypeId",realmNameField="realmNames",message="Realm Names must be specified")
@ValidObject
@XmlType(propOrder ={"name","description","esiTypeId","address","sharedSecret","realmNames","timeout","retryLimit",
		"expiredRequestLimitCount","statusCheckDuration","statusCheckMethod","packetBytes","minLocalPort","supportedAttribute",
		"unSupportedAttribute"})
public class ExternalSystemInterfaceInstanceData extends BaseData implements Serializable,IExternalSystemInterfaceInstanceData,Differentiable,Validator{
	
	private static final long serialVersionUID = 1L;
	private String esiInstanceId;
	
	@NotNull(message = "ESI Type must be specified")
	@Min(value=0,message = "Enter proper ESI Type")
	private Long esiTypeId;
	
	@NotEmpty(message = "Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX,message=RestValidationMessages.NAME_INVALID)
	private String name;
	
	private String description;
	
	@NotEmpty(message = "Address must be specified")
	private String address;

	@NotEmpty(message = "Shared Secret must be specified")
	private String sharedSecret;
	private String status;
	
	private String createdByStaffId;
	private String lastModifiedByStaffId;
	private Timestamp lastModifiedDate;
	private Timestamp createDate;
	
	@NotNull(message = "Timeout must be specified")
	@Min(value=0,message="Timeout must be numeric")
	private Long timeout;
	
	@NotNull(message = "Minimum Local Port must be specified")
	private Integer minLocalPort;
	
	@NotNull(message = "Expired Request Limit Count must be specified")
	private Long expiredRequestLimitCount;
	
	@NotNull(message = "Retry Limit must be specified")
	@Min(value=0,message="Retry Limit must be numeric")
	private Long retryLimit;
	
	@NotNull(message = "Status Check Duration must be specified")
	@Min(value=0,message = "Status Check Duration must be numeric")
	private Long statusCheckDuration;
	private String realmNames;
	private String supportedAttribute;
	private String unSupportedAttribute;
	
	@Min(value=1L,message="provide valid Status Check Method Name, Value must be from ICMP_REQUEST or RADIUS_PACKET or PACKET_BYTES")
	private Long statusCheckMethod;
	private String packetBytes;
	private String auditUId;
	
	public ExternalSystemInterfaceInstanceData() {
		description = RestUtitlity.getDefaultDescription();
	}

	@XmlElement(name = "realm")
	public String getRealmNames() {
		return realmNames;
	}
	public void setRealmNames(String realmNames) {
		this.realmNames = realmNames;
	}
	
	@XmlElement(name = "timeout",defaultValue ="0")
	@IsNumeric(message="Timeout must be numeric")
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	
	@XmlElement(name = "expired-request-limit-count",defaultValue ="10")
	@IsNumeric(message="Expired Request Limit Count must be numeric")
	public Long getExpiredRequestLimitCount() {
		return expiredRequestLimitCount;
	}
	public void setExpiredRequestLimitCount(Long expiredRequestLimitCount) {
		this.expiredRequestLimitCount = expiredRequestLimitCount;
	}
	
	@XmlElement(name="esi-type")
	@XmlJavaTypeAdapter(ExternalSystemTypeAdapter.class)
	public Long getEsiTypeId() {
		return esiTypeId;
	}
	public void setEsiTypeId(Long esiTypeId) {
		this.esiTypeId = esiTypeId;
	}
	
	@XmlTransient
	public String getEsiInstanceId() {
		return esiInstanceId;
	}
	public void setEsiInstanceId(String esiInstanceId) {
		this.esiInstanceId = esiInstanceId;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name = "address")
	@ValidateIPPort(message="Please Enter Valid Address [HOST:PORT]")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@XmlElement(name = "minimum-local-port",defaultValue ="10")
	@IsNumeric(message="Minimum Local Port must be numeric")
	public Integer getMinLocalPort() {
		return minLocalPort;
	}
	public void setMinLocalPort(Integer minLocalPort) {
		this.minLocalPort = minLocalPort;
	}
	
	@XmlElement(name = "shared-secret",defaultValue ="secret")
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
	
	@XmlTransient
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	
	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
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
	
	@XmlElement(name = "retry-count")
	@IsNumeric(message="Retry Limit must be numeric")
	public Long getRetryLimit() {
		return retryLimit;
	}
	public void setRetryLimit(Long retryLimit) {
		this.retryLimit = retryLimit;
	}
	
	@XmlElement(name = "status-check-duration")
	@IsNumeric(message="Status Check Duration must be numeric")
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	
	@XmlElement(name = "supported-attributes")
	public String getSupportedAttribute() {
		return supportedAttribute;
	}
	public void setSupportedAttribute(String supportedAttribute) {
		this.supportedAttribute = supportedAttribute;
	}
	
	@XmlElement(name = "unsupported-attributes")
	public String getUnSupportedAttribute() {
		return unSupportedAttribute;
	}
	public void setUnSupportedAttribute(String unSupportedAttribute) {
		this.unSupportedAttribute = unSupportedAttribute;
	}
	
	@XmlElement(name = "status-check-method")
	@XmlJavaTypeAdapter(value = StatusCheckMethodAdapter.class)
	public Long getStatusCheckMethod() {
		return statusCheckMethod;
	}
	public void setStatusCheckMethod(Long statusCheckMethod) {
		this.statusCheckMethod = statusCheckMethod;
	}
	
	@XmlElement(name = "packet-bytes")
	public String getPacketBytes() {
		return packetBytes;
	}
	public void setPacketBytes(String udpBytes) {
		this.packetBytes = udpBytes;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("Address", address);
		object.put("Shared Secret", sharedSecret);
		if(realmNames != null && realmNames.length() > 0)
			object.put("Realm Names", realmNames);
		object.put("Timeout (ms)", timeout);
		object.put("Retry Count", retryLimit);
		object.put("Expired Request Limit Count", expiredRequestLimitCount);
		object.put("Status Check Duration (Sec.)", statusCheckDuration);
		object.put("Status Check Method", getStatusCheckMethodName(statusCheckMethod));
		object.put("Packet Bytes", packetBytes);
		object.put("Minimum Local Port", minLocalPort);
		object.put("Supported Attribute", supportedAttribute);
		object.put("Unsupported Attribute", unSupportedAttribute);
		return object;
	}
	
	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	private String getStatusCheckMethodName(Long statusCheckMethod) {
		StatusCheckMethod[] statusCheckMethods=StatusCheckMethod.VALUES;
		
		for(StatusCheckMethod stCheckMethod:statusCheckMethods){
			if(stCheckMethod.id == statusCheckMethod){
				return stCheckMethod.name;
			}
		}
		return null;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if(this.statusCheckMethod != null && this.statusCheckMethod == 3){
			if(Strings.isNullOrBlank(this.packetBytes)){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Packet Bytes must be specified");
			}
		}
		
		return isValid;
	}

}
