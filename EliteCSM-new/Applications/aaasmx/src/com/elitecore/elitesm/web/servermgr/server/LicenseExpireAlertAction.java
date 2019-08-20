package com.elitecore.elitesm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ListNetServerInstanceForm;

public class LicenseExpireAlertAction extends BaseWebAction {
    private static final String SUCCESS_FORWARD ="licenseExpireAlert";
    private static final String FAILURE_FORWARD ="failure";
    private static final String MODULE = "LIST SERVER ACTION";

    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
    	Logger.logTrace(MODULE,"Enter execute method of  "+getClass().getName());
       try {
    	    boolean bLicenseAlert = false;
			boolean bLicenseAlertForPopup = false;
			boolean bLicenseExpireAlert = false;
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            List<NetServerInstanceData> netServerListForLicense = netServerBLManager.getNetServerInstanceListForLicense();
            List netServerTypeList = netServerBLManager.getNetServerTypeList();
            ListNetServerInstanceForm lstNetServerInstanceForm = (ListNetServerInstanceForm)form;
            
            if(netServerListForLicense != null) {
				for(NetServerInstanceData netServerInstanceData : netServerListForLicense) {
					Integer licenseExpireDays = 0;
					if(netServerInstanceData != null) {
						licenseExpireDays = netServerInstanceData.getLicenseExpiryDays();
					}
					if(licenseExpireDays != null) {
						if(licenseExpireDays > 0 && licenseExpireDays <= 30)
							bLicenseAlert = true;
						if(licenseExpireDays < 3)
							bLicenseAlertForPopup = true;
						if(licenseExpireDays < 0)
							bLicenseExpireAlert = true;
					}
				}
			}
            
            request.setAttribute("bLicenseAlert", String.valueOf(bLicenseAlert));
			request.setAttribute("bLicenseAlertForPopup", String.valueOf(bLicenseAlertForPopup));
			request.setAttribute("bLicenseExpireAlert", String.valueOf(bLicenseExpireAlert));
            request.setAttribute("netServerTypeList",netServerTypeList);
            lstNetServerInstanceForm.setListServer(netServerListForLicense);
            request.setAttribute("netServerListForLicense", netServerListForLicense);
            return mapping.findForward(SUCCESS_FORWARD);
        }
        catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (DataManagerException hExp) {
            Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
        }catch (Exception hExp) {
            Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
        }
        Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
        ActionMessage message = new ActionMessage("servermgr.list.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
}
