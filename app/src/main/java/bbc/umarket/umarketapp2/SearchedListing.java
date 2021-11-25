package bbc.umarket.umarketapp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.ItemAdapter;
import bbc.umarket.umarketapp2.Adapter.ItemHelperClass;

public class SearchedListing extends AppCompatActivity {
    ImageView back;
    ItemAdapter itemAdapter;
    ArrayList<ItemHelperClass> listItem;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    RecyclerView sprod;
    ItemHelperClass itemHelperClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_searched_listing);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        Intent searchintent = getIntent();
        String searched = searchintent.getExtras().getString("searched");

        rootNode = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");

        back = findViewById(R.id.editprof_back);
        sprod = findViewById(R.id.searchRV);

        sprod.setHasFixedSize(true);
        sprod.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        listItem = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, listItem);
        sprod.setAdapter(itemAdapter);

/**
        reference = rootNode.getReference("products").child("pName");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                  //  ItemHelperClass itemHelperClass = snap.getValue(ItemHelperClass.class);
                  //  if (itemHelperClass != null && itemHelperClass.getpName().contains(searched)) {
                 //       listItem.add(snap.getValue(ItemHelperClass.class));
                  //  }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

 **/

        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeContainer.class);
            intent.putExtra("back_Home", "Dashboard");
            startActivity(intent);
        });


    }
}