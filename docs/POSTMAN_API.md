# Postman API Dokümantasyonu

## 🌐 Base URL

**Tüm istekler API Gateway üzerinden yapılır:**
```
http://localhost:8090
```

**ÖNEMLİ:** Port-forward aktif olmalıdır. Kubernetes kullanıyorsanız:
```powershell
kubernetes\scripts\port-forward.bat
```

## ⚠️ ID Yönetimi

**ID'ler otomatik oluşturulur!**

- ✅ **Request Body'de ID göndermeyin** - MongoDB otomatik olarak benzersiz ID oluşturur
- ✅ **Response'da ID alırsınız** - Oluşturulan kayıtların ID'si response'da döner
- ✅ **ID'ler sıralı** - User, Stock, Order için otomatik artan sayılar (1, 2, 3, ...)
- ✅ **Product ID random** - Product için MongoDB ObjectId (random String)

**Örnek:**
```json
// ❌ YANLIŞ - ID göndermeyin
{
  "id": 1,  // ← Bunu göndermeyin! ID otomatik oluşturulur (User, Stock, Order için sıralı; Product için random)
  "username": "johndoe",
  "email": "john@example.com"
}

// ✅ DOĞRU - Sadece veri gönderin
{
  "username": "johndoe",
  "email": "john@example.com"
}
```

---

## 📋 İçindekiler

