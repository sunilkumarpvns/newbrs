package com.elitecore.aaa.radius.plugins.quotamgmt;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementData;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConfiguration;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.base.RadAcctRequestBuilder;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class AttributesToAddInPacketTest {
	
	private static final int ACTION_ACCEPT = 1;
	private static final String CLASS_ATTRIBUTE = "0:25";
	private static final String ACCOUNTING_STATUS_TYPE = "0:40";
	private static final String SESSION_ID = "0:44";	
	private static final String DURATION_TYPE_QUOTA = "TIME";
	private static final String ACCT_SESSION_TIME = "0:46";
	private static final PluginCallerIdentity DUMMY_IDENTITY = null;
	private static final String DUMMY_ARGUMENT = "";
	
	private QuotaManagementPluginConfiguration pluginConfiguration;
	private QuotaManagementData pluginPolicyData;
	private RadAcctRequest request;
	private RadAcctResponse response;
	private RadAcctRequestBuilder requestBuilder;
	private QuotaManagementPlugin plugin;
	
	@Mock private RadClientData clientData;
	@Mock private EliteAAAServiceExposerManager dummyExposerManager;
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		createRadRequest();
		
		createRadResponse();
	}
	
	private void createRadRequest() throws Exception {
		requestBuilder = new RadAcctRequestBuilder().addAttribute("0:1", "test")
				.addAttribute(ACCOUNTING_STATUS_TYPE, RadiusAttributeValuesConstants.INTERIM_UPDATE + "")
				.addAttribute("0:4", "127.0.0.1")
				.addAttribute(SESSION_ID, "123")
				.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_TIME=" + TimeUnit.SECONDS.toSeconds(1))
				.addAttribute(ACCT_SESSION_TIME, TimeUnit.SECONDS.toSeconds(1));

		request = requestBuilder.build();
	}

	private void setUpConfigurationOfPlugin(String listOfAttributes) {
		pluginConfiguration = new QuotaManagementPluginConfiguration();
		
		pluginPolicyData = new QuotaManagementData();
		pluginPolicyData.setName("TestPolicy");
		pluginPolicyData.setRuleset("0:1=\"*\"");
		pluginPolicyData.setEnabled(true);
		pluginPolicyData.setKeyForTime("MAX_SESSION_TIME=");
		pluginPolicyData.setKeyForVolume("MAX_SESSION_VOLUME=");
		pluginPolicyData.setAction(ACTION_ACCEPT);
		pluginPolicyData.setStrAttributes(listOfAttributes);
		pluginPolicyData.setPrepaidQuotaType(DURATION_TYPE_QUOTA);
		pluginPolicyData.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE + "");
		pluginPolicyData.setFurtherProcessing("true");
		
		pluginConfiguration.getPluginsData().add(pluginPolicyData);
	}
	
	private void createRadResponse() throws Exception {
		
		when(clientData.getSharedSecret(anyInt())).thenReturn("secret");
		
		response = new RadAcctRequestBuilder().buildResponse(request);
		response.setClientData(clientData);
	}
	
	private QuotaManagementPlugin createPlugin() throws Exception {
		
		pluginConfiguration.postRead();
		QuotaManagementPlugin plugin = new QuotaManagementPlugin(new PluginContext() {

			@Override
			public ServerContext getServerContext() {
				return null;
			}

			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return null;
			}
		}, pluginConfiguration, dummyExposerManager);
		
		plugin.init();
		
		return plugin;
	}
	
	
	@Test
	public void attributesWithValidConfigurationMustBeAddedInConfiguredPacketType() throws Exception {
		
		setUpConfigurationOfPlugin("0:1,0:31=\"\\\"12345678\\\"\",0:4=0:4,0:46=");
		
		plugin = createPlugin();
		
		plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
		
		RadiusPacket capturedPacket = capturePacket();
		
		assertThat(capturedPacket.getRadiusAttribute("0:1").getStringValue(), is("test"));
		assertThat(capturedPacket.getRadiusAttribute("0:31").getStringValue(), is("12345678"));
		assertThat(capturedPacket.getRadiusAttribute("0:4").getStringValue(), is("127.0.0.1"));
		assertThat(capturedPacket.getRadiusAttribute("0:46").getStringValue(), is("1"));
		
	}
	
	private String[][] data_ForonlyInvalidConfiguredAttributesMustbeSkippedAndRestofMustbeAddedInConfiguredPacketType(){
		
		return new String[][]{
			{"0:1,0;31=0:1,0:4d=0:4"},
			{"0:1,0;31=0:d1,0:4/,invalid:5,0:d"},
			{"0:1,fddfg"}
		};
	}
	
	@Test
	@Parameters(method="data_ForonlyInvalidConfiguredAttributesMustbeSkippedAndRestofMustbeAddedInConfiguredPacketType")
	public void onlyInvalidConfiguredAttributesMustbeSkippedAndRestofMustbeAddedInConfiguredPacketType(String attributes) throws Exception {
		
		setUpConfigurationOfPlugin(attributes);
		
		plugin = createPlugin();
		
		plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
		
		RadiusPacket capturedPacket = capturePacket();
		
		assertThat(capturedPacket.getRadiusAttribute("0:1").getStringValue(), is("test"));
		assertNull(capturedPacket.getRadiusAttribute("0:31"));
		assertNull(capturedPacket.getRadiusAttribute("0:4"));
		
	}

	
	private RadiusPacket capturePacket() throws Exception {
		
		ArgumentCaptor<RadiusPacket> packetCapture = ArgumentCaptor.forClass(RadiusPacket.class);
		verify(dummyExposerManager).sendBlockingLocalRequest(packetCapture.capture());
		
		return packetCapture.getValue();
	}
}
