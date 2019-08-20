package com.validator;

import java.text.SimpleDateFormat;

import com.elitecore.commons.base.Strings;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public class DateFormatValidator extends FieldValidatorSupport {

	@Override
	public void validate(Object object) throws ValidationException {
		String fieldName = getFieldName();
		Object fieldValue = this.getFieldValue(fieldName, object);
		String strFieldValue = null;
		if (fieldValue != null) {
			if (fieldValue instanceof String) {
				if (Strings.isNullOrBlank(strFieldValue) == false) {
					try {
						SimpleDateFormat df = new SimpleDateFormat(NVSMXUtil.getDateFormat());
						df.setLenient(false);
						df.parse(fieldValue.toString());
					} catch (Exception e) {
						addFieldError(fieldName, object);
					}
				}
			}
		}
	}

}
