/**
 *
 */
package com.elitecore.elitesm.web.diameter.dictionary;

import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.elitesm.blmanager.diameter.dictionary.DiameterDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.AuthorizationException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.*;
import com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport.EnvironmentNotSupportedException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicParamDetailData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.exception.AttributeNotFoundException;
import com.elitecore.elitesm.datamanager.radius.dictionary.exception.DuplicateDictionaryConstraintExcpetion;
import com.elitecore.elitesm.util.constants.DictionaryConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.dictionary.forms.CreateDiameterdicForm;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * @author pratik.chauhan
 *
 */
public class CreateDiameterdicAction extends BaseWebAction {

	private static final String SUCCESS_FORWARD                                            = "success";
	private static final String FAILURE_FORWARD                                            = "failure";
	private static final String ACTION_ALIAS = "CREATE_DICTIONARY_ACTION";
	private Hashtable           htab_strName_strDataTypeId                                 = null;
	private DiameterdicData diameterdicData = new DiameterdicData();
	private static final String MODULE="DIAMETER DICTIONARY";


	public ActionForward execute( ActionMapping mapping ,ActionForm form ,HttpServletRequest request ,HttpServletResponse response ) throws Exception {
		Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
		Reader isrDictionary = null;



		try{
			DiameterDictionaryBLManager dictionaryBLManager = new DiameterDictionaryBLManager();
			CreateDiameterdicForm createDiameterdicform=(CreateDiameterdicForm)form;

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String staffId =  ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			htab_strName_strDataTypeId=dictionaryBLManager.getDatatype();
			FormFile formFile = createDiameterdicform.getDictionaryFile();
			isrDictionary = new InputStreamReader(formFile.getInputStream());
			DiameterDictionary dic = DiameterDictionary.getInstance();
			Map<Object, Object> resultMap=dic.readDictionaryForServerManager(isrDictionary);
			convertToBean(resultMap,createDiameterdicform);
			diameterdicData.setCreatedByStaffId(staffId);
			diameterdicData.setLastModifiedByStaffId(staffId);
			String actionAlias=ACTION_ALIAS;
			//Logger.logDebug(MODULE,diameterdicData);
			dictionaryBLManager.create(diameterdicData);
			doAuditing(staffData, actionAlias);
			request.setAttribute("responseUrl", "/listDiameterdic.do");
			ActionMessage message = new ActionMessage("dictionary.create.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveMessages(request, messages);
			isrDictionary.close();
			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName());
			return mapping.findForward(SUCCESS_FORWARD);


		}catch (AttributeNotFoundException e) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
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
		catch (AuthorizationException authExp) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		}
		catch (EnvironmentNotSupportedException envException) {

			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + envException.getMessage());
			Logger.logTrace(MODULE, envException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(envException);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		catch (CommunicationException connException) {

			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + connException.getMessage());
			Logger.logTrace(MODULE, connException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(connException);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		catch (OperationFailedException opException) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + opException.getMessage());
			Logger.logTrace(MODULE, opException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(opException);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}

		catch (ConstraintViolationException conException) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + conException.getMessage());
			Logger.logTrace(MODULE, conException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(conException);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		catch (FormatInvalidException forException) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + forException.getMessage());
			Logger.logTrace(MODULE, forException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(forException);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
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
		catch (InvalidFileExtensionException invException) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + invException.getMessage());
			Logger.logTrace(MODULE, invException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invException);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		catch (InvalidValueException invalidValueException) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + invalidValueException.getMessage());
			Logger.logTrace(MODULE, invalidValueException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invalidValueException);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		catch (ValueOutOfRangeException valueOutOfRangeException) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + valueOutOfRangeException.getMessage());
			Logger.logTrace(MODULE, valueOutOfRangeException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(valueOutOfRangeException);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		catch (DataValidationException validationException) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + validationException.getMessage());
			Logger.logTrace(MODULE, validationException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(validationException);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		catch (DataManagerException mgrException) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager (DataMgr) operation , reason :" + mgrException.getMessage());
			Logger.logTrace(MODULE, mgrException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(mgrException);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		catch (Exception exp) {
			if (isrDictionary != null)
				isrDictionary.close();
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);

			ActionMessage message = new ActionMessage("dictionary.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
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


	}

	private void convertToBean(Map<Object, Object> resultMap, CreateDiameterdicForm createDiameterdicform) throws DataManagerException{

		Long vendorId=Long.parseLong(resultMap.get("vendor_id").toString());

		diameterdicData.setApplicationId(Long.parseLong(resultMap.get("application_id").toString()));
		diameterdicData.setApplicationName(resultMap.get("application_name").toString());
		diameterdicData.setVendorId(vendorId);
		diameterdicData.setVendorName(resultMap.get("vendor_name").toString());

		diameterdicData.setDescription(createDiameterdicform.getDescription());
		diameterdicData.setCommonStatusId((createDiameterdicform.getCommonStatusId() == null ? DictionaryConstant.HIDE_STATUS_ID : DictionaryConstant.SHOW_STATUS_ID));
		diameterdicData.setCreateDate(getCurrentTimeStemp());
		diameterdicData.setLastModifiedDate(getCurrentTimeStemp());
		diameterdicData.setModalNumber((new Integer(DictionaryConstant.DEFAULT_MODAL_NUMBER)).toString());
		diameterdicData.setEditable(DictionaryConstant.YES);
		diameterdicData.setStatusChangedDate(getCurrentTimeStemp());
		diameterdicData.setSystemGenerated(DictionaryConstant.NO);
		diameterdicData.setDictionaryNumber(vendorId);

		Map attributeMap=(Map)resultMap.get("attribute_list");

		Iterator iterator=attributeMap.keySet().iterator();
		String strAttributeDataType = null;
		List<DiameterdicParamDetailData> diameterdicParamDetailList = new ArrayList<DiameterdicParamDetailData>();
		while (iterator.hasNext()) {

			Map<String, Object> attribute = (Map<String,Object>)attributeMap.get((String)iterator.next());

			DiameterdicParamDetailData diameterdicParamDetailData = new DiameterdicParamDetailData();
			diameterdicParamDetailData.setAlias(DictionaryConstant.DEFAULT_ALIAS);
			Integer attributeId =(Integer)attribute.get("attribute_id");
			String name=(String)attribute.get("attribute_name");
			if(name!=null && name.trim().length()>0){
				diameterdicParamDetailData.setName(name);
			}else{
				throw new DataManagerException("Attribute Name Not Found, Attribute Id:"+attributeId);
			}

			diameterdicParamDetailData.setVendorId(vendorId);
			strAttributeDataType = (String)attribute.get("attribute_type");

			if(vendorId==DictionaryConstant.STANDARD_VENDOR_ID){
				diameterdicParamDetailData.setVendorParameterOveridden(DictionaryConstant.NO);
			}else{
				diameterdicParamDetailData.setVendorParameterOveridden(DictionaryConstant.YES);
			}

			if(htab_strName_strDataTypeId.containsKey(strAttributeDataType) == true) {
				diameterdicParamDetailData.setDataTypeId((String) htab_strName_strDataTypeId.get(strAttributeDataType));
			} else {
				diameterdicParamDetailData.setDataTypeId(DictionaryConstant.DATA_TYPE_ID_STRING);
			}

			diameterdicParamDetailData.setVendorParameterId(attributeId);

			String predefinedValue=attribute.get("supported_value").toString();

			diameterdicParamDetailData.setPredefinedValues(predefinedValue);
			diameterdicParamDetailData.setDictionaryNumber(vendorId);

			String encryption=attribute.get("attribute_encryption").toString();
			diameterdicParamDetailData.setEncryption(encryption);

			String strProtected=attribute.get("attribute_protected").toString();
			diameterdicParamDetailData.setStrProtected(strProtected);

			String mandatory=attribute.get("attribute_mandatory").toString();
			diameterdicParamDetailData.setMandatory(mandatory);

			if(strAttributeDataType.equals("Grouped")){

				//fixed group attribute

				Map fixedGroupedAttribute = (Map)attribute.get("fixed_group_attribute_list");
				diameterdicParamDetailData.setFixedGroupedAttribute(fixedGroupedAttribute);

				Map requiredGroupedAttribute = (Map)attribute.get("required_group_attribute_list");
				diameterdicParamDetailData.setRequiredGroupedAttribute(requiredGroupedAttribute);

				Map optionalGroupedAttribute = (Map)attribute.get("optional_group_attribute_list");
				diameterdicParamDetailData.setOptionalGroupedAttribute(optionalGroupedAttribute);

			}
			diameterdicParamDetailList.add(diameterdicParamDetailData);
		}
		diameterdicData.setDiameterdicParamDetailList(diameterdicParamDetailList);

	}

}
