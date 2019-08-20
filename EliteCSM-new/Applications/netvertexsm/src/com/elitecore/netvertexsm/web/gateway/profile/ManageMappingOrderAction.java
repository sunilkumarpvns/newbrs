package com.elitecore.netvertexsm.web.gateway.profile;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.gateway.MappingBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.profile.form.ManagePacketMapOrderForm;

public class ManageMappingOrderAction extends BaseWebAction{

	private static final String ORDER_FORWARD = "orderlist";
	private static final String SUCCESS_FORWARD = "success";
	private static final String ACTION_ALIAS = "MANAGE_PACKET_MAP_ORDER";
	private static final String MODULE = "GATEWAY-PROFILE";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		try{
			checkActionPermission(request, ACTION_ALIAS);
			ManagePacketMapOrderForm packetMapForm = (ManagePacketMapOrderForm) form;
			MappingBLManager mappingBLManager = new MappingBLManager();
			long profileId = Long.parseLong(request.getParameter("profileId"));

			String[] order = request.getParameterValues("order");
			if(order != null){
				Long[] orderIds = new Long[order.length];
				for(int i=0; i<order.length; i++)
					orderIds[i] = Long.parseLong(order[i]); 
				
				// change the order and save the data
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				mappingBLManager.changeMappingOrder(orderIds, profileId, staffData, actionAlias);
				
				request.setAttribute("responseUrl", "/initSearchProfile");
				ActionMessage message = new ActionMessage("alert.manage.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
			}
			List list = mappingBLManager.searchPacketMap(profileId);

			packetMapForm.setPacketMapList(list);
			packetMapForm.setAction("list");
			packetMapForm.setProfileId(profileId);

			request.getSession().setAttribute("packetMapList",packetMapForm.getPacketMapList());
			request.getSession().setAttribute("packetMapForm",packetMapForm);

			return mapping.findForward(ORDER_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException de){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,de);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(de);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("alert.manage.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);

		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);			
	}
}