package com.elitecore.elitesm.web.datasource.database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.datasource.database.forms.CreateDatabaseDSForm;



public class InitCreateDatabaseDSAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "createDatabaseDB";
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			CreateDatabaseDSForm createDatabaseDSForm=(CreateDatabaseDSForm)form;
			return mapping.findForward(SUCCESS_FORWARD);
		
			
    }
}
