package com.elitecore.elitesm.datamanager.servermgr.drivers.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.DetailLocalAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.CrestelRatingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data.UserFileAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebServiceAuthDriverData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.SingleDriver;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


@XmlType(propOrder = { "name", "description", "dbdetail", "userFileDetail",	"dbacctset", "csvset", "mapGatewaySet", "ldapdetail", "httpAuthFieldMapSet" ,"webServiceAuthDriverSet" ,"hssDetail", "crestelChargingSet", "diameterChargingDriverSet","crestelRatingSet" })
@XmlRootElement(name="driver-instance")		
@SingleDriver
public class DriverInstanceData extends BaseData implements IDriverInstanceData,Serializable,Differentiable{
	
	public DriverInstanceData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
	private static final long serialVersionUID = 1L;
	private String driverInstanceId;	
	
	@Expose
	@SerializedName("Name")
	private String name;
	
	@Expose
	@SerializedName("Description")
	private String description;
	
	@Expose
	@SerializedName("Cacheable")
	private String cacheable;
	
	private String status;
	private String createdByStaffId;
	private String lastModifiedByStaffId;
	private Timestamp lastModifiedDate;
	private Timestamp createDate;
	private long driverTypeId;
	
	@Valid
	private Set<DBAuthDriverData> dbdetail;
	@Valid
	private Set<LDAPAuthDriverData> ldapdetail;
	@Valid
	private Set<UserFileAuthDriverData> userFileDetail;
	@Valid
	private Set<DetailLocalAcctDriverData> detaillocalset;
	private DriverTypeData driverTypeData;
	@Valid
	private Set<DBAcctDriverData> dbacctset;
	@Valid
	private Set<ClassicCSVAcctDriverData> csvset;
	@Valid
	private Set<CrestelRatingDriverData> crestelRatingSet;
	// for the purpose of displaying name when needed.....
	private String driverTypeName;
	@Valid
	private Set<MappingGatewayAuthDriverData> mapGatewaySet;
	@Valid
	private Set<DiameterChargingDriverData> diameterChargingDriverSet;
	@Valid
	private Set<WebServiceAuthDriverData> webServiceAuthDriverSet;
	@Valid
	private Set<CrestelChargingDriverData> crestelChargingSet;
	@Valid
	private Set<HttpAuthDriverData> httpAuthFieldMapSet;
	@Valid
	private Set<HssAuthDriverData> hssDetail;
	private String auditUId;
	
	@XmlElement(name = "http-auth-driver")
	public Set<HttpAuthDriverData> getHttpAuthFieldMapSet() {
		return httpAuthFieldMapSet;
	}
	
	public void setHttpAuthFieldMapSet(Set<HttpAuthDriverData> httpAuthFieldMapSet) {
		this.httpAuthFieldMapSet = httpAuthFieldMapSet;
	}
	
	@XmlTransient
	public String getCacheable() {
		return cacheable;
	}
	public void setCacheable(String cacheable) {
		this.cacheable = cacheable;
	}
	
	@XmlElement(name = "crestel-charging-driver")
	public Set<CrestelChargingDriverData> getCrestelChargingSet() {
		return crestelChargingSet;
	}
	public void setCrestelChargingSet(Set<CrestelChargingDriverData> crestelChargingSet) {
		this.crestelChargingSet = crestelChargingSet;
	}
	
