package bbc.umarket.umarketapp2.Helper;

public class FeatProdtHelperClass {
    String imageUrl, pName, pOverallRate, pPrice, pID, pSellerID;

    public FeatProdtHelperClass() { }

    public FeatProdtHelperClass(String imageUrl, String pName, String pOverallRate, String pPrice, String pID, String pSellerID) {
        this.imageUrl = imageUrl;
        this.pName = pName;
        this.pOverallRate = pOverallRate;
        this.pPrice = pPrice;
        this.pID = pID;
        this.pSellerID = pSellerID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getpSellerID() { return pSellerID;}

    public void setpSellerID(String pSellerID) {this.pSellerID = pSellerID;}
}
