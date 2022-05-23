package bbc.umarket.umarketapp2.Main;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import bbc.umarket.umarketapp2.Adapter.CheckOutAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.CheckOutHelperClass;
import bbc.umarket.umarketapp2.Helper.NotifModel;
import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.R;

public class CheckoutForCart extends AppCompatActivity {

    public static String studid, firstname, lastname;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    //checkout for cart recyclerview
    RecyclerView FcRecyclerview;
    CheckOutHelperClass fcCheckoutModel;
    CheckOutAdapter FcCheckoutAdapter;
    ArrayList<CheckOutHelperClass> FCList;

   // public static HashMap<String, String> orderLog = new HashMap<>();
    public static ArrayList<CheckOutHelperClass> orderedItem = new ArrayList<>();
    public static List<String> ItemIdOrdered = new ArrayList<>();
    public static List<String> sellerId = new ArrayList<>();
    public static List<String> qtyList = new ArrayList<>();
    public static List<String> prodnameList = new ArrayList<>();
    public static List<String> sellernameList = new ArrayList<>();
    public static List<String> priceList = new ArrayList<>();

    ImageView back;
    TextView totPayment;
    LinearLayout PlaceOrder;
    String totpay;

    //for user notification
    Calendar calendar;
    String currenttime;
    FirebaseDatabase rootNode;
    DatabaseReference reference, ref2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_checkout_forcart);

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);
        firstname = usersdetails.get(SessionManager.KEY_FNAME);
        lastname = usersdetails.get(SessionManager.KEY_LNAME);

        //usernotif date and time format
        calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        currenttime = simpleDateFormat.format(calendar.getTime());
        Date date = new Date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        totPayment = findViewById(R.id.FCtvCHTotalPayment);
        PlaceOrder = findViewById(R.id.FCLinear_PlaceOrder);
        back = findViewById(R.id.FCcheckout_back);
        FcRecyclerview = findViewById(R.id.FCrv_checkout);

        rootNode = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = rootNode.getReference("checkout_cart").child(studid).child("items");

        //for checkout cart recyclerview
        FcRecyclerview.setHasFixedSize(true);
        FcRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        FCList = new ArrayList<>();
        FcCheckoutAdapter = new CheckOutAdapter(this, FCList);
        FcRecyclerview.setAdapter(FcCheckoutAdapter);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        fcCheckoutModel = snapshot1.getValue(CheckOutHelperClass.class);
                        FCList.add(fcCheckoutModel);
                    //    orderedItem.add(fcCheckoutModel);
                   //     ItemIdOrdered.add(snapshot1.child("prodId").getValue(String.class));
                        //for multiple items checkout notif
                        sellerId.add(snapshot1.child("sellerID").getValue(String.class));
                        qtyList.add(snapshot1.child("qty").getValue(String.class));
                        prodnameList.add(snapshot1.child("prodName").getValue(String.class));
                        sellernameList.add(snapshot1.child("sellerName").getValue(String.class));
                        priceList.add(snapshot1.child("price").getValue(String.class));


                    }
                    FcCheckoutAdapter.notifyDataSetChanged();
                    Log.d("CHECKOUT FCLIST", String.valueOf(FCList.size()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { Log.d("Error in CHECKOUT:", error.getMessage());} });

        ref2 = rootNode.getReference("checkout_cart").child(studid);
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snaps) {
                if (snaps.exists()) {
                    totPayment.setText(String.format("₱ %s", snaps.child("subtotal").getValue(String.class)));
                    totpay = snaps.child("subtotal").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        back.setOnClickListener(view -> {
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("checkout_cart")
                    .child(studid)
                    .removeValue()
                    .addOnSuccessListener(unused -> Log.d(TAG, "Delete Success!"));

            FCList.clear();

            Intent intent = new Intent(CheckoutForCart.this, AddToCart.class);
            startActivity(intent);
            finish();
        });

        PlaceOrder.setOnClickListener(view -> {
            //remove from cart
            for (String id : ItemIdOrdered) {
                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("cart")
                        .child(studid)
                        .child(id)
                        .removeValue()
                        .addOnSuccessListener(unused -> Log.d("REMOVE ITEMS FROM CART", id));
            }

            //usernotif
            String initSentence = "You placed the orders of the following:\n";

            StringBuilder sb = new StringBuilder();
            for(int i =0;i<sellerId.size();i++){
                    sb.append("- ").append(qtyList.get(i)).append(" of ").append(prodnameList.get(i)).append(" from ").append(sellernameList.get(i)).append("\n");
            }

            String sentence = initSentence + sb;
            NotifModel notifModel = new NotifModel(studid, sentence, currenttime);
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("notification")
                    .child(studid)
                    .child(String.valueOf(date.getTime()))
                    .setValue(notifModel);

            //toprocess order for seller
//            for(String seller : sellerId){
//                ToProcessModel toProcessModel = new ToProcessModel(seller_ID, buyerID, buyerName, prodID, prodName, price, qty, totAmt, order_currentdate, order_currenttime);
//                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                        .getReference("process_order")
//                        .child(seller)
//                        .push()
//                        .setValue(toProcessModel);
//            }

            Intent intent = new Intent(CheckoutForCart.this, HomeContainer.class);
            intent.putExtra("back_Home", "Home");
            startActivity(intent);
            finish();
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