package com.elitecore.elitesm.web.radius.clientprofile.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ProfileSuppVendorRelData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateClientProfileForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	
	private long clientTypeId;
	private String clientTypeName;
	private List<ClientTypeData> lstClientType= new ArrayList<ClientTypeData>();
	private long vendorId;
	private String vendorName;
	private List<VendorData> lstVendorData = new ArrayList<VendorData>();
	private long profileId;
	private String profileName;
	private String description;
	private String vendorInstanceId;
	private String dnsList;
	private String userIdentities;
	private String prepaidStandard;
	private String clientPolicy;
	private String hotlinePolicy;
	//private String framedPool;
	private String dhcpAddress;
	private String haAddress;
	private String multipleClassAttribute="yes";
	private boolean filterUnsupportedVsa;
	private List<ProfileSuppVendorRelData> supportedVendorList;
	private String[] selectedSupportedVendorIdList;
	
	//DynaAuth Specific Configurations
	private Integer dynaAuthPort=3799;
	private String coaSupportedAttributes;
	private String coaUnsupportedAttributes;
	private String dmSupportedAttributes;
	private String dmUnsupportedAttributes;
	
	
	public boolean isFilterUnsupportedVsa() {
		return filterUnsupportedVsa;
	}
	public void setFilterUnsupportedVsa(boolean filterUnsupportedVsa) {
		this.filterUnsupportedVsa = filterUnsupportedVsa;
	}
	public String[] getSelectedSupportedVendorIdList() {
		return selectedSupportedVendorIdList;
	}
	public void setSelectedSupportedVendorIdList(
			String[] selectedSupportedVendorIdList) {
		this.selectedSupportedVendorIdList = selectedSupportedVendorIdList;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	public long getClientTypeId() {
		return clientTypeId;
	}
	public void setClientTypeId(long clientTypeId) {
		this.clientTypeId = clientTypeId;
	}
	public String getClientTypeName() {
		return clientTypeName;
	}
	public void setClientTypeName(String clientTypeName) {
		this.clientTypeName = clientTypeName;
	}
	public List<ClientTypeData> getLstClientType() {
		return lstClientType;
	}
	public void setLstClientType(List<ClientTypeData> lstClientType) {
		this.lstClientType = lstClientType;
	}
	public long getVendorId() {
		return vendorId;
	}
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public List<VendorData> getLstVendorData() {
		return lstVendorData;
	}
	public void setLstVendorData(List<VendorData> lstVendorData) {
		this.lstVendorData = lstVendorData;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getVendorInstanceId() {
		return vendorInstanceId;
	}
	public void setVendorInstanceId(String vendorInstanceId) {
		this.vendorInstanceId = vendorInstanceId;
	}
	public String getDnsList() {
		return dnsList;
	}
	public void setDnsList(String dnsList) {
		this.dnsList = dnsList;
	}
	public String getUserIdentities() {
		return userIdentities;
	}
	public void setUserIdentities(String userIdentities) {
		this.userIdentities = userIdentities;
	}
	public String getPrepaidStandard() {
		return prepaidStandard;
	}
	public void setPrepaidStandard(String prepaidStandard) {
		this.prepaidStandard = prepaidStandard;
	}
	public String getClientPolicy() {
		return clientPolicy;
	}
	public void setClientPolicy(String clientPolicy) {
		this.clientPolicy = clientPolicy;
	}
	public String getHotlinePolicy() {
		return hotlinePolicy;
	}
	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}
	/*public String getFramedPool() {
		return framedPool;
	}
	public void setFramedPool(String framedPool) {
		this.framedPool = framedPool;
	}*/
	public String getDhcpAddress() {
		return dhcpAddress;
	}
	public void setDhcpAddress(String dhcpAddress) {
		this.dhcpAddress = dhcpAddress;
	}
	public String getHaAddress() {
		return haAddress;
	}
	public void setHaAddress(String haAddress) {
		this.haAddress = haAddress;
	}
	public List<ProfileSuppVendorRelData> getSupportedVendorList() {
		return supportedVendorList;
	}
	public void setSupportedVendorList(
			List<ProfileSuppVendorRelData> supportedVendorList) {
		this.supportedVendorList = supportedVendorList;
	}
	public String getMultipleClassAttribute() {
		return multipleClassAttribute;
	}
	public void setMultipleClassAttribute(String multipleClassAttribute) {
		this.multipleClassAttribute = multipleClassAttribute;
	}
	public String getCoaSupportedAttributes() {
		return coaSupportedAttributes;
	}
	public void setCoaSupportedAttributes(String coaSupportedAttribute) {
		this.coaSupportedAttributes = coaSupportedAttribute;
	}
	public String getCoaUnsupportedAttributes() {
		return coaUnsupportedAttributes;
	}
	public void setCoaUnsupportedAttributes(String coaUnsupportedAttribute) {
		this.coaUnsupportedAttributes = coaUnsupportedAttribute;
	}
	public String getDmSupportedAttributes() {
		return dmSupportedAttributes;
	}
	public void setDmSupportedAttributes(String dmSupportedAttribute) {
		this.dmSupportedAttributes = dmSupportedAttribute;
	}
	public String getDmUnsupportedAttributes() {
		return dmUnsupportedAttributes;
	}
	public void setDmUnsupportedAttributes(String dmUnsupportedAttribute) {
		this.dmUnsupportedAttributes = dmUnsupportedAttribute;
	}
	public Integer getDynaAuthPort() {
		return dynaAuthPort;
	}
	public void setDynaAuthPort(Integer dynaAuthPort) {
		this.dynaAuthPort = dynaAuthPort;
	}
}
