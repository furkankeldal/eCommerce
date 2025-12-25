# Kubernetes Deployment Guide

Bu dizin, E-Ticaret mikroservis sisteminin Kubernetes deployment dosyalarını içerir.

## 📁 Dizin Yapısı

```
kubernetes/
├── config/              # Konfigürasyon dosyaları
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── ingress.yaml
│   └── network-policies.yaml
├── deployments/         # Deployment dosyaları
│   ├── api-gateway-deployment.yaml
│   ├── user-service-deployment.yaml
│   ├── product-service-deployment.yaml
│   ├── stock-service-deployment.yaml
│   ├── order-service-deployment.yaml
│   ├── mongodb-deployment.yaml
│   ├── redis-deployment.yaml
│   └── kafka-deployment.yaml
└── scripts/             # Yardımcı scriptler
    ├── deploy-all.ps1
    ├── deploy-all.sh
    ├── port-forward.ps1
    ├── port-forward.bat
    ├── debug-swagger.ps1
    └── debug-swagger.bat
```

## 🚀 Hızlı Başlangıç

### 1. Tüm Servisleri Deploy Et

**Windows:**
```powershell
cd kubernetes/scripts
.\deploy-all.ps1
```

**Linux/Mac:**
```bash
cd kubernetes/scripts
chmod +x deploy-all.sh
./deploy-all.sh
```

### 2. Port-Forward Başlat

**Windows:**
```powershell
cd kubernetes/scripts
.\port-forward.ps1
# veya
.\port-forward.bat
```

**Linux/Mac:**
```bash
kubectl port-forward -n ecommerce service/api-gateway-service 8090:8090
```

### 3. Servisleri Kontrol Et

```bash
kubectl get pods -n ecommerce
kubectl get services -n ecommerce
```

## 📝 Notlar

- Tüm pod'lar `project=ecommerce` label'ına sahiptir
- Servisler arası iletişim Kubernetes DNS üzerinden yapılır
- API Gateway üzerinden tüm API'lere erişilebilir
- Swagger UI'lar doğrudan servislerden erişilebilir (port-forward gerekir)

