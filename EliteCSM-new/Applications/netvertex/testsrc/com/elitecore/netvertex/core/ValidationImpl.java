package com.elitecore.netvertex.core;

import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

public class ValidationImpl<T> implements Validation<T> {
	
	private T expectedVal;
	private String message;
	
	public ValidationImpl(String msg,T expectedVal) {
		this.expectedVal = expectedVal;
		this.message = msg;
	}

	@Override
	public void validate(T actualVal){
		if(message != null && message.trim().isEmpty() == false){
			ReflectionAssert.assertReflectionEquals(expectedVal,actualVal, ReflectionComparatorMode.LENIENT_ORDER);
		} else {
			ReflectionAssert.assertReflectionEquals(message,expectedVal, actualVal,ReflectionComparatorMode.LENIENT_ORDER);
		}
	}

}
