package bbc.umarket.umarketapp2.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;

import bbc.umarket.umarketapp2.R;

public class SellerRegistration extends AppCompatActivity {

    TextInputLayout sname, semail, scontactnum;
    Button register;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sellerreg);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        sname = findViewById(R.id.sellerreg_name);
        semail = findViewById(R.id.sellerreg_email);
        scontactnum = findViewById(R.id.sellerreg_contact);
        register = findViewById(R.id.sellerreg_button);
        back = findViewById(R.id.sellerreg_back);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(SellerRegistration.this, HomeContainer.class);
            intent.putExtra("back_Acc", "Account");
            startActivity(intent);
            finish();
        });




    }
}