package bbc.umarket.umarketapp2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.WindowManager;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import bbc.umarket.umarketapp2.Fragments.FragCart;
import bbc.umarket.umarketapp2.Fragments.FragChat;
import bbc.umarket.umarketapp2.Fragments.FragHome;
import bbc.umarket.umarketapp2.Fragments.FragProfile;

public class HomeContainer extends AppCompatActivity {
    //initialize variables
    MeowBottomNavigation bottomNavigation;
    Fragment fragment= null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_homecontainer);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        //Assign variables
        bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_chat));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_cart));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_profile));

        bottomNavigation.setOnShowListener(item -> {

            //Check condition
            switch (item.getId()) {
                case 1:
                    //When id is 1; initialize home fragment
                    fragment = new FragHome();
                    break;
                case 2:
                    //When id is 2; initialize chat fragment
                    fragment = new FragChat();
                    break;
                case 3:
                    //When id is 3; initialize cart fragment
                    fragment = new FragCart();
                    break;
                case 4:
                    //When id is 4; initialize profile fragment
                    fragment = new FragProfile();
                    break;
            }
            //load fragment
            HomeContainer.this.loadFragment(fragment);
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.getString("back_Acc") != null){
                fragment = new FragProfile();
                HomeContainer.this.loadFragment(fragment);
                bottomNavigation.show(4, true);
            }else if (bundle.getString("back_Home") != null) {
                fragment = new FragHome();
                HomeContainer.this.loadFragment(fragment);
                bottomNavigation.show(1, true);
            }else if (bundle.getString("back_Home") != null) {
                fragment = new FragHome();
                HomeContainer.this.loadFragment(fragment);
                bottomNavigation.show(1, true);
            }
        }else {
            //set notification count
            bottomNavigation.setCount(2,"10");
            //set home initially selected
            bottomNavigation.show(1, true);

        }

        bottomNavigation.setOnClickMenuListener(item -> item.getId());

        bottomNavigation.setOnReselectListener(item -> item.getId());
    }

    private void loadFragment(Fragment fragment) {
        //Replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}