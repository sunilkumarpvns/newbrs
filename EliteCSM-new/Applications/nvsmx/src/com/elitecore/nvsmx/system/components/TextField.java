package com.elitecore.nvsmx.system.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.util.ValueStack;


public class TextField extends org.apache.struts2.components.TextField {

	protected String labelClass;
	protected String icon;
	protected String iconClass;
	protected String bodyString;
	protected String width;
	
	
	public TextField(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		if (Strings.isNullOrBlank(labelClass) == false) {
			addParameter("labelClass", findValue(labelClass,String.class));
		}
		if(Strings.isNullOrBlank(icon) == false){
			addParameter("icon", findValue(icon, String.class));
		}
		if(Strings.isNullOrBlank(iconClass) ==  false){
			addParameter("iconClass", findValue(iconClass, String.class));
		}
		if(Strings.isNullOrBlank(bodyString) == false){
			addParameter("bodyString", findValue(bodyString, String.class));
		}
		if(Strings.isNullOrBlank(width) == false){
			addParameter("width", findValue(width, String.class));
		}
		
	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}
	
	@Override
	protected String getDefaultTemplate() {
		return super.getDefaultTemplate();
	}
	
	@Override
	public boolean usesBody() {
		return true;
	}
	

	public void setBodyString(String bodyString) {
		this.bodyString = bodyString;
	}

	
	
}