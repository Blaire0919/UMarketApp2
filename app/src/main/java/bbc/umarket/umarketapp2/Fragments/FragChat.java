package bbc.umarket.umarketapp2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import bbc.umarket.umarketapp2.Helper.FirebaseModel;
import bbc.umarket.umarketapp2.Main.OTPAuth;

import bbc.umarket.umarketapp2.Main.SpecificChat;
import bbc.umarket.umarketapp2.R;

public class FragChat extends Fragment {
    Context context;
    Button test;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<FirebaseModel, FragChat.NoteViewHolder> chatAdapter;
    RecyclerView recyclerView;

    public FragChat() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_chat, container, false);
        context = view.getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.rvChatThread);
        test = view.findViewById(R.id.testotp);

        //tawagin yung button dito kapag verified na yung phone number and may user credential sa firestore
        test.setOnClickListener(v -> startActivity(new Intent(getActivity(), OTPAuth.class)));


        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            test.setVisibility(View.GONE);
        }else{
            
        }




        Query query = firebaseFirestore.collection("Users");
        FirestoreRecyclerOptions<FirebaseModel> allusername = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query, FirebaseModel.class).build();
        chatAdapter = new FirestoreRecyclerAdapter<FirebaseModel, FragChat.NoteViewHolder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull FragChat.NoteViewHolder noteViewHolder, int i, @NonNull FirebaseModel firebaseModel) {

                noteViewHolder.name.setText(firebaseModel.getName());

                if (firebaseModel.getStatus().equals("Online")) {
                    noteViewHolder.status.setText(firebaseModel.getStatus());
                    noteViewHolder.status.setTextColor(Color.GREEN);
                } else {
                    noteViewHolder.status.setText(firebaseModel.getStatus());
                }

                noteViewHolder.itemView.setOnClickListener(view ->
                        {
                            Intent intent = new Intent(getActivity(), SpecificChat.class);
                            intent.putExtra("name", firebaseModel.getName());
                            intent.putExtra("receiveruid", firebaseModel.getUid());
                            startActivity(intent);
                        }
                );
            }

            @NonNull
            @Override
            public FragChat.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_chat_thread, parent, false);
                return new NoteViewHolder(view1);
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(chatAdapter);

        return view;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.chat_name);
            status = itemView.findViewById(R.id.chat_status);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatAdapter != null) {
            chatAdapter.stopListening();
        }
    }

}