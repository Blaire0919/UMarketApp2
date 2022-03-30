package bbc.umarket.umarketapp2.Helper;

public class CartHelperClass {
    String userID, prodID, sellerID, sellerName, prodName, prodQty, prodPrice, imgUrl, dateTime, totalPrice;

    public CartHelperClass() {}

    public CartHelperClass(String userID, String prodID, String sellerID, String sellerName, String prodName,
                           String prodQty, String prodPrice, String imgUrl, String dateTime, String totalPrice) {
        this.userID = userID;
        this.prodID = prodID;
        this.sellerID = sellerID;
        this.sellerName = sellerName;
        this.prodName = prodName;
        this.prodQty = prodQty;
        this.prodPrice = prodPrice;
        this.imgUrl = imgUrl;
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
    }

    public String getUserID() {return userID;}

    public void setUserID(String userID) {this.userID = userID;}

    public String getProdID() {return prodID;}

    public void setProdID(String prodID) {this.prodID = prodID; }

    public String getSellerID() {return sellerID; }

    public void setSellerID(String sellerID) {this.sellerID = sellerID;}

    public String getSellerName() {return sellerName; }

    public void setSellerName(String sellerName) {this.sellerName = sellerName;}

    public String getProdName() {return prodName;}

    public void setProdName(String prodName) {this.prodName = prodName;}

    public String getProdQty() {return prodQty;}

    public void setProdQty(String prodQty) {this.prodQty = prodQty;}

    public String getProdPrice() {return prodPrice; }

    public void setProdPrice(String prodPrice) {this.prodPrice = prodPrice; }

    public String getImgUrl() {return imgUrl;}

    public void setImgUrl(String imgUrl) {this.imgUrl = imgUrl; }

    public String getDateTime() {return dateTime;}

    public void setDateTime(String dateTime) {this.dateTime = dateTime; }

    public String getTotalPrice() { return totalPrice; }

    public void setTotalPrice(String totalPrice) { this.totalPrice = totalPrice; }
}
