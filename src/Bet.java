
public class Bet {
    private double amount;
    private boolean isDoubled;
    private boolean isSplit;
    private BetResult result;
    private double insuranceBet;
    private double pairBet;
    private double twentyOnePlusThreeBet;

    public enum BetResult {
        PENDING, WIN, LOSE, PUSH, BLACKJACK
    }
    
    public Bet(double amount) {
        this.amount = amount;
        this.insuranceBet = 0;
        this.pairBet = 0;
        this.twentyOnePlusThreeBet = 0;
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
                return (amount * 2);
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

    public void placeInsuranceBet(double insuranceAmount) {
        if (insuranceAmount > amount / 2) {
            throw new IllegalArgumentException("Insurance bet cannot exceed half of the original bet");
        }
        this.insuranceBet = insuranceAmount;
    }

    public double getInsuranceBet() {
        return insuranceBet * 2;
    }

    public boolean isInsurancePlaced() {
        return insuranceBet > 0;
    }

    public void placePairBet(double pairAmount) {
        this.pairBet = pairAmount;
    }

    public boolean isPairBetPlaced() {
        return pairBet > 0;
    }

    public double getCurrentPairBet() {
        return pairBet;
    }

    public void placeTwentyOnePlusThreeBet(double amount) {
        this.twentyOnePlusThreeBet = amount;
    }

    public boolean isTwentyOnePlusThreeBetPlaced() {
        return twentyOnePlusThreeBet > 0;
    }

    public double getCurrentTwentyOnePlusThreeBet() {
        return twentyOnePlusThreeBet;
    }
}
