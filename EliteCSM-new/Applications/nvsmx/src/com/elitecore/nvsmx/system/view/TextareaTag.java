package com.elitecore.nvsmx.system.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.elitecore.nvsmx.system.components.TextArea;
import com.opensymphony.xwork2.util.ValueStack;



public class TextareaTag extends org.apache.struts2.views.jsp.ui.TextareaTag{

	/**
	 * Custom Text Area Tag.
	 * Extends default textarea tag of Struts.
	 * Provides one extra attribute -> labelClass  
	 */
	private static final long serialVersionUID = 1L;
	protected String labelClass;
	
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new TextArea(stack, req, res);
	}
	
	@Override
	protected void populateParams() {
		super.populateParams();
		TextArea textArea = (TextArea)component;
		textArea.setLabelClass(labelClass);
	}
	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}

}
