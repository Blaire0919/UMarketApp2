package bbc.umarket.umarketapp2.Helper;

public class Model {

    String imageUrl, pID, pBrand, pCategory, pSubCategory, pCondition, pDescription, pHandlingFee, pName, pPrice, pStock, pSellerID, pSoldItems, pOverallRate, pScore;

    public Model() {    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    public String getpBrand() {
        return pBrand;
    }

    public void setpBrand(String pBrand) {
        this.pBrand = pBrand;
    }

    public String getpCategory() {
        return pCategory;
    }

    public void setpCategory(String pCategory) {
        this.pCategory = pCategory;
    }

    public String getpSubCategory() {
        return pSubCategory;
    }

    public void setpSubCategory(String pSubCategory) {
        this.pSubCategory = pSubCategory;
    }

    public String getpCondition() {
        return pCondition;
    }

    public void setpCondition(String pCondition) {
        this.pCondition = pCondition;
    }

    public String getpDescription() {
        return pDescription;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public String getpHandlingFee() {
        return pHandlingFee;
    }

    public void setpHandlingFee(String pHandlingFee) {
        this.pHandlingFee = pHandlingFee;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpPrice() {
        return pPrice;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public String getpStock() {
        return pStock;
    }

    public void setpStock(String pStock) {
        this.pStock = pStock;
    }

    public String getpSellerID() {
        return pSellerID;
    }

    public void setpSellerID(String pSellerID) {
        this.pSellerID = pSellerID;
    }

    public String getpSoldItems() {
        return pSoldItems;
    }

    public void setpSoldItems(String pSoldItems) {
        this.pSoldItems = pSoldItems;
    }

    public String getpOverallRate() {
        return pOverallRate;
    }

    public void setpOverallRate(String pOverallRate) {
        this.pOverallRate = pOverallRate;
    }

    public String getpScore() {
        return pScore;
    }

    public void setpScore(String pScore) {
        this.pScore = pScore;
    }

    public Model(String imageUrl, String pID, String pBrand, String pCategory, String pSubCategory, String pCondition,
                 String pDescription, String pHandlingFee, String pName, String pPrice, String pStock, String pSellerID,
                 String pOverallRate, String pSoldItems, String pScore) {
        this.imageUrl = imageUrl;
        this.pID = pID;
        this.pBrand = pBrand;
        this.pCategory = pCategory;
        this.pSubCategory = pSubCategory;
        this.pCondition = pCondition;
        this.pDescription = pDescription;
        this.pHandlingFee = pHandlingFee;
        this.pName = pName;
        this.pPrice = pPrice;
        this.pStock = pStock;
        this.pSellerID = pSellerID;
        this.pOverallRate = pOverallRate;
        this.pSoldItems = pSoldItems;
        this.pScore = pScore;
    }
}
