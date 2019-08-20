package com.elitecore.elitesm.web.expressionbuilder.client.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.web.expressionbuilder.client.core.ListUtility;
import com.elitecore.elitesm.web.expressionbuilder.client.core.StateMachine;
import com.elitecore.elitesm.web.expressionbuilder.shared.AttributeData;

public class AutoCompletionItems implements ICompletionItems {
	  
	  private ListUtility lstUtil = null;
	  public AutoCompletionItems(List<AttributeData> attributeList)
	  {
		lstUtil = new ListUtility(attributeList);
	  }

      public Map<String,String> getCompletionItems(String prefixText) {
	   
		  
		  
			String currentState = StateMachine.getCurrentState(prefixText);
			Map<String,String> comp = new HashMap<String, String>();
			if(currentState == "s0"){
				comp = lstUtil.getCompletionsForS0(new String(StateMachine.currentExpr));
			}else if(currentState == "s1"){
				comp = lstUtil.getCompletionsForS1(new String(StateMachine.currentExpr));
			}else if(currentState == "s2"){
				comp = lstUtil.getCompletionsForS2(new String(prefixText));
			}else if(currentState == "s4"){
				comp = lstUtil.getCompletionsForS4(new String(StateMachine.currentExpr));
			}
			return comp;
	} 
}
