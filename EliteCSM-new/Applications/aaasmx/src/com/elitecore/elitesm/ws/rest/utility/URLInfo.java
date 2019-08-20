package com.elitecore.elitesm.ws.rest.utility;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.elitecore.commons.base.Strings;

public class URLInfo {

	private static final String STATUS_RECORD = "statusRecord";
	private static final String PARTIAL_SUCCESS = "partialSuccess";

	public static String isPartialSuccess(UriInfo url){
		return checkFlag(url,PARTIAL_SUCCESS);
	}

	public static String isStatusRecord(UriInfo url){
		return checkFlag(url,STATUS_RECORD);
	}

	private static String checkFlag(UriInfo url, String flagValue) {
		
		if(url != null){
			MultivaluedMap<String,String> queryParameters = url.getQueryParameters();
			if(queryParameters.containsKey(flagValue)){
				for (Entry<String, List<String>> element : queryParameters.entrySet()) {
					if(element.getKey().equals(flagValue)){
						if(Strings.isNullOrBlank(element.getValue().get(0)) == false){
							if(element.getValue().get(0).equalsIgnoreCase("true") || element.getValue().get(0).equalsIgnoreCase("false")){
								return element.getValue().get(0);
							}
						}
					}
				}
			}
		}
		return "";
	}
}
