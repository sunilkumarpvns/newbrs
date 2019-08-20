package com.elitecore.nvsmx.policydesigner.controller.notification;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.UsageNotificationData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.google.gson.JsonArray;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

/**
 * 
 * Provided create/update/delete functionality for UsageNotification
 * @author Dhyani.Raval
 *
 */
public class UsageNotificationCTRL extends EliteGenericCTRL<UsageNotificationData> implements ServletRequestAware,ModelDriven<UsageNotificationData> {

	private static final long serialVersionUID = 1L;
	private static final String MODULE  = UsageNotificationCTRL.class.getSimpleName();
	private UsageNotificationData usageNotificationData = new UsageNotificationData();
	private String actionChainUrl;
	private String pkgId;
	Object [] messageParameter = {Discriminators.USAGE_NOTIFICTION};

	@InputConfig(resultName = NVSMXCommonConstants.ACTION_PKG_VIEW)
	public String create(){
		if (LogManager.getLogger().isDebugLogLevel()) { 
			LogManager.getLogger().debug(MODULE,"Method called create()");	
		}
		pkgId = request.getParameter(Attributes.PKG_ID);
		setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
		setParentIdKey(Attributes.PKG_ID);
		setParentIdValue(pkgId);
		try{
			PkgData pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);
			if (LogManager.getLogger().isDebugLogLevel()) { 
				LogManager.getLogger().debug(MODULE,"Set the PkgData id for UsageNotification");	
			}
			usageNotificationData.setQuotaProfile(getQuotaProfileForUsageNotification());
			usageNotificationData.setDataServiceTypeData(getSetviceTypeForUsageNotificaion());
			if(Strings.isNullOrEmpty(usageNotificationData.getEmailTemplateData().getId()) == false){
				usageNotificationData.setEmailTemplateData(getEmailTemplateData());
			}
			if(Strings.isNullOrEmpty(usageNotificationData.getSmsTemplateData().getId()) == false){
				usageNotificationData.setSmsTemplateData(getSMSTemplateData());
			}
			usageNotificationData.setPkgData(pkgData);
			
			usageNotificationData.setCreatedDateAndStaff(getStaffData());
			
			CRUDOperationUtil.save(usageNotificationData);
			String message = Discriminators.USAGE_NOTIFICTION + " Created";
			CRUDOperationUtil.audit(usageNotificationData,usageNotificationData.getPkgData().getName(),AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), new String(), message);
			MessageFormat messageFormat = new MessageFormat(getText("create.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.REDIRECT_TO_PARENT.getValue();
 		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error while creating UsageNotification: "+e.getMessage());
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
			String usageNotificationId = request.getParameter(Attributes.USAGE_NOTIFICATION_ID);
			if(Strings.isNullOrEmpty(usageNotificationId) == false){
				usageNotificationData = CRUDOperationUtil.get(UsageNotificationData.class,usageNotificationId);
				usageNotificationData.setStatus(CommonConstants.STATUS_DELETED);
			}
			String message = Discriminators.USAGE_NOTIFICTION + " Deleted";
			CRUDOperationUtil.audit(usageNotificationData,usageNotificationData.getPkgData().getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(), "", message);
			MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.REDIRECT_TO_PARENT.getValue();
 		}catch(Exception he){
			LogManager.getLogger().error(MODULE, "Error while deleting UsageNotification: "+he.getMessage());
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
		try{
			UsageNotificationData usageNotificationDataToBeUpdate = CRUDOperationUtil.get(UsageNotificationData.class,usageNotificationData.getId());
				if (LogManager.getLogger().isDebugLogLevel()) { 
					LogManager.getLogger().debug(MODULE,"Set the PkgData id for UsageNotification");	
				}
				usageNotificationData.setQuotaProfile(getQuotaProfileForUsageNotification());
				usageNotificationData.setDataServiceTypeData(getSetviceTypeForUsageNotificaion());
				if (Strings.isNullOrBlank(usageNotificationData.getEmailTemplateData().getId()) == false){
					usageNotificationData.setEmailTemplateData(getEmailTemplateData());
				}else{
					usageNotificationData.setEmailTemplateData(null);
				}
				if(Strings.isNullOrBlank(usageNotificationData.getSmsTemplateData().getId()) == false){
					usageNotificationData.setSmsTemplateData(getSMSTemplateData());
				}else{
					usageNotificationData.setSmsTemplateData(null);
				}
				JsonArray difference = ObjectDiffer.diff(usageNotificationDataToBeUpdate, usageNotificationData);
				usageNotificationData.setPkgData(usageNotificationDataToBeUpdate.getPkgData());
				CRUDOperationUtil.merge(usageNotificationData);
				
				String message = Discriminators.USAGE_NOTIFICTION + " Updated";
				CRUDOperationUtil.audit(usageNotificationDataToBeUpdate,usageNotificationDataToBeUpdate.getPkgData().getName(),AuditActions.UPDATE,getStaffData(), request.getRemoteAddr(), difference ,new String(),message);
			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.REDIRECT_TO_PARENT.getValue();
			
 		}catch(Exception he){
			LogManager.getLogger().error(MODULE, "Error while updating UsageNotification: "+he.getMessage());
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
		if (Strings.isNullOrBlank(usageNotificationData.getSmsTemplateData().getId()) == false) {
			smsTemplateData = CRUDOperationUtil.get(NotificationTemplateData.class, usageNotificationData.getSmsTemplateData().getId());
		}
		return smsTemplateData;
	}

	private NotificationTemplateData getEmailTemplateData() {
		if (LogManager.getLogger().isDebugLogLevel()) { 
			LogManager.getLogger().debug(MODULE,"Method called setEmailTemplateData()");	
		}
		NotificationTemplateData emailTemplateData = new NotificationTemplateData();
		if (Strings.isNullOrBlank(usageNotificationData.getEmailTemplateData().getId()) == false) {
			emailTemplateData = CRUDOperationUtil.get(NotificationTemplateData.class, usageNotificationData.getEmailTemplateData().getId()); 
		}
		return emailTemplateData;
	}

	private DataServiceTypeData getSetviceTypeForUsageNotificaion() {
		if (LogManager.getLogger().isDebugLogLevel()) { 
			LogManager.getLogger().debug(MODULE,"Method called setSetviceType()");	
		}
		DataServiceTypeData dataServiceTypeData = new DataServiceTypeData();
		if(Strings.isNullOrBlank(usageNotificationData.getDataServiceTypeData().getId()) == false){
			dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class,usageNotificationData.getDataServiceTypeData().getId());
		}
		return dataServiceTypeData;
	}

	private QuotaProfileData getQuotaProfileForUsageNotification() {
		if (LogManager.getLogger().isDebugLogLevel()) { 
			LogManager.getLogger().debug(MODULE,"Method called setQuotaProfile()");	
		}
		QuotaProfileData quotaProfileToBeSet = new QuotaProfileData();
		if(Strings.isNullOrEmpty(usageNotificationData.getQuotaProfile().getId()) == false){
			quotaProfileToBeSet = CRUDOperationUtil.get(QuotaProfileData.class, usageNotificationData.getQuotaProfile().getId());
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
	public UsageNotificationData getModel() {
		return usageNotificationData;
	}

	public UsageNotificationData getUsageNotificationData() {
		return usageNotificationData;
	}
	public void setUsageNotificationData(UsageNotificationData usageNotificationData) {
		this.usageNotificationData = usageNotificationData;
	}
	public String getActionChainUrl() {
		return actionChainUrl;
	}
	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

}
