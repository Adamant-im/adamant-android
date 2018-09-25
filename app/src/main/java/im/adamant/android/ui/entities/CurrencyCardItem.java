package im.adamant.android.ui.entities;

import java.math.BigDecimal;

public class CurrencyCardItem {
    private BigDecimal balance;
    private String address;
    private String titleString;
    private int titleResource;
    private int backgroundLogoResource;
    private int precision;
    private String abbreviation;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitleString() {
        return titleString;
    }

    public void setTitleString(String titleString) {
        this.titleString = titleString;
    }

    public int getTitleResource() {
        return titleResource;
    }

    public void setTitleResource(int titleResource) {
        this.titleResource = titleResource;
    }

    public int getBackgroundLogoResource() {
        return backgroundLogoResource;
    }

    public void setBackgroundLogoResource(int backgroundLogoResource) {
        this.backgroundLogoResource = backgroundLogoResource;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
