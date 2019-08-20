package com.elitecore.aaa.diameter.plugin;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.plugin.data.UniversalDiameterPluginTestCaseData;
import com.elitecore.aaa.diameter.plugins.conf.UniversalDiameterPluginConfigurable;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.config.ListWrapper;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.plugins.universal.UniversalDiameterPlugin;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

@RunWith(JUnitParamsRunner.class)
public class UniversalDiameterPluginTest {

	private static final String MODULE = "UNIVERSAL-DIAMETER-PLUGIN-TEST";
	
	private static final String UNIVERSAL_DIAMETER_PLUGIN_IN_POLICY = "universaldiameterpluginxmls/inpolicy/";
	private static final String UNIVERSAL_DIAMETER_PLUGIN_OUT_POLICY = "universaldiameterpluginxmls/outpolicy/";
	private static final String DUMMY_ARGUMENT = "";
	
	private UniversalDiameterPlugin universalDiameterPlugin;
	private PluginCallerIdentity dummyPluginCallerIdentity ;
	private UniversalDiameterPluginConfigurable configurable = new UniversalDiameterPluginConfigurable(); 

	@Mock private PluginContext pluginContext;
	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	static {
		DummyDiameterDictionary.getInstance();
	}
	
	@Test
	public void throwsInitializationFailedExceptionIfPluginDataIsNull() throws InitializationFailedException{
		PluginInfo pluginInfo = null;
		universalDiameterPlugin = new UniversalDiameterPlugin(pluginContext,pluginInfo);

		exception.expect(InitializationFailedException.class);
		exception.expectMessage("Universal Plugin configuration is null");
		universalDiameterPlugin.init();
	}
	
	public static Object[] dataFor_testInPolicy() throws FileNotFoundException, UnsupportedEncodingException, JAXBException, InvalidExpressionException {

		String[] testcaseFiles = {
				"universal-diameter-plugin-multiple-in-policy-configuration-data.xml",
				"universal-diameter-plugin-in-policy-check-item-and-reject-item-configuration.xml",
				"universal-diameter-plugin-in-policy-add-item-filter-item-replace-item-reply-item-configuration-data.xml"
		};
		
		return readPluginTestCaseDataFrom(UNIVERSAL_DIAMETER_PLUGIN_IN_POLICY,testcaseFiles).toArray();
	}
	
	@Test
	@Parameters(method = "dataFor_testInPolicy")
	public void testInPolicy(UniversalDiameterPluginTestCaseData testCaseData) throws Exception {
		
		parasePlicyData(testCaseData);
		initPlugin(testCaseData);
		
		DiameterPacket diameterRequest = testCaseData.getRequestPacket().create();
		DiameterPacket diameterAnswer = testCaseData.getResponsePacket().create();
		
		universalDiameterPlugin.handleInMessage(diameterRequest, diameterAnswer, ISession.NO_SESSION, DUMMY_ARGUMENT, dummyPluginCallerIdentity);

		assertThat(diameterRequest.getAsDiameterRequest(), testCaseData.getAssertion().createRequestMatcher());
		assertThat(diameterAnswer.getAsDiameterAnswer(), testCaseData.getAssertion().createResponseMatcher());
	}
	
	public static Object[] dataFor_testOutPolicy() throws FileNotFoundException, UnsupportedEncodingException, JAXBException, InvalidExpressionException {

		String[] testcaseFiles = {
				"universal-diameter-plugin-multiple-out-policy-configuration-data.xml",
				"universal-diameter-plugin-out-policy-check-item-and-reject-item-configuration.xml",
				"universal-diameter-plugin-out-policy-add-item-filter-item-replace-item-reply-item-configuration-data.xml"
		};
		
		return readPluginTestCaseDataFrom(UNIVERSAL_DIAMETER_PLUGIN_OUT_POLICY,testcaseFiles).toArray();
	}
	
	@Test
	@Parameters(method = "dataFor_testOutPolicy")
	public void testOutPolicy(UniversalDiameterPluginTestCaseData testCaseData) throws Exception {
		
		parasePlicyData(testCaseData);
		initPlugin(testCaseData);
		
		DiameterPacket diameterRequest = testCaseData.getRequestPacket().create();
		DiameterPacket diameterAnswer = testCaseData.getResponsePacket().create();
		
		universalDiameterPlugin.handleOutMessage(diameterRequest, diameterAnswer, ISession.NO_SESSION, DUMMY_ARGUMENT, dummyPluginCallerIdentity);

		assertThat(diameterRequest.getAsDiameterRequest(), testCaseData.getAssertion().createRequestMatcher());
		assertThat(diameterAnswer.getAsDiameterAnswer(), testCaseData.getAssertion().createResponseMatcher());
	}
	
	private static List<UniversalDiameterPluginTestCaseData> readPluginTestCaseDataFrom(String policyType, String[] paths) throws FileNotFoundException, UnsupportedEncodingException, JAXBException {
		List<UniversalDiameterPluginTestCaseData> caseCollection = new ArrayList<UniversalDiameterPluginTestCaseData>();
		for (String path : paths) {
			caseCollection.addAll(readPluginTestCaseDataFrom(policyType + path));
		}
		return caseCollection;
	}
	
	private static List<UniversalDiameterPluginTestCaseData> readPluginTestCaseDataFrom(String path) throws FileNotFoundException, UnsupportedEncodingException, JAXBException {
		ClasspathResource resource = ClasspathResource.at(path);
		return ConfigUtil.<ListWrapper<UniversalDiameterPluginTestCaseData>>deserialize(new File(resource.getAbsolutePath()), ListWrapper.class, UniversalDiameterPluginTestCaseData.class).getList();
	}
	
	private void parasePlicyData(UniversalDiameterPluginTestCaseData testCaseData) {
		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");
		
		configurable.getData().add(testCaseData.getDiameterUniversalPluginData());
		
		configurable.postReadProcessing();
	}

	private void initPlugin(UniversalDiameterPluginTestCaseData testCaseData)
			throws InitializationFailedException {
		universalDiameterPlugin = new UniversalDiameterPlugin(pluginContext, testCaseData.getDiameterUniversalPluginData());
		universalDiameterPlugin.init();
	}
}
