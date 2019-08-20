package com.elitecore.aaa.core.plugins;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.plugins.ParamDetails;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

@XmlType(propOrder={})
public class RadiusParamDetails extends ParamDetails implements Differentiable{

	private LogicalExpression logicalExpression;
	private Expression valueExpression;
	
	public RadiusParamDetails() {
	}

	@XmlTransient
	public LogicalExpression getLogicalExpression() {
		return logicalExpression;
	}

	public void setLogicalExpression(LogicalExpression logicalExpression) {
		this.logicalExpression = logicalExpression;
	}
	
	@XmlTransient
	public Expression getExpression(){
		return valueExpression;
	}
	
	public void setExpression(Expression valueExpression) {
		this.valueExpression = valueExpression;
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
