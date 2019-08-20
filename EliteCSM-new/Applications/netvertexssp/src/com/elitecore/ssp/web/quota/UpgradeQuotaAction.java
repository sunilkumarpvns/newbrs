package com.elitecore.ssp.web.quota;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.web.core.base.BaseWebAction;

public class UpgradeQuotaAction extends BaseWebAction {

	private static final String MODULE = "Upgrade Quota ACTION";
	private static final String UPGRADE_QUOTA="upgrade_quota";

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		try
		{
			//Perform Authentication
			return mapping.findForward(UPGRADE_QUOTA);
			
		}catch(Exception e){
			
		}
		return mapping.findForward(FAILURE);
	}

	

}
