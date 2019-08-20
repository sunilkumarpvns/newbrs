package com.elitecore.elitesm.web.servermgr.copypacket;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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

import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketDummyResponseParameterData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapDetailData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.copypacket.forms.CreateCopyPacketMappingConfDetailForm;
import com.elitecore.elitesm.web.servermgr.copypacket.forms.CreateCopyPacketTransMappingForm;

public class CreateCopyPacketMappingConfDetailAction extends BaseWebAction {

	private static final String MODULE = CreateCopyPacketMappingConfDetailAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG;
	private static final String CRESTEL_RATING_TRANS_MAP_CONFIG ="createCopyPacketTransMapConfig";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		CreateCopyPacketMappingConfDetailForm createCopyPacketMappingDetailForm = (CreateCopyPacketMappingConfDetailForm) form;
		CreateCopyPacketTransMappingForm copyPacketMappingConfForm = (CreateCopyPacketTransMappingForm)request.getSession().getAttribute("copyPacketMappingConfForm");
		try{
			checkActionPermission(request, ACTION_ALIAS);
			if(createCopyPacketMappingDetailForm.getAction()==null || createCopyPacketMappingDetailForm.getAction().equals("") ){
				return mapping.findForward(CRESTEL_RATING_TRANS_MAP_CONFIG);

			}else if (createCopyPacketMappingDetailForm.getAction().equals("save")){
				CopyPacketTranslationConfData copyPacketMappingConfData = convertFormToBean(copyPacketMappingConfForm);
				String strMappingIndex = request.getParameter("mappingIndex");
				int mappingIndex = Integer.parseInt(strMappingIndex);
				Logger.logDebug(MODULE, "Mapping Index : " + mappingIndex);
				List<CopyPacketTranslationMapData> instanceDataList = new ArrayList<CopyPacketTranslationMapData>();
				for(int i=0;i<mappingIndex ;i++){
					int index = i+1;
					String mappingName = request.getParameter("mappingName"+index);
					if(mappingName!=null && mappingName.trim().length()>0){
						CopyPacketTranslationMapData instanceData =  new CopyPacketTranslationMapData();
						String inExpression = request.getParameter("inExpression"+index);
						String defaultMapping = request.getParameter("defaultMappingChkBox"+index);
						String dummyResponse = request.getParameter("dummyResponseChkBox"+index);
						instanceData.setMappingName(mappingName);
						instanceData.setInExpression(inExpression);
						instanceData.setCopyPacketTransConfId(copyPacketMappingConfData.getCopyPacketTransConfId());
						if(dummyResponse != null) {
							instanceData.setDummyResponse(Boolean.toString(true));
						}else{
							instanceData.setDummyResponse(Boolean.toString(false));
						}
						if(defaultMapping!=null){
							int idx=0;
							instanceData.setIsDefaultMapping(Boolean.toString(true));
							List<CopyPacketTranslationMapDetailData> defaultDetailData = new ArrayList<CopyPacketTranslationMapDetailData>();
							String defaultReqTable = "defaultRequestMapping";
							String defaultRespTable = "defaultResponseMapping";
							defaultDetailData.addAll(getMappingDetailList(request, idx, defaultReqTable, TranslationMappingConfigConstants.REQUEST_PARAMETERS));
							defaultDetailData.addAll(getMappingDetailList(request, idx, defaultRespTable, TranslationMappingConfigConstants.RESPONSE_PARAMETERS));
							instanceData.setCopyPacketTransMapDetail(defaultDetailData);

						}else{
							instanceData.setIsDefaultMapping(Boolean.toString(false));
							List<CopyPacketTranslationMapDetailData> detailDataSet = new ArrayList<CopyPacketTranslationMapDetailData>();

							String reqTableId = "requestMapping";
							detailDataSet.addAll(getMappingDetailList(request,index,reqTableId,TranslationMappingConfigConstants.REQUEST_PARAMETERS));

							String respTableId = "responseMapping";
							detailDataSet.addAll(getMappingDetailList(request,index,respTableId,TranslationMappingConfigConstants.RESPONSE_PARAMETERS));

							instanceData.setCopyPacketTransMapDetail(detailDataSet);

						}
						instanceDataList.add(instanceData);
					}
				}
				copyPacketMappingConfData.setCopyPacketTransMapData(instanceDataList);

				String tableIdForDummyResp = "tableDummyResponseParams";
				String strOutFields[] = request.getParameterValues("outField"+tableIdForDummyResp);
				String strValues[] = request.getParameterValues("value"+tableIdForDummyResp);
				if(strOutFields!=null && strValues!=null  && strOutFields.length>0){
					List<CopyPacketDummyResponseParameterData> dummyResponseParameterDataList = new ArrayList<CopyPacketDummyResponseParameterData>();
					for (int i = 0; i < strOutFields.length; i++) {

						CopyPacketDummyResponseParameterData parameterData = new CopyPacketDummyResponseParameterData();
						parameterData.setOutField(strOutFields[i]);
						parameterData.setValue(strValues[i]);
						dummyResponseParameterDataList.add(parameterData);
					}
					copyPacketMappingConfData.setDummyParameterData(dummyResponseParameterDataList);
				}
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
				blManager.create(copyPacketMappingConfData, staffData);;
				request.setAttribute("responseUrl","/initSearchCopyPacketMappingConfig.do"); 
				ActionMessage message = new ActionMessage("copypacket.create.success");
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
		}catch (DuplicateInstanceNameFoundException dpfExp) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dpfExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("copypacket.duplicate.failure",copyPacketMappingConfForm.getName());
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
		}catch(ConstraintViolationException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("copypacket.duplicatemessage.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("copypacket.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);

	}
	private CopyPacketTranslationConfData convertFormToBean(CreateCopyPacketTransMappingForm form){
		CopyPacketTranslationConfData data = new CopyPacketTranslationConfData();
		data.setName(form.getName());
		data.setDescription(form.getDescription());
		data.setTransFromType(form.getSelectedFromTranslatorType());
		data.setTransToType(form.getSelectedToTranslatorType());
		data.setScript(form.getScript());
		return data;
	}
	private Set<CopyPacketTranslationMapDetailData> getMappingDetailList(HttpServletRequest request, int index,String tableId,String mappingTypeId){
		Set<CopyPacketTranslationMapDetailData> detailDataList = new LinkedHashSet<CopyPacketTranslationMapDetailData>();
		String orderNumber[] = request.getParameterValues("orderNumber"+tableId+index);
		String operation[] = request.getParameterValues("operation"+tableId+index);
		String checkExpression[] = request.getParameterValues("checkExpression"+tableId+index);
		String destinationExpression[] = request.getParameterValues("destinationExpression"+tableId+index);
		String sourceExpression[] = request.getParameterValues("sourceExpression"+tableId+index);
		String defaultValues[] = request.getParameterValues("defaultValue"+tableId+index);
		String valueMappings[] = request.getParameterValues("valueMapping"+tableId+index);
		if( operation!=null && destinationExpression!=null){
			for (int j = 0; j < operation.length; j++) {

				if(operation[j]!=null && operation[j].trim().length()>0){
					CopyPacketTranslationMapDetailData detailData = new CopyPacketTranslationMapDetailData();
					detailData.setOperation(operation[j].trim());
					detailData.setCheckExpression(checkExpression[j]);
					detailData.setDestinationExpression(destinationExpression[j]);
					detailData.setSourceExpression(sourceExpression[j]);
					detailData.setOrderNumber(Long.parseLong(orderNumber[j]));
					detailData.setDefaultValue(defaultValues[j]);
					detailData.setValueMapping(valueMappings[j]);
					detailData.setMappingTypeId(mappingTypeId);
					detailDataList.add(detailData);
				}
			}
		}
		return detailDataList;
	}

}
