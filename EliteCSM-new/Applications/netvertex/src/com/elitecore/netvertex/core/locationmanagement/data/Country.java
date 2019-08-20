package com.elitecore.netvertex.core.locationmanagement.data;

import org.apache.commons.lang.StringUtils;

public class Country {

    private String id;
    private String name;
    private String code;
    private String geography;


    public Country(String id,String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    public String getGeography() {
        return geography;
    }

    public void setGeography(String geography) {
        this.geography = geography;
    }

    public void addGeography(String geography) {
        if(StringUtils.isEmpty(this.geography)) {
            setGeography(geography);
        } else {
            this.geography += "," + geography;
        }

    }

}
