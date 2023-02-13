package org.shniaa;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class OrderBook implements Displayable {

    private final SortedMap<Order.OrderKey, Order> bidOrders;
    private final SortedMap<Order.OrderKey, Order> askOrders;

    private final String symbol;

    private volatile float currentPrice;

    public OrderBook(String symbol){

        this.symbol = symbol;

        Comparator<Order.OrderKey> bidComparator = (o1, o2) -> {

            int priceDelta = Float.compare(
                    o2.getPrice(),
                    o1.getPrice()
            );

            int timeDelta = o1.getTime().
                    compareTo(o2.getTime());


            return priceDelta == 0 ? timeDelta
                    : priceDelta;

        };

        Comparator<Order.OrderKey> askComparator = (o1, o2) -> {

            int priceDelta = Float.compare(
                    o1.getPrice(),
                    o2.getPrice()
            );

            int timeDelta = o1.getTime().
                    compareTo(o2.getTime());

            return priceDelta == 0 ? timeDelta
                    : priceDelta;

        };

        bidOrders = new ConcurrentSkipListMap(bidComparator);
        askOrders = new ConcurrentSkipListMap(askComparator);
    }

    public void addOrder(final Order order){

        final Order.OrderKey key = new Order.OrderKey(
                order.getPrice(),
                order.getTime(),
                order.getQuantity()
        );

        if(order.getSide() == Side.BUY)
            bidOrders.put(key,order);
        else askOrders.put(key, order);

        synchronized (this) {
            matchOrder();
        }
    }

    public void removeOrder(final Order order){

        if(order.getSide() == Side.BUY)
            bidOrders.remove(order.getKey());
        else
            askOrders.remove(order.getKey());

        synchronized (this){
            matchOrder();
        }
    }

    private void matchOrder(){

        if(bidOrders.size() > 0 && askOrders.size() > 0) {

            final Order bestBid = bidOrders.get(bidOrders.firstKey());
            final Order bestAsk = askOrders.get(askOrders.firstKey());

            final int spread = Float.compare(bestBid.getPrice(),
                    bestAsk.getPrice());

            if (spread >= 0) { // best bid price is >= best ask price , match occurred

                int qtyDiff = bestBid.getQuantity() -
                        bestAsk.getQuantity();

                if (qtyDiff >= 0) {// sell order (ask) will be completely filled

                    askOrders.remove(askOrders.firstKey());
                    bidOrders.replace(bidOrders.firstKey(), new Order(
                            bestBid.getOrderId(),
                            bestBid.getSymbol(),
                            bestBid.getSide(),
                            bestBid.getPrice(),
                            qtyDiff)
                    );

                } else { // buy order will be completely filled

                    bidOrders.remove(bidOrders.firstKey());
                    askOrders.replace(askOrders.firstKey(), new Order(
                            bestAsk.getOrderId(),
                            bestAsk.getSymbol(),
                            bestAsk.getSide(),
                            bestAsk.getPrice(),
                            qtyDiff)
                    );
                }
                currentPrice = bestAsk.getPrice();
            } else {
                //order matching not occurred.
            }
        }
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void display(){
        //ytd
    }
}
