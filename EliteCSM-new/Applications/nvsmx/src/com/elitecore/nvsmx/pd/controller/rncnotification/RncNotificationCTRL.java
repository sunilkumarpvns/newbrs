package com.elitecore.nvsmx.pd.controller.rncnotification;

import java.util.List;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.rncpackage.notification.RncNotificationData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;


import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_ITALIC_TEXT_TAG;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "pd")
@Namespace("/pd/rncnotification")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "rnc-notification" }),

})
public class RncNotificationCTRL extends RestGenericCTRL<RncNotificationData> {

    private String rncPackageId;

    @Override
    public HttpHeaders create() {
		HttpHeaders httpHeaders = super.create();
		setActionChainUrl();
		RncNotificationData model = (RncNotificationData)getModel();
		return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(httpHeaders.getStatus()).disableCaching().setLocationId(model.getId());
    }

    @Override
    public HttpHeaders update() {
		HttpHeaders httpHeaders = super.update();
		setActionChainUrl();
		return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(httpHeaders.getStatus()).disableCaching();
    }

    @Override
    public HttpHeaders destroy() {// delete
		HttpHeaders httpHeaders = super.destroy();
		setActionChainUrl();
		return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(httpHeaders.getStatus()).disableCaching();
    }

	private void setActionChainUrl() {
		setActionChainUrl(CommonConstants.FORWARD_SLASH + NVSMXCommonConstants.RNC_PACKAGE_URL
                + (getRequest().getParameter(NVSMXCommonConstants.RNC_PACKAGE_ID)));
	}

	@Override
    public void validate() {

		super.validateIdExistForCreateMode();
		RncNotificationData rncNotificationData = (RncNotificationData) getModel();

        RncPackageData rncPackageData = null;
        String rncPackageId = getRncPackageId();
        if (Strings.isNullOrEmpty(rncPackageId)) {
            rncPackageId = rncNotificationData.getRncPackageId();
        }
        if (Strings.isNullOrEmpty(rncPackageId)) {
            addFieldError("rncPackageId", getText("error.valueRequired"));
            return;
        } else {
            rncPackageData = CRUDOperationUtil.get(RncPackageData.class, rncPackageId);
            if (rncPackageData == null) {
                addFieldError("rncPackageId", getText("Rnc Package does not exist"));
                return;
            } else if (PkgMode.LIVE2.name().equalsIgnoreCase(rncPackageData.getMode())) {
                addActionError("Live or Live2 Package does not allow create / update operation on Threshold Notification");
                return;
            }  else if (RnCPkgType.MONETARY_ADDON.name().equals(rncPackageData.getType())) {
                addActionError("RnC package of type "+ RnCPkgType.MONETARY_ADDON.getVal()+" does not support notification");
                return;
            } else {
                rncNotificationData.setRncPackageData(rncPackageData);
            }
        }

		validateRateCard(rncNotificationData, rncPackageId);

		validateTemplate(rncNotificationData, rncPackageData);

		validateUniqueness(rncNotificationData, rncPackageData);
    }

	private void validateUniqueness(RncNotificationData newNotificationData, RncPackageData rncPackageData) {

		List<RncNotificationData> rncNotifications = rncPackageData.getRncNotifications();
		if (rncNotifications.isEmpty()) {
			return;
		}

		for (RncNotificationData data : rncNotifications) {
			if (data.getId().equals(newNotificationData.getId()) == false
					&& data.getThreshold().equals(newNotificationData.getThreshold())
					&& data.getRateCardId().equals(newNotificationData.getRateCardId())) {
				addFieldError("threshold", "Notification already configured for threshold: " + data.getThreshold());
				return;
			}
		}
	}

