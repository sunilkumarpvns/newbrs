package com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin;

import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

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
		jsonObject.put("Attribute Id ", getAttr_id());
		jsonObject.put("Attribute Value", getAttribute_value());
		jsonObject.put("Parameter Usage", getParameter_usage());

		return jsonObject;
	}
	
}

