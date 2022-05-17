package bbc.umarket.umarketapp2.Helper;

public class NotifModel {
    String prodName, sellerName, Qty, Price, currenttime;

    public NotifModel() {}

    public NotifModel(String prodName, String sellerName, String Qty, String Price, String currenttime){
        this.prodName = prodName;
        this.sellerName = sellerName;
        this.Qty = Qty;
        this.Price = Price;
        this.currenttime = currenttime;
    }

    public String getProdName() {return prodName;}

    public void setProdName(String prodName) {this.prodName = prodName; }

    public String getSellerName() {return sellerName;}

    public void setSellerName(String sellerName) {this.sellerName = sellerName;}

    public String getQty() {return Qty;}

    public void setQty(String qty) {Qty = qty;}

    public String getPrice() {return Price;}

    public void setPrice(String price) { Price = price;}

    public String getCurrenttime() { return currenttime;}

    public void setCurrenttime(String currenttime) { this.currenttime = currenttime;}
}
