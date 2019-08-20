package com.elitecore.core.notification.email;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.notification.ConnectionStatus;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.TaskScheduler;

public class EmailNotifier {

	private String MODULE = "EMAIL-NTFR";
	
	private ConnectionStatus emailConnectionStatus;
	private EmailConfiguration configuration;
	private Session session;
	private Transport bus;
	private Properties props;
	
	public EmailNotifier(EmailConfiguration configuration, TaskScheduler taskScheduler) {
		this.configuration = configuration;
		taskScheduler.scheduleIntervalBasedTask(new EmailServerConnector()); 
	}
	
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initializing Email Notifier");
		props = new Properties();
		props.put("mail.smtp.host", configuration.getHost());
		props.put("mail.smtp.port", configuration.getPort());
		props.put("mail.debug", "false");
		props.put("mail.smtp.auth", configuration.isAuthRequired());
		props.put("mail.smtp.connectiontimeout", TimeUnit.SECONDS.toMillis(3));
			
		attemptNewConnection() ;
		LogManager.getLogger().info(MODULE, "Email Notifier initialized successfully");
		 
	}
	
	
	private void connect() throws MessagingException {
		if(configuration.isAuthRequired()) {
			Authenticator authenticator = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(configuration.getUserName(), configuration.getPassword());
			}
			};
			
			session = Session.getInstance(props, authenticator);
		} else {
			session = Session.getInstance(props);
		}

		bus = session.getTransport("smtp");
		bus.connect();
	}
	
	public boolean send(String subject, String description, List<String> recipients) {
		if(recipients == null || recipients.isEmpty()) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Canot send email notification. Reason: Email recipients not found");
			return false;
		}
		
		StringBuffer strRecipients = new StringBuffer();
		for(String recipient : recipients) {
			strRecipients.append(recipient);
			strRecipients.append(",");
		}
		return send(subject, description, strRecipients.toString());
	}
	
	@SuppressWarnings("static-access")
	public boolean send(String subject, String description, String recipient) {
		try {
			if(emailConnectionStatus != ConnectionStatus.CONNECTED){
				return false;
			}
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(configuration.getMailFrom()));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			
			setHTMLContent(msg, description);
			msg.saveChanges();
			bus.send(msg);
			return true;
			
			/*msg.setRecipients(Message.RecipientType.TO, address);
			msg.setRecipients(Message.RecipientType.CC,
			InternetAddress.parse(to, true));
			msg.setRecipients(Message.RecipientType.BCC,
			InternetAddress.parse(to, false));
			
			setMultipartContent(msg);
			msg.saveChanges();
			bus.sendMessage(msg, address);
			
			setFileAsAttachment(msg, "C:/WINDOWS/CLOUD.GIF");
			msg.saveChanges();
			bus.sendMessage(msg, address);
			
			setHTMLContent(msg);
			msg.saveChanges();
			bus.sendMessage(msg, msg.getAllRecipients());
			bus.close();*/

		} catch (MessagingException me) {
			LogManager.getLogger().error(MODULE, "Message sending failed. Reason: " + me);
			LogManager.getLogger().trace(MODULE, me);
		}
		return false;
	}

	// A simple, single-part text/plain e-mail.
	/*private void setTextContent(Message msg, String description) throws MessagingException {
		msg.setContent(description, "text/plain");
	}*/

	// A simple multipart/mixed e-mail. Both body parts are text/plain.
	/*private static void setMultipartContent(Message msg) throws MessagingException {
		MimeBodyPart p1 = new MimeBodyPart();
		p1.setText("This is part one of a test multipart e-mail.");

		MimeBodyPart p2 = new MimeBodyPart();
		p2.setText("This is the second part", "us-ascii");

		Multipart mp = new MimeMultipart();
		mp.addBodyPart(p1);
		mp.addBodyPart(p2);

		msg.setContent(mp);
	}*/

	// Set a file as an attachment. Uses JAF FileDataSource.
	/*private void setFileAsAttachment(Message msg, String filename) throws MessagingException {

		MimeBodyPart p1 = new MimeBodyPart();
		p1.setText("This is part one of a test multipart e-mail."
				+ "The second part is file as an attachment");

		MimeBodyPart p2 = new MimeBodyPart();
		FileDataSource fds = new FileDataSource(filename);
		p2.setDataHandler(new DataHandler(fds));
		p2.setFileName(fds.getName());

		Multipart mp = new MimeMultipart();
		mp.addBodyPart(p1);
		mp.addBodyPart(p2);

		msg.setContent(mp);
	}*/

	// Set a single part html content.
	private void setHTMLContent(Message msg, String description) throws MessagingException {
		msg.setDataHandler(new DataHandler(new HTMLDataSource(description)));
	}

	/*
	 * Inner class to act as a JAF datasource to send HTML e-mail content
	 */
	static class HTMLDataSource implements DataSource {
		private String html;

		public HTMLDataSource(String htmlString) {
			html = htmlString;
		}

		public InputStream getInputStream() throws IOException {
			if (html == null)
				throw new IOException("Null HTML");
			return new ByteArrayInputStream(html.getBytes());
		}

		public OutputStream getOutputStream() throws IOException {
			throw new IOException("This DataHandler cannot write HTML");
		}

		public String getContentType() {
			return "text/html";
		}

		public String getName() {
			return "JAF text/html dataSource to send e-mail only";
		}
	}
	
	public void stop() {
		if(bus != null) {
			
			try {
				bus.close();
			} catch (MessagingException e) {
				LogManager.getLogger().trace(MODULE, e) ;
			}
			
		}
	}
	
	private void attemptNewConnection() throws InitializationFailedException{
		
		ExecutorService service = Executors.newSingleThreadExecutor(new EliteThreadFactory(MODULE,MODULE + "-OPEN-CONN-THR", Thread.NORM_PRIORITY));
		Future<ConnectionStatus> future = service.submit(new ConnectionTask()) ;
		
		try {
			emailConnectionStatus = future.get(3, TimeUnit.SECONDS);
		
			if(emailConnectionStatus == ConnectionStatus.PERMANENT_FAILURE){
				throw new InitializationFailedException("Authentication failure while connecting to HOST: "
					+ configuration.getHost() + " PORT: "+ configuration.getPort() + " for USERNAME: "+ configuration.getUserName());
			} else if(emailConnectionStatus == ConnectionStatus.CONNECTION_FAILURE){
				throw new InitializationFailedException("Connection failure while connecting to HOST: "
					+ configuration.getHost() + " PORT: "+ configuration.getPort() + " for USERNAME: "+ configuration.getUserName());
			}
			
		} catch (TimeoutException e) {
			emailConnectionStatus = ConnectionStatus.CONNECTION_FAILURE;
			stopConnection() ;
			throw new InitializationFailedException("Connection timed out while connecting to HOST: "
				+ configuration.getHost() + " PORT: "+ configuration.getPort() + " for USERNAME: "+ configuration.getUserName(), e);
		
		} catch (Exception e) {
			emailConnectionStatus = ConnectionStatus.CONNECTION_FAILURE;
			stopConnection() ;
			throw new InitializationFailedException(e.getMessage(), e);
		}finally {
			service.shutdownNow();
		}
		
	}
	
	private class ConnectionTask  implements Callable<ConnectionStatus>{

		@Override
		public ConnectionStatus call(){
			
				try{
					connect();
					return ConnectionStatus.CONNECTED;
				} catch(AuthenticationFailedException e) {
					LogManager.getLogger().trace(MODULE,e);
					return ConnectionStatus.PERMANENT_FAILURE;
				} catch (MessagingException e) {
					LogManager.getLogger().trace(MODULE,e);
					return ConnectionStatus.CONNECTION_FAILURE;
				} 
			}
		
	}
	
	private void stopConnection(){
			
		ExecutorService service = Executors.newSingleThreadExecutor(new EliteThreadFactory(MODULE, MODULE + "-CLOSE-CONN-THR", Thread.NORM_PRIORITY));
		Future<ConnectionStatus> future = service.submit(new ConnectionStopTask()) ;
		
		try {
			future.get(2, TimeUnit.SECONDS) ;
		} catch (Exception e){
			LogManager.getLogger().trace(MODULE,e) ;
		} finally {
			service.shutdownNow();
		}
			
	}
	
	private class ConnectionStopTask implements Callable<ConnectionStatus>{

		@Override
		public ConnectionStatus call() throws MessagingException{
			stop();
			return ConnectionStatus.DISCONNECTED;
			
		}
		
	}
	
	private class EmailServerConnector extends BaseIntervalBasedTask{

		@Override
		public long getInitialDelay() {
			return 60;
		}
		
		@Override
		public long getInterval() {
			return 60;
		}

		@Override
		public boolean isFixedDelay() {
			return false;
		}
		
		@Override
		public void execute(AsyncTaskContext context) {
			try {
					if(emailConnectionStatus != ConnectionStatus.CONNECTED || 
							emailConnectionStatus != ConnectionStatus.PERMANENT_FAILURE){
						attemptNewConnection();
					}
					
			} catch (InitializationFailedException e) {
				LogManager.getLogger().error(MODULE, "Cannot connect to email server. Reason: " +e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
		
	}
	
}
