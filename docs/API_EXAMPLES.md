# E-Ticaret API - Örnek İstekler ve Akış

## 🎯 Proje Ne İşe Yarar?

Bu proje, **mikroservis mimarisi** kullanılarak geliştirilmiş bir **e-ticaret sistemi**dir. Müşteriler ürün satın alabilir, stok takibi yapılır ve siparişler yönetilir.

## 🔄 Genel Akış

```
1. Müşteri → API Gateway (8090) → User Service → Kullanıcı kaydı/girişi
2. Müşteri → API Gateway → Product Service → Ürünleri görüntüleme
3. Müşteri → API Gateway → Stock Service → Stok kontrolü
4. Müşteri → API Gateway → Order Service → Sipariş oluşturma
   └─ Order Service → User Service (Feign) → Kullanıcı doğrulama
   └─ Order Service → Product Service (Feign) → Ürün bilgisi
   └─ Order Service → Stock Service (Feign) → Stok rezervasyonu
   └─ Order Service → Kafka → Sipariş event'i gönder
```

## 📍 API Gateway Base URL

**Tüm istekler API Gateway üzerinden yapılır:**
- Base URL: `http://localhost:8090`
- Swagger UI: Her servis için ayrı (doğrudan servis portlarından)

---

## 📝 Örnek İstekler (Sırasıyla)

### 1️⃣ USER SERVICE - Kullanıcı Yönetimi

#### 1.1. Yeni Kullanıcı Oluştur
```http
POST http://localhost:8090/api/users
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+905551234567",
  "address": "İstanbul, Türkiye"
}
```

**Response:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+905551234567",
  "address": "İstanbul, Türkiye"
}
```
⚠️ **Not:** Password response'da döndürülmez (güvenlik)

#### 1.2. Kullanıcı Bilgisi Getir
```http
GET http://localhost:8090/api/users/507f1f77bcf86cd799439011
```

#### 1.3. Tüm Kullanıcıları Listele
```http
GET http://localhost:8090/api/users
```

#### 1.4. Kullanıcı Güncelle
```http
PUT http://localhost:8090/api/users/507f1f77bcf86cd799439011
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john.updated@example.com",
  "firstName": "John",
  "lastName": "Doe Updated",
  "phone": "+905551234567",
  "address": "Ankara, Türkiye"
}
```

#### 1.5. Kullanıcı Sil
```http
DELETE http://localhost:8090/api/users/507f1f77bcf86cd799439011
```

---

### 2️⃣ PRODUCT SERVICE - Ürün Yönetimi

#### 2.1. Yeni Ürün Oluştur
```http
POST http://localhost:8090/api/products
Content-Type: application/json

{
  "name": "iPhone 15 Pro",
  "description": "Apple'ın en yeni flagship telefonu",
  "price": 45000.00,
  "category": "Elektronik",
  "imageUrl": "https://example.com/iphone15.jpg"
}
```

**Response:**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "name": "iPhone 15 Pro",
  "description": "Apple'ın en yeni flagship telefonu",
  "price": 45000.00,
  "category": "Elektronik",
  "imageUrl": "https://example.com/iphone15.jpg"
}
```

#### 2.2. Ürün Bilgisi Getir
```http
GET http://localhost:8090/api/products/507f1f77bcf86cd799439012
```

#### 2.3. Tüm Ürünleri Listele
```http
GET http://localhost:8090/api/products
```

#### 2.4. Kategoriye Göre Ürünleri Listele
```http
GET http://localhost:8090/api/products?category=Elektronik
```

#### 2.5. İsme Göre Ürün Ara
```http
GET http://localhost:8090/api/products?name=iPhone
```

#### 2.6. Ürün Güncelle
```http
PUT http://localhost:8090/api/products/507f1f77bcf86cd799439012
Content-Type: application/json

{
  "name": "iPhone 15 Pro Max",
  "description": "Apple'ın en yeni flagship telefonu - Güncellendi",
  "price": 50000.00,
  "category": "Elektronik",
  "imageUrl": "https://example.com/iphone15max.jpg"
}
```

