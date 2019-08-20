package com.elitecore.nvsmx.system.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.elitecore.nvsmx.system.components.Form;
import com.opensymphony.xwork2.util.ValueStack;


public class FormTag extends org.apache.struts2.views.jsp.ui.FormTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String validator;
	
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new Form(stack, req, res);
	}
	
	@Override
	protected void populateParams() {
		super.populateParams();
		Form form = ((Form)component);
		form.setValidator(validator);
	}

	public String getValidator() {
		return validator;
	}

	public void setValidator(String validator) {
		this.validator = validator;
	}
	
}
