package com.elitecore.corenetvertex.sm.alerts;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.RollingType;
import com.elitecore.corenetvertex.constants.TimeBasedRollingUnit;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "com.elitecore.corenetvertex.sm.alerts.AlertListenerData")
@Table(name = "TBLM_ALERT_LISTENER_INSTANCE")
public class AlertListenerData  extends DefaultGroupResourceData implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String FLOOD_CONTROL = "Flood Control";

	private String name;
	private String type;
	private String fileName;
	private Integer rollingType;
	private Long rollingUnit;
	private Long maxRollingUnit;
	private Boolean compRollingUnit;
	private String trapServer;
	private String trapVersion;
	private String community;
	private Boolean advanceTrap;
	private String snmpRequestType;
	private Integer timeOut;
	private Byte retryCount;
	private transient List<AlertListenerRelData> alertListenerRelDataList;

	public AlertListenerData() {
		alertListenerRelDataList = new ArrayList<>();
	}

	@Column(name="NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name="FILE_NAME")
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name="ROLLING_TYPE")
	public Integer getRollingType() {
		return rollingType;
	}

	public void setRollingType(Integer rollingType) {
		this.rollingType = rollingType;
	}

	@Column(name="ROLLING_UNIT")
	public Long getRollingUnit() {
		return rollingUnit;
	}

	public void setRollingUnit(Long rollingUnit) {
		this.rollingUnit = rollingUnit;
	}

	@Column(name="MAX_ROLLING_UNIT")
	public Long getMaxRollingUnit() {
		return maxRollingUnit;
	}

	public void setMaxRollingUnit(Long maxRollingUnit) {
		this.maxRollingUnit = maxRollingUnit;
	}

	@Column(name="COMP_ROLLING_UNIT")
	public Boolean getCompRollingUnit() {
		return compRollingUnit;
	}

	public void setCompRollingUnit(Boolean compRollingUnit) {
		this.compRollingUnit = compRollingUnit;
	}

	@Column(name="TRAP_SERVER")
	public String getTrapServer() {
		return trapServer;
	}

	public void setTrapServer(String trapServer) {
		this.trapServer = trapServer;
	}

	@Column(name="TRAP_VERSION")
	public String getTrapVersion() {
		return trapVersion;
	}

	public void setTrapVersion(String trapVersion) {
		this.trapVersion = trapVersion;
	}

	@Column(name="COMMUNITY")
	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	@Column(name="ADVANCE_TRAP")
	public Boolean getAdvanceTrap() {
		return advanceTrap;
	}

	public void setAdvanceTrap(Boolean advanceTrap) {
		this.advanceTrap = advanceTrap;
	}

	@Column(name="SNMP_REQUEST_TYPE")
	public String getSnmpRequestType() {
		return snmpRequestType;
	}


	public void setSnmpRequestType(String snmpRequestType) {
		this.snmpRequestType = snmpRequestType;
	}

	@Column(name="TIME_OUT")
	public Integer getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}

	@Column(name="RETRY_COUNT")
	public Byte getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Byte retryCount) {
		this.retryCount = retryCount;
	}

	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "INSTANCE_ID")
	public List<AlertListenerRelData> getAlertListenerRelDataList() {
		return alertListenerRelDataList;
	}

	public void setAlertListenerRelDataList(List<AlertListenerRelData> alertListenerRelDataList) {
		this.alertListenerRelDataList = alertListenerRelDataList;
	}



	@Override
	@Column(name="STATUS")
	public String getStatus() {
		return super.getStatus();
	}

    @Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("Name", name);
		jsonObject.addProperty("Type",type);
		if(Strings.isNullOrBlank(getType())){
			return jsonObject;
		}
		if(AlertTypes.FILE.name().equalsIgnoreCase(getType())) {
			jsonObject.addProperty("File Name", fileName);
			jsonObject.addProperty("Rolling Type", getRollingTypeDisplayValue());
			jsonObject.addProperty("Rolling Unit", getRollingUnitDisplayValue());
			jsonObject.addProperty("Max Rolled Unit", maxRollingUnit);
			jsonObject.addProperty("Compress Rolled Unit", com.elitecore.corenetvertex.constants.CommonStatusValues.fromBooleanValue(compRollingUnit).getStringName());
			if (Collectionz.isNullOrEmpty(alertListenerRelDataList) == false) {
				jsonObject.add("Configured Alerts", createAlertListnerRelJsonObject());
			}
			return jsonObject;
		}
		if(AlertTypes.TRAP.name().equalsIgnoreCase(getType())) {
			jsonObject.addProperty("Trap Server", trapServer);
			jsonObject.addProperty("Trap version", trapVersion);
			jsonObject.addProperty("SNMP Request Type", snmpRequestType);
			jsonObject.addProperty("Advance Trap", com.elitecore.corenetvertex.constants.CommonStatusValues.fromBooleanValue(advanceTrap).getStringName());
			jsonObject.addProperty("Community", community);
			jsonObject.addProperty("Time Out", timeOut);
			jsonObject.addProperty("Retry Count", retryCount);
			if (Collectionz.isNullOrEmpty(alertListenerRelDataList) == false) {
				jsonObject.add("Configured Alerts", createAlertListnerRelJsonObject());
			}
			return jsonObject;
		}
		return jsonObject;
	}

	private JsonObject createAlertListnerRelJsonObject() {

		JsonObject alertListnerRelJsonObject = new JsonObject();

		for (AlertListenerRelData alertListenerRelData : alertListenerRelDataList) {
			JsonObject floodControl = new JsonObject();
			floodControl.addProperty(FLOOD_CONTROL, alertListenerRelData.isFloodControl()? CommonStatusValues.ENABLE.getStringName() : CommonStatusValues.DISABLE.getStringName());
			alertListnerRelJsonObject.add(alertListenerRelData.from().getName(), floodControl);
		}

		return alertListnerRelJsonObject;
	}


	@JsonIgnore
	@Transient
	public String getRollingTypeDisplayValue() {
		if(AlertTypes.FILE.name().equalsIgnoreCase(getType())) {
          return RollingType.fromValue(rollingType).label;
		}
		return null;
	}



	@JsonIgnore
	@Transient
	public String getRollingUnitDisplayValue() {
		if (RollingType.TIME_BASED.value == rollingType) {
		  return TimeBasedRollingUnit.fromValue(String.valueOf(rollingUnit)).unit ;
		}
		return String.valueOf(rollingUnit);
	}


}
