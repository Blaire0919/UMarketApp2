package bbc.umarket.umarketapp2.Listener;

import java.util.ArrayList;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;

public interface ToRateLoadListener {
    void onToRateLoadSuccess (ArrayList<ToReceiveModel> toRateList);
    void onToRateLoadFailed (String Message);
}
