package com.elitecore.corenetvertex.sm.systemparameter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This enum contains default value for file system parameters. If value doesn't exists in db, then this value will be used.
 *
 * @author vijayrajsinh
 */

public enum OfflineRnCFileSystemParameter {

	FAILED_FILE_EXTENSION("Failed file extension", ".failed", "Define file extension for failed file. Defulat is .failed, for example file name is sms_records.csv then file will be generate with sms_records.csv.failed for fail records.") {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    },

	RE_RATING_PROCESS_RECORD_BATCH_SIZE("Re-rating process record batch size", "5000", "Define number of records for rerating in one batch so process will generate batches base on size define in this parameter.") {
    	@Override
        public boolean validate(String value) {
            return NUMERIC_REGEX.matcher(value).matches();
        }
    },

	ERROR_PROCESSING_RECORD_BATCH_SIZE("Error Processing record batch size", "5000", "Define number of re-processing records from error file in one batch so process will generate batches base on size define in this parameter.") {
        @Override
        public boolean validate(String value) {
            return NUMERIC_REGEX.matcher(value).matches();
        }
    },

	EDR_NOT_RECEIVED_THRESHOLD_DAYS("EDR not received Threshold ( Days)", "1", "An alert will be generated if the no EDR received from EMS in configured days.For example, if configured number is 2 days & On today, if no EDR received Today, Today-1, Today-2 then notification will be sent.") {
        @Override
        public boolean validate(String value) {
            return NUMERIC_REGEX.matcher(value).matches();
        }
    };
	
    private String name;
    private String value;
    private String description;
    private static final Pattern NUMERIC_REGEX = Pattern.compile("^[0-9]*$");
    private static Map<String, OfflineRnCFileSystemParameter> nameMap;

    static {
        nameMap = new HashMap<>();
        for (OfflineRnCFileSystemParameter systemParameter : values()) {
            nameMap.put(systemParameter.name(), systemParameter);
        }
    }

    OfflineRnCFileSystemParameter(String name, String value, String description) {
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

    public static OfflineRnCFileSystemParameter fromName(String name) {
        return nameMap.get(name);
    }
}
