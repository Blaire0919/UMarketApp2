package bbc.umarket.umarketapp2.Helper;

public class SellerHelperClass {
    String sellerID, imgSeller, userID,  email, contact;

    public SellerHelperClass() {}

    public SellerHelperClass(String sellerID, String imgSeller, String userID, String email, String contact) {
        this.sellerID = sellerID;
        this.imgSeller = imgSeller;
        this.userID = userID;
        this.email = email;
        this.contact = contact;
    }

    public String getSellerID() { return sellerID; }

    public void setSellerID(String sellerID) { this.sellerID = sellerID;}

    public String getImgSeller() {return imgSeller;}

    public void setImgSeller(String imgSeller) {this.imgSeller = imgSeller;}

    public String getUserID() {return userID;}

    public void setUserID(String userID) {this.userID = userID;}


    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getContact() {return contact;}

    public void setContact(String contact) {this.contact = contact;}
}
