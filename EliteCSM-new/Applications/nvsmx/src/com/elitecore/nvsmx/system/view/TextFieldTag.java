package com.elitecore.nvsmx.system.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.struts2.components.Component;

import com.elitecore.nvsmx.system.components.TextField;
import com.opensymphony.xwork2.util.ValueStack;

public class TextFieldTag extends org.apache.struts2.views.jsp.ui.TextFieldTag {

	/**
	 * Custom Text Field Tag.
	 * Extends default textfield tag of Struts.
	 * Provides one extra attribute -> labelClass 
	 */

	private static final long serialVersionUID = 5811285953670562288L;

	protected String labelClass;
	protected String icon;
	protected String iconClass;

    protected BodyContent body;
    protected String bodyString;
    protected String width;

	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new TextField(stack, req, res);
	}

	@Override
	protected void populateParams() {

		super.populateParams();
		setBodyContent(getBodyContent());
		
		TextField textField = ((TextField) component);
		textField.setLabelClass(labelClass);
		textField.setIcon(icon);
		textField.setIconClass(iconClass);
		textField.setWidth(width);
		textField.setBodyString(bodyString);
		
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

	@Override
	protected String getBody() {
		// TODO Auto-generated method stub
		return super.getBody();
	}

	public void setWidth(String width) {
		this.width = width;
	}
	
	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	@Override
	public int doAfterBody() throws JspException {
		this.body = bodyContent;
		setBodyString(body.getString());
		return super.doAfterBody();
	}
	
    
	public void setBodyString(String bodyString) {
		TextField textField = ((TextField) component);
		textField.setBodyString(bodyString);
		this.bodyString = bodyString;
	}

}
