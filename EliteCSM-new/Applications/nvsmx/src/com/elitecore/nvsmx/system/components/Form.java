package com.elitecore.nvsmx.system.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.util.ValueStack;


public class Form extends org.apache.struts2.components.Form {
	protected String validator;
	public Form(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}
	
	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		if (Strings.isNullOrBlank(validator) == false) {
			addParameter("validator", findValue(validator,String.class));
		}
	}
	public String getValidator() {
		return validator;
	}
	public void setValidator(String validator) {
		this.validator = validator;
	}

}
