package com.elitecore.elitesm.ws.rest.exception;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.exception.EliteResponseCode.EliteResponse;
import com.elitecore.elitesm.ws.rest.validator.CompareESIType;
import com.elitecore.elitesm.ws.rest.validator.Contains;
import com.elitecore.elitesm.ws.rest.validator.Depends;
import com.elitecore.elitesm.ws.rest.validator.IsNumeric;
import com.elitecore.elitesm.ws.rest.validator.SingleDriver;
import com.elitecore.elitesm.ws.rest.validator.ValidAccessPolicyTime;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.esi.ValidateIPPort;
import com.elitecore.elitesm.ws.rest.validator.esi.ValidateRealmName;
import com.elitecore.elitesm.ws.rest.validator.server.ValidServerType;
import com.elitecore.elitesm.ws.rest.validator.server.ValidServiceType;

/**
 * Whenever the bean validation fails on an object Hibernate validator throws ValidationException
 * containing list of failed validation. Based on it proper message is provided in response using this class. 
 * @author nayana.rathod
 */
public class RESTValidationMapper implements ExceptionMapper<ValidationException> {

	@Override
	public Response toResponse(ValidationException validationException) {
		
		EliteResponseCode eliteResponseCode = new EliteResponseCode();
		List<EliteResponse> eliteResponseCodeList = eliteResponseCode.getResponseCodes();

		if (validationException instanceof ConstraintViolationException) {
			ConstraintViolationException constraint = (ConstraintViolationException) validationException;

			Iterator<ConstraintViolation<?>> it = constraint.getConstraintViolations().iterator();

			while (it.hasNext()) {
				
				ConstraintViolation<?> violation = (ConstraintViolation<?>) it.next();
				
				String response = ResponseCodes.getResponseCode(violation.getConstraintDescriptor()
						.getAnnotation().annotationType());
				
				if( Strings.isNullOrBlank(response) ){
					response = ResultCode.INTERNAL_ERROR.responseCode;
				}
				
				eliteResponseCodeList.add(new EliteResponse(violation.getMessage(), response));
			}

			if( Collectionz.isNullOrEmpty(eliteResponseCodeList) == false){
				eliteResponseCode.setResponseCodes(eliteResponseCodeList);
			}

		} else {
			eliteResponseCodeList.add(new EliteResponse("Error processing request : " + validationException.getMessage(), ResultCode.INTERNAL_ERROR.responseCode));
			eliteResponseCode.setResponseCodes(eliteResponseCodeList);
		}
		return Response.ok(eliteResponseCode).build();
	}
	
	enum ResponseCodes {
		NOTEMPY(NotEmpty.class, ResultCode.INPUT_PARAMETER_MISSING.responseCode), 
		NOTNULL(NotNull.class, ResultCode.INPUT_PARAMETER_MISSING.responseCode), 
		PATTERN(Pattern.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode), 
		MIN(Min.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode),
		MAX(Max.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode),
		LENGTH(Length.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode),
		RANGE(Range.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode),
		SIZE(Size.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode),
		ISNUMERIC(IsNumeric.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode), 
		VALIDATEREALMNAME(ValidateRealmName.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode), 
		COMPAREESITYPE(CompareESIType.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode), 
		CONTAINS(Contains.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode), 
		VALIDOBJECT(ValidObject.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode), 
		VALIDACCESSPOLICYTIME(ValidAccessPolicyTime.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode), 
		SINGLEDRIVER(SingleDriver.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode),
		DEPENDS(Depends.class, ResultCode.INPUT_PARAMETER_MISSING.responseCode),
		VALIDSERVERTYPE(ValidServerType.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode),
		VALIDSERVICETYPE(ValidServiceType.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode),
		VALIDATEIPPORT(ValidateIPPort.class, ResultCode.INVALID_INPUT_PARAMETER.responseCode);

		private Class classname;
		private String code;
		private static Map<Class, String> classToName = new HashMap<Class, String>();
		
		private ResponseCodes(Class classname, String code) {
			this.classname = classname;
			this.code = code;
		}
		
		static {
			for (ResponseCodes x : values()) {
				classToName.put(x.classname, x.code);
			}
		}
		
		private static String getResponseCode(Class classname) {
			return classToName.get(classname);
		}
	}

}
