# Kubernetes ile Çalıştırma Kılavuzu

## 🎯 Ön Gereksinimler

- Kubernetes cluster çalışıyor olmalı (Minikube, Docker Desktop Kubernetes, veya cloud cluster)
- kubectl yüklü ve yapılandırılmış olmalı
- Docker image'ları build edilmiş olmalı

### ⚠️ ÖNEMLİ: Kubernetes Cluster Kontrolü

Deploy etmeden önce cluster'ın çalıştığını kontrol edin:

```bash
# Cluster durumunu kontrol et
kubectl get nodes

# Başarılı çıktı örneği:
# NAME             STATUS   ROLES           AGE   VERSION
# docker-desktop   Ready    control-plane   5m    v1.28.0
```

**Eğer hata alıyorsanız:** [KUBERNETES_TROUBLESHOOTING.md](KUBERNETES_TROUBLESHOOTING.md) dosyasına bakın.

## 📦 Docker Image'larını Hazırlama

Kubernetes, Docker image'larını kullanır. Önce image'ları oluşturmalısınız:

### Yöntem 1: Local Image'lar (Minikube/Docker Desktop)

```bash
# 1. Projeyi derle
mvn clean package

# 2. Docker image'larını oluştur
docker-compose build

# Veya tek tek:
docker build -t user-service:latest ./user-service
docker build -t product-service:latest ./product-service
docker build -t stock-service:latest ./stock-service
docker build -t order-service:latest ./order-service
docker build -t api-gateway:latest ./api-gateway
```

**Minikube kullanıyorsanız:**
```bash
# Minikube Docker environment'ını kullan
eval $(minikube docker-env)

# Image'ları build et
docker build -t user-service:latest ./user-service
docker build -t product-service:latest ./product-service
docker build -t stock-service:latest ./stock-service
docker build -t order-service:latest ./order-service
docker build -t api-gateway:latest ./api-gateway
```

### Yöntem 2: Docker Registry (Production)

```bash
# 1. Image'ları tag'le
docker tag user-service:latest your-registry/user-service:latest
docker tag product-service:latest your-registry/product-service:latest
docker tag stock-service:latest your-registry/stock-service:latest
docker tag order-service:latest your-registry/order-service:latest
docker tag api-gateway:latest your-registry/api-gateway:latest

# 2. Registry'ye push et
docker push your-registry/user-service:latest
docker push your-registry/product-service:latest
docker push your-registry/stock-service:latest
docker push your-registry/order-service:latest
docker push your-registry/api-gateway:latest

# 3. Kubernetes deployment'larında image adreslerini güncelle
```

## 🚀 Kubernetes'e Deploy Etme

### ⚡ Hızlı Başlatma (Önerilen)

**Tüm servisleri tek komutla başlatın:**

**Windows (PowerShell):**
```powershell
cd kubernetes
.\deploy-all.ps1
```

**Linux/Mac (Bash):**
```bash
cd kubernetes
chmod +x deploy-all.sh
./deploy-all.sh
```

Bu script otomatik olarak:
1. ✅ Namespace oluşturur
2. ✅ ConfigMap ve Secret oluşturur
3. ✅ MongoDB, Redis, Kafka servislerini başlatır
4. ✅ Servislerin hazır olmasını bekler
5. ✅ Tüm mikroservisleri (User, Product, Stock, Order, API Gateway) deploy eder
6. ✅ Ingress'i oluşturur

---

### 📋 Manuel Deploy (Alternatif)

Eğer adım adım deploy etmek isterseniz:

#### Adım 1: Namespace Oluştur

```bash
kubectl apply -f kubernetes/namespace.yaml
```

#### Adım 2: ConfigMap ve Secret Oluştur

```bash
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/secret.yaml
```

#### Adım 3: Veritabanı ve Mesajlaşma Servislerini Başlat

```bash
# MongoDB
kubectl apply -f kubernetes/mongodb-deployment.yaml

# Redis
kubectl apply -f kubernetes/redis-deployment.yaml

# Kafka ve Zookeeper
kubectl apply -f kubernetes/kafka-deployment.yaml
```

**Servislerin hazır olmasını bekleyin:**
```bash
kubectl wait --for=condition=ready pod -l app=mongodb -n ecommerce --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis -n ecommerce --timeout=300s
```

#### Adım 4: Mikroservisleri Deploy Et

```bash
# User Service
kubectl apply -f kubernetes/user-service-deployment.yaml

# Product Service
kubectl apply -f kubernetes/product-service-deployment.yaml

# Stock Service
kubectl apply -f kubernetes/stock-service-deployment.yaml

# Order Service
kubectl apply -f kubernetes/order-service-deployment.yaml

# API Gateway
kubectl apply -f kubernetes/api-gateway-deployment.yaml
```

#### Adım 5: Ingress Oluştur

```bash
kubectl apply -f kubernetes/ingress.yaml
```

### Adım 5: Ingress Oluştur

```bash
kubectl apply -f kubernetes/ingress.yaml
```

## 🔍 Durum Kontrolü

### Pod'ların Durumunu Kontrol Et

```bash
# Tüm pod'ları listele
kubectl get pods -n ecommerce

# Belirli bir pod'un detaylarını gör
kubectl describe pod <pod-name> -n ecommerce

# Pod loglarını görüntüle
kubectl logs <pod-name> -n ecommerce
```

