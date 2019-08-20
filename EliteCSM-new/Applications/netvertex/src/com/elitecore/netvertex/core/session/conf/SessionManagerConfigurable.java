package com.elitecore.netvertex.core.session.conf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.impl.FieldMappingImpl;
import com.elitecore.corenetvertex.constants.BatchUpdateMode;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.session.SessionConfigurationData;
import com.elitecore.corenetvertex.sm.session.SessionConfigurationFieldMappingData;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.session.conf.impl.SessionManagerConfigurationImpl;
import com.elitecore.netvertex.core.util.ConfigLogger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.BatchUpdateMode.fromValue;

public class SessionManagerConfigurable extends BaseConfigurationImpl {

    private static final String MODULE = "SESS-MNGR-CONFIGRBLE";
    private SessionManagerConfigurationImpl configuration;
    private SessionFactory sessionFactory;

    public SessionManagerConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
        super(serverContext);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void readConfiguration() throws LoadConfigurationException {

        getLogger().info(MODULE, "Read Session Manager configuration started");

        Session session = sessionFactory.openSession();

        try {
            List<SessionConfigurationData> sessionConfigurationDatas = HibernateReader.readAll(SessionConfigurationData.class, session);
            if (Collectionz.isNullOrEmpty(sessionConfigurationDatas)) {
                throw new LoadConfigurationException("No session configuration found.");
            }

            if (sessionConfigurationDatas.size() > 1) {
                getLogger().warn(MODULE, "Invalid session configuation, taking first session configration with id: "
                        + sessionConfigurationDatas.get(0).getId() + ". Reason: More than one session configurations found.");
            }

            SessionConfigurationData sessionConfigurationData = sessionConfigurationDatas.get(0);

            List<FieldMapping> coreSessFieldMappings = new ArrayList<>();
            boolean isBatchUpdateEnable;
            long batchSize = CommonConstants.BATCH_SIZE_MIN;
            long batchUpdateInterval = 1; //making it to 1 second
            int batchQueryTimeout = 2;
            List<FieldMapping> sessionRuleFieldMappings = new ArrayList<>();
            boolean isSaveInBatch;
            boolean isUpdateInBatch;
            boolean isDeleteInBatch;

            Integer batchUpdate = sessionConfigurationData.getBatchMode();

            BatchUpdateMode batchUpdateMode = BatchUpdateMode.FALSE;
            if (batchUpdate == null) {
                LogManager.getLogger().warn(MODULE, "Considering default value " + BatchUpdateMode.FALSE + " for batch update. Reason: Configuration value not specified");
            } else {
                batchUpdateMode = fromValue(batchUpdate);
            }

            if (batchUpdateMode == BatchUpdateMode.TRUE) {
                isBatchUpdateEnable = true;
                isSaveInBatch = true;
                isUpdateInBatch = true;
                isDeleteInBatch = true;
            } else if (batchUpdateMode == BatchUpdateMode.HYBRID) {

                isBatchUpdateEnable = true;
                isSaveInBatch = false;
                isUpdateInBatch = true;
                isDeleteInBatch = true;
            } else {
                isBatchUpdateEnable = false;
                isSaveInBatch = false;
                isUpdateInBatch = false;
                isDeleteInBatch = false;
            }

            if (isBatchUpdateEnable) {

                int size = sessionConfigurationData.getBatchSize();

                if (size > CommonConstants.MAX_BATCH_SIZE) {
                    batchSize = CommonConstants.MAX_BATCH_SIZE;
                } else if (size >= CommonConstants.BATCH_SIZE_MIN) {
                    batchSize = size;
                } else {
                    getLogger().warn(MODULE, "Using default value '" + batchSize
                            + "' for 'Batch Size'. Reason: Configured 'Batch Size' " + size
                            + " is less than minimum required value '" + CommonConstants.BATCH_SIZE_MIN + "'");
                }

                if (sessionConfigurationData.getQueryTimeout() != null) {
                    batchQueryTimeout = sessionConfigurationData.getQueryTimeout();
                }

            }

            coreSessFieldMappings.addAll(formDefaultCoreSessionFieldMappings());
            for (SessionConfigurationFieldMappingData fieldMapping : sessionConfigurationData.getSessionConfigurationFieldMappingDatas()) {
                coreSessFieldMappings.add(new FieldMappingImpl(fieldMapping.getDataType(), fieldMapping.getReferringAttribute()
                        , fieldMapping.getFieldName()));
            }

            sessionRuleFieldMappings.addAll(formDefaultSessionRuleFieldMappings());

            configuration = new SessionManagerConfigurationImpl(isBatchUpdateEnable, batchSize, batchUpdateInterval, batchQueryTimeout,
                    isSaveInBatch, isUpdateInBatch, isDeleteInBatch, coreSessFieldMappings, sessionRuleFieldMappings);
            ConfigLogger.getInstance().info(MODULE, configuration.toString());
        } finally {
            HibernateConfigurationUtil.closeQuietly(session);
        }

        getLogger().debug(MODULE, "Read Session Manager configuration completed");
    }

