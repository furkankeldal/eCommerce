# Swagger UI Kullanım Kılavuzu

## ⚠️ Port-Forward Başlatma

Servislere erişmek için port-forward scriptini kullanabilirsiniz:

```powershell
cd kubernetes/scripts
.\port-forward.bat
```

## 🎯 Swagger UI'ya Erişim

### 1. API Gateway Üzerinden (Merkezi Erişim)

Tüm servisleri tek bir portalda görmek için:
**URL:** [http://localhost:8090/swagger-ui.html](http://localhost:8090/swagger-ui.html)

### 2. Mikroservisler Üzerinden Doğrudan Erişim

Hata ayıklama veya doğrudan test için her servisin kendi Swagger arayüzüne de ulaşabilirsiniz:
- **User Service:** [http://localhost:9011/swagger-ui.html](http://localhost:9011/swagger-ui.html)
- **Product Service:** [http://localhost:9012/swagger-ui.html](http://localhost:9012/swagger-ui.html)
- **Stock Service:** [http://localhost:9013/swagger-ui.html](http://localhost:9013/swagger-ui.html)
- **Order Service:** [http://localhost:9014/swagger-ui.html](http://localhost:9014/swagger-ui.html)

## 🚀 Swagger UI'dan İstek Atma

### 1. Swagger UI'ı Açın

Tarayıcınızda API Gateway Swagger adresini açın:
`http://localhost:8090/swagger-ui.html`

### 2. Server URL Kontrolü

**ÖNEMLİ:** Swagger UI'da Server URL otomatik olarak API Gateway'e ayarlıdır. İstekleriniz otomatik olarak şu formatlarda iletilir:
- `http://localhost:8090/user-service/api/...`
- `http://localhost:8090/product-service/api/...`
- `http://localhost:8090/stock-service/api/...`
- `http://localhost:8090/order-service/api/...`

Ayrıca doğrudan `/api/...` formatı da desteklenmektedir.

### 2. API Endpoint'ini Seçin

Swagger UI'da gösterilen endpoint'lerden birini seçin (örneğin: `POST /users`)

### 3. "Try it out" Butonuna Tıklayın

Endpoint'in yanındaki "Try it out" butonuna tıklayın

### 4. Request Body'yi Doldurun (Gerekirse)

POST, PUT gibi isteklerde Request Body bölümüne JSON formatında veri girin:

**Örnek: User Service - POST /users**
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User",
  "address": "Test Address",
  "phone": "1234567890"
}
```

**Örnek: Product Service - POST /products**
```json
{
  "name": "Test Product",
  "description": "Test Description",
  "price": 99.99,
  "category": "Electronics",
  "imageUrl": "https://example.com/image.jpg",
  "stockQuantity": 100
}
```

**Örnek: Order Service - POST /orders**
```json
{
  "userId": "user-id-here",
  "items": [
    {
      "productId": "product-id-here",
      "productName": "Product Name",
      "quantity": 2,
      "price": 99.99
    }
  ],
  "totalAmount": 199.98,
  "shippingAddress": "Shipping Address"
}
```

### 5. "Execute" Butonuna Tıklayın

"Execute" butonuna tıklayarak isteği gönderin

### 6. Response'u İnceleyin

Response bölümünde:
- **Status Code**: HTTP durum kodu (200, 201, 404, vb.)
- **Response Body**: Sunucudan dönen JSON verisi
- **Response Headers**: HTTP header'ları

## 📝 Örnek Senaryolar

### Senaryo 1: Yeni Kullanıcı Oluşturma

1. **API Gateway Swagger UI'ı açın**: `http://localhost:8090/swagger-ui.html`
2. **User Service** tanımını (definition) seçin.
3. **POST /users** endpoint'ini seçin
3. "Try it out" butonuna tıklayın
4. Request Body'ye kullanıcı bilgilerini girin
5. "Execute" butonuna tıklayın
6. Response'da oluşturulan kullanıcının `id`'sini kopyalayın

### Senaryo 2: Ürün Oluşturma ve Sipariş Verme

1. **API Gateway Swagger UI'ı açın**: `http://localhost:8090/swagger-ui.html`
2. **Product Service** tanımını seçin ve **POST /products** ile yeni ürün oluşturun.
3. Oluşturulan ürünün `id`'sini kopyalayın.
4. **Stock Service** tanımını seçin ve **POST /stocks** ile ürün için stok oluşturun.
5. **Order Service** tanımını seçin ve **POST /orders** ile sipariş oluşturun (userId ve productId'yi kullanın).

### Senaryo 3: API Gateway Üzerinden İstek Atma

**Tüm API istekleri otomatik olarak API Gateway üzerinden yapılır!**

Swagger UI'lar API Gateway üzerinden erişildiği için, tüm istekler otomatik olarak API Gateway üzerinden gider.

API Gateway URL'leri:
   - `http://localhost:8090/api/users` (User Service için)
   - `http://localhost:8090/api/products` (Product Service için)
   - `http://localhost:8090/api/stocks` (Stock Service için)
   - `http://localhost:8090/api/orders` (Order Service için)

## ⚠️ Önemli Notlar

1. **Merkezi Erişim**: API Gateway ana giriş noktasıdır. Mikroservislere doğrudan erişim kapatılmıştır.
2. **Servislerin Çalışıyor Olması**: Kubernetes'te servislerin `Running` durumunda olması gerekir
3. **Swagger UI Seçeneği**: Swagger UI'ya sadece API Gateway portu (8090) üzerinden erişilebilir.
4. **API İstekleri Gateway Üzerinden**: Tüm API isteklerinin API Gateway üzerinden yapılması zorunludur.
5. **Server URL Otomatik**: Swagger UI'da Server URL otomatik olarak API Gateway'e ayarlıdır
6. **ID'leri Kopyalayın**: Oluşturulan kayıtların ID'lerini kopyalayarak diğer isteklerde kullanın

## 🔧 Sorun Giderme

### Swagger UI açılmıyor

1. API Gateway port-forward'unun çalıştığından emin olun:
   ```powershell
   Get-NetTCPConnection -LocalPort 8090 -ErrorAction SilentlyContinue
   ```

2. Pod'ların çalıştığını kontrol edin:
   ```powershell
   kubectl get pods -n ecommerce
   ```

3. API Gateway log'larını kontrol edin:
   ```powershell
   kubectl logs -n ecommerce -l app=api-gateway --tail=50
   ```

### 404 Not Found hatası

- Doğru URL'yi kullandığınızdan emin olun: `/swagger/{service}/index.html` formatında
- Örnek: `http://localhost:8090/swagger/users/index.html`
- API Gateway port-forward'unun aktif olduğundan emin olun (8090 portu)

### Connection Refused hatası

- API Gateway port-forward'unun aktif olduğundan emin olun (8090 portu)
- Pod'ların `Running` durumunda olduğunu kontrol edin
- NetworkPolicy'lerin doğru yapılandırıldığından emin olun

### API İstekleri Gateway Üzerinden Gitmiyor

- Swagger UI'lar API Gateway üzerinden erişildiği için istekler otomatik olarak API Gateway üzerinden gider
- Eğer sorun devam ederse, Swagger UI'da Server URL'ini kontrol edin: `http://localhost:8090/api/{service}`
- API Gateway port-forward'unun aktif olduğundan emin olun (8090 portu)


