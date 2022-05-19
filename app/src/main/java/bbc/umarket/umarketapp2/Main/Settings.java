package bbc.umarket.umarketapp2.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.R;

public class Settings extends AppCompatActivity {
    ImageView btnsettings;
    LinearLayout btnlogout;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_settings);

  firebaseAuth = FirebaseAuth.getInstance();
       firebaseFirestore = FirebaseFirestore.getInstance();

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