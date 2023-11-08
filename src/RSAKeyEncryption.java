import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Base64;

public class RSAKeyEncryption {

    public static void main(String[] args) throws Exception {
        // Generate an RSA key pair
        KeyPair rsaKeyPair = generateRSAKeyPair();

        // Get the private key from the key pair
        PrivateKey privateKey = rsaKeyPair.getPrivate();

        // Convert the private key to a byte array
        byte[] privateKeyBytes = privateKey.getEncoded();

        System.out.println("Private key generated.");

        // Generate an AES key and save it
        SecretKey aesKey = AESKeyManager.generateAESKey();
        AESKeyManager.saveAESKey(aesKey);

        // Encrypt the private key bytes using AES
        byte[] encryptedPrivateKeyBytes = encryptWithAES(privateKeyBytes, aesKey);

        System.out.println("Private key encrypted with AES.");

        // Save the encrypted private key to a file
        saveEncryptedPrivateKeyToFile(encryptedPrivateKeyBytes, "encrypted_private_key.dat");
    }

    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // You can choose a different key size if needed
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] encryptWithAES(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static void saveEncryptedPrivateKeyToFile(byte[] data, String fileName) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(data);
        }
    }
}
