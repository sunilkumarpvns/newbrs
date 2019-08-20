package com.elitecore.elitecodegen.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableDataBean {
    
    private static int         totalTable      = 0;
    public static final Map    orcalVsJavaType = new HashMap<String, String>();
    private String             strTableName;
    private String             strPackageName;
    private String             strjavaName;
    transient List<ColumnBean> cloumnList;
    private boolean            staus;
    private String             strPrimaryKey;
    static {
        TableDataBean.orcalVsJavaType.put("VARCHAR2", "String");
        TableDataBean.orcalVsJavaType.put("DATE", "java.sql.Timestamp");
        TableDataBean.orcalVsJavaType.put("CHAR", "String");
    }
    
    public TableDataBean() {
        this.staus = false;
    }
    
    public boolean isStaus() {
        return this.staus;
    }
    
    public void setStaus( boolean staus ) {
        this.staus = staus;
    }
    
    public List getCloumnList() {
        return this.cloumnList;
    }
    
    public ColumnBean getCloumn( int index ) {
        return this.cloumnList.get(index);
    }
    
    public TableDataBean( String strTableName ) {
        
        TableDataBean.totalTable++;
        this.strTableName = strTableName;
        // TODO Auto-generated constructor stub
    }
    
    public String getStrjavaName() {
        return this.strjavaName;
    }
    
    public void setStrjavaName( String strjavaName ) {
        this.strjavaName = strjavaName;
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
    
    public String getStrPrimaryKey() {
        return this.strPrimaryKey;
    }
    
    public void setStrPrimaryKey( String strPrimaryKey ) {
        this.strPrimaryKey = strPrimaryKey;
    }
    
    public void setCloumnList( List cloumnList ) {
        this.cloumnList = cloumnList;
    }
    
}
