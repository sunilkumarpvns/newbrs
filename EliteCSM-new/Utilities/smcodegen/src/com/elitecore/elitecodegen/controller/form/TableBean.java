package com.elitecore.elitecodegen.controller.form;

public class TableBean {
    
    private String strColumnName;
    
    private String strColumnType;
    
    private int    intSize;
    
    private int    intJavaSqlType;
    
    public TableBean( String columnName ,
                      String columnType ,
                      int columnSize ,
                      int javaSqlType ) {
        this.intJavaSqlType = javaSqlType;
        this.strColumnName = columnName;
        this.strColumnType = columnType;
        this.intSize = columnSize;
    }
    
    public int getIntSize() {
        return this.intSize;
    }
    
    public void setIntSize( int intSize ) {
        this.intSize = intSize;
    }
    
    public String getStrColumnName() {
        return this.strColumnName;
    }
    
    public void setStrColumnName( String strColumnName ) {
        this.strColumnName = strColumnName;
    }
    
    public String getStrColumnType() {
        return this.strColumnType;
    }
    
    public void setStrColumnType( String strColumnType ) {
        this.strColumnType = strColumnType;
    }
    
    public int getIntJavaSqlType() {
        return this.intJavaSqlType;
    }
    
    public void setIntJavaSqlType( int intJavaSqlType ) {
        this.intJavaSqlType = intJavaSqlType;
    }
    
}
