# Mikroservis Tabanlı E-Ticaret Sistemi

## Proje Yapısı

Bu proje, Spring Boot ve Spring Cloud kullanılarak geliştirilmiş mikroservis tabanlı bir e-ticaret sistemidir.

### Servisler

1. **API Gateway** (Port: 8090) - Gateway Servisi
2. **User Service** (Port: 9011) - Kullanıcı Yönetimi
3. **Product Service** (Port: 9012) - Ürün Yönetimi
4. **Stock Service** (Port: 9013) - Stok Yönetimi (Redis Cache ile)
5. **Order Service** (Port: 9014) - Sipariş Yönetimi (Kafka ile)

**Not:** Service Discovery için Docker Compose'ta Docker DNS, Kubernetes'te ise Kubernetes Service Discovery kullanılır.

### Teknolojiler

- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- MongoDB
- Redis
- Kafka
- Docker
- Kubernetes

## Derleme

```bash
# Proje kök dizininde
mvn clean package
```

Bu komut tüm servisleri derler ve JAR dosyalarını oluşturur.

Detaylı bilgi için: [docs/BUILD_GUIDE.md](docs/BUILD_GUIDE.md)

## Çalıştırma

### Docker ile Çalıştırma (Önerilen)

```bash
# 1. Projeyi derle
mvn clean package

# 2. Docker image'larını oluştur ve servisleri başlat
docker-compose up -d --build

# Veya ayrı ayrı:
docker-compose build
docker-compose up -d
```

Detaylı bilgi için: [docs/DOCKER_GUIDE.md](docs/DOCKER_GUIDE.md)

### Kubernetes ile Çalıştırma

**ÖNEMLİ:** Kubernetes için önce Docker image'larını oluşturmalısınız!

#### Adım 1: Docker Image'larını Oluştur

```bash
# PowerShell'de
.\build-k8s-images.ps1

# Veya Linux/Mac'te
chmod +x build-k8s-images.sh
./build-k8s-images.sh

# Veya manuel olarak
mvn clean package
docker build -t user-service:latest ./user-service
docker build -t product-service:latest ./product-service
docker build -t stock-service:latest ./stock-service
docker build -t order-service:latest ./order-service
docker build -t api-gateway:latest ./api-gateway
```

**Minikube kullanıyorsanız:**
```bash
eval $(minikube docker-env)
.\build-k8s-images.ps1
```

#### Adım 2: Kubernetes'e Deploy Et

```bash
# PowerShell'de
cd kubernetes
.\deploy-all.ps1

# Veya Linux/Mac'te
cd kubernetes
chmod +x deploy-all.sh
./deploy-all.sh

# Veya manuel olarak
kubectl apply -f kubernetes/
```

Detaylı bilgi için: [docs/KUBERNETES_GUIDE.md](docs/KUBERNETES_GUIDE.md)

## API Endpoints

Tüm API'ler API Gateway üzerinden erişilebilir:

- User Service: `http://localhost:8090/api/users`
- Product Service: `http://localhost:8090/api/products`
- Stock Service: `http://localhost:8090/api/stocks`
- Order Service: `http://localhost:8090/api/orders`

## Swagger UI

Her servis kendi Swagger UI'ına sahiptir:

- User Service: `http://localhost:9011/swagger-ui.html`
- Product Service: `http://localhost:9012/swagger-ui.html`
- Stock Service: `http://localhost:9013/swagger-ui.html`
- Order Service: `http://localhost:9014/swagger-ui.html`

Detaylı dokümantasyon için: [docs/README.md](docs/README.md)

## Loglama

Tüm servisler düzenli log formatı kullanır:
```
%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

