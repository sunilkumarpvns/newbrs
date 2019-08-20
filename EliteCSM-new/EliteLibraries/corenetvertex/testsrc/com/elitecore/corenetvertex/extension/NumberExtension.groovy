package com.elitecore.corenetvertex.extension

import com.elitecore.corenetvertex.constants.DataUnit
import com.elitecore.corenetvertex.constants.QoSUnit
import com.elitecore.corenetvertex.pm.util.QoS
import com.elitecore.corenetvertex.pm.util.Usage

class NumberExtension {

    public static QoS getMbps(final Integer self) {

        def s = new QoS()
        s.unit = QoSUnit.Mbps;
        s.noOfQoS = self;
        return s;
    }

    public static Usage getKb(final Integer self) {

        def s = new Usage()
        s.unit = DataUnit.KB
        s.noOfunit = self;
        return s;
    }
}
