package com.skeds.android.phone.business.Utilities.REST;

/**
 * Created by Mikhail on 25.08.2014.
 */

import java.io.File;
import java.security.Security;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";


    static {
        Security.addProvider(new JSSEProvider());
    }

    public static void sendWithAttachment(String subject, String messageBody, List<File> files) {
        String to = "zisman.mikhail.exadel@gmail.com";//change accordingly
        //from address
        final String user = "zisman.mikhail.exadel@gmail.com";//change accordingly
        final String password = "zis183313";//change accordingly
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        //1) get the session object
        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        //2) compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            //3) create MimeBodyPart object and set your message content
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(messageBody);

            //5) create Multipart object and add MimeBodyPart objects to this object
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);

            for (File file : files) {
                MimeBodyPart fileBodyPart = new MimeBodyPart();
                //Location of file to be attached
                String fullFilename = file.getAbsolutePath();//change accordingly
                DataSource source = new FileDataSource(fullFilename);
                fileBodyPart.setDataHandler(new DataHandler(source));
                String fileName = file.getName();
                fileBodyPart.setFileName(fileName);
                multipart.addBodyPart(fileBodyPart);
            }

            //6) set the multiplart object to the message object
            message.setContent(multipart);
            //7) send message
            Transport.send(message);
            System.out.println("MESSAGE SENT....");
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }


//
//    public GMailSender(String user, String password) {
//        this.user = user;
//        this.password = password;
//
//        Properties props = new Properties();
//        props.setProperty("mail.transport.protocol", "smtp");
//        props.setProperty("mail.host", mailhost);
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.port", "465");
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class",
//                "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.socketFactory.fallback", "false");
//        props.setProperty("mail.smtp.quitwait", "false");
//
//        session = Session.getDefaultInstance(props, this);
//    }
//
//    protected PasswordAuthentication getPasswordAuthentication() {
//        return new PasswordAuthentication(user, password);
//    }
//
//    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {
//        try{
//            MimeMessage message = new MimeMessage(session);
//            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
//            message.setSender(new InternetAddress(sender));
//            message.setSubject(subject);
//            message.setDataHandler(handler);
//            if (recipients.indexOf(',') > 0)
//                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
//            else
//                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
//            Transport.send(message);
//        }catch(Exception e){
//
//        }
//    }
//
//    public class ByteArrayDataSource implements DataSource {
//        private byte[] data;
//        private String type;
//
//        public ByteArrayDataSource(byte[] data, String type) {
//            super();
//            this.data = data;
//            this.type = type;
//        }
//
//        public ByteArrayDataSource(byte[] data) {
//            super();
//            this.data = data;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//
//        public String getContentType() {
//            if (type == null)
//                return "application/octet-stream";
//            else
//                return type;
//        }
//
//        public InputStream getInputStream() throws IOException {
//            return new ByteArrayInputStream(data);
//        }
//
//        public String getName() {
//            return "ByteArrayDataSource";
//        }
//
//        public OutputStream getOutputStream() throws IOException {
//            throw new IOException("Not Supported");
//        }
//    }
}