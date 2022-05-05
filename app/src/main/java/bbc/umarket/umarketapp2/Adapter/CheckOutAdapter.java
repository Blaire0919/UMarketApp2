package bbc.umarket.umarketapp2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import bbc.umarket.umarketapp2.Helper.CheckOutHelperClass;
import bbc.umarket.umarketapp2.R;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.CheckOutViewHolder> {
    ArrayList<CheckOutHelperClass> checkoutlist;
    Context context;

    public CheckOutAdapter(Context context, ArrayList<CheckOutHelperClass> checkoutlist) {
        this.checkoutlist = checkoutlist;
        this.context = context;
    }

    @Override
    public CheckOutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_checkoutprods, parent, false);
        return new CheckOutViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CheckOutViewHolder holder, int position) {
        Glide.with(context)
                .load(checkoutlist.get(position).getpUrl())
                .into(holder.img);
        holder.name.setText(checkoutlist.get(position).getProdName());
        holder.seller.setText(checkoutlist.get(position).getSellerName());
        holder.price.setText(String.format("₱ %s", checkoutlist.get(position).getPrice()));
        holder.qty.setText(String.format("QTY: %s", checkoutlist.get(position).getQty()));
        holder.numItems.setText(String.format("(%s Items)", checkoutlist.get(position).getQty()));
        holder.subtot.setText(String.format("₱ %.2f", Float.parseFloat(checkoutlist.get(position).getSubTotal())));
    }

    @Override
    public int getItemCount() {
        return checkoutlist.size();
    }

    public static class CheckOutViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, seller, price, qty, numItems, subtot;

        public CheckOutViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.coImg);
            name = itemView.findViewById(R.id.coName);
            seller = itemView.findViewById(R.id.coSellerName);
            price = itemView.findViewById(R.id.coPrice);
            qty = itemView.findViewById(R.id.coQty);
            numItems = itemView.findViewById(R.id.coNumItems);
            subtot = itemView.findViewById(R.id.coSubTot);
        }
    }
}
