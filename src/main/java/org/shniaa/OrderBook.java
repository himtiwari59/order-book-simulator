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

            int qtyDelta = o1.getQuantity() -
                    o2.getQuantity();

            return priceDelta == 0 ?
                    (timeDelta ==0 ?
                            qtyDelta : timeDelta)
                    : priceDelta;

        };

        Comparator<Order.OrderKey> askComparator = (o1, o2) -> {

            int priceDelta = Float.compare(
                    o1.getPrice(),
                    o2.getPrice()
            );

            int timeDelta = o1.getTime().
                    compareTo(o2.getTime());

            int qtyDelta = o1.getQuantity() -
                    o2.getQuantity();

            return priceDelta == 0 ?
                    (timeDelta ==0 ?
                            qtyDelta : timeDelta)
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

        int count = bidOrders.size()>=
                askOrders.size() ?
                bidOrders.size() :
                askOrders.size();

        List<Order> bidList = new ArrayList<Order>(
                bidOrders.values());
        List<Order> askList = new ArrayList<Order>(
                askOrders.values());
        System.out.println("Bid Price : Qty || Ask Price : Qty");

        for(int i =0;i<count;i++){

            display(bidList,
                    askList,
                    count,
                    i
            );

            display(askList,
                    bidList,
                    count,
                    i
            );
        }
    }

    private void display(final List<Order> list1,final List<Order> list2, int totalCount, int counter){
        String price1="NA";String qty1="NA";String price2 =""; String qty2="";
        if(list1.size() < totalCount){

            if(counter< list1.size()){
                price1 = ""+list1
                        .get(counter)
                        .getPrice();
                qty1 = ""+list1
                        .get(counter)
                        .getQuantity();
            }

            price2 = ""+list2
                    .get(counter)
                    .getPrice();
            qty2 = ""+list2
                    .get(counter)
                    .getQuantity();

            System.out.println(price1+" : "+qty1+" || "+price2+" : "+qty2);
        }
    }
}
