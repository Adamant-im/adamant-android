package im.adamant.android.core.encryption;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Hex {
    public static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (byte aHash : hash) {
            String hex = Integer.toHexString(0xff & aHash);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] encodeStringToHexArray(String string) {
        if (string == null){return new byte[0];}
        if (string.length() % 2 != 0){return new byte[0];}

        try {
            byte[] dstBytes = new byte[string.length() / 2];

            int sourceIndex = 0;
            int dstIndex = 0;
            while (sourceIndex < string.length()){
                String byteString = string.substring(sourceIndex, sourceIndex + 2);

                dstBytes[dstIndex] = (byte)Integer.parseInt(byteString, 16);

                sourceIndex += 2;
                dstIndex++;
            }

            return dstBytes;
        }catch (Exception ex) {
            ex.printStackTrace();
            return new byte[0];
        }
    }

    public static int[] getUnsignedBytes(byte[] bytes){
        if (bytes == null){ return new int[0];}

        int[] unsignedBytes = new int[bytes.length];

        for (int i = 0; i < bytes.length; i++){
            unsignedBytes[i] = bytes[i] & 0xff;
        }

        return unsignedBytes;
    }

    public static String unsignedBytesToString(int[] bytes){
        if (bytes == null){return "null";}

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for(int i = 0; i < bytes.length; i++){
            sb.append(Integer.toString(bytes[i]));
            sb.append(", ");
        }
        sb.append(']');

        return sb.toString();
    }

    public static String md5Hash(String data) {
        String hash = "";
        try {
            byte[] bytesOfMessage = data.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5KeyHash = md.digest(bytesOfMessage);
            hash = bytesToHex(md5KeyHash);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }
}
