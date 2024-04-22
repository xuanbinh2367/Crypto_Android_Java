package example.txb.crypto_android.model.response;

import java.util.ArrayList;

public class BanTradingCommandModel {
    public static class OpenTradingCommandItem{
        public String id;
        public String buyOrSell;
        public float coinNumber = 0;
        public float moneyNumber = 0;
        public int leverage = 1;
        public float openPrice=0;
        public long openTime;
        public boolean enableTpSl = false;
        public String coinId;
        public float finalProfit=0;

        public OpenTradingCommandItem(String id, String buyOrSell, float coinNumber, float moneyNumber, int leverage, float openPrice, long openTime, boolean enableTpSl, String coinId, float finalProfit) {
            this.id = id;
            this.buyOrSell = buyOrSell;
            this.coinNumber = coinNumber;
            this.moneyNumber = moneyNumber;
            this.leverage = leverage;
            this.openPrice = openPrice;
            this.openTime = openTime;
            this.enableTpSl = enableTpSl;
            this.coinId = coinId;
            this.finalProfit = finalProfit;
        }
    }
    public static class OpenTradingCommandList{
        public ArrayList<OpenTradingCommandItem> items;

        public OpenTradingCommandList(ArrayList<OpenTradingCommandItem> items) {
            this.items = items;
        }
    }
    public static class CloseTradingCommandItem{
        public String id;
        public String buyOrSell;
        public float moneyNumber = 0;
        public int leverage = 1;
        public float openPrice=0;
        public long openTime;
        public long closeTime;
        public String coinId;
        public float finalProfit=0;

        public CloseTradingCommandItem(String id, String buyOrSell, float moneyNumber, int leverage, float openPrice, long openTime, long closeTime, String coinId, float finalProfit) {
            this.id = id;
            this.buyOrSell = buyOrSell;
            this.moneyNumber = moneyNumber;
            this.leverage = leverage;
            this.openPrice = openPrice;
            this.openTime = openTime;
            this.closeTime = closeTime;
            this.coinId = coinId;
            this.finalProfit = finalProfit;
        }
    }
    public static class CloseTradingCommandList{
        public ArrayList<CloseTradingCommandItem> items;

        public CloseTradingCommandList(ArrayList<CloseTradingCommandItem> items) {
            this.items = items;
        }
    }
}
