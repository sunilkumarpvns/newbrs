package com.elitecore.core.commons.fileio.loactionalloactor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.util.FileUtil;

public abstract class BaseCommonFileAllocator implements IFileLocationAllocater {

	public static final String MODULE = "BASE_COMMON_FILE_ALLOCATOR";
	
	private String user;
	private String password;
	private String address;
	private int port;
	private String destinationLocation;
	protected static final String FILE_SEPARATOR = System.getProperty("file.separator");
	protected String folderSepretor;
	private String postOperatoin="archive";
	public static String UIP_EXTENSION="uip"; // Temporary Extension which will be assigned to the Files while file will be in progress for uploading. (Upload In Progress)
	private String uploadPrefix = "uploaded_";
	public static final String DELETE= "DELETE";
	public static final String RENAME= "RENAME";
	public static final String ARCHIVE= "ARCHIVE";
	public static final String POF_EXTENSION = "pof";//POST OPERATION FAILED
	public static final String STAR_WILDCARD_CHARACTER="*";
	protected String originalExtension;
	private boolean isArchiveTreeCreated = false;
	private Set<String> archiveLocationSet;


	public void initialize(String user, String password, String address, String destinationLocation, int port, String postOperation,String folderSepretor,String archiveLocations,String originalExtension) throws FileAllocatorException{
		this.user = user;
		this.password = password;
		this.address = address;
		this.port = port;
		this.destinationLocation = destinationLocation;
		
		if(postOperation != null && !postOperation.trim().isEmpty()) {
			this.postOperatoin =  postOperation;
		}
		this.folderSepretor = folderSepretor;
		this.originalExtension = originalExtension; 
		connect();
		
		if(ARCHIVE.equalsIgnoreCase(postOperation)){
			
			if(archiveLocations != null && !archiveLocations.trim().isEmpty()) {
				String[] archiveLocationsArr = archiveLocations.split("[,;]");
				for(int i=0; i<archiveLocationsArr.length; i++) {
					archiveLocationsArr[i] = archiveLocationsArr[i].trim();
				
					if(!archiveLocationsArr[i].endsWith(FILE_SEPARATOR)) {
						archiveLocationsArr[i] = archiveLocationsArr[i] + FILE_SEPARATOR;
					}
				}
				archiveLocationSet = createArchiveDirectory(archiveLocationsArr);
			}
		}

	}

	protected BaseCommonFileAllocator() {
		super();
	}
	protected String getUser() {
		return user;
	}
	protected void setUser(String user) {
		this.user = user;
	}
	protected String getPassword() {
		return password;
	}
	protected void setPassword(String password) {
		this.password = password;
	}
	protected String getAddress() {
		return address;
	}
	protected void setAddress(String address) {
		this.address = address;
	}
	protected int getPort() {
		return port;
	}
	protected void setPort(int port) {
		this.port = port;
	}
	protected String getDestinationLocation() {
		return destinationLocation;
	}
	protected void setDestinationLocation(String destinationLocation) {
		this.destinationLocation = destinationLocation;
	}
	public void setFolderSepretor(String folderSepretor) {
		this.folderSepretor = folderSepretor;
	}
	public String getFolderSepretor() {
		if(folderSepretor==null)folderSepretor=destinationLocation;
		return folderSepretor;
	}
	
	public String getUploadPrefix() {
		return uploadPrefix;
	}

