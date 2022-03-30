package bbc.umarket.umarketapp2.Adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.CartHelperClass;

import bbc.umarket.umarketapp2.Main.AddToCart;
import bbc.umarket.umarketapp2.Main.ProductDetails;
import bbc.umarket.umarketapp2.R;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {
    ArrayList<CartHelperClass> cartItem;
    Context context;

    String itemqty;
    String studid;


    public CartItemAdapter(Context context, ArrayList<CartHelperClass> cartItem) {
        this.cartItem = cartItem;
        this.context = context;
    }

    @NonNull
    @Override
    public CartItemAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_cart_item, parent, false);
        return new CartItemAdapter.CartViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        final CartHelperClass currentItem = cartItem.get(position);

        SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        Glide.with(context)
                .load(currentItem.getImgUrl())
                .into(holder.img);
        holder.sellername.setText(currentItem.getSellerName());
        holder.prodname.setText(currentItem.getProdName());
        holder.price.setText(currentItem.getProdPrice());
        holder.qty.setText(String.valueOf(currentItem.getProdQty()));


        if (cartItem.get(position).getProdQty().equals("1")) {
            itemqty = cartItem.get(position).getProdQty();
            holder.qty.setText(itemqty);
            holder.minus.setEnabled(false);
            holder.minus.setBackgroundColor(Color.LTGRAY);
        }

        holder.minus.setOnClickListener(view ->


                minusCartItem(holder, cartItem.get(position))


        );

        holder.add.setOnClickListener(view -> addCartItem(holder, cartItem.get(position)));

        holder.delete.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Do you really want to delete item?")
                    .setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss())
                    .setPositiveButton("OK", (dialog2, which) -> {

                        //Temporary remove
                        notifyItemRemoved(position);

                        deleteFromFirebase(cartItem.get(position));
                        dialog2.dismiss();
                    })
                    .create();
            dialog.show();
            Intent intent = new Intent(context, AddToCart.class);
            context.startActivity(intent);
        });

    }

    private void addCartItem(CartViewHolder holder, CartHelperClass cartHelperClass) {
        cartHelperClass.setProdQty(String.valueOf(Integer.parseInt(cartHelperClass.getProdQty()) + 1));
        cartHelperClass.setTotalPrice(String.valueOf(Integer.parseInt(cartHelperClass.getProdQty()) * Float.parseFloat(cartHelperClass.getProdPrice())));

        //update quantity
        holder.qty.setText(new StringBuilder().append(cartHelperClass.getProdQty()));

        FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("cart")
                .child(studid)
                .child(cartHelperClass.getProdID())
                .setValue(cartHelperClass)
                .addOnSuccessListener(unused -> Log.d(TAG, "Update Success!"));
    }

    private void minusCartItem(CartViewHolder holder, CartHelperClass cartHelperClass) {
        if (Integer.parseInt(cartHelperClass.getProdQty()) > 1) {
            cartHelperClass.setProdQty(String.valueOf(Integer.parseInt(cartHelperClass.getProdQty()) - 1));
            cartHelperClass.setTotalPrice(String.valueOf(Integer.parseInt(cartHelperClass.getProdQty()) * Float.parseFloat(cartHelperClass.getProdPrice())));


            //update quantity
            holder.qty.setText(new StringBuilder().append(cartHelperClass.getProdQty()));

            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("cart")
                    .child(studid)
                    .child(cartHelperClass.getProdID())
                    .setValue(cartItem)
                    .addOnSuccessListener(unused -> Log.d(TAG, "Update Success!"));
        }
    }

    private void deleteFromFirebase(CartHelperClass cartHelperClass) {
        FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("cart")
                .child(studid)
                .child(cartHelperClass.getProdID())
                .removeValue()
                .addOnSuccessListener(unused -> Log.d(TAG, "Remove Success!"));
    }

    @Override
    public int getItemCount() {
        return cartItem.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView img, delete;
        TextView sellername, prodname, price, qty;
        CheckBox selectItem;

        //qty button
        MaterialCardView add, minus;

        public CartViewHolder(@NonNull View cartitemView) {
            super(cartitemView);
            img = cartitemView.findViewById(R.id.cartitemimg);
            sellername = cartitemView.findViewById(R.id.cartitemseller);
            prodname = cartitemView.findViewById(R.id.cartitemname);
            price = cartitemView.findViewById(R.id.cartitemprice);
            selectItem = cartitemView.findViewById(R.id.chkcartselect);
            delete = cartitemView.findViewById(R.id.remove_item);

            qty = cartitemView.findViewById(R.id.tvQty);
            add = cartitemView.findViewById(R.id.qty_add);
            minus = cartitemView.findViewById(R.id.qty_minus);

        }

    }


}
