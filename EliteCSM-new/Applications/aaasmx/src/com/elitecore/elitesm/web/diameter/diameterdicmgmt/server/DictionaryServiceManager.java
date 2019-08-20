package com.elitecore.elitesm.web.diameter.diameterdicmgmt.server;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.DictionaryParseException;
import com.elitecore.coreradius.commons.util.constants.LengthFormat;
import com.elitecore.coreradius.commons.util.constants.PaddingType;
import com.elitecore.coreradius.commons.util.dictionary.AttributeModel;
import com.elitecore.coreradius.commons.util.dictionary.AttributeSupportedValueModel;
import com.elitecore.coreradius.commons.util.dictionary.DictionaryModel;
import com.elitecore.coreradius.commons.util.dictionary.VendorInformation;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.AuthorizationException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.*;
import com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport.EnvironmentNotSupportedException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.exception.DuplicateDictionaryConstraintExcpetion;
import com.elitecore.elitesm.util.constants.DictionaryConstant;
import com.elitecore.elitesm.util.logger.Logger;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.*;

public class DictionaryServiceManager {
	private final String MODULE="DictionaryServiceManager";

	private Hashtable           htab_strName_strDataTypeId                                 = null;
	private Hashtable           htab_strVendorName_intObjIanaPvtEntNumber                  = new Hashtable();
	// private Hashtable           htab_intObjVendorParameterId_dictionaryParameterDetailData = new Hashtable();
	private List<DictionaryParameterDetailData> dictionaryParentAttributeList=null;

	public IDictionaryData saveDictionary(String strXML, String strDictionaryId, String userId){

		htab_strVendorName_intObjIanaPvtEntNumber = new Hashtable();
		// htab_intObjVendorParameterId_dictionaryParameterDetailData = new Hashtable();

		dictionaryParentAttributeList = new ArrayList<DictionaryParameterDetailData>();
		Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());

		StringReader stringReader = null;
		IDictionaryData dictionaryData= null;


		try {
			stringReader = new StringReader(strXML);
			//InputStreamReader inr = new InputStreamReader(new InputStream());



			RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
			htab_strName_strDataTypeId = dictionaryBLManager.getDatatype();


			Dictionary dic = Dictionary.getInstance();

			populteMap(dic.readDictionaryForServerManager(stringReader));
			dictionaryData = getDictionaryData(userId);
			dictionaryData.setDictionaryId(strDictionaryId);

			dictionaryBLManager.updateDictionary(dictionaryData);


		}
		catch (DictionaryParseException dicExp) {
			if (stringReader != null)
				stringReader.close();
		}
		catch (DuplicateDictionaryConstraintExcpetion e) {
		}
		catch (AuthorizationException authExp) {
			if (stringReader != null)
				stringReader.close();

		}
		catch (EnvironmentNotSupportedException envException) {

			if (stringReader != null)
				stringReader.close();
		}
		catch (CommunicationException connException) {

			if (stringReader != null)
				stringReader.close();
		}
		catch (OperationFailedException opException) {
			if (stringReader != null)
				stringReader.close();
		}

		catch (ConstraintViolationException conException) {
			if (stringReader != null)
				stringReader.close();
		}
		catch (FormatInvalidException forException) {
			if (stringReader != null)
				stringReader.close();

		}
		catch (NullValueException nullValueException) {
			if (stringReader != null)
				stringReader.close();
		}
		catch (InvalidFileExtensionException invException) {
			if (stringReader != null)
				stringReader.close();
		}
		catch (InvalidValueException invalidValueException) {
			if (stringReader != null)
				stringReader.close();
		}
		catch (ValueOutOfRangeException valueOutOfRangeException) {
			if (stringReader != null)
				stringReader.close();
		}
		catch (DataValidationException validationException) {
			if (stringReader != null)
				stringReader.close();
		}
		catch (DataManagerException mgrException) {
			if (stringReader != null)
				stringReader.close();
		}
		catch (Exception exp) {
			if (stringReader != null)
				stringReader.close();
		}
		finally {
			if (stringReader != null)
				stringReader.close();
		}

