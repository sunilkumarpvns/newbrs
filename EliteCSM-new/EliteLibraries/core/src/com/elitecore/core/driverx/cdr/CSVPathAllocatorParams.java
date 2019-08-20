package com.elitecore.core.driverx.cdr;


public class CSVPathAllocatorParams {
	
	private String fileName;
	private String defaultDirectoryName;
	private String fileLocation;
	private String directoryName;
	public CSVPathAllocatorParams(String defaultDirectoryName, String fileLocation,
			String directoryName) {
		super();
		this.defaultDirectoryName = defaultDirectoryName;
		this.fileLocation = fileLocation;
		this.directoryName = directoryName;
	}


	public String getFileName() {
		return fileName;
	}
	public String getDefaultDirectoryName() {
		return defaultDirectoryName;
	}

	public String getFileLocation() {
		return fileLocation;
	}
	public String getDirectoryName() {
		return directoryName;
	}

	public static class CSVPathAllocatorBuilder{

		private String defaultDirectoryName="no_directory";
		private String fileLocation="data/csvfiles";
		private String directoryName;
		public CSVPathAllocatorBuilder withDefaultDirectoryName(String defaultDirectoryName) {
			this.defaultDirectoryName = defaultDirectoryName;
			return this;
		}
		public CSVPathAllocatorBuilder withFileLocation(String fileLocation) {
			this.fileLocation = fileLocation;
			return this;
		}
		public CSVPathAllocatorBuilder withDirectoryName(String directoryName) {
			this.directoryName = directoryName;
			return this;
		}
		public CSVPathAllocatorParams build(){
			return new CSVPathAllocatorParams(defaultDirectoryName, fileLocation, directoryName);
		}
		
	}
	
	

}
