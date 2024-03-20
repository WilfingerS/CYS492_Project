// ChatGPT of version of ChaCha20 Stream Cipher

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.ChaCha20ParameterSpec;

public class ChaCha20 {

    public static void main(String[] args) throws Exception {
        // Generate a random key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("ChaCha20");
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();

        // Nonce should be 12 bytes
        byte[] nonce = new byte[12];
        System.arraycopy(key.getEncoded(), 0, nonce, 0, nonce.length);

        // Example plaintext
        byte[] plaintext = "Hello, World!".getBytes();

        // Initialize ChaCha20 Cipher for encryption
        Cipher cipher = Cipher.getInstance("ChaCha20");
        cipher.init(Cipher.ENCRYPT_MODE, key, new ChaCha20ParameterSpec(nonce, 1));
        
        // Encrypt the plaintext
        byte[] ciphertext = cipher.doFinal(plaintext);
        
        // Now decrypt
        cipher.init(Cipher.DECRYPT_MODE, key, new ChaCha20ParameterSpec(nonce, 1));
        byte[] decrypted = cipher.doFinal(ciphertext);
        
        // Show the decrypted message
        System.out.println(new String(decrypted));
    }
}

