/**
 * Copyright (C) Elitecore Technologies Ltd.
 * ColumnBean.java
 * Created on Jul 21, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.base;

public class ColumnBean {
    
    private int     size;
    private String  type;
    private String  name;
    private String  javaName;
    private String  javaType;
    private boolean selectionStaus;
    private boolean nullable;
    
    public ColumnBean( int size ,
                       String type ,
                       String name ,
                       String javaName ,
                       String javaType ,
                       boolean nullStaus ) {
        
        this.javaName = javaName;
        this.name = name;
        this.size = size;
        this.type = type;
        this.javaType = javaType;
        this.selectionStaus = true;
        this.nullable = nullStaus;
    }
    
    public boolean isNullable() {
        return this.nullable;
    }
    
    public void setNullable( boolean nullable ) {
        this.nullable = nullable;
    }
    
    public String getJavaType() {
        return this.javaType;
    }
    
    public void setJavaType( String javaType ) {
        this.javaType = javaType.trim();
    }
    
    public boolean isSelectionStaus() {
        return this.selectionStaus;
    }
    
    public void setSelectionStaus( boolean selectionStaus ) {
        this.selectionStaus = selectionStaus;
    }
    
    public String getJavaName() {
        return this.javaName;
    }
    
    public void setJavaName( String javaName ) {
        this.javaName = javaName.trim();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName( String name ) {
        this.name = name.trim();
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void setSize( int size ) {
        this.size = size;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType( String type ) {
        this.type = type.trim();
    }
    
}
