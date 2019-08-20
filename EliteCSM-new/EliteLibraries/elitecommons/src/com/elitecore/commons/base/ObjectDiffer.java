package com.elitecore.commons.base;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

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
@SuppressWarnings("unchecked")
public class ObjectDiffer {
	private static final String VALUES = "values";
	private static final String NEW_VALUE = "NewValue";
	private static final String OLD_VALUE = "OldValue";
	private static final String FIELD = "Field";
	
	public static <T extends Differentiable> JSONArray diff(T element1, T element2){
		JSONObject element1Json = element1.toJson();
		JSONObject element2Json = element2.toJson();
		return diff(element1Json, element2Json);
	}
	
	public static JSONArray diff(JSONObject element1Json, JSONObject element2Json){
		JSONObject ans = new JSONObject().accumulate(FIELD, "ans");
		if(diff(element1Json, element2Json, ans)){
			JSONArray values = ans.getJSONArray(VALUES);
			removeUnwantedElements(values);
			return (JSONArray) ans.get(VALUES);
		}
		return new JSONArray();
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
			JSONObject jsonObject = (JSONObject) object;
			if(jsonObject.containsKey(VALUES)){
				if(removeUnwantedElements(jsonObject.get(VALUES))){
					return true;
				}
			}else {
				Object oldValue = jsonObject.get(OLD_VALUE);
				Object newValue = jsonObject.get(NEW_VALUE);
				if( (oldValue == null || oldValue == "" || oldValue == "-") &&
					(newValue == null || newValue == "" || newValue == "-")){
					return true;
				}
			}
		} else if(isJsonArray(object)){
			JSONArray array = (JSONArray) object;
			ArrayList<Object> elementsToRemove = new ArrayList<Object>();
			for (Object element : array) {
				if(removeUnwantedElements(element)){
					elementsToRemove.add(element);
				}
			}
			for (Object element : elementsToRemove) {
				array.remove(element);
			}
			if(array.isEmpty()){
				return true;
			}
		}
		return false;
	}

	private static boolean diff(JSONObject element1, JSONObject element2, JSONObject resultObject) {
		if(element1.equals(element2)){
			return false;
		}
		Set<String> keySet = new HashSet<String>(element1.keySet());
		keySet.addAll(element2.keySet());
		
		JSONArray resultArray = new JSONArray();
		
		for (String keyString : keySet) {
			Object object1 = element1.get(keyString);
			Object object2 = element2.get(keyString);
			if(object1 == null){
				if(isJsonObject(object2)){
					addAsNew((JSONObject)object2, resultArray, keyString);
				}else if(isJsonArray(object2)){
					JSONObject jsonObject = new JSONObject().accumulate(keyString, JSONArray.fromObject(object2.toString()));
					addAsNew(jsonObject, resultArray, keyString);
				}else{
					resultArray.add(addValue(keyString, "-", element2.getString(keyString)));
				}
			}else if(object2 == null){
				if(isJsonObject(object1)){
					addAsOld((JSONObject)object1, resultArray, keyString);
				}else if(isJsonArray(object1)){
					JSONObject jsonObject = new JSONObject().accumulate(keyString, JSONArray.fromObject(object1.toString()));
					addAsOld(jsonObject, resultArray, keyString);
				}else{
					resultArray.add(addValue(keyString, element1.getString(keyString), "-"));
				}
			}else if(diff(object1, object2, resultArray, keyString)){
				resultArray.add(addValue(keyString, element1.getString(keyString), element2.getString(keyString)));
			}
		}
		resultObject = resultObject.accumulate(NEW_VALUE, " ").accumulate(OLD_VALUE, " ").accumulate(VALUES, resultArray);
		return true;
	}

	private static boolean diff(JSONArray element1, JSONArray element2, JSONArray resultArray, String fieldString) {
		if(element1.equals(element2)){
			return false;
		}
		JSONArray element1MinusElement2 = new JSONArray();
		element1MinusElement2.addAll(element1);
		element1MinusElement2.removeAll(element2);
		JSONArray element2MinusElement1 = new JSONArray();
		element2MinusElement1.addAll(element2);
		element2MinusElement1.removeAll(element1);

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
				addAsOld((JSONObject)oldO, resultArray, fieldString + "[" + (++i) + "]");
			}else {
				resultArray.add(addValue(fieldString + "[" + (++i) + "]", oldO.toString(), "-"));
			}
		}
		while (element2MinusElement1Iterator.hasNext()) {
			Object newO = element2MinusElement1Iterator.next();
			if(isJsonObject(newO)){
				addAsNew((JSONObject)newO, resultArray, fieldString + "[" + (++i) + "]");
			}else {
				resultArray.add(addValue(fieldString + "[" + (++i) + "]", "-", newO.toString()));
			}
		}
		return true;
	}

	private static void addAsNew(JSONObject object, JSONArray array, String fieldString) {
		JSONObject jsonObject = new JSONObject().accumulate(FIELD, fieldString);
		JSONArray jsonArray = new JSONArray();
		for (Object key : object.keySet()) {
			if(isJsonObject(object.get(key))){
				addAsNew((JSONObject)object.get(key), jsonArray, key.toString());
			}else if(isJsonArray(object.get(key))) {
				Iterator<?> jsonArrayIterator = ((JSONArray)object.get(key)).iterator();
				int i = 0;
				while (jsonArrayIterator.hasNext()) {
					Object innerObject = jsonArrayIterator.next();
					if(isJsonObject(innerObject)){
						addAsNew((JSONObject)innerObject, jsonArray, key + "[" + (++i) + "]");
					}else{
						jsonArray.add(addValue(key.toString() + "[" + (++i) + "]", "-", innerObject.toString()));
					}
				}
			}else {
				jsonArray.add(addValue(key.toString(), "-", object.getString(key.toString())));
			}
		}
		array.add(jsonObject.accumulate(NEW_VALUE, " ").accumulate(OLD_VALUE, " ").accumulate(VALUES, jsonArray));
	}

	private static void addAsOld(JSONObject object, JSONArray array, String string) {
		JSONObject jsonObject = new JSONObject().accumulate(FIELD, string);
		JSONArray jsonArray = new JSONArray();
		for (Object key : object.keySet()) {
			if(isJsonObject(object.get(key))){
				addAsOld((JSONObject)object.get(key), jsonArray, key.toString());
			}else if(isJsonArray(object.get(key))) {
				Iterator<?> jsonArrayIterator = ((JSONArray)object.get(key)).iterator();
				int i = 0;
				while (jsonArrayIterator.hasNext()) {
					Object innerObject = jsonArrayIterator.next();
					if(isJsonObject(innerObject)){
						addAsOld((JSONObject)innerObject, jsonArray, key + "[" + (++i) + "]");
					}else{
						jsonArray.add(addValue(key.toString() + "[" + (++i) + "]", innerObject.toString(), "-"));
					}
				}
			}else {
				jsonArray.add(addValue(key.toString(), object.getString(key.toString()), "-"));
			}
		}
		array.add(jsonObject.accumulate(NEW_VALUE, " ").accumulate(OLD_VALUE, " ").accumulate(VALUES, jsonArray));
	}

	private static boolean diff(Object o1, Object o2, JSONArray array, String string){
		if(isJsonObject(o1) && isJsonObject(o2)){
			JSONObject temp = new JSONObject().accumulate(FIELD, string);
			if(diff((JSONObject)o1, (JSONObject)o2, temp)){
				array.add(temp);
			}
		}else if(isJsonArray(o1) && isJsonArray(o2)){
			diff((JSONArray)o1, (JSONArray)o2, array, string);
		}else{
			return !(o1.toString().equals(o2.toString()));
		}
		return false;
	}

	private static JSONObject addValue(String field, String oldV, String newV){
		return new JSONObject().
				accumulate(FIELD, field).
				accumulate(OLD_VALUE, oldV).
				accumulate(NEW_VALUE, newV);
	}
	
	private static boolean isJsonArray(Object object) {
		boolean valid = false;
		try {
			JSONArray.fromObject(object.toString());
			valid = true;
		}
		catch(JSONException ex) { 
			valid = false;
			ignoreTrace(ex);
		}
		return valid;
	}
	
	private static boolean isJsonObject(Object object) {
		boolean valid = false;
		try {
			JSONObject.fromObject(object.toString());
			valid = true;
		}
		catch(JSONException ex) { 
			valid = false;
			ignoreTrace(ex);
		}
		return valid;
	}
}