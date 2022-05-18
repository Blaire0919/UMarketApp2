package bbc.umarket.umarketapp2.Helper;

public class ToReceiveModel {
    String ImgUrl, buyerID, prodID, sellerID, shopName, prodName, prodQty, prodPrice;


    public ToReceiveModel(){}

    public ToReceiveModel(String imgUrl, String buyerID, String prodID, String sellerID, String shopName, String prodName, String prodQty, String prodPrice) {
        ImgUrl = imgUrl;
        this.buyerID = buyerID;
        this.prodID = prodID;
        this.sellerID = sellerID;
        this.shopName = shopName;
        this.prodName = prodName;
        this.prodQty = prodQty;
        this.prodPrice = prodPrice;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
    }

    public String getProdID() {
        return prodID;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdQty() {
        return prodQty;
    }

    public void setProdQty(String prodQty) {
        this.prodQty = prodQty;
    }

    public String getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(String prodPrice) {
        this.prodPrice = prodPrice;
    }
}
