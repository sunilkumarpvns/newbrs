/**
 * Copyright (C) Elitecore Technologies Ltd.
 * StrutsFormClassGen.java
 * Created on Jul 27, 2007
 * Last Modified on 
 * @author : kaushik vira
 */
package com.elitecore.elitecodegen.genrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.base.ActionBean;
import com.elitecore.elitecodegen.base.ColumnBean;
import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.controller.form.PackageGenarationForm;
import com.elitecore.elitecodegen.genrator.base.BaseCodeGen;
import com.elitecore.elitecodegen.genrator.base.BaseJavaCodeGen;

public class StrutsFormClassGen extends BaseJavaCodeGen {
    
    private static String prefix = "com.elitecore";
    
    public StrutsFormClassGen( TableDataBean tableBean ,
                               PackageGenarationForm packageForm ,
                               ActionBean actionBean ,
                               String workingDir ) {
        
        this.applicationName = packageForm.getStrApplicationName();
        this.subModualName = packageForm.getStrSubMoudalName().replace(File.separator, ".");
        this.modualName = packageForm.getStrModualName();
        BaseCodeGen.tableBean = tableBean;
        this.packageName = actionBean.getFormClassPath().replace(File.separator, ".");
        this.className = actionBean.getFormClassName();
        this.realPath = workingDir;
        this.log = Logger.getLogger(StrutsFormClassGen.class);
    }
    
    @Override
    public void genrateFile() throws FileNotFoundException {
        
        this.writer = getPrintWritter();
        genrateBaseComment();
        
        this.writer.println("package " + this.packageName.toLowerCase() + ";");
        this.writer.println("");
        this.writer.println("import com.elitecore."+ applicationName.toLowerCase() +".web.core.base.forms.BaseWebForm;");
        this.writer.println("");
        this.writer.println("public class " + this.className + " extends BaseWebForm{");
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
