package com.elitecore.diameterapi.plugins.universal.constant;

public interface UniversalDiameterPluginConstant {

	public static final String POLICY_ACTION = "POLICY_ACTION";
	public static final String POLICY_NAME = "POLICY_NAME";
	public static final String POLICY_PARAMETERS = "POLICY_PARAMETERS";
	
	public static final String COMMAND_CODE = "COMMAND_CODE";
	public static final String APPLICATION_ID = "APPLICATION_ID";
	
	public static final String $COMMAND_CODE = "$COMMAND_CODE";
	public static final String $APPLICATION_ID = "$APPLICATION_ID";
	public static final String REJECT_ON_CHECK_ITEM_NOT_FOUND = "REJECT_ON_CHECK_ITEM_NOT_FOUND";
	
	public static final int NO_ACTION = 0;
	public static final int NONE = 1;
	public static final int STOP = 2;
	public static final String ACTIVE = "ACTIVE";
	
	public static final String ATTRIBUTE_ID = "ATTRIBUTE_ID";
	public static final String PACKET_TYPE = "PACKET_TYPE";
	public static final String ATTRIBUTE_VALUE = "ATTRIBUTE_VALUE";
	public static final String PARAMETER_USAGE = "PARAMETER_USAGE";
	public static final String IN_UNIVERSAL_DIAMETER = "IN-UNIVERSAL-DIAMETER";
	public static final String OUT_UNIVERSAL_DIAMETER = "OUT-UNIVERSAL-DIAMETER";
	
	public static final String CHECK_ITEM = "C";
	public static final String REJECT_ITEM = "J";
	
	public static final String REPLY_ITEM = "R";
	public static final String FILTER_ITEM = "F";
	public static final String VALUE_REPLACE_ITEM = "V";
	public static final String ASSIGN_ITEM = "A";	
	
	
	public static final int IN_PACKET = 1;
	public static final int OUT_PACKET = 2;
	
	public static final String COMMA_SAPARATOR = ",";
	public static final String[] POLICY_MAIN_TAGS = {"POLICY_NAME","POLICY_ACTION","REJECT_ON_CHECK_ITEM_NOT_FOUND"};
	public static final String[] POLICY_CHILD_TAGS = {"ACTIVE","PACKET_TYPE","ATTRIBUTE_ID","ATTRIBUTE_VALUE","PARAMETER_USAGE"};
	public static final String UNIVERSAL_DIAMETER_PLUGIN = "UNIVERSAL_DIAMETER_PLUGIN";
}
