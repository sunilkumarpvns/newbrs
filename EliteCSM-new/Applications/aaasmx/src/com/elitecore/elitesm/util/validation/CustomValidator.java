

package com.elitecore.elitesm.util.validation;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.util.ValidatorUtils;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.Resources;


public class CustomValidator implements Serializable {

    /**
     *  Commons Logging instance.
     */
    private static final Log log = LogFactory.getLog(CustomValidator.class);
	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
													"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
													"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
													"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    
   
    /**
     * Checks if the field isn't null and Value of the field is zero. 
     *
     * @param bean The bean validation is being performed on.
     * @param va The <code>ValidatorAction</code> that is currently being performed.
     * @param field The <code>Field</code> object associated with the current
     * field being validated.
     * @param errors The <code>ActionMessages</code> object to add errors to if
     * any validation errors occur.
     * @param validator The <code>Validator</code> instance, used to access
     *                  other field values.
     * @param request Current request object.
     * @return true if meets stated requirements, false otherwise.
     */
    public static boolean validateSelectionRequired(Object bean,
                                           ValidatorAction va, Field field,
                                           ActionMessages errors,
                                           Validator validator,
                                           HttpServletRequest request) {

        String value = null;
        if (isString(bean)) {
            value = (String) bean;
        } else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }

        if (value.equalsIgnoreCase("0")) {
            errors.add(field.getKey(), Resources.getActionMessage(validator, request, va, field));
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean validateTextAreaMaxLength(Object bean,
            ValidatorAction va, Field field,
            ActionMessages errors,
            Validator validator,
            HttpServletRequest request) {

		    	String value = null;
		        if (isString(bean)) {
		            value = (String) bean;
		        } else {
		            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
		        }
		
		        if (value != null) {
		            try {
		                int max = Integer.parseInt(field.getVarValue("maxlength"));
		
		                if(!limitCheck(value,max)){
					        return false;
					    }           
	                } 
		            catch (Exception e) {
	                   errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
	                   return false;
	                }
		        }
		        return true;
			}
 
    /**
     *  Return <code>true</code> if the specified object is a String or a <code>null</code>
     *  value.
     *
     * @param o Object to be tested
     * @return The string value
     */
    protected static boolean isString(Object o) {
        return (o == null) ? true : String.class.isInstance(o);
    }
    
    public static boolean limitCheck(String fieldValue, int maxlimit) {
    	boolean isValid=true;
    	int fieldLength=fieldValue.length();
    	int decrementValue=fieldValue.split("\n").length + fieldValue.split("\r").length;
    	 maxlimit = maxlimit - (2*decrementValue); 
		   if(fieldLength > maxlimit){
			   isValid= false;
		   }else{
			   isValid= true;
		   }
        return isValid;
    }
    
    /**  
     * @param ipAddress   the IP Address to be tested. 
     * @return <code>true</code> if the String is valid IPv4Address.
     *         <code>false</code> otherwise.
     */
    public static boolean isValidIP4Address(final String ipAddress){	
      Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
  	  Matcher matcher = pattern.matcher(ipAddress);
  	  return matcher.matches();	    	    
    }

}
