package bbc.umarket.umarketapp2.Listener;

import java.util.ArrayList;
import bbc.umarket.umarketapp2.Helper.NotifModel;

public interface NotifItemLoadListener {
    void onNotifLoadSuccess (ArrayList<NotifModel> notifList);
    void onNotifLoadFailed (String Message);
}
