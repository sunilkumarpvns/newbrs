package com.elitecore.corenetvertex.pm.pkg.imspackage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang.SystemUtils;

import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.IMSServiceAction;
import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class IMSServiceTable implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private final String name;
	private final MediaType serviceType;
	private IMSServiceAction action;
	@Nullable private final String afAppId;
	@Nullable private final String expressionStr;
	@Nullable private final LogicalExpression logicalExpression;
	
	@Nullable private final Map<PCCAttribute, List<PCCAttributeTableEntry>> pccAttributeEntries;
	public static final ToStringStyle IMS_PKG_SERVICE_STYLE = new IMSPkgServiceDataToString();
	
	public IMSServiceTable(String name, 
			MediaType serviceType, 
			String afAppId, 
			LogicalExpression logicalExpression, 
			String expressionStr,
			IMSServiceAction action,
			Map<PCCAttribute, List<PCCAttributeTableEntry>> pccAttributeToTableEntries) {
		this.name = name;
		this.serviceType = serviceType;
		this.afAppId = afAppId;
		this.logicalExpression = logicalExpression;
		this.expressionStr = expressionStr;
		this.action = action;
		this.pccAttributeEntries = pccAttributeToTableEntries;
		
	}

	public String getName() {
		return name;
	}

	public MediaType getServiceType() {
		return serviceType;
	}

	public IMSServiceAction getAction() {
		return action;
	}

	public String getAfAppId() {
		return afAppId;
	}

	public String getExpressionStr() {
		return expressionStr;
	}

	public LogicalExpression getLogicalExpression() {
		return logicalExpression;
	}

	public Map<PCCAttribute, List<PCCAttributeTableEntry>> getPccAttributeEntries() {
		return pccAttributeEntries;
	}
	
	public String toString(ToStringStyle toStringStyle) {
		
		ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
		.append("Name", name)
		.append("Media", serviceType.getName())
		.append("Media identifier", serviceType.getIdentifier())
		.append("AF app id", afAppId)
		.append("Additional condition", expressionStr)
		.append("Action", action);

		toStringBuilder.append("\t");
		if (Maps.isNullOrEmpty(pccAttributeEntries) == false) {
			for (List<PCCAttributeTableEntry> pccAttributeTableEntries : pccAttributeEntries.values()) {
				
				for (PCCAttributeTableEntry entry : pccAttributeTableEntries) {
					
					toStringBuilder.append("PCC attributes", entry);
				}
			}
		} else {

			toStringBuilder.append("No pcc attribute found");
		}

		return toStringBuilder.toString();

	}
	
	@Override
	public String toString() {
		return toString(IMS_PKG_SERVICE_STYLE);
	}
	
	private static final class IMSPkgServiceDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		IMSPkgServiceDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(1));
		}
	}
}
