package com.brs.beans.common.enums;

public enum ResponseEnum {
	SUCCESS(1, "SUCCESS"),
	FAILURE(2, "FAILURE");
	private int code;
	private String value;

	private ResponseEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public int getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

}
