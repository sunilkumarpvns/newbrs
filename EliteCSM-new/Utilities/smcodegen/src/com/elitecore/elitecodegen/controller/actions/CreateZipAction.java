/**
 * Copyright (C) Elitecore Technologies Ltd.
 * CreateZipAction.java
 * Created on Jul 24, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.controller.actions;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitecodegen.base.EliteCodeGenConstant;

/**
 * @author kaushikvira
 */
public class CreateZipAction extends Action {
    
    private static Logger log = Logger.getLogger(CreateZipAction.class);
    
    @Override
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        ActionErrors errors = new ActionErrors();
        try {
            CreateZipAction.log.info("CreateZipAction Called");
            ServletOutputStream out = null;
            response.setHeader("Content-Disposition", "attachment;filename=\"src.zip");
            out = response.getOutputStream();
            ZipOutputStream zos = new ZipOutputStream(out);
            
            String workingDir = (String) request.getSession(false).getAttribute(EliteCodeGenConstant.WorkingDir);
            // call the zipDir method
            zipDir(workingDir, zos);
            // close the stream
            zos.close();
        }
        catch (Exception e) {
            log.info("Exceptin in creation of zip");
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.enable.connect"));
            saveErrors(request, errors);
            return null;
        }
        log.info("Forward to succss");
        return null;
    }
    
    public void zipDir( String dir2Zip ,
                        ZipOutputStream zos ) {
        try {
            // create a new File object based on the directory we have to zip
            // File
            
            File zipDir = new File(dir2Zip);
            // get a listing of the directory content
            
            CreateZipAction.log.info("zipDir" + zipDir.getPath());
            
            String[] dirList = zipDir.list();
            byte[] readBuffer = new byte[2156];
            int bytesIn = 0;
            // loop through dirList, and zip the files
            for ( int i = 0; i < dirList.length; i++ ) {
                
                CreateZipAction.log.info("dir Name" + dirList[i]);
                File f = new File(zipDir, dirList[i]);
                if (f.isDirectory()) {
                    // if the File object is a directory, call this
                    // function again to add its content recursively
                    String filePath = f.getPath();
                    zipDir(filePath, zos);
                    // loop again
                    continue;
                }
                // if we reached here, the File object f was not a directory
                
                // create a FileInputStream on top of f
                FileInputStream fis = new FileInputStream(f);
                // create a new zip entry
                ZipEntry anEntry = new ZipEntry(f.getPath());
                // place the zip entry in the ZipOutputStream object
                zos.putNextEntry(anEntry);
                // now write the content of the file to the ZipOutputStream
                while ((bytesIn = fis.read(readBuffer)) != -1)
                    zos.write(readBuffer, 0, bytesIn);
                // close the Stream
                fis.close();
            }
        }
        catch (Exception e) {
            
            CreateZipAction.log.info("Exception", e);
        }
        
    }
    
}