	@XmlElement(name = "web-service-auth-driver")
	public Set<WebServiceAuthDriverData> getWebServiceAuthDriverSet() {
		return webServiceAuthDriverSet;
	}
	public void setWebServiceAuthDriverSet(Set<WebServiceAuthDriverData> webServiceAuthDriverSet) {
		this.webServiceAuthDriverSet = webServiceAuthDriverSet;
	}
	
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	@XmlElement(name = "diameter-charging-driver")
	public Set<DiameterChargingDriverData> getDiameterChargingDriverSet() {
		return diameterChargingDriverSet;
	}
	public void setDiameterChargingDriverSet(Set<DiameterChargingDriverData> diameterChargingDriverSet) {
		this.diameterChargingDriverSet = diameterChargingDriverSet;
	}
	@XmlElement(name = "map-gateway-auth-driver")
	public Set<MappingGatewayAuthDriverData> getMapGatewaySet() {
		return mapGatewaySet;
	}
	public void setMapGatewaySet(Set<MappingGatewayAuthDriverData> mapGatewaySet) {
		this.mapGatewaySet = mapGatewaySet;
	}
	@XmlElement(name = "crestel-rating-driver")
	public Set<CrestelRatingDriverData> getCrestelRatingSet() {
		return crestelRatingSet;
	}
	public void setCrestelRatingSet(Set<CrestelRatingDriverData> crestelRatingSet) {
		this.crestelRatingSet = crestelRatingSet;
	}
	
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@NotEmpty(message = "Driver name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message=RestValidationMessages.NAME_INVALID)
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
	
	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
		
	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	@XmlTransient
	public long getDriverTypeId() {
		return driverTypeId;
	}
	public void setDriverTypeId(long driverTypeId) {
		this.driverTypeId = driverTypeId;
	}
	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return this.lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	@XmlTransient
	public String getDriverTypeName() {
		return driverTypeName;
	}
	public void setDriverTypeName(String driverTypeName) {
		this.driverTypeName = driverTypeName;
	}

	@XmlElement(name = "db-auth-driver")
	public Set<DBAuthDriverData> getDbdetail() {
		return dbdetail;
	}
	public void setDbdetail(Set<DBAuthDriverData> dbdetail) {
		this.dbdetail = dbdetail;
	}
	
	@XmlElement(name = "ldap-auth-driver")
	public Set<LDAPAuthDriverData> getLdapdetail() {
		return ldapdetail;
	}
	public void setLdapdetail(Set<LDAPAuthDriverData> ldapdetail) {
		this.ldapdetail = ldapdetail;
	}
	
	@XmlElement(name = "user-file-auth-driver")
	public Set<UserFileAuthDriverData>  getUserFileDetail() {
		return userFileDetail;
	}
	public void setUserFileDetail(Set<UserFileAuthDriverData> userFileDetail) {
		this.userFileDetail = userFileDetail;
	}
	
	@XmlTransient
	public DriverTypeData getDriverTypeData() {
		return driverTypeData;
	}
	public void setDriverTypeData(DriverTypeData driverTypeData) {
		this.driverTypeData = driverTypeData;
	}
	@XmlTransient
	public Set getDetaillocalset() {
		return detaillocalset;
	}
	public void setDetaillocalset(Set detaillocalset) {
		this.detaillocalset = detaillocalset;
	}

	@XmlElement(name = "db-acct-driver")
	public Set<DBAcctDriverData> getDbacctset() {
		return dbacctset;
	}
	public void setDbacctset(Set<DBAcctDriverData> dbacctset) {
		this.dbacctset = dbacctset;
	}
	@XmlElement(name = "classic-csv-acct-driver")
	public Set<ClassicCSVAcctDriverData> getCsvset() {
		return csvset;
	}
	public void setCsvset(Set<ClassicCSVAcctDriverData> csvset) {
		this.csvset = csvset;
	}
	
	@XmlElement(name = "hss-auth-driver")
	public Set<HssAuthDriverData> getHssDetail() {
		return hssDetail;
	}

	public void setHssDetail(Set<HssAuthDriverData> hssDetail) {
		this.hssDetail = hssDetail;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Instance Name", name);
		object.put("Description", description);
//		if(driverTypeData!=null){
//			object.put("Database Name", driverTypeData.getDisplayName());
//		}
		object.put("Cacheable", cacheable);
		if(Collectionz.isNullOrEmpty(dbdetail) == false) {
			for (DBAuthDriverData element : dbdetail) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(ldapdetail) == false){
			for (LDAPAuthDriverData element : ldapdetail) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(userFileDetail) == false){
			for (UserFileAuthDriverData element : userFileDetail) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(detaillocalset) == false){
			for (DetailLocalAcctDriverData element : detaillocalset) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(dbacctset) == false){
			for (DBAcctDriverData element : dbacctset) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(csvset) == false ){
			for (ClassicCSVAcctDriverData element : csvset) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(crestelRatingSet) == false ){
			for (CrestelRatingDriverData element : crestelRatingSet) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(mapGatewaySet) == false ){
			for (MappingGatewayAuthDriverData element : mapGatewaySet) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(diameterChargingDriverSet) == false ){
			for (DiameterChargingDriverData element : diameterChargingDriverSet) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(webServiceAuthDriverSet) == false ){
			for (WebServiceAuthDriverData element : webServiceAuthDriverSet) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(crestelChargingSet) == false ){
			for (CrestelChargingDriverData element : crestelChargingSet) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(httpAuthFieldMapSet) == false ){
			for (HttpAuthDriverData element : httpAuthFieldMapSet) {
				object.putAll(element.toJson());
			}
			return object;
		}
		if(Collectionz.isNullOrEmpty(hssDetail)  == false ){
			for (HssAuthDriverData element : hssDetail) {
				object.putAll(element.toJson());
			}
		}
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

