package com.elitecore.nvsmx.policydesigner.controller.notification;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.google.gson.JsonArray;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.List;

public class QuotaNotificationCTRL extends EliteGenericCTRL<QuotaNotificationData> implements ServletRequestAware,ModelDriven<QuotaNotificationData> {
    private static final long serialVersionUID = 1L;
    private static final String MODULE  = UsageNotificationCTRL.class.getSimpleName();
    private QuotaNotificationData quotaNotificationData = new QuotaNotificationData();
    private String actionChainUrl;
    private String pkgId;
    Object [] messageParameter = {Discriminators.QUOTA_NOTIFICTION};

    private Criterion getCriteriaForDuplicates() {
        return Restrictions.and(Restrictions.eq("dataServiceTypeData.id", quotaNotificationData.getDataServiceTypeData().getId()),
               Restrictions.eq("aggregationKey",quotaNotificationData.getAggregationKey()),
               Restrictions.eq("quotaProfile.id",quotaNotificationData.getQuotaProfile().getId()),
               Restrictions.eq("fupLevel",quotaNotificationData.getFupLevel()),
               Restrictions.eq("threshold",quotaNotificationData.getThreshold()));
    }

    private Boolean isUniqueNotification() {
        List<QuotaNotificationData> dupQuotaNotification = CRUDOperationUtil.findAll(QuotaNotificationData.class, getCriteriaForDuplicates());
        if(dupQuotaNotification != null && !dupQuotaNotification.isEmpty()) {
            LogManager.getLogger().error(MODULE, "Notification already configured");
            addActionError(Discriminators.QUOTA_NOTIFICTION + " " +getText(ActionMessageKeys.CREATE_FAILURE.key));
            addActionError(getText("Notification already configured for threshold: "+dupQuotaNotification.get(0).getThreshold()));
            return false;
        }
        return true;
    }

