package com.elitecore.aaa.core.diameter.conf.impl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.elitecore.aaa.diameter.conf.impl.RoutingEntryDataImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.DiameterFailoverConfigurationImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

/**
 * JUnit Test cases for {@link RoutingEntryDataImpl}
 * @author monica.lulla
 *
 */
public class RoutingEntryDataImplTest{

	/**
	 * Test behavior of <code>RoutingEntryDataImpl</code> for improper values.<br />
	 * This test case will help us for Null checks and default value handling 
	 * even in case of Improper values.
	 */
	@Test
	public void testBuggyValues(){
		try{
			//RoutingEntryData with Buggy Values...
			RoutingEntryDataImpl routingEntryData3 = new RoutingEntryDataImpl(); 
			routingEntryData3.setRoutingName("Buggy_Routing_Entry");
			routingEntryData3.setAdvancedCondition("Advanced Condition");
			routingEntryData3.setApplicationIds("Application Ids");
			routingEntryData3.setAttachedRedirection(false);
			routingEntryData3.setDestRealm("Destination Realm");
			routingEntryData3.setTransMapName("Trans Mapping");
			
			List<DiameterFailoverConfigurationImpl> failConfigs = new ArrayList<DiameterFailoverConfigurationImpl>();
			DiameterFailoverConfigurationImpl failConf1 = new DiameterFailoverConfigurationImpl();
			//Setting Invalid Failure Action
			failConf1.setAction(10);
			failConf1.setErrorCodes("30000");
			failConf1.setFailoverArguments("Trans Mapping");
			failConfigs.add(failConf1);

			routingEntryData3.setFailoverDataList(failConfigs);
			routingEntryData3.setOriginHostIp("Origin Host IP");
			routingEntryData3.setOriginRealm("Origin Realm");

			//for setting PeerGroupList
			List<PeerGroupImpl> peerGroupImpls = new ArrayList<PeerGroupImpl>();
			//creating element for peerGroupImpls List 
			PeerGroupImpl peerGroupImpl = new PeerGroupImpl();
			peerGroupImpl.setAdvancedConditionStr("Peer Level RuleSet");

			//for setting peerGroupImpls
			List<PeerInfoImpl> peerInfoList = new ArrayList<PeerInfoImpl>();
			//creating element for peerInfoList List 
			PeerInfoImpl peerInfoImpl = new PeerInfoImpl();
			
			peerInfoImpl.setLoadFactor(10);
			peerInfoImpl.setPeerName("Peer Name");
			
			peerInfoList.add(peerInfoImpl);
			peerGroupImpl.setPeerInfoList(peerInfoList);
			
			peerGroupImpls.add(peerGroupImpl);
			
			routingEntryData3.setPeerGroupList(peerGroupImpls);
			//setting Invalid Routing Action..
			routingEntryData3.setRoutingAction(10);
			
			assertEquals("Unable to Get configured Advanced Condition / RuleSet", 
					routingEntryData3.getAdvancedCondition() , "Advanced Condition");
			assertEquals("Unable to Get configured Application IDs", 
					routingEntryData3.getApplicationIds() , "Application Ids");
			assertTrue("Unable to Get configured Attached Redirection", 
					routingEntryData3.getAttachedRedirection() == false);
			assertEquals("Unable to Get configured Destination Realm", 
					routingEntryData3.getDestRealm() , "Destination Realm");			
			assertEquals("Unable to Get configured Trans Mapping", 
					routingEntryData3.getTransMapName(), "Trans Mapping");
			assertEquals("Unable to Get configured Trans Mapping", 
					routingEntryData3.getTransMapName(), "Trans Mapping");
			assertEquals("Unable to Get configured Origin Host IP", 
					routingEntryData3.getOriginHostIp(), "Origin Host IP");
			assertEquals("Unable to Get configured Origin Realm", 
					routingEntryData3.getOriginRealm(), "Origin Realm");
			assertNotNull("Peer Group List must notbe Null", 
					routingEntryData3.getPeerGroupList());
			assertEquals("Unable to Get configured Routing Action", 
					routingEntryData3.getRoutingAction() , 10);
			assertNotNull("Routing Name must NOT be Null", 
					routingEntryData3.getRoutingName());
			routingEntryData3.toString();
		}catch(Exception e){
			e.printStackTrace();
			fail("Exception Occured:" + e.getMessage());
		}
	}
	
