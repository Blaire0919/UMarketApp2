package bbc.umarket.umarketapp2.Main;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.ItemAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.FeatProdtHelperClass;
import bbc.umarket.umarketapp2.Helper.ItemHelperClass;
import bbc.umarket.umarketapp2.R;

public class Search extends AppCompatActivity {

    ImageView back;
    DatabaseReference mref;
    String studid;

    private ListView listdata;
    private AutoCompleteTextView txtSearch;
    RecyclerView searchrv;
    ArrayList<ItemHelperClass> searched_item;
    ItemAdapter itemAdapter;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_search);
     //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(Search.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

     firebaseAuth = FirebaseAuth.getInstance();
      firebaseFirestore = FirebaseFirestore.getInstance();

//        Database Reference on Products
        mref = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("products");

//        Hooks
        listdata = (ListView) findViewById(R.id.listData);
        txtSearch = (AutoCompleteTextView) findViewById(R.id.txtSearch);
        back = (ImageView) findViewById(R.id.search_back);
        searchrv = (RecyclerView) findViewById(R.id.search_recyclerview);


        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        mref.addListenerForSingleValueEvent(event);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(Search.this, HomeContainer.class);
            intent.putExtra("back_Home", "Home");
            startActivity(intent);
            finish();
        });

//        txtSearch.setOnKeyListener((view, i, keyEvent) -> {
//            if (i==KeyEvent.KEYCODE_ENTER) {
//
//            }
//
//            return false;
//        });

    }

    //    Methods
    @SuppressLint("NotifyDataSetChanged")
    private void populateSearch(DataSnapshot snapshot) {
        ArrayList<String> pnames = new ArrayList<>();

        //for filtered recyclerview
        //for product listing
        searchrv.setHasFixedSize(true);
        searchrv.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false ));
        searched_item = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, searched_item);
        searchrv.setAdapter(itemAdapter);

        if (snapshot.exists()) {

            for (DataSnapshot ds : snapshot.getChildren()) {
                String pname = ds.child("pName").getValue(String.class);
                pnames.add(pname);

                ItemHelperClass itemHelperClass = ds.getValue(ItemHelperClass.class);
                if (itemHelperClass != null && itemHelperClass.getpName().equals(pname) && !itemHelperClass.getpSellerID().equals(studid)) {
                    searched_item.add(itemHelperClass);
                }
            }
            itemAdapter.notifyDataSetChanged();

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pnames);
            txtSearch.setAdapter(adapter);

        } else {
            Log.d("Users", "No data found");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        try {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.update("status", "Online");}
        catch (Exception exception) {
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