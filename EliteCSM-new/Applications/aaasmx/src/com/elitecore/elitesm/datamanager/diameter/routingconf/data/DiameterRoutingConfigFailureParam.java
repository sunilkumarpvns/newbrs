package com.elitecore.elitesm.datamanager.diameter.routingconf.data;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.util.constants.DiameterRoutingTableConstant;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.adapter.diameterroutingtable.FailureActionAdapter;

import net.sf.json.JSONObject;

@XmlRootElement(name = "failure-action-data")
@XmlType(propOrder = { "errorCodes", "failureAction", "failureArgument" })
public class DiameterRoutingConfigFailureParam extends BaseData implements Differentiable{
	
	public DiameterRoutingConfigFailureParam() {
		failureArgument = DiameterRoutingTableConstant.FAILURE_ARGUMENT;
	}
	
	private String parameterId;
	
	@Pattern(regexp = "(\\d+([,;]\\d+)*)*", message = "Error Code must be comma or semicolon seperated numeric value.")
	private String errorCodes;
	
	private Short  failureAction;
	private String failureArgument;
	private DiameterRoutingConfData diameterRoutingConfData;
	private Integer orderNumber;
	
	@XmlTransient	
	public String getParameterId() {
		return parameterId;
	}
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	
	@XmlElement(name = "error-code")
	public String getErrorCodes() {
		return errorCodes;
	}
	public void setErrorCodes(String errorCodes) {
		this.errorCodes = errorCodes;
	}
	
	@XmlElement(name = "failure-action")
	@XmlJavaTypeAdapter(FailureActionAdapter.class)
	public Short getFailureAction() {
		return failureAction;
	}
	public void setFailureAction(Short failureAction) {
		this.failureAction = failureAction;
	}
	
 	@XmlElement(name = "failure-argument")
	public String getFailureArgument() {
		return failureArgument;
	}
	public void setFailureArgument(String failureArgument) {
		this.failureArgument = failureArgument;
	}
	
	@XmlTransient
	public DiameterRoutingConfData getDiameterRoutingConfData() {
		return diameterRoutingConfData;
	}
	public void setDiameterRoutingConfData(DiameterRoutingConfData diameterRoutingConfData) {
		this.diameterRoutingConfData = diameterRoutingConfData;
	}
	
	@XmlTransient	
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public JSONObject toJson() {
        JSONObject object = new JSONObject();
        for(DiameterFailureConstants diameterFailureConstant : DiameterFailureConstants.VALUES){
			if (diameterFailureConstant.failureAction == failureAction.intValue()){
				object.put("Failure Action", diameterFailureConstant.failureActionStr);
			}
		}
        object.put("Failure Argument", failureArgument);
        object.put("Error Code", errorCodes);
        return object;
    }
}