1. [User Service API](#1-user-service-api)
2. [Product Service API](#2-product-service-api)
3. [Stock Service API](#3-stock-service-api)
4. [Order Service API](#4-order-service-api)

---

## 1. User Service API

### 1.1. Yeni Kullanıcı Oluştur

**Method:** `POST`  
**Path:** `/api/users`  
**Content-Type:** `application/json`

**Not:** ID otomatik oluşturulur, request body'de göndermeyin.

**Request Body:**
```json
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

**Response (201 Created):**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+905551234567",
  "address": "İstanbul, Türkiye"
}
```

**Not:** ID otomatik olarak sıralı şekilde oluşturulur (1, 2, 3, ...)

---

### 1.2. Kullanıcı Bilgisi Getir

**Method:** `GET`  
**Path:** `/api/users/{id}`

**Path Variables:**
- `id`: Kullanıcı ID'si

**Example:**
```
GET http://localhost:8090/api/users/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+905551234567",
  "address": "İstanbul, Türkiye"
}
```

---

### 1.3. Tüm Kullanıcıları Listele

**Method:** `GET`  
**Path:** `/api/users`

**Example:**
```
GET http://localhost:8090/api/users
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+905551234567",
    "address": "İstanbul, Türkiye"
  }
]
```

---

### 1.4. Kullanıcı Bilgilerini Güncelle

**Method:** `PUT`  
**Path:** `/api/users/{id}`  
**Content-Type:** `application/json`

**Path Variables:**
- `id`: Kullanıcı ID'si

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john.updated@example.com",
  "password": "newpassword123",
  "firstName": "John",
  "lastName": "Doe Updated",
  "phone": "+905551234567",
  "address": "Ankara, Türkiye"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john.updated@example.com",
  "firstName": "John",
  "lastName": "Doe Updated",
  "phone": "+905551234567",
  "address": "Ankara, Türkiye"
}
```

---

### 1.5. Kullanıcı Sil

**Method:** `DELETE`  
**Path:** `/api/users/{id}`

**Path Variables:**
- `id`: Kullanıcı ID'si

**Example:**
```
DELETE http://localhost:8090/api/users/1
```

**Response (204 No Content):**
```
(boş body)
```

---

### 1.6. Kullanıcının Siparişlerini Getir

**Method:** `GET`  
**Path:** `/api/users/{id}/orders`

**Path Variables:**
- `id`: Kullanıcı ID'si

**Not:** Bu endpoint, User Service üzerinden Order Service'i çağırarak kullanıcının siparişlerini getirir. Order Service çalışmıyorsa fallback devreye girer ve boş liste döner.

**Example:**
```
GET http://localhost:8090/api/users/1/orders
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "items": [
      {
        "productId": "507f1f77bcf86cd799439012",
        "productName": "iPhone 15 Pro",
        "quantity": 2,
        "price": 49999.99
      }
    ],
    "totalAmount": 99999.98,
    "status": "PENDING",
    "shippingAddress": "İstanbul, Türkiye",
    "createdAt": "2025-12-24T08:00:00",
    "updatedAt": "2025-12-24T08:00:00"
  }
]
```

**Fallback Senaryosu (Order Service çalışmıyorsa):**
```json
[]
```
(Boş liste döner, hata fırlatılmaz)

---

## 2. Product Service API

### 2.1. Yeni Ürün Oluştur

**Method:** `POST`  
**Path:** `/api/products`  
**Content-Type:** `application/json`

**Not:** Product ID'si MongoDB tarafından otomatik oluşturulur (random ObjectId). Request body'de göndermeyin.

**Request Body:**
```json
{
  "name": "iPhone 15 Pro",
  "description": "Apple'ın en yeni akıllı telefonu",
  "price": 49999.99,
  "category": "Elektronik",
  "imageUrl": "https://example.com/iphone15.jpg"
}
```

**Not:** Stok yönetimi Stock Service üzerinden yapıldığı için ürün oluştururken stok miktarı girilmez. Ürün oluşturulduktan sonra Stock Service üzerinden stok kaydı oluşturulmalıdır.

**Response (201 Created):**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "name": "iPhone 15 Pro",
  "description": "Apple'ın en yeni akıllı telefonu",
  "price": 49999.99,
  "category": "Elektronik",
  "imageUrl": "https://example.com/iphone15.jpg"
}
```

---

### 2.2. Ürün Bilgisi Getir

**Method:** `GET`  
**Path:** `/api/products/{id}`

**Path Variables:**
- `id`: Ürün ID'si

**Example:**
```
GET http://localhost:8090/api/products/507f1f77bcf86cd799439012
```

**Response (200 OK):**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "name": "iPhone 15 Pro",
  "description": "Apple'ın en yeni akıllı telefonu",
  "price": 49999.99,
  "category": "Elektronik",
  "imageUrl": "https://example.com/iphone15.jpg"
}
```

---

### 2.3. Ürünleri Listele

**Method:** `GET`  
**Path:** `/api/products`

**Query Parameters (Opsiyonel):**
- `category`: Kategoriye göre filtrele
- `name`: İsme göre arama

**Examples:**
```
GET http://localhost:8090/api/products
GET http://localhost:8090/api/products?category=Elektronik
GET http://localhost:8090/api/products?name=iPhone
```

**Response (200 OK):**
```json
[
  {
    "id": "507f1f77bcf86cd799439012",
    "name": "iPhone 15 Pro",
    "description": "Apple'ın en yeni akıllı telefonu",
    "price": 49999.99,
    "category": "Elektronik",
    "imageUrl": "https://example.com/iphone15.jpg"
  }
]
```

---

### 2.4. Ürün Bilgilerini Güncelle

**Method:** `PUT`  
**Path:** `/api/products/{id}`  
**Content-Type:** `application/json`

**Path Variables:**
- `id`: Ürün ID'si

**Request Body:**
```json
{
  "name": "iPhone 15 Pro Max",
  "description": "Apple'ın en yeni akıllı telefonu - Güncellenmiş",
  "price": 54999.99,
  "category": "Elektronik",
  "imageUrl": "https://example.com/iphone15max.jpg"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro Max",
  "description": "Apple'ın en yeni akıllı telefonu - Güncellenmiş",
  "price": 54999.99,
  "category": "Elektronik",
  "imageUrl": "https://example.com/iphone15max.jpg"
}
```

---

### 2.5. Ürün Sil

**Method:** `DELETE`  
**Path:** `/api/products/{id}`

**Path Variables:**
- `id`: Ürün ID'si

**Example:**
```
DELETE http://localhost:8090/api/products/507f1f77bcf86cd799439012
```

**Response (204 No Content):**
```
(boş body)
```

---

## 3. Stock Service API

### 3.1. Yeni Stok Kaydı Oluştur

**Method:** `POST`  
**Path:** `/api/stocks`  
**Content-Type:** `application/json`

**Not:** 
- Stock ID'si sıralı olarak oluşturulur (1, 2, 3, ...)
- **Product ID String (ObjectId) formatında olmalıdır!** Örnek: `"694b8fa7105098079b976387"`
- ❌ **YANLIŞ:** `"productId": 8` veya `"productId": "8"` (sayı değil, ObjectId string olmalı)
- ✅ **DOĞRU:** `"productId": "694b8fa7105098079b976387"` (24 karakterlik hex string)
- Mevcut Product ID'leri görmek için: `GET /api/products`
- Stock'a `productId` ile de erişim yapılabilir

**Request Body:**
```json
{
  "productId": "694b8fa7105098079b976387",
  "quantity": 100,
  "reservedQuantity": 0
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 100,
  "reservedQuantity": 0,
  "availableQuantity": 100
}
```

**Not:** Stock ID sıralı Long (1, 2, 3...) olarak oluşturulur. Product ID String (ObjectId) formatındadır.

---

### 3.2. Stok Bilgisi Getir

**Method:** `GET`  
**Path:** `/api/stocks/{id}`

**Path Variables:**
- `id`: Stok ID'si

**Example:**
```
GET http://localhost:8090/api/stocks/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 100,
  "reservedQuantity": 0,
  "availableQuantity": 100
}
```

---

### 3.3. Ürüne Göre Stok Getir

**Method:** `GET`  
**Path:** `/api/stocks/product/{productId}`

**Path Variables:**
- `productId`: Ürün ID'si

**Example:**
```
GET http://localhost:8090/api/stocks/product/507f1f77bcf86cd799439012
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 100,
  "reservedQuantity": 0,
  "availableQuantity": 100
}
```

---

### 3.4. Tüm Stokları Listele

**Method:** `GET`  
**Path:** `/api/stocks`

**Example:**
```
GET http://localhost:8090/api/stocks
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "productId": "507f1f77bcf86cd799439012",
    "quantity": 100,
    "reservedQuantity": 0,
    "availableQuantity": 100
  }
]
```

---

### 3.5. Stok Bilgilerini Güncelle

**Method:** `PUT`  
**Path:** `/api/stocks/{id}`  
**Content-Type:** `application/json`

**Path Variables:**
- `id`: Stok ID'si

**Request Body:**
```json
{
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 150,
  "reservedQuantity": 10
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 150,
  "reservedQuantity": 10,
  "availableQuantity": 140
}
```

---

### 3.6. Stok Rezerve Et

**Method:** `POST`  
**Path:** `/api/stocks/{id}/reserve`  
**Content-Type:** `application/x-www-form-urlencoded` veya `Query Parameter`

**Path Variables:**
- `id`: Stok ID'si

**Query Parameters:**
- `quantity`: Rezerve edilecek miktar

**Example:**
```
POST http://localhost:8090/api/stocks/1/reserve?quantity=5
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 100,
  "reservedQuantity": 5,
  "availableQuantity": 95
}
```

---

### 3.7. Stok Rezervasyonunu Serbest Bırak

**Method:** `POST`  
**Path:** `/api/stocks/{id}/release`  
**Content-Type:** `application/x-www-form-urlencoded` veya `Query Parameter`

**Path Variables:**
- `id`: Stok ID'si

**Query Parameters:**
- `quantity`: Serbest bırakılacak miktar

**Example:**
```
POST http://localhost:8090/api/stocks/1/release?quantity=3
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": "507f1f77bcf86cd799439012",
  "quantity": 100,
  "reservedQuantity": 2,
  "availableQuantity": 98
}
```

---

### 3.8. Stok Sil

**Method:** `DELETE`  
**Path:** `/api/stocks/{id}`

**Path Variables:**
- `id`: Stok ID'si

**Example:**
```
DELETE http://localhost:8090/api/stocks/1
```

**Response (204 No Content):**
```
(boş body)
```

---

## 4. Order Service API

### 4.1. Yeni Sipariş Oluştur

**Method:** `POST`  
**Path:** `/api/orders`  
**Content-Type:** `application/json`

**Not:** ID otomatik oluşturulur, request body'de göndermeyin. `totalAmount` ve `status` servis tarafından otomatik hesaplanır.

**Request Body:**
```json
{
  "userId": 1,
  "items": [
    {
      "productId": "507f1f77bcf86cd799439012",
      "quantity": 2,
      "price": 49999.99
    }
  ]
}
```

**Not:** `totalAmount` ve `status` otomatik hesaplanır, request body'de göndermeyin.

**Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "items": [
    {
      "productId": "507f1f77bcf86cd799439012",
      "quantity": 2,
      "price": 49999.99
    }
  ],
  "totalAmount": 99999.98,
  "status": "PENDING",
  "createdAt": "2025-12-23T10:00:00Z"
}
```

