package com.openclassrooms.shopmanager.product;

import com.openclassrooms.shopmanager.order.Cart;
import com.openclassrooms.shopmanager.order.CartLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository repository) {
        this.productRepository = repository;
    }

    /**
     * @return all products from the inventory
     */
    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    public List<Product> getAllAdminProducts() {

        return productRepository.findAllByOrderByIdDesc();
    }

    public Product getByProductId(Long productId) {
        return productRepository.findById(productId).get();
    }

    /**
     * Creates a product and stores it in the database.
     *
     * @param productModel Product to create and store
     */
    public void createProduct(ProductModel productModel) {
        Product product = new Product();
        product.setDescription(productModel.getDescription());
        product.setDetails(productModel.getDetails());
        product.setName(productModel.getName());
        product.setPrice(Double.parseDouble(productModel.getPrice()));
        product.setQuantity(Integer.parseInt(productModel.getQuantity()));

        productRepository.save(product);
    }

    /**
     * Determines if a string is a valid representation of a double.
     *
     * @param potentialDouble String to check
     * @return True if the string is a double, false otherwise
     */
    public boolean isStringDouble(final String potentialDouble) {
        try {
            Double.parseDouble(potentialDouble);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Determines if a string is a valid representation of an integer.
     *
     * @param potentialInt String to check
     * @return True if the string is an integer, false otherwise
     */
    public boolean isStringInteger(final String potentialInt) {
        try {
            Integer.parseInt(potentialInt);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Returns a list of validation errors, which are mapped to error strings in the messages files. The returned list
     * may be empty, in which case no errors exist.
     *
     * @param productModel Product to check for validity
     * @return List of validation errors associated with the specified product, which may be empty, indicating no errors
     */
    public List<String> checkProductIsValid(final ProductModel productModel) {
        final List<String> errors = new LinkedList<>();
        if (productModel.getName() == null || productModel.getName().trim().isEmpty())
            errors.add("product.MissingName");

        if (productModel.getPrice() == null || productModel.getPrice().trim().isEmpty())
            errors.add("product.MissingPrice");
        else if (!isStringDouble(productModel.getPrice()))
            errors.add("product.PriceNotANumber");
        else if (Double.valueOf(productModel.getPrice()) <= 0D)
            errors.add("product.PriceNotGreaterThanZero");

        if (productModel.getQuantity() == null || productModel.getQuantity().trim().isEmpty())
            errors.add("product.MissingQuantity");
        else if (!isStringInteger(productModel.getQuantity()))
            errors.add("product.QuantityNotAnInteger");
        else if (Integer.valueOf(productModel.getQuantity()) <= 0)
            errors.add("product.QuantityNotGreaterThanZero");

        return errors;
    }

    public void deleteProduct(Long productId) {
        // TODO what happens if a product has been added to a cart and has been later removed from the inventory ?
        // delete the product form the cart by using the specific method
        // => the choice is up to the student
        productRepository.deleteById(productId);
    }

    public void updateProductQuantities(Cart cart) {

        for (CartLine cartLine : cart.getCartLineList()) {
            Optional<Product> productOptional = productRepository.findById(cartLine.getProduct().getId());
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                product.setQuantity(product.getQuantity() - cartLine.getQuantity());
                if (product.getQuantity() < 1) {
                    productRepository.delete(product);
                } else {
                    productRepository.save(product);
                }
            }
        }
    }

}
