/**
 * Copyright (C) Elitecore Technologies Ltd.
 * DataClassGenrator.java
 * Created on Jul 23, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.genrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.base.BaseCodeGenrator;
import com.elitecore.elitecodegen.base.ColumnBean;
import com.elitecore.elitecodegen.base.TableDataBean;

/**
 * @author kaushikvira
 */
public class DataClassGenrator extends BaseCodeGenrator {
    
    protected TableDataBean tableBean;
    
    protected String        fileName;
    
    private static Logger   log = Logger.getLogger(DataClassGenrator.class);
    
    public DataClassGenrator( TableDataBean tableBean ,
                              String path ) {
        this.tableBean = tableBean;
        this.filePath = path;
        this.fileName = tableBean.getStrjavaName();
    }
    
    @Override
    public PrintWriter getPrintWriter() throws FileNotFoundException {
        
        String packageName = this.tableBean.getStrPackageName();
        if (packageName != null) {
            String packagePath = packageName.replace(".", File.separator);
            
            DataClassGenrator.log.info("Dir path + :" + packagePath);
            
            File f = new File(this.filePath + File.separator + packagePath);
            
            if (!f.exists()) {
                
                DataClassGenrator.log.info("Dir is not Exists...");
                
                if (f.mkdirs()) {
                    DataClassGenrator.log.info("Dir is Creadted...");
                    
                    DataClassGenrator.log.info("File Genrated" + this.filePath + File.separator + packagePath + File.separator + this.fileName + ".java");
                    return new PrintWriter(new FileOutputStream(new File(this.filePath + File.separator + packagePath + File.separator + this.fileName + ".java")));
                }
            }
            
            DataClassGenrator.log.info("File genrated :" + this.filePath + File.separator + packagePath + File.separator + this.fileName + ".java");
            
            return new PrintWriter(new FileOutputStream(new File(this.filePath + File.separator + packagePath + File.separator + this.fileName + ".java")));
            
        } else {
            DataClassGenrator.log.info("File genrated :" + this.filePath + File.separator + this.fileName + ".java");
            return new PrintWriter(new FileOutputStream(new File(this.filePath + File.separator + this.fileName + ".java")));
        }
    }
    
    public void genrateDataFile( PrintWriter writer ) {
        
        if (!tableBean.getStrPackageName().equals("")) {
            writer.println("package " + this.tableBean.getStrPackageName() + ";");
            writer.println("");
        }
        
        genrateBaseComment(writer);
        
        writer.println("public class " + this.tableBean.getStrjavaName() + "{");
        writer.println("");
        
        Iterator itColumnList = this.tableBean.getCloumnList().iterator();
        
        while (itColumnList.hasNext()) {
            ColumnBean colBean = (ColumnBean) itColumnList.next();
            if (colBean.isSelectionStaus())
                writer.println("    private " + colBean.getJavaType() + " " + colBean.getJavaName() + ";");
        }
        
        writer.println("");
        writer.println("");
        writer.println("");
        itColumnList = this.tableBean.getCloumnList().iterator();
        
        while (itColumnList.hasNext()) {
            ColumnBean colBean = (ColumnBean) itColumnList.next();
            if (colBean.isSelectionStaus()) {
                writer.println("    public " + colBean.getJavaType() + " get" + initCap(colBean.getJavaName()) + "(){");
                writer.println("        return " + colBean.getJavaName() + ";");
                writer.println("    }");
                
                writer.println("");
                
                writer.println("	public void set" + initCap(colBean.getJavaName()) + "(" + colBean.getJavaType() + " " + colBean.getJavaName() + ") {");
                writer.println("		this." + colBean.getJavaName() + " = " + colBean.getJavaName() + ";");
                writer.println("	}");
                writer.println("");
                writer.println("");
                
            }
        }
        writer.println("}");
        writer.close();
        
    }
    
}
