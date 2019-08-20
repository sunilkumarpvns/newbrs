package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import java.util.List;

/**
 * A class that defines import operation on list of IMS Package from Web Service
 */
public class IMSPackageManagementListRequest {
    private List<IMSPkgData> imsPkgData;
    private String action;
    private String parameter1;
    private String parameter2;

    public IMSPackageManagementListRequest(List<IMSPkgData> imsPkgData, String action, String parameter1, String parameter2){
        this.imsPkgData = imsPkgData;
        this.action = action;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }


    public List<IMSPkgData> getImsPkgData() {
        return imsPkgData;
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

    @Override
    public String toString(){
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);

        builder.append("Action ", action);
        builder.append("Parameter 1 ", getParameter1());
        builder.append("Parameter 2 ", getParameter2());

        return builder.toString();
    }
}