    @InputConfig(resultName = NVSMXCommonConstants.ACTION_PKG_VIEW)
    public String create(){
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE,"Method called create()");
        }
        pkgId = request.getParameter(Attributes.PKG_ID);
        setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
        setParentIdKey(Attributes.PKG_ID);
        setParentIdValue(pkgId);
        if(!isUniqueNotification()) {
            return Results.REDIRECT_TO_PARENT.getValue();
        }
        try{
            PkgData pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE,"Set the PkgData id for QuotaNotification");
            }
            quotaNotificationData.setQuotaProfile(getQuotaProfileForQuotaNotification());
            quotaNotificationData.setDataServiceTypeData(getSetviceTypeForQuotaNotificaion());
            if(Strings.isNullOrEmpty(quotaNotificationData.getEmailTemplateData().getId()) == false){
                quotaNotificationData.setEmailTemplateData(getEmailTemplateData());
            }
            if(Strings.isNullOrEmpty(quotaNotificationData.getSmsTemplateData().getId()) == false){
                quotaNotificationData.setSmsTemplateData(getSMSTemplateData());
            }
            quotaNotificationData.setPkgData(pkgData);

            quotaNotificationData.setCreatedDateAndStaff(getStaffData());

            CRUDOperationUtil.save(quotaNotificationData);
            String message = Discriminators.USAGE_NOTIFICTION + " Created";
            CRUDOperationUtil.audit(quotaNotificationData, quotaNotificationData.getPkgData().getName(), AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), new String(), message);
            MessageFormat messageFormat = new MessageFormat(getText("create.success"));
            addActionMessage(messageFormat.format(messageParameter));
            return Results.REDIRECT_TO_PARENT.getValue();
        }catch(Exception e){
            LogManager.getLogger().error(MODULE, "Error while creating QuotaNotification: "+e.getMessage());
            LogManager.getLogger().trace(MODULE,e);
            addActionError(Discriminators.USAGE_NOTIFICTION + " " +getText(ActionMessageKeys.CREATE_FAILURE.key));
            addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            return Results.REDIRECT_ERROR.getValue();
        }
    }

    public String delete(){
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE,"Method called delete()");
        }
        setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
        setParentIdKey(Attributes.PKG_ID);
        setParentIdValue(request.getParameter(Attributes.PKG_ID));
        try{
            String quotaNotificationId = request.getParameter(Attributes.QUOTA_NOTIFICATION_ID);
            if(Strings.isNullOrEmpty(quotaNotificationId) == false){
                quotaNotificationData = CRUDOperationUtil.get(QuotaNotificationData.class,quotaNotificationId);
                quotaNotificationData.setStatus(CommonConstants.STATUS_DELETED);
            }
            String message = Discriminators.USAGE_NOTIFICTION + " Deleted";
            CRUDOperationUtil.audit(quotaNotificationData, quotaNotificationData.getPkgData().getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(), "", message);
            MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
            addActionMessage(messageFormat.format(messageParameter));
            return Results.REDIRECT_TO_PARENT.getValue();
        }catch(Exception he){
            LogManager.getLogger().error(MODULE, "Error while deleting QuotaNotification: "+he.getMessage());
            LogManager.getLogger().trace(he);
            addActionError(Discriminators.USAGE_NOTIFICTION + " " +getText(ActionMessageKeys.DELETE_FAILURE.key));
            addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            return Results.REDIRECT_ERROR.getValue();
        }
    }

    @InputConfig(resultName = NVSMXCommonConstants.ACTION_PKG_VIEW)
    public String update(){
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE,"Method called update()");
        }
        pkgId = request.getParameter(Attributes.PKG_ID);
        setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
        setParentIdKey(Attributes.PKG_ID);
        setParentIdValue(pkgId);
        if(!isUniqueNotification()) {
            return Results.REDIRECT_TO_PARENT.getValue();
        }

        try{
            QuotaNotificationData quotaNotificationDataToBeUpdate = CRUDOperationUtil.get(QuotaNotificationData.class, quotaNotificationData.getId());
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE,"Set the PkgData id for QuotaNotification");
            }
            quotaNotificationData.setQuotaProfile(getQuotaProfileForQuotaNotification());
            quotaNotificationData.setDataServiceTypeData(getSetviceTypeForQuotaNotificaion());
            if (Strings.isNullOrBlank(quotaNotificationData.getEmailTemplateData().getId()) == false){
                quotaNotificationData.setEmailTemplateData(getEmailTemplateData());
            }else{
                quotaNotificationData.setEmailTemplateData(null);
            }
            if(Strings.isNullOrBlank(quotaNotificationData.getSmsTemplateData().getId()) == false){
                quotaNotificationData.setSmsTemplateData(getSMSTemplateData());
            }else{
                quotaNotificationData.setSmsTemplateData(null);
            }
            JsonArray difference = ObjectDiffer.diff(quotaNotificationDataToBeUpdate, quotaNotificationData);
            quotaNotificationData.setPkgData(quotaNotificationDataToBeUpdate.getPkgData());
            CRUDOperationUtil.merge(quotaNotificationData);

            String message = Discriminators.USAGE_NOTIFICTION + " Updated";
            CRUDOperationUtil.audit(quotaNotificationDataToBeUpdate,quotaNotificationDataToBeUpdate.getPkgData().getName(),AuditActions.UPDATE,getStaffData(), request.getRemoteAddr(), difference ,new String(),message);
            MessageFormat messageFormat = new MessageFormat(getText("update.success"));
            addActionMessage(messageFormat.format(messageParameter));
            return Results.REDIRECT_TO_PARENT.getValue();

        }catch(Exception he){
            LogManager.getLogger().error(MODULE, "Error while updating QuotaNotification: "+he.getMessage());
            LogManager.getLogger().trace(MODULE,he);
            addActionError(Discriminators.USAGE_NOTIFICTION + " " +getText(ActionMessageKeys.UPDATE_FAILURE.key));
            addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            return Results.REDIRECT_ERROR.getValue();
        }
    }

    private NotificationTemplateData getSMSTemplateData() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE,"Method called setSMSTemplateData()");
        }
        NotificationTemplateData smsTemplateData = new NotificationTemplateData();
        if (Strings.isNullOrBlank(quotaNotificationData.getSmsTemplateData().getId()) == false) {
            smsTemplateData = CRUDOperationUtil.get(NotificationTemplateData.class, quotaNotificationData.getSmsTemplateData().getId());
        }
        return smsTemplateData;
    }

    private NotificationTemplateData getEmailTemplateData() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE,"Method called setEmailTemplateData()");
        }
        NotificationTemplateData emailTemplateData = new NotificationTemplateData();
        if (Strings.isNullOrBlank(quotaNotificationData.getEmailTemplateData().getId()) == false) {
            emailTemplateData = CRUDOperationUtil.get(NotificationTemplateData.class, quotaNotificationData.getEmailTemplateData().getId());
        }
        return emailTemplateData;
    }

    private DataServiceTypeData getSetviceTypeForQuotaNotificaion() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE,"Method called setSetviceType()");
        }
        DataServiceTypeData dataServiceTypeData = new DataServiceTypeData();
        if(Strings.isNullOrBlank(quotaNotificationData.getDataServiceTypeData().getId()) == false){
            dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class, quotaNotificationData.getDataServiceTypeData().getId());
        }
        return dataServiceTypeData;
    }

    private RncProfileData getQuotaProfileForQuotaNotification() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE,"Method called setQuotaProfile()");
        }
        RncProfileData quotaProfileToBeSet = new RncProfileData();
        if(Strings.isNullOrEmpty(quotaNotificationData.getQuotaProfile().getId()) == false){
            quotaProfileToBeSet = CRUDOperationUtil.get(RncProfileData.class, quotaNotificationData.getQuotaProfile().getId());
        }
        return quotaProfileToBeSet;
    }




    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getIncludeProperties() {
        return null;
    }

    @Override
    public QuotaNotificationData getModel() {
        return quotaNotificationData;
    }

    public QuotaNotificationData getQuotaNotificationData() {
        return quotaNotificationData;
    }
    public void setQuotaNotificationData(QuotaNotificationData quotaNotificationData) {
        this.quotaNotificationData = quotaNotificationData;
    }
    public String getActionChainUrl() {
        return actionChainUrl;
    }
    @ActionChain(name = "actionChainUrlMethod")
    public void setActionChainUrl(String actionChainUrl) {
        this.actionChainUrl = actionChainUrl;
    }
}
