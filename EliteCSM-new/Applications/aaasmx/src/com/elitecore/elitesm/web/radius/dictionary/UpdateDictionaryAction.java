package com.elitecore.elitesm.web.radius.dictionary;

import com.elitecore.commons.base.Strings;
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
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.exception.UpdateDictionaryException;
import com.elitecore.elitesm.util.constants.DictionaryConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.dictionary.forms.UpdateDictionaryForm;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.*;

public class UpdateDictionaryAction extends BaseDictionaryAction {

	private static final String SUCCESS_FORWARD                                            = "success";
	private static final String UPDATE_FORWARD                                             = "updateDictionary";
	private static final String FAILURE_FORWARD                                            = "failure";

	private Hashtable           htab_strName_strDataTypeId;
	private Hashtable           htab_strVendorName_intObjIanaPvtEntNumber;
	private List<DictionaryParameterDetailData> dictionaryParentAttributeList;

	private String              defaultStaff                                               = "STF0000";
	private static final String ACTION_ALIAS = "UPDATE_DICTIONARY_ACTION";

	public ActionForward execute( ActionMapping mapping ,
								  ActionForm form ,
								  HttpServletRequest request ,
								  HttpServletResponse response ) throws Exception {
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			htab_strName_strDataTypeId = new Hashtable();
			htab_strVendorName_intObjIanaPvtEntNumber = new Hashtable();
			dictionaryParentAttributeList = new ArrayList<DictionaryParameterDetailData>();


			ActionErrors errors = new ActionErrors();
			defaultStaff = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			try {
				String strdictionaryId = request.getParameter("dictionaryId");

				if (Strings.isNullOrEmpty(strdictionaryId) == false) {
					UpdateDictionaryForm updateForm = (UpdateDictionaryForm) form;
					RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
					Reader isrDictionary = null;
					String action = updateForm.getAction();
					if (action == null || action.equalsIgnoreCase("edit")) {
						Collection<String> colDictionaryId = new ArrayList();
						colDictionaryId.add(strdictionaryId);
						List dictionaryList = dictionaryBLManager.getAllDictionaryById(colDictionaryId);

						IDictionaryData dictionaryData;

						if (dictionaryList.size() == 1) {
							dictionaryData = (IDictionaryData) dictionaryList.get(0);
							updateForm = getDictionaryValues(dictionaryData);
							request.setAttribute("updateDictionaryForm", updateForm);
							return mapping.findForward(UPDATE_FORWARD);
						}
					} else if (action.equalsIgnoreCase("update")) {
						htab_strName_strDataTypeId = dictionaryBLManager.getDatatype();
						FormFile formFile = updateForm.getDictionaryFile();
						isrDictionary = new InputStreamReader(formFile.getInputStream());
						Dictionary dic = Dictionary.getInstance();
						populteMap(dic.readDictionaryForServerManager(isrDictionary));
						IDictionaryData dictionaryData = getDictionaryData(updateForm);

						if(updateForm.getVendorId()!=dictionaryData.getVendorId()){
							throw new UpdateDictionaryException("Vendor can not be changed while updating Dictionary");
						}
    					/*
    					 * System.out.println("*********** UPDATE 1 ********************* "); //String strFileName = UploadFile(updateForm.getDictionaryFile()); System.out.println("*********** UPDATE 2
    					 * ********************* "); isrDictionary = new InputStreamReader(updateForm.getDictionaryFile().getInputStream()); //isrDictionary = new FileReader(new File(strFileName));
    					 * System.out.println("*********** UPDATE 3 ********************* "); parseDictionary(isrDictionary); System.out.println("*********** UPDATE 4 ********************* ");
    					 * IDictionaryData dictionaryData = getDictionaryData(updateForm); System.out.println("********** HERE ACCESSING THE UPDATE ACTION *************");
    					 */

						// if(dictionaryData.getVendorId() != 0)..

						IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						String actionAlias = ACTION_ALIAS;
						dictionaryBLManager.updateBasicDetails(dictionaryData,staffData,actionAlias);
						// else
						// throw new DataManagerException("Invalid Dictionary File");

						request.setAttribute("responseUrl", "/listDictionary.do");
						ActionMessage message = new ActionMessage("dictionary.update.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information", message);
						saveMessages(request, messages);
						isrDictionary.close();
						// removeFile(strFileName);

						Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName());
						return mapping.findForward(SUCCESS_FORWARD);
					}
				}
			}
			catch(UpdateDictionaryException invdException){
				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +invdException.getMessage());
				Logger.logTrace(MODULE,invdException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invdException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.dictionaryupdatefail");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch (DictionaryParseException dicExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + dicExp.getMessage());
				Logger.logTrace(MODULE,dicExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dicExp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch (AuthorizationException authExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +authExp.getMessage());
				Logger.logTrace(MODULE,authExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);

			}catch (EnvironmentNotSupportedException envException) {

				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +envException.getMessage());
				Logger.logTrace(MODULE,envException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(envException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch (CommunicationException connException) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +connException.getMessage());
				Logger.logTrace(MODULE,connException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(connException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch(OperationFailedException opException)
			{
				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +opException.getMessage());
				Logger.logTrace(MODULE,opException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(opException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch(ConstraintViolationException conException)
			{
				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +conException.getMessage());
				Logger.logTrace(MODULE,conException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(conException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch(FormatInvalidException forException)
			{
				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +forException.getMessage());
				Logger.logTrace(MODULE,forException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(forException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);

			}
			catch(NullValueException nullValueException)
			{

				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +nullValueException.getMessage());
				Logger.logTrace(MODULE,nullValueException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(nullValueException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch(InvalidFileExtensionException invException)
			{

				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +invException.getMessage());
				Logger.logTrace(MODULE,invException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch(InvalidValueException invalidValueException)
			{

				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +invalidValueException.getMessage());
				Logger.logTrace(MODULE,invalidValueException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invalidValueException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch(ValueOutOfRangeException valueOutOfRangeException)
			{

				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +valueOutOfRangeException.getMessage());
				Logger.logTrace(MODULE,valueOutOfRangeException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(valueOutOfRangeException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch(DataValidationException validationException)
			{
				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +validationException.getMessage());
				Logger.logTrace(MODULE,validationException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(validationException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch(DataManagerException mgrException)
			{
				Logger.logError(MODULE, "Error during Data Manager operation , reason :" +mgrException.getMessage());
				Logger.logTrace(MODULE,mgrException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(mgrException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch (Exception exp){

				exp.printStackTrace();
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("dictionary.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			// TODO : Baiju - set error message and then forward to error page.
			Logger.logTrace(MODULE, "Returning error forward from " + getClass().getName());
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

	private UpdateDictionaryForm getDictionaryValues( IDictionaryData dictionaryData ) {

		UpdateDictionaryForm updateDictionaryForm = new UpdateDictionaryForm();
		updateDictionaryForm.setDictionaryId(dictionaryData.getDictionaryId());
		updateDictionaryForm.setName(dictionaryData.getName());
		updateDictionaryForm.setDescription(dictionaryData.getDescription());
		updateDictionaryForm.setModalNumber(dictionaryData.getModalNumber());
		updateDictionaryForm.setEditable(dictionaryData.getEditable());
		updateDictionaryForm.setSystemGenerated(dictionaryData.getSystemGenerated());
		updateDictionaryForm.setDictionaryNumber(dictionaryData.getDictionaryNumber());
		updateDictionaryForm.setCommonStatusId(dictionaryData.getCommonStatusId());
		updateDictionaryForm.setLastModifiedDate(dictionaryData.getLastModifiedDate());
		updateDictionaryForm.setLastModifiedByStaffId(dictionaryData.getLastModifiedByStaffId());
		updateDictionaryForm.setVendorId(dictionaryData.getVendorId());
		updateDictionaryForm.setCreateDate(dictionaryData.getCreateDate());
		updateDictionaryForm.setCreatedByStaffId(dictionaryData.getCreatedByStaffId());
		updateDictionaryForm.setStatusChangedDate(dictionaryData.getStatusChangedDate());
		updateDictionaryForm.setDictionaryParameterDetail(dictionaryData.getDictionaryParameterDetail());

		updateDictionaryForm.setLastModifiedByStaff(dictionaryData.getLastModifiedByStaff());
		updateDictionaryForm.setCreatedByStaff(dictionaryData.getCreatedByStaff());

		return updateDictionaryForm;
	}



	private IDictionaryData getDictionaryData( UpdateDictionaryForm addForm ) {
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

		dictionaryData.setDictionaryId(addForm.getDictionaryId());
		dictionaryData.setDescription(addForm.getDescription());
		dictionaryData.setVendorId(iIanaPvtEntNumber);
		dictionaryData.setDictionaryNumber(iIanaPvtEntNumber);
		dictionaryData.setLastModifiedByStaffId(defaultStaff);
		dictionaryData.setModalNumber((new Integer(DictionaryConstant.DEFAULT_MODAL_NUMBER)).toString());
		dictionaryData.setName(strVendorName);
		// System.out.println("Common Status : "+addForm.getCommonStatusId());
		dictionaryData.setCommonStatusId((addForm.getCommonStatusId() == null ? DictionaryConstant.HIDE_STATUS_ID : DictionaryConstant.SHOW_STATUS_ID));
		// System.out.println("Set Status : "+dictionaryData.getCommonStatusId());
		// System.out.println("Created By Staff Id : "+addForm.getCreatedByStaffId());
		dictionaryData.setCreatedByStaffId(addForm.getCreatedByStaffId());
		dictionaryData.setEditable(DictionaryConstant.YES);
		dictionaryData.setSystemGenerated(DictionaryConstant.NO);
		dictionaryData.setCreateDate(addForm.getCreateDate());
		dictionaryData.setLastModifiedDate(new Timestamp(currentDate.getTime()));
		dictionaryData.setStatusChangedDate(new Timestamp(currentDate.getTime()));

		Set<DictionaryParameterDetailData> listDictionaryDetails = new LinkedHashSet<DictionaryParameterDetailData>(dictionaryParentAttributeList);
		dictionaryData.setDictionaryParameterDetail(listDictionaryDetails);
		return dictionaryData;
	}
}
