package com.validator;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public class GreaterThanZeroValidator extends FieldValidatorSupport {

	@Override
	public void validate(Object object) throws ValidationException {

		String fieldName = getFieldName();
		Object fieldValue = getFieldValue(fieldName, object);
		Long longFieldValue = null;
		if (fieldValue != null) {
			if (fieldValue instanceof Long) {
				longFieldValue = (Long) fieldValue;
			} else if (fieldValue instanceof Integer) {
				longFieldValue = ((Integer) fieldValue).longValue();
			}
		}
		if (longFieldValue !=null && longFieldValue <= 0) {
			addFieldError(fieldName, object);
		}

	}
}
