package bbc.umarket.umarketapp2.SellerSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.ShippingAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.NotifModel;
import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;
import bbc.umarket.umarketapp2.R;

public class ShippingOrder extends AppCompatActivity {

    String buyerName, prodID, prodName, price, qty, totAmt, buyerid, currenttime, studid, sellerid;
    TextView bname, pID, pname, pprice, pqty, ptotamt;
    ImageView img, back;
    Button shipped;
    Calendar calendar;
    //for to receive buyer
    String TRImgUrl, TRbuyerID, TRprodID, TRsellerID, TRsellerName, TRprodName, TRprodQty, TRprodPrice;
    LinearLayout isshippedout;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_shippingorder);
        //    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(ShippingOrder.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        buyerName = intent.getExtras().getString("buyername");
        prodID = intent.getExtras().getString("pid");
        prodName = intent.getExtras().getString("prodname");
        price = intent.getExtras().getString("price");
        qty = intent.getExtras().getString("qty");
        totAmt = intent.getExtras().getString("totamt");
        buyerid = intent.getExtras().getString("buyerid");
        sellerid = intent.getExtras().getString("sellerid");

        //hooks
        back = findViewById(R.id.shipped_back);
        img = findViewById(R.id.shipped_img);
        bname = findViewById(R.id.shippedbuyername);
        pID = findViewById(R.id.shippedprodID);
        pname = findViewById(R.id.shippedprodName);
        pprice = findViewById(R.id.shippedprice);
        pqty = findViewById(R.id.shippedqty);
        ptotamt = findViewById(R.id.shippedtotamt);
        shipped = findViewById(R.id.btnShipped);
        isshippedout = findViewById(R.id.isshippedout);

        //usernotif
        calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        currenttime = simpleDateFormat.format(calendar.getTime());
        Date date = new Date();


        back.setOnClickListener(view -> {
            Intent backintent = new Intent(ShippingOrder.this, ManageOrders.class);
            startActivity(backintent);
        });


        DatabaseReference reference = FirebaseDatabase
                .getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("products");

        Query checkID = reference.orderByChild("pID").equalTo(prodID);

        checkID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Glide.with(ShippingOrder.this)
                            .load(snapshot.child(prodID).child("imageUrl").getValue(String.class))
                            .into(img);

                    bname.setText(buyerName);
                    pID.setText(prodID);
                    pname.setText(prodName);
                    pprice.setText(price);
                    pqty.setText(qty);
                    ptotamt.setText(totAmt);

                    //for to receive of buyer
                    TRImgUrl = snapshot.child(prodID).child("imageUrl").getValue(String.class);
                    TRbuyerID = buyerid;
                    TRprodID = prodID;
                    TRsellerID = sellerid;
                    TRprodName = prodName;
                    TRprodQty = qty;
                    TRprodPrice = price;

                    DatabaseReference userRef = FirebaseDatabase
                            .getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("users");

                    Query checkUser = userRef.orderByChild("studID").equalTo(sellerid);

                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            if (snapshot1.exists()) {
                                String fname = snapshot1.child(sellerid).child("fname").getValue(String.class);
                                String lname = snapshot1.child(sellerid).child("lname").getValue(String.class);
                                TRsellerName = String.format("%s %s", fname, lname);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        shipped.setOnClickListener(view -> {
            //for buyernotif

            String sentence = String.format("Your order of %s %s is being shipped", qty, prodName);
            NotifModel notifModel = new NotifModel(buyerid, sentence, currenttime);
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("notification")
                    .child(buyerid)
                    .child(String.valueOf(date.getTime()))
                    .setValue(notifModel)
                    .addOnSuccessListener(unused -> {
                        Log.d("NOtif", "Order is being shipped");
                        ShippingAdapter.StatusShipped(1);
                        Intent backintent = new Intent(ShippingOrder.this, SellerCenter.class);
                        startActivity(backintent);
                    });


            //toreceive order for buyer
            ToReceiveModel toReceiveModel = new ToReceiveModel(TRImgUrl, buyerid, TRprodID, TRsellerID, TRsellerName, TRprodName, TRprodQty, TRprodPrice);
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("to_receive")
                    .child(buyerid)
                    .push()
                    .setValue(toReceiveModel)
                    .addOnSuccessListener(unused -> {
                        Log.d("to receive", "To Receive Success");
                    });

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