package com.elitecore.elitesm.web.expressionbuilder.client;

import java.util.List;

import com.elitecore.elitesm.web.expressionbuilder.shared.AttributeData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("expbuilder")
public interface ExpressionBuilderService extends RemoteService {
	
	List<AttributeData> getAttributeList(String DictionaryType) throws IllegalArgumentException;
	
}
