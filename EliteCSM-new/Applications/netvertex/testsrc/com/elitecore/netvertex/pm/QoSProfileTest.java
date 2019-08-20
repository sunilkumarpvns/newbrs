package com.elitecore.netvertex.pm;


import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QoSProfileTest {

    private QuotaProfile quotaProfile = mock(QuotaProfile.class);
    private final String INR = "INR";
    private final String packageId = "testId";
    private final String packageName = "testBasePackage";
    private PolicyContext policyContext;
    private QoSInformation qoSInformation;
    private MockBasePackage mockBasePackage;
    private QoSProfile qoSProfile;

    @Before
    public void setUp() {

        mockBasePackage = MockBasePackage.create(packageId,packageName);
        qoSInformation = spy(new QoSInformation());
        PCRFRequest pcrfRequest = new PCRFRequestImpl();
        PCRFResponse pcrfResponse = new PCRFResponseImpl();
        policyContext = spy(new PCRFPolicyContextImpl(pcrfRequest, pcrfResponse,
                mockBasePackage,
                new ExecutionContext(pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(), INR),
                new PCRFQoSProcessor(qoSInformation), new DummyPolicyRepository()));

    }

    @SuppressWarnings("unchecked")
	@Test
	public void policyContextShouldNotBeProcessedWhenValidationOfLogicalExpressionFails() throws InvalidExpressionException {
		
		LogicalExpression logicalExpression = Compiler.getDefaultCompiler().parseLogicalExpression("\"true\"=\"false\"");
        Subscription subscription = new Subscription.SubscriptionBuilder().withPackageId(packageId).withSubscriberIdentity("testSubscriberId").build();

		qoSProfile = getQoSProfile(logicalExpression, null, null, null, null, 0);
		
		qoSProfile.selectRule(policyContext, mockBasePackage, subscription);

		verify(policyContext, never()).process(qoSProfile, mockBasePackage, subscription);
	}

    @SuppressWarnings("unchecked")
    @Test
    public void policyContextShouldNotBeProcessedWhenValidationOfAccessTimePolicyFails(){

        AccessTimePolicy accessTimePolicy = mock(AccessTimePolicy.class);
        when(accessTimePolicy.applyPolicy()).thenReturn(new Long(AccessTimePolicy.FAILURE));

        Subscription subscription = new Subscription.SubscriptionBuilder().withPackageId(packageId).withSubscriberIdentity("testSubscriberId").build();

        qoSProfile = getQoSProfile(null, accessTimePolicy, null, null, null, 0);

        qoSProfile.selectRule(policyContext, mockBasePackage, subscription);

        verify(policyContext, never()).process(qoSProfile, mockBasePackage, subscription);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void policyContextShouldNotBeProcessedWhenValidationOfAccessNetworkFails(){

        Subscription subscription = new Subscription.SubscriptionBuilder().withPackageId(packageId).withSubscriberIdentity("testSubscriberId").build();

        qoSProfile = getQoSProfile(null, null, null, null, Arrays.asList("test"), 0);

        qoSProfile.selectRule(policyContext, mockBasePackage, subscription);

        verify(policyContext, never()).process(qoSProfile, mockBasePackage, subscription);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void policyContextShouldNotBeProcessedWhenAccessNetworkIsNotSameAsCurrentAccessNetwork(){

        PCRFResponse pcrfResponse = spy(new PCRFResponseImpl());
        when(policyContext.getPCRFResponse()).thenReturn(pcrfResponse);
        when(pcrfResponse.getAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val)).thenReturn(PCRFKeyValueConstants.ACCESS_NETWORK_3GPP_GPRS.val);

        Subscription subscription = new Subscription.SubscriptionBuilder().withPackageId(packageId).withSubscriberIdentity("testSubscriberId").build();

        qoSProfile = getQoSProfile(null, null, null, null, Arrays.asList("test"), 0);

        qoSProfile.selectRule(policyContext, mockBasePackage, subscription);

        verify(policyContext, never()).process(qoSProfile, mockBasePackage, subscription);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void policyContextShouldNotBeProcessedWhenDurationBetweenStartTimeAndCurrentTimeIsZero(){

        int duration = 20;
        long currentTime = System.currentTimeMillis();
        Calendar calendar = spy(Calendar.getInstance());
        calendar.setTimeInMillis(currentTime);

        when(policyContext.getCurrentTime()).thenReturn(calendar);
        when(policyContext.getSessionStartTime()).thenReturn(new Timestamp(currentTime - TimeUnit.MINUTES.toMillis(duration)));

        Subscription subscription = new Subscription.SubscriptionBuilder().withPackageId(packageId).withSubscriberIdentity("testSubscriberId").build();

        qoSProfile = getQoSProfile(null, null, null, null, null, duration);

        qoSProfile.selectRule(policyContext, mockBasePackage, subscription);

        verify(policyContext, never()).process(qoSProfile, mockBasePackage, subscription);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void policyContextShouldNotBeProcessedWhenDurationBetweenStartTimeAndCurrentTimeIsLessThanZero(){

        int duration = 20;
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);

        when(policyContext.getCurrentTime()).thenReturn(calendar);
        when(policyContext.getSessionStartTime()).thenReturn(new Date(currentTime - TimeUnit.MINUTES.toMillis(duration)-1));

        Subscription subscription = new Subscription.SubscriptionBuilder().withPackageId(packageId).withSubscriberIdentity("testSubscriberId").build();

        qoSProfile = getQoSProfile(null, null, null, null, null, duration);

        qoSProfile.selectRule(policyContext, mockBasePackage, subscription);

        verify(policyContext, never()).process(qoSProfile, mockBasePackage, subscription);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void FupLevelQoSProfileDetailShouldNotApplyIfHsqLevelQoSProfileDetailIsFullyApplied(){

        QoSProfileDetail hsqQoSProfileDetail = mock(UMBaseQoSProfileDetail.class);
        QoSProfileDetail fupQoSProfileDetail = mock(UMBaseQoSProfileDetail.class);
        Subscription subscription = new Subscription.SubscriptionBuilder().withPackageId(packageId).withSubscriberIdentity("testSubscriberId").build();

        when(hsqQoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)).thenReturn(SelectionResult.FULLY_APPLIED);
        when(fupQoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)).thenThrow(new RuntimeException("Should not call fup level qos detail"));

        qoSProfile = getQoSProfile(null, null, hsqQoSProfileDetail, Arrays.asList(fupQoSProfileDetail), null, 0);

        qoSProfile.selectRule(policyContext, mockBasePackage, subscription);

        verify(hsqQoSProfileDetail).apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
    }

	@SuppressWarnings("unchecked")
	@Test
	public void ApplyFupLevelQoSProfileDetailsUntilQoSProfileDetailWithFullyAppliedSelectionResultIsNotDetected(){


        Subscription subscription = new Subscription.SubscriptionBuilder().withPackageId(packageId).withSubscriberIdentity("testSubscriberId").build();

		QoSProfileDetail hsqQoSProfileDetail = mock(UMBaseQoSProfileDetail.class);
		QoSProfileDetail fupQoSProfileDetail = mock(UMBaseQoSProfileDetail.class);
		QoSProfileDetail fup2QoSProfileDetail = mock(UMBaseQoSProfileDetail.class);
		QoSProfileDetail fup3QoSProfileDetail = mock(UMBaseQoSProfileDetail.class);
		QoSProfileDetail fup4QoSProfileDetail = mock(UMBaseQoSProfileDetail.class);

        doReturn(QuotaProfileType.RnC_BASED).when(quotaProfile).getType();

        when(hsqQoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)).thenReturn(SelectionResult.NOT_APPLIED);

        when(fupQoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)).thenReturn(SelectionResult.PARTIALLY_APPLIED);
        when(fup2QoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.PARTIALLY_APPLIED)).thenReturn(SelectionResult.NOT_APPLIED);
        when(fup3QoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.PARTIALLY_APPLIED)).thenReturn(SelectionResult.FULLY_APPLIED);
        when(fup4QoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)).thenThrow(new RuntimeException("Should not call fup level qos detail"));

        qoSProfile = getQoSProfile(null, null, hsqQoSProfileDetail, Arrays.asList(fupQoSProfileDetail,fup2QoSProfileDetail,fup3QoSProfileDetail,fup4QoSProfileDetail), null, 0);

        qoSProfile.selectRule(policyContext, mockBasePackage, subscription);
		
		verify(hsqQoSProfileDetail).apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
		verify(fupQoSProfileDetail).apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
		verify(fup2QoSProfileDetail).apply(policyContext, qoSInformation, SelectionResult.PARTIALLY_APPLIED);
		verify(fup3QoSProfileDetail).apply(policyContext, qoSInformation, SelectionResult.PARTIALLY_APPLIED);
		
	}

    private QoSProfile getQoSProfile(LogicalExpression logicalExpression, AccessTimePolicy accessTimePolicy, QoSProfileDetail hsqQoSProfileDetail, List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail> fupLevelQoSDetails, List<String> accessNetwork, int duration) {
        return new QoSProfile(
                "QoSProfile",
                "pkg",
                null,
                null,
                quotaProfile,
                null,
                accessNetwork,
                duration ,
                hsqQoSProfileDetail,
                fupLevelQoSDetails,
                logicalExpression,
                null,
                accessTimePolicy);
    }

}
