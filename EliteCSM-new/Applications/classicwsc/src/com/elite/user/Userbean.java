package com.elite.user;

import java.util.Date;

public class Userbean {
	private String username;
	private String password;
	private Date login_time;
	
	private UserOtherDetail userotherdetail;
    
    public UserOtherDetail getUserotherdetail() {
		return userotherdetail;
	}
	public void setUserotherdetail(UserOtherDetail userotherdetail) {
		this.userotherdetail = userotherdetail;
	}
	public Userbean(String user, String pass, Date time) {
    	username = user;
    	password = pass;
    	login_time = time;
	}
    public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getLogin_time() {
		return login_time;
	}
	public void setLogin_time(Date login_time) {
		this.login_time = login_time;
	}

}
