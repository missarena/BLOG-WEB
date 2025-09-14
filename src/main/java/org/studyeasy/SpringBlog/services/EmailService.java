package org.studyeasy.SpringBlog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringBlog.util.email.emailDetails;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private  String sender;
    private MailSender mailSender;

    public boolean sendSimpleEmail(emailDetails emaildetails){

        try{
         SimpleMailMessage message = new SimpleMailMessage();
         message.setFrom(sender);
         message.setTo(emaildetails.getRecipient());
         message.setText(emaildetails.getMsgbody());
         message.setSubject(emaildetails.getSubject());

         javaMailSender.send(message);
         return true;
     }catch(Exception e){
            return false;
        }
    }
}
