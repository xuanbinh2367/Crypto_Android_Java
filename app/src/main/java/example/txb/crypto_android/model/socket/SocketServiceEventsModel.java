package example.txb.crypto_android.model.socket;

import java.util.ArrayList;

public class SocketServiceEventsModel {
    public static class EventNames{
        public static class Send{
            public static String JoinPersonalRoom="join-personal-room";
            public static String OutPersonalRoom="out-personal-room";
        }
        public static class Receive{
            public static String CoinsPriceNow="coins-price-now";
            public static String AutoCloseTradingCommand="auto-close-trading-command";
        }
    }

    public static class SocketRequestAndResponse{
        public String event;
        public String content;

        public SocketRequestAndResponse(String event, String content) {
            this.event = event;
            this.content = content;
        }
    }

    public static class SocketServiceEventManager{
        private ArrayList<EventListener> eventListeners=new ArrayList<>();

        public void addEventListener(String eventName, String author, EventCallbackInterface callback){
            int ind = findIndex(eventName,author);
            if(ind==-1){
                eventListeners.add(new EventListener(eventName,author,callback));
            }
        }
        public void removeEventListener(String eventName, String author){
            int ind = findIndex(eventName,author);
            if(ind!=-1){
                eventListeners.remove(ind);
            }
        }
        public void removeEventListener(String author){
            int ind=0;
            while(ind!=-1){
                ind = findIndex(author);
                if(ind!=-1){
                    eventListeners.remove(ind);
                }
            }
        }

        public void handleEvent(String eventName, String data){
            for(int i=0;i<eventListeners.size();i++){
                if(eventListeners.get(i).eventName.equals(eventName)){
                    eventListeners.get(i).callback.handle(data);
                }
            }
        }

        private int findIndex(String eventName, String author){
            for(int i=eventListeners.size()-1;i>=0;i--){
                if(eventListeners.get(i).author.equals(author)&&eventListeners.get(i).eventName.equals(eventName)){
                    return i;
                }
            }
            return -1;
        }
        private int findIndex(String author){
            for(int i=eventListeners.size()-1;i>=0;i--){
                if(eventListeners.get(i).author.equals(author)){
                    return i;
                }
            }
            return -1;
        }

    }

    public static interface EventCallbackInterface{
        public void handle(String data);
    }

    private static class EventListener{
        public String eventName;
        public String author;
        public EventCallbackInterface callback;

        public EventListener(String eventName, String author, EventCallbackInterface callback) {
            this.eventName = eventName;
            this.author = author;
            this.callback = callback;
        }
    }

}
