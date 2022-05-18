package bbc.umarket.umarketapp2.Helper;

public class NotifModel {
    String userID, notification, currenttime;

    public NotifModel() {}

    public NotifModel(String userID, String notification, String currenttime){
       this.userID = userID;
       this.notification = notification;
        this.currenttime = currenttime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getCurrenttime() { return currenttime;}

    public void setCurrenttime(String currenttime) { this.currenttime = currenttime;}
}