	/**
	 * Test behavior of <code>RoutingEntryDataImpl</code> for proper values.
	 * <br /> This Test case MUST NEVER Fail.
	 */
	@Test
	public void testActualValues(){
		try{
			
			//RoutingEntryData with proper values
			RoutingEntryDataImpl routingEntryData3 = new RoutingEntryDataImpl(); 
			routingEntryData3.setRoutingName("routing_Enrty_Name");
			routingEntryData3.setAdvancedCondition("0:283=\"elitecore.com\"");
			routingEntryData3.setApplicationIds("0");
			routingEntryData3.setAttachedRedirection(false);
			routingEntryData3.setDestRealm("Destination Realm");
			routingEntryData3.setTransMapName("Crestel_rating_Mapping");
			
			List<DiameterFailoverConfigurationImpl> failConfigs = new ArrayList<DiameterFailoverConfigurationImpl>();		
			DiameterFailoverConfigurationImpl failConf1 = new DiameterFailoverConfigurationImpl();
			failConf1.setAction(DiameterFailureConstants.REDIRECT.failureAction);
			failConf1.setErrorCodes("3000");
			failConf1.setFailoverArguments("Redirect_Translation_Mapping");
			failConfigs.add(failConf1);
			
			routingEntryData3.setFailoverDataList(failConfigs);
			routingEntryData3.setOriginHostIp("*");
			routingEntryData3.setOriginRealm("*");

			//for setting PeerGroupList
			List<PeerGroupImpl> peerGroupImpls = new ArrayList<PeerGroupImpl>();
			//creating element for peerGroupImpls List 
			PeerGroupImpl peerGroupImpl = new PeerGroupImpl();
			peerGroupImpl.setAdvancedConditionStr("0:283=\"elitecore.com\"");

			//for setting peerGroupImpls
			List<PeerInfoImpl> peerInfoList = new ArrayList<PeerInfoImpl>();
			//creating element for peerInfoList List 
			PeerInfoImpl peerInfoImpl = new PeerInfoImpl();
			peerInfoImpl.setLoadFactor(10);
			peerInfoImpl.setPeerName("diameter_peer");
			
			peerInfoList.add(peerInfoImpl);
			peerGroupImpl.setPeerInfoList(peerInfoList);
			
			peerGroupImpls.add(peerGroupImpl);		
			routingEntryData3.setPeerGroupList(peerGroupImpls);
			routingEntryData3.setRoutingAction(RoutingActions.PROXY.routingAction);
			
			assertEquals("Unable to Get configured Advanced Condition / RuleSet", 
					routingEntryData3.getAdvancedCondition() , "0:283=\"elitecore.com\"");
			assertEquals("Unable to Get configured Application IDs", 
					routingEntryData3.getApplicationIds() , "0");
			assertTrue("Unable to Get configured Attached Redirection", 
					routingEntryData3.getAttachedRedirection() == false);
			assertEquals("Unable to Get configured Destination Realm", 
					routingEntryData3.getDestRealm() , "Destination Realm");			
			assertEquals("Unable to Get configured Trans Mapping", 
					routingEntryData3.getTransMapName(), "Crestel_rating_Mapping");
			assertEquals("Unable to Get configured Origin Host IP", 
					routingEntryData3.getOriginHostIp(), "*");
			assertEquals("Unable to Get configured Origin Realm", 
					routingEntryData3.getOriginRealm(), "*");
			assertNotNull("Peer Group List must not be Null", 
					routingEntryData3.getPeerGroupList());
			assertTrue("Peer Group List must contain atleast one entry", 
					routingEntryData3.getPeerGroupList().size() > 0);
			//Checking data i=of Peer Group List..
			List<PeerGroupImpl> peerGroups = routingEntryData3.getPeerGroupList();
			for(PeerGroupImpl peerGroup : peerGroups){
				List<PeerInfoImpl> peerInfoImpls = peerGroup.getPeerList();
				for(PeerInfoImpl pInfoImpl : peerInfoImpls){
					
					assertEquals("Peer Group List must contain the values configured Load Factor in Peer Group. Mismatch in Getter/Setter",
							pInfoImpl.getLoadFactor(), peerInfoImpl.getLoadFactor());
					
					assertEquals("Peer Group List must contain the values configured Peer Name in Peer Group. Mismatch in Getter/Setter",
							pInfoImpl.getPeerName(), peerInfoImpl.getPeerName());
					
				}
			}
			assertEquals("Unable to Get configured Routing Action", 
					routingEntryData3.getRoutingAction() , RoutingActions.PROXY.routingAction);
			assertNotNull("Routing Name must NOT be Null", 
					routingEntryData3.getRoutingName());
			assertNotNull("As Failure Arguments are Set for this Test Case, it must not be null", 
					routingEntryData3.getFailoverDataList());
			assertTrue("As Failure Arguments are Set for this Test Case, Failover Data List must contain atleast one entry", 
					routingEntryData3.getFailoverDataList().size() > 0);
			
			routingEntryData3.toString();
		}catch(Exception e){
			fail("Exception Occured:" + e.getMessage());
		}
	}
	
