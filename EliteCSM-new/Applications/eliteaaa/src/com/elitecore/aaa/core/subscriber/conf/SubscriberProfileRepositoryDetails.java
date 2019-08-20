package com.elitecore.aaa.core.subscriber.conf;

import com.elitecore.aaa.core.conf.impl.UpdateIdentityParameters;
import com.elitecore.aaa.radius.conf.impl.ProfileDriverDetails;

public interface SubscriberProfileRepositoryDetails {

	public String getAnonymousProfileIdentity();

	public UpdateIdentityParameters getUpdateIdentity();

	public ProfileDriverDetails getDriverDetails();

}
