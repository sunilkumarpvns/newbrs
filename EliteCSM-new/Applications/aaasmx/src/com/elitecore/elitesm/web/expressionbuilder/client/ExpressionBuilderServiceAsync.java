package com.elitecore.elitesm.web.expressionbuilder.client;

import java.util.List;

import com.elitecore.elitesm.web.expressionbuilder.shared.AttributeData;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>ExpressionBuilderService</code>.
 */
public interface ExpressionBuilderServiceAsync {

	void getAttributeList(String DictionaryType,AsyncCallback<List<AttributeData>> callback);
	
}
