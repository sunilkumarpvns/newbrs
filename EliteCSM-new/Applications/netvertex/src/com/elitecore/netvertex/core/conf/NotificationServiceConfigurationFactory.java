package com.elitecore.netvertex.core.conf;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.notificationagents.EmailAgentData;
import com.elitecore.corenetvertex.sm.notificationagents.SMSAgentData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.sm.serverprofile.ServerProfileData;
import com.elitecore.netvertex.service.notification.conf.EmailAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.SMSAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

import java.util.Objects;

public class NotificationServiceConfigurationFactory {
    private static final String MODULE = "NOTIFICATION";

    public NotificationServiceConfigurationImpl create(ServerInstanceData serverInstanceData, ServerProfileData serverProfileData) {

        EmailAgentData emailAgentData = serverInstanceData.getEmailAgentData();
        EmailAgentConfiguration emailAgentConfiguration = null;
        if(Objects.nonNull(emailAgentData)) {
            emailAgentConfiguration = createEmailAgentConfiguration(emailAgentData);
        }

        SMSAgentConfiguration smsAgentConfiguration = null;
        SMSAgentData smsAgentData = serverInstanceData.getSmsAgentData();
        if(Objects.nonNull(smsAgentData)) {
            smsAgentConfiguration = createSMSAgentConfiguration(smsAgentData);
        }


        int serviceExecutionPeriod = serverProfileData.getNotificationServiceExecutionPeriod();
        if(serviceExecutionPeriod < 1) {
            LogManager.getLogger().warn(MODULE,"Using default value '"+ CommonConstants.BATCH_SIZE_MIN
                    +"' for parameter 'Service Execution Period'. Reason: Configured 'Service Execution Period' "+ serverProfileData.getNotificationServiceExecutionPeriod()
                    +" is less than minimum required value '1'");
            serviceExecutionPeriod = 1;
        }

        int maxParallelExecution = serverProfileData.getMaxParallelExecution();
        if(maxParallelExecution < 1) {
            LogManager.getLogger().warn(MODULE,"Using default value 1'"
                    +"' for parameter 'Max Parallel Execution'. Reason: Configured 'Max Parallel Execution' "+ serverProfileData.getMaxParallelExecution()
                    +" is less than minimum required value 1'");
            maxParallelExecution = 1;
        }

        int batchSize = serverProfileData.getBatchSize();
        if(batchSize < CommonConstants.BATCH_SIZE_MIN) {
            LogManager.getLogger().warn(MODULE,"Using default value '"+ CommonConstants.BATCH_SIZE_MIN
                    +"' for parameter 'Batch Size'. Reason: Configured 'Batch Size' "+ serverProfileData.getBatchSize()
                    +" is less than minimum required value '"+CommonConstants.BATCH_SIZE_MIN+"'");
            batchSize = CommonConstants.BATCH_SIZE_MIN;
        }

        return new NotificationServiceConfigurationImpl(serviceExecutionPeriod, maxParallelExecution, batchSize, emailAgentConfiguration, smsAgentConfiguration);

    }

    private EmailAgentConfiguration createEmailAgentConfiguration(EmailAgentData emailAgentData) {


        try {
            URLData parse = URLParser.parse(emailAgentData.getEmailHost());
            int port = parse.getPort();
            String host = parse.getHost();
            return new EmailAgentConfiguration(emailAgentData.getFromAddress(), host, port, emailAgentData.getUserName(), emailAgentData.getPassword());
        } catch (InvalidURLException e) {
            LogManager.getLogger().error(MODULE, "Error while parsing from address:" + emailAgentData.getFromAddress() + ". Reason:" +e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
        }

        return null;
    }

    private SMSAgentConfiguration createSMSAgentConfiguration(SMSAgentData smsAgentData) {
        try {
            return new SMSAgentConfiguration(smsAgentData.getServiceURL(), PasswordEncryption.getInstance().decrypt(smsAgentData.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT));
        } catch (NoSuchEncryptionException | DecryptionNotSupportedException | DecryptionFailedException e) {
            throw new AssertionError("Password alway decrypted by elite password crypt", e);
        }
    }
}
