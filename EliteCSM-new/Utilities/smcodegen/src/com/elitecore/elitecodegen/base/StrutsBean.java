/**
 * Copyright (C) Elitecore Technologies Ltd.
 * StrutsBean.java
 * Created on Jul 27, 2007
 * Last Modified on 
 * @author : kaushik vira
 */
package com.elitecore.elitecodegen.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.controller.form.PackageGenarationForm;

/**
 * @author kaushikvira
 */
public class StrutsBean {
    
    private String        fileName;
    
    private String        filePath;
    
    private List          lstAction;
    
    private String        baseModualName;
    
    private String        applicationResource;
    
    private static String applicationResourcePostfix = "Resources";
    
    private TableDataBean tableDataBean;
    
    public  List           actionPreFix;
    
    private static Logger log                        = Logger.getLogger(StrutsBean.class);
    
    public String initCap( String name ) {
        return String.valueOf(name.charAt(0)).toUpperCase().concat(name.substring(1));
    }
    
    public ActionBean getActionBean( String name ) {
        
        Iterator<ActionBean> itAction = getLstAction().iterator();
        
        while (itAction.hasNext()) {
            ActionBean actionBean = (ActionBean) itAction.next();
            
            if (actionBean.getFormName().equals(name)) { return actionBean; }
        }
        return null;
    }
    
    public List getLstAction() {
        return this.lstAction;
    }
    
    public void setLstAction( List lstAction ) {
        this.lstAction = lstAction;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return this.filePath;
    }
    
    public void setFilePath( String filePath ) {
        this.filePath = filePath;
    }
    
    public TableDataBean getTableDataBean() {
        return this.tableDataBean;
    }
    
    public void setTableDataBean( TableDataBean tableDataBean ) {
        this.tableDataBean = tableDataBean;
    }
    
    public String getApplicationResource() {
        return applicationResource;
    }
    
    public void setApplicationResource( String applicationResource ) {
        this.applicationResource = applicationResource;
    }
    
    public String getBaseModualName() {
        return baseModualName;
    }
    
    public void setBaseModualName( String baseModualName ) {
        this.baseModualName = baseModualName;
    }
    
    public StrutsBean( TableDataBean tableDataBean ,
                       PackageGenarationForm packageForm ,
                       String workingDir ) {
        
        if (packageForm.getStrSubMoudalName().equals("")) {
            this.baseModualName = packageForm.getStrModualName();
        } else {
            StringTokenizer name = new StringTokenizer(packageForm.getStrSubMoudalName().replace('/', '.'));
            this.baseModualName = name.nextToken(".");
            log.info("BaseModualName" + this.baseModualName);
        }
        
        this.applicationResource = this.baseModualName + StrutsBean.applicationResourcePostfix;
        log.info("Application Resource File" + this.applicationResource);
        
        this.lstAction = new ArrayList<ActionBean>();
        this.actionPreFix = new ArrayList<String>();
        
        if (packageForm.getJspRadiol() == 1) {
            this.actionPreFix.add("InitSearch");
            this.actionPreFix.add("Search");
            this.actionPreFix.add("InitCreate");
            this.actionPreFix.add("Create");
            this.actionPreFix.add("InitUpdate");
            this.actionPreFix.add("Update");
            this.actionPreFix.add("Misc");
        } else {
            this.actionPreFix.add("InitList");
            this.actionPreFix.add("List");
            this.actionPreFix.add("InitCreate");
            this.actionPreFix.add("Create");
            this.actionPreFix.add("InitUpdate");
            this.actionPreFix.add("Update");
            this.actionPreFix.add("Misc");
        }
        
        StrutsBean.log.info("Struts Bean Constructor called");
        this.tableDataBean = tableDataBean;
        this.lstAction = new ArrayList<ActionBean>();
        StrutsBean.log.info("action list genrated ");
        
        Iterator itActionList = this.actionPreFix.iterator();
        
        while (itActionList.hasNext()) {
            String strActionPreFix = (String) itActionList.next();
            this.lstAction.add(new ActionBean(packageForm,tableDataBean,strActionPreFix));
        }
    }
}
