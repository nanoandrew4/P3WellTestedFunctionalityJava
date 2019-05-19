package com.openclassrooms.shopmanager.product;

import com.openclassrooms.shopmanager.order.Cart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Take this test method as a template to write your test methods for ProductService and OrderService.
 * A test method must check if a definite method does its job:
 * <p>
 * Naming follows this popular convention : http://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

    @InjectMocks
    ProductService productService;

    @Mock
    ProductRepository productRepository;

    @Test
    public void isStringDouble_NonDoubleStrings_returnFalse() {
        assertFalse(productService.isStringDouble("Double"));
        assertFalse(productService.isStringDouble("1.01.01"));
        assertFalse(productService.isStringDouble("-1.01.01"));
        assertFalse(productService.isStringDouble("1.01DDF"));
        assertFalse(productService.isStringDouble("1,01"));
    }

    @Test
    public void isStringDouble_DoubleStrings_returnTrue() {
        assertTrue(productService.isStringDouble("1.01"));
        assertTrue(productService.isStringDouble("-1.01"));
        assertTrue(productService.isStringDouble("1"));
        assertTrue(productService.isStringDouble("1.01D"));
        assertTrue(productService.isStringDouble("1.01F"));
        assertTrue(productService.isStringDouble(String.valueOf(Long.MAX_VALUE)));
    }

    @Test
    public void isStringInteger_NonIntegerStrings_returnFalse() {
        assertFalse(productService.isStringInteger("1,01"));
        assertFalse(productService.isStringInteger("Integer"));
        assertFalse(productService.isStringInteger("1,01"));
        assertFalse(productService.isStringInteger(String.valueOf(Long.MAX_VALUE)));
        assertFalse(productService.isStringInteger("1D"));
        assertFalse(productService.isStringInteger("1F"));
        assertFalse(productService.isStringInteger("1L"));
    }

    @Test
    public void isStringInteger_IntegerStrings_returnTrue() {
        assertTrue(productService.isStringInteger(String.valueOf(Integer.MAX_VALUE)));
        assertTrue(productService.isStringInteger("1"));
        assertTrue(productService.isStringInteger("111"));
        assertTrue(productService.isStringInteger("-1"));
    }

    @Test
    public void getAllProducts_DbHasData_allDataReturned() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("First product");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("First product");

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        assertEquals(1L, products.get(0).getId(), 0);
        assertEquals(2L, products.get(1).getId(), 0);
    }

    @Test
    public void createProduct_MissingName_MissingNameErrorMessageReturned() {
        final ProductModel productModel = new ProductModel();
        productModel.setQuantity("1");
        productModel.setPrice("1");
        productModel.setDescription("Desc");
        productModel.setDetails("Details");

        final List<String> errors = productService.checkProductIsValid(productModel);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("product.MissingName"));
    }

    @Test
    public void createProduct_MissingPrice_MissingPriceErrorMessageReturned() {
        final ProductModel productModel = new ProductModel();
        productModel.setQuantity("1");
        productModel.setName("Name");
        productModel.setDescription("Desc");
        productModel.setDetails("Details");

        final List<String> errors = productService.checkProductIsValid(productModel);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("product.MissingPrice"));
    }

    @Test
    public void createProduct_PriceNaN_PriceNotANumberErrorMessageReturned() {
        final ProductModel productModel = new ProductModel();
        productModel.setQuantity("1");
        productModel.setPrice("Price");
        productModel.setName("Name");
        productModel.setDescription("Desc");
        productModel.setDetails("Details");

        final List<String> errors = productService.checkProductIsValid(productModel);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("product.PriceNotANumber"));
    }

    @Test
    public void createProduct_PriceNotGreaterThanZero_PriceNotGreaterThanZeroErrorMessageReturned() {
        final ProductModel productModel = new ProductModel();
        productModel.setQuantity("1");
        productModel.setPrice("-1.01");
        productModel.setName("Name");
        productModel.setDescription("Desc");
        productModel.setDetails("Details");

        final List<String> errors = productService.checkProductIsValid(productModel);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("product.PriceNotGreaterThanZero"));
    }

    @Test
    public void createProduct_MissingQuantity_MissingQuantityErrorMessageReturned() {
        final ProductModel productModel = new ProductModel();
        productModel.setPrice("1.01");
        productModel.setName("Name");
        productModel.setDescription("Desc");
        productModel.setDetails("Details");

        final List<String> errors = productService.checkProductIsValid(productModel);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("product.MissingQuantity"));
    }

    @Test
    public void createProduct_QuantityNotAnInteger_QuantityNotAnIntegerErrorMessageReturned() {
        final ProductModel productModel = new ProductModel();
        productModel.setQuantity("Quantity");
        productModel.setPrice("1.01");
        productModel.setName("Name");
        productModel.setDescription("Desc");
        productModel.setDetails("Details");

        final List<String> errors = productService.checkProductIsValid(productModel);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("product.QuantityNotAnInteger"));
    }

    @Test
    public void createProduct_QuantityNotGreaterThanZero_QuantityNotGreaterThanZeroErrorMessageReturned() {
        final ProductModel productModel = new ProductModel();
        productModel.setQuantity("-1");
        productModel.setPrice("1.01");
        productModel.setName("Name");
        productModel.setDescription("Desc");
        productModel.setDetails("Details");

        final List<String> errors = productService.checkProductIsValid(productModel);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("product.QuantityNotGreaterThanZero"));
    }

    @Test
    public void createProduct_ValidProduct_createProductSuccessful() {
        final List<Product> products = new LinkedList<>();
        when(productRepository.save(any(Product.class))).then(invocation -> {
            final Product savedProduct = invocation.getArgument(0);
            products.add(savedProduct);
            return savedProduct;
        });
        when(productRepository.findAll()).thenReturn(products);

        productService.createProduct(createValidTestProductModel());
        final List<Product> storedProducts = productService.getAllProducts();

        assertEquals(1, storedProducts.size());
        assertEquals("Name", storedProducts.get(0).getName());
        assertEquals(1, storedProducts.get(0).getQuantity());
        assertEquals(1.01, storedProducts.get(0).getPrice(), 0);
        assertEquals("Desc", storedProducts.get(0).getDescription());
        assertEquals("Details", storedProducts.get(0).getDetails());
    }

    @Test
    public void ProductDeletion_DeleteExistingProduct_ProductDeletedSuccessfully() {
        final List<Product> products = new LinkedList<>();

        when(productRepository.save(any(Product.class))).then(invocation -> {
            final Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(System.currentTimeMillis());
            products.add(savedProduct);
            return savedProduct;
        });
        when(productRepository.findAll()).thenReturn(products);
        doAnswer(invocation -> {
            products.removeIf(product -> invocation.getArgument(0).equals(product.getId()));
            return null;
        }).when(productRepository).deleteById(anyLong());

        productService.createProduct(createValidTestProductModel());
        productService.deleteProduct(productService.getAllProducts().get(0).getId());

        assertTrue(products.isEmpty());
    }

    @Test
    public void getByProductId_RetrieveStoredProduct_ReturnRequestedProduct() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(createValidTestProduct()));

        final Product retrievedProduct = productService.getByProductId(1L);

        assertEquals("Name", retrievedProduct.getName());
        assertEquals(1, retrievedProduct.getQuantity());
        assertEquals(1.01, retrievedProduct.getPrice(), 0);
        assertEquals("Desc", retrievedProduct.getDescription());
        assertEquals("Details", retrievedProduct.getDetails());
    }

    @Test
    public void getByProductId_ProductDoesNotExist_ThrowNoSuchElementException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        final Product retrievedProduct = productService.getByProductId(1L);
        assertNull(retrievedProduct);
    }

    @Test
    public void updateProductQuantities_ModifyStockQuantities_AllProductQuantitiesModified() {
        final List<Product> products = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            final Product newProduct = createValidTestProduct();
            newProduct.setId((long) i);
            newProduct.setQuantity(i + 1);
            newProduct.setName(newProduct.getName() + i);
            products.add(newProduct);
        }

        when(productRepository.findById(anyLong())).then(invocation ->
                products.stream().filter(product -> product.getId().equals(invocation.getArgument(0))).findFirst()
        );
        doAnswer(invocation -> {
            final Product productToBeRemoved = invocation.getArgument(0);
            products.removeIf(product -> product.getId().equals(productToBeRemoved.getId()));
            return null;
        }).when(productRepository).delete(any(Product.class));
        when(productRepository.save(any(Product.class))).then(invocation -> {
            final Product productToBeSaved = invocation.getArgument(0);
            products.removeIf(product -> product.getId().equals(productToBeSaved.getId()));
            products.add(productToBeSaved);
            return productToBeSaved;
        });

        final Cart cart = new Cart();
        int[] quantitiesToAddToCart = {1, 2, 1, 2};
        products.forEach(product -> cart.addItem(product, quantitiesToAddToCart[Math.toIntExact(product.getId())]));

        // First two products should be removed from the 'products' list, since they are out of stock
        productService.updateProductQuantities(cart);

        assertEquals(2, products.size());
        assertEquals("Name2", products.get(0).getName());
        assertEquals(2, products.get(0).getQuantity());
        assertEquals("Name3", products.get(1).getName());
        assertEquals(2, products.get(1).getQuantity());
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

    private Product createValidTestProduct() {
        final Product product = new Product();
        product.setQuantity(1);
        product.setPrice(1.01);
        product.setName("Name");
        product.setDescription("Desc");
        product.setDetails("Details");
        return product;
    }
}
