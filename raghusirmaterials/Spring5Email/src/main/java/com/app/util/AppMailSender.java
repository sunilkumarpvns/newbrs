package com.app.util;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class AppMailSender {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public boolean sendEmail(String to,String subject,String text,FileSystemResource file) {
		boolean status=false;
		try {
			//1. Create MimeMessage
			MimeMessage message=mailSender.createMimeMessage();
			//2. create Helper class object
			MimeMessageHelper helper=new MimeMessageHelper(message, file!=null?true:false);
			
			//3.set details to message
			helper.setTo(to);
			//helper.setCc(String[]);
			//helper.setBcc(String[]);
			helper.setSubject(subject);
			helper.setText(text);
			helper.setFrom("javaraghu2018@gmail.com");
			
			//4. add attachment to MimeMessage
			helper.addAttachment(file.getFilename(), file);
			
			//5. send email
			mailSender.send(message);
			status=true;
		} catch (Exception e) {
			status=false;
			e.printStackTrace();
		}
		
		return status;
	}
	
}
