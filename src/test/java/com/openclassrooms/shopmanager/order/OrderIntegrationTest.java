package com.openclassrooms.shopmanager.order;

import com.openclassrooms.shopmanager.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.support.BindingAwareModelMap;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class
)
public class OrderIntegrationTest {

    @Autowired
    private OrderController orderController;

    @Test
    public void retrieveEmptyCart() {
        final Model model = new BindingAwareModelMap();

        final String targetUrl = orderController.getCart(model);

        assertEquals("cart", targetUrl);
        final Cart cart = (Cart) model.asMap().get("cart");
        assertTrue(cart.getCartLineList().isEmpty());
    }

    @Test
    @DirtiesContext
    public void addValidProductToCart() {
        final Model cartModel = new BindingAwareModelMap();
        final Long productId = 1L;

        final String targetUrl = orderController.addToCart(productId);
        orderController.getCart(cartModel);

        assertEquals("redirect:/order/cart", targetUrl);
        final Cart cart = (Cart) cartModel.asMap().get("cart");
        assertEquals(1, cart.getCartLineList().size());
        assertEquals(productId, cart.getCartLineByIndex(0).getProduct().getId());
        assertTrue(cart.getTotalValue() > 0);
        assertTrue(cart.getAverageValue() > 0);
        assertTrue(cart.getCartLineByIndex(0).getQuantity() > 0);
        assertTrue(cart.getCartLineByIndex(0).getSubtotal() > 0);
        assertTrue(cart.getCartLineByIndex(0).getOrderLineID() >= 0);
    }

    @Test
    @DirtiesContext
    public void addInvalidProductToCart() {
        final Model cartModel = new BindingAwareModelMap();
        final Long productId = 0L;

        final String targetUrl = orderController.addToCart(productId);
        orderController.getCart(cartModel);

        assertEquals("redirect:/products", targetUrl);
        final Cart cart = (Cart) cartModel.asMap().get("cart");
        assertTrue(cart.getCartLineList().isEmpty());
    }

    @Test
    @DirtiesContext
    public void removeProductFromCart() {
        final Model cartModel = new BindingAwareModelMap();
        final Long productId = 1L;

        final String targetAddProductToCartUrl = orderController.addToCart(productId);
        final String targetRemoveProductFromCartUrl = orderController.removeFromCart(productId);
        orderController.getCart(cartModel);

        assertEquals("redirect:/order/cart", targetRemoveProductFromCartUrl);
        assertEquals("redirect:/order/cart", targetAddProductToCartUrl);
        final Cart cart = (Cart) cartModel.asMap().get("cart");
        assertTrue(cart.getCartLineList().isEmpty());
    }

    @Test
    public void getValidOrderForm() {
        assertEquals("order", orderController.getOrderForm(new Order()));
    }

    @Test
    @DirtiesContext
    public void createValidOrder() {
        final Long productId = 1L;
        final Order order = new Order();
        final BindingResult bindingResult = new BeanPropertyBindingResult(order, "order");

        orderController.addToCart(productId);
        final String targetUrl = orderController.createOrder(order, bindingResult);

        assertEquals("orderCompleted", targetUrl);
        assertFalse(bindingResult.hasErrors());
        assertFalse(order.getLines().isEmpty());
    }

    @Test
    @DirtiesContext
    public void attemptCreateEmptyOrder() {
        final Order order = new Order();
        final BindingResult bindingResult = new BeanPropertyBindingResult(order, "order");

        final String targetUrl = orderController.createOrder(order, bindingResult);

        assertEquals("order", targetUrl);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getErrorCount());
        assertEquals("cart.empty", bindingResult.getAllErrors().get(0).getCode());
    }
}
