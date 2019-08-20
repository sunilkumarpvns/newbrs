/**
 * Copyright (C) Elitecore Technologies Ltd.
 * BaseJspCodeGen.java
 * Created on Jul 25, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.genrator.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * @author kaushikvira
 */
public abstract class BaseJspCodeGen extends BaseCodeGen {
    
    protected String        fileExt    = ".jsp";
    
   
    
    protected String        fileName;
    
    protected String        filePath;
    
    protected static String prefixPath = File.separator + "jsp";
    
    
    
    
    
    
    /*
     * (non-Javadoc)
     * 
     * @see com.elitecore.elitecodegen.genrator.base.BaseCodeGen#getPrintWritter()
     */
    @Override
    public PrintWriter getPrintWritter() throws FileNotFoundException {
        
        File f = new File(this.realPath + File.separator + "webroot" + File.separator + this.filePath);
        
        if (!f.exists()) {
            this.log.info("Dir is not Exists...");
            if (f.mkdirs()) {
                this.log.info("Dir is Creadted..." + f.getAbsolutePath());
                return new PrintWriter(new FileOutputStream(new File(this.realPath + File.separator +"webroot" + File.separator + this.filePath + File.separator + this.fileName + this.fileExt)));
            }
        }
        this.log.info("Dir is Exit " + f.getAbsolutePath());
        
        return new PrintWriter(new FileOutputStream(new File(this.realPath + File.separator + "webroot" + File.separator + this.filePath + File.separator + this.fileName + this.fileExt)));
    }
    
}
