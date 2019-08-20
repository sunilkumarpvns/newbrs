/**
 *
 */
package com.elitecore.diameterapi.diameter.common.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.avps.BaseAVPBuilder;
import com.elitecore.diameterapi.diameter.common.packet.avps.BaseGroupAvpBuilder;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.UnknownAttribute;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.*;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.*;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpRule;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpUserEquipmentInfo;
import com.elitecore.diameterapi.diameter.common.packet.avps.threegpp.AvpUserLocationInfo;
import com.elitecore.diameterapi.diameter.common.util.constant.AVPType;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeData;
import com.elitecore.diameterapi.diameter.common.util.dictionary.DiameterDictionaryModel;
import com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

/**
 * @author dhaval.jobanputra
 *
 */

public  class DiameterDictionary{
	public static final String MODULE = "DIAMETER-DICTIONARY" ;
	private static final String OTHER_TYPE = "OCTETS";
	private static final String ANY = "*";
	private static DiameterDictionary dictionaryInstance;
	public static final String VALUE = "value";
	public static final String ID = "id" ;
	public static final String TYPE = "type" ;
	public static final String VENDOR_ID = "vendorid" ;
	public static final String APPLICATION_ID = "applicationid" ;
	public static final String YES = "yes";
	public static final int STANDARD_VENDOR_ID = 0 ;
	public static final long VALUE_NOT_FOUND = -1;

	//Used by old XML procedure
	public static final String ATTRIBUTE_LIST = "attribute-list" ;
	public static final String ATTRIBUTE = "attribute" ;
	public static final String SUPPORTED_VALUES = "supported-values" ;
	public static final String MANDATORY = "mandatory" ;
	public static final String PROTECTED = "protected" ;
	public static final String ENCRYPTION = "encryption" ;
	public static final String FIXED = "fixed";
	public static final String REQUIRED = "required";
	public static final String OPTIONAL = "optional";
	public static final String VENDOR_NAME = "vendor-name" ;
	public static final String APPLICATION_NAME = "application-name" ;
	public static final int STANDARD_APPLICATION_ID = 0 ;
	private static final String NAME = "name";
	private static final String GROUPED ="grouped";
	private static final String ATTRIBUTE_RULE ="attributerule";
	private static final String MINIMUM ="minimum";
	private static final String MAXIMUM ="maximum";
	private static final String STATUS = "Active";
	private static final String	DICTIONARY_TYPE = "Diameter";
	private static final String XML_EXTENSION = ".xml";

	private Map <String, VendorInformation>vendorMap  = new HashMap<String, VendorInformation>();
	private Map <AttributeData, BaseAVPBuilder> attributeMap = new HashMap <AttributeData , BaseAVPBuilder>();
	private Map <String, AttributeData> attributeIdMap = new HashMap<String, AttributeData>();
	private List<String> vendorIdList = new ArrayList<String>();
	private List<String> dictionariesRead = new ArrayList<String>();

	private void setDefaultAttributes(){

		// USER_EQUIPMENT_INFO_TYPE
		BaseAVPBuilder usrEqpmntInfoTypeBuilder = new AvpEnumeratedBuilder();
		usrEqpmntInfoTypeBuilder.setAVPCode(DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE_INT);
		usrEqpmntInfoTypeBuilder.setVendorId(STANDARD_VENDOR_ID);
		usrEqpmntInfoTypeBuilder.setAVPId(STANDARD_VENDOR_ID, DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE_INT);
		usrEqpmntInfoTypeBuilder.setAVPEncryption(YES);
		usrEqpmntInfoTypeBuilder.setMandatoryBit();
		Map<Integer,String>supportedValueMap = new HashMap<Integer, String>();
		supportedValueMap.put(0, "IMEISV");
		supportedValueMap.put(1, "MAC");
		supportedValueMap.put(2, "EUI64");
		supportedValueMap.put(3, "MODIFIED_EUI64");
		((AvpEnumeratedBuilder)usrEqpmntInfoTypeBuilder).setSupportedValuesMap(supportedValueMap);

		VendorInformation vendorInformation = new VendorInformation(Integer.toString(STANDARD_VENDOR_ID),"STANDARD","ACTIVE");
		AttributeData usrEqpmntInfoTypeAvpId = new AttributeData(vendorInformation.getVendorId(), Integer.toString(DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE_INT), "User-Equipment-Info-Type",
				YES,"no", YES,AVPType.valueOf("ENUMERATED"), "ACTIVE", "Diameter",null, null, null, supportedValueMap);
		attributeMap.put(usrEqpmntInfoTypeAvpId, usrEqpmntInfoTypeBuilder);
		attributeIdMap.put(STANDARD_VENDOR_ID + ":" + DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE_INT, usrEqpmntInfoTypeAvpId);


		// USER_EQUIPMENT_INFO
		BaseAVPBuilder usrEqpmntInfoBuilder = new UserEquipmentInfoAvpBuilder();
		ArrayList<AvpRule> requiredAttrList = new ArrayList<AvpRule>();
		AvpRule usrEqpmntInfoTypeAvpRule = new AvpRule();
		usrEqpmntInfoTypeAvpRule.setVendorId(0);
		usrEqpmntInfoTypeAvpRule.setAttrId(DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE_INT);
		usrEqpmntInfoTypeAvpRule.setMaximum("1");
		usrEqpmntInfoTypeAvpRule.setMinimum("1");
		usrEqpmntInfoTypeAvpRule.setName("User-Equipment-Info-Type");
		requiredAttrList.add(usrEqpmntInfoTypeAvpRule);

		AvpRule usrEqpmntInfoValueAvpRule = new AvpRule();
		usrEqpmntInfoTypeAvpRule.setVendorId(0);
		usrEqpmntInfoTypeAvpRule.setAttrId(DiameterAVPConstants.USER_EQUIPMENT_INFO_VALUE_INT);
		usrEqpmntInfoTypeAvpRule.setMaximum("1");
		usrEqpmntInfoTypeAvpRule.setMinimum("1");
		usrEqpmntInfoTypeAvpRule.setName("User-Equipment-Info-Value");
		requiredAttrList.add(usrEqpmntInfoValueAvpRule);

		usrEqpmntInfoBuilder.setAVPCode(DiameterAVPConstants.USER_EQUIPMENT_INFO_INT);
		usrEqpmntInfoBuilder.setVendorId(STANDARD_VENDOR_ID);
		usrEqpmntInfoBuilder.setAVPId(STANDARD_VENDOR_ID, DiameterAVPConstants.USER_EQUIPMENT_INFO_INT);
		usrEqpmntInfoBuilder.setAVPEncryption(YES);
		usrEqpmntInfoBuilder.setMandatoryBit();
		((BaseGroupAvpBuilder)usrEqpmntInfoBuilder).setRequiredAttrList(requiredAttrList);
		((BaseGroupAvpBuilder)usrEqpmntInfoBuilder).setFixedAttrList(new ArrayList<AvpRule>());
		((BaseGroupAvpBuilder)usrEqpmntInfoBuilder).setOptionalAttrList(new ArrayList<AvpRule>());

		vendorInformation = new VendorInformation(Integer.toString(STANDARD_VENDOR_ID),"STANDARD","ACTIVE");
		AttributeData usrEqpmntInfoAvpId = new AttributeData(vendorInformation.getVendorId(), Integer.toString(DiameterAVPConstants.USER_EQUIPMENT_INFO_INT), "User-Equipment-Info",
				YES,"no", YES,AVPType.valueOf("USEREQUIPMENTINFO"), "ACTIVE", "Diameter",null, null, null, supportedValueMap);
		attributeMap.put(usrEqpmntInfoAvpId, usrEqpmntInfoBuilder);
		attributeIdMap.put(STANDARD_VENDOR_ID + ":" + DiameterAVPConstants.USER_EQUIPMENT_INFO_INT,  usrEqpmntInfoAvpId);

		// 3GPP-User-Location-Info
		BaseAVPBuilder tgppUsrLocationInfoBuilder = new AvpUserLocationInfoAvpBuilder();
		tgppUsrLocationInfoBuilder.setAVPCode(22);
		tgppUsrLocationInfoBuilder.setVendorId(10415);
		tgppUsrLocationInfoBuilder.setAVPId(10415, 22);
		tgppUsrLocationInfoBuilder.setAVPEncryption(YES);
		tgppUsrLocationInfoBuilder.setMandatoryBit();

		vendorInformation = new VendorInformation(String.valueOf(DiameterConstants.VENDOR_3GPP_ID),"3GPP","ACTIVE");
		AttributeData tgppUsrLocationInfoAvpId = new AttributeData(vendorInformation.getVendorId(), "22", "3GPP-User-Location-Info",
				YES,"no", YES,AVPType.valueOf("USERLOCATIONINFO"), "ACTIVE", "Diameter",null, null, null, supportedValueMap);
		attributeMap.put(tgppUsrLocationInfoAvpId, tgppUsrLocationInfoBuilder);
		attributeIdMap.put(String.valueOf(DiameterAVPConstants.TGPP_USER_LOCATION_INFO), tgppUsrLocationInfoAvpId);

	}

