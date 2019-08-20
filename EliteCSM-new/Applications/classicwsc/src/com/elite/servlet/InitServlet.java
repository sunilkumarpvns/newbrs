package com.elite.servlet;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;


import com.elite.config.AAAConfig;
import com.elite.config.LDAPConfig;
import com.elite.config.ORACLEConfig;
import com.elite.config.WSCConfig;
import com.elite.context.Context_Manager;
import com.elite.context.WSCContext;
import com.elite.logger.Log4jLogger;
import com.opensymphony.xwork2.ActionContext;
import com.sun.org.apache.regexp.internal.recompile;
import com.wutka.jox.JOXBeanInputStream;

public class InitServlet extends HttpServlet {
	Logger logger = Logger.getLogger("wsc");
	public void destroy() {
		super.destroy(); 
	}

	public void init() throws ServletException {
		try
        {
			new Log4jLogger(); 
			logger.info("init config read is starting...");
			String path =  getServletContext().getRealPath(File.separatorChar+"WEB-INF"+File.separatorChar+"config"+File.separatorChar+"wsc.xml");
			JOXBeanInputStream joxIn = new JOXBeanInputStream(new FileInputStream(path));
			WSCConfig wsc = (WSCConfig) joxIn.readObject(WSCConfig.class);
			path =  getServletContext().getRealPath(File.separatorChar+"WEB-INF"+File.separatorChar+"config"+File.separatorChar+"ldap.xml");
			joxIn = new JOXBeanInputStream(new FileInputStream(path));
			LDAPConfig ldap = (LDAPConfig) joxIn.readObject(LDAPConfig.class);
			path =  getServletContext().getRealPath(File.separatorChar+"WEB-INF"+File.separatorChar+"config"+File.separatorChar+"aaa.xml");
			joxIn = new JOXBeanInputStream(new FileInputStream(path));
			AAAConfig aaa = (AAAConfig) joxIn.readObject(AAAConfig.class);
			path =  getServletContext().getRealPath(File.separatorChar+"WEB-INF"+File.separatorChar+"config"+File.separatorChar+"oracle-database.xml");
			joxIn = new JOXBeanInputStream(new FileInputStream(path));
			ORACLEConfig ora = (ORACLEConfig) joxIn.readObject(ORACLEConfig.class);
			logger.info(wsc);
			logger.info(ldap);
			logger.info(aaa);
			logger.info(ora);
			logger.info("init config read has finished");
			logger.info("init context setup is starting...");
            //Context_Manager.init(getServletContext());
            //Context_Manager context_manager = Context_Manager.getInstance();
            WSCContext wsc_context = new WSCContext();
            wsc_context.setAttribute("wsc", wsc);
            wsc_context.setAttribute("ldap", ldap);
            wsc_context.setAttribute("aaa", aaa);
            wsc_context.setAttribute("ora", ora);
            getServletContext().setAttribute("wsc_context", wsc_context);
            logger.info("init context setup has finished");
        }
        catch (Exception exc)
        {
        	logger.error(exc);
        }
	}

}
