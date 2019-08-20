package com.elitecore.netvertex.service.pcrf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.MeteringType;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageThresholdEvent;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.util.MockProductOffer;
import com.elitecore.netvertex.service.notification.UsageNotificationScheme;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationProcessorTest {

	private static final String PROMOTIONAL_PACKAGE = "promotional_package1";
	private static final String GROUP_ID = "group_id";
	private static final String PACKAGE_ID = "package_id";
	private static final String PRODUCT_OFFER_ID = "product_offer_id";
    private static final String PRODUCT_OFFER_NAME = "product_offer_name";
	private static final String QUOTA_PROFILE_ID = "quota_profile_id";
	private static final String SERVICE_ID = "service_id";
	private static final String BASE_PKG_NAME = "base_pkg";
	private static final String SUBSCRIBER_IDENTITY = "007";
	private static final String CURRENCY = "INR";
	private NotificationProcessor notificationProcessor;
	private NetVertexServerContext serverContext;
	private @Mock
    DummyPolicyRepository policyRepository;
	private UsageThresholdEvent usageThresholdEvent;
	
	@Before 
	public void before() {
		policyRepository = spy(new DummyPolicyRepository());
		serverContext = spy(new DummyNetvertexServerContextImpl());
		notificationProcessor = new UsageNotificationProcessor(serverContext);
		usageThresholdEvent = new UsageThresholdEvent(MeteringType.VOLUME_TOTAL, AggregationKey.BILLING_CYCLE, SERVICE_ID, QUOTA_PROFILE_ID, 1500l
				, new Template("1", "email_template", "quota_limit_reached", "Hi!! quota limit reahed 1500bytes"), null);
	}
	
	@Test
	public void test_eligibleNotificationFromPromotionalPackage_addedInto_notificationQueue() {
		PCRFRequestImpl request = new PCRFRequestImpl();
		PCRFResponseImpl response = new PCRFResponseImpl();
		request.setSPRInfo(createDummySPRInfo());

		addDefaultParametersForNotificationProcessor(request, response);
        MockProductOffer productOffer  = MockProductOffer.create(policyRepository, PRODUCT_OFFER_ID, PRODUCT_OFFER_NAME);
        productOffer.addDataPackage(createBasePackage());
        policyRepository.addProductOffer(productOffer);
		when(serverContext.getPolicyRepository()).thenReturn(policyRepository);
		when(policyRepository.getPromotionalPackagesOfGroup(GROUP_ID)).thenReturn(createPromotionalPackageWithNotificationScheme());
		
		notificationProcessor.process(request, response);
		
		verify(serverContext).sendNotification(usageThresholdEvent.getEmailTemplate(), null, response, null, usageThresholdEvent.getNotificationRecipient());
		verify(serverContext, times(0)).getSPRInfo(anyString());
	}

	private BasePackage createBasePackage() {

        return new BasePackage("2", BASE_PKG_NAME, QuotaProfileType.USAGE_METERING_BASED, PkgStatus.ACTIVE, Collections.emptyList()
				, null, null, null, (double) 100l, null,null, null, PolicyStatus.SUCCESS, null, null, null, null, null,CURRENCY);
	}

	private ArrayList<PromotionalPackage> createPromotionalPackageWithNotificationScheme() {

		PromotionalPackage promotionalPackage = new PromotionalPackage(PACKAGE_ID, PROMOTIONAL_PACKAGE, QuotaProfileType.USAGE_METERING_BASED
                , PkgStatus.ACTIVE, Collections.emptyList(), createUsageNotificationScheme(), PkgMode.LIVE, "desc", (double) 100l, null, null, Arrays.asList(GROUP_ID)
				, false, PolicyStatus.SUCCESS, null,null, null, null, null);
		
		ArrayList<PromotionalPackage> promotionalPackages = new ArrayList<PromotionalPackage>();
		promotionalPackages.add(promotionalPackage);
		
		return promotionalPackages;
	}

	private UsageNotificationScheme createUsageNotificationScheme() {

		List<List<UsageThresholdEvent>> usageThresholdEvents = new ArrayList<List<UsageThresholdEvent>>();
		usageThresholdEvents.add(Arrays.asList(usageThresholdEvent));
		
		UsageNotificationScheme usageNotificationScheme = new UsageNotificationScheme(usageThresholdEvents);
		
		return usageNotificationScheme;
	}

	private SPRInfo createDummySPRInfo() {

		SPRInfoImpl sprInfo = new SPRInfoImpl();
		sprInfo.setSPRGroupIds(Arrays.asList(GROUP_ID));
		
		return sprInfo;
	}

	private void addDefaultParametersForNotificationProcessor(PCRFRequestImpl request, PCRFResponseImpl response) {

		request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, SUBSCRIBER_IDENTITY);
		request.setAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val, PRODUCT_OFFER_NAME);
		request.addPCRFEvent(PCRFEvent.USAGE_REPORT);
		response.setServiceUsage(new ServiceUsage(createCurrentUsage()));
		response.setServiceUsageTillLastUpdate(new ServiceUsage(createPreviousUsage()));
		
	}

	private Map<String, Map<String, SubscriberUsage>> createPreviousUsage() {

		Map<String, Map<String, SubscriberUsage>> previousUsageMap = new HashMap<String, Map<String,SubscriberUsage>>();
		Map<String, SubscriberUsage> quotaServiceWiseUsage = new HashMap<String, SubscriberUsage>();
		
		quotaServiceWiseUsage.put(QUOTA_PROFILE_ID + CommonConstants.USAGE_KEY_SEPARATOR + SERVICE_ID, new SubscriberUsage.SubscriberUsageBuilder("1"
				, SUBSCRIBER_IDENTITY, SERVICE_ID, QUOTA_PROFILE_ID, PACKAGE_ID,PRODUCT_OFFER_ID).withBillingCycleTotalUsage(1000).build());
		
		previousUsageMap.put(PACKAGE_ID, quotaServiceWiseUsage);
		
		return previousUsageMap;
	}

	private Map<String, Map<String, SubscriberUsage>> createCurrentUsage() {
		
		Map<String, Map<String, SubscriberUsage>> currentUsageMap = new HashMap<String, Map<String,SubscriberUsage>>();
		Map<String, SubscriberUsage> quotaServiceWiseUsage = new HashMap<String, SubscriberUsage>();
		
		quotaServiceWiseUsage.put(QUOTA_PROFILE_ID + CommonConstants.USAGE_KEY_SEPARATOR + SERVICE_ID, new SubscriberUsage.SubscriberUsageBuilder("1"
				, SUBSCRIBER_IDENTITY, SERVICE_ID, QUOTA_PROFILE_ID, PACKAGE_ID,PRODUCT_OFFER_ID).withBillingCycleTotalUsage(2000).build());
		
		currentUsageMap.put(PACKAGE_ID, quotaServiceWiseUsage);
		
		return currentUsageMap;
	}
}
