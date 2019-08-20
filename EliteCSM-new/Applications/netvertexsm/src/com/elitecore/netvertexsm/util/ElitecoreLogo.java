package com.elitecore.netvertexsm.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.elitecore.netvertexsm.ws.logger.Logger;

/**
 * @author kirpalsinh.raj
 *
 */
public class ElitecoreLogo extends TagSupport {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = ElitecoreLogo.class.getSimpleName();

	public int doStartTag() throws JspException {
		
		JspWriter 	 painter = pageContext.getOut();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();		
		StringBuffer results = new StringBuffer();
		results.append("<img  type=\"image\" src=\""+request.getContextPath()+"/images/jisp.jpg\"  width=\"101\" height=\"113\" />");
		try {
			painter.write(results.toString());
		} catch (IOException e) {
			Logger.logError(MODULE, "Exception reason : "+e.getMessage());
			e.printStackTrace();
		}		
		return EVAL_PAGE;		
	}
}
