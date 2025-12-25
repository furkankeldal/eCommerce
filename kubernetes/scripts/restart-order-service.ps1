# Order Service'i tekrar başlat

Write-Host "Order Service başlatılıyor..." -ForegroundColor Yellow
kubectl scale deployment/order-service --replicas=2 -n ecommerce

Write-Host ""
Write-Host "Pod durumları:" -ForegroundColor Yellow
kubectl get pods -n ecommerce | Select-String "order-service"

Write-Host ""
Write-Host "Order Service başlatıldı!" -ForegroundColor Green
Write-Host "Pod'ların hazır olmasını bekleyin..." -ForegroundColor Yellow

