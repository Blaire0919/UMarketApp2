package bbc.umarket.umarketapp2.SellerSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.R;

public class CompletedOrder extends AppCompatActivity {

    String buyerName, prodID, prodName, price, qty, totAmt, buyerid, currenttime, studid, process_key;
    TextView bname, pID, pname, pprice, pqty, ptotamt;
    ImageView img, back;

    Calendar calendar;

    DatabaseReference completedRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_completedorder);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);


        //shipping database reference
        completedRef = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("completed_order").child(studid);

        Intent intent = getIntent();
        buyerName = intent.getExtras().getString("buyername");
        prodID = intent.getExtras().getString("pid");
        prodName = intent.getExtras().getString("prodname");
        price = intent.getExtras().getString("price");
        qty = intent.getExtras().getString("qty");
        totAmt = intent.getExtras().getString("totamt");
        buyerid = intent.getExtras().getString("buyerid");

        //hooks
        back = findViewById(R.id.completed_back);
        img = findViewById(R.id.completed_img);
        bname = findViewById(R.id.completedbuyername);
        pID = findViewById(R.id.completedprodID);
        pname = findViewById(R.id.completedprodName);
        pprice = findViewById(R.id.completedprice);
        pqty = findViewById(R.id.completedqty);
        ptotamt = findViewById(R.id.completedtotamt);

        back.setOnClickListener(view -> {
            Intent backintent = new Intent(CompletedOrder.this, ManageOrders.class);
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

                    Glide.with(CompletedOrder.this)
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



    }
}