package example.txb.crypto_android.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import example.txb.crypto_android.R;
import example.txb.crypto_android.model.socket.SocketServiceEventsModel;
import example.txb.crypto_android.service.CoinService;
import example.txb.crypto_android.service.ServiceConnections;
import example.txb.crypto_android.service.ServiceCreatedCallback;
import example.txb.crypto_android.service.SocketService;


public class QuyMainActivity extends BaseActivity {
    BanEditMainActivityHomeFragment banEditMainActivityHomeFragment;


    QuyMainActivityInterestedCoinsFragment quyMainActivityInterestedCoinsFragment;
    QuyMainActivityTradingFragment quyMainActivityTradingFragment;
    BinhMainActivityProfileFragment binhMainActivityProfileFragment;

    private String REGISTER_COIN_SERVICE_NAME = "main-activity";
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

        }
    }

    private String REGISTER_SOCKET_SERVICE_NAME = "main-activity";
    private SocketService socketService;
    private Boolean isBoundSocketService;
    private ServiceConnection socketServiceConnection = new ServiceConnections.SocketServiceConnection(new SocketServiceCreatedCallback());
    private class SocketServiceCreatedCallback implements ServiceCreatedCallback{
        @Override
        public void setService(Service service) {
            socketService = (SocketService) service;
        }
        @Override
        public void setIsBound(Boolean isBound) {
            isBoundSocketService = isBound;
        }
        @Override
        public void createdComplete() {
            System.out.println("SocketServiceCreatedCallback");
            socketService.addEventListener(SocketServiceEventsModel.EventNames.Receive.AutoCloseTradingCommand, REGISTER_SOCKET_SERVICE_NAME, new SocketServiceEventsModel.EventCallbackInterface() {
                @Override
                public void handle(String data) {
                    socketReloadProfile();
                }
            });
        }
    }
    private boolean interestedCoinsChangeStatus;
    public static interface InterestedCoinsChange{
        public void setStatus(boolean status);
        public boolean getStatus();
    }
    private InterestedCoinsChange InterestedCoinsChangeObject = new InterestedCoinsChange() {
        @Override
        public void setStatus(boolean status) {
            interestedCoinsChangeStatus = status;
            if(interestedCoinsChangeStatus==true){
                quyMainActivityInterestedCoinsFragment.getInterestedCoin();
            }
        }

        @Override
        public boolean getStatus() {
            return interestedCoinsChangeStatus;
        }
    };

    public static interface OpenViewCoin{
        public void open(String coinId);
    }
    private OpenViewCoin OpenViewCoinObject = new OpenViewCoin() {
        @Override
        public void open(String coinId) {
            quyMainActivityTradingFragment.changeCoinChartView(coinId);
            viewPager2Adapter.changeLayout(2);
        }
    };
    ViewPager2Adapter viewPager2Adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quy_main);
        getSupportActionBar().hide();


        View[] tabItem = {
                super.getCustomViewTabLayout(R.drawable.quy_home, "Home"),
                super.getCustomViewTabLayout(R.drawable.quy_interested_coin, "Yêu thích"),
                super.getCustomViewTabLayout(R.drawable.quy_trading_coin, "Giao dịch"),
                super.getCustomViewTabLayout(R.drawable.quy_profile, "Hồ sơ")
        };

        banEditMainActivityHomeFragment = new BanEditMainActivityHomeFragment(getApplicationContext());
        banEditMainActivityHomeFragment.setEditInfoNavigation(homeEditInfoNavigation);
        banEditMainActivityHomeFragment.setOpenSettingCallbackObject(openSettingCallbackOj);
        quyMainActivityInterestedCoinsFragment = new QuyMainActivityInterestedCoinsFragment(getApplicationContext(),InterestedCoinsChangeObject,OpenViewCoinObject);
        quyMainActivityTradingFragment = new QuyMainActivityTradingFragment(getApplicationContext(),changeCoinLauncher,InterestedCoinsChangeObject);
        quyMainActivityTradingFragment.setReloadProfileObject(reloadProfileObject);
        binhMainActivityProfileFragment = new BinhMainActivityProfileFragment(getApplicationContext());
        binhMainActivityProfileFragment.setOpenChooseImageActivity(openChooseImageActivity);
        binhMainActivityProfileFragment.setOpenSettingCallbackObject(openSettingCallbackOj);
        binhMainActivityProfileFragment.setReloadProfileObject(reloadProfileObject);



        ArrayList<Fragment> fragments = new ArrayList<>(Arrays.asList(banEditMainActivityHomeFragment, quyMainActivityInterestedCoinsFragment,quyMainActivityTradingFragment, binhMainActivityProfileFragment));
