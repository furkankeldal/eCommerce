package com.eticaret.stock.service;

import com.eticaret.stock.exception.DuplicateResourceException;
import com.eticaret.stock.exception.InsufficientStockException;
import com.eticaret.stock.exception.ResourceNotFoundException;
import com.eticaret.stock.model.Stock;
import com.eticaret.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockServiceImpl implements IStockService {
    
    private final StockRepository stockRepository;
    private final SequenceService sequenceService;

    @Override
    @CacheEvict(value = "stocks", key = "#stock.productId")
    public Stock createStock(Stock stock) {
        log.info("Yeni stok kaydı oluşturuluyor: productId={}, quantity={}", stock.getProductId(), stock.getQuantity());
        
        // Aynı ürün için stok kaydı var mı kontrol et
        if (stockRepository.findByProductId(stock.getProductId()).isPresent()) {
            log.warn("Bu ürün için stok kaydı zaten mevcut: productId={}", stock.getProductId());
            throw new DuplicateResourceException("Bu ürün için stok kaydı zaten mevcut: " + stock.getProductId());
        }
        
        // Sıralı ID oluştur
        try {
            Long newId = sequenceService.getNextSequence("stock_sequence");
            stock.setId(newId);
            log.debug("Stok ID'si oluşturuldu: id={}", newId);
        } catch (Exception e) {
            log.error("Stok ID'si oluşturulamadı: {}", e.getMessage(), e);
            throw new RuntimeException("Stok ID'si oluşturulamadı: " + e.getMessage());
        }
        
        if (stock.getReservedQuantity() == null) {
            stock.setReservedQuantity(0);
        }
        
        Stock savedStock = stockRepository.save(stock);
        log.info("Stok kaydı başarıyla oluşturuldu: id={}, productId={}, quantity={}", 
                savedStock.getId(), savedStock.getProductId(), savedStock.getQuantity());
        return savedStock;
    }

    @Override
    public Stock getStockById(Long id) {
        log.debug("Stok getiriliyor: id={}", id);
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Stok bulunamadı: id={}", id);
                    return new ResourceNotFoundException("Stok", id);
                });
        log.debug("Stok bulundu: id={}, productId={}", stock.getId(), stock.getProductId());
        return stock;
    }

    @Override
    @Cacheable(value = "stocks", key = "#productId")
    public Stock getStockByProductId(String productId) {
        log.debug("Ürün ID'sine göre stok getiriliyor: productId={}", productId);
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.warn("Ürün için stok bulunamadı: productId={}", productId);
                    return new ResourceNotFoundException("Stok", productId);
                });
        log.debug("Stok bulundu: id={}, productId={}", stock.getId(), stock.getProductId());
        return stock;
    }

    @Override
    public List<Stock> getAllStocks() {
        log.debug("Tüm stok kayıtları getiriliyor");
        List<Stock> stocks = stockRepository.findAll();
        log.info("Toplam {} stok kaydı bulundu", stocks.size());
        return stocks;
    }

    @Override
    @CacheEvict(value = "stocks", key = "#stock.productId")
    public Stock updateStock(Long id, Stock stock) {
        log.info("Stok güncelleniyor: id={}", id);
        Stock existingStock = getStockById(id);
        
        existingStock.setQuantity(stock.getQuantity());
        existingStock.setReservedQuantity(stock.getReservedQuantity());
        existingStock.setLocation(stock.getLocation());
        
        Stock updatedStock = stockRepository.save(existingStock);
        log.info("Stok başarıyla güncellendi: id={}, productId={}, quantity={}", 
                updatedStock.getId(), updatedStock.getProductId(), updatedStock.getQuantity());
        return updatedStock;
    }

    @Override
    @CacheEvict(value = "stocks", key = "#result.productId")
    public Stock reserveStock(Long id, Integer quantity) {
        log.info("Stok rezerve ediliyor: id={}, quantity={}", id, quantity);
        Stock stock = getStockById(id);
        
        if (stock.getAvailableQuantity() < quantity) {
            log.warn("Yetersiz stok: id={}, available={}, requested={}", 
                    id, stock.getAvailableQuantity(), quantity);
            throw new InsufficientStockException(stock.getAvailableQuantity(), quantity);
        }
        
        stock.setReservedQuantity(stock.getReservedQuantity() + quantity);
        Stock updatedStock = stockRepository.save(stock);
        log.info("Stok başarıyla rezerve edildi: id={}, reservedQuantity={}", 
                updatedStock.getId(), updatedStock.getReservedQuantity());
        return updatedStock;
    }

    @Override
    @CacheEvict(value = "stocks", key = "#result.productId")
    public Stock releaseStock(Long id, Integer quantity) {
        log.info("Stok serbest bırakılıyor: id={}, quantity={}", id, quantity);
        Stock stock = getStockById(id);
        
        if (stock.getReservedQuantity() < quantity) {
            log.warn("Rezerve edilmiş stoktan fazla serbest bırakılamaz: id={}, reserved={}, requested={}", 
                    id, stock.getReservedQuantity(), quantity);
            throw new InsufficientStockException("Rezerve edilmiş stoktan fazla serbest bırakılamaz. Rezerve: " + 
                    stock.getReservedQuantity() + ", İstenen: " + quantity);
        }
        
        stock.setReservedQuantity(stock.getReservedQuantity() - quantity);
        Stock updatedStock = stockRepository.save(stock);
        log.info("Stok başarıyla serbest bırakıldı: id={}, reservedQuantity={}", 
                updatedStock.getId(), updatedStock.getReservedQuantity());
        return updatedStock;
    }

    @Override
    @CacheEvict(value = "stocks", allEntries = true)
    public void deleteStock(Long id) {
        log.info("Stok siliniyor: id={}", id);
        if (!stockRepository.existsById(id)) {
            log.warn("Silinecek stok bulunamadı: id={}", id);
            throw new ResourceNotFoundException("Stok", id);
        }
        stockRepository.deleteById(id);
        log.info("Stok başarıyla silindi: id={}", id);
    }
}
