package com.elitecore.netvertexsm.web.core.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public interface ISearchAction {
	
	public ActionForward initSearch(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception;
	public ActionForward search(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception;

}
