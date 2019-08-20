package com.elitecore.coreradius.commons.util.dictionary;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.AddressAttribute;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.ByteAttribute;
import com.elitecore.coreradius.commons.attributes.EUI64Attribute;
import com.elitecore.coreradius.commons.attributes.EUIAttribute;
import com.elitecore.coreradius.commons.attributes.EgressVLANID;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IPv6PrefixAttribute;
import com.elitecore.coreradius.commons.attributes.IntegerAttribute;
import com.elitecore.coreradius.commons.attributes.LongAttribute;
import com.elitecore.coreradius.commons.attributes.ShortAttribute;
import com.elitecore.coreradius.commons.attributes.StringAttribute;
import com.elitecore.coreradius.commons.attributes.TextAttribute;
import com.elitecore.coreradius.commons.attributes.TimeAttribute;
import com.elitecore.coreradius.commons.attributes.UnknownAttribute;
import com.elitecore.coreradius.commons.attributes.cisco.CiscoCommandCodeAttribute;
import com.elitecore.coreradius.commons.attributes.ericsson.GTPTunnelDataAttribute;
import com.elitecore.coreradius.commons.attributes.ericsson.GTPv1TunnelDataAttribute;
import com.elitecore.coreradius.commons.attributes.threegpp.TWANIdentifierAttribute;
import com.elitecore.coreradius.commons.attributes.threegpp.UserLocationInfoAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.PrepaidTLVAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

/**
 * 
 * @author narendra.pathai
 *
 */
public class StandardAttributeFactory {

	private static final String MODULE = "STANDARD-ATTRIB-FACTORY";

	/**
	 * Creates a new attribute corresponding to the attribute detail, returns an instance of
	 * {@link UnknownAttribute} if the type of attribute is unknown to the factory.
	 * For supported types refer {@link DictionaryAttributeTypeConstant}
	 * 
	 * @param attributeDetail a non-null attribute detail
	 * @return a new attribute corresponding to attribute detail, UnknownAttribute if the type
	 * of attribute is not known to factory.
	 */
	public BaseRadiusAttribute newStandardAttribute(AttributeId attributeDetail){
		//FIXME while reading the grouped attribute this check is skipped
		if (attributeDetail.getEncryptStandard() == Dictionary.RFC2868_ENCRYPT_STANDARD) {
			return new TextAttribute(attributeDetail);
		}

		BaseRadiusAttribute bRAttribute = null;
		switch (DictionaryAttributeTypeConstant.from(attributeDetail.getAttributeDataType())) {
		case STRING:
			bRAttribute = new StringAttribute(attributeDetail);	
			break;
		case LONG:
			bRAttribute = new LongAttribute(attributeDetail);
			break;
		case INTEGER:
			bRAttribute = new IntegerAttribute(attributeDetail);
			break;
		case SHORT:
			bRAttribute = new ShortAttribute(attributeDetail);
			break;
		case BYTE:
			bRAttribute = new ByteAttribute(attributeDetail);
			break;
		case OCTETS:
			bRAttribute = new TextAttribute(attributeDetail);
			break;
		case IPADDR:
			bRAttribute = new AddressAttribute(attributeDetail);
			break;
		case DATE:
			bRAttribute = new TimeAttribute(attributeDetail);
			break;
		case IPV6PREFIX:
			bRAttribute = new IPv6PrefixAttribute(attributeDetail);
			break;
		case EUI64:
			bRAttribute = new EUI64Attribute(attributeDetail);
			break;
		case EUI:
			bRAttribute = new EUIAttribute(attributeDetail);
			break;
		case GROUPED:
			bRAttribute = new GroupedAttribute(attributeDetail);
			break;
		case USER_LOCATION_INFO:
			bRAttribute = new UserLocationInfoAttribute(attributeDetail);
			break;
		case CISCO_COMMAND_CODE:
			bRAttribute = new CiscoCommandCodeAttribute(attributeDetail);
			break;
		case EVLANID:
			bRAttribute = new EgressVLANID(attributeDetail);
			break;
		case GTP_TUNNEL_DATA:
			bRAttribute = new GTPTunnelDataAttribute(attributeDetail);
			break;
		case GTPV1_TUNNEL_DATA:
			bRAttribute = new GTPv1TunnelDataAttribute(attributeDetail);
			break;
		case PREPAIDTLV:
			bRAttribute = new PrepaidTLVAttribute(attributeDetail);
			break;
		case TWAN_IDENTIFIER:
			bRAttribute = new TWANIdentifierAttribute(attributeDetail);
			break;
		case UNKNOWN:
		default:
			LogManager.getLogger().info(MODULE, "Attribute type: " + attributeDetail.getAttributeDataType()
					+ " is unknown for attribute: " + attributeDetail.getAttributeName() 
					+ "(" + attributeDetail.getAttrId() + ")" + ", will continue using Unknown type.");
			bRAttribute = new UnknownAttribute(attributeDetail);
			break;
		}

		return bRAttribute;
	}
}
