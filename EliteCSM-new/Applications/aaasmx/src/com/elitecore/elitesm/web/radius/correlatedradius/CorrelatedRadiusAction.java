package com.elitecore.elitesm.web.radius.correlatedradius;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.radius.correlatedradius.CorrelatedRadiusBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.radius.correlatedradius.data.CorrelatedRadiusData;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseMappingDispatchAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.radius.correlatedradius.form.CorrelatedRadiusForm;
import org.apache.struts.action.*;
import org.apache.struts.actions.MappingDispatchAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

public class CorrelatedRadiusAction extends BaseMappingDispatchAction {

    private static final String MODULE = "CORRELATED_RADIUS";
    private static final String SEARCH_CORRELATED_RADIUS = "searchCorrelatedRadius";
    private static final String CREATE_CORRELATED_RADIUS = "createCorrelatedRadius";
    private static final String VIEW_CORRELATED_RADIUS = "viewCorrelatedRadius";
    private static final String UPDATE_CORRELATED_RADIUS_ESI = "updateCorrelatedRadius";
    private static final String VIEW_CORRELATED_RADIUS_HISTORY = "viewCorrelatedRadiusHistory";
    private static final String ACTION_ALIAS = ConfigConstant.VIEW_CORRELATED_RADIUS;
    protected static final String SUCCESS = "success";
    protected static final String FAILURE = "failure";

    public ActionForward searchCorrelatedRadius(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
        String actionMessage = "";
        StaffBLManager staffBLManager = new StaffBLManager();
        IStaffData staffData = staffBLManager.getStaffDataByUserName("admin");

        CorrelatedRadiusForm correlatedRadiusForm = (CorrelatedRadiusForm) form;
        CorrelatedRadiusBLManager blManager = new CorrelatedRadiusBLManager();

        String[] policyIds = request.getParameterValues("select");
        Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

        int requiredPageNo;
        if(request.getParameter("pageNo") != null){
            requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
        }else{
            requiredPageNo = new Long(correlatedRadiusForm.getPageNumber()).intValue();
        }
        if(requiredPageNo == 0)
            requiredPageNo =1;

        if(correlatedRadiusForm.getAction() != null){

            if(correlatedRadiusForm.getAction().equals("delete")){
                actionMessage="correlated.radius.delete.failed";
                String actionAlias = ConfigConstant.DELETE_CORRELATED_RADIUS;

                checkActionPermission(request,actionAlias);
                try {
                    blManager.deleteCorrelatedRadiusById(Arrays.asList(policyIds), staffData);
                }catch (Exception e)
                {
                    Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                    Logger.logTrace(MODULE,e);
                    Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
                    request.setAttribute("errorDetails", errorElements);
                    ActionMessage message = new ActionMessage(actionMessage);
                    ActionMessages messages = new ActionMessages();
                    messages.add("information",message);
                    saveErrors(request,messages);
                    return mapping.findForward(FAILURE);
                }

                int strSelectedIdsLen = policyIds.length;
                long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,correlatedRadiusForm.getPageNumber(),correlatedRadiusForm.getTotalPages(),correlatedRadiusForm.getTotalRecords());

                request.setAttribute("responseUrl","/searchCorrelatedRadius.do");
                ActionMessage message = new ActionMessage("correlated.radius.delete.success");
                ActionMessages messages1 = new ActionMessages();
                messages1.add("information",message);
                saveMessages(request,messages1);
                return mapping.findForward(SEARCH_CORRELATED_RADIUS);
            }
        }

        CorrelatedRadiusData correlatedRadiusData = new  CorrelatedRadiusData();

        String strName=correlatedRadiusForm.getName();
        if(strName!=null){
            correlatedRadiusData.setName("%"+strName+"%");
        }
        else{
            correlatedRadiusData.setName("");
        }

        PageList pageList = blManager.search(correlatedRadiusData, staffData, requiredPageNo, pageSize);

