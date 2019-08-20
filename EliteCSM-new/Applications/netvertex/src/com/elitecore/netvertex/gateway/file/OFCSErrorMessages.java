package com.elitecore.netvertex.gateway.file;


public enum OFCSErrorMessages {

	ALREADY_INTIALIZED("is failed to start as DBManager is already initialized"),
	CONFIG_DIR_NOT_FOUND("Failed to find config folder for reading configuration"),
	CONFIGURATION_LOADING_COMPLETED("has been loaded successfully"),
	CONFIGURATION_LOADING_FAILED("Failed to load configuration file for"),
	CONFIGURATION_LOADING_START("configuration has been started loading"),
	CONFIGURATION_READING_FAILED("Failed to read configuration for"),
	CONTACT_LICENSE_SUPPORT_TEAM("Please contact license support team for help via mail : support.license@elitecore.com"),
	DB_NAME_INFO("is configured for"),
	DISABLE_STATUS("is disabled"),
	ENGINE_HOME_ENV_NOT_FOUND("Define the Environment Variable For CRESTEL_P_ENGINE_HOME which is a home directory for Mediation Server"),
	FULL_LICENSE_CHECK_FAILED("Crestel-P-Engine full license has been expired"),
	FULL_LICENSE_EXPIRY_DATE_STATUS("Crestel-P-Engine full license is about to expired on"),
	INITIALIZATION_COMPLETED(" has been initialized successfully"),
	INITIALIZATION_FAIL("is failed to initialize"),
	INITIALIZATION_STARTED("Initilization started for the"),
	INVALID_ALERT_ID("Invalid Alert ID found; Alert ID -"),
	INVALID_PARAMETER("is failed to initialize due to invalid parameter configuration; Parameter:"),
	IO_FAILURE("Failed to write data bytes into a data file as"),
	LICENSE_CHECK_FAILED("License activation has been failed"),
	LICENSE_DECODING_FAILURE("System is unable to read license key file; it may be corrupted"),
	LICENSE_DECRYPTION_FAILURE("System is failed to decrypt license message"),
	LICENSE_ENCRYPTION_FAILURE("System is failed to encrypt license message"),
	LOG_DIRECTORY_CREATION_FAILED("Failed to create log directory"),
	MBEAN_REGISTRATION_FAILURE("Failed to register Mbean property"),
	NOT_SUPPORTED("Database type not supported"),
	SERVICE_ADDED_SUCCESSFULLY("has been added successfully"),
	SERVICE_ADDING_FAILED(" is failed to add"),
	START_UP_FAILED("is failed to startup"),
	START_UP_TIME(" is started on"),
	STARTING_STATE("Service has been invoked on server startup"),
	STARTED_STATE("has been started successfully"),
	COMPLETED_STATE("has been completed successfully"),
	STREAM_CLOSE_FAILURE("Data writing stream may be stopped to write data bytes into a data file."),
	TRAIL_LICENSE_CHECK_FAILED("Crestel-P-Engine trial license has been expired"),
	TRAIL_LICENSE_EXPIRY_DATE_STATUS("Crestel-P-Engine trial license is about to expired on"),
	ENTRY_ADDING_FAILED_IN_TABLE("Error Occured while adding service entry in service table. Service Name :"),
	BATCH_EXECUTION_STARTED("Batch Execution is started for"),
	BATCH_EMPTY("No entities found for processing of"),
	EXECUTION_FAILED("Error occured in execution for"),
	RELOAD_CONFIG_START("Reloading configuration started for"),
	RELOAD_CONFIG_FAIL("Reloading configuration failed for"),
	RELOAD_CONFIG_COMPLETE("Reloading configuration completed successfully for"),
	SCHEDULING_DAY_RANGE("Scheduling day should be in range 1 to 7 for"),
	SCHEDULING_TIME_RANGE("Scheduling Time should properly configured for"),
	SCHEDULING_DATE_RANGE("Scheduling date should be in range 1 to 28 for"),
	DIR_CREATION_FAILED("Directory creation Failed -"),
	DB_CONNECTION_ISSUE("Error occured while creating a db connection and preparedstatements"),
	HANDLE_REQUEST_FAIL("Error occured while handling the request for"),
	STATS_INSERT_IN_DB_FAILS("Error while insert cdr statistics"),
	DB_CONNECTION_CLOSE_ISSUE("Error occured while closing a db connection/prepared statements"),
	RENAME_FAILURES("Could not rename to"),
	FILE_LISTING_FAILS("Error occured while getting file name list"),
	PARSING_DATE_FAILED("Parsing date failed"),
	PARSING_PACKET_FAILED("Parsing packet fails");

	private String message;
	private static final String SPACE = " ";
	private static final String REASON = ", - Reason :";
	private final static int MAX_LENGTH = 30;
	private final static char PADDING_CHAR = '*';

	private OFCSErrorMessages(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String generateTraceMessage(String reason){
		return messageBuilder(message, REASON, reason);
	}

	public String generateLogMessage(String reason,String suffix,String prefix){
		return messageBuilder(prefix,message,suffix,REASON,reason);
	}

	public String generateTraceMessage(Throwable throwable){
		return messageBuilder(message,REASON,throwable.getMessage());
	}

	public String generateTraceMessage(Throwable throwable,String prefix,String suffix){

		return messageBuilder(prefix,message,suffix,REASON,throwable.getMessage());
	}

	public String getMessage(String prefix,String suffix){

		return messageBuilder(prefix,message,suffix);
	}

	private String messageBuilder(String... messageTokenArray){

		StringBuilder stringBuilder = new StringBuilder();
		if(messageTokenArray != null){
			for(String messageToken : messageTokenArray){
				if(messageToken != null && messageToken.length() > 0){
					stringBuilder.append(messageToken);
					stringBuilder.append(SPACE);
				}
			}
		}
		return stringBuilder.toString();
	}

	public static String formatModuleName(String moduleName){
		int paddingCount = 0;
		if(moduleName.length() < MAX_LENGTH){
			paddingCount = MAX_LENGTH - moduleName.length();
			return moduleName+getPaddingChars(paddingCount);
		}else if(moduleName.length() == MAX_LENGTH){
			return moduleName;
		}else{
			return moduleName.substring(0, MAX_LENGTH);
		}
	}

	private static String getPaddingChars(int length){
		StringBuilder stBuilder = new StringBuilder();
		if(length > 0){
			while(!(length-- == 0)){
				stBuilder.append(PADDING_CHAR);
			}
		}
		return stBuilder.toString();
	}

}

