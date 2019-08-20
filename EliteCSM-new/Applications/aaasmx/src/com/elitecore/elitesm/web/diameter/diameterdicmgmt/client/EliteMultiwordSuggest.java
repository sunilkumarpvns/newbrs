package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.AttributeData;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

public class EliteMultiwordSuggest extends MultiWordSuggestOracle {
	
	public EliteMultiwordSuggest(String seperator) {
		super(seperator);
	}

	public void setSuggestions(List<String> idSuggestion,List<String> displaySuggestion) {
		try{
			
			Log.debug("EliteMultiwordSuggest : setSuggestions() Call Start ");
			Collection<Suggestion> accum = new ArrayList<Suggestion>();
			int len = displaySuggestion.size();
			for(int i=0;i<len;i++){
				String key=idSuggestion.get(i);
				String displayValue=displaySuggestion.get(i);
				add(key);
				accum.add(createSuggestion(key, displayValue));
			}
			setDefaultSuggestions(accum);
			
		}catch(Exception e){
			Log.error("set suggestions in EliteMultiwordSuggest",e);
		}
		
		
	}


	public void setSuggestions(Map<String, AttributeData> attrMap) {
		try{
			Collection<Suggestion> accum = new ArrayList<Suggestion>();
			Iterator<String> iterator=attrMap.keySet().iterator();

			while(iterator.hasNext()){
				String key=iterator.next();
				String displayString="";
				Object valueObj=attrMap.get(key);
					AttributeData attrData=(AttributeData)valueObj;
					//key=attrData.getVendorId()+":"+attrData.getVendorParameterId();
					displayString=attrData.getName()+ "[ " +attrData.getVendorId()+":"+attrData.getVendorParameterId() + " ] ";
					add(displayString);
				  accum.add(createSuggestion(key, displayString));
			}
			setDefaultSuggestions(accum);


		}catch(Exception e){
			Log.error("set suggestions in EliteMultiwordSuggest",e);
		}


	}
	
	
	
	

}
