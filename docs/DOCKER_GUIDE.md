# Docker ile Çalıştırma Kılavuzu

## 🚀 Hızlı Başlangıç

### Tek Komutla Tüm Sistemi Başlatma

```bash
# 1. Projeyi build et
mvn clean package

# 2. Tüm servisleri başlat (arka planda)
docker-compose up -d

# 3. Servislerin durumunu kontrol et
docker-compose ps

# 4. Logları izle
docker-compose logs -f
```

## 📋 Adım Adım Kurulum

### Adım 1: Projeyi Build Etme

```bash
# Proje kök dizininde
mvn clean package
```

Bu komut tüm servisleri derler ve JAR dosyalarını oluşturur.

### Adım 2: Docker Image'larını Oluşturma

```bash
docker-compose build
```

Veya build ve start'ı birlikte:

```bash
docker-compose up -d --build
```

### Adım 3: Servisleri Başlatma

```bash
# Tüm servisleri başlat
docker-compose up -d

# Belirli bir servisi başlat
docker-compose up -d mongodb
docker-compose up -d user-service
```

### Adım 4: Servis Durumunu Kontrol Etme

```bash
# Tüm container'ların durumu
docker-compose ps

# Belirli bir servisin logları
docker-compose logs user-service

# Tüm logları canlı izle
docker-compose logs -f
```

## 🔍 Servis Kontrolleri

### Servislerin Çalıştığını Doğrulama

```bash
# API Gateway
curl http://localhost:8090/actuator/health

# User Service
curl http://localhost:9011/actuator/health

# Swagger UI'ları kontrol et
# http://localhost:9011/swagger-ui.html
# http://localhost:9012/swagger-ui.html
# http://localhost:9013/swagger-ui.html
# http://localhost:9014/swagger-ui.html
```

### Veritabanı Bağlantılarını Kontrol Etme

```bash
# MongoDB'ye bağlan
docker exec -it mongodb mongosh

# Redis'e bağlan
docker exec -it redis redis-cli ping

# Kafka topic'lerini listele
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:29092
```

## 🛠️ Yaygın Komutlar

### Servisleri Durdurma

```bash
# Tüm servisleri durdur
docker-compose down

# Servisleri durdur ve volume'ları sil
docker-compose down -v
```

### Servisleri Yeniden Başlatma

```bash
# Belirli bir servisi yeniden başlat
docker-compose restart user-service

# Tüm servisleri yeniden başlat
docker-compose restart
```

### Logları İzleme

```bash
# Tüm loglar
docker-compose logs -f

# Belirli servis logları
docker-compose logs -f user-service

# Son 100 satır
docker-compose logs --tail=100 user-service
```

### Container'a Bağlanma

```bash
# Container içine gir
docker exec -it user-service sh

# MongoDB shell
docker exec -it mongodb mongosh

# Redis CLI
docker exec -it redis redis-cli
```

## 🔧 Sorun Giderme

### Problem: Servisler başlamıyor

**Çözüm:**
```bash
# Logları kontrol et
docker-compose logs user-service

# Container'ı yeniden oluştur
docker-compose up -d --force-recreate user-service
```

### Problem: Port çakışması

**Çözüm:**
```bash
# Port'u kullanan process'i bul
netstat -ano | findstr :9011

# docker-compose.yml'de port'u değiştir
```

### Problem: MongoDB bağlantı hatası

**Çözüm:**
```bash
# MongoDB'nin çalıştığını kontrol et
docker ps | grep mongodb

# MongoDB loglarını kontrol et
docker logs mongodb

# MongoDB'yi yeniden başlat
docker-compose restart mongodb
```

### Problem: Kafka mesajları gönderilmiyor

**Çözüm:**
```bash
# Kafka ve Zookeeper'ın çalıştığını kontrol et
docker ps | grep kafka
docker ps | grep zookeeper

# Kafka loglarını kontrol et
docker logs kafka

# Topic oluştur
docker exec -it kafka kafka-topics --create --topic order-created --bootstrap-server localhost:29092 --partitions 1 --replication-factor 1
```

## 📊 Servis Bağımlılıkları

Servislerin başlatılma sırası:

1. **MongoDB** - Veritabanı
2. **Redis** - Cache
3. **Zookeeper** - Kafka için
4. **Kafka** - Mesajlaşma
5. **User Service** - Kullanıcı servisi
6. **Product Service** - Ürün servisi
7. **Stock Service** - Stok servisi
8. **Order Service** - Sipariş servisi
9. **API Gateway** - Gateway servisi

Docker Compose otomatik olarak `depends_on` ile sıralamayı yönetir.

## 🎯 Test Senaryosu

### 1. Sistemin Çalıştığını Test Etme

```bash
# Tüm servislerin çalıştığını kontrol et
docker-compose ps

# API Gateway üzerinden test
curl http://localhost:8090/api/users
```

### 2. Swagger UI ile Test

1. Tarayıcıda aç: `http://localhost:9011/swagger-ui.html`
2. API endpoint'lerini test et
3. Request/Response'ları görüntüle

## 💡 İpuçları

1. **İlk çalıştırmada**: Servislerin tamamen başlaması için 30-60 saniye bekleyin
2. **Log takibi**: `docker-compose logs -f` ile tüm logları canlı izleyin
3. **Performans**: Docker Desktop'ta yeterli kaynak ayırın (RAM: 4GB+, CPU: 2+)
4. **Temizlik**: `docker-compose down -v` ile tüm verileri temizleyin

## 🔄 Güncelleme

Kod değişikliklerinden sonra:

```bash
# Servisleri durdur
docker-compose down

# Yeniden build et
mvn clean package
docker-compose build

# Yeniden başlat
docker-compose up -d
```

---

**Not:** Tüm servisler aynı Docker network'ünde (`ecommerce-network`) çalışır ve birbirlerini container adları ile bulabilirler.

