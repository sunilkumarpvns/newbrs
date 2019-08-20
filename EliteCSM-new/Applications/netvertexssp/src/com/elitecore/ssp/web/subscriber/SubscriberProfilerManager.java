package com.elitecore.ssp.web.subscriber;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.elitecore.ssp.util.EliteUtility;
import com.elitecore.ssp.util.constants.BaseConstant;
import com.elitecore.ssp.util.logger.Logger;

public class SubscriberProfilerManager {
	private static String SUBSCRIBEPROPERTYFILE = EliteUtility.getSSPHome() + File.separator +BaseConstant.SUBSCRIBE_PROFILER_FILE_LOCATION;
	private static String MODULE = SubscriberProfilerManager.class.getSimpleName();
	
	public static String[] getSubscriberProfileSearchFilelds(){
		FileInputStream fileInputStream = null;
		String propertyFields = null; 
		try{
			Properties properties = new Properties();
			File dbPropsFile = new File(SUBSCRIBEPROPERTYFILE);
			fileInputStream = new FileInputStream(dbPropsFile);
			properties.load(fileInputStream);
			propertyFields =  properties.getProperty("searchfields") != null ? properties.getProperty("searchfields")  : "";
		}catch (Exception e) {
			Logger.logError(MODULE,"Error while reading subscribeProfiler-search properties, reason:"+e.getMessage());
		}finally{
			try{
				if(fileInputStream!=null){
					fileInputStream.close();
				}
			}catch(Exception e){
				Logger.logError(MODULE,"Error while closing Stream, reason:"+e.getMessage());	
			}
		}
		return StringUtils.split(propertyFields,',');
	}
	
	
}
