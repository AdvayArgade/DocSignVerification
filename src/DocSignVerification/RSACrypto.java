package DocSignVerification;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import java.io.*;
import java.util.Base64;

public class RSACrypto{

    public static PrivateKey privateKey;
    private static PublicKey publicKey;

    public static void main(String[] args) throws Exception {
        try {
//            KeyPair keyPair = generateRSAKeyPair();
//            PublicKey publicKey = keyPair.getPublic();
//            RSACrypto.publicKey = publicKey;
//            PrivateKey privateKey = keyPair.getPrivate();
//
//            // Store the private key in a file
//            storePrivateKeyToFile(privateKey, "private_key.pem");
//
//            String originalMessage = "9c76f4a7bc633a42779723a0485c3ac1";
//
//            // Encrypt the message using the public key
//            String encryptedMessage = encrypt(originalMessage);
//
//            System.out.println("Original Message: " + originalMessage);
//            System.out.println("Encrypted Message: " + encryptedMessage);

        // Decrypt the message using the private key
        PrivateKey privateKey = loadPrivateKeyFromFile("private_key.pem");
        String cipherText = "mofdqKnfCzdF/cWYVHUFKTPdC5jgnU+zpl9BQik+zbxBPwz3OgEnGX5+VhucwzSKCTDRuRMBavhWHRpAma/KfRf4TfLpJ36/nuTnU1iLkaEIWxxXk2c4/oAohwdfcdrp5qieNSbfc40uA1oJZcm/+V5TKtoMBS0Rce0Nh+/6RaojThRAwoJmnnne4P9srckuiteKRf8EF7dPWSa/c+5DfK58ryGMlRtXOqaHHMJUvQUxP4aZ7ZnkYkjeUpUC3GSQxinELOrc9wf3nx4ZW7cMfJwb15+vfYuFSAHb3/TsOEfwITuDIz3iXBLFCofj9sjxQMyGCM1xFoYFM/gxxcZaXA==";
        String decryptedMessage = decrypt(privateKey,cipherText);
        System.out.println("Decrypted Message: " + decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String doFinal(String originalMessage) throws Exception {
        KeyPair keyPair = generateRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        RSACrypto.publicKey = publicKey;
        PrivateKey privateKey = keyPair.getPrivate();

        // Store the private key in a file
        storePrivateKeyToFile(privateKey, "private_key.pem");

        // Encrypt the message using the public key
        String encryptedMessage = encrypt(originalMessage);

        System.out.println("Original Message: " + originalMessage);
        System.out.println("Encrypted Message: " + encryptedMessage);

        return encryptedMessage;
    }


    public RSACrypto() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        publicKey = keyPairGenerator.generateKeyPair().getPublic();
        privateKey = keyPairGenerator.generateKeyPair().getPrivate();
    }

    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static String encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, RSACrypto.publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(PrivateKey privateKey, String encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static void storePrivateKeyToFile(PrivateKey privateKey, String fileName) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(keyBytes);
        fos.close();
    }

    public static PrivateKey loadPrivateKeyFromFile(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        byte[] keyBytes = new byte[fis.available()];
        fis.read(keyBytes);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        privateKey = keyFactory.generatePrivate(keySpec);
        return keyFactory.generatePrivate(keySpec);
    }
}