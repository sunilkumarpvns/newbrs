package com.elitecore.netvertex.service.notification.conf;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class EmailAgentConfiguration implements ToStringable{

    private String mailFrom;
    private String host;
    private int port;
    private String userName;
    private String password;

    public EmailAgentConfiguration(String mailFrom, String host, int port, String userName, String password) {
        this.mailFrom = mailFrom;
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.append("Host", host);
        builder.append("Port", port);
        builder.append("From", mailFrom);
        builder.append("UserName", userName);
    }

    @Override
    public String toString() {
        IndentingToStringBuilder indentingToStringBuilder = new IndentingToStringBuilder();
        indentingToStringBuilder.appendHeading("-- Email Templete -- ");
        indentingToStringBuilder.incrementIndentation();
        toString(indentingToStringBuilder);
        indentingToStringBuilder.decrementIndentation();
        return indentingToStringBuilder.toString();
    }
}
