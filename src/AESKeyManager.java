import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.crypto.spec.SecretKeySpec;

public class AESKeyManager {

    private static final String AES_KEY_FILE = "aes_key.dat";

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // You can choose a different key size if needed
        return keyGenerator.generateKey();
    }

    public static SecretKey loadAESKey() {
        try (FileInputStream fis = new FileInputStream(AES_KEY_FILE)) {
            byte[] aesKeyData = new byte[fis.available()];
            fis.read(aesKeyData);
            return new SecretKeySpec(aesKeyData, "AES");
        } catch (IOException e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return null;
        }
    }

    public static void saveAESKey(SecretKey key) {
        try (FileOutputStream fos = new FileOutputStream(AES_KEY_FILE)) {
            byte[] aesKeyData = key.getEncoded();
            fos.write(aesKeyData);
        } catch (IOException e) {
            // Handle exceptions appropriately
            e.printStackTrace();
        }
    }
}
