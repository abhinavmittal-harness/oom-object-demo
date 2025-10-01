package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductService {
    private final Map<String, Product> products;
    private final Map<String, List<Product>> productsByCategory;
    private final Logger logger;
    
    public ProductService(Logger logger) {
        this.products = new ConcurrentHashMap<>();
        this.productsByCategory = new ConcurrentHashMap<>();
        this.logger = logger;
    }
    
    public Product addProduct(Product product) {
        products.put(product.getId(), product);
        
        // Add to category index
        productsByCategory.computeIfAbsent(product.getCategory(), k -> new ArrayList<>())
                         .add(product);
        
        logger.debug("Added product: " + product.getId());
        return product;
    }
    
    public Product getProduct(String productId) {
        return products.get(productId);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productsByCategory.getOrDefault(category, new ArrayList<>())
                                .stream()
                                .filter(Product::isActive)
                                .collect(Collectors.toList());
    }
    
    public List<Product> searchProducts(String query) {
        return products.values().stream()
                      .filter(Product::isActive)
                      .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()) ||
                                  p.getDescription().toLowerCase().contains(query.toLowerCase()))
                      .collect(Collectors.toList());
    }
    
    public void deactivateProduct(String productId) {
        Product product = products.get(productId);
        if (product != null) {
            product.setActive(false);
            logger.info("Deactivated product: " + productId);
        }
    }
    
    public int getProductCount() {
        return products.size();
    }
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
}
