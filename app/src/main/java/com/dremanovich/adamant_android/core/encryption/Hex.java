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
        //TODO: Refactor this. Check String Length
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
    }
}
