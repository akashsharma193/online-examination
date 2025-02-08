package com.online.examination.utility;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.online.examination.entity.ExceptionEntity;
import com.online.examination.entity.User;
import com.online.examination.repository.ExceptionRepository;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailUtils {

	@Value("${email.username}")
	private String username;

	@Value("${email.password}")
	private String password;
	
	@Autowired
	private ExceptionRepository exceptionRepository;

	public Boolean sendEmail(String to, String subject, String body) {
		Boolean flag = Boolean.FALSE;

		try {
			Properties properties = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream stream = loader.getResourceAsStream("email.properties");
			properties.load(stream);

			Session session = Session.getInstance(properties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			Message message = new MimeMessage(session);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
//			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);
			flag = Boolean.TRUE;
		} catch (Exception ex) {
			ExceptionEntity data = new ExceptionEntity();
			data.setError(ex.getMessage());
			data.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			data.setTrac(ExceptionUtils.getStackTrace(ex));
			exceptionRepository.save(data);
		}
		return flag;

	}
	
	public String activationMail(User user) {
		String mail = "Dear [User's Name],\r\n"
				+ "\r\n"
				+ "Congratulations! Your registration with [Your Company Name] is almost complete. We're excited to have you on board!\r\n"
				+ "\r\n"
				+ "To activate your account and start exploring all the features we offer, please click the link below:\r\n"
				+ "\r\n"
				+ "[Activation Link]\r\n"
				+ "\r\n"
				+ "If you did not initiate this registration, please ignore this email. Your account will not be activated until you click the link above.\r\n"
				+ "\r\n"
				+ "Should you have any questions or need assistance, feel free to reach out to our support team at [Support Email or Contact Information].\r\n"
				+ "\r\n"
				+ "Welcome to the [Your Company Name] community!\r\n"
				+ "\r\n"
				+ "Best regards,\r\n[Your Company Name] Team";
		
		mail = mail.replace("[User's Name]", user.getName()).replace("[Your Company Name]", "Online-Examination").replace("[Support Email or Contact Information]", "kshsarswat@gmail.com");
		return mail;
	}
	
}
