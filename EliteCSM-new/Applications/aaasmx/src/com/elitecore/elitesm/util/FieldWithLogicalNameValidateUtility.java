package com.elitecore.elitesm.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
/**
 * Class to valid invalid logical name and also checks for duplicate logical name for REST API purpose
 * SM will handle it by GUI.Moreover Class validate if the combination of multiple allow Logical name and 
 * Field name (i.e Profile field,DB field) found.
 * 
 * @author Tejas.P.Shah
 *
 */
public class FieldWithLogicalNameValidateUtility {
	public static ResultObject checkFieldWithLogicalNameValidate(Map<String,String> validateMultipleAllowMap,Set<String> uniqueLogicalNameSet, List<String> uniqueLogicalNameList,List<String> logicalNameMultipleAllowList,
			StringBuilder duplicateLogicalNames,StringBuilder invalidLogicalNames, String logicalName, String field, ConstraintValidatorContext context){
		ResultObject resultObject = new ResultObject();
		if(Collectionz.isNullOrEmpty(uniqueLogicalNameList) == false){
			if(uniqueLogicalNameList.contains(logicalName) && Strings.isNullOrEmpty(logicalName) == false){
				boolean flag = uniqueLogicalNameSet.add(logicalName);
				if(flag == false){
					duplicateLogicalNames.append(logicalName.trim() + ", ");
				}
			}else if(logicalNameMultipleAllowList.contains(logicalName) == false && Strings.isNullOrEmpty(logicalName) == false){
				invalidLogicalNames.append(logicalName.trim() + ", ");
			}	
		}
		

		if(logicalNameMultipleAllowList.contains(logicalName)) {
			if(validateMultipleAllowMap.containsKey(logicalName) == false){
				validateMultipleAllowMap.put(logicalName, field);
			}else if(validateMultipleAllowMap.get(logicalName).equalsIgnoreCase(field)){
				resultObject.setValid(true);
				resultObject.setErrorMsg("Mapping with Logical Name "+logicalName+" and Profile Field "+field+" exists multiple times");
			}
		}
		return resultObject;
	}
}