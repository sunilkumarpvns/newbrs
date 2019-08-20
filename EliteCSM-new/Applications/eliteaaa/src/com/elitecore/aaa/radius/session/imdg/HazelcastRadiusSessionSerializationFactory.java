package com.elitecore.aaa.radius.session.imdg;

import java.util.List;

import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.core.conf.impl.RadiusSessionFieldMapping;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

/**
 * @author soniya
 *
 */
public class HazelcastRadiusSessionSerializationFactory implements DataSerializableFactory {

	public static final int FACTORY_ID = 3;
	/**
	 * 
	 */
	private final HazelcastRadiusSessionFactory hazelcastSessionFactory;
	private List<ImdgIndexDetail> sessionIndexFieldMapping;
	private List<RadiusSessionFieldMapping> sessionDataFieldMapping;

	/**
	 * @param hazelcastRadiusSessionFactory
	 */
	public HazelcastRadiusSessionSerializationFactory(HazelcastRadiusSessionFactory hazelcastRadiusSessionFactory, List<ImdgIndexDetail> sessionIndexFieldMapping,List<RadiusSessionFieldMapping> sessionDataFieldMapping ) {
		this.hazelcastSessionFactory = hazelcastRadiusSessionFactory;
		this.sessionIndexFieldMapping = sessionIndexFieldMapping;
		this.sessionDataFieldMapping = sessionDataFieldMapping;
	}


	/* (non-Javadoc)
	 * @see com.hazelcast.nio.serialization.DataSerializableFactory#create(int)
	 */
	
	@Override
	public IdentifiedDataSerializable create(int classId) {
		switch (classId) {
		case HazelcastRadiusSession.SERIALIZATION_ID: return new HazelcastRadiusSession(null, this.hazelcastSessionFactory, sessionIndexFieldMapping, sessionDataFieldMapping);
		default: return null;
		}
	}
}