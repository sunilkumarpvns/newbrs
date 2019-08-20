package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;

import java.util.Date;
import java.util.List;

/**
 * Created by harsh on 6/29/17.
 */
public interface AvpAccumalator {
    public void add(IDiameterAVP diameterAVP);
    public void addInfoAVP(IDiameterAVP diameterAVP);
    public void addInfoAVP(String id, String value);
    public void add(List<IDiameterAVP> diameterAVP);
    public boolean isEmpty();

    void addAvp(String id, String value);
    void addAvp(String id, int value);
    void addAvp(String id, long value);
    void addAvp(String id, Date value);
}
