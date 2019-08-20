package com.elitecore.coreradius.commons.util.dictionary;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.EgressVLANID;
import com.elitecore.coreradius.commons.attributes.threegpp.UserLocationInfoAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.OctetString;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.UnsignedInteger;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.UnsignedLong;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.UnsignedOctet;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.UnsignedShort;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.WiMAXIPAddressAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.WiMAXStringAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.WimaxGroupedAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.WimaxUnknownAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

/**
 * 
 * @author narendra.pathai
 *
 */
public class WimaxAttributeFactory {

	private static final String MODULE = "WIMAX-ATTRIB-FACTORY";

	/**
	 * Creates a new attribute corresponding to the attribute detail, returns an instance of
	 * {@link WimaxUnknownAttribute} if the type of attribute is unknown to the factory.
	 * For supported types refer {@link DictionaryAttributeTypeConstant}
	 * 
	 * @param attributeDetail a non-null attribute detail
	 * @return a new attribute corresponding to attribute detail, UnknownAttribute if the type
	 * of attribute is not known to factory.
	 */
	public BaseRadiusAttribute newWimaxAttribute(AttributeId attributeDetail){
		//wimax attributes for main attributes, standard attributes for sub attributes
		BaseRadiusAttribute bRAttribute = null;
		
		if (attributeDetail.getEncryptStandard() == Dictionary.RFC2868_ENCRYPT_STANDARD) {
			return new OctetString(attributeDetail);
		}
		
		switch (DictionaryAttributeTypeConstant.from(attributeDetail.getAttributeDataType())) {
		case STRING:
			bRAttribute = new WiMAXStringAttribute(attributeDetail);
			break;
		case LONG:
			bRAttribute = new UnsignedLong(attributeDetail);
			break;
		case INTEGER:
			bRAttribute = new UnsignedInteger(attributeDetail);
			break;
		case SHORT:
			bRAttribute = new UnsignedShort(attributeDetail);
			break;
		case BYTE:
			bRAttribute = new UnsignedOctet(attributeDetail);
			break;
		case OCTETS:
			bRAttribute = new OctetString(attributeDetail);
			break;
		case IPADDR:
			bRAttribute = new WiMAXIPAddressAttribute(attributeDetail);
			break;
		case GROUPED:
			bRAttribute = new WimaxGroupedAttribute(attributeDetail);
			break;
		case USER_LOCATION_INFO:
			bRAttribute = new UserLocationInfoAttribute(attributeDetail);
			break;
		case EVLANID:
			bRAttribute = new EgressVLANID(attributeDetail);
			break;
		case UNKNOWN:
		default:
			LogManager.getLogger().info(MODULE, "Attribute type: " + attributeDetail.getAttributeDataType()
					+ " is unknown for attribute: " + attributeDetail.getAttributeName() 
					+ "(" + attributeDetail.getAttrId() + ")" + ", will continue using Unknown type.");
			bRAttribute = new WimaxUnknownAttribute(attributeDetail);
			break;
		}
		return bRAttribute;
	}
}
