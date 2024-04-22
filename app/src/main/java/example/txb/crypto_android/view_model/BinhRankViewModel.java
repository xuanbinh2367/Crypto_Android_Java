package example.txb.crypto_android.view_model;

import android.content.Context;


import example.txb.crypto_android.api.API;
import example.txb.crypto_android.model.SystemNotificationModel;

public class BinhRankViewModel extends BaseViewModel {
    private Context context;

    public BinhRankViewModel(Context context) {
        this.context = context;
    }

    public void getRank(int start, int end, OkCallback okCallback) {
        if (isLoading().getValue()) return;
        _isLoading.postValue(true);
        new Thread(() -> {
            getRankAPI(start, end, okCallback);
        }).start();
    }

    private void getRankAPI(int start, int end, OkCallback okCallback) {
        try {
            API.RequestParams params = new API.RequestParams();
            params.add("start",""+start);
            params.add("end",""+end);


            API.ResponseAPI response = API.get(context, "/profile/topChartUser", params);

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
