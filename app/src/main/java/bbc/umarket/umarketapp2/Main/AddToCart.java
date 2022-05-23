package bbc.umarket.umarketapp2.Main;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.CartItemAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.CartHelperClass;

import bbc.umarket.umarketapp2.Helper.CheckOutHelperClass;
import bbc.umarket.umarketapp2.Listener.CartItemLoadListener;

import bbc.umarket.umarketapp2.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddToCart extends AppCompatActivity implements CartItemLoadListener {
    CartItemAdapter cartItemAdapter;
    ArrayList<CartHelperClass> cartitem;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    public static String studid;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    public static HashMap<String, String> checkoutmain = new HashMap<>();
    public static ArrayList<CheckOutHelperClass> trylang = new ArrayList<>();
    AlertDialog dialog;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cartItem_RecyclerView)
    RecyclerView cartItemRView;
    public static LinearLayout mainlayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cart_back)
    ImageView back;
    public static TextView txttotal;
    public static Integer x = 0;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btncheckout)
    Button checkout;
    CartItemLoadListener cartItemLoadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_addtocart);

        init();

        txttotal = findViewById(R.id.tvTotalPrice);
        mainlayout = findViewById(R.id.cartlayout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //studid
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        rootNode = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = rootNode.getReference("cart").child(studid);

        //for cart item recycler view
        cartItemRView.setHasFixedSize(true);
        cartitem = new ArrayList<>();
        cartItemAdapter = new CartItemAdapter(this, cartitem);
        cartItemRView.setAdapter(cartItemAdapter);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snaps1 : snapshot.getChildren()) {
                        CartHelperClass cartHelperClass = snaps1.getValue(CartHelperClass.class);
                        if (cartHelperClass != null) {
                            cartitem.add(cartHelperClass);
                        }
                        cartItemAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        checkout.setOnClickListener(view -> {
            if (x != 0) {
                checkoutmain.put("date", java.text.DateFormat.getDateInstance().format(new Date()));

                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("checkout_cart")
                        .child(studid)
                        .setValue(checkoutmain)
                        .addOnSuccessListener(unused -> {
                            Log.d(TAG, "Insert 1st child Success!");

                            for (int i = 0; i < trylang.size(); i++) {
                                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("checkout_cart")
                                        .child(studid)
                                        .child("items")
                                        .child(trylang.get(i).getProdId())
                                        .setValue(trylang.get(i))
                                        .addOnSuccessListener(unused2 -> {
                                            Log.d(TAG, "Insert 2nd child Success!");

                                            Intent intent = new Intent(AddToCart.this, CheckoutForCart.class);
                                            startActivity(intent);
                                        });
                            }

                        });
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddToCart.this, R.style.CustomAlertDialog));
                dialog = builder.setTitle("")
                        .setMessage("Select atleast one product to check out")
                        .setNeutralButton("OK", null)
                        .create();
                dialog.show();
            }
        });
    }

    private void init() {
        ButterKnife.bind(this);
        cartItemLoadListener = this;
        cartItemRView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        back.setOnClickListener(view -> {
            Intent intent = new Intent(AddToCart.this, HomeContainer.class);
            intent.putExtra("back_Home", "Home");
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("DefaultLocale")
    public static void selected_subtotal(String value) {
        try {
            txttotal.setText(String.format("₱ %.2f", Float.parseFloat(value)));
            checkoutmain.put("subtotal", String.format("%.2f", Float.parseFloat(value)));
            Log.d("SUBTOT: ", value);
        } catch (Exception ex) {
            Log.d("Exception", "Exception of type" + ex.getMessage());
        }
    }

    public static void selectedItemCount(Integer value) {
        try {
            x = value;
        } catch (Exception exception) {
           Log.d("Exception", exception.getMessage());
        }
    }

    public static void check_out(List<String> idlist, CheckOutHelperClass value) {
        try {
            for (String id : idlist) {
                if (value != null && id.equals(value.getProdId())) {
                    trylang.add(value);
                    Log.d("CHECKOUT CART VALUE", String.valueOf(trylang));
                }
            }
        } catch (Exception ex) {
            Log.d("Error: ", ex.getMessage());
        }
    }

    @Override
    public void onCartLoadSuccess(ArrayList<CartHelperClass> cartItemList) {
    }

    @Override
    public void onCartLoadFailed(String Message) {
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.update("status", "Online");
        } catch (Exception exception) {
            Log.d("EXCEPTION", exception.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.update("status", "Offline");
        } catch (Exception exception) {
            Log.d("EXCEPTION", exception.getMessage());

        }
    }
}