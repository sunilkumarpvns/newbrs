/**
 * Copyright (C) Elitecore Technologies Ltd.
 * StrutsConfigGen.java
 * Created on Jul 27, 2007
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

import com.elitecore.elitecodegen.base.ActionBean;
import com.elitecore.elitecodegen.base.StrutsBean;
import com.elitecore.elitecodegen.base.ActionBean.Forward;
import com.elitecore.elitecodegen.controller.form.PackageGenarationForm;
import com.elitecore.elitecodegen.genrator.base.BaseXmlCodeGen;

public class StrutsConfigGen extends BaseXmlCodeGen {
    
    private StrutsBean strutsBean;
    private String strApplicationName;
    
        
    private void genrateHeader() {
        this.writer.println("<?xml version='1.0' encoding='UTF-8'?>                                 ");
        this.writer.println("<!DOCTYPE hibernate-mapping                                            ");
        this.writer.println("    PUBLIC \"-//Hibernate/Hibernate Mapping DTD//EN\"                    ");
        this.writer.println("          \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">");
    }
    
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
                return new PrintWriter(new FileOutputStream(new File(this.realPath + File.separator + "webroot" + File.separator + this.filePath + File.separator + this.fileName + fileExt)));
            }
        }
        this.log.info("Dir is Exit " + f.getAbsolutePath());
        
        return new PrintWriter(new FileOutputStream(new File(this.realPath + File.separator + "webroot" + File.separator + this.filePath + File.separator + this.fileName + fileExt)));
    }
    
    public StrutsConfigGen( StrutsBean strutsBean ,
                            PackageGenarationForm packageform ,
                            String workingDir ) {
        
    	this.fileExt = ".xml";
        this.filePath = "WEB-INF" + File.separator + "config" + File.separator + strutsBean.getBaseModualName();
        this.fileName = "struts-config" + "-" + strutsBean.getBaseModualName();
        this.realPath = workingDir;
        this.log = Logger.getLogger(HibernateCfgXmlGen.class);
        this.strutsBean = strutsBean;
        this.applicationName = packageform.getStrApplicationName();
    }
    
    @Override
    public void genrateFile() throws FileNotFoundException {
        
        writer = getPrintWritter();
        genrateHeader();
        
        writer.println("<struts-config>");
        writer.println("    <data-sources />");
        writer.println("");
        writer.println("    <form-beans>");
        
        Iterator itActionList = strutsBean.getLstAction().iterator();
        
        while (itActionList.hasNext()) {
            ActionBean actionBean = (ActionBean) itActionList.next();
            writer.println("      <form-bean name=\"" + actionBean.getFormName() + "\" type=\"" + actionBean.getFormClass() + "\" /> ");
        }
        
        writer.println("    </form-beans>");
        writer.println("");
        writer.println("    <global-exceptions />");
        writer.println("");
        writer.println("    <global-forwards />     ");
        writer.println("                   ");
        writer.println("    <action-mappings> ");
        
        itActionList = strutsBean.getLstAction().iterator();
        
        while (itActionList.hasNext()) {
            ActionBean actionBean = (ActionBean) itActionList.next();
            
            writer.println("");
            writer.println("      <action path=\"" + actionBean.getPath() + "\"  ");
            writer.println("          type=\"" + actionBean.getType() + "\"   ");
            writer.println("          name=\"" + initSmall(actionBean.getFormName()) + "\"     ");
            writer.println("          validate=\"" + actionBean.isValidate() + "\">  ");
            
            Iterator itForward = actionBean.getForward().iterator();
            
            while (itForward.hasNext()) {
                Forward forwardOb = (Forward) itForward.next();
                writer.println("        <forward name=\"" + forwardOb.getName() + "\" path=\"" + forwardOb.getPath() + "\" />");
            }
            writer.println("     </action>  ");
            writer.println(" ");
        }
        writer.println("   </action-mappings>  ");
        writer.println("");
        
        writer.println("<message-resources parameter=\""+ StrutsConfigGen.prefixPath.replace(File.separatorChar,'.') +"."+ this.applicationName.toLowerCase() +".web."+ strutsBean.getApplicationResource()+"\" />");
        writer.println("");
        writer.println("</struts-config> ");
        
        
        
        writer.close();
        
    }
}
