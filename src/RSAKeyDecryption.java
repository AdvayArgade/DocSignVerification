import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.io.IOException;

public class RSAKeyDecryption {

    public static void main(String[] args) throws Exception {
        // Load the AES key
        SecretKey aesKey = AESKeyManager.loadAESKey();

        if (aesKey == null) {
            System.out.println("Failed to load AES key. Make sure it's generated.");
            return;
        }

        // Read the encrypted private key from the file
        byte[] encryptedPrivateKeyBytes = readEncryptedPrivateKeyFromFile("encrypted_private_key.dat");

        // Decrypt the private key using the AES key
        PrivateKey decryptedPrivateKey = decryptWithAES(encryptedPrivateKeyBytes, aesKey);

        if (decryptedPrivateKey != null) {
            System.out.println("Private key decrypted successfully.");
            // You can use the decryptedPrivateKey for further operations.
        } else {
            System.out.println("Failed to decrypt the private key.");
        }
    }

    public static byte[] readEncryptedPrivateKeyFromFile(String fileName) throws Exception {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PrivateKey decryptWithAES(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decryptedData);
        return keyFactory.generatePrivate(keySpec);
    }
}
