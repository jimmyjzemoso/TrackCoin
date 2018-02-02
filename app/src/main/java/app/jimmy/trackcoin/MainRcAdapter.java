package app.jimmy.trackcoin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * @author Jimmy
 *         Created on 15/1/18.
 */

public class MainRcAdapter extends RecyclerView.Adapter<MainRcAdapter.ViewHolder> {
    private ArrayList<CoinDataSet> dataSet;
    private Context mContext;
    public MainRcAdapter(ArrayList<CoinDataSet> coinDataSet, Context mContext) {
        dataSet = coinDataSet;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
// - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.coinName.setText(dataSet.get(position).getCoinName());
        holder.coinCost.setText(dataSet.get(position).getCoinCost());
        Glide.with(mContext).load("https://bitcoin.org/img/icons/opengraph.png").into(holder.coinIcon);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView coinIcon;
        public TextView coinName;
        public TextView coinCost;
        public ViewHolder(View itemView) {
            super(itemView);
            coinIcon = itemView.findViewById(R.id.coin_icon);
            coinName = itemView.findViewById(R.id.coin_name);
            coinCost = itemView.findViewById(R.id.coin_price);

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        coinIcon.setImageAlpha(255);
                    }else {
                        coinIcon.setImageAlpha(120);
                    }
                }
            });
        }
    }
}
