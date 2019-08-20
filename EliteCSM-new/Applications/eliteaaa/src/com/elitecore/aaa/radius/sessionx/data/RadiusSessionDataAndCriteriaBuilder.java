package com.elitecore.aaa.radius.sessionx.data;

import java.util.List;

import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;


/**
 * This builder does the task of filling the session data with the values from request and 
 * response packet.
 * It takes Field Mapping parser and uses it to find the mappings from column name to properties and
 * hence fetch the values based on MUI concept.
 * It also creates the Radius packet to be formed at the time of Auto Session closure.
 * It also creates the search criteria based on which the session is to be created
 * <br>
 * 
 * @author narendra.pathai
 * <br>
 * 
 *
 */
public class RadiusSessionDataAndCriteriaBuilder {
	private FieldMappingParser parser;
	private List<FieldMapping> sessionSearchAttributesList;

	public RadiusSessionDataAndCriteriaBuilder(FieldMappingParser parser,List<FieldMapping> sessionSearchAttributesList){
		this.parser = parser;
		this.sessionSearchAttributesList = sessionSearchAttributesList;
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
	public void setSessionDataForInsert(SessionData sessionData,RadServiceRequest request, RadServiceResponse response, int requestType){
		//Step 1
		List<FieldMapping> fieldMappingList = this.parser.getFieldMappings();
		if(fieldMappingList != null){
			for(int i=0;i<fieldMappingList.size();i++){
				String propertyNames = fieldMappingList.get(i).getPropertyName();
				IRadiusAttribute radAttribute;
				//Step 2
				List<PropertyType> properties = parser.getPropertyListByProperties(propertyNames);
				if(properties != null){
					boolean useDefault = true;
					//Step 3
					for(PropertyType property : properties){
						if(property.getPacketType() == PropertyType.ACCESS_REQ){
							radAttribute = request.getRadiusAttribute(property.getPropertyName(),true);
						}else{
							radAttribute = response.getRadiusAttribute(true,property.getPropertyName());
						}
						if(radAttribute != null){
							useDefault = false;
							sessionData.addValue(propertyNames, valueOf(radAttribute, request, response, requestType));
							break;
						}
					}
					if(useDefault){
						String defaultValue = fieldMappingList.get(i).getDefaultValue();
						sessionData.addValue(propertyNames, defaultValue);
					}
				}
			}	
		}
	}

	private String valueOf(IRadiusAttribute radAttribute, RadServiceRequest request, RadServiceResponse response, int requestType) {
		RadClientData clientData = response.getClientData();
		if (radAttribute.getEncryptStandard() > 0) {
			return radAttribute.getStringValue(clientData.getSharedSecret(requestType), request.getAuthenticator());
		}
		return radAttribute.getStringValue(false);
	}

	/*
	 * Same as setSessionDataForInsert the only difference is that this method does not use the default
	 * value of the field mapping and skips the mapping if the attributes are not found 
	 */
	public void setSessionDataForUpdate(SessionData sessionData,RadServiceRequest request, RadServiceResponse response, int requestType){
		//Step 1
		List<FieldMapping> fieldMappingList = this.parser.getFieldMappings();
		if(fieldMappingList != null){
			for(int i=0;i<fieldMappingList.size();i++){
				String propertyNames = fieldMappingList.get(i).getPropertyName();
				IRadiusAttribute radAttribute;
				//Step 2
				List<PropertyType> properties = parser.getPropertyListByProperties(propertyNames);
				if(properties != null){
					//Step 3
					for(PropertyType property : properties){
						if(property.getPacketType() == PropertyType.ACCESS_REQ){
							radAttribute = request.getRadiusAttribute(property.getPropertyName(),true);
						}else{
							radAttribute = response.getRadiusAttribute(true,property.getPropertyName());
						}
						if(radAttribute != null){
							sessionData.addValue(propertyNames,valueOf(radAttribute, request, response, requestType));
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
	public void prepareRadiusPacket(RadiusPacket radiusPacket,SessionData sessionData){		
		//Step 1
		List<FieldMapping> fieldMappingList = parser.getFieldMappings();
		for(int i=0;i<fieldMappingList.size();i++){
			String propertyNames = fieldMappingList.get(i).getPropertyName();
			
			String valueForAttribute = sessionData.getValue(propertyNames);
			if(valueForAttribute == null || valueForAttribute.trim().length() == 0){
				//the value from the database is blank or not present so skipping that mapping
				continue;
			}
			
			IRadiusAttribute radAttribute;
			//Step 2
			List<PropertyType> properties = parser.getPropertyListByProperties(propertyNames);
			for(PropertyType property : properties){
				//Step 3
				try {
					radAttribute = radiusPacket.getRadiusAttribute(property.getPropertyName(),true);
					if(radAttribute != null){
						radAttribute.setStringValue(valueForAttribute);
					}else{
						radAttribute = Dictionary.getInstance().getAttribute(property.getPropertyName());
						radAttribute.setStringValue(valueForAttribute);
						radiusPacket.addAttribute(radAttribute);
					}

				} catch (InvalidAttributeIdException e) {
					//getLogger().debug(MODULE, this.smInstanceName + "- Invalid attribute :" + fieldMapping.getPropertyName());
					//getLogger().debug(MODULE, ConcurrencySessionManager.this.smInstanceName + "- Invalid attribute:" + property.getPropertyName());
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
	public void fillSessionSearchCriteria(Criteria criteria, RadServiceRequest request, RadServiceResponse response) throws ImproperSearchCriteriaException {
		if(criteria != null && sessionSearchAttributesList != null){
			for(int i=0;i<sessionSearchAttributesList.size();i++){
				//Step 1
				FieldMapping fieldMapping = this.sessionSearchAttributesList.get(i);
				List<PropertyType> property = parser.getPropertyListByProperties(fieldMapping.getPropertyName());
				if(property != null){
					boolean criteriaAddded = false;
					for(PropertyType prop : property){
						//Step 2
						if(prop != null && prop.getPacketType() == PropertyType.ACCESS_REQ){
							IRadiusAttribute searchAttr = request.getRadiusAttribute(prop.getPropertyName(), true);
							//as soon as the first search attribute is found in Request in case of MUI only that will be considered
							String value = null;
							if(searchAttr != null && (value = searchAttr.getStringValue(false)) != null && value.trim().isEmpty() == false){
								criteria.add(Restrictions.eq(sessionSearchAttributesList.get(i).getColumnName(), value));
								criteriaAddded = true;
								break;
							}
						}
					}
					if (criteriaAddded == false) {
						throw new ImproperSearchCriteriaException("Attribute(s) " + fieldMapping.getPropertyName() 
								+ " bound to coloumn " + fieldMapping.getColumnName() + " is/are either unavailable in request or empty");
				}
			}
		}
	}
	}

	public String getValueBasedOnFieldMapping(FieldMapping fieldMapping, RadServiceRequest radServiceRequest, RadServiceResponse radServiceResponse){
		String valueUsingFieldMapping = null;

		List<PropertyType> properties = parser.getPropertyListByProperties(fieldMapping.getPropertyName());
		if(properties != null){
			IRadiusAttribute radAttribute;
			boolean useDefault = true;
			for(PropertyType property : properties){
				if(property.getPacketType() == PropertyType.ACCESS_REQ){
					radAttribute = radServiceRequest.getRadiusAttribute(property.getPropertyName(),true);
				}else{
					radAttribute = radServiceResponse.getRadiusAttribute(true,property.getPropertyName());
				}
				if(radAttribute != null){
					useDefault = false;
					valueUsingFieldMapping = radAttribute.getStringValue(false);
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
