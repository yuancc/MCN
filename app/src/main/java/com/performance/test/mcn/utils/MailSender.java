package com.performance.test.mcn.utils;

import android.util.Log;

import com.performance.test.mcn.R;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
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
 * 发送邮件给多个接收者、抄送邮件
 * 
 */
public class MailSender {

	private static final String LOG_TAG = MailSender.class.getSimpleName();
	private static final int PORT = 25;

	/**
	 * 以文本格式发送邮件
	 * 
	 * 待发送的邮件的信息
	 */
//	public static boolean sendTextMail(String sender, String encryptPassword, String smtp, String subject, String content, String file,
//			String[] maillists) {
////		send();
//		if (maillists == null || maillists.length == 0 || ("".equals(maillists[0].trim()))) {
//			return false;
//		} else {
//			// Get system properties
//			Properties props = new Properties();
//
//			// Setup mail server
//			props.put("mail.smtp.host", "smtp.163.com");
//			props.put("mail.smtp.port", PORT);
//			// Get session
//			props.put("mail.smtp.auth", "true"); // 如果需要密码验证，把这里的false改成true
//			props.setProperty("mail.transport.protocol", "smtp");
//
//			// 判断是否需要身份认证
////			CustomizedAuthenticator authenticator = null;
////			if (true) {
////				// 如果需要身份认证，则创建一个密码验证器
////				authenticator = new CustomizedAuthenticator(sender, encryptPassword);
////			}
//			// 根据邮件会话属性和密码验证器构造一个发送邮件的session
//			Session sendMailSession = Session.getInstance(props);
//			try {
//				// 创建邮件对象
//				Message msg = new MimeMessage(sendMailSession);
//
//				msg.setSubject("测试结果");
//				// 设置邮件内容
//				msg.setText("yuancc");
//				// 设置发件人
//				msg.setFrom(new InternetAddress("vcloudvod12@163.com"));
//				Transport transport = sendMailSession.getTransport();
//				// 连接邮件服务器
//				transport.connect("vcloudvod12@163.com", "Yuancc163");
//				// 发送邮件
//				transport.sendMessage(msg, new Address[]{new InternetAddress("hzyuanchangcheng15@corp.netease.com")});
//				// 关闭连接
//				transport.close();
//				return true;
//			} catch (MessagingException ex) {
//				ex.printStackTrace();
//			}
//		}
//		return false;
//	}

	public static boolean sendTextMail(String sender, String password, String smtp, String subject, String content, String file,
			String[] maillists) {
//		send();
		if (maillists == null || maillists.length == 0 || ("".equals(maillists[0].trim()))) {
			Log.i(LOG_TAG, "send email list is null");
//			return false;
		} else {
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.163.com");
			props.put("mail.smtp.port", PORT);
			// Get session
			props.put("mail.smtp.auth", "true"); // 如果需要密码验证，把这里的false改成true
			props.setProperty("mail.transport.protocol", "smtp");
			Session sendMailSession = Session.getInstance(props);
			try {
				// 创建邮件对象
				Message mailMessage = new MimeMessage(sendMailSession);
				// 创建邮件发送者地址
				Address from = new InternetAddress(sender);
				// 设置邮件消息的发送者
				mailMessage.setFrom(from);
				// 创建邮件的接收者地址，并设置到邮件消息中
//				for (int i = 0; i < maillists.length; i++) {
//					// Message.RecipientType.TO属性表示接收者的类型为TO
//					mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(maillists[i]));
//				}
				// 设置邮件消息的主题
				mailMessage.setSubject(subject);
				// 设置邮件消息发送的时间
//				mailMessage.setSentDate(new Date());

				Multipart multipart = new MimeMultipart();
				BodyPart bodyPart = new MimeBodyPart();
				bodyPart.setText(content);
//				bodyPart.setText("欢迎使用MCN测试性能测试工具");
				multipart.addBodyPart(bodyPart);

				File attach = new File(file);
				if (attach.exists()) {
					MimeBodyPart attachPart = new MimeBodyPart();
					DataSource source = new FileDataSource(attach);
					attachPart.setDataHandler(new DataHandler(source));
					attachPart.setFileName(attach.getName());

					multipart.addBodyPart(attachPart);
				}
				mailMessage.setContent(multipart);
				MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
				mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
				mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
				mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
				mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
				mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
				CommandMap.setDefaultCommandMap(mc);

				Transport transport = sendMailSession.getTransport();
//				// 连接邮件服务器
				transport.connect(sender, password);
//				// 发送邮件
				transport.sendMessage(mailMessage, new Address[]{new InternetAddress("hzyuanchangcheng15@corp.netease.com")});
//				transport.send(mailMessage);
//				// 关闭连接
				transport.close();
				return true;
			} catch (MessagingException ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	/*private static void send() {
		try {
			Properties props = new Properties();
			// 开启debug调试
			props.setProperty("mail.debug", "true");
			// 发送服务器需要身份验证
			props.setProperty("mail.smtp.auth", "true");
			//设置邮件服务器主机名
			props.setProperty("mail.host", "smtp.163.com");
			// 发送邮件协议名称
			props.setProperty("mail.transport.protocol", "smtp");

			// 设置环境信息
			Session session = Session.getInstance(props);

			// 创建邮件对象
			Message msg = new MimeMessage(session);

			msg.setSubject("AndroidMail测试");
			// 设置邮件内容
			msg.setText("这是一封由大当家发送的邮件！");
			// 设置发件人
			msg.setFrom(new InternetAddress("vcloudvod12@163.com"));
			Transport transport = session.getTransport();
			// 连接邮件服务器
			transport.connect("vcloudvod12@163.com", "admin163");
			// 发送邮件
			transport.sendMessage(msg, new Address[]{new InternetAddress("hzyuanchangcheng15@corp.netease.com")});
			// 关闭连接
			transport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}*/
}
