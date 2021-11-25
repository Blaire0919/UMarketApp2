package bbc.umarket.umarketapp2.Adapter;

public class ClickedHistoryHelperClass {
    String studID, dateNTime, prodID;

    public ClickedHistoryHelperClass() {}

    public String getStudID() {
        return studID;
    }

    public void setStudID(String studID) {
        this.studID = studID;
    }

    public String getDateNTime() {
        return dateNTime;
    }


    public void setDateNTime(String dateNTime) {
        this.dateNTime = dateNTime;
    }

    public String getProdID() {
        return prodID;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public ClickedHistoryHelperClass(String studID, String dateNTime, String prodID){
        this.studID = studID;
        this.dateNTime = dateNTime;
        this.prodID = prodID;
    }
}
