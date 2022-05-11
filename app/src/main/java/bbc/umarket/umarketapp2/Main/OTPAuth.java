package bbc.umarket.umarketapp2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import bbc.umarket.umarketapp2.R;

public class OTPAuth extends AppCompatActivity {
    EditText getphonenum;
    Button sendotp;
    CountryCodePicker mcountrycodepicker;
    String countrycode;
    String phonenumber;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codesent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_otp_auth);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        getphonenum = findViewById(R.id.getphonenumber);
        sendotp =  findViewById(R.id.btnsendotp);
        mcountrycodepicker = findViewById(R.id.countrycodepicker);
        progressBar = findViewById(R.id.progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
        countrycode = mcountrycodepicker.getSelectedCountryCodeWithPlus();
        mcountrycodepicker.setOnCountryChangeListener(() ->
                countrycode = mcountrycodepicker.getSelectedCountryCodeWithPlus()
        );

        sendotp.setOnClickListener(view -> {
            String number;
            number = getphonenum.getText().toString();
            if (number.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please Enter Your number", Toast.LENGTH_SHORT).show();
            }else if (number.length()<10) {
                Toast.makeText(getApplicationContext(), "Please Enter Your correct number", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                phonenumber = countrycode+number;

                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phonenumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(OTPAuth.this)
                        .setCallbacks(mCallbacks)
                        .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //how to automatically fetch code here
                Toast.makeText(getApplicationContext(),
                        "Verification Complete: "+ phoneAuthCredential,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(),  "Error: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("Error: ", e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(), "OTP is Sent", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                codesent=s;

                Intent intent = new Intent(OTPAuth.this, OTPVerificationCode.class);
                intent.putExtra("otp", codesent);
                startActivity(intent);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(OTPAuth.this, HomeContainer.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("back_Chat","Chat");
            startActivity(intent);
        }




    }
}