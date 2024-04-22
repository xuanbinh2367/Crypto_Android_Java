package example.txb.crypto_android.view_model;

import android.content.Context;

import example.txb.crypto_android.api.API;
import example.txb.crypto_android.model.SystemNotificationModel;

public class QuyCoinViewModel extends BaseViewModel{
    private Context context;

    public QuyCoinViewModel(Context context){
        this.context = context;
    }

    public void loadChart(String type, String coidId, String interval, String start, String end , BaseViewModel.OkCallback okCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            loadChartAPI(type,coidId,interval,start,end,okCallback);
        }).start();
    }

    private void loadChartAPI(String type, String coidId, String interval, String start, String end ,BaseViewModel.OkCallback okCallback){
        try{
            System.out.println("ate void loadChartAPI(String type, String coidId,");
            API.RequestParams params = new API.RequestParams();
            params.add("coinId",coidId);
            params.add("interval",interval);
            params.add("start",start);
            params.add("end",end);

            API.ResponseAPI response = API.get(context,"/coins/chart/"+type,params);
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

    public void getAll(BaseViewModel.OkCallback okCallback){
//        if(isLoading().getValue())return;
//        _isLoading.postValue(true);
        new Thread(()->{
            getAllAPI(okCallback);
        }).start();
    }

    private void getAllAPI(BaseViewModel.OkCallback okCallback){
        try{
            API.RequestParams params = new API.RequestParams();
            params.add("userId","123");

            API.ResponseAPI response = API.get(context,"/coins/getAll",params);
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


}
