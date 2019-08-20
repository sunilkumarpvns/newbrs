package com.elitecore.elitecodegen.genrator.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.base.TableDataBean;

abstract public class BaseCodeGen {
    
    protected String               fileExt;
    
    protected static TableDataBean tableBean;
    
    protected String               modualName;
    
    protected String               subModualName;
    
    protected PrintWriter          writer;
    
    protected String               applicationName;
    
    protected String               realPath;
    
    protected Logger               log;
    
    abstract public PrintWriter getPrintWritter() throws FileNotFoundException;
    
    abstract public void genrateFile() throws FileNotFoundException;
    
    public static void setTableBean( TableDataBean tableBean ) {
        BaseCodeGen.tableBean = tableBean;
    }
    
    public String initCap( String name ) {
        return String.valueOf(name.charAt(0)).toUpperCase().concat(name.substring(1));
    }
    
    public String initSmall( String name ) {
        return String.valueOf(name.charAt(0)).toLowerCase().concat(name.substring(1));
    }
    
    public String getFileExt() {
		return fileExt;
	}
    
public String convertToPath( String name ) {
        
        return name.replace(".", File.separator);
    }
    
    public String convertToPackage( String name ) {
        
        return name.replace(File.separator, ".");
    }
}
