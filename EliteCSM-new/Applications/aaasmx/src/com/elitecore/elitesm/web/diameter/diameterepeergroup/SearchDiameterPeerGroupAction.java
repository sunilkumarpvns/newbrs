package com.elitecore.elitesm.web.diameter.diameterepeergroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.diameterepeergroup.form.DiameterPeerGroupForm;

public class SearchDiameterPeerGroupAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-PEER-GROUP";
	private static final String SEARCH_DIAMETER_PEER_GROUP = "searchDiameterPeerGroup"; 
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DIAMETER_PEER_GROUP;
	private static final String DELETE_ALIAS = ConfigConstant.DELETE_DIAMETER_PEER_GROUP;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			
			DiameterPeerGroupForm diameterPeerGroupForm = (DiameterPeerGroupForm)form;
			DiameterPeerGroupBLManager blManager = new DiameterPeerGroupBLManager();
			
			String requestAction = request.getParameter("action");
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if( requestAction != null && requestAction.equals("deletePeerGroup")){
				try{
					checkActionPermission(request, DELETE_ALIAS);
					String[] strSelectedIds = request.getParameterValues("select");
					  
					List<String> listStrSelectedIds = Arrays.asList(strSelectedIds);
					blManager.deleteDiameterPeerGroupById(listStrSelectedIds,staffData);
					request.setAttribute("responseUrl", "/searchDiameterPeerGroup");
					ActionMessage message = new ActionMessage("diameterpeergroup.delete.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
				}catch(ActionNotPermitedException e){
					Logger.logError(MODULE,"Error :-" + e.getMessage());
					printPermitedActionAlias(request);
					ActionMessages messages = new ActionMessages();
					messages.add("information", new ActionMessage("general.user.restricted"));
					saveErrors(request, messages);
					return mapping.findForward(INVALID_ACCESS_FORWARD);
				}catch(Exception exp){
					Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
					ActionMessage message = new ActionMessage("diameterpeergroup.delete.failure");                                                         
					ActionMessages messages = new ActionMessages();                                                                                 
					messages.add("information", message);                                                                                           
					saveErrors(request, messages); 
					return mapping.findForward(FAILURE_FORWARD); 
				}
			}else{
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				
				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(diameterPeerGroupForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo =1;
				
				DiameterPeerGroup diameterPeerGroupData = new DiameterPeerGroup();
				String strPeerGroupName = diameterPeerGroupForm.getPeerGroupName();
				if(strPeerGroupName!=null)
					diameterPeerGroupData.setPeerGroupName(strPeerGroupName);
				else
					diameterPeerGroupData.setPeerGroupName("");
				
				String peerGroupId = diameterPeerGroupForm.getPeerGroupId();
				if(Strings.isNullOrBlank(peerGroupId) == false)
					diameterPeerGroupData.setPeerGroupId(peerGroupId);
			    
			    PageList pageList = blManager.searchDiameterPeerGroupData(diameterPeerGroupData,requiredPageNo,pageSize,staffData);
			
			    diameterPeerGroupForm.setDiameterPeerGroupList(pageList.getListData());
			    diameterPeerGroupForm.setAction(BaseConstant.LISTACTION);
			    diameterPeerGroupForm.setPageNumber(pageList.getCurrentPage());
			    diameterPeerGroupForm.setTotalPages(pageList.getTotalPages());
			    diameterPeerGroupForm.setTotalRecords(pageList.getTotalItems());
			    diameterPeerGroupForm.setPeerGroupName(diameterPeerGroupData.getPeerGroupName());
			    
				request.setAttribute("diameterPeerGroupForm", diameterPeerGroupForm);
				return mapping.findForward(SEARCH_DIAMETER_PEER_GROUP);  
			}
			        
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("diameterpeergroup.search.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}
}