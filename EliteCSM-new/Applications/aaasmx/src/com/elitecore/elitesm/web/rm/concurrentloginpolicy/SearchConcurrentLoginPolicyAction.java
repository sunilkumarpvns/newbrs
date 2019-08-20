package com.elitecore.elitesm.web.rm.concurrentloginpolicy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData;
import com.elitecore.elitesm.util.constants.ConcurrentLoginPolicyConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseConcurrentLoginPolicyAction;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.SearchConcurrentLoginPolicyForm;

public class SearchConcurrentLoginPolicyAction extends BaseDictionaryAction {
	
	private static final String SUCCESS_FORWARD= "searchConc";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_CONCURRENT_LOGIN_POLICY_ACTION;
	
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{

		if(checkAccess(request, ACTION_ALIAS)){
			try {
				SearchConcurrentLoginPolicyForm searchConcurrentLoginPolicyForm = (SearchConcurrentLoginPolicyForm)form ;

				ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();


				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(searchConcurrentLoginPolicyForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo =1;

				IConcurrentLoginPolicyData  concurrentLoginPolicyData= new ConcurrentLoginPolicyData();

				String strName=searchConcurrentLoginPolicyForm.getName();
				if(strName!=null){
					concurrentLoginPolicyData.setName("%"+strName+"%");
				}else{
					concurrentLoginPolicyData.setName("");
				}
				if(request.getParameter("resultStatus") != null){
					searchConcurrentLoginPolicyForm.setStatus(request.getParameter("resultStatus"));
				}
					
				if(searchConcurrentLoginPolicyForm.getStatus() != null){
					if(searchConcurrentLoginPolicyForm.getStatus().equalsIgnoreCase("Show")){
						concurrentLoginPolicyData.setCommonStatusId("CST01");
					}else if(searchConcurrentLoginPolicyForm.getStatus().equalsIgnoreCase("Hide")){
						concurrentLoginPolicyData.setCommonStatusId("CST02");
					}
				}
				if(request.getParameter("resultPolicyType") != null){
					searchConcurrentLoginPolicyForm.setConcurrentLoginPolicyId(request.getParameter("resultPolicyType"));
				}
				
				if(searchConcurrentLoginPolicyForm.getConcurrentLoginPolicyId() != null){
					if(searchConcurrentLoginPolicyForm.getConcurrentLoginPolicyId().equalsIgnoreCase("I")){
						concurrentLoginPolicyData.setConcurrentLoginPolicyTypeId("SMS0138");
					}else if(searchConcurrentLoginPolicyForm.getConcurrentLoginPolicyId().equalsIgnoreCase("G")){
						concurrentLoginPolicyData.setConcurrentLoginPolicyTypeId("SMS0139");	
					}
				}

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
                Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
                
                PageList pageList = concurrentLoginPolicyBLManager.searchConcurrentLoginPolicy(concurrentLoginPolicyData,staffData,requiredPageNo,pageSize);
                
				searchConcurrentLoginPolicyForm.setName(strName);
				searchConcurrentLoginPolicyForm.setPageNumber(pageList.getCurrentPage());
				searchConcurrentLoginPolicyForm.setTotalPages(pageList.getTotalPages());
				searchConcurrentLoginPolicyForm.setTotalRecords(pageList.getTotalItems());
				searchConcurrentLoginPolicyForm.setConcurrentLoginPolicyList(pageList.getListData());
				searchConcurrentLoginPolicyForm.setAction(ConcurrentLoginPolicyConstant.LISTACTION);

				return mapping.findForward(SUCCESS_FORWARD);

			}catch(Exception e){
				e.printStackTrace();
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
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
}
