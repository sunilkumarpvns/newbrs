package com.elitecore.elitesm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.ServerTypeAdapter;
import com.elitecore.elitesm.ws.rest.adapter.ServiceTypeAdaptor;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.elitecore.elitesm.ws.rest.validator.server.ValidServerType;


/**
 * 
 * @author dhavalraval
 *
 */
@XmlRootElement(name="server")
@XmlType(propOrder ={"netServerTypeId","name","description","adminHost","adminPort","javaHome","serverHome",
		"version","netServerId","servicesListForView","servicesListForAdd","serviceListForDelete","staff"})
@ValidObject
public class NetServerInstanceData extends BaseData implements INetServerInstanceData,Serializable,Validator{
	
	
	private static final long serialVersionUID = 1L;
	private String netServerId;
	private String netServerCode;
	
	@NotEmpty(message="Name is a compulsory field Please enter required data in this field")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX,message=RestValidationMessages.NAME_INVALID)
	private String name;
	
	@NotEmpty(message="Description cannot be null")
	private String description;
	
	private String version;
	
	private String netServerTypeId;
	
	@NotEmpty(message="Admin Interface IP is a compulsory field Please enter required data in this field")
	@Pattern(regexp=RestValidationMessages.IPV4_IPV6_REGEX,message="Please Verify Admin Inteface IP")
	private String adminHost;
	
	@NotNull(message="AdminInterfacePort is a compulsory field Please enter required data in this field")
	@Range(min=0,max=65535,message="Please verify Admin Interface Port")
	private Integer adminPort;
	
	private Timestamp createDate;
	private String createdByStaffId;
	private Timestamp lastModifiedDate;
	private String lastModifiedByStaffId;

	private String commonStatusId;
	
	private Timestamp statusChangeDate;
	private String systemGenerated;
	private Timestamp lastSyncDate;
	private Timestamp lastSuccessSynDate;
	private String lastSyncStatus;
	private INetServerTypeData netServerType;
	private String javaHome;
	private String serverHome;
	private String isInSync;
	private NetServerStartupConfigData startupConfig;
	private Integer licenseExpiryDays;    
	private Date licenseCheckDate;
	
	/* Below properties is for REST only */
	private NetServiceData servicesListForAdd; 
	
	private NetServiceData serviceListForDelete;
	
	private NetServiceData servicesListForView;
	
	private String staff;
	
	public NetServerInstanceData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
	@XmlTransient
	public Integer getLicenseExpiryDays() {
		return licenseExpiryDays;
	}

	public void setLicenseExpiryDays(Integer licenseExpiryDays) {
		this.licenseExpiryDays = licenseExpiryDays;
	}

	@XmlTransient
	public Date getLicenseCheckDate() {
		return licenseCheckDate;
	}

	public void setLicenseCheckDate(Date licenseCheckDate) {
		this.licenseCheckDate = licenseCheckDate;
	}

	@XmlTransient
	public NetServerStartupConfigData getStartupConfig() {
		return startupConfig;
	}

	public void setStartupConfig( NetServerStartupConfigData startupConfig ) {
		this.startupConfig = startupConfig;
	}

	@XmlElement(name="server-home")
	public String getServerHome() {
		return serverHome;
	}

	public void setServerHome( String serverHome ) {
		this.serverHome = serverHome;
	}

	@XmlElement(name="java-home")
	public String getJavaHome() {
		return javaHome;
	}

	public void setJavaHome( String javaHome ) {
		this.javaHome = javaHome;
	}

	@XmlElement(name="server-identification")
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}
	
	@XmlTransient
	public String getNetServerCode() {
		return netServerCode;
	}
	public void setNetServerCode(String netServerCode) {
		this.netServerCode = netServerCode;
	}
	
	@XmlElement(name="server-instance-name")
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
	
	@XmlElement(name="version")
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	@XmlElement(name="server-type")
	@ValidServerType
	@XmlJavaTypeAdapter(ServerTypeAdapter.class)
	public String getNetServerTypeId() {
		return netServerTypeId;
	}
	public void setNetServerTypeId(String netServerTypeId) {
		this.netServerTypeId = netServerTypeId;
	}
	
	@XmlElement(name="admin-interface-ip")
	public String getAdminHost() {
		return adminHost;
	}
	public void setAdminHost(String adminHost) {
		this.adminHost = adminHost;
	}
	
	@XmlElement(name="admin-interface-port")
	public Integer getAdminPort() {
		return adminPort;
	}
	public void setAdminPort(Integer adminPort) {
		this.adminPort = adminPort;
	}
	
	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	
	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	
	@XmlTransient
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	
	@XmlTransient
	public Timestamp getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	
	@XmlTransient
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	
	@XmlTransient
	public Timestamp getLastSyncDate() {
		return lastSyncDate;
	}
	public void setLastSyncDate(Timestamp lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	
	@XmlTransient
	public Timestamp getLastSuccessSynDate() {
		return lastSuccessSynDate;
	}
	public void setLastSuccessSynDate(Timestamp lastSuccessSynDate) {
		this.lastSuccessSynDate = lastSuccessSynDate;
	}
	
	@XmlTransient
	public String getLastSyncStatus() {
		return lastSyncStatus;
	}
	public void setLastSyncStatus(String lastSyncStatus) {
		this.lastSyncStatus = lastSyncStatus;
	}
	
	@XmlTransient
	public INetServerTypeData getNetServerType() {
		return netServerType;
	}
	public void setNetServerType(INetServerTypeData netServerType) {
		this.netServerType = netServerType;
	}
	
	@XmlTransient
	public boolean isInSync( ) {
		if(isInSync != null && isInSync.equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID))
			return true;
		return false; 
	}
	
	@XmlTransient
	public String getIsInSync( ) {
		return isInSync;
	}
	public void setIsInSync( String isInSync ) {
		this.isInSync = isInSync;
	}

	@Override                                                        
	public String toString() {                                        
		StringWriter out = new StringWriter();                        
		PrintWriter writer = new PrintWriter(out);
		writer.println();                    
		writer.println("------------NetServerInstanceData-----------------");
		writer.println("netServerId=" +netServerId);                                     
		writer.println("netServerCode=" +netServerCode);                                     
		writer.println("name=" +name);                                     
		writer.println("description=" +description);                                     
		writer.println("version=" +version);                                     
		writer.println("netServerTypeId=" +netServerTypeId);                                     
		writer.println("adminHost=" +adminHost);                                     
		writer.println("adminPort=" +adminPort);                                     
		writer.println("createDate=" +createDate);                                     
		writer.println("createdByStaffId=" +createdByStaffId);                                     
		writer.println("lastModifiedDate=" +lastModifiedDate);                                     
		writer.println("lastModifiedByStaffId=" +lastModifiedByStaffId);                                     
		writer.println("commonStatusId=" +commonStatusId);                                     
		writer.println("statusChangeDate=" +statusChangeDate);                                     
		writer.println("systemGenerated=" +systemGenerated);                                     
		writer.println("lastSyncDate=" +lastSyncDate);                                     
		writer.println("lastSuccessSynDate=" +lastSuccessSynDate);                                     
		writer.println("lastSyncStatus=" +lastSyncStatus);                                     
		writer.println("netServerType=" +netServerType);                                     
		writer.println("javaHome=" +javaHome); 
		writer.println("serverHome"+(getServerHome().replaceAll("\\\\", "")));
		writer.println("isInSync=" +isInSync); 
		writer.println("----------------------------------------------------");
		writer.close();                                               
		return out.toString();
	}

	@XmlElement(name = "configured-services")
	public NetServiceData getServicesListForView() {
		return servicesListForView;
	}

	public void setServicesListForView(NetServiceData servicesListForView) {
		this.servicesListForView = servicesListForView;
	}

	@XmlElement(name = "add-services")
	@XmlJavaTypeAdapter(value=ServiceTypeAdaptor.class)
	@Valid
	public NetServiceData getServicesListForAdd() {
		return servicesListForAdd;
	}

	public void setServicesListForAdd(NetServiceData servicesList) {
		this.servicesListForAdd = servicesList;
	}
	
	@XmlElement(name = "remove-services")
	@XmlJavaTypeAdapter(value=ServiceTypeAdaptor.class)
	@Valid
	public NetServiceData getServiceListForDelete() {
		return serviceListForDelete;
	}

	public void setServiceListForDelete(NetServiceData serviceListForDelete) {
		this.serviceListForDelete = serviceListForDelete;
	}

	

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if(Strings.isNullOrBlank(this.staff)) {
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Staff name must be specified");
		} else {
			StaffBLManager blManager = new StaffBLManager();
			try{
				IStaffData staffData = blManager.getStaffDataByUserName(this.staff);
				if(staffData == null) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Invalid value of staff");
					return isValid;
				}
			}catch (DataManagerException de) {
				de.printStackTrace();
				RestUtitlity.setValidationMessage(context, de.getMessage());
			}
			
		}
		return isValid;
	}

	@Override
	@XmlElement(name="staff")
	public String getStaff() {
		return staff;
	}

	@Override
	public void setStaff(String staff) {
		this.staff = staff;
	}
}
