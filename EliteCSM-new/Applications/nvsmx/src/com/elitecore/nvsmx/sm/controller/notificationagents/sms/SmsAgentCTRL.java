package com.elitecore.nvsmx.sm.controller.notificationagents.sms;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.NotificationAgentType;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.notificationagents.SMSAgentData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;


@ParentPackage(value = "sm")
@Namespace("/sm/notificationagents")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","sms-agent"}),
})
public class SmsAgentCTRL extends RestGenericCTRL<SMSAgentData> {
    @Override
    public ACLModules getModule() {
        return ACLModules.NOTIFICATIONAGENTS;
    }

    @Override
    public SMSAgentData createModel() {
        return new SMSAgentData();
    }


    @Override
    @SkipValidation
    public HttpHeaders index() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"called index() method");
        }
        List<SMSAgentData> agentDataList = CRUDOperationUtil.findAll(SMSAgentData.class);
        setList(agentDataList);
        setActionChainUrl("sm/notificationagents/notificationagents/index");
        getRequest().setAttribute(NVSMXCommonConstants.TYPE,NotificationAgentType.SMS_AGENT.name());
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
    }


    @Override
    public HttpHeaders create() {
        SMSAgentData smsAgentData = (SMSAgentData) getModel();
        try {
            String smsPassword = smsAgentData.getPassword();
            if(Strings.isNullOrBlank(smsPassword) == false) {
                smsAgentData.setPassword(PasswordUtility.getEncryptedPassword(smsPassword));
            }
        } catch (Exception e) {
            getLogger().error(getLogModule(),"Error while encrypt password for "+ getModule().getDisplayLabel() +". Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while encrypt password "+ getModule().getDisplayLabel());
        }
        return super.create();
    }

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[1]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }

    @Override
    protected boolean prepareAndValidateDestroy(SMSAgentData smsAgentData) {
        DetachedCriteria serverInstanceData = DetachedCriteria.forClass(ServerInstanceData.class);
        serverInstanceData.createAlias("smsAgentData", "smsAgent");
        serverInstanceData.add(Restrictions.eq("smsAgent.id", smsAgentData.getId()));
        List<ServerInstanceData> serverInstanceDataList = CRUDOperationUtil.findAllByDetachedCriteria(serverInstanceData);
        if (Collectionz.isNullOrEmpty(serverInstanceDataList) == false) {
            addActionError("SMS Agent  " + smsAgentData.getName() + " is associated with ServerInstances");
            String attachedInstances = Strings.join(",", serverInstanceDataList, ServerInstanceData::getName);
            getLogger().error(getLogModule(), "Error while deleting SMS Agent" + smsAgentData.getName() + ".Reason: SMS Agent  is associated with " + attachedInstances + " Diameter Gateways.");
            return false;
        }
        return true;
    }

    @Override
    public HttpHeaders update() {
        SMSAgentData agentData = (SMSAgentData) getModel();
        SMSAgentData agentDataFromDB = CRUDOperationUtil.get(SMSAgentData.class, agentData.getId());
        if(agentDataFromDB !=null ) {
            agentData.setPassword(agentDataFromDB.getPassword());
        }
        return super.update();
    }

    @Override
    public void validate() {
        super.validate();
        String methodName = getMethodName();
        if (ACLAction.CREATE.getVal().equalsIgnoreCase(methodName)) {
            SMSAgentData agentData = (SMSAgentData) getModel();
            if (Strings.isNullOrBlank(agentData.getPassword())) {
                addFieldError("password", getText("error.required.field", new String[]{getText("sms.agent.password")}));
                return;
            }
            int passwordLength = agentData.getPassword().length();
            if ((passwordLength >= NVSMXCommonConstants.NOTIFICATION_AGENT_PASSWORD_MIN_LENGTH || passwordLength <= NVSMXCommonConstants.NOTIFICATION_AGENT_PASSWORD_MAX_LENGTH) == false) {
                addFieldError("password", getText("email.agent.password.length.error"));
            }
        }
    }
}
