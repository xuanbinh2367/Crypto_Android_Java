package example.txb.crypto_android.view.adapter;

import android.content.Context;
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
import example.txb.crypto_android.model.response.QuyProfileResponseModel;

public class BinhRankTopUserAdapter extends RecyclerView.Adapter<BinhRankTopUserAdapter.ViewHolderCustom> {

    private ArrayList<QuyProfileResponseModel.TopUser> topUsers = new ArrayList<>();
    Context context;

    public void setList(ArrayList<QuyProfileResponseModel.TopUser> topUsers) {
        this.topUsers = topUsers;
        notifyDataSetChanged();
        System.out.println("ublic void setList(ArrayList<QuyProfileResponseModel");
    }

    public BinhRankTopUserAdapter(Context context) {
        this.context = context;
    }

    public static interface OpenCallback {
        public void open(String userId);
    }

    OpenCallback openCallback;

    public void setOpenCallback(OpenCallback openCallback) {
        this.openCallback = openCallback;
    }

    @NonNull
    @Override
    public ViewHolderCustom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderCustom(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.binh_item_list_rank, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCustom holder, int position) {
        try {
            holder.bindData(topUsers.get(position), context, position);
        } catch (IOException e) {

        }
    }

    @Override
    public int getItemCount() {
        return topUsers.size();
    }

    public class ViewHolderCustom extends RecyclerView.ViewHolder {


        private ImageView imageRankUser;
        private TextView txtRankUserName;
        private TextView txtRankMoneyProfit;
        private TextView txtRankUserPercent;
        private TextView txtRankTopNumber;
        private TextView txtRankCommandNumber;

        public ViewHolderCustom(@NonNull View view) {
            super(view);
            imageRankUser = view.findViewById(R.id.image_item_rank_user);
            txtRankUserName = view.findViewById(R.id.txt_item_rank_user_name);
            txtRankTopNumber = view.findViewById(R.id.txt_item_rank_top_number);
            txtRankUserPercent = view.findViewById(R.id.txt_item_rank_percent);
            txtRankMoneyProfit = view.findViewById(R.id.txt_item_rank_money_profit);
            txtRankCommandNumber = view.findViewById(R.id.txt_item_trading_command_number);
        }

        public void bindData(QuyProfileResponseModel.TopUser topUser, Context context, int position) throws IOException {
            General.setAvatarUrl(context, imageRankUser, topUser.avatar);
            txtRankUserName.setText(topUser.name);
            txtRankTopNumber.setText("" + topUser.topNumber);
            txtRankMoneyProfit.setText("$" + topUser.moneyNow);
            txtRankCommandNumber.setText("" + topUser.tradingCommandNumber);
            //kiểm tra lại dòng này

            if (topUser.tradingCommandNumber == 0) {
                txtRankUserPercent.setText("0" + "%");
            } else {
                txtRankUserPercent.setText((100 * topUser.tradingCommandProfitNumber / topUser.tradingCommandNumber) + "%");

            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openCallback.open(topUser.id);
                }
            });
        }
    }
}
