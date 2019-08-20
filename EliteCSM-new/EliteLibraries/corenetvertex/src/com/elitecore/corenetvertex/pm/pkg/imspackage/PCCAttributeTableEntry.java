package com.elitecore.corenetvertex.pm.pkg.imspackage;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.apache.commons.lang.SystemUtils;

import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.pkg.ims.PCCRuleAttributeAction;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class PCCAttributeTableEntry implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static final ToStringStyle IMS_PKG_PCC_ATTR_SERVICE_STYLE = new IMSPkgPCCAttributeDataToString();
	
	private final PCCAttribute attribute;
	private final PCCRuleAttributeAction action;
	private final String value;
	@Nullable private final String expressionStr; 
	@Nullable private final LogicalExpression expression;


	public PCCAttributeTableEntry(PCCAttribute attribute, PCCRuleAttributeAction action, String value, String expressionStr, LogicalExpression expression) {
		super();
		this.attribute = attribute;
		this.action = action;
		this.value = value;
		this.expressionStr = expressionStr;
		this.expression = expression;
	}

	public PCCAttribute getAttribute() {
		return attribute;
	}

	public PCCRuleAttributeAction getAction() {
		return action;
	}

	public String getValue() {
		return value;
	}

	public String getExpressionStr() {
		return expressionStr;
	}

	public LogicalExpression getExpression() {
		return expression;
	}

	public PCCAttribute getPCCAttribute() {
		return attribute;
	}
	
	public String toString(ToStringStyle toStringStyle) {
		ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
		.append("Attribute", attribute)
		.append("Expression", expression)
		.append("Action", action.val)
		.append("Value", value);

		return toStringBuilder.toString();

	}
	
	public String toString() {
		return toString(IMS_PKG_PCC_ATTR_SERVICE_STYLE);
	}
	
	private static final class IMSPkgPCCAttributeDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		IMSPkgPCCAttributeDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(2));
		}
	}
}
