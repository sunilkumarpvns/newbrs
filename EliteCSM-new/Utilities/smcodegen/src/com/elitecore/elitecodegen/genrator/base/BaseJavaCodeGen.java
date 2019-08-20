/**
 * Copyright (C) Elitecore Technologies Ltd.
 * BaseJavaCodeGen.java
 * Created on Jul 25, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.genrator.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.elitecore.elitecodegen.base.EliteCodeGenConstant;

/**
 * @author kaushikvira
 */
public abstract class BaseJavaCodeGen extends BaseCodeGen {
    
    protected String fileExt = ".java";
    
    protected String packageName;
    
    protected String className;
    
    public String getClassName() {
        return this.className;
    }
    
    public String getPackageName() {
        return this.packageName;
    }
    
    public String getPackagePath() {
        
        return this.packageName.replace(".", File.separator);
    }
    
    protected void genrateBaseComment() {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM, yyyy");
        this.writer.println("/**                                                     ");
        this.writer.println(" * Copyright (C) Elitecore Technologies Ltd.            ");
        this.writer.println(" * FileName   " + this.className + ".java                 		");
        this.writer.println(" * ModualName " + this.modualName + "    			      		");
        this.writer.println(" * Created on " + sdf.format(new Date(System.currentTimeMillis())));
        this.writer.println(" * Last Modified on                                     ");
        this.writer.println(" * @author :  " + EliteCodeGenConstant.DocumentAuthor);
        this.writer.println(" */                                                     ");
    }
    
    @Override
    public PrintWriter getPrintWritter() throws FileNotFoundException {
        
        File f = new File(this.realPath + File.separator + "src" + File.separator + getPackagePath());
        if (!f.exists()) {
            this.log.info("Dir is not Exists...");
            if (f.mkdirs()) {
                this.log.info("Dir is Creadted..." + f.getAbsolutePath());
                return new PrintWriter(new FileOutputStream(new File(this.realPath + File.separator + "src" + File.separator + getPackagePath() + File.separator + this.className + this.fileExt)));
            }
        }
        this.log.info("Dir is Exit " + f.getAbsolutePath());
        
        return new PrintWriter(new FileOutputStream(new File(this.realPath + File.separator + "src" + File.separator + getPackagePath() + File.separator + this.className + this.fileExt)));
        
    }
    
}
