package routing;

import inputOutput.output.IODroneAction;
import world.DroneArea;
import drone.MoveList;
import world.MapPoint;
import orders.Cafe;
import orders.Order;

import java.util.ArrayList;
import java.util.List;

public class DroneRouter {
    /**
     * How far the drone can go in 1 move.
     */
    public static final double SHORT_MOVE_LENGTH = 0.00015;
    /**
     * How much slower it is to move in 10 degree lines, instead of any line. Worst case. See report more for details.
     */
    public static final double UNLUCKY_WOBBLE_MULTIPLIER = 1.1;
    /**
     * How many drone actions the drone can do before it runs out of charge.
     */
    public static final int DRONE_MAX_MOVES = 1500;

    /**
     * The map the drone is to move around.
     */
    private final DroneArea area;
    /**
     * All the cafes on the map.
     */
    private final ArrayList<Cafe> cafes;

    public DroneRouter(DroneArea area, ArrayList<Cafe> cafes) {
        this.area = area;
        this.cafes = cafes;
    }

    /**
     * Runs the algorithm, calculating the best route the drone can take.
     * @param droneLaunchPoint Where the drone starts, e.g. appleton tower.
     * @param ordersList All the orders the drone should do.
     * @param technique What technique to use to find the next best order.
     * @return The best route for the drone.
     */
    public DroneRouteResults calculateDroneMoves(MapPoint droneLaunchPoint, MapPoint end, List<Order> ordersList, PathingTechnique technique){
        // Clone the orders list, so we can delete the orders we've completed from it.
        ArrayList<Order> ordersToGo = new ArrayList<>(ordersList);
        CafeTracker tracker = new CafeTracker(cafes, ordersToGo);

        DroneRouteResults results = new DroneRouteResults(DRONE_MAX_MOVES, droneLaunchPoint);
        // Loop until we've found our best route.
        while(true){
            // For each move, we want to calculate which order to take next, using the appropriate technique.
            Order bestOrder = switch (technique) {
                case MAX_PRICE_PER_MOVE -> calcBestNextOrderPricePerMove(results.currentLocation, ordersToGo, tracker, results.remainingShortMoves);
                case MAX_ORDER_COUNT -> calcBestNextOrderMaxOrders(results.currentLocation, ordersToGo, results.remainingShortMoves);
            };

            // If there is an order we can take. (E.g. can't if won't make it back to appleton in time).
            if (bestOrder != null){
                // Take this order:
                ordersToGo.remove(bestOrder);
                tracker.completeOrder(bestOrder);
                // Work out what moves are required for this order, and add them to the total.
                MoveList movesForBestOrder = bestOrder.getDroneMovesForOrder(results.currentLocation, area);
                results.addOrder(bestOrder, movesForBestOrder);
            }else{ // There is no more good orders to do. Just go back to appleton.
                // Fly back to appleton.
                MoveList routeBackToAppleton = area.pathfind(results.currentLocation, droneLaunchPoint);
                results.addMove(IODroneAction.NO_ORDER_STRING, routeBackToAppleton);
                return results;
            }
        }
    }

    private Order calcBestNextOrderPricePerMove(MapPoint start, List<Order> ordersList, CafeTracker shops, int maxMoves){
        double bestPriceToLength = Double.MIN_VALUE;
        Order bestOrder = null;
        // Try all orders, to see what's the best one's price per move.
        for (Order potentialOrder : ordersList){
            MoveList routeToCompleteOrder = potentialOrder.getDroneMovesForOrder(start, area);
            if (routeToCompleteOrder == null){
                continue; // Can't route to this order. Don't take this order.
            }
            MapPoint closestActiveShopRemaining = shops.getClosestShopWithItemsLeft(routeToCompleteOrder.getLastLocation(), cafes);
            if (closestActiveShopRemaining == null){
                // If no more shops with orders left, this is the last order, thus the best.
                return potentialOrder;
            }
            boolean canMakeItToShop = routeToCompleteOrder.addPathfoundDestination(closestActiveShopRemaining, area);
            if (!canMakeItToShop){
                continue; // Can't make it to any more shops. This order is bad.
            }
            double routeLength = routeToCompleteOrder.getTotalLength();
            int routePrice = potentialOrder.getTotalPrice();
            double pricePerLength = (double) routePrice / routeLength;

            // Now we've calculated price per length, we want to work out if we can make it back to appleton if we do this order.
            boolean makeItToAppleton = routeToCompleteOrder.addPathfoundDestination(MapPoint.APPLETON_TOWER, area);
            if (!makeItToAppleton){
                continue; // Can't make it home. This order is bad.
            }
            int totalShortMovesEstimate = routeToCompleteOrder.getShortMoveSafeEstimate();

            // Don't do this order if we're not going to make it back to appleton afterwards.
            if (totalShortMovesEstimate > maxMoves){
                continue;
            }
            // Standard 'find max' algorithm part.
            if (pricePerLength > bestPriceToLength){
                bestPriceToLength = pricePerLength;
                bestOrder = potentialOrder;
            }
        }
        return bestOrder;
    }
    private Order calcBestNextOrderMaxOrders(MapPoint start, List<Order> ordersList, int maxMoves){
        double distanceToClosestOrder = Double.MAX_VALUE;
        Order bestOrder = null;
        orderLoop: for (Order potentialOrder : ordersList){
            // 'Find Max' algorithm to see where this order's closest shop is:
            double distToClosestShop = Double.MAX_VALUE;
            for (MapPoint point : potentialOrder.getShopLocations()){
                MoveList pathToPoint = area.pathfind(start, point);
                if (pathToPoint == null){
                    continue orderLoop; // This order can't be pathed to.
                }
                distToClosestShop = Math.min(distToClosestShop, pathToPoint.getTotalLength());
            }
            // Work out how to do this order:
            MoveList routeToCompleteOrder = potentialOrder.getDroneMovesForOrder(start, area);
            if (routeToCompleteOrder == null){
                continue; // Can't route this order. Don't take it.
            }
            boolean canMakeItHome = routeToCompleteOrder.addPathfoundDestination(MapPoint.APPLETON_TOWER, area);
            if (!canMakeItHome){
                continue; // Can't make it home. This order is bad.
            }
            int totalShortMovesEstimate = routeToCompleteOrder.getShortMoveSafeEstimate();

            // Don't do this order if we're not going to make it back to appleton afterwards.
            if (totalShortMovesEstimate > maxMoves){
                continue;
            }
            // Standard 'find min' algorithm part.
            if (distToClosestShop < distanceToClosestOrder){
                distanceToClosestOrder = distToClosestShop;
                bestOrder = potentialOrder;
            }
        }
        return bestOrder;
    }
}