**Not:** Order ID sıralı Long (1, 2, 3...) olarak oluşturulur.

---

### 4.2. Sipariş Bilgisi Getir

**Method:** `GET`  
**Path:** `/api/orders/{id}`

**Path Variables:**
- `id`: Sipariş ID'si

**Example:**
```
GET http://localhost:8090/api/orders/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "items": [
    {
      "productId": "507f1f77bcf86cd799439012",
      "quantity": 2,
      "price": 49999.99
    }
  ],
  "totalAmount": 99999.98,
  "status": "PENDING",
  "createdAt": "2025-12-23T10:00:00Z"
}
```

---

### 4.3. Kullanıcının Siparişlerini Listele

**Method:** `GET`  
**Path:** `/api/orders/user/{userId}`

**Path Variables:**
- `userId`: Kullanıcı ID'si

**Example:**
```
GET http://localhost:8090/api/orders/user/1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "items": [
      {
        "productId": "507f1f77bcf86cd799439012",
        "quantity": 2,
        "price": 49999.99
      }
    ],
    "totalAmount": 99999.98,
    "status": "PENDING",
    "createdAt": "2025-12-23T10:00:00Z"
  }
]
```

---

### 4.4. Tüm Siparişleri Listele

**Method:** `GET`  
**Path:** `/api/orders`

