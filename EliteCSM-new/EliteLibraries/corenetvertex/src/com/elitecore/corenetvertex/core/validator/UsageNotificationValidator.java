package com.elitecore.corenetvertex.core.validator;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType;
import com.elitecore.corenetvertex.pkg.notification.UsageNotificationData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

/**
 * Created by Ishani on 7/7/16.
 */
public class UsageNotificationValidator implements Validator<UsageNotificationData,PkgData,PkgData> {

    private static final String MODULE = "USAGE-NOTIFICATION-VALIDATOR";
    private static final String NAME_REGEX = "^[a-zA-Z0-9]+.*[a-zA-Z0-9)]+$";

    @Override
    public List<String> validate(UsageNotificationData usageNotificationImported, PkgData pkgData, PkgData superObject, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {

            String id = usageNotificationImported.getId();

            UsageNotificationData existingUsageNotificationData = null;
            if (Strings.isNullOrBlank(id) == false) {
                existingUsageNotificationData = ImportExportCRUDOperationUtil.get(UsageNotificationData.class, id, session);
            }
            if(existingUsageNotificationData != null && CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingUsageNotificationData.getStatus()) == false){
               validateUsageNotificationBasedOnAction(usageNotificationImported, existingUsageNotificationData, pkgData,session, action, subReasons);
            }
            validateMandatoryParameters(usageNotificationImported,subReasons);
            validateEmailAndSmsNotification(usageNotificationImported,pkgData,session,subReasons);

            validateQuotaProfileIdAndNameForUsageNotification(usageNotificationImported,pkgData,subReasons,session);
        }catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate usage notification with Id: " + usageNotificationImported.getId() + " associated with package: " + pkgData.getName());
            LogManager.getLogger().trace(MODULE,e);
            subReasons.add( "Failed to validate usage notification with Id: " + usageNotificationImported.getId() + " associated with package: " + pkgData.getName()+". Kindly refer logs for further details");

        }
        return subReasons;
    }

    private void validateMandatoryParameters(UsageNotificationData usageNotificationImported, List<String> subReasons) {
        if(usageNotificationImported.getAggregationKey() == null){
            subReasons.add("Invalid Aggregation Key is configured with Usage Notification " + BasicValidations.printIdAndName(usageNotificationImported.getId(),null));
        }
        if(usageNotificationImported.getMeteringType() == null){
            subReasons.add("Invalid Metering Type is configured with Usage Notification " + BasicValidations.printIdAndName(usageNotificationImported.getId(),null));
        }
        if(usageNotificationImported.getDataServiceTypeData() == null){
            subReasons.add("Data Service Type must be configured with Usage Notification " + BasicValidations.printIdAndName(usageNotificationImported.getId(),null));
        }
        if(usageNotificationImported.getThreshold() == null){
            subReasons.add("Threshold must be configured with Usage Notification " + BasicValidations.printIdAndName(usageNotificationImported.getId(),null));
        }else{
            validateThresholdValue(usageNotificationImported.getThreshold(), subReasons);
        }
    }

    private void validateThresholdValue(Integer threshold, List<String> subReasons) {
        boolean isValidThreshold = BasicValidations.isPositiveNumber(threshold.longValue());
        if(isValidThreshold == false){
            subReasons.add("Threshold must be positive numeric value");
            return;
        }
        if(threshold < 1 || threshold > 10000){
            subReasons.add("Threshold value must be between 1 to 10000");
        }
    }

    private void validateEmailAndSmsNotification(UsageNotificationData usageNotificationImported,PkgData pkgData, SessionProvider session, List<String> subReasons) throws Exception {
        NotificationTemplateData emailNotificationTemplateData = usageNotificationImported.getEmailTemplateData();
        NotificationTemplateData smsNotificationTemplateData = usageNotificationImported.getSmsTemplateData();
        if(emailNotificationTemplateData != null) {
            String emailNotificationId = emailNotificationTemplateData.getId();
            String emailNotificationName = emailNotificationTemplateData.getName();
            if (Strings.isNullOrBlank(emailNotificationId) && Strings.isNullOrBlank(emailNotificationName)) {
                usageNotificationImported.setEmailTemplateData(null);
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
                usageNotificationImported.setSmsTemplateData(null);
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
        if (Strings.isNullOrBlank(id) == false) {
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
    }


    private void validateUsageNotificationBasedOnAction(UsageNotificationData usageNotificationImported, UsageNotificationData existingUsageNotification,PkgData pkgData, SessionProvider session, String action, List<String> subReasons){
        if(CommonConstants.FAIL.equalsIgnoreCase(action)){
            subReasons.add("Usage Notification with Id: " + usageNotificationImported.getId() + " already exists with package: " + pkgData.getName());
        }else if(CommonConstants.REPLACE.equalsIgnoreCase(action)){
            if(Strings.isNullOrBlank(pkgData.getId()) == false && existingUsageNotification.getPkgData().getId().equalsIgnoreCase(pkgData.getId()) == false){
                subReasons.add("Usage Notification (" + usageNotificationImported.getId() + ") with package("+pkgData.getId() + CommonConstants.COMMA + pkgData.getName()
                        +") already exists in different package(" + existingUsageNotification.getPkgData().getId() + CommonConstants.COMMA + existingUsageNotification.getPkgData().getName() +")");
            } else if(Strings.isNullOrBlank(pkgData.getName()) == false && existingUsageNotification.getPkgData().getName().equalsIgnoreCase(pkgData.getName()) == false){
                subReasons.add("Usage Notification (" + usageNotificationImported.getId() + ") with package("+pkgData.getId() + CommonConstants.COMMA + pkgData.getName()
                        +") already exists in different package(" + existingUsageNotification.getPkgData().getId() + CommonConstants.COMMA + existingUsageNotification.getPkgData().getName() +")");
            }else{
                usageNotificationImported.setId(existingUsageNotification.getId());
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


    private void validateQuotaProfileIdAndNameForUsageNotification(UsageNotificationData usageNotificationImported, PkgData pkgData, List<String> subReasons, SessionProvider session) throws Exception {
        String quotaProfileId = usageNotificationImported.getQuotaProfileId();
        String quotaProfileName = usageNotificationImported.getQuotaProfileName();
        if (Strings.isNullOrBlank(quotaProfileId) && Strings.isNullOrBlank(quotaProfileName)) {
            subReasons.add("Quota Profile Id/Name is mandatory for Usage Notification" + BasicValidations.printIdAndName(usageNotificationImported.getId(), null));
            return;
        }
        boolean isQuotaProfileExists = false;

        List<QuotaProfileData> quotaProfilesImported = pkgData.getQuotaProfiles();
        if (Strings.isNullOrBlank(quotaProfileId) == false) {
            for (QuotaProfileData quotaProfile : quotaProfilesImported) {
                if (Strings.isNullOrBlank(quotaProfile.getId()) == false && quotaProfileId.equals(quotaProfile.getId())) {
                    isQuotaProfileExists = true;
                }
            }
            if (isQuotaProfileExists == false) {
                subReasons.add("Quota Profile" + BasicValidations.printIdAndName(quotaProfileId, quotaProfileName) + " associated with Usage Notification " + BasicValidations.printIdAndName(usageNotificationImported.getId(), null) + " does not exists");
            }
        } else if (Strings.isNullOrBlank(quotaProfileName) == false) {
            for (QuotaProfileData quotaProfile : quotaProfilesImported) {
                if (Strings.isNullOrBlank(quotaProfile.getName()) == false && quotaProfileName.equalsIgnoreCase(quotaProfile.getName())) {
                    isQuotaProfileExists = true;
                }
            }
            if (isQuotaProfileExists == false) {
                subReasons.add("Quota Profile" + BasicValidations.printIdAndName(quotaProfileId, quotaProfileName) + " associated with Usage Notification " + BasicValidations.printIdAndName(usageNotificationImported.getId(), null) + " does not exists");
            }
        }

    }


}
