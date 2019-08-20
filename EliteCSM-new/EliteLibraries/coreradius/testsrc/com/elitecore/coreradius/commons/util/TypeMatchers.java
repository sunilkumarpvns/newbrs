package com.elitecore.coreradius.commons.util;

import com.elitecore.coreradius.commons.attributes.*;
import com.elitecore.coreradius.commons.attributes.cisco.CiscoCommandCodeAttribute;
import com.elitecore.coreradius.commons.attributes.ericsson.GTPTunnelDataAttribute;
import com.elitecore.coreradius.commons.attributes.ericsson.GTPv1TunnelDataAttribute;
import com.elitecore.coreradius.commons.attributes.threegpp.TWANIdentifierAttribute;
import com.elitecore.coreradius.commons.attributes.threegpp.UserLocationInfoAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.*;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class TypeMatchers {

    public static class AttributeOfType extends TypeSafeDiagnosingMatcher<IRadiusAttribute> {


        private DictionaryAttributeTypeConstant type;
        private long vendorId;

        public AttributeOfType(DictionaryAttributeTypeConstant type, long vendorId) {

            this.type = type;
            this.vendorId = vendorId;
        }

        @Override
        public void describeTo(Description arg0) {
            arg0.appendText("attribute of type ").appendText(type.name());
        }

        @Override
        protected boolean matchesSafely(IRadiusAttribute arg0, Description arg1) {

            if(RadiusAttributeConstants.VENDOR_SPECIFIC != vendorId) {
                switch (type) {
                    case GROUPED:
                        if ((arg0 instanceof GroupedAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxGroupedAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute(" + arg0.getID() + ") is not group type");
                            return false;
                        }
                    case INTEGER:
                        if ((arg0 instanceof IntegerAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof UnsignedInteger && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not IntegerAttribute Avp");
                            return false;
                        }
                    case STRING:
                        if ((arg0 instanceof StringAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WiMAXStringAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not StringAttribute type");
                            return false;
                        }
                    case UNKNOWN:
                        if ((arg0 instanceof UnknownAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not UnknownAttribute Avp");
                            return false;
                        }
                    case BYTE:
                        if ((arg0 instanceof ByteAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof UnsignedOctet && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not ByteAttribute Avp");
                            return false;
                        }
                    case LONG:
                        if ((arg0 instanceof LongAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof UnsignedLong && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not LongAttribute Avp");
                            return false;
                        }
                    case SHORT:
                        if ((arg0 instanceof ShortAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof UnsignedShort && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not ShortAttribute Avp");
                            return false;
                        }
                    case IPADDR:
                        if ((arg0 instanceof AddressAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WiMAXIPAddressAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not AddressAttribute Avp");
                            return false;
                        }
                    case OCTETS:
                        if ((arg0 instanceof TextAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof OctetString && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not TextAttribute Avp");
                            return false;
                        }
                    case EVLANID:
                        if ((arg0 instanceof EgressVLANID && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof EgressVLANID && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not EgressVLANID Avp");
                            return false;
                        }
                    case USER_LOCATION_INFO:
                        if ((arg0 instanceof UserLocationInfoAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not UserLocationInfoAttribute Avp");
                            return false;
                        }
                    case EUI:
                        if ((arg0 instanceof EUIAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not EUIAttribute Avp");
                            return false;
                        }
                    case DATE:
                        if ((arg0 instanceof TimeAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not TimeAttribute Avp");
                            return false;
                        }
                    case EUI64:
                        if ((arg0 instanceof EUI64Attribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not EUI64Attribute Avp");
                            return false;
                        }
                    case IPV6PREFIX:
                        if ((arg0 instanceof IPv6PrefixAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not IPv6 prefix type");
                            return false;
                        }
                    case PREPAIDTLV:
                        if ((arg0 instanceof PrepaidTLVAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not prepaid TLV type");
                            return false;
                        }
                    case CISCO_COMMAND_CODE:
                        if ((arg0 instanceof CiscoCommandCodeAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not cisco command code type");
                            return false;
                        }
                    case GTP_TUNNEL_DATA:
                        if ((arg0 instanceof GTPTunnelDataAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not GTP tunnel data type");
                            return false;
                        }
                    case TWAN_IDENTIFIER:
                        if ((arg0 instanceof TWANIdentifierAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not TWAN identifier type");
                            return false;
                        }
                    case GTPV1_TUNNEL_DATA:
                        if ((arg0 instanceof GTPv1TunnelDataAttribute && vendorId == RadiusConstants.STANDARD_VENDOR_ID) || (arg0 instanceof WimaxUnknownAttribute && vendorId == RadiusConstants.WIMAX_VENDOR_ID) == true) {
                            return true;
                        } else {
                            arg1.appendText("attribute( " + arg0.getID() + ") is not GTPV1 tunnel data type");
                            return false;
                        }
                    default:
                        arg1.appendText("avp( " + arg0.getID() + ") is not attribute type");
                        return false;

                }
            }else {
                if(arg0 instanceof IRadiusAttribute == true){
                    return true;
                }else {
                    arg1.appendText("attribute( " + arg0.getID() + ") is not vendor specific");
                    return false;
                }
            }

        }
    }
}
