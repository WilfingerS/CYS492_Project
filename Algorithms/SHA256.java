import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SHA256 {
        /*For testing
        public static void main(String[] args) {
        String unhashed = "yourPassword";
        
        // Generate a new salt
        byte[] salt = createSalt();
        
        // Hash the password with the salt
        String securePassword = getSecurePassword(unhashed, salt);
        
        // Save the salt and the hash somewhere (e.g., a database)
        System.out.println("Secure Password: " + securePassword);
        System.out.println("Salt: " + Base64.getEncoder().encodeToString(salt));
        System.out.println(securePassword.length());
    
        // Example of saving and using them later
        String savedSalt = byteToStr(salt);
        String savedHash = securePassword;
        
        // Simulate retrieving and checking the hash
        boolean isSame = verifyUserPassword("yourPassword", savedHash, strToByte(savedSalt));
        System.out.println("Password match: " + isSame);
    }
    //*/

    // Hashes the password with the salt and returns the hash
    private static String getSecurePassword(String unhashed, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(unhashed.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generates a new random salt
    private static byte[] createSalt() {
        SecureRandom sr;
        byte[] salt = new byte[16];
        try {
            sr = SecureRandom.getInstanceStrong();
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return salt;
    }

    public static String byteToStr(byte[] salt){
        return Base64.getEncoder().encodeToString(salt);
    }

    public static byte[] strToByte(String str){
        return Base64.getDecoder().decode(str);
    }

    // Verifies a password against a hash and salt
    private static boolean verifyUserPassword(String password, String savedHash, byte[] salt) {
        // Recompute the hash with the given salt and compare it to the saved hash
        String hashedPassword = getSecurePassword(password, salt);
        return hashedPassword != null && hashedPassword.equals(savedHash);
    }
}
