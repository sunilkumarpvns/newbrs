package com.elitecore.corenetvertex.pm.service;

import com.elitecore.corenetvertex.constants.PkgStatus;

import java.io.Serializable;

public class Service implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private PkgStatus status;

    public Service(String id, String name, PkgStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PkgStatus getStatus() {
        return status;
    }
}