        correlatedRadiusForm.setName(strName);
        correlatedRadiusForm.setPageNumber(pageList.getCurrentPage());
        correlatedRadiusForm.setTotalPages(pageList.getTotalPages());
        correlatedRadiusForm.setTotalRecords(pageList.getTotalItems());
        correlatedRadiusForm.setCorrelatedEsiList(pageList.getListData());
        correlatedRadiusForm.setCorrelatedEsiData(pageList.getCollectionData());
        correlatedRadiusForm.setAction(BaseConstant.LISTACTION);
        return  mapping.findForward(SEARCH_CORRELATED_RADIUS);
    }

    public ActionForward createCorrelatedRadius(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
            String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();

            CorrelatedRadiusForm correlatedRadiusForm = (CorrelatedRadiusForm) form;
            ExternalSystemInterfaceBLManager esiBlManager = new ExternalSystemInterfaceBLManager();
            if("create".equals(correlatedRadiusForm.getAction())){

                CorrelatedRadiusData correlatedRadiusData = new CorrelatedRadiusData();
                try {
                    String authEsiName = esiBlManager.getRadiusESINameById(correlatedRadiusForm.getAuthEsiName());
                    String acctEsiName = esiBlManager.getRadiusESINameById(correlatedRadiusForm.getAcctEsiName());

                    correlatedRadiusData.setName(correlatedRadiusForm.getName());
                    correlatedRadiusData.setDescription(correlatedRadiusForm.getDescription());
                    correlatedRadiusData.setAuthEsiId(correlatedRadiusForm.getAuthEsiName());
                    correlatedRadiusData.setAcctEsiId(correlatedRadiusForm.getAcctEsiName());
                    correlatedRadiusData.setAuthEsiName(authEsiName);
                    correlatedRadiusData.setAcctEsiName(acctEsiName);

                    CorrelatedRadiusBLManager correlatedRadiusBLManager = new CorrelatedRadiusBLManager();
                    correlatedRadiusBLManager.create(correlatedRadiusData,staffData);

                    request.setAttribute("responseUrl", "/searchCorrelatedRadius.do");

                    ActionMessage message = new ActionMessage("correlated.radius.create");
                    ActionMessages messages = new ActionMessages();
                    messages.add("information", message);
                    saveMessages(request, messages);

                    return mapping.findForward(SUCCESS);
                }catch(DuplicateParameterFoundExcpetion dpf){
                    Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                    Logger.logTrace(MODULE,dpf);
                    Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
                    request.setAttribute("errorDetails", errorElements);
                    ActionMessage message = new ActionMessage("correlated.radius.duplicate.failure",correlatedRadiusData.getName());
                    ActionMessages messages = new ActionMessages();
                    messages.add("information",message);
                    saveErrors(request,messages);
                    return mapping.findForward(FAILURE);
                }catch(Exception e) {
                    Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                    Logger.logTrace(MODULE,e);
                    Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
                    request.setAttribute("errorDetails", errorElements);
                    ActionMessage message = new ActionMessage("correlated.radius.failed");
                    ActionMessages messages = new ActionMessages();
                    messages.add("information",message);
                    saveErrors(request,messages);
                    return mapping.findForward(FAILURE);
                }
            }else{
                ExternalSystemInterfaceBLManager externalSysBlManager = new ExternalSystemInterfaceBLManager();
                List<ExternalSystemInterfaceInstanceData> authESIDataList = externalSysBlManager.getExternalSystemInstanceDataList(ExternalSystemConstants.AUTH_PROXY);
                List<ExternalSystemInterfaceInstanceData> acctESIDataList = externalSysBlManager.getExternalSystemInstanceDataList(ExternalSystemConstants.ACCT_PROXY);

                correlatedRadiusForm.setDescription(getDefaultDescription(userName));
                correlatedRadiusForm.setAuthExternalSystemInterfaceInstanceData(authESIDataList);
                correlatedRadiusForm.setAcctExternalSystemInterfaceInstanceData(acctESIDataList);
                request.setAttribute("correlatedRadiusForm", correlatedRadiusForm);
                return  mapping.findForward(CREATE_CORRELATED_RADIUS);
            }
        }catch(Exception e){
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
            request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("correlated.radius.failed");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
        }
        return mapping.findForward(FAILURE);
    }

    public ActionForward viewCorrelatedRadius(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CorrelatedRadiusForm correlatedRadiusForm = (CorrelatedRadiusForm) form;
        String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();

        CorrelatedRadiusBLManager blManager = new CorrelatedRadiusBLManager();
        ExternalSystemInterfaceBLManager esiBlManager = new ExternalSystemInterfaceBLManager();
        CorrelatedRadiusData correlatedRadiusData = new CorrelatedRadiusData();
        if(checkActionPermission(request, ACTION_ALIAS)){
            try {
                Logger.logTrace(MODULE, "Enter execute method of" + getClass().getName());

                String correlatedRadId = request.getParameter("id");
                Logger.getLogger().info(MODULE, "ESI Group Id is : " + correlatedRadId);

                String radCorId = "";
                if(Strings.isNullOrBlank(correlatedRadId) == false){
                    radCorId = correlatedRadId;
                }

                if(Strings.isNullOrBlank(radCorId) == false){
                    correlatedRadiusData.setId(radCorId);

                    correlatedRadiusData = blManager.getCorrelatedRadiusDataById(radCorId);

                    correlatedRadiusData.setAuthEsiId(correlatedRadiusData.getAuthEsiId());
                    correlatedRadiusData.setAcctEsiId(correlatedRadiusData.getAcctEsiId());

                    request.setAttribute("correlatedEsi",correlatedRadiusData);
                }
                return mapping.findForward(VIEW_CORRELATED_RADIUS);
            }catch (DataManagerException managerExp) {
                Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
                Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
                request.setAttribute("errorDetails", errorElements);

                ActionMessage message = new ActionMessage("correlated.radius.view.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
                return mapping.findForward(FAILURE);
            }

        }else{
            Logger.logError(MODULE, "Error during Data Manager operation ");
            ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);

            return mapping.findForward(FAILURE);
        }
    }

    public ActionForward viewCorrelatedRadiusHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
        if(checkActionPermission(request, ACTION_ALIAS)){
            try{
                CorrelatedRadiusForm correlatedRadiusForm = (CorrelatedRadiusForm)form;
                String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();

                CorrelatedRadiusBLManager blManager = new CorrelatedRadiusBLManager();
                CorrelatedRadiusData correlatedRadiusData = new CorrelatedRadiusData();

                String correlateId = request.getParameter("id");
                if(Strings.isNullOrBlank(correlateId) == true){
                    correlateId = correlatedRadiusForm.getId();
                }

                if(Strings.isNullOrBlank(correlateId) == false){
                    correlatedRadiusData.setId(correlateId);
                    IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
                    String actionAlias = ACTION_ALIAS;

                    correlatedRadiusData = blManager.getCorrelatedRadiusDataById(correlateId);

                    HistoryBLManager historyBlManager= new HistoryBLManager();

                    String strAuditUid = request.getParameter("auditUid");
                    String strSytemAuditId=request.getParameter("systemAuditId");
                    String name=request.getParameter("name");

                    if(strSytemAuditId != null){
                        request.setAttribute("systemAuditId", strSytemAuditId);
                    }

                    if(correlateId != null && Strings.isNullOrBlank(strAuditUid) == false){

                        staffData.setAuditName(correlatedRadiusData.getName());
                        staffData.setAuditId(correlatedRadiusData.getAuditUId());

                        List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);

                        request.setAttribute("name", name);
                        request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
                    }
                    request.setAttribute("correlatedEsi",correlatedRadiusData);
                }

                return mapping.findForward(VIEW_CORRELATED_RADIUS_HISTORY);

            }catch(Exception e){
                Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
                Logger.logTrace(MODULE,e);
                Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
                request.setAttribute("errorDetails", errorElements);
                ActionMessages messages = new ActionMessages();
                ActionMessage message1 = new ActionMessage("correlated.radius.view.failure");
                messages.add("information",message1);
                saveErrors(request,messages);
            }
            return mapping.findForward(FAILURE);

        }else{
            Logger.logWarn(MODULE, "No Access on this Operation ");
            ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE);
        }
    }
    public ActionForward updateCorrelatedRadius(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
            Logger.logInfo(MODULE,"Enter execute method of :- "+getClass().getName());
            try{
                checkActionPermission(request, ACTION_ALIAS);
                CorrelatedRadiusForm correlatedRadiusForm = (CorrelatedRadiusForm)form;

                CorrelatedRadiusBLManager blManager = new CorrelatedRadiusBLManager();
                CorrelatedRadiusData correlatedRadiusData = new CorrelatedRadiusData();
                ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager =new ExternalSystemInterfaceBLManager();

                String corrId = request.getParameter("id");
                Logger.getLogger().info(MODULE, "Correalted Radius Id is : " + corrId);

                String correlatedId = "";
                if(Strings.isNullOrBlank(corrId) == false){
                    correlatedId = corrId;
                }

                if("update".equals(correlatedRadiusForm.getAction())){
                    try{
                        IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

                        String authEsiName = externalSystemInterfaceBLManager.getRadiusESINameById(correlatedRadiusForm.getAuthEsiName());
                        String acctEsiName = externalSystemInterfaceBLManager.getRadiusESINameById(correlatedRadiusForm.getAcctEsiName());

                        staffData.setAuditName(correlatedRadiusData.getName());
                        staffData.setAuditId(correlatedRadiusData.getAuditUId());

                        correlatedRadiusData.setId(correlatedRadiusForm.getId());
                        correlatedRadiusData.setName(correlatedRadiusForm.getName());
                        correlatedRadiusData.setDescription(correlatedRadiusForm.getDescription());
                        correlatedRadiusData.setAuthEsiId(correlatedRadiusForm.getAuthEsiName());
                        correlatedRadiusData.setAcctEsiId(correlatedRadiusForm.getAcctEsiName());
                        correlatedRadiusData.setAuthEsiName(authEsiName);
                        correlatedRadiusData.setAcctEsiName(acctEsiName);
                        blManager.updateCorrelatedRadiusById(correlatedRadiusData, staffData);

                        Logger.getLogger().info(MODULE, "Correlated Radius [" + correlatedRadiusData.getName() + "] Updated Successfully");

                        request.setAttribute("responseUrl", "/viewCorrelatedRadius?id="+correlatedRadiusData.getId());
                        ActionMessage message = new ActionMessage("correlated.radius.update.success");
                        ActionMessages messages = new ActionMessages();
                        messages.add("information", message);
                        saveMessages(request, messages);

                        return mapping.findForward(SUCCESS);
                    }catch(DuplicateParameterFoundExcpetion dpf){
                        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                        Logger.logTrace(MODULE,dpf);
                        Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
                        request.setAttribute("errorDetails", errorElements);
                        ActionMessage message = new ActionMessage("correlated.radius.update.duplicate.failure",correlatedRadiusData.getName());
                        ActionMessages messages = new ActionMessages();
                        messages.add("information",message);
                        saveErrors(request,messages);
                        return mapping.findForward(FAILURE);
                    }catch(Exception e) {
                        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                        Logger.logTrace(MODULE,e);
                        Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
                        request.setAttribute("errorDetails", errorElements);
                        ActionMessage message = new ActionMessage("correlated.radius.update.failure");
                        ActionMessages messages = new ActionMessages();
                        messages.add("information",message);
                        saveErrors(request,messages);
                        return mapping.findForward(FAILURE);
                    }
                }else{
                    Logger.getLogger().info(MODULE, "Enter in Else method of Update Correlated Radius method : ");

                    CorrelatedRadiusData correlatedRadData = blManager.getCorrelatedRadiusDataById(correlatedId);

                    ExternalSystemInterfaceBLManager externalSysBlManager = new ExternalSystemInterfaceBLManager();
                    List<ExternalSystemInterfaceInstanceData> authESIDataList = externalSysBlManager.getExternalSystemInstanceDataList(ExternalSystemConstants.AUTH_PROXY);
                    List<ExternalSystemInterfaceInstanceData> acctESIDataList = externalSysBlManager.getExternalSystemInstanceDataList(ExternalSystemConstants.ACCT_PROXY);

                    String authEsiId = externalSysBlManager.getRadiusESIIdByName(correlatedRadData.getAuthEsiName());
                    String acctEsiId = externalSysBlManager.getRadiusESIIdByName(correlatedRadData.getAcctEsiName());

                    correlatedRadiusForm.setName(correlatedRadData.getName());
                    correlatedRadiusForm.setDescription(correlatedRadData.getDescription());
                    correlatedRadiusForm.setAuthEsiName(authEsiId);
                    correlatedRadiusForm.setAcctEsiName(acctEsiId);
                    correlatedRadiusForm.setAuditUId(correlatedRadData.getAuditUId());
                    correlatedRadiusForm.setAuthExternalSystemInterfaceInstanceData(authESIDataList);
                    correlatedRadiusForm.setAcctExternalSystemInterfaceInstanceData(acctESIDataList);

                    request.setAttribute("correlatedRadiusForm", correlatedRadiusForm);
                    request.setAttribute("correlatedEsi", correlatedRadData);

                    return mapping.findForward(UPDATE_CORRELATED_RADIUS_ESI);
                }
            }catch(ActionNotPermitedException e){
                Logger.logError(MODULE,"Error :-" + e.getMessage());
                printPermitedActionAlias(request);
                ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("general.user.restricted"));
                saveErrors(request, messages);
                return mapping.findForward(FAILURE);
            }catch(Exception e){
                Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
                Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
                request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("correlated.radius.failed");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
            }
            return mapping.findForward(FAILURE);
    }
}