/**
 * 
 */
package com.elitecore.core.util.mbean;

import java.util.List;
import java.util.Map;

/**
 * @author rakeshkachhadiya
 *
 */
public interface IEliteFileEntityMBean extends IEliteBaseMBean{

	public String getSourcePath(String groupName)throws Exception;
	public String getDestinationPath(String groupName)throws Exception;
	public List searchFile(String groupName,String filename)throws Exception;
	public void addFiles(String groupName,List fileList)throws Exception; 	
	public String getServerHome();
	public void saveFile(String fileName,byte[] doc) throws Exception;
	public List readFileList() throws Exception;
	public void deleteFile(String fileName) throws Exception;

	public String[] getFileGroups();
	public String getSourcePath(String groupName,String fileGroup)throws Exception;
	public String getDestinationPath(String groupName,String fileGroup)throws Exception;
	public Map<String,List<String>> readFilesFromAllFileGroups() throws Exception;
	public void deleteFile(String fileName,String fileGroup) throws Exception;
	public List<String> readFileList(String fileGroup) throws Exception;
	public void saveFile(String fileName, byte[] doc, String fileGroup) throws Exception;
}


