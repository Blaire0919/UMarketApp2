package bbc.umarket.umarketapp2.Main;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.Helper.FeatProdtHelperClass;
import bbc.umarket.umarketapp2.R;

public class Search extends AppCompatActivity {


    DatabaseReference mref;

    private ListView listdata;
    private AutoCompleteTextView txtSearch;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);

//        Database Reference on Products
        mref = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("products");

//        Hooks
        listdata = (ListView) findViewById(R.id.listData);
        txtSearch = (AutoCompleteTextView) findViewById(R.id.txtSearch);

        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        };

        mref.addListenerForSingleValueEvent(event);

    }


//    Methods
    private void populateSearch(DataSnapshot snapshot) {
        ArrayList<String> pnames = new ArrayList<>();
        if(snapshot.exists()){

            for(DataSnapshot ds:snapshot.getChildren()){
                String pname = ds.child("pName").getValue(String.class);
                pnames.add(pname);
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pnames);
            txtSearch.setAdapter(adapter);

        }else{
            Log.d("Users", "No data found");
        }
    }
}