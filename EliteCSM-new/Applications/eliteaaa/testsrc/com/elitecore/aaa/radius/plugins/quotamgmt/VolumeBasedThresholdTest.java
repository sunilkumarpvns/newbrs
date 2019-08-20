package com.elitecore.aaa.radius.plugins.quotamgmt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class VolumeBasedThresholdTest {

	private static final String CLASS_ATTRIBUTE = "0:25";
	private static final String ACCT_OUTPUT_OCTETS = "0:43";
	private static final String ACCT_INPUT_OCTETS = "0:42";
	private static final String ACCT_OUTPUT_GIGAWORDS = "0:53";
	private static final String ACCT_INPUT_GIGAWORDS = "0:52";
	private static final String ACCOUNTING_STATUS_TYPE = "0:40";
	private static final String SESSION_ID = "0:44";	

	private static final String VOLUME_TYPE_QUOTA = "VOLUME";

	private static final int ONE_KB_IN_BYTES = 1024;

	private static final int TWO_GW_IN_KB = (int) ((Math.pow(2, 32) * 2) / 1024);

	private static final int ONE_KB = 1;

	private static final String TWO_GW = "2";

	private static final String ONE_GW = "1";

	private static final String BYTES_512 = "512";

	private static final int ACTION_ACCEPT = 1;

	@Mock private EliteAAAServiceExposerManager dummyExposerManager;

	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	private QuotaManagementPluginConfiguration pluginConfiguration;
	private QuotaManagementData pluginPolicyData;
	private RadAcctRequest request;
	private RadAcctRequestBuilder requestBuilder;
	private QuotaManagementPlugin plugin;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		pluginConfiguration = new QuotaManagementPluginConfiguration();
		pluginPolicyData = new QuotaManagementData();
		pluginPolicyData.setName("TestPolicy");
		pluginPolicyData.setRuleset("0:1=\"*\"");
		pluginPolicyData.setEnabled(true);
		pluginPolicyData.setKeyForVolume("MAX_SESSION_VOLUME=");
		pluginPolicyData.setAction(ACTION_ACCEPT);
		pluginPolicyData.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE + "");
		pluginConfiguration.getPluginsData().add(pluginPolicyData);

		requestBuilder = new RadAcctRequestBuilder().addAttribute("0:1", "test")
				.addAttribute(ACCOUNTING_STATUS_TYPE, RadiusAttributeValuesConstants.INTERIM_UPDATE + "")
				.addAttribute(SESSION_ID, "123");
		pluginPolicyData.setPrepaidQuotaType(VOLUME_TYPE_QUOTA);

		plugin = createPlugin();
	}

	@Test
	public void isNotDetectedIfKeyForVolumeIsNotConfiguredInPolicyData() {
		pluginPolicyData.setKeyForVolume(null);
		request = requestBuilder.build();

		assertFalse(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void isNotDetectedIfClassAttributeIsMissingInRequest() throws Exception {
		request = requestBuilder.build();

		assertFalse(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void isNotDetectedIfAllUsageSpecificAttributesAreAbsentInRequest() throws Exception {
		request = requestBuilder.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_VOLUME=" + ONE_KB_IN_BYTES).build();

		assertFalse(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void permittedVolumeIsFetchedFromClassAttributeAndHasKiloByteAsUnitOfMeasure() throws Exception {
		request = requestBuilder
				.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_VOLUME=" + TWO_GW_IN_KB)
				.addAttribute(ACCT_OUTPUT_GIGAWORDS, TWO_GW)
				.build();

		assertTrue(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void isNotDetectedIfConsumedVolumeIsLessThanThreshold() throws Exception {
		request = requestBuilder
				.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_VOLUME=" + ONE_KB_IN_BYTES)
				.addAttribute(ACCT_INPUT_OCTETS, "1").build();

		assertFalse(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void isDetectedIfConsumedVolumeIsEqualToThreshold() throws Exception {
		request = requestBuilder
				.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_VOLUME=" + TWO_GW_IN_KB)
				.addAttribute(ACCT_OUTPUT_GIGAWORDS, TWO_GW)
				.build();

		assertTrue(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void isDetectedIfConsumedVolumeIsGreaterThanThreshold() throws Exception {
		request = requestBuilder
				.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_VOLUME=" + TWO_GW_IN_KB)
				.addAttribute(ACCT_OUTPUT_GIGAWORDS, TWO_GW)
				.addAttribute(ACCT_OUTPUT_OCTETS, "1")
				.build();

		assertTrue(plugin.isVolumeExceeded(request, pluginPolicyData));
	}


	@Test
	public void sumOfAcctInputGigawordsInBytesAndOutputGigawordsInBytesIsConvertedToKilobytes() throws Exception {
		givenThresholdOf(TWO_GW_IN_KB);

		request = requestBuilder.addAttribute(ACCT_INPUT_GIGAWORDS, ONE_GW)
				.addAttribute(ACCT_OUTPUT_GIGAWORDS, ONE_GW).build();

		assertTrue(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void reportedAcctInputGigawordsIsEqualToThreshold() throws Exception {
		givenThresholdOf(TWO_GW_IN_KB);

		request = requestBuilder.addAttribute(ACCT_INPUT_GIGAWORDS, TWO_GW).build();

		assertTrue(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void reportedAcctOutputGigawordsIsEqualToThreshold() throws Exception {
		givenThresholdOf(TWO_GW_IN_KB);

		request = requestBuilder.addAttribute(ACCT_OUTPUT_GIGAWORDS, TWO_GW).build();

		assertTrue(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void sumOfAcctInputOctetsAndOutputOctetsIsConvertedToKilobytes() throws Exception {
		givenThresholdOf(ONE_KB);

		request = requestBuilder
				.addAttribute(ACCT_INPUT_OCTETS, BYTES_512)
				.addAttribute(ACCT_OUTPUT_OCTETS, BYTES_512)
				.build();

		assertTrue(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void reportedAcctInputOctetsIsEqualToThreshold() throws Exception {
		givenThresholdOf(ONE_KB);

		request = requestBuilder.addAttribute(ACCT_INPUT_OCTETS, ONE_KB_IN_BYTES).build();

		assertTrue(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void reportedAcctOutputOctetsIsEqualToThreshold() throws Exception {
		givenThresholdOf(ONE_KB);

		request = requestBuilder.addAttribute(ACCT_OUTPUT_OCTETS, ONE_KB_IN_BYTES).build();

		assertTrue(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	@Test
	public void sumOfGigawordAttributesAndOctetAttributesIsEqualToThreshold() throws Exception {
		givenThresholdOf(TWO_GW_IN_KB + ONE_KB);

		request = requestBuilder
				.addAttribute(ACCT_INPUT_OCTETS, BYTES_512)
				.addAttribute(ACCT_OUTPUT_OCTETS, BYTES_512)
				.addAttribute(ACCT_INPUT_GIGAWORDS, ONE_GW)
				.addAttribute(ACCT_OUTPUT_GIGAWORDS, ONE_GW)
				.build();

		assertTrue(plugin.isVolumeExceeded(request, pluginPolicyData));
	}

	private void givenThresholdOf(int threshold)
			throws InvalidAttributeIdException {
		requestBuilder.addAttribute(CLASS_ATTRIBUTE, "MAX_SESSION_VOLUME=" + threshold);
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
		}, pluginConfiguration, dummyExposerManager);
		plugin.init();
		return plugin;
	}

}
