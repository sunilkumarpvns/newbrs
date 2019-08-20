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

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitelicgen.base.BaseWebAction;
import com.elitecore.elitelicgen.commons.DataValidationException;
import com.elitecore.elitelicgen.util.Logger;
import com.elitecore.elitelicgen.web.forms.GenrateLicenseForm;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.core.EliteLicenceKeyGenerator;

/**
 * @author kaushikvira
 *
 */
public class GenrateLicenseAction extends BaseWebAction {

	private static String MODULE          = "Genrate License";
	private static String FAILURE_FORWARD = "failure";

	public ActionForward execute( ActionMapping mapping ,ActionForm form , HttpServletRequest request ,	HttpServletResponse response ) throws Exception {

		ServletOutputStream out = null;
		PrintWriter writer = null;
		ActionErrors errors = null;
		String strDate=null;
		try {
			Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
			errors = new ActionErrors();
			SimpleDateFormat dateFormat = new SimpleDateFormat(LicenseConstants.DATE_FORMAT);
			GenrateLicenseForm genrateLicForm = (GenrateLicenseForm) form;
			List<LicenseData> lstLicdata = genrateLicForm.getLstLicData();
			Iterator<LicenseData> itLstLicData = lstLicdata.iterator();

			while (itLstLicData.hasNext()) {

				LicenseData templic = itLstLicData.next();
				if(templic.getType().equalsIgnoreCase(LicenseTypeConstants.EXPIRY_DATE)){
					strDate=(String)templic.getValue();
				}
				if(templic.getType().equalsIgnoreCase(LicenseTypeConstants.SUPPORTED_VENDORS) || templic.getType().equalsIgnoreCase(LicenseTypeConstants.VENDOR_TYPE)){
					if((templic.getValue()).contains("ANY")){
						templic.setValue("-1");
					}
				}
				if (templic.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE)) {
					templic.setValue(strDate);
					if (templic.getSelect() == null || (!templic.getSelect().equalsIgnoreCase("1") && !templic.getSelect().equalsIgnoreCase("N"))) {
						Logger.logDebug(MODULE,templic.getName() + " is Marked N");
						templic.setSelect("N");
						markUnchekedSubLic(templic.getModule(), lstLicdata);
					}
				}
			}

			List<LicenseData> newlstLicData = new ArrayList<LicenseData>();
			/* Adding Moduallic Choosed Modual for Lic genration */
			LicenseData modualLicData = (LicenseData) request.getSession(false).getAttribute("modualLicData");
			Logger.logDebug(MODULE, modualLicData.getName() + "Added to New List");
			newlstLicData.add(modualLicData);

			itLstLicData = lstLicdata.iterator();
			while (itLstLicData.hasNext()) {
				LicenseData templic = itLstLicData.next();
				if (templic.getSelect() != null && !templic.getSelect().equalsIgnoreCase("N")) {
					Logger.logDebug(MODULE,templic.getName() + "Added to New List");
					newlstLicData.add(templic);
				}
			}
			itLstLicData = newlstLicData.iterator();

			/*Validating all License Data*/
			Logger.logDebug(MODULE,"Validating All License Data");
			while (itLstLicData.hasNext()) {
				LicenseData templic = itLstLicData.next();
				Logger.logDebug(MODULE, "Validating :" + templic.getModule());
				Logger.logDebug(MODULE, "Validating :" + templic.getName());
				Logger.logDebug(MODULE, "Validating :" + templic.getValue());
				Logger.logDebug(MODULE, "Validating :" + templic.getOperator());
				Logger.logDebug(MODULE, "Validating :" + templic.getValueType());
				if (templic.getVersion() == null || templic.getVersion().equalsIgnoreCase("")) {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.nullorempty","Version",templic.getName()));
					throw new DataValidationException("Vesion is null/Empty");
				}

				if (templic.getAdditionalKey() == null || templic.getAdditionalKey().equalsIgnoreCase("")) {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.nullorempty","Additional Key",templic.getName()));
					throw new DataValidationException("Additional Key is null/Empty");
				}
				
				if (templic.getStatus() == null || templic.getStatus().equalsIgnoreCase("")) {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.nullorempty","Status",templic.getName()));
					throw new DataValidationException("Status is null/Empty");
				}

				if(templic.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE)){
					if(templic.getValue() == null || templic.getValue().toString().equalsIgnoreCase("")){
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.invalid","Module Value",templic.getName()));
						throw new DataValidationException("Module Value is null/Empty");
					}
				}

				if(templic.getType().equalsIgnoreCase(LicenseTypeConstants.NODE)){
					if(templic.getValue() == null || templic.getValue().toString().equalsIgnoreCase("")){
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.invalid","Public Key",templic.getName()));
						throw new DataValidationException("publickey is null/Empty");
					}
				}

				if (templic.getType().equalsIgnoreCase(LicenseTypeConstants.EXPIRY_DATE)) {
					try {
						Date d = dateFormat.parse(templic.getValue().toString());
						templic.setValue(templic.getValue().toString());
					}catch (Exception e) {
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.invalid","Expiry Date",templic.getName()));
						throw new DataValidationException("Expiry Date is Invalid",e);
					}
				}

			}
			Logger.logDebug(MODULE,"Validating All License Data Complete...");

