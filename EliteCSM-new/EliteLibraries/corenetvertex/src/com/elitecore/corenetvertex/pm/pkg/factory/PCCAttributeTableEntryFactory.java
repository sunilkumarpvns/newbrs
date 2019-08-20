package com.elitecore.corenetvertex.pm.pkg.factory;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PriorityLevel;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.core.constant.ChargingModes;
import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgPCCAttributeData;
import com.elitecore.corenetvertex.pkg.ims.PCCRuleAttributeAction;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.imspackage.PCCAttributeTableEntry;
import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class PCCAttributeTableEntryFactory {

	private static final String MODULE = "PCC-ATTR-TABLE-ENTRY-FCTRY";
	private PackageFactory packageFactory;

	public PCCAttributeTableEntryFactory(PackageFactory packageFactory) {
		this.packageFactory = packageFactory;
	}

	public PCCAttributeTableEntry create(IMSPkgPCCAttributeData pccAttributeData, List<String> pccAttributeFailReasons) {

		LogicalExpression logicalExpression = null;

		if (pccAttributeData.getExpression() != null) {
			try {
				logicalExpression = com.elitecore.exprlib.compiler.Compiler.getDefaultCompiler()
						.parseLogicalExpression(pccAttributeData.getExpression());
			} catch (InvalidExpressionException e) {
				pccAttributeFailReasons.add("Invalid condition: " + pccAttributeData.getExpression());
				ignoreTrace(e);
			}
		}

		PCCRuleAttributeAction pccRuleAttributeAction = pccAttributeData.getAction();
		if (pccRuleAttributeAction == null) {
			pccAttributeFailReasons.add("Invalid pcc attribute: " + pccAttributeData.getAction());
		}

		String value = pccAttributeData.getValue();
		PCCAttribute pccAttribute = pccAttributeData.getAttribute();
		if (pccAttribute == null) {
			pccAttributeFailReasons.add("Invalid pcc attribute: " + pccAttributeData.getAttribute());
			/* if action is STANDARD OR SKIP then no need to check the value */
		} else if (pccRuleAttributeAction != PCCRuleAttributeAction.STANDARD && pccRuleAttributeAction != PCCRuleAttributeAction.SKIP) {
			if (Strings.isNullOrBlank(value)) {
				/* returning null will skip the pcc attribute entry */
				return null;
			}
			validatePCCAttributeValue(pccAttribute, value, pccAttributeFailReasons);
		}

		if (pccAttributeFailReasons.isEmpty() == false) {
			return null;
		}

		return packageFactory.createPCCAttributeTableEntry(pccAttribute,
				pccRuleAttributeAction,
				StringUtil.trimParameter(value),
				pccAttributeData.getExpression(),
				logicalExpression);

	}

	/*
	 * Validates value according to its supported type in Diameter AVP.
	 * 
	 * BEHAVIOUR:
	 * 
	 * IF value format is not correct THEN add fail log
	 * 
	 * for UnSigned32 avp type, IF value less than zero THEN add fail reason IF
	 * value more than max_value of UNSIGNED32 THEN add fail reason
	 * 
	 * for enum based attribute IF value is not from supported value THEN add
	 * fail reason
	 * 
	 * 
	 * @param pccAttribute
	 * 
	 * @param value
	 * 
	 * @param pccAttributeFailReasons
	 * 
	 * @return
	 */
	private void validatePCCAttributeValue(PCCAttribute pccAttribute, String value, List<String> pccAttributeFailReasons) {

		value = value.trim();

		switch (pccAttribute) {
			case CHARGING_KEY:
			case PRECEDENCE:
			case GBRDL:
			case GBRUL:
			case MBRDL:
			case MBRUL:

				long numberValue;
				try {
					numberValue = Long.parseLong(value);
				} catch (NumberFormatException nfe) {
					getLogger().error(MODULE, "Error while parsing(" + value + ") for pcc attribute(" + pccAttribute.name() + "). Reason: "
							+ nfe.getMessage());
					getLogger().trace(MODULE, nfe);
					pccAttributeFailReasons.add("Invalid value(" + value + "). Reason: Only 0 or positive numeric value allowed");
					return;
				}

				if (numberValue < 0) {
					pccAttributeFailReasons.add("Invalid value(" + value + "). Reason: Only 0 or positive numeric value allowed");
					return;
				}

				if (numberValue > CommonConstants.UNSIGNED32_MAX_VALUE) {
					pccAttributeFailReasons.add("Invalid value(" + value + "). Reason: Value configured(" + value
							+ ") is greator than UNSIGNED32 max value(" + CommonConstants.UNSIGNED32_MAX_VALUE + ")");
					return;
				}

				break;
			case CHARGING_MODE:

				if (ChargingModes.fromValue(value) == null) {
					pccAttributeFailReasons.add("Invalid value(" + value + ") for PCCAttribute(" + pccAttribute.name()
							+ "). Reason: Allowed values are " + getAllChargingModeNames());
					return;
				}
				break;
			case FLOW_STATUS:

				if (FlowStatus.fromValue(value) == null) {
					pccAttributeFailReasons.add("Invalid value(" + value + "). Reason: Allowed values are " + getAllFlowStatusNames());
					return;
				}

				break;
			case PREEMPTION_CAPABILITY:
			case PREEMPTION_VULNERABILITY:

				if (Boolean.TRUE.toString().equalsIgnoreCase(value) == false && Boolean.FALSE.toString().equalsIgnoreCase(value) == false) {
					pccAttributeFailReasons.add("Invalid value(" + value + "). Reason: Allowed values are (true, false)");
					return;
				}
				break;
			case QCI:

				int qciIntValue;
				try {
					qciIntValue = Integer.parseInt(value);
				} catch (NumberFormatException nfe) {
					getLogger().error(MODULE, "Error while parsing(" + value + ") for pcc attribute(" + pccAttribute.name() + "). Reason: "
							+ nfe.getMessage());
					getLogger().trace(MODULE, nfe);
					pccAttributeFailReasons.add("Invalid value(" + value + "). Reason: Allowed only [1, 9] value");
					return;
				}

				QCI qci = QCI.fromId(qciIntValue);

				if (qci == null) {
					pccAttributeFailReasons.add("Invalid value(" + value + "). Reason: Allowed only [1, 9] value");
					return;
				}

				break;
			case PRIORITY_LEVEL:

				int priorityIntValue;
				try {
					priorityIntValue = Integer.parseInt(value);
				} catch (NumberFormatException nfe) {
					getLogger().error(MODULE, "Error while parsing(" + value + ") for pcc attribute(" + pccAttribute.name() + "). Reason: "
							+ nfe.getMessage());
					getLogger().trace(MODULE, nfe);
					pccAttributeFailReasons.add("Invalid value(" + value + "). Reason: Allowed only [1, 15] value");
					return;
				}

				if (PriorityLevel.fromVal(priorityIntValue) == null) {
					pccAttributeFailReasons.add("Invalid value(" + value + "). Reason: Allowed only [1, 15] value");
					return;
				}

				break;
			case APP_SERVICE_PROVIDER_ID:
			case SPONSOR_ID:
				break;
		}
	}

	private String getAllFlowStatusNames() {

		StringBuilder builder = new StringBuilder();

		builder.append(CommonConstants.OPENING_PARENTHESES);

		for (FlowStatus status : FlowStatus.values()) {
			builder.append(status.displayVal).append(",");
		}

		builder.delete(builder.length() - 1, builder.length());
		return builder.append(CommonConstants.CLOSING_PARENTHESES).toString();
	}
	
	private String getAllChargingModeNames() {

        StringBuilder builder = new StringBuilder();

        builder.append(CommonConstants.OPENING_PARENTHESES);

        for (ChargingModes mode : ChargingModes.values()) {
            builder.append(mode.displayName).append(",");
        }

        builder.delete(builder.length() - 1, builder.length());
        return builder.append(CommonConstants.CLOSING_PARENTHESES).toString();
    }
}
