package com.elitecore.nvsmx.system.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.util.ValueStack;
/*
@StrutsTag(name = "label",
tldTagClass = "com.elitecore.nvsmx.policydesigner.view.LabelTag", 
description = "Renders an HTML output", 
allowDynamicAttributes = true)*/
public class Label extends org.apache.struts2.components.Label{

	protected String labelClass;

	public Label(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		if(Strings.isNullOrBlank(labelClass) == false){
			addParameter("labelClass", findValue(labelClass, String.class));
		}
	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}


	@Override
	protected String getDefaultTemplate() {
		return "label";
	}
}
