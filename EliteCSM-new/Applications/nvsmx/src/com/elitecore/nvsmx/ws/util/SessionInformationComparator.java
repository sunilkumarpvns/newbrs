package com.elitecore.nvsmx.ws.util;

import com.elitecore.corenetvertex.session.SessionInformation;
import com.elitecore.nvsmx.remotecommunications.RMIResponse;

import java.util.Comparator;

/**
 * Created by aditya on 5/11/17.
 */
public class SessionInformationComparator implements Comparator<RMIResponse<SessionInformation>> {

    private static final SessionInformationComparator sessionInformationComparator;


    static {
        sessionInformationComparator = new SessionInformationComparator();
    }

    public static SessionInformationComparator getSessionInformationComparatorBasedOnCreationTime(){
        return sessionInformationComparator;
    }

    @Override
    public int compare(RMIResponse<SessionInformation> o1, RMIResponse<SessionInformation> o2) {
        return o1.getResponse().getCreationTime().compareTo(o2.getResponse().getCreationTime());
    }




}
