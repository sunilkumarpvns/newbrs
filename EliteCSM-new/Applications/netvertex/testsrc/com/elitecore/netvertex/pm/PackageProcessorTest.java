package com.elitecore.netvertex.pm;


import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.spr.data.Subscription;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PackageProcessorTest {

    private QuotaProfile quotaProfile = mock(QuotaProfile.class);
    private final String INR = "INR";
    private final String packageId = "testId";
    private final String packageName = "testBasePackage";
    private PolicyContext policyContext;
    private QoSInformation qoSInformation;
    private MockBasePackage mockBasePackage;

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
    public void FupLevelQoSProfileDetailShouldNotApplyIfHsqLevelQoSProfileDetailIsFullyApplied(){

        QoSProfileDetail hsqQoSProfileDetail = mock(UMBaseQoSProfileDetail.class);
        QoSProfileDetail fupQoSProfileDetail = mock(UMBaseQoSProfileDetail.class);

        Subscription subscription = new Subscription.SubscriptionBuilder().withPackageId(packageId).withSubscriberIdentity("testSubscriberId").build();

        List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile> qosProfiles = new ArrayList<>();
        qosProfiles.add(getQoSProfile(null, null, hsqQoSProfileDetail, Arrays.asList(fupQoSProfileDetail), null, 0));
        qosProfiles.add(getQoSProfile(null, null, hsqQoSProfileDetail, Arrays.asList(fupQoSProfileDetail), null, 0));

        when(mockBasePackage.getQoSProfiles()).thenReturn(qosProfiles);
        when(hsqQoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)).thenReturn(SelectionResult.FULLY_APPLIED);
        when(fupQoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)).thenThrow(new RuntimeException("Should not call fup level qos detail"));

        PackageProcessor.apply(policyContext, mockBasePackage, subscription);

        verify(hsqQoSProfileDetail).apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
        verify(fupQoSProfileDetail, never()).apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
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

        List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile> qosProfiles = new ArrayList<>();
        qosProfiles.add(getQoSProfile(null, null, hsqQoSProfileDetail, Arrays.asList(fupQoSProfileDetail,fup2QoSProfileDetail,fup3QoSProfileDetail,fup4QoSProfileDetail), null, 0));
        qosProfiles.add(getQoSProfile(null, null, hsqQoSProfileDetail, Arrays.asList(fupQoSProfileDetail,fup2QoSProfileDetail,fup3QoSProfileDetail,fup4QoSProfileDetail), null, 0));
        qosProfiles.add(getQoSProfile(null, null, hsqQoSProfileDetail, Arrays.asList(fupQoSProfileDetail,fup2QoSProfileDetail,fup3QoSProfileDetail,fup4QoSProfileDetail), null, 0));
        qosProfiles.add(getQoSProfile(null, null, hsqQoSProfileDetail, Arrays.asList(fupQoSProfileDetail,fup2QoSProfileDetail,fup3QoSProfileDetail,fup4QoSProfileDetail), null, 0));

        when(mockBasePackage.getQoSProfiles()).thenReturn(qosProfiles);

        when(hsqQoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)).thenReturn(SelectionResult.NOT_APPLIED);

        when(fupQoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)).thenReturn(SelectionResult.PARTIALLY_APPLIED);
        when(fup2QoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.PARTIALLY_APPLIED)).thenReturn(SelectionResult.NOT_APPLIED);
        when(fup3QoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.PARTIALLY_APPLIED)).thenReturn(SelectionResult.FULLY_APPLIED);
        when(fup4QoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)).thenThrow(new RuntimeException("Should not call fup level qos detail"));

        PackageProcessor.apply(policyContext, mockBasePackage, subscription);

		verify(hsqQoSProfileDetail).apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
		verify(fupQoSProfileDetail).apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
		verify(fup2QoSProfileDetail).apply(policyContext, qoSInformation, SelectionResult.PARTIALLY_APPLIED);
		verify(fup3QoSProfileDetail).apply(policyContext, qoSInformation, SelectionResult.PARTIALLY_APPLIED);
		verify(fup4QoSProfileDetail, never()).apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

	}

    private QoSProfile getQoSProfile(LogicalExpression logicalExpression, AccessTimePolicy accessTimePolicy, QoSProfileDetail hsqQoSProfileDetail, List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail> fupLevelQoSDetails, List<String> accessNetwork, int duration) {
        return new QoSProfile(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
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
