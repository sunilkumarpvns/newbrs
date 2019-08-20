package com.elitecore.corenetvertex.pkg.constants;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PriorityLevel;
import com.elitecore.corenetvertex.core.constant.ChargingModes;
import com.elitecore.corenetvertex.pkg.ims.PCCRuleAttributeAction;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;

import java.util.*;

import static java.util.Comparator.comparing;

public enum PCCAttribute {

	FLOW_STATUS(
			"Flow Status",
			PCRFKeyConstants.PCC_RULE_FLOW_STATUS,
			Arrays.asList(PCCRuleAttributeAction.STANDARD, PCCRuleAttributeAction.OVERRIDE, PCCRuleAttributeAction.ADD),
			Arrays.asList(FlowStatus.ENABLED.displayVal, FlowStatus.ENABLED_UPLINK.displayVal, FlowStatus.ENABLED_DOWNLINK.displayVal, FlowStatus.DISABLED.displayVal),
			PCCRuleAttributeAction.STANDARD,
			FlowStatus.ENABLED.displayVal) {

		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setFlowStatus(FlowStatus.fromValue(value));
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			return pccRule.getFlowStatus().getVal();
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			pccRule.setFlowStatus(FlowStatus.fromValue(value));
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			return pccRule.getFlowStatus().displayVal;
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setFlowStatus(null);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getFlowStatus() != null;
		}

	},

	CHARGING_KEY("Charging Key",
			PCRFKeyConstants.PCC_RULE_CHARGING_KEY,
			Arrays.asList(PCCRuleAttributeAction.OVERRIDE, PCCRuleAttributeAction.SKIP),
			Arrays.<String> asList(),
			PCCRuleAttributeAction.OVERRIDE,
			"0"
	) {

		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setChargingKey(value);
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			return pccRule.getChargingKey();
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			setLongVal(pccRule, Long.parseLong(value));
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			return String.valueOf(pccRule.getChargingKey());
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setChargingKey(-1);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getChargingKey() >= 0;
		}
	},

	PREEMPTION_CAPABILITY("Preemption Capability",
			PCRFKeyConstants.PCC_RULE_PREEMPTION_CAPABILITY,
			Arrays.asList(PCCRuleAttributeAction.STANDARD, PCCRuleAttributeAction.OVERRIDE),
			Arrays.asList("true", "false"),
			PCCRuleAttributeAction.STANDARD,
			"true"
	) {
		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			if (value >= 0) {
				pccRule.setPeCapability(true);
			} else {
				pccRule.setPeCapability(false);
			}
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			if (pccRule.getPeCapability()) {
				return 0l;
			} else {
				return 1l;
			}

		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			if ("true".equalsIgnoreCase(value)) {
				pccRule.setPeCapability(true);
			} else {
				pccRule.setPeCapability(false);
			}
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			return Boolean.toString(pccRule.getPeCapability());
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			//Noting to remove
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return true;
		}
	},

	PREEMPTION_VULNERABILITY("Preemption Vulnerability",
			PCRFKeyConstants.PCC_RULE_PREEMPTION_VULNERABILITY,
			Arrays.asList(PCCRuleAttributeAction.STANDARD, PCCRuleAttributeAction.OVERRIDE),
			Arrays.asList("true", "false"),
			PCCRuleAttributeAction.STANDARD,
			"true") {
		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			if (value >= 0) {
				pccRule.setPeVulnerability(true);
			} else {
				pccRule.setPeVulnerability(false);
			}
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			if (pccRule.getPeVulnerability()) {
				return 0l;
			} else {
				return 1l;
			}
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			if ("true".equalsIgnoreCase(value)) {
				pccRule.setPeCapability(true);
			} else {
				pccRule.setPeCapability(false);
			}
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			return Boolean.toString(pccRule.getPeVulnerability());
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			//Noting to remove
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return true;
		}
	},

	PRIORITY_LEVEL("Priority Level", PCRFKeyConstants.PCC_RULE_PRIORITY_LEVEL,
			Arrays.asList(PCCRuleAttributeAction.STANDARD, PCCRuleAttributeAction.OVERRIDE),
			Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"),
			PCCRuleAttributeAction.STANDARD,
			"1") {

		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setPriorityLevel(PriorityLevel.fromVal((int) value));
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			if (pccRule.getPriorityLevel() != null) {
				return pccRule.getPriorityLevel().val;
			}

			return -1;
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			pccRule.setPriorityLevel(PriorityLevel.fromVal(value));
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			if (pccRule.getPriorityLevel() != null) {
				return pccRule.getPriorityLevel().stringVal;
			}
			return "";
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setPriorityLevel(null);

		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getPriorityLevel() != null;
		}

	},

	SPONSOR_ID("Sponser Id",
			PCRFKeyConstants.PCC_RULE_SPONSOR_ID,
			Arrays.asList(PCCRuleAttributeAction.OVERRIDE, PCCRuleAttributeAction.SKIP),
			Arrays.<String> asList(),
			PCCRuleAttributeAction.SKIP,
			"") {

		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setSponsorIdentity(String.valueOf(value));
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			return Long.parseLong(pccRule.getSponsorIdentity());
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			pccRule.setSponsorIdentity(value);
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			return pccRule.getSponsorIdentity();
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setSponsorIdentity(null);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getSponsorIdentity() != null;
		}

	},

	APP_SERVICE_PROVIDER_ID("App Service Provider Id",
			PCRFKeyConstants.PCC_RULE_APP_SERVICE_PROVIDER_ID,
			Arrays.asList(PCCRuleAttributeAction.OVERRIDE, PCCRuleAttributeAction.SKIP),
			Arrays.<String> asList(),
			PCCRuleAttributeAction.SKIP,
			"") {

		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setAppServiceProviderId(String.valueOf(value));
		}

		@Override
		public long getLongVal(PCCRule ccRule) {
			return Long.parseLong(ccRule.getAppServiceProviderId());
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			pccRule.setAppServiceProviderId(value);
		}

		@Override
		public String getStringVal(PCCRule ccRule) {
			return ccRule.getAppServiceProviderId();
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setSponsorIdentity(null);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getAppServiceProviderId() != null;
		}

	},

	CHARGING_MODE(
			"Charging Mode",
			PCRFKeyConstants.PCC_RULE_CHARGING_MODE,
			Arrays.asList(PCCRuleAttributeAction.OVERRIDE, PCCRuleAttributeAction.SKIP),
			Arrays.asList(ChargingModes.BOTH.displayName, ChargingModes.ONLINE.displayName, ChargingModes.OFFLINE.displayName, ChargingModes.NONE.displayName),
			PCCRuleAttributeAction.OVERRIDE,
			ChargingModes.NONE.displayName) {

		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setChargingMode(ChargingModes.fromValue((int) value));
		}

		@Override
		public long getLongVal(PCCRule ccRule) {
			return ccRule.getChargingMode().val;
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			pccRule.setChargingMode(ChargingModes.fromValue(value));
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			if (pccRule.getChargingMode() != null) {
				return String.valueOf(pccRule.getChargingMode().displayName);
			}

			return null;
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setChargingMode(null);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getChargingMode() != null;
		}

	},

	PRECEDENCE("Precedence", PCRFKeyConstants.PCC_RULE_PRECEDENCE,
			Arrays.asList(PCCRuleAttributeAction.OVERRIDE),
			Arrays.<String> asList(),
			PCCRuleAttributeAction.OVERRIDE,
			"1") {

		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setPrecedence((int) value);
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			return pccRule.getPrecedence();
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			setLongVal(pccRule, Long.parseLong(value));
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			return String.valueOf(pccRule.getPrecedence());
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setPrecedence(0);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getPrecedence() > 0;
		}

	},

	MBRUL(
			"MBRUL",
			PCRFKeyConstants.PCC_RULE_MBRUL,
			Arrays.asList(PCCRuleAttributeAction.ADD, PCCRuleAttributeAction.STANDARD, PCCRuleAttributeAction.SKIP, PCCRuleAttributeAction.OVERRIDE, PCCRuleAttributeAction.MAX, PCCRuleAttributeAction.MIN),
			Arrays.<String> asList(),
			PCCRuleAttributeAction.MIN,
			"") {
		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setMBRUL(value);
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			return pccRule.getMBRUL();
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			pccRule.setMBRUL(Long.parseLong(value));
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			return String.valueOf(pccRule.getMBRUL());
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setMBRUL(0);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getMBRUL() > 0;
		}

	},

	MBRDL(
			"MBRDL",
			PCRFKeyConstants.PCC_RULE_MBRDL,
			Arrays.asList(PCCRuleAttributeAction.ADD, PCCRuleAttributeAction.STANDARD, PCCRuleAttributeAction.SKIP, PCCRuleAttributeAction.OVERRIDE, PCCRuleAttributeAction.MAX, PCCRuleAttributeAction.MIN),
			Arrays.<String> asList(),
			PCCRuleAttributeAction.MIN,
			"") {
		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setMBRDL(value);
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			return pccRule.getMBRDL();

		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			pccRule.setMBRDL(Long.parseLong(value));
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			return String.valueOf(pccRule.getMBRDL());
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setMBRDL(0);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getMBRDL() > 0;
		}
	},

	GBRUL(
			"GBRUL",
			PCRFKeyConstants.PCC_RULE_GBRUL,
			Arrays.asList(PCCRuleAttributeAction.ADD, PCCRuleAttributeAction.STANDARD, PCCRuleAttributeAction.SKIP, PCCRuleAttributeAction.OVERRIDE, PCCRuleAttributeAction.MAX, PCCRuleAttributeAction.MIN),
			Arrays.<String> asList(),
			PCCRuleAttributeAction.MIN,
			"") {
		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setGBRUL(value);
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			return pccRule.getGBRUL();
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			pccRule.setGBRUL(Long.parseLong(value));
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			return String.valueOf(pccRule.getGBRUL());
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setGBRUL(0);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getGBRUL() > 0;
		}
	},

	GBRDL(
			"GBRDL",
			PCRFKeyConstants.PCC_RULE_GBRDL,
			Arrays.asList(PCCRuleAttributeAction.ADD, PCCRuleAttributeAction.STANDARD, PCCRuleAttributeAction.SKIP, PCCRuleAttributeAction.OVERRIDE, PCCRuleAttributeAction.MAX, PCCRuleAttributeAction.MIN),
			Arrays.<String> asList(),
			PCCRuleAttributeAction.MIN,
			"") {
		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setGBRDL(value);
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			return pccRule.getGBRDL();
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			pccRule.setGBRDL(Long.parseLong(value));
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			return String.valueOf(pccRule.getGBRDL());
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setGBRDL(0);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getGBRDL() > 0;
		}
	},

	QCI("QCI", PCRFKeyConstants.PCC_RULE_QCI,
			Arrays.asList(PCCRuleAttributeAction.STANDARD, PCCRuleAttributeAction.OVERRIDE, PCCRuleAttributeAction.MAX, PCCRuleAttributeAction.MIN),
			Arrays.<String> asList("1", "2", "3", "4", "5", "6", "7", "8", "9"),
			PCCRuleAttributeAction.MIN,
			"2") {

		@Override
		public void setLongVal(PCCRule pccRule, long value) {
			pccRule.setQCI(com.elitecore.corenetvertex.constants.QCI.fromId((int) value));
		}

		@Override
		public long getLongVal(PCCRule pccRule) {
			if (pccRule != null) {
				return pccRule.getQCI().val;
			}

			return -1;
		}

		@Override
		public void setStringVal(PCCRule pccRule, String value) {
			pccRule.setQCI(com.elitecore.corenetvertex.constants.QCI.fromId(value));
		}

		@Override
		public String getStringVal(PCCRule pccRule) {
			if (pccRule.getQCI() != null) {
				return pccRule.getQCI().stringVal;
			}

			return "";
		}

		@Override
		public void removeVal(PCCRule pccRule) {
			pccRule.setQCI(null);
		}

		@Override
		public boolean isValuePresent(PCCRule pccRule) {
			return pccRule.getQCI() != null;
		}

	},
	;

	public final String displayValue;
	public final PCRFKeyConstants pcrfKey;
	private final List<String> possibleValues;
	private final List<PCCRuleAttributeAction> possibleActions;
	public final String defaultVal;
	public final PCCRuleAttributeAction defaultAction;

	private static List<PCCAttribute> displayValues;
	private static Map<String, PCCAttribute> displayValToPCCAttribute;

	PCCAttribute(String displayValue,
				 PCRFKeyConstants key,
				 List<PCCRuleAttributeAction> action,
				 List<String> possibleValues,
				 PCCRuleAttributeAction defaultAction,
				 String defaultValue) {
		this.displayValue = displayValue;
		this.pcrfKey = key;
		this.possibleValues = possibleValues;
		this.defaultVal = defaultValue;
		this.defaultAction = defaultAction;
		this.possibleActions = action;
	}

	static {
		displayValues = new ArrayList<>();
		displayValToPCCAttribute = new HashMap<>();
		for (PCCAttribute pccAttribute : values()) {
			displayValToPCCAttribute.put(pccAttribute.displayValue, pccAttribute);
			displayValues.add(pccAttribute);
		}
		Collections.sort(displayValues, comparing(PCCAttribute::getDisplayValue));
	}

	public static PCCAttribute fromStringValue(String val) {
		return displayValToPCCAttribute.get(val);
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public static List<PCCAttribute> sortedValues() {
		return displayValues;
	}

	public abstract void setLongVal(PCCRule pccRule, long value);

	public abstract long getLongVal(PCCRule pccRule);

	public abstract void setStringVal(PCCRule pccRule, String value);

	public abstract String getStringVal(PCCRule pccRule);

	public abstract void removeVal(PCCRule pccRule);

	public abstract boolean isValuePresent(PCCRule pccRule);

	public List<PCCRuleAttributeAction> getPossibleActions() {
		return possibleActions;
	}

	public List<String> getPossibleValues() {
		return possibleValues;
	}
}
