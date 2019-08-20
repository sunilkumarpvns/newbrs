package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.transaction.DummyTransaction;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.spr.data.AlternateIdType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.sql.Connection;
import java.util.Objects;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static groovy.util.GroovyTestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class AlternateIdentityMapperTest {

    private DummyTransactionFactory transactionFactory;
    private ExternalAlternateIdEDRListener externalAlternateIdEDRListener;
    private AlternateIdentityMapper alternateIdentityMapper;
    private HibernateSessionFactory hibernateSessionFactory;
    private String subscriberId = UUID.randomUUID().toString();
    private String alternateId = UUID.randomUUID().toString();
    private String subscriberExternalAlternateId = UUID.randomUUID().toString();
    private String updatedSubscriberId = UUID.randomUUID().toString();

    private static final String CONNECTION_NOT_FOUND = "CONNECTION_NOT_FOUND";
    private static final String BEGIN = "BEGIN";
    private static final String COMMIT = "COMMIT";
    private static final String PREPARED_STATEMENT = "PREPARED_STATEMENT";
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        String ssid = UUID.randomUUID().toString();
        transactionFactory = DummyTransactionFactory.createTransactionFactory(ssid);
        externalAlternateIdEDRListener = Mockito.mock(ExternalAlternateIdEDRListener.class);
        alternateIdentityMapper = new AlternateIdentityMapper(transactionFactory, null);
        hibernateSessionFactory = HibernateSessionFactory.newInMemorySessionFactory(ssid);
    }


    @After
    public void tearDown() throws Exception {
        //cache.flush();

        Connection connection = transactionFactory.getConnection();
        if (Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        hibernateSessionFactory.shutdown();
    }


    public class addMappingShouldBe {

        @Before
        public void added() throws OperationFailedException {
            addMapping();
        }

        @Test
        public void intoDB() throws OperationFailedException {
            String expectedSubscriberId = alternateIdentityMapper.getSubscriberId(alternateId);
            assertEquals(subscriberId, expectedSubscriberId);
        }

        @Test
        public void alwaysAddSPRAndActiveStatusTypeOfAlternateId() throws OperationFailedException {
            SubscriberAlternateIds alternateIds = alternateIdentityMapper.getAlternateIds(subscriberId);
            assertEquals(AlternateIdType.SPR.name(), alternateIds.getSprTypeAlternateId().getType());
            assertEquals(CommonConstants.STATUS_ACTIVE, alternateIds.getSprTypeAlternateId().getStatus());
        }


        public class ThrowOperationFailedException {

            @Test
            public void whenTransactionFactoryIsNotAlive() throws OperationFailedException {

                doReturn(false).when(transactionFactory).isAlive();

                exception.expect(OperationFailedException.class);
                exception.expectMessage("Datasource not available");
                addMapping();
            }

            @Test(expected = OperationFailedException.class)
            public void whilePreparedStatementCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(PREPARED_STATEMENT);
                addMapping();
            }

            @Test(expected = OperationFailedException.class)
            public void whileConnectionNotFound() throws TransactionException, OperationFailedException {
                setUpTransactionFailedException(CONNECTION_NOT_FOUND);
                addMapping();
            }

            @Test(expected = OperationFailedException.class)
            public void whileCommitCall() throws TransactionException, OperationFailedException {
                setUpTransactionFailedException(COMMIT);
                addMapping();
            }

            @Test(expected = OperationFailedException.class)
            public void transactionIsNull() throws OperationFailedException {
                doReturn(null).when(transactionFactory).createTransaction();
                addMapping();
            }

            @Test(expected = OperationFailedException.class)
            public void tryingToAddDuplicateId() throws OperationFailedException {
                addMapping();
                addMapping();
            }

        }


    }


    public class updateMappingShouldBe {
        @Before
        public void updated() throws OperationFailedException {
            addMapping();
            updateMapping();
        }

        @Test
        public void intoDB() throws OperationFailedException {
            String actualAlternateId = alternateIdentityMapper.getAlternateId(subscriberId);
            assertEquals(alternateId, actualAlternateId);
        }

        @Test
        public void updateSPRTypeOfAlternateId() throws OperationFailedException {
            addExternalMapping();
            String updatedId = UUID.randomUUID().toString();
            alternateIdentityMapper.updateMapping(subscriberId, updatedId);
            SubscriberAlternateIds alternateIds = alternateIdentityMapper.getAlternateIds(subscriberId);
            String externalId = alternateIds.byAlternateId(subscriberExternalAlternateId).getAlternateId();
            assertEquals(externalId, subscriberExternalAlternateId);
            assertEquals(AlternateIdType.SPR.name(), alternateIds.getSprTypeAlternateId().getType());
        }
    }



    public class removeMappingShouldBe {

        @Before
        public void removed() throws OperationFailedException {
            removeMapping();
        }

        @Test
        public void fromDB() throws OperationFailedException {

            String expectedSubscriberId = alternateIdentityMapper.getSubscriberId(alternateId);
            assertEquals(null, expectedSubscriberId);
        }
    }


    public class AddExternalMapping {


        public class ThrowsOperationFailedExceptionWhen {

            @Test
            public void transactionFactoryIsNotAlive() throws OperationFailedException {

                doReturn(false).when(transactionFactory).isAlive();

                exception.expect(OperationFailedException.class);
                exception.expectMessage("Datasource not available");

                addExternalMapping();
            }

            @Test(expected = OperationFailedException.class)
            public void preparedStatementCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(PREPARED_STATEMENT);

                addExternalMapping();
            }

            @Test(expected = OperationFailedException.class)
            public void connectionNotFound() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(CONNECTION_NOT_FOUND);

                addExternalMapping();
            }

            @Test(expected = OperationFailedException.class)
            public void commitCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(COMMIT);

                addExternalMapping();
            }

            @Test(expected = OperationFailedException.class)
            public void transactionIsNull() throws TransactionException, OperationFailedException {
                doReturn(null).when(transactionFactory).createTransaction();
                addExternalMapping();

            }

            @Test(expected = OperationFailedException.class)
            public void externalIdCountIncreasesToMaximumLimit() throws OperationFailedException {
                for (int i = 0; i <= CommonConstants.MAX_EXTERNAL_ALTERNATE_ID_LIMIT; i++) {
                    alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, UUID.randomUUID().toString());
                }
                alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, UUID.randomUUID().toString());
            }


            @Test(expected = OperationFailedException.class)
            public void tryingToAddDuplicateExternalMapping() throws OperationFailedException {
                String alternateId = UUID.randomUUID().toString();
                alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, alternateId);
                alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, alternateId);
            }

        }

        @Test
        public void addExternalMappingAndmethodForCreatingEDRforAddExternalAlternateIdOperationIsCalledOnce() throws OperationFailedException {
            String alternateId = UUID.randomUUID().toString();
            alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, alternateId);
            assertEquals(subscriberId, alternateIdentityMapper.getSubscriberId(alternateId));
            verify(externalAlternateIdEDRListener, atMost(1)).addExternalAlternateIdEDR(subscriberId, alternateId, CommonConstants.ADD_EXTERNAL_ALTERNATE_ID);
        }

        @Test
        public void externalMappingShouldAddExternalAndActiveStatusTypeAlternateIdAndmethodForCreatingEDRforAddExternalAlternateIdOperationIsCalledOnce() throws OperationFailedException {
            String alternateId = UUID.randomUUID().toString();
            alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, alternateId);
            SubscriberAlternateIds alternateIds = alternateIdentityMapper.getAlternateIds(subscriberId);
            assertEquals(AlternateIdType.EXTERNAL.name(), alternateIds.byAlternateId(alternateId).getType());
            assertEquals(CommonConstants.STATUS_ACTIVE, alternateIds.byAlternateId(alternateId).getStatus());
            verify(externalAlternateIdEDRListener, atMost(1)).addExternalAlternateIdEDR(subscriberId, alternateId, CommonConstants.ADD_EXTERNAL_ALTERNATE_ID);
        }
    }

    public class UpdateExternalMapping {

        public class ThrowsOperationFailedExceptionWhen {

            @Test
            public void transactionFactoryIsNotAlive() throws OperationFailedException {

                doReturn(false).when(transactionFactory).isAlive();
                exception.expect(OperationFailedException.class);
                exception.expectMessage("Datasource not available");
                alternateIdentityMapper.updateExternalAlternateIdentity(subscriberId, alternateId, UUID.randomUUID().toString(), CommonConstants.STATUS_ACTIVE);

            }

            @Test(expected = OperationFailedException.class)
            public void preparedStatementCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(PREPARED_STATEMENT);

                alternateIdentityMapper.updateExternalAlternateIdentity(subscriberId, alternateId, UUID.randomUUID().toString(), CommonConstants.STATUS_ACTIVE);
            }

            @Test(expected = OperationFailedException.class)
            public void connectionNotFound() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(CONNECTION_NOT_FOUND);

                alternateIdentityMapper.updateExternalAlternateIdentity(subscriberId, alternateId, UUID.randomUUID().toString(), CommonConstants.STATUS_ACTIVE);
            }

            @Test(expected = OperationFailedException.class)
            public void commitCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(COMMIT);

                alternateIdentityMapper.updateExternalAlternateIdentity(subscriberId, alternateId, UUID.randomUUID().toString(), CommonConstants.STATUS_ACTIVE);
            }

            @Test(expected = OperationFailedException.class)
            public void transactionIsNull() throws TransactionException, OperationFailedException {
                doReturn(null).when(transactionFactory).createTransaction();
                alternateIdentityMapper.updateExternalAlternateIdentity(subscriberId, alternateId, UUID.randomUUID().toString(), CommonConstants.STATUS_ACTIVE);

            }
        }

        @Test
        public void updateExternalIdShouldUpdateExternalIdOnlyAndmethodForCreatingEDRforUpdateExternalAlternateIdOperationIsCalledOnce() throws OperationFailedException {
            String sprAlternateId = UUID.randomUUID().toString();
            String externalAlternateId = UUID.randomUUID().toString();
            String updatedExternalAlternateId = UUID.randomUUID().toString();
            alternateIdentityMapper.addMapping(subscriberId, alternateId);
            alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, externalAlternateId);
            assertEquals(0, alternateIdentityMapper.updateExternalAlternateIdentity(subscriberId, sprAlternateId, updatedExternalAlternateId, CommonConstants.STATUS_ACTIVE));
            verify(externalAlternateIdEDRListener, atMost(1)).updateExternalAlternateIdEDR(subscriberId, externalAlternateId, updatedExternalAlternateId, CommonConstants.STATUS_ACTIVE, "", CommonConstants.UPDATE_EXTERNAL_ALTERNATE_ID_EDR);
        }

        @Test
        public void updateExternalIdShouldUpdateExternalIdAndmethodForCreatingEDRforUpdateExternalAlternateIdOperationIsCalledOnce() throws OperationFailedException {
            String externalAlternateId = UUID.randomUUID().toString();
            String updatedExternalAlternateId = UUID.randomUUID().toString();
            alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, externalAlternateId);
            assertEquals(1, alternateIdentityMapper.updateExternalAlternateIdentity(subscriberId, externalAlternateId, updatedExternalAlternateId, CommonConstants.STATUS_ACTIVE));
            assertEquals(subscriberId, alternateIdentityMapper.getSubscriberId(updatedExternalAlternateId));
            assertNull(alternateIdentityMapper.getSubscriberId(externalAlternateId));
            verify(externalAlternateIdEDRListener, atMost(1)).updateExternalAlternateIdEDR(subscriberId, externalAlternateId, updatedExternalAlternateId, CommonConstants.STATUS_ACTIVE, "", CommonConstants.UPDATE_EXTERNAL_ALTERNATE_ID_EDR);
        }
    }


    public class ChangeExternalAlternateIdentityStatus {

        public class ThrowsOperationFailedExceptionWhen {

            @Test
            public void transactionFactoryIsNotAlive() throws OperationFailedException {

                doReturn(false).when(transactionFactory).isAlive();
                exception.expect(OperationFailedException.class);
                exception.expectMessage("Datasource not available");
                alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId, alternateId, CommonConstants.STATUS_INACTIVE, CommonConstants.STATUS_ACTIVE);

            }

            @Test(expected = OperationFailedException.class)
            public void preparedStatementCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(PREPARED_STATEMENT);

                alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId, alternateId, CommonConstants.STATUS_INACTIVE, CommonConstants.STATUS_ACTIVE);
            }

            @Test(expected = OperationFailedException.class)
            public void connectionNotFound() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(CONNECTION_NOT_FOUND);

                alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId, alternateId, CommonConstants.STATUS_INACTIVE, CommonConstants.STATUS_ACTIVE);
            }

            @Test(expected = OperationFailedException.class)
            public void commitCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(COMMIT);

                alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId, alternateId, CommonConstants.STATUS_INACTIVE, CommonConstants.STATUS_ACTIVE);
            }

            @Test(expected = OperationFailedException.class)
            public void transactionIsNull() throws TransactionException, OperationFailedException {
                doReturn(null).when(transactionFactory).createTransaction();
                alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId, alternateId, CommonConstants.STATUS_INACTIVE, CommonConstants.STATUS_ACTIVE);
            }
        }

        @Test
        public void shouldChangeExternalIdStatusOnly() throws OperationFailedException {
            String sprAlternateId = UUID.randomUUID().toString();
            alternateIdentityMapper.addMapping(subscriberId, alternateId);
            assertEquals(0, alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId, alternateId, CommonConstants.STATUS_ACTIVE, CommonConstants.STATUS_INACTIVE));
            verify(externalAlternateIdEDRListener, atMost(1)).updateExternalAlternateIdEDR(subscriberId, alternateId, "", CommonConstants.STATUS_ACTIVE,
                    CommonConstants.STATUS_INACTIVE, CommonConstants.UPDATE_EXTERNAL_ALTERNATE_ID_EDR);
        }

        @Test
        public void shouldChangeExternalIdStatus() throws OperationFailedException {
            String externalAlternateId = UUID.randomUUID().toString();
            alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, externalAlternateId);
            assertEquals(1, alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId, externalAlternateId, CommonConstants.STATUS_ACTIVE, CommonConstants.STATUS_INACTIVE));
            SubscriberAlternateIdStatusDetail alternateIdStatusDetail = alternateIdentityMapper.getAlternateIds(subscriberId).byAlternateId(externalAlternateId);
            assertEquals(AlternateIdType.EXTERNAL.name(), alternateIdStatusDetail.getType());
            assertEquals(CommonConstants.STATUS_INACTIVE, alternateIdStatusDetail.getStatus());
            verify(externalAlternateIdEDRListener, atMost(1)).updateExternalAlternateIdEDR(subscriberId, alternateId, "", CommonConstants.STATUS_ACTIVE,
                    CommonConstants.STATUS_INACTIVE, CommonConstants.UPDATE_EXTERNAL_ALTERNATE_ID_EDR);
        }
    }


    public class RemoveAlternateIdMappingByType {

        public class ThrowsOperationFailedExceptionWhen {

            @Test
            public void transactionFactoryIsNotAlive() throws OperationFailedException {
                doReturn(false).when(transactionFactory).isAlive();
                exception.expect(OperationFailedException.class);
                exception.expectMessage("Datasource not available");
                alternateIdentityMapper.removeAlternateIdMappingByType(subscriberId, alternateId, AlternateIdType.SPR, CommonConstants.STATUS_ACTIVE);
            }

            @Test(expected = OperationFailedException.class)
            public void preparedStatementCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(PREPARED_STATEMENT);

                alternateIdentityMapper.removeAlternateIdMappingByType(subscriberId, alternateId, AlternateIdType.EXTERNAL, CommonConstants.STATUS_ACTIVE);
            }

            @Test(expected = OperationFailedException.class)
            public void connectionNotFound() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(CONNECTION_NOT_FOUND);

                alternateIdentityMapper.removeAlternateIdMappingByType(subscriberId, alternateId, AlternateIdType.EXTERNAL, CommonConstants.STATUS_ACTIVE);
            }

            @Test(expected = OperationFailedException.class)
            public void commitCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(COMMIT);

                alternateIdentityMapper.removeAlternateIdMappingByType(subscriberId, alternateId, AlternateIdType.EXTERNAL, CommonConstants.STATUS_ACTIVE);
            }

            @Test(expected = OperationFailedException.class)
            public void transactionIsNull() throws TransactionException, OperationFailedException {
                doReturn(null).when(transactionFactory).createTransaction();
                alternateIdentityMapper.removeAlternateIdMappingByType(subscriberId, alternateId, AlternateIdType.EXTERNAL, CommonConstants.STATUS_ACTIVE);
            }
        }

        @Test
        public void shouldRemoveGivenTypeOfAlternateIdAndmethodForCreatingEDRforRemoveExternalAlternateIdOperationIsCalledOnce() throws OperationFailedException {
            alternateIdentityMapper.addMapping(subscriberId, alternateId);
            String externalId = UUID.randomUUID().toString();
            alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, externalId);
            alternateIdentityMapper.removeAlternateIdMappingByType(subscriberId, alternateId, AlternateIdType.SPR, CommonConstants.STATUS_ACTIVE);
            assertNull(alternateIdentityMapper.getSubscriberId(alternateId));
            assertEquals(subscriberId, alternateIdentityMapper.getSubscriberId(externalId));
            verify(externalAlternateIdEDRListener, atMost(1)).removeExternalAlternateIdEDR(subscriberId, alternateId, CommonConstants.STATUS_ACTIVE, CommonConstants.REMOVE_EXTERNAL_ALTERNATE_ID);
        }

        @Test
        public void shouldNotRemoveNonExistAlternateIdAndmethodForCreatingEDRforRemoveExternalAlternateIdOperationIsNeverCalled() throws OperationFailedException {
            alternateIdentityMapper.addMapping(subscriberId, alternateId);
            String externalId = UUID.randomUUID().toString();
            String unknownId = UUID.randomUUID().toString();
            alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, externalId);
            assertEquals(0, alternateIdentityMapper.removeAlternateIdMappingByType(subscriberId, unknownId, AlternateIdType.EXTERNAL, CommonConstants.STATUS_ACTIVE));
            verify(externalAlternateIdEDRListener, never()).removeExternalAlternateIdEDR(subscriberId, alternateId, CommonConstants.STATUS_ACTIVE, CommonConstants.REMOVE_EXTERNAL_ALTERNATE_ID);
        }
    }


    public class GetAlternateIds {

        public class ThrowsOperationFailedExceptionWhen {

            @Test
            public void transactionFactoryIsNotAlive() throws OperationFailedException {
                doReturn(false).when(transactionFactory).isAlive();
                exception.expect(OperationFailedException.class);
                exception.expectMessage("Datasource not available");
                alternateIdentityMapper.getAlternateIds(subscriberId);
            }

            @Test(expected = OperationFailedException.class)
            public void preparedStatementCall() throws TransactionException, OperationFailedException {
                setUpDBTransactionFailedException(PREPARED_STATEMENT);
                alternateIdentityMapper.getAlternateIds(subscriberId);
            }

            @Test(expected = OperationFailedException.class)
            public void connectionNotFound() throws TransactionException, OperationFailedException {
                setUpDBTransactionFailedException(CONNECTION_NOT_FOUND);
                alternateIdentityMapper.getAlternateIds(subscriberId);
            }

            @Test(expected = OperationFailedException.class)
            public void transactionIsNull() throws TransactionException, OperationFailedException {
                doReturn(null).when(transactionFactory).createReadOnlyTransaction();
                alternateIdentityMapper.getAlternateIds(subscriberId);
            }


        }

        @Test
        public void returnNullIfAlternatIdDoesNotExist() throws OperationFailedException {
            assertNull(alternateIdentityMapper.getAlternateIds(subscriberId));
        }

        @Test
        public void returnNullIfNonExistSubscriberIdCalled() throws OperationFailedException {
            alternateIdentityMapper.addMapping(subscriberId, alternateId);
            String externalUUID = UUID.randomUUID().toString();
            alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, externalUUID);
            String unknownSubscriber = UUID.randomUUID().toString();
            assertNull(alternateIdentityMapper.getAlternateIds(unknownSubscriber));
        }

        @Test
        public void returnSubscriberAlternateIdIfExist() throws OperationFailedException {
            alternateIdentityMapper.addMapping(subscriberId, alternateId);
            String externalUUID = UUID.randomUUID().toString();
            alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, externalUUID);
            SubscriberAlternateIds subscriberAlternateIds = alternateIdentityMapper.getAlternateIds(subscriberId);
            assertNotNull(subscriberAlternateIds);
            assertEquals(alternateId, subscriberAlternateIds.getSprTypeAlternateId().getAlternateId());
            assertNotNull(subscriberAlternateIds.byAlternateId(externalUUID));
        }

    }

    public class GetSubscriberId {


        public class ThrowsOperationFailedExceptionWhen {

            @Test
            public void transactionFactoryIsNotAlive() throws OperationFailedException {
                doReturn(false).when(transactionFactory).isAlive();
                exception.expect(OperationFailedException.class);
                exception.expectMessage("Datasource not available");
                alternateIdentityMapper.getSubscriberId(alternateId);
            }

            @Test(expected = OperationFailedException.class)
            public void preparedStatementCall() throws TransactionException, OperationFailedException {
                setUpDBTransactionFailedException(PREPARED_STATEMENT);
                alternateIdentityMapper.getSubscriberId(alternateId);
            }

            @Test(expected = OperationFailedException.class)
            public void connectionNotFound() throws TransactionException, OperationFailedException {
                setUpDBTransactionFailedException(CONNECTION_NOT_FOUND);
                alternateIdentityMapper.getSubscriberId(alternateId);
            }

            @Test(expected = OperationFailedException.class)
            public void transactionIsNull() throws TransactionException, OperationFailedException {
                doReturn(null).when(transactionFactory).createReadOnlyTransaction();
                alternateIdentityMapper.getSubscriberId(alternateId);
            }
        }

        @Test
        public void willAlwaysReturnActiveSubscriber() throws OperationFailedException {
            addExternalMapping();
            alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId, subscriberExternalAlternateId, CommonConstants.STATUS_ACTIVE, CommonConstants.STATUS_INACTIVE);
            assertNull(alternateIdentityMapper.getSubscriberId(alternateId));
        }

        @Test
        public void willReturnAllIfPassedWithTruePredicate() throws OperationFailedException {
            addExternalMapping();
            alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId, subscriberExternalAlternateId, CommonConstants.STATUS_ACTIVE, CommonConstants.STATUS_INACTIVE);
            String expectedSubscriberId = alternateIdentityMapper.getSubscriberId(subscriberExternalAlternateId, status -> true);
            assertNotNull(expectedSubscriberId);
            assertEquals(expectedSubscriberId, subscriberId);
        }
    }


    public class GetAlternateId {

        public class ThrowsOperationFailedExceptionWhen {

            @Test
            public void transactionFactoryIsNotAlive() throws OperationFailedException {
                doReturn(false).when(transactionFactory).isAlive();
                exception.expect(OperationFailedException.class);
                exception.expectMessage("Datasource not available");
                alternateIdentityMapper.getAlternateId(subscriberId);
            }

            @Test(expected = OperationFailedException.class)
            public void preparedStatementCall() throws TransactionException, OperationFailedException {
                setUpDBTransactionFailedException(PREPARED_STATEMENT);
                alternateIdentityMapper.getAlternateId(subscriberId);
            }

            @Test(expected = OperationFailedException.class)
            public void connectionNotFound() throws TransactionException, OperationFailedException {
                setUpDBTransactionFailedException(CONNECTION_NOT_FOUND);
                alternateIdentityMapper.getAlternateId(subscriberId);
            }

            @Test(expected = OperationFailedException.class)
            public void transactionIsNull() throws TransactionException, OperationFailedException {
                doReturn(null).when(transactionFactory).createReadOnlyTransaction();
                alternateIdentityMapper.getAlternateId(subscriberId);
            }
        }

        @Test
        public void willAlwaysReturnSPRTypeOfAlternateId() throws OperationFailedException {
            addExternalMapping();
            assertNull(alternateIdentityMapper.getAlternateId(subscriberId));
            addMapping();
            String expectedAlternateId = alternateIdentityMapper.getAlternateId(subscriberId);
            assertNotNull(expectedAlternateId);
            assertEquals(expectedAlternateId, alternateId);
        }

        @Test
        public void willReturnAllIfPassedWithTruePredicate() throws OperationFailedException {
            addExternalMapping();
            alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId, subscriberExternalAlternateId, CommonConstants.STATUS_ACTIVE, CommonConstants.STATUS_INACTIVE);
            String expectedSubscriberId = alternateIdentityMapper.getSubscriberId(subscriberExternalAlternateId, status -> true);
            assertNotNull(expectedSubscriberId);
            assertEquals(expectedSubscriberId, subscriberId);
        }
    }

    public class ReplaceMapping {


        public class ThrowsOperationFailedExceptionWhen {

            @Test
            public void transactionFactoryIsNotAlive() throws OperationFailedException {
                doReturn(false).when(transactionFactory).isAlive();
                exception.expect(OperationFailedException.class);
                exception.expectMessage("Datasource not available");
                alternateIdentityMapper.replaceMapping(subscriberId, updatedSubscriberId, alternateId);
            }

            @Test(expected = OperationFailedException.class)
            public void preparedStatementCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(PREPARED_STATEMENT);

                alternateIdentityMapper.replaceMapping(subscriberId, updatedSubscriberId, alternateId);
            }

            @Test(expected = OperationFailedException.class)
            public void connectionNotFound() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(CONNECTION_NOT_FOUND);

                alternateIdentityMapper.replaceMapping(subscriberId, updatedSubscriberId, alternateId);
            }

            @Test(expected = OperationFailedException.class)
            public void commitCall() throws TransactionException, OperationFailedException {

                setUpTransactionFailedException(COMMIT);

                alternateIdentityMapper.replaceMapping(subscriberId, updatedSubscriberId, alternateId);
            }

            @Test(expected = OperationFailedException.class)
            public void transactionIsNull() throws TransactionException, OperationFailedException {
                doReturn(null).when(transactionFactory).createTransaction();
                alternateIdentityMapper.replaceMapping(subscriberId, updatedSubscriberId, alternateId);
            }
        }

        @Test
        public void shouldMigrateToNewSubscriberIdentity() throws OperationFailedException{
            addMapping();
            addExternalMapping();
            String updatedAlternateId = UUID.randomUUID().toString();
            alternateIdentityMapper.replaceMapping(subscriberId,updatedSubscriberId,updatedAlternateId);
            assertNull(alternateIdentityMapper.getAlternateIds(subscriberId));
            SubscriberAlternateIds updatedSubscriberAlternateIds = alternateIdentityMapper.getAlternateIds(updatedSubscriberId);
            assertEquals(updatedAlternateId,updatedSubscriberAlternateIds.getSprTypeAlternateId().getAlternateId());
            assertEquals(subscriberExternalAlternateId,updatedSubscriberAlternateIds.byAlternateId(subscriberExternalAlternateId).getAlternateId());
        }

        @Test
        public void shouldMigrateWhenAlternateIdIsNull() throws OperationFailedException{
            addMapping();
            addExternalMapping();
            alternateIdentityMapper.replaceMapping(subscriberId,updatedSubscriberId,null);
            assertNull(alternateIdentityMapper.getAlternateIds(subscriberId));
            SubscriberAlternateIds updatedSubscriberAlternateIds = alternateIdentityMapper.getAlternateIds(updatedSubscriberId);
            assertNull(updatedSubscriberAlternateIds.getSprTypeAlternateId());
            assertEquals(subscriberExternalAlternateId,updatedSubscriberAlternateIds.byAlternateId(subscriberExternalAlternateId).getAlternateId());
        }

        @Test
        public void shouldMaintainAlternateIdStatusWhileMigratingTONewSubscriberIdentity() throws OperationFailedException{
            addMapping();
            addExternalMapping();
            alternateIdentityMapper.changeExternalAlternateIdentityStatus(subscriberId,subscriberExternalAlternateId, CommonConstants.STATUS_ACTIVE, CommonConstants.STATUS_INACTIVE);
            String updatedAlternateId = UUID.randomUUID().toString();
            alternateIdentityMapper.replaceMapping(subscriberId,updatedSubscriberId,updatedAlternateId);
            SubscriberAlternateIds updatedSubscriberAlternateIds = alternateIdentityMapper.getAlternateIds(updatedSubscriberId);
            assertEquals(CommonConstants.STATUS_INACTIVE,updatedSubscriberAlternateIds.byAlternateId(subscriberExternalAlternateId).getStatus());
        }

        @Test
        public void providedAlternateWillAlwaysBeOfTypeSPRAndActiveStatus() throws OperationFailedException{
            addMapping();
            addExternalMapping();
            String updatedAlternateId = UUID.randomUUID().toString();
            alternateIdentityMapper.replaceMapping(subscriberId,updatedSubscriberId,updatedAlternateId);
            SubscriberAlternateIds updatedSubscriberAlternateIds = alternateIdentityMapper.getAlternateIds(updatedSubscriberId);
            assertEquals(AlternateIdType.SPR.name(),updatedSubscriberAlternateIds.byAlternateId(updatedAlternateId).getType());
            assertEquals(CommonConstants.STATUS_ACTIVE,updatedSubscriberAlternateIds.byAlternateId(updatedAlternateId).getStatus());
        }


    }


    private void addExternalMapping() throws OperationFailedException {
        getLogger().debug("subscriber id", subscriberId);
        getLogger().debug("alternate id", subscriberExternalAlternateId);

        alternateIdentityMapper.addExternalAlternateIdMapping(subscriberId, subscriberExternalAlternateId);
    }

    private void addMapping() throws OperationFailedException {
        getLogger().debug("subscriber id", subscriberId);
        getLogger().debug("alternate id", alternateId);

        alternateIdentityMapper.addMapping(subscriberId, alternateId);
    }

    private void updateMapping() throws OperationFailedException {
        alternateId = UUID.randomUUID().toString();

        getLogger().debug("Updated Alternate Id", alternateId);
        alternateIdentityMapper.updateMapping(subscriberId, alternateId);
    }

    private void removeMapping() throws OperationFailedException {

        getLogger().debug("Remove Subscriber Id", subscriberId);
        alternateIdentityMapper.removeMapping(subscriberId);
    }


    private void setUpTransactionFailedException(String on) throws TransactionException {

        Transaction transaction = spy(new DummyTransaction(mock(Connection.class), null));

        doReturn(transaction).when(transactionFactory).createTransaction();
        doReturn(true).when(transactionFactory).isAlive();
        when(transactionFactory.createTransaction()).thenReturn(transaction);

        if (on.equals(PREPARED_STATEMENT)) {
            doThrow(new TransactionException("Test Exception")).when(transaction).prepareStatement(Mockito.anyString());
        } else if (on.equals(BEGIN)) {
            doThrow(new TransactionException("Test Exception")).when(transaction).begin();
        } else if (on.equals(COMMIT)) {
            doThrow(new TransactionException("Test Exception")).when(transaction).commit();
        } else if (on.equals(CONNECTION_NOT_FOUND)) {
            doThrow(new TransactionException("Test Exception", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
        }
    }

    private void setUpDBTransactionFailedException(String on) throws TransactionException {
        Transaction transaction = spy(new DummyTransaction(mock(Connection.class), null));
        doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
        doReturn(true).when(transactionFactory).isAlive();
        when(transactionFactory.createTransaction()).thenReturn(transaction);

        if (on.equals(PREPARED_STATEMENT)) {
            doThrow(new TransactionException("Test Exception")).when(transaction).prepareStatement(Mockito.anyString());
        } else if (on.equals(BEGIN)) {
            doThrow(new TransactionException("Test Exception")).when(transaction).begin();
        } else if (on.equals(COMMIT)) {
            doThrow(new TransactionException("Test Exception")).when(transaction).commit();
        } else if (on.equals(CONNECTION_NOT_FOUND)) {
            doThrow(new TransactionException("Test Exception", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
        }
    }
}


