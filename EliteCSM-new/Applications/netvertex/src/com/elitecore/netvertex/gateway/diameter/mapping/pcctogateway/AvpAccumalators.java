package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;

import java.util.Date;
import java.util.List;

public class AvpAccumalators {

	public static  AvpAccumalator of(final AvpGrouped avpGrouped) {
        return new GroupedAvpAccumelator(avpGrouped);
    }

    public static  AvpAccumalator of(final DiameterPacket diameterPacket) {

        return new DiameterPacketAvpAccumalator(diameterPacket);
    }

    private static class GroupedAvpAccumelator implements AvpAccumalator {

	    private final AvpGrouped avpGrouped;

        private GroupedAvpAccumelator(AvpGrouped avpGrouped) {
            this.avpGrouped = avpGrouped;
        }


        @Override
        public void add(IDiameterAVP diameterAVP) {
            avpGrouped.addSubAvp(diameterAVP);
        }

        @Override
        public void add(List<IDiameterAVP> diameterAVP) {
            avpGrouped.addSubAvps(diameterAVP);
        }

        @Override
        public boolean isEmpty() {
            return Collectionz.isNullOrEmpty(avpGrouped.getGroupedAvp());
        }

        @Override
        public void addAvp(String id, String value) {
            avpGrouped.addSubAvp(id, value);
        }

        @Override
        public void addAvp(String id, int value) {
            avpGrouped.addSubAvp(id, value);
        }

        @Override
        public void addAvp(String id, long value) {
            avpGrouped.addSubAvp(id, value);
        }

        @Override
        public void addAvp(String id, Date value) {
            avpGrouped.addSubAvp(id, value);
        }

        @Override
        public void addInfoAVP(IDiameterAVP diameterAVP) {
            add(diameterAVP);
        }

        @Override
        public void addInfoAVP(String id, String value) {
            throw new UnsupportedOperationException("Info avp is not supported in group avp");
        }
    }

    private static class DiameterPacketAvpAccumalator implements AvpAccumalator{

        private DiameterPacket diameterPacket;

        public DiameterPacketAvpAccumalator(DiameterPacket diameterPacket) {

            this.diameterPacket = diameterPacket;
        }

        @Override
        public void add(IDiameterAVP diameterAVP) {
            diameterPacket.addAvp(diameterAVP);
        }

        @Override
        public void add(List<IDiameterAVP> diameterAVP) {
            diameterPacket.addAvps(diameterAVP);
        }

        @Override
        public boolean isEmpty() {
            return Collectionz.isNullOrEmpty(diameterPacket.getAVPList());
        }

        @Override
        public void addInfoAVP(IDiameterAVP diameterAVP) {
            diameterPacket.addInfoAvp(diameterAVP);
        }

        @Override
        public void addInfoAVP(String id, String value) {
            diameterPacket.addInfoAvp(id, value);
        }

        @Override
        public void addAvp(String id, String value) {
            diameterPacket.addAvp(id, value);
        }

        @Override
        public void addAvp(String id, int value) {
            diameterPacket.addAvp(id, value);
        }

        @Override
        public void addAvp(String id, long value) {
            diameterPacket.addAvp(id, value);
        }

        @Override
        public void addAvp(String id, Date value) {
            diameterPacket.addAvp(id, value);
        }
    }
}
