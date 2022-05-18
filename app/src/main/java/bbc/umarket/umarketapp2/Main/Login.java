package bbc.umarket.umarketapp2.Main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;


import java.util.HashMap;
import java.util.Objects;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.R;

//Just Press CTRL+ALT+L on Windows. The android studio will reformat all the code for you
public class Login extends AppCompatActivity {
    Animation topAnim, bottomAnim;
    TextView logintext, btnReg;
    TextInputLayout l_user, l_pass;
    Button login;
    EditText et_studId, et_pass;
    public Bundle bundle;
    CheckBox RememberMe;
    String Entered_pass, Entered_studID, fnamefromDB, lnamefromDB, studIDfromDB, phonefromDB, genderfromDB, bdayfromDB, emailfromDB, passFromDB, sellerFromDB;

    ProgressBar login_progressbar;

    //Authentication
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();

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
        login_progressbar = findViewById(R.id.login_progressbar);

        login = findViewById(R.id.btnLogin);
        l_user = findViewById(R.id.LoginInputStudID);
        l_pass = findViewById(R.id.LoginInputPass);
        RememberMe = findViewById(R.id.chkremember_me);

        et_studId = findViewById(R.id.loginedittext_StudID);
        et_pass = findViewById(R.id.loginedittext_Pass);

        logintext.setAnimation(topAnim);

        //Check whether studid and pass is already saved in Shared Preferences or not
        SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailsFromSession();
            et_studId.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONSTUDID));
            et_pass.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASS));
        }

        btnReg.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });

    }

    public void letTheUserLoggedIn(View view) {
        login_progressbar.setVisibility(View.VISIBLE);
        if (validatestudID() | validatePass()) {

            if (!isConnected(this)) {
                showCustomDialog();
            } else {
                isUser();
            }
        }
    }

    //Checking for Internet Connection
    private boolean isConnected(Login login) {
        ConnectivityManager connectivityManager = (ConnectivityManager) login.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private void showCustomDialog() {
        login_progressbar.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        AlertDialog dialog = builder.setTitle("")
                .setMessage("Please connect to the internet")
                .setCancelable(false)
                .setPositiveButton("Connect", (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                })
                .create();
        dialog.show();

    }

    private void isUser() {
        Entered_studID = Objects.requireNonNull(l_user.getEditText()).getText().toString().toLowerCase();
        Entered_pass = Objects.requireNonNull(l_pass.getEditText()).getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users");
        Query checkUser = reference.orderByChild("studID").equalTo(Entered_studID);


        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    l_user.setError(null);
                    l_user.setErrorEnabled(false);
                    passFromDB = snapshot.child(Entered_studID).child("password").getValue(String.class);
                    assert passFromDB != null;
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


                        firebaseAuth.signInWithEmailAndPassword(emailfromDB, passFromDB).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                login_progressbar.setVisibility(View.INVISIBLE);

                                //Create a Session
                                if (RememberMe.isChecked()) {
                                    SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);
                                    sessionManager.createRememberMeSession(Entered_studID, Entered_pass);
                                }

                                SessionManager sessionManager2 = new SessionManager(Login.this, SessionManager.SESSION_USERSESSION);
                                sessionManager2.createLoginSession(fnamefromDB, lnamefromDB, studIDfromDB, phonefromDB, genderfromDB,
                                        bdayfromDB, emailfromDB, passFromDB, sellerFromDB);

                                Intent intent = new Intent(Login.this, HomeContainer.class);
                                startActivity(intent);
                                finish();
                            }
                        });

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
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
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