### Service'lerin Durumunu Kontrol Et

```bash
# Tüm service'leri listele
kubectl get services -n ecommerce

# Service detaylarını gör
kubectl describe service <service-name> -n ecommerce
```

### Deployment'ların Durumunu Kontrol Et

```bash
# Tüm deployment'ları listele
kubectl get deployments -n ecommerce

# Deployment detaylarını gör
kubectl describe deployment <deployment-name> -n ecommerce
```

## 🌐 Servislere Erişim

### ⚠️ ÖNEMLİ: Tüm İstekler API Gateway Üzerinden

Tüm servislere erişim **sadece API Gateway üzerinden** yapılır. Servislere doğrudan erişim yoktur.

### Port Forward ile Erişim

**Sadece API Gateway için port-forward yapın:**

**PowerShell Script (Önerilen):**
```powershell
cd kubernetes
powershell -ExecutionPolicy Bypass -File .\port-forward.ps1
```

**Veya Manuel:**
```bash
# API Gateway (Tüm servislere erişim için yeterli)
kubectl port-forward service/api-gateway-service 8090:80 -n ecommerce
```

**Swagger UI Erişim Adresleri (API Gateway üzerinden):**
- User Service: http://localhost:8090/swagger/users/swagger-ui.html
- Product Service: http://localhost:8090/swagger/products/swagger-ui.html
- Stock Service: http://localhost:8090/swagger/stocks/swagger-ui.html
- Order Service: http://localhost:8090/swagger/orders/swagger-ui.html

**API Endpoint'leri (API Gateway üzerinden):**
- User Service: `http://localhost:8090/api/users/**`
- Product Service: `http://localhost:8090/api/products/**`
- Stock Service: `http://localhost:8090/api/stocks/**`
- Order Service: `http://localhost:8090/api/orders/**`

### Ingress ile Erişim

Ingress controller yüklüyse:

```bash
# Ingress durumunu kontrol et
kubectl get ingress -n ecommerce

# Ingress IP'sini al
kubectl get ingress ecommerce-ingress -n ecommerce
```

Sonra `/etc/hosts` dosyasına ekleyin:
```
<ingress-ip> ecommerce.local
```

Tarayıcıda: `http://ecommerce.local/api/users`

## 🔧 Sorun Giderme

### Problem: ImagePullBackOff Hatası

**Çözüm:**
```bash
# Image'ın var olduğunu kontrol et
docker images | grep user-service

# Minikube kullanıyorsanız
eval $(minikube docker-env)
docker images | grep user-service

# Image'ı yeniden build et
docker build -t user-service:latest ./user-service
```

### Problem: Pod'lar CrashLoopBackOff

**Çözüm:**
```bash
# Pod loglarını kontrol et
kubectl logs <pod-name> -n ecommerce

# Pod'u yeniden başlat
kubectl delete pod <pod-name> -n ecommerce
```

### Problem: Servisler Birbirini Bulamıyor

**Çözüm:**
```bash
# Service'lerin doğru namespace'te olduğunu kontrol et
kubectl get services -n ecommerce

# DNS çözümlemesini test et
kubectl run -it --rm debug --image=busybox --restart=Never -n ecommerce -- nslookup user-service
```

### Problem: MongoDB Bağlantı Hatası

**Çözüm:**
```bash
# MongoDB pod'unun çalıştığını kontrol et
kubectl get pods -l app=mongodb -n ecommerce

# MongoDB loglarını kontrol et
kubectl logs -l app=mongodb -n ecommerce
```

## 📊 HPA (Horizontal Pod Autoscaler) Kontrolü

```bash
# HPA durumunu kontrol et
kubectl get hpa -n ecommerce

# HPA detaylarını gör
kubectl describe hpa user-service-hpa -n ecommerce
```

## 🗑️ Temizleme

### Tüm Kaynakları Sil

```bash
# Tüm deployment'ları sil
kubectl delete -f kubernetes/ --ignore-not-found=true

# Namespace'i sil (tüm kaynakları siler)
kubectl delete namespace ecommerce
```

## 🔄 Güncelleme

Kod değişikliklerinden sonra:

```bash
# 1. Projeyi yeniden derle
mvn clean package

# 2. Image'ları yeniden build et
docker-compose build

# 3. Deployment'ları yeniden başlat
kubectl rollout restart deployment/user-service -n ecommerce
kubectl rollout restart deployment/product-service -n ecommerce
kubectl rollout restart deployment/stock-service -n ecommerce
kubectl rollout restart deployment/order-service -n ecommerce
kubectl rollout restart deployment/api-gateway -n ecommerce
```

## 💡 İpuçları

1. **Minikube kullanıyorsanız**: `eval $(minikube docker-env)` komutunu çalıştırmayı unutmayın
2. **Image boyutu**: Production'da multi-stage build kullanarak image boyutunu küçültebilirsiniz
3. **Resource limits**: Production'da resource limit'leri ayarlayın
4. **Health checks**: Liveness ve readiness probe'ları ekleyin
5. **Secrets**: Production'da gerçek secret'ları kullanın

---

**Not:** Kubernetes deployment'ları `imagePullPolicy: Never` kullanıyor, bu yüzden image'ların local'de olması gerekiyor.

