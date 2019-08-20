package com.elitecore.core.server.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import com.elitecore.core.serverx.ServerContext;

public class EliteSystemDetail {
	
	private static String oid = ".1.3.6.1.4.1.21067.1";								  
	private static long startUpTime;
	private static String contact= "";
	private static String hostName="";
	private static String location = "";
	private static int supportedServices=64;
	private static String summary;
	
	public static void loadSystemDetail(ServerContext serverContext) {
		if(serverContext==null)
			return;
		startUpTime = serverContext.getServerStartUpTime();
		hostName = serverContext.getLocalHostName();
		location = serverContext.getServerHome();
		contact = serverContext.getContact();
		Description.name = serverContext.getServerName();
		Description.majorVersion  = serverContext.getServerMajorVersion();
		Description.version = serverContext.getServerVersion();
		Description.revision = serverContext.getSVNRevision();
		Description.strReleaseDate = serverContext.getReleaseDate();
	}
	private static class Description{
		private static String name ="";
		private static String majorVersion= "";
		private static String os = System.getProperty("os.name");
		private static String summary;
		private static String version;
		private static String revision;
		private static String strReleaseDate;
		
		public static String getSummary() {
			if(summary==null){
				
				StringBuilder responseBuilder = new StringBuilder();
				responseBuilder.append(name+" ");
				responseBuilder.append(majorVersion+" ");
				responseBuilder.append("("+hostName+") ");
				responseBuilder.append("(version "+version+" "+revision+" ("+os+")"+"("+strReleaseDate+"))");
				
				summary  = responseBuilder.toString();
			}
			return summary;
				
			
		}
	}
	public static long getStartUpTime() {
		return startUpTime;
	}
	
	public static String getContact() {
		return contact;
	}
	
	public static String getHostName() {
		return hostName;
	}
	
	public static String getLocation() {
		return location;
	}
	
	public static int getSupportedServices() {
		return supportedServices;
	}

	public static String getOID() {
		return oid;
	}

	
	public static String getDescription() {
		return Description.getSummary();
	}
	
	public static String getSummary(){
		
		if(summary==null){
			StringWriter sb = new StringWriter();
			PrintWriter out = new PrintWriter(sb);
			out.println();
			out.println(Description.getSummary());
			out.println();
			out.println("StartUp Time : "+new Date(startUpTime));
			out.println("Contact      : "+contact);
			out.println("Location     : "+location);
			out.println("Services     : "+supportedServices+" (OSLayer - Application)");
			out.close();
			summary = sb.toString();
		}
		return summary;
	}

}
