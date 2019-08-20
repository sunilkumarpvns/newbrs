package com.elitecore.elitesm.ws.rest.validator;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

public class DependsOnValidator implements ConstraintValidator<Depends, Object>{

	private Depends dependsAnnotatedField;

	/**
	 * This method will be used for assignment or initialization purpose.
	 * @param dependsAnnotatedField having field defined in <code>{@link Depends}</code>
	 */
	@Override
	public void initialize(Depends dependsAnnotatedField) {
		this.dependsAnnotatedField = dependsAnnotatedField;
	}

	/**
	 * This method will validate the object of class having <code>{@link Depends}</code> annotation.
	 * 
	 * @param dependsAnnotatedObject object of class annotated with <code>{@link Depends}</code>
	 * @param context
	 */
	@Override
	public boolean isValid(Object dependsAnnotatedObject, ConstraintValidatorContext context) {
		
		boolean isValid = true;
		try {
			String field = BeanUtils.getProperty(dependsAnnotatedObject, this.dependsAnnotatedField.field());
			String dependsOn = BeanUtils.getProperty(dependsAnnotatedObject, this.dependsAnnotatedField.dependsOn());

			String message = null;
			if (DefaultResponseBehaviorType.REJECT.name().equalsIgnoreCase(dependsOn) 
					&& Strings.isNullOrBlank(field)) {
				
				isValid = false;
				message = "Default Response Behaviour Argument must be specified when " +
						"Default Response Behaviour is " + DefaultResponseBehaviorType.REJECT.name();
				
			} else if (DefaultResponseBehaviorType.HOTLINE.name().equalsIgnoreCase(dependsOn) 
					&& Strings.isNullOrBlank(field)) {
				
				isValid = false;
				message = "Default Response Behaviour Argument must be specified when " +
						"Default Response Behaviour is " + DefaultResponseBehaviorType.HOTLINE.name();
			} 			
			
			if (isValid == false) {
				RestUtitlity.setValidationMessage(context, message);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return isValid;
	}

}
