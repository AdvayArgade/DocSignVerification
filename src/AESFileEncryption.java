// import javax.crypto.Cipher;
// import javax.crypto.KeyGenerator;
// import javax.crypto.SecretKey;
// import javax.crypto.spec.SecretKeySpec;
// import java.util.Base64;
// import java.security.NoSuchAlgorithmException;
// import java.io.*;
// import java.nio.file.Files;
// import java.nio.file.Path;

// public class FileEncryptionAES {

//     private SecretKey secretKey;

//     public FileEncryptionAES() throws NoSuchAlgorithmException {
//         // Generate AES secret key
//         KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//         keyGenerator.init(128); // You can choose the key size (128, 192, or 256 bits)
//         secretKey = keyGenerator.generateKey();
//     }

//     public void encryptFile(String inputFilePath, String outputFilePath) throws Exception {
//         Cipher cipher = Cipher.getInstance("AES");
//         cipher.init(Cipher.ENCRYPT_MODE, secretKey);

//         byte[] inputBytes = Files.readAllBytes(Path.of(inputFilePath));
//         byte[] encryptedBytes = cipher.doFinal(inputBytes);

//         Files.write(Path.of(outputFilePath), encryptedBytes);
//     }

//     public void decryptFile(String inputFilePath, String outputFilePath) throws Exception {
//         Cipher cipher = Cipher.getInstance("AES");
//         cipher.init(Cipher.DECRYPT_MODE, secretKey);

//         byte[] encryptedBytes = Files.readAllBytes(Path.of(inputFilePath));
//         byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

//         Files.write(Path.of(outputFilePath), decryptedBytes);
//     }

//     public static void main(String[] args) throws Exception {
//         FileEncryptionAES aesEncryption = new FileEncryptionAES();

//         String inputFilePath = "gmat.pdf"; // Replace with your input file
//         String encryptedFilePath = "encrypted.pdf"; // Replace with your output encrypted file
//         String decryptedFilePath = "decrypted.pdf"; // Replace with your output decrypted file

//         // Encrypt the file
//         aesEncryption.encryptFile(inputFilePath, encryptedFilePath);

//         // Decrypt the file
//         aesEncryption.decryptFile(encryptedFilePath, decryptedFilePath);
//     }
// }
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;

public class AESFileEncryption {

    private SecretKey secretKey;

    public AESFileEncryption() throws NoSuchAlgorithmException {
        // Generate AES secret key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // You can choose the key size (128, 192, or 256 bits)
        secretKey = keyGenerator.generateKey();
    }

    public String encryptString(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decryptString(String encryptedString) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }

    public static void main(String[] args) throws Exception {
        AESFileEncryption aesEncryption = new AESFileEncryption();

        String plaintext = "This is a secret message.";
        String encryptedString = aesEncryption.encryptString(plaintext);
        String decryptedString = aesEncryption.decryptString(encryptedString);

        System.out.println("Original: " + plaintext);
        System.out.println("Encrypted: " + encryptedString);
        System.out.println("Decrypted: " + decryptedString);
    }
}
