package im.adamant.android.markdown;

import java.util.Objects;

public class AdamantAddressEntity {
    private String address;
    private String label = "";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return address + " : " + Objects.toString(label);
    }
}
