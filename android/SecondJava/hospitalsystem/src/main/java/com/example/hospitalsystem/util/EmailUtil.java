package com.example.hospitalsystem.util;


import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    public static String account = "15895523375@163.com";

    public static String password = "DNyhrue56zq4jpR4";

    public static String myEmailSMTPHost = "smtp.163.com";

    public static String randomVerify() {
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    public static void sendMail(String to, String code) {
        Properties props = new Properties(); // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");// 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);// 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true"); // 需要请求认证
        props.setProperty("mail.debug", "true");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(account));
            // 设置收件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // 设置邮件的主题
            message.setSubject("479医院预约挂号系统-邮箱验证");
            // 3.4设置邮件的正文
            message.setText("尊敬的用户您好：" +
                    "您正在进行邮箱验证，本次验证码为：" + code + "，请在5分钟内进行使用。" +
                    "如非本人操作，请忽略此邮件，由此给您带来的不便请谅解！--医院预约挂号平台");
            // 保存并生成最终的邮件内容
//            message.saveChanges();
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

}
