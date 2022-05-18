package bbc.umarket.umarketapp2.SellerSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.NotifModel;
import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.R;

public class ShippingOrder extends AppCompatActivity {

    String buyerName, prodID, prodName, price, qty, totAmt, buyerid, currenttime, studid, process_key;
    TextView bname, pID, pname, pprice, pqty, ptotamt;
    ImageView img, back;
    Button shipped;

    Calendar calendar;

    DatabaseReference shippingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_shippingorder);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(ShippingOrder.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        //shipping database reference
        shippingRef = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shipping_order").child(studid);


        Intent intent = getIntent();
        buyerName = intent.getExtras().getString("buyername");
        prodID = intent.getExtras().getString("pid");
        prodName = intent.getExtras().getString("prodname");
        price = intent.getExtras().getString("price");
        qty = intent.getExtras().getString("qty");
        totAmt = intent.getExtras().getString("totamt");
        buyerid = intent.getExtras().getString("buyerid");

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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        shipped.setOnClickListener( view -> {
            //for buyernotif
            String sentence = String.format("Your order of %s %s is being shipped", qty, prodName);
            NotifModel notifModel = new NotifModel(buyerid, sentence, currenttime);
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("notification")
                    .child(buyerid)
                    .child(String.valueOf(date.getTime()))
                    .setValue(notifModel)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Order is being shipped", Toast.LENGTH_LONG).show();
                        Intent backintent = new Intent(ShippingOrder.this, ManageOrders.class);
                        startActivity(backintent);
                    });

            //deleting shippped order
            shippingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            ToProcessModel toProcessModel = snapshot1.getValue(ToProcessModel.class);
                            process_key = snapshot1.getKey();
                            Log.d("PUSH KEY", process_key);
                            if (toProcessModel != null && toProcessModel.getProdName().equals(prodName)) {
                                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("completed_order")
                                        .child(studid)
                                        .push()
                                        .setValue(toProcessModel)
                                        .addOnSuccessListener(unused -> {
                                                    Toast.makeText(ShippingOrder.this, "Moved to Completed order", Toast.LENGTH_LONG)
                                                            .show();

                                                    FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                            .getReference("shipping_order")
                                                            .child(studid)
                                                            .child(process_key)
                                                            .removeValue()
                                                            .addOnSuccessListener(unused1 ->
                                                                    Toast.makeText(ShippingOrder.this, "Removed from Shipping Order with Key "+ process_key, Toast.LENGTH_LONG)
                                                                            .show()
                                                            );
                                                }
                                        );
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        });




    }
}