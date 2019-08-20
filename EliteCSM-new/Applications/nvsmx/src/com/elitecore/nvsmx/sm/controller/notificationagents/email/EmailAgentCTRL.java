package com.elitecore.nvsmx.sm.controller.notificationagents.email;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.NotificationAgentType;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.notificationagents.EmailAgentData;
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
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","email-agent"}),

})
public class EmailAgentCTRL extends RestGenericCTRL<EmailAgentData> {

    @Override
    public ACLModules getModule() {
        return ACLModules.NOTIFICATIONAGENTS;
    }

    @Override
    public EmailAgentData createModel() {
        return new EmailAgentData();
    }


    @Override
    @SkipValidation
    public HttpHeaders index() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"called index() method");
        }
        List<EmailAgentData> agentDataList = CRUDOperationUtil.findAll(EmailAgentData.class);

        setList(agentDataList);
        setActionChainUrl("sm/notificationagents/notificationagents/index");
        getRequest().setAttribute(NVSMXCommonConstants.TYPE, NotificationAgentType.EMAIL_AGENT.name());
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
    }

    @Override
    public HttpHeaders create() {
        EmailAgentData emailAgentData = (EmailAgentData) getModel();
        try {
            String emailPassword = emailAgentData.getPassword();
            if(Strings.isNullOrBlank(emailPassword) == false) {
                emailAgentData.setPassword(PasswordUtility.getEncryptedPassword(emailPassword));
            }
        } catch (Exception e) {
            getLogger().error(getLogModule(),"Error while encrypting     password for "+ getModule().getDisplayLabel() +". Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while encrypt password "+ getModule().getDisplayLabel());
        }
        return super.create();
    }

    @Override
    public HttpHeaders update() {
        EmailAgentData emailAgentData = (EmailAgentData) getModel();
        EmailAgentData emailAgentDataFromDB = CRUDOperationUtil.get(EmailAgentData.class, emailAgentData.getId());
        if(emailAgentDataFromDB !=null ){
            emailAgentData.setPassword(emailAgentDataFromDB.getPassword());
        }
        return super.update();
    }

    @Override
    protected boolean prepareAndValidateDestroy(EmailAgentData emailAgentData) {
        DetachedCriteria serverInstanceData = DetachedCriteria.forClass(ServerInstanceData.class);
        serverInstanceData.createAlias("emailAgentData", "emailAgent");
        serverInstanceData.add(Restrictions.eq("emailAgent.id", emailAgentData.getId()));
        List<ServerInstanceData> serverInstanceDataList = CRUDOperationUtil.findAllByDetachedCriteria(serverInstanceData);
        if (Collectionz.isNullOrEmpty(serverInstanceDataList) == false) {
            addActionError("Email Agent  " + emailAgentData.getName() + " is associated with ServerInstances");
            String attachedInstances = Strings.join(",", serverInstanceDataList, ServerInstanceData::getName);
            getLogger().error(getLogModule(), "Error while deleting Email Agent" + emailAgentData.getName() + ".Reason: email Agent  is associated with " + attachedInstances + " Diameter Gateways.");
            return false;
        }
        return true;
    }

    @Override
    public void validate() {
        super.validate();
        String methodName = getMethodName();
        if (ACLAction.CREATE.getVal().equalsIgnoreCase(methodName)) {
            EmailAgentData emailAgentData = (EmailAgentData) getModel();
            if (Strings.isNullOrBlank(emailAgentData.getPassword())) {
                addFieldError("password", getText("error.required.field", new String[]{getText("email.agent.password")}));
                return;
            }
            int passwordLength = emailAgentData.getPassword().length();
            if ((passwordLength >= NVSMXCommonConstants.NOTIFICATION_AGENT_PASSWORD_MIN_LENGTH || passwordLength <= NVSMXCommonConstants.NOTIFICATION_AGENT_PASSWORD_MAX_LENGTH) == false) {
                addFieldError("password", getText("email.agent.password.length.error"));
            }
        }
    }

}
