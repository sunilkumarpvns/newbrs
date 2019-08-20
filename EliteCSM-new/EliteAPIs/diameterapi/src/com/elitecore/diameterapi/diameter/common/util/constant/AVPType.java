package com.elitecore.diameterapi.diameter.common.util.constant;

import com.elitecore.diameterapi.diameter.common.packet.avps.BaseAVPBuilder;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum AVPType {

    @XmlEnumValue("Unsigned32")
    UNSIGNED32("Unsigned32"){
        @Override
        public BaseAVPBuilder baseAVPBuilder() { return new DiameterDictionary.AvpUnsigned32Builder(); }
    },
    @XmlEnumValue("Unsigned64")
    UNSIGNED64("Unsigned64"){
        @Override
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpUnsigned64Builder();
        }
    },
    @XmlEnumValue("Integer32")
    INTEGER32("Integer32"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpInteger32Builder();
        }
    },
    @XmlEnumValue("Integer64")
    INTEGER64("Integer64"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpInteger64Builder();
        }
    },
    @XmlEnumValue("Float32")
    FLOAT32("Float32"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpFloat32Builder();
        }
    },
    @XmlEnumValue("Float64")
    FLOAT64("Float64"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpFloat64Builder();
        }
    },
    @XmlEnumValue("Grouped")
    GROUPED("Grouped"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpGroupedBuilder();
        }
    },
    @XmlEnumValue("DiameterIdentity")
    DIAMETERIDENTITY("DiameterIdentity"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpDiameterIdentityBuilder();
        }
    },
    @XmlEnumValue("DiameterURI")
    DIAMETERURI("DiameterURI"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpDiameterURIBuilder();
        }
    },
    @XmlEnumValue("Time")
    TIME("Time"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpTimeBuilder();
        }
    },
    @XmlEnumValue("UTF8String")
    UTF8STRING("UTF8String"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpUTF8StringBuilder();
        }
    },
    @XmlEnumValue("IPAdress")
    IPADDRESS("IPAdress"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpAddressBuilder();
        }
    },
    @XmlEnumValue("IPv4Address")
    IPV4ADDRESS("IPv4Address"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpIpv4AddressBuilder();
        }
    },
    @XmlEnumValue("Enumerated")
    ENUMERATED("Enumerated"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpEnumeratedBuilder();
        }
    },
    @XmlEnumValue("UserLocationInfo")
    USERLOCATIONINFO("UserLocationInfo"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.AvpUserLocationInfoAvpBuilder();
        }
    },
    @XmlEnumValue("UserEquipmentInfoValue")
    USEREQUIPMENTINFOVALUE("UserEquipmentInfoValue"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.UserEquipmentInfoValueAvpBuilder();
        }
    },
    @XmlEnumValue("UserEquipmentInfo")
    USEREQUIPMENTINFO("UserEquipmentInfo"){
        public BaseAVPBuilder baseAVPBuilder() {
            return new DiameterDictionary.UserEquipmentInfoAvpBuilder();
        }
    },
    @XmlEnumValue("Octets")
    OCTETS("Octets"){
        public BaseAVPBuilder baseAVPBuilder() { return new DiameterDictionary.AvpOctetStringBuilder(); }
    };

    private final String avpType;

    AVPType( String avpType) {
        this.avpType = avpType;
    }

    public String getAVPType() {
        return avpType;
    }

    public abstract BaseAVPBuilder baseAVPBuilder();
}
