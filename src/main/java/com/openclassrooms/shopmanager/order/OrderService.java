package com.openclassrooms.shopmanager.order;

import com.openclassrooms.shopmanager.product.Product;
import com.openclassrooms.shopmanager.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ProductService productService;

    private Cart cart = new Cart();

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public boolean addToCart(Long productId) {
        Product product = productService.getByProductId(productId);
        if (product != null) {
            cart.addItem(product, 1);
            return true;
        }
        return false;
    }

    /**
     * @param order Order to be saved
     */
    public void saveOrder(Order order) {
        orderRepository.save(order);
        productService.updateProductQuantities(this.cart);
    }

    /**
     * @return Returns the single instance of cart in the application
     */
    public Cart getCart() {
        return this.cart;
    }

    public void removeFromCart(Long productId) {
        Product product = productService.getByProductId(productId);
        if (product != null) {
            getCart().removeLine(product);
        }
    }

    public boolean isCartEmpty() {
        return getCart().getCartLineList().isEmpty();
    }

    public void createOrder(Order order) {
        order.setLines(getCart().getCartLineList());
        saveOrder(order);
        this.cart.clear();
    }
}
