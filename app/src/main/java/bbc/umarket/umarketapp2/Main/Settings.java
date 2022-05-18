package bbc.umarket.umarketapp2.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.R;

public class Settings extends AppCompatActivity {
    ImageView btnsettings;
    LinearLayout btnlogout;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        //hook
        btnsettings = findViewById(R.id.settings_back);
        btnlogout = findViewById(R.id.logout);

        btnsettings.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, HomeContainer.class);
            intent.putExtra("back_Acc","Account");
            startActivity(intent);
            finish();
        });

        btnlogout.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION );
            sessionManager.logoutUserfromSession();

            SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_REMEMBERME );
            sessionManager2.logoutUserfromSession();

            firebaseAuth.signOut();

            Intent intent = new Intent(Settings.this, Login.class);
            startActivity(intent);
            finish();

        });
    }
}