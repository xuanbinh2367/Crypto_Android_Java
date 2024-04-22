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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.CoinServiceModel;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.model.response.QuyProfileResponseModel;
import example.txb.crypto_android.service.CoinService;
import example.txb.crypto_android.service.ServiceConnections;
import example.txb.crypto_android.service.ServiceCreatedCallback;
import example.txb.crypto_android.view_model.BaseViewModel;
import example.txb.crypto_android.view_model.QuyProfileViewModel;

public class QuyMainActivityInterestedCoinsFragment extends Fragment {
    private QuyProfileViewModel quyProfileViewModel;
    private QuyProfileResponseModel.InterestedCoins interestedCoins;

    private QuyMainActivity.InterestedCoinsChange interestedCoinsChange;

    private String REGISTER_COIN_SERVICE_NAME = "interested-coin-list-fragment";
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
            //
//            getData();
        }
    }


    private QuyCoinListFragment normalList, increaseList, decreaseList;
    private LinearLayout loadingLayout;

    private QuyMainActivity.OpenViewCoin openViewCoinObject;

    Context context;
    public QuyMainActivityInterestedCoinsFragment(Context context, QuyMainActivity.InterestedCoinsChange interestedCoinsChange, QuyMainActivity.OpenViewCoin openViewCoinObject) {
        this.context = context;
        quyProfileViewModel = new QuyProfileViewModel(context);
        this.interestedCoinsChange = interestedCoinsChange;
        this.openViewCoinObject = openViewCoinObject;

        CoinServiceCreatedCallback serviceCreatedCallback = new CoinServiceCreatedCallback();
        serviceConnection = new ServiceConnections.CoinServiceConnection(serviceCreatedCallback);
        Intent intent = new Intent(context, CoinService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quy_main_activity_interested_coins, container, false);

        loadingLayout = view.findViewById(R.id.loadingLayout);

        View[] tabItem = {
                BaseActivity.getCustomViewTabLayout(getActivity().getApplicationContext(),R.drawable.quy_cryptocurrency, "Coins"),
                BaseActivity.getCustomViewTabLayout(getActivity().getApplicationContext(),R.drawable.quy_increase_coin, "Top tăng giá"),
                BaseActivity.getCustomViewTabLayout(getActivity().getApplicationContext(),R.drawable.quy_decrease_coin, "Top giảm giá")
        };

        normalList = new QuyCoinListFragment(getContext(),QuyCoinListFragment.SortStatus.None);
        normalList.setChooseCallback(new ChooseCallback());

        increaseList = new QuyCoinListFragment(getContext(),QuyCoinListFragment.SortStatus.Increase);
        increaseList.setChooseCallback(new ChooseCallback());

        decreaseList = new QuyCoinListFragment(getContext(),QuyCoinListFragment.SortStatus.Decrease);
        decreaseList.setChooseCallback(new ChooseCallback());

        ArrayList<Fragment> fragments = new ArrayList<>(Arrays.asList(normalList, increaseList, decreaseList));
        ViewPager2 viewPager2 = view.findViewById(R.id.quyMainActivityInterestedCoinsFragmentViewPager);
        viewPager2.setAdapter(new ViewPager2Adapter(getActivity().getSupportFragmentManager(),getLifecycle(),viewPager2,fragments));
        TabLayout tabLayout = view.findViewById(R.id.quyMainActivityInterestedCoinsFragmentTabLayout);
        viewPager2.setUserInputEnabled(true);
        new TabLayoutMediator(tabLayout, viewPager2,
                ((tab, position) -> {
                    tab.setCustomView(tabItem[position]);
                }
                )).attach();




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setObserve();
        getInterestedCoin();
    }

    public void setObserve(){
        quyProfileViewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
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
        quyProfileViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isLoading==true){
                            loadingLayout.setVisibility(View.VISIBLE);
                        }else{
                            loadingLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
    public void getInterestedCoin(){
        quyProfileViewModel.getInterestedCoins(new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                interestedCoins = new Gson().fromJson(data, QuyProfileResponseModel.InterestedCoins.class);
                getCoinData();
                interestedCoinsChange.setStatus(false);
            }
        });
    }
    private class GetCoinDataCallback implements CoinService.GetAllWaitCallback {
        @Override
        public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<CoinServiceModel.CoinNow> gettingCoins = new ArrayList<>(coins.stream().filter(e->interestedCoins.items.contains(e.id)).collect(Collectors.toList()));
                    Collections.sort(gettingCoins, new Comparator<CoinServiceModel.CoinNow>(){
                        public int compare(CoinServiceModel.CoinNow o1, CoinServiceModel.CoinNow o2){
                            return o1.name.compareTo(o2.name);
                        }
                    });
                    normalList.setList(gettingCoins);
                    increaseList.setList(gettingCoins);
                    decreaseList.setList(gettingCoins);
                    List<String> coinIds = gettingCoins.stream().map(e->e.id).collect(Collectors.toList());
                    coinService.addEventListener(new ArrayList<>(coinIds), REGISTER_COIN_SERVICE_NAME, new CoinChangeValue());
                }
            });
        }
    }
    private void getCoinData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i=0;
                    while(true){
                        System.out.println("QuyMainActivityInterestedCoinsFragment: "+i++);
                        if(isBoundCoinService){
                            coinService.getAllCoins(new GetCoinDataCallback());
                            break;
                        }
                        Thread.sleep(300);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }
    private void updateData(ArrayList<CoinServiceModel.CoinNow> coins){
        normalList.updateValues(coins);
        increaseList.updateValues(coins);
        decreaseList.updateValues(coins);
    }

    private class CoinChangeValue implements CoinServiceModel.EventCallbackInterface{
        @Override
        public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
            updateData(coins);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBoundCoinService) {
            coinService.removeEventListener(REGISTER_COIN_SERVICE_NAME);
            context.unbindService(serviceConnection);
            isBoundCoinService = false;
        }
    }

    private class ChooseCallback implements QuyCoinListFragment.ChooseCallback{
        @Override
        public void choose(String coinId) {
            openViewCoinObject.open(coinId);
        }
    }
    private class ViewPager2Adapter extends FragmentStateAdapter {
        ViewPager2 viewPager2;
        List<Fragment> fragments;
        public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ViewPager2 viewPager2, List<Fragment> fragments) {
            super(fragmentManager, lifecycle);
            this.viewPager2 = viewPager2;
            this.fragments = fragments;
        }
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}