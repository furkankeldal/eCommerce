package com.eticaret.product.service;

import com.eticaret.product.exception.ResourceNotFoundException;
import com.eticaret.product.model.Product;
import com.eticaret.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    
    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        log.info("Yeni ürün oluşturuluyor: name={}, category={}, price={}", 
                product.getName(), product.getCategory(), product.getPrice());
        try {
            // ID MongoDB tarafından otomatik oluşturulacak (random ObjectId)
            Product savedProduct = productRepository.save(product);
            log.info("Ürün başarıyla oluşturuldu: id={}, name={}", savedProduct.getId(), savedProduct.getName());
            return savedProduct;
        } catch (Exception e) {
            log.error("Ürün oluşturulurken hata: name={}, error={}", product.getName(), e.getMessage(), e);
            throw new RuntimeException("Ürün oluşturulamadı: " + e.getMessage());
        }
    }

    @Override
    public Product getProductById(String id) {
        log.debug("Ürün getiriliyor: id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ürün bulunamadı: id={}", id);
                    return new ResourceNotFoundException("Ürün", id);
                });
        log.debug("Ürün bulundu: id={}, name={}", product.getId(), product.getName());
        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        log.debug("Tüm ürünler getiriliyor");
        List<Product> products = productRepository.findAll();
        log.info("Toplam {} ürün bulundu", products.size());
        return products;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        log.debug("Kategoriye göre ürünler getiriliyor: category={}", category);
        List<Product> products = productRepository.findByCategory(category);
        log.info("Kategori '{}' için {} ürün bulundu", category, products.size());
        return products;
    }

    @Override
    public List<Product> searchProducts(String name) {
        log.debug("Ürün arama yapılıyor: name={}", name);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        log.info("'{}' araması için {} ürün bulundu", name, products.size());
        return products;
    }

    @Override
    public Product updateProduct(String id, Product product) {
        log.info("Ürün güncelleniyor: id={}", id);
        Product existingProduct = getProductById(id);
        
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setImageUrl(product.getImageUrl());
        
        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Ürün başarıyla güncellendi: id={}, name={}", updatedProduct.getId(), updatedProduct.getName());
        return updatedProduct;
    }

    @Override
    public void deleteProduct(String id) {
        log.info("Ürün siliniyor: id={}", id);
        if (!productRepository.existsById(id)) {
            log.warn("Silinecek ürün bulunamadı: id={}", id);
            throw new ResourceNotFoundException("Ürün", id);
        }
        productRepository.deleteById(id);
        log.info("Ürün başarıyla silindi: id={}", id);
    }
}

