package com.elitecore.netvertexsm.web.core.util;

import java.io.PrintWriter;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;


public class ValidateConditionAction extends BaseWebDispatchAction{
	
	private static final String TIMEOUT = "Timeout Occur";
	private static final String ERROR_MESSAGE = "Error While Validating With Live Server";
	private static final String NO_LIVE_SERVER_FOUND = "No Live Server Found";
	private static final int JMX_TIMEOUT = 5;
	public ActionForward validateCondition(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE,"Enter validateCondition method of "+getClass().getName());
		response.setContentType("application/text; charset=utf-8");
		String condition = request.getParameter("condition");
		String validationResult = null;
		PrintWriter out = null;
		try{
			out = response.getWriter();
			Object[] objParameter = {condition};
    		String[] parameterTypes = {"java.lang.String"};
			Future<String> future = TaskSchedular.getInstance().submit(new JMXCallable<String>(MBeanConstants.VALIDATECONDITION,"validateCondition",objParameter,parameterTypes));  
			
			validationResult = future.get(JMX_TIMEOUT, TimeUnit.SECONDS);
			if(Strings.isNullOrBlank(validationResult) == true){
				validationResult = NO_LIVE_SERVER_FOUND;
			}
		} catch(TimeoutException e){
			validationResult = TIMEOUT;
			Logger.logError(MODULE, "Timeout occur while validating condition. Reason: "+e.getMessage());
		} catch(Exception e){
			validationResult = ERROR_MESSAGE;
			Logger.logError(MODULE, "Error while validating condition. Reason: "+e.getMessage());
		} finally {
			out.print(validationResult);
			out.flush();
			out.close();
		}
		return null;
	}
	
	public ActionForward validateTimeSlot(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE,"Enter validateTimeSlot method of "+getClass().getName());
		response.setContentType("application/text; charset=utf-8");
		String moy = request.getParameter("moyVal");
		String dom = request.getParameter("domVal");
		String dow = request.getParameter("dowVal");
		String timePeriod = request.getParameter("timePeriodVal");
		PrintWriter out = null;
		
		String validationResult = null;
		try{
			out = response.getWriter();
			Object[] objParameter = {moy,dom,dow,timePeriod};
			String[] parameterTypes = {"java.lang.String","java.lang.String","java.lang.String","java.lang.String"};
			Future<String> future = TaskSchedular.getInstance().submit(new JMXCallable<String>(MBeanConstants.VALIDATECONDITION,"validateTimeSlot",objParameter,parameterTypes));   
			validationResult  = future.get(JMX_TIMEOUT, TimeUnit.SECONDS);
			if(Strings.isNullOrBlank(validationResult) == true){
				validationResult = NO_LIVE_SERVER_FOUND;
			}
			
		}catch(TimeoutException e){
			validationResult = TIMEOUT;
			Logger.logError(MODULE, "Timeout occur while validating time slot. Reason: "+e.getMessage());
		}catch(Exception e){
			validationResult = ERROR_MESSAGE;
			Logger.logError(MODULE, "Error while validating time slot. Reason: "+e.getMessage());
		}finally {
			out.print(validationResult);
			out.flush();
			out.close();
		}
		return null;
	}
	public ActionForward validatePacketMapping(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE,"Enter validatePacketMapping() method of "+getClass().getName());
		response.setContentType("application/text; charset=utf-8");
		String validationResult = null;
		String policyKey = request.getParameter("policyKey");
		String attribute = request.getParameter("attribute");
		String defaultValue = request.getParameter("defaultValue");
		String valueMapping = request.getParameter("valueMapping");
		String conversationType = request.getParameter("conversationType").trim();
		PrintWriter out = null;
		
		try {
			out = response.getWriter();
			Object[] objParameter = {policyKey,attribute,defaultValue,valueMapping,conversationType};
			String[] parameterTypes = {"java.lang.String","java.lang.String","java.lang.String","java.lang.String","java.lang.String"};
			Future<String> future = TaskSchedular.getInstance().submit(new JMXCallable<String>(MBeanConstants.VALIDATECONDITION,"validateMapping",objParameter,parameterTypes)); 
			validationResult = future.get(JMX_TIMEOUT, TimeUnit.SECONDS);
			
			if(Strings.isNullOrBlank(validationResult) == true){
				validationResult = NO_LIVE_SERVER_FOUND;
			}
			
		} catch(TimeoutException e){
			validationResult = TIMEOUT;
			Logger.logError(MODULE, "Timeout occur while validating mapping. Reason: "+e.getMessage());
		} catch(Exception e){
			validationResult = ERROR_MESSAGE;
			Logger.logError(MODULE, "Error while validating mapping. Reason: "+e.getMessage());
		} finally {
			out.print(validationResult);
			out.flush();
			out.close();
		}
		return null;
	}
	
}
