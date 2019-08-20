package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupDataExt;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import java.util.List;

/**
 * A class that defines import operation of list of Rating Group from Web Service
 * Created by Ishani on 23/9/16.
 */
public class RatingGroupListManagementRequest {

    private List<RatingGroupDataExt> ratingGroup;
    private String action;
    private String parameter1;
    private String parameter2;

    public RatingGroupListManagementRequest(List<RatingGroupDataExt> ratingGroup, String action, String parameter1, String parameter2) {
        this.ratingGroup = ratingGroup;
        this.action = action;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public List<RatingGroupDataExt> getRatingGroup() {
        return ratingGroup;
    }

    public String getParameter1() {
        return parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public String getAction() {
        return action;
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);

        builder.append("Action ", action);
        builder.append("Parameter 1 ", getParameter1());
        builder.append("Parameter 2 ", getParameter2());

        return builder.toString();
    }
}
