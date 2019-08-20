/**
 * Copyright (C) Elitecore Technologies Ltd.
 * HibernateCfgXmlGen.java
 * Created on Jul 27, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.genrator;

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.genrator.base.BaseXmlCodeGen;

/**
 * @author kaushikvira
 */
public class HibernateCfgXmlGen extends BaseXmlCodeGen {
    
    private static String prefixPath = "";
    
    HibernateHbmXmlGen    hibernetHbmXmlGen;
    
    private void genrateHeader() {
        this.writer.println("<?xml version='1.0' encoding='UTF-8'?>                                 ");
        this.writer.println("<!DOCTYPE hibernate-configuration PUBLIC                                            ");
        this.writer.println("    PUBLIC \"-//Hibernate/Hibernate Mapping DTD//EN\"                    ");
        this.writer.println("          \"-//Hibernate/Hibernate Configuration DTD//EN\" \"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">");
        this.writer.println("");
        this.writer.println("");
    }
    
    public HibernateCfgXmlGen( HibernateHbmXmlGen hibernetHbmXmlGen ,
                               String workingDir ) {
    	this.fileExt = ".cfg.xml";
        this.filePath = HibernateCfgXmlGen.prefixPath + File.separator + "config";
        this.fileName = "hibernate";
        this.realPath = workingDir;
        this.log = Logger.getLogger(HibernateCfgXmlGen.class);
        this.hibernetHbmXmlGen = hibernetHbmXmlGen;
    }
    
    @Override
    public void genrateFile() throws FileNotFoundException {
        
        this.writer = getPrintWritter();
        genrateHeader();
        this.writer.println("<hibernate-configuration> ");
        this.writer.println("");
        this.writer.println("<session-factory>");
        this.writer.println("");
        this.writer.println("	<mapping resource=\"" + hibernetHbmXmlGen.getFilePath().replace('\\', '/').toLowerCase() + "/" + this.hibernetHbmXmlGen.getFileName() + ".hbm.xml\"/>");
        this.writer.println("");
        this.writer.println("</session-factory> ");
        this.writer.println("");
        this.writer.println("</hibernate-configuration>");
        this.writer.close();
        
    }
}
