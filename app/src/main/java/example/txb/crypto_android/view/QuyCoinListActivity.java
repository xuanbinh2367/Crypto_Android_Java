package example.txb.crypto_android.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import example.txb.crypto_android.R;
import example.txb.crypto_android.model.CoinServiceModel;
import example.txb.crypto_android.service.CoinService;
import example.txb.crypto_android.service.ServiceConnections;
import example.txb.crypto_android.service.ServiceCreatedCallback;

public class QuyCoinListActivity extends BaseActivity {
    private String REGISTER_COIN_SERVICE_NAME = "coin-list-activity";
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
            getData();
        }
    }


    private QuyCoinListFragment normalList, increaseList, decreaseList;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quy_coin_list);

        getSupportActionBar().setTitle("Danh sách coin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View[] tabItem = {
                super.getCustomViewTabLayout(R.drawable.quy_cryptocurrency, "Coins"),
                super.getCustomViewTabLayout(R.drawable.quy_increase_coin, "Top tăng giá"),
                super.getCustomViewTabLayout(R.drawable.quy_decrease_coin, "Top giảm giá")
        };

        normalList = new QuyCoinListFragment(getApplicationContext(),QuyCoinListFragment.SortStatus.None);
        normalList.setChooseCallback(new ChooseCallback());

        increaseList = new QuyCoinListFragment(getApplicationContext(),QuyCoinListFragment.SortStatus.Increase);
        increaseList.setChooseCallback(new ChooseCallback());

        decreaseList = new QuyCoinListFragment(getApplicationContext(),QuyCoinListFragment.SortStatus.Decrease);
        decreaseList.setChooseCallback(new ChooseCallback());

        ArrayList<Fragment> fragments = new ArrayList<>(Arrays.asList(normalList, increaseList, decreaseList));
        ViewPager2 viewPager2 = findViewById(R.id.quyCoinListActivityViewPager);
        viewPager2.setAdapter(new ViewPager2Adapter(getSupportFragmentManager(),getLifecycle(),viewPager2,fragments));
        TabLayout tabLayout = findViewById(R.id.quyCoinListActivityTabLayout);
        viewPager2.setUserInputEnabled(true);
        new TabLayoutMediator(tabLayout, viewPager2,
                ((tab, position) -> {
                    tab.setCustomView(tabItem[position]);
                }
                )).attach();


        CoinServiceCreatedCallback serviceCreatedCallback = new CoinServiceCreatedCallback();
        serviceConnection = new ServiceConnections.CoinServiceConnection(serviceCreatedCallback);
        Intent intent = new Intent(this, CoinService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetDataCallback implements CoinService.GetAllWaitCallback {
        @Override
        public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Collections.sort(coins, new Comparator<CoinServiceModel.CoinNow>(){
                        public int compare(CoinServiceModel.CoinNow o1, CoinServiceModel.CoinNow o2){
                            return o1.name.compareTo(o2.name);
//                            if(o1.name.equals(o2.name))
//                                return 0;
//                            return o1.changePercent24Hr < o2.changePercent24Hr ? -1 : 1;
                        }
                    });
                    normalList.setList(coins);
                    increaseList.setList(coins);
                    decreaseList.setList(coins);
                    List<String> coinIds = coins.stream().map(e->e.id).collect(Collectors.toList());
                    coinService.addEventListener(new ArrayList<>(coinIds), REGISTER_COIN_SERVICE_NAME, new CoinChangeValue());
                }
            });
        }
    }
    private void getData(){
        coinService.getAllCoins(new GetDataCallback());
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
    protected void onDestroy() {
        super.onDestroy();
        if (isBoundCoinService) {
            coinService.removeEventListener(REGISTER_COIN_SERVICE_NAME);
            unbindService(serviceConnection);
            isBoundCoinService = false;
        }
    }

    private class ChooseCallback implements QuyCoinListFragment.ChooseCallback{
        @Override
        public void choose(String coinId) {
            Intent intent = new Intent();
            intent.putExtra("coinId",coinId);
            setResult(Activity.RESULT_OK, intent);
            finish();
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