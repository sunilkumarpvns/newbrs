package com.elitecore.netvertexsm.util;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.elitecore.netvertexsm.ws.logger.Logger;

public class EliteVerifyNameTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = EliteVerifyNameTag.class.getSimpleName();

	private String size = "32";// default size of input text box for name field
	private String maxLength = "100"; // default max length for name field
	private String value = "";
	private String id = "name";//default id of input text box
	private String name = "name";//default name of input text box



	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size=size;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int doStartTag() throws JspException {
		JspWriter 	 painter = pageContext.getOut();
		StringBuilder results = new StringBuilder();
		results.append("<td align=\"left\" class=\"labeltext\" valign=\"top\" width=\"32%\">");
		results.append("<input type=\"text\" name=\""+getName()+"\" maxlength=\""+getMaxLength()+"\" size=\""+getSize()+"\" tabindex=\"1\" onkeyup=\"verifyFormat();\" onblur=\"verifyName();\" id=\""+getId()+"\" value=\""+getValue()+"\"/>");
		try {
			painter.write(results.toString());
		} catch (IOException e) {
			Logger.logError(MODULE, "Exception reason : "+e.getMessage());
			Logger.logTrace(MODULE, e);
		}		
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag()throws JspTagException {
		JspWriter 	 painter = pageContext.getOut();
		StringBuilder results = new StringBuilder();
		results.append("</div><font color=\"#FF0000\"> *</font><div id=\"verifyNameDiv\" class=\"labeltext\"></td>");
		try {
			painter.write(results.toString());
		} catch (IOException e) {
			Logger.logError(MODULE, "Exception reason : "+e.getMessage());
			Logger.logTrace(MODULE, e);
		}		
		return EVAL_PAGE;
	}

}