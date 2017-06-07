package com.sunshine.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 邮件发送工具类
 * @author 
 *
 */
public class MailUtils {
	/**
	 * 发送邮件的方法
	 * @param to	收件人
	 * @param title	邮件标题
	 * @param areas	邮件正文
	 * @param cc	抄送人
	 * @param bcc	密送人
	 */
//	public static void sendMail(String to,String code){
	public static void sendMail(String to,String title,String areas,String cc,String bcc){
		/**
		 * 1.获得一个Session对象.
		 * 2.创建一个代表邮件的对象Message.
		 * 3.发送邮件Transport
		 */
		// 1.获得连接对象
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.126.com");
		props.put("mail.smtp.port","25");   
		props.put("mail.smtp.auth", "true"); 
//		props.setProperty("mail.smtp.host", "smtp.126.com");
		Session session = Session.getInstance(props, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("xupengfei151369@126.com", "feifei1314");
			}
			
		});
		// 2.创建邮件对象:
		Message message = new MimeMessage(session);
		// 设置发件人:
		try {
			message.setFrom(new InternetAddress("xupengfei151369@126.com"));
			// 设置收件人:
			message.addRecipient(RecipientType.TO, new InternetAddress(to));
			// 抄送 CC   密送BCC
			if(cc != ""){
				message.setRecipient(RecipientType.CC, new InternetAddress(cc));
			}
			if(bcc != ""){
				message.setRecipient(RecipientType.BCC, new InternetAddress(bcc));
			}			
			// 设置标题
			message.setSubject(title);
			// 设置邮件正文:
//			message.setContent("<h1>购物天堂传智商城官方激活邮件!点下面链接完成激活操作!</h1><h3><a href='http://localhost:8080/shopping/user_active.do?code="+code+"'>http://192.168.10.29:8080/shopping/user_active.do?code="+code+"</a></h3>", "text/html;charset=UTF-8");
			message.setContent(areas, "text/html;charset=UTF-8");
			// 3.发送邮件:
			Transport.send(message);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
//		sendMail("1048593688@qq.com");
	}
}
