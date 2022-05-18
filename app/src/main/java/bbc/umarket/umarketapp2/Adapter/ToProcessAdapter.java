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

import bbc.umarket.umarketapp2.Helper.NotifModel;
import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.Main.ProductDetails;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.SellerSide.ProcessOrder;


public class ToProcessAdapter extends RecyclerView.Adapter<ToProcessAdapter.ToProcessViewHolder> {
    ArrayList<ToProcessModel> toProcessList;
    Context context;

    public ToProcessAdapter(Context context, ArrayList<ToProcessModel> toProcessList) {
        this.toProcessList = toProcessList;
        this.context = context;
    }

    @NonNull
    @Override
    public ToProcessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_orders, parent, false);
        return new ToProcessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToProcessViewHolder holder, int position) {

        ToProcessModel toProcessModel = toProcessList.get(position);

        holder.buyerName.setText(toProcessModel.getBuyerName());
        holder.prodID.setText(toProcessModel.getProdID());
        holder.prodName.setText(toProcessModel.getProdName());
        holder.price.setText(toProcessModel.getPrice());
        holder.qty.setText(toProcessModel.getQty());
        holder.totAmt.setText(toProcessModel.getTotAmt());
        holder.datetime.setText(String.format("%s %s", toProcessModel.getCurrentdate(), toProcessModel.getCurrenttime()));

        holder.card.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProcessOrder.class);
            intent.putExtra("buyername", toProcessModel.getBuyerName());
            intent.putExtra("pid", toProcessModel.getProdID());
            intent.putExtra("prodname", toProcessModel.getProdName());
            intent.putExtra("price", toProcessModel.getPrice());
            intent.putExtra("qty", toProcessModel.getQty());
            intent.putExtra("totamt", toProcessModel.getTotAmt());
            intent.putExtra("buyerid", toProcessModel.getBuyerID());

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return toProcessList.size();
    }

    public static class ToProcessViewHolder extends RecyclerView.ViewHolder {
        TextView buyerName, prodID, prodName, price, qty, totAmt, datetime;
        CardView card;

        public ToProcessViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.order_card);
            buyerName = itemView.findViewById(R.id.txt_buyersname);
            prodID = itemView.findViewById(R.id.txt_prodid);
            prodName = itemView.findViewById(R.id.txt_prodname);
            price = itemView.findViewById(R.id.txt_price);
            qty = itemView.findViewById(R.id.txt_qty);
            totAmt = itemView.findViewById(R.id.txt_totamt);
            datetime = itemView.findViewById(R.id.txt_datetime);


        }
    }


}
