package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.List;
import java.util.Set;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.radius.sessionx.data.FieldMappingParser;
import com.elitecore.aaa.radius.sessionx.data.PropertyType;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;


/**
 * This builder does the task of filling the session data with the values from request and 
 * response packet.
 * It takes Field Mapping parser and uses it to find the mappings from column name to properties and
 * hence fetch the values based on MUI concept.
 * It also creates the Diameter packet to be formed at the time of Session Override.
 * It also creates the search criteria based on which the session is to be created
 * 
 * @author malav.desai
 */
public class DiameterSessionDataAndCriteriaBuilder {
	private FieldMappingParser parser;
	private List<String> sessionSearchAttributesList;
	private Set<String> includeInASRMap;

	public DiameterSessionDataAndCriteriaBuilder(FieldMappingParser parser,List<String> sessionSearchAttributesList, 
			Set<String> includeInASRMap) {
		this.parser = parser;
		this.sessionSearchAttributesList = sessionSearchAttributesList;
		this.includeInASRMap = includeInASRMap;
	}


	/*
	 * ------Step 1------
	 * This method first fetches the fieldMapping list from FieldMapping parser which contains
	 * Mappings from column name to properties which are comma separated values given in SM Configuration
	 * PARAM_STR0 -----> $REQ(0:1),$RES(0:4),0:31
	 * 
	 * ------Step 2------
	 * Then it one by one iterates over the fieldMappings received and asks the parser for mapping from
	 * Properties to list of property which contains info about the packet type to refer and attribute id 
	 * to refer to. See class PropertyType
	 * 
	 * ------Step 3------
	 * Then on fetching the list of property it gets each property and tries to fetch the attribute from
	 * request or response. It follows MUI concept. So if one attribute is found then the attribute after it
	 * is not checked. The value of attribute is added to session data to be dumped in DB.
	 * 
	 */
	public void setSessionDataForInsert(SessionData sessionData,ApplicationRequest request, ApplicationResponse response) {
		//Step 1
		List<FieldMapping> fieldMappingList = this.parser.getFieldMappings();
		if (fieldMappingList != null) {
			for (int i=0; i < fieldMappingList.size(); i++) {
				String propertyNames = fieldMappingList.get(i).getPropertyName();
				IDiameterAVP diameterAVP;
				//Step 2
				List<PropertyType> properties = parser.getPropertyListByProperties(propertyNames);
				if (properties != null) {
					boolean useDefault = true;
					//Step 3
					for (PropertyType property : properties) {
						if (property.getPacketType() == PropertyType.ACCESS_REQ) {
							diameterAVP = request.getAVP(property.getPropertyName(), true);
						} else {
							diameterAVP = response.getAVP(property.getPropertyName(), true);
						}
						if (diameterAVP != null) {
							useDefault = false;
							sessionData.addValue(propertyNames, diameterAVP.getStringValue(false));
							break;
						}
					}
					if (useDefault) {
						String defaultValue = fieldMappingList.get(i).getDefaultValue();
						sessionData.addValue(propertyNames, defaultValue);
					}
				}
			}	
		}
	}

	/*
	 * Same as setSessionDataForInsert the only difference is that this method does not use the default
	 * value of the field mapping and skips the mapping if the attributes are not found 
	 */
	public void setSessionDataForUpdate(SessionData sessionData,ApplicationRequest request, ApplicationResponse response) {
		//Step 1
		List<FieldMapping> fieldMappingList = this.parser.getFieldMappings();
		if (fieldMappingList != null) {
			for (int i=0; i < fieldMappingList.size(); i++) {
				String propertyNames = fieldMappingList.get(i).getPropertyName();
				IDiameterAVP diameterAVP;
				//Step 2
				List<PropertyType> properties = parser.getPropertyListByProperties(propertyNames);
				if (properties != null) {
					//Step 3
					for (PropertyType property : properties) {
						if (property.getPacketType() == PropertyType.ACCESS_REQ) {
							diameterAVP = request.getAVP(property.getPropertyName(), true);
						} else {
							diameterAVP = response.getAVP(property.getPropertyName(), true);
						}
						if (diameterAVP != null) {
							sessionData.addValue(propertyNames, diameterAVP.getStringValue(false));
							break;
						}
					}
				}
			}	
		}
	}

