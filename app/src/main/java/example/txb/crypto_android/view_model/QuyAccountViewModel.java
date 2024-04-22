package example.txb.crypto_android.view_model;

import android.content.Context;


import example.txb.crypto_android.api.API;

public class QuyAccountViewModel extends BaseViewModel{
    private Context context;

    public QuyAccountViewModel(Context context){
        this.context = context;
    }

    public void checkAuth(OkCallback okCallback){
//        if(isLoading().getValue())return;
//        _isLoading.postValue(true);
        new Thread(()->{
            checkAuthAPI(okCallback);
        }).start();
    }

    private void checkAuthAPI(OkCallback okCallback){
        try{
            API.RequestParams params = new API.RequestParams();
            params.add("userId","123");

            API.ResponseAPI response = API.get(context,"/account/checkAuth",params);
            if(response.status== API.ResponseAPI.Status.Fail){
                okCallback.handle("false");
//                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,response.error));
            }else{
                okCallback.handle("true");
//                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"ok"));
            }
        }catch(Exception e){
//            throw new RuntimeException(e);
            okCallback.handle("false");
            System.out.println(e);
//            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
//            _isLoading.postValue(false);
        }
    }


}
