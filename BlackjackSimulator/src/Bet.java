
public class Bet {
    private double amount;
    private boolean isDoubled;
    private boolean isSplit;
    private BetResult result;
    
    public enum BetResult {
        PENDING, WIN, LOSE, PUSH, BLACKJACK
    }
    
    public Bet(double amount) {
        this.amount = amount;
        this.isDoubled = false;
        this.isSplit = false;
        this.result = BetResult.PENDING;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void doubleDown() {
        if (isDoubled) {
            throw new IllegalStateException("Bet already doubled");
        }
        amount *= 2;
        isDoubled = true;
    }
    
    public boolean isDoubled() {
        return isDoubled;
    }
    
    public void setSplit(boolean split) {
        this.isSplit = split;
    }
    
    public boolean isSplit() {
        return isSplit;
    }
    
    public void setResult(BetResult result) {
        this.result = result;
    }
    
    public BetResult getResult() {
        return result;
    }
    
    public double getPayout() {
        switch (result) {
            case WIN:
                return amount * 2;
            case BLACKJACK:
                return amount * 2.5;
            case PUSH:
                return amount;
            case LOSE:		//w trickle down
            case PENDING:
            default:
                return 0;
        }
    }
}
