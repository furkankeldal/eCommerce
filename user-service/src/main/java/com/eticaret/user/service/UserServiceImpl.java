package com.eticaret.user.service;

import com.eticaret.user.exception.DuplicateResourceException;
import com.eticaret.user.exception.ResourceNotFoundException;
import com.eticaret.user.model.User;
import com.eticaret.user.repository.UserRepository;
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
public class UserServiceImpl implements IUserService {
    
    private final UserRepository userRepository;
    private final SequenceService sequenceService;

    @Override
    public User createUser(User user) {
        log.info("Yeni kullanıcı oluşturuluyor: username={}, email={}", user.getUsername(), user.getEmail());
        
        // Kullanıcı adı kontrolü
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.warn("Kullanıcı adı zaten mevcut: username={}", user.getUsername());
            throw new DuplicateResourceException("Kullanıcı adı", user.getUsername());
        }
        
        // Email kontrolü
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Email adresi zaten mevcut: email={}", user.getEmail());
            throw new DuplicateResourceException("Email adresi", user.getEmail());
        }
        
        // Telefon kontrolü
        if (user.getPhone() != null && !user.getPhone().isEmpty() && 
            userRepository.findByPhone(user.getPhone()).isPresent()) {
            log.warn("Telefon numarası zaten mevcut: phone={}", user.getPhone());
            throw new DuplicateResourceException("Telefon numarası", user.getPhone());
        }
        
        // Sıralı ID oluştur ve set et
        try {
            Long newId = sequenceService.getNextSequence("user_sequence");
            user.setId(newId);
            log.debug("Kullanıcı ID'si oluşturuldu: id={}", newId);
        } catch (Exception e) {
            log.error("Kullanıcı ID'si oluşturulamadı: {}", e.getMessage(), e);
            throw new RuntimeException("Kullanıcı ID'si oluşturulamadı: " + e.getMessage());
        }
        
        // ID'nin kesinlikle set edildiğinden emin ol
        if (user.getId() == null) {
            log.error("Kullanıcı ID'si null: username={}", user.getUsername());
            throw new RuntimeException("Kullanıcı ID'si oluşturulamadı");
        }
        
        User savedUser = userRepository.save(user);
        log.info("Kullanıcı başarıyla oluşturuldu: id={}, username={}", savedUser.getId(), savedUser.getUsername());
        return savedUser;
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        log.debug("Kullanıcı getiriliyor: id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Kullanıcı bulunamadı: id={}", id);
                    return new ResourceNotFoundException("Kullanıcı", id);
                });
        log.debug("Kullanıcı bulundu: id={}, username={}", user.getId(), user.getUsername());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Tüm kullanıcılar getiriliyor");
        List<User> users = userRepository.findAll();
        log.info("Toplam {} kullanıcı bulundu", users.size());
        return users;
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    public User updateUser(Long id, User user) {
        log.info("Kullanıcı güncelleniyor: id={}", id);
        User existingUser = getUserById(id);
        
        // Güncelleme öncesi kontrol
        String newUsername = user.getUsername();
        if (!existingUser.getUsername().equals(newUsername) && 
            userRepository.findByUsername(newUsername).isPresent()) {
            log.warn("Güncelleme sırasında kullanıcı adı çakışması: id={}, newUsername={}", id, newUsername);
            throw new DuplicateResourceException("Kullanıcı adı", newUsername);
        }
        
        String newEmail = user.getEmail();
        if (!existingUser.getEmail().equals(newEmail) && 
            userRepository.findByEmail(newEmail).isPresent()) {
            log.warn("Güncelleme sırasında email çakışması: id={}, newEmail={}", id, newEmail);
            throw new DuplicateResourceException("Email adresi", newEmail);
        }
        
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAddress(user.getAddress());
        existingUser.setPhone(user.getPhone());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        
        User updatedUser = userRepository.save(existingUser);
        log.info("Kullanıcı başarıyla güncellendi: id={}, username={}", updatedUser.getId(), updatedUser.getUsername());
        // @CacheEvict: Cache temizlenir, sonraki istekte @Cacheable DB'den okuyup cache'e yazar
        return updatedUser;
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.info("Kullanıcı siliniyor: id={}", id);
        if (!userRepository.existsById(id)) {
            log.warn("Silinecek kullanıcı bulunamadı: id={}", id);
            throw new ResourceNotFoundException("Kullanıcı", id);
        }
        userRepository.deleteById(id);
        log.info("Kullanıcı başarıyla silindi: id={}", id);
    }
}
