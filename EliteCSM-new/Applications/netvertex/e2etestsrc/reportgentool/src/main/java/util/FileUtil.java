package util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static boolean checkIfDirectoryExists(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }

    public static boolean checkIfFileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void copyNetverexData(String source, String destination){
        File sourceDir = new File(source);
        File netvertexLogs = new File(destination+File.separator+"netvertex");

        if(netvertexLogs.exists()==false){
            netvertexLogs.mkdir();
        }

        try {
            for(File file:sourceDir.listFiles()){
                if(file.isDirectory()){
                    File newDir = new File(destination+File.separator+"netvertex"+File.separator+file.getName());
                    FileUtils.copyDirectory(file,newDir);
                } else {
                    File newDir = new File(destination+File.separator+file.getName());
                    FileUtils.copyFile(file,newDir);
                }
            }
        } catch (IOException ex){
            System.out.println("Exception: "+ex.getMessage());
        }
    }

    public static void copyMappingAndResultFile(String source, String destination){
        File sourceFile = new File(source);
        File csvDir = new File(destination+File.separator+"csv");

        sourceFile = new File(sourceFile.getParent());

        if(csvDir.exists()==false){
            csvDir.mkdir();
        }

        try {
            for(File file:sourceFile.listFiles()){
                if(file.isDirectory()){
                    continue;
                } else {
                    File newDir = new File(csvDir+File.separator+file.getName());
                    FileUtils.copyFile(file,newDir);
                }
            }
        } catch (IOException ex){
            System.out.println("Exception: "+ex.getMessage());
        }
    }

    public static void copySMData(String source, String destination){
        File sourceDir = new File(source);
        File smLogs = new File(destination+File.separator+"server_manager");

        if(smLogs.exists()==false){
            smLogs.mkdir();
        }

        try {
            for(File file:sourceDir.listFiles()){
                if(file.isDirectory()){
                    File newDir = new File(destination+File.separator+"server_manager"+File.separator+file.getName());
                    FileUtils.copyDirectory(file,newDir);
                } else {
                    File newFile = new File(destination+File.separator+file.getName());
                    FileUtils.copyFile(file,newFile);
                }
            }
        } catch (IOException ex){
            System.out.println("Exception: "+ex.getMessage());
        }
    }
}
