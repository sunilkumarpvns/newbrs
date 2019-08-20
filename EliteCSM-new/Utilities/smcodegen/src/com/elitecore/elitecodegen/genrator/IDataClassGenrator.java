/**
 * Copyright (C) Elitecore Technologies Ltd.
 * IDataClassGenrator.java
 * Created on Sep 28, 2007
 * Last Modified on 
 * @author : kaushikvira
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

public class IDataClassGenrator extends BaseJavaCodeGen {

	 private static String prefix       = "com.elitecore";
	    
	    private static String classPostfix = "Data";
	    
	    public IDataClassGenrator( TableDataBean tableBean ,
	                         		String strApplicationName ,
	                         		String subModualName ,
	                         		String modualName ,
	                         		String workingDir ) {
	        
	        this.applicationName = strApplicationName;
	        this.subModualName = subModualName.replace(File.separator, ".");
	        this.modualName = modualName;
	        BaseCodeGen.tableBean = tableBean;
	        
	        if (subModualName.equals(""))
	            this.packageName = prefix + "." + this.applicationName.toLowerCase() + ".datamanager." + modualName.toLowerCase() + ".data";
	        else this.packageName =prefix + "." + this.applicationName.toLowerCase() + ".datamanager." + subModualName.toLowerCase() + "." + modualName.toLowerCase() + ".data";
	        
	        this.className = "I"+initCap(tableBean.getStrTableName().substring(4)) + classPostfix;
	        this.realPath = workingDir;
	        this.log = Logger.getLogger(DataClassGen.class);
	    }
	    
	    @Override
	    public void genrateFile() throws FileNotFoundException {
	        
	        this.writer = getPrintWritter();
	        genrateBaseComment();
	        
	        this.writer.println("package " + this.packageName + ";");
	        this.writer.println("   ");
	        
	        this.writer.println("public interface " + this.className + "{");
	        this.writer.println("");
	        
	        Iterator itColumnList = BaseCodeGen.tableBean.getCloumnList().iterator();
	        
	        this.writer.println("");
	        this.writer.println("");
	        this.writer.println("");
	        itColumnList = BaseCodeGen.tableBean.getCloumnList().iterator();
	        
	        while (itColumnList.hasNext()) {
	            ColumnBean colBean = (ColumnBean) itColumnList.next();
	            if (colBean.isSelectionStaus()) {
	                this.writer.println("    public " + colBean.getJavaType() + " get" + initCap(colBean.getJavaName()) + "();");
	                this.writer.println("	public void set" + initCap(colBean.getJavaName()) + "(" + colBean.getJavaType() + " " + colBean.getJavaName() + ");");
	                this.writer.println("");
	            }
	        }
	        this.writer.println("}");
	        this.writer.close();
	    }
}
