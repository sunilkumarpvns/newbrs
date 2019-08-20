package com.elitecore.diameterapi.plugins;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.plugins.ParamDetails;
import com.elitecore.exprlib.parser.expression.Expression;

@XmlType(propOrder={})
public class DiameterParamDetail extends ParamDetails  implements Differentiable {

	private Boolean isActive;
	private Expression parameterExpression;
	
	@XmlTransient
	public Boolean isActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	@XmlTransient
	public Expression getParameterExpression() {
		return parameterExpression;
	}
	public void setParameterExpression(Expression parameterExpression) {
		this.parameterExpression = parameterExpression;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("Active", getActive());
		jsonObject.put("Packet Type", getPacket_type());
		
		if (getAttr_id() != null && Strings.isNullOrBlank(getAttr_id()) == false)
			jsonObject.put("Attribute Id ", getAttr_id());

		if (getAttribute_value() != null && Strings.isNullOrBlank(getAttribute_value()) == false)
			jsonObject.put("Attribute Value", getAttribute_value());

		if (getParameter_usage() != null && Strings.isNullOrBlank(getParameter_usage()) == false)
			jsonObject.put("Parameter Usage", getParameter_usage());
		
		return jsonObject;
	}

}
