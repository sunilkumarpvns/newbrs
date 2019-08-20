package com.elitecore.nvsmx.system.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.elitecore.nvsmx.system.components.Select;
import com.opensymphony.xwork2.util.ValueStack;

public class SelectTag extends org.apache.struts2.views.jsp.ui.SelectTag {

	/**
 	 * Custom Select Tag.
	 * Extends default select tag of Struts.
	 * Provides one extra attribute -> labelClass 
	 */
	private static final long serialVersionUID = 1L;
	protected String labelClass ;
	protected String width;
	
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new Select(stack, req, res);
	}
	
	@Override
	protected void populateParams() {
		super.populateParams();
		Select select = (Select)component;
		select.setLabelClass(labelClass);
		select.setWidth(width);
	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}