	static {

		dictionaryInstance = new DiameterDictionary();
		dictionaryInstance.setDefaultAttributes();
	}

	public static boolean contains(String type) {

		for (AVPType avpType : AVPType.values()) {
			if (avpType.name().equalsIgnoreCase(type)) {
				return true;
			}
		}

		return false;
	}

	/**
	 *
	 * this method will return the instance of Dictionary
	 * @return
	 */
	public static DiameterDictionary getInstance() {
		return dictionaryInstance;
	}

	public void readDictionary(DiameterDictionaryModel diameterDictionaryModel, ILicenseValidator licenseValidator) throws DictionaryParseException {

		///FIXME do we need to check on whole model or per vendor
		if(doLicenseValidation(diameterDictionaryModel, licenseValidator) == false){
			return;
		}

		Map <AttributeData, BaseAVPBuilder> attributeDataToBuidler = new HashMap<>();
		Map <String, AttributeData> idToAttributeData = new HashMap<>();

		for(VendorInformation vendorInformation: diameterDictionaryModel.getIdtoVendorInformation().values()) {
			List<AttributeData> attributeDataList = vendorInformation.getAttributeData();
			for(AttributeData attributeData: attributeDataList) {
				BaseAVPBuilder baseAVPBuilder = createAvpBuilder(vendorInformation, attributeData);
				attributeData.setAVPId(vendorInformation.getVendorId(), attributeData.getAttributeId());
				idToAttributeData.put(attributeData.getAVPId(), attributeData);
				attributeDataToBuidler.put(attributeData, baseAVPBuilder);
			}
		}

		vendorMap = diameterDictionaryModel.getIdtoVendorInformation();
		attributeMap = attributeDataToBuidler;
		attributeIdMap = idToAttributeData;
		vendorIdList = diameterDictionaryModel.getVendorIds();
	}

	private BaseAVPBuilder createAvpBuilder(VendorInformation vendorInformation, AttributeData attributeData) throws DictionaryParseException {
		BaseAVPBuilder baseAVPBuilder = attributeData.getType().baseAVPBuilder();

		String attributeId = attributeData.getAttributeId();
		String vendorId = vendorInformation.getVendorId();
		try {
			baseAVPBuilder.setAVPCode(Integer.parseInt(attributeId));
		} catch (NumberFormatException e) {
			if (ANY.equalsIgnoreCase(attributeId)) {
				attributeId = "-1";
				baseAVPBuilder.setAVPCode(-1);
			} else {
				throw new DictionaryParseException("Invalid attribute id "+attributeId+" found");
			}
		}

		try {
			baseAVPBuilder.setVendorId(Integer.parseInt(vendorId));
		} catch (NumberFormatException e) {
			if (ANY.equalsIgnoreCase(vendorId)) {
				vendorId = "-1";
				baseAVPBuilder.setVendorId(-1);
			} else {
				throw new DictionaryParseException("Invalid vendor id "+ vendorId +" found");
			}
		}
		baseAVPBuilder.setAVPId(Integer.parseInt(vendorId), Integer.parseInt(attributeId));
		baseAVPBuilder.setAVPEncryption(attributeData.getEncryption());

		if (vendorId.equals(String.valueOf(STANDARD_VENDOR_ID)) == false) {
			baseAVPBuilder.setVendorBit();
		}
		if (attributeData.getMandatory() != null && YES.equalsIgnoreCase(attributeData.getMandatory())) {
			baseAVPBuilder.setMandatoryBit();
		}
		if (attributeData.getProtectedValue() != null && YES.equalsIgnoreCase(attributeData.getProtectedValue())) {
			baseAVPBuilder.setProtectedBit();
		}

		if (AVPType.ENUMERATED.name().equalsIgnoreCase(attributeData.getType().toString())){
			((AvpEnumeratedBuilder)baseAVPBuilder).setSupportedValuesMap(attributeData.getIdToSupportedValues());
		}else if (AVPType.UNSIGNED32.name().equalsIgnoreCase(attributeData.getType().toString())){
			((AvpUnsigned32Builder)baseAVPBuilder).setSupportedValuesMap(attributeData.getIdToSupportedValues());
		}
		return baseAVPBuilder;
	}

	public void load(DiameterDictionaryModel diameterDictionaryModel, ILicenseValidator validator) throws Exception {	//Reader dictionaryInStream
		try{
			readDictionary(diameterDictionaryModel,validator);
		}catch(DictionaryParseException e){
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().error(MODULE, e.getMessage());
		}
	}

	private boolean doLicenseValidation(DiameterDictionaryModel diameterDictionaryModel, ILicenseValidator licenseValidator){
		for (String vendorId: diameterDictionaryModel.getVendorIds()) {
			if(licenseValidator!=null
					&& licenseValidator.isVendorSupported(String.valueOf(vendorId)) == false){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "License not acquired for " + vendorId
							+ "-"+ diameterDictionaryModel.getIdtoVendorInformation().get(vendorId).getName() + ". Hence dictionary will not be loaded.");
				return false;
			}
		}

