package com.elitecore.netvertex.service.notification;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.notification.sms.SMSCommunicatorGroup;
import com.elitecore.core.notification.sms.SMSNotifier;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.util.TagProcessor;
import com.elitecore.netvertex.core.util.exception.TagParsingException;
import com.elitecore.netvertex.service.notification.data.NotificationEntity;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SMSNotificationSender implements SMSSender {
    private static final String MODULE = "SMS-SENDER";

    private NetVertexServerContext serverContext;
    private SMSNotifier smsNotifier;
    private SMSCommunicatorGroup smsCommunicatorGroup;
    private NotificationServiceCounters notificationServiceCounters;
    private TagProcessor tagProcessor;

    public SMSNotificationSender(NetVertexServerContext serverContext,
                                 SMSNotifier smsNotifier,
                                 SMSCommunicatorGroup smsCommunicatorGroup,
                                 NotificationServiceCounters notificationServiceCounters) {
        this.serverContext = serverContext;
        this.smsNotifier = smsNotifier;
        this.smsCommunicatorGroup = smsCommunicatorGroup;
        this.notificationServiceCounters = notificationServiceCounters;
        tagProcessor = new TagProcessor();
    }

    /**
     *
     *
     * @return boolean smsStatus
     */
    @Override
    public boolean sendSMS(NotificationEntity notificationEntity) {

        notificationServiceCounters.incTotalSMSProcessed();
        if(notificationEntity.getToSMSAddress() == null) {
            getLogger().error(MODULE, "SMS address not found for notification Id: " + notificationEntity.getNotificationId()
                    + logSubscriberIdentityIfAvailable(notificationEntity));
            notificationServiceCounters.incTotalSMSFailuresCntr();
            return false;
        }

        try {
            notificationEntity.setSmsData(tagProcessor.getTemplate(notificationEntity.getSMSData(), notificationEntity.getPcrfResponse()));

            if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "Sending SMS to " + notificationEntity.getToSMSAddress());

            boolean smsStatus = smsCommunicatorGroup.send(notificationEntity.getSMSData(), notificationEntity.getToSMSAddress());

            if (smsStatus == true) {
                notificationServiceCounters.incTotalSMSSentCntr();
            } else {
                notificationServiceCounters.incTotalSMSFailuresCntr();
            }

            return smsStatus;
        } catch (TagParsingException tpExe) {

            getLogger().warn(MODULE, "SMS notification sending failed to phone number: " + notificationEntity.getToSMSAddress()
                    + logSubscriberIdentityIfAvailable(notificationEntity)
                    + ". Reason: " + tpExe.getMessage());
            getLogger().trace(MODULE, tpExe);
            generateSMSAlerts("Error in tag parsing");

            notificationServiceCounters.incTotalSMSFailuresCntr();

        }catch (Exception e) {

            getLogger().error(MODULE, "SMS notification sending failed to phone number: " + notificationEntity.getToSMSAddress()
                    + logSubscriberIdentityIfAvailable(notificationEntity)
                    + ". Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);

            generateSMSAlerts(e.getMessage());

            notificationServiceCounters.incTotalSMSFailuresCntr();
        }

        return false;
    }

    private void generateSMSAlerts(String reason) {
        serverContext.generateSystemAlert(AlertSeverity.WARN, Alerts.SMS_SENDING_FAILED, MODULE, "SMS notification sending failed Reason: " + reason);
    }

    /*
     * USED FOR LOG READABILITY
     *
     *   first gets subscriber identity from NotificationEntity,
     *   IF subscriber identity null THEN
     *   	returns empty string
     *   ELSE
     *   	return "for subscriber identity: " + <subscriber identity>
     */
    private String logSubscriberIdentityIfAvailable(
            NotificationEntity notificationEntity) {
        return getSubscriberIdentity(notificationEntity) != null ? ", for subscriber identity: " + getSubscriberIdentity(notificationEntity) : "";
    }

    /*
     * fetches Subscriber Identity from PCRFResponse
     *
     * IF subscriber id not found THEN
     * 		returns null
     *
     */
    private String getSubscriberIdentity(NotificationEntity notificationEntity) {
        if (notificationEntity.getPcrfResponse() == null) {
            return null;
        }
        return notificationEntity.getPcrfResponse().getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
    }

    @Override
    public boolean stop() {
        if(smsNotifier != null) {
            smsNotifier.stop();
        }

        return true;
    }
}
