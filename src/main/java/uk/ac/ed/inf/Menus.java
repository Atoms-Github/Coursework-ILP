package uk.ac.ed.inf;

public class Menus {
    public static final int DELIVERY_COST = 50;
    String machine_name;
    String port;

    public Menus(String machine_name, String port) {
        this.machine_name = machine_name;
        this.port = port;
    }
    public int getDeliveryCost(String ... args){
        // TODO:
        return 0 + DELIVERY_COST;
    }
}
