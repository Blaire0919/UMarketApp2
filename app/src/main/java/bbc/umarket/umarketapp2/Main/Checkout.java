package bbc.umarket.umarketapp2.Main;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bbc.umarket.umarketapp2.Adapter.CartItemAdapter;
import bbc.umarket.umarketapp2.Adapter.CheckOutAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.CheckOutHelperClass;
import bbc.umarket.umarketapp2.R;
import butterknife.BindView;

public class Checkout extends AppCompatActivity {

    CheckOutAdapter checkOutAdapter;
    ArrayList<CheckOutHelperClass> checkoutList;
    public static HashMap<String, String> orderLog = new HashMap<>();
    public static ArrayList<CheckOutHelperClass> orderedItem = new ArrayList<>();
    public static List<String> ItemIdOrdered = new ArrayList<>();

    public static String studid;

    FirebaseDatabase rootNode;
    DatabaseReference reference, ref2;

    RecyclerView checkoutRecyclerView;
    ImageView back;
    TextView totPayment;
    LinearLayout PlaceOrder;

    String totpay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_checkout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar


        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        checkoutRecyclerView = findViewById(R.id.rv_checkout);
        back = findViewById(R.id.checkout_back);
        totPayment = findViewById(R.id.tvCHTotalPayment);
        PlaceOrder = findViewById(R.id.Linear_PlaceOrder);

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
                        CheckOutHelperClass checkOutHelperClass = snapshot1.getValue(CheckOutHelperClass.class);
                        checkoutList.add(checkOutHelperClass);
                        orderedItem.add(checkOutHelperClass);
                     //  Log.d("ItemIdOrdered", snapshot1.child("prodId").getValue(String.class));
                        ItemIdOrdered.add(snapshot1.child("prodId").getValue(String.class));
                    }
                    checkOutAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error in CHECKOUT:", error.getMessage());
            }
        });

        ref2 = rootNode.getReference("checkout").child(studid);

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snaps) {
                if (snaps.exists()) {
                    totPayment.setText(String.format("₱ %s", snaps.child("subtotal").getValue(String.class)));
                    totpay = snaps.child("subtotal").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(view -> {
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("checkout")
                    .child(studid)
                    .removeValue()
                    .addOnSuccessListener(unused -> Log.d(TAG, "Delete Success!"));


            Intent intent = new Intent(Checkout.this, AddToCart.class);
            startActivity(intent);
            finish();
        });

        PlaceOrder.setOnClickListener(view -> {
            orderLog.put("date", java.text.DateFormat.getDateInstance().format(new Date()));
            orderLog.put("totPayment", totpay);

            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("order")
                    .child(studid)
                    .setValue(orderLog)
                    .addOnSuccessListener(unused -> Log.d("ORDER in DB", "Order Success!"));

            for (int i = 0; i < orderedItem.size(); i++) {
                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("order")
                        .child(studid)
                        .child("itemsOrdered")
                        .child(orderedItem.get(i).getProdId())
                        .setValue(orderedItem.get(i))
                        .addOnSuccessListener(unused -> Log.d("ORDERED ITEMS", "Inserted order_items Successfully!"));
            }

            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("checkout")
                    .child(studid)
                    .removeValue()
                    .addOnSuccessListener(unused -> Log.d(TAG, "Delete Success!"));


            for (String id : ItemIdOrdered){
                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("cart")
                        .child(studid)
                        .child(id)
                        .removeValue()
                        .addOnSuccessListener(unused -> Log.d("REMOVE ITEMS FROM CART", id));
            }

            Intent intent = new Intent(Checkout.this, HomeContainer.class);
            intent.putExtra("back_Chat","Chat");
            startActivity(intent);
        } );

    }
}