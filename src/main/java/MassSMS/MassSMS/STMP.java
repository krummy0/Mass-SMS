package MassSMS.MassSMS;

import javax.mail.NoSuchProviderException;

import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class STMP {
	private String stmpHost;
	private int stmpPort;
	private String username;
	private String password;
	private String encryptionType; // "SSL", "TLS", or empty
	private boolean valid = true;

    public STMP(Map<String, String> stmpMap) {

        initialize(stmpMap.get("host"), Integer.parseInt(stmpMap.get("port")),
        		stmpMap.get("username"), stmpMap.get("password"),
        		stmpMap.get("enc"));
    }

    public STMP(String stmpHost, int stmpPort, String username, String password, String encryptionType) {
        initialize(stmpHost, stmpPort, username, password, encryptionType);
    }
    
    public String getHost() {
    	return stmpHost;
    }

    private void initialize(String stmpHost, int stmpPort, String username, String password, String encryptionType) {
        this.stmpHost = stmpHost;
        this.stmpPort = stmpPort;
        this.username = username;
        this.password = password;
        this.encryptionType = encryptionType;

        if (!verifyCredentials()) {
            valid = false;
        }
    }
	
	private boolean verifyCredentials() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", stmpHost);
		props.put("mail.smtp.port", stmpPort);

		if ("SSL".equalsIgnoreCase(encryptionType)) {
			props.put("mail.smtp.ssl.enable", "true");
		} else if ("TLS".equalsIgnoreCase(encryptionType)) {
			props.put("mail.smtp.starttls.enable", "true");
		}

		try {
			Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

			// Attempt to connect to the SMTP server
			Transport transport = session.getTransport("smtp");
			transport.connect();
			transport.close();
			return true;

		} catch (NoSuchProviderException e) {
			System.err.println("SMTP provider not found: " + e.getMessage());
		} catch (MessagingException e) {
			System.err.println("Failed to connect: " + e.getMessage());
		}
		return false;
	}
	
	public boolean isValid() {
		return valid;
	}

	public boolean sendEmail(String from, String to, String subject, String body) {
		if (!valid) {
			System.err.println("tried to send with incorrect STMP");
			return false;
		}
		
		// Set up the properties for the SMTP server
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", stmpHost);
		props.put("mail.smtp.port", stmpPort);

		if ("SSL".equalsIgnoreCase(encryptionType)) {
			props.put("mail.smtp.ssl.enable", "true");
		} else if ("TLS".equalsIgnoreCase(encryptionType)) {
			props.put("mail.smtp.starttls.enable", "true"); // Enable TLS
		}

		// Create a session with an authenticator
		Session session = Session.getInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

		try {
			// Create a MimeMessage
			Message message = new MimeMessage(session);
			String setFrom = '\"' + from + "\" <" + username + ">";
			message.setFrom(new InternetAddress(setFrom)); // Sender's email
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)); // Recipient's email
			message.setSubject(subject); // Email subject
			message.setText(body); // Email body

			// Send the email
			Transport.send(message);
			System.out.println("Email sent successfully!");

		} catch (MessagingException e) {
			if (e.getCause() instanceof javax.mail.AuthenticationFailedException) {
				System.err.println("Authentication failed: Please check your username and password.");
				valid = false;
				return false;
			} else {
				System.err.println("Error while sending email: " + e.getMessage());
				return false;
			}
		} catch (Exception e) {
			System.err.println("An unexpected error occurred: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
