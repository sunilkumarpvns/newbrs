package com.elitecore.nvsmx.system.components;
		
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.util.ValueStack;

public class Radio extends org.apache.struts2.components.Radio {

	protected String labelClass;
	
	public Radio(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	public void evaluateExtraParams() {
		super.evaluateExtraParams();
		if(Strings.isNullOrBlank(labelClass) == false){
			addParameter("labelClass", findValue(labelClass, String.class));
		}
	}

	@Override
	protected String getDefaultTemplate() {
		return super.getDefaultTemplate();
	}
	

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}
	
}
