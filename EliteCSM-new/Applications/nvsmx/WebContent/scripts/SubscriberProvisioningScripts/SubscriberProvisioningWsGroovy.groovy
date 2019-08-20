
import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
import com.elitecore.nvsmx.system.groovy.SubscriberProvisioningWsScript;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddBulkSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.GetSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ListSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.SubscriberProvisioningWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.UpdateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.SubscriberProvisioningResponse;


public class SubscriberProvisioningWsGroovy extends SubscriberProvisioningWsScript {

	private static final String MODULE = "SUBSCRIBER-PROV-WS-GRY";
	
	public void init() throws InitializationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "SubscriberProvisioningWS script(" + getName() + ") initialized successfully");
		}
	}
	
	public String getName() {
		return MODULE;
	}
	
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
}
