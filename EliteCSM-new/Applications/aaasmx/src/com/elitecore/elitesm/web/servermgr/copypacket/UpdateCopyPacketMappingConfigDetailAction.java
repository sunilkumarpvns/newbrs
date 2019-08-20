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

import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketDummyResponseParameterData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapDetailData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.copypacket.forms.UpdateCopyPacketMappingConfigDetailForm;
import com.elitecore.elitesm.ws.logger.Logger;

public class UpdateCopyPacketMappingConfigDetailAction extends BaseWebAction {
	private static final String MODULE = UpdateCopyPacketMappingConfigDetailAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG;
	private static final String ACTION_FORWARD = "updateCopyPacketMappingConfDetail";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		UpdateCopyPacketMappingConfigDetailForm updateCopyPacketDetailForm = (UpdateCopyPacketMappingConfigDetailForm)form;
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE, "Execute Method of " + getClass().getName());
			String strCopyPacketMappingConfId = request.getParameter("copyPacketMappingConfigId");
			String copyPacketMapConfId = null;
			if(strCopyPacketMappingConfId != null){
				copyPacketMapConfId = strCopyPacketMappingConfId;
			}
			if(copyPacketMapConfId == null){
				copyPacketMapConfId = updateCopyPacketDetailForm.getCopyPacketMapConfId();
			}
			
			CopyPacketTranslationConfData copyPacketConfData = (CopyPacketTranslationConfData) request.getAttribute("copyPacketMappingConfData");
			CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
			if(updateCopyPacketDetailForm.getAction() == null || updateCopyPacketDetailForm.getAction().equals("") == true){
				if(copyPacketConfData != null){
					copyPacketConfData = blManager.getCopyPacketTransMapConfigDetailDataById(copyPacketMapConfId);
					request.setAttribute("copyPacketMappingConfData", copyPacketConfData);
					updateCopyPacketDetailForm.setCopyPacketMappingConfData(copyPacketConfData);
				}
				return mapping.findForward(ACTION_FORWARD);
			}else if(updateCopyPacketDetailForm.getAction() != null && updateCopyPacketDetailForm.getAction().equals("save")){
				copyPacketConfData = blManager.getCopyPacketTransMapConfigData(copyPacketMapConfId);
				String strMappingIndex = request.getParameter("mappingIndex");
				int mappingIndex = Integer.parseInt(strMappingIndex);
				List<CopyPacketTranslationMapData> instanceDataList = new ArrayList<CopyPacketTranslationMapData>();
				
				for(int i=0;i<mappingIndex ;i++){
					int index = i+1;
					String mappingName = request.getParameter("mappingName"+index);
					if(mappingName!=null && mappingName.trim().length()>0){
						CopyPacketTranslationMapData instanceData =  new CopyPacketTranslationMapData();
						String inExpression = request.getParameter("inExpression"+index);
						String defaultMapping = request.getParameter("defaultMappingChkBox"+index);
						String dummyResponse = request.getParameter("dummyResponseChkBox"+index);
						instanceData.setCopyPacketTransConfId(copyPacketConfData.getCopyPacketTransConfId());
						instanceData.setMappingName(mappingName);
						instanceData.setInExpression(inExpression);
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
				copyPacketConfData.setCopyPacketTransMapData(instanceDataList);

				//Dummy Response Parameters Update
				String tableIdForDummyResp = "tableDummyResponseParams";
				String strOutFields[] = request.getParameterValues("outField"+tableIdForDummyResp);
				String strValues[] = request.getParameterValues("value"+tableIdForDummyResp);
				if(strOutFields != null && strValues != null  && strOutFields.length>0){
					List<CopyPacketDummyResponseParameterData> dummyResponseParameterDataList = new ArrayList<CopyPacketDummyResponseParameterData>();
					for (int i = 0; i < strOutFields.length; i++) {

						CopyPacketDummyResponseParameterData parameterData = new CopyPacketDummyResponseParameterData();
						parameterData.setOutField(strOutFields[i]);
						parameterData.setValue(strValues[i]);


						dummyResponseParameterDataList.add(parameterData);

					}
					copyPacketConfData.setDummyParameterData(dummyResponseParameterDataList);
				}else{
					copyPacketConfData.setDummyParameterData(null);
				}
				request.setAttribute("copyPacketMappingConfData",copyPacketConfData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				blManager.updateById(copyPacketConfData,staffData);
				request.setAttribute("responseUrl","/viewCopyPacketConfigBasicDetail.do?copyPacketTransConfId="+copyPacketConfData.getCopyPacketTransConfId()); 
				ActionMessage message = new ActionMessage("copypacket.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS);	
			}


		} catch (ActionNotPermitedException e) {
			e.printStackTrace();
		} catch (DataManagerException e) {
			e.printStackTrace();
		}
		return null;
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
					detailData.setSourceExpression(sourceExpression[j].trim());
					detailData.setOrderNumber(Long.parseLong(orderNumber[j]));
					detailData.setDefaultValue(defaultValues[j]);
					detailData.setValueMapping(valueMappings[j]);
					detailData.setMappingTypeId(mappingTypeId);
					detailDataList.add(detailData);
					Logger.logInfo(MODULE, "Copy Packet Mapping Instance Detail Data : \n"+ detailData);

				}
			}
		}
		return detailDataList;
	}


}
