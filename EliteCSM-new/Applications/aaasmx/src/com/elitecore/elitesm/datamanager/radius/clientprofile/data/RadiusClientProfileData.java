package com.elitecore.elitesm.datamanager.radius.clientprofile.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
import org.hibernate.validator.constraints.Range;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.ClientTypeAdapter;
import com.elitecore.elitesm.ws.rest.adapter.VendorNameAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.IsNumeric;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "trusted-client-profile")
@XmlType(propOrder = {"profileName","description","clientTypeId","vendorInstanceId","dnsList","userIdentities","prepaidStandard",
		"clientPolicy","hotlinePolicy","dhcpAddress","haAddress","multipleClassAttribute","filterUnsupportedVsa","supportedVendorLst",
		"dynAuthPort","coaSupportedAttributes","coaUnsupportedAttributes","dmSupportedAttributes","dmUnsupportedAttributes"})
@ValidObject
public class RadiusClientProfileData extends BaseData implements Serializable,Differentiable,Validator{

	private static final long serialVersionUID = 1L;
	
	private String profileId;
	
	@Expose
	@SerializedName("Client Profile Name")
	@NotEmpty(message = "client profile name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX,message=RestValidationMessages.NAME_INVALID)
	private String profileName;
	
	@Expose
	@SerializedName("Description")
	private String description;
	
	// view purpose.
	@Expose
	@SerializedName("Vendor Name")
	private VendorData vendorData;
		
	@Expose
	@SerializedName("Client Type")
	private ClientTypeData clientTypeData;
	
	private Long clientTypeId;
	private String vendorInstanceId;
	private String dnsList;
	private String userIdentities;
	private String prepaidStandard;
	private String clientPolicy;
	private String hotlinePolicy;
	private String dhcpAddress;
	private String haAddress;
	
	@NotEmpty(message="Multiple Class Attribute must be specified")
	private String multipleClassAttribute;
	
	private List<ProfileSuppVendorRelData> supportedVendorList;
		
	private List<VendorData> supportedVendorLst;
	
	@Pattern(regexp="|true|false|TRUE|FALSE",message="Filter Unsupported VSA must be true or false")
	private String filterUnsupportedVsa;
	
	private String systemgenerated;
	private Timestamp createDate;
	private Timestamp lastModifiedDate;
	private String createdByStaffId;
	private String lastModifiedByStaffId;
	
	//DynaAuth Specific Configurations
	private Integer dynAuthPort;
	private String coaSupportedAttributes;
	private String coaUnsupportedAttributes;
	private String dmSupportedAttributes;
	private String dmUnsupportedAttributes;
	private String auditUId;
	
	public RadiusClientProfileData() {
		description = RestUtitlity.getDefaultDescription();
		setDynAuthPort(3799);
		setFilterUnsupportedVsa("false");
	}
	
	@XmlElement(name = "filter-unsupported-vsa")
	public String getFilterUnsupportedVsa() {
		return filterUnsupportedVsa;
	}
	public void setFilterUnsupportedVsa(String filterUnsupportedVsa) {
		this.filterUnsupportedVsa = filterUnsupportedVsa;
	}
	
	@XmlElementWrapper(name="supported-vendors")
	@XmlElement(name="vendor")
	@Valid
	public List<VendorData> getSupportedVendorLst() {
		return supportedVendorLst;
	}
	public void setSupportedVendorLst(List<VendorData> supportedVendorLst) {
		this.supportedVendorLst = supportedVendorLst;
	}
	
	@XmlTransient
	public VendorData getVendorData() {
		return vendorData;
	}
	public void setVendorData(VendorData vendorData) {
		this.vendorData = vendorData;
	}
	
	@XmlTransient
	public ClientTypeData getClientTypeData() {
		return clientTypeData;
	}
	public void setClientTypeData(ClientTypeData clientTypeData) {
		this.clientTypeData = clientTypeData;
	}
	
	@XmlTransient
	public String getProfileId() {
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	
	@XmlElement(name = "profile-name")
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	@XmlElement(name = "decription")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name = "vendor-name")
	@NotNull(message = "Vendor Name must be specified")
	@XmlJavaTypeAdapter(value = VendorNameAdapter.class)
	public String getVendorInstanceId() {
		return vendorInstanceId;
	}
	public void setVendorInstanceId(String vendorInstanceId) {
		this.vendorInstanceId = vendorInstanceId;
	}
	
	@XmlElement(name = "dns-list")
	public String getDnsList() {
		return dnsList;
	}
	public void setDnsList(String dnsList) {
		this.dnsList = dnsList;
	}
	
	@XmlElement(name = "user-identities")
	public String getUserIdentities() {
		return userIdentities;
	}
	public void setUserIdentities(String userIdentities) {
		this.userIdentities = userIdentities;
	}
	
	@XmlElement(name = "prepaid-standard")
	public String getPrepaidStandard() {
		return prepaidStandard;
	}
	public void setPrepaidStandard(String prepaidStandard) {
		this.prepaidStandard = prepaidStandard;
	}
	
	@XmlElement(name = "client-policy")
	public String getClientPolicy() {
		return clientPolicy;
	}
	public void setClientPolicy(String clientPolicy) {
		this.clientPolicy = clientPolicy;
	}
	
	@XmlElement(name = "hotline-policy")
	public String getHotlinePolicy() {
		return hotlinePolicy;
	}
	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}
	
	@XmlElement(name = "dhcp-address")
	@Pattern(regexp="|"+RestValidationMessages.IPV4_IPV6_REGEX,message="Enter Valid DHCP Address")
	public String getDhcpAddress() {
		return dhcpAddress;
	}
	public void setDhcpAddress(String dhcpAddress) {
		this.dhcpAddress = dhcpAddress;
	}
	
