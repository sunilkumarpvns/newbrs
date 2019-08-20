package com.elitecore.netvertex.service.pcrf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.service.notification.NotificationEvent;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.netvertex.core.NetVertexServerContext;

import java.util.List;

public abstract class NotificationProcessor {

	private static final String MODULE = "NTF-PROC";
	private final NetVertexServerContext serverContext;

	public NotificationProcessor(NetVertexServerContext serverContext) {
		this.serverContext = serverContext;
	}

	public void process(PCRFRequest request, PCRFResponse response) {
        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        if (subscriberIdentity == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().warn(MODULE, "Skipping notification processing. Reason: Sub.SubscriberIdentity not found from PCRF Response");
            }
            return;
        }

        String productOfferName = request.getAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val);
        if (productOfferName == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping notification for subcriber " + subscriberIdentity
                        + ". Reason: Sub.ProductOffer not found in PCRFResponse ");
            }
            return;
        }

        ProductOffer productOffer = serverContext.getPolicyRepository().getProductOffer().base().byName(productOfferName);

        if (productOffer == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping notification for subcriber " + subscriberIdentity
                        + ". Reason: Product offer not found for name : " + productOfferName);
            }
            return;
        }

        if (productOffer.getPolicyStatus() == PolicyStatus.FAILURE) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping notification for subcriber " + subscriberIdentity
                        + ". Reason: Product offer (" + productOffer.getName() + ") status is: " + PolicyStatus.FAILURE
                        + ", with fail reason(s): " + productOffer.getFailReason());
            }
            return;
        }


        BasePackage basePackage = (BasePackage) productOffer.getDataServicePkgData();

        if (basePackage == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping notification for subcriber " + subscriberIdentity
                        + ". Reason: Base data package not found from product offer: " + productOfferName);
            }
            return;
        }

        if (basePackage.getStatus() == PolicyStatus.FAILURE) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping notification for subscriber " + subscriberIdentity
                        + ". Reason: Base package(" + basePackage.getName() + ") status is: " + PolicyStatus.FAILURE + ", with fail reason(s): " + basePackage.getFailReason());
            }
            return;
        }

        if (isEligibleFurtherProcessing(request, response) == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping Notification processing. Reason: Notification eligibility criteria not matched");
            }
            return;
        }

        NotificationQueue notificationQueue = new NotificationQueueImpl(response, serverContext);
        processBasePackage(request, response, basePackage, notificationQueue);

        processSubscriptions(request, response, subscriberIdentity, notificationQueue);

        SPRInfo sprInfo = request.getSPRInfo();
        if (sprInfo == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping promotional policy notification processing. Reason: Subscriber profile not found from pcrf request");
            }
            return;
        }

        List<String> sprGroupIds = sprInfo.getSPRGroupIds();
        if (Collectionz.isNullOrEmpty(sprGroupIds)) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping promotional policy notification. Reason: SPR Groups not configured");
            }
        }

        processPromotionalPackages(request, response, notificationQueue);
    }



    protected abstract boolean isEligibleFurtherProcessing(PCRFRequest request, PCRFResponse response);
    protected abstract void processBasePackage(PCRFRequest request, PCRFResponse response, BasePackage basePackage, NotificationQueue notificationQueue);
    protected abstract void processPromotionalPackages(PCRFRequest request, PCRFResponse response, NotificationQueue notificationQueue);
    protected abstract void processSubscriptions(PCRFRequest request, PCRFResponse response, String subscriberIdentity, NotificationQueue notificationQueue);

	protected ILogger getLogger() {
		return LogManager.getLogger();
	}

    protected static class NotificationQueueImpl implements NotificationQueue {

		private final PCRFResponse response;
		private final NetVertexServerContext context;

		public NotificationQueueImpl(PCRFResponse pcrfResponse, NetVertexServerContext context) {
			super();
			this.response = pcrfResponse;
			this.context = context;
		}

		@Override
		public void add(NotificationEvent event) {
			context.sendNotification(event.getEmailTemplate(), event.getSMSTemplate(), response, null, event.getNotificationRecipient());
		}
	}


}
