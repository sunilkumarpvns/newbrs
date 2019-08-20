package com.elitecore.nvsmx.system.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.util.ValueStack;


public class Password extends org.apache.struts2.components.Password{

	protected String labelClass ;
	protected String icon;
	protected String iconClass;
	
	public Password(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}
	@Override
	public void evaluateExtraParams() {
		super.evaluateExtraParams();
		if(Strings.isNullOrBlank(labelClass) == false){
			addParameter("labelClass", findValue(labelClass, String.class));
		}
		if(Strings.isNullOrBlank(icon) == false){
			addParameter("icon", findValue(icon, String.class));
		}
		if(Strings.isNullOrBlank(iconClass) ==  false){
			addParameter("iconClass", findValue(iconClass, String.class));
		}
	}
	
	@Override
	protected String getDefaultTemplate() {
		return super.getDefaultTemplate();
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
