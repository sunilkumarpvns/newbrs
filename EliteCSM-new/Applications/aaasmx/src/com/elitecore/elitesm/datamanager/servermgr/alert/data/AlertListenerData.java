package com.elitecore.elitesm.datamanager.servermgr.alert.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.AlertListenerConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.serverconfig.alertconfiguration.AlertConfigurationConstant;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "alert-listener-data")
@XmlType(propOrder = {"name", "alertFileListenerData", "alertTrapListenerData", "alertSysLogAlertListenerData", "alertList"})
@ValidObject
public class AlertListenerData extends BaseData implements Differentiable, Serializable , Validator {
	
	private static final long serialVersionUID = 1L;
	private String listenerId;
	
	@Expose
	@SerializedName("Name")
	@NotEmpty(message = "Alert Listener Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	
	@Expose
	@SerializedName("Listener Type")
	private String typeId;
	
	@Valid
	private AlertFileListenerData alertFileListenerData;
	
	@Valid
	private AlertTrapListenerData alertTrapListenerData;
	
	private AlertListenerTypeData alertListenerTypeData;
	
	@Valid
	private SYSLogAlertListenerData alertSysLogAlertListenerData;
	private List<AlertListenerRelData> alertListenerRelDataList;
	private BaseAlertListener alertListener;
	private String auditUId;
	
	//for rest purpose 
	 private List<String> alertList;

	public AlertListenerData() {
		super();
		alertList = new LinkedList<String>();
	}
	
	
	@XmlElementWrapper(name = "enable")
	@XmlElement(name = "alert-name", type = String.class)
	public List<String> getAlertList() {
		return alertList;
	}
	
	public void setAlertList(List<String> names) {
		this.alertList = names;
	}
	
	@XmlTransient
	public List<AlertListenerRelData> getAlertListenerRelDataList() {
		return alertListenerRelDataList;
	}
	public void setAlertListenerRelDataList(
			List<AlertListenerRelData> alertListenerRelDataSet) {
		this.alertListenerRelDataList = alertListenerRelDataSet;
	}
	
	@XmlTransient
	public String getListenerId() {
		return listenerId;
	}
	public void setListenerId(String listenerId) {
		this.listenerId = listenerId;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlTransient
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	
	@XmlElement(name = "file-listener")
	public AlertFileListenerData getAlertFileListenerData() {
		return alertFileListenerData;
	}
	public void setAlertFileListenerData(AlertFileListenerData alertFileListenerData) {
		this.alertFileListenerData = alertFileListenerData;
	}
	
	@XmlElement(name = "trap-listener")
	public AlertTrapListenerData getAlertTrapListenerData() {
		return alertTrapListenerData;
	}
	public void setAlertTrapListenerData(AlertTrapListenerData alertTrapListenerData) {
		this.alertTrapListenerData = alertTrapListenerData;
	}
	
	@XmlTransient
	public AlertListenerTypeData getAlertListenerTypeData() {
		return alertListenerTypeData;
	}
	public void setAlertListenerTypeData(AlertListenerTypeData alertListenerTypeData) {
		this.alertListenerTypeData = alertListenerTypeData;
	}
	
	@XmlTransient
	public BaseAlertListener getAlertListener() {
		return alertListener;
	}
	public void setAlertListener(BaseAlertListener alertListener) {
		this.alertListener = alertListener;
	}
	
	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	@XmlElement(name = "sys-log-listener")
	public SYSLogAlertListenerData getAlertSysLogAlertListenerData() {
		return alertSysLogAlertListenerData;
	}
	public void setAlertSysLogAlertListenerData(
			SYSLogAlertListenerData alertSysLogAlertListenerData) {
		this.alertSysLogAlertListenerData = alertSysLogAlertListenerData;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object=new JSONObject();
		object.put("Name", name);
		object.put("Listener Type", typeId); //TODO : check again -->based on listener type
		if(alertListener!=null){
			if(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(typeId)){
				alertFileListenerData = (AlertFileListenerData) alertListener;
			}else if(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(typeId)){
				alertTrapListenerData = (AlertTrapListenerData) alertListener;
			}else if(AlertListenerConstant.SYS_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(typeId)){
				alertSysLogAlertListenerData = (SYSLogAlertListenerData) alertListener;
			}else{
				alertFileListenerData = null;
				alertTrapListenerData = null;
			}
		}
		if(alertFileListenerData !=null){
			object.put("File Listener", alertFileListenerData.toJson());
		}
		if(alertTrapListenerData != null){
			object.put("Trap Listener", alertTrapListenerData.toJson());
		}
		if(alertSysLogAlertListenerData != null){
			object.put("SysLog Listener", alertSysLogAlertListenerData.toJson());
		}
		if(alertListenerRelDataList != null){
			JSONArray array = new JSONArray();
			for(AlertListenerRelData element:alertListenerRelDataList){
				array.add(EliteSMReferencialDAO.fetchAlertTypeData(element.getTypeId()));
			}
			object.put("Alerts List",array);
		}
	
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		if (validateAlertList(context)) {
			return checkDuplicationInAlertNameList(context) ;
		}
		return true;
	}

	private boolean validateAlertList(ConstraintValidatorContext context) {
		AlertListenerBLManager alertListenerBLManager =  new AlertListenerBLManager();
		try {
			List<AlertTypeData> allAlertData = alertListenerBLManager.getAlertTypeDataList();

			Map<String,	String> mappingData = getNameToIdData(allAlertData);

			List<AlertListenerRelData> alertListenerRelDataList = new ArrayList<AlertListenerRelData>();
			List<String> alertNameList = alertList;
			for (String relData : alertNameList) {
				if (Strings.isNullOrEmpty(relData)) {
					RestUtitlity.setValidationMessage(context, "Invalid alert-name. It can not be empty.");
					return false;
				}
				AlertListenerRelData data = new AlertListenerRelData();
				if (AlertConfigurationConstant.Generics.getGenericId(relData) != null) {
					data.setTypeId(AlertConfigurationConstant.Generics.getGenericId(relData));
				} else if (mappingData.containsKey(relData)) {
					data.setTypeId(mappingData.get(relData));
				} else {
					RestUtitlity.setValidationMessage(context, "Invalid alert-name value: " + relData);
					return false;
				}
				alertListenerRelDataList.add(data);
			}
			
			if (this.getAlertListenerRelDataList() == null) {
			setAlertListenerRelDataList(alertListenerRelDataList);
			}

		} catch (DataManagerException e) {
			e.printStackTrace();
		}
		return true;
	}


	private boolean checkDuplicationInAlertNameList(ConstraintValidatorContext context) {
		Map<String, Integer> nameAndCount = new HashMap<String, Integer>();
		for (String alerts : alertList) {
			nameAndCount.put(alerts, 0);
		}

		int noOfDuplicateElement = 0;
		List<String> duplicateValues = new ArrayList<String>();

		for (String name : alertList) {
			Integer count = nameAndCount.get(name);
			if (count == 0) {
				nameAndCount.put(name, 1);
			} else {
				noOfDuplicateElement++;
				nameAndCount.put(name, ++count);
			}
		}

		if (noOfDuplicateElement > 0) {
			Set<Entry<String, Integer>> entrySet = nameAndCount.entrySet();
			for (Entry<String, Integer> entry : entrySet) {

				if (entry.getValue() > 1) {
					duplicateValues.add(entry.getKey());
				}
			}

			RestUtitlity.setValidationMessage(context, "Duplicate alert-name is not allowed. Duplicate values are : " + duplicateValues.toString());
			return false;
		}

		return true;
	}
	
	private Map<String, String> getNameToIdData(List<AlertTypeData> allAlertData) {
		Map<String,	String> mappingData = new LinkedHashMap<String, String>();
		for (AlertTypeData data : allAlertData) {
			if (AlertConfigurationConstant.LEAF_NODE.equals(data.getType()) && data.getName().equals(AlertConfigurationConstant.GENERIC) == false) {
				mappingData.put(data.getName(), data.getAlertTypeId());
			}
		}
		return mappingData;
	}
}
