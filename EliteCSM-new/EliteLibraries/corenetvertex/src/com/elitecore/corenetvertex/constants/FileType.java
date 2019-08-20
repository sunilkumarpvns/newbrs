package com.elitecore.corenetvertex.constants;

/**
 * This enum will define status for the FileType. It can be File Writing or File
 * Reading @author Seekarla.Krishna on 14/12/2017
 *
 */

public enum FileType {

	FILEREADING("File Reading"), FILEWRITING("File Writing");

	private String value;

	private FileType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static FileType fromVal(String value) {
		for (FileType status : values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		return null;
	}
}
