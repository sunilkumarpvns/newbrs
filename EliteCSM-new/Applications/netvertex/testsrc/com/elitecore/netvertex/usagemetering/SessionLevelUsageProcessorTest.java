package com.elitecore.netvertex.usagemetering;


import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.pm.util.DataPackageStore;
import com.elitecore.corenetvertex.pm.util.MockAddOnPackage;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.usagemetering.factory.UMInfoFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;

import static com.elitecore.corenetvertex.pm.util.MockPackages.addOn;
import static com.elitecore.netvertex.core.util.Maps.Entry.newEntry;
import static com.elitecore.netvertex.core.util.Maps.newLinkedHashMap;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by harsh on 5/18/17.
 */
public class SessionLevelUsageProcessorTest {

    private SessionLevelUsageProcessor sessionLevelUsageProcessor;
    @Mock private ExecutionContext executionContext;
    private PCRFResponse pcrfResponse;
    private DataPackageStore dataPackageStore;



    @Before
    public void setUp() {
        initMocks(this);
        dataPackageStore = new DataPackageStore();
        PolicyProvider policyProvider = new PolicyProvider(dataPackageStore);
        DummyNetvertexServerContextImpl netvertexServerContext = new DummyNetvertexServerContextImpl();
        netvertexServerContext.setPolicyRepository(policyProvider);
        sessionLevelUsageProcessor = new SessionLevelUsageProcessor(netvertexServerContext);
        pcrfResponse = spy(new PCRFResponseImpl());
    }


    @Test
    public void test_process_skip_processingOfAddOnUsage_when_AddOnDoesNotHaveQuotaProfile() throws OperationFailedException {
        final UsageMonitoringInfo usedServiceUnit = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(
                "mk1",
                new ServiceUnit.ServiceUnitBuilder().withTotalOctets(10).build()
        );

        final String subscriptionId = "subscription1";
        pcrfResponse.setUsageReservations(newLinkedHashMap(newEntry(subscriptionId, "quota1")));

        final MockAddOnPackage addOn = addOn("1", "addOn1");
        addOn.quotaProfileTypeIsUM();
        dataPackageStore.policyReloaded(addOn);

        final Subscription subscription = new Subscription.SubscriptionBuilder().withId(subscriptionId).withPackageId(addOn.getId()).withSubscriberIdentity("test").build();


        sessionLevelUsageProcessor.process(pcrfResponse,
                executionContext,
                Arrays.asList(usedServiceUnit),
                newLinkedHashMap(newEntry(subscriptionId, subscription)),
                null,
                null);

        verify(addOn, times(1)).getQuotaProfiles();
        verify(pcrfResponse, times(0)).setServiceUsage(Mockito.<ServiceUsage>any());
        verify(pcrfResponse, times(0)).setSessionUsage(Mockito.<SessionUsage>any());
        verify(executionContext, times(0)).addToExistingUsage(anyCollection());
        verify(executionContext, times(0)).insertNewUsage(anyCollection());
    }



}
