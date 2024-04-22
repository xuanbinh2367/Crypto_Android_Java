package example.txb.crypto_android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import example.txb.crypto_android.service.CoinService;
import example.txb.crypto_android.service.ServiceConnections;
import example.txb.crypto_android.service.ServiceCreatedCallback;
import example.txb.crypto_android.service.SocketService;

public class TestActivity extends AppCompatActivity {
    private String REGISTER_COIN_SERVICE_NAME = "view-trading-command-activity";
    private CoinService coinService;
    private Boolean isBoundCoinService=false;
    private ServiceConnection coinServiceConnection = new ServiceConnections.CoinServiceConnection(new CoinServiceCreatedCallback());

    private String REGISTER_SOCKET_SERVICE_NAME = "view-trading-command";
    private SocketService socketService;
    private Boolean isBoundSocketService;
    private ServiceConnection socketServiceConnection = new ServiceConnections.SocketServiceConnection(new SocketServiceCreatedCallback());
    private class SocketServiceCreatedCallback implements ServiceCreatedCallback {
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
        }
    }

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
            System.out.println("CoinServiceCreatedCallback");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //test quy branch

        Intent intentCoinService = new Intent(this, CoinService.class);

        bindService(intentCoinService, coinServiceConnection, Context.BIND_AUTO_CREATE);

        Intent intentSocketService = new Intent(this, SocketService.class);
//
        bindService(intentSocketService, socketServiceConnection, Context.BIND_AUTO_CREATE);
    }
}