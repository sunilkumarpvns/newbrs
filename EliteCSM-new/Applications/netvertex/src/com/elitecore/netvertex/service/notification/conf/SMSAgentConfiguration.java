package com.elitecore.netvertex.service.notification.conf;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class SMSAgentConfiguration implements ToStringable{

    private String url;
    private String password;

    public SMSAgentConfiguration(String url, String password) {
        this.url = url;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.append("URL", url);
    }

    @Override
    public String toString() {
        IndentingToStringBuilder indentingToStringBuilder = new IndentingToStringBuilder();
        indentingToStringBuilder.appendHeading("-- SMS Templete -- ");
        indentingToStringBuilder.incrementIndentation();
        toString(indentingToStringBuilder);
        indentingToStringBuilder.decrementIndentation();
        return indentingToStringBuilder.toString();
    }
}
