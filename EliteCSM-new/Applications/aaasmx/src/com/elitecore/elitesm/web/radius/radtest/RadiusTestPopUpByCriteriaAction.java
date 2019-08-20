package com.elitecore.elitesm.web.radius.radtest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.radtest.forms.RadiusTestPopUpForm;


public class RadiusTestPopUpByCriteriaAction extends BaseWebAction {
    private static final String SUCCESS_FORWARD = "success";               
    private static final String FAILURE_FORWARD = "failure";               
    private static final String MODULE = "RadiusTestPopUpByCriteriaAction";
    private static final String VIEW_FORWARD = "radiusPacketPopUp"; 

    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        RadiusTestPopUpForm radiusTestPopUpForm = (RadiusTestPopUpForm)form;
        ActionMessages messages = new ActionMessages();
        
        try {
        		HttpSession session = request.getSession(true);
                String dictionaryId = radiusTestPopUpForm.getDictionaryId();
                String searchByName = radiusTestPopUpForm.getSearchByName();
                List listDictionaryByCriteria = new ArrayList();
                
                RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
                        IDictionaryParameterDetailData dictionaryParameterSearchData = new DictionaryParameterDetailData(); 
                
                List<DictionaryData> dictionaryList = dictionaryBLManager.getDictionaryList();        
                List<String> dictionaryIDList = new ArrayList<String>();
                
                if(dictionaryList!=null){
                	for (Iterator<DictionaryData> iterator = dictionaryList.iterator(); iterator.hasNext();) {
                		DictionaryData dictionaryData = (DictionaryData) iterator.next();
                		dictionaryIDList.add(dictionaryData.getDictionaryId());
					}
                }
                
                if( Strings.isNullOrEmpty(dictionaryId) == false )
                        dictionaryParameterSearchData.setDictionaryId(dictionaryId);
                else
                        dictionaryParameterSearchData.setDictionaryId("0");
                
                if(searchByName != null && !(searchByName.equalsIgnoreCase(""))){
                        dictionaryParameterSearchData.setName(searchByName);
                }
                else
                        dictionaryParameterSearchData.setName("");
                
                
                
         		if(dictionaryId.equals("0") && searchByName.equalsIgnoreCase("")){
        			listDictionaryByCriteria = (ArrayList)dictionaryBLManager.getDictionaryListByParameter(dictionaryIDList,"");

        		}
        		else if(dictionaryId.equals("0")  && !(searchByName.equalsIgnoreCase(""))){
        			listDictionaryByCriteria = (ArrayList)dictionaryBLManager.getDictionaryListByParameter(dictionaryIDList,searchByName);
        		}
        		else
        			listDictionaryByCriteria = (ArrayList)dictionaryBLManager.getDictionaryListByParameter(dictionaryParameterSearchData,searchByName);
        		
        		Collections.sort(listDictionaryByCriteria);
        		radiusTestPopUpForm.setDictionaryListByCriteria(listDictionaryByCriteria);

        		radiusTestPopUpForm.setDictionaryListInCombo((ArrayList)session.getAttribute("dl"));
        		
        } catch(DataManagerException e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
        	Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
        	request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("radius.radtest.datasource.failed");
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
            
        } catch(Exception e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
        	Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
        	request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);   
            saveMessages(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        
        radiusTestPopUpForm.setDictionaryListInCombo((ArrayList)request.getSession(true).getAttribute("dl"));
        return mapping.findForward(VIEW_FORWARD); 
    }

}
