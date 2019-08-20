package com.elitecore.netvertex.pm

import com.elitecore.corenetvertex.constants.PriorityLevel
import com.elitecore.corenetvertex.constants.QCI
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS
import com.elitecore.corenetvertex.pm.util.QoS

public class IPCanQoSBuilder {
    private static final Random RANDOM = new Random();
    private QoS mbrdl;
    private QoS mbrul;

    static IPCANQoS bandwidth(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = IPCanQoSBuilder) Closure closure) {
        IPCanQoSBuilder builder = new IPCanQoSBuilder();
        def code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY;
        code()
        return new IPCANQoS.IPCANQoSBuilder().withMBRDL(builder.mbrdl.noOfQoS, builder.mbrdl.unit)
        .withMBRUL(builder.mbrul.noOfQoS, builder.mbrul.unit)
        .withQCI(QCI.QCI_NON_GBR_9)
        .withPriorityLevel(PriorityLevel.PRIORITY_LEVEL_1).build();
    }

    def upload(QoS qoS) {
        mbrul = qoS;
    }

    def download(QoS qoS) {
        mbrdl = qoS;
    }

    public static int randomQoS() {
        return RANDOM.nextInt(Integer.MAX_VALUE);
    }
}