    /**
     * Set Default field mappings for core session
     *
     * @return <code>List<FieldMappings></code>
     */
    private List<FieldMapping> formDefaultCoreSessionFieldMappings() {
        List<FieldMapping> fieldMappings = new ArrayList<>();
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal(), "SUBSCRIBER_IDENTITY"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_CORESESSION_ID.getVal(), "CORE_SESSION_ID"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_ID.getVal(), "SESSION_ID"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_MANAGER_ID.getVal(), "SESSION_MANAGER_ID"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_IPV4.getVal(), "SESSION_IP_V4"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_IPV6.getVal(), "SESSION_IP_V6"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_ACCESS_NETWORK.getVal(), "ACCESS_NETWORK"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_TYPE.getVal(), "SESSION_TYPE"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.QOS_PROFILE_NAME.getVal(), "QOS_PROFILE"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal(), "SOURCE_GATEWAY"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SY_SESSION_ID.getVal(), "SY_SESSION_ID"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SY_GATEWAY_NAME.getVal(), "SY_GATEWAY_NAME"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_GATEWAY_NAME.val, "GATEWAY_NAME"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.LOCATION_NEW_CONGESTION_STATUS.val, "CONGESTION_STATUS"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val, "IMSI"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val, "MSISDN"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_NAI.val, "NAI"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_NAI_REALM.val, "NAI_REALM"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_NAI_RELATED_USERNAME.val, "NAI_USER_NAME"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_SIPURI.val, "SIP_URL"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val, "PCC_RULES"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_REQ_IP_CAN_QOS.val, "REQUESTED_QOS"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_USAGE.val, "SESSION_USAGE"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.REQUEST_NUMBER.val, "REQUEST_NUMBER"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_USAGE_RESERVATION.val, "USAGE_RESERVATION"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), "GATEWAY_ADDRESS"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_GATEWAY_REALM.getVal(), "GATEWAY_REALM"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_PACKAGE_USAGE.getVal(), "PACKAGE_USAGE"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.val, "CHARGING_RULE_BASE_NAMES"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_CALLING_STATION_ID.val, "CALLING_STATION_ID"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_CALLED_STATION_ID.val, "CALLED_STATION_ID"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_QUOTA_RESERVATION.val, "QUOTA_RESERVATION"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_PCC_PROFILE_SELECTION_STATE.val, "PCC_PROFILE_SELECTION_STATE"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val, "UNACCOUNTED_QUOTA"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SGSN_MCC_MNC.val, "SGSN_MCC_MNC"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SERVICE.val, "SERVICE"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_LOCATION.val, "LOCATION"));
        return fieldMappings;
    }

    private List<FieldMapping> formDefaultSessionRuleFieldMappings() {
        List<FieldMapping> fieldMappings = new ArrayList<>();

        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_CORESESSION_ID.getVal(), "SESSION_ID"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_AF_SESSION_ID.getVal(), "AF_SESSION_ID"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.PCC_RULE_LIST.getVal(), "PCC_RULE"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_MEDIA_TYPE.getVal(), "MEDIA_TYPE"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_MEDIA_COMPONENT_NUMBER.getVal(), "MEDIA_COMPONENT_NUMBER"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_FLOW_NUMBSER.getVal(), "FLOW_NUMBER"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), "GATEWAY_ADDRESS"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_UPLINK_FLOW.getVal(), "UPLINK_FLOW"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_DOWNLINK_FLOW.getVal(), "DOWNLINK_FLOW"));
        fieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.PCC_ADDITIONAL_PARAMETER.getVal(), "ADDITIONAL_PARAMETER"));

        return fieldMappings;
    }

    public SessionManagerConfiguration getConfiguration() {
        return configuration;
    }

    public void reloadConfiguration() {

        getLogger().info(MODULE, "Reload Session manager configuration started");

        Session session = sessionFactory.openSession();
        try {
            List<SessionConfigurationData> sessionConfigurationDatas = HibernateReader.readAll(SessionConfigurationData.class, session);
            if (Collectionz.isNullOrEmpty(sessionConfigurationDatas)) {
                getLogger().warn(MODULE, "No session configuration found. Continuing with previous configuration.");
                return;
            }
            long previousBatchSize = configuration.getBatchSize();

            SessionConfigurationData sessionConfigurationData = sessionConfigurationDatas.get(0);

            if (configuration.isBatchUpdateEnable()) {

                int newBatchSize = sessionConfigurationData.getBatchSize();
                if (newBatchSize >= CommonConstants.BATCH_SIZE_MIN) {
                    configuration.setBatchSize(newBatchSize);
                } else {
                    LogManager.getLogger().warn(MODULE, "Using previous value '" + previousBatchSize
                            + "' for 'Batch Size'. Reason: Configured 'Batch Size' " + newBatchSize
                            + " is less than minimum required value '" + CommonConstants.BATCH_SIZE_MIN + "'");
                }
            }

            ConfigLogger.getInstance().info(MODULE, configuration.toString());
        } finally {
            HibernateConfigurationUtil.closeQuietly(session);
        }

        getLogger().debug(MODULE, "Reload Session Manager configuration completed.");
    }
}
