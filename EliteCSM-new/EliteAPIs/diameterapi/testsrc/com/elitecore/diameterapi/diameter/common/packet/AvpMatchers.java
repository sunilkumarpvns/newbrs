package com.elitecore.diameterapi.diameter.common.packet;

import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.*;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.*;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpUserEquipmentInfo;
import com.elitecore.diameterapi.diameter.common.packet.avps.threegpp.AvpUserLocationInfo;
import com.elitecore.diameterapi.diameter.common.util.constant.AVPType;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class AvpMatchers {
    public static class ContainsAttributeMatcher extends TypeSafeDiagnosingMatcher<IDiameterAVP> {

        private String avp;
        private String value;

        public ContainsAttributeMatcher(String attributeId, String value) {
            this.avp = attributeId;
            this.value = value;
        }

        public ContainsAttributeMatcher(String attributeId, long value) {
            this.avp = attributeId;
            this.value = Long.toString(value);
        }

        @Override
        public void describeTo(Description arg0) {
            arg0.appendText("request to contain avp ").appendText(avp).appendText(" with value ")
                    .appendText(value);
        }

        @Override
        protected boolean matchesSafely(IDiameterAVP arg0, Description arg1) {

            if(arg0.isGrouped() == false) {
                arg1.appendText("avp( "+ arg0.getAVPId() +") is not grouped Avp");
                return false;
            }

            IDiameterAVP avp = ((AvpGrouped)arg0).getSubAttribute(this.avp);
            if (avp == null) {
                arg1.appendText("avp(" + avp + ") was not found from avp( " + arg0.getAVPId() + ")");
                return false;
            }

            if (!avp.getStringValue().equals(value)) {
                arg1.appendText("was found with value ").appendText(avp.getStringValue());
                return false;
            }
            return true;
        }
    }


    public static class AvpOfType extends TypeSafeDiagnosingMatcher<IDiameterAVP> {


        private AVPType avpType;

        public AvpOfType(AVPType avpType) {

            this.avpType = avpType;
        }

        @Override
        public void describeTo(Description arg0) {
            arg0.appendText("attribute of type ").appendText(avpType.name());
        }

        @Override
        protected boolean matchesSafely(IDiameterAVP arg0, Description arg1) {

            switch (avpType) {
                case GROUPED:
                    if((arg0 instanceof AvpGrouped) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not grouped Avp");
                        return false;
                    } else {
                        return true;
                    }
                case ENUMERATED:
                    if((arg0 instanceof AvpEnumerated) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not enumerated Avp");
                        return false;
                    } else {
                        return true;
                    }
                case UNSIGNED32:
                    if((arg0 instanceof AvpUnsigned32) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not unsigned32 Avp");
                        return false;
                    } else {
                        return true;
                    }
                case UNSIGNED64:
                    if((arg0 instanceof AvpUnsigned64) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not unsigned64 Avp");
                        return false;
                    } else {
                        return true;
                    }
                case UTF8STRING:
                    if((arg0 instanceof AvpUTF8String) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not UTF8string Avp");
                        return false;
                    } else {
                        return true;
                    }
                case TIME:
                    if((arg0 instanceof AvpTime) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not time Avp");
                        return false;
                    } else {
                        return true;
                    }
                case OCTETS:
                    if((arg0 instanceof AvpOctetString) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not octetstring Avp");
                        return false;
                    } else {
                        return true;
                    }
                case FLOAT32:
                    if((arg0 instanceof AvpFloat32) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not float32 Avp");
                        return false;
                    } else {
                        return true;
                    }
                case FLOAT64:
                    if((arg0 instanceof AvpFloat64) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not float64 Avp");
                        return false;
                    } else {
                        return true;
                    }
                case INTEGER32:
                    if((arg0 instanceof AvpInteger32) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not integer32 Avp");
                        return false;
                    } else {
                        return true;
                    }
                case INTEGER64:
                    if((arg0 instanceof AvpInteger64) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not integer64 Avp");
                        return false;
                    } else {
                        return true;
                    }
                case IPADDRESS:
                    if((arg0 instanceof AvpAddress) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not IPaddress Avp");
                        return false;
                    } else {
                        return true;
                    }
                case DIAMETERURI:
                    if((arg0 instanceof AvpDiameterURI) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not diameterURI Avp");
                        return false;
                    } else {
                        return true;
                    }
                case IPV4ADDRESS:
                    if((arg0 instanceof AvpIPv4Address) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not IPV4address Avp");
                        return false;
                    } else {
                        return true;
                    }
                case DIAMETERIDENTITY:
                    if((arg0 instanceof AvpDiameterIdentity) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not diameteridenitity Avp");
                        return false;
                    } else {
                        return true;
                    }
                case USERLOCATIONINFO:
                    if((arg0 instanceof AvpUserLocationInfo) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not userlocationinfo Avp");
                        return false;
                    } else {
                        return true;
                    }
                case USEREQUIPMENTINFO:
                    if((arg0 instanceof AvpUserEquipmentInfo) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not userequipmentinfo Avp");
                        return false;
                    } else {
                        return true;
                    }
                case USEREQUIPMENTINFOVALUE:
                    if((arg0 instanceof AvpUserEquipmentInfoValue) == false){
                        arg1.appendText("avp( "+ arg0.getAVPId() +") is not userequipmentinfovalue Avp");
                        return false;
                    } else {
                        return true;
                    }
                default:
                    arg1.appendText("avp( "+ arg0.getAVPId() +") is not grouped Avp");
                    return false;

            }

        }
    }
}
