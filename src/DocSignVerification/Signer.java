package DocSignVerification;

import java.security.*;
import javax.crypto.*;
import java.util.Base64;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;
import static java.nio.file.Files.lines;

public class Signer {
    SignerView sv = new SignerView();
}

class SignerView extends JFrame{
    String signedFilePath, fileText;
    boolean fileread = false;
    static String key;
    public SignerView(){
        this.setTitle("Document Signing");
        this.setVisible(true);
        this.setSize(500, 600);
        this.setLayout(new FlowLayout());

        JLabel heading = new JLabel("Signing");
        heading.setSize(30,20);
        heading.setLayout(new FlowLayout());
        this.add(heading);

        JButton openFile = new JButton("Open a file");
        openFile.setSize(50, 10);
        openFile.setLayout(new FlowLayout());
        this.add(openFile);

        JLabel selectedFile = new JLabel("Selected file:");
        selectedFile.setLayout(new FlowLayout());
        selectedFile.setSize(50,10);
        this.add(selectedFile);

        JLabel fileName = new JLabel("File name");
        fileName.setLayout(new FlowLayout());
        fileName.setSize(50,10);
        this.add(fileName);


        JLabel enter_sender_address = new JLabel("Enter signer's email address.");
        enter_sender_address.setSize(30,20);
        enter_sender_address.setLayout(new FlowLayout());
        this.add(enter_sender_address);

        JTextField saddress = new JTextField(20);
        this.add(saddress);

        JLabel enter_verifier_address = new JLabel("Enter verifier's email address.");
        enter_verifier_address.setSize(30,20);
        enter_verifier_address.setLayout(new FlowLayout());
        this.add(enter_verifier_address);

        JTextField vaddress = new JTextField(20);
        this.add(vaddress);

        JButton send = new JButton("Send");
        send.setSize(50, 10);
        send.setLayout(new FlowLayout());
        this.add(send);



        openFile.addActionListener (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int r = fileChooser.showSaveDialog(null);
                // if the user selects a file
                if (r == JFileChooser.APPROVE_OPTION)

                {
                    // set the label to the path of the selected file
                    fileName.setText(fileChooser.getSelectedFile().getName());
                    File fileobj = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    System.out.println(fileChooser.getSelectedFile().getAbsolutePath());
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    signedFilePath = filePath;
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new FileReader(filePath));
                        fileread = true;
                        fileText = br.lines().collect(Collectors.joining());
                        System.out.println(fileText);


                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    finally {
                        try {
                            br.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }


                }


                }

            }
        );


        //-----------Send a Mail-----------------
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileread==false) return;
                //Actually show a message that no file is selected.

                //--------------Encryption-----------------
                RSAFileEncryption rsafe = null;
                try {
                    rsafe = new RSAFileEncryption();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    rsafe.encryptString(fileText);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", true);
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.port", 465);
//                properties.put("mail.smtp.starttls.enable", true);
//                properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Use TLS 1.2 or a higher version
                properties.put("mail.smtp.ssl.ciphersuites", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"); // Use a secure cipher suite
                properties.put("mail.transport.protocol", "smtp");
                properties.put("mail.smtp.ssl.enable", true);
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.put("mail.smtp.socketFactory.fallback", false);

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("advay.argade22@vit.edu", "12210523");
                    }

                    ;
                });


                //Apparently this must be linked to javax/activation/DataSource
                Message message = new MimeMessage(session);
                try {
                    message.setSubject("Sample Email from my Java Program");
                } catch (MessagingException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    Address addressTo = new InternetAddress("advay.argade22@vit.edu");
                    message.setRecipient(Message.RecipientType.TO, addressTo);
                    MimeMultipart multipart = new MimeMultipart();
                    MimeBodyPart attachment = new MimeBodyPart();
                    MimeBodyPart attachment_key = new MimeBodyPart();
                    try {
                        attachment.attachFile(new File(signedFilePath));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (MessagingException ex) {
                        throw new RuntimeException(ex);
                    }
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    try {
                        messageBodyPart.setContent("<h1>Email from my cool program!</h1>" + "key:" + rsafe.publicKey,
                                "text/html");
                    } catch (MessagingException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        multipart.addBodyPart(messageBodyPart);
                    } catch (MessagingException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        multipart.addBodyPart(attachment);
                    } catch (MessagingException ex) {
                        throw new RuntimeException(ex);
                    }

                } catch (AddressException ex) {
                    throw new RuntimeException(ex);
                } catch (MessagingException ex) {
                    throw new RuntimeException(ex);

                }
                try {
                    Transport.send(message);
                    System.out.println("Email sent successfully.");
                } catch (MessagingException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

    }
        public static String readFileAsText(String path) throws Exception{
            StringBuilder sb = new StringBuilder();
            sb = new StringBuilder(Arrays.toString(Files.readAllBytes(Paths.get(path))));
            return sb.toString();
        }
}


class RSAFileEncryption {
    PublicKey publicKey;
    PrivateKey privateKey;
        public RSAFileEncryption() throws Exception {
            // Generate RSA key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // You can choose the key size in bits
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Get public and private keys
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
            SignerView.key = publicKey.toString();
            // Input plaintext string

            // Encrypt and decrypt the string
//            String encryptedString = encryptString(plaintext, publicKey);
//            String decryptedString = decryptString(encryptedString, privateKey);
//
//            System.out.println("Original Plaintext: " + plaintext);
//            System.out.println("Encrypted String: " + encryptedString);
//            System.out.println("Decrypted String: " + decryptedString);
        }

        public String encryptString(String plaintext) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        public String decryptString(String encryptedString) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes);
        }
}
