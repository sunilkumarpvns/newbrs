package com.elitecore.netvertex.service.notification;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.notification.email.EmailNotifier;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.util.TagProcessor;
import com.elitecore.netvertex.core.util.exception.TagParsingException;
import com.elitecore.netvertex.service.notification.data.NotificationEntity;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class EmailNotificationSender implements EmailSender {
    private static final String MODULE = "EMAIL-SENDER";

    private NetVertexServerContext serverContext;
    private EmailNotifier emailNotifier;
    private NotificationServiceCounters notificationServiceCounters;
    private TagProcessor tagProcessor;

    public EmailNotificationSender(NetVertexServerContext serverContext,
                                   EmailNotifier emailNotifier,
                                   NotificationServiceCounters notificationServiceCounters) {
        this.serverContext =  serverContext;
        this.emailNotifier = emailNotifier;
        this.notificationServiceCounters = notificationServiceCounters;
        tagProcessor = new TagProcessor();
    }

    @Override
    public boolean sendEmail(NotificationEntity notificationEntity) {

        notificationServiceCounters.incTotalEmailProcessed();
        if(notificationEntity.getToEmailAddress() == null) {
            getLogger().error(MODULE, "Email address not found for notification Id: " + notificationEntity.getNotificationId()
                    + logSubscriberIdentityIfAvailable(notificationEntity));
            notificationServiceCounters.incTotalEmailFailuresCntr();
            return false;
        }

        try {
            notificationEntity.setEmailData(tagProcessor.getTemplate(notificationEntity.getEmailData(), notificationEntity.getPcrfResponse()));
            if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "Sending email to " + notificationEntity.getToEmailAddress());

            boolean mailStatus = emailNotifier.send(notificationEntity.getEmailSubject(), notificationEntity.getEmailData(), notificationEntity.getToEmailAddress());
            if (mailStatus == true) {
                notificationServiceCounters.incTotalEmailSentCntr();
            } else {
                notificationServiceCounters.incTotalEmailFailuresCntr();
            }

            if(getLogger().isLogLevel(LogLevel.DEBUG)) {
                getLogger().debug(MODULE,"Email status for " + notificationEntity.getToEmailAddress() + " is " + mailStatus);
            }

            return mailStatus;
        } catch (TagParsingException tpExe) {
            getLogger().error(MODULE, "Email notification sending failed to email id: " + notificationEntity.getToEmailAddress()
                    + logSubscriberIdentityIfAvailable(notificationEntity) + ". Reason: " + tpExe.getMessage());
            getLogger().trace(MODULE, tpExe);
            generateEmailAlerts("Error in tag parsing");

            notificationServiceCounters.incTotalEmailFailuresCntr();

        } catch (Exception e) {
            getLogger().trace(MODULE, e);

            getLogger().error(MODULE, "Email notification sending failed to email id: " + notificationEntity.getToEmailAddress()
                    + logSubscriberIdentityIfAvailable(notificationEntity) + ". Reason: " + e.getMessage());

            generateEmailAlerts(e.getMessage());

            notificationServiceCounters.incTotalEmailFailuresCntr();

        }

        return false;
    }

    private void generateEmailAlerts(String reason) {
        serverContext.generateSystemAlert(AlertSeverity.WARN, Alerts.EMAIL_SENDING_FAILED, MODULE, "Email notification sending failed. Reason: " + reason);
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
        emailNotifier.stop();
        return true;
    }
}
