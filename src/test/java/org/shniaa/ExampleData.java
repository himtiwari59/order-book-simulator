package org.shniaa;

public class ExampleData {
    /**
     * Submits a series of orders for MSFT. The resulting Order Book for MSFT
     * is the one shown in Table 1 of the ReadMe document.
     * */
    public static void buildExampleOrderBookFromReadMe(OrderHandler orderHandler) {
        orderHandler.addOrder(new Order(1L, "MSFT", Side.SELL, 19, 8));
        orderHandler.addOrder(new Order(2L, "MSFT", Side.SELL, 19, 4));
        orderHandler.addOrder(new Order(3L, "MSFT", Side.SELL, 21, 16));
        orderHandler.addOrder(new Order(4L, "MSFT", Side.SELL, 21, 1));
        orderHandler.addOrder(new Order(5L, "MSFT", Side.SELL, 22, 7));
        orderHandler.displayOrderBook("MSFT");

        orderHandler.addOrder(new Order(6L, "MSFT", Side.BUY, 13, 5));
        orderHandler.modifyOrder(new OrderModification(6L, 15, 10));

        orderHandler.addOrder(new Order(7L, "MSFT", Side.BUY, 15, 20));
        orderHandler.removeOrder(7L);
        orderHandler.displayOrderBook("MSFT");

        orderHandler.addOrder(new Order(8L, "MSFT", Side.BUY, 10, 13));
        orderHandler.addOrder(new Order(9L, "MSFT", Side.BUY, 10, 13));
    }
}
