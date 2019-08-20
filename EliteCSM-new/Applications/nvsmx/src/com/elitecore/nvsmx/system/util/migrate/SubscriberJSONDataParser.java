package com.elitecore.nvsmx.system.util.migrate;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.subscriberimport.InputType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author Chetan.Sankhala
 */
public class SubscriberJSONDataParser implements SubscriberDataParser {

	private static final String MODULE = "SUBS-JSON-DATA-PARSER";
	private static final String BASE_USAGE_KEY = "SUBSCRIPTION BASE PACKAGE";
	private static final String SUBSCRIPTION_KEY = "SUBSCRIPTION ADDONS";
	private static final String SPR_KEY = "SPR";
	
	private final Gson gson;
	
	public SubscriberJSONDataParser(final String dateFormat) {
		this.gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Timestamp>() {

			@Override
			public Timestamp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext arg2) throws JsonParseException {
				String dateString = jsonElement.getAsString();
				if (Strings.isNullOrBlank(dateString)) {
					return null;
				}

				try {
					return new Timestamp(new SimpleDateFormat(dateFormat).parse(dateString).getTime());
				} catch (ParseException e) {
					throw new JsonParseException("Error while parsing date: " + dateString + ". Reason: " + e.getMessage(), e);
				}
			}
		}).create();
	}

	@Override
	public NV648SubscriberInfo parse(String dataString) {

		JsonObject rootJsonObject = gson.fromJson(dataString, JsonObject.class);
		// profile
		JsonArray sprJsonArray = rootJsonObject.get(SPR_KEY).getAsJsonArray();
		JsonElement sprJsonElement = sprJsonArray.get(0);

		Type mapType = new TypeToken<Map<String, String>>() {
			private static final long serialVersionUID = 1L;
		}.getType();

		Map<String, String> valueByColumnNameMap = gson.fromJson(sprJsonElement, mapType);
		validateAccountDataMap(valueByColumnNameMap);
		AccountDataInfo accountDataInfo = new AccountDataInfo(valueByColumnNameMap);

		// subscription
		JsonElement subscriptionJsonElemenet = rootJsonObject.get(SUBSCRIPTION_KEY);

		List<NV648SubscriptionInfo> nv648SubscriptionInfos = new ArrayList<NV648SubscriptionInfo>();

		if (subscriptionJsonElemenet != null) {
			JsonArray subscriptionsJsonArray = subscriptionJsonElemenet.getAsJsonArray();

			for (int index = 0; index < subscriptionsJsonArray.size(); index++) {
				JsonElement subscriptionJsonElement = subscriptionsJsonArray.get(index);
				NV648SubscriptionInfo nv648SubscriptionInfo = gson.fromJson(subscriptionJsonElement, NV648SubscriptionInfo.class);
				nv648SubscriptionInfos.add(nv648SubscriptionInfo);
			}
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Subscription not found from JSON");
			}
		}

		List<NV648BaseUsageInfo> baseUsageInfos = null;
		JsonElement baseJsonElement = rootJsonObject.get(BASE_USAGE_KEY);
		if (baseJsonElement != null) {
			JsonArray baseUsageJsonArray = baseJsonElement.getAsJsonArray();
			if (baseUsageJsonArray != null && baseUsageJsonArray.size() > 0) {

				Type listType = new TypeToken<List<NV648BaseUsageInfo>>() {
					private static final long serialVersionUID = 1L;
				}.getType();

				baseUsageInfos = gson.fromJson(baseUsageJsonArray, listType);
			}
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Base package usage not found from JSON");
			}
		}

		NV648SubscriberInfo subscriberInfo = new NV648SubscriberInfo();
		subscriberInfo.setAccountDataInfo(accountDataInfo);
		subscriberInfo.setSubscriptionInfos(nv648SubscriptionInfos);
		subscriberInfo.setBaseUsageInfos(baseUsageInfos);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Json parsing completed.");
		}

		return subscriberInfo;
	}

	private void validateAccountDataMap(Map<String, String> valueByColumnNameMap) {

		for (Entry<String, String> entryColumnValue : valueByColumnNameMap.entrySet()) {
			if (Strings.isNullOrBlank(entryColumnValue.getValue())) {
				entryColumnValue.setValue(null);
			}
		}
	}

	@Override
	public InputType getInputType() {
		return InputType.JSON;
	}

}
