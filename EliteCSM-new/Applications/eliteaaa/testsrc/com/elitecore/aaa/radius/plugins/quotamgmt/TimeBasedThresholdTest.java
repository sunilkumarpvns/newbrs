package com.elitecore.aaa.radius.plugins.quotamgmt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
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

public class TimeBasedThresholdTest {
	
	private static final String CLASS_ATTRIBUTE = "0:25";
	private static final String ACCOUNTING_STATUS_TYPE = "0:40";
	private static final String ACCT_SESSION_TIME = "0:46";
	private static final String SESSION_ID = "0:44";	
	private static final String DURATION_TYPE_QUOTA = "TIME";
	private static final int TIME_THRESHOLD_VALUE = 1000;
	private static final int ACTION_ACCEPT = 1;

	private QuotaManagementPluginConfiguration pluginConfiguration;
	private QuotaManagementData pluginPolicyData;

	@Mock private EliteAAAServiceExposerManager exposerManager;

	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	private RadAcctRequest request;
	private RadAcctRequestBuilder requestBuilder;
	private QuotaManagementPlugin plugin;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		pluginConfiguration = new QuotaManagementPluginConfiguration();
		pluginPolicyData = new QuotaManagementData();
		pluginPolicyData.setPrepaidQuotaType(DURATION_TYPE_QUOTA);
		pluginPolicyData.setName("TestPolicy");
		pluginPolicyData.setRuleset("0:1=\"*\"");
		pluginPolicyData.setEnabled(true);
		pluginPolicyData.setKeyForTime("MAX_SESSION_TIME=");
		pluginPolicyData.setAction(ACTION_ACCEPT);
		pluginPolicyData.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE + "");
		pluginConfiguration.getPluginsData().add(pluginPolicyData);

		requestBuilder = new RadAcctRequestBuilder().addAttribute("0:1", "test")
				.addAttribute(ACCOUNTING_STATUS_TYPE, RadiusAttributeValuesConstants.INTERIM_UPDATE + "")
				.addAttribute(SESSION_ID, "123");
		
		plugin = createPlugin();
	}
	
	@Test
	public void isNotDetectedIfKeyForTimeIsNotConfiguredInPolicyData() {
		pluginPolicyData.setKeyForTime(null);

		request = requestBuilder.build();
		
		assertFalse(plugin.isTimeExceeded(request, pluginPolicyData));
	}
	
	@Test
	public void isNotDetectedIfClassAttributeIsMissing() throws Exception {
		request = requestBuilder.build();
		
		assertFalse(plugin.isTimeExceeded(request, pluginPolicyData));
	}
	
	@Test
	public void isNotDetectedIfAcctSessionTimeAttributeIsMissingInRequest() throws Exception {
		request = requestBuilder.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_VOLUME=" + TIME_THRESHOLD_VALUE).build();
		
		assertFalse(plugin.isTimeExceeded(request, pluginPolicyData));
	}
	
	@Test
	public void permittedTimeIsFetchedFromClassAttributeAndHasSecondsAsUnitOfMeasure() throws Exception {
		request = requestBuilder
				.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_TIME=" + TimeUnit.SECONDS.toSeconds(1))
				.addAttribute(ACCT_SESSION_TIME, TimeUnit.SECONDS.toSeconds(1))
				.build();

		assertTrue(plugin.isTimeExceeded(request, pluginPolicyData));
	}
	
	@Test
	public void isNotDetectedIfAcctSessionTimeIsLessThanThreshold() throws Exception {
		request = requestBuilder
				.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_TIME=" + TimeUnit.SECONDS.toSeconds(2))
				.addAttribute(ACCT_SESSION_TIME, TimeUnit.SECONDS.toSeconds(1))
				.build();

		assertFalse(plugin.isTimeExceeded(request, pluginPolicyData));
	}
	
	@Test
	public void isDetectedIfAcctSessionTimeIsEqualToThreshold() throws Exception {
		request = requestBuilder
				.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_TIME=" + TimeUnit.SECONDS.toSeconds(1))
				.addAttribute(ACCT_SESSION_TIME, TimeUnit.SECONDS.toSeconds(1))
				.build();

		assertTrue(plugin.isTimeExceeded(request, pluginPolicyData));
	}
	
	@Test
	public void isDetectedIfAcctSessionTimeIsGreaterThanThreshold() throws Exception {
		request = requestBuilder
				.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_TIME=" + TimeUnit.SECONDS.toSeconds(1))
				.addAttribute(ACCT_SESSION_TIME, TimeUnit.SECONDS.toSeconds(2))
				.build();

		assertTrue(plugin.isTimeExceeded(request, pluginPolicyData));
	}
	
	private QuotaManagementPlugin createPlugin() throws InitializationFailedException {
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
		}, pluginConfiguration, exposerManager);
		plugin.init();
		return plugin;
	}
}
