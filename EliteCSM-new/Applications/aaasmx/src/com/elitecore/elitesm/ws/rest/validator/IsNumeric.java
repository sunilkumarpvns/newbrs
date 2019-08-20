package com.elitecore.elitesm.ws.rest.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
/**
 * <p> <b> @IsNumeric </b> Validates value of class property is numeric or not.</br>
 * For example</br>
 * <p>In REST there is possibility user may enter wrong characters in Integral data type , 
 * so we allows only numeric value in Integral data type(used in REST).</p>   
 * @author chirag.i.prajapati
 */

@Target({ METHOD,FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumberValidator.class)
@Documented
public @interface IsNumeric {
	String message() default "Allows only numeric value";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	boolean isAllowBlank()default false ;
	
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface List {
		IsNumeric[] value();
	}
}
