package com.elite.context;

import javax.servlet.ServletContext;

import com.elite.exception.ContextManagerNotInitialisedException;


/* Context_Manager is deprecated and
 * the present implementation is using ActionContext.*/
@Deprecated
public class Context_Manager {
	private static Context_Manager context_manager = null;
	
	public static boolean init(ServletContext serv_context)
	{
		Context_Manager.context_manager = new Context_Manager(); 
		context_manager.servlet_context = serv_context;
		context_manager.wsc_context = new WSCContext();
		return true;
	}
	
	
	public static Context_Manager getInstance() throws ContextManagerNotInitialisedException
	{
		if(context_manager == null)
		{
			throw new ContextManagerNotInitialisedException();
		}
		return context_manager;
	}
	
	private Context_Manager(){}
	private ServletContext servlet_context = null;
	private WSCContext wsc_context = null;
	
	public ServletContext getServlet_context() {
		return servlet_context;
	}

	public WSCContext getWsc_context() {
		return wsc_context;
	}

}
