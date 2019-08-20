package com.elitecore.corenetvertex.spr.ddf;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.AlternateIdentityMapper;
import com.elitecore.corenetvertex.spr.SPRProvider;
import com.elitecore.corenetvertex.spr.SubscriberAlternateIdStatusDetail;
import com.elitecore.corenetvertex.spr.SubscriberAlternateIds;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class DDFTableImplTest {

    private String subscriberIdentityVal = UUID.randomUUID().toString();
    private String alternateIdentityVal = UUID.randomUUID().toString();

    private DDFTableImpl ddfTable;

    private String newSubscriberId = UUID.randomUUID().toString();
    private String externalAlternateId = UUID.randomUUID().toString();
    private DummyTransactionFactory transactionFactory;

    private AlternateIdentityMapper mapper;
    @Mock
    SubscriberRepository subscriberRepository;
    @Mock
    PolicyRepository policyRepository;
    @Mock
    ProductOfferStore productOfferStore;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private HibernateSessionFactory hibernateSessionFactory;

    @Mock
    private StaffData staffData;

    @Before
    public void setUp() throws OperationFailedException {
        MockitoAnnotations.initMocks(this);
        SPRProvider sprProvider = mock(SPRProvider.class);
        when(sprProvider.getDefaultRepository()).thenReturn(subscriberRepository);

        String ssid = UUID.randomUUID().toString();
        transactionFactory = DummyTransactionFactory.createTransactionFactory(ssid);
        mapper = spy(new AlternateIdentityMapper(transactionFactory , null));
        hibernateSessionFactory = HibernateSessionFactory.newInMemorySessionFactory(ssid);

        RepositorySelector repositorySelector = RepositorySelector.create(null, sprProvider);
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
        ddfTable = new DDFTableImpl(null, policyRepository, sprProvider, mapper, repositorySelector);
    }

    private SPRInfoImpl getExpectedSPRInfo() {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentityVal);
        return sprInfo;
    }

    @After
    public void tearDown() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if (Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        hibernateSessionFactory.shutdown();
    }


    public class AddExternalId {

        @Test(expected = UnauthorizedActionException.class)
        public void throwsUnAuthorizedActionWhenUserNotAuthorizeForUpdateSubscriberProfileAction() throws OperationFailedException, UnauthorizedActionException {
            when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(false);
            ddfTable.addExternalAlternateId(subscriberIdentityVal, alternateIdentityVal, staffData);
        }

        public class ThrowsOperationFailedExceptionWhen {

            @Test(expected = OperationFailedException.class)
            public void alternateIdentityAlreadyExist() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                mapper.addExternalAlternateIdMapping(subscriberIdentityVal, externalAlternateId);
                ddfTable.addExternalAlternateId(subscriberIdentityVal, externalAlternateId, staffData);
            }

            @Test(expected = OperationFailedException.class)
            public void alternateIdentityAlreadyExistWithDifferentSubscriber() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                mapper.addExternalAlternateIdMapping(newSubscriberId, externalAlternateId);
                ddfTable.addExternalAlternateId(subscriberIdentityVal, externalAlternateId, staffData);
            }
        }

        @Test
        public void shouldAddExternalAlternateId() throws OperationFailedException, UnauthorizedActionException {
            when(staffData.isAccessibleAction(anyList(), any(), any())).thenReturn(true);
            ddfTable.addExternalAlternateId(subscriberIdentityVal, alternateIdentityVal, staffData);
            SubscriberAlternateIds externalAlternateIds = ddfTable.getExternalAlternateIds(subscriberIdentityVal, staffData);
            assertNotNull(externalAlternateIds);
            assertEquals(alternateIdentityVal, externalAlternateIds.byAlternateId(alternateIdentityVal).getAlternateId());
        }

    }


    public class RemoveExternalAlternateId {


        public class ThrowsOperationFailedExceptionWhen {

            @Test(expected = OperationFailedException.class)
            public void alternateIdentityDoesNotExist() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                mapper.addExternalAlternateIdMapping(subscriberIdentityVal, UUID.randomUUID().toString());
                ddfTable.removeExternalAlternateId(subscriberIdentityVal, alternateIdentityVal, staffData);
            }

            @Test(expected = OperationFailedException.class)
            public void alternateIdentityAlreadyExistWithDifferentSubscriber() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                mapper.addExternalAlternateIdMapping(subscriberIdentityVal, externalAlternateId);
                ddfTable.removeExternalAlternateId(UUID.randomUUID().toString(), externalAlternateId, staffData);
            }
        }

        @Test(expected = UnauthorizedActionException.class)
        public void throwsUnAuthorizedActionWhenUserNotAuthorizeForUpdateSubscriberProfileAction() throws OperationFailedException, UnauthorizedActionException {
            when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(false);
            ddfTable.removeExternalAlternateId(subscriberIdentityVal, alternateIdentityVal, staffData);
        }

        @Test
        public void shouldRemoveAlternateId() throws OperationFailedException, UnauthorizedActionException {
            when(staffData.isAccessibleAction(anyList(), any(), any())).thenReturn(true);
            ddfTable.addExternalAlternateId(subscriberIdentityVal, alternateIdentityVal, staffData);
            ddfTable.removeExternalAlternateId(subscriberIdentityVal, alternateIdentityVal, staffData);
            SubscriberAlternateIds externalAlternateIds = ddfTable.getExternalAlternateIds(subscriberIdentityVal, staffData);
            assertNull(ddfTable.getSubscriberIdForAlternateId(alternateIdentityVal));
            assertNull(externalAlternateIds);
        }
    }

    public class MigrateSubscriberAlternateIds {

        private SPRInfoImpl createSPRInfo() {
            SPRInfoImpl sprInfo = new SPRInfoImpl();
            sprInfo.setSubscriberIdentity(subscriberIdentityVal);
            sprInfo.setUserName("newUsername");
            return sprInfo;
        }

        private ProductOffer createProductOffer(){
            return new ProductOffer("base_offer", "base_offer", null, PkgType.BASE, PkgMode.TEST,
                    30, ValidityPeriodUnit.DAY, 0.0, 0.0,
            PkgStatus.ACTIVE, new ArrayList<>(), new ArrayList<>(), null, staffData.getGroupList(),
                    null, null, PolicyStatus.SUCCESS, null, null,
                    false, null, null, policyRepository, null,
                    null, null,"INR");
        }

        @Test
        public void whenAlternateIdFieldIsSetInSPRRepositoryThenDoMigrateAlternateId() throws OperationFailedException, UnauthorizedActionException {
            ddfTable = spy(ddfTable);
            SPRInfo sprInfo = createSPRInfo();
            sprInfo.setProductOffer("base_offer");
            doReturn(sprInfo).when(ddfTable).searchSubscriber(subscriberIdentityVal);
            ProductOffer po = createProductOffer();
            when(productOfferStore.byName("base_offer")).thenReturn(po);
            when(staffData.isAccessibleAction(anyList(), any(), any())).thenReturn(true);
            when(subscriberRepository.getAlternateIdField()).thenReturn(SPRFields.USERNAME);
            ddfTable.migrateSubscriber(subscriberIdentityVal, newSubscriberId, staffData,null);
            verify(mapper,times(1)).replaceMapping(subscriberIdentityVal,newSubscriberId,"newUsername");
        }

        @Test
        public void whenAlternateIdFieldIsNotSetInSPRRepositoryThenDoNotMigrateAlternateId() throws OperationFailedException, UnauthorizedActionException {
            ddfTable = spy(ddfTable);
            SPRInfo sprInfo = createSPRInfo();
            sprInfo.setProductOffer("base_offer");
            doReturn(sprInfo).when(ddfTable).searchSubscriber(subscriberIdentityVal);
            ProductOffer po = createProductOffer();
            when(productOfferStore.byName("base_offer")).thenReturn(po);
            when(staffData.isAccessibleAction(anyList(), any(), any())).thenReturn(true);
            ddfTable.migrateSubscriber(subscriberIdentityVal, newSubscriberId, staffData,null);
            verify(mapper,times(1)).replaceMapping(subscriberIdentityVal,newSubscriberId,null);
        }

    }

    public class UpdateExternalAlternateIds {

        public class ThrowsOperationFailedExceptionWhen {

            @Test(expected = OperationFailedException.class)
            public void oldAlternateIdentityDoesNotExist() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                mapper.addExternalAlternateIdMapping(subscriberIdentityVal, alternateIdentityVal);
                ddfTable.updateExternalAlternateId(subscriberIdentityVal, UUID.randomUUID().toString(), UUID.randomUUID().toString(), staffData);
            }

            @Test(expected = OperationFailedException.class)
            public void oldAlternateIdentityAlreadyExistWithDifferentSubscriber() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                mapper.addExternalAlternateIdMapping(UUID.randomUUID().toString(), externalAlternateId);
                ddfTable.updateExternalAlternateId(subscriberIdentityVal, externalAlternateId, UUID.randomUUID().toString(), staffData);
            }

            @Test(expected = OperationFailedException.class)
            public void newAlternateIdentityAlreadyExistForSubscriber() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                mapper.addExternalAlternateIdMapping(subscriberIdentityVal, externalAlternateId);
                ddfTable.updateExternalAlternateId(subscriberIdentityVal, externalAlternateId, externalAlternateId, staffData);
            }

            @Test(expected = OperationFailedException.class)
            public void newAlternateIdentityAlreadyExistWithDifferentSubscriber() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                String updatedAlternateId = UUID.randomUUID().toString();
                mapper.addExternalAlternateIdMapping(subscriberIdentityVal, alternateIdentityVal);
                mapper.addExternalAlternateIdMapping(UUID.randomUUID().toString(), updatedAlternateId);
                ddfTable.updateExternalAlternateId(subscriberIdentityVal, alternateIdentityVal, updatedAlternateId, staffData);
            }
        }

        @Test(expected = UnauthorizedActionException.class)
        public void throwsUnAuthorizedActionWhenUserNotAuthorizeForUpdateSubscriberProfileAction() throws OperationFailedException, UnauthorizedActionException {
            when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(false);
            ddfTable.updateExternalAlternateId(subscriberIdentityVal, UUID.randomUUID().toString(), UUID.randomUUID().toString(), staffData);
        }


        @Test
        public void shouldUpdateAlternateId() throws OperationFailedException, UnauthorizedActionException {
            when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
            String updatedAlternateId = UUID.randomUUID().toString();
            mapper.addExternalAlternateIdMapping(subscriberIdentityVal, alternateIdentityVal);
            ddfTable.updateExternalAlternateId(subscriberIdentityVal, alternateIdentityVal, updatedAlternateId, staffData);
            assertNull(ddfTable.getSubscriberIdForAlternateId(alternateIdentityVal));
            assertSame(subscriberIdentityVal, ddfTable.getSubscriberIdForAlternateId(updatedAlternateId));
        }
    }

    public class ChangeAlternateIdentityStatus {

        @Test(expected = UnauthorizedActionException.class)
        public void throwsUnAuthorizedActionWhenUserNotAuthorizeForUpdateSubscriberProfileAction() throws OperationFailedException, UnauthorizedActionException {
            when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(false);
            ddfTable.changeAlternateIdentityStatus(subscriberIdentityVal, UUID.randomUUID().toString(), UUID.randomUUID().toString(), staffData);
        }

        public class ThrowsOperationFailedException {

            @Test(expected = OperationFailedException.class)
            public void alternateIdentityDoesNotExist() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                mapper.addExternalAlternateIdMapping(subscriberIdentityVal, UUID.randomUUID().toString());
                ddfTable.changeAlternateIdentityStatus(subscriberIdentityVal, alternateIdentityVal, UUID.randomUUID().toString(), staffData);
            }

            @Test(expected = OperationFailedException.class)
            public void alternateIdentityAlreadyExistWithDifferentSubscriber() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                mapper.addExternalAlternateIdMapping(UUID.randomUUID().toString(), externalAlternateId);
                ddfTable.changeAlternateIdentityStatus(subscriberIdentityVal, alternateIdentityVal, UUID.randomUUID().toString(), staffData);
            }

            @Test(expected = OperationFailedException.class)
            public void alternateIdentityAlreadyInProvidedStatus() throws OperationFailedException, UnauthorizedActionException {
                when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.UPDATE_SUBSCRIBER))).thenReturn(true);
                mapper.addExternalAlternateIdMapping(subscriberIdentityVal, externalAlternateId);
                SubscriberAlternateIdStatusDetail subscriberAlternateIdStatusDetail = mapper.getAlternateIds(subscriberIdentityVal).byAlternateId(externalAlternateId);
                ddfTable.changeAlternateIdentityStatus(subscriberIdentityVal, alternateIdentityVal, subscriberAlternateIdStatusDetail.getStatus(), staffData);
            }
        }

        @Test
        public void changeAlternateIdStatusToProvided() throws OperationFailedException, UnauthorizedActionException {
            when(staffData.isAccessibleAction(anyList(), any(), any())).thenReturn(true);
            mapper.addExternalAlternateIdMapping(subscriberIdentityVal, alternateIdentityVal);
            SubscriberAlternateIds alternateIds = mapper.getAlternateIds(subscriberIdentityVal);
            assertEquals(CommonConstants.STATUS_ACTIVE, alternateIds.byAlternateId(alternateIdentityVal).getStatus());
            ddfTable.changeAlternateIdentityStatus(subscriberIdentityVal, alternateIdentityVal, CommonConstants.STATUS_INACTIVE, staffData);
            SubscriberAlternateIds afterOperationPerformed = mapper.getAlternateIds(subscriberIdentityVal);
            assertEquals(CommonConstants.STATUS_INACTIVE, afterOperationPerformed.byAlternateId(alternateIdentityVal).getStatus());
        }

    }

    public class GetExternalIds {

        @Test(expected = UnauthorizedActionException.class)
        public void throwsUnAuthorizedActionWhenUserNotAuthorizeForViewSubscriberProfileAction() throws OperationFailedException, UnauthorizedActionException {
            when(staffData.isAccessibleAction(anyList(), Matchers.eq(ACLModules.SUBSCRIBER), Matchers.eq(ACLAction.VIEW_SUBSCRIBER))).thenReturn(false);
            ddfTable.getExternalAlternateIds(subscriberIdentityVal, staffData);
        }

        @Test
        public void returnAlternateIds() throws OperationFailedException, UnauthorizedActionException {
            when(staffData.isAccessibleAction(anyList(), any(), any())).thenReturn(true);
            mapper.addExternalAlternateIdMapping(subscriberIdentityVal, externalAlternateId);
            mapper.addMapping(subscriberIdentityVal, alternateIdentityVal);
            SubscriberAlternateIds externalAlternateIds = ddfTable.getExternalAlternateIds(subscriberIdentityVal, staffData);
            assertNotNull(externalAlternateIds);
            assertEquals(alternateIdentityVal, externalAlternateIds.getSprTypeAlternateId().getAlternateId());
            assertNotNull(externalAlternateIds.byAlternateId(externalAlternateId));
        }
    }


}