package com.elitecore.nvsmx.system.util;


import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyType;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.elitecore.corenetvertex.constants.CommonConstants.DEFAULT_DATE_FORMAT;
import static com.elitecore.corenetvertex.constants.CommonConstants.DEFAULT_SHORT_DATE_FORMAT;

public class NVSMXUtil {

	
	private static final String MODULE = "NVSMX-UTIL";

	
	private NVSMXUtil(){
		
	}
	/**
	 * Get the format from SystemParameterDAO map and return the date format.
	 * If format is not found then it will set the default format.
	 * @return String
	 */
	public static String getDateFormat() {
		String dateFormat = SystemParameterDAO.get(CommonConstants.DATE_FORMAT);
		if(Strings.isNullOrBlank(dateFormat)){
			dateFormat = DEFAULT_DATE_FORMAT;
		}else{
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				String dateAsStr = sdf.format(new Date());
				sdf.parse(dateAsStr);
			} catch (Exception e) {
				if(LogManager.getLogger().isDebugLogLevel()){
					LogManager.getLogger().debug(MODULE, "Invalid Date Format configured in System parameter, So taking default date format : " + DEFAULT_DATE_FORMAT);
				}
				dateFormat = DEFAULT_DATE_FORMAT;
			}
		}
		return dateFormat;
	}

	/**
	 * Used to set the default description for the module.
	 * @param HttpServletRequest request
	 * @return String
	 */
	public static String getDefaultDescription(HttpServletRequest request){
		return "Created by " + request.getSession().getAttribute(Attributes.STAFF_USERNAME) + " on " + simpleDateFormatPool.get().format(new Date());
		
	}

	public static final ThreadLocal<SimpleDateFormat> simpleDateFormatPool = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat simpleDateFormat = null;
			String dateFormat = getDateFormat();
			if(Strings.isNullOrBlank(dateFormat)){
				simpleDateFormat = new SimpleDateFormat();
			}else{
				simpleDateFormat = new SimpleDateFormat(dateFormat);
			}
			return simpleDateFormat;
		}
	};
	
	
	public static String getShortDateFormat() {
		String shortDateFormat = SystemParameterDAO.get(CommonConstants.SHORT_DATE_FORMAT);
		if(Strings.isNullOrBlank(shortDateFormat)){
			shortDateFormat = DEFAULT_SHORT_DATE_FORMAT;
		}else{
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(shortDateFormat);
				String dateAsStr = sdf.format(new Date());
				sdf.parse(dateAsStr);
			} catch (Exception e) {
				if(LogManager.getLogger().isDebugLogLevel()){
					LogManager.getLogger().debug(MODULE, "Invalid Short Date Format configured in System parameter, So taking default short date format : " + DEFAULT_SHORT_DATE_FORMAT);
				}
				shortDateFormat = DEFAULT_SHORT_DATE_FORMAT;
			}
		}
		return shortDateFormat;
	}

	/**
	 * Fetch the PCRF Key from PCRF Key Constant , whose request and response parameter is true
	 * @return json String
	 */
	public static String getPcrfKeySuggestions(String request, String response) {

		Set<String> pcrfKeySuggestions = new HashSet(); //KEY SHOULD BE UNIQUE THAT'S WHY SET IS USED

		if(Strings.isNullOrBlank(request) == false && PCRFKeyType.REQUEST.name().equals(request.toUpperCase())) {
			for (PCRFKeyConstants pccAttribute : PCRFKeyConstants.values(PCRFKeyType.REQUEST)) {
				pcrfKeySuggestions.add(pccAttribute.getVal());
			}
		}

		if(Strings.isNullOrBlank(response) == false && PCRFKeyType.RESPONSE.name().equals(response.toUpperCase())) {
			for (PCRFKeyConstants pccAttribute : PCRFKeyConstants.values(PCRFKeyType.RESPONSE)) {
				pcrfKeySuggestions.add(pccAttribute.getVal());
			}
		}
		Gson gson = GsonFactory.defaultInstance();
		return gson.toJson(pcrfKeySuggestions);
	}

}
