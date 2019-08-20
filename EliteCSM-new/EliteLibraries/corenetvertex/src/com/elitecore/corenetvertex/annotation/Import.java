package com.elitecore.corenetvertex.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elitecore.corenetvertex.core.imports.ImportOperation;
import com.elitecore.corenetvertex.core.validator.Validator;

@Retention(RetentionPolicy.RUNTIME)  @Target({FIELD, METHOD,TYPE})
public @interface Import {
	  
	boolean required() default false;
	Class<? extends Validator> validatorClass() default Validator.class;
	Class<? extends ImportOperation> importClass() default ImportOperation.class;

}