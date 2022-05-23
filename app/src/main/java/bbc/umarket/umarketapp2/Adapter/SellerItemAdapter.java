package bbc.umarket.umarketapp2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import bbc.umarket.umarketapp2.Helper.ItemHelperClass;
import bbc.umarket.umarketapp2.Main.ProductDetails;
import bbc.umarket.umarketapp2.R;

public class SellerItemAdapter extends RecyclerView.Adapter<SellerItemAdapter.ItemViewHolder> {
    Context context;
    ArrayList<ItemHelperClass> itemlist;

    DatabaseReference ratingref = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("rateandreview");

    public SellerItemAdapter(Context context, ArrayList<ItemHelperClass> itemlist) {
        this.context = context;
        this.itemlist = itemlist;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_proditemcard, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context)
                .load(itemlist.get(position)
                        .getImageUrl())
                .into(holder.image);

        holder.title.setText(itemlist.get(position).getpName());
        holder.price.setText(itemlist.get(position).getpPrice());

        ratingref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float average, total = 0.0F, rating;
                int count = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (Objects.equals(dataSnapshot.child("prodID").getValue(String.class), itemlist.get(position).getpID())){
                            rating = Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("rate").getValue(String.class)));
                            total = total + rating;
                            count = count + 1;
                            average = total / count;
                            holder.rate.setRating(average);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });


    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price;
        RatingBar rate;
        CardView cardView;

        public ItemViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.item_img);
            title = view.findViewById(R.id.item_name);
            price = view.findViewById(R.id.item_price);
            rate = view.findViewById(R.id.item_rate);
            cardView = view.findViewById(R.id.itemCardView);

        }

    }

}
