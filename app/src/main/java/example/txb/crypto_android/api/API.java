package example.txb.crypto_android.api;


import android.content.Context;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import example.txb.crypto_android.core.LocalData;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class API {
    private static String AUTH_LOCAL_DATA_NAME = "auth";

    public static class ResponseAPI{
        public static enum Status {
            Success,Fail
        }
        public int code;
        public Status status;
        public String data;
        public String error;

        public ResponseAPI(int code, Status status, String data, String error) {
            this.code = code;
            this.status = status;
            this.data = data;
            this.error = error;
        }
        public ResponseAPI(ResponseAPICrude crude) {
            this.code = crude.code;
            if(crude.status.equals("success")){
                this.status = Status.Success;
            }else{
                this.status = Status.Fail;
            }
            this.data = crude.data;
            this.error = crude.error;
        }
    }
    public static class ResponseAPICrude{
        public int code;
        public String status;
        public String data;
        public String error;

        public ResponseAPICrude(int code, String status, String data, String error) {
            this.code = code;
            this.status = status;
            this.data = data;
            this.error = error;
        }
    }


    public static String SERVER_URL = "10.0.2.2";

    public static String SERVER_URL_AND_PORT= "http://"+SERVER_URL+":8080";


    public static String getAuth (Context context){
        LocalData localData = new LocalData(context);
        return localData.getString(AUTH_LOCAL_DATA_NAME);
    }
    public static void setAuth(Context context, String auth){
        if (auth != null && !auth.isEmpty()) {
            LocalData localData = new LocalData(context);
            localData.setString(AUTH_LOCAL_DATA_NAME, auth);
        }
    }

//    public static String getMimeType (File file){
//        if (file == null) return null;
//        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.);
//    }

    public static ResponseAPI post(Context context, String path, RequestBody requestBody){
        try {
            OkHttpClient client = new OkHttpClient();
//            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());
            Request request = new Request.Builder()
                    .url(SERVER_URL_AND_PORT + path)
                    .addHeader("auth",getAuth(context))
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new Exception();
            }

//            System.out.println(response.body().toString());

            ResponseAPICrude responseAPICrude = new Gson().fromJson(response.body().string(),ResponseAPICrude.class);
            return new ResponseAPI(responseAPICrude);

        } catch (Exception err){
            System.out.println(err.toString());
            return new ResponseAPI(1, ResponseAPI.Status.Fail, null, "system_error");
        }
    }
    public static class RequestParams{
        private ArrayList<RequestParam> params = new ArrayList<>();
        public void add(String key, String value){
            if(key!=null&&!key.isEmpty()&&!isExitKey(key)){
                params.add(new RequestParam(key,value));
            }
        }
        public void add(RequestParam param){
            if(param.key!=null&&!param.key.isEmpty()&&!isExitKey(param.key)){
                params.add(param);
            }

        }
        public RequestParam get(int ind){
            return params.get(ind);
        }
        private boolean isExitKey(String key){
            for (int i=0;i<params.size();i++){
                if(params.get(i).key.equals(key))return true;
            }
            return false;
        }
        public int size(){
            return params.size();
        }
        public static class RequestParam{
            public String key;
            public String value;

            public RequestParam(String key, String value) {
                this.key = key;
                this.value = value;
            }
        }
    }
    public static ResponseAPI get(Context context, String path, RequestParams requestParams){
        try {
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL_AND_PORT + path).newBuilder();
            if(requestParams!=null&&requestParams.size()!=0){
                for(int i=0;i<requestParams.size();i++){
                    urlBuilder.addQueryParameter(requestParams.get(i).key,requestParams.get(i).value);
                }
            }
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                .url(url)
                .addHeader("auth",getAuth(context))
                .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new Exception();
            }

//            System.out.println(response.body().string());

            ResponseAPICrude responseAPICrude = new Gson().fromJson(response.body().string(),ResponseAPICrude.class);
            return new ResponseAPI(responseAPICrude);

        } catch (Exception err){
//            throw new RuntimeException(err);
            System.out.println(err);
            return new ResponseAPI(1, ResponseAPI.Status.Fail, null, "system_error");
        }
    }
}