/**
 * Copyright (C) Elitecore Technologies Ltd.
 * HDataManagerCodeGen.java
 * Created on Jul 26, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.genrator;

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.genrator.base.BaseCodeGen;
import com.elitecore.elitecodegen.genrator.base.BaseJavaCodeGen;

public class HDataManagerCodeGen extends BaseJavaCodeGen {
    
    private static String prefix       = "com.elitecore";
    
    private static String classPrefix  = "H";
    
    private static String classPostfix = "DataManager";
    
    public HDataManagerCodeGen( TableDataBean tableBean ,
                                String strApplicationName ,
                                String subModualName ,
                                String modualName ,
                                String workingDir ) {
        
        this.applicationName = strApplicationName;
        this.modualName = modualName;
        this.subModualName = subModualName.replace(File.separator, ".");
        BaseCodeGen.tableBean = tableBean;
        
        if (subModualName.equals(""))
            this.packageName = HDataManagerCodeGen.prefix + "." + this.applicationName.toLowerCase() + "." + "hibernate."+modualName.toLowerCase();
        else this.packageName = HDataManagerCodeGen.prefix + "." + this.applicationName.toLowerCase() + "." + "hibernate" + "." + subModualName.toLowerCase()+"."+modualName.toLowerCase();
        this.className = HDataManagerCodeGen.classPrefix + initCap(modualName) + HDataManagerCodeGen.classPostfix;
        this.realPath = workingDir;
        this.log = Logger.getLogger(HDataManagerCodeGen.class);
    }
    
    @Override
    public void genrateFile() throws FileNotFoundException {
        
        this.writer = getPrintWritter();
        genrateBaseComment();
        this.writer.println("package " + this.packageName + ";");
        this.writer.println("   ");
        this.writer.println("import "+ HDataManagerCodeGen.prefix+"."+applicationName+".hibernate.core.base.HBaseDataManager;");
        
        if (this.subModualName.equals(""))
            this.writer.println("import " + HDataManagerCodeGen.prefix + "." + this.applicationName.toLowerCase() + ".datamanager" + "." + modualName.toLowerCase() + "."+ initCap(modualName) +"DataManager;");
        else this.writer.println("import " + HDataManagerCodeGen.prefix + "." + this.applicationName.toLowerCase() + ".datamanager" + "." + this.subModualName.toLowerCase() +"." + modualName.toLowerCase() + "."+ initCap(modualName) +"DataManager;");
        
        this.writer.println("      ");
        this.writer.println("public class " + this.className + " extends HBaseDataManager implements " + initCap(this.modualName) + "DataManager{");
        this.writer.println("}");
        this.writer.close();
        
    }
    
}
