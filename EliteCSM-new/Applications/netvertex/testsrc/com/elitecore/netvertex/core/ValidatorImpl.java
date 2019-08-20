package com.elitecore.netvertex.core;

import java.util.ArrayList;
import java.util.List;

public class ValidatorImpl<T> implements Validator<T> {

	private List<Validation<T>> validations;
	
	public ValidatorImpl() {
		validations = new ArrayList<Validation<T>>();
	}
	
	public ValidatorImpl(List<Validation<T>> validations) {
		this.validations = new ArrayList<Validation<T>>(validations);
	}
	
	
	
	@Override
	public void validate(T t) throws Exception {
		
		for(Validation<T> validation : validations){
			validation.validate(t);
			
		}
	}

}
