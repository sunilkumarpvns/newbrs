/**
 * 
 */
package com.elitecore.core.util.mbean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;

/**
 * @author rakeshkachhadiya
 *
 */
public abstract class EliteFileEntityMBean implements IEliteFileEntityMBean {
    
    private static final String MODULE   = "EliteFileEntityMBean";
    
    public void saveFile( String fileName ,
                          byte[] doc ) throws Exception {
        
        String SERVERHOME = getServerHome();
        String destinationDirectory = getDestinationPath(null);
        LogManager.getLogger().info(MODULE, "SERVER HOME : " + SERVERHOME);
        if (SERVERHOME == null || doc == null || fileName == null) {
            LogManager.getLogger().error(MODULE, "Requried Arguments found null");
            throw new Exception("Requried Argument found null");
        }
        FileOutputStream fs = null;
        try {
            LogManager.getLogger().info(MODULE, "File Name : " + fileName);
            File srcDir = new File(SERVERHOME + File.separator + destinationDirectory);
            File srcFile = new File(SERVERHOME + File.separator + destinationDirectory + File.separator + fileName);
            LogManager.getLogger().info(MODULE, "File Absolute path : " + srcFile.getAbsolutePath());
            
            if (!srcDir.exists())
                srcDir.mkdir();
            
            if (srcFile.exists())
                srcFile.delete();
            
            srcFile.createNewFile();
            fs = new FileOutputStream(srcFile);
            fs.write(doc);
        }
        catch (IOException exception) {
            LogManager.getLogger().error(MODULE, "Error in File Operation");
            LogManager.getLogger().trace(MODULE, exception);
            throw new Exception("Unable Perform File operation due to Access Rights or already exists read-only file.", exception);
        }
        finally {
            try {
                if (fs != null)
                    fs.close();
            }
            catch (Exception e) {
                fs = null;
            }
            
        }
    }
    
