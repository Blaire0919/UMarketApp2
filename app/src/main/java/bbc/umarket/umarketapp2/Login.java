package bbc.umarket.umarketapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;


import java.util.Objects;

import bbc.umarket.umarketapp2.Database.SessionManager;

//Just Press CTRL+ALT+L on Windows. The android studio will reformat all the code for you
public class Login extends AppCompatActivity {
    Animation topAnim, bottomAnim;
    TextView btnReg, logintext, xtratxt;
    TextInputLayout l_user, l_pass;
    RelativeLayout panel;
    Button login;
    public Bundle bundle;
    String Entered_pass, Entered_studID, fnamefromDB, lnamefromDB, studIDfromDB, phonefromDB, genderfromDB, bdayfromDB, emailfromDB, passFromDB, sellerFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        // Animation
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        btnReg = findViewById(R.id.txtRegister);
        logintext = findViewById(R.id.logintxt);
        xtratxt = findViewById(R.id.xtratext);
        panel = findViewById(R.id.panelrect);
        login = findViewById(R.id.btnLogin);
        l_user = findViewById(R.id.LoginInputStudID);
        l_pass = findViewById(R.id.LoginInputPass);

        btnReg.setAnimation(topAnim);
        logintext.setAnimation(topAnim);
        xtratxt.setAnimation(topAnim);
        panel.setAnimation(bottomAnim);

        btnReg.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });

        login.setOnClickListener(v -> LoginUser());

    }

   private void LoginUser() { if (validatestudID() | validatePass()) {isUser();}

    }

      private void isUser() {
        Entered_studID = l_user.getEditText().getText().toString();
        Entered_pass = l_pass.getEditText().getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
        Query checkUser = reference.orderByChild("studID").equalTo(Entered_studID);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    l_user.setError(null);
                    l_user.setErrorEnabled(false);
                    passFromDB = snapshot.child(Entered_studID).child("password").getValue(String.class);
                    if (passFromDB.equals(Entered_pass)) {
                        l_pass.setError(null);
                        l_pass.setErrorEnabled(false);
                        //for passing data to Account Fragment
                        fnamefromDB = snapshot.child(Entered_studID).child("fname").getValue(String.class);
                        lnamefromDB = snapshot.child(Entered_studID).child("lname").getValue(String.class);
                        studIDfromDB = snapshot.child(Entered_studID).child("studID").getValue(String.class);
                        phonefromDB = snapshot.child(Entered_studID).child("contacts").getValue(String.class);
                        genderfromDB = snapshot.child(Entered_studID).child("gender").getValue(String.class);
                        bdayfromDB = snapshot.child(Entered_studID).child("bday").getValue(String.class);
                        emailfromDB = snapshot.child(Entered_studID).child("email").getValue(String.class);
                        passFromDB = snapshot.child(Entered_studID).child("password").getValue(String.class);
                        sellerFromDB = snapshot.child(Entered_studID).child("is_seller").getValue(String.class);
                        //Create a Session
                        SessionManager sessionManager = new SessionManager(Login.this);
                        sessionManager.createLoginSession(fnamefromDB, lnamefromDB, studIDfromDB, phonefromDB, genderfromDB, bdayfromDB, emailfromDB, passFromDB, sellerFromDB);

                        Intent intent = new Intent(Login.this, HomeContainer.class);
                        startActivity(intent);
                        finish();
                    } else {
                        l_pass.setError("Wrong Password");
                        l_pass.requestFocus();
                    }
                } else {
                    l_user.setError("No such User exist");
                    l_user.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }



    private Boolean validatestudID() {
        String val = Objects.requireNonNull(l_user.getEditText()).getText().toString();
        if (val.isEmpty()) {
            l_user.setError("Field cannot be empty");
            return false;
        } else {
            l_user.setError(null);
            l_user.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePass() {
        String val = Objects.requireNonNull(l_pass.getEditText()).getText().toString();
        if (val.isEmpty()) {
            l_pass.setError("Field cannot be empty");
            return false;
        } else {
            l_pass.setError(null);
            l_pass.setErrorEnabled(false);
            return true;
        }
    }


}