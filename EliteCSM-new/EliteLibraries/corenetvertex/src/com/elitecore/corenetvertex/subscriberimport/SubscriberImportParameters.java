package com.elitecore.corenetvertex.subscriberimport;


public class SubscriberImportParameters {

	private InputType inputType;
	private String inputFilePath;
	private String packageMappingFilePath;
	
	public InputType getInputType() {
		return inputType;
	}
	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}
	public String getInputFilePath() {
		return inputFilePath;
	}
	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}
	public String getPackageMappingFilePath() {
		return packageMappingFilePath;
	}
	public void setPackageMappingFilePath(String packageMappingFile) {
		this.packageMappingFilePath = packageMappingFile;
	}
}
