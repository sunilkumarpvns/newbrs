package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

import java.util.Comparator;

public class IPCANQosBasedComparator implements Comparator<IPCANQoS> {

    private static final String MODULE = "IP-CAN-QOS-COMPARATOR";

    @Override
    public int compare(IPCANQoS o1, IPCANQoS o2) {
        if(o1 == o2){
            return 0;
        }

        if(o2 == null){
            return 1;
        }

        if(o1.getGbrdlInBytes() == o2.getGbrdlInBytes() &&
                o1.getGbrulInBytes() == o2.getGbrulInBytes() &&
                o1.getMBRDLInBytes() == o2.getMBRDLInBytes() &&
                o1.getMBRULInBytes() == o2.getMBRULInBytes() &&
                o1.getAAMBRDLInBytes() == o2.getAAMBRDLInBytes() &&
                o1.getAAMBRULInBytes() == o2.getAAMBRULInBytes()){
            return 0;
        }


        if(o1.getQCI().isGBRQCI()){
            if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE, "Comparing GBRDL...");
            if(o1.getGbrdlInBytes() > o2.getGbrdlInBytes()){
                return 1;
            } else if(o1.getGbrdlInBytes() < o2.getGbrdlInBytes()) {
                return -1;
            }



            if(o1.getGbrulInBytes() > o2.getGbrulInBytes()){
                return 1;
            } else if(o1.getGbrulInBytes() < o2.getGbrulInBytes()) {
                return -1;
            }
        }


        if(o1.getAAMBRDLInBytes() > o2.getAAMBRDLInBytes()){
            return 1;
        } else if(o1.getAAMBRDLInBytes() < o2.getAAMBRDLInBytes()) {
            return -1;
        }



        if(o1.getMBRDLInBytes() > o2.getMBRDLInBytes()){
            return 1;
        } else if(o1.getMBRDLInBytes() < o2.getMBRDLInBytes()) {
            return -1;
        }

        if(o1.getMBRULInBytes() > o2.getMBRULInBytes()){
            return 1;
        } else if(o1.getMBRULInBytes() < o2.getMBRULInBytes()) {
            return -1;
        }

        return 0;
    }
}