#### 2.7. Ürün Sil
```http
DELETE http://localhost:8090/api/products/507f1f77bcf86cd799439012
```

---

### 3️⃣ STOCK SERVICE - Stok Yönetimi

#### 3.1. Ürün İçin Stok Kaydı Oluştur
```http
POST http://localhost:8090/api/stocks
Content-Type: application/json

{
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 100,
  "reservedQuantity": 0,
  "location": "İstanbul Depo"
}
```

**Response:**
```json
{
  "id": "507f1f77bcf86cd799439013",
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 100,
  "reservedQuantity": 0,
  "availableQuantity": 100,
  "location": "İstanbul Depo"
}
```

#### 3.2. Stok Bilgisi Getir
```http
GET http://localhost:8090/api/stocks/507f1f77bcf86cd799439013
```

#### 3.3. Ürüne Göre Stok Getir
```http
GET http://localhost:8090/api/stocks/product/507f1f77bcf86cd799439012
```

#### 3.4. Tüm Stokları Listele
```http
GET http://localhost:8090/api/stocks
```

#### 3.5. Stok Rezerve Et
```http
POST http://localhost:8090/api/stocks/507f1f77bcf86cd799439013/reserve?quantity=5
```

#### 3.6. Stok Rezervasyonunu Serbest Bırak
```http
POST http://localhost:8090/api/stocks/507f1f77bcf86cd799439013/release?quantity=5
```

#### 3.7. Stok Güncelle
```http
PUT http://localhost:8090/api/stocks/507f1f77bcf86cd799439013
Content-Type: application/json

{
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 150,
  "reservedQuantity": 10,
  "location": "Ankara Depo"
}
```

---

### 4️⃣ ORDER SERVICE - Sipariş Yönetimi

#### 4.1. Yeni Sipariş Oluştur (EN ÖNEMLİ - Tüm Servisleri Kullanır)
```http
POST http://localhost:8090/api/orders
Content-Type: application/json

{
  "userId": "507f1f77bcf86cd799439011",
  "items": [
    {
      "productId": "507f1f77bcf86cd799439012",
      "productName": "iPhone 15 Pro",
      "quantity": 2,
      "price": 45000.00
    }
  ],
  "shippingAddress": "İstanbul, Türkiye"
}
```

**Bu istek şunları yapar:**
1. ✅ User Service'e istek atar → Kullanıcı doğrulama
2. ✅ Product Service'e istek atar → Ürün bilgilerini alır
3. ✅ Stock Service'e istek atar → Stok kontrolü ve rezervasyon
4. ✅ Toplam tutarı hesaplar
5. ✅ Siparişi MongoDB'ye kaydeder
6. ✅ Kafka'ya `order-created` event'i gönderir

**Response:**
```json
{
  "id": "507f1f77bcf86cd799439014",
  "userId": "507f1f77bcf86cd799439011",
  "items": [
    {
      "productId": "507f1f77bcf86cd799439012",
      "productName": "iPhone 15 Pro",
      "quantity": 2,
      "price": 45000.00
    }
  ],
  "totalAmount": 90000.00,
  "status": "PENDING",
  "shippingAddress": "İstanbul, Türkiye",
  "createdAt": "2025-12-23T12:00:00",
  "updatedAt": "2025-12-23T12:00:00"
}
```

#### 4.2. Sipariş Bilgisi Getir
```http
GET http://localhost:8090/api/orders/507f1f77bcf86cd799439014
```

#### 4.3. Kullanıcının Siparişlerini Listele
```http
GET http://localhost:8090/api/orders/user/507f1f77bcf86cd799439011
```

#### 4.4. Tüm Siparişleri Listele
```http
GET http://localhost:8090/api/orders
```

#### 4.5. Sipariş Durumunu Güncelle
```http
PUT http://localhost:8090/api/orders/507f1f77bcf86cd799439014/status?status=CONFIRMED
```

