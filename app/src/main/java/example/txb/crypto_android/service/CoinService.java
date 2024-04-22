package example.txb.crypto_android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;

import java.util.ArrayList;

import example.txb.crypto_android.model.CoinServiceModel;
import example.txb.crypto_android.model.socket.SocketServiceEventsModel;
import example.txb.crypto_android.view_model.BaseViewModel;
import example.txb.crypto_android.view_model.QuyCoinViewModel;

public class CoinService extends Service {
    private QuyCoinViewModel quyCoinViewModel;
    private String socketName = "coin-service";
    private SocketService socketService;
    private Boolean isBoundSocketService;
    private ServiceConnection serviceConnection;
    private CoinServiceModel.CoinServiceListenerManager coinServiceListenerManager;
    private CoinServiceModel.CoinsNow coinsNow = null;

    public void addEventListener(ArrayList<String> coinsId, String mainAuthor,String subAuthor, CoinServiceModel.EventCallbackInterface callback){
        coinServiceListenerManager.addListener(coinsId,mainAuthor,subAuthor,callback);
    }
    public void addEventListener(ArrayList<String> coinsId, String mainAuthor, CoinServiceModel.EventCallbackInterface callback){
        coinServiceListenerManager.addListener(coinsId,mainAuthor,"main",callback);
    }
    public void removeEventListener(String mainAuthor){
        coinServiceListenerManager.removeListener(mainAuthor);
    }
    public void removeEventListener(String mainAuthor, String subAuthor){
        coinServiceListenerManager.removeListener(mainAuthor,subAuthor);
    }


    public static interface GetAllWaitCallback{
        public void handle(ArrayList<CoinServiceModel.CoinNow> coins);
    }
    public void getAllCoins(GetAllWaitCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = 0;
                    while(true){
                        if(i++>20)break;
                        if(coinsNow!=null)
                        {
                            callback.handle(coinsNow.data);
                            return;
                        };
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                }
                callback.handle(new ArrayList<>());
            }
        }).start();
    }

    public static interface GetOneWaitCallback{
        public void handle(CoinServiceModel.CoinNow coin);
    }
    public void getCoinById(String coinId, GetOneWaitCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = 0;
                    while(true){
                        if(i++>20)break;
                        if(coinsNow!=null)
                        {
                            for (int j = 0; j < coinsNow.data.size(); j++) {
                                if(coinsNow.data.get(j).id.equals(coinId)){
                                    callback.handle(coinsNow.data.get(j));
                                    return;
                                }
                            }
                            callback.handle(null);
                            return;
                        };
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                }
                callback.handle(null);
            }
        }).start();
    }

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
            socketService.addEventListener(SocketServiceEventsModel.EventNames.Receive.CoinsPriceNow,socketName,new UpdateValue());
        }
    }
    private class UpdateValue implements SocketServiceEventsModel.EventCallbackInterface {
        @Override
        public void handle(String data) {
            try{
                coinsNow = new Gson().fromJson(data,CoinServiceModel.CoinsNow.class);
                coinServiceListenerManager.handleEvent(coinsNow.data);
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        coinServiceListenerManager = new CoinServiceModel.CoinServiceListenerManager();
        // Bind to the service
        SocketServiceCreatedCallback serviceCreatedCallback = new SocketServiceCreatedCallback();
        serviceConnection = new ServiceConnections.SocketServiceConnection(serviceCreatedCallback);
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        quyCoinViewModel = new QuyCoinViewModel(getApplicationContext());
        loadFirstTime();
    }

    private void loadFirstTime(){
        quyCoinViewModel.getAll(new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                coinsNow = new Gson().fromJson(data, CoinServiceModel.CoinsNow.class);
                System.out.println("private void loadFirstTime(){");
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    // Binder class to provide access to the service
    public class MyBinder extends Binder {
        CoinService getService() {
            return CoinService.this;
        }
    }

    @Override
    public void onDestroy() {
        socketService.removeEventListener(socketName);
        if (isBoundSocketService) {
            unbindService(serviceConnection);
            isBoundSocketService = false;
        }
        super.onDestroy();
    }
}