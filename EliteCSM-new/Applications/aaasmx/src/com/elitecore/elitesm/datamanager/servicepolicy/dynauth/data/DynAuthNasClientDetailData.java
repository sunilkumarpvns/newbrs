package com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data;

import java.io.Serializable;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.util.constants.DynauthServicePolicyConstants;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.RadiusESINameByIdAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "nas-client")
@XmlType(propOrder = {"esiInstanceId", "loadFactor"})
@ValidObject
public class DynAuthNasClientDetailData extends BaseData implements Serializable,Differentiable,Validator{
	
	private static final long serialVersionUID = 1L;
	private String nasClientDetailId;
	
	private String esiInstanceId;
	private Long loadFactor;
	private String dynaAuthNasId;
	private ExternalSystemInterfaceInstanceData externalSystemInstanceData;
	
	public DynAuthNasClientDetailData() {
		this.loadFactor = DynauthServicePolicyConstants.LOAD_FACTOR;
	}
	
	@XmlTransient
	public String getNasClientDetailId() {
		return nasClientDetailId;
	}

	public void setNasClientDetailId(String nasClientDetailId) {
		this.nasClientDetailId = nasClientDetailId;
	}

	@XmlElement(name = "server-name")
	@NotEmpty(message="ESI name must be specified")
	@XmlJavaTypeAdapter(RadiusESINameByIdAdapter.class)
	public String getEsiInstanceId() {
		return esiInstanceId;
	}

	public void setEsiInstanceId(String esiInstanceId) {
		this.esiInstanceId = esiInstanceId;
	}
	
	@XmlElement(name = "load-factor")
	public Long getLoadFactor() {
		return loadFactor;
	}

	public void setLoadFactor(Long loadFactor) {
		this.loadFactor = loadFactor;
	}

	@XmlTransient
	public String getDynaAuthNasId() {
		return dynaAuthNasId;
	}

	public void setDynaAuthNasId(String dynaAuthNasId) {
		this.dynaAuthNasId = dynaAuthNasId;
	}

	@Override
	public String toString() {
		return "DynAuthNasClientDetailData [nasClientDetailId="
				+ nasClientDetailId + ", esiInstanceId=" + esiInstanceId
				+ ", loadFactor=" + loadFactor + ", dynaAuthNasId="
				+ dynaAuthNasId + "]";
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("LoadFactor", loadFactor);
		object.put("Server Name", getExternalSystemInstanceData().getName());
		return object;
	}

	@XmlTransient
	public ExternalSystemInterfaceInstanceData getExternalSystemInstanceData() {
		return externalSystemInstanceData;
	}

	public void setExternalSystemInstanceData(ExternalSystemInterfaceInstanceData externalSystemInstanceData) {
		this.externalSystemInstanceData = externalSystemInstanceData;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if(RestValidationMessages.INVALID.equals(this.esiInstanceId)){
			isValid = false;
			RestUtitlity.setValidationMessage(context, "External System Interface data not found");
		}
		return isValid;
	}
}
