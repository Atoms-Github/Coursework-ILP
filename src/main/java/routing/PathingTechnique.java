package routing;

public enum PathingTechnique {
    MAX_PRICE_PER_MOVE, // Go for 'high value and short' orders first.
    MAX_ORDER_COUNT,// Go for closest order, in an attempt to complete them all.
}
