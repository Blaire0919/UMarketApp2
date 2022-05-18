package bbc.umarket.umarketapp2.Helper;

public class UserHelperClass {
    String fname, lname, studID, contacts, gender, bday, email, password;

    public UserHelperClass(String fname, String lname, String studID, String contacts, String gender, String bday, String email, String password) {
        this.fname = fname;
        this.lname = lname;
        this.studID = studID;
        this.contacts = contacts;
        this.gender = gender;
        this.bday = bday;
        this.email = email;
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getStudID() {
        return studID;
    }

    public void setStudID(String studID) {
        this.studID = studID;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBday() {
        return bday;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