    public List<String> readFileList( ) throws Exception {
        
        //System.out.println("In List Dictionary Method");
        
        List<String> srcFiles = new ArrayList<String>();
        //final String RADIUSHOME =System.getenv(RadiusConfConstant.SERVER_HOME);
        String sourceDirectory = getSourcePath(null);
        String SERVERHOME = getServerHome();
        LogManager.getLogger().info(MODULE, "SERVER HOME : " + SERVERHOME);
        
        if (SERVERHOME == null) {
            LogManager.getLogger().error(MODULE, "Server Home not set.");
            throw new Exception("Server Home not set.");
        }
        
        try {
            
            File srcDir = new File(SERVERHOME + File.separator + sourceDirectory);
            if (srcDir.exists()) {
                //File dictionaryFile = new File(RADIUSHOME + File.separator + RadiusConstants.DICTIONARY_DIRECTORY + File.separator + fileName);
                //System.out.println("hello");
                //System.out.println("path directory"+RADIUSHOME + File.separator + RadiusConstants.DICTIONARY_DIRECTORY);
                LogManager.getLogger().info(MODULE, "Directory path : " + srcDir.getPath());
                
                File[] files = srcDir.listFiles();
                
                for ( int i = 0; i < files.length; i++ ) {
                    if (files[i].getName().contains(".xml")) {
                        srcFiles.add(files[i].getName());
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            LogManager.getLogger().trace(MODULE, exception);
            throw new Exception("Reading File(s) list failed : Reason : " + exception.getMessage(), exception);
        }
        return srcFiles;
        
    }
    
    public void deleteFile( String fileName ) throws Exception {
        
        //System.out.println("In delete Dictionary Method");
        
        //final String RADIUSHOME =System.getenv(RadiusConfConstant.SERVER_HOME);
        String sourceDirectory = getSourcePath(null);
        String SERVERHOME = getServerHome();
        
        System.out.println(SERVERHOME);
        LogManager.getLogger().error(MODULE, "SERVER HOME : " + SERVERHOME);
        
        if (SERVERHOME == null || fileName == null) {
            LogManager.getLogger().error(MODULE, "SERVER HOME not set");
            throw new Exception("SERVER HOME not set");
        }
        try {
            //System.out.println("File Name : "+fileName);
            File srcFileToDelete = new File(SERVERHOME + File.separator + sourceDirectory + File.separator + fileName);
            
            srcFileToDelete.delete();
            
        }
        catch (Exception delexp) {
            LogManager.getLogger().trace(MODULE, delexp);
            delexp.printStackTrace();
            throw new Exception("Deleting file(s) failed : Reason : " + delexp.getMessage(), delexp);
        }
    }
    
    public byte[] readFileInByte( String fileName ) throws Exception {
        byte[] dataBytes = null;
        BufferedInputStream bistream = null; 
        try {
            
            //File file = new File(fileName);
            String sourceDirectory = getSourcePath(null);
            String SERVERHOME = getServerHome();
          
            File dictionaryFileToRead = new File(SERVERHOME + File.separator + sourceDirectory + File.separator + fileName);
            
            dataBytes = new byte[(int) dictionaryFileToRead.length()];
            
            //dictionaryFileToDelete.
            byte tempBytes[] = new byte[1024];
            bistream = new BufferedInputStream(new FileInputStream(dictionaryFileToRead)); //NOSONAR - Reason: Resources should be closed

            int readBytes = -1;
            int counter = 0;
            while((readBytes= bistream.read(tempBytes))>0){
            	System.arraycopy(tempBytes, 0, dataBytes,counter,readBytes);
            	counter = counter + readBytes;
            }
            
        } catch (Exception e) {
            throw e;
        } finally {
        	Closeables.closeQuietly(bistream);
        }
        return dataBytes;
    }
    
    public byte[] readFileInByte( String fileName, String fileGroup ) throws Exception {
        byte[] dataBytes = null;
        BufferedInputStream bistream = null;
        try {
            
            //File file = new File(fileName);
            String sourceDirectory = getSourcePath(null,fileGroup);
            String SERVERHOME = getServerHome();
          
            File dictionaryFileToRead = new File(SERVERHOME + File.separator + sourceDirectory + File.separator + fileName);
            
            dataBytes = new byte[(int) dictionaryFileToRead.length()];
            
            //dictionaryFileToDelete.
            byte tempBytes[] = new byte[1024];
            bistream = new BufferedInputStream(new FileInputStream(dictionaryFileToRead)); //NOSONAR - Reason: Resources should be closed
            
            int readBytes = -1;
            int counter = 0;
            while((readBytes= bistream.read(tempBytes))!=-1){
            	System.arraycopy(tempBytes, 0, dataBytes,counter,readBytes);
            	counter = counter + readBytes;
            }
            
        } catch (Exception e) {
            throw e;
        } finally {
        	Closeables.closeQuietly(bistream);
        }
        return dataBytes;
    }
    
    public List<String> readFileList(String fileGroup) throws Exception{
		  List<String> srcFiles = new ArrayList<String>();
	        String sourceDirectory = getSourcePath(null,fileGroup);
	        String SERVERHOME = getServerHome();
	        LogManager.getLogger().info(MODULE, "SERVER HOME :" + SERVERHOME);
	        LogManager.getLogger().info(MODULE, "File Group  :" + fileGroup);
	        if (SERVERHOME == null) {
	            LogManager.getLogger().error(MODULE, "Server Home not set.");
	            throw new Exception("Server Home not set.");
	        }
	        
	        try {
	            
	            File srcDir = new File(SERVERHOME + File.separator + sourceDirectory);
	            if (srcDir.exists()) {
	                LogManager.getLogger().info(MODULE, "Directory path : " + srcDir.getPath());
	                
	                File[] files = srcDir.listFiles();
	                
	                for ( int i = 0; i < files.length; i++ ) {
	                    if (files[i].getName().contains(".xml")) {
	                        srcFiles.add(files[i].getName());
	                    }
	                }
	            }
	        }
	        catch (Exception exception) {
	            exception.printStackTrace();
	            LogManager.getLogger().trace(MODULE, exception);
	            throw new Exception("Reading File(s) list failed : Reason : " + exception.getMessage(), exception);
	        }
	        return srcFiles;
		
	}
	public String[] getAllFileGroups(){
		return getFileGroups();
	}
	public Map<String,List<String>> readFilesFromAllFileGroups() throws Exception {
	   
	   String[] fileGroups = getAllFileGroups();
	   
	   Map<String,List<String>> map = new LinkedHashMap<String,List<String>>();
	   if(fileGroups!=null){
		   for (int i = 0; i < fileGroups.length; i++) {
			   map.put(fileGroups[i],readFileList(fileGroups[i]));
		   }
	   }
		
	   return map;	
	}
	public void saveFile(String fileName, byte[] doc, String fileGroup) throws Exception{
		String SERVERHOME = getServerHome();
		String destinationDirectory = getDestinationPath(null,fileGroup);
		LogManager.getLogger().info(MODULE, "SERVER HOME : " + SERVERHOME);
		LogManager.getLogger().info(MODULE, "File Group  : " + fileGroup);
		if (SERVERHOME == null || doc == null || fileName == null) {
			LogManager.getLogger().error(MODULE, "Requried Arguments found null");
			throw new Exception("Requried Argument found null");
		}
		FileOutputStream fs = null;
		try {
			LogManager.getLogger().info(MODULE, "File Name : " + fileName);
			File srcDir = new File(SERVERHOME + File.separator + destinationDirectory);
			if(!srcDir.exists()){
				srcDir.mkdir();
			}
			File srcFile = new File(SERVERHOME + File.separator + destinationDirectory + File.separator + fileName);
			LogManager.getLogger().info(MODULE, "File Absolute path : " + srcFile.getAbsolutePath());

			if (!srcDir.exists())
				srcDir.mkdir();

			if (srcFile.exists())
				srcFile.delete();

			srcFile.createNewFile();
			fs = new FileOutputStream(srcFile);
			fs.write(doc);
		}
		catch (IOException exception) {
			LogManager.getLogger().error(MODULE, "Error in File Operation");
			LogManager.getLogger().trace(MODULE, exception);
			throw new Exception("Unable Perform File operation due to Access Rights or already exists read-only file.", exception);
		}
		finally {
			try {
				if (fs != null)
					fs.close();
			}
			catch (Exception e) {
				fs = null;
			}

		}
	}
	public void deleteFile( String fileName ,String fileGroup) throws Exception {

		String sourceDirectory = getSourcePath(null,fileGroup);
		String SERVERHOME = getServerHome();

		System.out.println(SERVERHOME);
		LogManager.getLogger().info(MODULE, "SERVER HOME : " + SERVERHOME);
		LogManager.getLogger().info(MODULE, "File Group  : " + fileGroup);
		
		if (SERVERHOME == null || fileName == null) {
			LogManager.getLogger().error(MODULE, "SERVER HOME not set");
			throw new Exception("SERVER HOME not set");
		}
		try {
			File srcFileToDelete = new File(SERVERHOME + File.separator + sourceDirectory + File.separator + fileName);
			srcFileToDelete.delete();

		}
		catch (Exception delexp) {
			LogManager.getLogger().trace(MODULE, delexp);
			delexp.printStackTrace();
			throw new Exception("Deleting file(s) failed : Reason : " + delexp.getMessage(), delexp);
		}
	}
	public void deleteFile( String fileNames[] ,String fileGroup) throws Exception {

		String sourceDirectory = getSourcePath(null,fileGroup);
		String SERVERHOME = getServerHome();

		System.out.println(SERVERHOME);
		LogManager.getLogger().info(MODULE, "SERVER HOME : " + SERVERHOME);
		LogManager.getLogger().info(MODULE, "File Group  : " + fileGroup);
		
		if (SERVERHOME == null || fileNames == null) {
			LogManager.getLogger().error(MODULE, "SERVER HOME not set");
			throw new Exception("SERVER HOME not set");
		}
		try {
			//System.out.println("File Name : "+fileName);
			for(int fileCnt = 0 ; fileCnt<fileNames.length ; fileCnt++)
			{
				File srcFileToDelete = new File(SERVERHOME + File.separator + sourceDirectory + File.separator + fileNames[fileCnt]);
				if(srcFileToDelete.exists())
				{
					srcFileToDelete.delete();
				}
			}
		}
		catch (Exception delexp) {
	 		LogManager.getLogger().trace(MODULE, delexp);
			delexp.printStackTrace();
			throw new Exception("Deleting file(s) failed : Reason : " + delexp.getMessage(), delexp);
		}
	}
}
