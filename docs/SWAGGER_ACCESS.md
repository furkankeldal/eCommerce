# Swagger UI Erişim Kılavuzu

## 🌐 Port-Forward ve Erişim

Tüm servislere ve Swagger UI'lara erişmek için port-forward scriptini kullanabilirsiniz.

```powershell
cd kubernetes/scripts
.\port-forward.bat
```

Bu script şunları başlatır:
- **API Gateway**: 8090 portu
- **User Service**: 9011 portu
- **Product Service**: 9012 portu
- **Stock Service**: 9013 portu
- **Order Service**: 9014 portu

## 🎯 Swagger UI Adresleri

Port-forward aktif olduktan sonra tarayıcıda şu adresleri açabilirsiniz:

### API Gateway Üzerinden (Merkezi)
- **Tüm Servisler (Aggregator)**: http://localhost:8090/swagger-ui.html

### Doğrudan Mikroservisler Üzerinden
- **User Service**: http://localhost:9011/swagger-ui.html
- **Product Service**: http://localhost:9012/swagger-ui.html
- **Stock Service**: http://localhost:9013/swagger-ui.html
- **Order Service**: http://localhost:9014/swagger-ui.html

## 🔧 Sorun Giderme

Eğer "Internal Server Error" veya 404 hataları alıyorsanız, API Gateway loglarını kontrol edin ve port-forward pencerelerinin açık olduğundan emin olun.


