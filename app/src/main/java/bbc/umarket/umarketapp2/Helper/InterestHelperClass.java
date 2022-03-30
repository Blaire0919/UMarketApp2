package bbc.umarket.umarketapp2.Helper;

public class InterestHelperClass {
    String studid, schoolsupplies, electronics, foodbeverages, clothesaccessories, beautyproducts, sportsequipment;


    public InterestHelperClass() { }

    public String getStudid() { return studid; }

    public void setStudid(String studid) { this.studid = studid; }

    public String getSchoolsupplies() { return schoolsupplies; }

    public void setSchoolsupplies(String schoolsupplies) { this.schoolsupplies = schoolsupplies;  }

    public String getElectronics() { return electronics; }

    public void setElectronics(String electronics) { this.electronics = electronics; }

    public String getFoodbeverages() { return foodbeverages; }

    public void setFoodbeverages(String foodbeverages) { this.foodbeverages = foodbeverages; }

    public String getClothesaccessories() { return clothesaccessories; }

    public void setClothesaccessories(String clothesaccessories) { this.clothesaccessories = clothesaccessories; }

    public String getBeautyproducts() { return beautyproducts; }

    public void setBeautyproducts(String beautyproducts) { this.beautyproducts = beautyproducts; }

    public String getSportsequipment() { return sportsequipment; }

    public void setSportsequipment(String sportsequipment) { this.sportsequipment = sportsequipment; }

    public InterestHelperClass(String studid, String schoolsupplies, String electronics, String foodbeverages, String clothesaccessories, String beautyproducts, String sportsequipment) {
        this.studid = studid;
        this.schoolsupplies = schoolsupplies;
        this.electronics = electronics;
        this.foodbeverages = foodbeverages;
        this.clothesaccessories = clothesaccessories;
        this.beautyproducts = beautyproducts;
        this.sportsequipment = sportsequipment;
    }


}
