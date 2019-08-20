/**
 * Copyright (C) Elitecore Technologies Ltd.
 * InterfaceDataManager.java
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

/**
 * @author kaushikvira
 */
public class InterfaceDataManagerGen extends BaseJavaCodeGen {
    
    private static String prefix       = "com.elitecore";
    private static String classPostfix = "DataManager";
    
    public InterfaceDataManagerGen( TableDataBean tableBean ,
                                    String strApplicationName ,
                                    String subModualName ,
                                    String modualName ,
                                    String workingDir ) {
        
        this.applicationName = strApplicationName;
        this.modualName = modualName;
        this.subModualName = subModualName.replace(File.separator, ".");
        BaseCodeGen.tableBean = tableBean;
        
        if (subModualName.equals(""))
            this.packageName = InterfaceDataManagerGen.prefix + "." + this.applicationName.toLowerCase() + "." + "datamanager" + "." + modualName.toLowerCase();
        else this.packageName = InterfaceDataManagerGen.prefix + "." + this.applicationName.toLowerCase() + "." + "datamanager" + "." + subModualName.toLowerCase() + "." + modualName.toLowerCase();
        
        this.className = initCap(modualName) + InterfaceDataManagerGen.classPostfix;
        this.realPath = workingDir;
        this.log = Logger.getLogger(InterfaceDataManagerGen.class);
    }
    
    @Override
    public void genrateFile() throws FileNotFoundException {
        
        this.writer = getPrintWritter();
        genrateBaseComment();
        this.writer.println("package " + this.packageName + ";");
        this.writer.println("");
        this.writer.println("import com.elitecore."+ applicationName.toLowerCase() +".datamanager.core.base.DataManager;");
        this.writer.println("");
        this.writer.println("public interface " + this.className + " extends DataManager{");
        this.writer.println("");
        this.writer.println("");
        this.writer.println("}");
        this.writer.close();
    }
    
}
