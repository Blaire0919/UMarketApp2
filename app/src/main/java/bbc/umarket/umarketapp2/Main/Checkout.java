package bbc.umarket.umarketapp2.Main;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.CartItemAdapter;
import bbc.umarket.umarketapp2.Adapter.CheckOutAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Fragments.FragChat;
import bbc.umarket.umarketapp2.Helper.CheckOutHelperClass;
import bbc.umarket.umarketapp2.Helper.NotifModel;
import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;
import bbc.umarket.umarketapp2.R;
import butterknife.BindView;

public class Checkout extends AppCompatActivity {

    CheckOutAdapter checkOutAdapter;
    CheckOutHelperClass checkOutHelperClass = new CheckOutHelperClass();
    ArrayList<CheckOutHelperClass> checkoutList;

    public static HashMap<String, String> orderLog = new HashMap<>();
    public static ArrayList<CheckOutHelperClass> orderedItem = new ArrayList<>();
    public static List<String> ItemIdOrdered = new ArrayList<>();

    public static String studid, firstname, lastname;

    FirebaseDatabase rootNode;
    DatabaseReference reference, ref2;

    RecyclerView checkoutRecyclerView;
    ImageView back;
    TextView totPayment;
    LinearLayout PlaceOrder;
    String totpay;
    String checkoutProduct, sellerID;

    //for user notification
    Calendar calendar;
    String un_prodname, un_sellername, un_qty, un_price, currenttime;

    //for managing orders by sellers
    String seller_ID, buyerID, buyerName, prodID, prodName, price, qty, totAmt, order_currentdate, order_currenttime;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_checkout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);
        firstname = usersdetails.get(SessionManager.KEY_FNAME);
        lastname = usersdetails.get(SessionManager.KEY_LNAME);

        Intent COintent = getIntent();
        checkoutProduct = COintent.getExtras().getString("pID");

        checkoutRecyclerView = findViewById(R.id.rv_checkout);
        back = findViewById(R.id.checkout_back);
        totPayment = findViewById(R.id.tvCHTotalPayment);
        PlaceOrder = findViewById(R.id.Linear_PlaceOrder);

        //usernotif
        calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        currenttime = simpleDateFormat.format(calendar.getTime());
        Date date = new Date();

        rootNode = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = rootNode.getReference("checkout").child(studid).child("items");

        //for checkout recyclerview
        checkoutRecyclerView.setHasFixedSize(true);
        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        checkoutList = new ArrayList<>();
        checkOutAdapter = new CheckOutAdapter(this, checkoutList);
        checkoutRecyclerView.setAdapter(checkOutAdapter);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        checkOutHelperClass = snapshot1.getValue(CheckOutHelperClass.class);
                        checkoutList.add(checkOutHelperClass);
                        orderedItem.add(checkOutHelperClass);
                        ItemIdOrdered.add(snapshot1.child("prodId").getValue(String.class));
                        sellerID = snapshot1.child("sellerID").getValue(String.class);

                        //user notif
                        un_prodname = snapshot1.child("prodName").getValue(String.class);
                        un_sellername = snapshot1.child("sellerName").getValue(String.class);
                        un_qty = snapshot1.child("qty").getValue(String.class);
                        un_price = snapshot1.child("price").getValue(String.class);

                        //for seller managing orders
                        seller_ID = sellerID;
                        buyerID = studid;
                        buyerName = String.format("%s %s", firstname, lastname);
                        prodID = snapshot1.child("prodId").getValue(String.class);
                        prodName = un_prodname;
                        price = un_price;
                        qty = un_qty;
                        totAmt = String.valueOf(Float.parseFloat(String.valueOf(Float.parseFloat(price) * Integer.parseInt(qty))));
                        order_currentdate = java.text.DateFormat.getDateInstance().format(new Date());
                        order_currenttime = currenttime;

                    }
                    checkOutAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error in CHECKOUT:", error.getMessage());
            }
        });

        if (checkoutProduct != null) {
            ref2 = rootNode.getReference("checkout").child(studid).child("items").child(checkoutProduct);
            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snaps) {
                    if (snaps.exists()) {
                        totPayment.setText(String.format("₱ %s", snaps.child("subTotal").getValue(String.class)));
                        totpay = snaps.child("subTotal").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            ref2 = rootNode.getReference("checkout").child(studid);
            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snaps) {
                    if (snaps.exists()) {
                        totPayment.setText(String.format("₱ %s", snaps.child("subTotal").getValue(String.class)));
                        totpay = snaps.child("subTotal").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        back.setOnClickListener(view -> {
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("checkout")
                    .child(studid)
                    .removeValue()
                    .addOnSuccessListener(unused -> Log.d(TAG, "Delete Success!"));

            checkoutList.clear();
            ItemIdOrdered.clear();
            orderLog.clear();

            Intent intent = new Intent(Checkout.this, HomeContainer.class);
            intent.putExtra("back_Home", "Home");
            startActivity(intent);
            finish();
        });

        PlaceOrder.setOnClickListener(view -> {
            //remove from checkout
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("checkout")
                    .child(studid)
                    .removeValue()
                    .addOnSuccessListener(unused -> Log.d(TAG, "Delete Success!"));

            //usernotif
            String sentence = String.format("You placed an order of %s %s from %s with a price of %s", un_qty, un_prodname, un_sellername, un_price);
            NotifModel notifModel = new NotifModel(studid, sentence, currenttime);
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("notification")
                    .child(studid)
                    .child(String.valueOf(date.getTime()))
                    .setValue(notifModel);

            //toprocess order for seller
            ToProcessModel toProcessModel = new ToProcessModel(seller_ID, buyerID, buyerName, prodID, prodName, price, qty, totAmt, order_currentdate, order_currenttime);
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("process_order")
                    .child(seller_ID)
                    .push()
                    .setValue(toProcessModel);

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Checkout.this, R.style.CustomAlertDialog));

            dialog = builder.setTitle("")
                    .setMessage("Successfully ordered the product!")
                    .setNeutralButton("OK", null)
                    .create();

            dialog.show();

            Intent intent = new Intent(Checkout.this, HomeContainer.class);
            intent.putExtra("back_Home", "Home");
            startActivity(intent);
        });

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