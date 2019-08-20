package com.elitecore.nvsmx.system.util.migrate;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;


public class PackageNameMapping {

	private static final String MODULE = "PACKAGE-MAPPING";
	private Map<String, String> packageNameMapping;
	private String mappingFileName;
	private boolean isInitialized = false;
	
	public PackageNameMapping(String mappingFileName) {
		this.packageNameMapping = new HashMap<String, String>();
		this.isInitialized = false;
		this.mappingFileName = mappingFileName;
	}
	
	private void addDefaultMappings() {
		// currently no default mapping provided
	}
	
	public void init() {
		
		if (isInitialized) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Package name mapping is already initialized");
			}
			return;
		}
		
		addDefaultMappings();

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Initializing package name mapping");
		}

		if (mappingFileName == null) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Package name mapping file not provided. So it will work with only default mappings");
			}
			return;
		}


		try {
			File confFile = new File(mappingFileName);

			PackageMappingData packageMappingData = ConfigUtil.deserialize(confFile, PackageMappingData.class);

			Map<String, String> tempPackageMapping = new HashMap<String, String>();
			if (packageMappingData != null) {
				
				if (Collectionz.isNullOrEmpty(packageMappingData.getMappings()) == false) {
					for (MappingData mappingData : packageMappingData.getMappings()) {
						
						if (Strings.isNullOrBlank(mappingData.getOldValue()) == false && 
								Strings.isNullOrBlank(mappingData.getNewValue()) == false) {
							tempPackageMapping.put(mappingData.getOldValue().trim(), mappingData.getNewValue().trim());
						}
						
					}
				}
			} 

			this.packageNameMapping.putAll(tempPackageMapping);
			this.isInitialized = true;

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, toString());
				getLogger().info(MODULE, "Package name mapping initialization completed");
			}
		
		} catch (FileNotFoundException e) {
			getLogger().info(MODULE, "File for package name mapping is not found, using default configuration. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in reading package name mapping, using default configuration. Reason : " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}
	
	/**
	 * 
	 * @param oldPackageName
	 * @return if mapping exist then mapped package name otherwise returns same package name 
	 */
	public String getNewPackageName(String oldPackageName) {
		String newPackageName = packageNameMapping.get(oldPackageName);
		return newPackageName == null ? oldPackageName : newPackageName;
	}
	
	public void reload() {
		this.isInitialized = false;
		packageNameMapping.clear();
		init();
	}
	
	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(stringWriter);
		out.println();
		out.println("-- Package Name Mapping detail --");
		out.incrementIndentation();
		
			if (Maps.isNullOrEmpty(packageNameMapping)) {
				out.println(" NO PACKAGE NAME MAPPING CONFIGURED");
			} else {
				
				for (Entry<String, String> entryMapping : packageNameMapping.entrySet()) {
					out.println(entryMapping.getKey() + " =  " + entryMapping.getValue());	
				}
			}
			out.decrementIndentation();
		out.close();
		
		return stringWriter.toString();
	}
	
}
