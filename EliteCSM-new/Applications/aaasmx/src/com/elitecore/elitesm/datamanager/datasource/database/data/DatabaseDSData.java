package com.elitecore.elitesm.datamanager.datasource.database.data;

import java.io.StringWriter;
import java.sql.Timestamp;

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
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.NumericAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlRootElement(name="database-datasource")
@XmlType(propOrder = {"name","connectionUrl","userName","password","timeout","statusCheckDuration","minimumPool","maximumPool"})
public class DatabaseDSData extends BaseData implements IDatabaseDSData,Cloneable,Differentiable{
	
	private String databaseId;
	
	@Expose
	@SerializedName("Name")
	@NotEmpty(message="Datasource name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX,message=RestValidationMessages.NAME_INVALID)
	private String name;
	
	@Expose
	@SerializedName("Connection Url")
	@NotEmpty(message="Connection Url name must be specified")
    private String connectionUrl;
	
	@Expose
	@SerializedName("User Name")
	@NotEmpty(message="Username must be specified")
    private String userName;
	
	@NotEmpty(message="Password must be specified")
    private String password;
	
    @Expose
    @SerializedName("Timeout (ms)")
    @NotNull(message="Timeout(ms) must be specified")
    @Min(value=0,message="Timeout value must be numeric")
    private Long timeout;
    
    @Expose
    @SerializedName("Status Check Duration (Sec.)")
    @NotNull(message="Status Check Duration(Sec) must be specified")
    @Min(value=0,message="Status Check Duration value must be numeric")
    private Long statusCheckDuration;
    
    @Expose
	@SerializedName("Minimum Connection")
    @NotNull(message="Minimum Pool must be specified")
    @Min(value=0,message="Minimum Pool value must be numeric")
    private Long minimumPool;
	
    @Expose
	@SerializedName("Maximum Connection")
    @NotNull(message="Maximum Pool must be specified")
    @Min(value=0,message="Maximum Pool value must be numeric")
    private Long maximumPool;
   
    private String lastmodifiedByStaffId;
    private String createdByStaffId;
    private Timestamp lastmodifiedDate;
    private Timestamp createDate;
	private String auditUId;
	
	@XmlElement(name = "connection-url")
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	@XmlElement(name = "username")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@XmlElement(name = "password")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlElement(name = "minimum-pool-size")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	public Long getMinimumPool() {
		return minimumPool;
	}
	public void setMinimumPool(Long minimumPool) {
		this.minimumPool = minimumPool;
	}
	
	@XmlElement(name = "maximum-pool-size")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	public Long getMaximumPool() {
		return maximumPool;
	}
	public void setMaximumPool(Long maximumPool) {
		this.maximumPool = maximumPool;
	}
	
	@XmlTransient
	public String getLastmodifiedByStaffId() {
		return lastmodifiedByStaffId;
	}
	
	public void setLastmodifiedByStaffId(String lastmodifiedByStaffId) {
		this.lastmodifiedByStaffId = lastmodifiedByStaffId;
	}
	
	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	
	@XmlTransient
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlTransient
	public Timestamp getLastmodifiedDate() {
		return lastmodifiedDate;
	}
	public void setLastmodifiedDate(Timestamp lastmodifiedDate) {
		this.lastmodifiedDate = lastmodifiedDate;
	}
	
	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
	@XmlElement(name = "timeout")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	
	@XmlElement(name = "status-check-duration")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
        
	@Override
    public String toString() {
        StringWriter out = new StringWriter();
        TabbedPrintWriter writer = new TabbedPrintWriter(out);
        writer.print(StringUtility.fillChar("", 30, '-'));
        writer.print(this.getClass().getName());
        writer.println(StringUtility.fillChar("", 30, '-'));
        writer.incrementIndentation();
        writer.println("Database Id               :"+databaseId);
        writer.println("Name                 :" + name);
        writer.println("Connection Url      :" + connectionUrl);
        writer.println("Username :" + userName);
        writer.println("Password       :" + password);
        writer.println("Minimum Pool    :" + minimumPool);
        writer.println("maximumPool                   :"+maximumPool);
        writer.println("Create Date               :" + createDate);
        writer.println("Timeout               :" + timeout);
        writer.println("StatusCheckDuration               :" + statusCheckDuration);
        
        writer.decrementIndentation();
        writer.println(StringUtility.fillChar("", 80, '-'));
        writer.close();
        return out.toString();
    }
	public Object clone() throws CloneNotSupportedException {
		DatabaseDSData databaseDSData = (DatabaseDSData) super.clone() ;
		if(this.lastmodifiedDate != null)
			databaseDSData.lastmodifiedDate = new Timestamp(this.lastmodifiedDate.getTime());
		if(this.createDate != null)
			databaseDSData.createDate = new Timestamp(this.createDate.getTime());
		databaseDSData.timeout = new Long(this.timeout);
		databaseDSData.statusCheckDuration = new Long(this.statusCheckDuration);
		
		return databaseDSData;
		
	}
	
	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Datasource Name", name);
		object.put("Connection Url", connectionUrl);
		object.put("User Name", userName);
		object.put("Password", password);
		object.put("Timeout (ms)", timeout);
		object.put("Status Check Duration (Sec.)", statusCheckDuration);
		object.put("Minimum Connection", minimumPool);
		object.put("Maximum Connection", maximumPool);
		return object;
	}
}
