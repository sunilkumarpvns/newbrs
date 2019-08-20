package com.elitecore.elitesm.web.radius.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.AuthorizationException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.FormatInvalidException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidFileExtensionException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.NullValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.ValueOutOfRangeException;
import com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport.EnvironmentNotSupportedException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.dictionary.forms.ViewDictionaryForm;
import com.elitecore.elitesm.web.radius.dictionary.forms.ViewDictionaryParamForm;

public class ViewDictionaryParamAction extends BaseDictionaryAction {
    
    private static final String SUCCESS_FORWARD = "success";
    private static final String VIEW_FORWARD    = "viewDictionaryParam";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS = "VIEW_DICTIONARY_ACTION";
    
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
    	if(checkAccess(request, ACTION_ALIAS)){
	        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
	        ActionErrors errors = new ActionErrors();
	        try {
	            boolean dictionaryFoundFlag = false;
	            String dictionaryId = request.getParameter("dictionaryId");
	            String strDictionaryDetailId = request.getParameter("dictionaryDetailId");
	            	
	            
	            if (dictionaryId != null && strDictionaryDetailId != null) {
	                RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
	                String dictionaryDetailId = strDictionaryDetailId; 
	                
	                Collection<String> colDictionaryId = new ArrayList<String>();
	                colDictionaryId.add(dictionaryDetailId != null ? dictionaryId : null);
	                
	                IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
	                
	                List dictionaryList = dictionaryBLManager.getAllDictionaryById(colDictionaryId,staffData,actionAlias);
	                ViewDictionaryParamForm viewForm = (ViewDictionaryParamForm) form;
	                Logger.logTrace(MODULE, "Returning success forward from " + getClass().getName());
	                
	                // modification started by dhaval
	                IDictionaryData dictionaryData;
	                
	                if (dictionaryList.size() == 1) {
	                    dictionaryData = (IDictionaryData) dictionaryList.get(0);
	                    
	                    for ( Iterator itr = dictionaryData.getDictionaryParameterDetail().iterator(); itr.hasNext(); ) {
	                        IDictionaryParameterDetailData dictionaryParameterDetailData = (IDictionaryParameterDetailData) itr.next();
	                        if (dictionaryParameterDetailData.getDictionaryParameterDetailId().equals(dictionaryDetailId)) {
	                            viewForm.setDictionaryParamDetailData(dictionaryParameterDetailData);
	                            // dictionaryData.setDictionaryParameterDetail(null);
	                            viewForm.setDictionaryData(dictionaryData);
	                            dictionaryFoundFlag = true;
	                        }
	                    }
	                }
	            }
	            
	            if (dictionaryFoundFlag == false) {
	                ActionMessage message = new ActionMessage("dictionary.create.failure");
	                ActionMessages messages = new ActionMessages();
	                messages.add("information", message);
	                saveErrors(request, messages);
	                return mapping.findForward(FAILURE_FORWARD);
	            } else {
	                Logger.logTrace(MODULE, "Returning success forward from " + getClass().getName());
	                return mapping.findForward(VIEW_FORWARD);
	            }
	        }
	        
	        catch (AuthorizationException authExp) {
	            
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());
	            Logger.logTrace(MODULE, authExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
				request.setAttribute("errorDetails", errorElements);

	        }
	        catch (EnvironmentNotSupportedException envException) {
	            
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + envException.getMessage());
	            Logger.logTrace(MODULE, envException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(envException);
				request.setAttribute("errorDetails", errorElements);

	        }
	        catch (CommunicationException connException) {
	            
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + connException.getMessage());
	            Logger.logTrace(MODULE, connException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(connException);
				request.setAttribute("errorDetails", errorElements);

	        }
	        catch (OperationFailedException opException) {
	            
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + opException.getMessage());
	            Logger.logTrace(MODULE, opException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(opException);
				request.setAttribute("errorDetails", errorElements);

	        }
	        catch (ConstraintViolationException conException) {
	            
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + conException.getMessage());
	            Logger.logTrace(MODULE, conException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(conException);
				request.setAttribute("errorDetails", errorElements);

	        }
	        catch (FormatInvalidException forException) {
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + forException.getMessage());
	            Logger.logTrace(MODULE, forException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(forException);
				request.setAttribute("errorDetails", errorElements);

	        }
	        catch (NullValueException nullValueException) {
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + nullValueException.getMessage());
	            Logger.logTrace(MODULE, nullValueException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(nullValueException);
				request.setAttribute("errorDetails", errorElements);

	            
	        }
	        catch (InvalidFileExtensionException invException) {
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + invException.getMessage());
	            Logger.logTrace(MODULE, invException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invException);
				request.setAttribute("errorDetails", errorElements);

	        }
	        catch (InvalidValueException invalidValueException) {
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + invalidValueException.getMessage());
	            Logger.logTrace(MODULE, invalidValueException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invalidValueException);
				request.setAttribute("errorDetails", errorElements);

	            
	        }
	        catch (ValueOutOfRangeException valueOutOfRangeException) {
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + valueOutOfRangeException.getMessage());
	            Logger.logTrace(MODULE, valueOutOfRangeException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(valueOutOfRangeException);
				request.setAttribute("errorDetails", errorElements);

	            
	        }
	        catch (DataValidationException validationException) {
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + validationException.getMessage());
	            Logger.logTrace(MODULE, validationException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(validationException);
				request.setAttribute("errorDetails", errorElements);

	        }
	        catch (DataManagerException mgrException) {
	            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + mgrException.getMessage());
	            Logger.logTrace(MODULE, mgrException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(mgrException);
				request.setAttribute("errorDetails", errorElements);

	        }
	        catch (Exception exp) {
	            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
	            Logger.logTrace(MODULE, exp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
				request.setAttribute("errorDetails", errorElements);

	        }
	        
	        // TODO : Baiju - set error message and then forward to error page.
	        Logger.logTrace(MODULE, "Returning error forward from " + getClass().getName());
	        ActionMessage message = new ActionMessage("dictionary.create.failure");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        return mapping.findForward(FAILURE_FORWARD);
	    }else{
            Logger.logError(MODULE, "Error during Data Manager operation ");
            ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
    }
	    
    private ViewDictionaryForm getDictionaryValues( IDictionaryData dictionaryData ) {
        ViewDictionaryForm viewDictionaryForm = new ViewDictionaryForm();
        viewDictionaryForm.setDictionaryId(dictionaryData.getDictionaryId());
        viewDictionaryForm.setName(dictionaryData.getName());
        viewDictionaryForm.setDescription(dictionaryData.getDescription());
        viewDictionaryForm.setModalNumber(dictionaryData.getModalNumber());
        viewDictionaryForm.setEditable(dictionaryData.getEditable());
        viewDictionaryForm.setSystemGenerated(dictionaryData.getSystemGenerated());
        viewDictionaryForm.setDictionaryNumber(dictionaryData.getDictionaryNumber());
        viewDictionaryForm.setCommonStatusId(dictionaryData.getCommonStatusId());
        viewDictionaryForm.setLastModifiedDate(dictionaryData.getLastModifiedDate());
        viewDictionaryForm.setLastModifiedByStaffId(dictionaryData.getLastModifiedByStaffId());
        viewDictionaryForm.setVendorId(dictionaryData.getVendorId());
        viewDictionaryForm.setCreateDate(dictionaryData.getCreateDate());
        viewDictionaryForm.setCreatedByStaffId(dictionaryData.getCreatedByStaffId());
        viewDictionaryForm.setStatusChangedDate(dictionaryData.getStatusChangedDate());
        viewDictionaryForm.setDictionaryParameterDetail(dictionaryData.getDictionaryParameterDetail());
        viewDictionaryForm.setLastModifiedByStaff(dictionaryData.getLastModifiedByStaff());
        viewDictionaryForm.setCreatedByStaff(dictionaryData.getCreatedByStaff());
        
        return viewDictionaryForm;
    }
}