//        ArrayList<Fragment> fragments = new ArrayList<>(Arrays.asList(quyMainActivityTradingFragment,banEditMainActivityHomeFragment, quyMainActivityInterestedCoinsFragment, binhMainActivityProfileFragment));


        ViewPager2 viewPager2 = findViewById(R.id.quyMainViewPager);
        viewPager2Adapter = new ViewPager2Adapter(getSupportFragmentManager(),getLifecycle(),viewPager2,fragments);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setAdapter(viewPager2Adapter);
        TabLayout tabLayout = findViewById(R.id.quyMainTabLayout);
        viewPager2.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager2,
                ((tab, position) -> {
                    tab.setCustomView(tabItem[position]);
                }
            )).attach();

        CoinServiceCreatedCallback serviceCreatedCallback = new CoinServiceCreatedCallback();
        serviceConnection = new ServiceConnections.CoinServiceConnection(serviceCreatedCallback);
        Intent intent = new Intent(QuyMainActivity.this, CoinService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        SocketServiceCreatedCallback socketServiceCreatedCallback = new SocketServiceCreatedCallback();
        socketServiceConnection = new ServiceConnections.SocketServiceConnection(socketServiceCreatedCallback);
        Intent intent2 = new Intent(QuyMainActivity.this, SocketService.class);
        bindService(intent2, socketServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBoundCoinService) {
            coinService.removeEventListener(REGISTER_COIN_SERVICE_NAME);
            unbindService(serviceConnection);
            isBoundCoinService = false;
        }
        if (isBoundSocketService) {
            socketService.removeEventListener(REGISTER_SOCKET_SERVICE_NAME);
            unbindService(socketServiceConnection);
            isBoundSocketService = false;
        }
    }

    private void socketReloadProfile(){
        banEditMainActivityHomeFragment.loadData();
        binhMainActivityProfileFragment.loadData();
        quyMainActivityTradingFragment.loadMiniProfile();
    }

    ActivityResultLauncher<Intent> changeCoinLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), o -> {
            if (o.getResultCode()== Activity.RESULT_OK){
                Intent intent = o.getData();
                String coinId = intent.getStringExtra("coinId");
                quyMainActivityTradingFragment.changeCoinChartView(coinId);
                //changeCoinOkCallback.handle(coinId);
            }
        });

    ActivityResultLauncher<Intent> profileOpenChoseLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            o -> {
                if (o.getResultCode() == Activity.RESULT_OK) {
                    Intent data = o.getData();
                    Bitmap selectedImage = null;
                    if (BinhMainActivityProfileFragment.selectImageChooseOption == 1) {
                        selectedImage = (Bitmap) data.getExtras().get("data");
                        System.out.println(selectedImage);
                    } else if (BinhMainActivityProfileFragment.selectImageChooseOption == 2) {
                        Uri selectedImageUrl = data.getData();
                        try {
                            selectedImage = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), selectedImageUrl);

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // Cập nhật ảnh cho cả userImageView và dialogImageView
                    Bitmap finalSelectedImage = selectedImage;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binhMainActivityProfileFragment.setChooseAvatarImage(finalSelectedImage);
                        }
                    });
                }
            }
    );

    BinhMainActivityProfileFragment.OpenChooseImageActivity openChooseImageActivity = new BinhMainActivityProfileFragment.OpenChooseImageActivity() {
        @Override
        public void open(int choose) {
            if (choose==1) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                profileOpenChoseLauncher.launch(takePicture);
            } else if (choose==2) {
                Intent pickPhoto = new Intent();
                pickPhoto.setType("image/*");
                pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                profileOpenChoseLauncher.launch(pickPhoto);
            }
        }
    };
    private class ViewPager2Adapter extends FragmentStateAdapter {
        ViewPager2 viewPager2;
        List<Fragment> fragments;
        public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,ViewPager2 viewPager2,List<Fragment> fragments) {
            super(fragmentManager, lifecycle);
            this.viewPager2 = viewPager2;
            this.fragments = fragments;
        }
        public void changeLayout(int position){
            viewPager2.setCurrentItem(position);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return 4 ;
        }

    }


    BanEditMainActivityHomeFragment.EditInfoNavigation homeEditInfoNavigation = new BanEditMainActivityHomeFragment.EditInfoNavigation() {
        @Override
        public void navigation() {
            //set open edit layout

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewPager2Adapter.changeLayout(3);
                }
            });

        }
    };

    public static interface OpenSettingCallback{
        public void handle();
    }
    private OpenSettingCallback openSettingCallbackOj = new OpenSettingCallback() {
        @Override
        public void handle() {
            Intent intent = new Intent(QuyMainActivity.this, Binh_SettingActivity.class);
            openSettingLauncher.launch(intent);
        }
    };
    ActivityResultLauncher<Intent> openSettingLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), o -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = o.getData();
                    if(intent!=null){
                        boolean isLogout = intent.getBooleanExtra("isLogout",false);
                        if(isLogout) {
                            Intent intent2 = new Intent();
                            intent2.putExtra("isLogout",true);
                            setResult(Activity.RESULT_OK,intent2);
                            finish();
                        }
                    }
                }
            });

        });

    public static interface ReloadProfile{
        public void reload();
    }
    private ReloadProfile reloadProfileObject = new ReloadProfile() {
        @Override
        public void reload() {
            banEditMainActivityHomeFragment.loadData();
            binhMainActivityProfileFragment.loadData();
        }
    };
}