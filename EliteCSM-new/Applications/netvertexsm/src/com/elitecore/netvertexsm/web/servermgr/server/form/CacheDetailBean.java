package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CacheDetailBean {
	String name = null;
	String source = null;
	String lastUpdatedTime = null;
	String lastReloadAttemptTime = null;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getLastUpdatedTime() {
		return lastUpdatedTime;
	}
	public void setLastUpdatedTime(String lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	public String getLastReloadAttemptTime() {
		return lastReloadAttemptTime;
	}
	public void setLastReloadAttemptTime(String lastReloadAttemptTime) {
		this.lastReloadAttemptTime = lastReloadAttemptTime;
	}
	
	@Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------------ Cache Detail Bean ---------------");
        writer.println("Name                      =" +name);
        writer.println("Source                    =" +source);
        writer.println("Last Updated Time         =" +lastUpdatedTime);
        writer.println("Last Reload Attempt Time  =" +lastReloadAttemptTime);
        writer.println("----------------------------------------------------");
        return out.toString();  
	}

}
