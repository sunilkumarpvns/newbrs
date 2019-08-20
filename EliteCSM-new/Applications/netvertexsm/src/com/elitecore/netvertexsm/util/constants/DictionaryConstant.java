package com.elitecore.netvertexsm.util.constants;

public class DictionaryConstant extends BaseConstant {

	public static final String MODULE_NAME = "Dictionary";
	
	/* following are used in parsing of uploaded Dictionary file */
	public static final String BEGIN = "BEGIN";
	public static final String STANDARD = "RADIUS-STANDARD";
    public static final long STANDARD_VENDOR_ID = 0L;
	public static final String END = "END";
	public static final String ATTRIBUTE = "ATTRIBUTE";
	public static final String VALUE = "VALUE";
	public static final long RADIUS_RFC_DICTIONARY_PARAMETER_ID_FOR_VSA =26L;
	public static final String AVPAIR = "AVPAIR";
	public static final String VENDOR = "VENDOR";
	public static final String DATA_TYPE_ID_STRING = "DTT03";
	public static final String DIAMETER_STRING_DATA_TYPE_ID = "DTT03";
	public static final int DEFAULT_MODAL_NUMBER = 2;
	public static final String DEFAULT_ALIAS = "default";	
	public static final String NO = "N";		
	public static final String YES = "Y";	
	public static final String DICTIONARY_PARAMETER_DETAIL_ALIAS = "default";
	public static final String DICTIONARY_PARAMETER_DETAIL_NETWORK_FILTER_SUPPORT = NO;
	public static final String DICTIONARY_PARAMETER_DETAIL_OPERATOR_ID = "1";
	public static final String DICTIONARY_PARAMETER_DETAIL_USAGE_TYPE = "A";
	
	public static final int MAX_LIMIT_FOR_NON_ALPHABET_VIEW = 25;
	public static final String ALPHABET = "ALPHABET";
	public static final String NON_ALPHABET = "NON_ALPHABET";
	public static final String DEFAULT_ALPHABET_FOR_SEARCH = "A";
	
	public static final String AVPAIR_ALIAS = "AVPair";
	public static final String DEFAULT_STATUS_ID = "CST00";
	public static final String SHOW_STATUS_ID = "CST01";
	public static final String HIDE_STATUS_ID = "CST02";
    public static final String PREDEFINEDVALUES_SEPARATOR = ",";
    public static final String PREDEFINEDVALUES_KEYVALUE_SEPARATOR = ":";
    public static final String VALUE_YES = "yes";		
	public static final String VALUE_NO = "no";	
    	
	//Dictonary XML tag
	public static final String TAG_ATTRIBUTE_LIST = "attribute-list";
	public static final String TAG_ATTRIBUTE = "attribute";
	public static final String TAG_SUPPORTED_VALUES = "supported-values";
	public static final String TAG_VALUE = "value";
	
	public static final String ATTR_VENDOR_NAME =  "vendor-name";
	public static final String ATTR_VENDOR_ID =  "vendorid";
	
	public static final String ATTR_ID = "id";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_TYPE = "type";
	public static final String ATTR_AVPAIR= "avpair";
	public static final String ATTR_HAS_TAG = "has-tag";
	public static final String ATTR_IGNORE_CASE = "ignore-case";
	public static final String ATTR_ENCRYPT_STANDARD = "encrypt-standard";
	
	public static final String ATTR_VALUE_ID = "id";
	public static final String ATTR_VALUE_NAME = "name";
	
}

