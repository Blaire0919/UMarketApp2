package bbc.umarket.umarketapp2.Main;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import bbc.umarket.umarketapp2.Fragments.FragChat;
import bbc.umarket.umarketapp2.Fragments.FragHome;
import bbc.umarket.umarketapp2.Fragments.FragProfile;
import bbc.umarket.umarketapp2.R;

public class HomeContainer extends AppCompatActivity {
    //initialize variables
    BottomNavigationView bottomnav;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Fragment selectedFragment = null;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_homecontainer);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new FragHome()).commit();
        }

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        //Assign variables
        bottomnav = findViewById(R.id.bottom_nav);

        Bundle bundle = getIntent().getExtras();

        bottomnav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new FragHome();
                    break;
                case R.id.nav_chat:
                    selectedFragment = new FragChat();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new FragProfile();
                    break;

            }
            assert selectedFragment != null;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, selectedFragment)
                    .commit();
            return true;
        });

        if (bundle != null) {
            if (bundle.getString("back_Acc") != null) {
                selectedFragment = new FragProfile();
                HomeContainer.this.loadFragment(selectedFragment);
            } else if (bundle.getString("back_Home") != null) {
                selectedFragment = new FragHome();
                HomeContainer.this.loadFragment(selectedFragment);
            } else if (bundle.getString("back_Chat") != null) {
                selectedFragment = new FragChat();
                HomeContainer.this.loadFragment(selectedFragment);
            }
        } else {
            selectedFragment = new FragHome();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, selectedFragment)
                    .commit();
        }
    }

    private void loadFragment(Fragment fragment) {
        //Replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.update("status", "Online");
        } catch (Exception exception) {
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