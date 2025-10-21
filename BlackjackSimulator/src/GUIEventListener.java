public interface GUIEventListener {
    /**
     * Called by GUI after the initial deal animation completes
     */
    void onInitialDealComplete();
    
    /**
     * Called by GUI after a hit animation completes
     * @param handIndex The hand that was hit
     */
    void onHitAnimationComplete(int handIndex);
    
    /**
     * Called by GUI after a double down animation completes
     * @param handIndex The hand that was doubled
     */
    void onDoubleAnimationComplete(int handIndex);
    
    /**
     * Called by GUI after dealer reveal animation completes
     */
    void onDealerRevealComplete();
    
    /**
     * Called by GUI after each dealer hit animation completes
     */
    void onDealerHitAnimationComplete();

    /**
     * Called by GUI after UI initialization is complete
     */
    // void onUiInitialized();
}