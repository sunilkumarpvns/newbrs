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

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.util.ArrayList;
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
import org.apache.struts.upload.FormFile;

import com.elitecore.elitelicgen.base.BaseWebAction;
import com.elitecore.elitelicgen.commons.DataValidationException;
import com.elitecore.elitelicgen.util.Logger;
import com.elitecore.elitelicgen.web.forms.ChooseModuleForm;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseDataManager;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.util.EncryptDecrypt;
import com.elitecore.license.util.SystemUtil;

/**
 * @author kaushikvira
 *
 */
public class ChooseModuleAction extends BaseWebAction {
    
    private static String MODULE          = "CHOOSEMODUAL";
    private static String VIEW_FORWARD    = "GenrateLicense";
    private static String FAILURE_FORWARD = "failure";
    
    @Override
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        ActionErrors errors = null;
        try {
            
            Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
            
            errors = new ActionErrors();
            ChooseModuleForm chooseForm = (ChooseModuleForm) form;
            Logger.logDebug(MODULE, " User Selected " + chooseForm.getChoice());
            
            Map<String, LicenseData> licMap = (SequencedHashMap) request.getSession(false).getAttribute("licMap");
            LicenseData licData = licMap.get(chooseForm.getChoice());
            String publicKey = readPublicKey(chooseForm.getPubkeyFile());
            
            
            try {
                EncryptDecrypt.decrypt(publicKey); 
            }catch(Exception e) {
                publicKey = "Invalid or corrupted License Key. License generated based on this License Key will not be a valid." + "|" + publicKey + "|";
            }

            request.getSession(false).setAttribute("modualLicData", licData);
            request.getSession(false).setAttribute("status", chooseForm.getStatus());
            request.getSession(false).setAttribute("publicKey", publicKey);
            request.getSession(false).setAttribute("additionalKey", chooseForm.getAdditionalKey());
            request.getSession(false).setAttribute("version", chooseForm.getVersion());
            
            List sysLiclist = new ArrayList();
            List<LicenseData> lstLicDataFromDataManager = (ArrayList<LicenseData>) LicenseDataManager.getLicenseData().getLicenseData();
            Iterator<LicenseData> itLstLicData = lstLicDataFromDataManager.iterator();
            
            while (itLstLicData.hasNext()) {
                LicenseData tempLicData = itLstLicData.next();
                if (!tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE)) {
                    sysLiclist.add(tempLicData);
                }
            }    
            
            List<LicenseData> lstLicData = new ArrayList<LicenseData>();
            lstLicData = SystemUtil.getSequenceLicData(licData, 0, lstLicData);
            
            List comparedLiclist = new ArrayList();
            for(int i = 0;i<lstLicData.size();i++) {
                LicenseData l1 = (LicenseData)lstLicData.get(i);
                comparedLiclist.add(l1);
            }    
            request.getSession(false).setAttribute("resultLiclist", comparedLiclist);
            request.getSession(false).setAttribute("sysLiclist", sysLiclist);
            
            return mapping.findForward(VIEW_FORWARD);
        }
        catch (DataValidationException e) {
            Logger.logError(MODULE, "Choose Module Failed...");
            Logger.logTrace(MODULE, e);
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("publickey.unable.read"));
        }
        catch (Exception e) {
            Logger.logError(MODULE, "Choose Module Failed...");
            Logger.logTrace(MODULE, e);
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("license.genration.faild"));
        }
        catch (Throwable e) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("license.genration.faild"));
            Logger.logError(MODULE, "Choose Module Failed...");
            Logger.logTrace(MODULE,e);
        }
        saveErrors(request, errors);
        return mapping.findForward(FAILURE_FORWARD);
    }
    
    public String readPublicKey( Object fileOb ) throws DataValidationException {
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
            throw new DataValidationException("Unable to Read Public Key file", e);
        }
        catch (Exception e) {
            if (dataInuptStream != null)
                dataInuptStream = null;
            throw new DataValidationException("Unable to Read Public Key file", e);
        }
    }
    
   /* public List<LicenseData> getSequenceLicData( LicenseData licData ,
                                                 int index ,
                                                 List<LicenseData> lstLicData ) {
        index++;
        try {
            if (licData.getLicenseData() != null) {
                List<LicenseData> subLicenseList = licData.getLicenseData();
                
                Iterator<LicenseData> itSubLicenseList = subLicenseList.iterator();
                while (itSubLicenseList.hasNext()) {
                    LicenseData tempLicData = itSubLicenseList.next();
                    tempLicData.setIndex(index);
                    lstLicData.add(tempLicData);
                    if (tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE)) {
                        lstLicData = getSequenceLicData(tempLicData, index, lstLicData);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return lstLicData;
    }*/
    
    /*public List<LicenseData> addAll( List<LicenseData> list1 ,
                        List<LicenseData> list2 ) {
        
        Iterator<LicenseData> itList2 = list2.iterator();
        
        while (itList2.hasNext()) {
            list1.add(itList2.next());
        }
        
        return list1;
    }
    
    public boolean isModuleLic( LicenseData lic ) {
        if (lic.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE))
            return true;
        else return false;
    }
    
    public boolean hasChildList( LicenseData licData ) {
        
        if (licData.getLicenseData() != null)
            return true;
        else {
            return !hasModualLic(licData);
        }
    }
    
    public boolean hasChildLicList( LicenseData licData ) {
        
        if (licData.getLicenseData() != null)
            return true;
        else {
            return false;
        }
    }
    
    public boolean hasModualLic( LicenseData licData ) {
        
        if (licData.getLicenseData() != null) {
            Iterator<LicenseData> itLic = licData.getLicenseData().iterator();
            
            while (itLic.hasNext()) {
                if (itLic.next().getType().equalsIgnoreCase(LicenseTypeConstants.MODULE))
                    return true;
            }
            
        }
        return false;
    }
    
    public List<LicenseData> getModualLicList( LicenseData licData ) {
        
        List<LicenseData> resultList = new ArrayList<LicenseData>();
        if (licData.getLicenseData() != null) {
            
            Iterator<LicenseData> itLstLicData = licData.getLicenseData().iterator();
            
            while (itLstLicData.hasNext()) {
                LicenseData tempLicData = itLstLicData.next();
                
                if (isModuleLic(tempLicData)) {
                    resultList.add(tempLicData);
                }
            }
        }
        licData.setFlag(true);
        resultList.add(licData);
        System.out.println("retruning from getModualLicList " + resultList);
        return resultList;
    }
    
    public List<LicenseData> getChildLicList( LicenseData licData ) {
        
        List<LicenseData> resultList = new ArrayList<LicenseData>();
        if (licData.getLicenseData() != null) {
            Iterator<LicenseData> itLstList = licData.getLicenseData().iterator();
            LicenseData tempLicData = null;
            while (itLstList.hasNext()) {
                tempLicData = itLstList.next();
                if (!isModuleLic(tempLicData)) {
                    resultList.add(tempLicData);
                }
            }
            
        }
        resultList.add(licData);
        return resultList;
    }*/
    
}
