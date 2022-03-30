package bbc.umarket.umarketapp2.Listener;

import java.util.List;


import bbc.umarket.umarketapp2.Helper.ItemHelperClass;

public interface ItemLoadListener {
    void onItemLoadSuccess (List<ItemHelperClass> itemList);
    void onItemLoadFailed (String Message);
}
