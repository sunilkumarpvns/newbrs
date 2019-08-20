/**
 * Copyright (C) Elitecore Technologies Ltd.
 * BLManagerGen.java
 * Created on Jul 27, 2007
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
public class BLManagerGen extends BaseJavaCodeGen {
    
    private static String prefix       = "com.elitecore";
    
    private static String classPostfix = "BLManager";
    
    public BLManagerGen( TableDataBean tableBean ,
                         String strApplicationName ,
                         String subModualName ,
                         String modualName ,
                         String workingDir ) {
        this.applicationName = strApplicationName;
        this.subModualName = subModualName.replace(File.separator, ".");
        this.modualName = modualName;
        BaseCodeGen.tableBean = tableBean;
        
        if (subModualName.equals(""))
            this.packageName = BLManagerGen.prefix + "." + this.applicationName.toLowerCase() + ".blmanager." + modualName.toLowerCase();
        else this.packageName = BLManagerGen.prefix + "." + this.applicationName.toLowerCase() + ".blmanager." + subModualName.toLowerCase() + "." + modualName.toLowerCase();
        
        this.className = initCap(this.modualName) + BLManagerGen.classPostfix;
        this.realPath = workingDir;
        this.log = Logger.getLogger(BLManagerGen.class);
    }
    
    @Override
    public void genrateFile() throws FileNotFoundException {
        
        this.writer = getPrintWritter();
        genrateBaseComment();
        this.writer.println("package " + this.packageName + ";");
        this.writer.println("");
        
        if (this.subModualName.equals(""))
            this.writer.println("import " + BLManagerGen.prefix + "." + this.applicationName.toLowerCase() + ".datamanager." + this.modualName.toLowerCase() + "." + initCap(this.modualName) + "DataManager;");
        else this.writer.println("import " + BLManagerGen.prefix + "." + this.applicationName.toLowerCase() + ".datamanager." + this.subModualName.toLowerCase() + "." + this.modualName.toLowerCase() + "." + initCap(this.modualName)
                + "DataManager;");
        
        this.writer.println("import " + BLManagerGen.prefix + "." + this.applicationName.toLowerCase() + ".blmanager.core.base.BaseBLManager;");
        this.writer.println("import " + BLManagerGen.prefix + "." + this.applicationName.toLowerCase() + ".datamanager.core.system.util.IDataManagerSession;");
        this.writer.println("import " + BLManagerGen.prefix + "." + this.applicationName.toLowerCase() + ".blmanager.core.system.util.DataManagerFactory;");
        this.writer.println("");
        this.writer.println("public class " + this.className + " extends BaseBLManager {");
        this.writer.println("	private static final String MODULE = \"" + this.modualName.toUpperCase() + "\";");
        this.writer.println("");
        this.writer.println("	/**");
        this.writer.println("	 * @return Returns Data Manager instance for " + initCap(this.modualName) + " data.");
        this.writer.println("	 */");
        this.writer.println("	public " + initCap(this.modualName) + "DataManager get" + initCap(this.modualName) + "DataManager(IDataManagerSession session) { ");
        this.writer.println("		" + initCap(this.modualName) + "DataManager " + this.modualName + "DataManager = (" + initCap(this.modualName) + "DataManager) DataManagerFactory ");
        this.writer.println("				.getInstance().getDataManager(" + initCap(this.modualName) + "DataManager.class, session);");
        this.writer.println("		return " + this.modualName + "DataManager;");
        this.writer.println("	}");
        this.writer.println("}");
        this.writer.close();
    }
}
