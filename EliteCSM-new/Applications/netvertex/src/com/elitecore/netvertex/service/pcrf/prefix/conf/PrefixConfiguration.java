package com.elitecore.netvertex.service.pcrf.prefix.conf;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class PrefixConfiguration implements ToStringable {
    private String prefix;
    private String country;
    private String operator;
    private String networkName;

    public PrefixConfiguration() {

    }

    public PrefixConfiguration(String prefix, String prefixName, String country, String operator, String networkName) {
        this.prefix = prefix;
        this.country = country;
        this.operator = operator;
        this.networkName = networkName;
    }

    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }


    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Prefix Configuration -- ");
        toString(builder);
        return builder.toString();

    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.incrementIndentation();
        builder.append("Prefix", prefix);
        builder.append("ContryCode", country);
        builder.append("Operator", operator);
        builder.append("NetworkName", networkName);
        builder.decrementIndentation();
    }
}
