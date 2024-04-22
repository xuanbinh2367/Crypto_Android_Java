package example.txb.crypto_android.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceConnections {
    public static class SocketServiceConnection implements ServiceConnection{
        private ServiceCreatedCallback serviceCreatedCallback;
        public SocketServiceConnection(ServiceCreatedCallback serviceCreatedCallback){
            this.serviceCreatedCallback = serviceCreatedCallback;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            SocketService.MyBinder myBinder = (SocketService.MyBinder) binder;
            serviceCreatedCallback.setService(myBinder.getService());
            serviceCreatedCallback.setIsBound(true);
            serviceCreatedCallback.createdComplete();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceCreatedCallback.setIsBound(false);
        }

        @Override
        public void onBindingDied(ComponentName name) {
            ServiceConnection.super.onBindingDied(name);
        }

        @Override
        public void onNullBinding(ComponentName name) {
            ServiceConnection.super.onNullBinding(name);
        }

    }
    public static class CoinServiceConnection implements ServiceConnection{
        private ServiceCreatedCallback serviceCreatedCallback;
        public CoinServiceConnection(ServiceCreatedCallback serviceCreatedCallback){
            this.serviceCreatedCallback = serviceCreatedCallback;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            CoinService.MyBinder myBinder = (CoinService.MyBinder) binder;
            serviceCreatedCallback.setService(myBinder.getService());
            serviceCreatedCallback.setIsBound(true);
            serviceCreatedCallback.createdComplete();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceCreatedCallback.setIsBound(false);
        }

        @Override
        public void onBindingDied(ComponentName name) {
            ServiceConnection.super.onBindingDied(name);
        }

        @Override
        public void onNullBinding(ComponentName name) {
            ServiceConnection.super.onNullBinding(name);
        }

    }

}
