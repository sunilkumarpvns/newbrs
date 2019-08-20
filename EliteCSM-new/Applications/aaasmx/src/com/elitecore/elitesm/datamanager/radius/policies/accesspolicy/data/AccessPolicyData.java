package com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data;

import java.io.StringWriter;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.base.data.CommonStatusData;
import com.elitecore.elitesm.util.constants.AccessPolicyConstant;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.DefaultAccessAdapter;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Contains;

import net.sf.json.JSONObject;

@XmlRootElement(name = "access-policy")
@XmlType(propOrder = { "name", "commonStatusId", "accessStatus", "description", "accessPolicyDetailDataList" })
public class AccessPolicyData extends BaseData implements IAccessPolicyData, Differentiable {

	private String accessPolicyId;

	@NotEmpty(message = "Access Policy name must be specified ")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;

	private String description;
	private String assigned;
	
	@Contains(allowedValues = { BaseConstant.SHOW_STATUS_ID, BaseConstant.HIDE_STATUS_ID}, invalidMessage = "Invalid value of status. Value could be 'ACTIVE' or 'INACTIVE' ", nullMessage = "Status must be specified. Value could be 'ACTIVE' or 'INACTIVE' ")
	private String commonStatusId;
	
	private java.sql.Timestamp lastUpdated;
	private java.sql.Timestamp statusChangeDate;
	private CommonStatusData commonStatus;
	
	public AccessPolicyData() {
		description = RestUtitlity.getDefaultDescription();
	}

	@Valid
	private List<AccessPolicyDetailData> accessPolicyDetailDataList;

	private String systemGenerated;
	
	@Contains(allowedValues = { AccessPolicyConstant.ALLOWED_VALUE, AccessPolicyConstant.DENIED_VALUE}, invalidMessage = "Invalid value of default access. Value could be 'ALLOWED' or 'DENIED' ", nullMessage = "Default access must be specified. Value could be 'ALLOWED' or 'DENIED' ")
	private String accessStatus;

	@XmlTransient
	public String getAccessPolicyId() {
		return accessPolicyId;
	}

	public void setAccessPolicyId(String accessPolicyId) {
		this.accessPolicyId = accessPolicyId;
	}

	@XmlTransient
	public String getAssigned() {
		return assigned;
	}

	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}

	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(StatusAdapter.class)
	public String getCommonStatusId() {
		return commonStatusId;
	}

	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	public java.sql.Timestamp getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(java.sql.Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public CommonStatusData getCommonStatus() {
		return commonStatus;
	}

	public void setCommonStatus(CommonStatusData commonStatus) {
		this.commonStatus = commonStatus;
	}

	@XmlTransient
	public String getSystemGenerated() {
		return systemGenerated;
	}

	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	
	@XmlElementWrapper(name="timeslaps")
	@XmlElement(name = "timeslap", type = AccessPolicyDetailData.class)
	public List getAccessPolicyDetailDataList() {
		return accessPolicyDetailDataList;
	}

	public void setAccessPolicyDetailDataList(List accessPolicyDetailDataList) {
		this.accessPolicyDetailDataList = accessPolicyDetailDataList;
	}

	@XmlElement(name = "default-access")
	@XmlJavaTypeAdapter(DefaultAccessAdapter.class)
	public String getAccessStatus() {
		return accessStatus;
	}

	public void setAccessStatus(String accessStatus) {
		this.accessStatus = accessStatus;
	}

	@XmlTransient
	public java.sql.Timestamp getStatusChangeDate() {
		return statusChangeDate;
	}

	public void setStatusChangeDate(java.sql.Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();

		writer.println("Name :" + name);
		writer.println("Description :" + description);
		writer.println("Assigned :" + assigned);
		writer.println("Common Status Id :" + commonStatusId);
		writer.println("LastUpdated :" + lastUpdated);
		writer.println("StatusChangeDate :" + statusChangeDate);
		writer.println("CommonStatus :" + commonStatus);
		writer.println("Access Policy Detail Data List :" + accessPolicyDetailDataList);
		writer.println("System Generated :" + systemGenerated);
		writer.println("Access Status :" + accessStatus);

		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Status", (commonStatusId.equals(BaseConstant.HIDE_STATUS_ID)) ? "Inactive" : "Active");
		object.put("Default Access", (accessStatus.equals(AccessPolicyConstant.ALLOWED_VALUE)) ? "Allowed" : "Denied");
		object.put("Description", description);
		if (accessPolicyDetailDataList != null) {
			JSONObject fields = new JSONObject();
			for (AccessPolicyDetailData element : accessPolicyDetailDataList) {
				fields.putAll(element.toJson());
			}
			object.put("Denied Timeslap", fields);
		}
		return object;
	}
}