	@XmlElement(name = "ha-address")
	@Pattern(regexp="|"+RestValidationMessages.IPV4_IPV6_REGEX,message="Enter Valid HA Address")
	public String getHaAddress() {
		return haAddress;
	}
	public void setHaAddress(String haAddress) {
		this.haAddress = haAddress;
	}
	
	@XmlElement(name = "client-type")
	@NotNull(message ="Client Type must be Specified" )
	@Min(value = 0,message = "Client Type must be Valid")
	@XmlJavaTypeAdapter(value = ClientTypeAdapter.class)
	public Long getClientTypeId() {
		return clientTypeId;
	}
	public void setClientTypeId(Long clientTypeId) {
		this.clientTypeId = clientTypeId;
	}
	
	@XmlTransient
	public List<ProfileSuppVendorRelData> getSupportedVendorList() {
		return supportedVendorList;
	}
	public void setSupportedVendorList(List<ProfileSuppVendorRelData> supportedVendorList) {
		this.supportedVendorList = supportedVendorList;
	}
	
	@XmlTransient
	public String getSystemgenerated() {
		return systemgenerated;
	}
	public void setSystemgenerated(String systemgenerated) {
		this.systemgenerated = systemgenerated;
	}
	
	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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
	
	@XmlElement(name = "multiple-class-attribute")
	@Pattern(regexp="YES|NO" ,message ="Multiple class attribute must be YES or NO")
	public String getMultipleClassAttribute() {
		return multipleClassAttribute;
	}
	public void setMultipleClassAttribute(String multipleClassAttribute) {
		this.multipleClassAttribute = multipleClassAttribute.toUpperCase();
	}
	
	@XmlElement(name = "coa-supported-attributes")
	public String getCoaSupportedAttributes() {
		return coaSupportedAttributes;
	}
	public void setCoaSupportedAttributes(String coaSupportedAttribute) {
		this.coaSupportedAttributes = coaSupportedAttribute;
	}
	
	@XmlElement(name = "coa-unsupported-attributes")
	public String getCoaUnsupportedAttributes() {
		return coaUnsupportedAttributes;
	}
	public void setCoaUnsupportedAttributes(String coaUnsupportedAttribute) {
		this.coaUnsupportedAttributes = coaUnsupportedAttribute;
	}
	
	@XmlElement(name = "dm-supported-attributes")
	public String getDmSupportedAttributes() {
		return dmSupportedAttributes;
	}
	public void setDmSupportedAttributes(String dmSupportedAttribute) {
		this.dmSupportedAttributes = dmSupportedAttribute;
	}
	
	@XmlElement(name = "dm-unsupported-attributes")
	public String getDmUnsupportedAttributes() {
		return dmUnsupportedAttributes;
	}
	public void setDmUnsupportedAttributes(String dmUnsupportedAttribute) {
		this.dmUnsupportedAttributes = dmUnsupportedAttribute;
	}
	
	@XmlElement(name = "dyn-auth-port")
	@IsNumeric(message="DynAuth Port must be numeric",isAllowBlank = true)
	@Range(min=0,max=65535,message="Please verify DynAuth Port")
	public Integer getDynAuthPort() {
		return dynAuthPort;
	}
	public void setDynAuthPort(Integer dynAuthPort) {
		this.dynAuthPort = dynAuthPort;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Client Profile Name", profileName);
		object.put("Client Type", EliteSMReferencialDAO.fetchClientTypeData(clientTypeId));
		object.put("Vendor Name", EliteSMReferencialDAO.fetchVendorData(vendorInstanceId));
		
		if(supportedVendorList!=null){
			JSONArray array = new JSONArray();
			for (ProfileSuppVendorRelData element : supportedVendorList) {
				array.add(EliteSMReferencialDAO.fetchVendorData(element.getVendorInstanceId()));
			}
			object.put("Supported Vendor", array);
		}
		object.put("Description", description);
		object.put("DNS List", dnsList);
		object.put("User Identities", userIdentities);
		object.put("Prepaid Standard", prepaidStandard);
		object.put("Client Policy", clientPolicy);
		object.put("Hotline Policy", hotlinePolicy);
		object.put("DHCP Address", dhcpAddress);
		object.put("HA Address", haAddress);
		object.put("Multiple Class Attribute", multipleClassAttribute);
		object.put("Filter Unsupported VSA", filterUnsupportedVsa);
		object.put("DynAuth Port", dynAuthPort);
		object.put("COA supported Attributes", coaSupportedAttributes);
		object.put("COA unsupported Attributes", coaUnsupportedAttributes);
		object.put("DM supported Attributes", dmSupportedAttributes);
		object.put("DM unsupported Attributes", dmUnsupportedAttributes);
		
		return object;
	}
	
	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
		List<String> invalidSupportedVendorlst = new LinkedList<String>();
	
		if (RestValidationMessages.INVALID.equals(vendorInstanceId)) {
			isValid = false;
			RestUtitlity.setValidationMessage(context,"Vendor Name must be valid");
		}
		
		if(Collectionz.isNullOrEmpty(this.supportedVendorLst) == false){
			for (VendorData vendorData : this.supportedVendorLst) {
				if(vendorData != null){
					try{
						clientProfileBLManager.getVendorIdFromName(vendorData.getVendorName());
					}catch(DataManagerException e){
						invalidSupportedVendorlst.add(vendorData.getVendorName());
					}
				}
			}
			if(Collectionz.isNullOrEmpty(invalidSupportedVendorlst) == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context,"Invalid supported vendor in supprted vendor list : "+invalidSupportedVendorlst.toString());
			}
		}
		
		return isValid;
	}
}
