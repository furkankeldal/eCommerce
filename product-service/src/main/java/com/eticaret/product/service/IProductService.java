package com.eticaret.product.service;

import com.eticaret.product.model.Product;

import java.util.List;

public interface IProductService {
    Product createProduct(Product product);
    Product getProductById(String id);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> searchProducts(String name);
    Product updateProduct(String id, Product product);
    void deleteProduct(String id);
}

