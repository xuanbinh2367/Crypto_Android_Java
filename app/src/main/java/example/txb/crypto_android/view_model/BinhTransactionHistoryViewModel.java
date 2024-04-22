package example.txb.crypto_android.view_model;

import android.content.Context;

import example.txb.crypto_android.api.API;
import example.txb.crypto_android.model.SystemNotificationModel;

public class BinhTransactionHistoryViewModel extends BaseViewModel {

    private Context context;

    public BinhTransactionHistoryViewModel(Context context) {
        this.context = context;
    }
    public void getTransactionHistory( OkCallback okCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            getTransactionHistoryAPI( okCallback);
        }).start();
    }

    private void getTransactionHistoryAPI( OkCallback okCallback) {
        try {
            API.RequestParams params = new API.RequestParams();
            API.ResponseAPI response = API.get(context, "/accountMoneyHistory/getList", params);
            if (response.status == API.ResponseAPI.Status.Fail) {
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, response.error));
            } else {
                okCallback.handle(response.data);
//                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"ok"));
            }


        } catch (Exception e) {
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, "Lỗi hệ thống."));
        } finally {
            _isLoading.postValue(false);
        }
    }
}
