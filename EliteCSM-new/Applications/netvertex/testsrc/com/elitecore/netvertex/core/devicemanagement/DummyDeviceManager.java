package com.elitecore.netvertex.core.devicemanagement;

import com.elitecore.netvertex.service.pcrf.DeviceManager;
import com.elitecore.netvertex.service.pcrf.TACDetail;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

public class DummyDeviceManager extends DeviceManager{
    private Map<String, TACDetail> tacDetailMap;

    public static DummyDeviceManager spy() {return Mockito.spy(new DummyDeviceManager());}

    public DummyDeviceManager() {
        tacDetailMap = new HashMap<>();
    }

    public TACDetail spyTACdetailWithTACid(String tac) {
        TACDetail detail = Mockito.spy(new TACDetail(tac, "brand", "model", "hwType", "os", "2011", "additionalInfo"));
        tacDetailMap.put(tac, detail);
        return detail;
    }

    public TACDetail getTACDetail(String tac) {
        return tacDetailMap.get(tac);
    }
}