		return true;
	}

	private void parseGroupedAvp(String parentKey) {

		AvpRule avpRule;
		AttributeData tempAvpId;
		ArrayList<List<AvpRule>> list = new ArrayList<List<AvpRule>>();

		for(int i=0;i<3;i++){
			List<AvpRule> avpRuleList = list.get(i);
			if(Collectionz.isNullOrEmpty(avpRuleList) == false){
				for (int j=0 ; j<avpRuleList.size() ; j++ ){
					avpRule = avpRuleList.get(j);

					if(!(avpRule.getVendorId()==-1 && avpRule.getAttrId()==-1)){
						tempAvpId = attributeIdMap.get(avpRule.getVendorId() + ":" + avpRule.getAttrId());
						attributeIdMap.put(parentKey + "." +tempAvpId.getAVPId(), tempAvpId);
						if (tempAvpId.isGrouped()){
							parseGroupedAvp(parentKey+ "." + tempAvpId.getAVPId());
						}
					}
				}
			}
		}
	}


	public IDiameterAVP getAttribute(int iAttrId){

		return getAttribute("0:" + iAttrId);
	}

	public List<String> getDictionaryNames(){
		return new ArrayList<String>(this.dictionariesRead);
	}

	public IDiameterAVP getAttribute(long iVendorId, int iAttrId){
		return getAttribute(iVendorId+ ":" + iAttrId);
	}

	public IDiameterAVP getAttribute(String attrID){

		IDiameterAVP diameterAVP=null;
		if(attrID!=null){
			AttributeData avpId = getAttributeId(attrID);
			if (avpId != null)
				diameterAVP = attributeMap.get(avpId).createAVP();
			else{
				LogManager.getLogger().warn(MODULE, "Unknown Attribute: "+attrID);
				diameterAVP = getUnknownAttribute(attrID);
			}

		}else{
			LogManager.getLogger().warn(MODULE, "Unknown Attribute: "+attrID);
			diameterAVP = new UnknownAttribute();
		}
		return diameterAVP;
	}

	public IDiameterAVP getKnownAttribute(String attrID){

		IDiameterAVP diameterAVP=null;
		if(attrID!=null){
			AttributeData avpId = getAttributeId(attrID);
			if (avpId != null)
				diameterAVP = attributeMap.get(avpId).createAVP();
		}
		return diameterAVP;
	}

	private UnknownAttribute getUnknownAttribute(String attrID) {
		StringTokenizer tokenizer = new StringTokenizer(attrID, ":");
		String vendorId = null;
		String attrId = null;
		if(tokenizer.hasMoreTokens()) {
			vendorId = tokenizer.nextToken();
			if(tokenizer.hasMoreTokens()) {
				attrId = tokenizer.nextToken();
			}
		}

		if(vendorId != null && attrId != null)  {
			try {
				int intVendorId = Integer.parseInt(vendorId);
				int intAttrId = Integer.parseInt(attrId);
				byte bFlag = 0;
				if(intVendorId > 0) {
					bFlag = (byte)(bFlag | DiameterUtility.BIT_10000000);
				}
				return new UnknownAttribute(intAttrId , intVendorId,bFlag,attrID,"false");
			}catch(Exception e) {
				LogManager.getLogger().info(MODULE, "Unknown Attribute" + attrID + "Reason: " + e);
				return new UnknownAttribute();
			}
		}else {
			return new UnknownAttribute();
		}

	}

	public AttributeData getAttributeId(String attrID) {
		AttributeData diameterAvpId=null;
		diameterAvpId = attributeIdMap.get(attrID);


		if(diameterAvpId==null){
			try{
				StringTokenizer attributeTokens = new StringTokenizer(attrID,".");
				if(attributeTokens.countTokens() >= 2){
					String parentId = attributeTokens.nextToken();
					String childId = attributeTokens.nextToken() ;
					AttributeData parentAvpId = attributeIdMap.get(parentId);
					AttributeData childAvpId = attributeIdMap.get(childId);
					if(parentAvpId !=null && childAvpId!=null ){
						if(childAvpId.isGrouped()){
							parseGroupedAvp(parentAvpId.getAVPId()+"."+childAvpId.getAVPId());
						}else {
							attributeIdMap.put(attrID, childAvpId);
						}
						diameterAvpId = attributeIdMap.get(attrID);
					}
				}
			}catch (Exception e) {
				LogManager.getLogger().trace(MODULE, e);
			}

		}
		return diameterAvpId;
	}
	/**
	 *
	 * @param intAVPCode argument specifies: code of Avp
	 * @return returns the standard vendor's attribute name of Avp-code passed
	 */
	public String getAttributeName(int intAVPCode) {
		return getAttributeName("0:"+intAVPCode);
	}

	public String getAttributeName(int intVendorId, int intAVPCode) {
		return getAttributeName(intVendorId+":"+intAVPCode);
	}

	/**
	 *
	 * @param strId  argument specifies: Vendorid:Avpcode
	 *
	 * @return returns name of Attribute, if not found then returns
	 *
	 * 	"unknown-attribute".
	 */
	public String getAttributeName(String strId){
		AttributeData avpId = attributeIdMap.get(strId);
		if (avpId != null){
			return avpId.getName();
		}
		return "unknown-attribute";
	}

	public String getStrAVPId(String name) {
		if(name!=null && attributeIdMap.get(name)!=null)
			return String.valueOf(attributeIdMap.get(name).getAVPId());
		else
			return null;
	}

	public List<String> getVendorIDs() {
		List<String> vendorIds = new ArrayList<String>();
		vendorIds.addAll(this.vendorIdList);
		return vendorIds;
	}

	public long getKeyFromValue(String attribute,String value){
		AttributeData diameterAvpId = attributeIdMap.get(attribute);
		if(diameterAvpId != null){

			Long val = diameterAvpId.getKeyForValue(value);
			if(val != null){
				return val.longValue();
			}
		}
		return VALUE_NOT_FOUND;
	}

	public static String nameOf(String avpCode) {
		return getInstance().getAttributeName(avpCode);
	}

    public static class AvpOctetStringBuilder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpOctetString(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpUnsigned32Builder extends BaseAVPBuilder{
		Map<Integer, String> supportedValues = new HashMap<Integer, String>();

		public void setSupportedValuesMap(Map<Integer, String> supportedValueMap) {
			this.supportedValues = supportedValueMap;
		}
		@Override
		public IDiameterAVP createAVP() {
			return new AvpUnsigned32(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption,supportedValues);
		}

	}
	public static class AvpAddressBuilder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpAddress(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpDiameterIdentityBuilder extends BaseAVPBuilder {

		@Override
		public IDiameterAVP createAVP() {
			return new AvpDiameterIdentity(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpDiameterURIBuilder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpDiameterURI(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpFloat32Builder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpFloat32(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpFloat64Builder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpFloat64(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpIpv4AddressBuilder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpIPv4Address(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}

	public static class AvpGroupedBuilder extends BaseGroupAvpBuilder {
		@Override
		public IDiameterAVP createAVP() {
			return new AvpGrouped(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption,fixedAttrList,requiredAttrList,optionalAttrList);
		}


	}
	public static class AvpInteger32Builder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpInteger32(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpInteger64Builder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpInteger64(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpTimeBuilder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpTime(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpUnsigned64Builder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpUnsigned64(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpUTF8StringBuilder extends BaseAVPBuilder{

		@Override
		public IDiameterAVP createAVP() {
			return new AvpUTF8String(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}

	}
	public static class AvpEnumeratedBuilder extends BaseAVPBuilder {

		Map<Integer, String> supportedValues = new HashMap<Integer, String>();
		@Override
		public IDiameterAVP createAVP() {
			return new AvpEnumerated(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption,supportedValues);
		}
		public void setSupportedValuesMap(Map<Integer, String> supportedValueMap) {
			this.supportedValues = supportedValueMap;
		}

	}
	public static class AvpUserLocationInfoAvpBuilder extends BaseAVPBuilder {

		@Override
		public IDiameterAVP createAVP() {
			return new AvpUserLocationInfo(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption);
		}
	}
	public static class UserEquipmentInfoValueAvpBuilder extends BaseAVPBuilder {

		@Override
		public IDiameterAVP createAVP() {
			return new AvpUserEquipmentInfoValue(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
		}

	}
	public static class UserEquipmentInfoAvpBuilder extends BaseGroupAvpBuilder {
		@Override
		public IDiameterAVP createAVP() {
			return new AvpUserEquipmentInfo(intAVPCode,intVendorId,bAVPFlag,strAvpId,strAVPEncryption,fixedAttrList,requiredAttrList,optionalAttrList);
		}

	}

	//Old way to process XML
	public String findDictionaryName(File file, long vendorId, long applicationId){
		if(file.exists() == false){
			return null;
		}
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				return findDictionaryName(fileList[i], vendorId, applicationId);
			}
		} else {
			if (file.getName().endsWith(XML_EXTENSION)) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Reading dictionary file ["	+ file.getName() + "]");
				try(Reader inStream = new FileReader(file)) {
					if(matchDictionary(inStream,vendorId,applicationId)){
						return file.getName();
					}
				}catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
					LogManager.getLogger().error(MODULE, e.getMessage());
				}

			}
		}
		return null;
	}

	private boolean matchDictionary(Reader inStream, long vendorId2,long applicationId) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder documentBuilder = null;
		Document docDictionaryXMLParse = null;

		try{

			String strVendorID = null;
			String strApplicationId = null;

			long lVendorId = 0;
			long lApplicationId = 0;

			factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			documentBuilder =  factory.newDocumentBuilder();

			InputSource inputSource = new InputSource(inStream);
			docDictionaryXMLParse = documentBuilder.parse(inputSource);

			Node vendorNode = docDictionaryXMLParse.getElementsByTagName("attribute-list").item(0);

			if("attribute-list".equals(vendorNode.getNodeName())){

				if(vendorNode.getAttributes().getNamedItem("vendorid").getTextContent() != null){
					strVendorID = vendorNode.getAttributes().getNamedItem("vendorid").getTextContent().trim();
					validateVendorIdFormat(strVendorID);
				}

				if(vendorNode.getAttributes().getNamedItem("applicationid").getTextContent() != null){
					strApplicationId = vendorNode.getAttributes().getNamedItem("applicationid").getTextContent().trim();

					validateApplicationIdFormat(strApplicationId, strApplicationId + " is not a valid Application-id");
				}
				return lVendorId == vendorId2 && lApplicationId == applicationId;
			}
		}catch(Exception exp){
			LogManager.ignoreTrace(exp);
		}
		return false;
	}

	public  void load(File file, ILicenseValidator licenseValidator) throws Exception {

		if (file.exists() == false) {
			throw new FileNotFoundException("File(" + file.getAbsolutePath() + ") does not exist");
		}

		Map <String, AttributeData> tempAttributeIdMap = new HashMap<String, AttributeData>();

		Map <String, VendorInformation> tmpVendorMap = new HashMap<String, VendorInformation>();
		Map <AttributeData, BaseAVPBuilder> tmpAttributeMap = new HashMap<AttributeData, BaseAVPBuilder>();
		Map <String, AttributeData> tmpAttributeIdMap = new HashMap<String, AttributeData>();
		List<String> tmpVendorList = new ArrayList<String>();
		List<String> dictionariesRead = new ArrayList<String>();

		readDictionary(file, tempAttributeIdMap, licenseValidator, tmpAttributeMap, tmpAttributeIdMap, tmpVendorMap, tmpVendorList, dictionariesRead);

		this.vendorMap = tmpVendorMap;
		this.attributeMap = tmpAttributeMap;
		this.attributeIdMap = tmpAttributeIdMap;
		this.vendorIdList = tmpVendorList;
		this.dictionariesRead = dictionariesRead;

		AttributeData diameterAvpId;

		for(Map.Entry<String, AttributeData> entry:tempAttributeIdMap.entrySet()){
			diameterAvpId = entry.getValue();
			if(diameterAvpId.isGrouped())
				parseGroupedAvp(entry.getKey());
		}
	}

	private static void readDictionary(File file, Map<String, AttributeData> tempAttributeIdMap, ILicenseValidator licenseValidator,
									   Map<AttributeData, BaseAVPBuilder> attributeMap, Map<String, AttributeData> attributeIdMap,
									   Map<String, VendorInformation> vendorMap, List<String> vendorList, List<String> dictionaryRead) throws Exception {

		if (file.isDirectory()) {
			File [] fileList = file.listFiles();
			for (int i=0;i<fileList.length;i++){
				readDictionary(fileList[i], tempAttributeIdMap, licenseValidator, attributeMap, attributeIdMap, vendorMap, vendorList, dictionaryRead);
			}
		} else {
			if (file.getName().endsWith(XML_EXTENSION) == false) {
				return;
			}

			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Reading dictionary file ["	+ file.getName() + "]");
			}

			try(Reader inStream = new FileReader(file)) {
				readDictionary(inStream, tempAttributeIdMap, file.getName(), licenseValidator, attributeMap, attributeIdMap, vendorMap, vendorList, dictionaryRead);
			}catch (IOException e) {
				LogManager.getLogger().trace(MODULE, e);
				LogManager.getLogger().error(MODULE, e.getMessage());
			}
		}
	}

	private static void readDictionary(Reader inStream, Map<String, AttributeData> tempAttributeIdMap,
									   String xmlFileName, ILicenseValidator licenseValidator, Map<AttributeData, BaseAVPBuilder> attributeMap,
									   Map<String, AttributeData> attributeIdMap, Map<String, VendorInformation> vendorMap, List<String> vendorIdList,
									   List<String> dictionariesRead) throws DictionaryParseException {

		String strVendorID = null;
		String strVendorName = null;

		String strEncryption = null;
		String strMandatory = null;
		String strProtected = null;

		String strAttrType = null;
		VendorInformation vendor = null;

		int iVendorId = 0;

		Map <String, VendorInformation> tmpVendorMap = new HashMap<>();
		Map <AttributeData, BaseAVPBuilder> tmpAttributeMap = new HashMap<AttributeData, BaseAVPBuilder>();
		Map <String, AttributeData> tmpAttributeIdMap = new HashMap<String, AttributeData>();

		DocumentBuilderFactory factory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		NodeList nodeList = null;

		try{
			factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			documentBuilder =  factory.newDocumentBuilder();

			InputSource inputSource = new InputSource(inStream);
			document = documentBuilder.parse(inputSource);

			Node vendorNode = document.getElementsByTagName(ATTRIBUTE_LIST).item(0);
			nodeList = vendorNode.getChildNodes();

			if(vendorNode.getNodeName().equals(ATTRIBUTE_LIST)){

				if(vendorNode.getAttributes().getNamedItem(VENDOR_ID).getTextContent() != null){
					strVendorID = vendorNode.getAttributes().getNamedItem(VENDOR_ID).getTextContent().trim();
					iVendorId = validateVendorIdFormat(strVendorID);
				}else {
					throw new DictionaryParseException("Vendor-Id not defined for the dictionary.");
				}

				if(vendorNode.getAttributes().getNamedItem(VENDOR_NAME).getTextContent() != null){
					strVendorName = vendorNode.getAttributes().getNamedItem(VENDOR_NAME).getTextContent().trim();
				}else {
					throw new DictionaryParseException("Vendor-Name not defined for the dictionary.");
				}

				boolean isVendorSupported = licenseValidator.isVendorSupported(strVendorID);
				if (isVendorSupported == false) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE, "License not acquired for vendor: "
								+ iVendorId + "-" + strVendorName
								+ ". Hence dictionary: " + xmlFileName + " will not be loaded.");
					}
					return;
				}

				if (!tmpVendorMap.containsKey(strVendorID)){
					vendor = new VendorInformation(strVendorID , strVendorName , STATUS);
					tmpVendorMap.put(strVendorID, vendor);
					tmpVendorMap.put(strVendorName, vendor);
					if (vendorIdList.contains(strVendorID) == false) {
						vendorIdList.add(strVendorID);
					}
				}

			}

			for(int i=0; i<nodeList.getLength(); i++) {

				Node node = nodeList.item(i);

				String attrID = null;
				int iAttrID = -1;
				String attrName = null;

				BaseAVPBuilder baseAVPBuilder = null;
				NamedNodeMap namedNodeMap;

				if(node.getNodeName().equals(ATTRIBUTE)){
					namedNodeMap = node.getAttributes();
					if(namedNodeMap.getNamedItem(ID).getTextContent() != null){
						attrID = namedNodeMap.getNamedItem(ID).getTextContent().trim();
						iAttrID = validateAttributeIdFormat(strVendorID, strVendorName, attrID);
					}
					if(namedNodeMap.getNamedItem(NAME).getTextContent() != null){
						attrName = namedNodeMap.getNamedItem(NAME).getTextContent().trim();
					}else {
						throw new DictionaryParseException("Attribute name not specified for " + strVendorID + ":" + strVendorName + ":" + attrID);
					}
					if(namedNodeMap.getNamedItem(MANDATORY).getTextContent() != null){
						strMandatory = namedNodeMap.getNamedItem(MANDATORY).getTextContent();
					}
					if(namedNodeMap.getNamedItem(PROTECTED).getTextContent()!= null){
						strProtected = namedNodeMap.getNamedItem(PROTECTED).getTextContent();
					}
					if(namedNodeMap.getNamedItem(ENCRYPTION).getTextContent() != null){
						strEncryption = namedNodeMap.getNamedItem(ENCRYPTION).getTextContent();
					}
					if(namedNodeMap.getNamedItem(TYPE).getTextContent() != null){
						strAttrType = namedNodeMap.getNamedItem(TYPE).getTextContent().trim();
					}else {
						throw new DictionaryParseException("Attribute type not specified for " + strVendorID + ":" + strVendorName + ":" + attrID);
					}

					if("Unsigned32".equalsIgnoreCase(strAttrType)){
						baseAVPBuilder = new AvpUnsigned32Builder();
					}else if("Unsigned64".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpUnsigned64Builder();
					}else if("Integer32".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpInteger32Builder();
					}else if("Integer64".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpInteger64Builder();
					}else if("Float32".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpFloat32Builder();
					}else if("Float64".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpFloat64Builder();
					}else if("Grouped".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpGroupedBuilder();
					}else if("DiameterIdentity".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpDiameterIdentityBuilder();
					}else if("DiameterURI".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpDiameterURIBuilder();
					}else if("Time".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpTimeBuilder();
					}else if("UTF8String".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpUTF8StringBuilder();
					}else if("IPAddress".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpAddressBuilder();
					}else if("IPv4Address".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpIpv4AddressBuilder();
					}else if("Enumerated".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpEnumeratedBuilder();
					}else if("UserLocationInfo".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new AvpUserLocationInfoAvpBuilder();
					}else if("UserEquipmentInfoValue".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new UserEquipmentInfoValueAvpBuilder();
					}else if("UserEquipmentInfo".equalsIgnoreCase(strAttrType)) {
						baseAVPBuilder = new UserEquipmentInfoAvpBuilder();
					}else{
						baseAVPBuilder = new AvpOctetStringBuilder();
					}


					baseAVPBuilder.setAVPCode(iAttrID);
					baseAVPBuilder.setVendorId(iVendorId);
					baseAVPBuilder.setAVPId(iVendorId,iAttrID);
					baseAVPBuilder.setAVPEncryption(strEncryption);

					if(iVendorId != STANDARD_VENDOR_ID){
						baseAVPBuilder.setVendorBit();
					}
					if(YES.equalsIgnoreCase(strMandatory)){
						baseAVPBuilder.setMandatoryBit();
					}
					if(YES.equalsIgnoreCase(strProtected)){
						baseAVPBuilder.setProtectedBit();
					}

					NodeList subNodeList = node.getChildNodes();

					Map<Integer, String> supportedValueMap = new HashMap<>();

					for(int k = 0; k < subNodeList.getLength() ; k++){

						String valueName = null;
						Node dictNameNode = subNodeList.item(k);

						if(dictNameNode.getNodeName().equals(SUPPORTED_VALUES)){
							supportedValueMap.putAll(getSupportedValueMap(strVendorID, strVendorName, strAttrType, attrID, baseAVPBuilder, valueName, dictNameNode));


						}else if(dictNameNode.getNodeName().equals(GROUPED)){

							strAttrType = processGroupedAttribute(strVendorID, strEncryption, strMandatory, strProtected, strAttrType, tmpAttributeMap, tmpAttributeIdMap, attrID, attrName, baseAVPBuilder, dictNameNode);
						}
					}

					if (contains(strAttrType) == false) {
						strAttrType = OTHER_TYPE;
					}
					AttributeData attributeData = new AttributeData(strVendorID,
							attrID,
							attrName,
							strMandatory,
							strProtected,
							strEncryption,
							AVPType.valueOf(strAttrType.toUpperCase()),
							STATUS,
							DICTIONARY_TYPE,
							null,
							null,
							null,
							supportedValueMap);

					tmpAttributeMap.put(attributeData, baseAVPBuilder);
					tmpAttributeIdMap.put(strVendorID + ":" + attrID , attributeData);
					tmpAttributeIdMap.put(attrName , attributeData);
				}
			}
		}
		catch (Exception e){
			throw new DictionaryParseException(e);
		}

		dictionariesRead.add(xmlFileName);
		vendorMap.putAll(tmpVendorMap);
		attributeMap.putAll(tmpAttributeMap);
		attributeIdMap.putAll(tmpAttributeIdMap);
		tempAttributeIdMap.putAll(tmpAttributeIdMap);

	}

	private static String processGroupedAttribute(String strVendorID, String strEncryption, String strMandatory, String strProtected, String strAttrType, Map<AttributeData, BaseAVPBuilder> tmpAttributeMap, Map<String, AttributeData> tmpAttributeIdMap, String attrID, String attrName, BaseAVPBuilder baseAVPBuilder, Node dictNameNode) throws DictionaryParseException {
		ArrayList<AvpRule> fixedAttrList;
		ArrayList<AvpRule> requiredAttrList;
		ArrayList<AvpRule> optionalAttrList;
		NodeList groupeNodeList = dictNameNode.getChildNodes();

		fixedAttrList = new ArrayList<AvpRule>();
		requiredAttrList = new ArrayList<AvpRule>();
		optionalAttrList = new ArrayList<AvpRule>();

		Node groupChildNode;
		String groupChildNodeName;
		NodeList groupChildNodeList;
		int groupChildNodeListSize;
		for(int n=0; n<groupeNodeList.getLength(); n++){
			groupChildNode = groupeNodeList.item(n);
			groupChildNodeName  = groupChildNode.getNodeName();
			if(groupChildNodeName.equalsIgnoreCase(FIXED) ||groupChildNodeName.equalsIgnoreCase(OPTIONAL)||groupChildNodeName.equalsIgnoreCase(REQUIRED)){
				groupChildNodeList = groupChildNode.getChildNodes();
				groupChildNodeListSize = groupChildNodeList.getLength();
				Node attributeRuleNode;
				NamedNodeMap ruleNamedNodeMap = null;
				AvpRule avpRule = null;
				for(int m=0;m<groupChildNodeListSize;m++){
					attributeRuleNode = groupChildNodeList.item(m);
					if(ATTRIBUTE_RULE.equalsIgnoreCase(attributeRuleNode.getNodeName())){

						ruleNamedNodeMap = attributeRuleNode.getAttributes();
						avpRule = new AvpRule();
						if(ruleNamedNodeMap.getNamedItem("vendor-id").getTextContent() != null){
							validateVendorId(ruleNamedNodeMap, avpRule);
						}else{
							throw new DictionaryParseException("Vendor id not found in grouped avp.");
						}
						if(ruleNamedNodeMap.getNamedItem(ID).getTextContent() != null){
							validateAttributeId(ruleNamedNodeMap, avpRule);
						}else{
							throw new DictionaryParseException("Id not found in grouped avp.");
						}
						if(ruleNamedNodeMap.getNamedItem(NAME).getTextContent() != null){
							avpRule.setName(ruleNamedNodeMap.getNamedItem(NAME).getTextContent());
						}else{
							throw new DictionaryParseException("Attribute name not found in grouped avp.");
						}
						if(ruleNamedNodeMap.getNamedItem(MINIMUM).getTextContent() != null){
							avpRule.setMinimum(ruleNamedNodeMap.getNamedItem(MINIMUM).getTextContent());
						}else{
							throw new DictionaryParseException("Attribute minimum not found in grouped avp.");
						}
						if(ruleNamedNodeMap.getNamedItem(MAXIMUM).getTextContent() != null){
							avpRule.setMaximum(ruleNamedNodeMap.getNamedItem(MAXIMUM).getTextContent());
						}else{
							throw new DictionaryParseException("Attribute maximum not found in grouped avp.");
						}
						if(groupChildNodeName.equalsIgnoreCase(FIXED)){
							fixedAttrList.add(avpRule);
						}else if (groupChildNodeName.equalsIgnoreCase(OPTIONAL)) {
							optionalAttrList.add(avpRule);
						}else{
							requiredAttrList.add(avpRule);
						}

						if (contains(strAttrType) == false) {
							strAttrType = OTHER_TYPE;
						}

						AttributeData attributeData = new AttributeData(strVendorID, attrID, attrName, strMandatory,strProtected, strEncryption, AVPType.valueOf(strAttrType.toUpperCase()), STATUS, DICTIONARY_TYPE, avpRule.getMinimum(), avpRule.getMaximum(), ruleNamedNodeMap.getNamedItem("vendor-id").getTextContent(), null);

						tmpAttributeMap.put(attributeData, baseAVPBuilder);
						tmpAttributeIdMap.put(strVendorID + ":" + attrID , attributeData);
						tmpAttributeIdMap.put(attrName , attributeData);
					}
				}
			}
		}
		((BaseGroupAvpBuilder)baseAVPBuilder).setOptionalAttrList(optionalAttrList);
		((BaseGroupAvpBuilder)baseAVPBuilder).setFixedAttrList(fixedAttrList);
		((BaseGroupAvpBuilder)baseAVPBuilder).setRequiredAttrList(requiredAttrList);
		return strAttrType;
	}

	public Map<Object, Object> readDictionaryForServerManager( Reader inStream ) throws DictionaryParseException {


		Map<Object, Object> resultMap= new HashMap<Object, Object>();
		Map<String, Object> attributeMap = new LinkedHashMap<String,Object>();
		Map<String, Object> attribute=null;

		final String VENDOR_ID="vendor_id";
		final String VENDOR_NAME="vendor_name";
		final String APPLICATION_ID="application_id";
		final String APPLICATION_NAME="application_name";
		final String ATTRIBUTE_ID="attribute_id";
		final String ATTRIBUTE_NAME="attribute_name";
		final String ATTRIBUTE_MANDATORY="attribute_mandatory";
		final String ATTRIBUTE_PROTECTED="attribute_protected";
		final String ATTRIBUTE_ENCRYPTION="attribute_encryption";
		final String ATTRIBUTE_TYPE="attribute_type";
		final String SUPPORTED_VALUE="supported_value";
		final String FIXED_GROUP_ATTRIBUTE_LIST="fixed_group_attribute_list";
		final String REQUIRED_GROUP_ATTRIBUTE_LIST="required_group_attribute_list";
		final String OPTIONAL_GROUP_ATTRIBUTE_LIST="optional_group_attribute_list";
		final String ATTRIBUTE_LIST="attribute_list";


		String strVendorID = null;
		String strVendorName = null;
		String strApplicationId = null;
		String strApplicationName = null;
		String strEncryption = null;
		String strMandatory = null;
		String strProtected = null;
		String strAttrType = null;

		int iVendorId = 0;
		int iApplicationId = 0;

		DocumentBuilderFactory factory = null;
		DocumentBuilder documentBuilder = null;
		Document docDictionaryXMLParse = null;
		NodeList nodeList = null;

		try{
			factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			documentBuilder =  factory.newDocumentBuilder();


			InputSource inputSource = new InputSource(inStream);
			docDictionaryXMLParse = documentBuilder.parse(inputSource);

			nodeList = docDictionaryXMLParse.getElementsByTagName("attribute-list").item(0).getChildNodes();

			Node vendorNode = docDictionaryXMLParse.getElementsByTagName("attribute-list").item(0);
			if("attribute-list".equals(vendorNode.getNodeName())){

				if(vendorNode.getAttributes().getNamedItem("vendorid").getTextContent() != null){
					strVendorID = vendorNode.getAttributes().getNamedItem("vendorid").getTextContent().trim();

					try {
						iVendorId = Integer.parseInt(strVendorID);
						resultMap.put(VENDOR_ID,iVendorId);
					}catch(NumberFormatException e) {
						throw new DictionaryParseException(strVendorID + " is not a valid Vendor-Id");
					}
				}else {
					throw new DictionaryParseException("Vendor-Id not defined for the dictionary.");
				}

				if(vendorNode.getAttributes().getNamedItem("vendor-name").getTextContent() != null){
					strVendorName = vendorNode.getAttributes().getNamedItem("vendor-name").getTextContent().trim();
					resultMap.put(VENDOR_NAME,strVendorName);

				}else {
					throw new DictionaryParseException("Vendor-Name not defined for the dictionary.");
				}

				if(vendorNode.getAttributes().getNamedItem("applicationid").getTextContent() != null){
					strApplicationId = vendorNode.getAttributes().getNamedItem("applicationid").getTextContent().trim();
					try{
						iApplicationId = Integer.parseInt(strApplicationId);
						resultMap.put(APPLICATION_ID,iApplicationId);
					}catch(NumberFormatException e){
						throw new DictionaryParseException(strVendorID + " is not a valid Application-Id");
					}
				}else {
					throw new DictionaryParseException("Application-Id not defined for the dictionary.");
				}

				if(vendorNode.getAttributes().getNamedItem("application-name").getTextContent() != null){
					strApplicationName = vendorNode.getAttributes().getNamedItem("application-name").getTextContent().trim();
					resultMap.put(APPLICATION_NAME,strApplicationName);
				}else {
					throw new DictionaryParseException("Application-Name not defined for the dictionary.");
				}

				for(int i=0; i<nodeList.getLength(); i++) {
					attribute = new HashMap<String, Object>();
					Node node = nodeList.item(i);

					String attrID = null;
					int iAttrID = -1;
					String attrName = null;

					if("attribute".equals(node.getNodeName())){

						if(node.getAttributes().getNamedItem("id").getTextContent() != null){
							attrID = node.getAttributes().getNamedItem("id").getTextContent().trim();
							try {
								iAttrID = Integer.parseInt(attrID);
								attribute.put(ATTRIBUTE_ID,iAttrID);
							} catch (NumberFormatException e) {
								if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
									LogManager.getLogger().trace(MODULE, "Attribute id "+attrID+" is not in proper format for " + strVendorID + ":" + strVendorName);
								throw new DictionaryParseException("Attribute id "+attrID+" is not in proper format for " + strVendorID + ":" + strVendorName);
							}
						}

						if(node.getAttributes().getNamedItem("name").getTextContent() != null){
							attrName = node.getAttributes().getNamedItem("name").getTextContent().trim();
							attribute.put(ATTRIBUTE_NAME,attrName);
						}else {
							throw new DictionaryParseException("Attribute name not specified for " + strVendorID + ":" + strVendorName + ":" + attrID);
						}

						if(node.getAttributes().getNamedItem("type").getTextContent() != null){
							strAttrType = node.getAttributes().getNamedItem("type").getTextContent().trim();
							attribute.put(ATTRIBUTE_TYPE,strAttrType);
						}else {
							throw new DictionaryParseException("Attribute type not specified for " + strVendorID + ":" + strVendorName + ":" + attrID);
						}


						if(node.getAttributes().getNamedItem("protected").getTextContent()!= null){
							strProtected = node.getAttributes().getNamedItem("protected").getTextContent();
							attribute.put(ATTRIBUTE_PROTECTED,strProtected);
						}

						if(node.getAttributes().getNamedItem("mandatory").getTextContent() != null){
							strMandatory = node.getAttributes().getNamedItem("mandatory").getTextContent();
							attribute.put(ATTRIBUTE_MANDATORY,strMandatory);
						}

						if(node.getAttributes().getNamedItem("encryption").getTextContent() != null){
							strEncryption = node.getAttributes().getNamedItem("encryption").getTextContent();
							attribute.put(ATTRIBUTE_ENCRYPTION,strEncryption);
						}



						// Now search for supported values for the attribute.
						StringBuilder strSupportedValue = new StringBuilder("");
						attribute.put(SUPPORTED_VALUE, strSupportedValue.toString());
						NodeList subNodeList = node.getChildNodes();


						for(int k = 0; k < subNodeList.getLength() ; k++){

							String valueID = null;
							String valueName = null;
							Node dictNameNode = subNodeList.item(k);

							processNodes(attribute, SUPPORTED_VALUE, FIXED_GROUP_ATTRIBUTE_LIST, REQUIRED_GROUP_ATTRIBUTE_LIST, OPTIONAL_GROUP_ATTRIBUTE_LIST, strVendorID, strVendorName, attrID, strSupportedValue, dictNameNode);
						}
						attributeMap.put(""+iAttrID+"",attribute);
					}


				}

				resultMap.put(ATTRIBUTE_LIST,attributeMap);
			}


		}catch(SAXParseException sax){
			LogManager.getLogger().error(MODULE, sax.getMessage());
			LogManager.getLogger().trace(MODULE, "Unexpected error while parsing dictionary "+sax);
			throw new DictionaryParseException("Unexpected error while parsing dictionary at Line Number: " + sax.getLineNumber() +
					" Column Number: " + sax.getColumnNumber());
		} catch (IOException io) {
			LogManager.getLogger().error(MODULE, io.getMessage());
			LogManager.getLogger().trace(MODULE, "Unexpected error while parsing dictionary "+io);
			throw new DictionaryParseException("Unexpected error while parsing dictionary, Reason: " + io.getMessage());
		} catch (Exception ex) {
			LogManager.getLogger().error(MODULE, ex.getMessage());
			LogManager.getLogger().trace(MODULE, "Unexpected error while parsing dictionary "+ex);
			throw new DictionaryParseException("Unexpected error while parsing dictionary ");
		}
		return resultMap;
	}

	private void processNodes(Map<String, Object> attribute, String SUPPORTED_VALUE, String FIXED_GROUP_ATTRIBUTE_LIST, String REQUIRED_GROUP_ATTRIBUTE_LIST, String OPTIONAL_GROUP_ATTRIBUTE_LIST, String strVendorID, String strVendorName, String attrID, StringBuilder strSupportedValue, Node dictNameNode) throws DictionaryParseException {
		String valueID;
		String valueName;
		if("supported-values".equals(dictNameNode.getNodeName())){

            NodeList valueNodeList = dictNameNode.getChildNodes();

            for(int m = 0; m < valueNodeList.getLength() ; m++){

                Node valueNameNode = valueNodeList.item(m);

                if("value".equals(valueNameNode.getNodeName())) {

                    if(valueNameNode.getAttributes().getNamedItem("id").getTextContent() != null){
                        valueID = valueNameNode.getAttributes().getNamedItem("id").getTextContent().trim();

                        try {
                            Integer.parseInt(valueID);
                        } catch (NumberFormatException e) {
                            if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
                                LogManager.getLogger().trace(MODULE, "Not a valid attribute value id "+valueID+" for " + strVendorID + ":" + strVendorName + ":" + attrID);
                            throw new DictionaryParseException("Not a valid attribute value id "+valueID+" for " + strVendorID + ":" + strVendorName + ":" + attrID);
                        }
                    }else{
                        throw new DictionaryParseException("Attribute value id not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
                    }

                    if(valueNameNode.getAttributes().getNamedItem("name").getTextContent() != null){
                        valueName = valueNameNode.getAttributes().getNamedItem("name").getTextContent().trim();
                    }else{
                        throw new DictionaryParseException("Attribute value name not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
                    }

                    if (valueID != null && valueName != null) {

                        if ("".equals(strSupportedValue.toString())){
                            strSupportedValue.append(valueName);
                            strSupportedValue.append(':');
                            strSupportedValue.append(valueID);
                        }else strSupportedValue.append("," + valueName + ":" + valueID);
                    }



                }

            } // end of for loop
            attribute.put(SUPPORTED_VALUE,strSupportedValue.toString());

        }else if("Grouped".equalsIgnoreCase(dictNameNode.getNodeName())){

            NodeList groupeNodeList = dictNameNode.getChildNodes();

            HashMap<String,Object> fixedAttrMap = new HashMap<String, Object>();
            HashMap<String,Object> requiredAttrMap = new HashMap<String, Object>();
            HashMap<String,Object> optionalAttrMap = new HashMap<String, Object>();




            for(int n=0; n<groupeNodeList.getLength(); n++){
                Node valueNameNode = groupeNodeList.item(n);

                if("fixed".equals(valueNameNode.getNodeName())) {

                    NodeList fixedNodeList = valueNameNode.getChildNodes();

                    for(int j=0; j<fixedNodeList.getLength();j++){

                        Node valNameNode = fixedNodeList.item(j);

                        if("attributerule".equals(valNameNode.getNodeName())) {
                            AvpRule avpRule = new AvpRule();

							processGroupedAttribute(fixedAttrMap, valNameNode, avpRule);

                        }
                    }
                    attribute.put(FIXED_GROUP_ATTRIBUTE_LIST,fixedAttrMap);

                }
                if("required".equals(valueNameNode.getNodeName())) {
                    NodeList requiredNodeList = valueNameNode.getChildNodes();
                    for(int j=0; j<requiredNodeList.getLength();j++){

                        Node valNameNode = requiredNodeList.item(j);
                        if("attributerule".equals(valNameNode.getNodeName())) {
                            AvpRule avpRule = new AvpRule();

							processGroupedAttribute(requiredAttrMap, valNameNode, avpRule);


                        }
                    }
                    attribute.put(REQUIRED_GROUP_ATTRIBUTE_LIST,requiredAttrMap);
                }
                if("optional".equals(valueNameNode.getNodeName())) {
                    NodeList optionalNodeList = valueNameNode.getChildNodes();
                    for(int j=0; j<optionalNodeList.getLength();j++){

                        Node valNameNode = optionalNodeList.item(j);
                        if("attributerule".equals(valNameNode.getNodeName())) {
                            AvpRule avpRule = new AvpRule();

							processGroupedAttribute(optionalAttrMap, valNameNode, avpRule);
                        }
                    }
                    attribute.put(OPTIONAL_GROUP_ATTRIBUTE_LIST,optionalAttrMap);
                }
            }

        }
	}

	private void processGroupedAttribute(HashMap<String, Object> fixedAttrMap, Node valNameNode, AvpRule avpRule) throws DictionaryParseException {
		if(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent() != null){
            try{
                avpRule.setVendorId(Integer.parseInt(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent()));
            }catch (NumberFormatException e) {
                if("*".equalsIgnoreCase(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent())){
                    avpRule.setVendorId(-1);
                }else{
                    throw new DictionaryParseException("Invalid vendor id found");
                }
            }
        }else{
            throw new DictionaryParseException("Attribute name not found in grouped avp.");
        }
		if(valNameNode.getAttributes().getNamedItem(ID).getTextContent() != null){
            try{
                avpRule.setAttrId(Integer.parseInt(valNameNode.getAttributes().getNamedItem(ID).getTextContent()));
            }catch (NumberFormatException e) {
                if("*".equalsIgnoreCase(valNameNode.getAttributes().getNamedItem(ID).getTextContent())){
                    avpRule.setAttrId(-1);
                }else{
                    throw new DictionaryParseException("Invalid Attribute id found");
                }
            }
        }else{
            throw new DictionaryParseException("Attribute name not found in grouped avp.");
        }

		if(valNameNode.getAttributes().getNamedItem("name").getTextContent() != null){
            avpRule.setName(valNameNode.getAttributes().getNamedItem("name").getTextContent());
        }else{
            throw new DictionaryParseException("Attribute name not found in grouped avp.");
        }
		if(valNameNode.getAttributes().getNamedItem("minimum").getTextContent() != null){
            avpRule.setMinimum(valNameNode.getAttributes().getNamedItem("minimum").getTextContent());
        }else{
            throw new DictionaryParseException("Attribute minimum not found in grouped avp.");
        }
		if(valNameNode.getAttributes().getNamedItem("maximum").getTextContent() != null){
            avpRule.setMaximum(valNameNode.getAttributes().getNamedItem("maximum").getTextContent());
        }else{
            throw new DictionaryParseException("Attribute maximum not found in grouped avp.");
        }
		fixedAttrMap.put(avpRule.getName(),avpRule);
	}

	private static Map<Integer, String> getSupportedValueMap(String strVendorID,
															 String strVendorName,
															 String strAttrType,
															 String attrID,
															 BaseAVPBuilder baseAVPBuilder,
															 String valueName,
															 Node dictNameNode) throws DictionaryParseException {
		Map<Integer, String> supportedValueMap;
		int ivalueID;
		String valueID;
		supportedValueMap = new HashMap<Integer, String>();

		NodeList valueNodeList = dictNameNode.getChildNodes();

		for(int m = 0; m < valueNodeList.getLength() ; m++){
			ivalueID = -1;

			Node valueNameNode = valueNodeList.item(m);

			if(VALUE.equals(valueNameNode.getNodeName())) {

				if(valueNameNode.getAttributes().getNamedItem(ID).getTextContent() != null){
					valueID = valueNameNode.getAttributes().getNamedItem(ID).getTextContent().trim();

					ivalueID = validateAttributeValueIdFormat(valueID, "Not a valid attribute value id " + valueID + " for " + "Not a valid attribute value id " + valueID + " for " + strVendorID + ":" + strVendorName + ":" + attrID + ":" + "Not a valid attribute value id " + valueID + " for " + strVendorID + ":" + strVendorName + ":" + attrID + ":" + "Not a valid attribute value id " + valueID + " for " + strVendorID + ":" + strVendorName + ":" + attrID);
				}else{
					throw new DictionaryParseException("Attribute value id not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
				}

				if(valueNameNode.getAttributes().getNamedItem(NAME).getTextContent() != null){
					valueName = valueNameNode.getAttributes().getNamedItem(NAME).getTextContent().trim();
				}else{
					throw new DictionaryParseException("Attribute value name not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
				}

			}
			if (ivalueID != -1)
				supportedValueMap.put(ivalueID, valueName);

		}
		if (AVPType.ENUMERATED.name().equalsIgnoreCase(strAttrType)){
			((AvpEnumeratedBuilder)baseAVPBuilder).setSupportedValuesMap(supportedValueMap);
		}
		if (AVPType.UNSIGNED32.name().equalsIgnoreCase(strAttrType)){
			((AvpUnsigned32Builder)baseAVPBuilder).setSupportedValuesMap(supportedValueMap);
		}

		return supportedValueMap;
	}

	private static int validateAttributeValueIdFormat(String valueID, String strMessage) throws DictionaryParseException {
		int ivalueID;
		try {
			ivalueID = Integer.parseInt(valueID);
		} catch (NumberFormatException e) {
			LogManager.getLogger().trace(MODULE, strMessage);
			throw new DictionaryParseException(strMessage);
		}
		return ivalueID;
	}

	private static int validateAttributeIdFormat(String strVendorID, String strVendorName, String attrID) throws DictionaryParseException {
		return validateAttributeValueIdFormat(attrID, "Attribute id " + attrID + " is not in proper format for " + strVendorID + ":" + strVendorName);
	}

	private static int validateApplicationIdFormat(String strApplicationId, String message) throws DictionaryParseException {
		int iApplicationId;
		try {
			iApplicationId = Integer.parseInt(strApplicationId);
		} catch (NumberFormatException e) {
			throw new DictionaryParseException(message);
		}
		return iApplicationId;
	}

	private static int validateVendorIdFormat(String strVendorID) throws DictionaryParseException {
		int iVendorId;
		try {
			iVendorId = Integer.parseInt(strVendorID);
		}catch(NumberFormatException e) {
			throw new DictionaryParseException(strVendorID + " is not a valid Vendor-Id");
		}
		return iVendorId;
	}

	private static void validateAttributeId(NamedNodeMap ruleNamedNodeMap, AvpRule avpRule) throws DictionaryParseException {
		try{
			avpRule.setAttrId(Integer.parseInt(ruleNamedNodeMap.getNamedItem(ID).getTextContent()));
		}catch (NumberFormatException e) {
			if(ANY.equalsIgnoreCase(ruleNamedNodeMap.getNamedItem(ID).getTextContent())){
				avpRule.setAttrId(-1);
			}else{
				throw new DictionaryParseException("Invalid id found");
			}
		}
	}

	private static void validateVendorId(NamedNodeMap ruleNamedNodeMap, AvpRule avpRule) throws DictionaryParseException {
		String vendorId = ruleNamedNodeMap.getNamedItem("vendor-id").getTextContent();
		try{
			avpRule.setVendorId(Integer.parseInt(vendorId));
		}catch (NumberFormatException e) {
			if(ANY.equalsIgnoreCase(vendorId)){
				avpRule.setVendorId(-1);
			}else{
				throw new DictionaryParseException("Invalid vendor id "+vendorId+"found");
			}
		}
	}

	public String getVendorNameById(String vendorId){
		return vendorMap.get(vendorId).getName();
	}

	public String getAttributeNameById(String avpId){
		return attributeIdMap.get(avpId).getName();
	}
}
