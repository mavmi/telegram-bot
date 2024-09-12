package mavmi.telegram_bot.rocketchat.utils;

import java.security.MessageDigest;
import java.util.UUID;

public abstract class Utils {

    private static final String HASH_ALGORITHM = "SHA256";

    public static String calculateHash(String input) {
        try{
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder output = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String tmp = Integer.toHexString(0xff & hash[i]);
                if (tmp.length() == 1) {
                    output.append('0');
                }
                output.append(tmp);
            }

            return output.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static long calculateCommandHash(String command, long timestamp) {
        long output = 0;

        for (int i = 0; i < command.length(); i++) {
            output += (long) command.charAt(i);
        }

        return output + timestamp;
    }

    public static String generateRandomString() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
