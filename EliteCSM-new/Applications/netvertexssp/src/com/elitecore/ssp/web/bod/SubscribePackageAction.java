package com.elitecore.ssp.web.bod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDPackage;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.bod.forms.SubscribePackageForm;
import com.elitecore.ssp.web.core.base.BaseWebAction;

public class SubscribePackageAction extends BaseWebAction {
	private static final String MODULE = SubscribePackageAction.class.getSimpleName();
	private static final String SUBSCRIBE_PACKAGE = "subscribePackage";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
	
		try
		{
			SubscribePackageForm subscribePackageForm = (SubscribePackageForm)form;		
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK, request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK));
			Long bodPackageId=subscribePackageForm.getBodPackageId();
			Logger.logDebug(MODULE, "BodPackageId: "+bodPackageId);
			
			BoDPackage bodPackageData = getBodPackageData(bodPackageId, request);					
			subscribePackageForm.setBodPackageData(bodPackageData);
			
			request.setAttribute(RequestAttributeKeyConstant.SUBSCRIBE_PACKAGE_FORM,subscribePackageForm);
			return mapping.findForward(SUBSCRIBE_PACKAGE);
			
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("bod.subscribe.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}