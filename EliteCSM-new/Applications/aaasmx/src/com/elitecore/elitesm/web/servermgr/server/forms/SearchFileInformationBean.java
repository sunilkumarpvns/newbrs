/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchFileInformationBean.java                             
 * ModualName                                     
 * Created on jan 10, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server.forms;

import java.io.File;

/**
 * @author kaushikvira
 *
 */
public class SearchFileInformationBean {
    
    private String fileName;
    private String filePath;
    private Long   fileSize;
    
    public String getFileName( ) {
        
        return fileName;
    }
    
    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }
    
    public String getFilePath( ) {
        return filePath;
    }
    
    public void setFilePath( String filePath ) {
        this.filePath = filePath;
    }
    
    public Long getFileSize( ) {
        return fileSize;
    }
    
    public void setFileSize( Long fileSize ) {
        this.fileSize = fileSize;
    }
    
/*    public void FindFileSize( ) {
        String fileLength = String.valueOf(fileSize);
        int fileLengthDigitCount = fileLength.length();
        double fileLengthLong = f.length();
        double decimalVal = 0.0;
        String howBig = "";
        
        System.out.println("fileLengthDigitCount is..." + fileLengthDigitCount);
        
        if (f.length() > 0) {
            if (fileLengthDigitCount < 5) {
                fileSizeKB = Math.abs(fileLengthLong);
                howBig = "Byte(s)";
            } else if (fileLengthDigitCount >= 5 && fileLengthDigitCount <= 6) {
                fileSizeKB = Math.abs((fileLengthLong / 1024));
                howBig = "KB";
            } else if (fileLengthDigitCount >= 7 && fileLengthDigitCount <= 9) {
                fileSizeKB = Math.abs(fileLengthLong / (1024 * 1024));
                howBig = "MB";
            } else if (fileLengthDigitCount > 9) {
                fileSizeKB = Math.abs((fileLengthLong / (1024 * 1024 * 1024)));
                decimalVal = fileLengthLong % (1024 * 1024 * 1024);
                howBig = "GB";
            }
        }
        System.out.println("....bytes....." + fileSizeKB);
        String finalResult = getRoundedValue(fileSizeKB);
        System.out.println("\n....Final Result....." + finalResult + " " + howBig);
    }*/
    
}
