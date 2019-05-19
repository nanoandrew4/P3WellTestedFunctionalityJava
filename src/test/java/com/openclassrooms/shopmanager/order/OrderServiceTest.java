package com.openclassrooms.shopmanager.order;

import com.openclassrooms.shopmanager.product.Product;
import com.openclassrooms.shopmanager.product.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {
    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductService productService;

    @Test
    public void addToCart_AddExistingProduct_ReturnTrue() {
        when(productService.getByProductId(anyLong())).thenReturn(createValidTestProduct());

        boolean productAdded = orderService.addToCart(0L);

        assertTrue(productAdded);
        assertEquals(1, orderService.getCart().getCartLineList().size());
        assertEquals("Name", orderService.getCart().getCartLineByIndex(0).getProduct().getName());
        assertEquals(1, orderService.getCart().getCartLineByIndex(0).getQuantity());
    }

    @Test
    public void addToCart_AddNonExistingProduct_ReturnFalse() {
        when(productService.getByProductId(anyLong())).thenReturn(null);

        boolean productAdded = orderService.addToCart(0L);

        assertFalse(productAdded);
        assertEquals(0, orderService.getCart().getCartLineList().size());
    }

    @Test
    public void saveOrder_SaveValidOrder_OrderSavedSuccessfully() {
        final List<Order> orders = new LinkedList<>();

        doAnswer(invocation -> {
            orders.add(invocation.getArgument(0));
            return null;
        }).when(orderRepository).save(any(Order.class));

        orderService.saveOrder(new Order());

        assertFalse(orders.isEmpty());
    }

    @Test
    public void removeFromCart_RemoveProductInCart_ProductRemovedSuccessfully() {
        final Product testProduct = createValidTestProduct();
        when(productService.getByProductId(anyLong())).thenReturn(testProduct);

        orderService.addToCart(0L);
        orderService.removeFromCart(0L);

        assertTrue(orderService.isCartEmpty());
    }

    @Test
    public void removeFromCart_RemoveProductNotInCart_NoChangeToCart() {
        when(productService.getByProductId(0L)).thenReturn(createValidTestProduct());
        when(productService.getByProductId(1L)).thenReturn(null);

        orderService.addToCart(0L);
        orderService.removeFromCart(1L);

        assertFalse(orderService.isCartEmpty());
    }

    @Test
    public void isCartEmpty_CartIsEmpty_ReturnTrue() {
        assertTrue(orderService.isCartEmpty());
    }

    @Test
    public void isCartEmpty_CartNotEmpty_ReturnFalse() {
        when(productService.getByProductId(anyLong())).thenReturn(createValidTestProduct());

        orderService.addToCart(0L);

        assertFalse(orderService.isCartEmpty());
    }

    @Test
    public void createOrder_CreateValidOrder_OrderCreatedSuccessfully() {
        final List<Order> orders = new LinkedList<>();

        doAnswer(invocation -> {
            orders.add(invocation.getArgument(0));
            return null;
        }).when(orderRepository).save(any(Order.class));

        when(productService.getByProductId(anyLong())).thenReturn(createValidTestProduct());

        orderService.addToCart(0L);
        orderService.createOrder(new Order());

        assertEquals(1, orders.size());
        assertNotNull(orders.get(0).getLines());
        assertEquals(1, orders.get(0).getLines().size());
        assertEquals("Name", orders.get(0).getLines().get(0).getProduct().getName());
        assertTrue(orderService.isCartEmpty());
    }

    private Product createValidTestProduct() {
        final Product product = new Product();
        product.setId(0L);
        product.setQuantity(1);
        product.setPrice(1.01);
        product.setName("Name");
        product.setDescription("Desc");
        product.setDetails("Details");
        return product;
    }
}
