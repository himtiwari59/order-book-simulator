package org.shniaa;

import org.junit.Test;

public class OrderHandlerTest {


    @Test
    public void testOrderHandel(){
        OrderHandler orderHandler = new OrderHandlerImpl();
        ExampleData.buildExampleOrderBookFromReadMe(orderHandler);
        orderHandler.getCurrentPrice("MSFT");
    }
}