**Status Değerleri:**
- `PENDING` - Beklemede
- `CONFIRMED` - Onaylandı
- `PROCESSING` - İşleniyor
- `SHIPPED` - Kargoya verildi
- `DELIVERED` - Teslim edildi
- `CANCELLED` - İptal edildi

#### 4.6. Sipariş İptal Et
```http
DELETE http://localhost:8090/api/orders/507f1f77bcf86cd799439014
```

**Bu işlem:**
- ✅ Rezerve edilmiş stokları geri bırakır (Stock Service)
- ✅ Sipariş durumunu `CANCELLED` yapar
- ✅ Kafka'ya `order-cancelled` event'i gönderir

---

## 🔄 Tam Senaryo: Sipariş Oluşturma Akışı

### Adım 1: Kullanıcı Oluştur
```http
POST http://localhost:8090/api/users
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```
**Response:** `userId = "507f1f77bcf86cd799439011"`

### Adım 2: Ürün Oluştur
```http
POST http://localhost:8090/api/products
{
  "name": "iPhone 15 Pro",
  "price": 45000.00,
  "category": "Elektronik"
}
```
**Response:** `productId = "507f1f77bcf86cd799439012"`

### Adım 3: Stok Kaydı Oluştur
```http
POST http://localhost:8090/api/stocks
{
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 100,
  "location": "İstanbul Depo"
}
```
**Response:** `stockId = "507f1f77bcf86cd799439013"`

### Adım 4: Sipariş Oluştur (Tüm Servisleri Kullanır)
```http
POST http://localhost:8090/api/orders
{
  "userId": "507f1f77bcf86cd799439011",
  "items": [
    {
      "productId": "507f1f77bcf86cd799439012",
      "productName": "iPhone 15 Pro",
      "quantity": 2,
      "price": 45000.00
    }
  ],
  "shippingAddress": "İstanbul, Türkiye"
}
```

**Bu istek sırasında:**
1. ✅ User Service → Kullanıcı doğrulandı
2. ✅ Product Service → Ürün bilgisi alındı
3. ✅ Stock Service → Stok kontrolü yapıldı ve 2 adet rezerve edildi
4. ✅ Toplam tutar hesaplandı: 90,000 TL
5. ✅ Sipariş kaydedildi
6. ✅ Kafka'ya event gönderildi

---

## 🛠️ Swagger UI Erişimi

Her servis için ayrı Swagger UI:

- **User Service:** `http://localhost:9011/swagger-ui/index.html`
- **Product Service:** `http://localhost:9012/swagger-ui/index.html`
- **Stock Service:** `http://localhost:9013/swagger-ui/index.html`
- **Order Service:** `http://localhost:9014/swagger-ui/index.html`

**Not:** Swagger UI'da Server URL'ini şu şekilde ayarlayın:
- `http://localhost:8090/api/users` (User Service için)
- `http://localhost:8090/api/products` (Product Service için)
- vb.

---

## 📊 Veri Akışı

```
Client Request
    ↓
API Gateway (8090)
    ↓
Microservice (9011-9014)
    ↓
Service Layer (Interface + Implementation)
    ↓
Mapper (DTO ↔ Entity)
    ↓
Repository (MongoDB)
    ↓
Database (MongoDB)
```

**Order Service özel durumu:**
```
Order Service
    ↓
Feign Client → User Service (kullanıcı doğrulama)
Feign Client → Product Service (ürün bilgisi)
Feign Client → Stock Service (stok rezervasyonu)
    ↓
Kafka (event gönderimi)
```

---

## 🎯 Özet

Bu proje:
- ✅ Mikroservis mimarisi kullanır
- ✅ API Gateway üzerinden tek noktadan erişim sağlar
- ✅ Servisler arası iletişim Feign Client ile yapılır
- ✅ Event-driven mimari (Kafka) kullanır
- ✅ DTO katmanı ile güvenli veri transferi sağlar
- ✅ Interface + Implementation pattern kullanır
- ✅ Docker ve Kubernetes ile deploy edilebilir


