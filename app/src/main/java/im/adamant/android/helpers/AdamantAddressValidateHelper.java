package im.adamant.android.helpers;

public class AdamantAddressValidateHelper {
    public static boolean validate(String address) {
        if (address == null || address.isEmpty()) { return false; }
        try {
            if (!"U".equalsIgnoreCase(address.substring(0, 1))) { return false; }
            if (address.length() < 16){return false;}
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

        return true;
    }
}
