package com.elitecore.nvsmx.system.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.elitecore.nvsmx.system.components.Radio;
import com.opensymphony.xwork2.util.ValueStack;

public class RadioTag extends org.apache.struts2.views.jsp.ui.RadioTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String labelClass ;
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new Radio(stack, req, res);
	}
	
	@Override
	protected void populateParams() {
		super.populateParams();
		Radio radio = (Radio) component;
		radio.setLabelClass(labelClass);
	}
	
	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}
	
	

}
