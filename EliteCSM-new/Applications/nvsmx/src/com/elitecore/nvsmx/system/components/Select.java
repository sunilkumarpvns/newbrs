package com.elitecore.nvsmx.system.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.util.ValueStack;

public class Select extends org.apache.struts2.components.Select{

	protected String labelClass;
	protected String width;

	public Select(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	public void evaluateParams() {
		super.evaluateParams();
		if(Strings.isNullOrBlank(labelClass) == false){
			addParameter("labelClass", findValue(labelClass, String.class));
		}
		if(Strings.isNullOrBlank(width) == false){
			addParameter("width", findValue(width, String.class));
		}

	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}

	public void setWidth(String width) {
		this.width = width;
	}


}
