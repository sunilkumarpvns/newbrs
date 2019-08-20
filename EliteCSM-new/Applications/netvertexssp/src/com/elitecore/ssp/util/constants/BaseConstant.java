package com.elitecore.ssp.util.constants;

import java.text.SimpleDateFormat;


public class BaseConstant {
		public static final int DESCRIPTION_LENGTH=20;
		public static final String WEBSERVICE_CONFIG_FILE_LOCATION="WEB-INF/webservice-config.properties";
		public static final String CLIENT_DEPLOY_FILE_LOCATION="/WEB-INF/client-deploy.wsdd";
		public static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		public static final String SUBSCRIBE_PROFILER_FILE_LOCATION="/WEB-INF/subscribe-profiler.properties";
		public static final String AVAILABLE_ADDON_SEARCH_CRITERIA="availableAddOnCriteria";
}
