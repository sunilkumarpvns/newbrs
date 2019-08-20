package com.elitecore.nvsmx.system.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import com.elitecore.nvsmx.system.components.Container;
import com.opensymphony.xwork2.util.ValueStack;

public class ContainerTag extends AbstractUITag{

	/**
	 * This tag will be used to generate a default container div for the page.
	 * It provides 2 attributes primarily
	 * 1) Content (Required) - Takes String value as input for the jsp to include in the body of the container
	 * 2) Left Menu  (Optional)- Takes String value as input for the jsp to include in the left menu space.
	 */
	private static final long serialVersionUID = 1L;


	protected String leftMenu ;
	protected String content ;
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new Container(stack, req, res);
	}

	@Override
	protected void populateParams() {
		super.populateParams();
		Container container = (Container)component;
		container.setLeftMenu(leftMenu);
		container.setContent(content);
	}
	
	public void setLeftMenu(String leftMenu) {
		this.leftMenu = leftMenu;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
