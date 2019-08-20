package com.elitecore.coreradius.commons.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.GeneralVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.UnknownAttribute;
import com.elitecore.coreradius.commons.attributes.UnknownVendorSpecificType;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.WiMAXVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.WimaxUnknownAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.dictionary.*;

import javax.xml.bind.JAXBException;

public class Dictionary {
	public static final String MODULE = "RADIUS-DICTIONARY" ;

	private static Dictionary dictionaryInstance;

	private String cacheStatus=RadiusConstants.SUCCESS;
	private Date lastUpdatedTime;

	private Map <String, String>vendorMap = new HashMap<String, String>();
	private Map <String, IVendorSpecificAttribute>vendorAttrTypeMap = new HashMap<String, IVendorSpecificAttribute>();
	private Map<String,String> vendorAVPairSeparatorMap = new HashMap<String, String>();
	Map <AttributeId,IRadiusAttribute> attributeMap = new HashMap<AttributeId, IRadiusAttribute>();
	Map<String, AttributeId> attributeIdMap = new HashMap<String, AttributeId>();

	public static final int RFC2868_ENCRYPT_STANDARD = 1;
	public static final int RFC2865_ENCRYPT_STANDARD = 2;
	public static final int RFC2865_ENCRYPT_STANDARD_FOR_CISCO = 9;

	public static final String INVALID_ATTRIBUTE_ID_MSG = "Invalid Attribute Id: ";

	private DictionaryModelFactory dictionaryModelFactory = new DictionaryModelFactory();
	private WimaxAttributeFactory wimaxAttributeFactory = new WimaxAttributeFactory();
	private StandardAttributeFactory standardAttributeFactory = new StandardAttributeFactory();

	static {
		dictionaryInstance = new Dictionary();
	}

	/**
	 * this method will return the instance of Dictionary
	 * @return
	 */
	public static Dictionary getInstance() {
		return dictionaryInstance;
	}

	public void loadDictionary(File file, ILicenseValidator licenseValidator) throws Exception {
		setCacheStatus(RadiusConstants.SUCCESS);
		try{
			Map<String, String> tmpVendorMap = new HashMap<String, String>();
			Map<String, IVendorSpecificAttribute> tmpVendorAttrTypeMap = new HashMap<String, IVendorSpecificAttribute>();
			Map<String, String> tmpVendorAVPairMap = new HashMap<String, String>();
			Map<AttributeId,IRadiusAttribute> tmpAttributeMap = new HashMap<AttributeId, IRadiusAttribute>();
			Map<String, AttributeId> tmpAttributeIdMap = new HashMap<String, AttributeId>();

			load(file,licenseValidator,tmpAttributeMap, tmpAttributeIdMap, tmpVendorMap, tmpVendorAttrTypeMap, tmpVendorAVPairMap);

			this.attributeMap=tmpAttributeMap;
			this.attributeIdMap=tmpAttributeIdMap;
			this.vendorMap=tmpVendorMap;
			this.vendorAttrTypeMap=tmpVendorAttrTypeMap;
			this.vendorAVPairSeparatorMap=tmpVendorAVPairMap;

			setLastCacheUpdatedTime(new Date());
		}catch(Exception e){
			setCacheStatus(RadiusConstants.FAILED);
			throw new Exception("Failed loading radius dictionary. Reason :" + e.getMessage(), e);
		}
	}

	public void loadDictionary(File file) throws Exception {
		loadDictionary(file, null);
	}

