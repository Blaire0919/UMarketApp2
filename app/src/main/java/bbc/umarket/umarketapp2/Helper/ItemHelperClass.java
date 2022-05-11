package bbc.umarket.umarketapp2.Helper;

public class ItemHelperClass {
    String imageUrl, pCategory, pName, pOverallRate, pPrice, pID;

    public ItemHelperClass() {
    }

    public ItemHelperClass(String imageUrl, String pCategory, String pName, String pOverallRate, String pPrice, String pID) {
        this.imageUrl = imageUrl;
        this.pCategory = pCategory;
        this.pName = pName;
        this.pOverallRate = pOverallRate;
        this.pPrice = pPrice;
        this.pID = pID;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getpCategory() {
        return pCategory;
    }

    public void setpCategory(String pCategory) {
        this.pCategory = pCategory;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpOverallRate() {
        return pOverallRate;
    }

    public void setpOverallRate(String pOverallRate) {
        this.pOverallRate = pOverallRate;
    }

    public String getpPrice() {
        return pPrice;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }
}

