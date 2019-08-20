package com.elitecore.netvertex.pm;


import java.util.Map;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.IPCanQoSFactory;
import com.elitecore.corenetvertex.pm.factory.QoSProfileDetailFactory;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;

public class QoSProfileDetailFactoryExt extends QoSProfileDetailFactory {

    public static QoSProfileDetailFactoryExt  createQoSProfile() {
        return new QoSProfileDetailFactoryExt();
    }

    @Override
    public QoSProfileDetail build() throws RuntimeException {
        QoSProfileAction action = QoSProfileAction.ACCEPT;

        if(reason != null){
            action = QoSProfileAction.REJECT;
        }


        if(ipcanQoS == null && action != QoSProfileAction.REJECT) {
            ipcanQoS = IPCanQoSFactory.randomQoS();
        }


        try {

            return new UMBaseQoSProfileDetail(name,
                    packageName,
                    action,
                    reason,
                    fupLevel,
                    (UMBaseQuotaProfileDetail) null
                    ,(Map<String, QuotaProfileDetail>) null,
                    false,
                    ipcanQoS,
                    pccRules
                    , false
                    , (SliceInformation)null
                    , 1,
                    false, com.elitecore.netvertex.pm.QoSProfileDetail.UsageProvider.CURRENT_EXECUTING_PACKAGE_USAGE_PROVIDER, "", null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}