package com.elitecore.core.util.url;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.commons.base.Equality;

/**
 * @author Milan Paliwal
 */
public class URLData {
	private String protocol;
	private String userName;
	private String password;
	private String host;
	private int port;
	private String resource;
	
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println("		-- URL Data Configuration -- ");
		out.println();
		out.println("		Protocol Name = " +protocol) ;
		out.println("		User Name = "+userName);
		out.println("		Password = " +password);
		out.println("		Host = "+host);
		out.println("		Port = " +port);
		out.println("		Resource = " + resource);
		out.close();

		return stringBuffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(!(obj instanceof URLData))
			return false;
		
		URLData that = (URLData) obj;
		return Equality.areEqual(this.getProtocol(), that.getProtocol())
			&& Equality.areEqual(this.getHost(), that.getHost())
			&& Equality.areEqual(this.getPort(), that.getPort())
			&& Equality.areEqual(this.getResource(), that.getResource())
			&& Equality.areEqual(this.getUserName(), that.getUserName())
			&& Equality.areEqual(this.getPassword(), that.getPassword());
	}
	
	@Override
	public int hashCode() {
		return (host + port).hashCode();
	}
}
