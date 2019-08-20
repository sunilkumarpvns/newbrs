package com.elitecore.elitesm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.server.forms.AddAttributeDetailForm;

public class AddAttributeDetailAction extends BaseWebAction{
    private final String FAILURE_FORWARD="failure";
    private final String VIEW_FORWARD ="listAttributeDetail";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.ADD_ATTRIBUTE_DETAIL_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE, "Enter the execute method of :"+ getClass().getName());
        AddAttributeDetailForm addAttributeDetailForm=(AddAttributeDetailForm)form;

        try{
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            RadiusDictionaryBLManager dictionaryDataManager=new RadiusDictionaryBLManager();
            List listDictionary=(List)dictionaryDataManager.getDictionaryList();
            addAttributeDetailForm.setListDictionary(listDictionary);
            return mapping.findForward(VIEW_FORWARD);

        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch(Exception managerExp){
            Logger.logError(MODULE,"Error during Data Manager operation, reason :"+managerExp.getMessage());
            Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
        }
        ActionMessage message = new ActionMessage("userfiledatasource.addattribute.failed");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD); 
    }

}
