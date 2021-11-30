package routing;

import dataDownload.CafeMenus;
import uk.ac.ed.inf.DroneUtils;
import uk.ac.ed.inf.MapPoint;

import java.util.ArrayList;
import java.util.List;

public class DroneRouter {
    private final DroneArea area;
    public final CafeMenus menus;
    public final ArrayList<ProcessedCafe> cafes; // I know calling this final doesn't make the interior final, but I'll use the idea that it does.

    public DroneRouter(DroneArea area, CafeMenus menus, ArrayList<ProcessedCafe> cafes) {
        this.area = area;
        this.menus = menus;
        this.cafes = cafes;
    }

    public DroneRouteResults calculateDroneMoves(MapPoint start, List<ProcessedOrder> ordersList, PathingTechnique technique){
        ArrayList<ProcessedOrder> ordersToGo = new ArrayList<>(ordersList);
        CafeTracker tracker = new CafeTracker(menus, cafes, ordersToGo);

        DroneRouteResults results = new DroneRouteResults(1500, start);
        while(true){
            // For each move, we want to calculate which order to take next.
            ProcessedOrder bestOrder = switch (technique) {
                case MAX_PRICE_PER_MOVE -> calcBestNextOrderPricePerMove(results.currentLocation, ordersToGo, tracker, results.remainingShortMoves);
                case MAX_ORDER_COUNT -> calcBestNextOrderMaxOrders(results.currentLocation, ordersToGo, tracker, results.remainingShortMoves);
            };

            if (bestOrder != null){
                ordersToGo.remove(bestOrder);
                DroneMoveList movesForBestOrder = bestOrder.getDroneMovesForOrder(results.currentLocation, area);
                results.addOrder(bestOrder, movesForBestOrder);
            }else{
                // There is no more good order to do. Just go back to appleton.
                DroneMoveList routeBackToAppleton = area.pathfind(results.currentLocation, MapPoint.APPLETON_TOWER);
                results.addMove(null, routeBackToAppleton); // TODO: Refact.
                return results;
            }
        }
    }

    private ProcessedOrder calcBestNextOrderPricePerMove(MapPoint start, List<ProcessedOrder> ordersList, CafeTracker shops, int maxMoves){
        // To do this, we'll calculate the price per move of all potential moves,
        // including an extra move back to the nearest shop that still has an order to be completed at.
        double bestPriceToLength = Double.MIN_VALUE;
        ProcessedOrder bestOrder = null;
        for (ProcessedOrder potentialOrder : ordersList){
            DroneMoveList routeToCompleteOrder = potentialOrder.getDroneMovesForOrder(start, area);
            MapPoint closestActiveShopRemaining = shops.getClosestShopWithItemsLeft(routeToCompleteOrder.getLastLocation());
            if (closestActiveShopRemaining == null){
                // If no more shops with orders left, this is the last order, thus the best.
                return potentialOrder;
            }
            routeToCompleteOrder.addRoutedDestination(closestActiveShopRemaining, area);
            double routeLength = routeToCompleteOrder.totalMoveLength();
            int routePrice = potentialOrder.getTotalPrice();
            double pricePerLength = (double) routePrice / routeLength;

            // Now we've calculated price per length, we want to work out if we can make it back to appleton if we do this order.
            routeToCompleteOrder.addRoutedDestination(MapPoint.APPLETON_TOWER, area);
            int totalShortMovesEstimate = routeToCompleteOrder.shortMoveSafeEstimate();
            // Round up. See report for unlucky zig zag modifier.
            int totalShortMovesIfUnlucky = (int)((double)totalShortMovesEstimate * DroneUtils.UNLUCKY_ZIG_ZAG_MULTIPLIER) + 1;

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
    private ProcessedOrder calcBestNextOrderMaxOrders(MapPoint start, List<ProcessedOrder> ordersList, CafeTracker shops, int maxMoves){
        double distanceToClosestOrder = Double.MAX_VALUE;
        ProcessedOrder bestOrder = null;
        for (ProcessedOrder potentialOrder : ordersList){
            double distToClosestShop = Double.MAX_VALUE;
            for (MapPoint point : potentialOrder.getShopLocations()){
                DroneMoveList pathToPoint = area.pathfind(start, point);
                distToClosestShop = Math.min(distToClosestShop, pathToPoint.totalMoveLength());
            }
            DroneMoveList routeToCompleteOrder = potentialOrder.getDroneMovesForOrder(start, area);

            routeToCompleteOrder.addRoutedDestination(MapPoint.APPLETON_TOWER, area);
            int totalShortMovesEstimate = routeToCompleteOrder.shortMoveSafeEstimate();

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











