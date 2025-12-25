package com.eticaret.product.controller;

import com.eticaret.product.dto.ProductRequestDTO;
import com.eticaret.product.dto.ProductResponseDTO;
import com.eticaret.product.mapper.ProductMapper;
import com.eticaret.product.model.Product;
import com.eticaret.product.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Service", description = "Ürün yönetimi API'leri - Ürün oluşturma, güncelleme, silme ve sorgulama işlemleri")
public class ProductController {
    
    private final IProductService productService;
    private final ProductMapper productMapper;

    @Operation(summary = "Yeni ürün oluştur", description = "Sistemde yeni bir ürün kaydı oluşturur. Ürün oluşturulurken otomatik olarak stok kaydı da oluşturulur.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ürün başarıyla oluşturuldu"),
        @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi")
    })
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        log.info("POST /products - Yeni ürün oluşturma isteği alındı: name={}", productRequestDTO.getName());
        try {
            Product product = productMapper.toEntity(productRequestDTO);
            Product createdProduct = productService.createProduct(product);
            ProductResponseDTO responseDTO = productMapper.toResponseDTO(createdProduct);
            log.info("POST /products - Ürün başarıyla oluşturuldu: id={}", responseDTO.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("POST /products - Ürün oluşturulurken hata: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Ürün bilgilerini getir", description = "ID'ye göre ürün bilgilerini getirir. Ürün bulunamazsa 404 hatası döner.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ürün bilgileri başarıyla getirildi"),
        @ApiResponse(responseCode = "404", description = "Ürün bulunamadı")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "Ürün ID'si (MongoDB ObjectId)", required = true, example = "694bc5213849350c4279c69f")
            @PathVariable String id) {
        log.info("GET /products/{} - Ürün getirme isteği alındı", id);
        try {
            Product product = productService.getProductById(id);
            ProductResponseDTO responseDTO = productMapper.toResponseDTO(product);
            log.info("GET /products/{} - Ürün başarıyla getirildi", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("GET /products/{} - Ürün getirilirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Ürünleri listele", description = "Tüm ürünleri, kategoriye göre veya isme göre filtreleyerek listeler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ürün listesi başarıyla getirildi")
    })
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @Parameter(description = "Kategori filtresi (opsiyonel)", example = "Elektronik")
            @RequestParam(required = false) String category,
            @Parameter(description = "İsim filtresi (opsiyonel)", example = "iPhone")
            @RequestParam(required = false) String name) {
        log.info("GET /products - Ürün listesi getirme isteği alındı: category={}, name={}", category, name);
        try {
            List<Product> products;
            if (category != null) {
                products = productService.getProductsByCategory(category);
            } else if (name != null) {
                products = productService.searchProducts(name);
            } else {
                products = productService.getAllProducts();
            }
            List<ProductResponseDTO> responseDTOs = products.stream()
                    .map(productMapper::toResponseDTO)
                    .collect(Collectors.toList());
            log.info("GET /products - {} ürün başarıyla getirildi", responseDTOs.size());
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("GET /products - Ürünler getirilirken hata: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Ürün bilgilerini güncelle", description = "Mevcut ürün bilgilerini günceller.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ürün bilgileri başarıyla güncellendi"),
        @ApiResponse(responseCode = "404", description = "Ürün bulunamadı")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "Ürün ID'si (MongoDB ObjectId)", required = true, example = "694bc5213849350c4279c69f")
            @PathVariable String id, 
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        log.info("PUT /products/{} - Ürün güncelleme isteği alındı", id);
        try {
            Product existingProduct = productService.getProductById(id);
            productMapper.updateEntityFromDTO(existingProduct, productRequestDTO);
            Product updatedProduct = productService.updateProduct(id, existingProduct);
            ProductResponseDTO responseDTO = productMapper.toResponseDTO(updatedProduct);
            log.info("PUT /products/{} - Ürün başarıyla güncellendi", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("PUT /products/{} - Ürün güncellenirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Ürünü sil", description = "ID'ye göre ürünü sistemden siler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ürün başarıyla silindi"),
        @ApiResponse(responseCode = "404", description = "Ürün bulunamadı")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Ürün ID'si (MongoDB ObjectId)", required = true, example = "694bc5213849350c4279c69f")
            @PathVariable String id) {
        log.info("DELETE /products/{} - Ürün silme isteği alındı", id);
        try {
            productService.deleteProduct(id);
            log.info("DELETE /products/{} - Ürün başarıyla silindi", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("DELETE /products/{} - Ürün silinirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}

