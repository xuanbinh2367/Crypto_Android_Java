package example.txb.crypto_android.view_model;

import android.content.Context;


import example.txb.crypto_android.api.API;
import example.txb.crypto_android.model.SystemNotificationModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BanCommandViewModel extends BaseViewModel{
    private Context context;

    public BanCommandViewModel(Context context){
        this.context = context;
    }

    public void getList(String userId, boolean isOpen, OkCallback okCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            getMiniInfoAPI(userId, isOpen, okCallback);
        }).start();
    }

    private void getMiniInfoAPI(String userId, boolean isOpen, OkCallback okCallback){
        try{
            API.RequestParams params = new API.RequestParams();
            params.add("userId",userId);
            params.add("isOpen",""+isOpen);


            API.ResponseAPI response = API.get(context,"/tradingCommand/getList",params);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,response.error));
            }else{
                okCallback.handle(response.data);
//                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"ok"));
            }
        }catch(Exception e){
//            throw new RuntimeException(e);
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void getInterestedCoins(OkCallback okCallback){
//        if(isLoading().getValue())return;
//        _isLoading.postValue(true);
        new Thread(()->{
            getInterestedCoinsAPI(okCallback);
        }).start();
    }

    private void getInterestedCoinsAPI(OkCallback okCallback){
        try{
            API.RequestParams params = new API.RequestParams();

            API.ResponseAPI response = API.get(context,"/profile/interestedCoins",params);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,response.error));
            }else{
                okCallback.handle(response.data);
//                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"ok"));
            }
        }catch(Exception e){
//            throw new RuntimeException(e);
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
//            _isLoading.postValue(false);
        }
    }

    public void toggleInterestedCoin(String coinId, OkCallback okCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            toggleInterestedCoinAPI(coinId, okCallback);
        }).start();
    }

    private void toggleInterestedCoinAPI(String coinId, OkCallback okCallback){
        try{
            RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("coinId", coinId)
                .build();


            API.ResponseAPI response = API.post(context,"/profile/toggleInterestedCoin",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,response.error));
            }else{
                okCallback.handle(response.data);
//                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"ok"));
            }
        }catch(Exception e){
//            throw new RuntimeException(e);
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }
}
