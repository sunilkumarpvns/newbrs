package com.elitecore.corenetvertex.constants;

public enum SubscriberLookupOn {
    SUBSCRIBER_IDENTITY("Subscriber Identity"),
    ALTERNATE_IDENTITY("Alternate Identity");

    private String label;

    SubscriberLookupOn(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

    public static String getLabelFromInstanceName(String name){
        if(SUBSCRIBER_IDENTITY.name().equals(name)){
            return SUBSCRIBER_IDENTITY.label;
        } else if(ALTERNATE_IDENTITY.name().equals(name)){
            return ALTERNATE_IDENTITY.label;
        }
        return null;
    }
}
