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
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.stream.Collectors;

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

public class Ban_OpenFragment extends Fragment {

    Context context;


    BanCommandViewModel banCommandViewModel;
    private ArrayList<Ban_OpenCommand> openCommands = new ArrayList<>();
    BanTradingCommandModel.OpenTradingCommandList commandResponses;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Ban_OpenAdapter openAdapter;
    Button editButton;
    Button closeButton;

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
        View view = inflater.inflate(R.layout.ban_fragment_open, container, false);
        recyclerView = view.findViewById(R.id.listOpenItem);
        progressBar = view.findViewById(R.id.LoadOpenList);
        openAdapter = new Ban_OpenAdapter(getContext(), openObject);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(openAdapter);
        editButton = view.findViewById(R.id.btnEdit);
        closeButton = view.findViewById(R.id.btnClose);

//        Custom custom = new Custom();
//        fetchData(custom);
        return view;
    }

    private Ban_OpenAdapter.OpenCallback openObject = new Ban_OpenAdapter.OpenCallback() {
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

//    boolean fetchData(FetchDataOKComeback comeback){
//        new Thread(()->{
//            try {
//                Thread.sleep(3000);
//                comeback.handle();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//        }).start();
//        return true;
//    }
//    public interface FetchDataOKComeback {
//        void handle();
//    }
//    public class Custom implements FetchDataOKComeback{
//        @Override
//        public void handle() {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    progressBar.setVisibility(View.GONE);
//                    ArrayList<Ban_OpenCommand> openCommandArrayList = new ArrayList<>();
//                    openCommandArrayList.add(new Ban_OpenCommand("adaw","dwad","dwadx"));
//                    openAdapter.setList(openCommandArrayList);
//
//                }
//            });
//        }
//    }

    public void loadCoins(){
        progressBar.setVisibility(View.VISIBLE);
        coinService.getAllCoins(new CoinService.GetAllWaitCallback() {
            @Override
            public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
                loadCommands(coins);
            }
        });
    }
    private void loadCommands(ArrayList<CoinServiceModel.CoinNow> coins){
        banCommandViewModel.getList("mine", true, new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                commandResponses = new Gson().fromJson(data, BanTradingCommandModel.OpenTradingCommandList.class);
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
        if(convertResponseModelToBan_OpenCommand(coins)){
            openAdapter.setList(openCommands);
            setUpdateRealTimePrice();
        }
    }

    private void setUpdateRealTimePrice(){
        ArrayList<String> coinIds = new ArrayList<>(openCommands.stream().map(e->e.getCoinId()).collect(Collectors.toList()));
        if(coinIds.size()!=0){
            coinService.addEventListener(coinIds, REGISTER_COIN_SERVICE_NAME, "update-command", new CoinServiceModel.EventCallbackInterface() {
                @Override
                public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
                    updateCommand(coins);
                }
            });
        }
    }
    private void updateCommand(ArrayList<CoinServiceModel.CoinNow> coins){
        for (int i = 0; i < openCommands.size(); i++) {
            Ban_OpenCommand item = openCommands.get(i);
            CoinServiceModel.CoinNow coin = getCoinById(coins, item.getCoinId());
            if(coin!=null){
                float profitNow = getProfitNow(item.getBuyOrSell(),coin.priceUsd,item.openPrice,item.coinNumber);
                openAdapter.updateCommand(item.Id,profitNow);
            }
        };
    }

    private CoinServiceModel.CoinNow getCoinById(ArrayList<CoinServiceModel.CoinNow> coins, String coinId){
        for (int i = 0; i < coins.size(); i++) {
            if(coins.get(i).id.equals(coinId))return coins.get(i);
        }
        return null;
    }

    private boolean convertResponseModelToBan_OpenCommand(ArrayList<CoinServiceModel.CoinNow> coins){
        if(commandResponses.items.size()!=openCommands.size()){
            openCommands = new ArrayList<>();
            for (int i = 0; i < commandResponses.items.size(); i++) {
                BanTradingCommandModel.OpenTradingCommandItem item = commandResponses.items.get(i);
                CoinServiceModel.CoinNow coin = getCoinById(coins, item.coinId);
                if(coin!=null){
                    float profitNow = getProfitNow(item.buyOrSell,coin.priceUsd,item.openPrice,item.coinNumber);
                    openCommands.add(new Ban_OpenCommand(item.id,coin.id,coin.name,profitNow,""+item.leverage, coin.icon,item.buyOrSell, item.openPrice, item.coinNumber, item.moneyNumber, item.openTime));
                }else{
                    openCommands.add(new Ban_OpenCommand(item.id,coin.id,"Coin unknown",0f,""+item.leverage, coin.icon,item.buyOrSell, item.openPrice, item.coinNumber, item.moneyNumber, item.openTime));
                }
            };
            return true;
        }else{
            return false;
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
