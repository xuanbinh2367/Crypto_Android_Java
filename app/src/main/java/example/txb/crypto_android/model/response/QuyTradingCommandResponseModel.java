package example.txb.crypto_android.model.response;


public class QuyTradingCommandResponseModel {
    public static class Created {
        public String newId;

        public Created(String newId) {
            this.newId = newId;
        }
    }

    public static class AutoClose {
        public String id;

        public AutoClose(String id) {
            this.id = id;
        }
    }

    public static class TradingCommandDetails {
        public String id;
        public String buyOrSell;
        public float coinNumber = 0;
        public float moneyNumber = 0;
        public int leverage = 1;
        public float openPrice = 0;
        public long openTime;
        public float closePrice = 0;
        public long closeTime;
        public boolean enableTpSl = false;
        public float takeProfit = 0;
        public float stopLoss = 0;
        public String coinId;
        public float finalProfit = 0;
        public boolean isOpen = true;
        public float commission = 0;

        public TradingCommandDetails(String id, String buyOrSell, float coinNumber, float moneyNumber, int leverage, float openPrice, long openTime, float closePrice, long closeTime, boolean enableTpSl, float takeProfit, float stopLoss, String coinId, float finalProfit, boolean isOpen, float commission) {
            this.id = id;
            this.buyOrSell = buyOrSell;
            this.coinNumber = coinNumber;
            this.moneyNumber = moneyNumber;
            this.leverage = leverage;
            this.openPrice = openPrice;
            this.openTime = openTime;
            this.closePrice = closePrice;
            this.closeTime = closeTime;
            this.enableTpSl = enableTpSl;
            this.takeProfit = takeProfit;
            this.stopLoss = stopLoss;
            this.coinId = coinId;
            this.finalProfit = finalProfit;
            this.isOpen = isOpen;
            this.commission = commission;
        }
    }


}
