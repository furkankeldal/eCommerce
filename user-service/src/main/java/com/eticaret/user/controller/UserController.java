package com.eticaret.user.controller;

import com.eticaret.user.client.OrderServiceClient;
import com.eticaret.user.dto.OrderResponseDTO;
import com.eticaret.user.dto.UserRequestDTO;
import com.eticaret.user.dto.UserResponseDTO;
import com.eticaret.user.mapper.UserMapper;
import com.eticaret.user.model.User;
import com.eticaret.user.service.IUserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Service", description = "Kullanıcı yönetimi API'leri - Kullanıcı oluşturma, güncelleme, silme ve sorgulama işlemleri")
public class UserController {
    
    private final IUserService userService;
    private final UserMapper userMapper;
    private final OrderServiceClient orderServiceClient;

    @Operation(summary = "Yeni kullanıcı oluştur", description = "Sistemde yeni bir kullanıcı kaydı oluşturur. Kullanıcı adı, email ve telefon numarası benzersiz olmalıdır.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Kullanıcı başarıyla oluşturuldu"),
        @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi"),
        @ApiResponse(responseCode = "409", description = "Kullanıcı adı, email veya telefon numarası zaten kullanılıyor")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        log.info("POST /users - Yeni kullanıcı oluşturma isteği alındı: username={}", userRequestDTO.getUsername());
        try {
            User user = userMapper.toEntity(userRequestDTO);
            User createdUser = userService.createUser(user);
            UserResponseDTO responseDTO = userMapper.toResponseDTO(createdUser);
            log.info("POST /users - Kullanıcı başarıyla oluşturuldu: id={}", responseDTO.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("POST /users - Kullanıcı oluşturulurken hata: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Kullanıcı bilgilerini getir", description = "ID'ye göre kullanıcı bilgilerini getirir. Kullanıcı bulunamazsa 404 hatası döner.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Kullanıcı bilgileri başarıyla getirildi"),
        @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "Kullanıcı ID'si", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /users/{} - Kullanıcı getirme isteği alındı", id);
        try {
            User user = userService.getUserById(id);
            UserResponseDTO responseDTO = userMapper.toResponseDTO(user);
            log.info("GET /users/{} - Kullanıcı başarıyla getirildi", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("GET /users/{} - Kullanıcı getirilirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Tüm kullanıcıları listele", description = "Sistemdeki tüm kullanıcıları listeler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Kullanıcı listesi başarıyla getirildi")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("GET /users - Tüm kullanıcıları getirme isteği alındı");
        try {
            List<User> users = userService.getAllUsers();
            List<UserResponseDTO> responseDTOs = users.stream()
                    .map(userMapper::toResponseDTO)
                    .collect(Collectors.toList());
            log.info("GET /users - {} kullanıcı başarıyla getirildi", responseDTOs.size());
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("GET /users - Kullanıcılar getirilirken hata: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Kullanıcı bilgilerini güncelle", description = "Mevcut kullanıcı bilgilerini günceller. Kullanıcı adı, email ve telefon numarası benzersiz olmalıdır.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Kullanıcı bilgileri başarıyla güncellendi"),
        @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
        @ApiResponse(responseCode = "409", description = "Kullanıcı adı, email veya telefon numarası zaten kullanılıyor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "Kullanıcı ID'si", required = true, example = "1")
            @PathVariable Long id, 
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        log.info("PUT /users/{} - Kullanıcı güncelleme isteği alındı", id);
        try {
            User existingUser = userService.getUserById(id);
            userMapper.updateEntityFromDTO(existingUser, userRequestDTO);
            User updatedUser = userService.updateUser(id, existingUser);
            UserResponseDTO responseDTO = userMapper.toResponseDTO(updatedUser);
            log.info("PUT /users/{} - Kullanıcı başarıyla güncellendi", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("PUT /users/{} - Kullanıcı güncellenirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Kullanıcıyı sil", description = "ID'ye göre kullanıcıyı sistemden siler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Kullanıcı başarıyla silindi"),
        @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Kullanıcı ID'si", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /users/{} - Kullanıcı silme isteği alındı", id);
        try {
            userService.deleteUser(id);
            log.info("DELETE /users/{} - Kullanıcı başarıyla silindi", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("DELETE /users/{} - Kullanıcı silinirken hata: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Kullanıcının siparişlerini getir", description = "Belirtilen kullanıcıya ait tüm siparişleri getirir. Order Service kullanılamazsa boş liste döner.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sipariş listesi başarıyla getirildi (Order Service kullanılamazsa boş liste döner)"),
        @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(
            @Parameter(description = "Kullanıcı ID'si", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /users/{}/orders - Kullanıcının siparişlerini getirme isteği alındı", id);
        try {
            // Önce kullanıcının var olup olmadığını kontrol et
            userService.getUserById(id);
            
            // Kullanıcının siparişlerini getir
            List<OrderResponseDTO> orders = orderServiceClient.getOrdersByUserId(id);
            if (orders == null) {
                log.warn("GET /users/{}/orders - Order Service null döndü, boş liste döndürülüyor", id);
                orders = new java.util.ArrayList<>();
            }
            log.info("GET /users/{}/orders - {} sipariş başarıyla getirildi", id, orders.size());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            // Feign Client hataları veya bağlantı hataları için boş liste döndür
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            if (errorMessage.contains("Connection refused") || 
                errorMessage.contains("Connection") || 
                errorMessage.contains("Feign") ||
                errorMessage.contains("timeout")) {
                log.warn("GET /users/{}/orders - Order Service'e bağlanılamadı: {} - Boş liste döndürülüyor (Fallback)", id, errorMessage);
            } else {
                log.error("GET /users/{}/orders - Siparişler getirilirken hata: {} - Boş liste döndürülüyor", id, errorMessage, e);
            }
            // Herhangi bir hata durumunda boş liste döndür (graceful degradation)
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }
}

