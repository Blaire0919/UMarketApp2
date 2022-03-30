package bbc.umarket.umarketapp2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

import bbc.umarket.umarketapp2.Main.CategorizedListing;
import bbc.umarket.umarketapp2.Helper.CategoryHelperClass;
import bbc.umarket.umarketapp2.R;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    ArrayList<CategoryHelperClass> categorylist;
    Context context;


    public CategoryAdapter(Context context, ArrayList<CategoryHelperClass> categorylist) {
        this.categorylist = categorylist;
        this.context = context;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_categorycard, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

        holder.catname.setText(categorylist.get(position).getpCategtory());

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategorizedListing.class);
            intent.putExtra("pcat", categorylist.get(position).getpCategtory());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return categorylist.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView catname;
        CardView cardView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            catname = itemView.findViewById(R.id.catname);
            cardView = itemView.findViewById(R.id.categoryCard);
        }
    }


}
