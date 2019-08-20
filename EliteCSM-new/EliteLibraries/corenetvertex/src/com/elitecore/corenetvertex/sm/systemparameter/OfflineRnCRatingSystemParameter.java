package com.elitecore.corenetvertex.sm.systemparameter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This enum contains default value for rating system parameters. If value doesn't exists in db, then this value will be used.
 *
 * @author vijayrajsinh
 */

public enum OfflineRnCRatingSystemParameter {

	INSERT_ZERO_USAGE_CDR("Insert Zero usage CDR ", "Yes", "Yes: CDR will be generated for zerousage.  No: CDR will not be generated for zerousage.") {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    },

	MULTIPLE_VALUE_SEPARATOR("Multiple Value Separator", ";", "This parameter specifies the string separator, if any request contains multiple value for same key, then this separator will be used.") {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    },

	BACK_DATED_UNRATED_EDR_PROCESSING_DAYS("Back Dated Unrated EDR Processing (Days)", "30", "This parameter specifies the number of days till when backdated unrated EDRs are to be processed. Prior to this duration, EDRs will not be considered for Rating.") {
        @Override
        public boolean validate(String value) {
            return NUMERIC_REGEX.matcher(value).matches() && Integer.parseInt(value) < 1000;
        }
    },

	RATING_SERVICE_CACHE_INTERVAL_SEC("Rating Service Cache Interval (Sec)", "86400", "The value defines the time interval after which the system will reload the rating cache.") {
    	@Override
        public boolean validate(String value) {
            return NUMERIC_REGEX.matcher(value).matches();
        }
    },

	RATED_EDR_LOCAL_ARCHIVE_DIRECTORY_JSON("Rated EDR Local Archive Directory (JSON)", "data/rated-edr","This parameter specifies the path of local archive directory") {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    },
	
	NUMBER_OF_DECIMAL_POINTS_IN_TRANSACTION("Number of decimal points in Transaction", "6","This parameter defines the default configuration of number of decimal points for rating.") {
    	@Override
        public boolean validate(String value) {
            return NUMERIC_REGEX.matcher(value).matches();
        }
    },
    
	ROUNDING_CURRENCY_TO_SPECIFIED_DECIMAL_POINT("Rounding currency to specified decimal point", "TRUNCATE", "This parameter will rounding the currency to specified number of decimal point. For example, if currency has value 45.3423215. If this parameter is false then value will be 45.342321 and if this parameter is true then 45.342322") {
    	@Override
        public boolean validate(String value) {
            return NUMERIC_REGEX.matcher(value).matches();
        }
    },

	EDR_DATE_TIMESTAMP_FORMAT("EDR Date Timestamp Format", "dd-MM-yyyy HH:mm:ss", "This parameter contains simpledate format. This parameter will be used in parsing Session-Connect-Time, Session-Disconnect-Time,Processed-Date") {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    },
	
	SYSTEM_TIMEZONE("System Timezone", "IST", "This parameter contains the timezone of the system.") {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    },
	
	RATE_SELECTION_WHEN_DATE_CHANGE("Rate Selection When Date Change", "SESSION-CONNECT-TIME", "This parameter identifies that when Connect time and Disconnect time is different then which time will be considered for Rate Card Selection. It will be useful when we have connect time in Peak hour and Disconnect Time in Off-peak hour. Second use case is Connect time is on 21st Janurary 2018 and Disconnect Time is on 22nd January 2018.") {
    	@Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    };

    private String name;
    private String value;
    private String description;
    private static final Pattern NUMERIC_REGEX = Pattern.compile("^[0-9]*$");
    private static Map<String, OfflineRnCRatingSystemParameter> nameMap;

    static {
        nameMap = new HashMap<>();
        for (OfflineRnCRatingSystemParameter systemParameter : values()) {
            nameMap.put(systemParameter.name(), systemParameter);
        }
    }

    OfflineRnCRatingSystemParameter(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }


    public String getDescription() {
        return description;
    }

    public abstract boolean validate(String value);

    public static OfflineRnCRatingSystemParameter fromName(String name) {
        return nameMap.get(name);
    }
}
