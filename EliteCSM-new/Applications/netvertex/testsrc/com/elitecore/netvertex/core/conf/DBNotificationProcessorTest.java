package com.elitecore.netvertex.core.conf;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.Notification;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.constant.NotificationConstants;
import com.elitecore.netvertex.service.notification.*;
import com.elitecore.netvertex.service.notification.conf.EmailAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.SMSAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.netvertex.service.notification.data.NotificationEntity;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class DBNotificationProcessorTest {

    private DBNotificationProcessor dbNotificationProcessor;
    private DBNotificationProcessorExt dbNotificationProcessorExt;
    private PCRFResponse pcrfResponse;
    private List<Notification> notifications = new ArrayList<>();
    private DummyNetvertexServerContextImpl serverContext;
    private TransactionFactory transactionFactory;
    private NotificationServiceCounters notificationServiceCounters;

    @Mock private EmailSender emailSender;
    @Mock private SMSSender smsSender;

    private static final String DS_NAME = "NetvertexServerDB";
    private static final String INSERT_QUERY = "INSERT INTO TBLT_NOTIFICATION_QUEUE " +
            "(ID, EMAIL_STATUS , SMS_STATUS , EMAIL_SUBJECT, EMAIL_DATA, SMS_DATA, PCRF_RESPONSE, SOURCE_ID, VALIDITY_DATE, NOTIFICATION_RECIPIENT, SUBSCRIBER_IDENTITY, SERVER_INSTANCE_ID, TO_EMAIL_ADDRESS, TO_SMS_ADDRESS, TIMESTAMP) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
    private static final String CREATE_QUERY = "CREATE TABLE TBLT_NOTIFICATION_QUEUE(\n" +
            "  ID               VARCHAR(36),\n" +
            "  TO_EMAIL_ADDRESS VARCHAR(256),\n" +
            "  TO_SMS_ADDRESS   VARCHAR(256),\n" +
            "  EMAIL_SUBJECT    VARCHAR(256),\n" +
            "  EMAIL_DATA       VARCHAR(4000),\n" +
            "  SMS_DATA         VARCHAR(4000),\n" +
            "  EMAIL_STATUS     VARCHAR(128),\n" +
            "  SMS_STATUS       VARCHAR(128),\n" +
            "  SOURCE_ID        VARCHAR(256),\n" +
            "  PCRF_RESPONSE     BLOB,\n" +
            "  SERVER_INSTANCE_ID   VARCHAR(36),\n" +
            "  VALIDITY_DATE        TIMESTAMP,\n" +
            "  NOTIFICATION_RECIPIENT NUMERIC,\n" +
            "  SUBSCRIBER_IDENTITY    VARCHAR(256),\n" +
            "  TIMESTAMP              TIMESTAMP,\n" +
            "  CONSTRAINT PK_MNOTIFICATION_QUEUE PRIMARY KEY (ID)\n" +
            ")";
    private static final String DROP_QUERY = "DROP TABLE TBLT_NOTIFICATION_QUEUE";

    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws IOException, SQLException, TransactionException, DatabaseInitializationException, DatabaseTypeNotSupportedException {
        MockitoAnnotations.initMocks(this);
        serverContext = spy(new DummyNetvertexServerContextImpl());
        serverContext.setServerInstanceId(UUID.randomUUID().toString());
        DummyNetvertexServerConfiguration serverConfiguration = serverContext.getServerConfiguration();

        EmailAgentConfiguration emailAgentConfiguration = new EmailAgentConfiguration(null, "127.0.0.1", 1000, null, null);
        SMSAgentConfiguration smsAgentConfiguration = new SMSAgentConfiguration(null, "");
        serverConfiguration.setNotificationServiceConfiguration(createDummyNotificationServiceConfiguration(emailAgentConfiguration, smsAgentConfiguration));

        NotificationServiceContext serviceContext = new NotificationServiceContext() {

            @Override
            public NetVertexServerContext getServerContext() {
                return serverContext;
            }

            @Override
            public NotificationServiceConfigurationImpl getNotificationServiceConfiguration() {
                return serverContext.getServerConfiguration().getNotificationServiceConfiguration();
            }
        };
        notificationServiceCounters = spy(new NotificationServiceCounters(serviceContext));
        notificationServiceCounters.init();

        pcrfResponse = new PCRFResponseImpl();
        dbNotificationProcessor = new DBNotificationProcessor(serverContext, emailSender, smsSender, notificationServiceCounters);
        dbNotificationProcessorExt = new DBNotificationProcessorExt(serverContext, emailSender, smsSender, notificationServiceCounters);

        setUpDB();
    }

    private void setUpDB() throws DatabaseTypeNotSupportedException, DatabaseInitializationException {
        String url = "jdbc:derby:memory:TestingDB;create=true";

        DBDataSource dbDataSource = new DBDataSourceImpl("0", DS_NAME, url,
                "", "", 2, 2, 3000, 10000, 2000);

        DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).init(dbDataSource, new FakeTaskScheduler());

        DummyNetvertexServerConfiguration serverConfiguration = serverContext.getServerConfiguration();
        NetvertexServerGroupConfiguration netvertexServerGroupConfiguration = serverConfiguration.spyGroupConfiguration();
        when(netvertexServerGroupConfiguration.getNotificationDS()).thenReturn(dbDataSource);

        transactionFactory = DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).getTransactionFactory();
    }

    private void saveNotificationInDB() throws IOException, SQLException, TransactionException {
        Transaction transaction = transactionFactory.createTransaction();
        transaction.begin();

        PreparedStatement preparedStatement = transaction.prepareStatement(CREATE_QUERY);
        preparedStatement.execute();

        for (Notification notification : notifications) {
            preparedStatement = transaction.prepareStatement(INSERT_QUERY);
            prepareInsertStatement(preparedStatement, notification);
            preparedStatement.execute();
        }

        transaction.commit();
        transaction.end();
    }

    private boolean prepareInsertStatement(PreparedStatement preparedStatement, Notification notification) throws SQLException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1000);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        PCRFResponse pcrfResponse = notification.getPCRFResponse();
        objectOutputStream.writeObject(pcrfResponse);
        preparedStatement.setString(1, UUID.randomUUID().toString());
        preparedStatement.setString(2, NotificationConstants.RESERVE);
        preparedStatement.setString(3, NotificationConstants.RESERVE);
        preparedStatement.setString(4, notification.getEmailSubject());
        preparedStatement.setString(5, notification.getEmailTemplateData());
        preparedStatement.setString(6, notification.getSMSTemplateData());
        preparedStatement.setBytes(7, byteArrayOutputStream.toByteArray());
        preparedStatement.setString(8, "");
        preparedStatement.setTimestamp(9, notification.getValidityDate());
        preparedStatement.setInt(10, notification.getRecipient());
        preparedStatement.setString(11, notification.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal()));
        preparedStatement.setString(12, serverContext.getServerInstanceId());
        preparedStatement.setString(13, notification.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_EMAIL.val));
        preparedStatement.setString(14, notification.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_PHONE.val));

        return true;
    }

    @Test
    public void testNotificationEntitiesToBeProcessedWhenNotificationIsSentThroughDB() throws TransactionException, SQLException, IOException {

        notifications.add(createNotification());
        saveNotificationInDB();

        List<NotificationEntity> notificationsToBeProcessed = dbNotificationProcessor.getNotificationsToBeProcessed();

        ReflectionAssert.assertReflectionEquals(notificationsToBeProcessed, getExpectedNotificationEntities(notificationsToBeProcessed));
    }

   @Test
    public void sendEmailAndSMSNotificationForMultipleNotificationEntity() throws TransactionException, SQLException, IOException {

       notifications.add(createNotification());
       notifications.add(createNotification());
       saveNotificationInDB();

        List<NotificationEntity> notificationsToBeProcessed = dbNotificationProcessor.getNotificationsToBeProcessed();

        notificationsToBeProcessed.stream().forEach(notificationEntity -> {
            dbNotificationProcessorExt.processTargetEntity(notificationEntity);
            verify(emailSender, atLeastOnce()).sendEmail(notificationEntity);
            verify(smsSender, atLeastOnce()).sendSMS(notificationEntity);
        });
    }

    @Test
    public void sendEmailAndSMSNotificationForParentNotificationRecepientAndValidEmailAndSMSAddress() throws TransactionException, SQLException, IOException {

        Notification notification = createNotification();
        notification.setRecipient(NotificationRecipient.PARENT);
        notifications.add(notification);
        saveNotificationInDB();

        SPRInfo sprInfo = new SPRInfoImpl.SPRInfoBuilder()
                .withEmail(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_EMAIL.val))
                .withPhone(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PHONE.val))
                .build();

        when(serverContext.getSPRInfo(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PARENTID.getVal()))).thenReturn(sprInfo);

        List<NotificationEntity> notificationEntities = dbNotificationProcessor.getNotificationsToBeProcessed();

        ReflectionAssert.assertReflectionEquals(getExpectedNotificationEntities(notificationEntities), notificationEntities);

        notificationEntities.stream().forEach(notificationEntity -> {
            dbNotificationProcessorExt.processTargetEntity(notificationEntity);
            verify(emailSender, atLeastOnce()).sendEmail(notificationEntity);
            verify(smsSender, atLeastOnce()).sendSMS(notificationEntity);
        });
    }

    @Test
    public void sendEmailAndSMSNotificationForParentNotificationRecepientAndNullEmailAndSMSAddress() throws TransactionException, SQLException, IOException {

        Notification notification = createNotification();
        notification.setRecipient(NotificationRecipient.PARENT);
        notification.getPCRFResponse().setAttribute(PCRFKeyConstants.SUB_EMAIL.val, null);
        notification.getPCRFResponse().setAttribute(PCRFKeyConstants.SUB_PHONE.val, null);
        notifications.add(notification);
        saveNotificationInDB();

        SPRInfo sprInfo = new SPRInfoImpl.SPRInfoBuilder()
                .withEmail(null)
                .withPhone(null)
                .build();

        when(serverContext.getSPRInfo(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PARENTID.getVal()))).thenReturn(sprInfo);

        List<NotificationEntity> notificationEntities = dbNotificationProcessor.getNotificationsToBeProcessed();

        notificationEntities.stream().forEach(notificationEntity -> {
            verify(emailSender, never()).sendEmail(notificationEntity);
            verify(smsSender, never()).sendSMS(notificationEntity);
        });
    }

   @Test
    public void incNotificationServiceCounterOnNotificationExecution() throws TransactionException, SQLException, IOException {
        notifications.add(createNotification());
        saveNotificationInDB();

       List<NotificationEntity> notificationEntities = dbNotificationProcessor.getNotificationsToBeProcessed();
       notificationEntities.stream().forEach(notificationEntity -> {
           dbNotificationProcessorExt.processTargetEntity(notificationEntity);
       });

       verify(notificationServiceCounters, times(notifications.size())).incTotalNotificationProcessed();
    }

    @After
    public void dropTable() throws TransactionException, SQLException {
        Transaction transaction = transactionFactory.createTransaction();
        transaction.begin();

        PreparedStatement preparedStatement = transaction.prepareStatement(DROP_QUERY);
        preparedStatement.execute();

        transaction.end();
        DerbyUtil.closeDerby("TestingDB");

    }

    public Notification createNotification() {
        Template emailTemplate = new Template(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        Template smsTemplate = new Template(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_EMAIL.val, "a@a.com");
        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_PHONE.val, "1234567899");
        return new Notification(emailTemplate, smsTemplate, pcrfResponse, new Timestamp(System.currentTimeMillis()), NotificationRecipient.SELF, NotificationConstants.FAILED.toString(), NotificationConstants.FAILED.toString());
    }

    public List<NotificationEntity> getExpectedNotificationEntities(List<NotificationEntity> notificationEntity) {

        List<NotificationEntity> notificationEntities = new ArrayList<>();
        for(int index = 0; index <  notificationEntity.size(); index++){
            notificationEntities.add(new NotificationEntity(
                    notificationEntity.get(index).getNotificationId(),
                    notifications.get(index).getEmailSubject(),
                    notifications.get(index).getEmailTemplateData(),
                    pcrfResponse.getAttribute(PCRFKeyConstants.SUB_EMAIL.val),
                    notifications.get(index).getSMSTemplateData(),
                    pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PHONE.val),
                    pcrfResponse,
                    false,
                    false));
        }

        return notificationEntities;
    }

    private NotificationServiceConfigurationImpl createDummyNotificationServiceConfiguration(EmailAgentConfiguration emailAgentConfiguration, SMSAgentConfiguration smsAgentConfiguration){
        return new NotificationServiceConfigurationImpl(nextInt(1, Integer.MAX_VALUE), nextInt(1, Integer.MAX_VALUE), nextInt(10, Integer.MAX_VALUE), emailAgentConfiguration, smsAgentConfiguration);
    }

    private class DBNotificationProcessorExt extends DBNotificationProcessor {

        public DBNotificationProcessorExt(NetVertexServerContext serverContext, EmailSender emailSender, SMSSender smsSender, NotificationServiceCounters notificationServiceCounters) {
            super(serverContext, emailSender, smsSender, notificationServiceCounters);
        }

        public void processTargetEntity(NotificationEntity notificationEntity){
            doProcessTargetEntity(notificationEntity);
        }
    }
}
