package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CustomerType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.store.BoDPackageStore;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriberRepositorySubscribeBoDPackageTest {

    private PolicyRepository policyRepository;
    private SubscriberRepository subscriberRepository;
    private SubscriptionParameter subscriptionParameter;
    private SPRInfo sprInfo;
    private BoDPackageStore boDPackageStore;
    private BoDPackage boDPackage;
    private EDRListener edrListener;
    private static final String BOD_PACKAGE_ID = "test";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp(){

        policyRepository = mock(PolicyRepository.class);
        edrListener = mock(EDRListener.class);
        subscriberRepository = new SubscriberRepositoryImpl(null,
                null,
                null,
                null,
                policyRepository,
                null,
                new ABMFconfigurationImpl(0, 0, 0),
                null,
                Collectionz.<String>newArrayList(),
                null,
                null,
                null, null, null, null, edrListener, "INR");

        sprInfo = new SPRInfoImpl.SPRInfoBuilder()
                .withCustomerType(CustomerType.PREPAID.val)
                .withCui("101")
                .withSubscriberIdentity("101")
                .withGroupIds(Collections.emptyList()).build();

        subscriptionParameter = new SubscriptionParameter(sprInfo, null, null, 0, null, BOD_PACKAGE_ID, BOD_PACKAGE_ID, null, null, 100, null, null, 0,null);

        boDPackageStore = mock(BoDPackageStore.class);
        when(policyRepository.getBoDPackage()).thenReturn(boDPackageStore);
    }

    @Test
    public void throwOperationFailedExceptionIfBoDNotFound() throws OperationFailedException {

        boDPackage = spy(new BoDPackage("Invalid Id", "Invalid Name", null, PkgMode.LIVE, PkgStatus.ACTIVE,
                30, ValidityPeriodUnit.DAY, null, null, null,
                null, PolicyStatus.SUCCESS, null, null, null, null, null));

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to subscribe BoD package(" + subscriptionParameter.getBodPackageId() + ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
                + ". Reason: BoD package not found for ID: " + subscriptionParameter.getBodPackageId());
        try {
            subscriberRepository.subscribeBoDPackage(subscriptionParameter, null);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.NOT_FOUND, e.getErrorCode());
            verify(edrListener, never()).addSubscriptionEDR(sprInfo, null, null, null);
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void throwOperationFailedExceptionIfBoDStatusIsFailure() throws OperationFailedException {

        boDPackage = spy(new BoDPackage(BOD_PACKAGE_ID, BOD_PACKAGE_ID, null, PkgMode.LIVE, PkgStatus.INACTIVE,
                30, ValidityPeriodUnit.DAY, null, null, null,
                null, PolicyStatus.FAILURE, null, null, null, null, null));
        when(boDPackageStore.byId(BOD_PACKAGE_ID)).thenReturn(boDPackage);
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to subscribe BoD package(" + subscriptionParameter.getBodPackageId() + ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
                + ". Reason: BoD package (" + boDPackage.getName() + ") is failed BoD package");
        try {
            subscriberRepository.subscribeBoDPackage(subscriptionParameter, null);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            verify(edrListener, never()).addSubscriptionEDR(sprInfo, null, null, null);
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void throwOperationFailedExceptionIfBoDIsNotActive() throws OperationFailedException {

        boDPackage = spy(new BoDPackage(BOD_PACKAGE_ID, BOD_PACKAGE_ID, null, PkgMode.LIVE, PkgStatus.INACTIVE,
                30, ValidityPeriodUnit.DAY, null, null, null,
                null, PolicyStatus.SUCCESS, null, null, null, null, null));
        when(boDPackageStore.byId(BOD_PACKAGE_ID)).thenReturn(boDPackage);
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to subscribe BoD(" + boDPackage.getName() + ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
                + " Reason: BoD package found with "
                + boDPackage.getPkgStatus() + " Status");
        try {
            subscriberRepository.subscribeBoDPackage(subscriptionParameter, null);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            verify(edrListener, never()).addSubscriptionEDR(sprInfo, null, null, null);
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void throwOperationFailedExceptionWhenTestBoDWithLiveSub() throws OperationFailedException {

        boDPackage = spy(new BoDPackage(BOD_PACKAGE_ID, BOD_PACKAGE_ID, null, PkgMode.TEST, PkgStatus.ACTIVE,
                30, ValidityPeriodUnit.DAY, null, null, null,
                null, PolicyStatus.SUCCESS, null, null, null, null, null));
        when(boDPackageStore.byId(BOD_PACKAGE_ID)).thenReturn(boDPackage);
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to subscribe BoD package(" + boDPackage.getName() + ") for subscriber ID: " + sprInfo.getSubscriberIdentity() + " Reason: Live subscriber must not have subscription of test BoD");
        try {
            subscriberRepository.subscribeBoDPackage(subscriptionParameter, null);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            verify(edrListener, never()).addSubscriptionEDR(sprInfo, null, null, null);
            throw e;
        }

        fail("should throw OperationFailedException");
    }
}