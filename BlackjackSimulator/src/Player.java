import java.util.*;
public class Player {
    private double money;
    private List<Hand> hands;
    private List<Bet> bets;
    private int currentHandIndex;
    
    public Player(double startingMoney) {
        this.money = startingMoney;
        this.hands = new ArrayList<>();
        this.bets = new ArrayList<>();
        this.currentHandIndex = 0;
    }
    
    public void placeBet(double amount) {
        if (amount > money) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        money -= amount;
        bets.add(new Bet(amount));
        hands.add(new Hand(false));
    }
    
    //when called pass in currentHandIndex
    public void doubleDown(int handIndex) {
        if (handIndex >= bets.size()) { // out of bounds check
            throw new IllegalArgumentException("Invalid hand index");
        }
        
        Hand hand = hands.get(handIndex);
        if (!hand.canDouble()) {
            throw new IllegalArgumentException("Cannot double down - can only double on first two cards");
        }
        
        Bet bet = bets.get(handIndex);
        double additionalAmount = bet.getAmount();
        
        if (additionalAmount > money) {
            throw new IllegalArgumentException("Insufficient funds for double down");
        }
        
        money -= additionalAmount;
        bet.doubleDown();
    }
    
  //when called pass in currentHandIndex
    public void split(int handIndex) {
        if (handIndex >= hands.size() || !hands.get(handIndex).canSplit()) {
            throw new IllegalArgumentException("Cannot split this hand");
        }
        
        //gets original hand and bet
        Hand originalHand = hands.get(handIndex);
        Bet originalBet = bets.get(handIndex);
        
        if (originalBet.getAmount() > money) {
            throw new IllegalArgumentException("Insufficient funds for split");
        }
        
        money -= originalBet.getAmount();
        
        //creating a new hand and bet
        Hand newHand = new Hand(false);
        Bet newBet = new Bet(originalBet.getAmount());
        newBet.setSplit(true);
        originalBet.setSplit(true);
        
        //moves the 2nd card to the new hand
        List<Card> cards = originalHand.getCards();
        newHand.addCard(cards.get(1));
        
        //adds back first card to the original hand
        originalHand.clear();
        originalHand.addCard(cards.get(0));
        
        //add the new hand and bet
        hands.add(handIndex + 1, newHand);
        bets.add(handIndex + 1, newBet);
    }
    
    public void addCardToHand(Card card, int handIndex) {
        if (handIndex < hands.size()) {
            hands.get(handIndex).addCard(card);
        }
    }
    
    public Hand getHand(int index) {
        return index < hands.size() ? hands.get(index) : null;
    }
    
    public List<Hand> getHands() {
        return new ArrayList<>(hands);
    }
    
    public Bet getBet(int index) {
        return index < bets.size() ? bets.get(index) : null;
    }
    
    public List<Bet> getBets() {
        return new ArrayList<>(bets);
    }
    
    public int getNumHands() {
        return hands.size();
    }
    
    public double getMoney() {
        return money;
    }
    
    public void addMoney(double amount) {
        money += amount;
    }
    
    public void clearHands() {
        hands.clear();
        bets.clear();
        currentHandIndex = 0;
    }
    
    public int getCurrentHandIndex() {
        return currentHandIndex;
    }
    
    public void setCurrentHandIndex(int index) {
        this.currentHandIndex = index;
    }
    
    public boolean canAfford(double amount) {
        return money >= amount;
    }

    public void placeInsurance(double insuranceAmount, int index) {
        
        Bet bet = bets.get(index);
        if (bet == null) {
            throw new IllegalArgumentException("No bet found for hand index");
        }

        bet.placeInsuranceBet(insuranceAmount);

    }
}
