package com.elitecore.aaa.diameter.service.application.handlers.conf;

import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;

public interface SubscriberProfileRepositoryAware {
	public void setSubscriberProfileRepository(DiameterSubscriberProfileRepository spr);
}
