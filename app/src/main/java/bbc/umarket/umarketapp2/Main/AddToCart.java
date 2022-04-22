package bbc.umarket.umarketapp2.Main;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import bbc.umarket.umarketapp2.Adapter.CartItemAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.CartHelperClass;

import bbc.umarket.umarketapp2.Listener.CartItemLoadListener;

import bbc.umarket.umarketapp2.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddToCart extends AppCompatActivity implements CartItemLoadListener {

    CartItemAdapter cartItemAdapter;
    ArrayList<CartHelperClass> cartitem;

    String studid;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cartItem_RecyclerView)
    RecyclerView cartItemRView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cartlayout)
    LinearLayout mainlayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cart_back)
    ImageView back;

    public static TextView txttotal;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btncheckout)
    Button checkout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.chkcartselectAll)
    CheckBox chkselectAll;

    CartItemLoadListener cartItemLoadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_addtocart);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        init();

        txttotal = findViewById(R.id.tvTotalPrice);

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
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        chkselectAll.setOnClickListener(view -> {
            if (chkselectAll.isChecked()) {
                cartItemAdapter.selectAll();
            } else {
                cartItemAdapter.unselectall();
            }
        });

        checkout.setOnClickListener(view -> {
            Intent intent = new Intent(AddToCart.this, Checkout.class);
            startActivity(intent);
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
            txttotal.setText(value);
            Log.d("SUBTOT: ", value);
        } catch (Exception ex) {
            Log.d("Exception", "Exception of type" + ex.getMessage());
        }
    }

    @Override
    public void onCartLoadSuccess(ArrayList<CartHelperClass> cartItemList) {
    }

    @Override
    public void onCartLoadFailed(String Message) {
    }
}