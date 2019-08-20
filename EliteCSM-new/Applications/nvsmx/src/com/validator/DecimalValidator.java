package com.validator;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

/**
 * Field Validator that checks if the specified decimal value is greater than allow decimal value.
 * 
 * @author Dhyani.Raval
 *
 */
public class DecimalValidator extends FieldValidatorSupport{
    private Integer allowDecimal = null;

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        try {
            Object obj = this.getFieldValue(fieldName, object);
            if (obj == null) {
                return;
            }
            if(obj.toString().indexOf('.') != -1){
        		String[] valueParts = obj.toString().split("\\.");
	        	if(valueParts[1].length() > allowDecimal || valueParts.length > 2){
	        		addFieldError(fieldName, object);
	        	}
        	}
        } catch (NumberFormatException e) {
            return;
        }
    }

	public Integer getAllowDecimal() {
		return allowDecimal;
	}

	public void setAllowDecimal(Integer allowDecimal) {
		this.allowDecimal = allowDecimal;
	}
}
