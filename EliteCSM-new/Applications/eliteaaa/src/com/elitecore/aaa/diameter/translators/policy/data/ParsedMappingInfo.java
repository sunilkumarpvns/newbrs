package com.elitecore.aaa.diameter.translators.policy.data;

import java.util.List;

import com.elitecore.exprlib.parser.expression.LogicalExpression;



public interface ParsedMappingInfo {
	
	public LogicalExpression getInRequestExpression();
	public String getInRequestStrExpression();
	public String getOutRequestType();
	public  List<ParsedMapping> getParsedRequestMappingDetails();
	public  List<ParsedMapping> getParsedResponseMappingDetails();
	public boolean isDummyResponse();

	//Added For Translation Mapping Name
	public String getMappingName();

}
