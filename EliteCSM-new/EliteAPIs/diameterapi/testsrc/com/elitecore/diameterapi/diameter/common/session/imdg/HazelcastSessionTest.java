package com.elitecore.diameterapi.diameter.common.session.imdg;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class HazelcastSessionTest {
	
	private static final String SESSION_ID = "session1";
	private static IMap<String, HazelcastSession> map;
	private static HazelcastInstance hazelcastInstance;
	
	
	@BeforeClass
	public static void beforeTest() {
		Config config = new Config();
		config.getSerializationConfig().addDataSerializableFactory(HazelcastSessionSerializationFactory.FACTORY_ID, 
				new HazelcastSessionSerializationFactory(null));
		hazelcastInstance = Hazelcast.newHazelcastInstance(config);
		map = hazelcastInstance.getMap("default");
	}
	
	@Test
	public void isAbleToSerializeAndDeserializeSessionWithoutParameters() {
		HazelcastSession originalSession = new HazelcastSession(SESSION_ID, null);
		
		map.put(originalSession.getSessionId(), originalSession);
		
		HazelcastSession deserializedSession = map.get(originalSession.getSessionId());
		
		ReflectionAssert.assertReflectionEquals(deserializedSession, originalSession);
	}
	
	@Test
	public void isAbleToSerializeAndDeserializeSessionWithParameters() {
		HazelcastSession originalSession = new HazelcastSession(SESSION_ID, null);
		originalSession.setParameter("key1", "value1");
		originalSession.setParameter("key2", 10000);
		originalSession.setParameter("key3", System.nanoTime()); // just to add a long value
		
		map.put(originalSession.getSessionId(), originalSession);
		
		HazelcastSession deserializedSession = map.get(originalSession.getSessionId());
		
		ReflectionAssert.assertReflectionEquals(deserializedSession, originalSession);
	}
	
	@After
	public void cleanUp() {
		map.remove(SESSION_ID);
	}
	
	@AfterClass
	public static void afterTest() {
		hazelcastInstance.shutdown();
	}
}
