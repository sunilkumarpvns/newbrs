/**
 * Copyright (C) Elitecore Technologies Ltd.
 * BaseCodeGenrator.java
 * Created on Jul 23, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.base;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author kaushikvira
 */

public abstract class BaseCodeGenrator {
    
    protected String fileExt;
    
    protected String filePath;
    
    protected String fileName;
    
    protected String modualName;
    
    abstract protected PrintWriter getPrintWriter() throws FileNotFoundException;
    
    protected void genrateBaseComment( PrintWriter writer ) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM, yyyy");
        writer.println("/**                                                     ");
        writer.println(" * Copyright (C) Elitecore Technologies Ltd.            ");
        writer.println(" * " + this.fileName + "                                       ");
        writer.println(" * Created on " + sdf.format(new Date(System.currentTimeMillis())) + "                                        ");
        writer.println(" * Last Modified on                                     ");
        writer.println(" * @author :                                            ");
        writer.println(" */                                                     ");
    }
    
    public String initCap( String name ) {
        return String.valueOf(name.charAt(0)).toUpperCase().concat(name.substring(1));
    }
    
    public String getFileExt() {
        return this.fileExt;
    }
    
    public void setFileExt( String fileExt ) {
        this.fileExt = fileExt;
    }
    
    public String getFilePath() {
        return this.filePath;
    }
    
    public void setFilePath( String filePath ) {
        this.filePath = filePath;
    }
    
    public String getModualName() {
        return this.modualName;
    }
    
    public void setModualName( String modualName ) {
        this.modualName = modualName;
    }
    
}