	/*
	 * ----Step 1-----
	 * This method first fetches the fieldMapping list from FieldMapping parser which contains
	 * Mappings from column name to properties which are comma separated values given in SM Configuration
	 * PARAM_STR0 -----> $REQ(0:1),$RES(0:4),0:31
	 * 
	 * ----Step 2-----
	 * Then it one by one iterates over the fieldMappings received and asks the parser for mapping from
	 * Properties to list of property which contains info about the packet type to refer and attribute id 
	 * to refer to. See class PropertyType
	 * 
	 * ----Step 3-----
	 * Now it iterates over each property type and adds the values fetched from the session to each attribute.
	 * If the attribute is already present then the value is updated and the attribute is not
	 * added again 
	 * 
	 */
	public void prepareDiameterPacket(DiameterPacket packet,SessionData sessionData) {		
		//Step 1
		List<FieldMapping> fieldMappingList = parser.getFieldMappings();
		
		for (FieldMapping diameterFieldMapping : fieldMappingList) {
			if (includeInASRMap.contains(diameterFieldMapping.getPropertyName())) {

				String propertyNames = diameterFieldMapping.getPropertyName();

				String valueForAttribute = sessionData.getValue(propertyNames);
				if (Strings.isNullOrBlank(valueForAttribute)) {
					valueForAttribute = diameterFieldMapping.getDefaultValue();
					if (Strings.isNullOrBlank(valueForAttribute)) {
						//the default value and the database value is blank or not present so skipping that mapping
						continue;
					}
				}

				//Step 2
				List<PropertyType> properties = parser.getPropertyListByProperties(propertyNames);
				for(PropertyType property : properties){
					//Step 3
					IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(property.getPropertyName());
					if (diameterAVP != null) {
						diameterAVP.setStringValue(valueForAttribute);
						packet.addAvp(diameterAVP);
					}
				}
			}
		}
	}

	/*
	 * ----Step 1-----
	 * Then it one by one iterates over the Session search criteria received and asks the parser for mapping from
	 * Properties to list of property, which contains info about the packet type to refer and attribute id 
	 * to refer to. See class PropertyType
	 * 
	 * ----Step 2-----
	 * Now if the property contains a attribute to be fetched from request the value of 
	 * that attribute is fetched and the value is added to the session search criteria
	 */
	public void fillSessionSearchCriteria(Criteria criteria, ApplicationRequest request, ApplicationResponse response){
		if (criteria != null && sessionSearchAttributesList != null) {
			for (int i=0; i<sessionSearchAttributesList.size(); i++) {
				//Step 1
				List<PropertyType> property = parser.getPropertyListByProperties(this.sessionSearchAttributesList.get(i));
				if (property != null) {
					for (PropertyType prop : property) {
						//Step 2
						if (prop != null && prop.getPacketType() == PropertyType.ACCESS_REQ) {
							IDiameterAVP searchAttr = request.getAVP(prop.getPropertyName(), true);
							//as soon as the first search attribute is found in Request in case of MUI only that will be considered
							if (searchAttr != null) { 
								criteria.add(Restrictions.eq(sessionSearchAttributesList.get(i), searchAttr.getStringValue(false)));
								break;
							}
						}
					}
				}
			}
		}
	}

	public String getValueBasedOnFieldMapping(FieldMapping fieldMapping, ApplicationRequest request, ApplicationResponse response){
		String valueUsingFieldMapping = null;

		List<PropertyType> properties = parser.getPropertyListByProperties(fieldMapping.getPropertyName());
		if(properties != null){
			IDiameterAVP diameterAVP;
			boolean useDefault = true;
			for(PropertyType property : properties){
				if(property.getPacketType() == PropertyType.ACCESS_REQ){
					diameterAVP = request.getAVP(property.getPropertyName(), true);
				}else{
					diameterAVP = response.getAVP(property.getPropertyName(), true);
				}
				if(diameterAVP != null){
					useDefault = false;
					valueUsingFieldMapping = diameterAVP.getStringValue(false);
					break;
				}
			}
			if(useDefault){
				valueUsingFieldMapping = fieldMapping.getDefaultValue();
			}
		}
		return valueUsingFieldMapping;
	}
}