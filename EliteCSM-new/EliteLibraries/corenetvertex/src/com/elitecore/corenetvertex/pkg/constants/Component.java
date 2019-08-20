package com.elitecore.corenetvertex.pkg.constants;

/**
 * A class that will bifurcate the ACL modules based on the defined components
 * @author ishani.dave
 */
public enum Component {
    SM("Server Manager","sm"),
    PD("Policy Designer","pd");

    private final String displayName;
    private final String url;

    Component(String displayName, String url) {
        this.displayName = displayName;
        this.url = url;
    }


    public String getDisplayName() {
        return displayName;
    }

    public String getUrl() {
        return url;
    }
}
