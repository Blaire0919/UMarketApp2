package bbc.umarket.umarketapp2.Helper;

public class CheckOutHelperClass {
    String pUrl, prodId, sellerName , prodName, qty, price, subTotal, sellerID;

    public CheckOutHelperClass(){}

    public CheckOutHelperClass(String pUrl, String prodId, String sellerName , String prodName, String qty, String price, String subTotal, String sellerID) {
        this.pUrl = pUrl;
        this.prodId = prodId;
        this.sellerName = sellerName;
        this.prodName = prodName;
        this.qty = qty;
        this.price = price;
        this.subTotal = subTotal;
        this.sellerID = sellerID;
    }

    public String getpUrl() { return pUrl; }

    public void setpUrl(String pUrl) { this.pUrl = pUrl; }

    public String getProdId() { return prodId; }

    public void setProdId(String prodId) { this.prodId = prodId; }

    public String getSellerName() { return sellerName; }

    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getProdName() { return prodName; }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getSellerID() {return sellerID;}

    public void setSellerID(String sellerID) { this.sellerID = sellerID;}
}
