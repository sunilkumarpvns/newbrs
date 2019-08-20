package com.elitecore.aaa.core.plugins.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.plugins.AAAUniversalPluginPolicyDetail;
import com.elitecore.aaa.core.plugins.BaseUniversalPlugin.UniversalPluginActionConstants;
import com.elitecore.aaa.core.plugins.RadiusParamDetails;
import com.elitecore.aaa.core.plugins.conf.BasePluginConfigurable;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.plugins.ParamDetails;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

public abstract class BaseUniversalPluginConfigurable<T> extends BasePluginConfigurable<T> {

	@XmlTransient private static final int DEFAULT_PACKET_TYPE = 0;
	@XmlTransient private static final String EXPR_LIB_MULTIPLE_OCCURANCES_TOKEN = "*";
	
	private List<AAAUniversalPluginPolicyDetail> prePolicyList;
	private List<AAAUniversalPluginPolicyDetail> postPolicyList;

	public BaseUniversalPluginConfigurable() {
		prePolicyList = new ArrayList<AAAUniversalPluginPolicyDetail>();
		postPolicyList = new ArrayList<AAAUniversalPluginPolicyDetail>();
	}

	@XmlTransient
	public List<AAAUniversalPluginPolicyDetail> getPrePolicyList() {
		return prePolicyList;
	}
	
	public void setPrePolicyList(
			List<AAAUniversalPluginPolicyDetail> prePolicyList) {
		this.prePolicyList = prePolicyList;
	}

	@XmlTransient
	public List<AAAUniversalPluginPolicyDetail> getPostPolicyList() {
		return postPolicyList;
	}
	
	public void setPostPolicyList(
			List<AAAUniversalPluginPolicyDetail> postPolicyList) {
		this.postPolicyList = postPolicyList;
	}
	
	/*
	 * This will compile the logical expression in such a way so that when the reject on check item not found
	 * property is set then the logical expression will be formed in such a way that the expression result will
	 * be false when the attribute is not found. And in the case when that parameter is false then the expression
	 * will be formed with the optional parameter syntax so if the attribute is not found still the result of the 
	 * expression will be true.
	 * 
	 */
	protected void compile(AAAUniversalPluginPolicyDetail pluginPolicyDetail, RadiusParamDetails details){
		try{
			if(details.getParameter_usage().equalsIgnoreCase("C") || details.getParameter_usage().equalsIgnoreCase("J")){
				if(details.getAttr_id() != null && details.getAttr_id().trim().length() > 0 && details.getAttribute_value() != null && details.getAttribute_value().trim().length() > 0){
					String condition;
					if(pluginPolicyDetail.isRejectOnCheckedItemNotFound()){
						condition = details.getAttr_id() + "="  + details.getAttribute_value();
					}else{
						condition = details.getAttr_id().trim();
						if(condition.startsWith(EXPR_LIB_MULTIPLE_OCCURANCES_TOKEN)){
							if(condition.length() > 1){
								condition = "*[" + condition.substring(1) + "]" + "="  + details.getAttribute_value();
							}else{
								throw new InvalidExpressionException(pluginPolicyDetail.getName() + "- Invalid attribute Id configured: " + details.getAttr_id());
							}
						}else{
							condition = "[" + condition + "]=" + details.getAttribute_value();
						}
					}
					details.setLogicalExpression(Compiler.getDefaultCompiler().parseLogicalExpression(condition));
				}
			}else if(details.getParameter_usage().equals("R") || details.getParameter_usage().equals("A") || details.getParameter_usage().equals("V") || details.getParameter_usage().equals("U")){
				if(details.getAttr_id() != null && details.getAttr_id().length() > 0 && details.getAttribute_value() != null && details.getAttribute_value().length() > 0){
					String condition = details.getAttribute_value();
					/**
					 * this check is applied for Update item operation.reason 
					 * ex- mac2tgpp("IMEISV",0:31.key,"01020304050607","FF#")* is not valid
					 * attribute value for registration of expression mac2tgpp("IMEISV",0:31.key,"01020304050607","FF#")
					 * so , remove first * from expression 
					 * than after register expression.
					 */
					if(condition.endsWith("*")){
						condition = condition.substring(0, condition.length()-1);
			}
					details.setExpression(Compiler.getDefaultCompiler().parseExpression(condition));
				}
			}
		}catch(InvalidExpressionException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModuleName(), pluginPolicyDetail.getName() + "- Invalid expression configured: " + details.getAttr_id() + "=" + details.getAttribute_value() + ". Reason:" + ex.getMessage());
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
				LogManager.getLogger().trace(getModuleName(), ex);
		}
		}
				}


	protected List<AAAUniversalPluginPolicyDetail> getParsePolicies(List<AAAUniversalPluginPolicyDetail> policyList){

		List<AAAUniversalPluginPolicyDetail> tempPolicyList = new ArrayList<AAAUniversalPluginPolicyDetail>();
		AAAUniversalPluginPolicyDetail pluginPolicyDetail;
		for(int i =0;i<policyList.size();i++){
			pluginPolicyDetail =  policyList.get(i);
			List<? extends ParamDetails> paramDetailList = pluginPolicyDetail.getParameterDetailsForPlugin();
			if(paramDetailList != null && paramDetailList.size() > 0){
				RadiusParamDetails details;
				for(int j = 0;j<paramDetailList.size();j++){
					details = (RadiusParamDetails)paramDetailList.get(j);
					pluginPolicyDetail.setPolicyAction(Numbers.parseInt(pluginPolicyDetail.getAction(),UniversalPluginActionConstants.NONE.value));

					//setting the logical expression
					compile(pluginPolicyDetail, details);
	}
				tempPolicyList.add(pluginPolicyDetail);
		}
	}
		return tempPolicyList;
	}

	@XmlTransient
	protected abstract String getModuleName();
	}