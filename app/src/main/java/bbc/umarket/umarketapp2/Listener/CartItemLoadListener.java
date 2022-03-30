package bbc.umarket.umarketapp2.Listener;

import java.util.ArrayList;
import java.util.List;

import bbc.umarket.umarketapp2.Helper.CartHelperClass;

public interface CartItemLoadListener {
    void onCartLoadSuccess (ArrayList<CartHelperClass> cartItemList);
    void onCartLoadFailed (String Message);
}
