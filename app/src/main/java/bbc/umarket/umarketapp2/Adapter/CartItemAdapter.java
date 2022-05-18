package bbc.umarket.umarketapp2.Adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.CartHelperClass;

import bbc.umarket.umarketapp2.Helper.CheckOutHelperClass;
import bbc.umarket.umarketapp2.Main.AddToCart;

import bbc.umarket.umarketapp2.R;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {
    ArrayList<CartHelperClass> cartItem;

    CheckOutHelperClass checkOutHelperClass;
    List<String> chkitemID = new ArrayList<>();

    final Context context;
    String itemqty;
    String studid;
    float sum = 0.00F;

    Integer SelectedCount = 0;

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
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final CartHelperClass currentItem = cartItem.get(position);

        SessionManager sessionManager = new SessionManager(context, SessionManager.SESSION_USERSESSION);
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

        holder.selectItem.setOnClickListener(view -> {
            if (holder.selectItem.isChecked()) {
                sum += Float.parseFloat(currentItem.getTotalPrice());
                AddToCart.selected_subtotal(String.format("%.2f", sum));
                holder.add.setEnabled(false);
                holder.add.setBackgroundColor(Color.LTGRAY);
                holder.minus.setEnabled(false);
                holder.minus.setBackgroundColor(Color.LTGRAY);
            }

            if (!holder.selectItem.isChecked()) {
                if (cartItem.get(position).getProdQty().equals("1")) {
                    itemqty = cartItem.get(position).getProdQty();
                    holder.qty.setText(itemqty);
                    holder.minus.setEnabled(false);
                    holder.minus.setBackgroundColor(Color.LTGRAY);
                    holder.add.setEnabled(true);
                    holder.add.setBackgroundColor(Color.WHITE);
                } else {
                    sum -= Float.parseFloat(currentItem.getTotalPrice());
                    AddToCart.selected_subtotal(String.format("%.2f", sum));
                    holder.add.setEnabled(true);
                    holder.add.setBackgroundColor(Color.WHITE);
                    holder.minus.setEnabled(true);
                    holder.minus.setBackgroundColor(Color.WHITE);
                }
            }
        });

        holder.minus.setOnClickListener(view -> minusCartItem(holder, cartItem.get(position)));

        holder.add.setOnClickListener(view -> addCartItem(holder, cartItem.get(position)));

        holder.delete.setOnClickListener(view -> {
            @SuppressLint("NotifyDataSetChanged") AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Do you really want to delete item?")
                    .setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss())
                    .setPositiveButton("OK", (dialog2, which) -> {
                        notifyItemRemoved(position);
                        cartItem.remove(position);
                        deleteFromFirebase(currentItem);
                        notifyItemRangeChanged(position, cartItem.size());
                        dialog2.dismiss();
                    })
                    .create();
            dialog.show();
        });

        holder.selectItem.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                chkitemID.add(currentItem.getProdID());
                SelectedCount++;
                String totprice = String.valueOf(Float.parseFloat(currentItem.getProdPrice()) * Integer.parseInt(currentItem.getProdQty()));
                checkOutHelperClass = new CheckOutHelperClass(
                        currentItem.getImgUrl(),
                        currentItem.getProdID(),
                        currentItem.getSellerName(),
                        currentItem.getProdName(),
                        currentItem.getProdQty(),
                        String.format("%s", currentItem.getProdPrice()),
                        totprice,
                        currentItem.getSellerID()
                );
                AddToCart.check_out(chkitemID, checkOutHelperClass);
            } else {
                try {
                    chkitemID.remove(currentItem.getProdID());
                    SelectedCount--;
//                    String totprice = String.valueOf(Float.parseFloat(currentItem.getProdPrice()) * Integer.parseInt(currentItem.getProdQty()));
//                    checkOutHelperClass = new CheckOutHelperClass(
//                            currentItem.getProdID(),
//                            currentItem.getProdName(),
//                            currentItem.getProdQty(),
//                            String.format("%s", currentItem.getProdPrice()),
//                            totprice
//                    );
//                    AddToCart.unselectItem();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            AddToCart.selectedItemCount(SelectedCount);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
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
                .addOnSuccessListener(unused -> Log.d(TAG, "Add qty Success!"));

        if (cartHelperClass.getProdQty().equals("1")) {
            itemqty = cartHelperClass.getProdQty();
            holder.qty.setText(itemqty);
            holder.minus.setEnabled(false);
            holder.minus.setBackgroundColor(Color.LTGRAY);
        } else {
            itemqty = cartHelperClass.getProdQty();
            holder.qty.setText(itemqty);
            holder.minus.setEnabled(true);
            holder.minus.setBackgroundColor(Color.WHITE);
        }

        synchronized (context) {
            context.notifyAll();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
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
                    .setValue(cartHelperClass)
                    .addOnSuccessListener(unused -> Log.d(TAG, "minus qty Success!"));

            if (cartHelperClass.getProdQty().equals("1")) {
                itemqty = cartHelperClass.getProdQty();
                holder.qty.setText(itemqty);
                holder.minus.setEnabled(false);
                holder.minus.setBackgroundColor(Color.LTGRAY);
            } else {
                itemqty = cartHelperClass.getProdQty();
                holder.qty.setText(itemqty);
                holder.minus.setEnabled(true);
                holder.minus.setBackgroundColor(Color.WHITE);
            }

            synchronized (context) {
                context.notifyAll();
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteFromFirebase(CartHelperClass cartHelperClass) {
        FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("cart")
                .child(studid)
                .child(cartHelperClass.getProdID())
                .removeValue()
                .addOnSuccessListener(unused -> Log.d(TAG, "delete Success!"));
        notifyDataSetChanged();
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
