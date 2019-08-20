package com.elitecore.elitesm.web.core.system.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.logger.Logger;



public final class RequestWrapper extends HttpServletRequestWrapper
{

	private static ESAPISecurityPlugin serverManagerSecurity;
	static final String MODULE = "[REQ-WRAPPER] ";

	public RequestWrapper(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SecurityException
	{
		super(servletRequest);
		if (serverManagerSecurity == null)
		{
			serverManagerSecurity = new ESAPISecurityPlugin();
			serverManagerSecurity.init();
		}
		EliteUtility.setRemoteAddress(servletRequest.getRemoteAddr());
	}

	public String[] getParameterValues(String parameter)
	{
		String[] values = super.getParameterValues(parameter);
		if (values == null) { return null; }
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++)
		{
			try
			{
				encodedValues[i] = (String) serverManagerSecurity.scanVulnerability(values[i], null);
			}
			catch (ServerManagerSecurityException e)
			{
				Logger.logError("SecurityException  :", e);
				throw new SecurityException();
			}
		}
		return encodedValues;
	}

	public String getParameter(String parameter) throws SecurityException
	{
		String value = super.getParameter(parameter);
		if (value == null) { return null; }
		String val = null;
		try
		{
			val = (String) serverManagerSecurity.scanVulnerability(value, null);
		}
		catch (ServerManagerSecurityException e)
		{
			Logger.logError("SecurityException  :", e);
			throw new SecurityException();
		}
		return val;
	}

	public String getHeader(String name)
	{
		String value = super.getHeader(name);
		if (value == null)
			return null;
		String val = null;
		try
		{
			System.out.println("value in wrapper : "+value);
			val = (String) serverManagerSecurity.scanVulnerability(value, null);
		}
		catch (ServerManagerSecurityException e)
		{
			Logger.logError("SecurityException  :", e);
			throw new SecurityException();
		}
		return val;
	}

	public Object getAttribute(String parameter)
	{
		Object value = super.getAttribute(parameter);
		if (value == null) { return null; }

		Object val = null;
		try
		{
			val = serverManagerSecurity.scanVulnerability(value, null);
		}
		catch (ServerManagerSecurityException e)
		{
			Logger.logError("SecurityException  :", e);
			throw new SecurityException();
		}
		return val;
	}

	public void setAttribute(String key, Object value)
	{
		if (value != null)
		{
			try
			{
				serverManagerSecurity.scanVulnerability(value, null);
			}
			catch (ServerManagerSecurityException e)
			{
				Logger.logError("SecurityException  :", e);
				throw new SecurityException();
			}
		}
		super.setAttribute(key, value);
	}

	// Created for Audit Purpose only. Do not use in Application any where.
	public String getUnSecuredParameter(String parameter) throws SecurityException
	{
		Logger.logDebug(MODULE, "In RequestWrapper getUnSecuredParameter ");
		String value = super.getParameter(parameter);
		if (value == null) { return null; }
		return value;
	}

	public Object getUnSecuredAttribute(String parameter)
	{
		Logger.logDebug(MODULE,  "In getAttribute getUnSecuredAttribute ");
		Object value = super.getAttribute(parameter);
		if (value == null) { return null; }
		return value;
	}
}