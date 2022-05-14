package bbc.umarket.umarketapp2.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import bbc.umarket.umarketapp2.Helper.FirebaseModel;
import bbc.umarket.umarketapp2.Main.OTPAuth;
import bbc.umarket.umarketapp2.Main.ProductDetails;
import bbc.umarket.umarketapp2.Main.SpecificChat;
import bbc.umarket.umarketapp2.R;

public class FragChat extends Fragment {
    Button test;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    FirestoreRecyclerAdapter<FirebaseModel, FragChat.NoteViewHolder> chatAdapter;
    FirestoreRecyclerOptions<FirebaseModel> allusername;
    RecyclerView recyclerView;

    public FragChat() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_chat, container, false);
        context = v.getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = v.findViewById(R.id.rvChatThread);
        test = v.findViewById(R.id.testotp);

        //tawagin yung button dito kapag verified na yung phone number and may user credential sa firestore
        test.setOnClickListener(view -> startActivity(new Intent(getActivity(), OTPAuth.class)));

        test.setVisibility(View.VISIBLE);
        recyclerView.setEnabled(false);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            test.setVisibility(View.GONE);
            recyclerView.setEnabled(true);
        } else {
            test.setVisibility(View.VISIBLE);
            recyclerView.setEnabled(false);
        }

        Query query = firebaseFirestore.collection("Users").whereNotEqualTo("uid", firebaseAuth.getUid());

        allusername = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query, FirebaseModel.class).build();

        chatAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allusername) {

            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull FirebaseModel firebaseModel) {
                noteViewHolder.name.setText(firebaseModel.getName());

                if (firebaseModel.getStatus().equals("Online")) {
                    noteViewHolder.status.setText(firebaseModel.getStatus());
                    noteViewHolder.status.setTextColor(Color.GREEN);
                } else {
                    noteViewHolder.status.setText(firebaseModel.getStatus());
                }
                try {
                    noteViewHolder.card.setOnClickListener(view1 -> {
                        try {
                            Intent intent = new Intent(getActivity(), SpecificChat.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("name", firebaseModel.getName());
                            intent.putExtra("receiveruid", firebaseModel.getUid());
                            startActivity(intent);
                        } catch (Exception exception) {
                            Log.d("EXCEPTION", exception.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Log.d("EXCEPTION", e.getMessage());
                }
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_chat_thread, parent, false);
                return new NoteViewHolder(view);
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(chatAdapter);

        return v;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        CardView card;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.chat_name);
            status = itemView.findViewById(R.id.chat_status);
            card = itemView.findViewById(R.id.chat_card);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
        if (documentReference != null) {

            try {
                documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
                documentReference.update("status", "Online");}
            catch (Exception exception) {
                Log.d("EXCEPTION", exception.getMessage());
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatAdapter != null) {
            chatAdapter.stopListening();
        }

        if (documentReference != null) {
            try {
                documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
                documentReference.update("status", "Offline");
            } catch (Exception exception) {
                Log.d("EXCEPTION", exception.getMessage());
            }

        }


    }

}