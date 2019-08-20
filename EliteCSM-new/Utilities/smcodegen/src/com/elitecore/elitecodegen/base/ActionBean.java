/**
 * Copyright (C) Elitecore Technologies Ltd.
 * ActionBean.java
 * Created on Jul 27, 2007
 * Last Modified on 
 * @author : kaushik vira
 */
package com.elitecore.elitecodegen.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.controller.form.PackageGenarationForm;

/**
 * @author kaushikvira
 */
public class ActionBean {
    
    private String  actionClassPath;
    private String  formClassPath;
    private String  formClassName;
    private String  AcitonClassName;
    private String  formName;
    private String  path;
    private String  formClass;
    private String  type;
    private String  scope;
    private boolean validate;
    private List    forward;
    
    public class Forward {
        
        private String name;
        private String path;
        
        public Forward( String name ,
                        String path ) {
            this.name = name;
            this.path = path;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName( String name ) {
            this.name = name;
        }
        
        public String getPath() {
            return this.path;
        }
        
        public void setPath( String path ) {
            this.path = path;
        }
        
    }
    
    private static Logger log = Logger.getLogger(ActionBean.class);
    
    public String initCap( String name ) {
        return String.valueOf(name.charAt(0)).toUpperCase().concat(name.substring(1));
    }
    
    public String initsmall( String name ) {
        return String.valueOf(name.charAt(0)).toLowerCase().concat(name.substring(1));
    }
    
    public String getPath( String name ) {
        return name.replace(".", "/");
    }
    
    public ActionBean( PackageGenarationForm pakageForm ,TableDataBean tableDataBean, String actionPreFix ) {
        
        this.path = "/" + initsmall(actionPreFix) + initCap(tableDataBean.getStrTableName().substring(4));
        this.formName = initsmall(actionPreFix) +initCap(tableDataBean.getStrTableName().substring(4)) + "Form";
        this.scope = "request";
        this.validate = false;
        
        if (pakageForm.getStrSubMoudalName().equals(""))
            this.actionClassPath = EliteCodeGenConstant.ClassPrefix + "." + pakageForm.getStrApplicationName().toLowerCase() + ".web." + pakageForm.getStrModualName().toLowerCase();
        else this.actionClassPath = EliteCodeGenConstant.ClassPrefix + "." + pakageForm.getStrApplicationName().toLowerCase() + ".web." + pakageForm.getStrSubMoudalName().toLowerCase() + "." + pakageForm.getStrModualName().toLowerCase();
        
        if (pakageForm.getStrSubMoudalName().equals(""))
            this.formClassPath = EliteCodeGenConstant.ClassPrefix + "." + pakageForm.getStrApplicationName().toLowerCase() + ".web." + pakageForm.getStrModualName().toLowerCase() + "." + "forms";
        else this.formClassPath =
                EliteCodeGenConstant.ClassPrefix + "." + pakageForm.getStrApplicationName().toLowerCase() + ".web." + pakageForm.getStrSubMoudalName().toLowerCase() + "." + pakageForm.getStrModualName().toLowerCase() + "." + "forms";
        
        this.forward = new ArrayList<Forward>();
        this.formClassName = initCap(actionPreFix) + initCap(tableDataBean.getStrTableName().substring(4)) + "Form";
        this.AcitonClassName = initCap(actionPreFix) + initCap(tableDataBean.getStrTableName().substring(4)) + "Action";
        this.formClass = this.formClassPath + "." + this.formClassName;
        this.type = this.actionClassPath + "." + this.AcitonClassName;
        
        if (actionPreFix.startsWith("Init")) {
            
            if (pakageForm.getStrSubMoudalName().equals(""))
                this.forward.add(new Forward(initsmall(actionPreFix) +  initCap(pakageForm.getStrModualName()), "/jsp/" + pakageForm.getStrModualName().toLowerCase() + "/" + initCap(actionPreFix.substring(4))
                        +initCap(tableDataBean.getStrTableName().substring(4)) + ".jsp"));
            else this.forward.add(new Forward(initsmall(actionPreFix) + initCap(pakageForm.getStrModualName()), "/jsp/" + getPath(pakageForm.getStrSubMoudalName()).toLowerCase() + "/" + pakageForm.getStrModualName().toLowerCase() + "/"
                    + initCap(actionPreFix.substring(4)) +initCap(tableDataBean.getStrTableName().substring(4)) + ".jsp"));
            
        } else if (actionPreFix.startsWith("misc")) {
            
            if (pakageForm.getStrSubMoudalName().equals(""))
                this.forward.add(new Forward(initsmall(actionPreFix) +  initCap(pakageForm.getStrModualName()), "/jsp/" + pakageForm.getStrModualName().toLowerCase() + "/Search.do"));
            else this.forward.add(new Forward(initsmall(actionPreFix) +  initCap(pakageForm.getStrModualName()), "/jsp/" + getPath(pakageForm.getStrSubMoudalName()).toLowerCase() + "/" + pakageForm.getStrModualName().toLowerCase() + "/Search.do"));
            
        } else if (pakageForm.getStrSubMoudalName().equals(""))
            this.forward.add(new Forward(initsmall(actionPreFix) +  initCap(pakageForm.getStrModualName()), "/jsp/" + pakageForm.getStrModualName().toLowerCase() + "/" + actionPreFix +initCap(tableDataBean.getStrTableName().substring(4)) + ".jsp"));
        else this.forward.add(new Forward(initsmall(actionPreFix) +  initCap(pakageForm.getStrModualName()), "/jsp/" + getPath(pakageForm.getStrSubMoudalName()).toLowerCase() + "/" + pakageForm.getStrModualName().toLowerCase() + "/" + initCap(actionPreFix)
                + initCap(tableDataBean.getStrTableName().substring(4)) + ".jsp"));
        
        ActionBean.log.info("action class path :" + getActionClassPath());
        ActionBean.log.info("form calss path :" + getFormClassPath());
        ActionBean.log.info("form class name : " + this.formClass);
        ActionBean.log.info("form name  :" + this.formName);
        ActionBean.log.info("form path  :" + this.path);
        ActionBean.log.info("Action class type :" + this.type);
        ActionBean.log.info("action scope  :" + this.scope);
        ActionBean.log.info("forward Name: " + ((Forward) (this.forward.get(0))).name);
        ActionBean.log.info("forward Name: " + ((Forward) (this.forward.get(0))).path);
        
    }
    
    public String getActionClassPath() {
        return this.actionClassPath.replace(".", File.separator);
    }
    
    public String getFormClassPath() {
        return this.formClassPath.replace(".", File.separator);
    }
    
    public String getFormName() {
        return this.formName;
    }
    
    public void setFormName( String formName ) {
        this.formName = formName;
    }
    
    public String getFormClass() {
        return this.formClass;
    }
    
    public void setFormClass( String formClass ) {
        this.formClass = formClass;
    }
    
    public List getForward() {
        return this.forward;
    }
    
    public void setForward( List forward ) {
        this.forward = forward;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath( String path ) {
        this.path = path;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public void setScope( String scope ) {
        this.scope = scope;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType( String type ) {
        this.type = type;
    }
    
    public boolean isValidate() {
        return this.validate;
    }
    
    public void setValidate( boolean validate ) {
        this.validate = validate;
    }
    
    public String getAcitonClassName() {
        return this.AcitonClassName;
    }
    
    public void setAcitonClassName( String acitonClassName ) {
        this.AcitonClassName = acitonClassName;
    }
    
    public String getFormClassName() {
        return this.formClassName;
    }
    
    public void setFormClassName( String formClassName ) {
        this.formClassName = formClassName;
    }
    
}
