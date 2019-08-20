package com.elitecore.nvsmx.system.util.migrate;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.constants.CommonConstants;

public class SPRColumnConfigurationProvider {

	private static final String MODULE = "SPR-COLUMN-CONF";

	private static final String COLUMN_CONFIGURATION_FILENAME = "spr-column-configuration.xml";
	private static final String SYSTEM_PATH = "system";

	private boolean isInitialized;
	private String serverHome;
	private Map<String, String> columnName648to680Map;
	private Set<String> ignoreColumnNames;
	private List<ColumnNameData> csvColumnHeaders;
	private List<ColumnNameData> subscriptionCsvHeaders;
	private String dateFormat = "dd-MMM-yyyy hh.mm.ss.SSS a";;


	public SPRColumnConfigurationProvider(String serverHome) {
		this.serverHome = serverHome;
		this.columnName648to680Map = new HashMap<String, String>();
		this.ignoreColumnNames = new HashSet<String>();
		this.isInitialized = false;
	}

	private void addDefaultMappings() {
	}

	public void init() {

		if (isInitialized) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Column configuraion is already initialized");
			}
			return;
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Initializing column configuraion");
		}

		addDefaultMappings();

		try {
			File confFile = new File(serverHome + File.separator + SYSTEM_PATH + File.separator + COLUMN_CONFIGURATION_FILENAME);

			SPRColumnConfigurationData columnConfigurationData = ConfigUtil.deserialize(confFile, SPRColumnConfigurationData.class);

			if (Strings.isNullOrEmpty(columnConfigurationData.getDateFormat()) == false) {
				this.dateFormat = columnConfigurationData.getDateFormat();
			}

			Map<String, String> tempColumnNameMap = new HashMap<String, String>();
			Set<String> tempIgnoreColumnNames = new HashSet<String>();
			List<ColumnNameData> tempColumnNameDatas = new ArrayList<ColumnNameData>();
			List<ColumnNameData> tempSubscriptionCSVHeaders = new ArrayList<ColumnNameData>();
			if (columnConfigurationData != null) {

				
				// TODO CHETAN create seperate method for each configuration
				if (columnConfigurationData.getColumnNameMapping() != null) {

					ColumnNameMappingData columnNameMapping = columnConfigurationData.getColumnNameMapping();

					if (Collectionz.isNullOrEmpty(columnNameMapping.getMappings()) == false) {

						for (MappingData mappingData : columnNameMapping.getMappings()) {
							if (Strings.isNullOrBlank(mappingData.getOldValue()) == false
									&& Strings.isNullOrBlank(mappingData.getNewValue()) == false) {
								tempColumnNameMap.put(mappingData.getOldValue().trim(), mappingData.getNewValue().trim());
							}
						}
					}
				}

				if (Collectionz.isNullOrEmpty(columnConfigurationData.getIgnoreColumnNames()) == false) {

					for (String ignoreColumnName : columnConfigurationData.getIgnoreColumnNames()) {
						if (Strings.isNullOrBlank(ignoreColumnName) == false) {
							tempIgnoreColumnNames.add(ignoreColumnName.trim());
						}
					}
				}

				if (Collectionz.isNullOrEmpty(columnConfigurationData.getCSVHeaders()) == false) {
					for (ColumnNameData columnNameData : columnConfigurationData.getCSVHeaders()) {
						if (Strings.isNullOrBlank(columnNameData.getName()) == false) {
							tempColumnNameDatas.add(columnNameData);
						}
					}
				}

				initSubscriptionCSVHeaders(columnConfigurationData, tempSubscriptionCSVHeaders);
				
			}

			this.columnName648to680Map.putAll(tempColumnNameMap);
			this.ignoreColumnNames = tempIgnoreColumnNames;
			this.csvColumnHeaders = tempColumnNameDatas;
			this.subscriptionCsvHeaders = tempSubscriptionCSVHeaders;
			this.isInitialized = true;

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, toString());
				getLogger().info(MODULE, "column configuraion initialization completed");
			}
		} catch (FileNotFoundException e) {
			getLogger().info(MODULE, "File for column configuraion is not found, using default configuration. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in reading column configuraion, using default configuration. Reason : " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}


	private void initSubscriptionCSVHeaders(SPRColumnConfigurationData columnConfigurationData, List<ColumnNameData> tempSubscriptionCSVHeaders) {
		if (Collectionz.isNullOrEmpty(columnConfigurationData.getSubscriptionCsvHeader())) {
			return;
		}

		for (ColumnNameData columnNameData : columnConfigurationData.getSubscriptionCsvHeader()) {
			if (Strings.isNullOrBlank(columnNameData.getName()) == false) {
				tempSubscriptionCSVHeaders.add(columnNameData);
			}
		}
	
	}

	/**
	 * 
	 * @param oldColumnName
	 * @return if mapping exist then mapped column name otherwise returns same
	 *         column name
	 */
	public String getNewColumnName(String oldColumnName) {
		String newColumnName = columnName648to680Map.get(oldColumnName);
		return newColumnName == null ? oldColumnName : newColumnName;
	}

	public boolean isIgnoreColumn(String columnName) {
		return ignoreColumnNames.contains(columnName);
	}

	public List<ColumnNameData> getColumnHeaders() {
		return csvColumnHeaders;
	}
	
	public List<ColumnNameData> getSubscriptionCSVHeaders() {
		return subscriptionCsvHeaders;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void reload() {
		this.isInitialized = false;
		columnName648to680Map.clear();
		init();
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(stringWriter);

		out.println();
		out.println("-- SPR Column Configuration detail --");
		out.println("Date Format:" + dateFormat);
		out.println();
		out.println("-- Column Name Mapping -- ");
    		out.incrementIndentation();
    
    		if (Maps.isNullOrEmpty(columnName648to680Map)) {
    			out.println("NO COLUMN MAPPING CONFIGURED");
    		} else {
    
    			for (Entry<String, String> entryMapping : columnName648to680Map.entrySet()) {
    				out.println(entryMapping.getKey() + " = " + entryMapping.getValue());
    			}
    		}
    		out.decrementIndentation();
		out.println("-- Ignore Column List -- ");
    		out.incrementIndentation();
    		if (Collectionz.isNullOrEmpty(ignoreColumnNames)) {
    			out.println("NO IGNORE COLUMN CONFIGURED");
    		} else {
    			for (String columnName : ignoreColumnNames) {
    				out.println(columnName);
    			}
    		}
    		out.decrementIndentation();

		out.println("-- Subscriber CSV Headers -- ");
    		out.incrementIndentation();
    		if (Collectionz.isNullOrEmpty(csvColumnHeaders)) {
    			out.println("NO CSV HEADERS CONFIGURED");
    		} else {
    			for (ColumnNameData columnNameData : csvColumnHeaders) {
    				out.println(columnNameData.getIndex() + "" + CommonConstants.COMMA + columnNameData.getName());
    			}
    		}
    		out.decrementIndentation();
    		
		out.println("-- Subscription CSV Headers -- ");
    		out.incrementIndentation();
    		if (Collectionz.isNullOrEmpty(subscriptionCsvHeaders)) {
    			out.println("NO SUBSCRIPTION CSV HEADERS CONFIGURED");
    		} else {
    			for (ColumnNameData columnNameData : subscriptionCsvHeaders) {
    				out.println(columnNameData.getIndex() + "" + CommonConstants.COMMA + columnNameData.getName());
    			}
    		}
    		out.decrementIndentation();


    		
		out.close();

		return stringWriter.toString();
	}


}
