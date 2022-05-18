package bbc.umarket.umarketapp2.Helper;

public class ToProcessModel {
    String sellerID, buyerID, buyerName, prodID, prodName, price, qty, totAmt, currentdate, currenttime;


    public ToProcessModel() {
    }

    public ToProcessModel(String sellerID, String buyerID, String buyerName, String prodID, String prodName, String price, String qty, String totAmt, String currentdate, String currenttime) {
        this.sellerID = sellerID;
        this.buyerID = buyerID;
        this.buyerName = buyerName;
        this.prodID = prodID;
        this.prodName = prodName;
        this.price = price;
        this.qty = qty;
        this.totAmt = totAmt;
        this.currentdate = currentdate;
        this.currenttime = currenttime;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getProdID() {
        return prodID;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTotAmt() {
        return totAmt;
    }

    public void setTotAmt(String totAmt) {
        this.totAmt = totAmt;
    }

    public String getCurrentdate() {
        return currentdate;
    }

    public void setCurrentdate(String currentdate) {
        this.currentdate = currentdate;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }
}
