/**
 * Copyright (C) Elitecore Technologies Ltd.
 * DataClassGen.java
 * Created on Jul 25, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.genrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.base.ColumnBean;
import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.genrator.base.BaseCodeGen;
import com.elitecore.elitecodegen.genrator.base.BaseJavaCodeGen;

/**
 * @author kaushikvira
 */
public class DataClassGen extends BaseJavaCodeGen {
    
    private static String prefix       = "com.elitecore";
    private static String classPostfix = "Data";
    
    public DataClassGen( TableDataBean tableBean ,
                         String strApplicationName ,
                         String subModualName ,
                         String modualName ,
                         String workingDir ) {
        
        this.applicationName = strApplicationName;
        this.subModualName = subModualName.replace(File.separator, ".");
        this.modualName = modualName;
        BaseCodeGen.tableBean = tableBean;
        
        if (subModualName.equals(""))
            this.packageName = DataClassGen.prefix + "." + this.applicationName.toLowerCase() + ".datamanager." + modualName.toLowerCase() + ".data";
        else this.packageName = DataClassGen.prefix + "." + this.applicationName.toLowerCase() + ".datamanager." + subModualName.toLowerCase() + "." + modualName.toLowerCase() + ".data";
        
        this.className = initCap(tableBean.getStrTableName().substring(4)) + DataClassGen.classPostfix;
        this.realPath = workingDir;
        this.log = Logger.getLogger(DataClassGen.class);
    }
    
    @Override
    public void genrateFile() throws FileNotFoundException {
        
        this.writer = getPrintWritter();
        genrateBaseComment();
        
        this.writer.println("package " + this.packageName + ";");
        
        this.writer.println("import " +  this.packageName+".I"+this.className+";");
         this.writer.println("   ");
       
        this.writer.println("public class " + this.className +  " implements "+  "I"+this.className + "{");
        this.writer.println("");
        
        Iterator itColumnList = BaseCodeGen.tableBean.getCloumnList().iterator();
        
        while (itColumnList.hasNext()) {
            ColumnBean colBean = (ColumnBean) itColumnList.next();
            if (colBean.isSelectionStaus())
                this.writer.println("    private " + colBean.getJavaType() + " " + colBean.getJavaName() + ";");
        }
        
        this.writer.println("");
        this.writer.println("");
        this.writer.println("");
        itColumnList = BaseCodeGen.tableBean.getCloumnList().iterator();
        
        while (itColumnList.hasNext()) {
            ColumnBean colBean = (ColumnBean) itColumnList.next();
            if (colBean.isSelectionStaus()) {
                this.writer.println("    public " + colBean.getJavaType() + " get" + initCap(colBean.getJavaName()) + "(){");
                this.writer.println("        return " + colBean.getJavaName() + ";");
                this.writer.println("    }");
                
                this.writer.println("");
                
                this.writer.println("	public void set" + initCap(colBean.getJavaName()) + "(" + colBean.getJavaType() + " " + colBean.getJavaName() + ") {");
                this.writer.println("		this." + colBean.getJavaName() + " = " + colBean.getJavaName() + ";");
                this.writer.println("	}");
                this.writer.println("");
                this.writer.println("");
                
            }
        }
        this.writer.println("}");
        this.writer.close();
        
    }
    
}
