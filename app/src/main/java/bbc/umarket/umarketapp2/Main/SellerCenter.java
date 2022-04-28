package bbc.umarket.umarketapp2.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import bbc.umarket.umarketapp2.R;

public class SellerCenter extends AppCompatActivity {

    LinearLayout btnadd;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_seller_center);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        btnadd = findViewById(R.id.add);
        back = findViewById(R.id.sc_back);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(SellerCenter.this, HomeContainer.class);
            intent.putExtra("back_Acc", "Account");
            startActivity(intent);
            finish();
        });

        btnadd.setOnClickListener(view1 -> {
            Intent intent = new Intent(SellerCenter.this, AddListing.class);
            startActivity(intent);
        });


    }
}