**Example:**
```
GET http://localhost:8090/api/orders
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "items": [
      {
        "productId": "507f1f77bcf86cd799439012",
        "quantity": 2,
        "price": 49999.99
      }
    ],
    "totalAmount": 99999.98,
    "status": "PENDING",
    "createdAt": "2025-12-23T10:00:00Z"
  }
]
```

---

### 4.5. Sipariş Durumunu Güncelle

**Method:** `PUT`  
**Path:** `/api/orders/{id}/status`  
**Content-Type:** `application/x-www-form-urlencoded` veya `Query Parameter`

**Path Variables:**
- `id`: Sipariş ID'si

**Query Parameters:**
- `status`: Yeni durum (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)

**Example:**
```
PUT http://localhost:8090/api/orders/1/status?status=CONFIRMED
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "items": [
    {
      "productId": "507f1f77bcf86cd799439012",
      "quantity": 2,
      "price": 49999.99
    }
  ],
  "totalAmount": 99999.98,
  "status": "CONFIRMED",
  "createdAt": "2025-12-23T10:00:00Z"
}
```

---

### 4.6. Sipariş İptal Et

**Method:** `DELETE`  
**Path:** `/api/orders/{id}`

**Path Variables:**
- `id`: Sipariş ID'si

**Example:**
```
DELETE http://localhost:8090/api/orders/1
```

**Response (204 No Content):**
```
(boş body)
```

---

## 📝 Postman Collection Oluşturma

### Adım 1: Postman'de Yeni Collection Oluştur

1. Postman'i açın
2. "Collections" sekmesine gidin
3. "New Collection" butonuna tıklayın
4. Collection adını girin: "E-Ticaret API"

### Adım 2: Environment Variables Oluştur

1. "Environments" sekmesine gidin
2. "New Environment" butonuna tıklayın
3. Environment adını girin: "Local"
4. Şu değişkenleri ekleyin:
   - `base_url`: `http://localhost:8090`
   - `user_id`: (oluşturduğunuz kullanıcı ID'si)
   - `product_id`: (oluşturduğunuz ürün ID'si)
   - `stock_id`: (oluşturduğunuz stok ID'si)
   - `order_id`: (oluşturduğunuz sipariş ID'si)

### Adım 3: Request'leri Oluştur

Her endpoint için yeni bir request oluşturun ve yukarıdaki path'leri kullanın.

**Örnek Request URL:**
```
{{base_url}}/api/users
```

---

## 🔧 Hata Kodları

| HTTP Status | Açıklama |
|-------------|----------|
| 200 | Başarılı |
| 201 | Oluşturuldu |
| 204 | Başarılı (İçerik yok) |
| 400 | Geçersiz istek |
| 404 | Bulunamadı |
| 500 | Sunucu hatası |

---

## ⚠️ Önemli Notlar

1. **Port-Forward:** Kubernetes kullanıyorsanız port-forward aktif olmalıdır
2. **Base URL:** Tüm istekler `http://localhost:8090` üzerinden yapılır
3. **Content-Type:** JSON istekleri için `application/json` kullanın
4. **Path Variables:** `{id}` gibi değişkenleri gerçek ID'lerle değiştirin
5. **Query Parameters:** `?` ile başlayan parametreler URL sonuna eklenir

---

## 🚀 Hızlı Başlangıç Senaryosu

1. **Kullanıcı Oluştur:**
   ```
   POST http://localhost:8090/api/users
   ```

2. **Ürün Oluştur:**
   ```
   POST http://localhost:8090/api/products
   ```

3. **Stok Oluştur:**
   ```
   POST http://localhost:8090/api/stocks
   ```

4. **Sipariş Oluştur:**
   ```
   POST http://localhost:8090/api/orders
   ```

5. **Sipariş Durumunu Güncelle:**
   ```
   PUT http://localhost:8090/api/orders/{order_id}/status?status=CONFIRMED
   ```