		return dictionaryData;
	}

	private void populteMap( DictionaryModel dictionaryModel) {

		for(VendorInformation vendorDictionary : dictionaryModel.getIdToVendorInformation().values()) {
			htab_strVendorName_intObjIanaPvtEntNumber.put(vendorDictionary.getVendorName(), vendorDictionary.getVendorId());
			Set<AttributeModel> attributeModels=vendorDictionary.getAttributeModels();
			DictionaryParameterDetailData dictionaryParameterDetailData;
			String strAttributeDataType = null;

			for(AttributeModel attributeModel:attributeModels){
				if(attributeModel != null){
					dictionaryParameterDetailData = new DictionaryParameterDetailData();
					dictionaryParameterDetailData.setAlias(DictionaryConstant.DEFAULT_ALIAS);
					dictionaryParameterDetailData.setName(attributeModel.getName());
					dictionaryParameterDetailData.setVendorId(vendorDictionary.getVendorId());
					strAttributeDataType=attributeModel.getType();

					if(vendorDictionary.getVendorId() == DictionaryConstant.STANDARD_VENDOR_ID){
						dictionaryParameterDetailData.setRadiusRFCDictionaryParameterId(attributeModel.getId());
						dictionaryParameterDetailData.setVendorParameterOveridden(DictionaryConstant.NO);
					}else{
						dictionaryParameterDetailData.setRadiusRFCDictionaryParameterId(DictionaryConstant.RADIUS_RFC_DICTIONARY_PARAMETER_ID_FOR_VSA);
						dictionaryParameterDetailData.setVendorParameterOveridden(DictionaryConstant.YES);
					}

					if (htab_strName_strDataTypeId.containsKey(strAttributeDataType) == true) {
						dictionaryParameterDetailData.setDataTypeId((String) htab_strName_strDataTypeId.get(strAttributeDataType));
					} else {
						dictionaryParameterDetailData.setDataTypeId(DictionaryConstant.DATA_TYPE_ID_STRING);
					}

					dictionaryParameterDetailData.setVendorParameterId(attributeModel.getId());

					Set<AttributeSupportedValueModel> supportedValueModels=attributeModel.getSupportedValues();
					StringBuilder sepValueBuilder = new StringBuilder();
					CharSequence separator=",";
					for(AttributeSupportedValueModel attributeSupportedValueModel:supportedValueModels){
						if(attributeSupportedValueModel.getName() != null && attributeSupportedValueModel.getName().length() > 0){
							sepValueBuilder.append(attributeSupportedValueModel.getName()+":"+attributeSupportedValueModel.getId()).append(",");
						}
					}

					if(sepValueBuilder.length() > 0){
						sepValueBuilder.setLength(sepValueBuilder.length() - separator.length());
					}

					dictionaryParameterDetailData.setPredefinedValues(sepValueBuilder.toString());
					dictionaryParameterDetailData.setAvPair(DictionaryConstant.NO);

					if(attributeModel.isAvPair()){
						dictionaryParameterDetailData.setAvPair(DictionaryConstant.VALUE_YES);
					}else{
						dictionaryParameterDetailData.setAvPair(DictionaryConstant.VALUE_NO);
					}

					if(attributeModel.isTagged()){
						dictionaryParameterDetailData.setHasTag(DictionaryConstant.VALUE_YES);
					}else{
						dictionaryParameterDetailData.setHasTag(DictionaryConstant.VALUE_NO);
					}

					if(attributeModel.isIgnoreCase()){
						dictionaryParameterDetailData.setIgnoreCase(DictionaryConstant.VALUE_YES);
					}else{
						dictionaryParameterDetailData.setIgnoreCase(DictionaryConstant.VALUE_NO);
					}

					int encryptStandard = attributeModel.getEncryptStandard();
					dictionaryParameterDetailData.setEncryptStandard(encryptStandard);

					LengthFormat lengthFormat =attributeModel.getLengthFormat();
					dictionaryParameterDetailData.setLengthFormat(lengthFormat != null ? lengthFormat.getName() : LengthFormat.TLV.getName());

					PaddingType paddingType = attributeModel.getPaddingType();
					dictionaryParameterDetailData.setPaddingType(paddingType != null ? paddingType.getType() : PaddingType.NONE.getType());

					String type = attributeModel.getType();

					if("grouped".equalsIgnoreCase(type)){
						Set<AttributeModel> subAttributeModel=attributeModel.getSubAttributes();
						List<DictionaryParameterDetailData> nestedParameterDetailDataList = recursiveGroupParameterDetailMap(subAttributeModel,vendorDictionary);
						dictionaryParameterDetailData.setNestedParameterDetailList(nestedParameterDetailDataList);
					}
					dictionaryParentAttributeList.add(dictionaryParameterDetailData);
				}
			}
		}


	}


	private List<DictionaryParameterDetailData> recursiveGroupParameterDetailMap(Set<AttributeModel> nestedAttributeSet, VendorInformation vendorInformation) {

		DictionaryParameterDetailData tempParameterDetailData;
		String strAttributeDataType = null;
		List<DictionaryParameterDetailData> nestedAttributeList = null;

		if (nestedAttributeSet != null && !nestedAttributeSet.isEmpty()) {
			nestedAttributeList = new ArrayList<DictionaryParameterDetailData>();
			for (AttributeModel attributeModel : nestedAttributeSet) {
				if (attributeModel != null) {
					tempParameterDetailData = new DictionaryParameterDetailData();
					tempParameterDetailData.setAlias(DictionaryConstant.DEFAULT_ALIAS);
					tempParameterDetailData.setName(attributeModel.getName());
					tempParameterDetailData.setVendorId(vendorInformation.getVendorId());
					strAttributeDataType = attributeModel.getType();

					if (vendorInformation.getVendorId() == DictionaryConstant.STANDARD_VENDOR_ID) {
						tempParameterDetailData.setRadiusRFCDictionaryParameterId(attributeModel.getId());
						tempParameterDetailData.setVendorParameterOveridden(DictionaryConstant.NO);
					} else {
						tempParameterDetailData.setRadiusRFCDictionaryParameterId(DictionaryConstant.RADIUS_RFC_DICTIONARY_PARAMETER_ID_FOR_VSA);
						tempParameterDetailData.setVendorParameterOveridden(DictionaryConstant.YES);
					}

					if (htab_strName_strDataTypeId.containsKey(strAttributeDataType) == true) {
						tempParameterDetailData.setDataTypeId((String) htab_strName_strDataTypeId.get(strAttributeDataType));
					} else {
						tempParameterDetailData.setDataTypeId(DictionaryConstant.DATA_TYPE_ID_STRING);
					}
					tempParameterDetailData.setVendorParameterId(attributeModel.getId());

					Set<AttributeSupportedValueModel> supportedValueModels = attributeModel.getSupportedValues();
					StringBuilder sepValueBuilder = new StringBuilder();
					CharSequence separator = ",";

					for (AttributeSupportedValueModel attributeSupportedValueModel : supportedValueModels) {
						if (attributeSupportedValueModel.getName() != null && attributeSupportedValueModel.getName().length() > 0) {
							sepValueBuilder.append(attributeSupportedValueModel.getName()+":"+attributeSupportedValueModel.getId()).append(",");
						}
					}

					if (sepValueBuilder.length() > 0) {
						sepValueBuilder.setLength(sepValueBuilder.length() - separator.length());
					}

					tempParameterDetailData.setPredefinedValues(sepValueBuilder.toString());
					tempParameterDetailData.setAvPair(DictionaryConstant.NO);

					if (attributeModel.isAvPair()) {
						tempParameterDetailData.setAvPair(DictionaryConstant.VALUE_YES);
					} else {
						tempParameterDetailData.setAvPair(DictionaryConstant.VALUE_NO);
					}

					if (attributeModel.isTagged()) {
						tempParameterDetailData.setHasTag(DictionaryConstant.VALUE_YES);
					} else {
						tempParameterDetailData.setHasTag(DictionaryConstant.VALUE_NO);
					}

					if (attributeModel.isIgnoreCase()) {
						tempParameterDetailData.setIgnoreCase(DictionaryConstant.VALUE_YES);
					} else {
						tempParameterDetailData.setIgnoreCase(DictionaryConstant.VALUE_NO);
					}

					int encryptStandard = attributeModel.getEncryptStandard();
					tempParameterDetailData.setEncryptStandard(encryptStandard);

					LengthFormat lengthFormat = attributeModel.getLengthFormat();
					tempParameterDetailData.setLengthFormat(lengthFormat != null ? lengthFormat.getName() : LengthFormat.TLV.getName());

					PaddingType paddingType = attributeModel.getPaddingType();
					tempParameterDetailData.setPaddingType(paddingType != null ? paddingType.getType() : PaddingType.NONE.getType());

					String type = attributeModel.getType();
					if ("grouped".equalsIgnoreCase(type)) {
						Set<AttributeModel> subAttributeModels = attributeModel.getSubAttributes();
						List<DictionaryParameterDetailData> tempNestedParameterDetailDataList = recursiveGroupParameterDetailMap(subAttributeModels, vendorInformation);
						tempParameterDetailData.setNestedParameterDetailList(tempNestedParameterDetailDataList);
					}
					nestedAttributeList.add(tempParameterDetailData);
				}
			}
		}
		return nestedAttributeList;
	}


	private IDictionaryData getDictionaryData(String userId) {
		IDictionaryData dictionaryData = new DictionaryData();
		String strVendorName = null;
		long iIanaPvtEntNumber = 0;
		Date currentDate = new Date();

		Enumeration enumeration = null;
		enumeration = htab_strVendorName_intObjIanaPvtEntNumber.keys();

		while (enumeration.hasMoreElements()) {
			strVendorName = (String) enumeration.nextElement();
			iIanaPvtEntNumber = ((Long) htab_strVendorName_intObjIanaPvtEntNumber.get(strVendorName)).longValue();
		}


		dictionaryData.setVendorId(iIanaPvtEntNumber);
		dictionaryData.setDictionaryNumber(iIanaPvtEntNumber);
		dictionaryData.setLastModifiedByStaffId(userId);
		dictionaryData.setModalNumber((new Integer(DictionaryConstant.DEFAULT_MODAL_NUMBER)).toString());
		dictionaryData.setName(strVendorName);
		dictionaryData.setEditable(DictionaryConstant.YES);
		dictionaryData.setSystemGenerated(DictionaryConstant.NO);
		dictionaryData.setLastModifiedDate(new Timestamp(currentDate.getTime()));


		Iterator itrDetails = dictionaryParentAttributeList.iterator();
		Set lstDictionaryDetails = new HashSet();
		while (itrDetails.hasNext()) {

			lstDictionaryDetails.add((IDictionaryParameterDetailData) itrDetails.next());

		}

		dictionaryData.setDictionaryParameterDetail(lstDictionaryDetails);

		return dictionaryData;

	}



}
