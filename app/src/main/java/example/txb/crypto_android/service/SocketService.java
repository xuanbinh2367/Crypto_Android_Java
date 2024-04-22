package example.txb.crypto_android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.StrictMode;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import example.txb.crypto_android.api.API;
import example.txb.crypto_android.model.socket.SocketServiceEventsModel;

public class SocketService extends Service {
    private final String SERVER_ADDRESS = API.SERVER_URL;
    private final int SERVER_POST = 8081;
    private Socket clientSocket=null;

    private SocketServiceEventsModel.SocketServiceEventManager socketServiceEventManager;

    private PrintWriter outputWriter;
    private InputStream inputStream;


    public void addEventListener(String eventName, String author, SocketServiceEventsModel.EventCallbackInterface callback){
        socketServiceEventManager.addEventListener(eventName,author,callback);
    }
    public void removeEventListener(String eventName, String author){
        socketServiceEventManager.removeEventListener(eventName,author);
    }
    public void removeEventListener(String author){
        socketServiceEventManager.removeEventListener(author);
    }

    public void send(String eventName, String data){
        try{
            String content = new Gson().toJson(new SocketServiceEventsModel.SocketRequestAndResponse(eventName,data));
            outputWriter.println(content);
        }catch (Exception e){};
    }

    @Override
    public void onCreate() {
        // Initialize your cursor or any other relevant data
        socketServiceEventManager = new SocketServiceEventsModel.SocketServiceEventManager();
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        try {
            clientSocket = new Socket(SERVER_ADDRESS, SERVER_POST);
            outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            inputStream = clientSocket.getInputStream();

            listenInput();

//            join personal room
            send(SocketServiceEventsModel.EventNames.Send.JoinPersonalRoom,API.getAuth(getApplicationContext()));

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    private void listenInput(){
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line=reader.readLine())!=null){
                    if(!line.isEmpty()){
                        handleReceive(line);
                    }
                }
            } catch (IOException e) {

//                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    outputWriter.close();
                    clientSocket.close();
                    System.out.println("server disconnected");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleReceive(String content){
        try{
            System.out.println("socket receive: "+content);
            SocketServiceEventsModel.SocketRequestAndResponse response = new Gson().fromJson(content,SocketServiceEventsModel.SocketRequestAndResponse.class);
            socketServiceEventManager.handleEvent(response.event,response.content);
        }catch (Exception e){}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    // Binder class to provide access to the service
    public class MyBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public void onDestroy() {
        try {
            //out personal room
            send(SocketServiceEventsModel.EventNames.Send.OutPersonalRoom,API.getAuth(getApplicationContext()));

            inputStream.close();
            outputWriter.close();
            clientSocket.close();
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}