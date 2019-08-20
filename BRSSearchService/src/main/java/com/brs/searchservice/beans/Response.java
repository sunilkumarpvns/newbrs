package com.brs.searchservice.beans;

import java.util.List;

public class Response {
private ResponseStatus status;
private Object data;
private List<Object> dataList;
private String message;
public ResponseStatus getStatus() {
	return status;
}
public void setStatus(ResponseStatus status) {
	this.status = status;
}
public Object getData() {
	return data;
}
public void setData(Object data) {
	this.data = data;
}
public List<Object> getDataList() {
	return dataList;
}
public void setDataList(List<Object> dataList) {
	this.dataList = dataList;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
} 

}
