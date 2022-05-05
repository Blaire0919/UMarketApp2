package bbc.umarket.umarketapp2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bbc.umarket.umarketapp2.Main.OTPAuth;
import bbc.umarket.umarketapp2.Main.Settings;
import bbc.umarket.umarketapp2.R;


public class FragChat extends Fragment {
    Context context;
    Button test;


    public FragChat() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.frag_chat, container, false);
        context = view.getContext();

        test = view.findViewById(R.id.testotp);

        test.setOnClickListener(v -> startActivity(new Intent(getActivity(), OTPAuth.class)));




        return view;
    }
}