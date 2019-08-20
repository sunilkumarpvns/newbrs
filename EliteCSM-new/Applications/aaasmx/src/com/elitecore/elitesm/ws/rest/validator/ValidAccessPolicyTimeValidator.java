package com.elitecore.elitesm.ws.rest.validator;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;

import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

/**
 * 
 * It validate time,hour and minute property of Access Policy Detail annotated with @ValidAccessPolicyTime. <br> 
 * It validate either time or hour and minute must be specify. <br> 
 * It validate hour and minute are properly specified. <br>  
 * It validate hour and minute are within its range. 
 * 
 * @author Shekhar Vyas
 *
 */

public class ValidAccessPolicyTimeValidator implements ConstraintValidator<ValidAccessPolicyTime, Object> {

	@Override
	public void initialize(ValidAccessPolicyTime arg0) {
		
	}

	@Override
	public boolean isValid(Object arg0, ConstraintValidatorContext context) {
		boolean isValid = true;
		
		StringBuilder message = new StringBuilder("");
		
		try {
			
			Object startTimeObj = null;
			Object stopTimeObj = null;
			Object startHourObj = null;
			Object startMinuteObj = null;
			Object stopHourObj = null;
			Object stopMinuteObj = null;
			
			try {
				startTimeObj = PropertyUtils.getProperty(arg0, "startTime");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} 
			
			try {
				stopTimeObj = PropertyUtils.getProperty(arg0, "stopTime");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			try {
				startHourObj = PropertyUtils.getProperty(arg0, "startHour");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			try {
				startMinuteObj = PropertyUtils.getProperty(arg0, "startMinute");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			try {
				stopHourObj = PropertyUtils.getProperty(arg0, "stopHour");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			try {
				stopMinuteObj = PropertyUtils.getProperty(arg0, "stopMinute");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			
			if ((startTimeObj == null) && (startHourObj == null || startMinuteObj == null)) {
				
				isValid=false;
				
				if (startHourObj == null) {
					message.append("Start hour must be specified ");
				}
				
				if (startMinuteObj == null) {
					message.append("Start minute must be specified ");
				}
				
			}
			
			if ((stopTimeObj == null) && (stopHourObj == null || stopMinuteObj == null)) {
				
				isValid=false;
				
				if (stopHourObj == null) {
					message.append("Stop hour must be specified ");
				} 
				
				if (stopMinuteObj == null) {
					message.append("Stop minute must be specified ");
				}
				
			}
			
			if (startHourObj != null) {
				
				String startHourStr = String.valueOf(startHourObj);
				
				if (startHourStr.equals("invalid")) {
					
					isValid = false;

					message.append("Invalid value of start hour ");

				} else {
					
					Integer startHour = Integer.parseInt(startHourStr);
					
					if (startHour < 0 || startHour > 23) {
						
						isValid = false;
						
						message.append("Start hour must be in range of 0 to 23 ");
						
					}
					
				}
				
			}
			
			if (startMinuteObj != null) {

				String startMinuteStr = String.valueOf(startMinuteObj);

				if (startMinuteStr.equals("invalid")) {

					isValid = false;

					message.append("Invalid value of start minute ");

				} else {

					Integer startMinute = Integer.parseInt(startMinuteStr);

					if (startMinute < 0 || startMinute > 59) {

						isValid = false;

						message.append("Start minute must be in range of 0 to 59 ");

					}

				}

			}
			
			if (stopHourObj != null) {

				String stopHourStr = String.valueOf(stopHourObj);

				if (stopHourStr.equals("invalid")) {

					isValid = false;

					message.append("Invalid value of stop hour ");

				} else {

					Integer stopHour = Integer.parseInt(stopHourStr);

					if (stopHour < 0 || stopHour > 23) {

						isValid = false;

						message.append("Stop hour must be in range of 0 to 23 ");

					}

				}

			}
			
			if (stopMinuteObj != null) {

				String stopMinuteStr = String.valueOf(stopMinuteObj);

				if (stopMinuteStr.equals("invalid")) {

					isValid = false;

					message.append("Invalid value of stop minute ");

				} else {

					Integer stopMinute = Integer.parseInt(stopMinuteStr);

					if (stopMinute < 0 || stopMinute > 59) {

						isValid = false;

						message.append("Stop minute must be in range of 0 to 59 ");

					}

				}

			}
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} finally {

			if (isValid == false) {

				RestUtitlity.setValidationMessage(context, message.toString());

			}

		}
		
		return isValid;
		
	}

}
