package org.shniaa;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderHandlerImpl implements OrderHandler{

    private static final Map<String, OrderBook> orderBookMap = new HashMap<>();
    private static final Map<Long, Order> orders = new ConcurrentHashMap<>();

    @Override
    public void addOrder(Order order) {
        getOrderBook(order.getSymbol()).addOrder(order);
        orders.put(order.getOrderId(),order);
    }

    @Override
    public void modifyOrder(OrderModification orderModification) {

        final Order order = orders.get(orderModification.getOrderId());
        final OrderBook orderBook = getOrderBook(order.getSymbol());

        final Order modifiedOrder = new Order(order.getOrderId(),
                order.getSymbol(),
                order.getSide(),
                orderModification.getNewPrice(),
                orderModification.getNewQuantity());
        orderBook.removeOrder(order);
        orderBook.addOrder(modifiedOrder);

        orders.replace(order.getOrderId(),modifiedOrder);
    }

    @Override
    public void removeOrder(long orderId) {

        Order order = orders.get(orderId);
        getOrderBook(order.getSymbol())
                .removeOrder(order);

        orders.remove(orderId);
    }

    @Override
    public double getCurrentPrice(String symbol) {
        return getOrderBook(symbol).getCurrentPrice();
    }

    @Override
    public void displayOrderBook(String symbol) {
        getOrderBook(symbol).display();
    }

    private OrderBook getOrderBook(String symbol){
        if(orderBookMap.containsKey(symbol)) {
            return orderBookMap.get(symbol);
        } else{
            OrderBook orderBook = new OrderBook(symbol);
            orderBookMap.put(symbol,orderBook);
            return orderBook;
        }
    }
}
