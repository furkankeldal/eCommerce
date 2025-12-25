# E-Ticaret Mikroservis Sistemi - Dokümantasyon

## 📋 İçindekiler

1. [Proje Hakkında](#proje-hakkında)
2. [Mimari Yapı](#mimari-yapı)
3. [Teknolojiler](#teknolojiler)
4. [Kurulum ve Çalıştırma](#kurulum-ve-çalıştırma)
5. [API Dokümantasyonu](#api-dokümantasyonu)
6. [Servis Detayları](#servis-detayları)
7. [Veritabanı Yapısı](#veritabanı-yapısı)
8. [Troubleshooting](#troubleshooting)

---

## 🎯 Proje Hakkında

Bu proje, mikroservis mimarisi kullanılarak geliştirilmiş bir e-ticaret sistemidir. Sistem, birbirinden bağımsız çalışan 5 mikroservisten oluşmaktadır:

- **API Gateway**: Tüm isteklerin yönlendirildiği merkezi giriş noktası
- **User Service**: Kullanıcı yönetimi
- **Product Service**: Ürün yönetimi
- **Stock Service**: Stok yönetimi (Redis cache ile)
- **Order Service**: Sipariş yönetimi (Kafka ile)

---

## 🏗️ Mimari Yapı

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ API Gateway │ (Port: 8090)
└──────┬──────┘
       │
   ┌───┴───┬─────────┬─────────┐
   │       │         │         │
   ▼       ▼         ▼         ▼
┌─────┐ ┌──────┐ ┌──────┐ ┌──────┐
│User │ │Product│ │Stock │ │Order │
│9011 │ │ 9012 │ │ 9013 │ │ 9014 │
└──┬──┘ └───┬──┘ └───┬──┘ └───┬──┘
   │        │        │        │
   ▼        ▼        ▼        ▼
┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐
│MongoDB│ │MongoDB│ │MongoDB│ │MongoDB│
│      │ │      │ │ Redis │ │ Kafka │
└──────┘ └──────┘ └──────┘ └──────┘
```

### Service Discovery

- **Docker Compose**: Docker DNS kullanılır (container adları ile)
- **Kubernetes**: Kubernetes Service Discovery (DNS + Service'ler)

---

## 🛠️ Teknolojiler

| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| Java | 17 | Programlama dili |
| Spring Boot | 3.2.0 | Framework |
| Spring Cloud | 2023.0.0 | Mikroservis araçları |
| MongoDB | 7 | NoSQL veritabanı |
| Redis | 7 | Cache ve session yönetimi |
| Kafka | Latest | Mesajlaşma sistemi |
| Docker | - | Containerization |
| Kubernetes | - | Orchestration |
| Lombok | - | Code generation |
| SpringDoc OpenAPI | 2.3.0 | API dokümantasyonu |

---

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler

- Java 17+
- Maven 3.6+
- Docker & Docker Compose (opsiyonel)
- Kubernetes cluster (opsiyonel)
- MongoDB (Docker ile otomatik)
- Redis (Docker ile otomatik)
- Kafka (Docker ile otomatik)

### Yöntem 1: Docker Compose ile Çalıştırma

#### Adım 1: Projeyi Klonlayın

```bash
git clone <repository-url>
cd eTicaret
```

#### Adım 2: Projeyi Build Edin

```bash
mvn clean package
```

#### Adım 3: Docker Container'ları Başlatın

```bash
docker-compose up -d
```

Bu komut şunları başlatır:
- MongoDB (Port: 27017)
- Redis (Port: 6379)
- Zookeeper (Port: 2181)
- Kafka (Port: 9092)
- API Gateway (Port: 8090)
- User Service (Port: 9011)
- Product Service (Port: 9012)
- Stock Service (Port: 9013)
- Order Service (Port: 9014)

#### Adım 4: Servislerin Durumunu Kontrol Edin

```bash
docker-compose ps
```

#### Adım 5: Logları İzleyin

```bash
docker-compose logs -f
```

### Yöntem 2: Kubernetes ile Çalıştırma

#### Ön Gereksinimler

- Kubernetes cluster çalışıyor olmalı
- kubectl yüklü ve yapılandırılmış olmalı

#### Adım 1: Namespace Oluşturun

```bash
kubectl apply -f kubernetes/namespace.yaml
```

#### Adım 2: ConfigMap ve Secret Oluşturun

```bash
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/secret.yaml
```

#### Adım 3: Veritabanı ve Mesajlaşma Servislerini Başlatın

```bash
kubectl apply -f kubernetes/mongodb-deployment.yaml
kubectl apply -f kubernetes/redis-deployment.yaml
kubectl apply -f kubernetes/kafka-deployment.yaml
```

#### Adım 4: Servisleri Başlatın

```bash
kubectl apply -f kubernetes/user-service-deployment.yaml
kubectl apply -f kubernetes/product-service-deployment.yaml
kubectl apply -f kubernetes/stock-service-deployment.yaml
kubectl apply -f kubernetes/order-service-deployment.yaml
kubectl apply -f kubernetes/api-gateway-deployment.yaml
```

#### Adım 5: Ingress Oluşturun

```bash
kubectl apply -f kubernetes/ingress.yaml
```

#### Adım 6: Pod Durumunu Kontrol Edin

```bash
kubectl get pods -n ecommerce
kubectl get services -n ecommerce
```

### Yöntem 3: Yerel Geliştirme Ortamı

#### Adım 1: MongoDB, Redis ve Kafka'yı Başlatın

```bash
docker-compose up -d mongodb redis zookeeper kafka
```

#### Adım 2: Servisleri Sırayla Başlatın

Her servisi ayrı terminal'de çalıştırın:

```bash
# Terminal 1 - User Service
cd user-service
mvn spring-boot:run

# Terminal 2 - Product Service
cd product-service
mvn spring-boot:run

# Terminal 3 - Stock Service
cd stock-service
mvn spring-boot:run

# Terminal 4 - Order Service
cd order-service
mvn spring-boot:run

# Terminal 5 - API Gateway
cd api-gateway
mvn spring-boot:run
```

---

## 📚 API Dokümantasyonu

### Swagger UI Erişimi

Her servis kendi Swagger UI'ına sahiptir:

| Servis | Swagger UI URL |
|--------|----------------|
| User Service | http://localhost:9011/swagger-ui.html |
| Product Service | http://localhost:9012/swagger-ui.html |
| Stock Service | http://localhost:9013/swagger-ui.html |
| Order Service | http://localhost:9014/swagger-ui.html |

### API Gateway Üzerinden Erişim

Tüm API'ler API Gateway üzerinden erişilebilir:

- **Base URL**: `http://localhost:8090`
- **User Service**: `http://localhost:8090/api/users`
- **Product Service**: `http://localhost:8090/api/products`
- **Stock Service**: `http://localhost:8090/api/stocks`
- **Order Service**: `http://localhost:8090/api/orders`

---

## 🔧 Servis Detayları

### 1. User Service (Port: 9011)

**Sorumluluklar:**
- Kullanıcı kaydı
- Kullanıcı bilgilerini güncelleme
- Kullanıcı silme
- Kullanıcı listeleme

**Veritabanı:** MongoDB (`ecommerce_users`)

**API Endpoints:**
- `POST /users` - Yeni kullanıcı oluştur
- `GET /users/{id}` - Kullanıcı bilgisi getir
- `GET /users` - Tüm kullanıcıları listele
- `PUT /users/{id}` - Kullanıcı güncelle
- `DELETE /users/{id}` - Kullanıcı sil

### 2. Product Service (Port: 9012)

**Sorumluluklar:**
- Ürün ekleme/düzenleme/silme
- Ürün arama (kategori/isim)
- Ürün listeleme

**Veritabanı:** MongoDB (`ecommerce_products`)

**API Endpoints:**
- `POST /products` - Yeni ürün oluştur
- `GET /products/{id}` - Ürün bilgisi getir
- `GET /products?category={category}` - Kategoriye göre listele
- `GET /products?name={name}` - İsme göre ara
- `GET /products` - Tüm ürünleri listele
- `PUT /products/{id}` - Ürün güncelle
- `DELETE /products/{id}` - Ürün sil

### 3. Stock Service (Port: 9013)

**Sorumluluklar:**
- Stok takibi
- Stok rezervasyonu
- Stok serbest bırakma
- Redis cache ile performans optimizasyonu

**Veritabanı:** MongoDB (`ecommerce_stocks`) + Redis (Cache)

**API Endpoints:**
- `POST /stocks` - Stok kaydı oluştur
- `GET /stocks/{id}` - Stok bilgisi getir
- `GET /stocks/product/{productId}` - Ürüne göre stok getir
- `GET /stocks` - Tüm stokları listele
- `PUT /stocks/{id}` - Stok güncelle
- `POST /stocks/{id}/reserve?quantity={qty}` - Stok rezerve et
- `POST /stocks/{id}/release?quantity={qty}` - Stok serbest bırak
- `DELETE /stocks/{id}` - Stok sil

### 4. Order Service (Port: 9014)

**Sorumluluklar:**
- Sipariş oluşturma
- Sipariş durumu güncelleme
- Sipariş iptal etme
- Kafka ile event yayınlama

**Veritabanı:** MongoDB (`ecommerce_orders`)

**Kafka Topics:**
- `order-created` - Yeni sipariş oluşturulduğunda
- `order-status-updated` - Sipariş durumu güncellendiğinde
- `order-cancelled` - Sipariş iptal edildiğinde

**API Endpoints:**
- `POST /orders` - Yeni sipariş oluştur
- `GET /orders/{id}` - Sipariş bilgisi getir
- `GET /orders/user/{userId}` - Kullanıcının siparişlerini listele
- `GET /orders` - Tüm siparişleri listele
- `PUT /orders/{id}/status?status={status}` - Sipariş durumu güncelle
- `DELETE /orders/{id}` - Sipariş iptal et

**Sipariş Durumları:**
- `PENDING` - Beklemede
- `CONFIRMED` - Onaylandı
- `PROCESSING` - İşleniyor
- `SHIPPED` - Kargoya verildi
- `DELIVERED` - Teslim edildi
- `CANCELLED` - İptal edildi

### 5. API Gateway (Port: 8090)

**Sorumluluklar:**
- Tüm istekleri yönlendirme
- Load balancing
- Request routing

**Routing Yapısı:**
- `/api/users/**` → User Service (9011)
- `/api/products/**` → Product Service (9012)
- `/api/stocks/**` → Stock Service (9013)
- `/api/orders/**` → Order Service (9014)

---

## 💾 Veritabanı Yapısı

### MongoDB Collections

#### users
```json
{
  "_id": "ObjectId",
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string",
  "address": "string",
  "phone": "string"
}
```

#### products
```json
{
  "_id": "ObjectId",
  "name": "string",
  "description": "string",
  "price": "BigDecimal",
  "category": "string",
  "imageUrl": "string",
  "stockQuantity": "integer"
}
```

#### stocks
```json
{
  "_id": "ObjectId",
  "productId": "string",
  "quantity": "integer",
  "reservedQuantity": "integer",
  "location": "string"
}
```

#### orders
```json
{
  "_id": "ObjectId",
  "userId": "string",
  "items": [
    {
      "productId": "string",
      "productName": "string",
      "quantity": "integer",
      "price": "BigDecimal"
    }
  ],
  "totalAmount": "BigDecimal",
  "status": "OrderStatus",
  "shippingAddress": "string",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

---

## 🧪 Test Senaryoları

### 1. Kullanıcı Oluşturma ve Sipariş Verme

```bash
# 1. Kullanıcı oluştur
curl -X POST http://localhost:8090/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'

# 2. Ürün oluştur
curl -X POST http://localhost:8090/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Ürün",
    "price": 100.00,
    "category": "Elektronik",
    "stockQuantity": 50
  }'

# 3. Stok oluştur
curl -X POST http://localhost:8090/api/stocks \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "<product-id>",
    "quantity": 50,
    "reservedQuantity": 0
  }'

# 4. Sipariş oluştur
curl -X POST http://localhost:8090/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "<user-id>",
    "items": [{
      "productId": "<product-id>",
      "productName": "Test Ürün",
      "quantity": 2,
      "price": 100.00
    }],
    "totalAmount": 200.00,
    "shippingAddress": "Test Adres"
  }'
```

---

## 🔍 Troubleshooting

### Problem: Servisler birbirini bulamıyor

**Çözüm:**
- Docker Compose: Container'ların aynı network'te olduğundan emin olun
- Kubernetes: Service'lerin doğru namespace'te olduğunu kontrol edin
- Port'ların doğru yapılandırıldığını kontrol edin

### Problem: MongoDB bağlantı hatası

**Çözüm:**
```bash
# MongoDB container'ının çalıştığını kontrol edin
docker ps | grep mongodb

# MongoDB loglarını kontrol edin
docker logs mongodb
```

### Problem: Kafka mesajları gönderilmiyor

**Çözüm:**
```bash
# Kafka ve Zookeeper'ın çalıştığını kontrol edin
docker ps | grep kafka
docker ps | grep zookeeper

# Kafka topic'lerini kontrol edin
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Problem: Redis cache çalışmıyor

**Çözüm:**
```bash
# Redis container'ının çalıştığını kontrol edin
docker ps | grep redis

# Redis'e bağlanıp test edin
docker exec -it redis redis-cli ping
```

### Problem: Swagger UI açılmıyor

**Çözüm:**
- Servisin çalıştığından emin olun
- Port'un doğru olduğunu kontrol edin
- `http://localhost:9011/swagger-ui.html` formatını kullanın

---

## 📝 Loglama

Tüm servisler düzenli log formatı kullanır:

```
%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

**Örnek log çıktısı:**
```
2024-01-15 10:30:45 [http-nio-9011-exec-1] INFO  c.e.user.controller.UserController - User created with ID: 507f1f77bcf86cd799439011
```

---

## 🔐 Güvenlik Notları

⚠️ **ÖNEMLİ:** Bu proje geliştirme amaçlıdır. Production ortamda kullanmadan önce:

1. Authentication/Authorization ekleyin (JWT, OAuth2)
2. HTTPS kullanın
3. Password'leri hash'leyin (BCrypt)
4. Rate limiting ekleyin
5. Input validation'ı güçlendirin
6. Secret'ları güvenli şekilde yönetin

---

## 📞 Destek

Sorularınız için:
- GitHub Issues açabilirsiniz
- Dokümantasyonu kontrol edebilirsiniz
- Swagger UI'dan API'leri test edebilirsiniz

---

## 📄 Lisans

Bu proje eğitim amaçlıdır.