	public boolean doLicenseValidation(DictionaryModel dictionaryModel, ILicenseValidator licenseValidator){
		for (Long vendorId: dictionaryModel.getIdToVendorInformation().keySet()) {
			if (licenseValidator != null
					&& !licenseValidator.isVendorSupported(String.valueOf(vendorId))) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "License not acquired for " + vendorId
							+ "-" + dictionaryModel.getIdToVendorInformation().get(vendorId).getVendorName() + ". Hence dictionary will not be loaded.");
				return false;
			}
		}
		return true;
	}

	public void load(DictionaryModel dictionaryModel, ILicenseValidator licenseValidator) throws DictionaryParseException{
		Map<String, String> tmpVendorMap = new HashMap<String, String>();
		Map<String, IVendorSpecificAttribute> tmpVendorAttrTypeMap = new HashMap<String, IVendorSpecificAttribute>();
		Map<String, String> tmpVendorAVPairMap = new HashMap<String, String>();
		Map<AttributeId,IRadiusAttribute> tmpAttributeMap = new HashMap<AttributeId, IRadiusAttribute>();
		Map<String, AttributeId> tmpAttributeIdMap = new HashMap<String, AttributeId>();

		load(dictionaryModel,licenseValidator,tmpAttributeMap, tmpAttributeIdMap, tmpVendorMap, tmpVendorAttrTypeMap, tmpVendorAVPairMap);

		this.attributeMap=tmpAttributeMap;
		this.attributeIdMap=tmpAttributeIdMap;
		this.vendorMap=tmpVendorMap;
		this.vendorAttrTypeMap=tmpVendorAttrTypeMap;
		this.vendorAVPairSeparatorMap=tmpVendorAVPairMap;

		setLastCacheUpdatedTime(new Date());
	}

	private void load(DictionaryModel dictionaryModel, ILicenseValidator licenseValidator, Map<AttributeId, IRadiusAttribute> attributeMap, Map<String, AttributeId> attributeIdMap, Map<String, String> vendorMap, Map<String, IVendorSpecificAttribute> vendorAttrTypeMap, Map<String, String> vendorAVPairSeparatorMap) throws DictionaryParseException{

		if(doLicenseValidation(dictionaryModel, licenseValidator) == false){
			return;
		}

		Map <String, String>tmpVendorMap = new HashMap<String, String>();
		Map <String, IVendorSpecificAttribute>tmpVendorAttrTypeMap = new HashMap<String, IVendorSpecificAttribute>();
		Map <String, String> tmpVendorAVPairMap = new HashMap<String, String>();
		Map <AttributeId,IRadiusAttribute> tmpAttributeMap = new HashMap<AttributeId, IRadiusAttribute>();
		Map<String, AttributeId> tmpAttributeIdMap = new HashMap<String, AttributeId>();

		for(VendorInformation vendorInformation: dictionaryModel.getIdToVendorInformation().values()) {
			tmpVendorMap.put(String.valueOf(vendorInformation.getVendorId()), vendorInformation.getVendorName());
			tmpVendorMap.put(vendorInformation.getVendorName(), String.valueOf(vendorInformation.getVendorId()));
			tmpVendorAVPairMap.put(vendorInformation.getVendorName(), vendorInformation.getAvPairSeparator());
			tmpVendorAVPairMap.put(String.valueOf(vendorInformation.getVendorId()), vendorInformation.getAvPairSeparator());

			IVendorSpecificAttribute vsaAttrType = null;

			if (vendorInformation.getVendorId() == RadiusConstants.WIMAX_VENDOR_ID) {
				vsaAttrType = new WiMAXVendorSpecificAttribute();
			} else {
				vsaAttrType = new GeneralVendorSpecificAttribute();
			}

			vsaAttrType.setVendorID(vendorInformation.getVendorId());
			tmpVendorAttrTypeMap.put(String.valueOf(vendorInformation.getVendorId()), vsaAttrType);
			tmpVendorAttrTypeMap.put(vendorInformation.getVendorName(), vsaAttrType);

			for (AttributeModel attributeModel : vendorInformation.getAttributeModels()) {
				BaseRadiusAttribute bRAttribute = null;

				Map<Long, String> keyValueMap = new HashMap<Long, String>();
				for (AttributeSupportedValueModel supportedValue : attributeModel.getSupportedValues()) {
					keyValueMap.put(supportedValue.getId(), supportedValue.getName());
				}


				if (DictionaryAttributeTypeConstant.GROUPED.equals(DictionaryAttributeTypeConstant.from(attributeModel.getType()))) {
					if (attributeModel.isTagged()) {
						attributeModel.setTagged(false);
						LogManager.getLogger().warn(MODULE, "Considering has-tag = false for group attribute " + vendorInformation.getVendorId() + ":" + vendorInformation.getVendorName() + ":" + attributeModel.getId() +
								". Reason: has-tag is not applicable to group attributes");
					}
					if (attributeModel.isIgnoreCase()) {
						attributeModel.setIgnoreCase(false);
						LogManager.getLogger().warn(MODULE, "Considering ignore-case = false for group attribute " + vendorInformation.getVendorId() + ":" + vendorInformation.getVendorName() + ":" + attributeModel.getId() +
								". Reason: ignore-case is not applicable to group attributes");
					}
					if (attributeModel.getEncryptStandard() != 0) {
						attributeModel.setEncryptStandard(0);
						LogManager.getLogger().warn(MODULE, "Considering encrypt-standard = 0 for group attribute " + vendorInformation.getVendorId() + ":" + vendorInformation.getVendorName() + ":" + attributeModel.getId() +
								". Reason: encrypt-standard is not applicable to group attributes");
					}
					if (attributeModel.isAvPair()) {
						attributeModel.setAvPair(false);
						LogManager.getLogger().warn(MODULE, "Considering avpair = false for group attribute " + vendorInformation.getVendorId() + ":" + vendorInformation.getVendorName() + ":" + attributeModel.getId() +
								". Reason: avpair is not applicable to group attributes");
					}
				}

				AttributeId attributeId = new AttributeId(vendorInformation.getVendorId(), vendorInformation.getVendorName(),
						attributeModel.getId(), attributeModel.getName(),
						attributeModel.getType(), attributeModel.isTagged(),
						keyValueMap, attributeModel.getLengthFormat().getValue(),
						attributeModel.getPaddingType(), attributeModel.isIgnoreCase(),
						attributeModel.isAvPair(), attributeModel.getEncryptStandard());

				if (vendorInformation.getVendorId() == RadiusConstants.WIMAX_VENDOR_ID) {
					bRAttribute = wimaxAttributeFactory.newWimaxAttribute(attributeId);
				} else if (vendorInformation.getVendorId() == RadiusConstants.STANDARD_VENDOR_ID && attributeModel.getId() == RadiusAttributeConstants.VENDOR_SPECIFIC) {
					bRAttribute = new VendorSpecificAttribute();
				} else {
					bRAttribute = standardAttributeFactory.newStandardAttribute(attributeId);
				}

				if (DictionaryAttributeTypeConstant.GROUPED.equals(DictionaryAttributeTypeConstant.from(attributeModel.getType()))) {
					readGroupedAttribute(bRAttribute.getType(), vendorInformation, attributeModel, attributeId, vendorInformation.getVendorName() + ":" + attributeModel.getName(), tmpAttributeMap, tmpAttributeIdMap);
				}

				tmpAttributeMap.put(attributeId, bRAttribute);
				tmpAttributeIdMap.put(attributeId.getStringID(), attributeId);
				if (vendorInformation.getVendorId() == RadiusConstants.STANDARD_VENDOR_ID) {
					tmpAttributeIdMap.put(attributeId.getAttributeName(), attributeId);
					tmpAttributeIdMap.put(String.valueOf(attributeId.getAttrbuteId()[0]), attributeId);
				} else {
					tmpAttributeIdMap.put(attributeId.getVendorName() + ":" + attributeId.getAttributeName(), attributeId);
				}
			}
		}
		attributeMap.putAll(tmpAttributeMap);
		attributeIdMap.putAll(tmpAttributeIdMap);
		vendorMap.putAll(tmpVendorMap);
		vendorAttrTypeMap.putAll(tmpVendorAttrTypeMap);
		vendorAVPairSeparatorMap.putAll(tmpVendorAVPairMap);
	}
	
	public DictionaryModel readDictionaryForServerManager( Reader inStream ) throws DictionaryParseException {
		DictionaryModel inputModel = null;
		try {
			inputModel = createModel(inStream);
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
		}
		return inputModel;
	}

	private DictionaryModel createModel(Reader inStream)throws DictionaryParseException {
		return dictionaryModelFactory.newModelfrom(inStream);
	}

	private void readGroupedAttribute(int iAttributeType, VendorInformation vendorInformation, AttributeModel parentAttributeModel, AttributeId parentAttributeId, String parentAttributeName, Map<AttributeId,IRadiusAttribute>attributeMap, Map<String,AttributeId> attributeIdMap) throws DictionaryParseException{

		for(AttributeModel subAttributeModel : parentAttributeModel.getSubAttributes()){
			BaseRadiusAttribute bRAttribute = null;

			int length = parentAttributeId.getAttrbuteId().length;
			int[] attrId = new int[length + 1];
			System.arraycopy(parentAttributeId.getAttrbuteId(), 0, attrId, 0, length);
			attrId[length] = subAttributeModel.getId();

			Map<Long,String> keyValueMap = new HashMap<Long, String>();
			for(AttributeSupportedValueModel supportedValue : subAttributeModel.getSupportedValues()){
				keyValueMap.put(supportedValue.getId(), supportedValue.getName());
			}

			if(DictionaryAttributeTypeConstant.GROUPED == DictionaryAttributeTypeConstant.from(subAttributeModel.getType())){
				if(subAttributeModel.isTagged()){
					subAttributeModel.setTagged(false);
					LogManager.getLogger().warn(MODULE, "Considering has-tag = false for group attribute " + parentAttributeId.getVendorId() +
							":"+ parentAttributeId.getVendorName() + ":" + subAttributeModel.getId() + ". Reason: has-tag is not applicable to group attributes");
				}
				if(subAttributeModel.isIgnoreCase()){
					subAttributeModel.setIgnoreCase(false);
					LogManager.getLogger().warn(MODULE, "Considering ignore-case = false for group attribute " + parentAttributeId.getVendorId() +
							":"+ parentAttributeId.getVendorName() + ":" + subAttributeModel.getId() + ". Reason: ignore-case is not applicable to group attributes");
				}
				if(subAttributeModel.getEncryptStandard() != 0){
					subAttributeModel.setEncryptStandard(0);
					LogManager.getLogger().warn(MODULE, "Considering encrypt-standard = 0 for group attribute " + parentAttributeId.getVendorId() +
							":"+ parentAttributeId.getVendorName() + ":" + subAttributeModel.getId() + ". Reason: encrypt-standard is not applicable to group attributes");
				}
				if(subAttributeModel.isAvPair()){
					subAttributeModel.setAvPair(false);
					LogManager.getLogger().warn(MODULE, "Considering avpair = false for group attribute " + parentAttributeId.getVendorId() +
							":"+ parentAttributeId.getVendorName() + ":" + subAttributeModel.getId() + ". Reason: avpair is not applicable to group attributes");
				}
			}

			AttributeId attributeDetail = new AttributeId(parentAttributeId.getVendorId(),parentAttributeId.getVendorName(),
					attrId ,subAttributeModel.getName(),
					subAttributeModel.getType(),subAttributeModel.isTagged(),
					keyValueMap ,
					subAttributeModel.getLengthFormat().getValue(), subAttributeModel.getPaddingType(),
					subAttributeModel.isIgnoreCase(), subAttributeModel.isAvPair(), subAttributeModel.getEncryptStandard());


			bRAttribute = standardAttributeFactory.newStandardAttribute(attributeDetail);

			attributeMap.put(attributeDetail, bRAttribute);
			attributeIdMap.put(attributeDetail.getStringID(), attributeDetail);
			attributeIdMap.put(parentAttributeName + ":" + subAttributeModel.getName(), attributeDetail);
			if(DictionaryAttributeTypeConstant.GROUPED == DictionaryAttributeTypeConstant.from(subAttributeModel.getType())){
				readGroupedAttribute(iAttributeType, vendorInformation, subAttributeModel, attributeDetail,parentAttributeName + ":" + subAttributeModel.getName(), attributeMap, attributeIdMap);
			}
		}
	}


	public IRadiusAttribute getKnownAttribute(String attributeId){
		AttributeId attrId = attributeIdMap.get(attributeId);
		if(attrId != null){
			return getKnownAttribute(attrId);
		}
		return null;
	}
	public IRadiusAttribute getKnownAttribute(AttributeId attrId){
		try{
			return (IRadiusAttribute) attributeMap.get(attrId).clone();
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
		}
		return null;
	}
	public IRadiusAttribute getKnownAttribute(int id){
		return getKnownAttribute(RadiusConstants.STANDARD_VENDOR_ID, id);
	}
	public IRadiusAttribute getKnownAttribute(long lVendorId, int...ids){
		AttributeId attrId = new AttributeId(lVendorId,ids);
		try{
			return (IRadiusAttribute) attributeMap.get(attrId).clone();
		}catch(Exception e){
			LogManager.ignoreTrace(e);
		}
		return null;
	}

	public IRadiusAttribute getAttribute(String attributeId)throws InvalidAttributeIdException{
		AttributeId attrId = null;

		attrId = getAttributeId(attributeId);

		IRadiusAttribute radAttribute = attributeMap.get(attrId);
		if(radAttribute!=null)
			try{
				return (IRadiusAttribute)radAttribute.clone();
			}catch(Exception e){
				LogManager.ignoreTrace(e);
			}
		return getUnknownAttribute(attrId);
	}

	public IRadiusAttribute getAttribute(AttributeId attrId){
		try{
			return (IRadiusAttribute) attributeMap.get(attrId).clone();
		}catch(Exception e){
			LogManager.ignoreTrace(e);
		}
		return getUnknownAttribute(attrId);
	}
	public IRadiusAttribute getAttribute(int id){
		return getAttribute(RadiusConstants.STANDARD_VENDOR_ID,id);
	}

	public IRadiusAttribute getAttribute(long lVendorId, int...ids){
		AttributeId attrId = new AttributeId(lVendorId,ids);
		try{
			IRadiusAttribute radAttributre = (IRadiusAttribute) attributeMap.get(attrId).clone();
			return (IRadiusAttribute) radAttributre.clone();
		}catch(Exception e){
			LogManager.ignoreTrace(e);
		}
		return getUnknownAttribute(attrId);
	}

	private IRadiusAttribute getUnknownAttribute(AttributeId attrId){
		int[] iAttrId = attrId.getAttrbuteId();
		if(attrId.getVendorId() == RadiusConstants.WIMAX_VENDOR_ID){
			return new WimaxUnknownAttribute((byte)iAttrId[iAttrId.length-1]);
		}
		attrId.getAttrbuteId();
		IRadiusAttribute unknownAttribute = new UnknownAttribute((byte)iAttrId[iAttrId.length-1]);
		unknownAttribute.setVendorID(attrId.getVendorId());
		return unknownAttribute;
	}

	public String getAttributeName(String attributeId){
		try {
			AttributeId attrId = getAttributeId(attributeId);
			if(attrId != null){
				String attrName = getAttributeName(attrId);
				if (attrName != null) {
					return attrName;
				}
			}
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		return null;
	}

	private String getAttributeName(AttributeId attrId) {
		try{
			String attrName = attrId.getAttributeName();
			if(attrName == null)
				attrName = attrId.getStringID();
			return attrName;
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
		}
		return null;
	}

	public String getAttributeName(int id){
		return getAttributeName(RadiusConstants.STANDARD_VENDOR_ID, id);
	}
	public String getAttributeName(long lVendorId, int...ids){
		AttributeId attrId = new AttributeId(lVendorId,ids);
		try{
			return attributeIdMap.get(attrId.getStringID()).getAttributeName();
		}catch(Exception e){
			LogManager.ignoreTrace(e);
		}
		return attrId.getStringID();
	}

	public String getValueFromKey(long lVendorId, int attributeID,long key){
		AttributeId attrId = attributeIdMap.get(lVendorId + ":" + attributeID);
		if(attrId != null){
			return attrId.getValueForKey(key);
		}
		return String.valueOf(key);
	}

	public String getValueFromKey(String attributeID,long key){
		AttributeId attrId = attributeIdMap.get(attributeID);
		if(attrId != null){
			return attrId.getValueForKey(key);
		}
		return String.valueOf(key);
	}

	public long getKeyFromValue(long lVendorId, int attributeID,String val){
		AttributeId attrId = attributeIdMap.get(lVendorId + ":" + attributeID);
		if(attrId != null){
			return attrId.getKeyForValue(val);
		}
		return -1;
	}

	/**
	 *
	 * @param strAttName   : Attribute Name
	 * @param strValueName : Value Name
	 * @return			   : int Value Id corresponding to Value Name, return -1 if not found
	 */
	public long getKeyFromValue(String attribute,String val){
		AttributeId attrId = attributeIdMap.get(attribute);
		if(attrId != null){
			return attrId.getKeyForValue(val);
		}
		return -1;
	}

	public String getValueFromKey(int attributeID,long key){
		return getValueFromKey(0, attributeID, key);
	}

	public long getKeyFromValue(int attributeID,String val){
		return getKeyFromValue(0, attributeID,val);
	}


	public String getVendorName(long lVendorID){
		return vendorMap.get(String.valueOf(lVendorID));
	}

	public String getVendorID(String strVendorName){
		return vendorMap.get(strVendorName);
	}

	public String getVendorAVPairSeparator(long lVendorId){
		return vendorAVPairSeparatorMap.get(String.valueOf(lVendorId));
	}

	public String getVendorAVPairSeparator(String strVendorName){
		return vendorAVPairSeparatorMap.get(strVendorName);
	}

	public IVendorSpecificAttribute getVendorAttributeType(long lVendorID){
		IVendorSpecificAttribute vsaAttrType = null;
		try{
			if(vendorAttrTypeMap.containsKey(String.valueOf(lVendorID))) {
				return (IVendorSpecificAttribute)vendorAttrTypeMap.get(String.valueOf(lVendorID)).clone();
			}else{
				LogManager.getLogger().warn(MODULE, "Unknown vendor: " + lVendorID);
				vsaAttrType = new UnknownVendorSpecificType();
				vsaAttrType.setVendorID(lVendorID);
			}
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
			vsaAttrType = new UnknownVendorSpecificType();
			vsaAttrType.setVendorID(lVendorID);
		}
		return vsaAttrType;
	}

	public void load(Reader dictionaryInStream) throws Exception {	//Reader dictionaryInStream
		try{
			setCacheStatus(RadiusConstants.SUCCESS);
			DictionaryModel dictionaryModel = createModel(dictionaryInStream);
			load(dictionaryModel,null, attributeMap, attributeIdMap, vendorMap, vendorAttrTypeMap, vendorAVPairSeparatorMap);
			setLastCacheUpdatedTime(new Date());
		}catch(DictionaryParseException e){
			setCacheStatus(RadiusConstants.FAILED);
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, e.getMessage());
		}
	}


	/**
	 *
	 * @return Set of Dictionary names which are loaded in memory
	 */
	public List<String> getDictionaryList(){
		return null;
	}

	/**
	 *
	 * @param file
	 *            : this file can be directory or exact file
	 */
	private void load(File file,
					  ILicenseValidator validator,
					  Map <AttributeId,IRadiusAttribute>attributeMap,
					  Map<String, AttributeId>attributeIdMap,
					  Map <String, String>vendorMap,
					  Map <String, IVendorSpecificAttribute>vendorAttrTypeMap,
					  Map<String,String> vendorAVPairSeparatorMap ) throws DictionaryParseException {
		if(!file.exists()){
			throw new DictionaryParseException("File does not exists. " + file.getAbsolutePath());
		}
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				load(fileList[i], validator,attributeMap,attributeIdMap,vendorMap, vendorAttrTypeMap, vendorAVPairSeparatorMap);
			}
		} else {
			if (file.getName().endsWith(".xml")) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Reading dictionary file ["	+ file.getName() + "]");
				Reader inStream =null;
				try {
					inStream = new FileReader(file);
					DictionaryModel dictionaryModel = createModel(inStream);
					load(dictionaryModel,validator,attributeMap,attributeIdMap,vendorMap, vendorAttrTypeMap, vendorAVPairSeparatorMap);
					inStream.close();
				}catch (DictionaryParseException | IOException e) {
					LogManager.getLogger().trace(MODULE, e);
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, e.getMessage());
				}
			}
		}
	}

	public AttributeId getAttributeId(String strAttributeId) throws InvalidAttributeIdException {
		AttributeId attrId = null;
		try{
			attrId = attributeIdMap.get(strAttributeId);
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
		}
		if(attrId == null){
			if(strAttributeId == null)
				throw new InvalidAttributeIdException(INVALID_ATTRIBUTE_ID_MSG + strAttributeId);
			if(strAttributeId.contains(":")){
				try{
					StringTokenizer attributeTokens = new StringTokenizer(strAttributeId,":");
					if(attributeTokens.countTokens() < 2)
						throw new InvalidAttributeIdException(INVALID_ATTRIBUTE_ID_MSG + strAttributeId);
					String vendorId = attributeTokens.nextToken();
					long lVendorId = Long.parseLong(vendorId);
					final int tokenCounts = attributeTokens.countTokens();
					int[] attrIds  = new int[tokenCounts];
					for(int i=0;i<tokenCounts;i++){
						attrIds[i] = Integer.parseInt(attributeTokens.nextToken());
					}
					attrId = new AttributeId(lVendorId, attrIds);
				}catch(Exception e){
					throw new InvalidAttributeIdException(INVALID_ATTRIBUTE_ID_MSG + strAttributeId,e);
				}
			}else{
				try{
					int[] attributeId = new int[1];
					attributeId[0] = Integer.parseInt(strAttributeId);
					attrId = new AttributeId(0, attributeId);
				}catch(Exception e){
					throw new InvalidAttributeIdException(INVALID_ATTRIBUTE_ID_MSG + strAttributeId,e);
				}
			}
		}
		return attrId;
	}


	//TODO this has a very limited usecase, right now it is just used from Rad client command
	public String getAttributeID(String attributeName){
		AttributeId attrId = null;
		try {
			attrId = getAttributeId(attributeName);
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		if(attrId != null)
			return String.valueOf(attrId.getAttrbuteId()[0]);
		return null;
	}

	/*
	 * The below Methods are introduced for the Reload cache.
	 */
	private void setCacheStatus(String cacheStatus){
		this.cacheStatus=cacheStatus;
	}

	public String getCacheStatus(){
		return cacheStatus;
	}

	private void setLastCacheUpdatedTime(Date date){
		this.lastUpdatedTime=date;
	}

	public Date getLastCacheUpdatedTime(){
		return this.lastUpdatedTime;
	}

	public Map<Long,List<AttributeId>> getAttributesMap(){
		Map<Long,List<AttributeId>> allAttributeMap = new HashMap<Long,List<AttributeId>>();
		for(AttributeId attributeId:attributeMap.keySet()){
			List<AttributeId> attributeList = allAttributeMap.get(attributeId.getVendorId());
			if(attributeList == null){
				attributeList = new ArrayList<AttributeId>();
				allAttributeMap.put(attributeId.getVendorId(), attributeList);
			}
			attributeList.add(attributeId);
		}
		return allAttributeMap;
	}

	public boolean isUnknownVendor(long vendorId) {
		return !vendorAttrTypeMap.containsKey(String.valueOf(vendorId));
	}

	public String findDictionaryName(File file, long vendorId){
		if(!file.exists()){
			return null;
		}
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				return findDictionaryName(fileList[i], vendorId);
			}
		} else {
			if (file.getName().endsWith(".xml")) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Reading dictionary file ["	+ file.getName() + "]");
				try {
					Map<Long, VendorInformation> idToVendorInformation = new HashMap<>();
					VendorInformation vendorInformation = ConfigUtil.deserialize(file, VendorInformation.class);
					idToVendorInformation.put(vendorInformation.getVendorId(), vendorInformation);
					DictionaryModel dictionaryModel = new DictionaryModel(idToVendorInformation);
					if(matchDictionary(dictionaryModel,vendorId)){
						return file.getName();
					}
				}catch (IOException | JAXBException e) {
					LogManager.getLogger().trace(MODULE, e);
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, e.getMessage());
				}

			}
		}
		return null;
	}

	private boolean matchDictionary(DictionaryModel dictionaryModel, long vendorId2) {
		boolean result = false;
		for(Long vendorId: dictionaryModel.getIdToVendorInformation().keySet()) {
			result = (vendorId == vendorId2);
		}
		return result;
	}
}
