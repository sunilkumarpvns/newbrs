package com.elitecore.elitesm.web.driver;

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

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.forms.SearchDriverInstanceForm;

public class SearchDriverInstanceAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "driverinstance";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String MODULE = "SearchDriverInstanceAction";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DRIVER;
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_DRIVER;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		String actionMessage="driver.search.failure";
		try {
			checkActionPermission(request,ACTION_ALIAS);
			SearchDriverInstanceForm driverForm = (SearchDriverInstanceForm)form;
			DriverBLManager driverBLManager = new DriverBLManager();
			
			String[] driverInstanceIds = (String[])request.getParameterValues("select");
			
			/* requiredPageNo */
			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(driverForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(driverForm.getAction() != null && driverForm.getAction().equals("delete")){
				actionMessage="driver.delete.failure";
				checkActionPermission(request, ACTION_ALIAS_DELETE);
				
				/**
				 *  This code is for to check whether any driver is bonded with radius service policy or not
				 *  If any driver is bound with radius service policy then it throws an exception and not allow user to delete driver.
				 */
				List<String> tagNameList = new ArrayList<String>();
				tagNameList.add("driver-instace-id");
				tagNameList.add("secondary-driver-id");
				tagNameList.add("additional-driver-id");
				
				List<String> driverInstanceIdsValues = Arrays.asList(driverInstanceIds);
				
				driverBLManager.deleteById(Arrays.asList(driverInstanceIds),staffData, driverInstanceIdsValues);
				
				int strSelectedIdsLen = driverInstanceIds.length;
				long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,driverForm.getPageNumber(),driverForm.getTotalPages(),driverForm.getTotalRecords());
				driverForm.setAction("list");
				request.setAttribute("responseUrl","/searchDriverInstance.do?name="+driverForm.getName()+"&pageNo="+currentPageNumber+"&totalPages="+driverForm.getTotalPages()+"&totalRecords="+driverForm.getTotalRecords()+"&driverType="+driverForm.getSelecteDriver());
				ActionMessage message = new ActionMessage("driver.delete.success");
				ActionMessages messages1 = new ActionMessages();
				messages1.add("information",message);
				saveMessages(request,messages1);
				return mapping.findForward(SUCCESS_FORWARD);
				
			}
			
			DriverInstanceData driverInstanceData = new DriverInstanceData();
			
			/*name*/
			String strdriverName =request.getParameter("name");
			if(strdriverName != null){
				driverInstanceData.setName(strdriverName);
			}else if(driverForm.getName() != null){
				driverInstanceData.setName(driverForm.getName());
			}else{
				driverInstanceData.setName("");
			}
			/*driver type*/
			String strdriverType =request.getParameter("driverType");
			if(strdriverType != null){
				driverInstanceData.setDriverTypeId(Long.parseLong(strdriverType));
			}else if(driverForm.getSelecteDriver() != null){
				
				driverInstanceData.setDriverTypeId(Long.parseLong(driverForm.getSelecteDriver()));
			}else{
				driverInstanceData.setDriverTypeId(0);
			}
			
			PageList pageList = driverBLManager.search(driverInstanceData,staffData,requiredPageNo,pageSize);
			
			driverForm.setPageNumber(pageList.getCurrentPage());
			driverForm.setTotalPages(pageList.getTotalPages());
			driverForm.setTotalRecords(pageList.getTotalItems());
			driverForm.setDriverList(pageList.getListData());
			driverForm.setName(driverInstanceData.getName());
			driverForm.setSelecteDriver(String.valueOf(driverInstanceData.getDriverTypeId()));
			
			List serviceList = driverBLManager.getListOfAllServices();		
			driverForm.setAction("list");
			driverForm.setServiceList(serviceList);
			request.getSession().setAttribute("searchForm",driverForm);
			
		}catch (ConstraintViolationException e) {
			
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("driver.search.childrecord");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);
			
		} catch (DataManagerException e) {
			
			if(e.getCause() instanceof ConstraintViolationException){
				actionMessage="driver.search.childrecord";
			}
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage(actionMessage);
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);
		}
		return mapping.findForward(LIST_FORWARD);
	}
}
