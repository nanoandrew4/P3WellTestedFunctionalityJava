package com.openclassrooms.shopmanager.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = {"/products", "/"})
    public String getProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    @GetMapping("/admin/products")
    public String getAdminProducts(Model model) {
        model.addAttribute("products", productService.getAllAdminProducts());
        return "productsAdmin";
    }


    @GetMapping("/admin/product")
    public String productForm(Model model) {
        model.addAttribute("product", new ProductModel());
        return "product";
    }

    @PostMapping("/admin/product")
    public String createProduct(@Valid @ModelAttribute("product") ProductModel productModel, BindingResult result) {
        for (String error : productService.checkProductIsValid(productModel))
            result.reject(error);

        if (!result.hasErrors()) {
            productService.createProduct(productModel);
            return "redirect:/admin/products";
        } else {
            return "product";
        }
    }

    @PostMapping("/admin/deleteProduct")
    public String deleteProduct(@RequestParam("delProductId") Long delProductId, Model model) {
        productService.deleteProduct(delProductId);
        model.addAttribute("products", productService.getAllAdminProducts());

        return "productsAdmin";
    }
}
