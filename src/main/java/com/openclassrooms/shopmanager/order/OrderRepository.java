package com.openclassrooms.shopmanager.order;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {

    private List<Order> orders = new ArrayList<>();

    /**
     * Saves an order
     *
     * @param order order to be saved
     */
    public void save(Order order) {
        orders.add(order);
    }
}
