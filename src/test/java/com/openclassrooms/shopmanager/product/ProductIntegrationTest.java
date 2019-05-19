package com.openclassrooms.shopmanager.product;

import com.openclassrooms.shopmanager.Application;
import com.openclassrooms.shopmanager.order.Cart;
import com.openclassrooms.shopmanager.order.OrderController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class
)
public class ProductIntegrationTest {

    @Autowired
    private ProductController productController;

    @Autowired
    private OrderController orderController;

    @Test
    public void productsRetrievedSuccessfully() {
        final Model model = new BindingAwareModelMap();

        final String targetUrl = productController.getProducts(model);

        final List<Product> displayedProducts = (List<Product>) model.asMap().get("products");
        assertEquals("products", targetUrl);
        assertEquals(5, displayedProducts.size());
    }

    @Test
    public void adminProductsRetrievedSuccessfully() {
        final Model model = new BindingAwareModelMap();

        final String targetUrl = productController.getAdminProducts(model);

        final List<Product> displayedProducts = (List<Product>) model.asMap().get("products");
        assertEquals("productsAdmin", targetUrl);
        assertEquals(5, displayedProducts.size());
    }

    @Test
    public void productFormRetrievedSuccessfully() {
        final Model model = new BindingAwareModelMap();

        final String targerUrl = productController.productForm(model);

        assertEquals("product", targerUrl);
        assertTrue(model.asMap().get("product") instanceof ProductModel);
    }

    @Test
    @DirtiesContext
    public void createProductWithRequiredData() {
        final ProductModel productModel = createValidTestProductModel();
        final BindingResult bindingResult = new BeanPropertyBindingResult(productModel, "product");

        final String targetUrl = productController.createProduct(productModel, bindingResult);

        assertEquals("redirect:/admin/products", targetUrl);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void createProductWithMissingData() {
        final ProductModel productModel = createValidTestProductModel();
        productModel.setName("");
        productModel.setPrice("");
        productModel.setQuantity("");
        final BindingResult bindingResult = new BeanPropertyBindingResult(productModel, "product");

        final String targetUrl = productController.createProduct(productModel, bindingResult);

        assertEquals("product", targetUrl);
        assertTrue(bindingResult.hasErrors());
        assertEquals(3, bindingResult.getErrorCount());
        final List<ObjectError> errors = bindingResult.getAllErrors();
        assertEquals("product.MissingName", errors.get(0).getCode());
        assertEquals("product.MissingPrice", errors.get(1).getCode());
        assertEquals("product.MissingQuantity", errors.get(2).getCode());
    }

    @Test
    @DirtiesContext
    public void deleteProductFromCatalogThatIsInCart() {
        final Model productModel = new BindingAwareModelMap();
        final Model cartModel = new BindingAwareModelMap();
        final Long productId = 1L;

        final String targetCartAddProductUrl = orderController.addToCart(productId);
        final String targetDeleteProductUrl = productController.deleteProduct(productId, productModel);
        orderController.getCart(cartModel);

        final List<Product> displayedAdminProducts = (List<Product>) productModel.asMap().get("products");
        final Cart cart = (Cart) cartModel.asMap().get("cart");

        assertEquals("productsAdmin", targetDeleteProductUrl);
        assertEquals(4, displayedAdminProducts.size()); // data.sql has 5 products defined, so removing one should leave it at 4
        assertEquals("redirect:/order/cart", targetCartAddProductUrl);
        assertTrue(cart.getCartLineList().isEmpty()); // Since the product in the cart was removed by the admin, cart should be empty
    }

    private ProductModel createValidTestProductModel() {
        final ProductModel productModel = new ProductModel();
        productModel.setQuantity("1");
        productModel.setPrice("1.01");
        productModel.setName("Name");
        productModel.setDescription("Desc");
        productModel.setDetails("Details");
        return productModel;
    }
}
