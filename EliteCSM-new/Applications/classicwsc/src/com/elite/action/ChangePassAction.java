package com.elite.action;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;

import com.elite.model.LoginModal;
import com.elite.user.Userbean;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ChangePassAction extends ActionSupport{
	Logger logger = Logger.getLogger("wsc");
	private String oldpassword;
	public String getOldpassword() {
		return oldpassword;
	}
	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}
	public String getNewpassword() {
		return newpassword;
	}
	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}
	public String getConfirmpassword() {
		return confirmpassword;
	}
	public void setConfirmpassword(String confirmpassword) {
		this.confirmpassword = confirmpassword;
	}
	private String newpassword;
	private String confirmpassword;
	@Override
	public String execute()  {
		if (isInvalid(getOldpassword())) return INPUT;
    	if (isInvalid(getNewpassword())) return INPUT;
        if (isInvalid(getConfirmpassword())) return INPUT;
        LoginModal loginmodel = new LoginModal();
        Map session = ActionContext.getContext().getSession();
        Userbean user = (Userbean)session.get("user");
        logger.info("Starting Changeing Password for User:-"+user.getUsername());
        try
        {	
        	if(newpassword.equals(confirmpassword) && oldpassword.equals(user.getPassword()))
        	{
        		if (loginmodel.changePassword(user.getUsername(), getOldpassword(),getNewpassword())) 
		        {
		        	this.addActionMessage("Password Change Successfully");
		        	user.setPassword(getNewpassword());
		        	logger.info("Password Changed for User:-"+user.getUsername());
        			return INPUT;
		        }
        		else
        		{
        			logger.info("Password Changed Unsuccessful for User:-"+user.getUsername());
        			this.addActionMessage("Password Change Unsuccessful");
        			return INPUT;
        		}
        	}
        	else
    		{
        		logger.info("Password Changed Unsuccessful for User:-"+user.getUsername());
    			this.addActionMessage("Password Change Unsuccessful");
    			return INPUT;
    		}
        }
        catch (Exception e) {
        	logger.error(e);
			this.addActionMessage("Exception Caught.");
			return INPUT;
		}
        
    }
	 private boolean isInvalid(String value) {
	        return (value == null || value.length() == 0);
	    }
}
