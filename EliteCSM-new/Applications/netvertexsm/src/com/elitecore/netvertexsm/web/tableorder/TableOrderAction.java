package com.elitecore.netvertexsm.web.tableorder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.base.GenericBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.tableorder.form.TableOrderForm;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class TableOrderAction extends BaseWebAction{
	private final static String MODULE = TableOrderAction.class.getSimpleName(); 
	
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter execute Method of "+getClass().getName());
		try{
			TableOrderForm tableOrderForm = (TableOrderForm) form;
			String className = tableOrderForm.getClassName();
			String actionAlias = TableOrderData.getInstance().getActionAlias(className);
			checkAccess(request, actionAlias);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			GenericBLManager genericBLManager = new GenericBLManager();
			
			genericBLManager.saveOrderOfData(className, tableOrderForm.getIds());
			
			doAuditing(staffData, actionAlias);
			
			request.setAttribute("responseUrl", "/"+tableOrderForm.getResponseUrl());
			ActionMessage message = new ActionMessage("alert.manage.success");
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
		}catch(Exception e){
			Logger.logError(MODULE, e.getMessage());
			ActionMessage message = new ActionMessage("alert.manage.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}
