package bbc.umarket.umarketapp2.Adapter;

public class RateReviewHelperClass {
    String rrID, prodID, studID, nameofBuyer, rate, review, currentdate;

    public RateReviewHelperClass() {}

    public RateReviewHelperClass(String rrID, String prodID, String studID, String nameofBuyer, String rate, String review, String currentdate) {
        this.rrID = rrID;
        this.prodID = prodID;
        this.studID = studID;
        this.nameofBuyer = nameofBuyer;
        this.rate = rate;
        this.review = review;
        this.currentdate = currentdate;
    }

    public String getRrID() { return rrID; }

    public void setRrID(String rrID) { this.rrID = rrID;  }

    public String getProdID() {return prodID;}

    public void setProdID(String prodID) {this.prodID = prodID;}

    public String getStudID() {return studID;}

    public void setStudID(String studID) {this.studID = studID;}

    public String getNameofBuyer() { return nameofBuyer;  }

    public void setNameofBuyer(String nameofBuyer) {  this.nameofBuyer = nameofBuyer;   }

    public String getRate() {return rate;}

    public void setRate(String rate) {this.rate = rate;}

    public String getReview() {return review;}

    public void setReview(String review) {this.review = review;}

    public String getCurrentdate() {return currentdate;}

    public void setCurrentdate(String currentdate) {this.currentdate = currentdate;}
}
