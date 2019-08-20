package com.elitecore.aaa.util.mbean;

import java.lang.reflect.InvocationTargetException;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;

public abstract class SubscriberProfileController extends BaseMBeanController implements DynamicMBean{

	private String name;
	public SubscriberProfileController(String name) {
		this.name = name;
	}
	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		return null;
	}

	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException,
			ReflectionException {
	}

	@Override
	public AttributeList getAttributes(String[] attributes) {
		return null;
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		return null;
	}

	@Override
	public Object invoke(String actionName, Object[] params,
			String[] signature) throws MBeanException, ReflectionException {
		
		try {
			Object accountData =SubscriberProfileController.class.getMethod("getSubscriberProfile", String.class).invoke(this, params); 
			if(accountData!=null){
				return accountData.toString();
			}else {
				return "customer profile not found";
			}
		} catch (IllegalArgumentException e) {
			
		} catch (SecurityException e) {
			
		} catch (IllegalAccessException e) {
			
		} catch (InvocationTargetException e) {
			
		} catch (NoSuchMethodException e) {
			
		}
		
		return null;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		
		MBeanOperationInfo[] operationArray = new MBeanOperationInfo[1];
		
		java.lang.reflect.Method  method= null;
		try {
			method= SubscriberProfileController.class.getMethod("getSubscriberProfile", String.class);
			
		} catch (SecurityException e) {
		
		} catch (NoSuchMethodException e) {
			
		};
		operationArray[0] = new MBeanOperationInfo("method to fetch subscriber profile", method);
		return new MBeanInfo(this.getClass().getName(), "subscriber profile implementation", null, null, operationArray, null);
	}
	public abstract AccountData getSubscriberProfile(String identity) ;
	
	@Override
	public String getName() {
		return name;
	}
	

}
