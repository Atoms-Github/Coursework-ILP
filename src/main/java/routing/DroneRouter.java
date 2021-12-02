package routing;

import world.DroneArea;
import drone.MoveList;
import world.MapPoint;
import orders.Cafe;
import orders.Order;
import inputOutput.IOMenus;

import java.util.ArrayList;
import java.util.List;

public class DroneRouter {
    public static final double SHORT_MOVE_LENGTH = 0.00015;
    public static final double UNLUCKY_ZIG_ZAG_MULTIPLIER = 1.15; // See report for where this comes from.
    private final DroneArea area;
    public final IOMenus menus;
    public final ArrayList<Cafe> cafes; // I know calling this final doesn't make the interior final, but I'll use the idea that it does.

    public DroneRouter(DroneArea area, IOMenus menus, ArrayList<Cafe> cafes) {
        this.area = area;
        this.menus = menus;
        this.cafes = cafes;
    }

    public DroneRouteResults calculateDroneMoves(MapPoint start, List<Order> ordersList, PathingTechnique technique){
        ArrayList<Order> ordersToGo = new ArrayList<>(ordersList);
        CafeTracker tracker = new CafeTracker(menus, cafes, ordersToGo);

        DroneRouteResults results = new DroneRouteResults(1500, start);
        while(true){
            // For each move, we want to calculate which order to take next.
            Order bestOrder = switch (technique) {
                case MAX_PRICE_PER_MOVE -> calcBestNextOrderPricePerMove(results.currentLocation, ordersToGo, tracker, results.remainingShortMoves);
                case MAX_ORDER_COUNT -> calcBestNextOrderMaxOrders(results.currentLocation, ordersToGo, tracker, results.remainingShortMoves);
            };

            if (bestOrder != null){
                ordersToGo.remove(bestOrder);
                tracker.completeOrder(bestOrder);
                MoveList movesForBestOrder = bestOrder.getDroneMovesForOrder(results.currentLocation, area);
                results.addOrder(bestOrder, movesForBestOrder);
            }else{
                // There is no more good order to do. Just go back to appleton.
                MoveList routeBackToAppleton = area.pathfind(results.currentLocation, MapPoint.APPLETON_TOWER);
                results.addMove("noOrders", routeBackToAppleton);
                return results;
            }
        }
    }

    private Order calcBestNextOrderPricePerMove(MapPoint start, List<Order> ordersList, CafeTracker shops, int maxMoves){
        // To do this, we'll calculate the price per move of all potential moves,
        // including an extra move back to the nearest shop that still has an order to be completed at.
        double bestPriceToLength = Double.MIN_VALUE;
        Order bestOrder = null;
        for (Order potentialOrder : ordersList){
            MoveList routeToCompleteOrder = potentialOrder.getDroneMovesForOrder(start, area);
            if (routeToCompleteOrder == null){
                continue; // Can't route this order's path.
            }
            MapPoint closestActiveShopRemaining = shops.getClosestShopWithItemsLeft(routeToCompleteOrder.getLastLocation());
            if (closestActiveShopRemaining == null){
                // If no more shops with orders left, this is the last order, thus the best.
                return potentialOrder;
            }
            boolean makeItToShop = routeToCompleteOrder.addPathfoundDestination(closestActiveShopRemaining, area);
            if (!makeItToShop){
                continue; // Can't make it to any more shops. This order is bad.
            }
            double routeLength = routeToCompleteOrder.getTotalMoveLength();
            int routePrice = potentialOrder.getTotalPrice();
            double pricePerLength = (double) routePrice / routeLength;

            // Now we've calculated price per length, we want to work out if we can make it back to appleton if we do this order.
            boolean makeItToAppleton = routeToCompleteOrder.addPathfoundDestination(MapPoint.APPLETON_TOWER, area);
            if (!makeItToAppleton){
                continue; // Can't make it home. This order is bad.
            }
            int totalShortMovesEstimate = routeToCompleteOrder.getShortMoveSafeEstimate();
            // Round up. See report for unlucky zig zag modifier.
            int totalShortMovesIfUnlucky = (int)((double)totalShortMovesEstimate * UNLUCKY_ZIG_ZAG_MULTIPLIER) + 1;

            // Don't do this order if we're not going to make it back to appleton afterwards.
            if (totalShortMovesIfUnlucky > maxMoves){
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
    private Order calcBestNextOrderMaxOrders(MapPoint start, List<Order> ordersList, CafeTracker shops, int maxMoves){
        double distanceToClosestOrder = Double.MAX_VALUE;
        Order bestOrder = null;
        orderLoop: for (Order potentialOrder : ordersList){
            double distToClosestShop = Double.MAX_VALUE;
            for (MapPoint point : potentialOrder.getShopLocations()){
                MoveList pathToPoint = area.pathfind(start, point);
                if (pathToPoint == null){
                    continue orderLoop; // This order can't be pathed to.
                }
                distToClosestShop = Math.min(distToClosestShop, pathToPoint.getTotalMoveLength());
            }
            MoveList routeToCompleteOrder = potentialOrder.getDroneMovesForOrder(start, area);
            if (routeToCompleteOrder == null){
                continue; // Can't do this move's order.
            }

            boolean makeItHome = routeToCompleteOrder.addPathfoundDestination(MapPoint.APPLETON_TOWER, area);
            if (!makeItHome){
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











