/**
 * Copyright (C) Elitecore Technologies Ltd.
 * ApplicationResourceCodeGen.java
 * Created on Jul 31, 2007
 * Last Modified on 
 * @author : kaushikvira
 */
package com.elitecore.elitecodegen.genrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.base.ColumnBean;
import com.elitecore.elitecodegen.base.StrutsBean;
import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.controller.form.PackageGenarationForm;
import com.elitecore.elitecodegen.genrator.base.BaseApplicationResourceCodeGen;
import com.elitecore.elitecodegen.genrator.base.BaseCodeGen;
import com.elitecore.elitecodegen.genrator.base.BaseXmlCodeGen;

public class ApplicationResourceCodeGen extends BaseApplicationResourceCodeGen {
    
    private static String filePrefix = "com" + File.separator + "elitecore";
    
    public ApplicationResourceCodeGen( StrutsBean strutsBean ,
                                       TableDataBean tableBean ,
                                       PackageGenarationForm packageform ,
                                       String workingDir ) {
        
        this.log = Logger.getLogger(ApplicationResourceCodeGen.class);
        this.baseModualName = strutsBean.getBaseModualName();
        this.modualName = packageform.getStrModualName();
        this.filePath = ApplicationResourceCodeGen.filePrefix + File.separator + packageform.getStrApplicationName() + File.separator + "web" + File.separator + strutsBean.getBaseModualName();
        this.fileName = strutsBean.getApplicationResource();
        
        log.info("filepath" + this.filePath);
        log.info("FileName" + this.fileName);
        
        this.realPath = workingDir;
        this.tableBean = tableBean;
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.elitecore.elitecodegen.genrator.base.BaseCodeGen#genrateFile()
     */
    @Override
    public void genrateFile() throws FileNotFoundException {
        this.writer = getPrintWritter();
        log.info("Application:genrateFile() called");
        Iterator itColumnList = tableBean.getCloumnList().iterator();
        
        while (itColumnList.hasNext()) {
            
            ColumnBean colBean = (ColumnBean) itColumnList.next();
            if (colBean.isSelectionStaus())
                
                this.writer.println(this.baseModualName.toLowerCase() + "." + this.modualName.toLowerCase() + "." + colBean.getName().toLowerCase() + "=" + colBean.getJavaName());
        }
        
        this.writer.close();
        
    }
    
}
