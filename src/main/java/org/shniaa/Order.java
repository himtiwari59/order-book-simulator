package org.shniaa;

import java.time.LocalDateTime;

public class Order {

    private final long orderId;
    private final String symbol;
    private final Side side;
    private final int price;
    private final int quantity;
    private final LocalDateTime time;
    private final OrderKey key;

    public Order(long orderId, String symbol, Side side, int price, int quantity) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.time = LocalDateTime.now();
        key = new OrderKey(this.price, this.time, this.quantity);
    }

    public long getOrderId() {
        return orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public Side getSide() {
        return side;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public OrderKey getKey() {
        return key;
    }

    static class OrderKey{

        final private LocalDateTime time;
        final private float price;
        final private int quantity;

        OrderKey(float price, LocalDateTime time, int quantity ){
            this.price = price;
            this.time = time;
            this.quantity = quantity;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public float getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OrderKey key = (OrderKey) o;

            if (Float.compare(key.price, price) != 0) return false;
            if (quantity != key.quantity) return false;
            return time != null ? time.equals(key.time) : key.time == null;
        }

        @Override
        public int hashCode() {
            int result = time != null ? time.hashCode() : 0;
            result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
            result = 31 * result + quantity;
            return result;
        }
    }
}
