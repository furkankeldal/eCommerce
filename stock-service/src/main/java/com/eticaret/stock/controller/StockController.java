package com.eticaret.stock.controller;

import com.eticaret.stock.dto.StockRequestDTO;
import com.eticaret.stock.dto.StockResponseDTO;
import com.eticaret.stock.mapper.StockMapper;
import com.eticaret.stock.model.Stock;
import com.eticaret.stock.service.IStockService;
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
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock Service", description = "Stok yönetimi API'leri - Stok oluşturma, güncelleme, rezervasyon ve sorgulama işlemleri")
public class StockController {
    
    private final IStockService stockService;
    private final StockMapper stockMapper;

    @Operation(summary = "Yeni stok kaydı oluştur", description = "Belirtilen ürün için yeni bir stok kaydı oluşturur. Her ürün için sadece bir stok kaydı olabilir.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Stok kaydı başarıyla oluşturuldu"),
        @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi"),
        @ApiResponse(responseCode = "409", description = "Bu ürün için zaten bir stok kaydı mevcut")
    })
    @PostMapping
    public ResponseEntity<StockResponseDTO> createStock(@Valid @RequestBody StockRequestDTO stockRequestDTO) {
        log.info("POST /stocks - Yeni stok kaydı oluşturma isteği alındı: productId={}", stockRequestDTO.getProductId());
        try {
            Stock stock = stockMapper.toEntity(stockRequestDTO);
            Stock createdStock = stockService.createStock(stock);
            StockResponseDTO responseDTO = stockMapper.toResponseDTO(createdStock);
            log.info("POST /stocks - Stok kaydı başarıyla oluşturuldu: id={}", responseDTO.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("POST /stocks - Stok kaydı oluşturulurken hata: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Stok bilgilerini getir", description = "ID'ye göre stok bilgilerini getirir.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stok bilgileri başarıyla getirildi"),
        @ApiResponse(responseCode = "404", description = "Stok bulunamadı")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StockResponseDTO> getStockById(
            @Parameter(description = "Stok ID'si", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /stocks/{} - Stok getirme isteği alındı", id);
        try {
            Stock stock = stockService.getStockById(id);
            StockResponseDTO responseDTO = stockMapper.toResponseDTO(stock);
            log.info("GET /stocks/{} - Stok başarıyla getirildi", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("GET /stocks/{} - Stok getirilirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Ürün ID'sine göre stok bilgilerini getir", description = "Belirtilen ürün ID'sine ait stok bilgilerini getirir.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stok bilgileri başarıyla getirildi"),
        @ApiResponse(responseCode = "404", description = "Stok bulunamadı")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<StockResponseDTO> getStockByProductId(
            @Parameter(description = "Ürün ID'si (MongoDB ObjectId)", required = true, example = "694bc5213849350c4279c69f")
            @PathVariable String productId) {
        log.info("GET /stocks/product/{} - Ürün ID'sine göre stok getirme isteği alındı", productId);
        try {
            Stock stock = stockService.getStockByProductId(productId);
            StockResponseDTO responseDTO = stockMapper.toResponseDTO(stock);
            log.info("GET /stocks/product/{} - Stok başarıyla getirildi", productId);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("GET /stocks/product/{} - Stok getirilirken hata: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Tüm stok kayıtlarını listele", description = "Sistemdeki tüm stok kayıtlarını listeler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stok listesi başarıyla getirildi")
    })
    @GetMapping
    public ResponseEntity<List<StockResponseDTO>> getAllStocks() {
        log.info("GET /stocks - Tüm stok kayıtlarını getirme isteği alındı");
        try {
            List<Stock> stocks = stockService.getAllStocks();
            List<StockResponseDTO> responseDTOs = stocks.stream()
                    .map(stockMapper::toResponseDTO)
                    .collect(Collectors.toList());
            log.info("GET /stocks - {} stok kaydı başarıyla getirildi", responseDTOs.size());
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("GET /stocks - Stok kayıtları getirilirken hata: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Stok bilgilerini güncelle", description = "Mevcut stok bilgilerini günceller.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stok bilgileri başarıyla güncellendi"),
        @ApiResponse(responseCode = "404", description = "Stok bulunamadı")
    })
    @PutMapping("/{id}")
    public ResponseEntity<StockResponseDTO> updateStock(
            @Parameter(description = "Stok ID'si", required = true, example = "1")
            @PathVariable Long id, 
            @Valid @RequestBody StockRequestDTO stockRequestDTO) {
        log.info("PUT /stocks/{} - Stok güncelleme isteği alındı", id);
        try {
            Stock existingStock = stockService.getStockById(id);
            stockMapper.updateEntityFromDTO(existingStock, stockRequestDTO);
            Stock updatedStock = stockService.updateStock(id, existingStock);
            StockResponseDTO responseDTO = stockMapper.toResponseDTO(updatedStock);
            log.info("PUT /stocks/{} - Stok başarıyla güncellendi", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("PUT /stocks/{} - Stok güncellenirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Stok rezerve et", description = "Belirtilen miktarda stoku rezerve eder. Rezerve edilen stok, sipariş işlemi tamamlanana kadar kullanılamaz.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stok başarıyla rezerve edildi"),
        @ApiResponse(responseCode = "404", description = "Stok bulunamadı"),
        @ApiResponse(responseCode = "400", description = "Yetersiz stok miktarı")
    })
    @PostMapping("/{id}/reserve")
    public ResponseEntity<StockResponseDTO> reserveStock(
            @Parameter(description = "Stok ID'si", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Rezerve edilecek miktar", required = true, example = "5")
            @RequestParam Integer quantity) {
        log.info("POST /stocks/{}/reserve - Stok rezerve etme isteği alındı: quantity={}", id, quantity);
        try {
            Stock stock = stockService.reserveStock(id, quantity);
            StockResponseDTO responseDTO = stockMapper.toResponseDTO(stock);
            log.info("POST /stocks/{}/reserve - Stok başarıyla rezerve edildi", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("POST /stocks/{}/reserve - Stok rezerve edilirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Stok serbest bırak", description = "Daha önce rezerve edilmiş stoku serbest bırakır. Sipariş iptal edildiğinde kullanılır.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stok başarıyla serbest bırakıldı"),
        @ApiResponse(responseCode = "404", description = "Stok bulunamadı"),
        @ApiResponse(responseCode = "400", description = "Serbest bırakılacak miktar rezerve edilmiş miktardan fazla")
    })
    @PostMapping("/{id}/release")
    public ResponseEntity<StockResponseDTO> releaseStock(
            @Parameter(description = "Stok ID'si", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Serbest bırakılacak miktar", required = true, example = "5")
            @RequestParam Integer quantity) {
        log.info("POST /stocks/{}/release - Stok serbest bırakma isteği alındı: quantity={}", id, quantity);
        try {
            Stock stock = stockService.releaseStock(id, quantity);
            StockResponseDTO responseDTO = stockMapper.toResponseDTO(stock);
            log.info("POST /stocks/{}/release - Stok başarıyla serbest bırakıldı", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("POST /stocks/{}/release - Stok serbest bırakılırken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Stok kaydını sil", description = "ID'ye göre stok kaydını sistemden siler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Stok kaydı başarıyla silindi"),
        @ApiResponse(responseCode = "404", description = "Stok bulunamadı")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(
            @Parameter(description = "Stok ID'si", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /stocks/{} - Stok silme isteği alındı", id);
        try {
            stockService.deleteStock(id);
            log.info("DELETE /stocks/{} - Stok başarıyla silindi", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("DELETE /stocks/{} - Stok silinirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}

