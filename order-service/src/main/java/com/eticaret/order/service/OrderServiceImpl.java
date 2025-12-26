package com.eticaret.order.service;

import com.eticaret.order.client.ProductServiceClient;
import com.eticaret.order.client.StockServiceClient;
import com.eticaret.order.client.UserServiceClient;
import com.eticaret.order.dto.ProductDTO;
import com.eticaret.order.dto.StockDTO;
import com.eticaret.order.dto.UserDTO;
import com.eticaret.order.exception.InsufficientStockException;
import com.eticaret.order.exception.OrderCancellationException;
import com.eticaret.order.exception.ResourceNotFoundException;
import com.eticaret.order.exception.ServiceUnavailableException;
import com.eticaret.order.model.Order;
import com.eticaret.order.model.OrderItem;
import com.eticaret.order.model.OrderStatus;
import com.eticaret.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Order> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final StockServiceClient stockServiceClient;
    private final SequenceService sequenceService;

    @Override
    public Order createOrder(Order order) {
        log.info("Yeni sipariş oluşturuluyor: userId={}, items={}", order.getUserId(), order.getItems().size());
        
        // Sıralı ID oluştur
        try {
            Long newId = sequenceService.getNextSequence("order_sequence");
            order.setId(newId);
            log.debug("Sipariş ID'si oluşturuldu: id={}", newId);
        } catch (Exception e) {
            log.error("Sipariş ID'si oluşturulamadı: {}", e.getMessage(), e);
            throw new RuntimeException("Sipariş ID'si oluşturulamadı: " + e.getMessage());
        }
        
        // Kullanıcı doğrulama
        try {
            UserDTO user = userServiceClient.getUserById(order.getUserId());
            if (user == null) {
                log.warn("Kullanıcı bulunamadı: userId={}", order.getUserId());
                throw new ResourceNotFoundException("Kullanıcı", order.getUserId());
            }
            log.info("Kullanıcı doğrulandı: userId={}, username={}", user.getId(), user.getUsername());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Kullanıcı doğrulama hatası: userId={}, error={}", order.getUserId(), e.getMessage(), e);
            throw new ServiceUnavailableException("User Service", e.getMessage());
        }
        
        // Ürün ve stok kontrolü
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            log.debug("Sipariş kalemi işleniyor: productId={}, quantity={}", item.getProductId(), item.getQuantity());
            
            // Ürün bilgilerini al
            ProductDTO product;
            try {
                product = productServiceClient.getProductById(item.getProductId());
                if (product == null) {
                    log.warn("Ürün bulunamadı: productId={}", item.getProductId());
                    throw new ResourceNotFoundException("Ürün bulunamadı: " + item.getProductId());
                }
                log.debug("Ürün bilgisi alındı: productId={}, name={}, price={}", 
                        product.getId(), product.getName(), product.getPrice());
            } catch (ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.error("Ürün bilgisi alınamadı: productId={}, error={}", item.getProductId(), e.getMessage(), e);
                throw new ServiceUnavailableException("Product Service", e.getMessage());
            }
            
            // Ürün bilgilerini güncelle
            item.setProductName(product.getName());
            
            // Stok kontrolü
            StockDTO stock;
            try {
                stock = stockServiceClient.getStockByProductId(item.getProductId());
                if (stock == null) {
                    log.warn("Ürün için stok bulunamadı: productId={}", item.getProductId());
                    throw new ResourceNotFoundException("Ürün için stok bulunamadı: " + item.getProductId());
                }
                
                if (stock.getAvailableQuantity() < item.getQuantity()) {
                    log.warn("Yetersiz stok: productId={}, productName={}, available={}, requested={}", 
                            item.getProductId(), product.getName(), stock.getAvailableQuantity(), item.getQuantity());
                    throw new InsufficientStockException(product.getName(), 
                            stock.getAvailableQuantity(), item.getQuantity());
                }
                
                // Stok rezerve et
                stockServiceClient.reserveStock(stock.getId(), item.getQuantity());
                log.info("Stok rezerve edildi: productId={}, quantity={}", item.getProductId(), item.getQuantity());
            } catch (InsufficientStockException | ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.error("Stok rezerve edilemedi: productId={}, error={}", item.getProductId(), e.getMessage(), e);
                throw new ServiceUnavailableException("Stock Service", e.getMessage());
            }
            
            // Toplam tutarı hesapla
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        try {
            kafkaTemplate.send("order-created", savedOrder);
            log.info("Kafka'ya sipariş mesajı gönderildi: orderId={}", savedOrder.getId());
        } catch (Exception e) {
            log.error("Kafka'ya mesaj gönderilemedi: orderId={}, error={}", savedOrder.getId(), e.getMessage(), e);
            // Kafka hatası sipariş oluşturmayı engellemez
        }
        
        log.info("Sipariş başarıyla oluşturuldu: orderId={}, userId={}, totalAmount={}, items={}", 
                savedOrder.getId(), savedOrder.getUserId(), savedOrder.getTotalAmount(), savedOrder.getItems().size());
        return savedOrder;
    }

    @Override
    public Order getOrderById(Long id) {
        log.debug("Sipariş getiriliyor: id={}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Sipariş bulunamadı: id={}", id);
                    return new ResourceNotFoundException("Sipariş", id);
                });
        log.debug("Sipariş bulundu: id={}, userId={}, status={}", order.getId(), order.getUserId(), order.getStatus());
        return order;
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        log.debug("Kullanıcıya ait siparişler getiriliyor: userId={}", userId);
        List<Order> orders = orderRepository.findByUserId(userId);
        log.info("Kullanıcı {} için {} sipariş bulundu", userId, orders.size());
        return orders;
    }

    @Override
    public List<Order> getAllOrders() {
        log.debug("Tüm siparişler getiriliyor");
        List<Order> orders = orderRepository.findAll();
        log.info("Toplam {} sipariş bulundu", orders.size());
        return orders;
    }

    @Override
    public Order updateOrderStatus(Long id, String status) {
        log.info("Sipariş durumu güncelleniyor: id={}, newStatus={}", id, status);
        Order order = getOrderById(id);
        
        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            Order updatedOrder = orderRepository.save(order);
            
            try {
                kafkaTemplate.send("order-status-updated", updatedOrder);
                log.info("Kafka'ya durum güncelleme mesajı gönderildi: orderId={}", updatedOrder.getId());
            } catch (Exception e) {
                log.error("Kafka'ya mesaj gönderilemedi: orderId={}, error={}", updatedOrder.getId(), e.getMessage(), e);
                // Kafka hatası durum güncellemeyi engellemez
            }
            
            log.info("Sipariş durumu başarıyla güncellendi: id={}, status={}", updatedOrder.getId(), updatedOrder.getStatus());
            return updatedOrder;
        } catch (IllegalArgumentException e) {
            log.error("Geçersiz sipariş durumu: status={}", status);
            throw new RuntimeException("Geçersiz sipariş durumu: " + status);
        }
    }

    @Override
    public void cancelOrder(Long id) {
        log.info("Sipariş iptal ediliyor: id={}", id);
        Order order = getOrderById(id);
        
        if (order.getStatus() == OrderStatus.DELIVERED) {
            log.warn("Teslim edilmiş sipariş iptal edilemez: id={}, status={}", id, order.getStatus());
            throw new OrderCancellationException("Teslim edilmiş sipariş iptal edilemez");
        }
        
        // Rezerve edilmiş stokları geri bırak
        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED) {
            log.info("Rezerve edilmiş stoklar serbest bırakılıyor: orderId={}", id);
            for (OrderItem item : order.getItems()) {
                try {
                    StockDTO stock = stockServiceClient.getStockByProductId(item.getProductId());
                    if (stock != null) {
                        stockServiceClient.releaseStock(stock.getId(), item.getQuantity());
                        log.info("Stok serbest bırakıldı: productId={}, quantity={}", item.getProductId(), item.getQuantity());
                    }
                } catch (Exception e) {
                    log.error("Stok serbest bırakılamadı: productId={}, error={}", item.getProductId(), e.getMessage(), e);
                    // Stok serbest bırakma hatası sipariş iptalini engellemez
                }
            }
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        
        try {
            kafkaTemplate.send("order-cancelled", order);
            log.info("Kafka'ya iptal mesajı gönderildi: orderId={}", id);
        } catch (Exception e) {
            log.error("Kafka'ya mesaj gönderilemedi: orderId={}, error={}", id, e.getMessage(), e);
            // Kafka hatası sipariş iptalini engellemez
        }
        
        log.info("Sipariş başarıyla iptal edildi: id={}", id);
    }
}

