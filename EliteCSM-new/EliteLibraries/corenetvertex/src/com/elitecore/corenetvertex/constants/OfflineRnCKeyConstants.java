package com.elitecore.corenetvertex.constants;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OfflineRnCKeyConstants {
	
	ACCT_SESSION_ID("ACCT-SESSION-ID"),
	LINE_OF_BUSINESS("LINE-OF-BUSINESS"),
	SERVICE_NAME("SERVICE-NAME"),
	RATING_STREAM("RATING-STREAM"),
	ACCOUNT_IDENTIFIER("ACCOUNT-IDENTIFIER", "Account Identifier value received in incoming EDR"),
	SESSION_CONNECT_TIME("SESSION-CONNECT-TIME", "Time when session started", true),
	SESSION_DISCONNECT_TIME("SESSION-DISCONNECT-TIME", "Time when session ended", true),
	SESSION_TIME("SESSION-TIME"),
	EVENT_QTY("EVENT-QTY"),
	CALLED_STATION_ID("CALLED-STATION-ID"),
	CALLING_STATION_ID("CALLING-STATION-ID"),
	INGRESS_TRUNK_GROUP_NAME("INGRESS-TRUNK-GROUP-NAME"),
	EGRESS_TRUNK_GROUP_NAME("EGRESS-TRUNK-GROUP-NAME"),
	SESSION_DOWNLOAD_DATA_TRANSFER("SESSION-DOWNLOAD-DATA-TRANSFER"),
	SESSION_UPLOAD_DATA_TRANSFER("SESSION-UPLOAD-DATA-TRANSFER"),
	SESSION_TOTAL_DATA_TRANSFER("SESSION-TOTAL-DATA-TRANSFER"),
	TRAFFIC_TYPE("TRAFFIC-TYPE"),
	SESSION_MONEY("SESSION-MONEY"),
	SERVICE_PRICE("SERVICE-PRICE"),
	CONTENT_SERVICE_ID("CONTENT-SERVICE-ID"),
	CONTENT_ID("CONTENT-ID"),
	SHORT_CODE("SHORT-CODE"),
	PRODUCT_CODE("PRODUCT-CODE"),
	OFFER_CODE("OFFER-CODE"),
	CALL_TYPE("CALL-TYPE"),
	CALLING_SERVICE_TYPE("CALLING-SERVICE-TYPE"),
	CALLING_SUBSERVICE_TYPE("CALLING-SUBSERVICE-TYPE"),
	CALLING_AREA_LEVEL1("CALLING-AREA-LEVEL1"),
	CALLING_AREA_LEVEL2("CALLING-AREA-LEVEL2"),
	CALLING_AREA_LEVEL3("CALLING-AREA-LEVEL3"),
	CALLING_AREA_LEVEL4("CALLING-AREA-LEVEL4"),
	CALLING_OPERATOR("CALLING-OPERATOR"),
	CALLED_SERVICE_TYPE("CALLED-SERVICE-TYPE"),
	CALLED_SUBSERVICE_TYPE("CALLED-SUBSERVICE-TYPE"),
	CALLED_AREA_LEVEL1("CALLED-AREA-LEVEL1"),
	CALLED_AREA_LEVEL2("CALLED-AREA-LEVEL2"),
	CALLED_AREA_LEVEL3("CALLED-AREA-LEVEL3"),
	CALLED_AREA_LEVEL4("CALLED-AREA-LEVEL4"),
	CALLED_OPERATOR("CALLED-OPERATOR"),
	CALL_DISTANCE("CALL-DISTANCE"),
	INGRESS_AREA_LEVEL1("INGRESS-AREA-LEVEL1"),
	INGRESS_AREA_LEVEL2("INGRESS-AREA-LEVEL2"),
	EGRESS_AREA_LEVEL1("EGRESS-AREA-LEVEL1"),
	EGRESS_AREA_LEVEL2("EGRESS-AREA-LEVEL2"),
	CALLING_SERVED_IMSI("CALLING-SERVED-IMSI"),
	CALLED_SERVED_IMSI("CALLED-SERVED-IMSI"),
	CALLING_PARTY_LAC("CALLING-PARTY-LAC"),
	CALLED_PARTY_LAC("CALLED-PARTY-LAC"),
	APN_ID("APN-ID"),
	URL("URL"),
	USAGE_TYPE("USAGE-TYPE"),
	EVENT_SOURCE_AREA_LEVEL1("EVENT-SOURCE-AREA-LEVEL1"),
	EVENT_SOURCE_AREA_LEVEL2("EVENT-SOURCE-AREA-LEVEL2"),
	EVENT_SOURCE_AREA_LEVEL3("EVENT-SOURCE-AREA-LEVEL3"),
	EVENT_SOURCE_AREA_LEVEL4("EVENT-SOURCE-AREA-LEVEL4"),
	EVENT_SOURCE_ID("EVENT-SOURCE-ID"),
	TECH_PREFIX("TECH-PREFIX"),
	GENERAL1("GENERAL1"),
	GENERAL2("GENERAL2"),
	GENERAL3("GENERAL3"),
	GENERAL4("GENERAL4"),
	GENERAL5("GENERAL5"),
	GENERAL6("GENERAL6"),
	GENERAL7("GENERAL7"),
	GENERAL8("GENERAL8"),
	GENERAL9("GENERAL9"),
	GENERAL10("GENERAL10"),
	GENERAL11("GENERAL11"),
	GENERAL12("GENERAL12"),
	GENERAL13("GENERAL13"),
	GENERAL14("GENERAL14"),
	GENERAL15("GENERAL15"),
	GENERAL16("GENERAL16"),
	GENERAL17("GENERAL17"),
	GENERAL18("GENERAL18"),
	GENERAL19("GENERAL19"),
	GENERAL20("GENERAL20"),
	GENERAL21("GENERAL21"),
	GENERAL22("GENERAL22"),
	GENERAL23("GENERAL23"),
	GENERAL24("GENERAL24"),
	GENERAL25("GENERAL25"),
	GENERAL26("GENERAL26"),
	GENERAL27("GENERAL27"),
	GENERAL28("GENERAL28"),
	GENERAL29("GENERAL29"),
	GENERAL30("GENERAL30"),
	GENERAL31("GENERAL31"),
	GENERAL32("GENERAL32"),
	GENERAL33("GENERAL33"),
	GENERAL34("GENERAL34"),
	GENERAL35("GENERAL35"),
	GENERAL36("GENERAL36"),
	GENERAL37("GENERAL37"),
	GENERAL38("GENERAL38"),
	GENERAL39("GENERAL39"),
	GENERAL40("GENERAL40"),
	GENERAL41("GENERAL41"),
	GENERAL42("GENERAL42"),
	GENERAL43("GENERAL43"),
	GENERAL44("GENERAL44"),
	GENERAL45("GENERAL45"),
	GENERAL46("GENERAL46"),
	GENERAL47("GENERAL47"),
	GENERAL48("GENERAL48"),
	GENERAL49("GENERAL49"),
	GENERAL50("GENERAL50"),
	GENERAL51("GENERAL51"),
	GENERALDATE1("GENERALDATE1"),
	GENERALDATE2("GENERALDATE2"),
	GENERALDATE3("GENERALDATE3"),
	GENERALDATE4("GENERALDATE4"),
	ERROR_CODE("ERROR-CODE"), 
	START_DATE("START-DATE", "START-DATE", true),
	END_DATE("END-DATE", "END-DATE", true),
	//SESSION_START_DATE("SESSION-START-DATE"),
	
	//Output EDR field
	PARTNER_NAME("PARTNER-NAME", "Partner legal name enriched by Partner Guiding"),
	PROCESS_DATE("PROCESS-DATE", "Timestamp representing the date when the record was processed", true),
	ACCOUNT_ID("ACCOUNT-ID", "Account ID value enriched by Partner Guiding"),
	TOTAL_PULSE1("TOTAL-PULSE1","Pulse accounted, configured separator separated if Incremental rating. For Rate Card 1."),
	TOTAL_PULSE2("TOTAL-PULSE2","Pulse accounted, configured separator separated if Incremental rating. For Rate Card 2."),
	TOTAL_PULSE3("TOTAL-PULSE3","Pulse accounted, configured separator separated if Incremental rating. For Rate Card 3."),
	RATE1("RATE1", "Rate per pulse, configured separator separated in case of incremental rating. For Rate card 1."),
	RATE2("RATE2", "Rate per pulse, configured separator separated in case of incremental rating. For Rate card 2."),
	RATE3("RATE3", "Rate per pulse, configured separator separated in case of incremental rating. For Rate card 3."),
	//RATE("RATE"),
	ACCOUNTED_COST1("ACCOUNTED-COST1", "Accounted Cost, Sum of cost incurred by different slabs in case of incremental Rating. For Rate card 1."),
	ACCOUNTED_COST2("ACCOUNTED-COST2", "Accounted Cost, Sum of cost incurred by different slabs in case of incremental Rating. For Rate card 2."),
	ACCOUNTED_COST3("ACCOUNTED-COST3", "Accounted Cost, Sum of cost incurred by different slabs in case of incremental Rating For Rate card 3."),
	ACCOUNTED_COST("ACCOUNTED-COST", "Total Accounted Cost in partner currency (Accounted Cost1 + Accounted Cost2 + Accounted Cost3)"),
	//ACCOUNTED_COST("ACCOUNTED-COST"),
	TAXABLE_AMOUNT("TAXABLE-AMOUNT"),
	TAXABLE_AMOUNT_2("TAXABLE-AMOUNT-2"),
	TAX_RATE("TAX-RATE"),
	TAX_TYPE("TAX-TYPE"),
	TAX_CODE("TAX-CODE"),
	TAX_AMOUNT("TAX-AMOUNT"),
	TAX_AMOUNT_2("TAX-AMOUNT-2"),
	PARTNER_CURRENCY_ISO_CODE("PARTNER-CURRENCY-ISO-CODE", "ISO code of configured currency in rate card"),
	DISCOUNT_AMOUNT("DISCOUNT_AMOUNT"),
	SDR_EXCHANGE_RATE("SDR-EXCHANGE-RATE"),
	TAXABLE_AMOUNT_SDR("TAXABLE-AMOUNT-SDR"),
	TAXABLE_AMOUNT_SDR_2("TAXABLE-AMOUNT-SDR-2"),
	TAX_AMOUNT_SDR("TAX-AMOUNT-SDR"),
	TAX_AMOUNT_SDR_2("TAX-AMOUNT-SDR-2"),
	TOTAL_COST_SDR("TOTAL-COST-SDR"),
	TOTAL_COST_SDR_2("TOTAL-COST-SDR-2"),
	TRANS_TYPE("TRANS-TYPE"),
	ACCOUNTED_TIME("ACCOUNTED-TIME" ,"Rounded up value, as per Rate card 1. Should be in seconds. max value 30 days."),
	ACCOUNTED_VOLUME("ACCOUNTED-VOLUME", "Rounded up value, as per Rate card 1. Should be in Bytes, max value 1 TB"),
	RATE_CARD_ID("RATE-CARD-ID"),
	PREV_ACCOUNTED_COST("PREV-ACCOUNTED-COST"),
	PREV_ACCOUNTED_COST_2("PREV-ACCOUNTED-COST-2"),
	FILE_NAME("FILE-NAME", "Name of the file which contains the EDR. Note that file name is not absolute, enriched by File parsing"),
	FILE_LOCATION("FILE-LOCATION", "Absolute path from where file is received, enriched by File parsing"),
	FILE_LOCATION_NAME("FILE-LOCATION-NAME", "Name of the file location from which file is received, enriched by File parsing"),
	CALLING_PREFIX("CALLING-PREFIX", "Calling prefix from where call was originated, enriched by Prefix Enrichment and contains prefix value (country code + area code) configured in Prefix Management. Example: 9181, 101"),
	CALLED_PREFIX("CALLED-PREFIX", "Called prefix to where call was destined, enriched by Prefix Enrichment and contains prefix value (country code + area code) configured in Prefix Management. Example: 9181, 101"),
	CALLING_NAME("CALLING-NAME", "Calling prefix name from where call was originated, enriched by Prefix Enrichment and contains prefix name configured in Prefix Management. Example: India, USA"),
	CALLED_NAME("CALLED-NAME", "Called prefix name to where call was destined, enriched by Prefix Enrichment and contains prefix name configured in Prefix Management. Example: India, USA"),
	//USAGE_TYPE("USAGE-TYPE"),
	CHARGE_PER_UOM("CHARGE-PER-UOM", "Configured rate, comma separated if Tier Type is Incremental. For Rate Card 1."),
	CURRENCY_ID("CURRENCY-ID"),
	RATE_TYPE("RATE-TYPE", "peak, off-peak, weekend, special. Of Rate Card 1."),
	MULTI_SESSION_ID("MULTI-SESSION-ID"),
	PARAM1("PARAM1"),
	POLICY_NAME("POLICY-NAME"),
	//RATE_CARD_ID("RATE-CARD-ID");
	
	/*
	 * Custom Keys added 
	 */
	
	// Account enrichment
	
	ACCOUNT_IDENTIFIER_TYPE("ACCOUNT-IDENTIFIER-TYPE"),
	
	// Prefix enrichment
	CALLING_COUNTRY_CODE("CALLING-COUNTRY-CODE", "Calling country code from where call was originated, enriched by Prefix Enrichment and contains calling country code configured in Prefix Management. Example: 91, 1"),
	CALLED_COUNTRY_CODE("CALLED-COUNTRY-CODE", "Called country code to where call was destined, enriched by Prefix Enrichment and contains called country configured in Prefix Management. Example: 91, 1"),
	CALLING_AREA_CODE("CALLING-AREA-CODE", "Calling country area code from where call was originated, enriched by Prefix Enrichment and contains calling area code configured in Prefix Management. Example: 41, 90, 200"),
	CALLED_AREA_CODE("CALLED-AREA-CODE", "Called country area code from where call has disconnected, enriched by Prefix Enrichment and contains called area code configured in Prefix Management. Example: 41, 90, 200"),
	
	// Rate card hunting keys
	PRODUCT_SPEC("PRODUCT-SPEC", "Product Specification name enriched by Rating Engine"),
	RNC_PACKAGE("RNC-PACKAGE", "RnC Package name enriched by Rating Engine"),
	
	//RATE-CARD
	ACCOUNTING_EFFECT("ACCOUNTING-EFFECT", "CR (credit) or DR(debit), based on rate card applied"), 
	RATE_CARD_GROUP("RATE-CARD-GROUP", "Rate Card Group name that was applied, enriched by Rating Engine"),
	SYSTEM_CURRENCY_ACCOUNTED_COST("SYSTEM-CURRENCY-ACCOUNTED-COST", "Total accounted cost in system currency"),
	
	//RATE-CARD-NAME
	RATE_CARD_ID1("RATE-CARD-ID1", "Rate Card Id1 enriched by Rate Card Group"),
	RATE_CARD_ID2("RATE-CARD-ID2", "Rate Card Id2 enriched by Rate Card Group"),
	RATE_CARD_ID3("RATE-CARD-ID3", "Rate Card Id3 enriched by Rate Card Group"),
	
	ABSLOUTE_FILE_NAME("ABSOLUTE-FILE-NAME", "Used to keep file wise aggregation record");
	
	private final String name;
	private final String description;
	private static Map<String, OfflineRnCKeyConstants> nameToConstant;
	private boolean isTimestamp;

	private OfflineRnCKeyConstants(String name, String description) {
		this(name, description, false);
	}
	
	private OfflineRnCKeyConstants(String name, String description, boolean isTimestamp) {
		this.name = name;
		this.description = description;
		this.isTimestamp = isTimestamp;
	}
	
	private OfflineRnCKeyConstants(String name) {
		this(name, name);
	}
	
	static {
		nameToConstant = Stream.of(values()).collect(Collectors.toMap(t -> t.getName(), t -> t));
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static OfflineRnCKeyConstants fromKeyName (String name) {
		return nameToConstant.get(name);
	}

	public boolean isTimestamp() {
		return isTimestamp;
	}

}
