package com.cafe.service;

public interface EmailService {

	public boolean sendEmail(String message, String subject, String to);

}
