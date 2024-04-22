package example.txb.crypto_android.view;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.CoinServiceModel;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.model.response.BanTradingCommandModel;
import example.txb.crypto_android.service.CoinService;
import example.txb.crypto_android.service.ServiceConnections;
import example.txb.crypto_android.service.ServiceCreatedCallback;
import example.txb.crypto_android.view_model.BanCommandViewModel;
import example.txb.crypto_android.view_model.BaseViewModel;


public class Ban_CloseFragment extends Fragment {
    Context context;
    BanCommandViewModel banCommandViewModel;
    private ArrayList<Ban_CloseCommand> closeCommands = new ArrayList<>();
    BanTradingCommandModel.CloseTradingCommandList commandResponses;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Ban_CloseAdapter closeAdapter;

    private String REGISTER_COIN_SERVICE_NAME = "ban-open-trading-fragment";
    private CoinService coinService;
    private Boolean isBoundCoinService=false;
    private ServiceConnection serviceConnection;

    private class CoinServiceCreatedCallback implements ServiceCreatedCallback {
        @Override
        public void setService(Service service) {
            coinService = (CoinService) service;
        }
        @Override
        public void setIsBound(Boolean isBound) {
            isBoundCoinService = isBound;
        }
        @Override
        public void createdComplete() {
            loadCoins();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        banCommandViewModel = new BanCommandViewModel(getContext());
        setObserve();

        CoinServiceCreatedCallback serviceCreatedCallback = new CoinServiceCreatedCallback();
        serviceConnection = new ServiceConnections.CoinServiceConnection(serviceCreatedCallback);
        Intent intent = new Intent(getActivity(), CoinService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ban_fragment_close, container, false);
        recyclerView = view.findViewById(R.id.listCloseItem);
        progressBar = view.findViewById(R.id.LoadCloseList);
        closeAdapter = new Ban_CloseAdapter(getContext(),openObject);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(closeAdapter);
        return view;
    }

    private Ban_CloseAdapter.OpenCallback openObject = new Ban_CloseAdapter.OpenCallback() {
        @Override
        public void open(String commandId) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getActivity(), QuyViewTradingCommandActivity.class);
                    intent.putExtra("id", commandId);
                    getActivity().startActivity(intent);
                }
            });

        }
    };

    public void reloadData() {
        if(isBoundCoinService){
            loadCoins();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (isBoundCoinService) {
            coinService.removeEventListener(REGISTER_COIN_SERVICE_NAME);
            getActivity().unbindService(serviceConnection);
            isBoundCoinService = false;
        }
    }

    public void setObserve(){
        banCommandViewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel systemNotificationModel) {
                if(systemNotificationModel!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            General.showNotification(getContext(),systemNotificationModel);
                        }
                    });
                }
            }
        });
        banCommandViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isLoading==true){
                            progressBar.setVisibility(View.VISIBLE);
                        }else{
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
    private void loadCoins(){
        progressBar.setVisibility(View.VISIBLE);
        coinService.getAllCoins(new CoinService.GetAllWaitCallback() {
            @Override
            public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
                loadCommands(coins);
            }
        });
    }
    private void loadCommands(ArrayList<CoinServiceModel.CoinNow> coins){
        banCommandViewModel.getList("mine", false, new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                commandResponses = new Gson().fromJson(data, BanTradingCommandModel.CloseTradingCommandList.class);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCommands(coins);
                    }
                });
            }
        });

    }

    private void setCommands(ArrayList<CoinServiceModel.CoinNow> coins){
        convertResponseModelToBan_CloseCommand(coins);
        closeAdapter.setList(closeCommands);
    }


    private CoinServiceModel.CoinNow getCoinById(ArrayList<CoinServiceModel.CoinNow> coins, String coinId){
        for (int i = 0; i < coins.size(); i++) {
            if(coins.get(i).id.equals(coinId))return coins.get(i);
        }
        return null;
    }

    private void convertResponseModelToBan_CloseCommand(ArrayList<CoinServiceModel.CoinNow> coins){
        if(closeCommands.size()!=commandResponses.items.size()){
            closeCommands = new ArrayList<>();
            for (int i = 0; i < commandResponses.items.size(); i++) {
                BanTradingCommandModel.CloseTradingCommandItem item = commandResponses.items.get(i);
                CoinServiceModel.CoinNow coin = getCoinById(coins, item.coinId);
                closeCommands.add(new Ban_CloseCommand(item.id,coin.id,coin.name,item.finalProfit,""+item.leverage, coin.icon,item.buyOrSell, item.openPrice, 1f, item.moneyNumber,item.closeTime));
            };
        }
    }

    private float getProfitNow(String buyOrSell, float priceNow, float openPrice, float coinNumber){
        float profitNow;
        if(buyOrSell.equals("buy")){
            profitNow = (priceNow-openPrice)*coinNumber;
        }else{
            profitNow = (-priceNow+openPrice)*coinNumber;
        }
        return profitNow;
    }


}
