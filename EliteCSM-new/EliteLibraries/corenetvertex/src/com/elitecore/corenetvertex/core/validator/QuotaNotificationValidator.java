package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

public class QuotaNotificationValidator implements Validator<QuotaNotificationData,PkgData,PkgData> {
    private static final String MODULE = "QUOTA-NOTIFICATION-VALIDATOR";

    @Override
    public List<String> validate(QuotaNotificationData quotaNotificationImported, PkgData pkgData, PkgData superObject, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {

            String id = quotaNotificationImported.getId();

            QuotaNotificationData existingQuotaNotificationData = null;
            if (Strings.isNullOrBlank(id) == false) {
                existingQuotaNotificationData = ImportExportCRUDOperationUtil.get(QuotaNotificationData.class, id, session);
            }
            if(existingQuotaNotificationData != null && CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingQuotaNotificationData.getStatus()) == false){
                validateQuotaNotificationBasedOnAction(quotaNotificationImported, existingQuotaNotificationData, pkgData, action, subReasons);
            }
            validateMandatoryParameters(quotaNotificationImported,subReasons);
            validateEmailAndSmsNotification(quotaNotificationImported, session,subReasons);

            validateQuotaProfileIdAndNameForQuotaNotification(quotaNotificationImported,pkgData,subReasons);
        }catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate quota notification with Id: " + quotaNotificationImported.getId() + " associated with package: " + pkgData.getName());
            LogManager.getLogger().trace(MODULE,e);
            subReasons.add( "Failed to validate quota notification with Id: " + quotaNotificationImported.getId() + " associated with package: " + pkgData.getName()+". Kindly refer logs for further details");

        }
        return subReasons;
    }

    private void validateMandatoryParameters(QuotaNotificationData quotaNotificationImported, List<String> subReasons) {
        if(quotaNotificationImported.getAggregationKey() == null){
            subReasons.add("Invalid Aggregation Key is configured with Quota Notification " + BasicValidations.printIdAndName(quotaNotificationImported.getId(),null));
        }
        if(quotaNotificationImported.getFupLevel() == null){
            subReasons.add("Invalid FUP Level is configured with Quota Notification " + BasicValidations.printIdAndName(quotaNotificationImported.getId(),null));
        }
        if(quotaNotificationImported.getDataServiceTypeData() == null){
            subReasons.add("Data Service Type must be configured with Quota Notification " + BasicValidations.printIdAndName(quotaNotificationImported.getId(),null));
        }
        if(quotaNotificationImported.getThreshold() == null){
            subReasons.add("Threshold must be configured with Quota Notification " + BasicValidations.printIdAndName(quotaNotificationImported.getId(),null));
        }else{
            validateThresholdValue(quotaNotificationImported.getThreshold(), subReasons);
        }
    }

    private void validateThresholdValue(Integer threshold, List<String> subReasons) {
        boolean isValidThreshold = BasicValidations.isPositiveNumber(threshold.longValue());
        if(isValidThreshold == false){
            subReasons.add("Threshold must be positive numeric value");
            return;
        }
        if(threshold < 1 || threshold > 100){
            subReasons.add("Threshold value must be between 1 to 100");
        }
    }

    private void validateEmailAndSmsNotification(QuotaNotificationData quotaNotificationImported, SessionProvider session, List<String> subReasons) throws Exception {
        NotificationTemplateData emailNotificationTemplateData = quotaNotificationImported.getEmailTemplateData();
        NotificationTemplateData smsNotificationTemplateData = quotaNotificationImported.getSmsTemplateData();
        if(emailNotificationTemplateData != null) {
            String emailNotificationId = emailNotificationTemplateData.getId();
            String emailNotificationName = emailNotificationTemplateData.getName();
            if (Strings.isNullOrBlank(emailNotificationId) && Strings.isNullOrBlank(emailNotificationName)) {
                quotaNotificationImported.setEmailTemplateData(null);
            }
            if (Strings.isNullOrBlank(emailNotificationId) == false) {
                validateWithExistingNotificationTemplateId(emailNotificationTemplateData, NotificationTemplateType.EMAIL, session, subReasons);
            } else if (Strings.isNullOrBlank(emailNotificationName) == false) {
                validateWithExistingNotificationName(emailNotificationTemplateData, NotificationTemplateType.EMAIL, session, subReasons);
            }
        }


        if(smsNotificationTemplateData != null) {
            String smsNotificationId = smsNotificationTemplateData.getId();
            String smsNotificationName = smsNotificationTemplateData.getName();
            if (Strings.isNullOrBlank(smsNotificationId) && Strings.isNullOrBlank(smsNotificationName)) {
                quotaNotificationImported.setSmsTemplateData(null);
            }
            if (Strings.isNullOrBlank(smsNotificationId) == false) {
                validateWithExistingNotificationTemplateId(smsNotificationTemplateData, NotificationTemplateType.SMS, session, subReasons);
            } else if (Strings.isNullOrBlank(smsNotificationName) == false) {
                validateWithExistingNotificationName(smsNotificationTemplateData, NotificationTemplateType.SMS, session, subReasons);
            }
        }
    }

    private void validateWithExistingNotificationTemplateId(NotificationTemplateData emailTemplateNotificationImported, NotificationTemplateType templateType, SessionProvider session, List<String> subReasons) throws Exception {
        String id = emailTemplateNotificationImported.getId();
        NotificationTemplateData existingEmailTemplateData = ImportExportCRUDOperationUtil.get(NotificationTemplateData.class, id, session);
        if (existingEmailTemplateData == null) {
            subReasons.add(emailTemplateNotificationImported.getTemplateType().getDisplayVal() + " Notification template with id: " + id + " does not exists");
        } else if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingEmailTemplateData.getStatus())) {
            subReasons.add(templateType.getDisplayVal() + " Notification template name:" + existingEmailTemplateData.getName() + " does not exists");
        } else if(existingEmailTemplateData.getTemplateType() != templateType){
            subReasons.add("Existing Template type is : " + existingEmailTemplateData.getTemplateType() + " and imported Template type is " + templateType);
        }  else {
            if (Strings.isNullOrBlank(emailTemplateNotificationImported.getName()) == false && emailTemplateNotificationImported.getName().equals(existingEmailTemplateData.getName()) == false) {
                subReasons.add(templateType + " Notification template name: " + emailTemplateNotificationImported.getName() + " and " + templateType + " id " + id + " are not related");
            }

        }
    }


    private void validateQuotaNotificationBasedOnAction(QuotaNotificationData quotaNotificationImported, QuotaNotificationData existingQuotaNotification, PkgData pkgData, String action, List<String> subReasons){
        if(CommonConstants.FAIL.equalsIgnoreCase(action)){
            subReasons.add("Quota Notification with Id: " + quotaNotificationImported.getId() + " already exists with package: " + pkgData.getName());
        }else if(CommonConstants.REPLACE.equalsIgnoreCase(action)){
            if(Strings.isNullOrBlank(pkgData.getId()) == false && existingQuotaNotification.getPkgData().getId().equalsIgnoreCase(pkgData.getId()) == false){
                subReasons.add("Quota Notification (" + quotaNotificationImported.getId() + ") with package("+pkgData.getId() + CommonConstants.COMMA + pkgData.getName()
                        +") already exists in different package(" + existingQuotaNotification.getPkgData().getId() + CommonConstants.COMMA + existingQuotaNotification.getPkgData().getName() +")");
            } else if(Strings.isNullOrBlank(pkgData.getName()) == false && existingQuotaNotification.getPkgData().getName().equalsIgnoreCase(pkgData.getName()) == false){
                subReasons.add("Quota Notification (" + quotaNotificationImported.getId() + ") with package("+pkgData.getId() + CommonConstants.COMMA + pkgData.getName()
                        +") already exists in different package(" + existingQuotaNotification.getPkgData().getId() + CommonConstants.COMMA + existingQuotaNotification.getPkgData().getName() +")");
            }else{
                quotaNotificationImported.setId(existingQuotaNotification.getId());
            }
        }
    }

    private void validateWithExistingNotificationName(NotificationTemplateData notificationTemplateImported, NotificationTemplateType templateType, SessionProvider session, List<String> subReasons) throws Exception {
        List<NotificationTemplateData> existingNotificationTemplate = ImportExportCRUDOperationUtil.getByName(NotificationTemplateData.class, notificationTemplateImported.getName(),session);
        if (Collectionz.isNullOrEmpty(existingNotificationTemplate)) {
            subReasons.add(templateType.getDisplayVal() + " Template with name: " + notificationTemplateImported.getName() + " does not exists");
        }else if(CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingNotificationTemplate.get(0).getStatus())){
            subReasons.add(templateType.getDisplayVal() + " Template with name:" + existingNotificationTemplate.get(0).getName() + " does not exists");
        }else if(existingNotificationTemplate.get(0).getTemplateType() != templateType){
            subReasons.add("Existing Template type is : " + existingNotificationTemplate.get(0).getTemplateType() + " and imported template type is " + templateType);
        } else{
            notificationTemplateImported.setId(existingNotificationTemplate.get(0).getId());
        }

    }


    private void validateQuotaProfileIdAndNameForQuotaNotification(QuotaNotificationData quotaNotificationImported, PkgData pkgData, List<String> subReasons) throws Exception {
        String quotaProfileId = quotaNotificationImported.getQuotaProfileId();
        String quotaProfileName = quotaNotificationImported.getQuotaProfileName();
        if (Strings.isNullOrBlank(quotaProfileId) && Strings.isNullOrBlank(quotaProfileName)) {
            subReasons.add("Quota Profile Id/Name is mandatory for Quota Notification" + BasicValidations.printIdAndName(quotaNotificationImported.getId(), null));
            return;
        }
        boolean isRnCProfileExists = false;

        List<RncProfileData> rncProfilesImported = pkgData.getRncProfileDatas();
        if (Strings.isNullOrBlank(quotaProfileId) == false) {
            for (RncProfileData quotaProfile : rncProfilesImported) {
                if (Strings.isNullOrBlank(quotaProfile.getId()) == false && quotaProfileId.equals(quotaProfile.getId())) {
                    isRnCProfileExists = true;
                }
            }
            if (isRnCProfileExists == false) {
                subReasons.add("RnC Profile" + BasicValidations.printIdAndName(quotaProfileId, quotaProfileName) + " associated with Quota Notification " + BasicValidations.printIdAndName(quotaNotificationImported.getId(), null) + " does not exists");
            }
        } else if (Strings.isNullOrBlank(quotaProfileName) == false) {
            for (RncProfileData quotaProfile : rncProfilesImported) {
                if (Strings.isNullOrBlank(quotaProfile.getName()) == false && quotaProfileName.equalsIgnoreCase(quotaProfile.getName())) {
                    isRnCProfileExists = true;
                }
            }
            if (isRnCProfileExists == false) {
                subReasons.add("RnC Profile" + BasicValidations.printIdAndName(quotaProfileId, quotaProfileName) + " associated with Quota Notification " + BasicValidations.printIdAndName(quotaNotificationImported.getId(), null) + " does not exists");
            }
        }

    }


}
