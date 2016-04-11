package cn.eakay.commons.util.mail;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 
 * @author hymagic
 *
 */
public class MailSender {
	public static void sendMail(String subject, String content) {
		MailSenderInfo info = new MailSenderInfo();
		info.setSubject(subject + System.getenv("HOSTNAME"));
 		info.setContent(content);
		info.setToAddress("guoyang@shanhaishu.com.cn");
		sendTextMail(info);
	}

//	public static void sendMail(String content) {
//		MailSenderInfo info = new MailSenderInfo();
//		info.setSubject("IndexRank");
//		info.setContent(content);
//		info.setToAddress("guoyang@lashou-inc.com");
//		sendTextMail(info);
//	}
	public static void sendMail(Exception e) {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] elements = e.getStackTrace();
		for(StackTraceElement s:elements){
			sb.append(s.getClassName())
			.append(" ").append(s.getMethodName()).append("(")
			.append(s.getLineNumber()).append(")").append("<br>");
		}
		MailSenderInfo info = new MailSenderInfo();
		info.setSubject("WebSearch" + System.getenv("HOSTNAME"));
		info.setContent(sb.toString());
		info.setToAddress("guoyang@lashou-inc.com");
		sendHtmlMail(info);
	}

	public static boolean sendTextMail(MailSenderInfo mailInfo) {
		// 判断是否需要身份认证
		MailAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new MailAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		// Session sendMailSession =
		// Session.getDefaultInstance(pro,authenticator);
		Session sendMailSession = Session.getInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 创建邮件的抄送地址
			String[] cc = mailInfo.getccAddress();
			Address[] ccAddress;
			ccAddress = new Address[cc.length];
			for (int i = 0; i < cc.length; i++) {
				ccAddress[i] = new InternetAddress(cc[i]);
			}
			mailMessage.setRecipients(Message.RecipientType.CC, ccAddress);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件信息
	 * @throws UnsupportedEncodingException
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
		// 判断是否需要身份认证
		MailAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new MailAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		// Session sendMailSession =
		// Session.getDefaultInstance(pro,authenticator);
		Session sendMailSession = Session.getInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			// Message.RecipientType.TO属性表示接收者的类型为TO
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	
	public static void main(String[] args) {
		 MailSender.sendMail("shanhaishu", "111");
	}
}
