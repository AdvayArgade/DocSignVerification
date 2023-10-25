import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
public class RSAFileEncryption2 {

    PrivateKey privateKey;
    String publicKey; // Store the public key as a string

    public RSAFileEncryption2(String publicKey) {
        this.publicKey = publicKey;
    }

    public RSAFileEncryption2() throws Exception {
        // Generate RSA key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // You can choose the key size in bits
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Get public and private keys
        privateKey = keyPair.getPrivate();
        publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public String encryptString(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKeyFromString(publicKey));

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

    private PublicKey getPublicKeyFromString(String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
    }
}
