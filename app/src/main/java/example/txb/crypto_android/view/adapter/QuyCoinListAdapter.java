package example.txb.crypto_android.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.CoinServiceModel;

public class QuyCoinListAdapter extends RecyclerView.Adapter<QuyCoinListAdapter.ViewHolderCustom> {
    private ArrayList<CoinServiceModel.CoinNow> coins = new ArrayList<>();
    private ClickCallback clickCallback;
    private int sortStatus = SortStatus.None;

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }
    public static class SortStatus{
        public static int Increase = 1;
        public static int Decrease = -1;
        public static int None = 0;
    }
    Context context;
    public QuyCoinListAdapter(Context context, int status){
        this.context = context;
        this.sortStatus = status;
    }

    public void setList(ArrayList<CoinServiceModel.CoinNow> coins) {
        this.coins = new ArrayList<>(coins);
        if(sortStatus!=SortStatus.None){
            sort();
        }
        this.notifyDataSetChanged();
    }
    private void sort(){
        Collections.sort(this.coins, new Comparator<CoinServiceModel.CoinNow>(){
            public int compare(CoinServiceModel.CoinNow o1, CoinServiceModel.CoinNow o2){
                if(o1.changePercent24Hr == o2.changePercent24Hr)
                    return 0;
                return o1.changePercent24Hr < o2.changePercent24Hr ? 1*sortStatus : -1*sortStatus;
            }
        });
    }
    @Override
    public ViewHolderCustom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderCustom(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quy_coin_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCustom holder, int position) {
        try {
            if((sortStatus==SortStatus.Increase&&coins.get(position).changePercent24Hr<=0)||(sortStatus==SortStatus.Decrease&&coins.get(position).changePercent24Hr>=0)){
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }else{
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            holder.bindData(coins.get(position),context,position);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateList(ArrayList<CoinServiceModel.CoinNow> coinsChange) {
        for (int i = 0; i < coinsChange.size(); i++) {
            int ind = indexOf(coinsChange.get(i).id);
            if(ind!=1){
                this.coins.get(ind).priceUsd = coinsChange.get(i).priceUsd;
                this.coins.get(ind).changePercent24Hr = coinsChange.get(i).changePercent24Hr;
                if(sortStatus==SortStatus.None)this.notifyItemChanged(ind);
            }
        }
        if(sortStatus!=SortStatus.None){
            sort();
            this.notifyDataSetChanged();
        }
    }

    private int indexOf(String coindId){
        for (int i = 0; i < this.coins.size(); i++) {
            if(this.coins.get(i).id.equals(coindId))return i;
        }
        return -1;
    }


    public static interface ClickCallback{
        public void handle(String coinId);
    }

    @Override
    public int getItemCount() {
        return coins != null ? coins.size() : 0;
    }

    public class ViewHolderCustom extends RecyclerView.ViewHolder {

        private TextView name, price, changePercent;
        private ImageView icon;

        public ViewHolderCustom(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.quyCoinListItemIcon);
            name = itemView.findViewById(R.id.quyCoinListItemName);
            price = itemView.findViewById(R.id.quyCoinListItemPrice);
            changePercent = itemView.findViewById(R.id.quyCoinListItemChangePercent);
        }

        public void bindData(CoinServiceModel.CoinNow item, Context context, int position) throws IOException {
            name.setText(item.name);
            price.setText("$ "+String.format("%.2f", item.priceUsd));

            if(item.changePercent24Hr>=0){
                changePercent.setTextColor(Color.GREEN);
                changePercent.setText("+ "+String.format("%.2f", item.changePercent24Hr)+"%");
            }else{
                changePercent.setTextColor(Color.RED);
                changePercent.setText(String.format("%.2f", item.changePercent24Hr)+"%");
            }

            General.setImageUrl(context,icon,item.icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickCallback.handle(item.id);
                }
            });
        }
    }
}