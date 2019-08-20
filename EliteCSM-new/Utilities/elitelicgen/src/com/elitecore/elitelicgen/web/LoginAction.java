/*
 *  License Generation Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on Nov 5, 2007
 *  Created By kaushikvira
 */
package com.elitecore.elitelicgen.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitelicgen.base.BaseWebAction;
import com.elitecore.elitelicgen.util.Logger;
import com.elitecore.elitelicgen.web.forms.LoginForm;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseDataManager;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.configuration.LicenseConfigurationManager;
import com.elitecore.elitelicgen.configuration.LicenseConfiguration;
/**
 * @author kaushikvira
 *
 */
public class LoginAction extends BaseWebAction {

	private static final String MODULE          = "LOGIN";
	private static final String SUCCESS_FORWARD = "ViewChooseModual";
	private static final String FAILURE_FORWARD = "InvaildLogin";

	public ActionForward execute( ActionMapping mapping , ActionForm form , 
								  HttpServletRequest request , HttpServletResponse response ) throws Exception {
		   
		try {
			Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());

			LoginForm loginForm = (LoginForm) form;

			if (loginForm.getUserName().equalsIgnoreCase(getResources(request).getMessage("login.username"))
					&& loginForm.getPassword().equalsIgnoreCase(getResources(request).getMessage("login.password"))) {

				LicenseConfiguration.readConfiguration();
				LicenseConfigurationManager.getInstance().init();
				LicenseData licdata = LicenseDataManager.getLicenseData();
				Map<String, LicenseData> licMap = new SequencedHashMap();
				List<LicenseData> lstLicData = licdata.getLicenseData();
				Iterator<LicenseData> itLstLicData = lstLicData.iterator();

				LicenseData tempLicData = null;
				while (itLstLicData.hasNext()) {

					tempLicData = itLstLicData.next();
					if (tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE)) {
						licMap.put(tempLicData.getModule(), tempLicData);
					}
				}
				Map<Integer,String> VendorList=LicenseConfigurationManager.getInstance().getSupportedVendorList();
				List<String> nasTypeList=LicenseConfigurationManager.getInstance().getNasTypeList();
				request.getSession().setAttribute("nastypelist",nasTypeList);
				request.getSession().setAttribute("VendorList",VendorList);
				request.getSession().setAttribute("licMap", licMap);
				return mapping.findForward(SUCCESS_FORWARD);
			}

			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("login.invalid.login"));
			saveErrors(request, errors);
		}
		catch (Exception e) {
			e.printStackTrace();
			Logger.logTrace(MODULE, e);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

}
