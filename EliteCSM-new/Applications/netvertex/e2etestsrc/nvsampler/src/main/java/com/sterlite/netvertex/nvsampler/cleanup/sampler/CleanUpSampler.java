package com.sterlite.netvertex.nvsampler.cleanup.sampler;

import com.sterlite.netvertex.nvsampler.cleanup.Result;
import com.sterlite.netvertex.nvsampler.cleanup.util.DBQueryExecutor;
import com.sterlite.netvertex.nvsampler.cleanup.util.HTTPConnector;
import com.sterlite.netvertex.nvsampler.cleanup.util.MiscCleaner;
import com.sterlite.netvertex.nvsampler.cleanup.util.PolicyCleaner;
import com.sterlite.netvertex.nvsampler.cleanup.util.SamplerLogger;
import com.sterlite.netvertex.nvsampler.cleanup.util.SubscriberCleaner;
import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class CleanUpSampler extends AbstractJavaSamplerClient {
	public static final String SUBSCRIBER_ID = "subscriber-id";
	public static final String PRODUCT_OFFER_ID = "product-offer-id";
	public static final String DATA_PACKAGE_ID = "data-package-id";
	public static final String DATA_TOPUP_ID = "data-topup-id";
	public static final String RNC_PACKAGE_ID = "rnc-package-id";
	public static final String EMAIL_TEMPLATE_ID = "email-template-id";
	public static final String SMS_TEMPLATE_ID = "sms-template-id";
	public static final String BOD_PACKAGE_ID = "bod-package-id";
	public static final String COMMA = ",";
	public static final String CLEAN_UP_COMPLETED = "Clean Up Completed";

	private PolicyCleaner policyCleaner;
	private SubscriberCleaner subscriberCleaner;
	private MiscCleaner miscCleaner;

	@Override
	public void setupTest(JavaSamplerContext context) {
		DBQueryExecutor dbQueryExecutor = new DBQueryExecutor();
		HTTPConnector httpConnector = new HTTPConnector();
		this.policyCleaner = new PolicyCleaner(dbQueryExecutor, httpConnector);
		this.subscriberCleaner = new SubscriberCleaner(dbQueryExecutor, httpConnector);
		this.miscCleaner = new MiscCleaner(dbQueryExecutor);
		SamplerLogger.setLogger(getLogger());
	}

	@Override
	public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
		SampleResult sampleResult = new SampleResult();
		sampleResult.sampleStart();

		getLogger().info("Clean Up Started.");

		Result resultSummary = new Result("Result Summary");

		String subscriberId = javaSamplerContext.getParameter(SUBSCRIBER_ID);
		if (StringUtils.isEmpty(subscriberId) == false) {
			getLogger().info("Cleaning Subscriber Balances for subscriber: " + subscriberId);
			resultSummary.addResult(subscriberCleaner.cleanSubscriber(subscriberId));
		}

		String poIds = javaSamplerContext.getParameter(PRODUCT_OFFER_ID);
		if (StringUtils.isEmpty(poIds) == false) {
			resultSummary.addResults(policyCleaner.cleanProductOffer(poIds.split(COMMA)));
		}

		String dataPackageIds = javaSamplerContext.getParameter(DATA_PACKAGE_ID);
		if (StringUtils.isEmpty(dataPackageIds) == false) {
			resultSummary.addResults(policyCleaner.cleanDataPackage(dataPackageIds.split(COMMA)));
		}

		String rnCPackageIds = javaSamplerContext.getParameter(RNC_PACKAGE_ID);
		if (StringUtils.isEmpty(rnCPackageIds) == false) {
			resultSummary.addResults(policyCleaner.cleanRnCPackages(rnCPackageIds.split(COMMA)));
		}

		String dataTopUpIds = javaSamplerContext.getParameter(DATA_TOPUP_ID);
		if (StringUtils.isEmpty(dataTopUpIds) == false) {
			resultSummary.addResults(policyCleaner.cleanDataTopUpIds(dataTopUpIds.split(COMMA)));
		}

		String bodPackageIds = javaSamplerContext.getParameter(BOD_PACKAGE_ID);
		if (StringUtils.isEmpty(bodPackageIds) == false) {
			resultSummary.addResults(policyCleaner.cleanBoDPackage(bodPackageIds.split(COMMA)));
		}

		String emailTemplateId = javaSamplerContext.getParameter(EMAIL_TEMPLATE_ID);
		if (StringUtils.isEmpty(emailTemplateId) == false) {
			getLogger().info("Cleaning Email Template for Id: " + emailTemplateId);
			resultSummary.addResult(miscCleaner.cleanTemplate(emailTemplateId));
		}

		String smsTemplateId = javaSamplerContext.getParameter(SMS_TEMPLATE_ID);
		if (StringUtils.isEmpty(smsTemplateId) == false) {
			getLogger().info("Cleaning SMS Template for Id: " + smsTemplateId);
			resultSummary.addResult(miscCleaner.cleanTemplate(smsTemplateId));
		}

		getLogger().info(CLEAN_UP_COMPLETED);
		sampleResult.setResponseCodeOK();
		sampleResult.setSuccessful(true);
		sampleResult.setResponseMessage(CLEAN_UP_COMPLETED);
		sampleResult.setResponseData(resultSummary.toString().getBytes());
		sampleResult.sampleEnd();
		return sampleResult;
	}

	@Override
	public Arguments getDefaultParameters() {
		Arguments arguments = new Arguments();
		arguments.addArgument(SUBSCRIBER_ID, "${subscriber_id}");
		arguments.addArgument(PRODUCT_OFFER_ID, "${base_product_offer_1}");
		arguments.addArgument(DATA_PACKAGE_ID, "${base_data_package_1}");
		arguments.addArgument(RNC_PACKAGE_ID, "${base_rnc_package_1}");
		arguments.addArgument(DATA_TOPUP_ID, "${data_topup_1}");
		arguments.addArgument(BOD_PACKAGE_ID, "${bod_package_1}");
		arguments.addArgument(EMAIL_TEMPLATE_ID, "${email_template_1}");
		arguments.addArgument(SMS_TEMPLATE_ID, "${sms_template_1}");
		return arguments;
	}

	@Override
	protected Logger getLogger() {
		return LoggingManager.getLoggerForClass();
	}
}
