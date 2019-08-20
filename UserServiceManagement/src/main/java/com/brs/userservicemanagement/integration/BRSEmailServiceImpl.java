package com.brs.userservicemanagement.integration;

import java.nio.charset.StandardCharsets;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.brs.beans.common.Mail;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class BRSEmailServiceImpl implements BRSEmailService {
	@Autowired
	private JavaMailSenderImpl mailSender;
	@Autowired
    private Configuration freemarkerConfig;

	@Override
	public String sendEmail(Mail mail){
		try{
			MimeMessage msg=mailSender.createMimeMessage();
			MimeMessageHelper helper=new MimeMessageHelper(msg,
	                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
	                StandardCharsets.UTF_8.name());
			helper.setTo(mail.getTo());
			helper.setFrom(mail.getFrom());
			helper.setSubject(mail.getSubject());
			
			Template t = freemarkerConfig.getTemplate("templates/email-template.ftl");
	        String html = FreeMarkerTemplateUtils.processTemplateIntoString(t,mail.getData());
	        
			helper.setText(html, true);
			mailSender.send(msg);
			return "Email sent!.";
		}catch(Exception e){
			return "Error in sending email!."+e.getMessage();
		}
		
	}

}
