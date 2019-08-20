package com.elitecore.core.imdg;

import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @author malav
 *
 */
public interface EventListner {
	void onStartUp(HazelcastInstance hazelcastInstance);
}