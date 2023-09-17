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
import java.util.Properties;
import java.util.stream.Collectors;
import static java.nio.file.Files.lines;

public class Signer {
    SignerView sv = new SignerView();
}


class SignerView extends JFrame implements ActionListener{
    String signedFilePath, fileText;
    boolean fileread = false;
    static String key;
    String recipientAddress;

    JLabel heading = new JLabel("Signing");
    JButton openFile = new JButton("Open a file");
    JLabel selectedFile = new JLabel("Selected file:");
    JLabel fileName = new JLabel("File name");
    JLabel enter_sender_address = new JLabel("Enter signer's email address.");
    JTextField saddress = new JTextField(20);
    JLabel enter_verifier_address = new JLabel("Enter verifier's email address.");
    JTextField vaddress = new JTextField(20);
    JButton send = new JButton("Send");

    public SignerView(){
        this.setTitle("Document Signing");
        this.setVisible(true);
        this.setSize(500, 600);
        this.setLayout(new FlowLayout());


        heading.setSize(30,20);
        heading.setLayout(new FlowLayout());
        this.add(heading);


        openFile.setSize(50, 10);
        openFile.setLayout(new FlowLayout());
        this.add(openFile);


        selectedFile.setLayout(new FlowLayout());
        selectedFile.setSize(50,10);
        this.add(selectedFile);


        fileName.setLayout(new FlowLayout());
        fileName.setSize(50,10);
        this.add(fileName);



        enter_sender_address.setSize(30,20);
        enter_sender_address.setLayout(new FlowLayout());
        this.add(enter_sender_address);


        this.add(saddress);


        enter_verifier_address.setSize(30,20);
        enter_verifier_address.setLayout(new FlowLayout());
        this.add(enter_verifier_address);


        this.add(vaddress);

        send.setSize(50, 10);
        send.setLayout(new FlowLayout());
        this.add(send);



        //----------------------File Reading-------------------------
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
                        } catch (IOException | NullPointerException ex) {
                            throw new RuntimeException(ex);
                        }
                    }


                }


                }

            }
        );


        //-----------Send a Mail-----------------
        send.addActionListener(this);

    }//constructor ends

    public void actionPerformed(ActionEvent e){
        if(e.getSource()==send){
            //Some basic checks

            if(saddress.getText().toString().equals(new String(""))){
                JOptionPane.showMessageDialog(this,
                        "Please provide the sender's email address!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(vaddress.getText().toString().equals(new String(""))){
                JOptionPane.showMessageDialog(this,
                        "Please provide the verifier's email address!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(fileread==false){
                JOptionPane.showMessageDialog(this, "Please select a file!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //--------------File Hashing---------------
            String fileHash = MD5FileHash.doFinal(signedFilePath);
            if(fileHash==""){
                JOptionPane.showMessageDialog(this,
                        "Oops! Error in hashing!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //--------------Encryption-----------------
            RSAFileEncryption rsafe = null;
            try {
                rsafe = new RSAFileEncryption();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            try {
                String encryptedHash = rsafe.encryptString(fileHash);
                key = rsafe.publicKey.toString();
                System.out.println(encryptedHash);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }


            //-------------actual mail sending--------------
            EmailSender emailSender = new EmailSender();
            String sender_addr_arg = saddress.getText();
            String verifier_addr_arg = vaddress.getText();
            try {
                emailSender.send(sender_addr_arg,"fcjbimretuvttjyq",
                        verifier_addr_arg,
                        "Please check this file.","Here is the hash of the file and the key to verify the attached file's integrity."
                        , signedFilePath, fileHash, key);
            }
            catch (SignerAddressException sae){
                JOptionPane.showMessageDialog(this,
                        "Enter a valid email address for the signer!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            catch (VerifierAddressException sae){
                JOptionPane.showMessageDialog(this,
                        "Enter a valid email address for the verifier!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

        }
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


class SignerAddressException extends AddressException{
    public SignerAddressException(String s){
        super(s);
    }
}

class VerifierAddressException extends AddressException{
    public VerifierAddressException(String s){
        super(s);
    }
}


class EmailSender {
    public void send(String from, String password, String to, String sub, String msg,
                     String filepath, String fileHash, String publicKey)
            throws VerifierAddressException, SignerAddressException{

        //Get properties object
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "*");
//         props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //get Session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });
        //compose message
        try {
            MimeMessage message = new MimeMessage(session);
            MimeMultipart multipart = new MimeMultipart();
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(sub);
            //Appending to the message.
            StringBuilder compositeMessage = new StringBuilder(msg);
            compositeMessage = compositeMessage.append(new String("\nFile Hash: " + fileHash));
            compositeMessage = compositeMessage.append(new String("\nYour key: " + publicKey));
            compositeMessage = compositeMessage.append(new String("\nRegards."));
            message.setText(compositeMessage.toString());
            MimeBodyPart file_attachment = new MimeBodyPart();
            try {
                file_attachment.attachFile(new File(filepath));
            } catch (IOException | MessagingException ex) {
                throw new RuntimeException(ex);
            }
            //Actually add the attachment in the mail
            try{
                multipart.addBodyPart(file_attachment);
            }catch (MessagingException me){
                System.out.println(me.getMessage());
            }
            message.setContent(multipart);
            //send message
            Transport.send(message);
            System.out.println("message sent successfully");
        } catch (MessagingException e) {
            System.out.println(e.getStackTrace() + e.getMessage());
        }

    }

}
//    public static void main(String[] args)
//    {
//        send("aparnatest745@gmail.com","fcjbimretuvttjyq","aparnatest745@gmail.com","hello","How are you?");
//
//    }



//    Properties properties = new Properties();
//                properties.put("mail.smtp.auth", true);
//                properties.put("mail.smtp.host", "smtp.gmail.com");
//                properties.put("mail.smtp.port", 465);
////                properties.put("mail.smtp.starttls.enable", true);
////                properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Use TLS 1.2 or a higher version
//                properties.put("mail.smtp.ssl.ciphersuites", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"); // Use a secure cipher suite
//                properties.put("mail.transport.protocol", "smtp");
//                properties.put("mail.smtp.ssl.enable", true);
//                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//                properties.put("mail.smtp.socketFactory.fallback", false);
//
//    Session session = Session.getInstance(properties, new Authenticator() {
//        @Override
//        protected PasswordAuthentication getPasswordAuthentication() {
//            return new PasswordAuthentication("advay.argade22@vit.edu", "12210523");
//        }
//
//        ;
//    });


    //Apparently this must be linked to javax/activation/DataSource
//    Message message = new MimeMessage(session);
//                try {
//        message.setSubject("Sample Email from my Java Program");
//    } catch (MessagingException ex) {
//        throw new RuntimeException(ex);
//    }
//                try {
//        Address addressTo = new InternetAddress("advay.argade22@vit.edu");
//        message.setRecipient(Message.RecipientType.TO, addressTo);
//        MimeMultipart multipart = new MimeMultipart();
//        MimeBodyPart attachment = new MimeBodyPart();
//        MimeBodyPart attachment_key = new MimeBodyPart();
//        try {
//            attachment.attachFile(new File(signedFilePath));
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        } catch (MessagingException ex) {
//            throw new RuntimeException(ex);
//        }
//        MimeBodyPart messageBodyPart = new MimeBodyPart();
//        try {
//            messageBodyPart.setContent("<h1>Email from my cool program!</h1>" + "key:" + rsafe.publicKey,
//                    "text/html");
//        } catch (MessagingException ex) {
//            throw new RuntimeException(ex);
//        }
//        try {
//            multipart.addBodyPart(messageBodyPart);
//        } catch (MessagingException ex) {
//            throw new RuntimeException(ex);
//        }
//        try {
//            multipart.addBodyPart(attachment);
//        } catch (MessagingException ex) {
//            throw new RuntimeException(ex);
//        }
//
//    } catch (AddressException ex) {
//        throw new RuntimeException(ex);
//    } catch (MessagingException ex) {
//        throw new RuntimeException(ex);
//
//    }
//                try {
//        Transport.send(message);
//        System.out.println("Email sent successfully.");
//    } catch (MessagingException ex) {
//        throw new RuntimeException(ex);
//    }
//
//}