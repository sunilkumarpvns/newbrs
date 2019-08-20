package com.elitecore.aaa.radius.plugins.quotamgmt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementData;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConfiguration;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.base.RadAcctRequestBuilder;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;


public class VolumeAndTimeBasedThresholdTest {


	private static final String CLASS_ATTRIBUTE = "0:25";
	private static final String ACCT_OUTPUT_OCTETS = "0:43";
	private static final String ACCT_INPUT_OCTETS = "0:42";
	private static final String ACCT_OUTPUT_GIGAWORDS = "0:53";
	private static final String ACCT_INPUT_GIGAWORDS = "0:52";
	private static final String ACCOUNTING_STATUS_TYPE = "0:40";
	private static final String ACCT_SESSION_TIME = "0:46";
	private static final String SESSION_ID = "0:44";	

	private static final String BOTH_TYPE_QUOTA = "BOTH";

	private static final int TWO_GW_IN_KB = (int) ((Math.pow(2, 32) * 2) / 1024);
	
	private static final int ONE_GW = 1;

	private static final String BYTES_512 = "512";

	private static final int ONE_KB = 1;

	
	private static final int ACTION_ACCEPT = 1;


	@Mock private RadClientData clientData;
	@Mock private EliteAAAServiceExposerManager exposerManager;

	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	private QuotaManagementPluginConfiguration pluginConfiguration;
	private QuotaManagementData pluginPolicyData;
	private QuotaManagementPlugin plugin;
	private RadAcctRequest request;
	private RadAcctRequestBuilder requestBuilder;


	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		when(clientData.getSharedSecret(anyInt())).thenReturn("secret");
		pluginConfiguration = new QuotaManagementPluginConfiguration();
		pluginPolicyData = new QuotaManagementData();
		pluginPolicyData.setName("TestPolicy");
		pluginPolicyData.setRuleset("0:1=\"*\"");
		pluginPolicyData.setEnabled(true);
		pluginPolicyData.setKeyForTime("MAX_SESSION_TIME=");
		pluginPolicyData.setKeyForVolume("MAX_SESSION_VOLUME=");
		pluginConfiguration.getPluginsData().add(pluginPolicyData);

		requestBuilder = new RadAcctRequestBuilder().addAttribute("0:1", "test")
				.addAttribute(ACCOUNTING_STATUS_TYPE, RadiusAttributeValuesConstants.INTERIM_UPDATE + "")
				.addAttribute(SESSION_ID, "123");


		pluginPolicyData.setPrepaidQuotaType(BOTH_TYPE_QUOTA);
		pluginPolicyData.setAction(ACTION_ACCEPT);
		pluginPolicyData.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE + "");
		pluginPolicyData.setFurtherProcessing("true");

		plugin = createPlugin();
	}

	@Test
	public void isNotDetectedIfClassAttributeForTimeIsMissing() throws Exception {
		request = requestBuilder.build();

		assertFalse(plugin.isBothVolumeAndDurationExceeded(request, pluginPolicyData));
	}
	
	@Test
	public void isNotDetectedIfClassAttributeForVolumeIsMissing() throws Exception {
		request = requestBuilder.build();

		assertFalse(plugin.isBothVolumeAndDurationExceeded(request, pluginPolicyData));
	}

	@Test
	public void isDetectedIfVolumeAndTimeGreaterThanThreshold() throws Exception {

		request = new RadAcctRequestBuilder()
		.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_VOLUME="  + (TWO_GW_IN_KB + ONE_KB))
		.addAttribute(ACCT_INPUT_OCTETS, BYTES_512)
		.addAttribute(ACCT_OUTPUT_OCTETS, BYTES_512)
		.addAttribute(ACCT_INPUT_GIGAWORDS, ONE_GW)
		.addAttribute(ACCT_OUTPUT_GIGAWORDS, ONE_GW + 1)
		.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_TIME=" + TimeUnit.SECONDS.toSeconds(1))
		.addAttribute(ACCT_SESSION_TIME, TimeUnit.SECONDS.toSeconds(2)).build();

		assertTrue(plugin.isBothVolumeAndDurationExceeded(request, pluginPolicyData));

	}

	@Test
	public void isDetectedIfOnlyConsumedVolumeIsGreaterThanThreshold() throws Exception {

		request = new RadAcctRequestBuilder()
		.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_VOLUME="  + (TWO_GW_IN_KB + ONE_KB))
		.addAttribute(ACCT_INPUT_OCTETS, BYTES_512)
		.addAttribute(ACCT_OUTPUT_OCTETS, BYTES_512)
		.addAttribute(ACCT_INPUT_GIGAWORDS, ONE_GW)
		.addAttribute(ACCT_OUTPUT_GIGAWORDS, ONE_GW + 1 )
		.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_TIME=" + TimeUnit.SECONDS.toSeconds(2))
		.addAttribute(ACCT_SESSION_TIME, TimeUnit.SECONDS.toSeconds(1)).build();

		assertTrue(plugin.isBothVolumeAndDurationExceeded(request, pluginPolicyData));

	}

	@Test
	public void isDetectedIfOnlyAcctSessionTimeIsGreaterThanThreshold() throws Exception {

		request = new RadAcctRequestBuilder()
		.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_VOLUME="  + TWO_GW_IN_KB)
		.addAttribute(ACCT_OUTPUT_GIGAWORDS, ONE_GW)
		.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_TIME=" + TimeUnit.SECONDS.toSeconds(1))
		.addAttribute(ACCT_SESSION_TIME, TimeUnit.SECONDS.toSeconds(2)).build();

		assertTrue(plugin.isBothVolumeAndDurationExceeded(request, pluginPolicyData));

	}

	private QuotaManagementPlugin createPlugin() throws InitializationFailedException {
		pluginConfiguration.postRead();
		QuotaManagementPlugin plugin = new QuotaManagementPlugin( new PluginContext() {

			@Override
			public ServerContext getServerContext() {
				return null;
			}

			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return null;
			}
		}, pluginConfiguration, exposerManager);
		plugin.init();
		return plugin;
	}

}
