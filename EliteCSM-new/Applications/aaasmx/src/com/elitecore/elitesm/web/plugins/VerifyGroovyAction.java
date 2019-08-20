package com.elitecore.elitesm.web.plugins;

import groovy.lang.GroovyClassLoader;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.codehaus.groovy.control.CompilationFailedException;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;

/**
 * @author nayana.rathod
 * 
 * <br/>
 * <br/>
 * 
 * <b>Purpose </b> : This class is mainly used for validating groovy file
 * 
 */

public class VerifyGroovyAction extends BaseWebAction{
	
	private static String MODULE = VerifyGroovyAction.class.getSimpleName();
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest  request, HttpServletResponse response) throws IOException,InvocationTargetException{
		
		Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
		PrintWriter out = response.getWriter();
		String groovyExecutorResult = "success";
		response.setContentType("text/plain");
		
		try{
			
			String fileContent = request.getParameter("fileContent");
			LogManager.getLogger().info(MODULE,fileContent);
			
			try {
				
				 final GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader());
			        
			      // Create a String with Groovy code.
			     final StringBuilder groovyScript = new StringBuilder();
			     groovyScript.append(fileContent);
			     
			     // Load string as Groovy script class.
			     Class groovy = classLoader.parseClass(groovyScript.toString());
			     LogManager.getLogger().info(MODULE, groovy.toString());
			     
			     LogManager.getLogger().info(MODULE, " Success..!!! Your Groovy Script is Valid. " );
			
			}catch(CompilationFailedException ex){
				LogManager.getLogger().trace(MODULE, ex);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ex);
				request.setAttribute("errorDetails", errorElements);
				LogManager.getLogger().debug(MODULE, "Error in loading external Script: Reason: " + errorElements.toString());
				
				groovyExecutorResult = Arrays.toString(errorElements);
				groovyExecutorResult = groovyExecutorResult.substring(1, groovyExecutorResult.length()-1);
			}catch(SecurityException ex){
				LogManager.getLogger().trace(MODULE, ex);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ex);
				request.setAttribute("errorDetails", errorElements);
				LogManager.getLogger().debug(MODULE, "Error in loading external Script: Reason: " + errorElements.toString());
				
				groovyExecutorResult = Arrays.toString(errorElements);
				groovyExecutorResult = groovyExecutorResult.substring(1, groovyExecutorResult.length()-1);
			
			}catch(ExceptionInInitializerError ex){
				LogManager.getLogger().trace(MODULE, ex);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ex);
				request.setAttribute("errorDetails", errorElements);
				LogManager.getLogger().debug(MODULE, "Error in loading external Script: Reason: " + errorElements.toString());
				groovyExecutorResult = Arrays.toString(errorElements);
				groovyExecutorResult = groovyExecutorResult.substring(1, groovyExecutorResult.length()-1);
			}catch(IllegalArgumentException ex){
				LogManager.getLogger().trace(MODULE, ex);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ex);
				request.setAttribute("errorDetails", errorElements);
				LogManager.getLogger().debug(MODULE, "Error in loading external Script: Reason: " + errorElements.toString());
				groovyExecutorResult = Arrays.toString(errorElements);
				groovyExecutorResult = groovyExecutorResult.substring(1, groovyExecutorResult.length()-1);
			}catch(Throwable ex){
				LogManager.getLogger().trace(MODULE, ex);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ex);
				request.setAttribute("errorDetails", errorElements);
				LogManager.getLogger().debug(MODULE, "Error in loading external Script: Reason: " + errorElements.toString());
				groovyExecutorResult = Arrays.toString(errorElements);
				groovyExecutorResult = groovyExecutorResult.substring(1, groovyExecutorResult.length()-1);
			}
			
		}catch(Exception ex){
			LogManager.getLogger().trace(MODULE, ex);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ex);
			request.setAttribute("errorDetails", errorElements);
			LogManager.getLogger().debug(MODULE, "Error in loading external Script: Reason: " + errorElements.toString());
			groovyExecutorResult = Arrays.toString(errorElements);
			groovyExecutorResult = groovyExecutorResult.substring(1, groovyExecutorResult.length()-1);
		}
		
		LogManager.getLogger().info(MODULE,"Groovy File Result is : "+ groovyExecutorResult);
		out.println(groovyExecutorResult);
		return null;
	}

}
