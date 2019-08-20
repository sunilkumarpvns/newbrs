package com.elitecore.elitesm.datamanager.servermgr.script.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.ws.rest.adapter.ScriptTypeAdapter;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Contains;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlType(propOrder = { "name", "description", "status", "scriptTypeId", "scriptFiles" })
@XmlRootElement(name = "script")
public class ScriptInstanceData extends BaseData implements Serializable, Differentiable {

	private static final long serialVersionUID = 1L;

	private String scriptId;

	@Expose
	@SerializedName("Instance Name")
	@NotEmpty(message="Script name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX,message=RestValidationMessages.NAME_INVALID)
	private String name;

	@Expose
	@SerializedName("Description")
	private String description;

	@NotEmpty(message = "Script Status must be specified")
    @Pattern(regexp = "CST01|CST02", message = "Invalid value of Status parameter. Value could be 'ACTIVE' or 'INACTIVE'.")
	private String status;
		
	private String createdByStaffId;
	private String lastModifiedByStaffId;
	private Timestamp lastModifiedDate;
	private Timestamp createDate;
	
	@NotEmpty(message = "Script Type must be specified. It can be Driver Script, Translation Mapping Script, External Radius Script or Diameter Router Script.")
	@Contains(allowedValues = {ScriptTypesConstants.DRIVER_SCRIPT, ScriptTypesConstants.TRANSLATION_MAPPING_SCRIPT, ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT, ScriptTypesConstants.DIAMETER_ROUTER_SCRIPT}, invalidMessage = "Invalid Script Type. It can be Driver Script, Translation Mapping Script, External Radius Script or Diameter Router Script.")
	private String scriptTypeId;

	// for the purpose of displaying name when needed.....
	private String auditUId;
	private ScriptTypeData scriptTypeData;
	private List<ScriptData> scriptDataList;
	private String scriptTypeName;
	
	private List<String> scriptFiles;
	
	public ScriptInstanceData() {
		description = RestUtitlity.getDefaultDescription();
	}

	@XmlTransient
	public String getScriptId() {
		return scriptId;
	}
	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}

	@XmlElement(name = "script-type")
	@XmlJavaTypeAdapter(ScriptTypeAdapter.class)
	public String getScriptTypeId() {
		return scriptTypeId;
	}
	public void setScriptTypeId(String scriptTypeId) {
		this.scriptTypeId = scriptTypeId;
	}

	@XmlElement(name = "script-name")
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
	
	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(StatusAdapter.class)
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

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	@XmlTransient
	public ScriptTypeData getScriptTypeData() {
		return scriptTypeData;
	}
	public void setScriptTypeData(ScriptTypeData scriptTypeData) {
		this.scriptTypeData = scriptTypeData;
	}

	@XmlTransient
	public List<ScriptData> getScriptDataList() {
		return scriptDataList;
	}
	public void setScriptDataList(List<ScriptData> scriptDataList) {
		this.scriptDataList = scriptDataList;
	}
	
	@XmlTransient
	public String getScriptTypeName() {
		return scriptTypeName;
	}
	public void setScriptTypeName(String scriptTypeName) {
		this.scriptTypeName = scriptTypeName;
	}

	@XmlElementWrapper(name = "script-files")
	@XmlElement(name = "script-filename")
	public List<String> getScriptFiles() {
		return scriptFiles;
	}

	public void setScriptFiles(List<String> scriptFiles) {
		this.scriptFiles = scriptFiles;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Script Name", name);
		object.put("Description", description);
		object.put("Status", status);

		if(Collectionz.isNullOrEmpty(scriptDataList) == false) {
			for(ScriptData data : scriptDataList) {
				object.putAll(data.toJson());
			}
		}
		return object;
	}
}