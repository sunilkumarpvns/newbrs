package com.elitecore.diameterapi.diameter.common.session.imdg;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

/**
 * @author malav
 *
 */
public class HazelcastSessionSerializationFactory implements
		DataSerializableFactory {

	public static final int FACTORY_ID = 2;
	/**
	 * 
	 */
	private final HazelcastSessionFactory hazelcastSessionFactory;

	/**
	 * @param hazelcastSessionFactory
	 */
	HazelcastSessionSerializationFactory(HazelcastSessionFactory hazelcastSessionFactory) {
		this.hazelcastSessionFactory = hazelcastSessionFactory;
	}


	/* (non-Javadoc)
	 * @see com.hazelcast.nio.serialization.DataSerializableFactory#create(int)
	 */
	@Override
	public IdentifiedDataSerializable create(int classId) {
		switch (classId) {
		case HazelcastSession.SERIALIZATION_ID: return new HazelcastSession(null, this.hazelcastSessionFactory);
		default: return null;
		}
	}
}