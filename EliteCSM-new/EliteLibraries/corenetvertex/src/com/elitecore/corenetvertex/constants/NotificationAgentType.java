package com.elitecore.corenetvertex.constants;

public enum NotificationAgentType {
    EMAIL_AGENT("Email Agent"),
    SMS_AGENT("SMS Agent");

    private String type;

    NotificationAgentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
