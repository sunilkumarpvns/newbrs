package com.elitecore.nvsmx.system.groovy;

import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddBulkSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberProfileBulkWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberProfileWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeBaseProductOfferWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeImsPackageWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.GetAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.GetSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ListSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.MigrateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.RestoreSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.RestoreSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.SubscriberProvisioningWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.UpdateAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.UpdateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.AlternateIdProvisioningResponse;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.SubscriberProvisioningResponse;

import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class SubscriberProvisioningWsScript {

	private static final String MODULE = "SUBSCRIBER-PROV-WS-SCRIPT";
	
	public void init() throws InitializationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "SubscriberProvisioningWS script(" + getName() + ") initialized successfully");
		}
	}
	
	abstract public String getName();
	
	public void preAddSubscriberProfile(AddSubscriberWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preAddSubscriberProfile of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void postAddSubscriberProfile(AddSubscriberWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postAddSubscriberProfile of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void preAddSubscriber(AddSubscriberProfileWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preAddSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void postAddSubscriber(AddSubscriberProfileWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postAddSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void preUpdateSubscriberProfile(UpdateSubscriberWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preUpdateSubscriberProfile of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void postUpdateSubscriberProfile(UpdateSubscriberWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postUpdateSubscriberProfile of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void preDeleteSubscriberProfile(DeleteSubscriberWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preDeleteSubscriberProfile of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void postDeleteSubscriberProfile(DeleteSubscriberWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postDeleteSubscriberProfile of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void preAddSubscriberBulk(AddBulkSubscriberWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preAddSubscriberBulk of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void postAddSubscriberBulk(AddBulkSubscriberWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postAddSubscriberBulk of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void preAddSubscriberProfileBulk(AddSubscriberProfileBulkWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preAddSubscriberProfileBulk of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void postAddSubscriberProfileBulk(AddSubscriberProfileBulkWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postAddSubscriberProfileBulk of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void preGetSubscriberProfileByID(GetSubscriberWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preGetSubscriberProfileByID of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void postGetSubscriberProfileByID(GetSubscriberWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postGetSubscriberProfileByID of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void preGetListSubscriberProfiles(ListSubscribersWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListSubscriberProfiles of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void postListSubscriberProfiles(ListSubscribersWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListSubscriberProfiles of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void prePurgeSubscriber(PurgeSubscriberWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "prePurgeSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postPurgeSubscriber(PurgeSubscriberWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postPurgeSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void preDeleteSubscribers(DeleteSubscribersWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preDeleteSubscribers of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postDeleteSubscribers(DeleteSubscribersWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postDeleteSubscribers of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void prePurgeSubscribers(PurgeSubscribersWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "prePurgeSubscribers of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postPurgeSubscribers(PurgeSubscribersWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postPurgeSubscribers of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void prePurgeAllSubscriber(SubscriberProvisioningWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "prePurgeAllSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postPurgeAllSubscriber(SubscriberProvisioningWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postPurgeAllSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void preRestoreSubscriber(RestoreSubscriberWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preRestoreSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postRestoreSubscriber(RestoreSubscriberWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postRestoreSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void preRestoreSubscribers(RestoreSubscribersWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preRestoreSubscribers of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postRestoreSubscribers(RestoreSubscribersWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postRestoreSubscribers of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void postRestoreAllSubscriber(SubscriberProvisioningWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postRestoreAllSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postMigrateSubscriber(MigrateSubscriberWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postMigrateSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void preMigrateSubscriber(MigrateSubscriberWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preMigrateSubscriber of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
	
	public void preChangeImsPackage(ChangeImsPackageWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preChangePackage of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postChangeImsPackage(ChangeImsPackageWSRequest request, SubscriberProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postChangeImsPackage of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

    public void preChangeBaseProductOffer(ChangeBaseProductOfferWSRequest request) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "preChangeBaseProductOffer of SubscriberProvisioningWsScript(" + getName() + ") is called");
        }
    }

    public void postChangeBaseProductOffer(ChangeBaseProductOfferWSRequest request, SubscriberProvisioningResponse response) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "postChangeBaseProductOffer of SubscriberProvisioningWsScript(" + getName() + ") is called");
        }
    }

	public void preAddAlternateId(AddAlternateIdWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preAddAlternateId of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postAddAlternateId(AddAlternateIdWSRequest request, AlternateIdProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postGetSubscriberProfileByID of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void preUpdateAlternateId(UpdateAlternateIdWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preUpdateAlternateId of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postUpdateAlternateId(UpdateAlternateIdWSRequest request, AlternateIdProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postUpdateAlternateId of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void preDeleteAlternateId(AddAlternateIdWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preDeleteAlternateId of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postDeleteAlternateId(AddAlternateIdWSRequest request, AlternateIdProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postDeleteAlternateId of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void preChangeAlternateIdStatus(UpdateAlternateIdWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preChangeAlternateIdStatus of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}

	}

	public void postChangeAlternateIdStatus(UpdateAlternateIdWSRequest request, AlternateIdProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postChangeAlternateIdStatus of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void preGetAlternateIds(GetAlternateIdWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preGetAlternateIds of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}

	public void postGetAlternateIds(GetAlternateIdWSRequest request, AlternateIdProvisioningResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postGetAlternateIds of SubscriberProvisioningWsScript(" + getName() + ") is called");
		}
	}
}
