package com.elitecore.core.commons.fileio.loactionalloactor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class FTPFileAllocator extends BaseCommonFileAllocator {

	private static final String MODULE= "FTP_FILE_ALLOCATOR";
	
	private FTPClient ftpClient = null;
	private Lock fileLock = new ReentrantLock();
//	private String userHome = null;
	private static final int UNABLE_TO_CHANGE_DIRECTORY = 550;
	private boolean rootDirectoryCreated = false;

	private void createFolderTree() throws FileAllocatorException{
		try{
			String userHome= getSocketClient().printWorkingDirectory();
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "User Home is  : "+userHome);


			String folderTree = getDestinationLocation();
			String folderStructure = "";
			if(folderTree.contains(userHome)){
				folderTree = getDestinationLocation().substring(getDestinationLocation().indexOf(userHome)+userHome.length());
				folderStructure=userHome;	
			}
			String folders[] = null;
			if(folderTree.contains(FILE_SEPARATOR)){
				folders = folderTree.split(FILE_SEPARATOR);
			}
			if(folders!=null){

				for(int i=0;i<folders.length;i++){
					if(folders[i].length()>0){
						folderStructure =folderStructure+FILE_SEPARATOR+folders[i];
						makeDirectory(folderStructure);
						changeWorkingLocation(folderStructure);
					}
				}
			}
			rootDirectoryCreated = true;
		}catch (IOException e) {
			throw new FileAllocatorException(e);
		}
	}
	
	public boolean disconnect() {
		if(getSocketClient() == null){
			return true;
		}

		try {
			getSocketClient().disconnect();
			ftpClient = null;
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Connection Disconnected with the server : "+getAddress());
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error in Closing Connection. Reason : "+e);
			LogManager.getLogger().trace(MODULE, e);
			return false;
		}
		return true;
	}

	public boolean connect() throws FileAllocatorException {
		
		if(getSocketClient() == null || !getSocketClient().isConnected()){
			try {
				ftpClient = new FTPClient();
				ftpClient.connect(getAddress(),getPort());
				boolean success = ftpClient.login(getUser(), getPassword());
				
				if(!success) {
					throw new FileAllocatorException("FTP login failed to " + getAddress() + ":" + getPort() + ". Reason: Invalid Username or Password");
				}
				
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Connection Acquired with the Server " + getAddress() + ":" + getPort());
			} catch (SocketException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in getting Connection to Server " + getAddress() + ":" + getPort() + ", Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (IOException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in getting Connection to Server " + getAddress() + ":" + getPort() + ", Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		if(!getPermission()) {
			String replyString = null;
			try{
				replyString= ftpClient.getReplyString();
			}catch (NullPointerException e) {
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "FTP server : "+getAddress()+":"+getPort()+" refused connection. Reason : "+replyString);
			LogManager.getLogger().trace(MODULE, "FTP server refused connection. Reason : "+replyString);
			disconnect();
			return false;
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "FTP server : "+getAddress()+":"+getPort()+" connected : "+ftpClient.getReplyString());
			return true;
		}
	}

	public boolean getPermission() {
		
		
		if(getSocketClient()!=null && !FTPReply.isPositiveCompletion(getSocketClient().getReplyCode())) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "FTP server Status : "+getSocketClient().getReplyString());
			return false;
		}else{
			return true;
		}
	}

	public boolean changeWorkingLocation(String workingLocation) throws FileAllocatorException {
		try {
			if(getSocketClient()!=null){
				boolean isChanged = getSocketClient().changeWorkingDirectory(workingLocation);
				if(isChanged) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Working directory Changed to "+workingLocation);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Failed to change working directory "+workingLocation);
				}
				return isChanged;
			}else {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error while changing working directory, Reason: Can not aquire connection to "
					+ getAddress() + ":" + getPort());
			}
		} catch (Exception e) {
			LogManager.getLogger().trace("Error while changing directory " + workingLocation +  " Reason: " + e.getMessage(), e);
			throw new FileAllocatorException("Error while changing directory " + workingLocation +  " Reason: " + e.getMessage());
		}
		return false;
	}
	
	public boolean makeDirectory(String directoryTree) throws FileAllocatorException {
		try {
			if(getSocketClient()!=null){
				boolean isCreated = getSocketClient().makeDirectory(directoryTree);
				if(isCreated) {
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "Directory Created "+directoryTree);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "Failed to create directory "+directoryTree + " Reason: No sufficient privileges or directory already exist");
				}
				return isCreated;
			}else {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Failed to create directory " + directoryTree + " Reason: Connection closed");
			}
		} catch (Exception e) {
			LogManager.getLogger().trace("Error while creating directory " + directoryTree + " Reason: " + e.getMessage(), e);
			throw new FileAllocatorException("Error while creating directory " + directoryTree + " Reason: " + e.getMessage());
		}
		return false;
	}
	
	public boolean persist() {
		return true;
	}

	public File transferFile(File file) throws FileAllocatorException {
		try {
			if(!fileLock.tryLock()){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Error in transferring file " + file.getName() + ", Reason: Can not aquire lock");
				return null;
			}
			
			if(!rootDirectoryCreated && getSocketClient() != null && getSocketClient().getReplyCode()!=UNABLE_TO_CHANGE_DIRECTORY){
				createFolderTree();
				if(getSocketClient().getReplyCode()==UNABLE_TO_CHANGE_DIRECTORY){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Can not aquire connection , Reason : "+getSocketClient().getReplyString());
					return null;
				}
			}
			String absolutePath = getDestinationLocation();
			String[] folderTree = splitBetween(file.getPath(), getFolderSepretor(), file.getName()).split(File.separator);
			
			
			if(rootDirectoryCreated && folderTree!=null && getSocketClient()!=null && FTPReply.isPositiveCompletion(getSocketClient().getReplyCode())){
				if(makeDirectory(absolutePath) || changeWorkingLocation(getDestinationLocation())){
					for(String location : folderTree){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Creating Folder : "+location);
						if(absolutePath.endsWith(File.separator)) {
							absolutePath = absolutePath + location;
						} else {
							absolutePath = absolutePath + File.separator + location;
						}
						makeDirectory(absolutePath); 
						changeWorkingLocation(absolutePath);
					}
					if(changeWorkingLocation(absolutePath)){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Folder Tree Created, Uploading file  : "+file.getName());

						File fileToTransfer = manageExtension(file, BaseCommonFileAllocator.UIP_EXTENSION, originalExtension, null);
						if(fileToTransfer==null)
							return null;
						if(getSocketClient().storeFile(fileToTransfer.getName(), new FileInputStream(fileToTransfer)))
							return fileToTransfer;
						else 
							return null;
					}
				}
			}else {
				connect();
				
				if(getSocketClient() == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in transferring file to destination location, Reason: Can not aquire connection to "
						+ getAddress() + ":" + getPort());
					return null;
				}
				
				if(FTPReply.isPositiveCompletion(getSocketClient().getReplyCode())) {
					File fileToTransfer = manageExtension(file, BaseCommonFileAllocator.UIP_EXTENSION, originalExtension, null);
					fileToTransfer = transferFile(fileToTransfer); 
					if(fileToTransfer!=null)
						return fileToTransfer;
					else 
						return null;
				}else{
					disconnect();
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in transferring file to  : "+file.getName()+" , Reason : "+getSocketClient().getReplyString());
					return null;	
				}
			}
		} catch (FileNotFoundException e) {
			LogManager.getLogger().trace("Error while transferring file " + file.getName() + ". Reason: " + e.getMessage(), e);
			throw new FileAllocatorException("Error while transferring file " + file.getName() + ". Reason: " + e.getMessage());
		} catch (IOException e) {
			disconnect();
			LogManager.getLogger().trace("Error in getting Connection " + file.getName() + ". Reason: " + e.getMessage(), e);
			throw new FileAllocatorException("Error in getting Connection " + file.getName() + ". Reason: " + e.getMessage());
		} catch (FileAllocatorException e) {
			disconnect();
			LogManager.getLogger().trace("Error while transferring file " + file.getName() + ". Reason: "+e.getMessage(), e);
			throw e;
		} finally {
			try {
				if(!changeWorkingLocation(getDestinationLocation())){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in changing back to directory : "+getDestinationLocation()+" , Reason : null");
				}
			} catch (Exception e) {
				// ignore exception
			}
			fileLock.unlock();
		}
		return null;
	}

	public FTPClient getSocketClient() {
		return ftpClient;
	}
	
}
