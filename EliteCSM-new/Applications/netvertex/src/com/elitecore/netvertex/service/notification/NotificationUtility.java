package com.elitecore.netvertex.service.notification;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.constant.NotificationConstants;
import com.elitecore.netvertex.service.notification.data.NotificationEntity;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.Arrays;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class NotificationUtility {

    private static final String MODULE = "NOTIFICATION-UTILITY";

    public static List<String> getAddressFromPCRFResponse(PCRFResponse pcrfResponse, NotificationRecipient recipient, NetVertexServerContext serverContext) {

        String emailAddress = null;
        String smsAddress = null;
        if (pcrfResponse != null) {
            if (NotificationRecipient.PARENT == recipient) {
                SPRInfo parentSPRInfo = serverContext.getSPRInfo(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PARENTID.getVal()));
                if (parentSPRInfo != null) {
                    emailAddress = parentSPRInfo.getEmail();
                    smsAddress = parentSPRInfo.getPhone();
                } else {
                    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
                        LogManager.getLogger().warn(MODULE, "Parent profile not found with parentId "
                                + pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PARENTID.getVal()) + " for Child "
                                + pcrfResponse.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal()));

                    return null;
                }

            } else {
                emailAddress = pcrfResponse.getAttribute(PCRFKeyConstants.SUB_EMAIL.val);
                smsAddress = pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PHONE.val);
            }
        }


        if (emailAddress == null && smsAddress == null) {
            return null;
        }


        return Arrays.asList(emailAddress, smsAddress);

    }

    public static NotificationEntity createNotificationEntity(String notificationId,
                                                              PCRFResponse pcrfResponse,
                                                              NotificationRecipient recipient,
                                                              String emailAddress,
                                                              String smsAddress,
                                                              String emailData,
                                                              String emailSubject,
                                                              String smsData,
                                                              NetVertexServerContext serverContext){

        List<String> addresses = getAddressFromPCRFResponse(pcrfResponse, recipient, serverContext);

        if (addresses == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
                LogManager.getLogger().warn(MODULE, "Email Address and SMS Address not found from PCRFResponse");
            }
            return null;
        } else {
            if (emailAddress == null) {
                emailAddress = addresses.get(0);
            }

            if (smsAddress == null) {
                smsAddress = addresses.get(1);
            }
        }

        return new NotificationEntity(notificationId, emailSubject, emailData, emailAddress, smsData, smsAddress, pcrfResponse, false, false);
    }

    public static Boolean[] send(EmailSender emailSender, SMSSender smsSender, NotificationEntity notificationEntity, NotificationServiceCounters notificationServiceCounters){
        notificationServiceCounters.incTotalNotificationProcessed();

        Boolean mailStatus = null;
        if(emailSender != null) {
            mailStatus = emailSender.sendEmail(notificationEntity);

            if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                getLogger().debug(MODULE, "Email status for " + notificationEntity.getToEmailAddress() + " is " + (mailStatus ? NotificationConstants.SENT : NotificationConstants.FAILED));
            }
        }

        Boolean smsStatus = null;
        if(smsSender != null) {
            smsStatus = smsSender.sendSMS(notificationEntity);

            if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                getLogger().debug(MODULE, "SMS status for " + notificationEntity.getToSMSAddress() + " is " + (smsStatus ? NotificationConstants.SENT : NotificationConstants.FAILED));
            }
        }

        return new Boolean[] {mailStatus, smsStatus};
    }
}
