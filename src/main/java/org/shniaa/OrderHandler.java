package org.shniaa;


public interface OrderHandler {

    void addOrder(Order order);

    void modifyOrder(OrderModification orderModification);

    void removeOrder(long orderId);

    double getCurrentPrice(String symbol);

    void displayOrderBook(String symbol);

}
