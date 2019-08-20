package com.elitecore.netvertexsm.web.servermgr.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ShowNetServerDictionaryForm;

public class InitShowNetServerDictionaryAction extends BaseWebAction {
    
    private static final String SUCCESS_FORWARD = "showNetServerDictionary";
    private static final String FAILURE_FORWARD = "failure";
    
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        Logger.logInfo(MODULE, "Enter the execute method of :" + getClass().getName());
        try {
            ShowNetServerDictionaryForm showNetServerDictionaryForm = (ShowNetServerDictionaryForm) form;
            showNetServerDictionaryForm.setFileName(request.getParameter("fileName"));
            showNetServerDictionaryForm.setNetServerId(Long.parseLong(request.getParameter("netServerId")));
            showNetServerDictionaryForm.setFileGroup(request.getParameter("fileGroup"));
            return mapping.findForward(SUCCESS_FORWARD);
        }
        catch (Exception seException) {
            Logger.logError(MODULE, "Error during operation,reason :" + seException.getMessage());
            Logger.logTrace(MODULE, seException);
            ActionMessage message = new ActionMessage("servermgr.server.dictionary.jmx.server.error");
            ActionMessages messages = new ActionMessages();
            messages.add("warn", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
    }
    
}
