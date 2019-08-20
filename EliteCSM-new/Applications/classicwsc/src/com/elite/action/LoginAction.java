/*
 * $Id: Login.java 471756 2006-11-06 15:01:43Z husted $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.elite.action;
import java.util.Date;import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.components.ActionError;

import com.elite.model.LoginModal;
import com.elite.user.Userbean;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport {
	Logger logger = Logger.getLogger("wsc");
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

	private String username;
    private String password;
   

    public String execute() 
    {
    	if (isInvalid(getUsername())) return INPUT;
        if (isInvalid(getPassword())) return INPUT;
        LoginModal loginmodel = new LoginModal();
        logger.info("Starting Authenticating for User:-"+username+" Pass:-"+password);
        try
        {
	        if (loginmodel.authenticate(username, password)) 
	        {
	        	Userbean user = new Userbean(username, password, new Date());
	        	user.setUserotherdetail(loginmodel.getdetail(username));
	        	Map session = ActionContext.getContext().getSession();
	        	session.put("user",user);
	        	logger.info("Authentication Success.");
	        	return SUCCESS;
	        	
	        }
        }
        catch (Exception e) {
        	logger.error(e);
		}
        addActionError("Login Failed");
        logger.info("Authentication Failed.");
        return INPUT;
    }

    public String logout() throws Exception 
    {
    	Map session = ActionContext.getContext().getSession();
    	Userbean user = (Userbean)ActionContext.getContext().getSession().get("user");
    	session.remove("user");
    	logger.info("User:- "+user.getUserotherdetail().getCustomername()+" Logged out. ");
        return SUCCESS;
    }
    
    private boolean isInvalid(String value) {
        return (value == null || value.length() == 0);
    }
    
}

















