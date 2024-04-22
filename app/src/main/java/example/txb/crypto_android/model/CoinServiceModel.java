package example.txb.crypto_android.model;

import java.util.ArrayList;

public class CoinServiceModel {

    public class CoinNow {
        public String id;
        public int rank;
        public String symbol;
        public String icon;
        public String name;
        public float volumeUsd24Hr;
        public float priceUsd;
        public float changePercent24Hr;
        public float vwap24Hr;

        public CoinNow(String id, int rank, String symbol, String icon, String name, float volumeUsd24Hr, float priceUsd, float changePercent24Hr, float vwap24Hr) {
            this.id = id;
            this.rank = rank;
            this.symbol = symbol;
            this.icon = icon;
            this.name = name;
            this.volumeUsd24Hr = volumeUsd24Hr;
            this.priceUsd = priceUsd;
            this.changePercent24Hr = changePercent24Hr;
            this.vwap24Hr = vwap24Hr;
        }
    }
    public static class CoinsNow{
        public ArrayList<CoinNow> data;
        public Long timestamp;

        public CoinsNow(ArrayList<CoinNow> data, Long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
        public CoinsNow() {
            this.data = new ArrayList<>();
            this.timestamp = 0L;
        }
    }
    public static class CoinServiceListenerManager{
        private ArrayList<Listener> listeners=new ArrayList<>();

        public void addListener(ArrayList<String> coinsId, String mainAuthor, String subAuthor, EventCallbackInterface callback){
            int ind = findIndex(mainAuthor,subAuthor);
            if(ind==-1){
                listeners.add(new Listener(coinsId,mainAuthor,subAuthor,callback));
            }else{
                listeners.get(ind).coinsId = coinsId;
            }
        }
        public void removeListener(String mainAuthor,String subAuthor){
            int ind = findIndex(mainAuthor,subAuthor);
            if(ind!=-1){
                listeners.remove(ind);
            }
        }
        public void removeListener(String mainAuthor){
            for (int i = listeners.size()-1; i >= 0; i--) {
                if(listeners.get(i).mainAuthor.equals(mainAuthor)){
                    listeners.remove(i);
                }
            }
        }

        public void handleEvent(ArrayList<CoinNow> coinNows){
            for(int i=0;i<listeners.size();i++){
                ArrayList<CoinNow> sendCoins = new ArrayList<>();
                Listener listener = listeners.get(i);
                for (int j = 0; j < listener.coinsId.size(); j++) {
                    int ind = findCoinIndex(coinNows,listener.coinsId.get(j));
                    if(ind!=-1){
                        sendCoins.add(coinNows.get(ind));
                    }
                }
                if(sendCoins.size()!=0){
                    listener.callback.handle(sendCoins);
                }
            }
        }

        private int findCoinIndex(ArrayList<CoinNow> coins, String coinId){
            for(int i=coins.size()-1;i>=0;i--){
                if(coins.get(i).id.equals(coinId)){
                    return i;
                }
            }
            return -1;
        }
        private int findIndex(String mainAuthor, String subAuthor){
            for(int i=listeners.size()-1;i>=0;i--){
                if(listeners.get(i).mainAuthor.equals(mainAuthor)&&listeners.get(i).subAuthor.equals(subAuthor)){
                    return i;
                }
            }
            return -1;
        }
    }

    public static interface EventCallbackInterface{
        public void handle(ArrayList<CoinNow> coins);
    }

    private static class Listener{
        public ArrayList<String> coinsId;
        public String mainAuthor, subAuthor;
        public EventCallbackInterface callback;

        public Listener(ArrayList<String> coinsId, String mainAuthor, String subAuthor, EventCallbackInterface callback) {
            this.coinsId = coinsId;
            this.mainAuthor = mainAuthor;
            this.subAuthor = subAuthor;
            this.callback = callback;
        }
    }

}
