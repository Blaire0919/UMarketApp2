package bbc.umarket.umarketapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.Adapter.ItemAdapter;
import bbc.umarket.umarketapp2.Adapter.ItemHelperClass;

public class CategorizedListing extends AppCompatActivity {
    ImageView back;
    TextView cattext;
    ItemAdapter itemAdapter;
    ArrayList<ItemHelperClass> listItem;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    RecyclerView items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_categorized_listing);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        Intent catintent = getIntent();
        String catname = catintent.getExtras().getString("pcat");

        rootNode = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = rootNode.getReference("products");

        //hooks
        back = findViewById(R.id.editprof_back);
        cattext = findViewById(R.id.cattext);
        items = findViewById(R.id.rvcategorized);

        cattext.setText(catname);

        items.setHasFixedSize(true);
        items.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        listItem = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, listItem);
        items.setAdapter(itemAdapter);

        reference = rootNode.getReference("products");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ItemHelperClass itemHelperClass = snap.getValue(ItemHelperClass.class);
                    if (itemHelperClass != null && itemHelperClass.getpCategory().equals(catname)) {
                        listItem.add(itemHelperClass);
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });


        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeContainer.class);
            intent.putExtra("back_Home", "Dashboard");
            startActivity(intent);
        });

    }
}