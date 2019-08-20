package com.elitecore.nvsmx.ws.sessionmanagement.blmanager;

import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.session.SessionInformation;

import javax.annotation.Nullable;

/**
 * Created by aditya on 5/17/17.
 */
public class SessionTypePredicate implements Predicate<SessionInformation> {

    @Nullable private final String sessionType;

    private SessionTypePredicate(@Nullable String sessionType) {
        this.sessionType = sessionType;
    }

    public static SessionTypePredicate create(String sessionType){
        return new SessionTypePredicate(sessionType);
    }

    @Override
    public boolean apply(SessionInformation sessionInformation) {
        if(sessionInformation == null ){
            return false;
        }
        if(Strings.isNullOrEmpty(sessionType) == false &&
                sessionType.equalsIgnoreCase(sessionInformation.getValue(PCRFKeyConstants.CS_SESSION_TYPE.val)) == false){
            return false;
        }
        return true;
    }}
