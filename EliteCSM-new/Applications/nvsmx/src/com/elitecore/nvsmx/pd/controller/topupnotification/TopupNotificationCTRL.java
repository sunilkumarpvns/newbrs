package com.elitecore.nvsmx.pd.controller.topupnotification;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pd.notification.TopUpNotificationData;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.function.Predicate;

import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_ITALIC_TEXT_TAG;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(NVSMXCommonConstants.REST_PARENT_PKG_PD)
@Namespace("/pd/topupnotification")
@Results({
        @Result(name = SUCCESS,
                type = RestGenericCTRL.REDIRECT_ACTION,
                params = {NVSMXCommonConstants.ACTION_NAME, "topup-notification"})
})
public class TopupNotificationCTRL extends RestGenericCTRL<TopUpNotificationData> {

    private static final String DATA_TOP_UP_ID = "dataTopUpId";

    @Override
    public ACLModules getModule() {
        return ACLModules.TOPUPNOTIFICATION;
    }

    @Override
    public TopUpNotificationData createModel() {
        return new TopUpNotificationData();
    }

    @Override
    public HttpHeaders create() {
        TopUpNotificationData topUpNotificationData =(TopUpNotificationData) getModel();
        super.create();
        if(hasActionErrors()){
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
        }
        setActionChainUrl(CommonConstants.FORWARD_SLASH+getRedirectURL(topUpNotificationData.getDataTopUpId()));
        return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(HttpServletResponse.SC_CREATED).disableCaching();
    }


    @Override
    public HttpHeaders destroy() {// delete
        super.destroy();
        setActionChainUrl(CommonConstants.FORWARD_SLASH+getRedirectURL(getRequest().getParameter(DATA_TOP_UP_ID)));
        return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
    }



    @Override
    protected boolean prepareAndValidateDestroy(TopUpNotificationData topUpNotificationData) {
        DataTopUpData dataTopUpData = topUpNotificationData.getDataTopUpData();
        if(PkgMode.LIVE2.name().equalsIgnoreCase(dataTopUpData.getPackageMode())){
            addActionError(getText("datatopup.live2.stage"));
            return false;
        }
        return true;
    }




    @Override
    public HttpHeaders update() {
        TopUpNotificationData topUpNotificationData =(TopUpNotificationData) getModel();
        super.update();
        if(hasActionErrors()){
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
        }
        setActionChainUrl(CommonConstants.FORWARD_SLASH+getRedirectURL(topUpNotificationData.getDataTopUpId()));
        return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
    }

    @Override
    public void validate() {

        TopUpNotificationData topupNotificationData = (TopUpNotificationData) getModel();
        if(Strings.isNullOrBlank(topupNotificationData.getEmailTemplateId()) && Strings.isNullOrBlank(topupNotificationData.getSmsTemplateId())){
            addActionError(getText("topup.notification.email.sms.mandatory"));
            return;
        }
        if(Strings.isNullOrBlank(topupNotificationData.getEmailTemplateId()) == false){
            NotificationTemplateData templateData = CRUDOperationUtil.getNotDeleted(NotificationTemplateData.class,topupNotificationData.getEmailTemplateId());
                if(templateData == null){
                  addFieldError("emailTemplateId",getText("topup.notification.email.invalid"));
                  return ;
                }
                if(NotificationTemplateType.EMAIL.equals(templateData.getTemplateType()) == false){
                    addFieldError("emailTemplateId",getText("topup.notification.email.invalid"));
                    return ;
                }
                topupNotificationData.setEmailTemplateData(templateData);
            }

        if(Strings.isNullOrBlank(topupNotificationData.getSmsTemplateId()) == false){
            NotificationTemplateData templateData = CRUDOperationUtil.getNotDeleted(NotificationTemplateData.class,topupNotificationData.getSmsTemplateId());
                if(templateData == null){
                    addFieldError("smsTemplateId",getText("topup.notification.sms.invalid"));
                    return ;
                }
                if(NotificationTemplateType.SMS.equals(templateData.getTemplateType()) == false){
                    addFieldError("smsTemplateId",getText("topup.notification.sms.invalid"));
                    return ;
                }
                topupNotificationData.setSmsTemplateData(templateData);
            }
        if(Strings.isNullOrBlank(topupNotificationData.getDataTopUpId()) == false) {
            DataTopUpData dataTopUpData = CRUDOperationUtil.get(DataTopUpData.class, topupNotificationData.getDataTopUpId());
            if (dataTopUpData == null) {
                addFieldError("dataTopUpId", getText("topup.notification.datatopup.invalid"));
                return;
            }

            String methodName = getMethodName();

            if(ACLAction.CREATE.getVal().equalsIgnoreCase(methodName) || ACLAction.UPDATE.getVal().equalsIgnoreCase(methodName)) {
                if (PkgMode.LIVE2.name().equalsIgnoreCase(dataTopUpData.getPackageMode())) {
                    addActionError(getText("datatopup.live2.stage"));
                }
                if (isSameNotificationAlreadyExist(dataTopUpData, topupNotificationData,methodName)) {
                    addActionError(getText("notification.already.exist", new String[]{topupNotificationData.getThreshold().toString()}));
                    return;
                }
            }
            if (ACLAction.DELETE.getVal().equalsIgnoreCase(methodName)) {
                if (PkgMode.LIVE2.name().equalsIgnoreCase(dataTopUpData.getPackageMode())) {
                    addActionError(getText("datatopup.live2.stage"));
                }
            }
            topupNotificationData.setDataTopUpData(dataTopUpData);
        }
    }

    private boolean isSameNotificationAlreadyExist(DataTopUpData dataTopUpData, TopUpNotificationData currentNotification, String methodName) {
        if (Collectionz.isNullOrEmpty(dataTopUpData.getTopUpNotificationList())) {
            return false;
        }
        ExistingNotificationPredicate existingNotificationPredicate = new ExistingNotificationPredicate(currentNotification,methodName);

        Optional<TopUpNotificationData> first = dataTopUpData.getTopUpNotificationList()
                .stream()
                .filter(existingNotificationPredicate)
                .findFirst();
        return first.isPresent();
    }



    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getParentModule().getActionURL()[0]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }

    @Override
    protected String getAuditMessage(TopUpNotificationData model, String action) {
        return getModule().getDisplayLabel() + "  " + START_BOLD_TEXT_TAG + START_ITALIC_TEXT_TAG +"at "+ model.getThreshold() +CommonConstants.PERECNTILE+ " threshold"+CLOSE_BOLD_TEXT_TAG + CLOSE_ITALIC_TEXT_TAG + action;
    }

    private class ExistingNotificationPredicate implements Predicate<TopUpNotificationData>{

        TopUpNotificationData currentNotificationData;
        String aclAction;

        public ExistingNotificationPredicate(TopUpNotificationData currentNotificationData, String aclAction) {
            this.currentNotificationData = currentNotificationData;
            this.aclAction = aclAction;
        }

        @Override
        public boolean test(TopUpNotificationData topUpNotificationData) {
            if(ACLAction.CREATE.getVal() == aclAction){
                return topUpNotificationData.getThreshold() == currentNotificationData.getThreshold();
            }else if(ACLAction.UPDATE.getVal() == aclAction){
                if(topUpNotificationData.getId().equalsIgnoreCase(currentNotificationData.getId()) == false){
                    return topUpNotificationData.getThreshold() == currentNotificationData.getThreshold();
                }
            }
           return false;
        }
    }
}
