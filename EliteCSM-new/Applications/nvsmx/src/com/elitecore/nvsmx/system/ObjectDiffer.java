package com.elitecore.nvsmx.system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/**
 *  <p> <b> ObjectDiffer </b> is a simple, yet powerful utility to find differences between Java objects. 
 *  It takes two objects and generates a tree structure that represents any differences between the objects and their children.
 * 
 *  This tree can then be traversed to extract more information or apply changes to the underlying data structures.</p>
 * 
 *  <b>For Example :</b> 
 *  <code><pre>
 *   SomeClass a = new SomeClass();
 *   a.setProp1("A");
 *   a.setProp2("X");
 * 
 *   SomeClass b = new SomeClass();
 *   b.setProp1("B");
 *   b.setProp2("X");
 *   
 *   JSONArray jsonArray = ObjectDiffer.diff(a,b); 
 * 	</pre></code>
 * 
 * 	<b>Output    :</b> Its output will be JSON Array containing all the differences 
 * <code>
 * [{"Field":"Prop1","OldValue":"A","NewValue":"B"}]
 * </code>
 */
public class ObjectDiffer {
	private static final String VALUES = "values";
	private static final String NEW_VALUE = "NewValue";
	private static final String OLD_VALUE = "OldValue";
	private static final String FIELD = "Field";
	
	public static <T extends ResourceData> JsonArray diff(T oldElement1, T newElement2){
		JsonObject oldJson = oldElement1.toJson(); 
		JsonObject newJson = newElement2.toJson();
		return diff(oldJson, newJson);
	}
	
	public static JsonArray diff(JsonObject element1Json, JsonObject element2Json){
		JsonObject ans = new JsonObject();
		ans.addProperty(FIELD, "ans");
		if(diff(element1Json, element2Json, ans)){
			JsonArray values = ans.getAsJsonArray(VALUES);
			removeUnwantedElements(values);
			return (JsonArray) ans.get(VALUES);
		}
		return new JsonArray();
	}

	/**
	 * removes the json elements with empty value.
	 * added due to specific requirement for SM ( Discussed with Devang Adeshara)
	 * 
	 * @param object
	 * @return boolean true value indicates the passed object in the method is empty 
	 */
	private static boolean removeUnwantedElements(Object object) {
		if(isJsonObject(object)){
			JsonObject jsonObject = (JsonObject) object;
			if(jsonObject.has(VALUES)){
				if(removeUnwantedElements(jsonObject.get(VALUES))){
					return true;
				}
			}else {
				JsonElement oldValue = jsonObject.get(OLD_VALUE);
				JsonElement newValue = jsonObject.get(NEW_VALUE);
				
				if((oldValue == null || oldValue.getAsString().equals("") || oldValue.getAsString().equals("-")) && 
						(newValue == null || newValue.getAsString().equals("") || newValue.getAsString().equals("-"))){
					return true;
				}
			}
		} else if(isJsonArray(object)){
			JsonArray array = (JsonArray) object;
			ArrayList<Object> elementsToRemove = new ArrayList<Object>();
			for (Object element : array) {
				if(removeUnwantedElements(element)){
					elementsToRemove.add(element);
				}
			}
			for (Object element : elementsToRemove) {
				array.remove((JsonElement) element);
			}
			if(array.size()==0){
				return true;
			}
		}
		return false;
	}

	private static boolean diff(JsonObject element1, JsonObject element2, JsonObject resultObject) {
		if(element1.equals(element2)){
			return false;
		}
		
		Set<String> keySets = getKeySet(element1);
		keySets.addAll(getKeySet(element2));
		
		JsonArray resultArray = new JsonArray();
		
    	for (String key : keySets){
		           
		           JsonElement object1 = element1.get(key);
		           JsonElement object2 = element2.get(key);
		           
		           if((object1==null || object1.isJsonNull())
		        		   && (object2==null || object2.isJsonNull())){
		        	   continue;
		           }else if(object1 == null || object1.isJsonNull()){
		        	  if(isJsonObject(object2)){
		        	    addAsNew(object2.getAsJsonObject(), resultArray, key);
		        	  }else if(isJsonArray(object2)){
		        		  JsonObject jsonObject = new JsonObject();
		        		  jsonObject.add(key, object2);
		        		  addAsNew(jsonObject, resultArray, key); 
		        	  }else{
		        		  if(object2 != null && object2.isJsonNull()==false){
		        		    resultArray.add(addValue(key, "-", object2.getAsString()));
		        		  }
		              }
		        	 }else if (object2 == null || object2.isJsonNull()){
		        		 if(isJsonObject(object1)){
		        			    addAsOld(object1.getAsJsonObject(), resultArray, key);
				        	  }else if(isJsonArray(object1)){
				        		  JsonObject jsonObject = new JsonObject();
				        		  jsonObject.add(key, object1.getAsJsonArray());
				        		 	addAsOld(jsonObject, resultArray, key);
				        	  }else{
				        		  resultArray.add(addValue(key, object1.getAsString(), "-"));
				              }
		         }else if(diff(object1, object2, resultArray, key)){
						resultArray.add(addValue(key, object1.getAsString(), object2.getAsString()));
				 }
    	    
		}
         resultObject.addProperty(NEW_VALUE, "");
         resultObject.addProperty(OLD_VALUE, "");
         resultObject.add(VALUES, resultArray);
		 return true;
}

	private static Set<String> getKeySet(JsonObject obj){
		 Set<String> keys = Collectionz.newLinkedHashSet();
	        for (Entry<String, JsonElement> e : obj.entrySet()) {
	            keys.add(e.getKey());
	        }
		return keys;
		
	}
	
