/**
 * Copyright (C) Elitecore Technologies Ltd.
 * BaseApplicationResourceCodeGen.java
 * Created on Jul 31, 2007
 * Last Modified on 
 * @author : kaushikvira
 */
package com.elitecore.elitecodegen.genrator.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public abstract class BaseApplicationResourceCodeGen extends BaseCodeGen {
    
    protected static String fileExt = ".properties";
    
    protected String baseModualName;
    
    protected String filePath;
    
    protected String fileName;
    
    @Override
    public PrintWriter getPrintWritter() throws FileNotFoundException {
        
        File f = new File(this.realPath + File.separator + "src" + File.separator + this.filePath + File.separator);
        
        if (!f.exists()) {
            this.log.info("Dir is not Exists...");
            if (f.mkdirs()) {
                this.log.info("Dir is Creadted..." + f.getAbsolutePath());
                return new PrintWriter(new FileOutputStream(new File(this.realPath + File.separator + "src" + File.separator + this.filePath + File.separator + this.fileName + BaseApplicationResourceCodeGen.fileExt)));
            }
        }
        this.log.info("Dir is Exit " + f.getAbsolutePath());
        
        return new PrintWriter(new FileOutputStream(new File(this.realPath + File.separator + "src" + File.separator + this.filePath + File.separator + this.fileName + BaseApplicationResourceCodeGen.fileExt)));
    }
    
}
