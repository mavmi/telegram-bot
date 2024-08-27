package mavmi.telegram_bot.rocketchat.utils;

import org.springframework.lang.Nullable;

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

    public static String generateRandomString() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Nullable
    public static String[] splitByFirstSpace(String line) {
        char c = ' ';
        int lineSize = line.length();

        int end1 = 0;
        while (end1 < lineSize && line.charAt(end1) != c) {
            end1++;
        }
        if (end1 == lineSize) {
            return null;
        }

        int begin2 = end1;
        while (begin2 < lineSize && line.charAt(begin2) == c) {
            begin2++;
        }
        if (begin2 == lineSize) {
            return null;
        }

        return new String[] {
                line.substring(0, end1),
                line.substring(begin2)
        };
    }
}
