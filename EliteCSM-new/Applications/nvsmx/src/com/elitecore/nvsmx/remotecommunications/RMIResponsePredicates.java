package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.session.SessionInformation;

import java.util.Collection;

/**
 * Created by aditya on 5/8/17.
 */
public class RMIResponsePredicates {

    public static final Predicate<RMIResponse<Collection<SessionInformation>>> NOT_NULL_AND_NOT_EMPTY_RMI_RESPONSE = new Predicate<RMIResponse<Collection<SessionInformation>>>() {
        @Override
        public boolean apply(RMIResponse<Collection<SessionInformation>> input) {
            if (input.isErrorOccurred()) {
                return false;
            } else {
                Collection<SessionInformation> response = input.getResponse();
                if (Collectionz.isNullOrEmpty(response)) {
                    return false;
                }

                return true;
            }
        }
    };





    
}
