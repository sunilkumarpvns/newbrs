package com.elitecore.nvsmx.sm.controller.notificationagents.sms;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.notificationagents.SMSAgentData;
import com.elitecore.corenetvertex.sm.password.PasswordData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Used to change password for SMS Agent
 * @author Aditya.Shrivastava
 */

@ParentPackage(value = "sm")
@Namespace("/sm/notificationagents")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","sms-agent-password"}),

})
@InterceptorRef(value = "restStack")
public class SmsAgentPasswordCTRL extends RestGenericCTRL<PasswordData> {

    public ACLModules getModule() {
        return ACLModules.NOTIFICATIONAGENTS;
    }

    @Override
    public PasswordData createModel() {
        return new PasswordData();
    }

    @Override
    public String edit() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called edit");
        }

        setActionChainUrl("sm/notificationagents/sms-agent-password/edit");
        return NVSMXCommonConstants.REDIRECT_URL;
    }

    @Override
    public HttpHeaders update() {

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(),"Method called update()");
        }
        PasswordData passwordData = (PasswordData) getModel();
        SMSAgentData smsAgentData = CRUDOperationUtil.get(SMSAgentData.class,passwordData.getId());

        if(smsAgentData == null) {
            addActionError(getModule().getDisplayLabel()+" not found with id: " + passwordData.getId());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        try {

            String result = authorize();
            if(result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            String encryptedPassword = null;
            if (Strings.isNullOrBlank(passwordData.getOldPassword()) == false) {
                encryptedPassword = PasswordUtility.getEncryptedPassword(passwordData.getOldPassword());
            }

            String existingDbPassword = smsAgentData.getPassword();

            if ((Strings.isNullOrBlank(existingDbPassword) && Strings.isNullOrBlank(encryptedPassword)) || existingDbPassword.equals(encryptedPassword)) {

                smsAgentData.setPassword(PasswordUtility.getEncryptedPassword(passwordData.getNewPassword()));
                smsAgentData.setModifiedDateAndStaff(getStaffData());
                CRUDOperationUtil.merge(smsAgentData);

                String message = getModule().getDisplayLabel() + " <b><i>" + smsAgentData.getResourceName() + "</i></b> " + "Password Updated";
                CRUDOperationUtil.audit(smsAgentData,smsAgentData.getResourceName(), AuditActions.UPDATE,getStaffData(),getRequest().getRemoteAddr(),smsAgentData.getHierarchy(),message);
                addActionMessage(getModule().getDisplayLabel()+" updated successfully");
                setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(smsAgentData.getId()));

                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();

            } else {
                addActionError(getText("password.not.match"));
                throw new Exception(getText("password.not.match"));
            }
        } catch (Exception e) {
            getLogger().error(getLogModule(),"Error while changing " + getModule().getDisplayLabel() + " password. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code);
        }

    }

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[1]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }
}