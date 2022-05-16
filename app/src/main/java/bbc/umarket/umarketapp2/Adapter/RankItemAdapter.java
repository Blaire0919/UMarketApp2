package bbc.umarket.umarketapp2.Adapter;

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
import bbc.umarket.umarketapp2.Helper.ItemRankedHelperClass;
import bbc.umarket.umarketapp2.R;

public class RankItemAdapter extends RecyclerView.Adapter<RankItemAdapter.RankedViewHolder> {
    ArrayList<ItemRankedHelperClass> itemranked;
    Context context;

    public RankItemAdapter(Context context, ArrayList<ItemRankedHelperClass > itemranked) {
        this.itemranked = itemranked;
        this.context = context;
    }

    @NonNull
    @Override
    public RankedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_ranking_item, parent, false);
        return new RankedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankedViewHolder holder, int position) {

            Glide.with(context)
                    .load(itemranked.get(position).getRankItemImg())
                    .into(holder.image);
            holder.counter.setText(String.valueOf(itemranked.size()-position));
            holder.name.setText(itemranked.get(position).getRankItemName());

    }

    @Override
    public int getItemCount() {
        return itemranked.size();
    }

    public static class RankedViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView counter, name;

        public RankedViewHolder(@NonNull View itemView) {
            super(itemView);
            counter = itemView.findViewById(R.id.ranked_counter);
            image = itemView.findViewById(R.id.ranked_img);
            name = itemView.findViewById(R.id.ranked_name);
        }
    }
}
