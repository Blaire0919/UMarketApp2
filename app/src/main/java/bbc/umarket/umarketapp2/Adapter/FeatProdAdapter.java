package bbc.umarket.umarketapp2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.Helper.FeatProdtHelperClass;
import bbc.umarket.umarketapp2.Main.ProductDetails;
import bbc.umarket.umarketapp2.R;

public class FeatProdAdapter extends RecyclerView.Adapter<FeatProdAdapter.FeaturedViewHolder> {
    ArrayList<FeatProdtHelperClass> featprod;
    Context context;


    public FeatProdAdapter(Context context, ArrayList<FeatProdtHelperClass> featprod) {
        this.featprod = featprod;
        this.context = context;
    }

    @Override
    public FeaturedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_featcard, parent, false);
        return new FeaturedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        Glide.with(context)
                .load(featprod.get(position).getImageUrl())
                .into(holder.image);
        holder.title.setText(featprod.get(position).getpName());
        holder.price.setText(featprod.get(position).getpPrice());

        holder.cardView.setOnClickListener(v -> {

            Intent intent = new Intent(context, ProductDetails.class);
            intent.putExtra("pID", featprod.get(position).getpID());
            intent.putExtra("imageUrl", featprod.get(position).getImageUrl());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return featprod.size();
    }

    public static class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price;
        CardView cardView;

        public FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ft_img01);
            title = itemView.findViewById(R.id.ft_txt01);
            price = itemView.findViewById(R.id.ft_price);
            cardView = itemView.findViewById(R.id.featuredCard);
        }
    }
}
