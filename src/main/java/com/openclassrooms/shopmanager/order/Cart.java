package com.openclassrooms.shopmanager.order;

import com.openclassrooms.shopmanager.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Cart {

    private List<CartLine> cartLineList = new ArrayList<>();

    /**
     *
     * @return the actual cartline list
     */
    public List<CartLine> getCartLineList() {
//        return new ArrayList<>();
        // TO REMOVE
        return cartLineList;
    }

    /**
     * Adds a getProductById in the cart or increment its quantity in the cart if already added
     * @param product getProductById to be added
     * @param quantity the quantity
     */
    public void addItem(Product product, int quantity) {

        Optional<CartLine> cartLine = cartLineList.stream().filter(cl -> cl.getProduct().equals(product)).findFirst();

        if (cartLine.isPresent()){
            cartLine.get().setQuantity(cartLine.get().getQuantity() + quantity);

        }else {
            CartLine newCartLine = new CartLine();
            newCartLine.setOrderLineID((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
            newCartLine.setQuantity(quantity);
            newCartLine.setProduct(product);
            cartLineList.add(newCartLine);
        }
    }

    /**
     * Removes a getProductById form the cart
     * @param product the getProductById to be removed
     */
    public void removeLine(Product product) {
        getCartLineList().removeIf(l -> l.getProduct().getId().equals(product.getId()));
    }


    /**
     * @return total value of a cart
     */
    public double getTotalValue()
    {
        // TODO implement the method
//        return 0.0;

        // To REMOVE
        return getCartLineList().stream().mapToDouble(CartLine::getSubtotal).sum();

    }

    /**
     * @return Get average value of a cart
     */
    public double getAverageValue()
    {
        // TODO implement the method
//        return 0.0;

        int totalQuantity = getCartLineList().stream().mapToInt(CartLine::getQuantity).sum();

        if (totalQuantity > 0) {
            return getTotalValue() / totalQuantity;
        } else {
            return 0;
        }
    }

    /**
     * @param productId the getProductById id to search for
     * @return getProductById in the cart if it finds it
     */
    public Product findProductInCartLines(Long productId)
    {
        // TODO implement the method
//        return null;

        // TO REMOVE
       return cartLineList.stream().filter(cl -> cl.getProduct().getId().equals(productId)).findFirst().get().getProduct();
    }

    /**
     *
     * @param index index of the cartLine
     * @return CartLine in that index
     */
    public CartLine getCartLineByIndex(int index)
    {
        return getCartLineList().get(index);
    }

    /**
     * Clears a the cart of all added products
     */
    public void clear()
    {
        List<CartLine> cartLines = getCartLineList();
        cartLines.clear();
    }
}

