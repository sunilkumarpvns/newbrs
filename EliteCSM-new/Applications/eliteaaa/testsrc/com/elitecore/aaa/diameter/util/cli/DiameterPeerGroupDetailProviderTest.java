package com.elitecore.aaa.diameter.util.cli;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.impl.DummyAAAServerConfigurationImpl;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.DiameterCompositeConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterPeerConfiguration;
import com.elitecore.aaa.diameter.conf.impl.DiameterPeerGroupConfigurable;
import com.elitecore.aaa.diameter.conf.impl.PeerGroupData;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.util.generator.UUIDGenerator;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.routerx.agent.data.PeerDataProvider;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class DiameterPeerGroupDetailProviderTest {

	private static final String MODULE = "DIA-PEERGRP-DETAIL-PROVIDER-TEST";
	
	private static final String PEERGROUP 	= "peergroup";
	private static final String PEERGROUP1 = "peergroup1";
	private static final String PEERGROUP2 = "peergroup2";
	private static final String PEERGROUP3 = "peergroup3";
	private static final String PEERGROUP4 = "peergroup4";
	
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	private DiameterPeerGroupDetailProvider diameterPeerGroupDetailProvider;
	
	@Mock AAAServerContext serverContext;
	private DiameterPeerGroupConfigurable peerGroupConfigurable = new DiameterPeerGroupConfigurable();
	private DummyAAAServerConfigurationImpl dummyAAAServerConfiguration = new DummyAAAServerConfigurationImpl();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		when(serverContext.getServerConfiguration()).thenReturn(dummyAAAServerConfiguration);
		
		dummyAAAServerConfiguration.setDiameterPeerGroupConfigurable(peerGroupConfigurable);
		
		addPeers();
		addPeerGroups();
		
		diameterPeerGroupDetailProvider = new DiameterPeerGroupDetailProvider(serverContext);
	}
	

	@Test
	public void getKeyReturns_peergroup() {
		assertThat(diameterPeerGroupDetailProvider.getKey(), is(equalTo(PEERGROUP)));
	}
	
	@Test
	public void displaysDescription() {
		assertThat(diameterPeerGroupDetailProvider.getDescription(), is(equalTo("Display Configuration details of Diameter Peer Group")));
	}

	@Test
	@Parameters({"?","-help","-HELP","-HeLp","-HeLP","-hELP","-HELp","-helP"})
	public void helpMessageIsDisplayedIfArgumentIsHelp(String cmdArgument) {
		String cmdOutput = diameterPeerGroupDetailProvider.execute(new String[] { cmdArgument });

		String helpMsg ="\nUsage 	 : show diameter config peergroup [<Peer Group Name>]"+"\nDescription: Displays Configuration Details of All Peer Groups."+"\n(If provided with Peer Group Name, displays details of that Peer Group.)";

		assertThat(cmdOutput , is(equalTo(helpMsg)));
	}
	
	@Test
	public void displayAllConfiguredPeerGroupSummary() {
		
		String cmdOutput = diameterPeerGroupDetailProvider.execute(new String[] {});
		
		LogManager.getLogger().info(MODULE,"\n" + cmdOutput);
		
		assertThat(cmdOutput, is(containsString(PEERGROUP1)));
		assertThat(cmdOutput, is(containsString(PEERGROUP2)));
		assertThat(cmdOutput, is(containsString(PEERGROUP3)));
		
		assertThat(cmdOutput, not(containsString(PEERGROUP4)));
	}
	
	@Test
	public void displaysConfiguredPeerGroupSummaryUsingPeerGroupName() {
		
		String cmdOutput = diameterPeerGroupDetailProvider.execute(new String[] { PEERGROUP2 });
		
		LogManager.getLogger().info(MODULE,"\n" + cmdOutput);
		
		assertThat(cmdOutput, is(containsString(PEERGROUP2)));
		
		assertThat(cmdOutput, not(containsString(PEERGROUP3)));
		assertThat(cmdOutput, not(containsString(PEERGROUP4)));
	}
	
	@Test
	public void peerGroupSummaryIsNotDisplayedIfPeerGroupNameDoesNotExist() {
		
		String cmdOutput = diameterPeerGroupDetailProvider.execute(new String[] { "invalidName" });
		
		assertThat(cmdOutput, is(equalTo("Peer Group: invalidName Does not exists.")));
	}
	
	private void addPeerGroups() {
		
		PeerGroupData peerGroupData = new PeerGroupData();
		peerGroupData.setName(PEERGROUP1);
		peerGroupData.setRetryLimit(3);
		peerGroupData.setStateFull(true);
		String uuid = UUIDGenerator.generate();
		peerGroupData.setGeoRedunduntGroupId("");
		peerGroupData.setId(uuid);
		
		
		PeerInfoImpl peer1 = new PeerInfoImpl();
		peer1.setLoadFactor(5);
		peer1.setPeerName("peer1");
		peerGroupData.getPeers().add(peer1);
		
		PeerInfoImpl peer2 = new PeerInfoImpl();
		peer2.setLoadFactor(2);
		peer2.setPeerName("peer2");
		peerGroupData.getPeers().add(peer2);
		
		peerGroupConfigurable.getPeerGroups().add(peerGroupData);
		
		PeerGroupData peerGroupData2 = new PeerGroupData();
		peerGroupData2.setName(PEERGROUP2);
		peerGroupData2.setRetryLimit(4);
		peerGroupData2.setStateFull(true);
		peerGroupData2.setId(UUIDGenerator.generate());
		peerGroupData2.setGeoRedunduntGroupId(uuid);
		
		PeerInfoImpl peer3 = new PeerInfoImpl();
		peer3.setLoadFactor(8);
		peer3.setPeerName("peer3");
		peerGroupData2.getPeers().add(peer3);
		
		peerGroupConfigurable.getPeerGroups().add(peerGroupData2);
		
		PeerGroupData peerGroupData3 = new PeerGroupData();
		peerGroupData3.setName(PEERGROUP3);
		peerGroupData3.setRetryLimit(8);
		peerGroupData3.setStateFull(true);
		peerGroupData3.setId(UUIDGenerator.generate());
		peerGroupData3.setGeoRedunduntGroupId("");
		
		PeerInfoImpl peer4 = new PeerInfoImpl();
		peer4.setLoadFactor(8);
		peer4.setPeerName("peer4");
		peerGroupData3.getPeers().add(peer4);
		
		peerGroupConfigurable.getPeerGroups().add(peerGroupData3);
		
		peerGroupConfigurable.postRead();
	}
	
	private void addPeers() {

		String realmName = "elitecore.com";
		List<PeerData> peerDataList = new ArrayList<PeerData>();

		PeerData peerData1 =new PeerDataProvider().withPeerName("peer1")
				.withHostIdentity("peer1.elitecore.com")
				.withRealmName(realmName)
				.build();
		
		peerDataList.add(peerData1);

		PeerData peerData2 =new PeerDataProvider().withPeerName("peer2")
				.withHostIdentity("peer2.elitecore.com")
				.withRealmName(realmName)
				.build();
				
		peerDataList.add(peerData2);
		
		PeerData peerData3 =new PeerDataProvider().withPeerName("peer3")
				.withHostIdentity("peer3.elitecore.com")
				.withRealmName(realmName)
				.build();
		
		peerDataList.add(peerData3);
		
		PeerData peerData4 =new PeerDataProvider().withPeerName("peer4")
				.withHostIdentity("peer4.elitecore.com")
				.withRealmName(realmName)
				.build();
				
		peerDataList.add(peerData4);
		
		DiameterPeerConfiguration diameterCompositeConfigurable = new DiameterCompositeConfigurable();
		
		dummyAAAServerConfiguration.setDiameterPeerConfiguration(diameterCompositeConfigurable);
		dummyAAAServerConfiguration.getDiameterPeerConfiguration().getPeerDataList().addAll(peerDataList);
	}
}
