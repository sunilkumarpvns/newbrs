package com.elitecore.aaa.radius.service.plugins.auth;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.elitecore.aaa.core.commons.util.exception.AttributeNotFoundException;
import com.elitecore.aaa.core.commons.util.exception.InvalidPacketException;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.plugins.UniversalAuthPlugin;
import com.elitecore.aaa.radius.service.auth.plugins.conf.impl.UniversalAuthPluginCofigurable;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.service.plugins.data.UniversalPluginTestCaseData;
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
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.impl.FunctionFailTestCase;

@RunWith(JUnitParamsRunner.class)
public class UniversalAuthPluginTest {

	private static final String MODULE = "UNIVERSAL-AUTH-PLUGIN-TEST";
	private static final String DUMMY_ARGUMENT = "";
	private static final String PRE_POLICIES = "auth/universalpluginxml/prepolicy/";
	private static final String POST_POLICIES = "auth/universalpluginxml/postpolicy/";
	private UniversalAuthPlugin universalAuthPlugin;
	private PluginCallerIdentity plg ;
	private UniversalAuthPluginCofigurable configurable = new UniversalAuthPluginCofigurable();

	@Mock private PluginContext pluginContext;
	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	static {
		RadiusDictionaryTestHarness.getInstance();
	}
	
	@BeforeClass
	public static void registerFailFunction() {
		Compiler.getDefaultCompiler().addFunction(new FunctionFailTestCase());
	}

	@Test
	public void throwsInitializationFailedExceptionIfPluginDataIsNull() throws InitializationFailedException{
		PluginInfo pluginInfo = null;
		universalAuthPlugin = new UniversalAuthPlugin(pluginContext,pluginInfo);

		exception.expect(InitializationFailedException.class);
		exception.expectMessage("Universal Auth Plugin configuration is null.");
		universalAuthPlugin.init();
	}
	
	public static Object[] dataFor_testPrePolicy() throws FileNotFoundException, UnsupportedEncodingException, JAXBException, InvalidExpressionException {

		String[] testcaseFiles = {
				"universal-plugin-pre-policy-check-item-and-reject-item-configuration.xml",
				"universal-plugin-pre-policy-add-item-update-item-filter-item-replace-item-reply-item-configuration-data.xml",
				"universal-plugin-pre-policy-data-with-action-reject.xml",
				"universal-plugin-pre-policy-data-with-action-accept.xml",
				"universal-plugin-pre-policy-data-with-action-none.xml",
				"universal-plugin-pre-policy-data-with-action-drop.xml",
				"universal-plugin-pre-policy-data-with-action-incorrect.xml",
				"eap-massge-and-eap-massage-authenticator-attribute-with-action-reject.xml",
				"eap-massge-and-eap-massage-authenticator-attribute-with-action-accept.xml",
				"universal-plugin-multiple-pre-policy-configuration-data.xml"
		};
		
		return readPluginTestCaseDataFrom(testcaseFiles,PRE_POLICIES).toArray();
	}
	
	public static Object[] dataFor_testPostPolicy() throws FileNotFoundException, UnsupportedEncodingException, JAXBException, InvalidExpressionException {
		String[] testcaseFiles = {
			"universal-plugin-post-policy-check-item-and-reject-item-configuration.xml",
			"universal-plugin-post-policy-data-with-action-reject.xml",
			"universal-plugin-post-policy-data-with-action-accept.xml",
			"universal-plugin-post-policy-data-with-action-none.xml",
			"universal-plugin-post-policy-data-with-action-incorrect.xml",
			"universal-plugin-post-policy-add-item-update-item-filter-item-replace-item-reply-item-configuration-data.xml",
			"universal-plugin-multiple-post-policy-configuration-data.xml",
			"universal-plugin-post-policy-data-with-action-drop.xml"
		};
		
		return readPluginTestCaseDataFrom(testcaseFiles,POST_POLICIES).toArray();
	}
	
	private static List<UniversalPluginTestCaseData> readPluginTestCaseDataFrom(String[] paths, String policyType) throws FileNotFoundException, UnsupportedEncodingException, JAXBException {
		List<UniversalPluginTestCaseData> caseCollection = new ArrayList<UniversalPluginTestCaseData>();
		for (String path : paths) {
			caseCollection.addAll(readPluginTestCaseDataFrom("radius/service/" + policyType + path));
		}
		return caseCollection;
	}
	
	private static List<UniversalPluginTestCaseData> readPluginTestCaseDataFrom(String path) throws FileNotFoundException, UnsupportedEncodingException, JAXBException {
		ClasspathResource resource = ClasspathResource.at(path);
		return ConfigUtil.<ListWrapper<UniversalPluginTestCaseData>>deserialize(new File(resource.getAbsolutePath()), ListWrapper.class, UniversalPluginTestCaseData.class).getList();
	}
	
	@Test
	@Parameters(method = "dataFor_testPrePolicy")
	public void testPrePolicy(UniversalPluginTestCaseData testCaseData) throws Exception {
		parsePolicyData(testCaseData);
		initPlugin(testCaseData);

		RadServiceRequest request = createRequest(testCaseData);
		RadServiceResponse response = createResponse(testCaseData);

		universalAuthPlugin.handlePreRequest(request, response, DUMMY_ARGUMENT, plg, ISession.NO_SESSION);
		
		assertThat(request, testCaseData.getAssertion().createRequestMatcher());
		assertThat(response, testCaseData.getAssertion().createResponseMatcher());
		
	}
	
	@Test
	@Parameters(method = "dataFor_testPostPolicy")
	public void testPostPolicy(UniversalPluginTestCaseData testCaseData) throws Exception {
		parsePolicyData(testCaseData);
		initPlugin(testCaseData);

		RadServiceRequest request = createRequest(testCaseData);
		RadServiceResponse response = createResponse(testCaseData);

		universalAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, plg, ISession.NO_SESSION);

		assertThat(request, testCaseData.getAssertion().createRequestMatcher());
		assertThat(response, testCaseData.getAssertion().createResponseMatcher());
		
	}
	
	private void initPlugin(UniversalPluginTestCaseData testCaseData)
			throws InitializationFailedException {
		universalAuthPlugin = new UniversalAuthPlugin(pluginContext, testCaseData.getUniversalPluginAuthData());
		universalAuthPlugin.init();
	}
	
	public void parsePolicyData(UniversalPluginTestCaseData testCaseData) {
		
		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");
		
		configurable.getData().add(testCaseData.getUniversalPluginAuthData());
		
		configurable.postProcessing();
	}
	
	private RadServiceResponse createResponse(UniversalPluginTestCaseData testCaseData)
			throws AttributeNotFoundException, InvalidPacketException {
		return testCaseData.getResponsePacketData().createResponse(new RadAuthRequestBuilder());
	}


	private RadServiceRequest createRequest(UniversalPluginTestCaseData testCaseData)
			throws Exception {
		return testCaseData.getRequestPacketData().create(new RadAuthRequestBuilder());
	}
}
