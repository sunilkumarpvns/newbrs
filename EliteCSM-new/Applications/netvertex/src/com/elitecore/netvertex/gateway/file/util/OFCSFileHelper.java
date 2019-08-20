package com.elitecore.netvertex.gateway.file.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.constant.OfflineConstants;
import com.elitecore.netvertex.core.constant.OfflineRnCConstants;
import com.elitecore.netvertex.gateway.file.FileGatewayConfiguration;

public class OFCSFileHelper {

	private static final String MODULE = "OFCS-FILE-PROCESSOR";

	/** The file range Minimum Miss Limit*/
	private static final int MAX_MISS_LIMIT = 10;

	/** The file range Minimum Miss Limit Counter*/
	private int missLimitCounter = 0;

	/** All Services Inprocess Extension array */
	private static final String[] inprocessServiceExtensions = { 
			OfflineRnCConstants.PARSING_SERVICE_INPROCESS_EXTENSION
	};

	public String getDatedPath(String fileGroupingType, String strPath){
		Calendar currentDate = Calendar.getInstance();
		int iYear = currentDate.get(Calendar.YEAR);
		int iMonth = currentDate.get(Calendar.MONTH)+1;
		int iDay = currentDate.get(Calendar.DAY_OF_MONTH);
		String targetFolder;

		if("Year".equalsIgnoreCase(fileGroupingType)){
			targetFolder = strPath+ File.separator + iYear;
		}else if("Month".equalsIgnoreCase(fileGroupingType)){
			targetFolder = strPath+ File.separator + iYear + File.separator + iMonth;
		}else if("Day".equalsIgnoreCase(fileGroupingType)){
			targetFolder = strPath+ File.separator + iYear+ File.separator + iMonth + File.separator + iDay;
		}else{
			targetFolder = strPath;
		}
		return targetFolder ;
	}

	public boolean checkDBConnectionException(String expMsg){
		boolean bFlag = false;
		if((expMsg != null && expMsg.length() > 0)
				&&  (expMsg.contains(OfflineRnCConstants.CLOSED_CONNECTION) ||
						expMsg.contains(OfflineRnCConstants.NETWORK_PROB_MESSAGE) ||
						expMsg.contains(OfflineRnCConstants.IMMEDIATE_SHUTDOWN) ||
						expMsg.contains(OfflineRnCConstants.SOFTWARE_CAUSED_CONNECTION) ||
						expMsg.contains(OfflineRnCConstants.NO_MORE_DATA_TO_READ) ||
						expMsg.contains(OfflineRnCConstants.INCONSISTENCE_STATE) ||
						expMsg.contains(OfflineRnCConstants.CONNECTION_RESET) ||
						expMsg.contains(OfflineRnCConstants.BROKEN_PIPE) ||
						expMsg.contains(OfflineRnCConstants.UNKNOWN_SID) ||
						expMsg.contains(OfflineRnCConstants.LISTENER_REFUSED) ||
						expMsg.contains(OfflineRnCConstants.CONNECTION_TIMED_OUT) ||
						expMsg.contains(OfflineRnCConstants.CLOSED_STATEMENT))){
			bFlag = true;
		}
		return bFlag;
	}

	/*public List<File> getRecursiveFileList(File directory, String strFilters, String inProcessExt, String selectFileOnSuffixes,String exculdeFileTypes){
		List<File> fileList = new ArrayList<>();
		if(!directory.exists()) {
			LogManager.getLogger().warn(MODULE, "Source Directory - " + directory.getAbsolutePath() + " not exists for retrieving file list");
			return fileList;
		}
		try{
			if(directory.isDirectory()){
				File[] templist = getFileList(directory, strFilters, inProcessExt,exculdeFileTypes);
				for(int j = 0 ; j < templist.length ; j++){
					fileList.addAll(getRecursiveFileList(templist[j],strFilters,inProcessExt,selectFileOnSuffixes,exculdeFileTypes));
				}
			}else{
				if(directory.canRead() && directory.canWrite()){
					String inProcessFileName = directory.getAbsolutePath() + inProcessExt;

					boolean bFlag = false;


					if(selectFileOnSuffixes != null && selectFileOnSuffixes.length() > 0) {
						StringTokenizer tokenizer = new StringTokenizer(selectFileOnSuffixes,",");
						while(tokenizer.hasMoreTokens()) {
							String strContainWith = tokenizer.nextToken();
							if(strContainWith != null && strContainWith.trim().length() > 0) {
								if(directory.getName().contains(strContainWith.trim())) {
									bFlag = true;
									break;
								} else {
									bFlag = false;
								}
							}
						}
					} else {
						bFlag = true;
					}
					if(bFlag) {
						File inProcessFile = new File(inProcessFileName);
						boolean bRenameSuccessed = directory.renameTo(inProcessFile);
						if(bRenameSuccessed)
							fileList.add(inProcessFile);
					}
				}
			}
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error scanning input folder, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return fileList;
	}*/

