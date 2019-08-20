/**
 * Copyright (C) Elitecore Technologies Ltd.
 * DataClassBean.java
 * Created on Jul 21, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.controller.form;

import java.util.Map;

/**
 * @author kaushikvira
 */
public class DataClassBean {
    
    private String strPackageName;
    
    private String strTableName;
    
    private String strModifer;
    
    private Map    filedMap;
    
    public Map getFiledMap() {
        return this.filedMap;
    }
    
    public void setFiledMap( Map filedMap ) {
        this.filedMap = filedMap;
    }
    
    public String getStrModifer() {
        return this.strModifer;
    }
    
    public void setStrModifer( String strModifer ) {
        this.strModifer = strModifer;
    }
    
    public String getStrPackageName() {
        return this.strPackageName;
    }
    
    public void setStrPackageName( String strPackageName ) {
        this.strPackageName = strPackageName;
    }
    
    public String getStrTableName() {
        return this.strTableName;
    }
    
    public void setStrTableName( String strTableName ) {
        this.strTableName = strTableName;
    }
    
    public DataClassBean( String strTableName ) {
        this.strTableName = strTableName;
        // TODO Auto-generated constructor stub
    }
}
