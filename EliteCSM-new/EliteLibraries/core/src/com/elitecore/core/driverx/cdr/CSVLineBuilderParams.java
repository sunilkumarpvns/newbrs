package com.elitecore.core.driverx.cdr;

import java.text.SimpleDateFormat;

import com.elitecore.commons.base.Strings;

public class CSVLineBuilderParams{
	private final String multiValueDelimiter;
	private final String enclosingCharacter;
	private final String delimiter;
	private final SimpleDateFormat cdrTimeStampFormat;
	
	
	public CSVLineBuilderParams(String multiValueDelimiter,
			String enclosingCharacter, String delimiter,
			SimpleDateFormat cdrTimeStampFormat) {
		super();
		this.multiValueDelimiter = multiValueDelimiter;
		this.enclosingCharacter = enclosingCharacter;
		this.delimiter = delimiter;
		this.cdrTimeStampFormat = cdrTimeStampFormat;
	}
	public String getMultiValueDelimiter() {
		return multiValueDelimiter;
	}
	
	public String getEnclosingCharacter() {
		return enclosingCharacter;
	}
	
	public String getDelimiter() {
		return delimiter;
	}
	
	public SimpleDateFormat getCdrTimeStampFormat() {
		return cdrTimeStampFormat;
	}
	
	
	public static class ParamBuilders {
		private String multiValueDelimiter = ";";
		private String enclosingCharacter = "";
		private String delimiter =",";
		private SimpleDateFormat cdrTimeStampFormat;
		
		
		public ParamBuilders withMultiValueDelimiter(String multiValueDelimiter) {
			if(Strings.isNullOrBlank(multiValueDelimiter.trim()) == false){
				this.multiValueDelimiter = multiValueDelimiter.trim();
			}
			return this;
		}
		public ParamBuilders withEnclosingCharacter(String enclosingCharacter) {
			if(Strings.isNullOrBlank(enclosingCharacter.trim()) == false){
				this.enclosingCharacter = enclosingCharacter.trim();
			}
			return this;
		}
		public ParamBuilders withDelimiter(String delimiter) {
			if(Strings.isNullOrBlank(delimiter.trim()) == false){
				this.delimiter = delimiter.trim();
			}
			return this;
		}
		public ParamBuilders withCdrTimeStampFormat(SimpleDateFormat cdrTimeStampFormat) {
			this.cdrTimeStampFormat = cdrTimeStampFormat;
			return this;
		}

		public CSVLineBuilderParams build(){
			return new CSVLineBuilderParams(multiValueDelimiter, enclosingCharacter, delimiter, cdrTimeStampFormat);
		}
	}
}	
