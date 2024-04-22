package example.txb.crypto_android.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import example.txb.crypto_android.model.SystemNotificationModel;

public class BaseViewModel extends ViewModel {
    protected MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    protected MutableLiveData<SystemNotificationModel> _notification = new MutableLiveData<>();

    // Getter method for the LiveData
    public LiveData<Boolean> isLoading() {
        return _isLoading;
    }
    public LiveData<SystemNotificationModel> notification() {
        return _notification;
    }

    // Method to update the data
//    public void updateData(boolean isLoading) {
//        _isLoading.setValue(isLoading);
//    }
    public static interface OkCallback{
        public void handle(String data);
    }
}
