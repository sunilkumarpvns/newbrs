package com.elitecore.elitesm.web.diameter.routingconfig;

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
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.SearchDiameterRoutingConfForm;

public class MiscDiameterRoutingConfAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-ROUTING-CONF";
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_DIAMETER_ROUTING_TABLE; 
	private static final String SHOW_ALL=ConfigConstant.SHOWALL_DIAMETER_ROUTING_TABLE;
	private static final String LIST_FORWARD = "searchDiameterRoutingConfList";
	private static final String SEARCHALLROUTINGCONF = "searchAllDiameterRoutingConfig";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		SearchDiameterRoutingConfForm searchDiameterRoutingConfForm = (SearchDiameterRoutingConfForm)form;
		try{
			checkActionPermission(request,ACTION_ALIAS_DELETE);
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			GenericBLManager genericBLManager=null;
			PageList pageList=null;
			if(searchDiameterRoutingConfForm.getAction() != null) {
				 if(searchDiameterRoutingConfForm.getAction().equalsIgnoreCase("delete")){
					String[] strSelectedIds = request.getParameterValues("select");
					checkActionPermission(request, ACTION_ALIAS_DELETE);
					
					diameterRoutingConfBLManager.deleteRoutingEntryById(Arrays.asList(strSelectedIds), staffData);
					
					request.setAttribute("responseUrl","/diameterRoutingTable.do?actionType=tablewiserouting&routingTableId="+request.getParameter("routingTableId"));
					ActionMessage message = new ActionMessage("diameter.routingconf.delete");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}
				else if(searchDiameterRoutingConfForm.getAction().equalsIgnoreCase("deletetable"))
				{
					String[] strSelectedIds = request.getParameterValues("select");
					
					diameterRoutingConfBLManager.deleteRoutingTableById(Arrays.asList(strSelectedIds), staffData);
					
					int strSelectedIdsLen = strSelectedIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchDiameterRoutingConfForm.getPageNumber(),searchDiameterRoutingConfForm.getTotalPages(),searchDiameterRoutingConfForm.getTotalRecords());
					request.setAttribute("responseUrl","/diameterRoutingTable.do?actionType=pagination&pageNo="+currentPageNumber+"&totalPages="+searchDiameterRoutingConfForm.getTotalPages()+"&totalRecords="+searchDiameterRoutingConfForm.getTotalRecords());
					ActionMessage message = new ActionMessage("diameter.routingtable.delete");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}
				else if(searchDiameterRoutingConfForm.getAction().equalsIgnoreCase("showall"))
				{
					genericBLManager = new GenericBLManager();
					pageList = genericBLManager.getAllRecords(DiameterRoutingTableData.class,"routingTableName",true);
					searchDiameterRoutingConfForm.setListDiameterRoutingTable(pageList.getListData());
					
					pageList = genericBLManager.getAllRecords(DiameterRoutingConfData.class,"name",true);
					searchDiameterRoutingConfForm.setListDiameterRoutingConf(pageList.getListData());
					searchDiameterRoutingConfForm.setPageNumber(pageList.getCurrentPage());
					searchDiameterRoutingConfForm.setTotalPages(pageList.getTotalPages());
					searchDiameterRoutingConfForm.setTotalRecords(pageList.getTotalItems());
					doAuditing(staffData, SHOW_ALL);
					request.setAttribute("searchDiameterRoutingConfForm", searchDiameterRoutingConfForm);
					return mapping.findForward(SEARCHALLROUTINGCONF); 
				}	
			}
			
			request.setAttribute("searchDiameterRoutingConfForm",searchDiameterRoutingConfForm);
			return mapping.findForward(LIST_FORWARD);
		}catch (ConstraintViolationException e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message=null;
			if(searchDiameterRoutingConfForm.getAction() != null && searchDiameterRoutingConfForm.getAction().equalsIgnoreCase("delete")) {
				message = new ActionMessage("diameter.routingconf.alreadyinuse");
			}
			else if(searchDiameterRoutingConfForm.getAction() != null && searchDiameterRoutingConfForm.getAction().equalsIgnoreCase("deletetable"))
			{
				message = new ActionMessage("diameter.routingtable.alreadyinuse");
			}	
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);	
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD); 
		}catch(Exception managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
		}

		return mapping.findForward(FAILURE_FORWARD);
	}
}