package com.elitecore.elitesm.web.digestconf;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.digestconf.forms.SearchDigestConfForm;

public class SearchDigestConfAction extends BaseWebAction{

	

	private static final String SUCCESS_FORWARD = "listclientprofiles";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DIGEST_CONFIGURATION;
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SEARCH DIGEST CONFIG ACTION";
	private static final String LIST_FORWARD = "searchDigestConfigList";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			try{
				
				DigestConfBLManager digestConfBLManager = new DigestConfBLManager();
				SearchDigestConfForm searchDigestconfForm = (SearchDigestConfForm)form;
				
				int requiredPageNo;
	            if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(searchDigestconfForm.getPageNumber()).intValue();
	    		}
				if(requiredPageNo == 0)
					requiredPageNo = 1;
				
				DigestConfigInstanceData digestConfigInstanceData = new DigestConfigInstanceData();
				
				
                String strDigestConfigName =request.getParameter("name"); 
                if(strDigestConfigName != null){
                	digestConfigInstanceData.setName(strDigestConfigName);
                }else if(searchDigestconfForm.getName() != null){
                	digestConfigInstanceData.setName(searchDigestconfForm.getName());
                }else{
                	digestConfigInstanceData.setName("");
                }
                strDigestConfigName = digestConfigInstanceData.getName();
            
                
              //draftAAASipEnable
                
                String strDraftAAASipEnable = request.getParameter("draftAAASipEnable");
                if(strDraftAAASipEnable != null){
                	digestConfigInstanceData.setDraftAAASipEnable(strDraftAAASipEnable);
                }else if(searchDigestconfForm.getDraftAAASipEnable() != null){
                	digestConfigInstanceData.setDraftAAASipEnable(searchDigestconfForm.getDraftAAASipEnable());
                }else{
                	digestConfigInstanceData.setDraftAAASipEnable("none");
                }
                strDraftAAASipEnable = digestConfigInstanceData.getDraftAAASipEnable();
            
               
                
                
                
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
							
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				Logger.logDebug(MODULE, "PAGENO IS:"+requiredPageNo+"");
				
				
				
				PageList pageList = digestConfBLManager.search(digestConfigInstanceData,requiredPageNo,pageSize);
				doAuditing(staffData, actionAlias);
				List<DigestConfigInstanceData> digestConfigList=pageList.getListData();
										
				
				searchDigestconfForm.setName(digestConfigInstanceData.getName());
				searchDigestconfForm.setDraftAAASipEnable(digestConfigInstanceData.getDraftAAASipEnable());
				
				searchDigestconfForm.setPageNumber(pageList.getCurrentPage());
				searchDigestconfForm.setTotalPages(pageList.getTotalPages());
				searchDigestconfForm.setTotalRecords(pageList.getTotalItems());
				searchDigestconfForm.setDigestConfigList(digestConfigList);
				searchDigestconfForm.setAction(BaseConstant.LISTACTION);
				
				return mapping.findForward(LIST_FORWARD);
				
				
				
			}catch(Exception e){
				Logger.logError(MODULE, "Error List Display operation , reason : " + e.getMessage());            
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);

			}
			
			Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
			ActionMessage message = new ActionMessage("digestconf.search.failed");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
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
