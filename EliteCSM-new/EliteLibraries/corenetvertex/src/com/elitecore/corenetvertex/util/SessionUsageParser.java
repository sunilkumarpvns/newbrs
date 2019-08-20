package com.elitecore.corenetvertex.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.PackageUsage;
import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.spr.balance.Usage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SessionUsageParser {

	private static final String QUOTAPROFILEID_KEY = "q";

	private static final String PACKAGEID_KEY = "p";

	private static final String TIME_KEY = "ti";

	private static final String TOTAL_KEY = "t";

	private static final String DOWNLOAD_KEY = "d";

	private static final String UPLOAD_KEY = "u";

	/**
	 * 
	 * Sample JSON output format;
	 * <PRE>
	 * 	{
	 *  	"<subscriptionOrPackageId>": {
	 *  		"p": "<packageId>",
	 *  		"q": {
	 *  			"<quotaprofileId>": {
	 *  				"<serviceId>": {
	 *   					"u": "100",
	 *   					"d": "100",
	 *   					"t": "100",
	 *   					"ti": "100"
	 *  				}
	 *  			}
	 *  		}
	 *  	}
	 *  }
	 *  
	 *  Example of one base package and one subscription usage json:
	 *  
	 *  {
	 *  	"b3b2e96d-8a25-43b4-a6a6-71feb3db3045": {
	 *  		"p": "b3b2e96d-8a25-43b4-a6a6-71feb3db3045",
	 *  		"q": {
	 *  			"9957569a-0a34-4c4f-8828-0770261d700c": {
	 *  				"SERVICE_TYPE_3": {
	 *  					"t": 300,
	 *  					"d": 200,
	 *  					"u": 100,
	 *  					"ti": 10
	 *   				}
	 *  			}
	 *  		}
	 *  	},
	 *  	"5536e1fb-b3b5-44ec-b908-1c91d6be9fc3": {
	 *  		"p": "31aa1bdf-d414-4afa-bb0f-a57c5aabc1a3",
	 *  		"q": {
	 *  			"bc5d46f0-57fb-4548-90df-57eb4193cfe4": {
	 *  				"SERVICE_TYPE_2": {
	 *  					"t": 250,
	 *  					"d": 100,
	 *  					"u": 50,
	 *  					"ti": 10
	 *  				}
	 *  			}
	 *  		}
	 *  	}
	 *  }
	 * </PRE>
	 * 
	 * @param sessionUsage
	 * @return
	 */
	public static String serialize(@Nullable SessionUsage sessionUsage) {

		if (sessionUsage == null) {
			return null;
		}

		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append(CommonConstants.OPENING_BRACES);

		for (Entry<String, Map<String, PackageUsage>> entrySubscriptionUsage : sessionUsage.getAllSubscriptionWiseUsage()) {

			StringBuilder subscriptionJsonBuilder = serializePackage(entrySubscriptionUsage.getValue());
			
			if (subscriptionJsonBuilder.length() == 0) {
				continue;
			}

			if (jsonBuilder.length() > 1) {
				jsonBuilder.append(CommonConstants.COMMA);
			}
			
			/*
			 * Building subscription or package block
			 */
			jsonBuilder.append(CommonConstants.DOUBLE_QUOTES)
					.append(entrySubscriptionUsage.getKey())
					.append(CommonConstants.DOUBLE_QUOTES)
					.append(CommonConstants.COLON)
					.append(CommonConstants.OPENING_BRACES).append(subscriptionJsonBuilder)
					.append(CommonConstants.CLOSING_BRACES);

		}

		jsonBuilder.append(CommonConstants.CLOSING_BRACES);

		return jsonBuilder.toString();
	}

	private static StringBuilder serializePackage(
			Map<String, PackageUsage> quotaProfileServiceIdWiseUsage) {

		String packageId = null;

		StringBuilder subscriptionJsonBuilder = new StringBuilder();

		Map<String, StringBuilder> quotaProfileWiseJsonBuilder = new HashMap<String, StringBuilder>();

		/*
		 * This flag is used to skip json building for package id and quota profile block when only one invalid entry is found.
		 * 
		 * IF at least one correct entry found THEN skipFlag set to false
		 */
		boolean skipFlag = true;
		
		for (Entry<String, PackageUsage> entryQuotaProfileServiceWiseToUsage : quotaProfileServiceIdWiseUsage
				.entrySet()) {

			String[] entryQuotaProfileIdServiceId = entryQuotaProfileServiceWiseToUsage.getKey().split(CommonConstants.USAGE_KEY_SEPARATOR);
			if (entryQuotaProfileIdServiceId.length != 2) {
				continue;
			}
			
			skipFlag = false;
			
			String quotaProfileId = entryQuotaProfileIdServiceId[0];

			/*
			 * building service and usage block
			 */
			StringBuilder serviceUsageJsonBuilder = quotaProfileWiseJsonBuilder
					.get(quotaProfileId);

			if (serviceUsageJsonBuilder == null) {
				serviceUsageJsonBuilder = new StringBuilder();
				quotaProfileWiseJsonBuilder.put(quotaProfileId,
						serviceUsageJsonBuilder);
			} else {
				serviceUsageJsonBuilder.append(CommonConstants.COMMA);
			}

			PackageUsage packageUsage = entryQuotaProfileServiceWiseToUsage
					.getValue();

			serializeService(serviceUsageJsonBuilder, packageUsage);
			packageId = packageUsage.getPackageId();
		}
		
		if (skipFlag) {
			return subscriptionJsonBuilder;
		}

		/*
		 * building 'p' block that represents package id
		 */
		subscriptionJsonBuilder.append(CommonConstants.DOUBLE_QUOTES)
		.append(PACKAGEID_KEY)
		.append(CommonConstants.DOUBLE_QUOTES)
		.append(CommonConstants.COLON)
		.append(CommonConstants.DOUBLE_QUOTES)
		.append(packageId)
		.append(CommonConstants.DOUBLE_QUOTES)
		.append(CommonConstants.COMMA)
		.append(CommonConstants.DOUBLE_QUOTES)
		.append(QUOTAPROFILEID_KEY)
		.append(CommonConstants.DOUBLE_QUOTES)
		.append(CommonConstants.COLON)
		.append(CommonConstants.OPENING_BRACES);

		/*
		 * building 'q' block that represents all quota profile usage
		 */
		for (Entry<String, StringBuilder> entryQuotaProfileIdWiseJsonBuilder : quotaProfileWiseJsonBuilder
				.entrySet()) {
					subscriptionJsonBuilder.append(CommonConstants.DOUBLE_QUOTES)
					.append(entryQuotaProfileIdWiseJsonBuilder.getKey())
					.append(CommonConstants.DOUBLE_QUOTES)
					.append(CommonConstants.COLON)
					.append(CommonConstants.OPENING_BRACES)
					.append(entryQuotaProfileIdWiseJsonBuilder.getValue())
					.append(CommonConstants.CLOSING_BRACES)
					.append(CommonConstants.COMMA);
		}
		subscriptionJsonBuilder.delete(subscriptionJsonBuilder.length() - 1, subscriptionJsonBuilder.length());

		subscriptionJsonBuilder.append(CommonConstants.CLOSING_BRACES);

		return subscriptionJsonBuilder;
	}

	private static void serializeService(StringBuilder serviceUsageJsonbuilder,
			PackageUsage packageUsage) {
		serviceUsageJsonbuilder.append(CommonConstants.DOUBLE_QUOTES)
				.append(packageUsage.getServiceId())
				.append(CommonConstants.DOUBLE_QUOTES)
				.append(CommonConstants.COLON);
		serializeUsage(serviceUsageJsonbuilder, packageUsage.getUsage());
	}

	private static void serializeUsage(StringBuilder builder, Usage usage) {

		builder.append(CommonConstants.OPENING_BRACES);

		builder.append(CommonConstants.DOUBLE_QUOTES);
		builder.append(TOTAL_KEY);
		builder.append(CommonConstants.DOUBLE_QUOTES);
		builder.append(CommonConstants.COLON);
		builder.append(usage.getTotalOctets());
		builder.append(CommonConstants.COMMA);

		builder.append(CommonConstants.DOUBLE_QUOTES);
		builder.append(DOWNLOAD_KEY);
		builder.append(CommonConstants.DOUBLE_QUOTES);
		builder.append(CommonConstants.COLON);
		builder.append(usage.getDownloadOctets());
		builder.append(CommonConstants.COMMA);

		builder.append(CommonConstants.DOUBLE_QUOTES);
		builder.append(UPLOAD_KEY);
		builder.append(CommonConstants.DOUBLE_QUOTES);
		builder.append(CommonConstants.COLON);
		builder.append(usage.getUploadOctets());
		builder.append(CommonConstants.COMMA);

		builder.append(CommonConstants.DOUBLE_QUOTES);
		builder.append(TIME_KEY);
		builder.append(CommonConstants.DOUBLE_QUOTES);
		builder.append(CommonConstants.COLON);
		builder.append(usage.getTime());

		builder.append(CommonConstants.CLOSING_BRACES);
	}

	public static SessionUsage deserialize(@Nullable String sessionUsageJsonStr) {

		if (Strings.isNullOrBlank(sessionUsageJsonStr)) {
			return null;
		}

		JsonObject sessionUsageJsonObject = GsonFactory.defaultInstance().fromJson(
				sessionUsageJsonStr, JsonObject.class);

		Map<String, Map<String, PackageUsage>> subscriptionOrPackageWiseUsages = new HashMap<String, Map<String, PackageUsage>>();
		for (Entry<String, JsonElement> entryPackageUsage : sessionUsageJsonObject.entrySet()) {
			subscriptionOrPackageWiseUsages
					.put(entryPackageUsage.getKey(),
							createPackageUsage(entryPackageUsage.getKey(),
									entryPackageUsage.getValue()));
		}
		
		SessionUsage sessionUsage = new SessionUsage();
		sessionUsage.setPackageUsage(subscriptionOrPackageWiseUsages);

		return sessionUsage;
	}

	private static Map<String, PackageUsage> createPackageUsage(
			String subscriptionIdOrPackageId, JsonElement packageUsageJsonElement) {

		Map<String, PackageUsage> quotaProfileAnsServiceWiseUsage = new HashMap<String, PackageUsage>();

		JsonObject packageUsageJsonObject = packageUsageJsonElement.getAsJsonObject();

		String packageId = packageUsageJsonObject.get(PACKAGEID_KEY).getAsString();
		String subscriptionId = subscriptionIdOrPackageId.equals(packageId) ? null
				: subscriptionIdOrPackageId;

		Set<Entry<String, JsonElement>> entryQuotaProfiles = packageUsageJsonObject.get(QUOTAPROFILEID_KEY)
				.getAsJsonObject().entrySet();

		for (Entry<String, JsonElement> entryQuotaProfile : entryQuotaProfiles) {

			String quotaProfileId = entryQuotaProfile.getKey();

			for (Entry<String, JsonElement> entryServiceWiseUsage : entryQuotaProfile
					.getValue().getAsJsonObject().entrySet()) {
				String serviceId = entryServiceWiseUsage.getKey();

				Usage usage = createUsage(entryServiceWiseUsage.getValue());

				PackageUsage packageUsage = new PackageUsage(packageId,
						subscriptionId, quotaProfileId, serviceId, usage);
				quotaProfileAnsServiceWiseUsage.put(quotaProfileId
						+ CommonConstants.USAGE_KEY_SEPARATOR + serviceId,
						packageUsage);
			}
		}

		return quotaProfileAnsServiceWiseUsage;

	}

	private static Usage createUsage(JsonElement usageJsonElement) {

		long uploadOctets = 0;
		long downloadOctets = 0;
		long totalOctets = 0;
		long time = 0;

		JsonObject usageJsonObject = usageJsonElement.getAsJsonObject();
		JsonElement uploadJsonElement = usageJsonObject.get(SessionUsageParser.UPLOAD_KEY);
		if (uploadJsonElement != null) {
			uploadOctets = uploadJsonElement.getAsLong();
		}

		JsonElement downloadJsonElement = usageJsonObject
				.get(SessionUsageParser.DOWNLOAD_KEY);
		if (downloadJsonElement != null) {
			downloadOctets = downloadJsonElement.getAsLong();
		}

		JsonElement totalJsonElement = usageJsonObject.get(SessionUsageParser.TOTAL_KEY);
		if (totalJsonElement != null) {
			totalOctets = totalJsonElement.getAsLong();
		}

		JsonElement timeJsonElement = usageJsonObject.get(SessionUsageParser.TIME_KEY);
		if (timeJsonElement != null) {
			time = timeJsonElement.getAsLong();
		}

		return new Usage(uploadOctets, downloadOctets, totalOctets, time);
	}
}