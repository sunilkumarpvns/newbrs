package com.elitecore.nvsmx.system.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.elitecore.nvsmx.system.components.Password;
import com.opensymphony.xwork2.util.ValueStack;

public class PasswordTag extends org.apache.struts2.views.jsp.ui.PasswordTag {

	/**
	 * Custom Password Tag.
	 * Extends default password tag of Struts.
	 * Provides one extra attribute -> labelClass 
	 */
	private static final long serialVersionUID = 1L;
	protected String labelClass ;
	protected String icon;
	protected String iconClass;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new Password(stack, req, res);
	}
	
	@Override
	protected void populateParams() {
		super.populateParams();
		Password password = (Password)component;
		password.setLabelClass(labelClass);
		password.setIcon(icon);
		password.setIconClass(iconClass);
	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}

}
