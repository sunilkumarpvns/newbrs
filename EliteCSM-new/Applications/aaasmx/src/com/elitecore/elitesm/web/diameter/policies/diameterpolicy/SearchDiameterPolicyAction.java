/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.policies.diameterpolicy; 
  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpolicy.DiameterPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.SearchDiameterPolicyForm;
                                                                               
public class SearchDiameterPolicyAction extends BaseWebAction { 
	                                                                       
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETERPOLICY";
	private static final String LIST_FORWARD = "diameterPolicySearchList";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		try{
			SearchDiameterPolicyForm searchDiameterPolicyForm = (SearchDiameterPolicyForm)form;
			DiameterPolicyBLManager blManager = new DiameterPolicyBLManager();

			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchDiameterPolicyForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo =1;

			DiameterPolicyData diameterPolicyData = new DiameterPolicyData();
			String strName = searchDiameterPolicyForm.getName();
			
			if(strName!=null)
				diameterPolicyData.setName("%"+strName+"%");
			else
				diameterPolicyData.setName("");


			//System.out.println("here the value of the commonstatusid is :"+searchRadiusPolicyForm.getStatus());
			if(request.getParameter("resultStatus")!= null){
				searchDiameterPolicyForm.setStatus(request.getParameter("resultStatus"));
			}
			if(searchDiameterPolicyForm.getStatus() != null){
				if(searchDiameterPolicyForm.getStatus().equalsIgnoreCase("Show"))
					diameterPolicyData.setCommonStatusId("CST01");
				else if(searchDiameterPolicyForm.getStatus().equalsIgnoreCase("Hide"))
					diameterPolicyData.setCommonStatusId("CST02");
			}

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			PageList pageList = blManager.search(diameterPolicyData,staffData, requiredPageNo,pageSize);
			searchDiameterPolicyForm.setName(strName);
			searchDiameterPolicyForm.setPageNumber(pageList.getCurrentPage());
			searchDiameterPolicyForm.setTotalPages(pageList.getTotalPages());
			searchDiameterPolicyForm.setTotalRecords(pageList.getTotalItems());
			searchDiameterPolicyForm.setListDiameterPolicy(pageList.getListData());
			searchDiameterPolicyForm.setDiameterPolicyList(pageList.getCollectionData());
			searchDiameterPolicyForm.setAction(BaseConstant.LISTACTION);
			
			request.setAttribute("searchDiameterPolicyForm", searchDiameterPolicyForm);
			return mapping.findForward(LIST_FORWARD);

		}catch(Exception managerExp){
			managerExp.printStackTrace();
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);          
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}
