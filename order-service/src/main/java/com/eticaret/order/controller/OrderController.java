package com.eticaret.order.controller;

import com.eticaret.order.dto.OrderRequestDTO;
import com.eticaret.order.dto.OrderResponseDTO;
import com.eticaret.order.mapper.OrderMapper;
import com.eticaret.order.model.Order;
import com.eticaret.order.service.IOrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Service", description = "Sipariş yönetimi API'leri - Sipariş oluşturma, güncelleme, iptal etme ve sorgulama işlemleri")
public class OrderController {
    
    private final IOrderService orderService;
    private final OrderMapper orderMapper;

    @Operation(summary = "Yeni sipariş oluştur", description = "Yeni bir sipariş oluşturur. Sipariş oluşturulurken ürün ve stok kontrolü yapılır, stok rezerve edilir. Toplam tutar otomatik hesaplanır.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Sipariş başarıyla oluşturuldu"),
        @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi"),
        @ApiResponse(responseCode = "404", description = "Kullanıcı, ürün veya stok bulunamadı"),
        @ApiResponse(responseCode = "400", description = "Yetersiz stok miktarı"),
        @ApiResponse(responseCode = "503", description = "Bağımlı servisler (User, Product, Stock) kullanılamıyor")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("POST /orders - Yeni sipariş oluşturma isteği alındı: userId={}, items={}", 
                orderRequestDTO.getUserId(), orderRequestDTO.getItems().size());
        try {
            Order order = orderMapper.toEntity(orderRequestDTO);
            Order createdOrder = orderService.createOrder(order);
            OrderResponseDTO responseDTO = orderMapper.toResponseDTO(createdOrder);
            log.info("POST /orders - Sipariş başarıyla oluşturuldu: id={}", responseDTO.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("POST /orders - Sipariş oluşturulurken hata: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Sipariş bilgilerini getir", description = "ID'ye göre sipariş bilgilerini getirir.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sipariş bilgileri başarıyla getirildi"),
        @ApiResponse(responseCode = "404", description = "Sipariş bulunamadı")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "Sipariş ID'si", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /orders/{} - Sipariş getirme isteği alındı", id);
        try {
            Order order = orderService.getOrderById(id);
            OrderResponseDTO responseDTO = orderMapper.toResponseDTO(order);
            log.info("GET /orders/{} - Sipariş başarıyla getirildi", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("GET /orders/{} - Sipariş getirilirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Kullanıcının siparişlerini getir", description = "Belirtilen kullanıcıya ait tüm siparişleri getirir.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sipariş listesi başarıyla getirildi")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(
            @Parameter(description = "Kullanıcı ID'si", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("GET /orders/user/{} - Kullanıcıya ait siparişler getirme isteği alındı", userId);
        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            List<OrderResponseDTO> responseDTOs = orders.stream()
                    .map(orderMapper::toResponseDTO)
                    .collect(Collectors.toList());
            log.info("GET /orders/user/{} - {} sipariş başarıyla getirildi", userId, responseDTOs.size());
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("GET /orders/user/{} - Siparişler getirilirken hata: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Tüm siparişleri listele", description = "Sistemdeki tüm siparişleri listeler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sipariş listesi başarıyla getirildi")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        log.info("GET /orders - Tüm siparişleri getirme isteği alındı");
        try {
            List<Order> orders = orderService.getAllOrders();
            List<OrderResponseDTO> responseDTOs = orders.stream()
                    .map(orderMapper::toResponseDTO)
                    .collect(Collectors.toList());
            log.info("GET /orders - {} sipariş başarıyla getirildi", responseDTOs.size());
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("GET /orders - Siparişler getirilirken hata: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Sipariş durumunu güncelle", description = "Sipariş durumunu günceller. Geçerli durumlar: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sipariş durumu başarıyla güncellendi"),
        @ApiResponse(responseCode = "404", description = "Sipariş bulunamadı"),
        @ApiResponse(responseCode = "400", description = "Geçersiz sipariş durumu veya sipariş zaten iptal edilmiş")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @Parameter(description = "Sipariş ID'si", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Yeni sipariş durumu (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)", required = true, example = "CONFIRMED")
            @RequestParam String status) {
        log.info("PUT /orders/{}/status - Sipariş durumu güncelleme isteği alındı: status={}", id, status);
        try {
            Order order = orderService.updateOrderStatus(id, status);
            OrderResponseDTO responseDTO = orderMapper.toResponseDTO(order);
            log.info("PUT /orders/{}/status - Sipariş durumu başarıyla güncellendi", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("PUT /orders/{}/status - Sipariş durumu güncellenirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Siparişi iptal et", description = "Siparişi iptal eder ve rezerve edilmiş stoku serbest bırakır. Sadece PENDING veya CONFIRMED durumundaki siparişler iptal edilebilir.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Sipariş başarıyla iptal edildi"),
        @ApiResponse(responseCode = "404", description = "Sipariş bulunamadı"),
        @ApiResponse(responseCode = "400", description = "Sipariş iptal edilemez (zaten teslim edilmiş veya iptal edilmiş)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "Sipariş ID'si", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /orders/{} - Sipariş iptal etme isteği alındı", id);
        try {
            orderService.cancelOrder(id);
            log.info("DELETE /orders/{} - Sipariş başarıyla iptal edildi", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("DELETE /orders/{} - Sipariş iptal edilirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}

