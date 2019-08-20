package com.elitecore.nvsmx.system.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.elitecore.nvsmx.system.components.Label;
import com.opensymphony.xwork2.util.ValueStack;

public class LabelTag extends org.apache.struts2.views.jsp.ui.LabelTag{

	/**
	 * Custom Label Tag
	 */
	private static final long serialVersionUID = 1L;
	protected String labelClass ;
	
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new Label(stack, req, res);
	}
	
	@Override
	protected void populateParams() {
		super.populateParams();
		Label label = (Label)component;
		label.setLabelClass(labelClass);
	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}
}
