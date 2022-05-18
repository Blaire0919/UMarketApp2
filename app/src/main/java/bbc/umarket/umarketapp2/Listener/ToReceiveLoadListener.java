package bbc.umarket.umarketapp2.Listener;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.Helper.NotifModel;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;

public interface ToReceiveLoadListener {
    void onToReceiveLoadSuccess (ArrayList<ToReceiveModel> toReceiveList);
    void onToReceiveLoadFailed (String Message);
}
