package bbc.umarket.umarketapp2.Helper;

public class MessagesModel {
    String messages, senderId, currenttime;
    long timestamp;

    public MessagesModel() {}

    public MessagesModel(String messages, String senderId, String currenttime, long timestamp) {
        this.messages = messages;
        this.senderId = senderId;
        this.currenttime = currenttime;
        this.timestamp = timestamp;
    }


    public String getMessages() {return messages;}

    public void setMessages(String messages) { this.messages = messages;}

    public String getSenderId() {return senderId;}

    public void setSenderId(String senderId) { this.senderId = senderId;}

    public String getCurrenttime() {return currenttime;}

    public void setCurrenttime(String currenttime) {this.currenttime = currenttime;}

    public long getTimestamp() {return timestamp;}

    public void setTimestamp(long timestamp) {this.timestamp = timestamp;}
}
