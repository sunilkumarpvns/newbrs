package com.elitecore.nvsmx.system.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.elitecore.commons.logging.LogManager;

/**
 * A utility that will convert any string value to the Timestamp
 * @author ishani.bhatt
 *
 */
public class DateConverter extends StrutsTypeConverter {

	
    private static final String MODULE = "DATE-CNVRTER";
	@Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
    	if (values == null || values.length == 0 || values[0].trim().length() == 0) {
            return null;
        }
    	String input = values[0].trim();
        try {
        	Date date = simpleDateFormatPool.get().parse(simpleDateFormatPool.get().format(new Date(input)).toString());
    		return new Timestamp(date.getTime());
        } catch (ParseException e) {
        	LogManager.getLogger().trace(MODULE, e);
            return null;
        }
    }
    @Override
    public String convertToString(Map context, Object date) {
    	 if (date != null && date instanceof Timestamp) {
    	        return simpleDateFormatPool.get().format(date);
    	    } else {
    	        return null;
    	    }
    }
    private static ThreadLocal<SimpleDateFormat> simpleDateFormatPool = new ThreadLocal<SimpleDateFormat>(){
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("dd-MMM-yyyy kk:mm:ss");
		};
	};

}
