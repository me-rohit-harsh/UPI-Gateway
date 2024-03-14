package com.emailSender.Service;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Store;



@Service
public class EmailService {
	@Autowired	
    private JavaMailSender emailSender;
	@Async
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
    
    
    public boolean fetchEmails(String submittedUtr, Integer moneySent) throws Exception {
        // Configure email properties
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imap");
        properties.setProperty("mail.imap.host", "imap.gmail.com");
        properties.setProperty("mail.imap.port", "993");
        properties.setProperty("mail.imap.ssl.enable", "true");
        // Create session
        Session session = Session.getInstance(properties);
        Store store = session.getStore("imap");
        boolean utrFound = false; // Flag to track if the UTR is found
        try {
            // Connect to the IMAP server
            store.connect("paymentgatewayproject@gmail.com", "jbhsozifcuegjmpf");

            // Open the Inbox and Trash folders
            Folder inbox = store.getFolder("INBOX");
            Folder trash = store.getFolder("[Gmail]/Trash");
            inbox.open(Folder.READ_ONLY);

            // Fetch and process emails from the Inbox folder
            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                // Process each email message
//            	System.out.println(message.getFrom()[0]);
//            	System.out.println(message.getSubject());
                if (message.getSubject().equals("Received")
                        && message.getFrom()[0].toString().equals("paymentgatewayproject@gmail.com")) {
                    // Assume submittedUtr is the UTR you want to match against

                    // Retrieve and print the email body
                    Object content = message.getContent();
                    if (content instanceof String) {
                        // If the content is text/plain
                        String emailBody = (String) content;
//                        System.out.println(emailBody);
                        if (emailBody.contains(submittedUtr) && emailBody.contains(Integer.toString(moneySent))) {
                            utrFound = true;
                            // Move the email to the Trash folder
                            inbox.copyMessages(new Message[] { message }, trash);
                            System.out.println("UTR Found!!");
                            System.out.println("Email moved to Trash successfully.");
                            message.setFlag(Flags.Flag.DELETED, true);
                            // here i want to set the status
                            break;
                        }
                    } else if (content instanceof Multipart) {
                        // If the content is multipart (e.g., contains attachments)
                        Multipart multipart = (Multipart) content;
//                        System.out.println(multipart);
                        for (int i = 0; i < multipart.getCount(); i++) {
                            BodyPart bodyPart = multipart.getBodyPart(i);
                            if (bodyPart.isMimeType("text/plain")) {
                                // Get the text/plain part of the email body
                                String emailBody = (String) bodyPart.getContent();
                                if (emailBody.contains(submittedUtr) && emailBody.contains(Integer.toString(moneySent))) {
                                    utrFound = true;
                                    // Move the email to the Trash folder
                                    inbox.copyMessages(new Message[] { message }, trash);
                                    System.out.println("UTR Found!!");
                                    System.out.println("Email moved to Trash successfully.");
                                    message.setFlag(Flags.Flag.DELETED, true);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("Didn't Matched");
                }
            }
            // Close the connection
            inbox.close(false);
            return utrFound;
        } catch (MessagingException | IOException e) {
            // Handle connection errors and content retrieval errors
            // System.out.println("Error caught in emailServices");
            // e.printStackTrace(); // You can comment out this line if you don't want to
            // print the stack trace
        } finally {
            store.close();
        }
        return utrFound;
    }
    
    
    
}
