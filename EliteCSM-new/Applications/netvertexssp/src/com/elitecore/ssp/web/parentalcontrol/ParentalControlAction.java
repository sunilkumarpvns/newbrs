package com.elitecore.ssp.web.parentalcontrol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.parentalcontrol.forms.ParentalControlForm;
import com.elitecore.ssp.webservice.parentalcontrol.ParentalControlDataManager;

public class ParentalControlAction extends BaseWebAction {
	
	private static final String MODULE = ParentalControlAction.class.getSimpleName();
	private static final String PARENTAL_CONTROL="parentalcontrol";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		ParentalControlForm parentalControlForm = (ParentalControlForm)form;
				
		try
		{
			ParentalControlDataManager parentalControlDataManager=new ParentalControlDataManager();
			SubscriberProfile subscriberProfileData = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			SubscriberProfile[] childAccounts=parentalControlDataManager.getChildAccounts(subscriberProfileData);
			Logger.logInfo(MODULE, " after subscriberProfileDatas " + getClass().getName());
			parentalControlForm.setSubscriberProfileData(childAccounts);			
			request.setAttribute("parentalControlForm", parentalControlForm);
						
			return mapping.findForward(PARENTAL_CONTROL);
			
		}catch(Exception e){
			e.printStackTrace();
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
			return mapping.findForward(FAILURE);
	}

}
