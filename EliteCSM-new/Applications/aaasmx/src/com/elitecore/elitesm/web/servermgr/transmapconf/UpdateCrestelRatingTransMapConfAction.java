package com.elitecore.elitesm.web.servermgr.transmapconf;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.DummyResponseParameterData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstDetailData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.transmapconf.forms.UpdateCrestelRatingTransMapConfigForm;

public class UpdateCrestelRatingTransMapConfAction  extends BaseWebAction{

	private static final String MODULE = UpdateCrestelRatingTransMapConfAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_TRANSLATION_MAPPING_CONFIG;
	private static final String ACTION_FORWARD = "openUpdateCrestelRatingTransMapConf";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {

		UpdateCrestelRatingTransMapConfigForm translationMappingConfigForm = (UpdateCrestelRatingTransMapConfigForm)form;

		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			String strTranslationMapConfigId = request.getParameter("translationMapConfigId");
			String translationMapConfigId = null;
			if(strTranslationMapConfigId != null){
				translationMapConfigId = strTranslationMapConfigId;
			}
			if (translationMapConfigId == null) {
				translationMapConfigId = translationMappingConfigForm.getTranslationMapConfigId();
			}
			TranslationMappingConfData translationMappingConfData =(TranslationMappingConfData) request.getAttribute("translationMappingConfData");
			TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();

			if(translationMappingConfigForm.getAction()==null || translationMappingConfigForm.getAction().equals("")){
				if(translationMappingConfData!=null){
					translationMappingConfData = blManager.getTranslationMappingConfDataById(translationMapConfigId);
					request.setAttribute("translationMappingConfData", translationMappingConfData);
					translationMappingConfigForm.setDummyResponse(translationMappingConfData.getDummyResponse());
					translationMappingConfigForm.setTranslationMappingConfData(translationMappingConfData);
					
					List<TranslationMappingConfData> baseTranslationMappingConfDataList = blManager.getTranslationMappingConfigList(translationMappingConfData);
					/*List<TranslationMappingConfData> cycleTranslationMappingConfList = new ArrayList<TranslationMappingConfData>();
					getCycleTranslationMappingConfData(translationMapConfigId, baseTranslationMappingConfDataList,cycleTranslationMappingConfList);
					for(TranslationMappingConfData cycleTranslationMappingConfData : cycleTranslationMappingConfList){
						baseTranslationMappingConfDataList.remove(cycleTranslationMappingConfData);
					}*/
					
					translationMappingConfigForm.setBaseTranslationMappingConfDataList(baseTranslationMappingConfDataList);
					if(translationMappingConfData.getBaseTranslationMapConfigId() != null){
						translationMappingConfigForm.setBaseTranslateConfigId(translationMappingConfData.getBaseTranslationMapConfigId());
					}
				}
				return mapping.findForward(ACTION_FORWARD);

			}else if(translationMappingConfigForm.getAction()!=null && translationMappingConfigForm.getAction().equals("save")){

				translationMappingConfData =blManager.getTranslationMappingConfData(translationMapConfigId);
				
				translationMappingConfData.setDummyResponse(translationMappingConfigForm.getDummyResponse());
				if(Strings.isNullOrBlank(translationMappingConfigForm.getBaseTranslateConfigId()) == false && ("0".equals(translationMappingConfigForm.getBaseTranslateConfigId()) == false)) {
					translationMappingConfData.setBaseTranslationMapConfigId(translationMappingConfigForm.getBaseTranslateConfigId());
				} else {
					translationMappingConfData.setBaseTranslationMapConfigId(null);
				}

				String strMappingIndex = request.getParameter("mappingIndex");
				int mappingIndex = Integer.parseInt(strMappingIndex);
				List<TranslationMappingInstData> instanceDataList = new LinkedList<TranslationMappingInstData>();
				Set<TranslationMappingInstDetailData> detailDataList = new LinkedHashSet<TranslationMappingInstDetailData>();
				
				for(int i=0;i<mappingIndex ;i++){

					int index = i+1;

					String mappingName = request.getParameter("mappingName"+index);

					if(mappingName!=null && mappingName.trim().length()>0){
						TranslationMappingInstData instanceData =  new TranslationMappingInstData();

						String inMessage = request.getParameter("inMessageMapping"+index);
						String defaultMapping = request.getParameter("defaultMappingChkBox"+index);
						String outMessage = request.getParameter("outMessageMapping"+index);
						String dummyResponse = request.getParameter("dummyResponseChkBox"+index);
						
						instanceData.setMappingName(mappingName);
						instanceData.setInMessage(inMessage);
						instanceData.setOutMessage(outMessage);
						if(defaultMapping!=null && defaultMapping.equals("Y")){
							instanceData.setDefaultMapping(defaultMapping);
						}else{
							instanceData.setDefaultMapping("N");	
						}
						if(dummyResponse != null) {
							instanceData.setDummyResponse(Boolean.toString(true));
						}else{
							instanceData.setDummyResponse(Boolean.toString(false));
						}

						Logger.logDebug(MODULE, "TranslationMappingInstData: "+instanceData);

						List<TranslationMappingInstDetailData> detailDataSet = new ArrayList<TranslationMappingInstDetailData>();

						String reqTableId = "requestTranslationMapTable";
						detailDataSet.addAll(getMappingDetailList(request,index,reqTableId,"N",TranslationMappingConfigConstants.REQUEST_PARAMETERS));
						detailDataList.addAll(getMappingDetailList(request,index,reqTableId,"N",TranslationMappingConfigConstants.REQUEST_PARAMETERS));

						
						String respTableId = "responseTranslationMapTable";
						detailDataSet.addAll(getMappingDetailList(request,index,respTableId,"N",TranslationMappingConfigConstants.RESPONSE_PARAMETERS));
						detailDataList.addAll(getMappingDetailList(request,index,respTableId,"N",TranslationMappingConfigConstants.RESPONSE_PARAMETERS));

						instanceData.setTranslationMappingInstDetailDataList(detailDataSet);
						instanceDataList.add(instanceData);
					}
				}

				//default Mapping
				Set<TranslationMappingInstDetailData> defaultDetailDataSet = new LinkedHashSet<TranslationMappingInstDetailData>();
				int index=0;

				String reqTableId = "requestTranslationMapTable";
				defaultDetailDataSet.addAll(getMappingDetailList(request,index,reqTableId,"Y",TranslationMappingConfigConstants.REQUEST_PARAMETERS));

				String respTableId = "responseTranslationMapTable";
				defaultDetailDataSet.addAll(getMappingDetailList(request,index,respTableId,"Y",TranslationMappingConfigConstants.RESPONSE_PARAMETERS));

				translationMappingConfData.setDefaultTranslationMappingDetailDataList(new  ArrayList<TranslationMappingInstDetailData>(defaultDetailDataSet));
				translationMappingConfData.setTranslationMappingList(new ArrayList<TranslationMappingInstDetailData>(detailDataList));
				translationMappingConfData.setTranslationMappingInstDataList(instanceDataList);

				//Dummy Response Parameters

				String strOutFields[] = request.getParameterValues("transRatingField");
				String strValues[] = request.getParameterValues("transRatingValue");

				if(strOutFields!=null && strValues!=null && strOutFields.length == strValues.length && strOutFields.length>0){
					List<DummyResponseParameterData> dummyResponseParameterDataList = new LinkedList<DummyResponseParameterData>();
					for (int i = 0; i < strOutFields.length; i++) {

						DummyResponseParameterData parameterData = new DummyResponseParameterData();
						parameterData.setOutField(strOutFields[i]);
						parameterData.setValue(strValues[i]);
						dummyResponseParameterDataList.add(parameterData);

					}
					translationMappingConfData.setDummyResponseParameterDataList(dummyResponseParameterDataList);
				}


				request.setAttribute("translationMappingConfData",translationMappingConfData);

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

				blManager.updateById(translationMappingConfData, staffData);;

				request.setAttribute("responseUrl","/viewTranslationMappingConfigBasicDetail.do?translationMapConfigId="+translationMappingConfData.getTranslationMapConfigId()); 
				ActionMessage message = new ActionMessage("transmapconf.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS);
			}
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch(ConstraintViolationException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("transmapconf.duplicatemessage.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("transmapconf.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
	private List<TranslationMappingInstDetailData> getMappingDetailList(HttpServletRequest request, int index,String tableId, String defaultMapping,String mappingTypeId){
		List<TranslationMappingInstDetailData> detailDataList = new ArrayList<TranslationMappingInstDetailData>();

		String checkExpression[] = request.getParameterValues("checkExpression"+tableId+index);
		String mappingExpression[] = request.getParameterValues("mappingExpression"+tableId+index);
		String defaultValues[] = request.getParameterValues("defaultValue"+tableId+index);
		String valueMappings[] = request.getParameterValues("valueMapping"+tableId+index);

		if( checkExpression!=null && 
				mappingExpression!=null && 
				defaultValues!=null && 
				valueMappings!=null && 
				checkExpression.length==mappingExpression.length && 
				mappingExpression.length==defaultValues.length && 
				defaultValues.length==valueMappings.length
		){
			for (int j = 0; j < checkExpression.length; j++) {

				if(checkExpression[j]!=null && checkExpression[j].trim().length()>0){

					TranslationMappingInstDetailData detailData = new TranslationMappingInstDetailData();
					detailData.setCheckExpression(checkExpression[j]);
					detailData.setMappingExpression(mappingExpression[j]);
					detailData.setDefaultMapping(defaultMapping);
					detailData.setDefaultValue(defaultValues[j]);
					detailData.setValueMapping(valueMappings[j]);
					detailData.setMappingTypeId(mappingTypeId);
					detailDataList.add(detailData);
					Logger.logInfo(MODULE, "detailData: "+ detailData);

				}
			}
		}
		return detailDataList;
	}
	
	/*private void getCycleTranslationMappingConfData(Long translationMapConfigId, List<TranslationMappingConfData> translationMappingConfDataList,List<TranslationMappingConfData> cycleTranslationMappingConfData) {
		for(TranslationMappingConfData translationMappingConfData : translationMappingConfDataList){
			if(translationMappingConfData.getBaseTranslationMapConfigId() != null && translationMappingConfData.getBaseTranslationMapConfigId().equals(translationMapConfigId)) {
				cycleTranslationMappingConfData.add(translationMappingConfData);
				getCycleTranslationMappingConfData(translationMappingConfData.getTranslationMapConfigId(), translationMappingConfDataList, cycleTranslationMappingConfData);
			}
		}
	}*/
	}
