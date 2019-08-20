/**
 * 
 */
package com.elitecore.elitecodegen.genrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.base.ActionBean;
import com.elitecore.elitecodegen.base.ColumnBean;
import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.controller.form.PackageGenarationForm;
import com.elitecore.elitecodegen.genrator.base.BaseCodeGen;
import com.elitecore.elitecodegen.genrator.base.BaseJavaCodeGen;

/**
 * @author kaushikvira
 */
public class UtilClassGen extends BaseJavaCodeGen {
    
    public UtilClassGen( TableDataBean tableBean , PackageGenarationForm packageForm ,String workingDir ) {
        
        this.applicationName = packageForm.getStrApplicationName();
        this.subModualName = packageForm.getStrSubMoudalName().replace(File.separator, ".");
        this.modualName = "";
        BaseCodeGen.tableBean = tableBean;
        this.className = "UtilClass";
        this.realPath = workingDir;
        this.log = Logger.getLogger(UtilClassGen.class);
    }
    
    public PrintWriter getPrintWritter( ) throws FileNotFoundException {
        
        File f = new File(this.realPath + File.separator + "util");
        if (!f.exists()) {
            this.log.info("Dir is not Exists...");
            if (f.mkdirs()) {
                this.log.info("Dir is Creadted..." + f.getAbsolutePath());
                return new PrintWriter(new FileOutputStream(new File(this.realPath + File.separator + "util" + File.separator + this.className + this.fileExt)));
            }
        }
        this.log.info("Dir is Exit " + f.getAbsolutePath());
        
        return new PrintWriter(new FileOutputStream(new File(this.realPath + File.separator + "util" + File.separator + this.className + this.fileExt)));
        
    }
    
    public void genrateFile( ) throws FileNotFoundException {
        
        this.writer = getPrintWritter();
        ColumnBean colBean = null;
        writer.println("  public void formToBean(XYZ xyz,ABC abc) {");
        writer.println("  Date currentDate = new Date(); {");
        Iterator itColumnList = tableBean.getCloumnList().iterator();
        while (itColumnList.hasNext()) {
            colBean = (ColumnBean) itColumnList.next();
            if (colBean.isSelectionStaus()) {
                writer.println("xyz.set" + initCap(colBean.getJavaName()) + "(abc.get" + initCap(colBean.getJavaName()) + ")");
            }
        }
        
        this.writer.println("}");
        writer.println("  public void beanToForm(ABC abc,XYZ xyz) {");
        writer.println("  Date currentDate = new Date(); {");
        itColumnList = tableBean.getCloumnList().iterator();
        while (itColumnList.hasNext()) {
            colBean = (ColumnBean) itColumnList.next();
            if (colBean.isSelectionStaus()) {
                writer.println("abc.set" + initCap(colBean.getJavaName()) + "(xyz.get" + initCap(colBean.getJavaName()) + ")");
            }
        }
        this.writer.println("}");
        this.writer.close();
    }
}
