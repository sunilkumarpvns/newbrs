package com.elitecore.elitesm.web.plugins;



/**
 * @author nayana.rathod
 *
 */

public class FileDetails {

	private String fileName;
	private String fileContent;
	private String lastModified;
	
	public String getFileContent() {
		return fileContent;
	}
	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public String toString() {
		return "FileDetails [filename=" + fileName + ", fileContent="
				+ fileContent + ", lastModified=" + lastModified + "]";
	}
	
}
