package com.elitecore.elitesm.web.radius.dictionary;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.DictionaryParseException;
import com.elitecore.coreradius.commons.util.constants.LengthFormat;
import com.elitecore.coreradius.commons.util.constants.PaddingType;
import com.elitecore.coreradius.commons.util.dictionary.AttributeModel;
import com.elitecore.coreradius.commons.util.dictionary.AttributeSupportedValueModel;
import com.elitecore.coreradius.commons.util.dictionary.DictionaryModel;
import com.elitecore.coreradius.commons.util.dictionary.VendorInformation;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.NullValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.exception.DuplicateDictionaryConstraintExcpetion;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.DictionaryConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.dictionary.forms.CreateDictionaryForm;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.*;

public class CreateDictionaryAction extends BaseDictionaryAction {

	private static final String SUCCESS_FORWARD                                            = "success";
	private static final String FAILURE_FORWARD                                            = "failure";

	private String              defaultStaff                                               = "STF0000";

	private Hashtable           htab_strName_strDataTypeId                                 = null;
	private Hashtable           htab_strVendorName_intObjIanaPvtEntNumber                  = new Hashtable();
	// private Hashtable           htab_intObjVendorParameterId_dictionaryParameterDetailData = new Hashtable();
	private List<DictionaryParameterDetailData> dictionaryParentAttributeList=null;
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DICTIONARY_ACTION;

	public ActionForward execute( ActionMapping mapping ,ActionForm form , HttpServletRequest request ,HttpServletResponse response ) throws Exception {
		if(checkAccess(request, ACTION_ALIAS)){
			htab_strVendorName_intObjIanaPvtEntNumber = new Hashtable();
			//htab_intObjVendorParameterId_dictionaryParameterDetailData = new Hashtable();
			dictionaryParentAttributeList = new ArrayList<DictionaryParameterDetailData>();
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			Reader isrDictionary = null;
			try {
				CreateDictionaryForm addForm = (CreateDictionaryForm) form;
				RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
				htab_strName_strDataTypeId = dictionaryBLManager.getDatatype();
				FormFile formFile = addForm.getDictionaryFile();
				isrDictionary = new InputStreamReader(formFile.getInputStream());

				Dictionary dic = Dictionary.getInstance();
				populteMap(dic.readDictionaryForServerManager(isrDictionary));
				isrDictionary.close();
				String staffId =  ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();

				addForm.setCreatedByStaffId(staffId);
				addForm.setLastModifiedByStaffId(staffId);
				IDictionaryData dictionaryData = getDictionaryData(addForm);

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;

				dictionaryBLManager.createDictionary(dictionaryData);
				doAuditing(staffData, actionAlias);
				request.setAttribute("responseUrl", "/listDictionary.do");
				ActionMessage message = new ActionMessage("dictionary.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveMessages(request, messages);
				isrDictionary.close();
				Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName());
				return mapping.findForward(SUCCESS_FORWARD);
			}
			catch (DictionaryParseException dicExp) {
				if (isrDictionary != null)
					isrDictionary.close();
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + dicExp.getMessage());
				Logger.logTrace(MODULE, dicExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dicExp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.create.failure");
				ActionMessage message1 = new ActionMessage("dictionary.parse.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message1);
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch (DuplicateDictionaryConstraintExcpetion e) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason Duplicate Dic:" + e);
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.create.failure");
				ActionMessage message1 = new ActionMessage("dictionary.duplicate");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				messages.add("information", message1);
				saveErrors(request, messages);

			}
			catch (NullValueException nullValueException) {
				if (isrDictionary != null)
					isrDictionary.close();
				Logger.logError(MODULE, "Error during Data Manager operation , reason NullValueException:" + nullValueException.getMessage());
				Logger.logTrace(MODULE, nullValueException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(nullValueException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.create.failure");
				ActionMessage message1 = new ActionMessage("errors.required", nullValueException.getSourceField());
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				messages.add("information", message1);
				saveErrors(request, messages);
			}

			catch (Exception exp) {
				if (isrDictionary != null)
					isrDictionary.close();
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("dictionary.create.failure");
				ActionMessage reasonMessage = new ActionMessage("reason.failure", exp.getMessage());
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				messages.add("information", reasonMessage);
				saveErrors(request, messages);
			}
			finally {
				if (isrDictionary != null)
					isrDictionary.close();
			}

			// TODO : Baiju - set error message and then forward to error page.
	        /*
	         * Logger.logTrace(MODULE, "Returning error forward from " + getClass().getName()); errors.add("fatal", new ActionError("dictionary.create.failure")); saveErrors(request, errors);
	         */
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}



	/* This method populte the vender Map of the class */
	private void populteMap( DictionaryModel dictionaryModel1) {


		for(VendorInformation vendorDictionary : dictionaryModel1.getIdToVendorInformation().values()) {
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


	private List<DictionaryParameterDetailData> recursiveGroupParameterDetailMap(Set<AttributeModel> nestedAttributeSet, VendorInformation vendorDictionary) {

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
					tempParameterDetailData.setVendorId(vendorDictionary.getVendorId());
					strAttributeDataType = attributeModel.getType();

					if (vendorDictionary.getVendorId() == DictionaryConstant.STANDARD_VENDOR_ID) {
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
						List<DictionaryParameterDetailData> tempNestedParameterDetailDataList = recursiveGroupParameterDetailMap(subAttributeModels, vendorDictionary);
						tempParameterDetailData.setNestedParameterDetailList(tempNestedParameterDetailDataList);
					}
					nestedAttributeList.add(tempParameterDetailData);
				}
			}
		}

		return nestedAttributeList;
	}

	private IDictionaryData getDictionaryData( CreateDictionaryForm addForm ) {
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

		dictionaryData.setDescription(addForm.getDescription());
		dictionaryData.setVendorId(iIanaPvtEntNumber);
		dictionaryData.setDictionaryNumber(iIanaPvtEntNumber);
		dictionaryData.setLastModifiedByStaffId(addForm.getLastModifiedByStaffId());
		dictionaryData.setModalNumber((new Integer(DictionaryConstant.DEFAULT_MODAL_NUMBER)).toString());
		dictionaryData.setName(strVendorName);
		dictionaryData.setCommonStatusId((addForm.getCommonStatusId() == null ? DictionaryConstant.HIDE_STATUS_ID : DictionaryConstant.SHOW_STATUS_ID));
		dictionaryData.setCreatedByStaffId(addForm.getCreatedByStaffId());
		dictionaryData.setEditable(DictionaryConstant.YES);
		dictionaryData.setSystemGenerated(DictionaryConstant.NO);
		dictionaryData.setCreateDate(new Timestamp(currentDate.getTime()));
		dictionaryData.setLastModifiedDate(new Timestamp(currentDate.getTime()));
		dictionaryData.setStatusChangedDate(new Timestamp(currentDate.getTime()));

		Iterator itrDetails = dictionaryParentAttributeList.iterator();
		Set lstDictionaryDetails = new LinkedHashSet();
		while (itrDetails.hasNext()) {
			lstDictionaryDetails.add((IDictionaryParameterDetailData) itrDetails.next());
		}
		dictionaryData.setDictionaryParameterDetail(lstDictionaryDetails);

		return dictionaryData;
	}
}