	private void validateTemplate(RncNotificationData rncNotificationData, RncPackageData rncPackageData) {
		String emailTemplateId = rncNotificationData.getEmailTemplateId();
		String smsTemplateId = rncNotificationData.getSmsTemplateId();
		if(StringUtils.isBlank(emailTemplateId) && StringUtils.isBlank(smsTemplateId)){
			addActionError(getText("error.template.required"));
		}
		if (StringUtils.isBlank(emailTemplateId) == false) {
			NotificationTemplateData emailTemplate = CRUDOperationUtil.get(NotificationTemplateData.class, emailTemplateId);
			if (emailTemplate == null || NotificationTemplateType.EMAIL != emailTemplate.getTemplateType()) {
				addFieldError("emailTemplateId", getText("error.emailtemplate.not.exist"));
			} else if (checkNotificationTemplateInformationBasedOnRncPackage(emailTemplate,rncPackageData) == false) {
				addFieldError("emailTemplateId", getText("error.emailtemplate.not.exist"));
			} else
				rncNotificationData.setEmailTemplateData(emailTemplate);
		}

		if (StringUtils.isBlank(smsTemplateId) == false) {
			NotificationTemplateData smsTemplate = CRUDOperationUtil.get(NotificationTemplateData.class, smsTemplateId);
			if (smsTemplate == null || NotificationTemplateType.SMS != smsTemplate.getTemplateType()) {
				addFieldError("smsTemplateId", getText("error.smstemplate.not.exist"));
			} else if (checkNotificationTemplateInformationBasedOnRncPackage(smsTemplate,rncPackageData) == false) {
				addFieldError("smsTemplateId", getText("error.smstemplate.not.exist"));
			}else {
				rncNotificationData.setSmsTemplateData(smsTemplate);
			}
		}
	}

	private void validateRateCard(RncNotificationData rncNotificationData, String rncPackageId) {
		String rateCardId = rncNotificationData.getRateCardId();
		if (Strings.isNullOrEmpty(rateCardId)) {
			addFieldError("rateCardId", getText("error.valueRequired"));
		} else {
			RateCardData nonMonetaryRateCard = CRUDOperationUtil.get(RateCardData.class, rateCardId);
			if (nonMonetaryRateCard == null || RateCardType.NON_MONETARY.name().equalsIgnoreCase(nonMonetaryRateCard.getType()) == false) {
				addFieldError("rateCardId", getText("error.ratecard.not.exist"));
			} else if (nonMonetaryRateCard.getRncPackageData().getId().equals(rncPackageId) == false) {
				addFieldError("rateCardId", getText("error.ratecard.not.belong.RnCPackage"));
			} else {
				rncNotificationData.setRateCardData(nonMonetaryRateCard);
			}
		}
	}

	private boolean checkNotificationTemplateInformationBasedOnRncPackage(NotificationTemplateData notificationTemplateData,RncPackageData rncPackageData) {
        List<String> groupDatas = CommonConstants.COMMA_SPLITTER.split(rncPackageData.getGroups());
        List<String> groups = CommonConstants.COMMA_SPLITTER.split(notificationTemplateData.getGroups());
        if(Strings.isNullOrBlank(notificationTemplateData.getGroups())){
            return true;
        }
        for(String groupId:groupDatas){
            if(groups.contains(groupId)){
                return true;
            }
        }
        return false;
    }

    @Override
	protected String getAuditMessage(RncNotificationData model, String action) {
		return getModule().getDisplayLabel() + " " + START_BOLD_TEXT_TAG + START_ITALIC_TEXT_TAG + "at "
				+ model.getThreshold() + CommonConstants.PERECNTILE + " threshold for rate card " + model.getRateCardData().getName()
				+ CLOSE_BOLD_TEXT_TAG + CLOSE_ITALIC_TEXT_TAG + action;
	}

    @Override
    public ACLModules getModule() {
        return ACLModules.RNCNOTIFICATION;
    }

    @Override
    public RncNotificationData createModel() {
        return new RncNotificationData();
    }

    public String getRncPackageId() {
        return rncPackageId;
    }

    public void setRncPackageId(String rncPackageId) {
        this.rncPackageId = rncPackageId;
    }
}
