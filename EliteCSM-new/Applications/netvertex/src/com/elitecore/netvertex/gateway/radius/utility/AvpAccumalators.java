package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import java.util.List;

public class AvpAccumalators {

    private  AvpAccumalators() {
        throw new IllegalStateException("Utility class");
    }

    public static AvpAccumalator of(final GroupedAttribute groupedAttribute) {
        return new GroupedAvpAccumelator(groupedAttribute);
    }

    public static AvpAccumalator of(final RadiusPacket radiusPacket) {

        return new RadiusPacketAvpAccumalator(radiusPacket);
    }

    private static class GroupedAvpAccumelator implements AvpAccumalator {

        private final GroupedAttribute groupedAttribute;

        private GroupedAvpAccumelator(GroupedAttribute groupedAttribute) {
            this.groupedAttribute = groupedAttribute;
        }


        @Override
        public void add(IRadiusAttribute radiusAttribute) {
            groupedAttribute.addTLVAttribute(radiusAttribute);
        }

        @Override
        public void addInfoAttribute(IRadiusAttribute radiusAttribute) {
            groupedAttribute.addTLVAttribute(radiusAttribute);
        }

        @Override
        public void add(List<IRadiusAttribute> radiusAttributeList) {
            radiusAttributeList.forEach(groupedAttribute::addTLVAttribute);
        }

        @Override
        public boolean isEmpty() {
            return Collectionz.isNullOrEmpty(groupedAttribute.getAttributes());
        }

        @Override
        public void addAttribute(String id, String value) {
            groupedAttribute.addsubAttribute(id, value);
        }

        @Override
        public void addAttribute(String id, int value) {
            groupedAttribute.addsubAttribute(id, value);
        }

        @Override
        public void addAttribute(String id, long value) {
            groupedAttribute.addsubAttribute(id, value);
        }
    }

    private static class RadiusPacketAvpAccumalator implements AvpAccumalator {

        private RadiusPacket radiusPacket;

        public RadiusPacketAvpAccumalator(RadiusPacket radiusPacket) {

            this.radiusPacket = radiusPacket;
        }

        @Override
        public void add(IRadiusAttribute radiusAttribute) {
            radiusPacket.addAttribute(radiusAttribute);
        }

        @Override
        public void add(List<IRadiusAttribute> radiusAttributes) {
            radiusPacket.addAttributes(radiusAttributes);
        }

        @Override
        public boolean isEmpty() {
            return Collectionz.isNullOrEmpty(radiusPacket.getRadiusAttributes());
        }

        @Override
        public void addInfoAttribute(IRadiusAttribute radiusAttribute) {
            radiusPacket.addInfoAttribute(radiusAttribute);
        }

        @Override
        public void addAttribute(String id, String value) {
            radiusPacket.addAttribute(id, value);
        }

        @Override
        public void addAttribute(String id, int value) {
            radiusPacket.addAttribute(id, value);
        }

        @Override
        public void addAttribute(String id, long value) {
            radiusPacket.addAttribute(id, value);
        }
    }
}
