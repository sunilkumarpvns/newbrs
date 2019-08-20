package com.elitecore.elitelicgen.web;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.util.ArrayList;
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
import org.apache.struts.upload.FormFile;

import com.elitecore.elitelicgen.base.BaseWebAction;
import com.elitecore.elitelicgen.commons.DataValidationException;
import com.elitecore.elitelicgen.util.Logger;
import com.elitecore.elitelicgen.web.forms.UploadLicenseForm;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseModuleConstants;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.util.SystemUtil;

/**
 * @author himanshudobaria
 *
 */
public class UploadLicenseFileAction extends BaseWebAction {
    
    private static String MODULE          = "UPLOADLICENSEFILE";
    private static String FAILURE_FORWARD = "failure";
    private static String VIEW_FORWARD    = "GenrateLicense";
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
        UploadLicenseForm uploadLicenseForm = (UploadLicenseForm) form;
        
        ActionErrors errors = new ActionErrors();
        
        try {
            
            /* Read License Data From License file */
            String licenseDataString = readLicense(uploadLicenseForm.getLicenseFile());
            List<LicenseData> lstLicenseData = new ArrayList<LicenseData>();
            
            if (licenseDataString != null && !licenseDataString.equalsIgnoreCase("")) {
                lstLicenseData = SystemUtil.sequenceingLicense(SystemUtil.populateAdditionalInformations(SystemUtil.getLicenseInformationMap(licenseDataString, false)));
            } else {
                throw new DataValidationException("License Data Is Empty");
            }
            
            /* Identify License Object */
            LicenseData licData = null;
            List sysLiclist = new ArrayList();
            LicenseData licNodeData = new LicenseData();
            
            for ( int i = 0; i < lstLicenseData.size(); i++ ) {
                LicenseData licenseData = (LicenseData) lstLicenseData.get(i);
            
                if (licenseData.getName().equalsIgnoreCase(LicenseNameConstants.SYSTEM_NODE)) {
                    licNodeData = licenseData;
                    sysLiclist.add(licenseData);
                }else if(licenseData.getModule().equals(LicenseModuleConstants.SYSTEM)){	
                	sysLiclist.add(licenseData);
                }else if (licenseData.getModule().equalsIgnoreCase(LicenseModuleConstants.SYSTEM_RADIUS)) {
                    licData = licenseData;
                }else if (licenseData.getModule().equalsIgnoreCase(LicenseModuleConstants.SYSTEM_RESOURCEMANAGER)) {
                    licData = licenseData;
                }else if (licenseData.getModule().equalsIgnoreCase(LicenseModuleConstants.SYSTEM_AAA)){
                	licData = licenseData;
                }else if (licenseData.getModule().equalsIgnoreCase(LicenseModuleConstants.SYSTEM_NV)){
                	licData = licenseData;
                }
            }
            if (licData == null) {
                throw new DataValidationException("License Data is not valid for this license");
            }
            
            /* Set value in session */
            request.getSession(false).setAttribute("sysLiclist", sysLiclist);
            request.getSession(false).setAttribute("modualLicData", licData);
            request.getSession(false).setAttribute("status", licData.getStatus());
            request.getSession(false).setAttribute("publicKey", licNodeData.getValue());
            request.getSession(false).setAttribute("additionalKey", licData.getAdditionalKey());
            request.getSession(false).setAttribute("version", licData.getVersion());
            
            /* Generate License List From LicenseDataManager */
            Map<String, LicenseData> licMap = (SequencedHashMap)request.getSession(false).getAttribute("licMap");
            LicenseData licDataFromMap = licMap.get(licData.getModule());
            List<LicenseData> lstLicDataFromMap = new ArrayList<LicenseData>();
            lstLicDataFromMap = SystemUtil.getSequenceLicData(licDataFromMap, 0, lstLicDataFromMap);
            
            /* Generate License List From Uploaded License File*/
            List resultLiclist = new ArrayList();
            for ( int i = 0; i < lstLicenseData.size(); i++ ) {
                LicenseData licenseData = (LicenseData) lstLicenseData.get(i);
                if (licenseData.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE) && !licenseData.getModule().equals(LicenseModuleConstants.SYSTEM_SM)) {
                    resultLiclist.add(licenseData);
                }
            }
            
            /* Compare Both License */
            List comparedLiclist = new ArrayList();
            for ( int i = 0; i < lstLicDataFromMap.size(); i++ ) {
                LicenseData l1 = (LicenseData) lstLicDataFromMap.get(i);
                
                boolean flag = false;
                for ( int j = 0; j < resultLiclist.size(); j++ ) {
                    LicenseData l2 = (LicenseData) resultLiclist.get(j);
                    if (l2.getModule().equalsIgnoreCase(l1.getModule())) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    l1.setState("true");
                    comparedLiclist.add(l1);
                } else {
                    l1.setState("false");
                    comparedLiclist.add(l1);
                }
            }
            
            /* Set compared license list in session */
            request.getSession(false).setAttribute("resultLiclist", comparedLiclist);
            return mapping.findForward(VIEW_FORWARD);
            
        }
        catch (InvalidLicenseKeyException e) {
            Logger.logError(MODULE, "Upload License Failed...");
            Logger.logTrace(MODULE, e);
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("license.genration.faild"));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (DataValidationException e) {
            Logger.logError(MODULE, "Upload License Failed...");
            Logger.logTrace(MODULE, e);
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("license.genration.faild"));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (Exception e) {
            Logger.logError(MODULE, "Upload License Failed...");
            Logger.logTrace(MODULE, e);
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("license.genration.faild"));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (Throwable e) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("license.genration.faild"));
            Logger.logError(MODULE, "Upload License Failed...");
            Logger.logTrace(MODULE, e);
            saveErrors(request, errors);
            return mapping.findForward(FAILURE_FORWARD);
        }
        
    }
    
    public String readLicense( Object fileOb ) throws DataValidationException {
        InputStream inputStream = null;
        DataInputStream dataInuptStream = null;
        
        FormFile file = null;
        try {
            if (fileOb instanceof FormFile) {
                file = (FormFile) fileOb;
            } else {
                throw new InvalidClassException("Value Object is not FormFile Type");
            }
            
            if (file.getFileSize() == 0) {
                throw new Exception("Publickey File Size is Zero");
            }
            
            inputStream = file.getInputStream();
            dataInuptStream = new DataInputStream(inputStream);
            System.out.println("File size" + file.getFileSize());
            byte[] buffer = new byte[(int) file.getFileSize()];
            dataInuptStream.readFully(buffer);
            dataInuptStream.close();
            inputStream.close();
            String publicKey = new String(buffer);
            return publicKey;
        }
        catch (InvalidClassException e) {
            if (dataInuptStream != null)
                dataInuptStream = null;
            throw new DataValidationException("Unable to Read License file", e);
        }
        catch (Exception e) {
            if (dataInuptStream != null)
                dataInuptStream = null;
            throw new DataValidationException("Unable to Read License file", e);
        }
    }
    
}
