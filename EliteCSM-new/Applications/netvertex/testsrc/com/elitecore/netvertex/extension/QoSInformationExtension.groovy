package com.elitecore.netvertex.extension

import com.elitecore.netvertex.pm.FinalQoSSelectionData
import com.elitecore.netvertex.pm.QoSInformation

class QoSInformationExtension {

    public static FinalQoSSelectionData stopSelectionProcess(final QoSInformation self) {
        self.endQoSSelectionProcess()
        FinalQoSSelectionData data = self.endProcess()
        //println self.trace

        return data;

    }
}
