package com.elitecore.elitesm.web.core.system.login;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.web.core.system.base.BaseSystemAction;
import com.elitecore.elitesm.web.core.system.login.forms.NewPasswordForm;

public class CheckHistoricalPasswordAction extends BaseSystemAction{
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		StaffBLManager staffBLManager = new StaffBLManager();
		
		NewPasswordForm newPasswordForm = (NewPasswordForm) form;
		
		String result = staffBLManager.passwordIsHistoricalPassword(newPasswordForm.getUserId(), newPasswordForm.getNewPassword());
		
		PrintWriter out = response.getWriter();
		out.println(result);
		out.flush();
		
		return null;
	}
}
