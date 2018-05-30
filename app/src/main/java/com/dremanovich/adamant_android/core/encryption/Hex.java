package com.dremanovich.adamant_android.core.encryption;

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
}
