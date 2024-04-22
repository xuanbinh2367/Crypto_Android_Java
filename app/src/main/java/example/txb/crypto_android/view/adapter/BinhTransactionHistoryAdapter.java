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

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.response.BinhTransactionHistoryResponseModel;

public class BinhTransactionHistoryAdapter extends RecyclerView.Adapter<BinhTransactionHistoryAdapter.ViewHolderCustom> {
    private ArrayList<BinhTransactionHistoryResponseModel.Item> items = new ArrayList<>();
    private Context context;

    public void setList(ArrayList<BinhTransactionHistoryResponseModel.Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public BinhTransactionHistoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderCustom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderCustom(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.binh_item_balance_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCustom holder, int position) {
        try {
            holder.bindData(items.get(position), context, position);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolderCustom extends RecyclerView.ViewHolder {

        private ImageView imageTransactionArrow;
        private TextView txtTransactionContent;
        private TextView txtTransactionTimeClose;
        private TextView txtTransactionMoney;

        public ViewHolderCustom(View itemView) {
            super(itemView);
            imageTransactionArrow = itemView.findViewById(R.id.img_transaction_arrow);
            txtTransactionContent = itemView.findViewById(R.id.txt_transaction_content);
            txtTransactionTimeClose = itemView.findViewById(R.id.txt_transaction_datetime_close);
            txtTransactionMoney = itemView.findViewById(R.id.txt_transaction_money_change);
        }

        public void bindData(BinhTransactionHistoryResponseModel.Item item, Context context, int position) throws IOException {
            if (item.money > 0) {
                txtTransactionMoney.setTextColor(Color.GREEN);
                imageTransactionArrow.setImageResource(R.drawable.binh_ic_arrow_up);
            } else {
                txtTransactionMoney.setTextColor(Color.RED);
                imageTransactionArrow.setImageResource(R.drawable.binh_ic_arrow_down);
            }
            String content = item.name;
            if(content.indexOf('$')!=-1){
                int ind1 = content.indexOf(",",content.indexOf("$"));
                content = content.substring(0,ind1+1) + "\n" + content.substring(ind1+1, content.length());
                int ind2 = content.indexOf(",",ind1+1);
                content = content.substring(0,ind2+1) + "\n" + content.substring(ind2+1, content.length());
            }


            txtTransactionContent.setText("" + content);

            txtTransactionMoney.setText(String.format("%.2f", item.money) + "$");
            txtTransactionTimeClose.setText(General.convertTimeToDateTime(item.time));
        }
    }
}
