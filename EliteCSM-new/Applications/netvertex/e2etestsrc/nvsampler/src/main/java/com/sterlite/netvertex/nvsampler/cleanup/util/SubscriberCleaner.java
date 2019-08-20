package com.sterlite.netvertex.nvsampler.cleanup.util;

import com.sterlite.netvertex.nvsampler.cleanup.Result;


import static com.sterlite.netvertex.nvsampler.cleanup.util.CleanUpUtils.getSmContextPath;
import static javax.ws.rs.HttpMethod.DELETE;

public class SubscriberCleaner {

	private static final String DELETE_TBLT_SESSION = "DELETE FROM TBLT_SESSION WHERE SUBSCRIBER_IDENTITY=?";
	public static final String DELETE_FROM_TBLT_SUBSCRIPTION = "DELETE FROM TBLT_SUBSCRIPTION WHERE SUBSCRIBER_ID=?";
	private DBQueryExecutor dbQueryExecutor;
	private HTTPConnector httpConnector;

	public SubscriberCleaner(DBQueryExecutor dbQueryExecutor, HTTPConnector httpConnector) {
		this.dbQueryExecutor = dbQueryExecutor;
		this.httpConnector = httpConnector;
	}

	public Result cleanSubscriber(String subscriberId) {
		Result result = new Result("Clean Subscriber: " + subscriberId);
		result.addResult(deleteSubscriber(subscriberId));
		result.addResult(purgeSubscriber(subscriberId));
		result.addResult(cleanSubscriptions(subscriberId));
		result.addResult(cleanSession(subscriberId));
		return result;
	}

	private Result deleteSubscriber(String subscriberId) {
		Result result = new Result("Delete Subscriber: " + subscriberId);
		String url = new StringBuilder(getSmContextPath()).append("/rest/restful/subscriberProvisioning/deleteSubscriber?subscriberId=")
				.append(subscriberId).toString();
		result.addResult(httpConnector.connect(url, DELETE));
		return result;
	}

	private Result purgeSubscriber(String subscriberId) {
		Result result = new Result("Purge Subscriber: " + subscriberId);
		String url = new StringBuilder(getSmContextPath())
				.append("/rest/restful/subscriberProvisioning/purgeSubscriber?subscriberId=")
				.append(subscriberId).toString();
		result.addResult(httpConnector.connect(url, DELETE));
		return result;
	}

	private Result cleanSubscriptions(String subscriberId) {
		Result result = new Result("Clean Subscription");
		result.addResult(dbQueryExecutor.executeUpdate(DELETE_FROM_TBLT_SUBSCRIPTION, subscriberId));
		return result;
	}

	private Result cleanSession(String subscriberId) {
		Result result = new Result("Clean Session");
		result.addResult(dbQueryExecutor.executeUpdate(DELETE_TBLT_SESSION, subscriberId));
		return result;
	}
}