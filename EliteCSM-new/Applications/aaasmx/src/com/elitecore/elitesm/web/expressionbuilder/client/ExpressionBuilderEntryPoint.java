package com.elitecore.elitesm.web.expressionbuilder.client;


import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.expressionbuilder.client.ui.EliteEditor;
import com.elitecore.elitesm.web.expressionbuilder.shared.AttributeData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ExpressionBuilderEntryPoint implements EntryPoint {



	private final ExpressionBuilderServiceAsync expressionBuilderService=GWT.create(ExpressionBuilderService.class);
	private static final boolean logEnabled = false;

	List<AttributeData> attributeList = new ArrayList<AttributeData>();
	


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		/*
		 *  get Attribute List from Server
		 */

		try{
			Log.setUncaughtExceptionHandler();
			if(!logEnabled){
				Widget logWidget = Log.getLogger(DivLogger.class).getWidget();
				logWidget.setVisible(false);
			}
           
			
			Dictionary javascriptVariableDictionary = Dictionary.getDictionary("jsVars"); 
			
			String dictionaryType = javascriptVariableDictionary.get("dictionaryType");
           //String dictionaryType=Window.Location.getParameter("dictionaryType");
   		   getAttributeList(dictionaryType);


		}catch(Exception exp){
			Log.error("Exception throws",exp.getCause());
		}


	}

	private void initGUI() {
		EliteEditor eliteEditor=new EliteEditor(attributeList);
		RootPanel.get("expBuilderId").add(eliteEditor);
       
	}

	private void  getAttributeList(String dictionaryType) {

		AsyncCallback<List<AttributeData>>  attrCallback = new AsyncCallback<List<AttributeData>>(){
			public void onFailure(Throwable caught) {
				Log.error("Error in fetching  Attribute List",caught);			
			}
			public void onSuccess(List<AttributeData> result) {
				if(result == null)
					Log.debug("Result is blank ");
				else
					Log.debug("Result : " + result.size());
								
				attributeList=result;	
				initGUI();
				DOM.removeChild(RootPanel.getBodyElement(), DOM.getElementById("loading"));
			}

		};
		expressionBuilderService.getAttributeList(dictionaryType, attrCallback);

	}
}