			EliteLicenceKeyGenerator eliteLicenseKeyGen = new EliteLicenceKeyGenerator();
			Logger.logDebug(MODULE,"Genrate License Called..");
			String licenseKey = eliteLicenseKeyGen.genreateLicense(newlstLicData);
			Logger.logDebug(MODULE,"Genrate License Genrated Succesfully..");

			Logger.logDebug(MODULE,"Printing Genrated Lic Details..");
			itLstLicData = newlstLicData.iterator();
			int i=1;
			while (itLstLicData.hasNext()) {
				LicenseData templic = itLstLicData.next();

				Logger.logWarn(MODULE, "Lic No~~~~~~~~~~:" + (i++));
				Logger.logWarn(MODULE, "Name~~~~~~~~~~~~:" + templic.getName());
				Logger.logWarn(MODULE, "Module~~~~~~~~~~:" + templic.getModule());
				Logger.logWarn(MODULE, "Type~~~~~~~~~~~~:" + templic.getType());
				Logger.logWarn(MODULE, "Value~~~~~~~~~~~:" + templic.getValue());
				Logger.logWarn(MODULE, "Version~~~~~~~~~:" + templic.getVersion());
				Logger.logWarn(MODULE, "Additional Key~~:" + templic.getAdditionalKey());
				Logger.logWarn(MODULE, "Stats~~~~~~~~~~~:" + templic.getStatus());
				Logger.logWarn(MODULE, "Display Name~~~~:" + templic.getDisplayName());
				Logger.logWarn(MODULE, "Description~~~~~:" + templic.getDescription() + "\n");

			}
			Logger.logWarn(MODULE,newlstLicData.size() + " License Successfulluy Genrated......");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + LicenseConstants.LICENSE_FILE_NAME + LicenseConstants.LICESE_FILE_EXT);
			response.setContentType("application/octet-stream");
			Logger.logWarn(MODULE,"Wrighting License Key in Response....");
			out = response.getOutputStream();
			writer = new PrintWriter(out);
			writer.print(licenseKey);
			/* Closeing all the streams */
			writer.close();
			out.close();
			Logger.logWarn(MODULE,"License Key successfully Exported......");
			return null;
		} 
		catch(DataValidationException e){
			Logger.logError(MODULE,"License Genration failed...");
			Logger.logTrace(MODULE, e);
			if (writer != null)
				writer.close();
			if (out != null)
				out.close();
		}
		catch (Exception e) {
			errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("license.genration.faild"));
			Logger.logError(MODULE,"License Genration failed...");
			Logger.logTrace(MODULE, e);
			if (writer != null)
				writer.close();
			if (out != null)
				out.close();
		}
		finally {
			if (writer != null)
				writer.close();
			if (out != null)
				out.close();
		}
		saveErrors(request,errors);
		return  mapping.findForward(FAILURE_FORWARD);
	}

	public void markUnchekedSubLic( String strModualName, List<LicenseData> lstLicData ) {
		Iterator<LicenseData> itLicData = lstLicData.iterator();
		Logger.logDebug(MODULE, "Modual" + strModualName + " is not Selected and Removing Sub Modules..");
		while (itLicData.hasNext()){
			LicenseData tempLicData = itLicData.next();

			if (tempLicData.getModule().startsWith(strModualName)) {
				Logger.logDebug(MODULE,tempLicData.getName()+ " is Marked N");
				tempLicData.setSelect("N");
			}
		}
		Logger.logDebug(MODULE, "Modual" + strModualName + " is not Selected and Removing Complete.."); 
	}

}

