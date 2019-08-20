package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.spr.data.SubscriberRepositoryConfiguration;

import java.util.Collection;


public interface SPRProvider {
	SubscriberRepository getDefaultRepository();
	Collection<SubscriberRepository> getAllSubscriberRepository();

	SubscriberRepository getRepository(SubscriberRepositoryConfiguration defaultSprData);
}
