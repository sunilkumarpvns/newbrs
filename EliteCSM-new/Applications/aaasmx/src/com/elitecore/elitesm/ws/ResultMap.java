package com.elitecore.elitesm.ws;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

public class ResultMap {
	Map<String,Map<String,String>> resultMap;
	public ResultMap(Map<String,Map<String,String>> resultMap){
		this.resultMap=resultMap;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
	
		if(resultMap!=null && !resultMap.isEmpty()) {
			

			Iterator<String> iterator = resultMap.keySet().iterator();
			
			while(iterator.hasNext()){
				String key = iterator.next();
				writer.print("###");
				Map<String,String> recordMap = resultMap.get(key);
				
				writer.print(key+",");
				
				if(!recordMap.isEmpty()){
					Iterator<String> recordIterator =recordMap.keySet().iterator();
					while(recordIterator.hasNext()){
						String recordKey = recordIterator.next();
						String recordValue = recordMap.get(recordKey);
						writer.print(recordKey+"="+recordValue);
						if(recordIterator.hasNext()){
							writer.print(",");
						}
					}
					
				}
				

			}
			
		}else{
			writer.print("###0,NO-RESULT");
		}
		writer.println();
		writer.close();
		return out.toString();
	}
}
