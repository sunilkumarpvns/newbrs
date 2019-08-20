package com.elitecore.elitesm.web.rm.concurrentloginpolicy;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyDetailData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyDetailData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.util.KeyValueBean;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.AddConcurrentLoginPolicyDetailForm;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.AddConcurrentLoginPolicyForm;

public class CreateConcurrentLoginPolicyAction extends BaseWebAction{
	protected static final String MODULE = "CREATE CONCURRENT LOGIN POLICY ACTION";
	private static final String FAILURE_FORWARD = "failure";
    private static final String SUCCESS_FORWARD = "success";
	private static List lstNasPortType ;
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_CONCURRENT_LOGIN_POLICY_ACTION;
	
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
			AddConcurrentLoginPolicyForm addConcurrentLoginPolicyForm = (AddConcurrentLoginPolicyForm)form;
			lstNasPortType = (List)request.getSession().getAttribute("lstNasPortType");
			ActionErrors errors = new ActionErrors();
			try {
				String strSystemGenerated="N";
				String strActive = "CST01";
				String strInActive = "CST02";
				String strGroupId ="SMS0139";
				String strIndividualId="SMS0138";
				String strGeneralId="SMS0149";
				String strServicewiseId="SMS0150";
				if (addConcurrentLoginPolicyForm !=null){
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					ConcurrentLoginPolicyData concurrentLoginPolicyData = new ConcurrentLoginPolicyData();
					
					if (addConcurrentLoginPolicyForm.getLogin() == -1) {
						concurrentLoginPolicyData.setLoginLimit("Unlimited");
					} else {
						concurrentLoginPolicyData.setLoginLimit("Limited");
					}
					
					concurrentLoginPolicyData.setLogin(addConcurrentLoginPolicyForm.getLogin());
					concurrentLoginPolicyData.setName(addConcurrentLoginPolicyForm.getName());
					concurrentLoginPolicyData.setSystemGenerated(strSystemGenerated);
					concurrentLoginPolicyData.setDescription(addConcurrentLoginPolicyForm.getDescription());
					concurrentLoginPolicyData.setAttribute(addConcurrentLoginPolicyForm.getAttribute());
					
                   if(BaseConstant.SHOW_STATUS.equals(addConcurrentLoginPolicyForm.getStatus())){
                	   concurrentLoginPolicyData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
                   }else{
                	   concurrentLoginPolicyData.setCommonStatusId(BaseConstant.HIDE_STATUS_ID);
                   }
					
					concurrentLoginPolicyData.setCreateDate(timestamp);

					String strStaffId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
					concurrentLoginPolicyData.setLastModifiedByStaffId(strStaffId);
					concurrentLoginPolicyData.setCreatedByStaffId(strStaffId);

					concurrentLoginPolicyData.setLastModifiedDate(timestamp);
					concurrentLoginPolicyData.setStatusChangeDate(timestamp);

					if(addConcurrentLoginPolicyForm.getConcurrentLoginPolicy().equalsIgnoreCase("group")){
						concurrentLoginPolicyData.setConcurrentLoginPolicyTypeId(strGroupId);
					}else{
						concurrentLoginPolicyData.setConcurrentLoginPolicyTypeId(strIndividualId);
					}

					if(addConcurrentLoginPolicyForm.getConcurrentLoginPolicyMode().equalsIgnoreCase("general")){
						concurrentLoginPolicyData.setConcurrentLoginPolicyModeId(strGeneralId);
					}else{
						concurrentLoginPolicyData.setConcurrentLoginPolicyModeId(strServicewiseId);
					}

					ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();
					String strAction = (String)request.getParameter("action");
					Logger.logDebug(MODULE, "Action : "+ strAction);
					if(strAction!=null ){

						if(strAction.equalsIgnoreCase("detail")){
							List lstConcurrentLoginPolicyDetail = constructConcurrentLoginPolicyDetail(addConcurrentLoginPolicyForm);
							concurrentLoginPolicyData.setConcurrentLoginPolicyDetail(lstConcurrentLoginPolicyDetail);
						}
					}
					int i=0;
					IConcurrentLoginPolicyDetailData concurrentLoginPolicyDetailData = new ConcurrentLoginPolicyDetailData();

					List<ConcurrentLoginPolicyDetailData> stConcurrentPolicyDetailList = concurrentLoginPolicyData.getConcurrentLoginPolicyDetail();
					
					if(Collectionz.isNullOrEmpty(stConcurrentPolicyDetailList) == false){
						for ( ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail : stConcurrentPolicyDetailList) {
							concurrentLoginPolicyDetail.setSerialNumber((i++)+1);
							if (concurrentLoginPolicyDetail.getLogin() == -1) {
								concurrentLoginPolicyDetail.setLoginLimit("Unlimited");
							} else {
								concurrentLoginPolicyDetail.setLoginLimit("Limited");
							}
						}
						
					}
					
					
					concurrentLoginPolicyData.setConcurrentLoginPolicyDetail(stConcurrentPolicyDetailList);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;

					concurrentLoginPolicyBLManager.createConcurrentLoginPolicy(concurrentLoginPolicyData,staffData,false);
					
					request.setAttribute("responseUrl","/initSearchConcurrentLoginPolicy.do");
					ActionMessage message = new ActionMessage("concurrentloginpolicy.create.successmessage");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);
				}
			} catch (DataManagerException managerExp){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				errors.add("fatal", new ActionError("concurrentloginpolicy.create.failuremessage")); 
				saveErrors(request,errors);
				managerExp.printStackTrace();

			}
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
	private List constructConcurrentLoginPolicyDetail(AddConcurrentLoginPolicyForm addConcLoginPolicyForm){
		List lstConcurentLoginPolicyDetail = new ArrayList();
		List lstTemp =   addConcLoginPolicyForm.getConcurrentLoginPolicyDetail();
		ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetailData ;
		for(int i=0; i< lstTemp.size();i++ ){
			concurrentLoginPolicyDetailData = new ConcurrentLoginPolicyDetailData();
			AddConcurrentLoginPolicyDetailForm addConcurrentLoginPolicyDetailForm =(AddConcurrentLoginPolicyDetailForm)lstTemp.get(i);
			
			concurrentLoginPolicyDetailData.setLogin(addConcurrentLoginPolicyDetailForm.getMaxNumLogin());
			concurrentLoginPolicyDetailData.setAttributeValue(addConcurrentLoginPolicyDetailForm.getAttributeValue());
			lstConcurentLoginPolicyDetail.add(concurrentLoginPolicyDetailData);	
		}
		
		
		return lstConcurentLoginPolicyDetail;
		
	}
	private static int getNasPortId(String strNasPortTypeName){
		KeyValueBean keyValueBean;
		int lNasPortId = -1;
		for(int i=0;i<lstNasPortType.size();i++){
			keyValueBean=(KeyValueBean )lstNasPortType.get(i);
			if(keyValueBean.getValue().equals(strNasPortTypeName)){
				lNasPortId= Integer.parseInt( keyValueBean.getKey());
			}
		}
		
		return lNasPortId;
	}
}
