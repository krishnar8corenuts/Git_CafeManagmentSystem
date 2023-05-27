package com.cafe.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@CrossOrigin
public class EmailServiceImpl implements EmailService {

	@Override
	public boolean sendEmail(String message, String subject, String to) {

		boolean f = false;

		String from = "ankushchourasiya0101@gmail.com";

		// Variable for gmail
		String host = "smtp.gmail.com";

		// get the system properties
		Properties prop = new Properties();
		// setting important information to properties object
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", host);
		prop.put("mail.smtp.port", 587);
		prop.put("mail.smtp.ssl.trust", host);

		// Step 1: to get the session object..
		Session session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("ankushchourasiya0101@gmail.com", "hyxoodlzmhzrcblj");
			}

		});

		session.setDebug(true);

		// Step 2 : compose the message [text,multi media]
		MimeMessage m = new MimeMessage(session);

		try {

			// from email
			m.setFrom(from);

			// adding recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to message
			m.setSubject(subject);

			// adding text to message
			m.setText(message);

			// send

			// Step 3 : send the message using Transport class
			Transport.send(m);

			log.info("Sent success...................");
			f = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;

	}

}
