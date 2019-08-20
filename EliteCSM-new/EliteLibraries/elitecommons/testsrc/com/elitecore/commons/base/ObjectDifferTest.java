package com.elitecore.commons.base;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * 
 * @author nayana.rathod
 * 
 */
@RunWith(JUnitParamsRunner.class)
public class ObjectDifferTest {

	@Test
	@Parameters(method = "dataFor_testDiff_WithJSONObject_ShouldShowDeletedValueDifferences_WhenSomeValuesAreDeletedInNewObject")
	public void testDiff_WithJSONObject_ShouldShowDeletedValueDifferences_WhenSomeValuesAreDeletedInNewObject(JSONObject oldObject, JSONObject newObject, JSONObject expectedDifference) {
		JSONArray expectedResult = new JSONArray();
		expectedResult.add(expectedDifference);

		assertEquals(expectedResult,
				ObjectDiffer.diff(oldObject, newObject));
	}
	
	public Object[] dataFor_testDiff_WithJSONObject_ShouldShowDeletedValueDifferences_WhenSomeValuesAreDeletedInNewObject() {
		return $(
				$(new JSONObject().accumulate("Datasource", "abc"), new JSONObject(), 
						new JSONObject().accumulate("Field", "Datasource")
						.accumulate("OldValue", "abc")
						.accumulate("NewValue", "-"))
		);
	}
}
