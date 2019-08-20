package com.elitecore.nvsmx.system.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.UIBean;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.util.ValueStack;


public class Container extends UIBean {

	protected String leftMenu ;
	protected String content ;

	public Container(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected String getDefaultTemplate() {
		return "container";
	}

	@Override
	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		if(Strings.isNullOrBlank(leftMenu) == false){
			addParameter("leftMenu", findValue(leftMenu, String.class));
		}
		if(Strings.isNullOrBlank(content) ==  false){
			addParameter("content", findValue(content, String.class));
		}
	}


	public void setLeftMenu(String leftMenu) {
		this.leftMenu = leftMenu;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLeftMenu() {
		return leftMenu;
	}

	public String getContent() {
		return content;
	}}
