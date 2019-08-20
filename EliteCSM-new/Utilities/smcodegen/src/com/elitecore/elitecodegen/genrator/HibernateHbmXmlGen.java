/**
 * Copyright (C) Elitecore Technologies Ltd.
 * HibernateHbmXmlGen.java
 * Created on Jul 27, 2007
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
import com.elitecore.elitecodegen.genrator.base.BaseXmlCodeGen;

/**
 * @author kaushikvira
 */
public class HibernateHbmXmlGen extends BaseXmlCodeGen {
    
    private static String filePostfix = "Data";
    
    
    private void genrateHeader() {
        this.writer.println("<?xml version='1.0' encoding='UTF-8'?>                                 ");
        this.writer.println("<!DOCTYPE hibernate-mapping                                            ");
        this.writer.println("    PUBLIC \"-//Hibernate/Hibernate Mapping DTD//EN\"                    ");
        this.writer.println("          \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">");
        
    }
    
    public HibernateHbmXmlGen( TableDataBean tableBean ,
                               String strApplicationName ,
                               String subModualName ,
                               String modualName ,
                               String workingDir ) {
        this.fileExt = ".hbm.xml";
        this.applicationName = strApplicationName;
        this.subModualName = subModualName.replace(File.separator, ".");
        this.modualName = modualName;
        BaseCodeGen.tableBean = tableBean;
        
        if (subModualName.equals(""))
            this.filePath = BaseXmlCodeGen.prefixPath + File.separator + this.applicationName + File.separator + "hibernate" + File.separator + modualName + File.separator + "data";
        else this.filePath =
                BaseXmlCodeGen.prefixPath + File.separator + this.applicationName + File.separator + "hibernate" + File.separator + convertToPath(subModualName) + File.separator + modualName
                        + File.separator + "data";
        
        this.fileName = initCap(tableBean.getStrTableName().substring(4)) + HibernateHbmXmlGen.filePostfix;
        this.realPath = workingDir;
        this.log = Logger.getLogger(HibernateHbmXmlGen.class);
        
    }
    
    @Override
    public void genrateFile() throws FileNotFoundException {
        
        this.writer = getPrintWritter();
        genrateHeader();
        
        this.writer.println("<hibernate-mapping>");
        this.writer.println("");
        this.writer.println("	<class");
        
        if (this.subModualName.equals(""))
            this.writer.println("            name=\"" + convertToPackage(BaseXmlCodeGen.prefixPath).toLowerCase() + "." + this.applicationName.toLowerCase() + ".datamanager." + this.modualName.toLowerCase() + ".data."
                    + initCap(tableBean.getStrTableName().substring(4)) + "Data\"");
        else this.writer.println("            name=\"" + convertToPackage(BaseXmlCodeGen.prefixPath) + "." + this.applicationName.toLowerCase() + ".datamanager." + convertToPackage(this.subModualName).toLowerCase() + "."
                + this.modualName.toLowerCase() + ".data." + initCap(tableBean.getStrTableName().substring(4)) + "Data\"");
        
        this.writer.println("		table=\"" + BaseCodeGen.tableBean.getStrTableName().toUpperCase() + "\">");
        this.writer.println("");
        
        Iterator itColumnList = BaseCodeGen.tableBean.getCloumnList().iterator();
        int i = 0;
        while (itColumnList.hasNext()) {
            ColumnBean columnBean = (ColumnBean) itColumnList.next();
            
            if (columnBean.isSelectionStaus())
                if (BaseCodeGen.tableBean.getStrPrimaryKey().equals(columnBean.getName())) {
                    
                    this.writer.println("");
                    this.writer.println("		<id name=\"" + columnBean.getJavaName() + "\" column=\"" + columnBean.getName() + "\" type=\"" + "type" + (i++) + "\">");
                    this.writer.println("			<generator");
                    this.writer.println("				class=\"" + convertToPackage(BaseXmlCodeGen.prefixPath) + "." + this.applicationName + ".hibernate.core.system.util.EliteSequenceGenerator\">");
                    this.writer.println("				<param name=\"eliteprefix\">" + "prefix" + "</param>");
                    this.writer.println("				<param name=\"elitefill\">" + "fill" + "</param>");
                    this.writer.println("				<param name=\"elitelength\">" + "length" + "</param>");
                    this.writer.println("			</generator>");
                    this.writer.println("		</id>    ");
                    this.writer.println("");
                    
                } else {
                    
                    this.writer.println("");
                    this.writer.println("		<property name=\"" + columnBean.getJavaName() + "\" type=\"" + "type" + (i++) + "\"> ");
                    this.writer.println("			<column name=\"" + columnBean.getName() + "\" sql-type=\"" + columnBean.getType() + "(" + columnBean.getSize() + ")\" not-null=\""
                            + columnBean.isNullable() + "\" /> ");
                    this.writer.println("		</property>");
                    this.writer.println("");
                    
                }
        }
        
        this.writer.println("</class>            ");
        this.writer.println("	            ");
        this.writer.println("</hibernate-mapping>");
        this.writer.close();
        
    }
    
}