	/**
	 * This method performs a set of Tests on <code>Routing Entry Data</code> Instance 
	 * where getters are called before setters.<br /><br />
	 * 
	 * Hence, this demonstrates use of default as well as NULL values of 
	 * Attributes of this Instance. 
	 */
	@Test
	public void testNullTestWOCallingSetters() {
		RoutingEntryDataImpl routingEntryDataNoSetter =  new RoutingEntryDataImpl();
		assertNull("As Setter is not called yet, Rule Set should be NULL", 
				routingEntryDataNoSetter.getAdvancedCondition());
		assertNull("As Setter is not called yet, Application Id String is Null", 
				routingEntryDataNoSetter.getApplicationIds());
		assertNotNull("As Setter is not called yet, Default value for Attached Redirection is not set", 
				routingEntryDataNoSetter.getAttachedRedirection());
		assertTrue("As Setter is not called yet, Default Value for Attached Redirection must be set to false", 
				routingEntryDataNoSetter.getAttachedRedirection()==false);
		assertTrue("As Setter is not called yet, Default Value for Statefull Routing must be Enabled(true)",
				routingEntryDataNoSetter.getStatefulRouting());
		assertNull("As Setter is not called yet, Dest Realm should be NULL", 
				routingEntryDataNoSetter.getDestRealm());
		assertNull("As Setter is not called yet, Failover Data List should be NULL", 
				routingEntryDataNoSetter.getFailoverDataList());
		assertNull("As Setter is not called yet, OriginHostIP should be NULL", 
				routingEntryDataNoSetter.getOriginHostIp());
		assertNull("As Setter is not called yet, Origin Realm should be NULL", 
				routingEntryDataNoSetter.getOriginRealm());
		assertNull("As Setter is not called yet, Peer Group must be NULL", 
				routingEntryDataNoSetter.getPeerGroupList());
		assertEquals("As Setter is not called yet, Default Routing Action must be PXOXY-->2", RoutingActions.PROXY.routingAction, 
				routingEntryDataNoSetter.getRoutingAction());
		assertEquals("As Setter is not called yet, Default Transaction T/O must be 3000", 
				3000,routingEntryDataNoSetter.getTransActionTimeOut()); 
		assertNull("As Setter is not called yet, Routing Name Must be NULL", 
				routingEntryDataNoSetter.getRoutingName());
		assertNull("As Setter is not called yet, Translation Mapping name must be NULL", 
				routingEntryDataNoSetter.getTransMapName());
	}

}
