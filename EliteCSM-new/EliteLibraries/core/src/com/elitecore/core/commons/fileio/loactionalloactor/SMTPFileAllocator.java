package com.elitecore.core.commons.fileio.loactionalloactor;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.smtp.SMTPClient;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class SMTPFileAllocator extends BaseCommonFileAllocator {

	private static final String MODULE = "SMTP_FILE_ALLOCATOR";

	private SMTPClient smtpClient;
	private String hostName;
	private String fromName;
	private String fromAddress;
	private String toAddress;
	private String toName;
	

	public boolean disconnect() {
		try{
			if(smtpClient!=null && smtpClient.isConnected())
				smtpClient.disconnect();
			return true;
		}catch (Exception e) {
			return false;
		}
	}

	public boolean connect() throws FileAllocatorException {

		if (getSMTPClient() == null) {
			smtpClient = new SMTPClient();
			smtpClient.setConnectTimeout(10000);
			try {
				smtpClient.connect(getHostName(), getPort());
				boolean success = smtpClient.login();
				if(!success) {
					throw new FileAllocatorException("SMTP login failed to " + getAddress() + ":" + getPort() + ". Reason: Invalid Username or Password");
				}
			} catch (SocketException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in getting Connection to Server " + getAddress() + ":" + getPort() + ", Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (IOException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in getting Connection to Server " + getAddress() + ":" + getPort() + ", Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		if (!getPermission()) {
			disconnect();
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "SMTP server : " + getToAddress() + ":" + getPort() + " refused connection. Reason : "+ smtpClient.getReplyString());
			LogManager.getLogger().trace(MODULE, "SMTP server refused connection. Reason : "+ smtpClient.getReplyString());
			return false;
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "SMTP server status: " + getToAddress()+ ":" + getPort() + " connected. Code: "+ smtpClient.getReplyString());
			return true;
		}
	}

	public boolean getPermission() {
		return(FTPReply.isPositiveCompletion(getSMTPClient().getReplyCode())); 
	}
	
	public File transferFile(File file) throws FileAllocatorException {

		try {
			
			file = manageExtension(file, BaseCommonFileAllocator.UIP_EXTENSION, originalExtension, null);
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Sending file with SMTP configuration, \n"+
				"File Path 		: "+file.getAbsolutePath()+"\n"+
				"File Name 		: "+file.getName()+"\n"+
				"Host Name		: "+getHostName()+"\n"+
				"From Address   : "+getFromAddress()+"\n"+
				"From Name 		: "+getFromName()+"\n"+
				"To Address  	: "+getToAddress()+"\n"+
				"To Name   		: "+getToName()+"\n");
			}
			
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(file.getAbsolutePath());
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("CDR File");
			attachment.setName(file.getName());

			// Create the email message
			MultiPartEmail email = new MultiPartEmail();
			email.setHostName(getHostName());
			email.setFrom(getFromAddress(), getFromName());
			email.setSubject(file.getName());
			email.setMsg("CDR File");
			email.addTo(getToAddress(), getToName());
			// add the attachment
			email.attach(attachment);
			// send the email
			email.send();
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Mail Send Successfully");
			}
			return file;
		} catch (EmailException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Mail Sending Failed to : \n"+
				"File Path 		: "+file.getAbsolutePath()+"\n"+
				"File Name 		: "+file.getName()+"\n"+
				"Host Name		: "+getHostName()+"\n"+
				"From  Address  : "+getFromAddress()+"\n"+
				"From Name 		: "+getFromName()+"\n"+
				"To Address  	: "+getToAddress()+"\n"+
				"To Name   		: "+getToName()+"\n");
			}
			
			LogManager.getLogger().trace(MODULE, e);
			throw new FileAllocatorException(e);
		}
	}


	private String getHostName() {
		if(hostName == null) {
			if(getAddress().contains("@")) {
				hostName = getAddress().substring(getAddress().indexOf("@") + 1);
				if(hostName.contains(","))
					hostName = hostName.substring(0, hostName.indexOf(','));
			} else {
				hostName = getAddress();
			}
		}
		return hostName; 
	}

	private String getFromName() {

		if(fromName == null) {
			if(getUser().contains("@"))
				fromName = getUser().substring(0,getUser().indexOf("@"));
			else 
				fromName = getUser();
		}
		return fromName;
	}

	private String getFromAddress() {
		if(fromAddress == null){
			if(getUser().contains(",")){
				fromAddress = getUser().substring(0, getUser().indexOf(","));
			}else{
				fromAddress = getUser();
			}
		}
		return fromAddress;
	}

	private String getToAddress() {
		if(toAddress == null) {
			if(getDestinationLocation().contains(","))
				toAddress = getDestinationLocation().substring(0, getDestinationLocation().indexOf(","));
			else
				toAddress = getDestinationLocation();
		}
		return toAddress;
	}


	private String getToName() {
		if(toName == null) {
			if(getDestinationLocation().contains("@"))
				toName = getDestinationLocation().substring(0,getDestinationLocation().indexOf("@"));
			else 
				toName = getDestinationLocation();
		}
		return toName;
	}
	
	public SMTPClient getSMTPClient() {
		return smtpClient;
	}
}
