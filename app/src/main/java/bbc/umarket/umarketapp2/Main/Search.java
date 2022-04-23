package bbc.umarket.umarketapp2.Main;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.Adapter.ItemAdapter;
import bbc.umarket.umarketapp2.Helper.ItemHelperClass;
import bbc.umarket.umarketapp2.R;

public class Search extends AppCompatActivity {

    ImageView back;
    DatabaseReference prodref;

    private ListView listdata;
    private AutoCompleteTextView txtSearch;

    RecyclerView searchrv;
    ArrayList<ItemHelperClass> searched_item;
//    ArrayList<ItemHelperClass> newSearch;
    ItemAdapter itemAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

//        Database Reference on Products
        prodref = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("products");

//        Hooks
        listdata = (ListView) findViewById(R.id.listData);
        txtSearch = (AutoCompleteTextView) findViewById(R.id.txtSearch);
        back = (ImageView) findViewById(R.id.search_back);
        searchrv = (RecyclerView) findViewById(R.id.search_recyclerview);

        //for filtered recyclerview
        //for product listing
        searchrv.setHasFixedSize(true);
        searchrv.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        searched_item = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, searched_item);
        searchrv.setAdapter(itemAdapter);



        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                populateSearch(snapshot);


            }

            @Override

            public void onCancelled( DatabaseError error) {  }
        };
        prodref.addListenerForSingleValueEvent(event);

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
    private void populateSearch(DataSnapshot snapshot) {
        ArrayList<String> pnames = new ArrayList<>();

//        Auto Suggest
        if(snapshot.exists()){
            for(DataSnapshot ds:snapshot.getChildren()){
                String pname = ds.child("pName").getValue(String.class);
                pnames.add(pname);

                ItemHelperClass itemHelperClass = ds.getValue(ItemHelperClass.class);
                if (itemHelperClass != null && itemHelperClass.getpName().equals(pname)) {
                    searched_item.add(itemHelperClass);
                }
            }
            itemAdapter.notifyDataSetChanged();
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pnames);
            txtSearch.setAdapter(adapter);


//            Clicking suggestion function
            txtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String name = txtSearch.getText().toString();
                    searchProduct(name);
                }
            });

            txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_SEARCH){
                        String search_query = textView.getText().toString();
                        searchProduct(search_query);

                        return true;
                    }
                    return false;
                }
            });

        }else{Log.d("Products", "No data found");
        }
    }


    private void searchProduct(String name){
        txtSearch.clearFocus();

        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);

        String searchInputToLower  = name.toLowerCase();
        String searchInputToUpper  = name.toUpperCase();

        Query query = prodref.orderByChild("pName").startAt(name).endAt(name + "\uf8ff");
//        Query query = prodref.orderByChild("pName").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    searched_item.clear();
                    itemAdapter.notifyDataSetChanged();

                    for(DataSnapshot ds : snapshot.getChildren()){
                        ItemHelperClass itemHelperClass = ds.getValue(ItemHelperClass.class);
                        searched_item.add(itemHelperClass);
                    }
                    itemAdapter = new ItemAdapter(getApplicationContext(), searched_item);
                    searchrv.setAdapter(itemAdapter);
                    itemAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




}