	private File[] getFileList(File direFile, String strFilters, final String inProcessExt,final String exculdeFileTypes){
		File[] totalFile = null;
		if(direFile != null && direFile.isDirectory()){
			if(strFilters != null && strFilters.length() > 0){
				final String[] strFileType = strFilters.split(",");
				totalFile = direFile.listFiles( new FileFilter(){
					@Override
					public boolean accept(File f){
						if(f.isDirectory()){
							return true;
						}else {
							for(int i = 0 ; i < strFileType.length ; i++){
								if(f.getName().endsWith(strFileType[i])){
									return true;
								}
							}
							return false;
						}
					}
				} );
			}else{
				totalFile = direFile.listFiles( new FileFilter(){
					@Override
					public boolean accept(File f){
						if(f.isDirectory()){
							return true;
						}else {
							boolean bFlag = false;
							if(!f.getName().endsWith(inProcessExt) &&
									!f.getName().endsWith(OfflineRnCConstants.ERROR_FILE_EXTENSION) &&
									!f.getName().endsWith(OfflineRnCConstants.WARN_FILE_EXTENSION) &&
									!isContainsInprocessExtention(f.getName())){
								bFlag = true;
							}
							if(bFlag && exculdeFileTypes != null && exculdeFileTypes.length() > 0) {
								String[] excludeFiltere = exculdeFileTypes.split(",");
								for(int k=0;k<excludeFiltere.length;k++) {
									if(f.getName().endsWith(excludeFiltere[k])){
										bFlag = false;
										break;
									}
								}
							}
							return bFlag;
						}
					}
				} );
			}
		}
		return totalFile;
	}

	public void deleteInprocessFile(final String fileName, String[] sourcePaths, final String inprocessExt){
		if(fileName != null && fileName.length() > 0){
			for(int j = 0 ; j < sourcePaths.length ; j++){
				File sourceDir = new File(sourcePaths[j]);
				if(sourceDir.exists() && sourceDir.isDirectory() && sourceDir.canRead() && sourceDir.canWrite() && inprocessExt != null){
					File[] inprocessFiles = sourceDir.listFiles(new FilenameFilter(){
						@Override
						public boolean accept(File dir, String name) {
							return name.startsWith(fileName) && name.endsWith(inprocessExt);
						}
					});

					for(int k = 0 ; k < inprocessFiles.length ; k++){
						File inprocessFile = inprocessFiles[k];
						if(inprocessFile.exists() && inprocessFile.canRead() && inprocessFile.canWrite()) {
							boolean bDeleteSuccess = inprocessFile.delete();
							if(!bDeleteSuccess){
								LogManager.getLogger().error(MODULE, "Could not delete inprocess file, deleteInprocessFile() - " + inprocessFile.getName());
							}
						}
					}
				}else{
					LogManager.getLogger().warn(MODULE,"Sourcepath is not a directory or read/write access problme");
				}
			}
		}
	}

	public void deleteInprocessFiles(List<String> fileNameList, String[] sourcePaths, final String inprocessExt){
		if(fileNameList != null && !fileNameList.isEmpty()){
			for(int j = 0 ; j < sourcePaths.length ; j++){
				File sourceDir = new File(sourcePaths[j]);
				if(sourceDir.exists() && sourceDir.isDirectory() && sourceDir.canRead() && sourceDir.canWrite() && inprocessExt != null){
					for(int i = 0 ; i < fileNameList.size() ; i++){
						final String strFileName = fileNameList.get(i);
						File[] inprocessFiles = sourceDir.listFiles(new FilenameFilter(){
							@Override
							public boolean accept(File dir, String name) {
								return name.startsWith(strFileName) && name.endsWith(inprocessExt);
							}
						});

						for(int k = 0 ; k < inprocessFiles.length ; k++){
							File inprocessFile = inprocessFiles[k];
							if(inprocessFile.exists() && inprocessFile.canRead() && inprocessFile.canWrite()) {
								boolean bDeleteSuccess = inprocessFile.delete();
								if(!bDeleteSuccess){
									LogManager.getLogger().error(MODULE, "Could not delete inprocess file, deleteInprocessFiles() - " + inprocessFile.getName());
								}
							}
						}
					}
				}else{
					LogManager.getLogger().warn(MODULE,"Sourcepath is not a directory or read/write access problem");
				}
			}
		}else{
			LogManager.getLogger().trace(MODULE,"In Process files received null.");
		}
	}

