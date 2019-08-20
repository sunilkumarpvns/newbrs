/**
 * Copyright (C) Elitecore Technologies Ltd.
 * NavigateDataClassForm.java
 * Created on Jul 21, 2007
 * Last Modified on 
 * @author : Kaushik vira
 */

package com.elitecore.elitecodegen.controller.form;

import org.apache.struts.action.ActionForm;

/**
 * @author kaushikvira
 */
public class NavigateDataClassForm extends ActionForm {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1772931603110986473L;
    
    private String            strPackageName;
    
    private String            strClassName;
    
    private int[]             chioce;
    
    private String            strJavaType[];
    
    private String            strJavaName[];
    
    private int               seq;
    
    private int               op;
    
    private String            strTableName;
    
    public String getStrTableName() {
        return this.strTableName;
    }
    
    public void setStrTableName( String strTableName ) {
        this.strTableName = strTableName;
    }
    
    public int getOp() {
        return this.op;
    }
    
    public void setOp( int op ) {
        this.op = op;
    }
    
    public int[] getChioce() {
        return this.chioce;
    }
    
    public void setChioce( int[] chioce ) {
        this.chioce = chioce;
    }
    
    public int getSeq() {
        return this.seq;
    }
    
    public void setSeq( int seq ) {
        this.seq = seq;
    }
    
    public String getStrClassName() {
        return this.strClassName;
    }
    
    public void setStrClassName( String strClassName ) {
        this.strClassName = strClassName;
    }
    
    public String[] getStrJavaName() {
        return this.strJavaName;
    }
    
    public void setStrJavaName( String[] strJavaName ) {
        this.strJavaName = strJavaName;
    }
    
    public String[] getStrJavaType() {
        return this.strJavaType;
    }
    
    public void setStrJavaType( String[] strJavaType ) {
        this.strJavaType = strJavaType;
    }
    
    public String getStrPackageName() {
        return this.strPackageName;
    }
    
    public void setStrPackageName( String strPackageName ) {
        this.strPackageName = strPackageName;
    }
    
}
