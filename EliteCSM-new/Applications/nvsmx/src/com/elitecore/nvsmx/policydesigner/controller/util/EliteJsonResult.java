package com.elitecore.nvsmx.policydesigner.controller.util;

import org.apache.struts2.json.JSONResult;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * {@link EliteJsonResult} will help to set "IncludeProperties" dynamically.
 * @author Dhyani.Raval
 *
 */
public class EliteJsonResult extends JSONResult {

	private static final long serialVersionUID = 1L;
	private String modelProperties;

	@Override
	public void execute(ActionInvocation actionInvocation) throws Exception {
		if(Strings.isNullOrBlank(modelProperties) == false){
			String parsedModelProperties; 
			if(modelProperties.startsWith("$") || modelProperties.startsWith("%")){
				parsedModelProperties = modelProperties.substring(2, modelProperties.length()-1);
			}
			else{
				parsedModelProperties = modelProperties;
			}
			ValueStack valueStack = actionInvocation.getStack();
			String includeSearchProperties = (String) valueStack.findValue(parsedModelProperties);
			if(Strings.isNullOrBlank(includeSearchProperties) == false){
				setIncludeProperties(includeSearchProperties);
			}
		}
		super.execute(actionInvocation);
	}
	
	public void setModelProperties(String modelProperties){
		this.modelProperties = modelProperties;
	}
	
	
	public String getModelProperties(){
		return modelProperties;
	}
	
}
