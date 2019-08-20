package com.elitecore.corenetvertex.core.validator;

import com.elitecore.corenetvertex.util.SessionProvider;

import java.util.List;


public interface Validator<T, PT, RT> {

	public List<String> validate(T t, PT parentObject, RT superObject, String action,SessionProvider session);

}