	protected String splitBetween(String main,String start,String end){
		try{
			if(!start.endsWith(File.separator)) start = start+File.separator;
			String ans = main.substring(main.indexOf(start)+start.length(),main.indexOf(end));
			return ans;
		}catch (ArrayIndexOutOfBoundsException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error in identifing location for the file to put on the remote location.");
			return main;
		}
	}
	public File manageExtension(File file,String srcExt,String destExt,String fileName){
		if((file.getName().endsWith("."+srcExt)||(STAR_WILDCARD_CHARACTER.equalsIgnoreCase(srcExt)) && !file.getName().endsWith("."+destExt))){
			if(fileName==null || fileName.substring(fileName.lastIndexOf("."))!=destExt){
				fileName =file.getName().substring(0,file.getName().lastIndexOf("."));
			}
			boolean isFileExsists = false;
			if(!(isFileExsists=file.exists())){
				try {
					isFileExsists = file.createNewFile();
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, "Error in creating file");
				}
			}
			if(!isFileExsists){
				return file;
			}
			File destFile = new File(file.getParent()+File.separator+fileName+"."+destExt);
			
			if(file.renameTo(destFile)){
				return destFile;
			}else{
				return null;
			}
		}
		return file;
	}
	
	private boolean renameManagement(File file){
		return file.renameTo(new File(file.getParent(),getUploadPrefix()+file.getName()));
	}

	private boolean deleteManagement(File file){
		return file.delete();
	}

	private boolean archiveManagement(File file){
		try {
			if(isArchiveTreeCreated) {
				File newFile = manageExtension(file, STAR_WILDCARD_CHARACTER, originalExtension, null);
				if(newFile == null)
					return false;
				
				String folderName = splitBetween(file.getPath(), getFolderSepretor(), file.getName());
				
				for(String destLocation : archiveLocationSet) {
					String dirTreeStr = null;
					try {
						dirTreeStr = destLocation + folderName;
						File directory = new File(dirTreeStr);
						
						//	If someone had deleted directory, create new directory 
						if(!directory.exists()) { 
							Set<String> dirs = createArchiveDirectory(directory.getPath());
							if(dirs.isEmpty()) {	// in case of directory creation failed
								continue;
							}
						}
						
						File fileToTransfer = new File(directory, newFile.getName());
						fileToTransfer.createNewFile();
						copyFile(newFile, fileToTransfer);
						
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "File archived successfully to location, file " + fileToTransfer);
						
					} catch (IOException e) {
						LogManager.getLogger().error(MODULE, "Error in performing archive management for dir " + dirTreeStr + ", Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
				
				return true;
			}
		} catch (FileAllocatorException e) {
			LogManager.getLogger().error(MODULE, "Error in performing archive management, Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error in performing archive management, Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return false;
	}

	public boolean postOperation(File file){
		if(DELETE.equalsIgnoreCase(postOperatoin)){
			return deleteManagement(file);
		}else if(RENAME.equalsIgnoreCase(postOperatoin)){
			return renameManagement(file);
		}else if(ARCHIVE.equalsIgnoreCase(postOperatoin)){
			return archiveManagement(file);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "MUST not be here, wrong post operation configured");
		}
		return false;
	}
	
	private void copyFile(File source, File dest) throws IOException{
		 
		if(source == null) {
			throw new IOException("Source File is null");
		}
		
		if(dest == null) {
			throw new IOException("Destination File is null");
		}
		
		if(!dest.exists()){
			dest.createNewFile();
		}
		   
		InputStream in = null;
		  
		OutputStream out = null;
		  
		try{
		 
			in = new FileInputStream(source);

			out = new FileOutputStream(dest);
		  
			byte[] buf = new byte[8192];	//	8 Kb for optimal performance

			int len;
		  
			while((len = in.read(buf)) > 0){
		  
				out.write(buf, 0, len);
            }
		   
	     }finally{
	    	 
	    	 FileUtil.closeQuietly(in);
	    	 FileUtil.closeQuietly(out);
        }
		  
	}
	
	private Set<String> createArchiveDirectory(String... archiveLocationStr) throws FileAllocatorException {
		Set<String> archiveLocationSet = new HashSet<String>();
		for (int i=0; i<archiveLocationStr.length; i++) {
			String destLocation = archiveLocationStr[i];
			File file = new File(destLocation);
			try {
				LogManager.getLogger().info(MODULE, "Creating archive directory " + destLocation);
				if(!file.exists() && !file.mkdirs()) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Problem in creating archive directory " + destLocation);
					continue;
				}

				if(!file.canWrite()) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Problem in creating archive directory " + destLocation + " Reason: No write access");
					continue;
				}
				
				isArchiveTreeCreated = true;
				archiveLocationSet.add(destLocation);
			} catch(SecurityException ex) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Problem in creating archive directory at location " + destLocation + " Reason: " + ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
			}
		}
		return archiveLocationSet;
	}
	
}
