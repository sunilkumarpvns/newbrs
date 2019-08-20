package com.brs.beans.common;

public class Response {
private String status;
private String msg;
private Object data;

@Override
public String toString() {
	return "Response [status=" + status + ", msg=" + msg + ", data=" + data + "]";
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getMsg() {
	return msg;
}
public void setMsg(String msg) {
	this.msg = msg;
}
public Object getData() {
	return data;
}
public void setData(Object data) {
	this.data = data;
}

}