	private static boolean diff(JsonArray element1, JsonArray element2, JsonArray resultArray, String fieldString) {
		if(element1.equals(element2)){
			return false;
		}
		JsonArray element1MinusElement2 = new JsonArray();
		element1MinusElement2.addAll(element1);
		element1MinusElement2.remove(element2);
		JsonArray element2MinusElement1 = new JsonArray();
		element2MinusElement1.addAll(element2);
		element2MinusElement1.remove(element1);

		Iterator<?> element1MinusElement2Iterator = element1MinusElement2.iterator();
		Iterator<?> element2MinusElement1Iterator = element2MinusElement1.iterator();
		int i = 0;
		while(element1MinusElement2Iterator.hasNext() && element2MinusElement1Iterator.hasNext()){
			Object object1 = element1MinusElement2Iterator.next();
			Object object2 = element2MinusElement1Iterator.next();
			if(diff(object1, object2, resultArray, fieldString + "[" + (++i) + "]")){
				resultArray.add(addValue(fieldString + "[" + i + "]", object1.toString(), object2.toString()));
			}
		}
		while (element1MinusElement2Iterator.hasNext()) {
			Object oldO = element1MinusElement2Iterator.next();
			if(isJsonObject(oldO)){
				addAsOld((JsonObject)oldO, resultArray, fieldString + "[" + (++i) + "]");
			}else {
				resultArray.add(addValue(fieldString + "[" + (++i) + "]", oldO.toString(), "-"));
			}
		}
		while (element2MinusElement1Iterator.hasNext()) {
			Object newO = element2MinusElement1Iterator.next();
			if(isJsonObject(newO)){
				addAsNew((JsonObject)newO, resultArray, fieldString + "[" + (++i) + "]");
			}else {
				resultArray.add(addValue(fieldString + "[" + (++i) + "]", "-", newO.toString()));
			}
		}
		return true;
	}

	private static void addAsNew(JsonObject object, JsonArray array, String fieldString) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FIELD, fieldString);
		
		JsonArray jsonArray = new JsonArray();
		for (Entry<String, JsonElement> entry : object.entrySet()) {
			if(entry.getValue().isJsonObject()){
				addAsNew(entry.getValue().getAsJsonObject(), jsonArray, entry.getKey());
			}else if(entry.getValue().isJsonArray()) {
				Iterator<?> jsonArrayIterator = entry.getValue().getAsJsonArray().iterator();
				int i = 0;
				while (jsonArrayIterator.hasNext()) {
					JsonElement innerObject = (JsonElement) jsonArrayIterator.next();
					if(innerObject.isJsonObject()){
						addAsNew(innerObject.getAsJsonObject(), jsonArray, entry.getKey() + "[" + (++i) + "]");
					}else{
						jsonArray.add(addValue(entry.getKey() + "[" + (++i) + "]", "-", innerObject.toString()));
					}
				}
			}else {
				if(object.get(entry.getKey()).isJsonNull() == false){
					jsonArray.add(addValue(entry.getKey(), "-", object.get(entry.getKey()).getAsString()));
				}
			}
		}
		     jsonObject.addProperty(NEW_VALUE, "");
	         jsonObject.addProperty(OLD_VALUE, "");
	         jsonObject.add(VALUES, jsonArray);
	         array.add(jsonObject);
	}
	         
	private static void addAsOld(JsonObject object, JsonArray array, String string) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FIELD, string);
		
		JsonArray jsonArray = new JsonArray();
		
		
		
		for (Entry<String, JsonElement> entry : object.entrySet()) {
			if(entry.getValue().isJsonObject()){
				addAsOld(entry.getValue().getAsJsonObject(), jsonArray, entry.getKey());
			}else if(entry.getValue().isJsonArray()) {
				    Iterator<JsonElement> jsonArrayiterator = entry.getValue().getAsJsonArray().iterator();
			     	int i = 0;
				   while (jsonArrayiterator.hasNext()) {
					JsonElement innerObject = jsonArrayiterator.next();
					if(innerObject.isJsonObject()){
						addAsOld(innerObject.getAsJsonObject(), jsonArray, entry.getKey() + "[" + (++i) + "]");
					}else{
						jsonArray.add(addValue(entry.getKey() + "[" + (++i) + "]", innerObject.toString(), "-"));
					}
				}
			}else {
				if(entry.getValue().isJsonNull()==false){
					jsonArray.add(addValue(entry.getKey(), entry.getValue().getAsString(), "-"));	
				}
			}
		}
		jsonObject.addProperty(NEW_VALUE, "");
		jsonObject.addProperty(OLD_VALUE, "");
		jsonObject.add(VALUES, jsonArray);
		array.add(jsonObject);
	}

	private static boolean diff(Object o1, Object o2, JsonArray array, String string){
		if(isJsonObject(o1) && isJsonObject(o2)){
			JsonObject temp = new JsonObject();
			temp.addProperty(FIELD, string);
			if(diff((JsonObject)o1, (JsonObject)o2, temp)){
				array.add(temp);
			}
		}else if(isJsonArray(o1) && isJsonArray(o2)){
			diff((JsonArray)o1, (JsonArray)o2, array, string);
		}else{
			return !(o1.toString().equals(o2.toString()));
		}
		return false;
	}

	private static JsonObject addValue(String field, String oldV, String newV){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FIELD, field);
		jsonObject.addProperty(OLD_VALUE,oldV);
		jsonObject.addProperty(NEW_VALUE, newV);
		return jsonObject;
	}
	
	private static boolean isJsonArray(Object object) {
		String value = object.toString();
		return value.startsWith("[") && value.endsWith("]");
	}
	
	private static boolean isJsonObject(Object object) {
		String value = object.toString();
		return value.startsWith("{") && value.endsWith("}");
	}
	
}