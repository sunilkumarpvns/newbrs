package com.elitecore.elitesm.web.radius.dictionary;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.dictionary.forms.ListDictionaryForm;


public class ListDictionaryAction extends BaseDictionaryAction {
    
    
    private static final String SUCCESS_FORWARD = "listDictionary";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS = ConfigConstant.LIST_DICTIONARY_ACTION;
    
    public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
    	if(checkAccess(request, ACTION_ALIAS)){	
    		Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());

    		try{
    			RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
    			ListDictionaryForm lstForm = (ListDictionaryForm)form;
    			Logger.logTrace(MODULE, "Returning success forward from " + getClass().getName());


    			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    			String actionAlias = ACTION_ALIAS;
    			List dictionaryList = dictionaryBLManager.getAllDictionaryList();
    			lstForm.setDictionaryList(dictionaryList);            			
    			lstForm.setAction("listTable");
    			doAuditing(staffData, actionAlias);
    			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName());  
    			return mapping.findForward(SUCCESS_FORWARD);

    		}
    		catch (AuthorizationException authExp) {

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +authExp.getMessage());
    			Logger.logTrace(MODULE,authExp);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);

    		}catch (EnvironmentNotSupportedException envException) {

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +envException.getMessage());
    			Logger.logTrace(MODULE,envException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(envException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);
    		}
    		catch (CommunicationException connException) {


    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +connException.getMessage());
    			Logger.logTrace(MODULE,connException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(connException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);
    		}
    		catch(OperationFailedException opException)
    		{

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +opException.getMessage());
    			Logger.logTrace(MODULE,opException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(opException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);
    		}
    		catch(ConstraintViolationException conException)
    		{

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +conException.getMessage());
    			Logger.logTrace(MODULE,conException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(conException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);
    		}
    		catch(FormatInvalidException forException)
    		{
    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +forException.getMessage());
    			Logger.logTrace(MODULE,forException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(forException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);

    		}
    		catch(NullValueException nullValueException)
    		{

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +nullValueException.getMessage());
    			Logger.logTrace(MODULE,nullValueException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(nullValueException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages); 
    		}
    		catch(InvalidFileExtensionException invException)
    		{

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +invException.getMessage());
    			Logger.logTrace(MODULE,invException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);
    		}
    		catch(InvalidValueException invalidValueException)
    		{

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +invalidValueException.getMessage());
    			Logger.logTrace(MODULE,invalidValueException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invalidValueException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);
    		}
    		catch(ValueOutOfRangeException valueOutOfRangeException)
    		{

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +valueOutOfRangeException.getMessage());
    			Logger.logTrace(MODULE,valueOutOfRangeException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(valueOutOfRangeException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);
    		}
    		catch(DataValidationException validationException)
    		{


    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +validationException.getMessage());
    			Logger.logTrace(MODULE,validationException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(validationException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);
    		}

    		catch(DataManagerException mgrException)
    		{
    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +mgrException.getMessage());
    			Logger.logTrace(MODULE,mgrException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(mgrException);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);
    		}
    		catch (Exception exp){
    			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
    			request.setAttribute("errorDetails", errorElements);

    			ActionMessage message = new ActionMessage("dictionary.list.failure");
    			ActionMessages messages = new ActionMessages();
    			messages.add("information", message);
    			saveErrors(request, messages);
    		}

    		//TODO : Baiju - set error message and then forward to error page.
    		Logger.logTrace(MODULE, "Returning error forward from " + getClass().getName());
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
	
	
}
