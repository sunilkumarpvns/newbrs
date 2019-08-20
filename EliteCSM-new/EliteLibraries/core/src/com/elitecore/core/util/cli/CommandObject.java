package com.elitecore.core.util.cli;

import java.io.Serializable;
import java.util.Date;

public class CommandObject implements Serializable{

	private static final long serialVersionUID = 1L;
	private String commandName;
	private String user;
	private Date executedTime;
	private String terminal;
	
	public CommandObject(String cmdName, String user, Date executeTime, String terminal){
		this.user=user;
		this.commandName=cmdName;
		this.executedTime=executeTime;
		this.terminal=terminal;
	}
	
	public void setUser(String user){
		this.user=user;
	}

	public void setName(String commandName){
		this.commandName=commandName;
	}

	public void setExecutedTime(Date execTime){
		this.executedTime = execTime;
	}
	
	public void setTerminal(String terminal){
		this.terminal=terminal;
	}
	
	public String getTerminal(){
		return terminal;
	}
	public Date getExecutedTime(){
		return executedTime;
	}
	public String getUser(){
		return user;
	}
	public String getCommandName(){
		return commandName;
	}
	
	@Override
	public String toString(){
		return String.format("%-30s %-15s %-16s %-10s", executedTime, user ,terminal, commandName);
	}
}