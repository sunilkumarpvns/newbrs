package com.elitecore.netvertex.core.lrn.data;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;

public class LRNConfiguration implements ToStringable {

    private String lrn;
    private NetworkConfiguration networkConfiguration;

    public LRNConfiguration(String lrn,NetworkConfiguration networkConfiguration) {
        this.lrn = lrn;
        this.networkConfiguration = networkConfiguration;
    }


    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- LRN Configuration -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.incrementIndentation();
        builder.append("LRN", lrn);
        builder.append("Operator Name", networkConfiguration.getOperator());
        builder.append("Network Name", networkConfiguration.getNetworkName());
        builder.append("Mobile Network Type", networkConfiguration.getTechnology());
        builder.decrementIndentation();
    }

    public String getLrn() {
        return lrn;
    }

    public NetworkConfiguration getNetworkConfiguration() {
        return networkConfiguration;
    }
}