	public List<String> getInProcessFileList(String sourcePath, final String inprocessExt){
		ArrayList<String> fileNameList = new ArrayList<>();
		File sourceDir = new File(sourcePath);
		if(sourceDir.exists() && sourceDir.canRead() && sourceDir.canWrite() && inprocessExt != null){
			File[] inprocessFiles = sourceDir.listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(inprocessExt);
				}
			});
			if(inprocessFiles != null && inprocessFiles.length > 0){
				int iFileCounter = inprocessFiles.length;
				for(int i = 0; i < iFileCounter; i++){
					String strFileName = inprocessFiles[i].getName();
					fileNameList.add(strFileName.substring(0,strFileName.indexOf(inprocessExt)));
				}
			}
		}else{
			LogManager.getLogger().warn(MODULE, "Source Directory is not a directory or it has a read/write problem");
		}
		return fileNameList;
	}

	public void renameRecursiveInProcessFileList(File directory, final String inprocessExt){
		if(!directory.exists()) {
			LogManager.getLogger().warn(MODULE, "Source Directory - " + directory.getAbsolutePath() + " not exists for retrieving file list");
		}
		try{
			if(directory.isDirectory()){
				File[] inprocessFiles = directory.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						if(pathname.isDirectory()){
							return true;
						}else{
							return pathname.getName().endsWith(inprocessExt);
						}
					}
				});

				for(int j = 0 ; j < inprocessFiles.length ; j++){
					renameRecursiveInProcessFileList(inprocessFiles[j],inprocessExt);
				}
			} else {
				String absolatePath = directory.getAbsolutePath();
				String fileName = absolatePath.substring(0,absolatePath.indexOf(inprocessExt));
				File newFile = new File(fileName);
				boolean bRenameSuccessed = directory.renameTo(newFile);
				if(bRenameSuccessed == false) {
					LogManager.getLogger().error(MODULE, "Could not rename the inprocess files on start up - " + directory.getName());
				}
			}
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error scanning input folder, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	public void renameInprocessFilesOnStartup(List<String> fileNameList, String sourcePath, String inprocessExt){
		if(fileNameList != null && !fileNameList.isEmpty()){
			File sourceDir = new File(sourcePath);
			if(sourceDir.exists() && sourceDir.canRead() && sourceDir.canWrite() && inprocessExt != null){
				for(int i = 0 ; i < fileNameList.size() ; i++){
					String strFileName = fileNameList.get(i);
					File inProcessFile = new File(sourcePath + File.separator + strFileName + inprocessExt);
					if(inProcessFile.exists() && inProcessFile.canRead() && inProcessFile.canWrite()){
						boolean renameSuccess = inProcessFile.renameTo(new File(sourcePath + File.separator + strFileName));
						if(renameSuccess == false) {
							LogManager.getLogger().error(MODULE, "Could not rename the inprocess files on start up - " + inProcessFile.getName());
						}
					}
				}
			}else{
				LogManager.getLogger().trace(MODULE, "Source Directory is not a directory or it has a read/write problem");
			}
		}else{
			LogManager.getLogger().trace(MODULE,"In Process files received null.");
		}
	}

	public boolean createDirOnCopyFolders(String[] arrFileCopy,String strDestDir) {
		for(int index = 0;index < arrFileCopy.length; index++){
			String strDestPath = arrFileCopy[index] + File.separator + strDestDir;
			try {
				if (!(new File(strDestPath)).exists()) {
					File file = new File(strDestPath);
					if(!file.mkdirs()){
						return false;
					}
				}
			}catch(Exception e) {
				LogManager.getLogger().error(MODULE, "Could not able to create directory on path :" + strDestPath + "Reason, " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				return false;
			}
		}
		return true;
	}

	public boolean createDirOnCopyFolders(List<String> arrFileCopy,String strDestDir, Method totalSpaceMethod, long requiredDiskSpaceinGB) {
		boolean bFlag = true;
		if(arrFileCopy != null && !arrFileCopy.isEmpty()){
			for(int index = 0;index < arrFileCopy.size(); index++){
				String strDestPath = arrFileCopy.get(index) + File.separator + strDestDir;
				File destDir = new File(strDestPath);
				try {
					if (!destDir.exists() && !destDir.mkdirs()) {
						bFlag = false;
					}
					boolean tmpFlag = isSpaceAvailableOnDisk(totalSpaceMethod, requiredDiskSpaceinGB, destDir);
					bFlag = bFlag && tmpFlag;
				}catch(Exception e) {
					LogManager.getLogger().error(MODULE, "Could not able to create directory on path :" + strDestPath + "Reason, " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
					bFlag = false;
				}

				if(!bFlag)
					break;
			}
		}
		return bFlag;
	}

	public void copyFileToMultipleFolders(String[] arrFileCopy,String strSourceFileNameWithPath,String fileName,String strDestDir,String inpExtension) throws IOException {
		try {
			for(int index = 0;index < arrFileCopy.length; index++) {
				String strDestPath = arrFileCopy[index] + File.separator + strDestDir;
				transfer(strSourceFileNameWithPath, fileName, inpExtension, strDestPath);
			}
		} catch(Exception e) {
			LogManager.getLogger().error(MODULE,"Error occurred while writing the file on specified copy folder, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
			throw new IOException(e.getMessage());
		}
	}

	private void transfer(String strSourceFileNameWithPath, String fileName, String inpExtension, String strDestPath)
			throws IOException {
		try (FileInputStream fileInputStream = new FileInputStream(strSourceFileNameWithPath);
				FileOutputStream fileOutputStream = new FileOutputStream(strDestPath + File.separator + fileName + inpExtension);
				FileChannel source = fileInputStream.getChannel();
				FileChannel destination = fileOutputStream.getChannel()) {
			destination.transferFrom(source, 0, source.size());
		} catch(Exception e) {
			LogManager.getLogger().error(MODULE,"Error occurred while writing the file on specified copy folder, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
			throw new IOException(e.getMessage());
		}
	}

	public void copyFileToMultipleFolders(List<String> arrFileCopy,String strSourceFileNameWithPath, String fileName, String strDestDir,
			String inpExtension) throws IOException {
		try {
			for(int index = 0;index < arrFileCopy.size(); index++){
				String strDestPath = arrFileCopy.get(index) + File.separator + strDestDir;
				transfer(strSourceFileNameWithPath, fileName, inpExtension, strDestPath);
			}
		}catch(Exception e) {
			LogManager.getLogger().error(MODULE,"Error occurred while writing the file on specified copy folder, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
			throw new IOException(e.getMessage());
		}
	}


	public void renameInProcessFileOnCopyFolders(String strFileName,String[] arrFileCopy,String strDestDir,String inProcessExtension) throws IOException{
		for(int index = 0;index < arrFileCopy.length; index++){
			String strDestPath = arrFileCopy[index] + File.separator + strDestDir;
			try {
				File destFile = new File(strDestPath + File.separator + strFileName + inProcessExtension);
				boolean renameSuccess = destFile.renameTo(new File(strDestPath + File.separator + strFileName));
				if(renameSuccess == false) {
					LogManager.getLogger().error(MODULE, "Error occurred while renaming the file on copy folder");
				}
			}catch(Exception e) {
				LogManager.getLogger().error(MODULE,"Error occurred while renaming the file on copy folder, reason : " + e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
				throw new IOException(e.getMessage());
			}
		}
	}

	public void renameInProcessFileOnCopyFolders(String strFileName,List<String> arrFileCopy,String strDestDir,String inProcessExtension) throws IOException{
		for(int index = 0;index < arrFileCopy.size(); index++){
			String strDestPath = arrFileCopy.get(index) + File.separator + strDestDir;
			try {
				File destFile = new File(strDestPath + File.separator + strFileName + inProcessExtension);
				boolean renameSuccess = destFile.renameTo(new File(strDestPath + File.separator + strFileName));
				if(renameSuccess == false) {
					LogManager.getLogger().error(MODULE,"Error occurred while renaming the file on copy folder");
				}
			}catch(Exception e) {
				LogManager.getLogger().error(MODULE, "Error occurred while renaming the file on copy folder, reason : " + e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
				throw new IOException(e.getMessage());
			}
		}
	}

	public void deleteInProcessFileOnCopyFolders(final String strFileName,String[] arrFileCopy,String strDestDir,final String inProcessExtension){
		for(int index = 0;index < arrFileCopy.length; index++){
			String strDestPath = arrFileCopy[index] + File.separator + strDestDir;
			File sourceDir = new File(strDestPath);
			if(sourceDir.exists() && sourceDir.isDirectory() && sourceDir.canRead() && sourceDir.canWrite() && inProcessExtension != null){
				File[] inprocessFiles = sourceDir.listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name) {
						return name.startsWith(strFileName) && name.endsWith(inProcessExtension);
					}
				});

				for(int k = 0 ; k < inprocessFiles.length ; k++){
					File inprocessFile = inprocessFiles[k];
					if(inprocessFile.exists() && inprocessFile.canRead() && inprocessFile.canWrite()) {
						boolean bDeleteSuccess = inprocessFile.delete();
						if(!bDeleteSuccess){
							LogManager.getLogger().error(MODULE, "Could not delete inprocess file, deleteInProcessFileOnCopyFolders() - " + inprocessFile.getName());
						}
					}
				}
			}else{
				LogManager.getLogger().warn(MODULE,"Sourcepath is not a directory or read/write access problem");
			}
		}
	}

	public void deleteInProcessFileOnCopyFolders(final String strFileName,List<String> arrFileCopy,String strDestDir,final String inProcessExtension){
		for(int index = 0;index < arrFileCopy.size(); index++){
			String strDestPath = arrFileCopy.get(index) + File.separator + strDestDir;
			File sourceDir = new File(strDestPath);
			if(sourceDir.exists() && sourceDir.isDirectory() && sourceDir.canRead() && sourceDir.canWrite() && inProcessExtension != null){
				File[] inprocessFiles = sourceDir.listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name) {
						return name.startsWith(strFileName) && name.endsWith(inProcessExtension);
					}
				});

				for(int k = 0 ; k < inprocessFiles.length ; k++){
					File inprocessFile = inprocessFiles[k];
					if(inprocessFile.exists() && inprocessFile.canRead() && inprocessFile.canWrite()) {
						boolean bDeleteSuccess = inprocessFile.delete();
						if(!bDeleteSuccess){
							LogManager.getLogger().error(MODULE, "Could not delete inprocess file, deleteInProcessFileOnCopyFolders() - " + inprocessFile.getName());
						}
					}
				}
			}else{
				LogManager.getLogger().warn(MODULE,"Sourcepath is not a directory or read/write access problem");
			}
		}
	}

	public void deleteInProcessFilesOnCopyFolders(List<String> fileNameList,String[] arrFileCopy,String strDestDir, final String inProcessExtension){
		if(fileNameList != null && !fileNameList.isEmpty()){
			for(int j = 0 ; j < arrFileCopy.length ; j++){
				String strDestPath = arrFileCopy[j] + File.separator + strDestDir;
				File sourceDir = new File(strDestPath);
				if(sourceDir.exists() && sourceDir.isDirectory() && sourceDir.canRead() && sourceDir.canWrite() && inProcessExtension != null){
					for(int i = 0 ; i < fileNameList.size() ; i++){
						final String strFileName = fileNameList.get(i);
						File[] inprocessFiles = sourceDir.listFiles(new FilenameFilter(){
							@Override
							public boolean accept(File dir, String name) {
								return name.startsWith(strFileName) && name.endsWith(inProcessExtension);
							}
						});

						for(int k = 0 ; k < inprocessFiles.length ; k++){
							File inprocessFile = inprocessFiles[k];
							if(inprocessFile.exists() && inprocessFile.canRead() && inprocessFile.canWrite()) {
								boolean bDeleteSuccess = inprocessFile.delete();
								if(!bDeleteSuccess){
									LogManager.getLogger().error(MODULE, "Could not delete inprocess file, deleteInProcessFilesOnCopyFolders() - " + inprocessFile.getName());
								}
							}
						}
					}
				}else{
					LogManager.getLogger().warn(MODULE,"Sourcepath is not a directory or read/write access problem");
				}
			}
		}else{
			LogManager.getLogger().trace(MODULE,"In Process files received null.");
		}
	}

	public void deleteInProcessFilesOnCopyFolders(List<String> fileNameList,List<String> arrFileCopy,String strDestDir, final String inProcessExtension){
		if(fileNameList != null && !fileNameList.isEmpty()){
			for(int j = 0 ; j < arrFileCopy.size() ; j++){
				String strDestPath = arrFileCopy.get(j) + File.separator + strDestDir;
				File sourceDir = new File(strDestPath);
				if(sourceDir.exists() && sourceDir.isDirectory() && sourceDir.canRead() && sourceDir.canWrite() && inProcessExtension != null){
					for(int i = 0 ; i < fileNameList.size() ; i++){
						final String strFileName = fileNameList.get(i);
						File[] inprocessFiles = sourceDir.listFiles(new FilenameFilter(){
							@Override
							public boolean accept(File dir, String name) {
								return name.startsWith(strFileName) && name.endsWith(inProcessExtension);
							}
						});

						for(int k = 0 ; k < inprocessFiles.length ; k++){
							File inprocessFile = inprocessFiles[k];
							if(inprocessFile.exists() && inprocessFile.canRead() && inprocessFile.canWrite()) {
								boolean bDeleteSuccess = inprocessFile.delete();
								if(!bDeleteSuccess){
									LogManager.getLogger().error(MODULE, "Could not delete inprocess file, deleteInProcessFilesOnCopyFolders() - " + inprocessFile.getName());
								}
							}
						}
					}
				}else{
					LogManager.getLogger().warn(MODULE,"Sourcepath is not a directory or read/write access problem");
				}
			}
		}else{
			LogManager.getLogger().trace(MODULE,"In Process files received null.");
		}
	}
	public synchronized void writeStatisticsForFile(String statFileName, String strMsg, String fileGroupingType, String strPath){
		File statFile = null;
		File inprocessFile = null;
		try{
			String outputPath = getDatedPath(fileGroupingType, strPath);
			File finalDirs = new File(outputPath);
			if(!finalDirs.exists() && !finalDirs.mkdirs()) {
				LogManager.getLogger().info(MODULE, "Error while creating folder : " + finalDirs.getName());
			}
			String finalPath = outputPath + File.separator + statFileName;
			statFile = new File(finalPath);
			inprocessFile = new File(finalPath+".inp");
			if(!inprocessFile.exists()){
				if(!statFile.exists() && !statFile.createNewFile()) {
					LogManager.getLogger().info(MODULE, "Error while creating file : " + statFile.getName());
				}
				tryRename(statFile, inprocessFile);
			}
			try (FileOutputStream fileOutputStream = new FileOutputStream(inprocessFile, true);
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					PrintWriter printWriter = new PrintWriter(bufferedOutputStream, true)) {
				printWriter.println(strMsg);
			}
		}catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().error(MODULE, "Error occured while writing in statistics file - " + statFileName + ", for statistics - " + strMsg + ", reason - " + e.getMessage());
		}finally{
			if(inprocessFile != null && inprocessFile.exists())
				tryRename(inprocessFile, statFile);
		}
	}

	private void tryRename(File statFile, File inprocessFile) {
		if (statFile.renameTo(inprocessFile) == false) {
			LogManager.getLogger().warn(MODULE, "Failed to rename " + statFile.getAbsolutePath());
		}
	}

	public static Map<String, FileGatewayConfiguration> getPathMap(List<FileGatewayConfiguration> pathList) {
		Map<String, FileGatewayConfiguration> pathMap = new HashMap<>();
		for(FileGatewayConfiguration sourcePathData : pathList) {
			String sourcePath = sourcePathData.getSourcePath();
			FileGatewayConfiguration data;
			if(sourcePath != null) {
				if(!pathMap.containsKey(sourcePath)) {
					data = sourcePathData;
					if(data.getDestinationPathCount() > 0) {
						data.setDestinationPathAvailable(true);
					}
					pathMap.put(sourcePath, data);
				} else {
					data = pathMap.get(sourcePath);
					data.addDestinationPath(data.getDestinationInputPath());
					data.addAllDestinationPath(sourcePathData.getDestinationPathList());
					if(data.getDestinationPathCount() > 0) {
						data.setDestinationPathAvailable(true);
					}
				}
			}
		}
		return pathMap;
	}

	public List<File> getFilterFileList(File directory, List<String> strFilters, String inProcessExt, List<String> selectFileOnSuffixes,
			List<String> exculdeFileTypes, String fileRange, Integer maxFileLimit, NetVertexServerContext serverContext, Alerts alertType, String module) {
		List<File> fileList = new ArrayList<>();
		Integer fileRangeMin = 0;
		Integer fileRangeMax = 0;
		try{
			if(fileRange != null && fileRange != "" && fileRange.contains("-")){
				String[] temp = fileRange.split("-", fileRange.length());
				fileRangeMin = Integer.parseInt(temp[0]);
				fileRangeMax = Integer.parseInt(temp[1]);
			}
		}catch(NumberFormatException e){
			LogManager.getLogger().error(MODULE, "Provide Proper File Range, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		if(!directory.exists()) {
			LogManager.getLogger().warn(MODULE, "Source Directory - " + directory.getAbsolutePath() + " not exists for retrieving file list");
			return fileList;
		}
		try{
			if(directory.isDirectory()){
				File[] templist = getFileList(directory, strFilters, inProcessExt,exculdeFileTypes);
				if(templist != null && templist.length > maxFileLimit && maxFileLimit != 0){
					serverContext.generateSystemAlert(AlertSeverity.WARN, alertType, module, "Maximum Files Threshold Limit Received From Path :" + directory.getAbsolutePath());
					LogManager.getLogger().error(module, "Maximum Files Threshold Limit Received From Path :" + directory.getAbsolutePath());
				}
				int fileCount = 0;
				if(templist != null && templist.length < fileRangeMin && missLimitCounter < MAX_MISS_LIMIT){
					missLimitCounter++;
					return fileList;
				}else {
					if(templist != null && templist.length > 0){
						for(int j = 0 ; j < templist.length ; j++) {

							if(fileRange != null && !fileRange.isEmpty() && fileCount >= fileRangeMax)
								break;

							if(templist[j].canRead() && templist[j].canWrite()){
								String inProcessFileName = templist[j].getAbsolutePath() + inProcessExt;

								boolean bFlag = false;

								if(!selectFileOnSuffixes.isEmpty()) {
									for(int i = 0 ; i < selectFileOnSuffixes.size() ; i++) {
										String strContainWith = selectFileOnSuffixes.get(i);
										if(strContainWith != null && strContainWith.trim().length() > 0) {
											if(templist[j].getName().contains(strContainWith.trim())) {
												bFlag = true;
												break;
											} else {
												bFlag = false;
											}
										}
									}
								} else {
									bFlag = true;
								}

								if(bFlag) {
									File inProcessFile = new File(inProcessFileName);
									boolean bRenameSuccessed = templist[j].renameTo(inProcessFile);
									if(bRenameSuccessed){
										fileList.add(inProcessFile);
										fileCount++;
									}
								}
							}
						}
						missLimitCounter = 0;
					}

				}
			}
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error scanning input folder, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return fileList;
	}

	private File[] getFileList(File direFile, final List<String> strFilters, final String inProcessExt,final List<String> exculdeFileTypes){
		File[] totalFile = null;
		if(direFile != null){
			if(!strFilters.isEmpty()){
				totalFile = direFile.listFiles( new FileFilter(){
					@Override
					public boolean accept(File f){
						if(f.isDirectory()){
							return false;
						}else {
							for(int i = 0 ; i < strFilters.size() ; i++){
								if(f.getName().endsWith(strFilters.get(i))){
									return true;
								}
							}
							return false;
						}
					}
				} );
			}else{
				totalFile = direFile.listFiles( new FileFilter() {
					@Override
					public boolean accept(File f){
						if(f.isDirectory()){
							return false;
						}else {
							boolean bFlag = false;
							if(!f.getName().endsWith(inProcessExt) &&
									!f.getName().endsWith(OfflineRnCConstants.ERROR_FILE_EXTENSION) &&
									!f.getName().endsWith(OfflineRnCConstants.WARN_FILE_EXTENSION) &&
									!isContainsInprocessExtention(f.getName())){
								bFlag = true;
							}
							if(bFlag && !exculdeFileTypes.isEmpty()) {
								for(int k=0;k<exculdeFileTypes.size();k++) {
									if(f.getName().endsWith(exculdeFileTypes.get(k))){
										bFlag = false;
										break;
									}
								}
							}
							return bFlag;
						}
					}
				} );
			}
		}
		return totalFile;
	}

	public String getLogicalFileName(String fileName){
		String logicalFileName = fileName;
		if(fileName != null && fileName.length() > 0){
			if(fileName.indexOf(OfflineConstants.PROCESSING_DUPLICATE_PARAM) > 1){
				int keyIndex = fileName.indexOf(OfflineConstants.PROCESSING_DUPLICATE_PARAM);
				if(keyIndex != -1)
					logicalFileName = fileName.substring(0,keyIndex);
			}else if(fileName.indexOf(OfflineConstants.PROCESSING_ERROR_PARAM) > 1){
				int keyIndex = fileName.indexOf(OfflineConstants.PROCESSING_ERROR_PARAM);
				if(keyIndex != -1)
					logicalFileName = fileName.substring(0,keyIndex);
			}else if(fileName.indexOf(OfflineConstants.PROCESSING_FILTER_PARAM) > 1){
				int keyIndex = fileName.indexOf(OfflineConstants.PROCESSING_FILTER_PARAM);
				if(keyIndex != -1)
					logicalFileName = fileName.substring(0,keyIndex);
			}else if(fileName.indexOf(OfflineConstants.PROCESSING_INVALID_PARAM) > 1){
				int keyIndex = fileName.indexOf(OfflineConstants.PROCESSING_INVALID_PARAM);
				if(keyIndex != -1)
					logicalFileName = fileName.substring(0,keyIndex);
			}else if(fileName.indexOf(OfflineConstants.DISTRIBUTION_ERROR_PARAM) > 1){
				int keyIndex = fileName.indexOf(OfflineConstants.DISTRIBUTION_ERROR_PARAM);
				if(keyIndex != -1)
					logicalFileName = fileName.substring(0,keyIndex);
			}else if(fileName.indexOf(OfflineConstants.PROCESSING_REPROCESSING_PARAM) > 1){
				int keyIndex = fileName.indexOf(OfflineConstants.PROCESSING_REPROCESSING_PARAM);
				if(keyIndex != -1)
					logicalFileName = fileName.substring(0,keyIndex);
			}
		}
		return logicalFileName;
	}

	public boolean isSpaceAvailableOnDisk(Method totalSpaceMethod, long requiredDiskSpaceinGB, File partition) {
		boolean bCheckForProcessing = true;
		if (requiredDiskSpaceinGB > 0) {
			try {
				long availableSpaceinGB = (Long) totalSpaceMethod.invoke(partition, (Object[]) null);
				availableSpaceinGB = availableSpaceinGB / 1024 / 1024 / 1024;
				if (availableSpaceinGB <= requiredDiskSpaceinGB) {
					bCheckForProcessing = false;
					LogManager.getLogger().warn(MODULE, "Disk space is not available, So stopping service for further processing for partition - " + partition);
					LogManager.getLogger().warn(MODULE, "Required space in GB is : " + requiredDiskSpaceinGB + ", Available space in GB is : " + availableSpaceinGB);
				}
			} catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
				LogManager.getLogger().trace(MODULE, e);
				LogManager.getLogger().warn(MODULE, "Problem in JVM version. Required JDK1.6 or higher version for disk space checking process");
			}
		}
		return bCheckForProcessing;
	}

	public boolean isSpaceAvailableOnDisk(Method totalSpaceMethod, long requiredDiskSpaceinGB, String partitionPath) {
		boolean bCheckForProcessing = true;
		if (requiredDiskSpaceinGB > 0) {
			try {
				File partition = new File(partitionPath);
				long availableSpaceinGB = (Long) totalSpaceMethod.invoke(partition, (Object[]) null);
				availableSpaceinGB = availableSpaceinGB / 1024 / 1024 / 1024;
				if (availableSpaceinGB <= requiredDiskSpaceinGB) {
					bCheckForProcessing = false;
					LogManager.getLogger().warn(MODULE, "Disk space is not available, So stopping service for further processing for partition - " + partition);
					LogManager.getLogger().warn(MODULE, "Required space in GB is : " + requiredDiskSpaceinGB + ", Available space in GB is : " + availableSpaceinGB);
				}
			} catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
				LogManager.getLogger().trace(MODULE, e);
				LogManager.getLogger().warn(MODULE, "Problem in JVM version. Required JDK1.6 or higher version for disk space checking process");
			}
		}
		return bCheckForProcessing;
	}

	public long getSchedulingInterval(String scheduleType, int scheduleHour, int scheduleMinute, int scheduleSecond,int scheduleDay, int scheduleDate, boolean lastDateFlag){
		Calendar assignDate = Calendar.getInstance();
		if(scheduleType.equalsIgnoreCase(OfflineConstants.SCHEDULE_DAILY)){
			assignDate = setTime(assignDate, scheduleHour, scheduleMinute, scheduleSecond);
			long lTimeInterval = assignDate.getTimeInMillis() - System.currentTimeMillis();

			if(lTimeInterval > 0) {
				return lTimeInterval;
			}else{
				//for next day
				assignDate = nextDay(assignDate.get(Calendar.DATE));
				assignDate = setTime(assignDate, scheduleHour, scheduleMinute, scheduleSecond);
				lTimeInterval = assignDate.getTimeInMillis() - System.currentTimeMillis();
				return lTimeInterval;
			}
		}else if(scheduleType.equalsIgnoreCase(OfflineConstants.SCHEDULE_WEEKLY)){
			//for week
			assignDate.set(Calendar.DAY_OF_WEEK, scheduleDay);

			assignDate = setTime(assignDate, scheduleHour, scheduleMinute, scheduleSecond);
			long lTimeInterval = assignDate.getTimeInMillis() - System.currentTimeMillis();

			if(lTimeInterval > 0) {
				return lTimeInterval;
			}else{
				//for next week
				assignDate = nextDayOfWeek(scheduleDay);
				assignDate = setTime(assignDate, scheduleHour, scheduleMinute, scheduleSecond);
				lTimeInterval = assignDate.getTimeInMillis() - System.currentTimeMillis();
				return lTimeInterval;
			}
		}else if(scheduleType.equalsIgnoreCase(OfflineConstants.SCHEDULE_MONTHLY)){
			if(lastDateFlag){
				//for month
				assignDate.set(Calendar.DATE, getLastDate(Calendar.getInstance()));
				assignDate = setTime(assignDate, scheduleHour, scheduleMinute, scheduleSecond);
				long lTimeInterval = assignDate.getTimeInMillis() - System.currentTimeMillis();
				if(lTimeInterval > 0) {
					return lTimeInterval;
				}else{
					//for next month
					assignDate.add(Calendar.MONTH, 1);
					assignDate.set(Calendar.DATE, getLastDate(assignDate));
					assignDate = setTime(assignDate, scheduleHour, scheduleMinute, scheduleSecond);
					lTimeInterval = assignDate.getTimeInMillis() - System.currentTimeMillis();
					return lTimeInterval;
				}
			}else{
				//for month
				assignDate.set(Calendar.DATE, scheduleDate);
				assignDate = setTime(assignDate, scheduleHour, scheduleMinute, scheduleSecond);
				long lTimeInterval = assignDate.getTimeInMillis() - System.currentTimeMillis();

				if(lTimeInterval > 0) {
					return lTimeInterval;
				}else{
					//for next month
					assignDate = nextMonth(scheduleDate);
					assignDate = setTime(assignDate, scheduleHour, scheduleMinute, scheduleSecond);
					lTimeInterval = assignDate.getTimeInMillis() - System.currentTimeMillis();
					return lTimeInterval;
				}
			}
		}else{
			return OfflineConstants.DEFAULT_TIME_INTERVAL;
		}

	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.services.serviceCommonAttributes#setTime()
	 */
	private Calendar setTime(Calendar assignDate, int scheduleHour, int scheduleMinute, int scheduleSecond){
		assignDate.set(Calendar.HOUR_OF_DAY, scheduleHour);
		assignDate.set(Calendar.MINUTE, scheduleMinute);
		assignDate.set(Calendar.SECOND, scheduleSecond);
		return assignDate;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.services.serviceCommonAttributes#nextDayOfWeek()
	 */
	private Calendar nextDayOfWeek(int week) {
		Calendar date = Calendar.getInstance();
		int diff = week - date.get(Calendar.DAY_OF_WEEK);
		if (!(diff > 0)) {
			diff += 7;
		}
		date.add(Calendar.DAY_OF_MONTH, diff);
		return date;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.services.serviceCommonAttributes#nextDay()
	 */
	private Calendar nextDay(int day) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DATE, ++day);
		return date;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.services.serviceCommonAttributes#nextMonth()
	 */
	private Calendar nextMonth(int monthDate) {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.MONTH, 1);
		date.set(Calendar.DATE, monthDate);
		return date;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.services.serviceCommonAttributes#getLastDate()
	 */
	private int getLastDate(Calendar calendarDate) {
		calendarDate.add(Calendar.MONTH, 1);
		calendarDate.set(Calendar.DAY_OF_MONTH, 1);
		calendarDate.add(Calendar.DATE, -1);
		return calendarDate.get(Calendar.DATE);
	}

	/**
	 * Checks if File name contains any of the service inprocess extention
	 * @param fileName
	 * @return true, if its inprocess file
	 * 		false, if not
	 */
	private boolean isContainsInprocessExtention(String fileName)
	{
		for(int i = 0; i < inprocessServiceExtensions.length; i++)
		{
			if(fileName.endsWith(inprocessServiceExtensions[i]))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Converts passed string date to equivalent {@code Date}.
	 * @param strDate - A string representing a date
	 * @param dateFormat - a string representing date format
	 * @return a {@code Date} corresponding to string date passed in parameter
	 * @throws ParseException
	 */
	public static Date dateOf(String strDate, String dateFormat) throws ParseException {
		Preconditions.checkNotNull(strDate, "date string passed is null");
		Preconditions.checkNotNull(dateFormat, "dateFormat passed is null");
		return new SimpleDateFormat(dateFormat).parse(strDate);
	}
	
	/**
	 * Converts passed string date to equivalent {@code Timestamp}.
	 * @param strDate - A string representing a date
	 * @param dateFormat - a string representing date format
	 * @return a {@code Timestamp} corresponding to string date passed in parameter
	 * @throws ParseException
	 */
	public static Timestamp timeStampOf(String strDate, String dateFormat) throws ParseException {
		return new Timestamp(dateOf(strDate, dateFormat).getTime());
	}
	
	/**
	 * Converts passed string date to equivalent {@code java.util.Date}.
	 * Masks hours, minutes, seconds and milliseconds.
	 * @param strDate - A string representing a date
	 * @param dateFormat - a string representing date format
	 * @return a {@code Date} corresponding to string date passed in parameter
	 * @throws ParseException
	 */
	public static java.sql.Date sqlDateOf(String strDate, String dateFormat) throws ParseException {
		Date javaDate = dateOf(strDate, dateFormat);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(javaDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND,0);
		return new java.sql.Date(calendar.getTimeInMillis());
	}
}
